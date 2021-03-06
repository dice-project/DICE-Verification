(asdf:operate 'asdf:load-op '{{ verification_params.plugin }})
(use-package :trio-utils)

;SHORTAND FOR PROPOSITIONS W 1 PARAMETER LIKE (-P- ,(format nil "START_S_~S" i))
(defun <P1> (x j)
  `(-P- ,(intern (format nil (concatenate 'string x "_~S") j))))
;SHORTAND FOR TVAR  W 1 PARAMETER LIKE ( -V- ,(intern (format nil "R_PROCESS_~S" j)))	
(defun <V1> (x j)
  `(-V- ,(intern (format nil (concatenate 'string x "_~S") j))))

(defun <V2> (x j k)
  `(-V- ,(intern (format nil (concatenate 'string x "_~S_~S") j k))))

;SHORTAND FOR CONSTANTS W 1 PARAMETER LIKE TOTTASKS_S2 
(defun <C1>(x j)
	(intern (format nil (concatenate 'string x "_~S") j)))

(defun getReciprocalGZ(x)
  (if (> x 0) (/ 1.0 x) 0.0))

(defun range (max &key (min 1) (step 1))
 (loop for n from min to max by step
    collect n))


;GLOBAL CONSTANTS

;DAG ELEMENTS DEFINITION
;list of stages
(defconstant the-stages '(S{{ stages|join(' S') }}))
;(defconstant the-stages '( S0 S1 S2 S3 S4 S5 S6 S7 S8 S9 S10 S11 S12 S13 S14 S15 S16 S17 ))

;dependency hash table, defining the 
(defvar the-dependency-table)
(setq the-dependency-table (make-hash-table :test 'equalp))  
;(setf (gethash 'S4 the-dependency-table) '( S0 S1 S2 ))
{% for k,v in stages.iteritems() -%}
(setf (gethash 'S{{k}} the-dependency-table) '(S{{ v.parentsIds|join(' S') }}))
{% endfor %}



(defvar the-labels-table)
(setq the-labels-table (make-hash-table :test 'equalp))
{% for k,v in stages.iteritems() -%}
(setf (gethash 'S{{k}} the-labels-table) '(S{{ v.label }}))
{% endfor %}


;(defconstant TOT_CORES 600)
(defconstant TOT_CORES {{ tot_cores }})

(defconstant MAX_IDLE_TIME 0.1)
;(defconstant MAX_TIME 60000.0)
(defconstant MAX_TIME {{ max_time }})
(defconstant DEADLINE {{ deadline }})

;STAGE-SPECIFIC CONSTANTS
; (defconstant TOT_TASKS_S0 1000)
{% for k,v in stages.iteritems() -%}
(defconstant TOT_TASKS_S{{ k }} {{ v.numtask }})
{% endfor %}

;tasks average duration
; (defconstant ALPHA_S0 1672)
{% for k,v in stages.iteritems() -%}
(defconstant ALPHA_S{{ k }} {{ v.duration / v.numtask }})
{% endfor %}

(defvar the-proc-time-table)
(setq the-proc-time-table (make-hash-table :test 'equalp))
; (setf (gethash 'S0 the-proc-time-table) (list (- ALPHA_S0 (/ ALPHA_S0 10.0)) (+ ALPHA_S0 (/ ALPHA_S0 10.0))))
{% for k,v in stages.iteritems() -%}
(setf (gethash 'S{{ k }} the-proc-time-table) (list (- ALPHA_S{{ k }} (/ ALPHA_S{{ k }} 10.0)) (+ ALPHA_S{{ k }} (/ ALPHA_S{{ k }} 10.0))))
{% endfor %}

(defconstant orig
    (-P- O))

;UTILITY FUNCTIONS
;SUM ELEMENTS IN A LIST
(defun plus (lst)
            (if (car lst)
                (if(cdr lst) (list '[+] (car lst) (plus (cdr lst)))
                             (car lst))
                 0)
          )



;MACROS



(defmacro stagesStateEvolution(stages dependency-table)
`(&&
  ,@(nconc  
     ;COMPLETED_S <-> P(END_S)
     (loop for j in stages collect
        `(<->
            ,(<P1> "COMPLETED_S" j)
            (somp 
                ,(<P1> "END_S" j))))
     ;ENABLED_S_I <-> forall J: Dep(I,J) COMPLETED_S_J  
     (loop for j in stages collect
       `(<->
         ,(<P1> "ENABLED_S" j)
         (&&
            ,@(loop for i in (gethash j dependency-table) collect
                `,(<P1> "COMPLETED_S" i)))))
     ;ENABLED_S && !(START_S || P(START_S)) -> F(START_S) 
;      (loop for i in stages collect
 ;      `(->
  ;          (&&
   ;             ,(<P1> "ENABLED_S" i)
    ;            (!!(somp ,(<P1> "START_S" i))))
  ;          (somf_e ,(<P1> "START_S" i))))
    )
  )
)



(defmacro tasksStateEvolution(stages)
`(&&
  ,@(nconc
  ;START_T -> RUN_T && !END_T && Y(!RUN_T snc (ORI || END_T)) && (END_T release !START_T)
	(loop for i in stages collect
		`(->
			,(<P1> "START_T" i)
			(&&
			,(<P1> "RUN_T" i)
			(yesterday ,(<P1> "ENABLED_S" i))
			(!! ,(<P1> "END_T" i))
			(yesterday
				(since
				(!! ,(<P1> "RUN_T" i))
				(||
					,(<P1> "END_T" i)
					orig)
					))
			(next
			(release
				,(<P1> "END_T" i)
				(!! ,(<P1> "START_T" i))))
					)))
		;RUN_T -> somp START_S RUN_T snc START_T && RUN_T until END_T
      (loop for i in stages collect
         `(->
           ,(<P1> "RUN_T" i)
           (&&
;			 ,(<P1> "RUN_S" i) ; UNSAT!!!?
             (since
               ,(<P1> "RUN_T" i)
               ,(<P1> "START_T" i))
             (until
                ,(<P1> "RUN_T" i)
                ,(<P1> "END_T" i)))))
      ;RUN_T -> RUN_T && Y(!END_T snc (ori || START_T) && X(X(START_T) release !RUN_T)
		(loop for i in stages collect
			`(->
				,(<P1> "END_T" i)
				(&&
					,(<P1> "RUN_T" i)
					(yesterday
						(since
							(!! ,(<P1> "END_T" i))
							(||
								,(<P1> "START_T" i)
								orig)
							))
; REDUNDANT
;					(next
;						(release
;								(next ,(<P1> "START_T" i))
;								(!! ,(<P1> "RUN_T" i))))
							)))
    )
 )
)

(defmacro newResourceFormulae(stages)
	`(&&
		; sum(RUN_TC) + AVA_CC = TOT_CORES 
		([=]
			([+] 
				(-V- AVA_CC)
				,(plus (loop for j in stages collect
						(<V1> "RUN_TC" j))))
				TOT_CORES)

	)
)



(defmacro newCountersFormulae(stages)
	`(&&
		,@(nconc
		;;; RUN_TC != XRUN_TC -> Y(START_T) || END_T
			(loop for i in stages collect
				`(&&
						(->
							([!=] ,(<V1> "RUN_TC" i) (next ,(<V1> "RUN_TC" i)))
							(||
								(next ,(<P1> "START_T" i))
								,(<P1> "END_T" i)))
		;;; REM_TC >= XREM_TC
;						(->
;							,(<P1> "ENABLED_S" i)
							([>=] ,(<V1> "REM_TC" i) (next ,(<V1> "REM_TC" i)));)
		 ; REM_TC != XREM_TC -> X(START_T)
						(->
							([!=] ,(<V1> "REM_TC" i) (next ,(<V1> "REM_TC" i)))
							(next ,(<P1> "START_T" i)))
						
					;START_T -> RUN_TC = YREM_TC - REM_TC
						(->
							,(<P1> "START_T" i)
							([=] ,(<V1> "RUN_TC" i) 
								([-] (yesterday ,(<V1> "REM_TC" i))
									 ,(<V1> "REM_TC" i))))								
					;RUN_T -> RUN_TC > 0 
						(->
							,(<P1> "RUN_T" i)
;							(&&
								([>] ,(<V1> "RUN_TC" i) 0)
;								(until 
;									([=] ,(<V1> "RUN_TC" i) (next ,(<V1> "RUN_TC" i))) ;UNSAT!!
;									 ,(<P1> "END_T" i)))
									 )
					;!RUN_T -> RUN_TC = 0
						(->
							(!! ,(<P1> "RUN_T" i))
							([=] ,(<V1> "RUN_TC" i) 0))		

					; (START_T && Y(REM_TC = TOT_TASKS)) <-> START_S
						(<->
							(&&
								,(<P1> "START_T" i)
								(yesterday ([=] ,(<V1> "REM_TC" i) ,(<C1> "TOT_TASKS" i))))
							,(<P1> "START_S" i))
					; (END_T && (REM_TC = 0)) <-> END_S
						(<->
							(&&
								,(<P1> "END_T" i)
								([=] ,(<V1> "REM_TC" i) 0))
							,(<P1> "END_S" i))								
); end &&
); end loop
); end nconc
); end &&
); end defmacro 


(defmacro allCompleted(stages)
`(&&
	,@(nconc
	(loop for i in stages collect
		` ,(<P1> "COMPLETED_S" i)
	)
	)
)
)


(defmacro countersGEqZero(stages)
`(&&
	([>=] (-V- AVA_CC) 0)
  ,@(nconc
    (loop for i in stages collect
       `(&&
					([>=] ,(<V1> "RUN_TC" i) 0)			 
					([>=] ,(<V1> "REM_TC" i) 0)					 
;					([>=] ,(<V1> "RUNNABLE_TC" i) 0)
;					([>=] ,(<V1> "RELEASE_CC" i) 0)
;					([>=] ,(<V1> "LOCK_CC" i) 0)			 																 
)))))


(defmacro countersFormulae(stages)
	`(&&
		,@(nconc
		;;;ENABLED_S && (!Y(ENABLED_S)||ORIG) -> REM_TC = TOT_TASKS
			(loop for i in stages collect
				`(&&
						(->
							(&&
								,(<P1> "ENABLED_S" i)
								(||
									(!!(yesterday ,(<P1> "ENABLED_S" i)))
									orig))
							([=] ,(<V1> "REM_TC" i) ,(<C1> "TOT_TASKS" i)))
		;;;RUN_T -> RUN_C>0 && RUNNABLE_TC=0 until END_T
						(->
							,(<P1> "RUN_T" i)
							(&&
								([>] ,(<V1> "RUN_TC" i) 0)
								(until 
									(&&
										([=] ,(<V1> "RUN_TC" i) (next ,(<V1> "RUN_TC" i))) ;UNSAT!!
										([=] ,(<V1> "RUNNABLE_TC" i) 0))
									 ,(<P1> "END_T" i))))
		;;;!RUN_T -> RUN_TC=0 && RUNNABLE_TC=REM_TC
						(->
							(!! ,(<P1> "RUN_T" i))
							(&&
								([=] ,(<V1> "RUN_TC" i) 0) 
								([=] ,(<V1> "RUNNABLE_TC" i) ,(<V1> "REM_TC" i))))		
		;;;RUN_TC>0 -> RUN_TC>0 snc START_T && XRUN_TC=RUN_TC until END_T 
		;SERVE?!
						(->
							([>] ,(<V1> "RUN_TC" i) 0)
							(&&
								,(<P1> "RUN_T" i)
								(since 
									([>] ,(<V1> "RUN_TC" i) 0)
									,(<P1> "START_T" i))
								(until 
									([=] (next ,(<V1> "RUN_TC" i)) ,(<V1> "RUN_TC" i))
									,(<P1> "END_T" i))
								))								
					;;;XREM_TC = REM_TC - XLOCK_CC
;VERSIONE "NEXT"
;						(->
;							,	(<P1> "ENABLED_S" i)
;							([=]
;								([+] 
;									(next ,(<V1> "REM_TC" i))
;									(next ,(<V1> "LOCK_CC" i))) 
;								,(<V1> "REM_TC" i)))
;versione "YESTERDAY"
						(->
							(yesterday ,(<P1> "ENABLED_S" i))
							([=]
								([+] 
									,(<V1> "REM_TC" i)
									,(<V1> "LOCK_CC" i)) 
								(yesterday ,(<V1> "REM_TC" i))))
					;START_T -> LOCK_CC > 0 && RUN_TC = LOCK_CC
						(->
							,(<P1> "START_T" i)
							(&&
								([>] ,(<V1> "LOCK_CC" i) 0)
								([=] ,(<V1> "RUN_TC" i) ,(<V1> "LOCK_CC" i))))
			;;;!START_T -> LOCK_CC=0
						(-> 
							(!! ,(<P1> "START_T" i))
							([=] ,(<V1> "LOCK_CC" i) 0))
			;;;!END_T -> RELEASE_CC=0
						(->
							(!! ,(<P1> "END_T" i))
							([=] ,(<V1> "RELEASE_CC" i) 0))
			;;; END -> RELEASE_CC=RUN_TC && RUNNABLE_TC=REM_TC 
						(->
							,(<P1> "END_T" i)
							(&&						
								([=] ,(<V1> "RELEASE_CC" i) ,(<V1> "RUN_TC" i))
								([=] ,(<V1> "RUNNABLE_TC" i) ,(<V1> "REM_TC" i))
								))
					; (START_T && Y(REM_TC = TOT_TASKS)) <-> START_S
						(<->
							(&&
								,(<P1> "START_T" i)
								(yesterday ([=] ,(<V1> "REM_TC" i) ,(<C1> "TOT_TASKS" i))))
							,(<P1> "START_S" i))
					; (END_T && (REM_TC = 0)) <-> END_S
						(<->
							(&&
								,(<P1> "END_T" i)
								([=] ,(<V1> "REM_TC" i) 0))
							,(<P1> "END_S" i))
						
				));END LOOP 	
		)
	)
); END countersFormulae




(defmacro resourcesFormulae(stages)
	`(&&
				;IDLE_CORES <-> (AVA_CC + sum(RELEASE_CC)) > 0 && sum(RUNNABLE_TC) > 0
				;(FORMULA 21)
						(<->
								(-P- IDLE_CORES)
								(&&
									([>]
										([+] 
											(-V- AVA_CC)
											,(plus (loop for j in stages collect
													(<V1> "RELEASE_CC" j))))
										 0)
										([>]
											,(plus (loop for j in stages collect
													(<V1> "RUNNABLE_TC" j)))
													0)))
					;FORMULA 22
						(->
							([<]
								,(plus (loop for j in stages collect
										(<V1> "RUNNABLE_TC" j)))
								([+] 
										(-V- AVA_CC)
										,(plus (loop for j in stages collect
												(<V1> "RELEASE_CC" j)))))
							(&&
								([=]
									(next (-V- AVA_CC))
									([-]
										([+] 
											(-V- AVA_CC)
											,(plus (loop for j in stages collect
													(<V1> "RELEASE_CC" j))))
										,(plus (loop for j in stages collect
											(<V1> "RUNNABLE_TC" j)))))
								([=]
									,(plus (loop for j in stages collect
											(next (<V1> "LOCK_CC" j))))
									,(plus (loop for j in stages collect
											(<V1> "RUNNABLE_TC" j))))))
					;FORMULA 23
						(->
							([>=]
								,(plus (loop for j in stages collect
										(<V1> "RUNNABLE_TC" j)))
								([+] 
										(-V- AVA_CC)
										,(plus (loop for j in stages collect
												(<V1> "RELEASE_CC" j)))))
							(&&
								([=] (next (-V- AVA_CC)) 0)
								([=]
									,(plus (loop for j in stages collect
											(next (<V1> "LOCK_CC" j))))
									([+] 
										(-V- AVA_CC)
										,(plus (loop for j in stages collect
												(<V1> "RELEASE_CC" j)))))))
														 

)); END resourcesFormulae



(defun t-process (j sign c)
    (let
  		((fmla0 (cond
  					((string= sign "<") `([<] ,(<V2> "PT" j  0) ,c))
  					((string= sign "<=") `([<=] ,(<V2> "PT" j  0) ,c))
  					((string= sign ">") `([>] ,(<V2> "PT" j  0) ,c))
  					((string= sign ">=") `([>=] ,(<V2> "PT" j  0) ,c))
  					((string= sign "=") `([=] ,(<V2> "PT" j  0) ,c))))
  		 (fmla1 (cond
  					((string= sign "<") `([<] ,(<V2> "PT" j  1) ,c))
  					((string= sign "<=") `([<=] ,(<V2> "PT" j  1) ,c))
  					((string= sign ">") `([>] ,(<V2> "PT" j  1) ,c))
  					((string= sign ">=") `([>=] ,(<V2> "PT" j  1) ,c))
  					((string= sign "=") `([=] ,(<V2> "PT" j  1) ,c)))))
  		`(&&
  			(->
  				(&&
  					([>] ,(<V2> "PT" j  0) 0.0)
  					(||
  						([=] ,(<V2> "PT" j  1) 0.0)
  						(since
  							([>] ,(<V2> "PT" j  1) 0.0)
  							([=] ,(<V2> "PT" j  0) 0.0))))
  				,fmla0)
  			(->
  				(&&
  					([>] ,(<V2> "PT" j  1) 0.0)
  					(||
  						([=] ,(<V2> "PT" j  0) 0.0)
  						(since
  							([>] ,(<V2> "PT" j  0) 0.0)
  							([=] ,(<V2> "PT" j  1) 0.0))))
  				,fmla1)))
        )




(defmacro clocksBehaviour (stages times)
; F35
	`(&&
		,@(loop for j in stages collect
			`(&&
					(->
						([=] ,(<V2> "PT" j  0) 0.0)
						(&&
							(next
								(release
									([=] ,(<V2> "PT" j  1) 0.0)
									([>] ,(<V2> "PT" j  0) 0.0)))
							([>] ,(<V2> "PT" j  1) 0.0)))
; F36
					(->
						([=] ,(<V2> "PT" j  1) 0.0)
						(&&
							(next
								(release
									([=] ,(<V2> "PT" j  0) 0.0)
									([>] ,(<V2> "PT" j  1) 0.0)))))))


		,@(loop for j in stages collect
				;CLOCK RESET CONDITION
				`(&&
					(<->
						(||
							([=] ,(<V2> "PT" j  0) 0.0)
							([=] ,(<V2> "PT" j  1) 0.0))
						(||
							orig
							,(<P1> "START_T" j)
       				))
					;RESET IDLECORES CLOCK
					(<->
						([=] (-V- CLOCK_IDC) 0)
						(||
							(&&
								(-P- IDLE_CORES)
								(!!(yesterday (-P- IDLE_CORES))))
							(||
								,@(loop for k in stages collect
										(<P1> "START_T" k)))))
			;TASK RUNNING DURATION - SINGLE INTERVAL
					(->
							,(<P1> "RUN_T" j)
						(until
							(&&
								,(<P1> "RUN_T" j)
								(!! ,(<P1> "END_T" j)))
								(&&
									,(t-process j ">=" (first (gethash j times)))
									,(t-process j "<=" (second (gethash j times)))
									,(<P1> "END_T" j))))
			;IDLE_CORES DURATION
		      (->
	           	(-P- IDLE_CORES)
							(until
									(-P- IDLE_CORES)
								(&&
									([<] (-V- CLOCK_IDC) MAX_IDLE_TIME)
									(||
										,@(loop for k in stages collect
												(<P1> "START_T" k))))))
			))
))



(defun genCounters(stages)
	  (define-tvar AVA_CC *int*)
      (loop for j in stages do
        	(eval `(define-tvar ,(intern (format nil "REM_TC_~S" j)) *int*))
					(eval `(define-tvar ,(<C1> "RUN_TC" j) *int*))
;					(eval `(define-tvar ,(<C1> "RUNNABLE_TC" j) *int*))
;					(eval `(define-tvar ,(<C1> "RELEASE_CC" j) *int*))
;					(eval `(define-tvar ,(<C1> "LOCK_CC" j) *int*))
        ))



(defun genClocks (stages)
 	(define-tvar TOTALTIME *real*)
	(define-tvar CLOCK_IDC *real*)
	(loop for j in stages do
		(eval `(define-tvar ,(intern (format nil "PT_~S_0" j)) *real*))
		(eval `(define-tvar ,(intern (format nil "PT_~S_1" j)) *real*))
	)
)

(defun flatten (l)
	(cond ((null l) nil)
				((atom l) (list l))
				(t (loop for a in l appending (flatten a)))))

(defun gen-counters-list(stages)
			(flatten
			`(
				(-V- AVA_CC)
			,@(loop for j in stages collect
				(list
					(intern (format nil "RUN_TC_~S" j))
					(intern (format nil "REM_TC_~S" j))
		))
)))

(defmacro initProps(stages)
`(&&
  ,@(nconc
    (loop for i in stages collect
       `(&&
           (!! ,(<P1> "RUN_T" i))
;           (!! ,(<P1> "RUN_S" i))
)))))

(defmacro initCounters(stages)
`(&&
  ([=] (-V- AVA_CC) TOT_CORES)
  ,@(nconc
    (loop for i in stages collect
       `(&&
					([=] ,(<V1> "RUN_TC" i) 0)			 					 
					([=] ,(<V1> "REM_TC" i) ,(<C1> "TOT_TASKS" i))
;					([=] ,(<V1> "RUNNABLE_TC" i) ,(<V1> "REM_TC" i))
;					([=] ,(<V1> "RELEASE_CC" i) 0)
;					([=] ,(<V1> "LOCK_CC" i) 0)			 																 
)))))


(defmacro initClocks(stages)
`(&&
	([=] (-V- TOTALTIME) 0.0)
	([=] (-V- CLOCK_IDC) 0.0)
	,@(loop for j in stages collect
		`(&&
				([=] (-V- ,(intern (format nil "PT_~S_0" j))) 0.0)
				([>] (-V- ,(intern (format nil "PT_~S_1" j))) 0.0)
		))
))



(defmacro clocksConstraints (stages)
	`(&&
			([>=] (-V- TOTALTIME) 0.0)
			([>=] (-V- CLOCK_IDC) 0.0)
			(next (alwf ([>] (-V- TOTALTIME) 0.0)))
	;		(somf ([=] (-V- TOTALTIME) MAX_TIME))
			,@(loop for j in stages collect
			  `(&&
	    			([>=] (-V- ,(intern (format nil "PT_~S_0" j))) 0.0)
	          		([>=] (-V- ,(intern (format nil "PT_~S_1" j))) 0.0)))
	)	
)



;(pprint (queueConstraint '(B1 B2 B3) 10))


;;; WRAPPERS FOR EVALUATING MACRO PARAMETERS
(defun f-stagesStateEvolution (stages dependency-table)
	(eval `(stagesStateEvolution ,stages ,dependency-table)))

(defun f-tasksStateEvolution (stages)
	(eval `(tasksStateEvolution ,stages)))

(defun f-initProps (stages)
	(eval `(initProps ,stages)))

(defun f-initCounters (stages)
	(eval `(initCounters ,stages)))

(defun f-countersFormulae (stages)
	(eval `(countersFormulae ,stages)))

(defun f-countersGEqZero (stages)
	(eval `(countersGEqZero ,stages)))

(defun f-resourcesFormulae (stages)
	(eval `(resourcesFormulae ,stages)))

(defun f-newCountersFormulae (stages)
	(eval `(newCountersFormulae ,stages)))	

(defun f-newResourceFormulae (stages)
	(eval `(newResourceFormulae ,stages)))

(defun f-clocksBehaviour (stages times)
	(eval `(clocksBehaviour ,stages ,times)))	
	
(defun f-clocksConstraints (stages)
	(eval `(clocksConstraints ,stages)))

(defun f-initClocks (stages)
	(eval `(initClocks ,stages)))

(defun f-allCompleted (stages)
	(eval `(allCompleted ,stages)))

	;;; GENERATE CLOCK VARIABLES
	(genClocks the-stages)

;GENERATE COUNTERS
 (genCounters the-stages)

;(pprint (f-tasksStateEvolution the-stages))
;(pprint (f-countersFormulae the-stages))
;(pprint (f-newResourceFormulae the-stages))
;(print (genClocks the-stages))
;(pprint (f-initClocks the-stages))

	({{ verification_params.plugin }}:zot {{ verification_params.time_bound }}
		(&&
			(yesterday (f-initClocks the-stages))

;			(yesterday (f-initProps the-stages))
			(yesterday (f-initCounters the-stages))
;			(yesterday(somf_e 
;				(&&
;					(-P- END_T_S1)
;					(next (&&
;							(!!(-P- RUN_T_S1))
;							(!!(-P- START_T_S1))))
;					(somf_e (-P- START_T_S1)))))
;			(somf_e (-P- START_T_S1))
;			(somf_e (-P- START_S_S2))
;			(somf_e ([=] (-V- REM_TC_S1) 0))
			(somf_e (&&
					(f-allCompleted the-stages)
;                        (-P- COMPLETED_S_S16)
;                        (-P- COMPLETED_S_S17)
 ;                       (-P- COMPLETED_S_S12)
  ;                      (-P- COMPLETED_S_S14)
   ;                     (-P- COMPLETED_S_S4)
    ;                    (-P- COMPLETED_S_S3)
						([<]
							(-V- TOTALTIME)
							DEADLINE)))
			(yesterday
			(alwf
				(&&
					(-P- COMPLETED_S_SS)				
					(f-stagesStateEvolution the-stages the-dependency-table)
					(f-tasksStateEvolution the-stages)
					(f-countersGEqZero the-stages)
;					(f-countersFormulae the-stages)
;					(f-resourcesFormulae the-stages)
 					(f-newCountersFormulae the-stages)
					(f-newResourceFormulae the-stages)
					(f-clocksConstraints the-stages)
                    (f-clocksBehaviour the-stages the-proc-time-table)
;                    (f-spoutClocksBehaviour the-spouts the-proc-time-table)
;		            (f-noFailures '(S1 URLEXPANSIONBOLT URLCRAWLDECIDERBOLT WEBPAGEFETCHERBOLT ARTICLEEXTRACTIONBOLT MEDIAEXTRACTIONBOLT SOLRBOLT))
				)
			)

        
;        (somf (alwf(f-growingConstraint '(WEBPAGEFETCHERBOLT))))
        

			)

			(&& (yesterday orig) (alwf (!! orig)))
		)


		:gen-symbolic-val nil
		:smt-lib :smt2
		:logic :QF_UFRDL
		{%- if verification_params.no_loops %}
		:over-clocks 0 
		:parametric-regions nil 
		{% else %}
		:over-clocks MAX_TIME
		:parametric-regions 't
		{% endif %}        
    
		:discrete-counters (gen-counters-list the-stages)

)
