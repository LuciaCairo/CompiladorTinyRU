/? Prueba una asiganciones sobre vars de tipo clase
/? Ejercita un poco mas las expresiones tambien y el uso de la clase Object

struct A {
    B a1;
}
impl A{
    fn m1(B p1)->void
    {
        B v1;
        Object v2;
        Array Int agus;
        Str ciudad;
        Int entero;
        agus= new Int[2];

        ciudad = "STRINGGGGGGGGGGG";
        v1 = p1;
        a1 = (p1);
        v1 = new C();
        p1 = nil;
        v2 = new Object();
        v2 = p1;
       /? v1 = self;

    }

    fn m2()->void
    {
    A objetoTipoC;
    (m1(objetoTipoC));}
    .(){}
}


struct B : A{}
impl B {
    .(){}
}

struct C : B{}
impl C{.(){ }}


start{}

