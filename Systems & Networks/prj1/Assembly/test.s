add $s0, $zero, $zero
add $a0, $zero, $zero
addi $s0, $s0, 5
loop:	addi $s0, $s0, -1
	addi $a0, $a0, 1
	beq $s0, $zero, end
	beq $zero, $zero, loop
end:	halt
halt
