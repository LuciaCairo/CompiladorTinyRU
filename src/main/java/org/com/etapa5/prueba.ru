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
        i=4; j=5; suma=10;
        while(i<= n){
            if (i==0){
                (imprimo_numero(i));
                (imprimo_sucesion(suma));
            }else{
                if(i==1){
                    (imprimo_numero(i));
                    suma=suma+i;
                    (imprimo_sucesion(suma));
                }else{
                    (imprimo_numero(i));
                    suma=suma+j;
                    j=suma;
                    (imprimo_sucesion(suma));
                    }
            }

            (++i);
        }

        ret suma;
    }

    fn imprimo_numero(Int num) -> void{

        (IO.out_str("f_"));
        (IO.out_int(num));
        (IO.out_str("="));
    }
    fn imprimo_sucesion(Int s) -> void{
           /?El valor es:
           (IO.out_int(s));
           (IO.out_str("\n"));
    }


}

start{
    Fibonacci fib;
    Int h;
    h=4;
    fib = new Fibonacci(6);

    (IO.out_int(fib.sucesion_fib(h)));
}
