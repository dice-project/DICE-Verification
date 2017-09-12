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
(setf (gethash 'S{{k}} the-labels-table) 'S{{ v.label }})
{% endfor %}
;extract values by invoking:
;(gethash i the-labels-table)

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


; OK FORALL STAGES
(defmacro stagesStateEvolution(stages dependency-table labels-table)
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
	;START_ENABLED_S <-> ENABLED && !Y(ENABLED)
     (loop for j in stages collect
       `(<->
         ,(<P1> "START_ENABLED_S" j)
         (&&
		 	,(<P1> "ENABLED_S" j)
			(!! (yesterday ,(<P1> "ENABLED_S" j)))
            )))
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
			,(<P1> "START_S" j)
			(&&
				,(<P1> "RUN_S" j)
				,(<P1> "START_T" (gethash j labels-table))
				(yesterday 
					([=] 
						,(<V1> "REM_TC" (gethash j labels-table)) 
						,(<C1> "TOT_TASKS" (gethash j labels-table))))
				,(<P1> "ENABLED_S" j)
				(!! ,(<P1> "COMPLETED_S" j))))
	; (END_T && (REM_TC = 0)) <-> END_S
		(<->
			,(<P1> "END_S" j)
			(&&
				,(<P1> "RUN_S" j)
				,(<P1> "END_T" (gethash j labels-table))
				([=] ,(<V1> "REM_TC" (gethash j labels-table)) 0)
				(!! ,(<P1> "COMPLETED_S" j))
				))
	 ; RUN_S -> RUN_S since START_S && RUN_S until END_S	
		(->
			,(<P1> "RUN_S" j)
			(&&
				(since
					,(<P1> "RUN_S" j)
					,(<P1> "START_S" j))
				(until
					,(<P1> "RUN_S" j)
					,(<P1> "END_S" j))))
		); end &&
	); end loop
    )
  )
)


; OK FORALL LABELS
(defmacro tasksStateEvolution(labels)
`(&&
  ,@(nconc
  ;START_T -> RUN_T && !END_T && Y(!RUN_T snc (ORI || END_T)) && (END_T release !START_T)
	(loop for i in labels collect
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
      (loop for i in labels collect
         `(->
           ,(<P1> "RUN_T" i)
           (&&
             (since
               ,(<P1> "RUN_T" i)
               ,(<P1> "START_T" i))
             (until
                ,(<P1> "RUN_T" i)
                ,(<P1> "END_T" i)))))
      ;RUN_T -> RUN_T && Y(!END_T snc (ori || START_T) && X(X(START_T) release !RUN_T)
		(loop for i in labels collect
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

;OK FORALL LABELS
(defmacro resourcesFormulae(labels)
	`(&&
		; sum(RUN_TC) + AVA_CC = TOT_CORES 
		([=]
			([+] 
				(-V- AVA_CC)
				,(plus (loop for j in labels collect
						(<V1> "RUN_TC" j))))
				TOT_CORES)
	)
)


;will iterate over labels
(defmacro countersFormulae(stages labels labels-table) ;TODO change macro invocations accordingly
	`(&&
		,@(nconc
		;;; RUN_TC != XRUN_TC -> Y(START_T) || END_T
			(loop for i in stages collect
				`(&&
		; START_ENABLED_S_I -> REM_TC_label(I) == TOT_TASKS_S_I
						(<->
							,(<P1> "START_ENABLED_S" i)
							([=] ,(<V1> "REM_TC" (gethash i labels-table)) ,(<C1> "TOT_TASKS" i)))
		;;; REM_TC >= XREM_TC
						(->
							,(<P1> "RUN_S" i)
							([<=] ,(<V1> "REM_TC" (gethash i labels-table)) (yesterday ,(<V1> "REM_TC" (gethash i labels-table)))))  
				); end &&
			); end loop stages
			(loop for i in labels collect
				`(&&
					(-> ; OK FOR ALL LABELS
						([!=] ,(<V1> "RUN_TC" i) (next ,(<V1> "RUN_TC" i)))
						(||
							(next ,(<P1> "START_T" i))
							,(<P1> "END_T" i)))
					; REM_TC != XREM_TC -> X(START_T)
					(-> 
						([!=] ,(<V1> "REM_TC" i) (yesterday ,(<V1> "REM_TC" i)))
						(|| 
							,(<P1> "START_T" i)
							,@(loop for j in stages when
								(eq (gethash j labels-table) i) collect
									`,(<P1> "START_ENABLED_S" j))))
					;START_T -> RUN_TC = YREM_TC - REM_TC
					(-> ;OK FOR ALL LABELS
						,(<P1> "START_T" i)
						([=] ,(<V1> "RUN_TC" i) 
							([-] (yesterday ,(<V1> "REM_TC" i))
									,(<V1> "REM_TC" i))))								
					;RUN_T -> RUN_TC > 0 
						(-> ;OK FOR ALL LABELS
							,(<P1> "RUN_T" i)
;							(&&
								([>] ,(<V1> "RUN_TC" i) 0)
;								(until 
;									([=] ,(<V1> "RUN_TC" i) (next ,(<V1> "RUN_TC" i))) ;UNSAT!!
;									 ,(<P1> "END_T" i)))
									 )
					;!RUN_T -> RUN_TC = 0
						(-> ;OK FOR ALL LABELS
							(!! ,(<P1> "RUN_T" i))
							([=] ,(<V1> "RUN_TC" i) 0))		
									
				))
); end nconc
		; IDLE_CORES <-> !(exist i in Labels: RUN_T_i) && sum(RETM_TC_j for j in stages) > 0
		(<->
			(-P- IDLE_CORES)
			(&&
				(!!
					(||
						,@(loop for i in labels collect
							`,(<P1> "RUN_T" i))))
				([>]
					,(plus (loop for j in labels collect
							`,(<V1> "REM_TC" j)))
					0)))

); end &&
); end defmacro 


