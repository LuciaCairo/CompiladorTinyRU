package org.com.etapa5.ArbolAST;

import org.com.etapa5.TablaDeSimbolos.TablaSimbolos;
import org.com.etapa5.Exceptions.SemantErrorException;

// Este es el nodo con la estructura general del que heredan todos los nodos
public class NodoExpresion extends NodoLiteral {

    private NodoLiteral exp;

    // Constructor
    public NodoExpresion(int line, int col, String name, String type, String value, NodoLiteral exp){
        super(line, col,name,type, value);
        this.exp = exp;
    }

    // Setters
    public void setExp(NodoLiteral exp) {
        this.exp = exp;
    }


    // Functions
    public String printSentencia(String space) {
        return "\"nodo\": \"" + this.getName() + "\",\n"
                + space + "\"tipo\":\"" + this.getNodeType() + "\",\n"
                + space + "\"expresion\": {\n" + this.exp.printSentencia(space + "\t") + "\n" + space + "}\n";


    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoExpresion: exp
        if(this.getName().equals("Retorno")){
            if(!ts.getCurrentStruct().getName().equals("start")){
                ts.getCurrentMetod().setHasRet(true);
            }

            // Si el retorno es void.
            if (exp == null){
                // Verifico que el metodo, me devuelva void
                if(this.getNodeType().equals("void")){
                    // Creo un nodo exp, para poder pasarlo al ret.
                    this.setExp(new NodoLiteral(this.getLine(),this.getCol(),"Ret;","void",";"));
                    // Setear el tipo correspondiente
                    this.setNodeType(exp.getNodeType());

                }else{
                    throw new SemantErrorException(this.getLine(), this.getCol(),
                            "El retorno del metodo no puede ser \"void\" porque en su firma esta declarado como "+ this.getNodeType(),
                            "sentencia");
                }

            }else { // Verifico q el ret sea igual al de la firma del metodo o tambien puede ser nil

                exp.checkTypes(ts); // Chequeo la expresion
                if(!(this.getNodeType().equals(exp.getNodeType())) && !(this.exp.getNodeType().equals("nil"))){

                    String[] palabras = exp.getNodeType().split(" ");
                    String isArray = palabras[0];
                    if(!(exp.getNodeType().equals("Int")||
                            exp.getNodeType().equals("Str")||
                            exp.getNodeType().equals("Char")||
                            exp.getNodeType().equals("Bool")||
                            isArray.equals(("Array")))) {
                        String h = exp.getNodeType();
                        if (h.equals("Object")) {
                            throw new SemantErrorException(this.getLine(), this.getCol(),
                                    "Incompatibilidad de tipos. No se puede retornar un objeto de tipo " + exp.getNodeType() + " cuando la firma del metodo define que retorna un objeto de tipo '"+this.getNodeType()+"' \n " +
                                            "debido a que no se encuentra en su arbol de herencia.",
                                    "nodoAsignacion");
                        }
                        while (!(ts.getTableStructs().get(h).getHerencia().equals(this.getNodeType()))) {
                            h = ts.getTableStructs().get(h).getHerencia();
                            if (h.equals("Object")) {
                                break;
                            }
                        }

                        if (h.equals("Object") && h != this.getNodeType()) {
                            throw new SemantErrorException(this.getLine(), this.getCol(),
                                    "Incompatibilidad de tipos. No se puede retornar un objeto de tipo " + exp.getNodeType() + " cuando la firma del metodo define que retorna un objeto de tipo '"+this.getNodeType()+"'.",
                                    "nodoAsignacion");
                        }
                    }else {
                        throw new SemantErrorException(this.getLine(), this.getCol(),
                                "El retorno del metodo no puede ser '" + exp.getNodeType() + "' porque en su firma esta declarado como '" + this.getNodeType() + "'",
                                "sentencia");
                    }
                } else {
                    if(this.exp.getNodeType().equals("nil") && this.getNodeType().equals("void")){
                        throw new SemantErrorException(this.getLine(), this.getCol(),
                                "El retorno del metodo no puede ser \"nil\" porque en su firma esta declarado como void",
                                "sentencia");
                    }
                    // Setear el tipo correspondiente
                    this.setNodeType(exp.getNodeType());
                }
            }
        } else {
            exp.checkTypes(ts); // Chequeo la expresion
            this.setNodeType(exp.getNodeType());
        }
        return true;
    }

    // Funcion para generar el codigo en MIPS de una asignacion
    public String generateNodeCode(TablaSimbolos ts){
        // Esta funcion es diferente para cada nodo
        return this.exp.generateNodeCode(ts);
    }
}
