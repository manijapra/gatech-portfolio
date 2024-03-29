main:       la $sp, stack				! load address of stack label into $sp
            noop                        ! FIXME: load desired value of the stack 
                                        ! (defined at the label below) into $sp
            la $at, recadd	        ! load address of fibonacci label into $at
            addi $a0, $zero, 0 	        ! $a0 = 8, the fibonacci argument
            jalr $at, $ra				! jump to fibonacci, set $ra to return addr
            halt						! when we return, just halt	

recadd:		addi $t0, $zero, 1	! t0 = 1
		beq $t0, $t2, cont	! fp already exists
	  	add $fp, $sp, $zero	! set frame pointer to stack pointer
		sw $ra, 0($fp)		! push ra
	
cont:		addi $a0, $a0, -1
		beq $a0, $zero, z
		

retzero:	

retone:
		


z:		add $v0, $a0, $zero	! return value
		lw $ra, 0($sp)
		jalr $ra, $t0
			

stack:	    .word 0x4000				! the stack begins here
