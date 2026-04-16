import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SistemaBancarioTest {

    private Cliente cliente;
    private Banco banco;

    @BeforeEach
    void setUp() {
        // Reseta o estado antes de cada teste para evitar interferências
        banco = new Banco();
        cliente = banco.CriarConta("João Silva", "12345678900");
    }

    // ================= TESTES DE CLIENTE =================

    @Test
    void testCriarContaCorrenteSucesso() {
        cliente.criarContaCorrente();
        assertNotNull(cliente.getContaCorrente());
    }

    @Test
    void testCriarContaCorrenteDuplicadaDeveLancarExcecao() {
        cliente.criarContaCorrente();
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            cliente.criarContaCorrente();
        });
        assertEquals("Cliente já possui Conta corrente", exception.getMessage());
    }

    // ================= TESTES DE CONTA CORRENTE =================

    @Test
    void testDepositoETransferenciaContaCorrente() throws SaldoInsuficienteException {
        cliente.criarContaCorrente();
        Cliente recebedor = banco.CriarConta("Maria", "00000000000");
        recebedor.criarContaCorrente();

        cliente.getContaCorrente().depositar(1000.0);
        assertEquals(1000.0, cliente.getContaCorrente().getSaldo());

        cliente.getContaCorrente().transferir(300.0, recebedor.getContaCorrente());

        assertEquals(700.0, cliente.getContaCorrente().getSaldo());
        assertEquals(300.0, recebedor.getContaCorrente().getSaldo());
    }

    @Test
    void testSaqueContaCorrenteUsandoChequeEspecial() throws SaldoInsuficienteException {
        cliente.criarContaCorrente();
        cliente.getContaCorrente().depositar(100.0);

        // Saca 500 (100 do saldo + 400 do limite de 500)
        cliente.getContaCorrente().sacar(500.0);

        assertEquals(-400.0, cliente.getContaCorrente().getSaldo());
    }

    @Test
    void testSaqueContaCorrenteAlemDoChequeEspecialDeveLancarExcecao() {
        cliente.criarContaCorrente();
        // Tenta sacar 600 com saldo 0 (limite é 500)
        assertThrows(SaldoInsuficienteException.class, () -> {
            cliente.getContaCorrente().sacar(600.0);
        });
    }

    @Test
    void testPassarMesContaCorrenteComSaldoDevedor() throws SaldoInsuficienteException {
        cliente.criarContaCorrente();
        cliente.getContaCorrente().sacar(100.0); // Saldo fica -100

        cliente.getContaCorrente().passarMes();

        // Juros de 8% sobre 100 = 8. Saldo deve ser -108.
        assertEquals(-108.0, cliente.getContaCorrente().getSaldo());
    }

    // ================= TESTES DE CONTA POUPANÇA =================

    @Test
    void testPassarMesContaPoupancaComRendimento() {
        cliente.criarContaPoupanca();
        cliente.getContaPoupanca().depositar(1000.0);

        cliente.getContaPoupanca().passarMes();

        // Rendimento de 5% sobre 1000 = 50. Saldo deve ser 1050.
        assertEquals(1050.0, cliente.getContaPoupanca().getSaldo());
    }

    // ================= TESTES DO BANCO =================

    @Test
    void testSaldoGeralDoBanco() {
        cliente.criarContaCorrente();
        cliente.criarContaPoupanca();

        Cliente cliente2 = banco.CriarConta("Ana", "111");
        cliente2.criarContaCorrente();

        cliente.getContaCorrente().depositar(500.0);
        cliente.getContaPoupanca().depositar(500.0);
        cliente2.getContaCorrente().depositar(1000.0);

        assertEquals(2000.0, banco.saldoGeral());
    }
}