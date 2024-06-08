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
}

start{
    Fibonacci fib;
    Int n;
    fib = new Fibonacci(8);
    n=fib.incrementador(5);
}
