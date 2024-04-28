struct A:B{
Char a;
}

struct B:C{
Array Int b;
}

impl A{
st fn f0(Str a, Int b) -> void{Int r;}
fn f1(Array Int d) -> Array Int{}
.(){C v;}  /? Aca esta el error C no existe
}

impl B{
fn f0(Str a, Int b) -> void{Int z;}
.(){}}

struct C{
A s;
}

impl C{
st fn f0(Str a, Int b) -> void{}
fn f1(Array Int d) -> Array Int{Str hola;}
.(){}
}

start{
Str g;
}