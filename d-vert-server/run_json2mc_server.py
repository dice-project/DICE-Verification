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


my_ip = 'localhost' if os.environ.get('LOCAL_DEPLOY', 'true') == 'true' else urlopen('http://ip.42.pl/raw').read()

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
app.config['CELERY_BROKER_URL'] = 'redis://redis:6379/0'
#app.config['CELERY_BROKER_URL'] = 'redis://localhost:6379/0'
app.config['CELERY_RESULT_BACKEND'] = 'redis://redis:6379/0'
#app.config['CELERY_RESULT_BACKEND'] = 'redis://localhost:6379/0'
# FROM OBJECT
#app.config.from_object('config')
app.config.update(
     CELERY_ACCEPT_CONTENT = ['json'],
     CELERY_TASK_SERIALIZER = 'json',
     CELERY_RESULT_SERIALIZER = 'json',
)


# Initialize Celery
celery = Celery(app.name, broker=app.config['CELERY_BROKER_URL'], backend=app.config['CELERY_RESULT_BACKEND'])
celery.conf.update(app.config)

redis = Redis('redis')

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

        outcome = v_task.verification_result.outcome
        ver_time = v_task.verification_result.verification_time
        hist_file = os.path.join(v_task.result_dir, v_task.hist_file)
        fig_path = v_task.figure_path
        lisp_path = os.path.join(v_task.result_dir, "zot_in.lisp")
        result_file = os.path.join(v_task.result_dir, v_task.result_file)
        json_path = v_task.json_context_path
        return {'name': task_name, 'result': outcome.upper(),
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
    if request.method == 'GET':
        return render_template('index.html',
                               param_name=session.get('param_name', ''),
                               hostname=my_ip,
                               flask_port=5000,
                               flower_port=5555)
 
    

@app.route('/tasks/<path:path>')
def send_js(path):
    return send_from_directory(app.static_folder+'/tasks', path, as_attachment=False)


@app.route('/task_list', methods=['GET'])
def get_task_list():
    my_list = redis.keys('celery-task-meta-*')
    statuslist = []
    print my_list
    for t in my_list:
        statuslist.append(taskstatus2(remove_prefix(t,'celery-task-meta-')))
    # print my_list
    return jsonify(statuslist), 200
    
def remove_prefix(s, prefix):
    return s[len(prefix):] if s.startswith(prefix) else s


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
    return jsonify({}), 202, {'Location': url_for('taskstatus',
                                                  task_id=task.id)}



@app.route('/status/<task_id>')
def taskstatus(task_id):
    return jsonify(taskstatus2(task_id))

def get_static_img_popup_link(fig_path, link_text):
    print 'fig_path:',fig_path, "link_text:",link_text
    if fig_path is None:
        return 'N/A'
    elif fig_path == '':
        return 'N/A'
    else:
        # return '<a class="popup-img" href="'+url_for('static', filename=get_static_url(fig_path))+'"> '+ link_text + '</a>'
        return '<a class="popup-img" href="{}">{}</a>'.format(url_for('static', filename=get_static_url(fig_path)),
                                                                 link_text)

def get_static_new_tab_link(file_path, link_text):
#    print 'file_path:',file_path, "link_text:",link_text
    if file_path is None:
        return 'N/A'
    elif file_path == '':
        return 'N/A'
    else:
        return '<a href="{}" target="_blank"> {} <img src="imgs/new_tab.png"></a>'.format(url_for('static', filename=get_static_url(file_path)),
                                                                                          link_text)
                                                                    

def get_static_current_tab_link(file_path, link_text):
#    print 'file_path:',file_path, "link_text:",link_text
    if file_path is None:
        return 'N/A'
    elif file_path == '':
        return 'N/A'
    else:
        return '<a href="{}"> {} </a>'.format(url_for('static', filename=get_static_url(file_path)),
                                              link_text)

def get_outcome_render(outcome):
    if outcome == 'SAT':
        return outcome + ' <img src="imgs/warning_16x16.png"'
    elif outcome == 'UNSAT':
        return outcome + ' <img src="imgs/success_16x16.png"'
    else:
        return outcome + ' <img src="imgs/question_mark_16x16.png"'

def taskstatus2(task_id):
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
                'out_trace': '<ul style="list-style-type: none;"> \
                                <li>'+ get_static_new_tab_link(task.info.get('hist_file', ''), 'text') +'</li> \
                                <li>'+ get_static_img_popup_link(task.info.get('fig_path', ''), 'figure') +'</li> \
                            </ul>',
                'json_path': get_static_new_tab_link(task.info.get('json_path', ''), 'view'),
                'lisp_path': get_static_current_tab_link(task.info.get('lisp_path', ''), 'download'),
                'result_file': get_static_new_tab_link(task.info.get('result_file', ''), 'view')
            }
            if 'result' in task.info:
                response['result'] = get_outcome_render(task.info['result'])
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
    app.run(debug=True, host='0.0.0.0')
