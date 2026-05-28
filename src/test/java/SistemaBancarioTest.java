import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários do Sistema Bancário — pós-refatoração.
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
class SistemaBancarioTest {

    private Banco banco;

    @BeforeEach
    void setUp() {
        Cliente.resetId();
        Conta.resetSequencial();
        banco = new Banco();
    }

    // ── Banco ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Banco: criarCliente registra cliente na lista")
    void bancoCriarClienteRegistraNaLista() {
        Cliente c = banco.criarCliente("Ana", "111.111.111-11");
        assertAll(
            () -> assertEquals(1, banco.getClientes().size()),
            () -> assertEquals("Ana", c.getNome()),
            () -> assertEquals("111.111.111-11", c.getCPF()),
            () -> assertEquals(1, c.getIdCliente())
        );
    }

    @Test
    @DisplayName("Banco: buscarClientePorId retorna cliente correto")
    void bancoBuscarClientePorIdRetornaClienteCorreto() {
        banco.criarCliente("Ana", "111.111.111-11");
        assertTrue(banco.buscarClientePorId(1).isPresent());
        assertEquals("Ana", banco.buscarClientePorId(1).get().getNome());
    }

    @Test
    @DisplayName("Banco: buscarClientePorId retorna vazio para ID inexistente")
    void bancoBuscarClientePorIdRetornaVazioParaIdInexistente() {
        assertTrue(banco.buscarClientePorId(99).isEmpty());
    }

    @Test
    @DisplayName("Banco: buscarClientePorNome retorna cliente correto")
    void bancoBuscarClientePorNomeRetornaClienteCorreto() {
        banco.criarCliente("Carlos", "222.222.222-22");
        assertTrue(banco.buscarClientePorNome("carlos").isPresent());
    }

    @Test
    @DisplayName("Banco: saldoGeral soma todas as contas")
    void bancoSaldoGeralSomaTodasAsContas() throws SaldoInsuficienteException {
        Cliente c1 = banco.criarCliente("Ana", "111.111.111-11");
        Cliente c2 = banco.criarCliente("Bob", "222.222.222-22");
        c1.criarContaCorrente();
        c1.getContaCorrente().depositar(500);
        c2.criarContaPoupanca();
        c2.getContaPoupanca().depositar(300);
        assertEquals(800.0, banco.saldoGeral(), 0.001);
    }

    @Test
    @DisplayName("Banco: listarClientes retorna mensagem quando vazio")
    void bancoListarClientesRetornaMensagemQuandoVazio() {
        assertEquals("Nenhum cliente cadastrado.", banco.listarClientes());
    }

    @Test
    @DisplayName("Banco: criarCliente lança exceção para nome vazio")
    void bancoCriarClienteLancaExcecaoParaNomeVazio() {
        assertThrows(IllegalArgumentException.class,
            () -> banco.criarCliente("", "111.111.111-11"));
    }

    @Test
    @DisplayName("Banco: criarCliente lança exceção para CPF nulo")
    void bancoCriarClienteLancaExcecaoParaCpfNulo() {
        assertThrows(IllegalArgumentException.class,
            () -> banco.criarCliente("Ana", null));
    }

    // ── Cliente ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Cliente: criarContaCorrente cria conta com agência padrão")
    void clienteCriarContaCorrenteCriaContaComAgenciaPadrao() {
        Cliente c = banco.criarCliente("Ana", "111.111.111-11");
        c.criarContaCorrente();
        assertNotNull(c.getContaCorrente());
        assertEquals(1, c.getContaCorrente().getAgencia());
    }

    @Test
    @DisplayName("Cliente: criarContaCorrente duplicada lança exceção")
    void clienteCriarContaCorrenteDuplicadaLancaExcecao() {
        Cliente c = banco.criarCliente("Ana", "111.111.111-11");
        c.criarContaCorrente();
        IllegalStateException ex = assertThrows(IllegalStateException.class,
            c::criarContaCorrente);
        assertTrue(ex.getMessage().toLowerCase().contains("corrente"));
    }

    @Test
    @DisplayName("Cliente: criarContaPoupanca duplicada lança exceção com mensagem correta")
    void clienteCriarContaPoupancaDuplicadaLancaExcecaoComMensagemCorreta() {
        Cliente c = banco.criarCliente("Ana", "111.111.111-11");
        c.criarContaPoupanca();
        IllegalStateException ex = assertThrows(IllegalStateException.class,
            c::criarContaPoupanca);
        // Verifica que a mensagem menciona "poupança" (bug corrigido na refatoração)
        assertTrue(ex.getMessage().toLowerCase().contains("poupan"),
            "Mensagem deve mencionar poupança, mas foi: " + ex.getMessage());
    }

