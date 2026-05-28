import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração: casos limite e validações de entrada.
 *
 * Pós-refatoração: todos os bugs identificados na Etapa 4 foram corrigidos.
 * Os testes agora validam o comportamento CORRETO do sistema.
 */
class FluxoCasosLimiteIT {

    private Banco banco;
    private Cliente samuel;

    @BeforeEach
    void setUp() {
        Cliente.resetId();
        Conta.resetSequencial();
        banco  = new Banco();
        samuel = banco.criarCliente("Samuel", "123.456.789-00");
        samuel.criarContaCorrente();
        samuel.criarContaPoupanca();
    }

    // ── Validações de entrada (bugs corrigidos) ───────────────────────

    @Test
    @DisplayName("CORRIGIDO: depositar valor negativo lança IllegalArgumentException")
    void depositarValorNegativoLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> samuel.getContaCorrente().depositar(-100),
            "depositar(-100) deve lançar exceção — bug #2 corrigido");
    }

    @Test
    @DisplayName("CORRIGIDO: sacar valor negativo lança IllegalArgumentException")
    void sacarValorNegativoLancaExcecao() {
        samuel.getContaCorrente().depositar(500);
        assertThrows(IllegalArgumentException.class,
            () -> samuel.getContaCorrente().sacar(-50),
            "sacar(-50) deve lançar exceção — bug #4 corrigido");
    }

    @Test
    @DisplayName("CORRIGIDO: sacar negativo não aumenta saldo")
    void sacarValorNegativoNaoAumentaSaldo() {
        samuel.getContaCorrente().depositar(500);
        try {
            samuel.getContaCorrente().sacar(-50);
        } catch (IllegalArgumentException | SaldoInsuficienteException ignored) {}
        assertEquals(500.0, samuel.getContaCorrente().getSaldo(), 0.001,
            "Saldo não deve aumentar após tentativa de sacar negativo — bug #4 corrigido");
    }

    @Test
    @DisplayName("CORRIGIDO: mensagem ao duplicar poupança menciona poupança")
    void mensagemDuplicarPoupancaMencionaPoupanca() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
            samuel::criarContaPoupanca);
        assertTrue(ex.getMessage().toLowerCase().contains("poupan"),
            "Mensagem deve conter 'poupança' — bug #3 corrigido. Recebido: " + ex.getMessage());
    }

    @Test
    @DisplayName("CORRIGIDO: criarCliente com nome vazio lança IllegalArgumentException")
    void criarClienteComNomeVazioLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> banco.criarCliente("", "000.000.000-00"),
            "Nome vazio deve lançar exceção — bug #1 corrigido");
    }

    @Test
    @DisplayName("CORRIGIDO: criarCliente com CPF nulo lança IllegalArgumentException")
    void criarClienteComCpfNuloLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> banco.criarCliente("Nome", null),
            "CPF nulo deve lançar exceção — bug #1 corrigido");
    }

    // ── Casos limite financeiros ──────────────────────────────────────

    @Test
    @DisplayName("Sacar exatamente o saldo disponível zera a conta")
    void sacarExatamenteOSaldoZeraAConta() throws SaldoInsuficienteException {
        samuel.getContaCorrente().depositar(1000);
        samuel.getContaCorrente().sacar(1000);
        assertEquals(0.0, samuel.getContaCorrente().getSaldo(), 0.001);
    }

    @Test
    @DisplayName("Sacar exatamente o limite do cheque especial deixa saldo no mínimo")
    void sacarExatamenteLimiteChequeEspecialDeixaSaldoMinimo() throws SaldoInsuficienteException {
        // Saldo 0 + cheque 500 = pode sacar até 500
        samuel.getContaCorrente().sacar(500);
        assertEquals(-500.0, samuel.getContaCorrente().getSaldo(), 0.001);
    }

    @Test
    @DisplayName("Sacar 1 centavo além do cheque especial lança exceção")
    void sacarUmCentavoAlemDoChequeEspecialLancaExcecao() {
        samuel.getContaCorrente().depositar(100);
        // saldo disponível = 100 + 500 = 600; tentar sacar 600.01 deve falhar
        assertThrows(SaldoInsuficienteException.class,
            () -> samuel.getContaCorrente().sacar(600.01));
    }

    @Test
    @DisplayName("Transferência entre contas mantém saldo total inalterado")
    void transferenciaMantemSaldoTotal() throws SaldoInsuficienteException {
        Cliente rhydrian = banco.criarCliente("Rhydrian", "987.654.321-00");
        rhydrian.criarContaCorrente();
        samuel.getContaCorrente().depositar(1000);
        samuel.getContaCorrente().transferir(400, rhydrian.getContaCorrente());
        assertEquals(1000.0, banco.saldoGeral(), 0.001);
    }
}
