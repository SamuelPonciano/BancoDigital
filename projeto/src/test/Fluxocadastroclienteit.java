import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TESTE DE INTEGRAÇÃO — Fluxo de Cadastro de Cliente
 *
 * Valida o fluxo completo: Banco → Cliente → ContaCorrente / ContaPoupanca
 * Simula o que o sistema faz quando um atendente cadastra um novo cliente
 * e abre suas contas bancárias.
 *
 * Componentes integrados: Banco  ←→  Cliente  ←→  Conta
 */
@DisplayName("[IT] Fluxo de Cadastro de Cliente")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FluxoCadastroClienteIT {

    private Banco banco;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        Cliente.resetId();
        Conta.resetSequencial();
        banco = new Banco();
    }

    // ── 1. Banco cria o cliente ──────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("Banco cria cliente e o registra internamente")
    void bancoDeveCriarClienteERegistrarNaLista() {
        cliente = banco.CriarConta("Maria Santos", "987.654.321-00");

        assertAll("cliente registrado no banco",
                () -> assertNotNull(cliente, "cliente não deve ser null"),
                () -> assertEquals(1, banco.getClienteList().size(), "banco deve ter 1 cliente"),
                () -> assertSame(cliente, banco.getClienteList().get(0), "objeto deve ser o mesmo")
        );
    }

    @Test
    @Order(2)
    @DisplayName("Banco cria cliente com dados corretos")
    void clienteCriadoPeloBancoDeveTerDadosCorretos() {
        cliente = banco.CriarConta("Maria Santos", "987.654.321-00");

        assertAll("dados do cliente",
                () -> assertEquals("Maria Santos", cliente.getNome()),
                () -> assertEquals("987.654.321-00", cliente.getCPF()),
                () -> assertEquals(1, cliente.getIdCliente())
        );
    }

    // ── 2. Cliente abre conta corrente via fluxo integrado ──────────

    @Test
    @Order(3)
    @DisplayName("Cliente criado pelo banco pode abrir conta corrente")
    void clienteCriadoPeloBancoDeveAbrirContaCorrente() {
        cliente = banco.CriarConta("João Ferreira", "111.222.333-44");
        cliente.criarContaCorrente();

        assertNotNull(cliente.getContaCorrente(), "conta corrente deve existir");
        assertEquals(1, cliente.getContaCorrente().getAgencia());
    }

    @Test
    @Order(4)
    @DisplayName("Cliente criado pelo banco pode abrir conta poupança")
    void clienteCriadoPeloBancoDeveAbrirContaPoupanca() {
        cliente = banco.CriarConta("Carla Dias", "555.666.777-88");
        cliente.criarContaPoupanca();

        assertNotNull(cliente.getContaPoupanca(), "conta poupança deve existir");
    }

    @Test
    @Order(5)
    @DisplayName("Cliente pode ter ambas as contas abertas")
    void clienteDevePoderTerContaCorrenteEPoupancaSimultaneamente() {
        cliente = banco.CriarConta("Pedro Lima", "999.888.777-66");
        cliente.criarContaCorrente();
        cliente.criarContaPoupanca();

        assertAll("ambas as contas",
                () -> assertNotNull(cliente.getContaCorrente()),
                () -> assertNotNull(cliente.getContaPoupanca())
        );
    }

    // ── 3. Busca integrada: banco encontra cliente recém-criado ─────

    @Test
    @Order(6)
    @DisplayName("Banco localiza cliente recém-criado por ID")
    void bancodeveLocalizarClientePorIdDepoisDaCriacao() {
        cliente = banco.CriarConta("Ana Paula", "123.123.123-12");
        Cliente encontrado = banco.buscarClienteId(cliente.getIdCliente());

        assertNotNull(encontrado);
        assertEquals(cliente.getNome(), encontrado.getNome());
    }

    @Test
    @Order(7)
    @DisplayName("Banco localiza cliente recém-criado por nome")
    void bancoDeveLocalizarClientePorNomeDepoisDaCriacao() {
        banco.CriarConta("Rodrigo Alves", "456.456.456-45");
        Cliente encontrado = banco.buscarClienteNome("Rodrigo Alves");

        assertNotNull(encontrado);
        assertEquals("456.456.456-45", encontrado.getCPF());
    }

    // ── 4. Estado consistente após múltiplos cadastros ──────────────

    @Test
    @Order(8)
    @DisplayName("Múltiplos cadastros geram IDs únicos e ordenados")
    void multiplosClientesDevemTerIdsUnicos() {
        Cliente c1 = banco.CriarConta("Cliente A", "001");
        Cliente c2 = banco.CriarConta("Cliente B", "002");
        Cliente c3 = banco.CriarConta("Cliente C", "003");

        assertAll("IDs únicos e crescentes",
                () -> assertNotEquals(c1.getIdCliente(), c2.getIdCliente()),
                () -> assertNotEquals(c2.getIdCliente(), c3.getIdCliente()),
                () -> assertTrue(c1.getIdCliente() < c2.getIdCliente()),
                () -> assertTrue(c2.getIdCliente() < c3.getIdCliente())
        );
    }

    @Test
    @Order(9)
    @DisplayName("saldoGeral do banco é zero logo após criação de clientes sem contas")
    void saldoGeralDeveSerZeroParaClientesSemContas() {
        banco.CriarConta("Sem Conta A", "001");
        banco.CriarConta("Sem Conta B", "002");

        assertEquals(0.0, banco.saldoGeral(), 0.001);
    }
}