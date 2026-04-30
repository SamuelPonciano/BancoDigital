import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TESTE DE INTEGRAÇÃO — Fluxo de Avanço de Mês
 *
 * Valida o fluxo completo da operação "passarMes" que percorre
 * todos os clientes do banco e aplica rendimentos/juros.
 *
 * Componentes integrados:
 *   Banco.avancarMes() → Cliente (iteração) → ContaCorrente.passarMes()
 *                                           → ContaPoupanca.passarMes()
 *
 * Regras de negócio verificadas:
 *   - Poupança: +5% ao mês sobre saldo positivo
 *   - Corrente: -8% ao mês sobre saldo negativo (juros sobre cheque especial usado)
 *   - Corrente com saldo positivo: sem alteração
 *   - Contas não abertas: sem erro (null safe)
 */
@DisplayName("[IT] Fluxo de Avanço de Mês")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FluxoAvancoMesIT {

    private Banco banco;

    @BeforeEach
    void setUp() {
        Cliente.resetId();
        Conta.resetSequencial();
        banco = new Banco();
    }

    // ── POUPANÇA: rendimento ─────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("Banco aplica rendimento de 5% na poupança ao avançar mês")
    void bancoAplicaRendimentoNaPoupancaAoAvancarMes() {
        Cliente c = banco.CriarConta("Invest", "001");
        c.criarContaPoupanca();
        c.getContaPoupanca().depositar(2000.0);

        banco.avancarMes();

        assertEquals(2100.0, c.getContaPoupanca().getSaldo(), 0.001);
    }

    @Test
    @Order(2)
    @DisplayName("Rendimento da poupança é composto ao avançar vários meses")
    void rendimentoPoupancaCompostoEmVariosMeses() {
        Cliente c = banco.CriarConta("Poupador", "002");
        c.criarContaPoupanca();
        c.getContaPoupanca().depositar(1000.0);

        for (int i = 0; i < 12; i++) banco.avancarMes();

        double esperado = 1000.0 * Math.pow(1.05, 12); // ≈ 1795.86
        assertEquals(esperado, c.getContaPoupanca().getSaldo(), 0.01);
    }

    // ── CORRENTE: juros sobre saldo negativo ─────────────────────────

    @Test
    @Order(3)
    @DisplayName("Banco aplica juros de 8% sobre saldo negativo na corrente")
    void bancoAplicaJurosNaCorrenteComSaldoNegativo() throws SaldoInsuficienteException {
        Cliente c = banco.CriarConta("Devedor", "003");
        c.criarContaCorrente();
        c.getContaCorrente().sacar(500.0); // usa cheque especial; saldo = -500

        banco.avancarMes(); // -500 * 0.08 = -40 → saldo = -540

        assertEquals(-540.0, c.getContaCorrente().getSaldo(), 0.001);
    }

    @Test
    @Order(4)
    @DisplayName("Juros sobre saldo negativo são compostos em meses consecutivos")
    void jurosSaldoNegativoSaoCompostos() throws SaldoInsuficienteException {
        Cliente c = banco.CriarConta("Devedor2", "004");
        c.criarContaCorrente();
        c.getContaCorrente().sacar(100.0); // saldo = -100

        banco.avancarMes(); // -100 - 8   = -108
        banco.avancarMes(); // -108 - 8.64 = -116.64

        assertEquals(-116.64, c.getContaCorrente().getSaldo(), 0.01);
    }

    @Test
    @Order(5)
    @DisplayName("Conta corrente com saldo positivo não sofre alteração no avanço de mês")
    void correnteComSaldoPositivoNaoAlteraNoPasarMes() {
        Cliente c = banco.CriarConta("Positivo", "005");
        c.criarContaCorrente();
        c.getContaCorrente().depositar(1000.0);

        banco.avancarMes();

        assertEquals(1000.0, c.getContaCorrente().getSaldo(), 0.001);
    }

    // ── NULL SAFE: clientes sem conta ────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("avancarMes não lança exceção para cliente sem nenhuma conta")
    void avancarMesNaoFalhaSemContas() {
        banco.CriarConta("SemConta", "006"); // sem criar conta corrente ou poupança
        assertDoesNotThrow(() -> banco.avancarMes());
    }

    @Test
    @Order(7)
    @DisplayName("avancarMes não falha para cliente só com conta corrente")
    void avancarMesNaoFalhaSoComCorrente() {
        Cliente c = banco.CriarConta("SóCorrente", "007");
        c.criarContaCorrente();
        c.getContaCorrente().depositar(300.0);

        assertDoesNotThrow(() -> banco.avancarMes());
    }

    @Test
    @Order(8)
    @DisplayName("avancarMes não falha para cliente só com conta poupança")
    void avancarMesNaoFalhaSoComPoupanca() {
        Cliente c = banco.CriarConta("SóPoupança", "008");
        c.criarContaPoupanca();
        c.getContaPoupanca().depositar(300.0);

        assertDoesNotThrow(() -> banco.avancarMes());
    }

    // ── MULTI-CLIENTE: todos afetados simultaneamente ────────────────

    @Test
    @Order(9)
    @DisplayName("avancarMes aplica regras em todos os clientes ao mesmo tempo")
    void avancarMesAfetaTodosOsClientesSimultaneamente() throws SaldoInsuficienteException {
        Cliente poupador = banco.CriarConta("Poupador2", "010");
        poupador.criarContaPoupanca();
        poupador.getContaPoupanca().depositar(1000.0);

        Cliente devedor = banco.CriarConta("Devedor3", "011");
        devedor.criarContaCorrente();
        devedor.getContaCorrente().sacar(200.0); // saldo = -200

        banco.avancarMes();

        assertAll("todos os clientes afetados",
                () -> assertEquals(1050.0, poupador.getContaPoupanca().getSaldo(), 0.001,
                        "poupança deve render 5%"),
                () -> assertEquals(-216.0, devedor.getContaCorrente().getSaldo(), 0.001,
                        "corrente negativa deve cobrar 8%")
        );
    }

    @Test
    @Order(10)
    @DisplayName("saldoGeral do banco reflete rendimentos e juros após avanço de mês")
    void saldoGeralRefletiRendimentosEJurosAposAvancoMes() throws SaldoInsuficienteException {
        Cliente c = banco.CriarConta("Misto", "012");
        c.criarContaCorrente();
        c.criarContaPoupanca();
        c.getContaCorrente().depositar(1000.0);
        c.getContaPoupanca().depositar(1000.0);

        // Antes: corrente=1000, poupança=1000, total=2000
        banco.avancarMes();
        // Depois: corrente=1000 (sem juros, positivo), poupança=1050, total=2050

        assertEquals(2050.0, banco.saldoGeral(), 0.001);
    }

    // ── INTERAÇÃO: depositar após avançar mês ───────────────────────

    @Test
    @Order(11)
    @DisplayName("Operações após avanço de mês partem do saldo atualizado")
    void operacoesAposAvancoMesPartemDoSaldoAtualizado() throws SaldoInsuficienteException {
        Cliente c = banco.CriarConta("Sequencial", "013");
        c.criarContaPoupanca();
        c.getContaPoupanca().depositar(1000.0);

        banco.avancarMes();                        // saldo = 1050
        c.getContaPoupanca().depositar(50.0);      // saldo = 1100
        c.getContaPoupanca().sacar(100.0);         // saldo = 1000

        assertEquals(1000.0, c.getContaPoupanca().getSaldo(), 0.001);
    }
}