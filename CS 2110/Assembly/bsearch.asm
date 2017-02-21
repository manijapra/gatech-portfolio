;;===============================
;;Name: Mani Japra (902958199)
;;===============================

; Main
; Do not edit this function!

;@plugin filename=lc3_udiv vector=x80

.orig x3000

	LD R6, STACK	; Initialize stack pointer

	LD R0, ARR_PTR	; \ Load parameters
	AND R1, R1, 0	; |
	LD R2, ARR_LEN	; |
	ADD R2, R2, -1	; | - R2 - Array Length - 1
	LD R3, X	; / - R3 - What to search for

	ADD R6, R6, -4	; \ Call BSEARCH
	STR R0, R6, 0	; | - R6, 0 array 
	STR R1, R6, 1	; | - R6, 1 LOW = 0
	STR R2, R6, 2	; | - R6, 2 HIGH = LENGTH - 1
	STR R3, R6, 3	; | - R6, 3 WHAT TO SEARCH FOR
	JSR BSEARCH	; /

	LDR R0, R6, 0	; \ Pop return value and args off the stack
	ADD R6, R6, 5	; /

	ST R0, ANSWER

	HALT

STACK   .fill xF000 ; Bottom of the stack + 1
ARR_PTR .fill x6000 ; Pointer to the array of elements
ARR_LEN .fill 16
X       .fill 99    ; What to search for
ANSWER  .fill -999  ; Do NOT write to this label from the subroutine!


; To call UDIV, use TRAP x80
; Preconditions:
;    R0 = X
;    R1 = Y
; Postconditions:
;    R0 = X / Y
;    R1 = X % Y

BSEARCH
	ADD R6, R6, -3
	STR R7, R6, 1
	STR R5, R6, 0
	ADD R5, R6, -1
	ADD R6, R6, -2
	; ---------------------------- START CODE ---------------------------------
	
	AND R0, R0, 0		; 
	STR R0, R5, 0		; STORE MID
	AND R1, R1, 0		; 
	
	LDR R0, R5, 5		; LOAD LOW
	LDR R1, R5, 6		; LOAD HIGH
	
	NOT R3, R0
	ADD R3, R3, 1
	ADD R3, R1, R3		; HIGH - LOW
	
	BRn RETURNNEG
	BR CALCMID
	
	RETURNNEG 	AND R0, R0, 0
			ADD R0, R0, -1
			STR R0, R5, 3
	BR DONE

	CALCMID		LDR R0, R5, 5
			LDR R1, R5, 6
			ADD R0, R0, R1
			AND R1, R1, 0
			ADD R1, R1, 2
			
			TRAP x80
			STR R0, R5, 0	; Store Mid		
	BR CHECKEQUAL

	CHECKEQUAL 	AND R0, R0, 0
			LDR R0, R5, 4
			LDR R1, R5, 0
			ADD R0, R0, R1
			LDR R0, R0, 0	; R0 <- ARRAY[MID]
			AND R1, R1, 0
			LDR R1, R5, 7	; R1 <- X
			NOT R0, R0
			ADD R0, R0, 1
			ADD R1, R0, R1
			BRz FOUND
			BRp FIRSTELSE
			BR SECONDELSE

	FOUND		AND R0, R0, 0
			LDR R0, R5, 0
			STR R0, R5, 3	; STORE MID TO RETURN VAL
	BR DONE

	FIRSTELSE	AND R1, R1, 0
			LDR R1, R5, 0
			ADD R1, R1, 1 	; R1 <- MID + 1
			LDR R0, R5, 4 	; R0 <- ARRAY
			LDR R2, R5, 6	; R2 <- LENGTH
			LDR R3, R5, 7	; R3 <- X
			ADD R6, R6, -4	; \ Call BSEARCH
			STR R0, R6, 0	; | - R6, 0 array 
			STR R1, R6, 1	; | - R6, 1 LOW = MID + 1
			STR R2, R6, 2	; | - R6, 2 HIGH = LENGTH - 1
			STR R3, R6, 3	; | - R6, 3 WHAT TO SEARCH FOR
			JSR BSEARCH
			LDR R0, R6, 0
			STR R0, R5, 3
	BR DONE

	SECONDELSE 	AND R2, R2, 0
			LDR R2, R5, 0
			ADD R2, R2, -1 	; R2 <- MID - 1
			LDR R0, R5, 4 	; R0 <- ARRAY
			LDR R1, R5, 5	; R1 <- LENGTH
			LDR R3, R5, 7	; R3 <- X
			ADD R6, R6, -4	; \ Call BSEARCH
			STR R0, R6, 0	; | - R6, 0 array 
			STR R1, R6, 1	; | - R6, 1 LOW = LOW
			STR R2, R6, 2	; | - R6, 2 HIGH = MID - 1
			STR R3, R6, 3	; | - R6, 3 WHAT TO SEARCH FOR
			JSR BSEARCH
			LDR R0, R6, 0
			STR R0, R5, 3
	BR DONE

	; ---------------------------- END CODE -----------------------------------
	DONE		ADD R6, R5, 3
			LDR R7, R5, 2
			LDR R5, R5, 1	
	RET

.end

.orig x6000

	.fill -45
	.fill -42
	.fill -30
	.fill -2
	.fill 6
	.fill 15
	.fill 16
	.fill 28
	.fill 51
	.fill 78
	.fill 99
	.fill 178
	.fill 200
	.fill 299
	.fill 491
	.fill 5103

.end