; OK FORALL STAGES
(defmacro allCompleted(stages)
`(&&
	,@(nconc
	(loop for i in stages collect
		` ,(<P1> "COMPLETED_S" i)
	)
	)
)
)

; OK FORALL LABELS
(defmacro countersGEqZero(labels)
`(&&
	([>=] (-V- AVA_CC) 0)
  ,@(nconc
    (loop for i in labels collect
       `(&&
					([>=] ,(<V1> "RUN_TC" i) 0)			 
					([>=] ,(<V1> "REM_TC" i) 0)					 	 																 
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




(defmacro clocksBehaviour (stages labels times labels-table)
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
				(||  ; OK FORALL LABELS
					,@(loop for k in labels collect
							(<P1> "START_T" k)))))

		;IDLE_CORES DURATION
			(->
				(-P- IDLE_CORES)
							(until
									(-P- IDLE_CORES)
								(&&
									([<] (-V- CLOCK_IDC) MAX_IDLE_TIME)
									(|| ; OK FORALL LABELS
										,@(loop for k in labels collect
												(<P1> "START_T" k))))))							
			; OK FORALL LABELS
			,@(loop for j in labels collect
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
					   )))

			; TASK RUNNING DURATION - SINGLE INTERVAL
			,@(loop for j in stages collect
				;STAGE CLOCK RESET CONDITIONS
				`(&&
					(->
						(&&
							,(<P1> "RUN_S" j)
							,(<P1> "RUN_T" (gethash j labels-table)))
						(until
							(&&
								,(<P1> "RUN_T" (gethash j labels-table))
								(!! ,(<P1> "END_T" (gethash j labels-table))))
								(&&
									([>=]
										,(<V1> "CLOCK_S" (gethash j labels-table))
										,(first (gethash j times)))
									([<=]
										,(<V1> "CLOCK_S" (gethash j labels-table))
										,(second (gethash j times)))
;									,(t-process j ">=" (first (gethash j times)))
;									,(t-process j "<=" (second (gethash j times)))
									,(<P1> "END_T" (gethash j labels-table)))))))
))



(defun genCounters(labels)
	  (define-tvar AVA_CC *int*)
      (loop for j in labels do
        	(eval `(define-tvar ,(intern (format nil "REM_TC_~S" j)) *int*))
					(eval `(define-tvar ,(<C1> "RUN_TC" j) *int*))
        ))

; OK FORALL LABELS
(defun genClocks (labels)
 	(define-tvar TOTALTIME *real*)
	(define-tvar CLOCK_IDC *real*)
	(loop for j in labels do
;		(eval `(define-tvar ,(intern (format nil "PT_~S_0" j)) *real*))
;		(eval `(define-tvar ,(intern (format nil "PT_~S_1" j)) *real*))
		(eval `(define-tvar ,(intern (format nil "CLOCK_S_~S" j)) *real*))
	)
)

(defun flatten (l)
	(cond ((null l) nil)
				((atom l) (list l))
				(t (loop for a in l appending (flatten a)))))

