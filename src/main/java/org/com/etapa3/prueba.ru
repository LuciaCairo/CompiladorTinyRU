struct B{Int w;}
impl B{ .(){}
fn b() -> A { }}
struct A{Int b; B c; }
impl A{
fn b(Int a) -> A { }
.(){
Array Int a;
Int s;
Bool b;
/? PRUEBA DE EXPRESIONES
/?a[s||2||true || b] = 1;
/?a[s && 2 && true && b] = 1;
/?a[s == 2 == true == b] = 1;
/?a[++b] = 1;
/?a[s * 2 / true % b] = 1;
/?a[s + 2 - true + b ] = 1;
/?a[s < 2 ] = 1; /? tambien funciona con <=, <, >=
/?a[s || true && false == 5 * b < ++2 ] = 1;
/?self.c.w = 1;
/?if(s < 2){};
s = (c).b().c;

}}


start{}