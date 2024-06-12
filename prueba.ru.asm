.data
IO_vtable:
	 .word IO_out_str
	 .word IO_out_int
	 .word IO_out_bool
	 .word IO_out_char
	 .word IO_out_array_int
	 .word IO_out_array_str
	 .word IO_out_array_bool
	 .word IO_out_array_char
	 .word IO_in_str
	 .word IO_in_int
	 .word IO_in_bool
	 .word IO_in_char
Char_vtable:
Object_vtable:
Str_vtable:
	 .word Str_length
	 .word Str_concat
Array_vtable:
	 .word Array_length
Int_vtable:
Bool_vtable:
Fibonacci_vtable:
	 .word Fibonacci_sucesion_fib
	 .word Fibonacci_constructor
	 .word Fibonacci_imprimo_numero
	 .word Fibonacci_imprimo_sucesion

.text
.globl main

main:
	addi $sp, $sp, -8   # Reservo espacio en la pila para el ra y el fp
	sw $fp, 0($sp)           # Guardar el frame pointer actual en la pila
	sw $ra, 4($sp)           # Guardar el return address actual en la pila
	move $fp, $sp            # Establecer el nuevo frame pointer
	# Reservar espacio en la pila para las variables locales
	addi $sp, $sp, -8

	# NODO ASIGNACION 

	# NODO LLAMADA METODO 
	# Preparo para realizar la llamada al metodo constructor
	addi $sp, $sp, -0
	jal Fibonacci_constructor
	addi $sp, $sp, 0# Liberar el espacio de parámetros
	 # Cargo el resultado de retorno fib
	sw $v0, -4($fp)   # Guardar el puntero de la estructura en la pila

	# NODO ASIGNACION 

	# NODO ACCESO 

	# NODO LLAMADA METODO 
	# Preparo para realizar la llamada al metodo in_int
	addi $sp, $sp, -0
	jal IO_in_int # Llamar al método
	move $a0, $t0
	 # Cargo el resultado en n
	sw $v0, -8($fp)

	# NODO ACCESO 

	# NODO LLAMADA METODO 
	# Preparo para realizar la llamada al metodo out_int
	addi $sp, $sp, -4

	# NODO ACCESO 
	 # Cargo la instancia fib
	lw $t1, -4($fp) #carga en $t... la direccion de la instancia
	 # Accedo al metodo 
	move $a0, $t2 # mueve a a0 la direccion de la instancia 
	lw $t2, 0($a0) #cargo en $t... la direccion a la vtable
	lw $s0, 0($t3) # Cargar el puntero al método desde la vtable

	# NODO LLAMADA METODO 
	# Preparo para realizar la llamada al metodo sucesion_fib
	addi $sp, $sp, -4
	lw $t3, -8($fp)
	sw $t4,0($sp)
	jalr $s0 # Llamar al método
	move $t4, $v0  # Guardamos la dirección de la instancia
	addi $sp, $sp, 4# Liberar el espacio de parámetros
	move $a0, $t5
	jal IO_out_int # Llamar al método
	move $a0, $t5
	# Restaurar el estado de la pila y terminar el programa
	move $sp, $fp            # Restaurar el puntero de pila
	lw $fp, 0($sp)           # Restaurar el frame pointer
	lw $ra, 4($sp)           # Restaurar el return address
	addi $sp, $sp, 8         # Establecer el nuevo frame pointer
	li $v0, 10
syscall


Fibonacci_imprimo_sucesion :
	addi $sp, $sp, -8   # Reservo espacio en la pila para el ra y el fp
	sw $fp, 0($sp)           # Guardar el frame pointer actual en la pila
	sw $ra, 4($sp)           # Guardar el return address actual en la pila
	move $fp, $sp            # Establecer el nuevo frame pointer
	move $s1,$a0 #guardo el puntero a la instancia del objeto q se llamo el metodo
# Obtener el puntero al objeto

	# NODO ACCESO 

	# NODO LLAMADA METODO 
	# Preparo para realizar la llamada al metodo out_int
	addi $sp, $sp, -4
	lw $t6,8($fp)# Guarda parametro en un registro temporal
	move $a0, $t7
	jal IO_out_int # Llamar al método
	move $a0, $t0

	# NODO ACCESO 

	# NODO LLAMADA METODO 
	# Preparo para realizar la llamada al metodo out_str
	addi $sp, $sp, -4