    @Test
    @DisplayName("Cliente: compareTo ordena por ID de criação")
    void clienteCompareToOrdenaPorId() {
        Cliente c1 = banco.criarCliente("Ana", "111.111.111-11");
        Cliente c2 = banco.criarCliente("Bob", "222.222.222-22");
        assertTrue(c1.compareTo(c2) < 0);
        assertTrue(c2.compareTo(c1) > 0);
        assertEquals(0, c1.compareTo(c1));
    }

    @Test
    @DisplayName("Cliente: toString exibe todos os 4 estados de conta")
    void clienteToStringExibeTodosOsEstados() {
        Cliente c = banco.criarCliente("Ana", "111.111.111-11");
        String semContas = c.toString();
        assertTrue(semContas.contains("Não criada"));

        c.criarContaCorrente();
        c.criarContaPoupanca();
        String comContas = c.toString();
        assertTrue(comContas.contains("Criada"));
        assertTrue(comContas.contains("Ana"));
        assertTrue(comContas.contains("111.111.111-11"));
    }

    // ── Conta Corrente ─────────────────────────────────────────────────

    @Test
    @DisplayName("ContaCorrente: depositar aumenta saldo corretamente")
    void contaCorrenteDepositarAumentaSaldo() {
        Cliente c = banco.criarCliente("Ana", "111.111.111-11");
        c.criarContaCorrente();
        c.getContaCorrente().depositar(1000);
        assertEquals(1000.0, c.getContaCorrente().getSaldo(), 0.001);
    }

    @Test
    @DisplayName("ContaCorrente: sacar diminui saldo corretamente")
    void contaCorrenteSacarDiminuiSaldo() throws SaldoInsuficienteException {
        Cliente c = banco.criarCliente("Ana", "111.111.111-11");
        c.criarContaCorrente();
        c.getContaCorrente().depositar(1000);
        c.getContaCorrente().sacar(400);
        assertEquals(600.0, c.getContaCorrente().getSaldo(), 0.001);
    }

    @Test
    @DisplayName("ContaCorrente: sacar usando cheque especial deixa saldo negativo")
    void contaCorrenteSacarUsandoChequeEspecialDeixaSaldoNegativo() throws SaldoInsuficienteException {
        Cliente c = banco.criarCliente("Ana", "111.111.111-11");
        c.criarContaCorrente();
        c.getContaCorrente().depositar(100);
        c.getContaCorrente().sacar(400); // usa 300 do cheque especial
        assertEquals(-300.0, c.getContaCorrente().getSaldo(), 0.001);
    }

    @Test
    @DisplayName("ContaCorrente: sacar além do cheque especial lança SaldoInsuficienteException")
    void contaCorrenteSacarAlemDoChequeEspecialLancaExcecao() {
        Cliente c = banco.criarCliente("Ana", "111.111.111-11");
        c.criarContaCorrente();
        c.getContaCorrente().depositar(100);
        assertThrows(SaldoInsuficienteException.class,
            () -> c.getContaCorrente().sacar(700));
    }

    @Test
    @DisplayName("ContaCorrente: depositar valor negativo lança IllegalArgumentException")
    void contaCorrenteDepositarValorNegativoLancaExcecao() {
        Cliente c = banco.criarCliente("Ana", "111.111.111-11");
        c.criarContaCorrente();
        assertThrows(IllegalArgumentException.class,
            () -> c.getContaCorrente().depositar(-100));
    }

    @Test
    @DisplayName("ContaCorrente: sacar valor negativo lança IllegalArgumentException")
    void contaCorrenteSacarValorNegativoLancaExcecao() {
        Cliente c = banco.criarCliente("Ana", "111.111.111-11");
        c.criarContaCorrente();
        c.getContaCorrente().depositar(500);
        assertThrows(IllegalArgumentException.class,
            () -> c.getContaCorrente().sacar(-50));
    }

    @Test
    @DisplayName("ContaCorrente: passarMes aplica juros sobre saldo devedor")
    void contaCorrentePassarMesAplicaJurosSobreSaldoDevedor() throws SaldoInsuficienteException {
        Cliente c = banco.criarCliente("Ana", "111.111.111-11");
        c.criarContaCorrente();
        c.getContaCorrente().depositar(100);
        c.getContaCorrente().sacar(400); // saldo = -300
        c.getContaCorrente().passarMes();
        // juros = 300 * 0.08 = 24 => saldo = -324
        assertEquals(-324.0, c.getContaCorrente().getSaldo(), 0.001);
    }