(defun gen-counters-list(labels)
			(flatten
			`(
				(-V- AVA_CC)
			,@(loop for j in labels collect
				(list
					(intern (format nil "RUN_TC_~S" j))
					(intern (format nil "REM_TC_~S" j))
		))
)))

(defmacro initProps(stages labels)
`(&&
  ,@(nconc
    (loop for i in labels collect
       `(&&
           (!! ,(<P1> "RUN_T" i))
	)))
  ,@(nconc
    (loop for i in stages collect
       `(&&
           (!! ,(<P1> "RUN_S" i)))))
))

(defmacro initCounters(stages labels labels-table)
`(&&
  ([=] (-V- AVA_CC) TOT_CORES)
  ,@(nconc
    (loop for i in stages collect
       `(&&
					(->
						,(<P1> "ENABLED_S" i)
						([=] ,(<V1> "REM_TC" (gethash i labels-table)) ,(<C1> "TOT_TASKS" i)))
					(->
						(!! ,(<P1> "ENABLED_S" i))
						([=] ,(<V1> "REM_TC" (gethash i labels-table)) 0)))))
  ,@(nconc
    (loop for i in labels collect
       `(&&
					([=] ,(<V1> "RUN_TC" i) 0))))						
))

; FORALL LABELS
(defmacro initClocks(labels)
`(&&
	([=] (-V- TOTALTIME) 0.0)
	([=] (-V- CLOCK_IDC) 0.0)
	,@(loop for j in labels collect
		`(&&
;				([=] (-V- ,(intern (format nil "PT_~S_0" j))) 0.0)
;				([>] (-V- ,(intern (format nil "PT_~S_1" j))) 0.0)
				([=] (-V- ,(intern (format nil "CLOCK_S_~S" j))) 0.0)
		))
))


; FORALL LABELS
(defmacro clocksConstraints (labels)
	`(&&
			([>=] (-V- TOTALTIME) 0.0)
			([>=] (-V- CLOCK_IDC) 0.0)
			(next (alwf ([>] (-V- TOTALTIME) 0.0)))
	;		(somf ([=] (-V- TOTALTIME) MAX_TIME))
			,@(loop for j in labels collect
			  `(&&
;	    			([>=] (-V- ,(intern (format nil "PT_~S_0" j))) 0.0)
;	          		([>=] (-V- ,(intern (format nil "PT_~S_1" j))) 0.0)
					([>=] (-V- ,(intern (format nil "CLOCK_S_~S" j))) 0.0)
				))
	)	
)



;;; WRAPPERS FOR EVALUATING MACRO PARAMETERS
(defun f-stagesStateEvolution (stages dependency-table labels-table)
	(eval `(stagesStateEvolution ,stages ,dependency-table ,labels-table)))

(defun f-tasksStateEvolution (labels)
	(eval `(tasksStateEvolution ,labels)))

(defun f-resourcesFormulae (labels)
	(eval `(resourcesFormulae ,labels)))

(defun f-countersFormulae (stages labels labels-table)
	(eval `(countersFormulae ,stages ,labels ,labels-table)))	

(defun f-countersGEqZero (labels)
	(eval `(countersGEqZero ,labels)))


(defun f-initProps (stages labels)
	(eval `(initProps ,stages ,labels)))

(defun f-initCounters (stages labels labels-table)
	(eval `(initCounters ,stages ,labels ,labels-table)))

(defun f-clocksBehaviour (stages labels times labels-table)
	(eval `(clocksBehaviour ,stages ,labels ,times ,labels-table)))	
	
(defun f-clocksConstraints (labels)
	(eval `(clocksConstraints ,labels)))

(defun f-initClocks (stages)
	(eval `(initClocks ,stages)))

(defun f-allCompleted (stages)
	(eval `(allCompleted ,stages)))

	;;; GENERATE CLOCK VARIABLES
	(genClocks the-labels)

;GENERATE COUNTERS
 (genCounters the-labels)

;(pprint (f-tasksStateEvolution the-stages))
;(pprint (f-countersFormulae the-stages))
;(pprint (f-resourcesFormulae the-stages))
;(print (genClocks the-stages))
;(pprint (f-initClocks the-stages))

	({{ verification_params.plugin }}:zot {{ verification_params.time_bound }}
		(&&
			(yesterday (f-initClocks the-labels))

;			(yesterday (f-initProps the-stages))
			(yesterday (f-initCounters the-stages the-labels the-labels-table))
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
			(yesterday
			(alwf
				(&&
					(-P- COMPLETED_S_SS)				
					(f-stagesStateEvolution the-stages the-dependency-table the-labels-table)
					(f-tasksStateEvolution the-labels)
					(f-countersGEqZero the-labels)
 					(f-countersFormulae the-stages the-labels the-labels-table)
					(f-resourcesFormulae the-labels)
					(f-clocksConstraints the-labels)
                    (f-clocksBehaviour the-stages the-labels the-proc-time-table the-labels-table)
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
        
    
		:discrete-counters (gen-counters-list the-labels)

)
