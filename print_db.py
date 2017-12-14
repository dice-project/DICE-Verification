from tinydb import TinyDB, Query
import argparse
import os

file_dir = os.path.dirname(os.path.realpath(__file__))
DEFAULT_FILE_PATH = os.path.join(file_dir, 'd-vert-server/d-vert-json2mc/d4s_27_11/d_vert_db.json')

import plotly.plotly as py
import plotly.graph_objs as go
import plotly.offline as offline
import os


def get_layout(title, x_title, y_title, logarithmic=False, y_max=None, min_deadline=None):
    return go.Layout(
        title=title,
        xaxis=dict(
            title=x_title,
            titlefont=dict(
                family='Courier New, monospace',
                size=18,
                color='#7f7f7f'
            )
        ),
        yaxis=dict(
            title=y_title,
            titlefont=dict(
                family='Courier New, monospace',
                size=18,
                color='#7f7f7f'
            ),
            exponentformat='none',
            type='log' if logarithmic else '-',
            autorange=True
        ),
        shapes=[
            # Line Vertical
            {
                'type': 'line',
                'x0': min_deadline,
                'y0': -y_max*0.05,
                'x1': min_deadline,
                'y1': y_max*1.1,
                'line': {
                    'color': 'rgb(0, 0, 0)',
                    'width': 1,
                    'dash': 'dot'
                }
            },
            {
                'type': 'line',
                'x0': 82133,
                'y0': -y_max * 0.05,
                'x1': 82133,
                'y1': y_max * 1.1,
                'line': {
                    'color': 'rgb(0, 0, 0)',
                    'width': 1,
                    'dash': 'dash'
                }
            }
        ] if y_max and min_deadline else []
    )

'''
def plot_figure(data, title, x_axis_label, y_axis_label, out_folder):
    layout = get_layout(title,
                        x_axis_label,
                        y_axis_label)
    fig = go.Figure(data=data, layout=layout)
    # url = py.plot(fig, filename=title, auto_open=False)
    # fig = py.get_figure(url)
    local_path = os.path.abspath(os.path.join(out_folder, '{}.html'.format(fig['layout']['title'])))
    # print("{} -> local: {}".format(url, local_path))
    utils.make_sure_path_exists(os.path.dirname(local_path))
    print("Saving plot in: {}".format(local_path))
    # py.image.save_as(fig, local_path)
    offline.plot(figure_or_data=fig, filename=local_path,
                 # image='png',
                 image_filename=title, auto_open=False)
'''

def scatter_table(conf_name, rows, logarithmic):
    sats = [r for r in rows if r['outcome'] == 'sat']
    unsats = [r for r in rows if r['outcome'] == 'unsat']
    x_sat = [row['deadline'] for row in sats]
    y_sat = [row['v_time'] for row in sats]
    labels_sat = ['outcome: {} - \ntime bound: {}'.format(row['outcome'], row['time_bound']) for row in sats]
    x_unsat = [row['deadline'] for row in unsats]
    y_unsat = [row['v_time'] for row in unsats]
    labels_unsat = ['outcome: {} - \ntime bound: {}'.format(row['outcome'], row['time_bound']) for row in unsats]
    max_y = max(y_sat + y_unsat) if y_sat and y_unsat else None
    rows_w_min_deadline, min_deadline = get_min_sat_deadline(rows)

    local_path = os.path.join('plots', '{}{}.html'.format(conf_name, '_log' if logarithmic else ''))
    trace_text = go.Scatter(
        x=[min_deadline,
           #min_deadline
           ],
        y=[max_y*1.05,
           #- max_y*0.1
           ] if max_y else [0,
                            #0
                            ],
        text=['',
              # min_deadline
              ]if max_y else ['',
                              #''
                              ],
        mode='text',
        showlegend=False
    )
    trace_sat = go.Scatter(
        x=x_sat,
        y=y_sat,
        mode='markers',
        name='Feasible',
        marker=dict(
            size=6,
            color='rgba(0, 255, 0, .9)',
            line=dict(
                width=1,
            )
        ),
        text=labels_sat
    )
    trace_unsat = go.Scatter(
        x=x_unsat,
        y=y_unsat,
        mode='markers',
        name='Unfeasible',
        marker=dict(
            symbol="x",
            size=6,
            color='rgba(255, 0, 0, .9)',
            line=dict(
                width=0.5,
            )
        ),
        text=labels_unsat
    )
    data = [trace_sat, trace_unsat, trace_text]
    layout = get_layout(conf_name,
                        'deadline [ms]',
                        'verification time [s]',
                        logarithmic, max_y, min_deadline)
    fig = go.Figure(data=data, layout=layout)
    offline.plot(figure_or_data=fig, filename=local_path, image_filename=conf_name, auto_open=False)

