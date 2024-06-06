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

            // Primero verifico si el struct es start (ya que es un caso especial de struct)
            if(struct.getName().equals("start")){

                // Start solo tiene variables
                for (EntradaVariable v : struct.getVariables().values()) {
                    if(v.getType().equals("Int")) {
                        this.data.add(new AbstractMap.SimpleEntry<>(v.getName(),
                                "\n" + struct.getName() + "_" + v.getName() + ": .word 0\n"));
                    } else if(v.getType().equals("Char") || v.getType().equals("Str")) {
                        this.data.add(new AbstractMap.SimpleEntry<>(v.getName(),
                                "\n" + struct.getName() + "_" + v.getName() + ": .asciiz " + " \n"));
                    } else if(v.getType().equals("Bool")){
                            this.data.add(new AbstractMap.SimpleEntry<>(v.getName(),
                                    "\n" + struct.getName() + "_" + v.getName() + ": .word 1\n"));
                        } else {
                            String[] palabras = v.getType().split(" ");
                            String isArray = palabras[0];
                            if (isArray.equals("Array")) {
                                this.data.add(new AbstractMap.SimpleEntry<>(v.getName(),
                                        "\n" + struct.getName() + "_" + v.getName() + ": .space 0\n"));
                            } else {
                                this.data.add(new AbstractMap.SimpleEntry<>(v.getName(),
                                        "\n" + struct.getName() + "_" + v.getName() + ": .space 8\n"));
                            }
                        }
                    }

            } else { // Ahora para los demas structs
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

        this.text += "main:\n";
        //int numVar = (ts.getCurrentStruct().getVariables().size() + 1) * 4 * (-1);
        //this.text += "addi $sp, $sp," + numVar +
        //        "\nsw $fp, " + ((numVar + 4) * -1) + "($sp)" +
        //        "\nmove $fp, $sp\n";



        //reservo memoria para el start, calculo la cantidad contando las declaraciones y sumando 4
        // luego agrego la $ra,end_program para cargar el puntero a la direccion de fin de programa
        this.text += "addi $sp, $sp,"+(((ts.getCurrentStruct().getVariables().size())+1)*4)*-1+"#reservo espacio en la pila"
                    +"la $ra,end_program #carga en $ra el puntero al espacio de fin de programa"+
                    "sw $ra, 0($sp)  # Guardar la dirección de retorno en la pila (end_program)"; //es es siempre 0, porque estamos guardando en ra, lo q acabamos de cargar anteriormente
        // Recorro las sentencias de start
        if (!value.getSentencias().isEmpty()) {
            for (NodoLiteral s : value.getSentencias()) {
                // Para cada sentencia genero codigo
                this.text += s.generateNodeCode(ts);
            }
            this.text +="li $v0, 10\nsyscall\n";
        }

        /*this.text += "move $sp, $fp" +
                "\nlw $fp, " + ((numVar + 4) * -1) + "($sp)" +
                "\naddi $sp, $sp," + numVar + "\n";*/


        // Recorro cada struct
        for (Map.Entry<String, NodoStruct> entry : ast.getStructs().entrySet()) {
            value = entry.getValue();
            ast.setCurrentStruct(value);

            if(!value.getName().equals("start")){

                // Recorro todos los nodos metodos del struct
                for (NodoMetodo m : value.getMetodos().values()) {
                    ts.setCurrentStruct(ts.getStruct(value.getName()));
                    ts.setCurrentMetod(ts.getCurrentStruct().getMetodo(m.getName()));
                    int sizeAtr = ts.getCurrentStruct().getAtributos().size() * 4;

                    if(m.getName().equals("constructor")) {
                        int reg = CodeGenerator.getNextRegister();
                        this.text += "\n" + value.getName() + "_" + m.getName() + " :\n"
                                + "\tli $v0, 9 #Reservamos memoria dinamica (heap)\n"
                                + "\tli $a0," + sizeAtr + "# Reservamos por cada atributo del struct\n"
                                + "\tsyscall\n"
                                //modifico esto pq quiero q siempre se guarde en t0 la direccion de memoria
                                + "\tmove $t0" /*+ reg*/ + "$v0 # Guardamos la dirección de la memoria reservada\n";
                        int offset = 0;
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
                        this.text +="move $v0, $t0 # Retornar la dirección base de la estructura en $v0\n" +
                                "jr $ra# Retornar";
                    }

                    // Recorro las sentencias del metodo
                    if(!m.getSentencias().isEmpty()){
                        this.text += "\n" +value.getName() + "_" + m.getName() + " :\n";
                        for (NodoLiteral s : m.getSentencias()) {

                            // Para cada nodo genero codigo
                            this.text += s.generateNodeCode(ts);

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
        text += "\nStr_length:\n";

        // Str
        // Código MIPS para las funciones predefinidas de Str

        // fn length()->Int. length devuelve la longitud del parametro self.
        text += "\nStr_length:\n";

        // fn concat(Str s)->Str. Devuelve la cadena formada al concatenar s despues de self.
        text += "\nStr_concat:\n";
    }

    public static int getNextRegister() {
        if(registerCounter == 9){
            resetRegisterCounter();
            return registerCounter;
        } else {
            return registerCounter++;
        }
    }

    public static int getBefRegister() {
        return registerCounter - 1;
    }

    public static void resetRegisterCounter() {
        registerCounter = 0;
    }

    public static String generateLabel(TablaSimbolos ts,String value) {
        // Implementar la generación de etiquetas únicas
        if(ts.getCurrentStruct().getName().equals("start")){
            return ts.getCurrentStruct().getName() +"_"+ value;
        }
        return ts.getCurrentStruct().getName() +"_"+ ts.getCurrentMetod().getName()+"_"+ value;
    }

}
