;;===============================
;; Name: Mani Japra (902958199)
;;===============================

.orig x3000

	LD R6, STACK
	LD R0, N

	ADD R6, R6, -1 ; Push
	STR R0, R6, 0 ; Store R0 next
	JSR MC91 ; Call Function

	; Pop return value and arg off stack
	LDR R0, R6, 0
	ADD R6, R6, 2

	ST R0, ANSWER

	HALT

STACK	.fill xF000
N	.fill 99       ;input n
ANSWER	.blkw 1    ;save your answer here

MC91
	ADD R6, R6, -3 ; R6 is at old frame pointer
	STR R7, R6, 1 ; Store Return Address (Located in R7)
	STR R5, R6, 0 ; Store old frame pointer
	ADD R5, R6, -1 ; POINT R5 TO FIRST LOCAL

	;START CODE -------------------------------------------
	
	AND R0, R0, 0
	AND R1, R1, 0	
	LDR R0, R5, 4 ; Set R0 to N
	ADD R1, R0, -10
	ADD R1, R1, -10
	ADD R1, R1, -10
	ADD R1, R1, -10
	ADD R1, R1, -10
	ADD R1, R1, -10
	ADD R1, R1, -10
	ADD R1, R1, -10
	ADD R1, R1, -10
	ADD R1, R1, -10

	BRp IF
	BR ELSE

	IF AND R1, R1, 0
	ADD R1, R0, -10
	STR R1, R5, 3
	BR DONE

	ELSE LDR R0, R5, 4 ; Make R0 N
	ADD R0, R0, 11 ; N + 11
	ADD R6, R6, -1 ; Push
	STR R0, R6, 0 ; Store R0 next
	JSR MC91
	LDR R0, R6, 0 ; Load ANSWER from MC91
	ADD R6, R6, -1 ; Push
	STR R0, R6, 0 ; Store R0 next
	JSR MC91
	LDR R0, R6, 0 ; Load ANSWER from MC91
	STR R0, R5, 3
	BR DONE
	
	;END CODE ---------------------------------------------

	DONE ADD R6, R5, 3 ; POINT R6 TO RETURN VALUE
	LDR R7, R5, 2 ; RESTORE R7 TO RETURN ADDRESS
	LDR R5, R5, 1 ; RESTORE R5 TO OLD FRAME POINTER
	RET
	
.end
