	add $a0, $zero, $zero
	addi $a1, $zero, 10
loop:	addi $a0, $a0, 1
	beq $a0, $a1, done
	beq $zero, $zero, loop
done:	halt
