;;===============================
;;Name: Mani Japra
;;===============================

.orig x3000

	LD R0, ARRAY
	AND R1, R1, 0 ; Clear
	AND R2, R2, 0 ; Clear -> K = 0
	ADD R1, R1, 1 ; First element is sorted
	
	LOOPONE NOT R2, R2 
		ADD R2, R2, 1 ; negate k -> (-k)
		LD R6, LENGTH
		ADD R6, R6, -1 ; LENGTH --
		ADD R6, R6, R2 ; LENGTH - k
	BRnz EXIT ; As soon as length is 0 == DONE

	LOOPTWO LDR R5, R0, 0 ; array[i]
		LDR R4, R0, 1 ; array[i + 1]
		NOT R3, R4
		ADD R3, R3, 1 ; negate array[i + 1] -> (-array[i + 1])
		ADD R3, R5, R3 ; array[i] - array[ i + 1]
	BRnz SKIP ; Skip when arr[i] < arr[i + 1]

	STR R4, R0, 0 ; Swap array[i] < == > array[i + 1]
	STR R5, R0, 1
	AND R1, R1, 0

	SKIP ADD R0, R0, 1 ; Increment index
		ADD R6, R6, -1 ; Length - 1
		BRp LOOPTWO ; If length != 0
		ADD R1, R1, 0 ; Get R1 for branch
	BRp EXIT ; sorted -> end

	NOT R2, R2 
	ADD R2, R2, 2 ; k++
	LD R0, ARRAY ; load array again
	BR LOOPONE
	
	EXIT HALT ; DONE

	
	


ARRAY   .fill x6000
LENGTH  .fill 12
.end

; This array should be sorted when finished.
.orig x6000
.fill 28
.fill -50
.fill 7
.fill 2
.fill 42
.fill 4
.fill 15
.fill -8
.fill 34
.fill 101
.fill -5
.fill 250
.end

