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


(defconstant the-labels '(S{{ labels|join(' S') }}))

(defvar the-labels-table)
(setq the-labels-table (make-hash-table :test 'equalp))
{% for k,v in stages.iteritems() -%}
(setf (gethash 'S{{k}} the-labels-table) '(S{{ v.label }}))
{% endfor %}
;extract values by invoking:
;(gethash i the-labels-table)

;(defconstant TOT_CORES 600)
(defconstant TOT_CORES {{ tot_cores }})

(defconstant MAX_IDLE_TIME 0.1)
;(defconstant MAX_TIME 60000.0)
(defconstant MAX_TIME {{ max_time }})
(defconstant DEADLINE {{ deadline }})
(defconstant TOLERANCE {{ tolerance }})

;STAGE-SPECIFIC CONSTANTS
; (defconstant TOT_TASKS_S0 1000)
{% for k,v in stages.iteritems() -%}
(defconstant TOT_TASKS_S{{ k }} {{ v.numtask }})
{% endfor %}

;tasks average duration
; (defconstant ALPHA_S0 1672)
{% for k,v in stages.iteritems() -%}
(defconstant ALPHA_S{{ k }} {{ v.t_task_verification }})
{% endfor %}

(defvar the-proc-time-table)
(setq the-proc-time-table (make-hash-table :test 'equalp))
; (setf (gethash 'S0 the-proc-time-table) (list (- ALPHA_S0 (/ ALPHA_S0 10.0)) (+ ALPHA_S0 (/ ALPHA_S0 10.0))))
{% for k,v in stages.iteritems() -%}
(setf (gethash 'S{{ k }} the-proc-time-table) 
	(loop for x in (range (if (> TOT_TASKS_S{{ k }} TOT_CORES) (/ TOT_TASKS_S{{ k }} TOT_CORES) 1) :min 1) collect
		(list (- (* ALPHA_S{{ k }} x) (* (* ALPHA_S{{ k }} x) TOLERANCE)) (+ (* ALPHA_S{{ k }} x) (* (* ALPHA_S{{ k }} x) TOLERANCE)))))
{% endfor %}

(defvar the-alphas-table)
(setq the-alphas-table (make-hash-table :test 'equalp))
{% for k,v in stages.iteritems() -%}
(setf (gethash 'S{{ k }} the-alphas-table) ALPHA_S{{ k }})
{% endfor %}

(defvar the-tot-tasks-table)
(setq the-tot-tasks-table (make-hash-table :test 'equalp))
{% for k,v in stages.iteritems() -%}
(setf (gethash 'S{{ k }} the-tot-tasks-table) TOT_TASKS_S{{ k }})
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
  					; (START_T && Y(REM_TC = TOT_TASKS)) <-> START_S
     (loop for j in stages collect
       `(&&
		(<->
			(&&
				,(<P1> "START_T" j)
				([=] ,(<V1> "REM_TC" j) ,(<C1> "TOT_TASKS" j)))
			,(<P1> "START_S" j))
	; (END_T && (REM_TC = 0)) <-> END_S
		(<->
			(&&
				,(<P1> "END_T" j)
				([=] ,(<V1> "REM_TC" j) 0))
	 		,(<P1> "END_S" j))))
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
;			 ,(<P1> "RUN_S" i) 
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

(defmacro resourcesFormulae(stages)
	`(&&
		; sum(RUN_TC) + AVA_CC = TOT_CORES 
		([=]
			([+] 
				(-V- AVA_CC)
				,(plus (loop for j in stages collect
						(<V1> "RUN_TC" j))))
				TOT_CORES)
		
		; ! (AVA_CC > 0 && ORoria: x in stages (Y(ENABLED_S_x) && (REM_TC_x - RUN_TC_x) >0 ))
		(!!
			(&&	
				([>] (-V- AVA_CC) 0)
				(||
					,@(nconc
					(loop for i in stages collect
					`(&&
						(yesterday ,(<P1> "ENABLED_S" i)) 
							([>] ,(<V1> "REM_TC" i) ,(<V1> "RUN_TC" i))))
					)
				)
			)
		)


	)
)



(defmacro countersFormulae(stages)
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
		 ; REM_TC != XREM_TC -> X(END_T)
						(->
							([!=] ,(<V1> "REM_TC" i) (next ,(<V1> "REM_TC" i)))
							(next ,(<P1> "END_T" i)))
						
					;END_T -> REM_TC = YREM_TC - RUN_TC
;						(->
;							,(<P1> "END_T" i)
;							([=] ,(<V1> "REM_TC" i) 
;								([-] (yesterday ,(<V1> "REM_TC" i))
;									 ,(<V1> "RUN_TC" i))))								
					;RUN_T -> RUN_TC > 0 
						(->
							,(<P1> "RUN_T" i)
								([>] ,(<V1> "RUN_TC" i) 0)
									 )
					;!RUN_T -> RUN_TC = 0
						(->
							(!! ,(<P1> "RUN_T" i))
							([=] ,(<V1> "RUN_TC" i) 0))		
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
;					([>=] ,(<V1> "N_ROUNDS_C" i) 0)					 	 																 					 	 																 
)))))



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




(defmacro clocksBehaviour (stages times tot-tasks alphas)
; F35
	`(&&
;		,@(loop for j in stages collect
;			`(&&
;					(->
;						([=] ,(<V2> "PT" j  0) 0.0)
;						(&&
;							(next
;								(release
;									([=] ,(<V2> "PT" j  1) 0.0)
;									([>] ,(<V2> "PT" j  0) 0.0)))
;							([>] ,(<V2> "PT" j  1) 0.0)))
;; F36
;					(->
;						([=] ,(<V2> "PT" j  1) 0.0)
;						(&&
;							(next
;								(release
;									([=] ,(<V2> "PT" j  0) 0.0)
;									([>] ,(<V2> "PT" j  1) 0.0)))))))


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

		,@(loop for j in stages collect
				;STAGE CLOCK RESET CONDITIONS
				`(&&
					(<->
						([=] ,(<V1> "CLOCK_S" j) 0.0)
;						(||
;							([=] ,(<V2> "PT" j  0) 0.0)
;							([=] ,(<V2> "PT" j  1) 0.0))
						(||
							orig
							,(<P1> "START_T" j))
					   )
			;TASK RUNNING DURATION 
					(->
						,(<P1> "RUN_T" j)
						(until
							(&&
								,(<P1> "RUN_T" j)
								(!! ,(<P1> "END_T" j)))
							(&&
								,(<P1> "END_T" j)
								(||
									(&&
										([>=]
											,(<V1> "CLOCK_S" j)
											,(first (first (gethash j times))))
										([<=]
											,(<V1> "CLOCK_S" j)
											,(second (first (gethash j times))))
										([=] ,(<V1> "REM_TC" j) 
											([-] (yesterday ,(<V1> "REM_TC" j))
												,(<V1> "RUN_TC" j)))
									)
									; generate formulae for a subset of the possible aggregations
									; TODO: let it be configurable and adjustable wrt TOT_CORES and TOT_TASKS
									; n_rounds: maximum number of "batch executions" that can be aggregated 
									,@(let ((n_rounds (floor (/ (gethash j tot-tasks) TOT_CORES))))
										(if (>= n_rounds 2)
										{%- if not verification_params.parametric_tc %}
										;	(loop for tc from TOT_CORES  downto (min (ceiling (/ TOT_CORES 2)) 12) by 12 append
										;	(loop for tc in (list TOT_CORES) append ;(if (> TOT_CORES 32) 32  16)) append
											(loop for tc in (list TOT_CORES (- TOT_CORES (mod (gethash j tot-tasks) TOT_CORES))) append
										{% endif %}
												(loop for k from n_rounds downto 2
														by (if (> n_rounds 5) (floor (/ n_rounds 5)) 1)
												 ;(loop for k in (list n_rounds)
														collect
													`(&&
													{%- if not verification_params.parametric_tc %}
														([=] ,(<V1> "RUN_TC" j)
																,tc)
													{% endif %}
														([>=]
															,(<V1> "CLOCK_S" j)
															,(first (nth (- k 1) (gethash j times))))
														([<=]
															,(<V1> "CLOCK_S" j)
															,(second (nth (- k 1) (gethash j times))))
														([=] ,(<V1> "REM_TC" j) 
															([-] (yesterday ,(<V1> "REM_TC" j))
															{%- if verification_params.parametric_tc %}
																([*] ,k ,(<V1> "RUN_TC" j))))
															{% else %}
																,(* tc k)))
															{% endif %}
													); end AND
												); end LOOP k
										{%- if not verification_params.parametric_tc %}
											); end LOOP tc
										{% endif %}
										); end IF
									); end LET
								); end OR 
							); end AND
						); end UNTIL 
					); end IMPL (TASK RUNNING DURATION)
			))
	); end &&
);end clocksBehaviour


(defun genCounters(stages)
	  (define-tvar AVA_CC *int*)
      (loop for j in stages do
        	(eval `(define-tvar ,(intern (format nil "REM_TC_~S" j)) *int*))
			(eval `(define-tvar ,(<C1> "RUN_TC" j) *int*))
;			(eval `(define-tvar ,(<C1> "N_ROUNDS_C" j) *int*))
        ))



(defun genClocks (stages)
 	(define-tvar TOTALTIME *real*)
	(define-tvar CLOCK_IDC *real*)
	(loop for j in stages do
;		(eval `(define-tvar ,(intern (format nil "PT_~S_0" j)) *real*))
;		(eval `(define-tvar ,(intern (format nil "PT_~S_1" j)) *real*))
		(eval `(define-tvar ,(intern (format nil "CLOCK_S_~S" j)) *real*))
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
)))))


(defmacro initClocks(stages)
`(&&
	([=] (-V- TOTALTIME) 0.0)
	([=] (-V- CLOCK_IDC) 0.0)
	,@(loop for j in stages collect
		`(&&
;				([=] (-V- ,(intern (format nil "PT_~S_0" j))) 0.0)
;				([>] (-V- ,(intern (format nil "PT_~S_1" j))) 0.0)
				([=] (-V- ,(intern (format nil "CLOCK_S_~S" j))) 0.0)
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
;	    			([>=] (-V- ,(intern (format nil "PT_~S_0" j))) 0.0)
;	          		([>=] (-V- ,(intern (format nil "PT_~S_1" j))) 0.0)
					([>=] (-V- ,(intern (format nil "CLOCK_S_~S" j))) 0.0)
				))
	)	
)



;;; WRAPPERS FOR EVALUATING MACRO PARAMETERS
(defun f-stagesStateEvolution (stages dependency-table)
	(eval `(stagesStateEvolution ,stages ,dependency-table)))

(defun f-tasksStateEvolution (stages)
	(eval `(tasksStateEvolution ,stages)))

(defun f-initProps (stages)
	(eval `(initProps ,stages)))

(defun f-initCounters (stages)
	(eval `(initCounters ,stages)))

(defun f-countersGEqZero (stages)
	(eval `(countersGEqZero ,stages)))

(defun f-countersFormulae (stages)
	(eval `(countersFormulae ,stages)))	

(defun f-resourcesFormulae (stages)
	(eval `(resourcesFormulae ,stages)))

(defun f-clocksBehaviour (stages times tottasks alphas)
	(eval `(clocksBehaviour ,stages ,times ,tottasks ,alphas)))	
	
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
;(pprint (f-resourcesFormulae the-stages))
;(print (genClocks the-stages))
;(pprint (f-initClocks the-stages))
(pprint (f-clocksBehaviour the-stages the-proc-time-table the-tot-tasks-table the-alphas-table))

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
	{%- if analysis_type == 'boundedness' %}
						(!! (yesterday (f-allCompleted the-stages)))
						([>]
	{% else %}
						([<]
	{% endif %}
							(-V- TOTALTIME)
							DEADLINE)))
			(somf_e
				(&&
					([>]
						(-V- TOTALTIME)
						DEADLINE)))
			(yesterday
			(alwf
				(&&
					(-P- COMPLETED_S_SS)				
					(f-stagesStateEvolution the-stages the-dependency-table)
					(f-tasksStateEvolution the-stages)
					(f-countersGEqZero the-stages)
 					(f-countersFormulae the-stages)
					(f-resourcesFormulae the-stages)
					(f-clocksConstraints the-stages)
                    (f-clocksBehaviour the-stages the-proc-time-table the-tot-tasks-table the-alphas-table)
				)
			)
        
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
