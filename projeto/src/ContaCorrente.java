public class ContaCorrente extends Conta{

    private double chequeEspecial = 500.0;

    public void ImprimirExtrato(){
        System.out.println("=====Conta Corrente=====");
        super.imprimir();
    }

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        double saldoDisponivel = saldo + chequeEspecial;
        if(valor > saldoDisponivel) throw new SaldoInsuficienteException("Saldo Insuficiente");
        this.saldo -= valor;
    }

    @Override
    public void passarMes() {
        if (saldo < 0) {
            double taxaJuros = 0.08;
            double juros = -saldo * taxaJuros;
            saldo -= juros;
        }
    }

}
