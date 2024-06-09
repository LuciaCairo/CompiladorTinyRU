package org.com.etapa5;

import org.com.etapa5.ArbolAST.AST;
import org.com.etapa5.ArbolAST.NodoLiteral;
import org.com.etapa5.ArbolAST.NodoMetodo;
import org.com.etapa5.ArbolAST.NodoStruct;
import org.com.etapa5.TablaDeSimbolos.*;

import java.util.*;

public class CodeGenerator {
    TablaSimbolos ts;
    AST ast;
    private List<AbstractMap.SimpleEntry<String, String>> data = new ArrayList<>();

    private String text = "";
    String code = "";
    public static int registerCounter = 0;
    public static String lit = "";

    // Constructor
    public CodeGenerator(TablaSimbolos ts, AST ast){
        this.ts = ts;
        this.ast = ast;
    }

    // Getters

    // Setters

    // Functions

    // Funcion para genera el codigo recorriendo la TS y luego el AST
    public void generateCode() {

        // Generar declaraciones de variables a partir de la TS
        generateData();

        // Generar codigo a partir del AST
        generateText();

        // Generar codigo a partir de los metodos predefinidos
        generatePred();

        // Ahora genero el codigo MIPS ordenado
        code += ".data\n";
        for(AbstractMap.SimpleEntry<String, String> d : data) {
            code += d.getValue() + "\n";
        }

        code += ".text\n";
        code += ".globl main\n";
        code += this.text;
        System.out.println(code);
    }

    // Funcion para generar la data recorriendo la TS
    public void generateData() {

        // Recorro los structs predefinidos de la TS
        for (Map.Entry<String, EntradaStructPredef> entry : ts.getStructsPred().entrySet()) {

            EntradaStructPredef struct = entry.getValue();
            this.data.add(new AbstractMap.SimpleEntry<>(struct.getName(), struct.getName()+"_vtable:"));

            // Entonces solo recorro los metodos del struct
            for (EntradaMetodo m : struct.getMetodos().values()) {
                //this.data.put(m.getName(), "\t .word "+ struct.getName() + "_" + m.getName());
                this.data.add(new AbstractMap.SimpleEntry<>(m.getName(), "\t .word "+ struct.getName() + "_" + m.getName()));

            }
        }

        // Recorro los structs de la TS
        for (Map.Entry<String, EntradaStruct> entry : ts.getTableStructs().entrySet()) {

            EntradaStruct struct = entry.getValue();

            if(!struct.getName().equals("start")){ // Ahora para los demas structs
                this.data.add(new AbstractMap.SimpleEntry<>(struct.getName(), struct.getName()+"_vtable:"));

                // Recorro los metodos del struct
                for (EntradaMetodo m : struct.getMetodos().values()) {

                    this.data.add(new AbstractMap.SimpleEntry<>(m.getName(), "\t .word "+ struct.getName() + "_" + m.getName()));

                    // Recorro las variables del metodo
                    for (EntradaVariable v : struct.getMetodos().get(m.getName()).getVariables().values()) {
                        if(v.getType().equals("Int")){
                            this.data.add(new AbstractMap.SimpleEntry<>(v.getName(), "\n" + struct.getName() +
                                    "_" + m.getName() + "_" + v.getName() + ": .word 0\n"));
                        } else if(v.getType().equals("Char") || v.getType().equals("Str")){
                            this.data.add(new AbstractMap.SimpleEntry<>(v.getName(), "\n" + struct.getName() +
                                    "_" + m.getName() + "_" + v.getName() + ": .asciiz " + " \n"));
                        } else if(v.getType().equals("Bool")){
                            this.data.add(new AbstractMap.SimpleEntry<>(v.getName(), "\n" + struct.getName() +
                                    "_" + m.getName() + "_" + v.getName() + ": .word 1\n"));
                        } else {
                            String[] palabras = v.getType().split(" ");
                            String isArray = palabras[0];
                            if (isArray.equals("Array")) {
                                this.data.add(new AbstractMap.SimpleEntry<>(v.getName(), "\n" + struct.getName() +
                                        "_" + m.getName() + "_" + v.getName() + ": .space 0\n"));
                            } else {
                                this.data.add(new AbstractMap.SimpleEntry<>(v.getName(), "\n" + struct.getName() +
                                        "_" + m.getName() + "_" + v.getName() + ": .space 8\n"));
                            }
                        }
                    }
                }
            }

        }
    }

