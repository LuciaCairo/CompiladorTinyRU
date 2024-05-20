/? Acceso
/? Verificar que haya error cuando se quiere hacer un acceso a un metodo que no existe en el struct
/? Salida esperada: ERROR: SEMANTICO - SENTENCIAS
/? | LINEA 8 | COLUMNA 3 | No se puede llamar a un metodo que no existe. Debe definir el metodo 's' en el struct 'B' o heredarlo.|

struct A{Str s;}
impl A{ .(B a, Int e){
(a.m1());}}

struct B{}
impl B {
    fn m1(B p1)->void{}
    .(){}
}

struct C{}
impl C {
    fn m(B p1)->void{}
    .(){}
}

start{}

