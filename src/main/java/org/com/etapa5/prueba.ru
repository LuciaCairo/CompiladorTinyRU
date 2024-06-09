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
    fn imprimo_numero(Int num) -> void{
        (IO.out_str("f_"));
        (IO.out_int(num));
        (IO.out_str("="));
    }
    fn fg()->void{
    }
}

start{
    Fibonacci fib;


    fib = new Fibonacci(6);

    (fib.imprimo_numero(6));
}