def display_normal(rows):
    for row in rows:
        print('app_type: {}\t cores:{} time_bound: {} deadline:{}\t'
              'outcome: {}\tv_time:  {}\tstarted:{}\t'
              'finished:{}'.format(row['app_type']
                                   if 'app_type' in row else 'unknown',
                                   row['cores'],
                                   row['time_bound'],
                                   row['deadline'],
                                   row['outcome'], row['v_time'],
                                   row['start_timestamp']
                                   if 'start_timestamp' in row
                                   else 'Unknown',
                                   row['end_timestamp']
                                   if 'end_timestamp' in row
                                   else row['interruption_timestamp']
                                   if 'interruption_timestamp' in row
                                   else'Unknown'))


def get_min_sat_deadline(rows):
    sats = [row for row in rows if row['outcome'] == 'sat']
    rows_w_min_deadline = min_deadline = None
    if sats:
        min_deadline = min(row['deadline'] for row in sats)
        rows_w_min_deadline = [row for row in sats if row['deadline'] == min_deadline]
    return rows_w_min_deadline, min_deadline

def display_latex(rows):
    for row in rows:
        print('{} & {} & {} & {} & {} & {} & {} & {} \\\\ \hline'.format(row['app_type'] if 'app_type' in row else 'pageRank',
                                               row['cores'],
                                               row['tasks'],
                                               row['input_records'],
                                               0,
                                               row['deadline'],
                                               '0\%',
                                               row['v_time']))


def show_entire_db(db, display, logarithmic):
    for t in db.tables():
        print('\n ~~~~~~~~~')
        print(t)
        tb = db.table(t)
        display(tb)
        print('~~~~~~~~~'*5)
        rows_w_min_deadline, min_deadline = get_min_sat_deadline(tb)
        if rows_w_min_deadline:
            print('the minimum SAT deadline is: {},\nobtained with the following experiments:'.format(min_deadline))
            display(rows_w_min_deadline)
        scatter_table(t, tb, logarithmic)



def show_queried_values(db, display, app_type, outcome, cores, tasks, records, v_time_limit):
    for t in db.tables():
        tb = db.table(t)
        run = Query()
        query_conditions = []
        if app_type:
            query_conditions.append((run.app_type == app_type))
        if outcome:
            if outcome == 'other':
                query_conditions.append(~(run.outcome == 'sat') & ~(run.outcome == 'unsat'))
            else:
                query_conditions.append((run.outcome == outcome))
        if cores:
            query_conditions.append((run.cores == cores))
        if tasks:
            query_conditions.append((run.tasks == tasks))
        if records:
            query_conditions.append((run.input_records == records))
        if v_time_limit:
            query_conditions.append((run.v_time <= v_time_limit))
        query = reduce(lambda a, b: a & b, query_conditions)
        res = tb.search(query)
        if res:
            print('\n ~~~~~~~~~')
            print(t)
            display(res)
            print('~~~~~~~~~' * 5)
            rows_w_min_deadline, min_deadline = get_min_sat_deadline(res)
            if rows_w_min_deadline:
                print('the minimum SAT deadline is: {},\nobtained with the following experiments:'.format(min_deadline))
                display(rows_w_min_deadline)


def get_results(args):
    file_path = args.file_path
    tasks = args.tasks
    records = args.records
    cores = args.cores
    app_type = args.benchmark
    outcome = args.outcome
    v_time_limit = args.v_time_limit
    logarithmic = args.log
    db = TinyDB(file_path)
    display = display_latex if args.latex else display_normal
    print('Sowing results for:\n'
          'app_type == {} & cores == {} & run.input_records == {} & run.tasks == {}'.format(app_type, cores, records, tasks))
    if not tasks and not cores and not records and not app_type and not outcome:
        show_entire_db(db, display, logarithmic)
    else:
        show_queried_values(db=db, display=display, app_type=app_type, outcome=outcome, cores=cores, tasks=tasks,
                            records=records, v_time_limit=v_time_limit)




if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description=
        """
        print DB
        """
    )

    parser.add_argument("-c", "--cores", type=int,
                        help="cores"
                             "[default: %(default)s]")

    parser.add_argument("-t", "--tasks", dest="tasks", type=int,
                            help="number of tasks for each stage"
                                 "[default: %(default)s]")

    parser.add_argument("-v", "--v-time-limit", dest="v_time_limit", type=int,
                        help="show only rows with verification time lower that v_time_limit"
                             "[default: %(default)s]")

    parser.add_argument("-i", "--input-records", dest="records", type=int,
                            help="input records"
                                 "[default: %(default)s]")
    parser.add_argument("-f", "--file", dest="file_path", type=str,
                        default=DEFAULT_FILE_PATH,
                        help="file path [default: %(default)s]")
    parser.add_argument('-b', '--benchmark', default=None,
                        choices=['pagerank', 'kmeans', 'sort_by_key'],
                        help='the benchmark application to run')
    parser.add_argument('--outcome', default=None,
                        choices=['sat', 'unsat', 'running', 'other'],
                        help='show only specific outcomes')
    parser.add_argument('-l', '--latex', action='store_true',
                        help='display row in latex table format')
    parser.add_argument('--log', action='store_true',
                        help='plots yaxis in logaritmic format')

    parser.set_defaults(func=get_results)
    args = parser.parse_args()

    try:
        getattr(args, "func")
    except AttributeError:
        parser.print_help()
        sys.exit(0)

    args.func(args)
