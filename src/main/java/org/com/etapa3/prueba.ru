/? Este caso verifica que cuando el metodo devuelva el tipo declarado en su firma, pase sin problemas.
/?CORRECTO: SEMANTICO - SENTENCIAS

struct B{Int w;}
impl B{ .(){}
    fn b() -> A { }
}
struct A{Int b; B c; }
impl A{
    fn b(Int a) -> Str{

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
    ret ciudad;
    }
    fn b1(Int a) -> Int{ret a;}
    fn b2() -> Bool{ret false;}
    fn b3() -> Char{ret 'a'; }
    .(){

    }
}


start{}