;;===============================
;;Name: Mani Japra (902958199)
;;===============================

.orig x3000
	LEA R0, A
	LEA R1, B
	NOT R3, R0
	NOT R4, R1
	AND R5, R3, R4
	NOT R6, R5
	ST R6, ANSWER

	HALT; CODE GOES HERE! :D
	
A       .fill 6
B       .fill 13
ANSWER  .fill 0	 ;This answer should contain A | B when finished.
.end
