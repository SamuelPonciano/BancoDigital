public interface IConta {
    void sacar(double valor) throws SaldoInsuficienteException;

    void depositar(double valor);

    void transferir(double valor, Conta conta) throws SaldoInsuficienteException;
}
