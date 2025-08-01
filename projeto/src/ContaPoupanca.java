public class ContaPoupanca extends Conta{
    public void ImprimirExtrato(){
        System.out.println("=====Conta Poupança=====");
        super.imprimir();
    }

    @Override
    public void passarMes() {
        saldo += saldo * 0.05;
    }
}
