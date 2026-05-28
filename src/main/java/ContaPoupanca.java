/**
 * Conta poupança com rendimento mensal automático.
 *
 * Refatorações aplicadas:
 *  - Número mágico substituído por constante RENDIMENTO_MENSAL
 *  - ImprimirExtrato() → imprimirExtrato() retorna String (camelCase + testável)
 */
public class ContaPoupanca extends Conta {

    public static final double RENDIMENTO_MENSAL = 0.05;

    /**
     * Aplica rendimento de {@value #RENDIMENTO_MENSAL} ao mês sobre o saldo atual.
     */
    @Override
    public void passarMes() {
        setSaldo(getSaldo() + getSaldo() * RENDIMENTO_MENSAL);
    }

    /** Retorna o extrato formatado da conta poupança (testável). */
    public String imprimirExtrato() {
        return "===== Conta Poupança =====\n" + toString();
    }
}
