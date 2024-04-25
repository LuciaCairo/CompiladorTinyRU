/? Herencia de una clase que no existe
/? Se espera:
/? ERROR: SEMANTICO - DECLARACIONES
/?  | NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: Herencia de clase inexistente. El struct ... no existe.


struct A:B{
Int a,b;
Str count;
}
impl A{
fn m() ->void{}
fn m2(Str a)-> void{ }
.(){}
}

struct B:C{
Int a,c;
Str coun;
Str count;
}
impl B{
fn m2(Str a)-> void{ }
.(){}}

struct C{
}
impl C{
.(){}

}


start{}