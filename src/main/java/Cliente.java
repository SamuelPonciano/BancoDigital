/**
 * Representa um cliente do banco, podendo possuir conta corrente e/ou poupança.
 *
 * Refatorações aplicadas:
 *  - Mensagem de erro ao duplicar conta poupança corrigida
 *  - toString() documenta estados nulos de forma explícita
 *  - Nomes de campo em camelCase (IdCliente → idCliente)
 *  - compareTo() mantido e documentado
 */
public class Cliente implements Comparable<Cliente> {

    private static final String MSG_CONTA_CORRENTE_DUPLICADA  = "Cliente já possui conta corrente";
    private static final String MSG_CONTA_POUPANCA_DUPLICADA  = "Cliente já possui conta poupança";

    private static int proximoId = 1;

    private final String nome;
    private final String cpf;
    private final int    idCliente;
    private ContaCorrente contaCorrente;
    private ContaPoupanca contaPoupanca;

    public Cliente(String nome, String cpf) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do cliente não pode ser vazio");
        }
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF do cliente não pode ser vazio");
        }
        this.nome      = nome;
        this.cpf       = cpf;
        this.idCliente = proximoId++;
    }

    // ── Getters ──────────────────────────────────────────────────────

    public String        getNome()          { return nome;          }
    public String        getCPF()           { return cpf;           }
    public int           getIdCliente()     { return idCliente;     }
    public ContaCorrente getContaCorrente() { return contaCorrente; }
    public ContaPoupanca getContaPoupanca() { return contaPoupanca; }

    /** Reseta o contador de IDs (uso exclusivo em testes). */
    public static void resetId() { proximoId = 1; }

    // ── Abertura de contas ────────────────────────────────────────────

    public void criarContaCorrente() {
        if (contaCorrente != null) {
            throw new IllegalStateException(MSG_CONTA_CORRENTE_DUPLICADA);
        }
        contaCorrente = new ContaCorrente();
    }

    public void criarContaPoupanca() {
        if (contaPoupanca != null) {
            throw new IllegalStateException(MSG_CONTA_POUPANCA_DUPLICADA);
        }
        contaPoupanca = new ContaPoupanca();
    }

    // ── Comparable ───────────────────────────────────────────────────

    /** Ordena clientes por ID de cadastro (ordem de criação). */
    @Override
    public int compareTo(Cliente outro) {
        return Integer.compare(this.idCliente, outro.idCliente);
    }

    // ── Representação ─────────────────────────────────────────────────

    @Override
    public String toString() {
        String statusCorrente = contaCorrente != null ? "Criada" : "Não criada";
        String statusPoupanca = contaPoupanca != null ? "Criada" : "Não criada";
        return String.format(
                "ID: %d | Nome: %s | CPF: %s | Corrente: %s | Poupança: %s",
                idCliente, nome, cpf, statusCorrente, statusPoupanca);
    }
}
