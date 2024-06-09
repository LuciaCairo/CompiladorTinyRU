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
    fn incrementador(Int k)->Int{

        ret ++k;
    }
    fn fg()->void{
    }
}

start{
    Fibonacci fib;
    Int n;
    n=6;
    fib = new Fibonacci(5);

    n=fib.incrementador(n);
    (IO.out_int(n));
}
