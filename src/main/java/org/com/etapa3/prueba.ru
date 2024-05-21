/? If / While
/? Verificar que haya error cuando se quiere hacer que la expresion no sea bool
/? Salida esperada: ERROR: SEMANTICO - SENTENCIAS
/? | LINEA 19 | COLUMNA 18 | La condicion del if debe ser de tipo Bool|

struct D{
    Int a;
    Int b;
    Bool x;
}
impl D { .(){}}

struct A :D{Int s;}
impl A {
    .(){}

    fn m(Object c) -> void {
        if(c){
            while(1+2){
                ret;
            }
        } /? Aca esta el error

    }
}

start{}
