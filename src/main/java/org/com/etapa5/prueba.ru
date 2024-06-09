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
    fn sucesion_fib(Int n)-> Int{
        i=0; j=0; suma=10;
        suma = suma +n;
        (imprimo_numero(suma));
        (imprimo_sucesion(suma));

        ret suma;
    }

    fn imprimo_numero(Int num) -> void{

        (IO.out_str("f_"));
        (IO.out_int(num));
        (IO.out_str("="));
    }
    fn imprimo_sucesion(Int s) -> void{
           (IO.out_str("f_"));
           (IO.out_int(s));
           (IO.out_str("="));
    }


}

start{
    Fibonacci fib;
    Int h;
    h=25;
    fib = new Fibonacci(6);

    (fib.sucesion_fib(h));
}
