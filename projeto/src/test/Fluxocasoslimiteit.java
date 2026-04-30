import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TESTE DE INTEGRAÇÃO — Casos Limite e Bugs Documentados
 *
 * Valida comportamentos de fronteira que envolvem múltiplos componentes
 * e documenta bugs conhecidos do sistema atual para rastreamento de regressão.
 *
 * Componentes integrados: todos — Banco, Cliente, ContaCorrente, ContaPoupanca
 */
@DisplayName("[IT] Casos Limite e Bugs Documentados")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FluxoCasosLimiteIT {

    private Banco banco;

    @BeforeEach
    void setUp() {
        Cliente.resetId();
        Conta.resetSequencial();
        banco = new Banco();
    }

    // ── LIMITE: valores extremos ─────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("Depósito de valor muito alto não causa overflow")
    void depositoDeValorAltoNaoCausaOverflow() {
        Cliente c = banco.CriarConta("Milionário", "001");
        c.criarContaCorrente();
        c.getContaCorrente().depositar(Double.MAX_VALUE / 2);

        assertTrue(c.getContaCorrente().getSaldo() > 0,
                "saldo deve ser positivo mesmo com valor muito alto");
    }

    @Test
    @Order(2)
    @DisplayName("Transferência de valor zero não altera nenhum saldo")
    void transferenciaDeZeroNaoAlteraSaldos() throws SaldoInsuficienteException {
        Cliente origem  = banco.CriarConta("Origem",  "002");
        Cliente destino = banco.CriarConta("Destino", "003");
        origem.criarContaCorrente();
        destino.criarContaCorrente();
        origem.getContaCorrente().depositar(500.0);

        origem.getContaCorrente().transferir(0.0, destino.getContaCorrente());

        assertAll("saldos inalterados",
                () -> assertEquals(500.0, origem.getContaCorrente().getSaldo(),  0.001),
                () -> assertEquals(0.0,   destino.getContaCorrente().getSaldo(), 0.001)
        );
    }

    @Test
    @Order(3)
    @DisplayName("Saque do valor exato do saldo deixa conta zerada")
    void saqueExatoDoSaldoDeixaContaZerada() throws SaldoInsuficienteException {
        Cliente c = banco.CriarConta("Exato", "004");
        c.criarContaPoupanca();
        c.getContaPoupanca().depositar(777.77);
        c.getContaPoupanca().sacar(777.77);

        assertEquals(0.0, c.getContaPoupanca().getSaldo(), 0.001);
        assertEquals(0.0, banco.saldoGeral(), 0.001);
    }

    // ── BUG #1: validação de CPF ausente ─────────────────────────────

    @Test
    @Order(4)
    @DisplayName("[BUG #1] Banco aceita CPF nulo — sem validação de entrada")
    void bancoAceitaCpfNuloSemValidacao() {
        // COMPORTAMENTO ATUAL (bug): sistema não valida CPF.
        // Ideal: lançar IllegalArgumentException para CPF null/vazio.
        assertDoesNotThrow(() -> banco.CriarConta("Sem CPF", null),
                "sistema atual aceita CPF nulo — bug documentado para correção futura");
    }

    @Test
    @Order(5)
    @DisplayName("[BUG #1] Banco aceita nome nulo — sem validação de entrada")
    void bancoAceitaNomeNuloSemValidacao() {
        // Mesmo bug: sem validação de campos obrigatórios
        assertDoesNotThrow(() -> banco.CriarConta(null, "000.000.000-00"),
                "sistema atual aceita nome nulo — bug documentado para correção futura");
    }

    // ── BUG #2: depósito de valor negativo aceito ────────────────────

    @Test
    @Order(6)
    @DisplayName("[BUG #2] Depósito de valor negativo diminui o saldo — sem validação")
    void depositoNegativoDiminuiSaldo() {
        Cliente c = banco.CriarConta("Teste", "005");
        c.criarContaCorrente();
        c.getContaCorrente().depositar(1000.0);

        // COMPORTAMENTO ATUAL (bug): depositar(-200) subtrai do saldo
        c.getContaCorrente().depositar(-200.0);

        // Documentando que o saldo caiu — não é o comportamento esperado
        assertEquals(800.0, c.getContaCorrente().getSaldo(), 0.001,
                "[BUG #2] depositar() aceita valores negativos — saldo reduzido indevidamente");
    }

    // ── BUG #3: mensagem de erro duplicidade poupança ─────────────────

    @Test
    @Order(7)
    @DisplayName("[BUG #3] Mensagem de erro ao duplicar conta poupança diz 'corrente'")
    void mensagemErroContaPoupancaDuplicadaEstaErrada() {
        Cliente c = banco.CriarConta("Poupador", "006");
        c.criarContaPoupanca();

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> c.criarContaPoupanca()
        );

        // BUG: a mensagem diz "Conta corrente" em vez de "Conta poupança"
        assertEquals("Cliente já possui Conta corrente", ex.getMessage(),
                "[BUG #3] Mensagem errada para duplicidade de poupança — " +
                        "deveria dizer 'Conta poupança'");
    }

    // ── BUG #4: saque de valor negativo é permitido ──────────────────

    @Test
    @Order(8)
    @DisplayName("[BUG #4] Saque de valor negativo incrementa o saldo indevidamente")
    void saqueDeValorNegativoIncrementaSaldo() throws SaldoInsuficienteException {
        Cliente c = banco.CriarConta("Malandragem", "007");
        c.criarContaCorrente();
        c.getContaCorrente().depositar(100.0);

        // COMPORTAMENTO ATUAL (bug): sacar(-50) faz saldo aumentar para 150
        c.getContaCorrente().sacar(-50.0);

        assertEquals(150.0, c.getContaCorrente().getSaldo(), 0.001,
                "[BUG #4] sacar() aceita valores negativos — saldo aumentado indevidamente");
    }

    // ── INTEGRAÇÃO: busca após remoção implícita ─────────────────────

    @Test
    @Order(9)
    @DisplayName("Busca por ID inexistente retorna null sem exceção")
    void buscaPorIdInexistenteRetornaNullSemExcecao() {
        banco.CriarConta("Alguém", "008");
        assertDoesNotThrow(() -> {
            Cliente resultado = banco.buscarClienteId(9999);
            assertNull(resultado);
        });
    }

    @Test
    @Order(10)
    @DisplayName("Busca por nome vazio retorna null sem exceção")
    void buscaPorNomeVazioRetornaNullSemExcecao() {
        banco.CriarConta("Alguém", "009");
        assertDoesNotThrow(() -> {
            Cliente resultado = banco.buscarClienteNome("");
            assertNull(resultado);
        });
    }

    // ── ESTADO: consistência após operações mistas ───────────────────

    @Test
    @Order(11)
    @DisplayName("saldoGeral é consistente após sequência complexa de operações")
    void saldoGeralConsistenteAposOperacoesMistas() throws SaldoInsuficienteException {
        Cliente c1 = banco.CriarConta("C1", "010");
        Cliente c2 = banco.CriarConta("C2", "011");
        c1.criarContaCorrente();
        c1.criarContaPoupanca();
        c2.criarContaCorrente();

        c1.getContaCorrente().depositar(1000.0);   // banco: 1000
        c1.getContaPoupanca().depositar(500.0);    // banco: 1500
        c2.getContaCorrente().depositar(300.0);    // banco: 1800

        c1.getContaCorrente().transferir(200.0, c2.getContaCorrente()); // banco: 1800
        c1.getContaPoupanca().sacar(100.0);        // banco: 1700

        banco.avancarMes();
        // corrente c1: 800 (positivo, sem juros)
        // poupança c1: 400 * 1.05 = 420
        // corrente c2: 500 (positivo, sem juros)
        // total: 800 + 420 + 500 = 1720

        assertEquals(1720.0, banco.saldoGeral(), 0.001);
    }
}