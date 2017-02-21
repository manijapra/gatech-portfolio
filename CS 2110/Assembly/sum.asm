;;===============================
;;Name: Mani Japra (902958199)
;;===============================

.orig x3000

	AND R0, R0, 0 ; Clear R0
	LD R4, LENGTH ; Load LENGTH into R4
	AND R1, R1, 0 ; Clear Register R1

	LD R2, ARRAY ; Load array address
	LDI R3, ARRAY ; Load first array value

	FOR 	ADD R0, R0, R3 ; Add value to sum
		ADD R2, R2, 1 ; Increase index
		LDR R3, R2, 0 ; Determine value
		ADD R4, R4, -1 ; Decrement counter
	BRp FOR

	ADD R0, R0, 0 ; Insure next branch works
	BRzp DONE
	
	ADD R0, R0, -1 ; abs(sum) -- incase sum is neg
	NOT R0, R0
	
	DONE	ST R0, ANSWER

	HALT
	
	
ARRAY   .fill x6000
LENGTH  .fill 10
ANSWER	.fill 0		; The answer should have the abs(sum) when finished.
.end

.orig x6000
.fill 8
.fill 9
.fill 7
.fill 0
.fill -3
.fill 11
.fill 9
.fill -9
.fill 2
.fill 9
.end
