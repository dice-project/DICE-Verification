from tinydb import TinyDB, Query

db = TinyDB('/home/ubuntu/DICE-Verification/d-vert-server/d-vert-json2mc/d4s_27_11/d_vert_db.json')
run = Query()

y10M = db.search(run.cores == 28 and run.input_records == 200000000)
y20M = db.search(run.cores == 28 and run.input_records == 400000000)
y15M = db.search(run.cores == 28 and run.input_records == 300000000)

for t in db.tables():
    print(t)
    tb = db.table(t)
    for row in tb:
        print('time_bound: {} deadline:{}\toutcome: {}\tv_time:  {}\tstarted:{}\tfinished:{}'.format(row['time_bound'],
                                                                                      row['deadline'],
                                                                                      row['outcome'], row['v_time'],
                                                                                      row['start_timestamp']
                                                                                      if 'start_timestamp' in row
                                                                                      else 'Unknown',
                                                                                      row['end_timestamp']
                                                                                      if 'end_timestamp' in row
                                                                                      else 'Unknown'))
