;;===============================
;;Name: Mani Japra (902958199)
;;===============================

.orig x3000

MAIN
	LD R6, STACK 			;initialize stack pointer

	; Call GCD(m,n)

	LD R0, N
	LD R1, M
	ADD R6, R6, -1 			; Push
	STR R0, R6, 0 			; Store R0 next
	STR R1, R6, 1 			; Store R1 next
	JSR GCD

	; Pop return value and arg off stack

	LDR R0, R6, 0
	ADD R6, R6, 2

	; Save the Answer

	ST R0, ANSWER

HALT

STACK .fill xF000
N .fill 2688	; input n
M .fill 768		; input m
ANSWER .fill 0  ; save your answer here

GCD
	ADD R6, R6, -3
	STR R7, R6, 1
	STR R5, R6, 0
	ADD R5, R6, -1
	ADD R6, R6, -2
	; -----------------------------Start Code----------------------------------------
	AND R0, R0, 0
	AND R1, R1, 0
	LDR R0, R5, 4 			; R0 <- M
	LDR R1, R5, 5 			; R1 <- N

	NOT R0, R0
	ADD R0, R0, 1
	
	ADD R0, R0, R1 
	BRz EQUAL
	BRn MLARGER
	BRp NLARGER
	BR DONE

	EQUAL LDR R0, R5, 4
	STR R0, R5, 3
	BR DONE

	MLARGER LDR R0, R5, 4		; R0 < - M
		LDR R1, R5, 5 		; R1 < - N
		NOT R3, R1
		ADD R3, R3, 1 		; R3 < - -N
		ADD R3, R0, R3 		; R3 < - (M-N)
	
		ADD R6, R6, -1 		; Push
		STR R3, R6, 0		; Store (M-N)
		STR R1, R6, 1		; Store N
		JSR GCD
		LDR R0, R6, 0
		STR R0, R5, 3
	BR DONE

	NLARGER LDR R0, R5, 4		; R0 < - M
		LDR R1, R5, 5 		; R1 < - N
		NOT R3, R0
		ADD R3, R3, 1		; R3 < - -M
		ADD R3, R1, R3

		ADD R6, R6, -1 		; Push
		STR R0, R6, 1		; Store M
		STR R3, R6, 0		; Store (N-M)
		JSR GCD
		LDR R0, R6, 0
		STR R0, R5, 3
	BR DONE
	; -------------------------------End Code----------------------------------------
	DONE	ADD R6, R5, 3 
		LDR R7, R5, 2
		LDR R5, R5, 1
	RET
.end
