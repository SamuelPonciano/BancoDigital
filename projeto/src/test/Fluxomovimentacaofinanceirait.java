import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TESTE DE INTEGRAÇÃO — Fluxo de Movimentação Financeira
 *
 * Valida os fluxos de depósito, saque e transferência passando por:
 * Banco → Cliente → ContaCorrente / ContaPoupanca → SaldoInsuficienteException
 *
 * Cada teste representa uma operação real que o usuário realiza no sistema.
 *
 * Componentes integrados: Banco ←→ Cliente ←→ Conta ←→ Exception
 */
@DisplayName("[IT] Fluxo de Movimentação Financeira")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FluxoMovimentacaoFinanceiraIT {

    private Banco banco;
    private Cliente clienteOrigem;
    private Cliente clienteDestino;

    @BeforeEach
    void setUp() {
        Cliente.resetId();
        Conta.resetSequencial();
        banco = new Banco();

        clienteOrigem  = banco.CriarConta("Origem Silva",  "111.000.000-01");
        clienteDestino = banco.CriarConta("Destino Souza", "222.000.000-02");

        clienteOrigem.criarContaCorrente();
        clienteOrigem.criarContaPoupanca();
        clienteDestino.criarContaCorrente();
        clienteDestino.criarContaPoupanca();
    }

    // ── DEPÓSITO ────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("Depósito na conta corrente reflete no saldo geral do banco")
    void depositoNaContaCorrenteDeveRefletirNoSaldoGeral() {
        clienteOrigem.getContaCorrente().depositar(1000.0);

        assertEquals(1000.0, banco.saldoGeral(), 0.001);
    }

    @Test
    @Order(2)
    @DisplayName("Depósito na poupança reflete no saldo geral do banco")
    void depositoNaPoupancaDeveRefletirNoSaldoGeral() {
        clienteOrigem.getContaPoupanca().depositar(500.0);

        assertEquals(500.0, banco.saldoGeral(), 0.001);
    }

    @Test
    @Order(3)
    @DisplayName("Depósito em ambas as contas acumula corretamente no saldo geral")
    void depositoEmAmbasAsContasAcumulaNoSaldoGeral() {
        clienteOrigem.getContaCorrente().depositar(700.0);
        clienteOrigem.getContaPoupanca().depositar(300.0);

        assertEquals(1000.0, banco.saldoGeral(), 0.001);
    }

    // ── SAQUE ───────────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("Saque válido na conta corrente atualiza saldo do banco")
    void saqueValidoNaCorrenteAtualizaSaldoBanco() throws SaldoInsuficienteException {
        clienteOrigem.getContaCorrente().depositar(800.0);
        clienteOrigem.getContaCorrente().sacar(300.0);

        assertEquals(500.0, banco.saldoGeral(), 0.001);
    }

    @Test
    @Order(5)
    @DisplayName("Saque usando cheque especial deixa saldo geral negativo")
    void saqueComChequeEspecialDeixaSaldoGeralNegativo() throws SaldoInsuficienteException {
        // saldo zero, cheque especial = 500 → pode sacar até 500
        clienteOrigem.getContaCorrente().sacar(400.0);

        assertEquals(-400.0, banco.saldoGeral(), 0.001);
    }

    @Test
    @Order(6)
    @DisplayName("Saque além do limite lança exceção — saldo do banco não muda")
    void saqueAlemDoLimiteLancaExcecaoESaldoBancoNaoMuda() {
        clienteOrigem.getContaCorrente().depositar(100.0);
        double saldoAntes = banco.saldoGeral();

        assertThrows(SaldoInsuficienteException.class,
                () -> clienteOrigem.getContaCorrente().sacar(700.0)); // 100 + 500 cheque = 600 max

        assertEquals(saldoAntes, banco.saldoGeral(), 0.001);
    }

    // ── TRANSFERÊNCIA ENTRE CLIENTES ────────────────────────────────

    @Test
    @Order(7)
    @DisplayName("Transferência entre contas correntes de clientes distintos")
    void transferenciaBetweenContasCorrentesDiferentesClientes()
            throws SaldoInsuficienteException {
        clienteOrigem.getContaCorrente().depositar(1000.0);

        clienteOrigem.getContaCorrente()
                .transferir(400.0, clienteDestino.getContaCorrente());

        assertAll("saldos após transferência",
                () -> assertEquals(600.0,  clienteOrigem.getContaCorrente().getSaldo(),  0.001),
                () -> assertEquals(400.0,  clienteDestino.getContaCorrente().getSaldo(), 0.001),
                () -> assertEquals(1000.0, banco.saldoGeral(), 0.001,
                        "saldo total do banco não deve mudar")
        );
    }

    @Test
    @Order(8)
    @DisplayName("Transferência de corrente para poupança do mesmo cliente")
    void transferenciaDeCorrenteParaPoupancaDoMesmoCliente()
            throws SaldoInsuficienteException {
        clienteOrigem.getContaCorrente().depositar(600.0);

        clienteOrigem.getContaCorrente()
                .transferir(200.0, clienteOrigem.getContaPoupanca());

        assertAll("redistribuição interna",
                () -> assertEquals(400.0, clienteOrigem.getContaCorrente().getSaldo(), 0.001),
                () -> assertEquals(200.0, clienteOrigem.getContaPoupanca().getSaldo(),  0.001),
                () -> assertEquals(600.0, banco.saldoGeral(), 0.001)
        );
    }

    @Test
    @Order(9)
    @DisplayName("Transferência com saldo insuficiente não altera nenhuma conta")
    void transferenciaComSaldoInsuficienteNaoAlteraNenhumaConta() {
        clienteOrigem.getContaCorrente().depositar(100.0);
        clienteDestino.getContaCorrente().depositar(50.0);

        // 100 + 500 cheque = 600; tentar transferir 700 deve falhar
        assertThrows(SaldoInsuficienteException.class,
                () -> clienteOrigem.getContaCorrente()
                        .transferir(700.0, clienteDestino.getContaCorrente()));

        assertAll("saldos intactos",
                () -> assertEquals(100.0, clienteOrigem.getContaCorrente().getSaldo(),  0.001),
                () -> assertEquals(50.0,  clienteDestino.getContaCorrente().getSaldo(), 0.001)
        );
    }

    @Test
    @Order(10)
    @DisplayName("Cadeia de transferências: A → B → C mantém saldo total constante")
    void cadeiaDeTransferenciasMantemSaldoTotal() throws SaldoInsuficienteException {
        Cliente c3 = banco.CriarConta("Terceiro", "333");
        c3.criarContaCorrente();

        clienteOrigem.getContaCorrente().depositar(900.0);

        clienteOrigem.getContaCorrente()
                .transferir(300.0, clienteDestino.getContaCorrente()); // 600 / 300 / 0
        clienteDestino.getContaCorrente()
                .transferir(100.0, c3.getContaCorrente());            // 600 / 200 / 100

        assertAll("saldo total preservado em toda a cadeia",
                () -> assertEquals(600.0, clienteOrigem.getContaCorrente().getSaldo(),  0.001),
                () -> assertEquals(200.0, clienteDestino.getContaCorrente().getSaldo(), 0.001),
                () -> assertEquals(100.0, c3.getContaCorrente().getSaldo(),             0.001),
                () -> assertEquals(900.0, banco.saldoGeral(),                          0.001)
        );
    }

    // ── SALDO GERAL MULTI-CLIENTE ────────────────────────────────────

    @Test
    @Order(11)
    @DisplayName("saldoGeral consolida contas de todos os clientes do banco")
    void saldoGeralConsolidaTodasAsContas() {
        clienteOrigem.getContaCorrente().depositar(1000.0);
        clienteOrigem.getContaPoupanca().depositar(500.0);
        clienteDestino.getContaCorrente().depositar(300.0);
        clienteDestino.getContaPoupanca().depositar(200.0);

        assertEquals(2000.0, banco.saldoGeral(), 0.001);
    }
}