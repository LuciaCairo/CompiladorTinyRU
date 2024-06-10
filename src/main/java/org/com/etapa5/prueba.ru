/? Salida esperada:
/? 5
struct Fib {}
impl Fib {
    .(){}
    st fn fib(Int n) -> Int{
        if(n==0){
            ret 0;
        }
        if(n==1){
            ret 1;
        }
        ret (Fib.fib(n-1) + Fib.fib(n-2));
    }
}


start{

    (IO.out_int(Fib.fib(5)));
}