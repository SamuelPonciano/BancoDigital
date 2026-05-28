/**
 * Conta corrente com limite de cheque especial e cobrança de juros sobre saldo devedor.
 *
 * Refatorações aplicadas:
 *  - Números mágicos substituídos por constantes nomeadas
 *  - ImprimirExtrato() → imprimirExtrato() retorna String (camelCase + testável)
 *  - Validação de valor herdada de Conta.validarValorPositivo()
 *  - Saldo manipulado via setSaldo() (encapsulamento mantido)
 */
public class ContaCorrente extends Conta {

    public static final double LIMITE_CHEQUE_ESPECIAL = 500.0;
    public static final double TAXA_JUROS_MENSAL      = 0.08;

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        validarValorPositivo(valor, "sacar");
        double saldoDisponivel = getSaldo() + LIMITE_CHEQUE_ESPECIAL;
        if (valor > saldoDisponivel) {
            throw new SaldoInsuficienteException(MSG_SALDO_INSUFICIENTE);
        }
        setSaldo(getSaldo() - valor);
    }

    /**
     * Aplica juros de {@value #TAXA_JUROS_MENSAL} ao mês sobre o saldo devedor.
     * Não realiza nenhuma operação se o saldo for não-negativo.
     */
    @Override
    public void passarMes() {
        if (getSaldo() < 0) {
            double juros = -getSaldo() * TAXA_JUROS_MENSAL;
            setSaldo(getSaldo() - juros);
        }
    }

    /** Retorna o extrato formatado da conta corrente (testável). */
    public String imprimirExtrato() {
        return "===== Conta Corrente =====\n" + toString();
    }
}
