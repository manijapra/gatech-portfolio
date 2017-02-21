!============================================================
! CS-2200 Homework 1
!
! Please do not change mains functionality, 
! except to change the argument for fibonacci or to meet your 
! calling convention
!============================================================
!			  Mani Japra
! ===========================================================
! Algorithm:
! ===========================================================
! fibonacci(n) = fibonacci(n - 1) + fibonacci (n - 2)
! fibonaicc(0) = 0, and fibonacci(1) = 1
! ===========================================================
main:       	la $sp, stack		! load address of stack label into $sp
            	lw $sp, 0($sp)          ! load desired value of the stack 
                                       	! (defined at the label below) into $sp
            	la $at, fibonacci	! load address of fibonacci label into $at
            	addi $a0, $zero, 11	! $a0 = 8, the fibonacci argument
            	jalr $at, $ra		! jump to fibonacci, set $ra to return addr
            	halt			! when we return, just halt	
! ==================== Start Fibonacci Method ===============
fibonacci:	sw $ra, 0($sp)		! save the prev return addr
		addi $sp, $sp, 1	! increment stack pointer to next available loc
		addi $t0, $zero, 1	! t0 = 1 --> to check if (n == 1) below
cont:		beq $zero, $a0, isZero  ! case: check n == 0
		beq $t0, $a0, isOne	! case: check n == 1
		beq $zero, $zero, rec	! unconditionally branch to rec
isZero:		add $a0, $zero, $zero	! n==0 --> set a0 to 0
		beq $zero, $zero, z	! branch unconditionally to z
isOne:		addi $a0, $zero, 1	! n==1 --> set a0 to 1
		beq $zero, $zero, z	! branch unconditionally to z
z:		add $v0, $a0, $zero	! set return register (v0) to the answer that is stored in a0
		lw $ra, -1($sp)		! get the prev return address off of the stack
		jalr $ra, $zero		! return to caller
rec:		addi $t1, $a0, -1	! t1 = n - 1
		addi $t2, $a0, -2	! t2 = n - 2
		add $a0, $t1, $zero	! Setup recursive call => a0 = t1 = n - 1
		sw $t1, 0($sp)		! Store t1 on the stack
		sw $t2, 1($sp)		! Store t2 on the stack
		addi $sp, $sp, 2	! Increment stack pointer by 2
		jalr $at, $ra		! Recursive Call
		add $t1, $v0, $zero	! Load value from recursive call to register t1
		lw $t2, -2($sp)		! Pop t2 off stack
		add $a0, $t2, $zero	! Setup recursive call => a0 = t2 = n - 2
		sw $t1, -3($sp)		! Store t1 on the stack
		addi $sp, $sp, -1	! Decrement stack pointer
		jalr $at, $ra		! Recursive Call
		add $t2, $v0, $zero	! Load value from recursive call to register t2
		lw $t1, -3($sp)		! Pop t1 off stack
		add $a0, $t1, $t2	! Use the results of previous rec calls and add them => t1 + t2 = a0
		addi $sp, $sp, -3	! Decrement Stack pointer by 3 to go up the recursive tree
		add $v0, $a0, $zero	! Set the return value
		lw $ra, -1($sp)		! Load original return addr from stack
		jalr $ra, $zero		! return to caller
stack:	    .word 0x4000		! the stack begins here

