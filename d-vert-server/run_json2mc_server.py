import os
import random
import time
from flask import Flask, request, render_template, session, flash, redirect, \
    url_for, jsonify, abort, send_from_directory
# from flask_mail import Mail, Message
from celery import Celery, states
from celery.exceptions import SoftTimeLimitExceeded

from json2mc.factory_methods import DiaVerificationFactory
import json2mc.config as cfg

from redis import Redis

from flask_cors import CORS, cross_origin
import time
from urllib2 import urlopen
from werkzeug.wsgi import LimitedStream
import json


my_ip = 'localhost' if os.environ.get('LOCAL_DEPLOY', 'true') == 'true' else urlopen('http://ip.42.pl/raw').read()

REDIS_ADDRESS = 'redis'

app = Flask(__name__,static_folder='static', static_url_path='')
CORS(app)


class StreamConsumingMiddleware(object):

    def __init__(self, app):
        self.app = app

    def __call__(self, environ, start_response):
        stream = LimitedStream(environ['wsgi.input'],
                               int(environ['CONTENT_LENGTH'] or 0))
        environ['wsgi.input'] = stream
        app_iter = self.app(environ, start_response)
        try:
            stream.exhaust()
            for event in app_iter:
                yield event
        finally:
            if hasattr(app_iter, 'close'):
                app_iter.close()


app.wsgi_app = StreamConsumingMiddleware(app.wsgi_app)


app.config['SECRET_KEY'] = 'top-secret!'

# Celery configuration
app.config['CELERY_BROKER_URL'] = 'redis://' + REDIS_ADDRESS + ':6379/0'
app.config['CELERY_RESULT_BACKEND'] = 'redis://' + REDIS_ADDRESS + ':6379/0'
# FROM OBJECT
#app.config.from_object('config')

# Initialize Celery
celery = Celery(app.name, broker=app.config['CELERY_BROKER_URL'], backend=app.config['CELERY_RESULT_BACKEND'])
celery.conf.update(app.config)

redis = Redis(REDIS_ADDRESS)

def get_static_url(path):
    return os.path.relpath(path, app.static_folder)


@celery.task(bind=True)
def verification_task(self, task_name, technology, context):
    import matplotlib
    print('Executing task id {0.id}, args: {0.args!r} kwargs: {0.kwargs!r}'
          .format(self.request))
    try:
        self.update_state(state='PROGRESS', meta={'name': task_name})

        output_dir = os.path.abspath("static/tasks/")
        v_task = DiaVerificationFactory.get_verif_task(tech=technology,
                                                       context=context,
                                                       output_dir=output_dir)
        v_task.launch_verification()
        if v_task.result_dir:
            v_task.process_zot_results()
            if v_task.verification_result.outcome == 'sat':
                v_task.plot_trace()
            print 'DONE'
        else:
            print 'FINISHED WITH ERRORS'

        outcome = v_task.verification_result.outcome.upper()
        ver_time = v_task.verification_result.verification_time
        hist_file = os.path.join(v_task.result_dir, v_task.hist_file) if outcome == 'SAT' else None
        fig_path = v_task.figure_path
        lisp_path = os.path.join(v_task.result_dir, "zot_in.lisp")
        result_file = os.path.join(v_task.result_dir, v_task.result_file)
        json_path = v_task.json_starting_context_path
        return {'name': task_name, 'result': outcome,
                'verification_time':ver_time, 'hist_file': hist_file,
                'fig_path':fig_path, 'json_path': json_path,
                'lisp_path':lisp_path, 'result_file':result_file}
    except SoftTimeLimitExceeded:
        print 'SoftTimeLimitExceeded!!'
        self.update_state(state='FAILURE', meta={'status': 'Task TIMEOUT!',
                                                 'result': 'FAIL', 'name': task_name})
        time.sleep(5)
        return {'name': task_name, 'status':'TIMEOUT'}




@app.route('/', methods=['GET', 'POST'])
def index():
    task_list_json = task_list()
    if request.method == 'GET':
        return render_template('index.html',
                               param_name=session.get('param_name', ''),
                               hostname=my_ip,
                               flask_port=5000,
                               flower_port=5555,
                               json_list=json.dumps(task_list_json))
 
    

@app.route('/tasks/<path:path>')
def send_js(path):
    return send_from_directory(app.static_folder+'/tasks', path, as_attachment=False)


@app.route('/task_list', methods=['GET'])
def get_task_list():
    return jsonify(task_list()), 200
    
def remove_prefix(s, prefix):
    return s[len(prefix):] if s.startswith(prefix) else s


def task_list():
    my_list = redis.keys('celery-task-meta-*')
    statuslist = []
    # print my_list
    for t in my_list:
        statuslist.append(task_status(remove_prefix(t, 'celery-task-meta-')))
        redis.persist(t)
    # print my_list
    return statuslist

