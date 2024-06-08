struct Fibonacci {
    Int suma;
    Int i,j;
}

impl Fibonacci {
    .(Int c){
        i=9; /? inicializo i
        j=c; /? inicializo j
        suma=0; /? inicializo suma
    }
}

start{
    Fibonacci fib;
    Int n;

    fib = new Fibonacci(15);
    n=fib.j;
    (IO.out_int(n));
}
