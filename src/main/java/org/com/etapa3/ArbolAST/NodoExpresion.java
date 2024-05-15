package org.com.etapa3.ArbolAST;

import org.com.etapa3.TablaSimbolos;
import org.com.etapa3.SemantErrorException;

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
        if (this.getName().equals("Retorno") && this.exp.getNodeType().equals("void")) {
            return "";

        } else {
            return "\"nodo\": \"" + this.getName() + "\",\n"
                    + space + "\"tipo\":\"" + this.getNodeType() + "\",\n"
                    + space + "\"expresion\": {\n" + this.exp.printSentencia(space + "\t") + "\n" + space + "},\n";

        }
    }

    @Override
    public boolean checkTypes(TablaSimbolos ts){
        // NodoExpresion: exp

        if(this.getName().equals("Retorno")){

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

            }else { // Verifico q el ret sea igual al de la firma del metodo

                exp.checkTypes(ts); // Chequeo la expresion
                if(!(this.getNodeType().equals(exp.getNodeType()))){
                    throw new SemantErrorException(this.getLine(), this.getCol(),
                            "El retorno del metodo no puede ser '" + exp.getNodeType() + "' porque en su firma esta declarado como '"+ this.getNodeType() +"'",
                            "sentencia");
                } else {
                    // Setear el tipo correspondiente
                    this.setNodeType(exp.getNodeType());
                }
            }
        }
        return true;
    }
}
