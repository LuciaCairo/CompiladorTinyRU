/? Acceso
/? Verificar que haya error cuando se quiere hacer un acceso encadenado a un atributo que no existe en el struct
/? Salida esperada: ERROR: SEMANTICO - SENTENCIAS
/? | LINEA 13 | COLUMNA 2 | El id "s" no esta declarado como atributo del struct 'B'.|

struct C{Int y;}
impl C{ .(){}}

struct A{}
impl A{ .(Array Str a){
Int s;
Int y;
y = a[s].s;
}
} /? Aca esta el error acceso al atributo inexistente del struct B

struct B{ Int y;}
impl B{ .(){C s;}}

start{Int b;}