struct B{Int w;}
impl B{ .(){}}
struct A{Int b; B c;}
impl A{ .(){
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
/?a[s || 2 && true == !b + 5 * b < ++2 ] = 1;
/?self.c.w = 1;
/?if(s < 2){};
/?while(s && 2){}
ret b || a;
}}


start{}