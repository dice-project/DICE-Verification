from tinydb import TinyDB, Query
import argparse

DEFAULT_FILE_PATH = '/home/ubuntu/DICE-Verification/d-vert-server/d-vert-json2mc/d4s_27_11/d_vert_db.json'


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
                                   else 'Unknown'))

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


def show_entire_db(db, display):
    for t in db.tables():
        print('\n ~~~~~~~~~ \n')
        print(t)
        tb = db.table(t)
        display(tb)


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
            print('\n ~~~~~~~~~ \n')
            print(t)
            display(res)


def get_results(args):
    file_path = args.file_path
    tasks = args.tasks
    records = args.records
    cores = args.cores
    app_type = args.benchmark
    outcome = args.outcome
    v_time_limit = args.v_time_limit
    db = TinyDB(file_path)
    display = display_latex if args.latex else display_normal
    print('Sowing results for:\n'
          'app_type == {} & cores == {} & run.input_records == {} & run.tasks == {}'.format(app_type, cores, records, tasks))
    if not tasks and not cores and not records:
        show_entire_db(db, display)
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
    parser.add_argument('-b', '--benchmark', default='kmeans',
                        choices=['pagerank', 'kmeans', 'sort_by_key'],
                        help='the benchmark application to run')
    parser.add_argument('--outcome', default=None,
                        choices=['sat', 'unsat', 'other'],
                        help='show only specific outcomes')
    parser.add_argument('-l', '--latex', action='store_true',
                        help='display row in latex table format')

    parser.set_defaults(func=get_results)
    args = parser.parse_args()

    try:
        getattr(args, "func")
    except AttributeError:
        parser.print_help()
        sys.exit(0)

    args.func(args)
