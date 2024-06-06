/? Construye un objeto basico lo asigna a una var local y llama a un metodo dinamico
/? con dos parametros debe mostrar 30

struct Fibonacci {
    Int count;
    Int i;
}

impl Fibonacci {
    .(){
        /?i=0; /? inicializo i
        /?j=0; /? inicializo j
        /?suma=0; /? inicializo suma
    }
}



start{
    Fibonacci fib2,fib1;
    Int n;
    fib1 = new Fibonacci();
    fib2=new Fibonacci();
    fib1.count = fib1.count +4;
    /?n = IO.in_int();
    /?(IO.out_int(fib.sucesion_fib(n)));
}
