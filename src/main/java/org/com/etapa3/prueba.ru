/? Herencia de una clase que no existe
/? Se espera:
/? ERROR: SEMANTICO - DECLARACIONES
/?  | NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: Herencia de clase inexistente. El struct ... no existe.


struct A:B{
Int a,b;
Str count5;
}
impl A{
fn m() ->void{}
.(){}
}

struct B:C{
Int p,c;
Str coun;
Str count;
}
impl B{
fn m(Str a)-> void{ }
.(){}}

struct C{
}
impl C{
.(){}}


start{}