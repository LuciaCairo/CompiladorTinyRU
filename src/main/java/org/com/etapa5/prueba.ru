/? Prueba while
/? Imprime 0 - 1 - 2 - 3 - 40

start{
    Int x;
    x = 0;
    while(x<4){
        (IO.out_int(x));
        (IO.out_str("\n"));
        x = x+1;
    }
    while(x<5){
        x = x * 10;
        (IO.out_int(x));
        (IO.out_str("\n"));
    }
}