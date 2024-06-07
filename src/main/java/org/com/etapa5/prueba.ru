struct Fibonacci {
    Int suma;
    Int i,j;
}

impl Fibonacci {
    .(){
        i=9; /? inicializo i
        j=0; /? inicializo j
        suma=0; /? inicializo suma
    }
}


start{
    Fibonacci fib;
    Int n;
    fib = new Fibonacci();
    n = 3;
    (IO.out_int(n));
}