    @Test
    @DisplayName("ContaCorrente: passarMes com saldo positivo não altera saldo")
    void contaCorrentePassarMesComSaldoPositivoNaoAlteraSaldo() {
        Cliente c = banco.criarCliente("Ana", "111.111.111-11");
        c.criarContaCorrente();
        c.getContaCorrente().depositar(1000);
        c.getContaCorrente().passarMes();
        assertEquals(1000.0, c.getContaCorrente().getSaldo(), 0.001);
    }

    @Test
    @DisplayName("ContaCorrente: imprimirExtrato retorna texto formatado")
    void contaCorrenteImprimirExtratoRetornaTextoFormatado() {
        Cliente c = banco.criarCliente("Ana", "111.111.111-11");
        c.criarContaCorrente();
        String extrato = c.getContaCorrente().imprimirExtrato();
        assertTrue(extrato.contains("Conta Corrente"));
        assertTrue(extrato.contains("Saldo"));
    }

    @Test
    @DisplayName("ContaCorrente: transferir move valor entre contas")
    void contaCorrenteTransferirMoveValorEntreContas() throws SaldoInsuficienteException {
        Cliente c1 = banco.criarCliente("Ana", "111.111.111-11");
        Cliente c2 = banco.criarCliente("Bob", "222.222.222-22");
        c1.criarContaCorrente();
        c2.criarContaCorrente();
        c1.getContaCorrente().depositar(1000);
        c1.getContaCorrente().transferir(300, c2.getContaCorrente());
        assertAll(
            () -> assertEquals(700.0, c1.getContaCorrente().getSaldo(), 0.001),
            () -> assertEquals(300.0, c2.getContaCorrente().getSaldo(), 0.001)
        );
    }

    // ── Conta Poupança ─────────────────────────────────────────────────

    @Test
    @DisplayName("ContaPoupanca: passarMes aplica rendimento de 5%")
    void contaPoupancaPassarMesAplicaRendimento() {
        Cliente c = banco.criarCliente("Ana", "111.111.111-11");
        c.criarContaPoupanca();
        c.getContaPoupanca().depositar(1000);
        c.getContaPoupanca().passarMes();
        assertEquals(1050.0, c.getContaPoupanca().getSaldo(), 0.001);
    }

    @Test
    @DisplayName("ContaPoupanca: sacar valor negativo lança IllegalArgumentException")
    void contaPoupancaSacarValorNegativoLancaExcecao() {
        Cliente c = banco.criarCliente("Ana", "111.111.111-11");
        c.criarContaPoupanca();
        c.getContaPoupanca().depositar(500);
        assertThrows(IllegalArgumentException.class,
            () -> c.getContaPoupanca().sacar(-50));
    }

    @Test
    @DisplayName("ContaPoupanca: sacar além do saldo lança SaldoInsuficienteException")
    void contaPoupancaSacarAlemDoSaldoLancaExcecao() {
        Cliente c = banco.criarCliente("Ana", "111.111.111-11");
        c.criarContaPoupanca();
        c.getContaPoupanca().depositar(100);
        assertThrows(SaldoInsuficienteException.class,
            () -> c.getContaPoupanca().sacar(200));
    }

    @Test
    @DisplayName("ContaPoupanca: imprimirExtrato retorna texto formatado")
    void contaPoupancaImprimirExtratoRetornaTextoFormatado() {
        Cliente c = banco.criarCliente("Ana", "111.111.111-11");
        c.criarContaPoupanca();
        String extrato = c.getContaPoupanca().imprimirExtrato();
        assertTrue(extrato.contains("Poupança"));
        assertTrue(extrato.contains("Saldo"));
    }

    // ── Menu ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Menu: menuPrincipal retorna string não vazia")
    void menuPrincipalRetornaStringNaoVazia() {
        assertFalse(new Menu().menuPrincipal().isBlank());
    }

    @Test
    @DisplayName("Menu: menuCliente retorna string não vazia")
    void menuClienteRetornaStringNaoVazia() {
        assertFalse(new Menu().menuCliente().isBlank());
    }

    @Test
    @DisplayName("Menu: menuConta retorna string não vazia")
    void menuContaRetornaStringNaoVazia() {
        assertFalse(new Menu().menuConta().isBlank());
    }

    @Test
    @DisplayName("Menu: menuPesquisa retorna string não vazia")
    void menuPesquisaRetornaStringNaoVazia() {
        assertFalse(new Menu().menuPesquisa().isBlank());
    }
}
