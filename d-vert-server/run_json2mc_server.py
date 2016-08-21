import os
import random
import time
from flask import Flask, request, render_template, session, flash, redirect, \
    url_for, jsonify, abort
# from flask_mail import Mail, Message
from celery import Celery
from json2mc import json2mc
app = Flask(__name__)

app.config['SECRET_KEY'] = 'top-secret!'

# Celery configuration
app.config['CELERY_BROKER_URL'] = 'redis://redis:6379/0'
#app.config['CELERY_BROKER_URL'] = 'redis://localhost:6379/0'
app.config['CELERY_RESULT_BACKEND'] = 'redis://redis:6379/0'
#app.config['CELERY_RESULT_BACKEND'] = 'redis://localhost:6379/0'
# FROM OBJECT
#app.config.from_object('config')

# Initialize Celery
celery = Celery(app.name, broker=app.config['CELERY_BROKER_URL'])
celery.conf.update(app.config)


@celery.task(bind=True)
def verification_task(self, param, context):
    import matplotlib
    print('Executing task id {0.id}, args: {0.args!r} kwargs: {0.kwargs!r}'
          .format(self.request))
    json2mc.main(["-m", "deliverable_2", "-j", context,
                 "-o", "/tmp/pinellaz-flask"])
    self.update_state(state='PROGRESS', meta={'name': param})
    return {'current': 100, 'total': 100, 'status': 'Task completed!',
            'result': 42, 'name': param}



@celery.task(bind=True)
def long_task(self, param):
    """Background task that runs a long function with progress reports."""
    verb = ['Starting up', 'Booting', 'Repairing', 'Loading', 'Checking']
    adjective = ['master', 'radiant', 'silent', 'harmonic', 'fast']
    noun = ['solar array', 'particle reshaper', 'cosmic ray', 'orbiter', 'bit']
    message = ''
    total = random.randint(10, 50)
    for i in range(total):
        if not message or random.random() < 0.25:
            message = '{0} {1} {2}...{3}'.format(random.choice(verb),
                                                 random.choice(adjective),
                                                 random.choice(noun),
                                                 param)
        self.update_state(state='PROGRESS',
                          meta={'current': i, 'total': total,
                                'status': message, 'name': param})
        time.sleep(1)
    return {'current': 100, 'total': 100, 'status': 'Task completed!',
            'result': 42, 'name': param}



@app.route('/', methods=['GET', 'POST'])
def index():
    if request.method == 'GET':
        return render_template('index.html',
                               param_name=session.get('param_name', ''))
    param_name = request.form['param_name']
    session['param_name'] = param_name
    task = long_task.apply_async(args=[param_name])
    return jsonify({}), 202, {'Location': url_for('taskstatus',
                                                  task_id=task.id)}
    


@app.route('/longtasks', methods=['POST'])
def create_task():
    if not request.json or 'title' not in request.json:
        abort(400)
    params = {
        'title': request.json['title'],
        'description': request.json.get('description', ""),
        'done': False,
        'json_context': request.json["json_context"]
    }
    task = verification_task.apply_async(args=[params["title"],
                                         params["json_context"]])
#    verification_task(params["title"])
    return jsonify({}), 202, {'Location': url_for('taskstatus',
                                                  task_id=task.id)}


@app.route('/status/<task_id>')
def taskstatus(task_id):
    task = long_task.AsyncResult(task_id)
    if task.state == 'PENDING':
        response = {
            'state': task.state,
            'current': 0,
            'total': 1,
            'status': 'Pending...'
        }
    elif task.state != 'FAILURE':
        response = {
            'state': task.state,
            'current': task.info.get('current', 0),
            'total': task.info.get('total', 1),
            'name': task.info.get('name', ''),
            'status': task.info.get('status', '')
        }
        if 'result' in task.info:
            response['result'] = task.info['result']
    else:
        # something went wrong in the background job
        response = {
            'state': task.state,
            'current': 1,
            'total': 1,
            'status': str(task.info)  # this is the exception raised
        }
    return jsonify(response)

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
