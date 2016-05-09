(asdf:operate 'asdf:load-op 'ae2sbvzot)
(use-package :trio-utils)

(defun getReciprocalGZ(x)
  (if (> x 0) (/ 1.0 x) 0.0))

(defun range (max &key (min 1) (step 1))
 (loop for n from min to max by step
    collect n))

(defun getIntervals (max alpha tolerance)
    (mapcar #'(lambda (l) (list (-(* l alpha) tolerance)(+ (* l alpha) tolerance))) (range max)))

(defun getRateIntervals(max); RETURNS LIST: C_TAKE * [1/3 2/3 1]
  			(mapcar #'(lambda (l) (floor(* l max))) (list (/ 1.0 3.0) (/ 2.0 3.0) 1 1.1)))

(defun getTimeIntervals(max alpha); RETURNS LIST: C_TAKE * [1/3 2/3 1]
  ;(append (list alpha) (mapcar #'(lambda (l) (floor(* l max alpha))) (list (/ 1.0 3.0) (/ 2.0 3.0) 1))))
  (append (list alpha) (mapcar #'(lambda (l) (floor(* l alpha))) (getRateIntervals max))))
;(defun getTimeIntervals(max alpha);  RETURNS LIST: 1/6*C_TAKE*ALPHA * [1 3 5 7 10]
 ; 			(append(mapcar #'(lambda (l) (floor(* 0.5 l (* (/ max 3.0) alpha)))) '(1 3 5 7 10))) )



;TOPOLOGY DEFINITION
  (defconstant the-spouts '(wpSpout))
  (defconstant the-bolts '(WpDeserializer expander articleExtraction mediaExtraction))

	(defvar the-topology-table)
	(setq the-topology-table (make-hash-table :test 'equalp))

  
;   use this if grouping is taken into account
;	(setf (gethash 'WpDeserializer the-topology-table) '())
    (setf (gethash 'WpDeserializer the-topology-table) '(wpSpout))
  
;   use this if grouping is taken into account
;	(setf (gethash 'expander the-topology-table) '())
    (setf (gethash 'expander the-topology-table) '(WpDeserializer))
  
;   use this if grouping is taken into account
;	(setf (gethash 'articleExtraction the-topology-table) '())
    (setf (gethash 'articleExtraction the-topology-table) '(expander))
  
;   use this if grouping is taken into account
;	(setf (gethash 'mediaExtraction the-topology-table) '())
    (setf (gethash 'mediaExtraction the-topology-table) '(expander))
  

(defconstant INIT_QUEUES 4)

;NEW --> ACTUAL RATES DEFINITION	TODO:CHECK IF IT MAKES SENSE
;	(defconstant BASE_QUANTITY 10	)
  (defconstant BASE_QUANTITY 10)
;	(defconstant AVG_EMIT_RATE_S1 0.2)
;	(defconstant AVG_EMIT_RATE_S2 0.2)
  
  	(defconstant AVG_EMIT_RATE_wpSpout 1.0)
  


;;	SPOUT AVG EMITTING RATES
;	(defconstant C_EMIT_S1 10.0)
;	(defconstant C_EMIT_S2 20.0)
;	(defconstant MIN_WAIT_FOR_EMIT_S1 100)
;	(defconstant MIN_WAIT_FOR_EMIT_S2 100)
  
  	(defconstant C_EMIT_wpSpout BASE_QUANTITY)
  	(defconstant MIN_WAIT_FOR_EMIT_wpSpout (/ BASE_QUANTITY AVG_EMIT_RATE_wpSpout))
    (defconstant ALPHA_wpSpout (getReciprocalGZ AVG_EMIT_RATE_wpSpout))
  


;;	BOLTS MAX TAKE RATES, SIGMA AND D



    (defconstant C_TAKE_WpDeserializer 4)


    (defconstant MAX_PROC_RATE_WpDeserializer (/ C_TAKE_WpDeserializer 0.5))

	(defconstant SIGMA_WpDeserializer 1.0)
	(defconstant D_WpDeserializer 0.0)
    (defconstant SIGMA_REC_WpDeserializer (getReciprocalGZ SIGMA_WpDeserializer))
	(defconstant MIN_TTF_WpDeserializer 1000)

    (defconstant ALPHA_WpDeserializer 0.5)



    (defconstant C_TAKE_expander 10)


    (defconstant MAX_PROC_RATE_expander (/ C_TAKE_expander 0.2))

	(defconstant SIGMA_expander 0.4)
	(defconstant D_expander 0.0)
    (defconstant SIGMA_REC_expander (getReciprocalGZ SIGMA_expander))
	(defconstant MIN_TTF_expander 1000)

    (defconstant ALPHA_expander 0.2)



    (defconstant C_TAKE_articleExtraction 4)


    (defconstant MAX_PROC_RATE_articleExtraction (/ C_TAKE_articleExtraction 0.05))

	(defconstant SIGMA_articleExtraction 1.0)
	(defconstant D_articleExtraction 0.0)
    (defconstant SIGMA_REC_articleExtraction (getReciprocalGZ SIGMA_articleExtraction))
	(defconstant MIN_TTF_articleExtraction 1000)

    (defconstant ALPHA_articleExtraction 0.05)



    (defconstant C_TAKE_mediaExtraction 4)


    (defconstant MAX_PROC_RATE_mediaExtraction (/ C_TAKE_mediaExtraction 0.05))

	(defconstant SIGMA_mediaExtraction 1.0)
	(defconstant D_mediaExtraction 0.0)
    (defconstant SIGMA_REC_mediaExtraction (getReciprocalGZ SIGMA_mediaExtraction))
	(defconstant MIN_TTF_mediaExtraction 1000)

    (defconstant ALPHA_mediaExtraction 0.05)




    ;RATE THRESHOLDS
    (defvar the-rate-threshold-table)
    (setq the-rate-threshold-table (make-hash-table :test 'equalp))
  ;PROCESSING TIMES
    (defvar the-proc-time-table)
    (setq the-proc-time-table (make-hash-table :test 'equalp))
    ;PROCESSING TIMES
      (defvar the-proc-time-table-2)
      (setq the-proc-time-table-2 (make-hash-table :test 'equalp))




;	(setf (gethash 'WpDeserializer the-rate-threshold-table) (getRateIntervals C_TAKE_WpDeserializer))
;	(setf (gethash 'WpDeserializer the-proc-time-table-2) (getTimeIntervals C_TAKE_WpDeserializer ALPHA_WpDeserializer)) ;OLDversion
    (setf (gethash 'WpDeserializer the-proc-time-table) (list (- ALPHA_WpDeserializer (/ ALPHA_WpDeserializer 10.0)) (+ ALPHA_WpDeserializer (/ ALPHA_WpDeserializer 10.0))))

;	(setf (gethash 'expander the-rate-threshold-table) (getRateIntervals C_TAKE_expander))
;	(setf (gethash 'expander the-proc-time-table-2) (getTimeIntervals C_TAKE_expander ALPHA_expander)) ;OLDversion
    (setf (gethash 'expander the-proc-time-table) (list (- ALPHA_expander (/ ALPHA_expander 10.0)) (+ ALPHA_expander (/ ALPHA_expander 10.0))))

;	(setf (gethash 'articleExtraction the-rate-threshold-table) (getRateIntervals C_TAKE_articleExtraction))
;	(setf (gethash 'articleExtraction the-proc-time-table-2) (getTimeIntervals C_TAKE_articleExtraction ALPHA_articleExtraction)) ;OLDversion
    (setf (gethash 'articleExtraction the-proc-time-table) (list (- ALPHA_articleExtraction (/ ALPHA_articleExtraction 10.0)) (+ ALPHA_articleExtraction (/ ALPHA_articleExtraction 10.0))))

;	(setf (gethash 'mediaExtraction the-rate-threshold-table) (getRateIntervals C_TAKE_mediaExtraction))
;	(setf (gethash 'mediaExtraction the-proc-time-table-2) (getTimeIntervals C_TAKE_mediaExtraction ALPHA_mediaExtraction)) ;OLDversion
    (setf (gethash 'mediaExtraction the-proc-time-table) (list (- ALPHA_mediaExtraction (/ ALPHA_mediaExtraction 10.0)) (+ ALPHA_mediaExtraction (/ ALPHA_mediaExtraction 10.0))))


 ;TODO risistemare
	(setf (gethash 'wpSpout the-rate-threshold-table) (getRateIntervals 10))
	;(setf (gethash 'wpSpout the-proc-time-table) (getTimeIntervals 10 ALPHA_wpSpout))
    (setf (gethash 'wpSpout the-proc-time-table) (getIntervals 10 ALPHA_wpSpout (/ ALPHA_wpSpout 10.0)))


;TOPOLOGY-INDEPENDENT PARAMETERS: TODO DEFINE BOLT-SPECIFIC VALUES

  (defconstant MIN_REBOOT_TIME 10)
  (defconstant MAX_REBOOT_TIME 100)
;  (defconstant MIN_IDLE_TIME )
  (defconstant MAX_IDLE_TIME 1.0)

  (defconstant QUEUE_THRESHOLD 0)


	(defconstant MAX_TIME 20000)

	(defconstant orig
		(-P- O))

;UTILITY FUNCTIONS

(defun plus (lst)
            (if (car lst)
                (if(cdr lst) (list '[+] (car lst) (plus (cdr lst)))
                             (car lst))
                 0)
          )



;MACROS
(defmacro singleBoltsBehaviour(bolts)
`(&&
  ,@(nconc
    (loop for i in bolts collect
       `(->
         (-P- ,(format nil "TAKE_~S" i))
         (&&
           (-P- ,(format nil "PROCESS_~S" i))
           (!! (-P- ,(format nil "EMIT_~S" i)))
;           (!! (-P- ,(format nil "IDLE_~S" i))) ; TODO: CHECK IF NEEDED
           (next
             (until
               (!! (-P- ,(format nil "TAKE_~S" i)))
               (||
                 (-P- ,(format nil "EMIT_~S" i))
                 (-P- ,(format nil "FAIL_~S" i)))))
           (yesterday
             (since
               (!!(-P- ,(format nil "PROCESS_~S" i)))
               (||
                 (-P- ,(format nil "EMIT_~S" i)) ; TODO CHECK THIS
                 (-P- ,(format nil "FAIL_~S" i))
                 orig))))))
      (loop for i in bolts collect
         `(->
           (-P- ,(format nil "PROCESS_~S" i))
           (&&
             (!! (-P- ,(format nil "FAIL_~S" i)))
             (since
               (-P- ,(format nil "PROCESS_~S" i))
               (||
                 (-P- ,(format nil "TAKE_~S" i))
                 (&&
                  	orig
                   (-P- ,(format nil "PROCESS_~S" i)))))
             (until
               (-P- ,(format nil "PROCESS_~S" i))
               (||
                 (-P- ,(format nil "EMIT_~S" i))
                 (-P- ,(format nil "FAIL_~S" i)))))))
        (loop for i in bolts collect
           `(->
             (-P- ,(format nil "EMIT_~S" i))
             (&&
               (-P- ,(format nil "PROCESS_~S" i))
               (next
                 (until
                   (!! (-P- ,(format nil "PROCESS_~S" i)))
                   (-P- ,(format nil "TAKE_~S" i))))
               (||
                 orig
                 (yesterday
                   (since
                     (!! (-P- ,(format nil "EMIT_~S" i)))
                     (-P- ,(format nil "TAKE_~S" i))))))))
      (loop for i in bolts collect
       `(somf
            (-P- ,(format nil "TAKE_~S" i))))
;STARTFAILURE
			(loop for i in bolts collect
       `(<->
					(-P- ,(format nil "STARTFAILURE_~S" i))
					(&& (-P- ,(format nil "FAIL_~S" i))
							(!! (yesterday (-P- ,(format nil "FAIL_~S" i)))))))
;SINGLE BOLT FAILING new
    (loop for i in bolts collect
     	`(->
    			 (-P- ,(format nil "STARTFAILURE_~S" i))
    			 (&&
    				 ,@(loop for j in bolts when
       					 (not (eq j i)) collect
      						 `(!! (-P- ,(format nil "STARTFAILURE_~S" j)))
      						 )))
 				 )
;IDLE (OLD) --> NOOP (just for debugging traces)
			(loop for i in bolts collect
             `(<->
					(-P- ,(format nil "NOOP_~S" i))
					(&& (!!	(-P- ,(format nil "FAIL_~S" i)))
							(!! (-P- ,(format nil "PROCESS_~S" i))))))

(loop for i in bolts collect ;TODO: RIDONDANTE?
    `(->
			(-P- ,(format nil "STARTIDLE_~S" i))
			(&&
				(-P- ,(format nil "IDLE_~S" i))
                (!! (yesterday (-P- ,(format nil "IDLE_~S" i)))))))

(loop for i in bolts collect
`(<->
			(-P- ,(format nil "STARTIDLE_~S" i))
			(||
				(&&		;	case EMIT && NEXT(Q>0)
					 (-P- ,(format nil "EMIT_~S" i))
					 (next ([>] (-V- ,(intern (format nil "Q_~S" i))) 0)))
				 (&&		; case NOT FAIL && NOT PROCESS Q>0 AND NOT YESTERDAY(EMIT OR Q>0)
					 (!! (-P- ,(format nil "FAIL_~S" i)))
					 (!! (-P- ,(format nil "PROCESS_~S" i)))
					 ([>] (-V- ,(intern (format nil "Q_~S" i))) 0)
					 (!! (yesterday
                            (||
								 ([>] (-V- ,(intern (format nil "Q_~S" i))) 0)
								 (-P- ,(format nil "EMIT_~S" i)))))))))

	;IDLE (NEW)
	(loop for i in bolts collect
		 `(->
			 (-P- ,(format nil "IDLE_~S" i))
			 (&&
				 (!! (-P- ,(format nil "FAIL_~S" i)))
				 (since
					 (-P- ,(format nil "IDLE_~S" i))
					 (-P- ,(format nil "STARTIDLE_~S" i)))
				 (until
					 (-P- ,(format nil "IDLE_~S" i))
					 (||
						 (-P- ,(format nil "TAKE_~S" i))
						 (-P- ,(format nil "FAIL_~S" i)))))))

    )
 )
)

;TODO inserire processing-time()


(defun t-process (j sign c)
    (let
  		((fmla0 (cond
  					((string= sign "<") `([<] (-V-,(intern (format nil "PT_~S_0" j))) ,c))
  					((string= sign "<=") `([<=] (-V-,(intern (format nil "PT_~S_0" j))) ,c))
  					((string= sign ">") `([>] (-V-,(intern (format nil "PT_~S_0" j))) ,c))
  					((string= sign ">=") `([>=] (-V-,(intern (format nil "PT_~S_0" j))) ,c))
  					((string= sign "=") `([=] (-V-,(intern (format nil "PT_~S_0" j))) ,c))))
  		 (fmla1 (cond
  					((string= sign "<") `([<] (-V-,(intern (format nil "PT_~S_1" j))) ,c))
  					((string= sign "<=") `([<=] (-V-,(intern (format nil "PT_~S_1" j))) ,c))
  					((string= sign ">") `([>] (-V-,(intern (format nil "PT_~S_1" j))) ,c))
  					((string= sign ">=") `([>=] (-V-,(intern (format nil "PT_~S_1" j))) ,c))
  					((string= sign "=") `([=] (-V-,(intern (format nil "PT_~S_1" j))) ,c)))))
  		`(&&
  			(->
  				(&&
  					([>] (-V-,(intern (format nil "PT_~S_0" j))) 0)
  					(||
  						([=] (-V-,(intern (format nil "PT_~S_1" j))) 0)
  						(since
  							([>] (-V-,(intern (format nil "PT_~S_1" j))) 0)
  							([=] (-V-,(intern (format nil "PT_~S_0" j))) 0))))
  				,fmla0)
  			(->
  				(&&
  					([>] (-V-,(intern (format nil "PT_~S_1" j))) 0)
  					(||
  						([=] (-V-,(intern (format nil "PT_~S_0" j))) 0)
  						(since
  							([>] (-V-,(intern (format nil "PT_~S_0" j))) 0)
  							([=] (-V-,(intern (format nil "PT_~S_1" j))) 0))))
  				,fmla1)))
        )




(defmacro clocks-behaviour (spouts bolts times)
;f5
	`(&&
		,@(loop for j in (append spouts bolts) collect
			`(&&
					(->
						([=] (-V-,(intern (format nil "PT_~S_0" j))) 0)
						(&&
							(next
								(release
									([=] (-V-,(intern (format nil "PT_~S_1" j))) 0)
									([>] (-V-,(intern (format nil "PT_~S_0" j))) 0)))
							([>] (-V-,(intern (format nil "PT_~S_1" j))) 0)))

					(->
						([=] (-V-,(intern (format nil "PT_~S_1" j))) 0)
						(&&
							(next
								(release
									([=] (-V-,(intern (format nil "PT_~S_0" j))) 0)
									([>] (-V-,(intern (format nil "PT_~S_1" j))) 0)))))))

;f6
		,@(loop for j in bolts collect
				;CLOCK RESET CONDITION
				`(&&
					(<->
						(||
							([=] (-V-,(intern (format nil "PT_~S_0" j))) 0)
							([=] (-V-,(intern (format nil "PT_~S_1" j))) 0))
						(||
							orig
       				(-P-,(intern (format nil "TAKE_~S" j)))
		          (&& (-P-,(intern (format nil "FAIL_~S" j))) (!! (yesterday (-P-,(intern (format nil "FAIL_~S" j))))))
       				(&& (-P-,(intern (format nil "IDLE_~S" j))) (!! (yesterday (-P-,(intern (format nil "IDLE_~S" j))))))
		          ))
					(<->
							([=] (-V-,(intern (format nil "CLOCK_BF_~S" j))) 0)
		          (&& (!! (-P-,(intern (format nil "FAIL_~S" j)))) (!! (yesterday (!! (-P-,(intern (format nil "FAIL_~S" j))))))))
			;PROCESSING DURATION - SINGLE INTERVAL
					(->
							(-P- ,(intern (format nil "PROCESS_~S" j)))
						(until
							(&&
								(-P-,(intern (format nil "PROCESS_~S" j)))
								(!!(-P-,(intern (format nil "EMIT_~S" j)))))
								(&&
									,(t-process j ">=" (first (gethash j times)))
									,(t-process j "<=" (second (gethash j times)))
									(||
										(-P-,(intern (format nil "FAIL_~S" j)))
										(-P-,(intern (format nil "EMIT_~S" j)))))))
			;IDLE DURATION
		      (->
	           		(-P-,(intern (format nil "IDLE_~S" j)))
		        (until
	           		(-P-,(intern (format nil "IDLE_~S" j)))
		          (&&
;		            ,(t-process j ">=" (intern (format nil "MIN_IDLE_TIME_~S" j)))
;								,(t-process j ">" MIN_IDLE_TIME)
								,(t-process j "<=" MAX_IDLE_TIME)
		            (||
               			(-P-,(intern (format nil "TAKE_~S" j)))
                  	(-P-,(intern (format nil "FAIL_~S" j)))))))


				;FAILURE DURATION
		      (->
		        (-P-,(intern (format nil "FAIL_~S" j)))
		        (until
		          (-P-,(intern (format nil "FAIL_~S" j)))
		          (&&
		            ,(t-process j ">" MIN_REBOOT_TIME)
								,(t-process j "<" MAX_REBOOT_TIME)
		            (!!(-P-,(intern (format nil "FAIL_~S" j)))))))

 				;TIME BETWEEN FAILURES
		      (->
		        (!! (-P-,(intern (format nil "FAIL_~S" j))))
		        (until
		          (!! (-P-,(intern (format nil "FAIL_~S" j))))
		          (&&
								([>] (-V-,(intern (format nil "CLOCK_BF_~S" j))) ,(intern (format nil "MIN_TTF_~S" j)))
;		            (-P-,(intern (format nil "FAIL_~S" j)))
        )))
       ))
;		,@(loop for j in spouts collect
;			`(&&
;				(<->
;					(||
;						([=] (-V-,(intern (format nil "PT_~S_0" j))) 0)
;						([=] (-V-,(intern (format nil "PT_~S_1" j))) 0))
;					(-P-,(intern (format nil "EMIT_~S" j))))
;				(->
;					(-P-,(intern (format nil "EMIT_~S" j)))
;					(next
;						(until
;							(!! (-P-,(intern (format nil "EMIT_~S" j))))
;							,(t-process j ">" (intern (format nil "MIN_WAIT_FOR_EMIT_~S" j))))))


    ;))
			))
; CREATES C_TAKE_X PROPOSITIONS. FOR EACH VALUE OF R_EMIT THE WAITING TIME IS INCLUDED IN THE RELATED INTERVAL (ALPHA*R_EMIT +/- TOLERANCE)
(defun emitting-time(j times)
  (loop for i in (range (eval(intern (format nil "C_EMIT_~S" j)))) collect
  `(->
   (&&
    (-P- ,(intern (format nil "EMIT_~S" j)))
    ([=] (-V-,(intern (format nil "L_EMIT_~S" j))) ,i))
      (next
       (until
          (!!(-P-,(intern (format nil "EMIT_~S" j))))
          (&&
              ,(t-process j ">=" (first (nth (- i 1) (gethash j times))))
              ,(t-process j "<=" (second (nth (- i 1) (gethash j times))))
                  (-P-,(intern (format nil "EMIT_~S" j)))))))))

(defmacro spoutClocksBehaviour(spouts times)
`(&&
,@(loop for j in spouts collect
  `(&&
    (<->
      (||
        ([=] (-V-,(intern (format nil "PT_~S_0" j))) 0)
        ([=] (-V-,(intern (format nil "PT_~S_1" j))) 0))
      ;(||
       (-P-,(intern (format nil "EMIT_~S" j)))
      ; orig)
      )
      ;CONSTRAINT ON SPOUT NOT EMITTING
    (->
      (since
        (!!(-P-,(intern (format nil "EMIT_~S" j))))
        orig)
      (until
        (&&
          (!!(-P-,(intern (format nil "EMIT_~S" j)))))
          (&&
            ,(t-process j "<=" (first(first (gethash j times))))
            (-P-,(intern (format nil "EMIT_~S" j))))))
    ,@(emitting-time j times)))))



(defmacro spoutClocksBehaviour1(spouts rates times)
`(&&
  ,@(loop for j in spouts collect
    `(&&
      (<->
        (||
          ([=] (-V-,(intern (format nil "PT_~S_0" j))) 0)
          ([=] (-V-,(intern (format nil "PT_~S_1" j))) 0))
        ;(||
         (-P-,(intern (format nil "EMIT_~S" j)))
        ; orig)
        )
  ;    (->
  ;      (-P-,(intern (format nil "EMIT_~S" j)))
  ;      (next
  ;        (until
  ;          (!! (-P-,(intern (format nil "EMIT_~S" j))))
  ;          ,(t-process j ">" (intern (format nil "MIN_WAIT_FOR_EMIT_~S" j))))))

    ;CONSTRAINT ON SPOUT NOT EMITTING
    ;  (->
    ;    (since
    ;      (!!(-P-,(intern (format nil "EMIT_~S" j))))
    ;      orig)
    ;    (until
    ;      (&&
    ;        (!!(-P-,(intern (format nil "EMIT_~S" j)))))
    ;        (&&
    ;          ,(t-process j "<" (second (gethash j times)))
    ;          (-P-,(intern (format nil "EMIT_~S" j))))))

  ;WAIT FOR SPOUT EMIT - INTERVAL 1
      (->
        (&&
          (-P- ,(intern (format nil "EMIT_~S" j)))
          ([>=] (-V-,(intern (format nil "R_EMIT_~S" j))) ,(third (gethash j rates))))
        (next
         (until
          (&&
            (!!(-P-,(intern (format nil "EMIT_~S" j)))))
            (&&
              ,(t-process j ">=" (fourth (gethash j times)))
              ,(t-process j "<" (fifth (gethash j times)))
              (-P-,(intern (format nil "EMIT_~S" j)))))))
  ;WAIT FOR SPOUT EMIT - INTERVAL 2
      (->
        (&&
          (-P- ,(intern (format nil "EMIT_~S" j)))
          ([>=] (-V-,(intern (format nil "R_EMIT_~S" j))) ,(second (gethash j rates)))
          ([<] (-V-,(intern (format nil "R_EMIT_~S" j))) ,(third (gethash j rates))))
          (next
           (until
            (&&
              (!!(-P-,(intern (format nil "EMIT_~S" j)))))
              (&&
                ,(t-process j ">=" (third (gethash j times)))
                ,(t-process j "<" (fourth (gethash j times)))
                  (-P-,(intern (format nil "EMIT_~S" j)))))))
  ;WAIT FOR SPOUT EMIT - INTERVAL 3
      (->
        (&&
          (-P- ,(intern (format nil "EMIT_~S" j)))
          ([>=] (-V-,(intern (format nil "R_EMIT_~S" j))) ,(first (gethash j rates)))
          ([<] (-V-,(intern (format nil "R_EMIT_~S" j))) ,(second (gethash j rates))))
          (next
           (until
            (&&
              (!!(-P-,(intern (format nil "EMIT_~S" j)))))
              (&&
                ,(t-process j ">=" (second (gethash j times)))
                ,(t-process j "<" (third (gethash j times)))
                  (-P-,(intern (format nil "EMIT_~S" j)))))))
  ;WAIT FOR SPOUT EMIT - INTERVAL 4
      (->
        (&&
          (-P- ,(intern (format nil "EMIT_~S" j)))
          ([>] (-V-,(intern (format nil "R_EMIT_~S" j))) 0)
          ([<] (-V-,(intern (format nil "R_EMIT_~S" j))) ,(first (gethash j rates))))
          (next
           (until
            (&&
              (!!(-P-,(intern (format nil "EMIT_~S" j)))))
              (&&
                ,(t-process j ">=" (first (gethash j times)))
                ,(t-process j "<" (second (gethash j times)))
                  (-P-,(intern (format nil "EMIT_~S" j)))))))))))



(defmacro singleQueueBehaviour(bolts topology-table)
`(&&
  ,@(nconc
    (loop for j in bolts collect
       `(<->
         (-P- ,(format nil "ADD_~S" j))
         (||
         ,@(loop for i in (gethash j topology-table) collect
             `(-P- ,(format nil "EMIT_~S" i))))))
    (loop for j in bolts collect
      `(->
        (&&
          (-P- ,(format nil "ADD_~S" j))
          (!! (-P- ,(format nil "TAKE_~S" j)))
          (!! (-P- ,(format nil "STARTFAILURE_~S" j))))
        ([=] (next ( -V-,(intern (format nil "Q_~S" j))))
          ([+]( -V-,(intern (format nil "Q_~S" j))) ( -V-,(intern (format nil "R_ADD_~S" j)))))))
    (loop for j in bolts collect
      `(->
          (-P- ,(format nil "STARTFAILURE_~S" j))
          ([=] (next ( -V-,(intern (format nil "Q_~S" j)))) 0)))
;    (loop for j in bolts collect ;-->ELIMINATO
;      `(->
;        (&&
;          (!! (-P- ,(format nil "ADD_~S" j)))
;          (-P- ,(format nil "TAKE_~S" j))
;					(!! (-P- ,(format nil "STARTFAILURE_~S" j))))
;        (&&
;          ([=] (next ( -V-,(intern (format nil "Q_~S" j))))
;            ([-]( -V-,(intern (format nil "Q_~S" j))) ( -V-,(intern (format nil "R_TAKE_~S" j)))))
;          ([>]( -V-,(intern (format nil "Q_~S" j))) ( -V-,(intern (format nil "R_TAKE_~S" j)))))))
		(loop for j in bolts collect ;DECREMENTO IN CASO DI ADD+TAKE NON ERA CORRETTO -->MODIFICARE PDF!
      `(->
        (-P- ,(format nil "TAKE_~S" j))
        (&&
          ([=] (next ( -V-,(intern (format nil "Q_~S" j))))
            ([-]
							([+]( -V-,(intern (format nil "Q_~S" j)))( -V-,(intern (format nil "R_ADD_~S" j))))
							( -V-,(intern (format nil "R_PROCESS_~S" j)))))
;REDUNDANT
;            ([>=] ([+]( -V-,(intern (format nil "Q_~S" j)))( -V-,(intern (format nil "R_ADD_~S" j))))
;		          ( -V-,(intern (format nil "R_PROCESS_~S" j))))
          )))

;NECESSARY CONDITIONS
    (loop for j in bolts collect
      `(->
        ([>] (next ( -V-,(intern (format nil "Q_~S" j)))) ( -V-,(intern (format nil "Q_~S" j))))
        (-P- ,(format nil "ADD_~S" j))))
    (loop for j in bolts collect
      `(->
        ([<] (next ( -V-,(intern (format nil "Q_~S" j)))) ( -V-,(intern (format nil "Q_~S" j))))
        (||
          (-P- ,(format nil "TAKE_~S" j))
          (-P- ,(format nil "STARTFAILURE_~S" j)))))
  )))





(defmacro ratesBehaviour(spouts bolts topology-table)
`(&&
  ,@(nconc
    (loop for j in bolts collect
      `(&&
        ([>=] ( -V-,(intern (format nil "R_ADD_~S" j))) 0)
      ;  ([>=] ( -V-,(intern (format nil "R_TAKE_~S" j))) 0)
        ([>=] ( -V-,(intern (format nil "R_EMIT_~S" j))) 0)
        ([>=] ( -V-,(intern (format nil "R_PROCESS_~S" j))) 0)))
    (loop for j in bolts collect
       `([=]
         ( -V-,(intern (format nil "R_ADD_~S" j)))
         ,(plus
          (loop for i in (gethash j topology-table) collect
             `( -V-,(intern (format nil "R_EMIT_~S" i)))))))
   (loop for j in bolts collect
         `(<->
           ([>] ( -V-,(intern (format nil "R_ADD_~S" j))) 0)
           (-P- ,(format nil "ADD_~S" j))))
   (loop for j in bolts collect
         `(->
			(-P- ,(format nil "PROCESS_~S" j))
			([>] ( -V-,(intern (format nil "R_PROCESS_~S" j))) 0)))
   (loop for j in bolts collect
         `(->
           ([>] ( -V-,(intern (format nil "R_PROCESS_~S" j))) 0)
	           (&&
	           	(until
	             	(&&
						(-P- ,(format nil "PROCESS_~S" j))
						([=] (next ( -V-,(intern (format nil "R_PROCESS_~S" j)))) ( -V-,(intern (format nil "R_PROCESS_~S" j)))))
		             (||
		               (-P- ,(format nil "EMIT_~S" j))
		               (-P- ,(format nil "STARTFAILURE_~S" j))))
              	(since
                	([>] ( -V-,(intern (format nil "R_PROCESS_~S" j))) 0)
                 	(||
                   	(-P- ,(format nil "TAKE_~S" j))
                    orig))
              )))

	(loop for j in bolts collect ;TODO: modifica pdf
         `(<->
           ([=] ( -V-,(intern (format nil "R_PROCESS_~S" j))) 0)
					(||
                        orig
                        (&&
							(!! (-P- ,(format nil "PROCESS_~S" j))) ; forse meglio not TAKE!
							(yesterday
		                      (||
                                (!! (-P- ,(format nil "PROCESS_~S" j)))
                                (-P- ,(format nil "EMIT_~S" j)))))
    					 )))


;		(loop for j in bolts collect ;MOD (INS)
;					`(->
;						(&&
;								(||
;									(!! (-P- ,(format nil "PROCESS_~S" j)))
;									(-P- ,(format nil "EMIT_~S" j)))
;								(!! (next (-P- ,(format nil "TAKE_~S" j)))))
;						(next  ([=] ( -V-,(intern (format nil "R_PROCESS_~S" j))) 0))))



;FINAL bolts HASH VERSION
   (loop for j in bolts when
      (not(loop for i in bolts when
        (member j (gethash i topology-table)) collect i)) collect
          `([=] (-V-,(intern (format nil "R_EMIT_~S" j))) 0))

;NON FINAL bolts
   (loop for j in bolts when
        (loop for i in (append bolts spouts) when
          (member j (gethash i topology-table)) collect i) collect
            `(&&
							(->
								(-P- ,(format nil "EMIT_~S" j))
								(&&
;         					([=] (-V-,(intern (format nil "R_EMIT_~S" j)))
;										([+]
;											([*] (-V-,(intern (format nil "R_PROCESS_~S" j)))
;  												 		 ,(intern (format nil "SIGMA_~S" j)))
;											,(intern (format nil "D_~S" j))) ;PROBLEM WHEN D=0 !?! SOLVED!
;									)

										;BUFFER INCREMENT
									 ([=] (-V-,(intern (format nil "BUFFER_~S" j)))
										 ([+] (yesterday (-V-,(intern (format nil "BUFFER_~S" j))))
													(-V-,(intern (format nil "R_PROCESS_~S" j)))))
										;DEFINING R_EMIT VALUE WRT BUFFER
										([<=] (-V-,(intern (format nil "R_EMIT_~S" j)))
											([+]
												([*] (-V- ,(intern (format nil "BUFFER_~S" j)))
																 ,(intern (format nil "SIGMA_~S" j)))
												,(intern (format nil "D_~S" j))))
										([>] (-V-,(intern (format nil "R_EMIT_~S" j)))
											([-]
											 ([+]
												([*] (-V- ,(intern (format nil "BUFFER_~S" j)))
																 ,(intern (format nil "SIGMA_~S" j)))
												,(intern (format nil "D_~S" j)))
											 1))

										;BUFFER DECREMENT AFTER EMIT(SAME MECHANISM AS ABOVE)
										([>=] (next (-V- ,(intern (format nil "BUFFER_~S" j))))
										      ([-]  (-V- ,(intern (format nil "BUFFER_~S" j)))
										            ([*] (-V- ,(intern (format nil "R_EMIT_~S" j)))
										                 ,(intern (format nil "SIGMA_REC_~S" j)))))
										([<] (next (-V- ,(intern (format nil "BUFFER_~S" j))))
										      ([+]	([-]  (-V- ,(intern (format nil "BUFFER_~S" j)))
										              		([*] (-V- ,(intern (format nil "R_EMIT_~S" j)))
										                        ,(intern (format nil "SIGMA_REC_~S" j))))
							                  1))




              	))

						(->
         				(!!(-P- ,(format nil "EMIT_~S" j)))
								(&&
         						([=] (-V-,(intern (format nil "R_EMIT_~S" j))) 0)
										;BUFFER STABILITY
;			 							 (since
;			 								 ([=] (yesterday (-V- ,(intern (format nil "BUFFER_~S" j))))
;			 														(-V- ,(intern (format nil "BUFFER_~S" j))))
;			 								 (||
;			 										 orig
;			 										 (-P- ,(format nil "EMIT_~S" j))))
			 								 (until
			 									 ([=] (next (-V- ,(intern (format nil "BUFFER_~S" j))))
			 															(-V- ,(intern (format nil "BUFFER_~S" j))))
			 									 (next	(-P- ,(format nil "EMIT_~S" j))))
               ))

       ))



			(loop for j in bolts collect
			      `(->
			        (&&
			          (-P- ,(format nil "TAKE_~S" j))
			          ([>=] ,(intern (format nil "C_TAKE_~S" j))
			            ([+] ( -V-,(intern (format nil "Q_~S" j))) ( -V-,(intern (format nil "R_ADD_~S" j))))))
			        ([=] ( -V-,(intern (format nil "R_PROCESS_~S" j)))
			           ([+] ( -V-,(intern (format nil "Q_~S" j))) ( -V-,(intern (format nil "R_ADD_~S" j)))))))

			(loop for j in bolts collect
			      `(->
			        (&&
			          (-P- ,(format nil "TAKE_~S" j))
			          ([<] ,(intern (format nil "C_TAKE_~S" j))
			               ([+] ( -V-,(intern (format nil "Q_~S" j))) ( -V-,(intern (format nil "R_ADD_~S" j))))))
			        ([=] ( -V-,(intern (format nil "R_PROCESS_~S" j)))
			             ,(intern (format nil "C_TAKE_~S" j)))))




;SPOUTS RATES
;	 (loop for j in spouts collect
;			`([>=] ( -V-,(intern (format nil "R_EMIT_~S" j))) 0))
   (loop for j in spouts collect
        `(&&
					(->
						(-P- ,(format nil "EMIT_~S" j))
						(&&
            ([>] ( -V-,(intern (format nil "L_EMIT_~S" j))) 0)
            ([=] ;TODO TESTARE
	            ( -V-,(intern (format nil "R_EMIT_~S" j)))
	            ([+]
	                ( -V-,(intern (format nil "L_EMIT_~S" j)))
	                ( -V-,(intern (format nil "R_REPLAY_~S" j)))))))
					(->
						(!! (-P- ,(format nil "EMIT_~S" j)))
						([=] (-V-,(intern (format nil "R_EMIT_~S" j))) 0))

              )))))




(defmacro failureRatesBehaviour(spouts bolts impacts-table)
`(&&
	,@(nconc
		(loop for i in spouts collect
			`([=]
				( -V-,(intern (format nil "R_REPLAY_~S" i)))
				,(plus
				 (loop for j in bolts when
					(> (gethash (cons j i) impacts-table ) 0) collect
						`([*] ( -V-,(intern (format nil "R_FAILURE_~S~S" j i)))
									,(ceiling(gethash (cons j i) impacts-table )))))))
;STARTFAILURE and !!EMIT
	(loop for i in spouts append
				(loop for j in bolts when
					(> (gethash (cons j i) impacts-table ) 0) collect
						`(->
								(&&
										(-P- ,(format nil "STARTFAILURE_~S" j))
										(!!(-P- ,(format nil "EMIT_~S" i))))
								([=] (next ( -V- ,(intern (format nil "R_FAILURE_~S~S" j i))))
											([+]	( -V- ,(intern (format nil "R_FAILURE_~S~S" j i)))
														([+] ( -V- ,(intern (format nil "Q_~S" j)))
																 ([+]	( -V- ,(intern (format nil "R_PROCESS_~S" j)))
																			( -V- ,(intern (format nil "R_ADD_~S" j))))))))))

;STARTFAILURE and EMIT
(loop for i in spouts append
			(loop for j in bolts when
				(> (gethash (cons j i) impacts-table ) 0) collect
					`(->
							(&&
									(-P- ,(format nil "STARTFAILURE_~S" j))
									(-P- ,(format nil "EMIT_~S" i)))
							([=] (next ( -V- ,(intern (format nil "R_FAILURE_~S~S" j i))))
													([+] ( -V- ,(intern (format nil "Q_~S" j)))
															 ([+]	( -V- ,(intern (format nil "R_PROCESS_~S" j)))
																		( -V- ,(intern (format nil "R_ADD_~S" j)))))))))
;!!STARTFAILURE and EMIT
	(loop for i in spouts append
			(loop for j in bolts when
				(> (gethash (cons j i) impacts-table ) 0) collect
					`(->
							(&&
									(!!(-P- ,(format nil "STARTFAILURE_~S" j)))
									(-P- ,(format nil "EMIT_~S" i)))
							([=] (next ( -V- ,(intern (format nil "R_FAILURE_~S~S" j i))))
										0))))
;!!STARTFAILURE and !!EMIT
	(loop for i in spouts append
				(loop for j in bolts when
					(> (gethash (cons j i) impacts-table ) 0) collect
						`(->
								(&&
										(!!(-P- ,(format nil "STARTFAILURE_~S" j)))
										(!!(-P- ,(format nil "EMIT_~S" i))))
								([=] (next ( -V- ,(intern (format nil "R_FAILURE_~S~S" j i))))
										( -V- ,(intern (format nil "R_FAILURE_~S~S" j i)))))))

)))


(defun gen-r-bolts(bolts)
      (loop for j in bolts do
          (eval `(define-tvar ,(intern (format nil "R_ADD_~S" j)) *int*))
					(eval `(define-tvar ,(intern (format nil "R_PROCESS_~S" j)) *int*))
		;			(eval `(define-tvar ,(intern (format nil "R_TAKE_~S" j)) *int*))
					(eval `(define-tvar ,(intern (format nil "Q_~S" j)) *int*))
					(eval `(define-tvar ,(intern (format nil "BUFFER_~S" j)) *int*))
        	(eval `(define-tvar ,(intern (format nil "R_EMIT_~S" j)) *int*))
        ))

(defun gen-r-spouts(spouts)
		(loop for j in spouts do
				(eval `(define-tvar ,(intern (format nil "R_EMIT_~S" j)) *int*))
				(eval `(define-tvar ,(intern (format nil "R_REPLAY_~S" j)) *int*))
        (eval `(define-tvar ,(intern (format nil "L_EMIT_~S" j)) *int*)) ; part of R_EMIT not due to failure and always <= C_EMIT
    ))


(defun gen-r-failures(spouts bolts impacts-table)
	(loop for i in spouts append
	    (loop for j in bolts when
	    (>  (gethash (cons j i) impacts-table ) 0) do
	        (eval `(define-tvar ,(intern (format nil "R_FAILURE_~S~S" j i)) *int*))
	    )))

(defun gen-pt-clocks (spouts bolts)
	(loop for j in (append spouts bolts) do
    (eval `(define-tvar ,(intern (format nil "PT_~S_0" j)) *real*))
  	(eval `(define-tvar ,(intern (format nil "PT_~S_1" j)) *real*))
		)
	(loop for j in bolts do
    (eval `(define-tvar ,(intern (format nil "CLOCK_BF_~S" j)) *real*)))
;	(loop for j in spouts do
;		(eval `(define-tvar ,(intern (format nil "CLOCK_EMIT_~S" j)) *real*)))
	(eval `(define-tvar ,(intern (format nil "TOTALTIME")) *real*)))

(defun flatten (l)
	(cond ((null l) nil)
				((atom l) (list l))
				(t (loop for a in l appending (flatten a)))))


(defun gen-counters-list(spouts bolts impacts-table)
			(flatten
			`(
			,@(loop for j in bolts collect
				(list
					(intern (format nil "R_ADD_~S" j))
					(intern (format nil "R_PROCESS_~S" j))
;					(intern (format nil "R_TAKE_~S" j))
					(intern (format nil "Q_~S" j))
     			(intern (format nil "BUFFER_~S" j))
					(intern (format nil "R_EMIT_~S" j))
		))
				,@(loop for j in spouts collect
					(list
						(intern (format nil "R_EMIT_~S" j))
						(intern (format nil "R_REPLAY_~S" j))
      			(intern (format nil "L_EMIT_~S" j))

				))
				,@(loop for i in spouts append
						(loop for j in bolts when
						(>  (gethash (cons j i) impacts-table ) 0) collect
								(intern (format nil "R_FAILURE_~S~S" j i))
						))
		)))


(defmacro rates-constraints (spouts bolts impacts-table)
	`(&&
			,@(loop for j in bolts collect
					`([>=] (-V- ,(intern (format nil "R_ADD_~S" j))) 0))
			,@(loop for j in (append spouts bolts) collect
					`([>=] (-V- ,(intern (format nil "R_EMIT_~S" j))) 0))
			,@(loop for j in bolts collect
					`([>=] (-V- ,(intern (format nil "R_PROCESS_~S" j))) 0))
			,@(loop for j in bolts collect ; ###FORSE NON SERVE
					`([<=] (-V- ,(intern (format nil "R_PROCESS_~S" j))) ,(intern (format nil "C_TAKE_~S" j))))
	;		,@(loop for j in bolts collect
	;				`([>=] (-V- ,(intern (format nil "R_TAKE_~S" j))) 0))
			,@(loop for j in bolts collect
					`([>=] (-V- ,(intern (format nil "Q_~S" j))) 0))
			,@(loop for j in bolts collect
					`([>=] (-V- ,(intern (format nil "BUFFER_~S" j))) 0))
			,@(loop for j in spouts collect
					`([>=] (-V- ,(intern (format nil "R_REPLAY_~S" j))) 0))
      ,@(loop for j in spouts collect
					`([>=] (-V- ,(intern (format nil "L_EMIT_~S" j))) 0))
      ,@(loop for j in spouts collect
					`([<=] (-V- ,(intern (format nil "L_EMIT_~S" j))) ,(intern (format nil "C_EMIT_~S" j))))
 			,@(loop for i in spouts append
					(loop for j in bolts when
					(>  (gethash (cons j i) impacts-table ) 0) collect
							`([>=] (-V- ,(intern (format nil "R_FAILURE_~S~S" j i))) 0)))
;clocks-constraints
			([>=] (-V- TOTALTIME) 0)
			,@(loop for j in (append spouts bolts) collect
			  `(&&
	    			([>=] (-V- ,(intern (format nil "PT_~S_0" j))) 0)
	          ([>=] (-V- ,(intern (format nil "PT_~S_1" j))) 0)))
			,@(loop for j in bolts collect
	    			`([>=] (-V- ,(intern (format nil "CLOCK_BF_~S" j))) 0))

;			,@(loop for j in spouts collect
;			  `(&&
;	          ([>=] (-V- ,(intern (format nil "CLOCK_EMIT_~S" j))) 0)
;					))
	))


(defmacro init-rates (spouts bolts impacts-table)
	`(&&
;			,@(loop for j in bolts collect
;					`([>=] (-V- ,(intern (format nil "R_ADD_~S" j))) 0))
;			,@(loop for j in (append spouts bolts) collect
;					`([=] (-V- ,(intern (format nil "R_EMIT_~S" j))) 0))

;			,@(loop for j in bolts collect
;					`([<=] (-V- ,(intern (format nil "R_PROCESS_~S" j)))
;								,(intern (format nil "C_TAKE_~S" j))))

			,@(loop for j in bolts collect
					`([=] (-V- ,(intern (format nil "R_PROCESS_~S" j)))
								0))


;			,@(loop for j in bolts collect
;					`([>=] (-V- ,(intern (format nil "R_TAKE_~S" j))) 0))
			,@(loop for j in bolts collect
					`(!! (-P- ,(intern (format nil "PROCESS_~S" j))))) ;TODO COMMENT
			,@(loop for j in bolts collect
					`([=] (-V- ,(intern (format nil "Q_~S" j))) INIT_QUEUES))
			,@(loop for j in bolts collect
					`([=] (-V- ,(intern (format nil "BUFFER_~S" j))) 0))
			,@(loop for j in spouts collect
					`([=] (-V- ,(intern (format nil "R_REPLAY_~S" j))) 0))
			,@(loop for i in spouts append
					(loop for j in bolts when
					(>  (gethash (cons j i) impacts-table ) 0) collect
							`([=] (-V- ,(intern (format nil "R_FAILURE_~S~S" j i))) 0)))
			)
		)


(defmacro init-clocks(spouts bolts)
`(&&
	([=] (-V- TOTALTIME) 0)
	,@(loop for j in (append spouts bolts) collect
		`(&&
;				([=] (-V- ,(intern (format nil "CLOCK_FAIL_~S" j))) 0)
				([=] (-V- ,(intern (format nil "PT_~S_0" j))) 0)
				([>] (-V- ,(intern (format nil "PT_~S_1" j))) 0)
;          (alwf (somf ([=] (-V- ,(intern (format nil "PT_~S_0" j))) 0)))		; fairness for clocks - nonzeno models
;          (alwf (somf ([=] (-V- ,(intern (format nil "PT_~S_1" j))) 0)))
		))
	,@(loop for j in bolts collect
				`([>=] (-V- ,(intern (format nil "CLOCK_BF_~S" j))) 0))

;	,@(loop for j in spouts collect
;		`(&&
;				([>=] (-V- ,(intern (format nil "CLOCK_EMIT_~S" j))) 0)
;		))


;		(somf ([=] (-V- TOTALTIME) MAX_TIME))
		(next (alwf ([>] (-V- TOTALTIME) 0)))
	))

; LIMIT QUEUE LENGTH
(defmacro queueConstraint(bolts threshold)
`(&&
  ,@(nconc
    (loop for j in bolts collect
        `([<] ( -V-,(intern (format nil "Q_~S" j))) ,threshold)))))

; NO FAILURES ARE HAPPENING
(defmacro noFailures(bolts)
`(&&
  ,@(nconc
    (loop for j in bolts collect
        `(!! ( -P- ,(format nil "FAIL_~S" j)))))))

;(pprint (queueConstraint '(B1 B2 B3) 10))


;;; WRAPPERS FOR EVALUATING MACRO PARAMETERS

(defun f-singleBoltsBehaviour (bolts)
	(eval `(singleBoltsBehaviour ,bolts)))

(defun f-singleQueueBehaviour(bolts topology-table)
	(eval `(singleQueueBehaviour ,bolts ,topology-table)))

(defun f-ratesBehaviour (spouts bolts topology-table)
	(eval `(ratesBehaviour ,spouts ,bolts ,topology-table)))

(defun f-failureRatesBehaviour (spouts bolts impacts-table)
	(eval `(failureRatesBehaviour ,spouts ,bolts ,impacts-table)))

(defun f-rates-constraints (spouts bolts impacts-table)
	(eval `(rates-constraints ,spouts ,bolts ,impacts-table)))

(defun f-init-rates (spouts bolts impacts-table)
		(eval `(init-rates ,spouts ,bolts ,impacts-table)))

(defun f-init-clocks (spouts bolts)
		(eval `(init-clocks ,spouts ,bolts)))

(defun f-queueConstraint (bolts threshold)
	(eval `(queueConstraint ,bolts ,threshold)))

(defun f-noFailures (bolts)
  (eval `(noFailures ,bolts)))

(defun f-clocks-behaviour (spouts bolts time)
	(eval `(clocks-behaviour ,spouts ,bolts ,time)))

(defun f-spoutClocksBehaviour (spouts time)
	(eval `(spoutClocksBehaviour ,spouts ,time)))


;UTILITY FUNCTIONS FOR CALCULATING IMPACTS
(defun recursive-sum (L)
   (if L
      (+ (car L) (recursive-sum (cdr L)))
      0))

(defun r-out(j)
  (if (boundp `,(intern (format nil "C_TAKE_~S" j ))) ;IF it is a BOLT
    (*  (EVAL `,(intern (format nil "C_TAKE_~S" j )))
        (EVAL `,(intern (format nil "SIGMA_~S" j ))))
      (EVAL `,(intern (format nil "C_EMIT_~S" j ))))) ;ELSE IF it is a SPOUT ;TODO MOFIFICARE CON RATE

(defun sigma(j)
  (if (boundp `,(intern (format nil "C_TAKE_~S" j ))) ;IF it is a BOLT
    (EVAL `,(intern (format nil "SIGMA_~S" j )))
    1))	;ELSE IF it is a SPOUT


(defun sum-r-out(L)
(let ((list-rates (loop for i in L collect
          (r-out i))))
      (recursive-sum list-rates)))

(defun get-paths(strt curr prev topology-table path-table)
  (let ((subs (gethash curr topology-table)))
  (if subs
      (loop for i in subs do
        (get-paths strt i (nconc (list curr) prev) topology-table path-table))
      (let ((old (gethash strt path-table))
            (new-path (reverse(nconc (list curr) prev))))
        (setf (gethash strt path-table)
          (if old (nconc old (list new-path))
                  (list new-path)))
  ;      (print (gethash strt the-path-table))
      ))))

(defun compute-direct-impacts(j topology-table impacts-table)
  (let ((subscription-list (gethash j topology-table)))
    (loop for i in subscription-list do
      (setf (gethash (cons j i) impacts-table) (/ (r-out i) (sum-r-out subscription-list)))
    )))

(defun recursive-impact(lst impacts-table)
    (if (cddr lst)
        (* (gethash (cons (car lst) (first (cdr lst))) impacts-table) (/ 1 (sigma (first (cdr lst)))) (recursive-impact (cdr lst) impacts-table))
        (gethash (cons (car lst) (first (cdr lst))) impacts-table)))

(defun get-path-impact(path impacts-table)
  (let (  (x (first path))
					(y (car (last  path))))
       (setf (gethash (cons x y) impacts-table) (recursive-impact path impacts-table))))

(defun compute-all-impacts(bolts topology-table path-table impacts-table)
	(loop for j in bolts do
	  (compute-direct-impacts j topology-table impacts-table)
	  (get-paths j j nil topology-table path-table)
	  (loop for p in (gethash j path-table) do
	    (get-path-impact p impacts-table))))



; HELPER STRUCTURE USED TO DETERMINE IMPACTS
	(defvar the-path-table)
	(setq the-path-table (make-hash-table :test 'equalp))


;	IMPACT VALUES (SHOULD BE DETERMINED BASED ON TOPOLOGY AND OTHER PREVIOUSLY DEFINED PARAMETERS)
	(defvar the-impacts-table)
	(setq the-impacts-table (make-hash-table :test 'equalp))
	;initialize to 0
	(loop for i in (append the-spouts the-bolts) append
			(loop for j in the-bolts do
			(setf (gethash (cons j i) the-impacts-table) 0)
			))

	(compute-all-impacts the-bolts the-topology-table the-path-table the-impacts-table)
;	(print the-impacts-table)

	;;; GENERATE CLOCK VARIABLES

	(gen-r-failures the-spouts the-bolts the-impacts-table)
	(gen-r-spouts the-spouts)
	(gen-r-bolts the-bolts)

	(gen-pt-clocks the-spouts the-bolts)


	(ae2sbvzot:zot 20
		(&&
			(yesterday (f-init-rates the-spouts the-bolts the-impacts-table))

			(yesterday (f-init-clocks the-spouts the-bolts))

			(yesterday
			(alwf
				(&&
					(f-rates-constraints the-spouts the-bolts the-impacts-table)
					(f-singleBoltsBehaviour the-bolts)
					(f-singleQueueBehaviour the-bolts the-topology-table)
					(f-ratesBehaviour the-spouts the-bolts the-topology-table)
					(f-failureRatesBehaviour the-spouts the-bolts the-impacts-table)
					;(f-clocks-behaviour the-spouts the-bolts the-rate-threshold-table the-proc-time-table)
                    (f-clocks-behaviour the-spouts the-bolts the-proc-time-table)
;OLD                    (f-spoutClocksBehaviour the-spouts the-rate-threshold-table the-proc-time-table)
                    (f-spoutClocksBehaviour the-spouts the-proc-time-table)
		            (f-noFailures '(WpDeserializer expander articleExtraction mediaExtraction))
;          (f-queueConstraint the-bolts QUEUE_THRESHOLD);trova run che non satura
				)
			))

    

			(&& (yesterday orig) (alwf (!! orig)))
		)


		:gen-symbolic-val nil
		:smt-lib :smt2
		:logic :QF_UFRDL
		:over-clocks MAX_TIME
		:parametric-regions 't
		:discrete-counters (gen-counters-list the-spouts the-bolts the-impacts-table)
    
    :l-monotonic '(Q_WpDeserializer Q_expander Q_articleExtraction Q_mediaExtraction)
    
    
    :l-strictly-monotonic '(Q_expander)
    

)