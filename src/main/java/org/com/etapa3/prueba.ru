struct A{Int b;}
impl A{ .(){}}

struct B{A a;}
impl B{.(){ a.b = b;}}

start{}