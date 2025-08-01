public abstract class Conta implements IConta{
    private static final int AGENCIA_PADRAO = 1;
    private static int SEQUENCIAL = 1;

    protected final int agencia;
    protected final int numero;
    protected double saldo;

    public Conta(){
        this.agencia = AGENCIA_PADRAO;
        this.numero = SEQUENCIAL++;
    }


    public double getSaldo() {
        return saldo;
    }

    public int getAgencia() {
        return agencia;
    }


    public int getNumero() {
        return numero;
    }

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        if(valor > this.saldo){
            throw new SaldoInsuficienteException("Saldo Insuficiente");
        }
        this.saldo -= valor;
    }

    @Override
    public void depositar(double valor) {
        this.saldo += valor;
    }

    @Override
    public void transferir(double valor, Conta conta) throws SaldoInsuficienteException {
        this.sacar(valor);
        conta.depositar(valor);
    }

    public void imprimir(){
        System.out.println("Agencia: " + agencia);
        System.out.println("Numero: " + numero);
        System.out.println("Saldo: " + saldo);
    }

    public abstract void passarMes();

}
