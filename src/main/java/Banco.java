import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Representa o banco, responsável por gerenciar clientes e suas contas.
 */
public class Banco {

    private final List<Cliente> clientes;

    public Banco() {
        this.clientes = new ArrayList<>();
    }

    // ── Gestão de clientes ────────────────────────────────────────────

    /**
     * Cadastra um novo cliente no banco e o retorna.
     * Nome canônico pós-refatoração.
     */
    public Cliente criarCliente(String nome, String cpf) {
        Cliente novo = new Cliente(nome, cpf);
        clientes.add(novo);
        return novo;
    }

    /**
     * Alias mantido para compatibilidade com testes de integração e Main.
     * Delega para criarCliente().
     */
    public Cliente CriarConta(String nome, String cpf) {
        return criarCliente(nome, cpf);
    }

    // ── Busca ─────────────────────────────────────────────────────────

    /** Retorna Optional — usado nos testes unitários (SistemaBancarioTest). */
    public Optional<Cliente> buscarClientePorId(int id) {
        return clientes.stream()
                .filter(c -> c.getIdCliente() == id)
                .findFirst();
    }

    /** Retorna Optional — usado nos testes unitários (SistemaBancarioTest). */
    public Optional<Cliente> buscarClientePorNome(String nome) {
        return clientes.stream()
                .filter(c -> c.getNome().equalsIgnoreCase(nome))
                .findFirst();
    }

    /**
     * Retorna o cliente diretamente ou null — usado nos testes IT e no Main.
     */
    public Cliente buscarClienteId(int id) {
        return buscarClientePorId(id).orElse(null);
    }

    /**
     * Retorna o cliente diretamente ou null — usado nos testes IT e no Main.
     */
    public Cliente buscarClienteNome(String nome) {
        return buscarClientePorNome(nome).orElse(null);
    }

    // ── Getters ───────────────────────────────────────────────────────

    /** Lista imutável — nome canônico pós-refatoração. */
    public List<Cliente> getClientes() {
        return List.copyOf(clientes);
    }

    /**
     * Alias mantido para compatibilidade com testes de integração
     * (FluxoCadastroClienteIT chama getClienteList()).
     */
    public List<Cliente> getClienteList() {
        return getClientes();
    }

    // ── Operações do banco ────────────────────────────────────────────

    /** Avança um mês: aplica juros/rendimentos em todas as contas. */
    public void avancarMes() {
        for (Cliente cliente : clientes) {
            if (cliente.getContaCorrente() != null) {
                cliente.getContaCorrente().passarMes();
            }
            if (cliente.getContaPoupanca() != null) {
                cliente.getContaPoupanca().passarMes();
            }
        }
    }

    /** Soma os saldos de todas as contas de todos os clientes. */
    public double saldoGeral() {
        return clientes.stream()
                .mapToDouble(c -> {
                    double sc = c.getContaCorrente() != null ? c.getContaCorrente().getSaldo() : 0;
                    double sp = c.getContaPoupanca() != null ? c.getContaPoupanca().getSaldo() : 0;
                    return sc + sp;
                })
                .sum();
    }

    /** Retorna a listagem de clientes formatada (testável). */
    public String listarClientes() {
        if (clientes.isEmpty()) {
            return "Nenhum cliente cadastrado.";
        }
        StringBuilder sb = new StringBuilder();
        for (Cliente c : clientes) {
            sb.append(c).append("\n");
        }
        return sb.toString().stripTrailing();
    }

    /**
     * Exibe a lista de clientes no console — chamado pelo Main.
     */
    public void exibirClientes() {
        System.out.println(listarClientes());
    }
}
