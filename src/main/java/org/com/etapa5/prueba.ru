/? Construye un objeto basico lo asigna a una var local y llama a un metodo dinamico
/? con dos parametros debe mostrar 30

struct Fibonacci {
    Int suma;
    Int i,j;
}

impl Fibonacci {
    .(){
        i=0; /? inicializo i
        j=0; /? inicializo j
        suma=0; /? inicializo suma
    }
}



start{
    Fibonacci fib;
    Int n;
    fib = new Fibonacci();
    /?n = IO.in_int();
    /?(IO.out_int(fib.sucesion_fib(n)));
}
