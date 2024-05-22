/? PRUEBA GENERAL
/? Verificar que se forme bien el json

struct R{}
impl R { .(D m, Int num){}}

struct D{
    Array Int a;
    Array Str t;
    Int b;
    Bool x;
}
impl D {

    .(){
        A claseA;
        Str f;
        (metodo(1,self.b));
    }

    fn metodo(Int a, Int w) -> D{
        (t[1].length());
        (IO.in_str().concat("hola").length());
        ret new A(new D());
    }
}

struct A:D{Str s;}
impl A {
    .(Object d){}

    st fn m(R c, D g) -> A{
        Object f;
        a = new Int[b];
        c = new R(self, b);
        ret self;
    }
}

start{
    A claseA;
    Str f;
    R y;
    (claseA.metodo(1,2));
    (A.m(y, claseA));
}