.data
str0: .asciiz "\n"
.text
la $t1, str0
	move $a0, $t2
	jal IO_out_str # Llamar al método
	move $a0, $t2
	move $v0, $t3     # Retornar la dirección base de la estructura en $v0
	move $sp, $fp         # Restaurar el puntero de pila
	lw $fp, 0($sp)        # Restaurar el puntero de marco
	lw $ra, 4($sp)        # Restaurar la dirección de retorno
	addi $sp, $sp, 8      # Ajustar el puntero de pila
	jr $ra


Fibonacci_constructor :
	addi $sp, $sp, -8   # Reservo espacio en la pila para el ra y el fp
	sw $fp, 0($sp)           # Guardar el frame pointer actual en la pila
	sw $ra, 4($sp)           # Guardar el return address actual en la pila
	move $fp, $sp            # Establecer el nuevo frame pointer
	li $v0, 9 #Reservamos memoria dinamica (heap)
	li $a0,12# Reservamos por cada atributo del struct
	syscall
	move $t3,$v0 # Guardamos la dirección de la memoria reservada
	la $t4, Fibonacci_vtable
	sw $t5,0($t3)
	# Primero inicializamos todo por defecto
	li $t5,0
	sw $t5, 4($t3)
	li $t6,0
	sw $t6, 8($t3)
	li $t0,0
	sw $t0, 12($t3)
	move $s1, $v0   # Guardamos la dirección de la memoria reservadaa line
	move $t0, $v0   # Guardamos la dirección de la memoria reservada

	# NODO ASIGNACION 
	li $t1, 0
	 # Cargo el resultado en i
	sw $t2, 8($s1)
	move $t2, $v0   # Guardamos la dirección de la memoria reservada

	# NODO ASIGNACION 
	li $t3, 0
	 # Cargo el resultado en j
	sw $t4, 12($s1)
	move $t4, $v0   # Guardamos la dirección de la memoria reservada

	# NODO ASIGNACION 
	li $t5, 0
	 # Cargo el resultado en suma
	sw $t6, 4($s1)
	move $v0, $t5     # Retornar la dirección base de la estructura en $v0
	# Restaurar el estado de la pila
	move $sp, $fp         # Restaurar el puntero de pila
	lw $fp, 0($sp)        # Restaurar el puntero de marco
	lw $ra, 4($sp)        # Restaurar la dirección de retorno
	addi $sp, $sp, 8      # Ajustar el puntero de pila
	jr $ra


Fibonacci_imprimo_numero :
	addi $sp, $sp, -8   # Reservo espacio en la pila para el ra y el fp
	sw $fp, 0($sp)           # Guardar el frame pointer actual en la pila
	sw $ra, 4($sp)           # Guardar el return address actual en la pila
	move $fp, $sp            # Establecer el nuevo frame pointer
	move $s1,$a0 #guardo el puntero a la instancia del objeto q se llamo el metodo
# Obtener el puntero al objeto

	# NODO ACCESO 

	# NODO LLAMADA METODO 
	# Preparo para realizar la llamada al metodo out_str
	addi $sp, $sp, -4
.data
str6: .asciiz "f_"
.text
la $t0, str6
	move $a0, $t0
	jal IO_out_str # Llamar al método
	move $a0, $t0

	# NODO ACCESO 

	# NODO LLAMADA METODO 
	# Preparo para realizar la llamada al metodo out_int
	addi $sp, $sp, -4
	lw $t1,8($fp)# Guarda parametro en un registro temporal
	move $a0, $t2
	jal IO_out_int # Llamar al método
	move $a0, $t2

	# NODO ACCESO 

	# NODO LLAMADA METODO 
	# Preparo para realizar la llamada al metodo out_str
	addi $sp, $sp, -4
.data
str3: .asciiz "="
.text
la $t4, str3
	move $a0, $t5
	jal IO_out_str # Llamar al método
	move $a0, $t5
	move $v0, $t6     # Retornar la dirección base de la estructura en $v0
	move $sp, $fp         # Restaurar el puntero de pila
	lw $fp, 0($sp)        # Restaurar el puntero de marco
	lw $ra, 4($sp)        # Restaurar la dirección de retorno
	addi $sp, $sp, 8      # Ajustar el puntero de pila
	jr $ra


Fibonacci_sucesion_fib :
	addi $sp, $sp, -8   # Reservo espacio en la pila para el ra y el fp
	sw $fp, 0($sp)           # Guardar el frame pointer actual en la pila
	sw $ra, 4($sp)           # Guardar el return address actual en la pila
	move $fp, $sp            # Establecer el nuevo frame pointer
	move $s1,$a0 #guardo el puntero a la instancia del objeto q se llamo el metodo
