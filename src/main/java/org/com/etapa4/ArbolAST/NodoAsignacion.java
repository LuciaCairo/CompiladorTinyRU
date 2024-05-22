package org.com.etapa4.ArbolAST;

import org.com.etapa4.SemantErrorException;
import org.com.etapa4.TablaSimbolos;

// Nodo para las asignaciones (expresion = expresion)
public class NodoAsignacion extends NodoLiteral {

    private NodoLiteral nodoI;
    private NodoLiteral nodoD;


    // Constructores
    public NodoAsignacion(int line, int col, NodoLiteral nodoI, NodoLiteral nodoD, String type){
        super(line, col, type);
        this.nodoD = nodoD;
        this.nodoI = nodoI;
    }

    @Override
    public String printSentencia(String space) {
        return "\"nodo\": \"Asignacion\",\n"
                + space + "\t\"tipo\":\""+ this.getNodeType() +"\",\n"
                + space + "\t\"NodoIzq\":{\n"+ this.nodoI.printSentencia(space+"\t\t")+"\n\t"+ space +"},\n"
                + space + "\t\"NodoDer\":{\n"+ this.nodoD.printSentencia(space+"\t\t")+ space +"\n\t" + space + "}";
    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoAsignacion: nodoI = nodoD
        // Chequeo del nodoI
        this.nodoI.checkTypes(ts);
        // Chequeo del nodoD

        this.nodoD.checkTypes(ts);
        // Verificar que el nodoI y el nodoD sean del mismo tipo
        String typeNI = nodoI.getNodeType();
        String typeND = nodoD.getNodeType();
        //primero analizo el caso que el nodoD sea de tipo "nil", ya que de ser asi, si o si el nodoI debe ser de tipo Object o algunas clase definida
        if(typeND.equals("nil")){
            if (typeNI.equals("Int") ||typeNI.equals("Str")||typeNI.equals("Bool")||typeNI.equals("Char")){ //si es nill, el nodoI no tiene q ser Int/Chat/Bool/Str
                throw new SemantErrorException(this.getLine(), this.getCol(),
                        "Incompatibilidad de tipos. No se puede asignar 'nil' a una variable de tipo '"+typeNI+"'.",
                        "nodoAsignacion");
            }
        }else{ // si el nodoD no es nill

            if (!typeNI.equals(typeND)) { //si, no son del mismo tipo
                if (typeNI.equals("Object")){ //si el nodoI es de tipo object
                    String[] palabras = typeNI.split(" ");
                    String isArray = palabras[0];
                    //el nodoD debe ser si o si algo distinto a Int,Str,Bool,Char porque Object es una superclase
                    if(typeND.equals("Int") ||typeND.equals("Str")||typeND.equals("Bool")||typeND.equals("Char")|| isArray.equals("Array")){
                        throw new SemantErrorException(this.getLine(), this.getCol(),
                                "Incompatibilidad de tipos. No se puede asignar un objeto de tipo " + typeND + " a la variable definida de tipo " + typeNI,
                                "nodoAsignacion");
                    }
                }else { // si no es de tipoObject quiere decir que tengo q evaluar si NodI=NodoD
                    //Primero evaluo el caso de que exista herencia de tipos, es decir: variable v1 sea de tipo
                    // B y yo le asigne un objeto de tipo D, se podria hacer si D:C:B
                    //primero evaluo: si el nodo izq es de tipo obj de clase

                    if(!(typeNI.equals("Int")||typeNI.equals("Str")||typeNI.equals("Bool")||typeNI.equals("Char") || typeNI.split(" ")[0].equals("Array"))
                            && !(typeND.equals("Int")||typeND.equals("Str")||typeND.equals("Bool")||typeND.equals("Char") || typeND.split(" ")[0].equals("Array"))){
                        String hD=typeND;
                        String hI=typeNI;

                        if (hD.equals("Object") && !(hI.equals("Object"))) {
                            throw new SemantErrorException(this.getLine(), this.getCol(),
                                    "Incompatibilidad de tipos. No se puede asignar un objeto de tipo '" + hI + "' cuando el lado izquierdo de la asignacion es de tipo '"+hD+"' \n " +
                                            "debido a que no se encuentra en su arbol de herencia.",
                                    "nodoAsignacion");
                        }
                        // realizo un while para poder recorrer la herencia hacia arriba, salgo del while cuando hD
                        //sea igual al tipo q estoy buscando
                        while((!(ts.getTableStructs().get(hD).getHerencia().equals(hI)))  ){

                            hD = ts.getTableStructs().get(hD).getHerencia();
                            if(hD.equals("Object")){
                                break;
                            }
                        }
                        if(hD.equals("Object") && hD != hI){
                            throw new SemantErrorException(this.getLine(), this.getCol(),
                                    "Incompatibilidad de tipos. No se puede asignar un objeto de tipo " + typeND + " a la variable definida de tipo " + typeNI+"."+
                                            "\n"+"Dentro de la variable, solo pueden guardarse objetos de clases, que hereden o sean del tipo '"+typeNI+"'.Es decir '"+typeND+ "' debe heredar de '"+typeNI+"'.",
                                    "nodoAsignacion");
                        }

                    }else{ //caso q no sean iguales, ni tampoco hereden.
                        throw new SemantErrorException(this.getLine(), this.getCol(),
                                "Incompatibilidad de tipos. No se puede asignar un objeto de tipo " + typeND + " a la variable definida de tipo " + typeNI,
                                "nodoAsignacion");
                    }


                }
            }
        }

        // Setear el tipo
        this.setNodeType(typeNI);
        return true;
    }

}

