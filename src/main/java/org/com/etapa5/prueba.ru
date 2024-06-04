/? Construye un objeto basico lo asigna a una var local y llama a un metodo dinamico
/? con dos parametros debe mostrar 30

struct A{
}

impl A{
    .(){
    }

    /?fn m1(Int p1, Int p2)-> void{(IO.out_int(p1+p2));}
}

start{
    A x;
    x = new A();
    /?(x.m1(10,20));
}