# Obtener el puntero al objeto

	# NODO ASIGNACION 
	li $t6, 0
	 # Cargo el resultado en i
	sw $t7, 8($s1)

	# NODO ASIGNACION 
	li $t0, 0
	 # Cargo el resultado en j
	sw $t0, 12($s1)

	# NODO ASIGNACION 
	li $t0, 0
	 # Cargo el resultado en suma
	sw $t1, 4($s1)

	# NODO WHILE 
	while_start_0:

	# NODO EXPRESION BINARIA 
	lw $t1, 8($s1)
	lw $t2,8($fp)# Guarda parametro en un registro temporal
	sle $t3, $t2, $t3
	beq $t4, $zero, while_end_0

	# NODO EXPRESION BINARIA 
	lw $t4, 8($s1)
	li $t5, 0
	seq $t6, $t5, $t6
	 # Condicion
	beq $t7, $zero, else_0
	bne $t7, $zero, if_0
	 # Sentencias if
	if_0:

	# NODO LLAMADA METODO 
	# Preparo para realizar la llamada al metodo imprimo_numero
	addi $sp, $sp, -4
	lw $t0, 8($s1)
	sw $t0,0($sp)
	lw $t0, 0($s1) #cargo en un registro temporal la direccion a la vtable para recuperar el puntero del metodo imprimo_numero
	lw $s0, 8($t0) #cargar en un registro temporal la direccion al metodo que quiero llamar
	move $s6, $s1   # Guardamos la dirección de la instancia
	jalr $s0 # Llamar al método
	addi $sp, $sp, 4# Liberar el espacio de parámetros
	move $s1, $s6   # Guardamos la dirección de la memoria reservadaa line

	# NODO LLAMADA METODO 
	# Preparo para realizar la llamada al metodo imprimo_sucesion
	addi $sp, $sp, -4
	lw $t1, 4($s1)
	sw $t2,0($sp)
	lw $t2, 0($s1) #cargo en un registro temporal la direccion a la vtable para recuperar el puntero del metodo imprimo_sucesion
	lw $s0, 12($t2) #cargar en un registro temporal la direccion al metodo que quiero llamar
	move $s6, $s1   # Guardamos la dirección de la instancia
	jalr $s0 # Llamar al método
	addi $sp, $sp, 4# Liberar el espacio de parámetros
	move $s1, $s6   # Guardamos la dirección de la memoria reservadaa line
	j if_end_0
	else_0:
	 # Sentencias else

	# NODO EXPRESION BINARIA 
	lw $t3, 8($s1)
	li $t4, 1
	seq $t5, $t4, $t5
	 # Condicion
	beq $t6, $zero, else_1
	bne $t6, $zero, if_1
	 # Sentencias if
	if_1:

	# NODO LLAMADA METODO 
	# Preparo para realizar la llamada al metodo imprimo_numero
	addi $sp, $sp, -4
	lw $t6, 8($s1)
	sw $t7,0($sp)
	lw $t0, 0($s1) #cargo en un registro temporal la direccion a la vtable para recuperar el puntero del metodo imprimo_numero
	lw $s0, 8($t0) #cargar en un registro temporal la direccion al metodo que quiero llamar
	move $s6, $s1   # Guardamos la dirección de la instancia
	jalr $s0 # Llamar al método
	addi $sp, $sp, 4# Liberar el espacio de parámetros
	move $s1, $s6   # Guardamos la dirección de la memoria reservadaa line

	# NODO ASIGNACION 

	# NODO EXPRESION BINARIA 
	lw $t0, 4($s1)
	lw $t1, 8($s1)
	add $t2, $t1, $t2
	 # Cargo el resultado en suma
	sw $t3, 4($s1)

	# NODO LLAMADA METODO 
	# Preparo para realizar la llamada al metodo imprimo_sucesion
	addi $sp, $sp, -4
	lw $t3, 4($s1)
	sw $t4,0($sp)
	lw $t4, 0($s1) #cargo en un registro temporal la direccion a la vtable para recuperar el puntero del metodo imprimo_sucesion
	lw $s0, 12($t4) #cargar en un registro temporal la direccion al metodo que quiero llamar
	move $s6, $s1   # Guardamos la dirección de la instancia
	jalr $s0 # Llamar al método
	addi $sp, $sp, 4# Liberar el espacio de parámetros
	move $s1, $s6   # Guardamos la dirección de la memoria reservadaa line
	j if_end_1
	else_1:
	 # Sentencias else

	# NODO LLAMADA METODO 
	# Preparo para realizar la llamada al metodo imprimo_numero
	addi $sp, $sp, -4
	lw $t5, 8($s1)
	sw $t6,0($sp)
	lw $t6, 0($s1) #cargo en un registro temporal la direccion a la vtable para recuperar el puntero del metodo imprimo_numero
	lw $s0, 8($t6) #cargar en un registro temporal la direccion al metodo que quiero llamar
	move $s6, $s1   # Guardamos la dirección de la instancia
	jalr $s0 # Llamar al método
	addi $sp, $sp, 4# Liberar el espacio de parámetros
	move $s1, $s6   # Guardamos la dirección de la memoria reservadaa line

	# NODO ASIGNACION 

	# NODO EXPRESION BINARIA 
	lw $t0, 4($s1)
	lw $t0, 12($s1)
	add $t1, $t0, $t1
	 # Cargo el resultado en suma
	sw $t2, 4($s1)

	# NODO ASIGNACION 
	lw $t2, 4($s1)
	 # Cargo el resultado en j
	sw $t3, 12($s1)

	# NODO LLAMADA METODO 
	# Preparo para realizar la llamada al metodo imprimo_sucesion
	addi $sp, $sp, -4
	lw $t3, 4($s1)
	sw $t4,0($sp)
	lw $t4, 0($s1) #cargo en un registro temporal la direccion a la vtable para recuperar el puntero del metodo imprimo_sucesion
	lw $s0, 12($t4) #cargar en un registro temporal la direccion al metodo que quiero llamar
	move $s6, $s1   # Guardamos la dirección de la instancia
	jalr $s0 # Llamar al método
	addi $sp, $sp, 4# Liberar el espacio de parámetros
	move $s1, $s6   # Guardamos la dirección de la memoria reservadaa line
	j if_end_1
	if_end_1:
	j if_end_0
	if_end_0:

	# NODO EXPRESION UNARIA 
	lw $t5, 8($s1)
	addi $t6, $t6, 1
	 # Guardo el valor en el atributo
	sw $t6, 8($s1)
	j while_start_0
	while_end_0:
	lw $t6, 4($s1)
	move $v0, $t7     # Retornar la dirección base de la estructura en $v0
	move $sp, $fp         # Restaurar el puntero de pila
	lw $fp, 0($sp)        # Restaurar el puntero de marco
	lw $ra, 4($sp)        # Restaurar la dirección de retorno
	addi $sp, $sp, 8      # Ajustar el puntero de pila
	jr $ra