    // Funcion para generar el text recorriendo el AST
    public void generateText() {
        // En esta funcion se recorren los nodos del AST y
        // para cada uno de ellos se realiza la generacion de codigo en MIPS

        // Comienzo con start
        NodoStruct value = ast.getStructs().get("start");
        ast.setCurrentStruct(value);
        ts.setCurrentStruct(ts.getStruct(value.getName()));
        ts.setCurrentMetod(null);

        this.text += "\nmain:\n";
        int numVar = (ts.getCurrentStruct().getVariables().size()) * 4;

        this.text += "\taddi $sp, $sp, -8   # Reservo espacio en la pila para el ra y el fp\n" +
                "\tsw $fp, 0($sp)           # Guardar el frame pointer actual en la pila\n" +
                "\tsw $ra, 4($sp)           # Guardar el return address actual en la pila\n" +
                "\tmove $fp, $sp            # Establecer el nuevo frame pointer\n";

        this.text += "\t# Reservar espacio en la pila para las variables locales\n" +
                "\taddi $sp, $sp, -" + numVar + "\n";

        // Recorro las sentencias de start
        if (!value.getSentencias().isEmpty()) {
            for (NodoLiteral s : value.getSentencias()) {
                // Para cada sentencia genero codigo
                this.text += s.generateNodeCode(ts);
            }
        }

        this.text += "\t# Restaurar el estado de la pila y terminar el programa\n" +
                "\tmove $sp, $fp            # Restaurar el puntero de pila\n" +
                "\tlw $fp, 0($sp)           # Restaurar el frame pointer\n" +
                "\tlw $ra, 4($sp)           # Restaurar el return address\n" +
                "\taddi $sp, $sp, 8         # Establecer el nuevo frame pointer\n" +
                "\tli $v0, 10\nsyscall\n";


        // Recorro cada struct
        for (Map.Entry<String, NodoStruct> entry : ast.getStructs().entrySet()) {
            value = entry.getValue();
            ast.setCurrentStruct(value);

            if(!value.getName().equals("start")){

                // Recorro todos los nodos metodos del struct
                for (NodoMetodo m : value.getMetodos().values()) {
                    ts.setCurrentStruct(ts.getStruct(value.getName()));
                    ts.setCurrentMetod(ts.getCurrentStruct().getMetodo(m.getName()));
                    int sizeAtr = (ts.getCurrentStruct().getAtributos().size() * 4 );
                    this.text += "\n\n" + value.getName() + "_" + m.getName() + " :\n";
                    if(m.getName().equals("constructor")) {
                        this.text += "\taddi $sp, $sp, -8   # Reservo espacio en la pila para el ra y el fp\n" +
                                "\tsw $fp, 0($sp)           # Guardar el frame pointer actual en la pila\n" +
                                "\tsw $ra, 4($sp)           # Guardar el return address actual en la pila\n" +
                                "\tmove $fp, $sp            # Establecer el nuevo frame pointer\n";

                        int reg = CodeGenerator.getNextRegister();
                        int regBef = CodeGenerator.getBefRegister();
                        this.text += "\tli $v0, 9 #Reservamos memoria dinamica (heap)\n"
                                + "\tli $a0," + sizeAtr + "# Reservamos por cada atributo del struct\n"
                                + "\tsyscall\n"
                                //modifico esto pq quiero q siempre se guarde en t0 la direccion de memoria
                                + "\tmove $t" + reg + ",$v0 # Guardamos la dirección de la memoria reservada\n"
                                +"\tla $t" + CodeGenerator.getNextRegister() +", "+value.getName()+"_vtable\n"//agrega agus, hay q ver como son los registros aqui
                                +"\tsw $t" + CodeGenerator.getBefRegister()+",0($t"+reg+")\n"; //AGREGA AGUS

                        int offset = 4;//Agrega Agus
                        this.text += "\t# Primero inicializamos todo por defecto\n";
                        for (EntradaAtributo a : ts.getCurrentStruct().getAtributos().values()) {
                            int reg1 = CodeGenerator.getNextRegister();
                            if(a.getType().equals("Int")){
                                this.text += "\tli $t" + reg1 + ",0\n"
                                        + "\tsw $t" + reg1 + ", " + offset + "($t" + reg +")\n"; //hay q ver pq como se guarda en la pila no hace falta ir calculando el offset
                            } else if(a.getType().equals("Char") || a.getType().equals("Str")){ //nose pq aca decia v
                                this.text += "\tli $t" + reg1 + ", \"\" \n"
                                        + "\tsw $t" + reg1 + ", " + offset + "($t" + reg +")\n";
                            } else if(a.getType().equals("Bool")){
                                this.text += "\tli $t" + reg1 + ",0\n"
                                        + "\tsw $t" + reg1 + ", " + offset + "($t" + reg +")\n";
                            } else {
                                String[] palabras = a.getType().split(" ");
                                String isArray = palabras[0];
                                if (isArray.equals("Array")) {
                                    this.text += "\tli $t" + reg1 + ",space 0\n"
                                            + "\tsw $t" + reg1 + ", " + offset + "($t" + reg +")\n";
                                } else {
                                    this.text += "\tli $t" + reg1 + ",space 0\n"
                                            + "\tsw $t" + reg1 + ", " + offset + "($t" + reg +")\n";
                                }
                            }
                            offset += 4;
                        }

                        this.text +="\tmove $v0, $t" + reg +"\n";
                    }else{ //metodo comun
                        this.text += "\taddi $sp, $sp, -8   # Reservo espacio en la pila para el ra y el fp\n" +
                                "\tsw $fp, 0($sp)           # Guardar el frame pointer actual en la pila\n" +
                                "\tsw $ra, 4($sp)           # Guardar el return address actual en la pila\n" +
                                "\tmove $fp, $sp            # Establecer el nuevo frame pointer\n"+
                                "# Obtener el puntero al objeto\n" +
                                "lw $a0, 0($a0)# Cargar el puntero al objeto (self)\n";
                    }

                    // Recorro las sentencias del metodo
                    if(!m.getSentencias().isEmpty()){
                        if (m.getName().equals("constructor")){
                            this.text += "\t\t\tmove $s1"  + ", $v0   # Guardamos la dirección de la memoria reservadaa line\n"; //esta linea la agregamos para solucionar problema move linea 87
                            int reg1=0;
                            for (NodoLiteral s : m.getSentencias()) {
                                this.text += "\tmove $t" + CodeGenerator.getNextRegister() + ", $v0   # Guardamos la dirección de la memoria reservada\n";
                                reg1 = CodeGenerator.getBefRegister();
                                // Para cada nodo genero codigo
                                this.text += s.generateNodeCode(ts);
                                //this.text += "\tmove $v0, $t" + reg1 + "     # Retornar la dirección base de la estructura en $v0\n";
                            }
                            int reg = CodeGenerator.getBefRegister() - 1;

                            this.text += "\tmove $v0, $t" + reg1+ "     # Retornar la dirección base de la estructura en $v0\n" +
                                    "\t# Restaurar el estado de la pila\n" +
                                    "\tmove $sp, $fp         # Restaurar el puntero de pila\n" +
                                    "\tlw $fp, 0($sp)        # Restaurar el puntero de marco\n" +
                                    "\tlw $ra, 4($sp)        # Restaurar la dirección de retorno\n" +
                                    "\taddi $sp, $sp, 8      # Ajustar el puntero de pila\n" +
                                    "\tjr $ra\n";
                        }else{

                            for (NodoLiteral s : m.getSentencias()) {
                                // Para cada nodo genero codigo
                                this.text += s.generateNodeCode(ts);
                                this.text += "\tmove $v0, $t" +CodeGenerator.getBefRegister()+ "     # Retornar la dirección base de la estructura en $v0\n";
                                this.text +="\tmove $sp, $fp         # Restaurar el puntero de pila\n" +
                                        "\tlw $fp, 0($sp)        # Restaurar el puntero de marco\n" +
                                        "\tlw $ra, 4($sp)        # Restaurar la dirección de retorno\n" +
                                        "\taddi $sp, $sp, 8      # Ajustar el puntero de pila\n" +
                                        "\tjr $ra\n";
                            }
                        }
                    }

                }
            }
        }
    }

