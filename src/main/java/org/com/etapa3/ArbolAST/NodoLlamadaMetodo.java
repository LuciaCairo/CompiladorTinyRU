package org.com.etapa3.ArbolAST;

import org.com.etapa3.ClasesSemantico.EntradaMetodo;
import org.com.etapa3.ClasesSemantico.EntradaParametro;
import org.com.etapa3.ClasesSemantico.EntradaStruct;
import org.com.etapa3.SemantErrorException;
import org.com.etapa3.TablaSimbolos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class NodoLlamadaMetodo extends NodoLiteral{
    private String typeStruct;
    private String nameStruct;
    private String metodo;
    private String ret= "void";
    private LinkedList<NodoLiteral> argumentos;


    public NodoLlamadaMetodo(int line, int col, String nameStruct,String typeStruct, String metodo, String type){
        super(line, col, type);
        this.nameStruct = nameStruct;
        this.typeStruct = typeStruct;
        this.metodo = metodo;
        this.argumentos = new LinkedList<>();
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
    // Setters

    // Functions
    public void insertArgumento(NodoLiteral argumento) {
        this.argumentos.add(argumento);
    }

    @Override
    public String printSentencia(String space) {
        String json = space + "\"nodo\": \"Llamada Metodo\",\n"
                + space + "\"metodo\": \""+ this.metodo +"\"," + space +"\n";
        if (!this.argumentos.isEmpty() && !(this.argumentos.getFirst() == null) ) {
            json +=  space + "\"argumentos\":[\n";
            for (int i = 0; i < this.argumentos.size(); i++) {
                json += space + "{\n" + this.argumentos.get(i).printSentencia(space + "\t") + space + "},\n";
            }
            json = json.substring(0, json.length() - 2);
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
            //evaluo si el tipo de la instacia del objeto existe como struct
            EntradaStruct StructConstructor = (ts.getStruct(this.getTypeStruct()));
            if(!(ts.getStruct(this.getTypeStruct()) != null)){
                throw new SemantErrorException(this.getLine(), this.getCol(),
                        "No se puede crear una instancia de '"+this.getTypeStruct()+"' porque no existe el struct.Primero"+
                        " debe crear el struct '"+this.getTypeStruct()+"'.",
                        "sentencia");

            }else{

                List<EntradaParametro> parametrosOrdenadosM1 = new ArrayList<>(StructConstructor.getMetodos().get("constructor").getParametros().values());
                parametrosOrdenadosM1.sort(Comparator.comparingInt(EntradaParametro::getPos));
                int i = 0;

                //recorro los argumentos de la llamada y chequeo
                for (NodoLiteral argumento : argumentos) {
                    //me traigo el tipo del argumento
                    argumento.checkTypes(ts);
                    //comparo si el tipo del argumento de la llamada es igual al tipo del argumento de la tabla
                    if (!(argumento.getNodeType().equals(parametrosOrdenadosM1.get(i).getType()))) {
                        throw new SemantErrorException(this.getLine(), this.getCol(),
                                "Llamada a constructor del struct '"+this.getTypeStruct()+"' incorrecta. El constructor espera un '"+
                                        parametrosOrdenadosM1.get(i).getType()+"' y recibe el parametro '"+
                                        argumento.getName()+"' de tipo '"+argumento.getNodeType()+"'.",
                                "sentencia");
                    }
                    i++;

                }

            }

        }else { // si no es constructor
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
                    throw new SemantErrorException(this.getLine(), this.getCol(),
                            "El tipo del parametro '" + argumento.getName() + "' no coincide con el tipo del parametro en la firma del metodo '" + metodo + "'.",
                            "sentencia");
                }
                i++;

            }
            //si pasa todas las validaciones seteo el tipo (?ESTA BIEN LUU... solo se hace para metodos q no sean constructor?
            this.setNodeType(ts.getCurrentStruct().getMetodos().get(metodo).getRet());
        }


        // Ver caso especial de constructor (y constructor de array) (FALTANNNNNNNNNNNNNNNNNNN estas )
        return true;
    }

}
