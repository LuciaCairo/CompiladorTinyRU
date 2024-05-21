struct Auto{
    Rueda rueda;
    Int numAuto;
}
impl Auto {
    .(){}
    fn metodo1 () -> void {
        Rueda rueda;
        D chevrolet;
        Array Int agus;
        rueda = new Rueda(chevrolet);
        /?agus = new Int[5];
    }

}
struct Rueda:Auto {
    Int numRueda;
}
impl Rueda {
    .(Auto corsa){
    Int a1;
    ret a1;}
}
struct D:Rueda{

}
impl D{
.(){}
}

start {}