    private void generatePred() {

        // IO
        // Código MIPS para las funciones predefinidas de IO

        // st fn out_int(Int s)->void: imprime el argumento
        text += "\nIO_out_int:\n";
        text += "\t# Asumimos que el argumento de la función está en $a0 \n";
        text += "\tli $v0, 1 \n\tsyscall \n\tjr $ra\n";

        // st fn out_str(Str s)->void: imprime el argumento.
        text += "\nIO_out_str:\n";
        text += "\t# Asumimos que el argumento de la función está en $a0 \n";
        text += "\tli $v0, 4 \n\tsyscall \n\tjr $ra\n";

        // st fn out_bool(Bool b)->void: imprime el argumento
        text += "\nIO_out_bool:\n";
        text += "\tbeqz $a0, print_false  # Si $a0 es 0, saltar a print_false\n";
        text += "\t# Si no, imprimir \"true\"\n";
        text += "\tli $v0, 4 \n\tla $a0, true_str \n\tsyscall \n\tjr $ra\n";

        text += "\nprint_false:\n";
        text += "\tli $v0, 4 \n\tla $a0, false_str \n\tsyscall \n\tjr $ra\n";

        text += "\n.data\n";
        text += "\ttrue_str: .asciiz \"true\"\n";
        text += "\tfalse_str: .asciiz \"false\"\n";
        text += "\n.text\n";

        // st fn out_char(Char c)->void: imprime el argumento.
        text += "\nIO_out_char:\n";
        text += "\t# Asumimos que el argumento de la función está en $a0 \n";
        text += "\tli $v0, 11 \n\tsyscall \n\tjr $ra\n";

        // st fn out_array_int(Array a)->void: imprime cada elemento del arreglo de tipo Int.
        // ENTENDER ARRAY Y HACER !!
        text += "\nIO_out_array_int:\n";

        // st fn out_array_str(Array a)->void: imprime cada elemento del arreglo de tipo Str
        // ENTENDER ARRAY Y HACER !!
        text += "\nIO_out_array_str:\n";

        // st fn out_array_bool(Array a)->void: imprime cada elemento del arreglo de tipo Bool.
        // ENTENDER ARRAY Y HACER !!
        text += "\nIO_out_array_bool:\n";

        // st fn out_array_char(Array a)->void: imprime cada elemento del arreglo de tipo Char
        // ENTENDER ARRAY Y HACER !!
        text += "\nIO_out_array_char:\n";

        // st fn_in_str()->Str: lee una cadena de la entrada estandar, sin incluir un caracter de nueva lınea.
        text += "\nIO_in_str:\n";
        text += "\t# Reservar un buffer para almacenar la cadena\n";
        text += "\tla $a0, buffer       # Cargar la dirección de inicio del buffer en $a0\n";
        text += "\tla $a1, 100          # Cargar la longuitud del buffer en $a1\n";
        text += "\tli $v0, 8 \n\tsyscall \n\tjr $ra\n";
        text += "\n.data\n";
        text += "\tbuffer: .space 100    # Buffer para almacenar la cadena leída\n";
        text += "\n.text\n";

        // st fn_in_int()->Int: lee un entero de la entrada estandar.
        text += "\nIO_in_int:\n";
        text += "\tli $v0, 5 \n\tsyscall \n\tjr $ra\n";

        // st fn_in_bool()->Bool: lee un bool de la entrada estandar.
        text += "\nIO_in_bool:\n";
        text += "\tjal IO_in_str # Llamar al método para leer un string\n";
        text += "\t# Verificar si la cadena es \"true\" o \"false\"\n";
        text += "\tla $t0, buffer       # Cargar la dirección de inicio de la cadena en $t0\n";
        text += "\tli $t1, 't'          # Cargar el carácter 't' en $t1\n";
        text += "\tli $t2, 'f'          # Cargar el carácter 'f' en $t2\n";
        text += "\tlb $t3, 0($t0)       # Cargar el primer carácter de la cadena en $t3\n";
        text += "\t# Comparar el primer carácter con 't' para determinar si es \"true\" o \"false\"\n";
        text += "\tbeq $t3, $t1, true_result  # Si es igual a 't', salta a true_result\n";
        text += "\tbeq $t3, $t2, false_result # Si es igual a 'f', salta a false_result\n";

        text += "\n\ttrue_result:\n";
        text += "\tli $v0, 1            # Si la cadena es \"true\", configurar $v0 en 1\n";
        text += "\tjr $ra               # Retornar a la dirección de retorno\n";

        text += "\n\tfalse_result:\n";
        text += "\tli $v0, 0            # Si la cadena es \"true\", configurar $v0 en 1\n";
        text += "\tjr $ra               # Retornar a la dirección de retorno\n";

        // st fn_in_char()->Char: lee un caracter de la entrada estandar.
        text += "\nIO_in_char:\n";
        text += "\tli $v0, 12 \n\tsyscall \n\tjr $ra\n";

        // Array
        // Código MIPS para las funciones predefinidas de Array

        // fn length()->Int. length devuelve la longitud del parametro self.
        text += "\nArray_length:\n";

        // Str
        // Código MIPS para las funciones predefinidas de Str

        // fn length()->Int. length devuelve la longitud del parametro self.
        text += "\nStr_length:\n";

        // fn concat(Str s)->Str. Devuelve la cadena formada al concatenar s despues de self.
        text += "\nStr_concat:\n";

        this.text += "\nend_program:";
    }

    public static int getNextRegister() {
        if(registerCounter == 7){
            resetRegisterCounter();
            return registerCounter;
        } else {
            return registerCounter++;
        }
    }

    public static int getBefRegister() {

        if(registerCounter == 0){
            return 7;
        } else {
            return registerCounter - 1;
        }
    }
    public static int getRest(int n) {
        int count = registerCounter;
        System.out.println(count);
        for (int i = 0; i <= n; i++) {
            if (count == 0){
                count  =7;
            }else{
                count = count -1;
            }


        }
        System.out.println(count);
        return count;
    }

    public static void resetRegisterCounter() {
        registerCounter = 0;
    }

}
