/**
 * Classe base para todos os tipos de conta bancária.
 *
 * Refatorações aplicadas:
 *  - saldo tornado private com setSaldo() protegido para subclasses
 *  - validação de valor positivo em sacar() e depositar()
 *  - toString() testável substitui imprimir()
 *  - constante MSG_SALDO_INSUFICIENTE centralizada (elimina duplicação)
 */
public abstract class Conta implements IConta {

    private static final int AGENCIA_PADRAO = 1;

    protected static final String MSG_SALDO_INSUFICIENTE =
            "Saldo insuficiente para a operação";

    private static int sequencial = 1;

    private final int agencia;
    private final int numero;
    private double saldo;

    public Conta() {
        this.agencia = AGENCIA_PADRAO;
        this.numero  = sequencial++;
    }

    // ── Getters ──────────────────────────────────────────────────────

    public double getSaldo()   { return saldo;   }
    public int    getAgencia() { return agencia; }
    public int    getNumero()  { return numero;  }

    /** Reseta o sequencial de número de conta (uso exclusivo em testes). */
    public static void resetSequencial() { sequencial = 1; }

    // ── Acesso protegido ao saldo (para subclasses com regras especiais) ──

    /**
     * Permite que subclasses ajustem o saldo diretamente,
     * inclusive para valores negativos (ex: cheque especial).
     */
    protected void setSaldo(double novoSaldo) {
        this.saldo = novoSaldo;
    }

    // ── Operações financeiras ─────────────────────────────────────────

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        validarValorPositivo(valor, "sacar");
        if (valor > this.saldo) {
            throw new SaldoInsuficienteException(MSG_SALDO_INSUFICIENTE);
        }
        this.saldo -= valor;
    }

    @Override
    public void depositar(double valor) {
        validarValorPositivo(valor, "depositar");
        this.saldo += valor;
    }

    @Override
    public void transferir(double valor, Conta destino) throws SaldoInsuficienteException {
        this.sacar(valor);
        destino.depositar(valor);
    }

    /** Aplica as regras financeiras do mês (juros, rendimentos). */
    public abstract void passarMes();

    // ── Representação ─────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format("Agência: %d | Número: %d | Saldo: R$ %.2f",
                agencia, numero, saldo);
    }

    // ── Auxiliares ────────────────────────────────────────────────────

    /**
     * Garante que o valor informado seja estritamente positivo.
     *
     * @throws IllegalArgumentException se valor for zero ou negativo
     */
    protected void validarValorPositivo(double valor, String operacao) {
        if (valor <= 0) {
            throw new IllegalArgumentException(
                    "O valor para '" + operacao + "' deve ser positivo. Recebido: " + valor);
        }
    }
}
