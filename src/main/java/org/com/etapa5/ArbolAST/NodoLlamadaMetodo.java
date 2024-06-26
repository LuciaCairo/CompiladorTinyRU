package org.com.etapa5.ArbolAST;

import org.com.etapa5.CodeGenerator;
import org.com.etapa5.TablaDeSimbolos.EntradaParametro;
import org.com.etapa5.TablaDeSimbolos.EntradaStruct;
import org.com.etapa5.TablaDeSimbolos.EntradaStructPredef;
import org.com.etapa5.Exceptions.SemantErrorException;
import org.com.etapa5.TablaDeSimbolos.TablaSimbolos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class NodoLlamadaMetodo extends NodoLiteral{
    private String typeStruct;
    private String nameStruct;
    private String metodo;

    private boolean isStatic = false;
    private LinkedList<NodoLiteral> argumentos;


    public NodoLlamadaMetodo(int line, int col, String nameStruct,String typeStruct, String metodo, String type, boolean isStatic){
        super(line, col, type);
        this.nameStruct = nameStruct;
        this.typeStruct = typeStruct;
        this.metodo = metodo;
        this.argumentos = new LinkedList<>();
        this.isStatic = isStatic;
    }

    // Getters

    public String getNameStruct(){
        return nameStruct;
    }
    public String getTypeStruct() {
        return typeStruct;
    }
    public String getMetodo() {
        return metodo;
    }
    public boolean getisStatic() {
        return isStatic;
    }

    // Functions
    public void insertArgumento(NodoLiteral argumento) {
        this.argumentos.add(argumento);
    }

    @Override
    public String printSentencia(String space) {
        String json = space + "\"nodo\": \"Llamada Metodo\",\n"
                + space + "\"tipo\":\""+ this.getNodeType() +"\",\n"
                + space + "\"metodo\": \""+ this.metodo +"\"," + space +"\n";
        if (!this.argumentos.isEmpty() && !(this.argumentos.getFirst() == null) ) {
            json +=  space + "\"argumentos\":[\n";
            for (int i = 0; i < this.argumentos.size(); i++) {
                json += space + "{\n" + this.argumentos.get(i).printSentencia(space + "\t") + "\n"+ space + "},\n";
            }
            json = json.substring(0, json.length() - 2) + "]";
        } else {
            json +=  space + "\"argumentos\":[]\n";
        }
        return json;
    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoLlamadaMetodo: metodo(lista expresiones)
        // Verificar que el metodo exista en su struct padre en la ts

        if(metodo.equals("constructor")){

            EntradaStruct StructConstructor = (ts.getStruct(this.getTypeStruct()));

            //evaluo si el tipo de la instacia del objeto existe como struct
            if(StructConstructor == null){
                //si no existe, busco en los struct pred
                EntradaStructPredef  StructPredConstructor =  (ts.getStructPred(this.getTypeStruct()));
                if (StructPredConstructor== null){
                    throw new SemantErrorException(this.getLine(), this.getCol(),
                            "No se puede crear una instancia de '"+this.getTypeStruct()+"' porque no existe el struct. Primero"+
                                    " debe crear el struct '"+this.getTypeStruct()+"'.",
                            "sentencia");
                }else{ //constructor Array
                    String[] palabras = this.getNodeType().split(" ");
                    String isArray = palabras[0];
                    if (isArray.equals("Array")) {

                        //evaluo la expresion
                        argumentos.get(0).checkTypes(ts);
                        //evaluo si parametro del array es int , ya que solo puede ser int new Int[1]
                        if (!(argumentos.get(0).getNodeType().equals("Int"))) {
                            throw new SemantErrorException(this.getLine(), this.getCol(),
                                    "Incompatibilidad de tipos. No se puede definir el tamaño de un array como '" + argumentos.get(0).getName() +
                                            "'.El tipo debe ser 'Int'",
                                    "sentencia");
                        }
                    }else{

                        if(!(argumentos.isEmpty())){
                            throw new SemantErrorException(this.getLine(), this.getCol(),
                                    "Instancia de 'Objeto' mal definida. El constructor de la clase predefinida 'Objeto' no debe recibir parametros.",
                                    "sentencia");
                        }
                    }
                    return true;
                }


            }else{ // si existe el constructor en los Structs
                if (!(argumentos.size() == StructConstructor.getMetodos().get(metodo).getParametros().size())) {
                    throw new SemantErrorException(this.getLine(), this.getCol(),
                            "Cantidad de argumentos incorrectos en la llamada a metodo '" + metodo + "'. Para llamarlo debe pasar la cantidad \n de" +
                                    " parametros corresponientes indicados en su firma.Se esperaban "+StructConstructor.getMetodos().get(metodo).getParametros().size()+
                            " parametros y llegaron "+argumentos.size()+".",
                            "sentencia");
                }
                List<EntradaParametro> parametrosOrdenadosM1 = new ArrayList<>(StructConstructor.getMetodos().get("constructor").getParametros().values());
                parametrosOrdenadosM1.sort(Comparator.comparingInt(EntradaParametro::getPos));
                int i = 0;

                //recorro los argumentos de la llamada y chequeo
                for (NodoLiteral argumento : argumentos) {

                    //me traigo el tipo del argumento
                    argumento.checkTypes(ts);
                    //comparo si el tipo del argumento de la llamada es igual al tipo del argumento de la tabla
                    if (!(argumento.getNodeType().equals(parametrosOrdenadosM1.get(i).getType()))) {
                        String[] palabras = argumento.getNodeType().split(" ");
                        String isArray = palabras[0];

                        if(!(argumento.getNodeType().equals("Int")||
                                argumento.getNodeType().equals("Str")||
                                argumento.getNodeType().equals("Char")||
                                argumento.getNodeType().equals("Bool")||
                                isArray.equals(("Array")))){
                            String h = argumento.getNodeType();
                            if (h.equals("Object")) {
                                throw new SemantErrorException(this.getLine(), this.getCol(),
                                        "Incompatibilidad de tipos. No se puede llamar a un metodo, pasando un parametro de tipo " + argumento.getNodeType() + " cuando la firma del metodo expecifica que espera un tipo '"+parametrosOrdenadosM1.get(i).getType()+"' \n " +
                                                "debido a que no se encuentra en su arbol de herencia.",
                                        "nodoAsignacion");
                            }
                            while (!(ts.getTableStructs().get(h).getHerencia().equals(parametrosOrdenadosM1.get(i).getType()))) {
                                h = ts.getTableStructs().get(h).getHerencia();
                                if (h.equals("Object")) {
                                    break;
                                }
                            }
                            if (h.equals("Object") && h != parametrosOrdenadosM1.get(i).getType()) {
                                throw new SemantErrorException(this.getLine(), this.getCol(),
                                        "Llamada a constructor del struct '"+this.getTypeStruct()+"' incorrecta. El constructor espera un '"+
                                                parametrosOrdenadosM1.get(i).getType()+"' y recibe el parametro '"+
                                                argumento.getName()+"' de tipo '"+argumento.getNodeType()+"'.",
                                        "sentencia");
                            }


                        }else{
                            throw new SemantErrorException(this.getLine(), this.getCol(),
                                    "Llamada a constructor del struct '"+this.getTypeStruct()+"' incorrecta. El constructor espera un '"+
                                            parametrosOrdenadosM1.get(i).getType()+"' y recibe el parametro '"+
                                            argumento.getName()+"' de tipo '"+argumento.getNodeType()+"'.",
                                    "sentencia");
                        }

                    }
                    i++;

                }

            }

        }else { // si no es constructor
            if(this.getParent().isEmpty()) {

                //Verificar que el metodo este definido en el struct padre
                if (!(ts.getCurrentStruct().getMetodos().containsKey(metodo))) {
                    throw new SemantErrorException(this.getLine(), this.getCol(),
                            "No se puede llamar a un metodo que no existe. Debe definir el metodo '" + metodo + "' en el struct '"
                                    + ts.getCurrentStruct().getName() + "' o heredarlo.",
                            "sentencia");
                }

                //verificar que la cantidad de argumentos en la llamada sea la misma de la firma del metodo

                if (!(argumentos.size() == ts.getCurrentStruct().getMetodos().get(metodo).getParametros().size())) {
                    throw new SemantErrorException(this.getLine(), this.getCol(),
                            "Cantidad de argumentos incorrectos en el metodo '" + metodo + "'. Para llamarlo debe pasar la cantidad de" +
                                    " parametros corresponientes indicados en su firma '",
                            "sentencia");
                }
                //Ordeno los parametros del metodo que estan en la ts

                List<EntradaParametro> parametrosOrdenadosM1 = new ArrayList<>(ts.getCurrentStruct().getMetodos().get(metodo).getParametros().values());
                parametrosOrdenadosM1.sort(Comparator.comparingInt(EntradaParametro::getPos));
                int i = 0;

                //recorro los argumentos de la llamada y chequeo
                for (NodoLiteral argumento : argumentos) {

                    //me traigo el tipo del argumento
                    argumento.checkTypes(ts);

                    //comparo si el tipo del argumento de la llamada es igual al tipo del argumento de la tabla
                    if (!(argumento.getNodeType().equals(parametrosOrdenadosM1.get(i).getType()))) {
                        String[] palabras = argumento.getNodeType().split(" ");
                        String isArray = palabras[0];
                        //si no es igual, tengo q tener en cuenta la herencia de clases
                        if (!(argumento.getNodeType().equals("Int") ||
                                argumento.getNodeType().equals("Str") ||
                                argumento.getNodeType().equals("Char") ||
                                argumento.getNodeType().equals("Bool"))||
                                isArray.equals("Array")) {
                            String h = argumento.getNodeType();
                            if (h.equals("Object")) {
                                throw new SemantErrorException(this.getLine(), this.getCol(),
                                        "Incompatibilidad de tipos. No se puede llamar a un metodo, pasando un parametro de tipo " + argumento.getNodeType() + " cuando la firma del metodo expecifica que espera un tipo '"+parametrosOrdenadosM1.get(i).getType()+"' \n " +
                                                "debido a que no se encuentra en su arbol de herencia.",
                                        "nodoAsignacion");
                            }
                            while (!(ts.getTableStructs().get(h).getHerencia().equals(parametrosOrdenadosM1.get(i).getType()))) {
                                h = ts.getTableStructs().get(h).getHerencia();
                                if (h.equals("Object")) {
                                    break;
                                }
                            }
                            if (h.equals("Object") && h != parametrosOrdenadosM1.get(i).getType()) {
                                throw new SemantErrorException(this.getLine(), this.getCol(),
                                        "El tipo del parametro '" + argumento.getName() + "' no coincide con el tipo del parametro en la firma del metodo '" + metodo + "'.",
                                        "sentencia");
                            }
                        } else {
                            throw new SemantErrorException(this.getLine(), this.getCol(),
                                    "El tipo del parametro '" + argumento.getName() + "' no coincide con el tipo del parametro en la firma del metodo '" + metodo + "'.",
                                    "sentencia");
                        }
                    }
                    i++;

                }
                //si pasa todas las validaciones seteo el tipo (?ESTA BIEN LUU... solo se hace para metodos q no sean constructor?
                this.setNodeType(ts.getCurrentStruct().getMetodos().get(metodo).getRet());

            } else { // CASO ESPECIAL DE QUE VENGA DE UN ACCESO
                if(ts.getStructsPred().get(this.getParent()) != null){ // Acceso desde un struct predefinido
                    //Verificar que el metodo este definido en en padre
                    if (!((ts.getStructsPred().get(this.getParent()).getMetodos().containsKey(metodo)))) {
                        throw new SemantErrorException(this.getLine(), this.getCol(),
                                "No se puede llamar a un metodo que no existe. Debe definir el metodo '" + metodo + "' en el struct '"
                                        + (ts.getStructsPred().get(this.getParent()).getName()) + "' o heredarlo.",
                                "sentencia");
                    }
                    //verificar que la cantidad de argumentos en la llamada sea la misma de la firma del metodo

                    if (!(argumentos.size() == (ts.getStructsPred().get(this.getParent()).getMetodos().get(metodo).getParametros().size()))) {
                        throw new SemantErrorException(this.getLine(), this.getCol(),
                                "Cantidad de argumentos incorrectos en el metodo '" + metodo + "'. Para llamarlo debe pasar la cantidad de" +
                                        " parametros corresponientes indicados en su firma '",
                                "sentencia");
                    }
                    //Ordeno los parametros del metodo que estan en la ts

                    List<EntradaParametro> parametrosOrdenadosM1 = new ArrayList<>((ts.getStructsPred().get(this.getParent()).getMetodos().get(metodo).getParametros().values()));
                    parametrosOrdenadosM1.sort(Comparator.comparingInt(EntradaParametro::getPos));
                    int i = 0;

                    //recorro los argumentos de la llamada y chequeo
                    for (NodoLiteral argumento : argumentos) {
                        //me traigo el tipo del argumento
                        argumento.checkTypes(ts);
                        String[] palabras = argumento.getNodeType().split(" ");
                        String isArray = palabras[0];

                        //comparo si el tipo del argumento de la llamada es igual al tipo del argumento de la tabla
                        if (!(argumento.getNodeType().equals(parametrosOrdenadosM1.get(i).getType()))) {
                            //si no es igual, tengo q tener en cuenta la herencia de clases
                            if (!(argumento.getNodeType().equals("Int") ||
                                    argumento.getNodeType().equals("Str") ||
                                    argumento.getNodeType().equals("Char") ||
                                    argumento.getNodeType().equals("Bool")||
                                    isArray.equals("Array"))) {
                                String h = argumento.getNodeType();
                                if (h.equals("Object")) {
                                    throw new SemantErrorException(this.getLine(), this.getCol(),
                                            "Incompatibilidad de tipos. No se puede llamar a un metodo, pasando un parametro de tipo " + argumento.getNodeType() + " cuando la firma del metodo expecifica que espera un tipo '"+parametrosOrdenadosM1.get(i).getType()+"' \n " +
                                                    "debido a que no se encuentra en su arbol de herencia.",
                                            "nodoAsignacion");
                                }
                                while (!(ts.getTableStructs().get(h).getHerencia().equals(parametrosOrdenadosM1.get(i).getType()))) {
                                    h = ts.getTableStructs().get(h).getHerencia();
                                    if (h.equals("Object")) {
                                        break;
                                    }
                                }
                                if (h.equals("Object") && h != parametrosOrdenadosM1.get(i).getType()) {
                                    throw new SemantErrorException(this.getLine(), this.getCol(),
                                            "El tipo del parametro '" + argumento.getName() + "' no coincide con el tipo del parametro en la firma del metodo '" + metodo + "'.",
                                            "sentencia");
                                }
                            } else {
                                throw new SemantErrorException(this.getLine(), this.getCol(),
                                        "El tipo del parametro '" + argumento.getName() + "' no coincide con el tipo del parametro en la firma del metodo '" + metodo + "'.",
                                        "sentencia");
                            }
                        }
                        i++;

                    }
                    //si pasa todas las validaciones seteo el tipo (?ESTA BIEN LUU... solo se hace para metodos q no sean constructor?
                    this.setNodeType(ts.getStructsPred().get(this.getParent()).getMetodos().get(metodo).getRet());

                } else if(ts.getTableStructs().get(this.getParent()) != null){ // acceso desde un struct creado
                    //Verificar que el metodo este definido en el padre
                    if (!((ts.getTableStructs().get(this.getParent()).getMetodos().containsKey(metodo)))) {
                        throw new SemantErrorException(this.getLine(), this.getCol(),
                                "No se puede llamar a un metodo que no existe. Debe definir el metodo '" + metodo + "' en el struct '"
                                        + (ts.getTableStructs().get(this.getParent()).getName()) + "' o heredarlo.",
                                "sentencia");
                    }

                    // Caso especial de que venga de un acceso de llamada de metodo estatico
                        if(this.getisStatic()){
                            // Hay que verificar que el metodo sea st
                            if (!((ts.getTableStructs().get(this.getParent()).getMetodos().get(metodo)).getSt())) {
                                throw new SemantErrorException(this.getLine(), this.getCol(),
                                        "No se puede realizar la llamada al metodo ya que este no es st. Debe definir el metodo '" + metodo + "' en el struct '"
                                                + (ts.getTableStructs().get(this.getParent()).getName()) + "' como estatico.",
                                        "sentencia");
                            }
                        }


                    //verificar que la cantidad de argumentos en la llamada sea la misma de la firma del metodo

                    if (!(argumentos.size() == (ts.getTableStructs().get(this.getParent()).getMetodos().get(metodo).getParametros().size()))) {
                        throw new SemantErrorException(this.getLine(), this.getCol(),
                                "Cantidad de argumentos incorrectos en el metodo '" + metodo + "'. Para llamarlo debe pasar la cantidad de" +
                                        " parametros corresponientes indicados en su firma '",
                                "sentencia");
                    }
                    //Ordeno los parametros del metodo que estan en la ts

                    List<EntradaParametro> parametrosOrdenadosM1 = new ArrayList<>((ts.getTableStructs().get(this.getParent()).getMetodos().get(metodo).getParametros().values()));
                    parametrosOrdenadosM1.sort(Comparator.comparingInt(EntradaParametro::getPos));
                    int i = 0;

                    //recorro los argumentos de la llamada y chequeo
                    for (NodoLiteral argumento : argumentos) {
                        //me traigo el tipo del argumento
                        argumento.checkTypes(ts);

                        String[] palabras = argumento.getNodeType().split(" ");
                        String isArray = palabras[0];

                        //comparo si el tipo del argumento de la llamada es igual al tipo del argumento de la tabla
                        if (!(argumento.getNodeType().equals(parametrosOrdenadosM1.get(i).getType()))) {
                            //si no es igual, tengo q tener en cuenta la herencia de clases
                            if (!(argumento.getNodeType().equals("Int") ||
                                    argumento.getNodeType().equals("Str") ||
                                    argumento.getNodeType().equals("Char") ||
                                    argumento.getNodeType().equals("Bool") ||
                                    isArray.equals("Array"))) {
                                String h = argumento.getNodeType();
                                if (h.equals("Object")) {
                                    throw new SemantErrorException(this.getLine(), this.getCol(),
                                            "Incompatibilidad de tipos. No se puede llamar a un metodo, pasando un parametro de tipo " + argumento.getNodeType() + " cuando la firma del metodo expecifica que espera un tipo '"+parametrosOrdenadosM1.get(i).getType()+"' \n " +
                                                    "debido a que no se encuentra en su arbol de herencia.",
                                            "nodoAsignacion");
                                }
                                while (!(ts.getTableStructs().get(h).getHerencia().equals(parametrosOrdenadosM1.get(i).getType()))) {
                                    h = ts.getTableStructs().get(h).getHerencia();
                                    if (h.equals("Object")) {
                                        break;
                                    }
                                }
                                if (h.equals("Object") && h != parametrosOrdenadosM1.get(i).getType()) {
                                    throw new SemantErrorException(this.getLine(), this.getCol(),
                                            "El tipo del parametro '" + argumento.getName() + "' no coincide con el tipo del parametro en la firma del metodo '" + metodo + "'.",
                                            "sentencia");
                                }
                            } else {
                                throw new SemantErrorException(this.getLine(), this.getCol(),
                                        "El tipo del parametro '" + argumento.getName() + "' no coincide con el tipo del parametro en la firma del metodo '" + metodo + "'.",
                                        "sentencia");
                            }
                        }
                        i++;

                    }
                    //si pasa todas las validaciones seteo el tipo (?ESTA BIEN LUU... solo se hace para metodos q no sean constructor?
                    this.setNodeType(ts.getTableStructs().get(this.getParent()).getMetodos().get(metodo).getRet());
                }
            }
        }

        return true;
    }

    // Funcion para generar el codigo en MIPS de una asignacion
    public String generateNodeCode(TablaSimbolos ts) {
        StringBuilder code = new StringBuilder();
        code.append("\n\t# NODO LLAMADA METODO \n");
        code.append("\t# Preparo para realizar la llamada al metodo "+this.metodo+"\n");
        if(metodo.equals("constructor")){ // NEW
            if(this.argumentos != null){
                code.append("\taddi $sp, $sp, -" + (this.argumentos.size()*4) + "\n");
                int pos = 0;
                for (NodoLiteral a : argumentos) {

                    code.append(a.generateNodeCode(ts));
                    code.append("\tsw $t" + CodeGenerator.registerCounter +"," + pos +"($sp)\n");
                    pos += 4;
                }
            }
            code.append("\tjal " + this.getNodeType() + "_constructor\n");

            code.append("\taddi $sp, $sp, " + (this.argumentos.size()*4) + "# Liberar el espacio de parámetros\n");
        } else {
            int cont = 0;
            if(this.argumentos != null){

                code.append("\taddi $sp, $sp, -" + (this.argumentos.size()*4) + "\n");
                int pos =  0;
                for (NodoLiteral a : argumentos) {


                    if (ts.getStructsPred().containsKey(this.nameStruct)) {
                        code.append(a.generateNodeCode(ts));

                        code.append("\tmove $a0, $t" + CodeGenerator.registerCounter + "\n");

                    } else {
                        code.append(a.generateNodeCode(ts));
                        // Voy a ver si lo que quiero asignar es una variable

                        code.append("\tsw $t" + CodeGenerator.registerCounter + "," + pos + "($sp)\n");

                    }
                    pos += 4;
                }
            }

            if(ts.getStructsPred().get(this.getParent()) != null) { // Acceso desde un struct predefinido

                code.append("\tjal " + this.getParent() +"_" + this.metodo + " # Llamar al método\n");
                code.append("\tmove $a0, $t" + CodeGenerator.getNextRegister()+ "\n");
                return code.toString();

            } else {

                if (ts.getCurrentStruct().getName().equals("start")){
                    code.append("\tjalr $s0 # Llamar al método\n");

                    if(ts.getStruct(this.getParent()).getMetodo(metodo).getRet() != null){
                        code.append( "\tmove $t"+CodeGenerator.getNextRegister()+", $v0  # Guardamos la dirección de la instancia\n");
                    }
                    code.append("\taddi $sp, $sp, " + (this.argumentos.size()*4) + "# Liberar el espacio de parámetros\n");

                }else{

                    int posMet=ts.getCurrentStruct().getMetodos().get(this.metodo).getPos()*4;

                    int posVtable=CodeGenerator.getNextRegister();

                    //siempre lo busco en s2, pq es donde voy a tener siempre la instancia guardada antes de cambiar de puntero
                    code.append("\tlw $t"+posVtable+", 0($s1) #cargo en un registro temporal la direccion a la vtable para recuperar el puntero del metodo " +this.metodo+"\n"
                            +"\tlw $s0, "+posMet+"($t"+posVtable+") #cargar en un registro temporal la direccion al metodo que quiero llamar\n");
                    code.append( "\tmove $s6"  + ", $s1   # Guardamos la dirección de la instancia\n");
                    code.append("\tjalr $s0 # Llamar al método\n");
                    code.append("\taddi $sp, $sp, " + (this.argumentos.size()*4) + "# Liberar el espacio de parámetros\n"+
                    "\tmove $s1"  + ", $s6   # Guardamos la dirección de la memoria reservadaa line\n");


                }
                //code.append("\tmove $a" + cont + ", $s0 # Pasar la instancia\n");
                //code.append("\tlw $t" + CodeGenerator.getNextRegister() + ", 0($s0) # Cargar la dirección de la vtable\n");
                //int reg = CodeGenerator.registerCounter-1;
                //code.append("\tlw $t" + CodeGenerator.getNextRegister() + ", " + (ts.getStruct(this.getParent()).getMetodo(this.getMetodo()).getPos()- 1 ) * 4 +
                //        "($t" + reg + ") # Cargar la dirección del método\n");



            }

        }

        return code.toString();
    }

}