IO_out_int:
	# Asumimos que el argumento de la función está en $a0 
	li $v0, 1 
	syscall 
	jr $ra

IO_out_str:
	# Asumimos que el argumento de la función está en $a0 
	li $v0, 4 
	syscall 
	jr $ra

IO_out_bool:
	beqz $a0, print_false  # Si $a0 es 0, saltar a print_false
	# Si no, imprimir "true"
	li $v0, 4 
	la $a0, true_str 
	syscall 
	jr $ra

print_false:
	li $v0, 4 
	la $a0, false_str 
	syscall 
	jr $ra

.data
	true_str: .asciiz "true"
	false_str: .asciiz "false"

.text

IO_out_char:
	# Asumimos que el argumento de la función está en $a0 
	li $v0, 11 
	syscall 
	jr $ra

IO_out_array_int:

IO_out_array_str:

IO_out_array_bool:

IO_out_array_char:

IO_in_str:
	# Reservar un buffer para almacenar la cadena
	la $a0, buffer       # Cargar la dirección de inicio del buffer en $a0
	la $a1, 100          # Cargar la longuitud del buffer en $a1
	li $v0, 8 
	syscall 
	jr $ra

.data
	buffer: .space 100    # Buffer para almacenar la cadena leída

.text

IO_in_int:
	li $v0, 5 
	syscall 
	jr $ra

IO_in_bool:
	jal IO_in_str # Llamar al método para leer un string
	# Verificar si la cadena es "true" o "false"
	la $t0, buffer       # Cargar la dirección de inicio de la cadena en $t0
	li $t1, 't'          # Cargar el carácter 't' en $t1
	li $t2, 'f'          # Cargar el carácter 'f' en $t2
	lb $t3, 0($t0)       # Cargar el primer carácter de la cadena en $t3
	# Comparar el primer carácter con 't' para determinar si es "true" o "false"
	beq $t3, $t1, true_result  # Si es igual a 't', salta a true_result
	beq $t3, $t2, false_result # Si es igual a 'f', salta a false_result

	true_result:
	li $v0, 1            # Si la cadena es "true", configurar $v0 en 1
	jr $ra               # Retornar a la dirección de retorno

	false_result:
	li $v0, 0            # Si la cadena es "true", configurar $v0 en 1
	jr $ra               # Retornar a la dirección de retorno

IO_in_char:
	li $v0, 12 
	syscall 
	jr $ra

Array_length:

Str_length:

Str_concat:

end_program: