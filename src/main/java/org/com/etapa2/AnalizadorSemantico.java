package org.com.etapa2;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AnalizadorSemantico extends AnalizadorSintactico {

    // Tabla de símbolos
    private static Map<String, Clase> tablaSimbolos = new HashMap<>();

    // Método principal para iniciar el análisis semántico
    public static void main(String[] args) {
        // Código de inicialización del analizador léxico y sintáctico (ya proporcionado en AnalizadorSintactico)
        // ...

        // Iniciar análisis semántico
        try {
            analisisSemantico();
            System.out.println("CORRECTO: ANALISIS SEMANTICO \n");

        } catch (SyntactErrorException e) {
            System.out.println("ERROR: SINTACTICO\n| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
            System.out.println("| LINEA " + e.getLineNumber() + " | COLUMNA " + e.getColumnNumber() + " | " + e.getDescription() + "|\n");

        } catch (SemanticErrorException e) {
            System.out.println("ERROR: SEMANTICO\n| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
            System.out.println("| LINEA " + e.getLineNumber() + " | COLUMNA " + e.getColumnNumber() + " | " + e.getDescription() + "|\n");

        } catch (LexicalErrorException e) {
            System.out.println("ERROR: LEXICO\n| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
            System.out.println("| LINEA " + e.getLineNumber() + " | COLUMNA " + e.getColumnNumber() + " | " + e.getDescription() + "|\n");
        }
    }

    // Método para realizar el análisis semántico
    private static void analisisSemantico() {
        if (currentToken.getLexema().equals("struct") ||
                currentToken.getLexema().equals("impl")){
            definiciones();
            start();
        } else if(currentToken.getLexema().equals("start")) {
            start();
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: struct, impl o start. Se encontró: " + currentToken.getLexema(),
                    "program");
        }
        match("EOF");
    }

    // Método para emparejar el token actual con un token esperado
    @Override
    protected static void match(String expectedToken) {
        if(!flagMatch){
            if (currentToken.getLexema().equals(expectedToken) ||
                    currentToken.getName().equals(expectedToken)) {
                advance();
            } else {
                throw new SyntactErrorException(currentToken.getLine(), currentToken.getCol(),
                        "Se esperaba: " + expectedToken + ". Se encontró: " + currentToken.getLexema(),"match");
            }
        }else{
            flagMatch = false;
        }
    }

    // Método para verificar la existencia de una clase en la tabla de símbolos
    private static boolean existeClase(String nombreClase) {
        return tablaSimbolos.containsKey(nombreClase);
    }

    // Método para agregar una clase a la tabla de símbolos
    private static void agregarClase(Clase clase) {
        tablaSimbolos.put(clase.getNombre(), clase);
    }

    // Método para verificar la existencia de un método en una clase
    private static boolean existeMetodoEnClase(String nombreMetodo, String nombreClase) {
        if (existeClase(nombreClase)) {
            return tablaSimbolos.get(nombreClase).existeMetodo(nombreMetodo);
        }
        return false;
    }

    // Método para verificar la existencia de un atributo en una clase
    private static boolean existeAtributoEnClase(String nombreAtributo, String nombreClase) {
        if (existeClase(nombreClase)) {
            return tablaSimbolos.get(nombreClase).existeAtributo(nombreAtributo);
        }
        return false;
    }

    // Método para verificar la existencia de una clase o método
    private static void verificarExistencia(String nombre, String tipo) {
        if (!existeClase(nombre) && !existeMetodoEnClase(nombre, tipo)) {
            throw new SemanticErrorException(currentToken.getLine(), currentToken.getCol(),
                    "El " + tipo + " '" + nombre + "' no está definido.");
        }
    }

    // Métodos restantes para cada no terminal de la gramática
    // ...
}