@app.route('/longtasks', methods=['POST'])
def create_task():
    if not request.json or 'title' not in request.json:
        abort(400)
    params = {
        'title': request.json['title'],
        'description': request.json.get('description', ""),
        'technology':request.json.get('technology', cfg.TECH_KEYS[0]),
        'done': False,
        'json_context': request.json["json_context"],
        'timeout': request.json.get('timeout', 300000)

    }
    task = verification_task.apply_async(args=[params["title"],
                                               params['technology'],
                                               params["json_context"]],
                                               soft_time_limit=params["timeout"])
#    verification_task(params["title"])
    return jsonify({}), 202, {'Location': url_for('get_task_status',
                                                  task_id=task.id)}



@app.route('/status/<task_id>')
def get_task_status(task_id):
    return jsonify(task_status(task_id))

def get_static_img_popup_link(fig_path, link_text):
    # print 'fig_path:',fig_path, "link_text:",link_text
    if fig_path is None:
        return ''
    elif fig_path == '':
        return ''
    else:
        # return '<a class="popup-img" href="'+url_for('static', filename=get_static_url(fig_path))+'"> '+ link_text + '</a>'
        return '<a class="popup-img" href="{}">{}</a>'.format(url_for('static', filename=get_static_url(fig_path)),
                                                                 link_text)

def get_static_new_tab_link(file_path, link_text, show_icon=True):
    icon = '<img src="imgs/new_tab.png">' if show_icon else ''
    if file_path is None or file_path == '':
        return '<a title="No output trace is generated \n' \
               ' when no problem is detected (UNSAT result)">' \
               'N/A </a>'
    else:
        return '<a href="{}" target="_blank"> {} {} </a>'.format(url_for('static', filename=get_static_url(file_path)),
                                                                                          link_text, icon)
                                                                    

def get_static_current_tab_link(file_path, link_text):
#    print 'file_path:',file_path, "link_text:",link_text
    if file_path is None:
        return 'N/A'
    elif file_path == '':
        return 'N/A'
    else:
        return '<a href="{}"> {}</a>'.format(url_for('static', filename=get_static_url(file_path)),
                                              link_text)

def get_outcome_render(outcome):
    if outcome == 'SAT':
        return '<img src="imgs/warning_32x32.png" title="Problem detected on the selected bolts! \n' \
               '(SAT)\n' \
               'Check the output trace">'
    elif outcome == 'UNSAT':
        return '<img src="imgs/success_32x32.png" title="NO Problem detected on the selected bolts! \n (UNSAT) ">'
    else:
        return '<img src="imgs/question_mark_32x32.png" title="the task did not terminate correctly">'

def task_status(task_id):
    task = verification_task.AsyncResult(task_id)
    if task.state == states.PENDING:
        response = {
            'id':task_id,
            'state': task.state,
            'status': 'Pending...'
        }
    elif task.state == 'PROGRESS':
        response = {
            'id':task_id,
            'state': '  <img src="imgs/progress.gif">',
            'name': task.info.get('name', ''),
            'status': "PROGRESS"
        }
    elif task.state != states.FAILURE and task.state != states.REVOKED:
        if task.state == states.SUCCESS and task.info.get('status', '')=='TIMEOUT':
            response = {
            'id':task_id,
            'state': task.info.get('status', ''),
            'name': task.info.get('name', ''),
            'status': task.info.get('status', '')
            }
        else:
            response = {
                'id':task_id,
                'state': task.state,
                'name': task.info.get('name', ''),
                'status': task.info.get('status', ''),
                'verification_time': str(task.info.get('verification_time', ''))+' s',
                'fig_path': get_static_img_popup_link(task.info.get('fig_path', ''), 'view'),
                'hist_file': get_static_new_tab_link(task.info.get('hist_file', ''), 'view'),
                'out_trace': '<center>' \
                             '<ul style="list-style-type: none;"> \
                                <li>'+ get_static_new_tab_link(task.info.get('hist_file', ''),
                                                               '<img src="imgs/text_trace_24x24.png" '
                                                               'title="Open the textual output trace"'
                                                               '>', False) +
                                '</li> \
                                </br> \
                                <li>'+ get_static_img_popup_link(task.info.get('fig_path', ''),
                                                                 '<img src="imgs/show_plot_24x24.png"'
                                                                 'title="Open the graphical output trace"'
                                                                 '>') +
                             '</li> \
                             </ul> \
                             <center>',
                'json_path': get_static_new_tab_link(task.info.get('json_path', ''), 'view'),
                'lisp_path': get_static_new_tab_link(task.info.get('lisp_path', ''), 'view'),
                'result_file': get_static_new_tab_link(task.info.get('result_file', ''), 'view')
            }
            if 'result' in task.info:
                response['result'] = '<center>'+get_outcome_render(task.info['result'])+ '</center>'
                response['state'] = 'COMPLETE'
    else:
        # something went wrong in the background job
        response = {
            'id':task_id,
            'state': task.state,
            'status': str(task.info)  # this is the exception raised
        }
    return response



if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', threaded=True)
