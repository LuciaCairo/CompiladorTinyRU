/? Este caso es para verificar que evalue el retorno de los metodos, en este caso el metodo b
/?retorna un INT y en el ret, no estoy devolviendo nada, por lo tanto deberia salir error
/? ERROR: SEMANTICO - DECLARACIONES
/?   | NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |
/?   | LINEA 25 | COLUMNA 11 | El retorno del metodo 'b' no puede ser 'Int' porque esta definido como void|

struct B{Int w;}
impl B{ .(){}
    fn b() -> A { }
}
struct A{Int b; B c; }
impl A{
    fn b(Int a) -> Int {

            Int s;
            Bool b;
            Str ciudad;
            while(s == 1) {ciudad= "Mendoza";
            b = false;
            if(s == 2)
                ciudad = "cordoba";
            else
                ciudad = "rosario";

            };
            ret (s+1);
           }
    .(){


    /? PRUEBA DE EXPRESIONES
    /?a[s||2||true || b] = 1;
    /?a[s && 2 && true && b] = 1;
    /?a[s == 2 == true == b] = 1;
    /?a[++b] = 1;
    /?a[s * 2 / true % b] = 1;
    /?a[s + 2 - true + b ] = 1;
    /?a[s < 2 ] = 1; /? tambien funciona con <=, <, >=
    /?a[s || true && false == 5 * b < ++2 ] = 1;
    /?self.c.w = 1;
    /?if(s < 2){};


    }
}


start{}