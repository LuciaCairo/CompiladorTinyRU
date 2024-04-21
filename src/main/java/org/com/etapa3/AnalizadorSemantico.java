package org.com.etapa3;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class AnalizadorSemantico {
    private TablaDeSimbolos ts;

    public AnalizadorSemantico(AnalizadorSintactico aS) {
        this.ts = aS.getTs();
        boolean exito = true;
        try{
            this.verificaHerenciaCircular();
            this.consolidar();
        }catch(ExcepcionSemantica eS){
            System.out.println(eS.mensaje);
            exito = false;
        }
        if(exito) System.out.println("CORRECTO: ANALISIS SEMANTICO - DECLARACIONES");
    }

    public void verificaHerenciaCircular() throws ExcepcionSemantica{
        Queue<String> verif = new LinkedList<>();
        Hashtable<String, String> finalizados = new Hashtable<>();

        verif.add("Object");
        while(finalizados.size()<this.ts.getClases().size()){
            String h = verif.remove();
            for(Map.Entry<String, EntradaClase> entry : this.ts.getClases().entrySet()) {
                String key = entry.getKey();
                EntradaClase c = entry.getValue();
                if(!key.equals(h) && c.getHerencia()!=null && c.getHerencia().equals(h)){
                    verif.add(key);
                    if(finalizados.containsKey(key)){
                        throw new ExcepcionSemantica(c.getFila(),c.getColumna(),"La clase contiene herencia circular",c.getNombre(),true);
                    }
                }
            }
            finalizados.put(h, h);
            if(verif.isEmpty() && finalizados.size()<this.ts.getClases().size()){
                for(Map.Entry<String, EntradaClase> entry : this.ts.getClases().entrySet()) {
                    String key = entry.getKey();
                    EntradaClase c = entry.getValue();
                    if(!finalizados.containsKey(key)){
                        verif.add(key);
                        continue;
                    }
                }
            }
        }

    }

    public boolean existeTipo(String tipo){
        return this.ts.getClases().get(tipo)!=null;
    }

    public void consolidar() throws ExcepcionSemantica{

        for(Map.Entry<String, EntradaClase> entry : this.ts.getClases().entrySet()) {
            String key = entry.getKey();
            EntradaClase value = entry.getValue();
            if(key.equals("Object")){
                value.setConsolidado(true);
            }
            if(!value.isConsolidado() && !key.equals("Object")){
                this.consolidarClase(key);
            }
        }
    }

    public void consolidarClase(String her) throws ExcepcionSemantica{
        EntradaClase eC = this.ts.getClases().get(her);
        if(!eC.isConsolidado() && !eC.getHerencia().equals("Object")){
            this.consolidarClase(eC.getHerencia());
        }
        EntradaClase padre = this.ts.getClases().get(eC.getHerencia()); //TODO verificar metodos y variables
        if(this.existeTipo(eC.getHerencia())){
            if(!eC.getHerencia().equals("Int") && !eC.getHerencia().equals("Bool") && !eC.getHerencia().equals("Char") && !eC.getHerencia().equals("String")){
                for(Map.Entry<String, EntradaConstante> ctes : eC.getConstantes().entrySet()) {
                    EntradaConstante cte = ctes.getValue();
                    if(!this.existeTipo(cte.getTipo())){
                        throw new ExcepcionSemantica(cte.getFila(),cte.getColumna(),"No existe el tipo de declaracion",cte.getTipo(),true);
                    }
                }
                for(Map.Entry<String, EntradaVar> vars : eC.getVariablesInst().entrySet()) {
                    EntradaVar var = vars.getValue();
                    if(!this.existeTipo(var.getTipo())){
                        throw new ExcepcionSemantica(var.getFila(),var.getColumna(),"No existe el tipo de declaracion",var.getTipo(),true);
                    }
                }
                for(Map.Entry<String, EntradaMetodo> mets : eC.getMetodos().entrySet()) {
                    EntradaMetodo met = mets.getValue();
                    if(!this.existeTipo(met.getTipoRetorno()) && !met.getTipoRetorno().equals("void")){
                        throw new ExcepcionSemantica(met.getFila(),met.getColumna(),"No existe el tipo de declaracion",met.getTipoRetorno(),true);
                    }
                    for(Map.Entry<String, EntradaParametro> param : met.getParametros().entrySet()) {
                        EntradaParametro par = param.getValue();
                        if(!this.existeTipo(par.getTipo())){
                            throw new ExcepcionSemantica(par.getFila(),par.getColumna(),"No existe el tipo de declaracion",par.getTipo(),true);
                        }
                    }
                    for(Map.Entry<String, EntradaConstante> ctesMet : met.getConstanteMet().entrySet()) {
                        EntradaConstante cteM = ctesMet.getValue();
                        if(!this.existeTipo(cteM.getTipo())){
                            throw new ExcepcionSemantica(cteM.getFila(),cteM.getColumna(),"No existe el tipo de declaracion",cteM.getTipo(),true);
                        }
                    }
                    for(Map.Entry<String, EntradaVar> varsMet : met.getVariablesMet().entrySet()) {
                        EntradaVar varM = varsMet.getValue();
                        if(!this.existeTipo(varM.getTipo())){
                            throw new ExcepcionSemantica(varM.getFila(),varM.getColumna(),"No existe el tipo de declaracion",varM.getTipo(),true);
                        }
                    }
                }
            }else{
                throw new ExcepcionSemantica(eC.getFila(),eC.getColumna(),"La clase no puede heredar de una clase base",eC.getHerencia(),true);
            }
        }else{
            throw new ExcepcionSemantica(eC.getFila(),eC.getColumna(),"Clase de Herencia mal declarada",eC.getHerencia(),true);
        }
        for(Map.Entry<String, EntradaMetodo> metH : padre.getMetodos().entrySet()) {
            EntradaMetodo meth = metH.getValue();
            EntradaMetodo auxM = eC.getMetodos().get(metH.getKey());
            if(auxM!=null){
                for(Map.Entry<String, EntradaParametro> paramH : meth.getParametros().entrySet()) {
                    EntradaParametro paramP = auxM.getParametros().get(paramH.getKey());
                    if(paramP==null){
                        throw new ExcepcionSemantica(auxM.getFila(),auxM.getColumna(),"Los atributos de un metodo heredado deben tener el mismo nombre",auxM.getNombre(),true);
                    }
                }
            }else{
                eC.insertaMetodo(metH.getKey(), meth);
            }
        }
        for(Map.Entry<String, EntradaVar> varsH : padre.getVariablesInst().entrySet()) {
            EntradaVar varsh = varsH.getValue();
            EntradaVar auxV = eC.getVariablesInst().get(varsH.getKey());
            if(auxV!=null){
                if(!auxV.getTipo().equals(varsh.getTipo())){
                    throw new ExcepcionSemantica(auxV.getFila(),auxV.getColumna(),"El tipo de la variable debe ser el mismo que el de su superclase",auxV.getTipo(),true);
                }
            }else{
                eC.insertaVariable(varsH.getKey(), varsh);
            }
        }
        eC.setConsolidado(true);
    }

}