package org.com.etapa5;
import org.com.etapa5.ArbolAST.*;
import org.com.etapa5.Exceptions.LexicalErrorException;
import org.com.etapa5.Exceptions.SemantErrorException;
import org.com.etapa5.Exceptions.SyntactErrorException;
import org.com.etapa5.TablaDeSimbolos.*;

import java.io.File;

public class AnalizadorSintactico {

    private static AnalizadorLexico l;
    private static AnalizadorSemantico s;
    private static CodeGenerator g;
    private static Token currentToken;
    private static boolean flagMatch = false;
    private static boolean isStart = false;
    private static int isConstr = 0;
    private static boolean isLocal = false;
    private static TablaSimbolos ts;
    private static AST ast;
    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("ERROR: Debe proporcionar el nombre del archivo fuente.ru como argumento");
            System.out.println("Uso: java -jar tinyRU.jar <ARCHIVO_FUENTE> ");
            return;
        }

        String input = args[0];
        String fileName;

        // Obtener el nombre del archivo
        File inputFile = new File(input);
        fileName = inputFile.getName();


        // Verificar existencia del archivo
        File file = new File(input);
        if (!file.exists()) {
            System.out.println("ERROR: El archivo fuente '" + fileName + "' no existe.");
            return;
        }

        // Verificar si el archivo no está vacío
        if (file.length() == 0) {
            System.out.println("ERROR: El archivo fuente '" + fileName + "' está vacío.");
            return;
        }

        // Verificar extensión del archivo
        if (!input.endsWith(".ru")) {
            System.out.println("ERROR: El archivo fuente debe tener la extensión '.ru'.");
            return;
        }

        l = new AnalizadorLexico();
        ts = new TablaSimbolos();
        ast = new AST();
        s = new AnalizadorSemantico(ts,ast);
        g = new CodeGenerator(ts,ast);
        try {
            l.analyzeFile(input);
            // Comenzar el análisis sintáctico desde el símbolo inicial ⟨program⟩
            if(l.countTokens() <= 0) { // No hay tokens
                currentToken = new Token(0, 0, "EOF", "EOF");
            } else {
                currentToken = l.nextToken();
            }
            program();      // Analisis Sintactico y Tabla de Simbolos
            s.checkDecl();  // Chequeo de Declaraciones
            //String json = ts.printJSON_Tabla(fileName);
            //ts.saveJSON(json, fileName + ".json");
            s.checkSent();  // Chequeo de Sentencias
            //String json = ast.printJSON_Arbol(fileName);
            //ast.saveJSON(json, fileName + ".json");
            String asm = g.generateCode();
            g.saveASM(asm, fileName + ".asm");
            System.out.println("CORRECTO GENERACION DE CODIGO\n");

        } catch (LexicalErrorException e) {
            System.out.println("ERROR: LEXICO\n| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
            System.out.println("| LINEA " + e.getLineNumber() + " | COLUMNA " + e.getColumnNumber() + " | " + e.getDescription() + "|\n");

        } catch (SyntactErrorException e) {
            System.out.println("ERROR: SINTACTICO\n| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
            System.out.println("| LINEA " + e.getLineNumber() + " | COLUMNA " + e.getColumnNumber() + " | " + e.getDescription() + "|\n");

        } catch (SemantErrorException e) {
            System.out.println("ERROR: SEMANTICO \n| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |");
            System.out.println("| LINEA " + e.getLineNumber() + " | COLUMNA " + e.getColumnNumber() + " | " + e.getDescription() + "|\n");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Método para emparejar el token actual con un token esperado
    private static void match(String expectedToken) {

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

    // Método para avanzar al siguiente token
    private static void advance() {
        if(l.countTokens() <= 0){ // No hay mas tokens
            currentToken = new Token(0, 0, "EOF", "EOF");
        } else {
            currentToken = l.nextToken();
        }
    }

    private static void program() throws Exception {
        if (currentToken.getLexema().equals("struct") ||
                currentToken.getLexema().equals("impl")){
            definiciones();
            isStart = true;
            start();
        } else if(currentToken.getLexema().equals("start")) {
            isStart = true;
            start();
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: struct, impl o start. Se encontró: " + currentToken.getLexema(),
                    "program");
        }
        match("EOF");
    }

    private static void start() {
        EntradaStruct e = new EntradaStruct(currentToken.getLine(), currentToken.getCol());
        ts.setCurrentStruct(e);
        ts.insertStruct_struct(e, currentToken);
        match("start");

        // Construccion AST
        NodoStruct nodo = new NodoStruct(currentToken.getLine(), currentToken.getCol(), "start");
        ast.setCurrentStruct(nodo); // Actualizo el struct actual
        ast.getProfundidad().push(nodo);
        ast.insertStruct("start",nodo); // Inserto el struct en el AST
        bloqueMetodo();
    }

    private static void definiciones() throws Exception {
        if (currentToken.getLexema().equals("struct")) {
            struct();
            definiciones1();
        } else if (currentToken.getLexema().equals("impl")) {
            impl();
            definiciones1();
        } else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: struct o impl. Se encontró: " + currentToken.getLexema(),
                    "definiciones");
        }
    }

    private static void definiciones1() throws Exception {
        if (currentToken.getLexema().equals("struct") ||
                currentToken.getLexema().equals("impl")) {
            definiciones();
        } else if(currentToken.getLexema().equals("start")){
            // lambda
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: start, struct o impl. Se encontró: " + currentToken.getLexema(),
                    "definiciones1");
        }
    }

    private static void struct(){
        match("struct");
        String nombre = currentToken.getLexema();
        match("struct_name");
        // Construccion TS
        EntradaStruct e;
        if(!(ts.searchStruct(nombre))){
            e = new EntradaStruct(nombre, currentToken.getLine(), currentToken.getCol());
        } else {
            e = ts.getStruct(nombre);
        }
        ts.setCurrentStruct(e);
        struct1();
    }

    private static void struct1() {
        if (currentToken.getLexema().equals(":")) {
            String nombre_ancestro = herencia();
            ts.getCurrentStruct().setHerencia(nombre_ancestro);
            ts.insertStruct_struct(ts.getCurrentStruct(), currentToken);
            ts.getCurrentStruct().sethaveStruct(true);
            match("{");
            struct2();
        } else if (currentToken.getLexema().equals("{")) {
            ts.getCurrentStruct().setHerencia("Object");
            ts.insertStruct_struct(ts.getCurrentStruct(), currentToken);
            ts.getCurrentStruct().sethaveStruct(true);
            match("{");
            struct2();
        } else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: ':' o '{'. Se encontró: " + currentToken.getLexema(),
                    "struct1");
        }
    }

    private static void struct2() {
        if (currentToken.getLexema().equals("pri") ||
                currentToken.getLexema().equals("Str") ||
                currentToken.getLexema().equals("Bool") ||
                currentToken.getLexema().equals("Int") ||
                currentToken.getLexema().equals("Char") ||
                currentToken.getName().equals("struct_name") ||
                currentToken.getLexema().equals("Array")) {
            atributos();
            match("}");
        } else if (currentToken.getLexema().equals("}")) {
            match("}");
        } else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: Identificador de Tipo (Str, Char, Int, Bool, Array), idStruct, pri o '}'. " +
                            "Se encontró: " + currentToken.getLexema(),
                    "struct2");
        }
    }

    private static void atributos() {
        atributo();
        atributos1();
    }

    private static void atributos1() {
        if (currentToken.getLexema().equals("pri") ||
                currentToken.getLexema().equals("Str") ||
                currentToken.getLexema().equals("Bool") ||
                currentToken.getLexema().equals("Int") ||
                currentToken.getLexema().equals("Char") ||
                currentToken.getLexema().equals("Array") ||
                currentToken.getName().equals("struct_name")){
            atributos();
        } else if(currentToken.getLexema().equals("}")){
            // lambda
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: Identificador de Tipo (Str, Char, Int, Bool, Array), idStruct, " +
                            "pri o '}'. Se encontró: " + currentToken.getLexema(),
                    "atributos1");
        }
    }

    private static void impl() {
        match("impl");
        String nombre = currentToken.getLexema();
        match("struct_name");

        // Construccion TS
        EntradaStruct e;
        if(!(ts.searchStruct(nombre))){
            e = new EntradaStruct(nombre, currentToken.getLine(), currentToken.getCol());
        } else {
            e = ts.getStruct(nombre);
        }
        ts.setCurrentStruct(e);
        ts.insertStruct_impl(ts.getCurrentStruct(), currentToken);
        ts.getCurrentStruct().sethaveImpl(true);

        // Construccion AST
        NodoStruct nodo = new NodoStruct(currentToken.getLine(), currentToken.getCol(), nombre);
        ast.setCurrentStruct(nodo); // Actualizo el struct actual
        ast.insertStruct(nombre,nodo); // Inserto el struct en el AST
        match("{");
        miembros();
        match("}");
    }

    private static void miembros() {
        miembro();
        miembros1();
    }

    private static void miembros1() {
        if (currentToken.getLexema().equals("st")
                || currentToken.getLexema().equals("fn")
                || currentToken.getLexema().equals(".")) {
            miembros();
        } else if(currentToken.getLexema().equals("}")){
            // lambda
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: st, fn, '.' o '}'. Se encontró: " + currentToken.getLexema(),
                    "miembros1");
        }
    }

    private static String herencia() {
        match(":");
        String nombre = currentToken.getLexema();
        tipo();
        return nombre;
    }

    private static void miembro() {
        if (currentToken.getLexema().equals("st")
                || currentToken.getLexema().equals("fn")){
            metodo();
        } else if(currentToken.getLexema().equals(".")){
            constructor();
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: st, fn o '.'. Se encontró: " + currentToken.getLexema(),
                    "miembro");
        }
    }

    private static void constructor() {
        match(".");

        // TS
        EntradaMetodo e = new EntradaMetodo(currentToken.getLine(), currentToken.getCol(),ts.getCurrentStruct().getMetodos().size());
        ts.setCurrentMetod(e);
        ts.getCurrentStruct().insertMetodo("constructor",e);
        ts.getCurrentStruct().setHaveConst(true);
        isConstr = 1;

        // AST
        NodoMetodo nodo = new NodoMetodo(currentToken.getLine(), currentToken.getCol(),
                "constructor");
        ast.setCurrentMetodo(nodo); // Actualizo el metodo actual
        ast.getProfundidad().push(nodo);

        argumentosFormales();
        bloqueMetodo();
        isConstr = 0;

        // Fin del bloque del constructor
        // Inserto el constructor en su struct padre (el actual)
        ast.getCurrentStruct().insertMetodo(nodo.getName(),nodo);
    }

    private static void atributo() {
        if (currentToken.getLexema().equals("pri")){
            visibilidad();
            String tipo = tipo();
            EntradaAtributo e = new EntradaAtributo(currentToken.getLexema(), tipo, false,
                    ts.getCurrentStruct().getAtributos().size(), currentToken.getLine(), currentToken.getCol());
            ts.getCurrentStruct().insertAtributo(currentToken.getLexema(),e);
            ts.setCurrentAtr(e);
            listaDeclaracionVariables();
            match(";");
        } else if (currentToken.getLexema().equals("Str") ||
                    currentToken.getLexema().equals("Bool") ||
                    currentToken.getLexema().equals("Int") ||
                    currentToken.getLexema().equals("Char") ||
                    currentToken.getLexema().equals("Array") ||
                    currentToken.getName().equals("struct_name")) {
            String tipo = tipo();
            EntradaAtributo e = new EntradaAtributo(currentToken.getLexema(), tipo, true, ts.getCurrentStruct().getAtributos().size(),
                    currentToken.getLine(), currentToken.getCol());
            ts.getCurrentStruct().insertAtributo(currentToken.getLexema(),e);
            ts.setCurrentAtr(e);
            listaDeclaracionVariables();
            match(";");
        } else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: Identificador de Tipo (Str, Char, Int, Bool, Array), idStruct o pri. " +
                            "Se encontró: " + currentToken.getLexema(),
                    "atributo");
        }
    }

    private static void metodo() {
        if (currentToken.getLexema().equals("st")){
            formaMetodo();
            match("fn");

            // Tabla de Simbolos
            EntradaMetodo e = new EntradaMetodo(currentToken.getLexema(),true,ts.getCurrentStruct().getMetodos().size()- isConstr,
                    currentToken.getLine(), currentToken.getCol());
            ts.setCurrentMetod(e);
            ts.getCurrentStruct().insertMetodo(currentToken.getLexema(),e);

            // AST
            NodoMetodo nodo = new NodoMetodo(currentToken.getLine(), currentToken.getCol(),
                    currentToken.getLexema());
            ast.setCurrentMetodo(nodo); // Actualizo el metodo actual
            ast.getProfundidad().push(nodo);
            match("id");
            argumentosFormales();
            match("->");
            String ret = tipoMetodo();

            ts.getCurrentMetod().setRet(ret);
            bloqueMetodo();

            // Fin del bloque del metodo
            // Inserto el metodo en su struct padre (el actual)
            ast.getCurrentStruct().insertMetodo(nodo.getName(),nodo);

        } else if (currentToken.getLexema().equals("fn")) {
            match("fn");

            // Tabla de Simbolos
            EntradaMetodo e = new EntradaMetodo(currentToken.getLexema(),false,ts.getCurrentStruct().getMetodos().size() - isConstr,
                    currentToken.getLine(), currentToken.getCol());
            ts.setCurrentMetod(e);
            ts.getCurrentStruct().insertMetodo(currentToken.getLexema(),e);

            // AST
            NodoMetodo nodo = new NodoMetodo(currentToken.getLine(), currentToken.getCol(),
                    currentToken.getLexema());
            ast.setCurrentMetodo(nodo); // Actualizo el metodo actual
            ast.getProfundidad().push(nodo);
            match("id");
            argumentosFormales();
            match("->");
            String ret = tipoMetodo();

            ts.getCurrentMetod().setRet(ret);
            bloqueMetodo();

            // Fin del bloque del metodo
            // Inserto el metodo en su struct padre (el actual)
            ast.getCurrentStruct().insertMetodo(nodo.getName(), nodo);

        } else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: st o fn. Se encontró: " + currentToken.getLexema(),
                    "metodo");
        }
    }

    private static void visibilidad() {
        match("pri");
    }

    private static void formaMetodo() {
        match("st");
    }

    private static void bloqueMetodo() {
        match("{");
        bloqueMetodo1();
    }

    private static void bloqueMetodo1() {
        if (currentToken.getLexema().equals("Str") ||
                currentToken.getLexema().equals("Bool") ||
                currentToken.getLexema().equals("Int") ||
                currentToken.getLexema().equals("Char") ||
                currentToken.getLexema().equals("Array") ||
                currentToken.getName().equals("struct_name")){
            declaraciones();
            bloqueMetodo2();
        } else if (currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals("if") ||
                currentToken.getLexema().equals("while") ||
                currentToken.getLexema().equals("ret") ||
                currentToken.getName().equals("id") ||
                currentToken.getLexema().equals("self") ||
                currentToken.getLexema().equals("{") ||
                currentToken.getLexema().equals("(")){
            sentencias();
            match("}");
        } else if(currentToken.getLexema().equals("}")) {
            match("}");
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: Identificador de Tipo (Str, Char, Int, Bool, Array), idStruct " +
                            ",de declaracion (;, if, while, ret, id, self, (, {)" +
                            "o '}'. Se encontró: " + currentToken.getLexema(),
                    "bloqueMetodo1");
        }
    }

    private static void bloqueMetodo2() {
        if (currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals("if") ||
                currentToken.getLexema().equals("ret") ||
                currentToken.getLexema().equals("while") ||
                currentToken.getName().equals("id") ||
                currentToken.getLexema().equals("self") ||
                currentToken.getLexema().equals("{") ||
                currentToken.getLexema().equals("(")){
                sentencias();
            match("}");
        } else if(currentToken.getLexema().equals("}")) {
            match("}");
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: ;, if, while, ret, self, (, {)" +
                            "o '}'. Se encontró: " + currentToken.getLexema(),
                    "bloqueMetodo2");
        }
    }

    private static void declaraciones() {
        declVarLocales();
        declaraciones1();
    }

    private static void declaraciones1() {
        if (currentToken.getLexema().equals("Str") ||
                currentToken.getLexema().equals("Bool") ||
                currentToken.getLexema().equals("Int") ||
                currentToken.getLexema().equals("Char") ||
                currentToken.getLexema().equals("Array") ||
                currentToken.getName().equals("struct_name")){
            declaraciones();
        } else if(currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals("if") ||
                currentToken.getLexema().equals("while") ||
                currentToken.getLexema().equals("ret") ||
                currentToken.getLexema().equals("self") ||
                currentToken.getLexema().equals("(") ||
                currentToken.getLexema().equals("{") ||
                currentToken.getLexema().equals("}") ||
                currentToken.getName().equals("id")){
            //lambda
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: Identificador de Tipo (Str, Char, Int, Bool, Array), idStruct, id " +
                            ",de sentencia (;, if, while, ret, self, (, {, }). " +
                            "Se encontró: " + currentToken.getLexema(),
                    "declaraciones1");
        }
    }

    private static void sentencias() {
        NodoLiteral nodo = sentencia();
        (ast.getProfundidad().peek()).insertSentencia(nodo);
        // Cuando terminaron las sentencias
        sentencias1();

    }

    private static void sentencias1() {
        if(currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals("if") ||
                currentToken.getLexema().equals("while") ||
                currentToken.getLexema().equals("ret") ||
                currentToken.getLexema().equals("self") ||
                currentToken.getLexema().equals("(") ||
                currentToken.getLexema().equals("{") ||
                currentToken.getName().equals("id")){
            sentencias();
        } else if(currentToken.getLexema().equals("}")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: ';', if, while, ret, id, self, '(', '{' o '}'. " +
                            "Se encontró: " + currentToken.getLexema(),
                    "sentencias1");
        }
    }

    private static void declVarLocales() {
        String tipo = tipo();
        if(isStart){
            EntradaVariable e = new EntradaVariable(currentToken.getLexema(), tipo,
                    ts.getCurrentStruct().getVariables().size(), currentToken.getLine(), currentToken.getCol());
            ts.getCurrentStruct().insertVariable(currentToken.getLexema(),e);
            ts.setCurrentVar(e);
        } else{
            EntradaVariable e = new EntradaVariable(currentToken.getLexema(), tipo,
                    ts.getCurrentMetod().getVariables().size(), currentToken.getLine(), currentToken.getCol());
            ts.getCurrentMetod().insertVariable(currentToken.getLexema(),e);
            ts.setCurrentVar(e);
        }
        isLocal = true;
        listaDeclaracionVariables();
        match(";");
        isLocal = false;
    }

    private static void listaDeclaracionVariables() {
        match("id");
        listaDeclaracionVariables1();
    }

    private static void listaDeclaracionVariables1() {
        if(currentToken.getLexema().equals(",")){
            match(",");
            if(isStart){
                EntradaVariable e = new EntradaVariable(currentToken.getLexema(), ts.getCurrentVar().getType(),
                        ts.getCurrentStruct().getVariables().size(), currentToken.getLine(), currentToken.getCol());
                ts.getCurrentStruct().insertVariable(currentToken.getLexema(), e);
            } else if(!isLocal) {
                EntradaAtributo e = new EntradaAtributo(currentToken.getLexema(), ts.getCurrentAtr().getType(), ts.getCurrentAtr().getPublic(),ts.getCurrentStruct().getAtributos().size(),
                        currentToken.getLine(), currentToken.getCol());
                ts.getCurrentStruct().insertAtributo(currentToken.getLexema(), e);
            } else {
                EntradaVariable e = new EntradaVariable(currentToken.getLexema(), ts.getCurrentVar().getType(),
                        ts.getCurrentMetod().getVariables().size(), currentToken.getLine(), currentToken.getCol());
                ts.getCurrentMetod().insertVariable(currentToken.getLexema(), e);
            }
            listaDeclaracionVariables();
        }else if(currentToken.getLexema().equals(";")){
            //lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: ',' o ';'. Se encontró " + currentToken.getLexema(),
                    "listaDeclaracionVariables1");
        }
    }

    private static void argumentosFormales() {
        match("(");
        argumentosFormales1();
    }

    private static void argumentosFormales1() {
        if (currentToken.getLexema().equals("Str") ||
                currentToken.getLexema().equals("Bool") ||
                currentToken.getLexema().equals("Int") ||
                currentToken.getLexema().equals("Char") ||
                currentToken.getLexema().equals("Array") ||
                currentToken.getName().equals("struct_name")){
            listaArgumentosFormales();
            match(")");
        }else if(currentToken.getLexema().equals(")")){
            match(")");
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: Identificador de Tipo (Str, Char, Int, Bool, Array), idStruct o ')'. Se encontró " + currentToken.getLexema(),
                    "argumentosFormales1");
        }
    }

    private static void listaArgumentosFormales() {
        argumentoFormal();
        listaArgumentosFormales1();
    }

    private static void listaArgumentosFormales1() {
        if (currentToken.getLexema().equals(",")){
            match(",");
            listaArgumentosFormales();
        }else if(currentToken.getLexema().equals(")")){
            //lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: ',' o ')'. Se encontró " + currentToken.getLexema(),
                    "listaArgumentosFormales1");
        }
    }

    private static void argumentoFormal() {
        String tipo = tipo();
        EntradaParametro e = new EntradaParametro(currentToken.getLexema(), tipo, ts.getCurrentMetod().getParametros().size(),
                currentToken.getLine(), currentToken.getCol());
        ts.getCurrentMetod().insertParametro(currentToken.getLexema(),e);
        match("id");
    }

    private static String tipoMetodo() {
        if (currentToken.getLexema().equals("Str") ||
                currentToken.getLexema().equals("Bool") ||
                currentToken.getLexema().equals("Int") ||
                currentToken.getLexema().equals("Char") ||
                currentToken.getLexema().equals("Array") ||
                currentToken.getName().equals("struct_name")){
            return tipo();
        }else if(currentToken.getLexema().equals("void")){
            match("void");
            return "void";
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: Identificador de Tipo (Str, Char, Int, Bool, Array, void) o idStruct. Se encontró " + currentToken.getLexema(),
                    "tipoMetodo");
        }
    }

    private static String tipo() {
        if (currentToken.getLexema().equals("Str") ||
                currentToken.getLexema().equals("Bool") ||
                currentToken.getLexema().equals("Int") ||
                currentToken.getLexema().equals("Char")) {
            String tipo = currentToken.getLexema();
            tipoPrimitivo();
            return tipo;
        }else if(currentToken.getLexema().equals("Array")){
            return tipoArreglo();
        }else if(currentToken.getName().equals("struct_name")){
            String tipo = currentToken.getLexema();
            tipoReferencia();
            return tipo;
        }else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: Identificador de Tipo (Str, Char, Int, Bool, Array) o idStruct. " +
                            "Se encontró " + currentToken.getLexema(),
                    "tipo");
        }
    }

    private static void tipoPrimitivo() {
        if (currentToken.getLexema().equals("Str")){
            match("Str");
        } else if (currentToken.getLexema().equals("Bool")){
            match("Bool");
        } else if (currentToken.getLexema().equals("Int")){
            match("Int");
        } else if(currentToken.getLexema().equals("Char")){
            match("Char");
        } else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: Identificador de Tipo (Str, Char, Int, Bool, Array). " +
                            "Se encontró " + currentToken.getLexema(),
                    "tipo");
        }
    }

    private static void tipoReferencia() {
        match("struct_name");
    }
    private static String tipoArreglo() {
        match("Array");
        String tipo = currentToken.getLexema();
        tipoPrimitivo();
        return "Array " + tipo;
    }

    private static NodoLiteral sentencia() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        if (currentToken.getLexema().equals(";")){
            NodoLiteral nodo = new NodoLiteral(currentToken.getLine(),currentToken.getCol(),
                    "punto y coma","void",";");
            match(";");
            return nodo;
        } else if (currentToken.getLexema().equals("self") ||
                currentToken.getName().equals("id") ){
            NodoLiteral nodo = asignacion(); // AST Asignaciones
            match(";");
            return nodo;
        } else if (currentToken.getLexema().equals("(") ){
            NodoLiteral exp = sentenciaSimple(); // AST Sentencia Simple
            match(";");
            return new NodoExpresion(line,col,"Sentencia Simple",exp.getNodeType(), null,exp);
        } else if (currentToken.getLexema().equals("if")){
            match("if"); // AST IF
            match("(");
            NodoLiteral exp = expresion();
            match(")");
            NodoIf nodoIf= new NodoIf(line,col,exp);
            ast.getProfundidad().push(nodoIf);
            NodoLiteral s = sentencia();
            (ast.getProfundidad().peek()).insertSentencia(s);
            ast.getProfundidad().pop();

            // Caso de que tenga un else
            NodoElse nodoElse = new NodoElse(line,col,nodoIf);
            ast.getProfundidad().push(nodoElse);
            sentencia1(); // AST ELSE
            ast.getProfundidad().pop();
            // Inserto el else en el nodo if
            nodoIf.setNodoElse(nodoElse);
            return nodoIf;
        } else if (currentToken.getLexema().equals("while")){
            match("while"); // AST While
            match("(");
            NodoLiteral exp = expresion();
            match(")");
            NodoWhile nodoW= new NodoWhile(line,col,exp);
            ast.getProfundidad().push(nodoW);
            NodoLiteral s = sentencia();
            (ast.getProfundidad().peek()).insertSentencia(s);
            ast.getProfundidad().pop();
            return nodoW;
        } else if (currentToken.getLexema().equals("{")){
            NodoBloque nodo = new NodoBloque(currentToken.getLine(), currentToken.getCol());
            ast.getProfundidad().push(nodo);
            bloque(); // AST Bloque
            ast.getProfundidad().pop();
            return nodo;

        } else if (currentToken.getLexema().equals("ret")){
            match("ret"); // AST Retorno
            NodoLiteral exp = sentencia2();

            if(isStart){
                return new NodoExpresion(line,col,"Retorno","void", ";", exp);
            }
            return new NodoExpresion(line,col,"Retorno",ts.getCurrentMetod().getRet(), ";", exp);
        }
        return null;
    }
    private static void sentencia1() {
        if (currentToken.getLexema().equals("else")){
            match("else");
            NodoLiteral s = sentencia();
            (ast.getProfundidad().peek()).insertSentencia(s);
        }else if(currentToken.getLexema().equals("}")||
                currentToken.getLexema().equals(";")||
                currentToken.getLexema().equals("if")||
                currentToken.getLexema().equals("while")||
                currentToken.getLexema().equals("ret")||
                currentToken.getName().equals("id")||
                currentToken.getLexema().equals("self")||
                (currentToken.getLexema().equals("(")) ||
                (currentToken.getLexema().equals("{"))){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: else, if, ';', self, id, '(', '{', while o ret. " +
                            "Se encontró " + currentToken.getLexema(),
                    "sentencia1");
        }
    }

    private static NodoLiteral sentencia2() {
        if (currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("-") ||
                currentToken.getLexema().equals("!") ||
                currentToken.getLexema().equals("++") ||
                currentToken.getLexema().equals("--") ||
                currentToken.getLexema().equals("nil") ||
                currentToken.getLexema().equals("true") ||
                currentToken.getLexema().equals("false") ||
                currentToken.getLexema().equals("self") ||
                currentToken.getLexema().equals("(") ||
                currentToken.getLexema().equals("new") ||
                currentToken.getName().equals("int") ||
                currentToken.getName().equals("str") ||
                currentToken.getName().equals("char") ||
                currentToken.getName().equals("id") ||
                currentToken.getName().equals("struct_name")){
            NodoLiteral nodo = expresion();
            match(";");
            return nodo;
        }else if(currentToken.getLexema().equals(";")){
            NodoLiteral nodo = new NodoLiteral(currentToken.getLine(),currentToken.getCol(),
                    "punto y coma","void",";");
            match(";");
            return nodo;
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operadores(+,-,++.--), nill, Identificador de Tipo (Str, Char, Int, Bool, Array), id, idStruct, " +
                            "; , self, id , o new. Se encontró " + currentToken.getLexema(),
                    "sentencia2");
        }
    }

    private static void bloque() {
        match("{");
        bloque1();
    }

    private static void bloque1() {
        if (currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals("if") ||
                currentToken.getLexema().equals("while") ||
                currentToken.getLexema().equals("ret") ||
                currentToken.getName().equals("id") ||
                currentToken.getLexema().equals("self") ||
                currentToken.getLexema().equals("(") ||
                currentToken.getLexema().equals("{")){
            sentencias();
            match("}");
        }else if(currentToken.getLexema().equals("}")){
            match("}");
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: ';', if, while, ret, id, self, '(', '{' o '}'. " +
                            "Se encontró " + currentToken.getLexema(),
                    "bloque1");
        }
    }

    private static NodoLiteral asignacion() {
        if (currentToken.getName().equals("id")){
            // AST
            // Me encuentro con un identificador
            ast.getProfundidad().push(new NodoLiteral(currentToken.getLine(),currentToken.getCol(),
                            currentToken.getLexema(),null, null));

            NodoLiteral nodoI= accesoVarSimple();
            // Ya puedo guardar el lado izquierdo de la asignacion
            if(nodoI == null){
                nodoI = (NodoLiteral) ast.getProfundidad().pop();
            }
            match("=");

            // Ahora armo el lado derecho de la asignacion
            NodoLiteral nodoD = expresion();

            // Se termino de armar la asignacion
            return new NodoAsignacion(currentToken.getLine(), currentToken.getCol(), nodoI,nodoD, null);

        }else if(currentToken.getLexema().equals("self")){
            // Lado izquierdo de la asignacion
            ast.getProfundidad().push(new NodoLiteral(ts.getCurrentStruct().getLine(),ts.getCurrentStruct().getCol(),
                    "self",ts.getCurrentStruct().getName(),null));
            NodoLiteral nodoD= accesoSelfSimple();
            NodoLiteral nodoI = (NodoLiteral) ast.getProfundidad().pop();

            if (nodoD != null){
                nodoI = new NodoAcceso(currentToken.getLine(), currentToken.getCol(),
                        nodoI,nodoD, nodoD.getNodeType());
            }

            match("=");

            // Ahora armo el lado derecho de la asignacion
            nodoD = expresion();
            return new NodoAsignacion(currentToken.getLine(), currentToken.getCol(),
                    nodoI,nodoD, null); // Se termino de armar la asignacion
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: self o id. Se encontró " + currentToken.getLexema(),
                    "asignacion");
        }
    }

    private static NodoLiteral accesoVarSimple() {
        match("id");
        return accesoVarSimple1();
    }

    private static NodoLiteral accesoVarSimple1() {
        if (currentToken.getLexema().equals(".")){
            int line = currentToken.getLine();
            int col = currentToken.getCol();
            NodoLiteral nodoD = encadenadosSimples();
            if (nodoD != null){
                return new NodoAcceso(line, col, (NodoLiteral) ast.getProfundidad().pop(),nodoD, nodoD.getNodeType());
            }
           return (NodoLiteral) ast.getProfundidad().pop();
        }else if(currentToken.getLexema().equals("[")){
            int line = currentToken.getLine();
            int col = currentToken.getCol();
            match("[");
            NodoLiteral exp = expresion();
            match("]");

            return new NodoAccesoArray(line, col, (NodoLiteral) ast.getProfundidad().pop(),exp, null);

        } else if(currentToken.getLexema().equals("=")) {
            // lambda
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: '.', '[' o =. Se encontró " + currentToken.getLexema(),
                    "accesoVarSimple1");
        }
        return null;
    }

    private static NodoLiteral encadenadosSimples() {
        NodoLiteral nodoI = encadenadoSimple();
        ast.getProfundidad().push(nodoI);
        NodoLiteral nodoD = encadenadosSimples1();
       if(nodoD==null){
           ast.getProfundidad().pop();
           return nodoI;
       }
       ast.getProfundidad().pop();
       return new NodoAcceso(currentToken.getLine(), currentToken.getCol(), nodoI, nodoD, nodoD.getNodeType());
    }

    private static NodoLiteral encadenadosSimples1() {
        if (currentToken.getLexema().equals(".")){
            return encadenadosSimples();
        }else if(currentToken.getLexema().equals("=")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: '.' o '='. Se encontró " + currentToken.getLexema(),
                    "encadenadosSimples1");
        }
        return null;
    }

    private static NodoLiteral accesoSelfSimple() {
        match("self");
        return accesoSelfSimple1();
    }

    private static NodoLiteral accesoSelfSimple1() {
        if (currentToken.getLexema().equals(".")){
            return encadenadosSimples();
        }else if(currentToken.getLexema().equals("=")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: '.' o '='. Se encontró " + currentToken.getLexema(),
                    "accesoSelfSimple1");
        }
        return null;
    }

    private static NodoLiteral encadenadoSimple() {
        match(".");

        NodoLiteral nodo= new NodoLiteral(currentToken.getLine(), currentToken.getCol(), currentToken.getLexema(),null, null);
        match("id");
        return nodo;
    }

    private static NodoLiteral sentenciaSimple() {
        match("(");
        NodoLiteral exp = expresion();
        match(")");
        return exp;
    }

    private static NodoLiteral expresion() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        // Armamos la expresion (unaria o binaria)
        NodoLiteral nodoI = expAnd();
        NodoLiteral nodoD = expresion1();
        if(nodoD == null){ // No hay lado derecho entonces es unaria
            return nodoI; // Es unaria
        } // Si no, es binaria
        return new NodoExpBin(line, col, nodoI,"||", nodoD, "Bool");
    }

    private static NodoLiteral expresion1() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        if (currentToken.getLexema().equals("||")){
            match("||");
            // Armamos la expresion (unaria o binaria)
            NodoLiteral nodoI = expAnd();
            NodoLiteral nodoD = expresion1();
            if(nodoD == null){ // No hay lado derecho entonces es unaria
                return nodoI; // Es unaria
            } // Si no, es binaria
            return new NodoExpBin(line, col, nodoI,"||", nodoD,"Bool");

        }else if(currentToken.getLexema().equals(")")||
                currentToken.getLexema().equals(";")||
                currentToken.getLexema().equals("]")||
                currentToken.getLexema().equals(",")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: '||', ')', ';', ']' o ','. " +
                            "Se encontró " + currentToken.getLexema(),
                    "expresion1");
        }
        return null;
    }

    private static NodoLiteral expAnd() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        // Armamos la expresion (unaria o binaria)
        NodoLiteral nodoI = expIgual();
        NodoLiteral nodoD = expAnd1();
        if(nodoD == null){ // No hay lado derecho entonces es unaria
            return nodoI; // Es unaria
        } // Si no, es binaria
        return new NodoExpBin(line, col, nodoI,"&&", nodoD, "Bool");
    }

    private static NodoLiteral expAnd1() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        if (currentToken.getLexema().equals("&&")){
            match("&&");
            // Armamos la expresion (unaria o binaria)
            NodoLiteral nodoI = expIgual();
            NodoLiteral nodoD = expAnd1();
            if(nodoD == null){ // No hay lado derecho entonces es unaria
                return nodoI; // Es unaria
            } // Si no, es binaria

            return new NodoExpBin(line, col, nodoI,"&&", nodoD, "Bool");
        }else if(currentToken.getLexema().equals("||")||
                currentToken.getLexema().equals(")")||
                currentToken.getLexema().equals(";")||
                currentToken.getLexema().equals("]")||
                currentToken.getLexema().equals(",")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: '&&','||', ')', ';', ']' o ','. " +
                            "Se encontró " + currentToken.getLexema(),
                    "expAnd1");
        }
        return null;
    }

    private static NodoLiteral expIgual() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();

        NodoLiteral nodoI = expCompuesta();
        NodoLiteral nodoD = expIgual1();
        if(nodoD == null){ // No hay lado derecho entonces es unaria
            return nodoI; // Es unaria
        } // Si no, es binaria

        return new NodoExpBin(line,col,nodoI,"==",nodoD, "Bool");

    }

    private static NodoLiteral expIgual1() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        if (currentToken.getLexema().equals("==") ||
                currentToken.getLexema().equals("!=")){
            String op = currentToken.getLexema();
            opIgual();

            NodoLiteral nodoI = expCompuesta();
            NodoLiteral nodoD = expIgual1();
            if(nodoD == null){ // No hay lado derecho entonces es unaria
                return nodoI; // Es unaria
            } // Si no, es binaria

            return new NodoExpBin(line, col, nodoI,op, nodoD,"Bool");


        }else if(currentToken.getLexema().equals("||")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals(")")||
                currentToken.getLexema().equals(";")||
                currentToken.getLexema().equals("]")||
                currentToken.getLexema().equals(",")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador logico ('&&','||', '==','!='), ')', ';', ']' o ','. " +
                            "Se encontró " + currentToken.getLexema(),
                    "expIgual1");
        }
        return null;
    }

    private static NodoLiteral expCompuesta() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();

        NodoLiteral nodoI = expAd();
        String op = currentToken.getLexema();
        NodoLiteral nodoD = expCompuesta1();
        if(nodoD == null){ // No hay lado derecho entonces es unaria
            return nodoI; // Es unaria
        } // Si no, es binaria

        return new NodoExpBin(line, col, nodoI, op, nodoD,"Bool");
    }

    private static NodoLiteral expCompuesta1() {
        if (currentToken.getLexema().equals("<") ||
                currentToken.getLexema().equals(">") ||
                currentToken.getLexema().equals("<=") ||
                currentToken.getLexema().equals(">=")){
            opCompuesto();
            return expAd();
        }else if(currentToken.getLexema().equals("||")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals(")")||
                currentToken.getLexema().equals(";")||
                currentToken.getLexema().equals("]")||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("==") ||
                currentToken.getLexema().equals("!=")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador logico ('&&','||', '==', '!=', '>','|>=', '<', '<='), " +
                            "')', ';', ']' o ','. " +
                            "Se encontró " + currentToken.getLexema(),
                    "expCompuesta1");
        }
        return null;
    }

    private static NodoLiteral expAd() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        NodoLiteral nodoI = expMul();
        String op = currentToken.getLexema();
        NodoLiteral nodoD = expAd1();

        if(nodoD == null){ // No hay lado derecho entonces es unaria
            return nodoI; // Es unaria
        } // Si no, es binaria

        return new NodoExpBin(line,col,nodoI,op,nodoD,"Int");
    }

    private static NodoLiteral expAd1() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        if (currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("-")){
            opAd();
            NodoLiteral nodoI = expMul();

            String op = currentToken.getLexema();
            NodoLiteral nodoD = expAd1();
            if(nodoD == null){ // No hay lado derecho entonces es unaria
                return nodoI; // Es unaria
            } // Si no, es binaria

            return new NodoExpBin(line,col,nodoI,op,nodoD,"Int");

        }else if(currentToken.getLexema().equals("||")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals(")")||
                currentToken.getLexema().equals(";")||
                currentToken.getLexema().equals("]")||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("==") ||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("<") ||
                currentToken.getLexema().equals(">") ||
                currentToken.getLexema().equals("<=") ||
                currentToken.getLexema().equals(">=")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "operador logico ('&&','||', '==', '!=', '>','|>=', '<', '<='), " +
                            "+, -, ')', ';', ']' o ','. " +
                            "Se encontró " + currentToken.getLexema(),
                    "expAd1");
        }
        return null;
    }

    private static NodoLiteral expMul() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        NodoLiteral nodoI = expUn();
        String op= currentToken.getLexema();

        NodoLiteral nodoD = expMul1();

        if(nodoD == null){ // No hay lado derecho entonces es unaria
            return nodoI; // Es unaria
        } // Si no, es binaria

        return new NodoExpBin(line,col,nodoI,op,nodoD,"Int");
    }

    private static NodoLiteral expMul1() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        if (currentToken.getLexema().equals("*") ||
                currentToken.getLexema().equals("/")||
                currentToken.getLexema().equals("%")){
            opMul();
            NodoLiteral nodoI = expUn();
            String op = currentToken.getLexema();
            NodoLiteral nodoD = expMul1();
            if(nodoD == null){ // No hay lado derecho entonces es unaria
                return nodoI; // Es unaria
            } // Si no, es binaria
            return new NodoExpBin(line,col,nodoI,op,nodoD,"Int");

        }else if(currentToken.getLexema().equals("||")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals(")")||
                currentToken.getLexema().equals(";")||
                currentToken.getLexema().equals("]")||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("==") ||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("<") ||
                currentToken.getLexema().equals(">") ||
                currentToken.getLexema().equals("<=") ||
                currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("-")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador logico ('&&','||', '==', '!=', '>','|>=', '<', '<='), " +
                            "operador aritmetico ('+','-', '/', '*', '%'), " +
                            "')', ';', ']' o ','. Se encontró " + currentToken.getLexema(),
                    "expMul1");
        }
        return null;
    }

    private static NodoLiteral expUn() {
        //int line = currentToken.getLine();
        //int col = currentToken.getCol();
        // Caso de expresion unaria
        if ( currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("-") ||
                currentToken.getLexema().equals("!") ||
                currentToken.getLexema().equals("++") ||
                currentToken.getLexema().equals("--")){
            int line = currentToken.getLine();
            int col = currentToken.getCol();
            String op = currentToken.getLexema(); // operador de la expresion unaria
            opUnario();
            NodoLiteral exp = expUn();
            return new NodoExpUn(line, col, null ,exp, op);

        } else if(currentToken.getLexema().equals("nil") ||
                currentToken.getLexema().equals("true") ||
                currentToken.getLexema().equals("false") ||
                currentToken.getLexema().equals("self") ||
                currentToken.getLexema().equals("(") ||
                currentToken.getLexema().equals("new") ||
                currentToken.getName().equals("int") ||
                currentToken.getName().equals("str") ||
                currentToken.getName().equals("char") ||
                currentToken.getName().equals("id") ||
                currentToken.getName().equals("struct_name")){
            return operando(); // Devuelve la expresion armada con el operando

        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador unario ('++', '--', '!'), " +
                            "literales, self, id o new. Se encontró " + currentToken.getLexema(),
                    "expUn");
        }
    }

    private static void opIgual() {
        if(currentToken.getLexema().equals("==")){
            match("==");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp("==");
        } else if(currentToken.getLexema().equals("!=")){
            match("!=");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp("!=");
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: == o !=. Se encontró " + currentToken.getLexema(),
                    "opIgual");
        }
    }

    private static void opCompuesto() {
        if(currentToken.getLexema().equals("<")){
            match("<");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp("<");
        } else if(currentToken.getLexema().equals(">")){
            match(">");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp(">");
        } else if(currentToken.getLexema().equals("<=")){
            match("<=");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp("<=");
        } else if(currentToken.getLexema().equals(">=")){
            match(">=");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp(">=");
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operadores compuestos ('>','>=', '<', '<='). Se encontró " + currentToken.getLexema(),
                    "literal");
        }
    }

    private static void opAd() {
        if(currentToken.getLexema().equals("+")){
            match("+");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp("+");
        }else if(currentToken.getLexema().equals("-")){
            match("-");
            //((NodoExpBin) ast.getProfundidad().peek()).setOp("-");
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: + o -. Se encontró " + currentToken.getLexema(),
                    "opAd");
        }
    }

    private static void opUnario() {
        if(currentToken.getLexema().equals("+")){
            match("+");
        } else if(currentToken.getLexema().equals("-")){
            match("-");
        } else if(currentToken.getLexema().equals("++")){
            match("++");
        } else if(currentToken.getLexema().equals("--")){
            match("--");
        } else if(currentToken.getLexema().equals("!")){
            match("!");
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operadores unarios ('+','-', '++', '--', '!'). Se encontró " + currentToken.getLexema(),
                    "opUnario");
        }
    }

    private static void opMul() {
        if(currentToken.getLexema().equals("*")){
            match("*");
        } else if(currentToken.getLexema().equals("/")){
            match("/");
        } else if(currentToken.getLexema().equals("%")){
            match("%");
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: *, / o %. Se encontró " + currentToken.getLexema(),
                    "opMul");
        }
    }

    private static NodoLiteral operando() {
        if (currentToken.getLexema().equals("nil") ||
                currentToken.getLexema().equals("true") ||
                currentToken.getLexema().equals("false") ||
                currentToken.getName().equals("int") ||
                currentToken.getName().equals("str") ||
                currentToken.getName().equals("char")) {
            return literal(); // Devuelve la expresion armada con el literal
        } else if (currentToken.getLexema().equals("(") ||
                currentToken.getLexema().equals("self") ||
                currentToken.getLexema().equals("new") ||
                currentToken.getName().equals("id") ||
                currentToken.getName().equals("struct_name")) {
            NodoLiteral nodoI = primario();
            int line = currentToken.getLine();
            int col = currentToken.getCol();

            NodoLiteral nodoD = operando1();

            if (nodoD == null) {
                return nodoI;
            }
            return new NodoAcceso(line, col, nodoI, nodoD, nodoD.getNodeType());

        } else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: literales, self, id o new. Se encontró " + currentToken.getLexema(),
                    "expUn");
        }
    }

    private static NodoLiteral operando1() {
        if (currentToken.getLexema().equals(".")){
            return encadenado();
        }else if(currentToken.getLexema().equals("||")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals(")")||
                currentToken.getLexema().equals(";")||
                currentToken.getLexema().equals("]")||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("==") ||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("<") ||
                currentToken.getLexema().equals(">") ||
                currentToken.getLexema().equals("<=") ||
                currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("-") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("*")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador logico, operador aritmetico, ')', ';', ']', ',' o '.'. Se encontró " + currentToken.getLexema(),
                    "operando1");
        }
        return null;
    }

    private static NodoLiteral literal() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        if(currentToken.getLexema().equals("nil")){
            // Llego a un literal nulo que sera una expresion
            NodoLiteral nodo = new NodoLiteral(line,col,"literal nulo","nil","nil");
            match("nil");
            return nodo;
        } else if(currentToken.getLexema().equals("true")){
            // Llego a un literal booleano que sera una expresion
            NodoLiteral nodo = new NodoLiteral(line,col,"literal bool","Bool","true");
            match("true");
            return nodo;
        } else if(currentToken.getLexema().equals("false")){
            // Llego a un literal booleano que sera una expresion
            NodoLiteral nodo = new NodoLiteral(line,col,"literal bool","Bool","false");
            match("false");
            return nodo;
        } else if(currentToken.getName().equals("int")){
            // Llego a un literal entero que sera una expresion
            NodoLiteral nodo = new NodoLiteral(line,col,"literal entero","Int",currentToken.getLexema());
            match("int");
            return nodo;
        } else if(currentToken.getName().equals("str")){
            // Llego a un literal string que sera una expresion
            NodoLiteral nodo = new NodoLiteral(line,col,"literal str","Str",currentToken.getLexema());
            match("str");
            return nodo;
        } else if(currentToken.getName().equals("char")){
            // Llego a un literal char que sera una expresion
            NodoLiteral nodo = new NodoLiteral(line,col,"literal char","Char",currentToken.getLexema());
            match("char");
            return nodo;
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: literales, nil, true o false. Se encontró " + currentToken.getLexema(),
                    "literal");
        }
    }

    private static NodoLiteral primario() {
        if(currentToken.getLexema().equals("(")){
           return expresionParentizada();
        } else if(currentToken.getLexema().equals("self")){
            return accesoSelf();
        } else if(currentToken.getName().equals("id")) {

            int line = currentToken.getLine();
            int col = currentToken.getCol();
            String identificador = currentToken.getLexema(); // Antes de que machee
            match("id");
            flagMatch = true;
            if(currentToken.getLexema().equals("(")) {

                NodoLlamadaMetodo nodo = new NodoLlamadaMetodo(line, col,ts.getCurrentStruct().getName(),
                        ts.getCurrentStruct().getName(),identificador, null, false);
                ast.getProfundidad().push(nodo);
                return  llamadaMetodo();
            } else{
                NodoLiteral nodoI = new NodoLiteral(line, col, identificador,null,null);
                ast.getProfundidad().push(nodoI);
                NodoLiteral nodoD = accesoVar(); //1
                if(nodoD == null){
                    return (NodoLiteral) ast.getProfundidad().pop();
                }
                return nodoD;
                //return new NodoAcceso(line, col, (NodoLiteral) ast.getProfundidad().pop(), nodoD, nodoD.getNodeType());
            }
        } else if(currentToken.getName().equals("struct_name")){
            int line = currentToken.getLine();
            int col = currentToken.getCol();

            NodoLiteral nodoI = new NodoLiteral(line, col,
                    currentToken.getLexema(), currentToken.getLexema(), "st");
            ast.getProfundidad().push(nodoI);

            NodoLiteral nodoD = llamadaMetodoEstatico();

            ast.getProfundidad().pop();

            if(nodoD == null){
                return nodoI;
            }
            return new NodoAcceso(line, col, nodoI, nodoD, nodoD.getNodeType());


        } else if(currentToken.getLexema().equals("new")){
            return llamadaConstructor();

        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: id, '(', self o new. Se encontró " + currentToken.getLexema(),
                    "primario");
        }
    }

    private static NodoLiteral expresionParentizada() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        // Lado izquierdo del acceso
        match("(");
        NodoLiteral exp = expresion();
        ast.getProfundidad().push(exp);
        match(")");
        NodoExpresion nodoI = new NodoExpresion(line, col,"Expresion Parentizada", exp.getNodeType(),null, exp);
        // El tipo de una expresion parentizada es el tipo de su expresion:
        // Ejemplo: (exp) se accede a algo del tipo de su expresion dentro de los ()

        // Lado derecho del acceso
        NodoLiteral nodoD = expresionParentizada1();
        ast.getProfundidad().pop();
        if (nodoD == null){
            return nodoI;
        }
        return new NodoAcceso(line,col,nodoI,nodoD, nodoD.getNodeType());
    }

    private static NodoLiteral expresionParentizada1() {
        if (currentToken.getLexema().equals(".")){
            return encadenado();
        }else if(currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("<=")||
                currentToken.getLexema().equals(">")||
                currentToken.getLexema().equals("<")||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("==")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals("||") ||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("]") ||
                currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals(")") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("*")){
            //lamda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador aritmetico, operador logico,')' ,';' ,']' o ','. Se encontró " + currentToken.getLexema(),
                    "expresionParentizada1");
        }
        return null;
    }
    private static NodoLiteral accesoSelf() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        // Guardo a self como (nombre: self, type: A), en vez de normalmente : (nombre: a, type: A)
        NodoLiteral nodoI = new NodoLiteral(ts.getCurrentStruct().getLine(),ts.getCurrentStruct().getCol(),
                "self",ts.getCurrentStruct().getName(),null);
        ast.getProfundidad().push(nodoI);
        match("self");
        NodoLiteral nodoD = accesoSelf1();
        ast.getProfundidad().pop();
        if(nodoD == null){
            return nodoI;
        }
        return new NodoAcceso(line, col, nodoI,nodoD, nodoD.getNodeType());
    }

    private static NodoLiteral accesoSelf1() {
        if (currentToken.getLexema().equals(".")){
            return encadenado();
        } else if (currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("<=")||
                currentToken.getLexema().equals(">")||
                currentToken.getLexema().equals("<")||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("==")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals("||") ||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("]") ||
                currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals(")") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("*")){
            //lamda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador aritmetico, operador logico,')' ,';' ,']' o ','. Se encontró " + currentToken.getLexema(),
                    "AccesoSelf1");
        }
        return null;

    }
    private static NodoLiteral accesoVar() {
        match("id");
        return accesoVar1();
    }
    private static NodoLiteral accesoVar1() {
        if (currentToken.getLexema().equals(".")){
            int line = currentToken.getLine();
            int col = currentToken.getCol();
            NodoLiteral nodoD = encadenado();
            if(nodoD == null){
                return (NodoLiteral) ast.getProfundidad().pop();
            }
            return new NodoAcceso(line, col, (NodoLiteral) ast.getProfundidad().pop(),nodoD, nodoD.getNodeType());
        } else if (currentToken.getLexema().equals("[")){
            int line = currentToken.getLine();
            int col = currentToken.getCol();
            NodoLiteral nodo = (NodoLiteral) ast.getProfundidad().pop();
            match("[");
            NodoLiteral exp = expresion();
            match("]");
            ast.getProfundidad().push(new NodoAccesoArray(line, col, nodo, exp, null));
            NodoLiteral nodoD = accesoVar2(); // 1
            if(nodoD == null){
                return (NodoLiteral) ast.getProfundidad().pop();
            }
            return new NodoAcceso(line, col, (NodoLiteral) ast.getProfundidad().pop(), nodoD, null);

        }else if (currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("<=")||
                currentToken.getLexema().equals(">")||
                currentToken.getLexema().equals("<")||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("==")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals("||") ||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("]") ||
                currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals(")") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("*")){
            //lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: '*', '.' ,'/','%','+','-',')' ,';' ,']',',','||','&&','==','!=','<','>','<=', '>='. Se encontró " + currentToken.getLexema(),
                    "accesoVar1");
        }
        return null;

    }
    private static NodoLiteral accesoVar2() {
        if (currentToken.getLexema().equals(".")){
            return encadenado();
        } else if (currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("<=")||
                currentToken.getLexema().equals(">")||
                currentToken.getLexema().equals("<")||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("==")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals("||") ||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("]") ||
                currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals(")") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("*")){
            //lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: '*', '.' ,'/','%','+','-',')' ,';' ,']',',','||','&&','==','!=','<','>','<=', '>='. Se encontró " + currentToken.getLexema(),
                    "accesoVar2");
        }
        return null;
    }
    private static NodoLiteral llamadaMetodo() {
        match("id");
        argumentosActuales();

        // Termino de formarse la llamada al metodo
        NodoLlamadaMetodo nodoI = (NodoLlamadaMetodo) ast.getProfundidad().pop();

        // Ahora es el caso de que se quiera acceder a algo con lo que retorna el metodo
        // ejemplo: metodo().acceso
        // Veo que tipo retorna el metodo (para hacer un encadenado debe devolver algo de tipo idStruct)
        int line = currentToken.getLine();
        int col = currentToken.getCol();

        //ast.getProfundidad().push(new NodoLiteral(line, col, null));
        NodoLiteral nodoD = llamadaMetodo1();
        //ast.getProfundidad().pop();
        if(nodoD == null){
            return nodoI;
        }
        return new NodoAcceso(line, col, nodoI, nodoD, nodoD.getNodeType());
    }

    private static NodoLiteral llamadaMetodo1() {
        if (currentToken.getLexema().equals(".")){
            return encadenado();
        } else if (currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("<=")||
                currentToken.getLexema().equals(">")||
                currentToken.getLexema().equals("<")||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("==")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals("||") ||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("]") ||
                currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals(")") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("*")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: '*', '.' ,'/','%','+','-',')' ,';' ,']',',','||','&&','==','!=','<','>','<=', '>='. Se encontró " + currentToken.getLexema(),
                    "llamadaMetodo1");
        }
        return null;
    }
    private static NodoLiteral llamadaMetodoEstatico() {
        match("struct_name");
        match(".");

        NodoLlamadaMetodo nodo = new NodoLlamadaMetodo(currentToken.getLine(), currentToken.getCol(),
                    ast.getProfundidad().peek().getNodeType(), ast.getProfundidad().peek().getNodeType(), currentToken.getLexema()
                    ,null, true);

        ast.getProfundidad().push(nodo);

        NodoLiteral nodoI = llamadaMetodo(); // 2

        //ast.getProfundidad().pop();

        // Ahora es el caso de que se quiera acceder a algo con lo que retorna el metodo
        // ejemplo: IO.metodo().acceso
        // Veo que tipo retorna el metodo (para hacer un encadenado debe devolver algo de tipo idStruct)
        int line = currentToken.getLine();
        int col = currentToken.getCol();

        ast.getProfundidad().push(new NodoLiteral(line, col, null));
        NodoLiteral nodoD = llamadaMetodoEstatico1();
        ast.getProfundidad().pop();
        if(nodoD == null){

            return nodoI;
        }
        return new NodoAcceso(line, col, nodoI, nodoD, nodoD.getNodeType());



    }
    private static NodoLiteral llamadaMetodoEstatico1() {
        if (currentToken.getLexema().equals(".")){
            return encadenado();
        } else if (currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("<=")||
                currentToken.getLexema().equals(">")||
                currentToken.getLexema().equals("<")||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("==")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals("||") ||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("]") ||
                currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals(")") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("*")){
            //lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: '*', '.' ,'/','%','+','-',')' ,';' ,']',',','||','&&','==','!=','<','>','<=', '>='. Se encontró " + currentToken.getLexema(),
                    "llamadaMetodoEstatico1");
        }
        return null;
    }

    private static NodoLiteral llamadaConstructor() {
        match("new");
        return llamadaConstructor1();
    }
    private static NodoLiteral llamadaConstructor1() {
        if (currentToken.getName().equals("struct_name")){ // a = new Fibonacci();
            String type = currentToken.getLexema();
            ast.getProfundidad().push(new NodoLlamadaMetodo(currentToken.getLine(), currentToken.getCol(), currentToken.getLexema(),
                    currentToken.getLexema(), "constructor", currentToken.getLexema(), false));
            // El tipo de un constructor es el tipo de su struct:
            // Ejemplo: a = new Fibonacci(), se crea una instancia de tipo Fibonacci

            match("struct_name");
            argumentosActuales();

            // Termino de formarse la llamada al metodo
            NodoLlamadaMetodo nodoI = (NodoLlamadaMetodo) ast.getProfundidad().pop();

            // Ahora es el caso de que se quiera acceder a algo del struct
            // ejemplo: new A().acceso
            int line = currentToken.getLine();
            int col = currentToken.getCol();
            ast.getProfundidad().push(new NodoLiteral(line, col,type));
            NodoLiteral nodoD = llamadaConstructor2();
            ast.getProfundidad().pop();
            if(nodoD == null){
                return nodoI;
            }
            return new NodoAcceso(line, col, nodoI, nodoD, nodoD.getNodeType());

        } else if(currentToken.getLexema().equals("Str")||
                currentToken.getLexema().equals("Bool")||
                currentToken.getLexema().equals("Int")||
                currentToken.getLexema().equals("Char")){
            NodoLlamadaMetodo nodo = new NodoLlamadaMetodo(currentToken.getLine(), currentToken.getCol(),
                    currentToken.getLexema(), currentToken.getLexema(), "constructor", "Array " + currentToken.getLexema(), false);
            // El tipo de un constructor de Array es del tipo Array + (Str, Bool, Int o Char segun corrresponda):
            // Ejemplo: a = new String[6], se crea una instancia de un array de strings
            tipoPrimitivo();
            match("[");
            nodo.insertArgumento(expresion());
            match("]");
            return nodo;
        } else{
            throw new SyntactErrorException(currentToken.getLine(),
                currentToken.getCol(),
                "Se esperaba: un tipo primitivo o un id struct. Se encontró " + currentToken.getLexema(),
                "llamadaConstructor1");
        }
    }

    private static NodoLiteral llamadaConstructor2() {
        if (currentToken.getLexema().equals(".")){
            return encadenado();
        } else if (currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("<=")||
                currentToken.getLexema().equals(">")||
                currentToken.getLexema().equals("<")||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("==")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals("||") ||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("]") ||
                currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals(")") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("*")){
            //lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador aritmetico, operador logico, ')' ,';' ,']' o ','. Se encontró " + currentToken.getLexema(),
                    "llamadaConstructor2");
        }
        return null;
    }

    private static void argumentosActuales() {
        match("(");
        argumentosActuales1();

    }
    private static void argumentosActuales1() {
        if (currentToken.getLexema().equals("new")||
                currentToken.getLexema().equals("self")||
                currentToken.getLexema().equals("(")||
                currentToken.getName().equals("int") ||
                currentToken.getName().equals("str") ||
                currentToken.getName().equals("char")||
                currentToken.getName().equals("id") ||
                currentToken.getName().equals("struct_name")||
                currentToken.getLexema().equals("false")||
                currentToken.getLexema().equals("true") ||
                currentToken.getLexema().equals("nil")||
                currentToken.getLexema().equals("--") ||
                currentToken.getLexema().equals("++") ||
                currentToken.getLexema().equals("!") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+")){
            listaExpresiones();
            match(")");
        } else if (currentToken.getLexema().equals(")")){
            match(")");
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: ')' ó '+' ó '-' ó '!' ó '++' ó '--' ó 'nill' ó 'true' ó 'false' ó 'int' ó 'str' ó 'char' ó '(' ó 'self' ó id ó idstruct ó new. Se encontró " + currentToken.getLexema(),
                    "argumentosActuales1");
        }
    }

    private static void listaExpresiones() {
        int line = currentToken.getLine();
        int col = currentToken.getCol();
        // Armamos la expresion (unaria o binaria)
        NodoLiteral nodoI = expAnd();
        NodoLiteral nodoD = expresion1();
        if (nodoD == null) { // No hay lado derecho entonces es unaria
            ((NodoLlamadaMetodo) ast.getProfundidad().peek()).insertArgumento(nodoI);
        }else{
            ((NodoLlamadaMetodo) ast.getProfundidad().peek()).insertArgumento(new NodoExpBin(line, col, nodoI,"||",
                    nodoD, "Bool"));
        }
        listaExpresiones1();
    }

    private static void listaExpresiones1() {
        if (currentToken.getLexema().equals(",")){
            match(",");
            listaExpresiones();
        } else if (currentToken.getLexema().equals(")")){
            // lambda
        }else {
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: ',' ó ')' . Se encontró " + currentToken.getLexema(),
                    "listaExpresiones1");
        }
    }

    private static NodoLiteral encadenado() {
        match(".");
        return encadenado1();
    }

    private static NodoLiteral encadenado1() {
        if (currentToken.getName().equals("id")){
            int line = currentToken.getLine();
            int col = currentToken.getCol();
            String lexema = currentToken.getLexema();
            match("id");
            flagMatch = true;

            if(currentToken.getLexema().equals("(")){

                ast.getProfundidad().push(new NodoLlamadaMetodo(line, col,ast.getProfundidad().peek().getName(),
                        ast.getProfundidad().peek().getNodeType(),lexema, null , false));
                return llamadaMetodoEncadenado(); // 2

            } else {
                ast.getProfundidad().push(new NodoLiteral(line, col, lexema, null, null));
                NodoLiteral nodoD = accesoVariableEncadenado();
                if(nodoD == null){
                    return (NodoLiteral) ast.getProfundidad().pop();
                }
                return nodoD;

                /*ast.getProfundidad().pop();
                if(nodoD == null){
                    return nodoI;
                }
                return new NodoAcceso(line,col,nodoI, nodoD, nodoD.getNodeType());*/
            }
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: identificador. Se encontró " + currentToken.getLexema(),
                    "encadenado1");
        }
    }

    private static NodoLiteral llamadaMetodoEncadenado() {
        match("id");
        argumentosActuales();
        NodoLlamadaMetodo nodoI = (NodoLlamadaMetodo) ast.getProfundidad().pop();
        int line = currentToken.getLine();
        int col = currentToken.getCol();
       //ast.getProfundidad().push(new NodoLiteral(line, col, null));
        NodoLiteral nodoD = llamadaMetodoEncadenado1();
        //ast.getProfundidad().pop();
        if(nodoD == null){
            return nodoI;
        }
        return new NodoAcceso(line, col, nodoI, nodoD, nodoD.getNodeType());
    }

    private static NodoLiteral llamadaMetodoEncadenado1() {
        if (currentToken.getLexema().equals(".")){
            return encadenado();
        } else if (currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("<=")||
                currentToken.getLexema().equals(">")||
                currentToken.getLexema().equals("<")||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("==")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals("||") ||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("]") ||
                currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals(")") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("*")){
            //lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador aritmetico, operador logico, ')' ,';' ,']' o ','. Se encontró " + currentToken.getLexema(),
                    " llamadaMetodoEncadenado2");
        }
        return null;
    }
    private static NodoLiteral accesoVariableEncadenado() {
        match("id");
        return accesoVariableEncadenado1();
    }
    private static NodoLiteral accesoVariableEncadenado1() {
        if (currentToken.getLexema().equals(".")){
            int line = currentToken.getLine();
            int col = currentToken.getCol();
            NodoLiteral nodoD = encadenado();
            if(nodoD == null){
                return (NodoLiteral) ast.getProfundidad().pop();
            }
            return new NodoAcceso(line, col, (NodoLiteral) ast.getProfundidad().pop(),nodoD, nodoD.getNodeType());

        } else if(currentToken.getLexema().equals("[")){
            int line = currentToken.getLine();
            int col = currentToken.getCol();
            NodoLiteral nodo = (NodoLiteral) ast.getProfundidad().pop();
            match("[");
            NodoLiteral exp = expresion();
            match("]");
            ast.getProfundidad().push(new NodoAccesoArray(line, col, nodo, exp,null));
            NodoLiteral nodoD = accesoVariableEncadenado2();
            if(nodoD == null){
                return (NodoLiteral) ast.getProfundidad().pop();
            }
            return new NodoAcceso(line, col, (NodoLiteral) ast.getProfundidad().pop(),nodoD, nodoD.getNodeType());

        }else if (currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("<=")||
                currentToken.getLexema().equals(">")||
                currentToken.getLexema().equals("<")||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("==")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals("||") ||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("]") ||
                currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals(")") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("*")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador aritmetico, operador logico, ')' ,';' ,'[',']' o ','. Se encontró " + currentToken.getLexema(),
                    "accesoVariableEncadenado1");
        }
        return null;
    }
    private static NodoLiteral accesoVariableEncadenado2() {
        if (currentToken.getLexema().equals(".")){
            return encadenado();
        } else if (currentToken.getLexema().equals(">=")||
                currentToken.getLexema().equals("<=")||
                currentToken.getLexema().equals(">")||
                currentToken.getLexema().equals("<")||
                currentToken.getLexema().equals("!=")||
                currentToken.getLexema().equals("==")||
                currentToken.getLexema().equals("&&")||
                currentToken.getLexema().equals("||") ||
                currentToken.getLexema().equals(",")||
                currentToken.getLexema().equals("]") ||
                currentToken.getLexema().equals(";") ||
                currentToken.getLexema().equals(")") ||
                currentToken.getLexema().equals("-")||
                currentToken.getLexema().equals("+") ||
                currentToken.getLexema().equals("%") ||
                currentToken.getLexema().equals("/") ||
                currentToken.getLexema().equals("*")){
            // lambda
        }else{
            throw new SyntactErrorException(currentToken.getLine(),
                    currentToken.getCol(),
                    "Se esperaba: operador aritmetico, operador logico, ')' ,';' ,']' o ','. Se encontró " + currentToken.getLexema(),
                    "accesoVariableEncadenado2");
        }
        return null;
    }
}
