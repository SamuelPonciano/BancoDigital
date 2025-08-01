public class Cliente implements Comparable<Cliente>{
    private static int id = 1;

    private final String nome;
    private final String CPF;
    private final int IdCliente;
    private ContaCorrente contaCorrente;
    private ContaPoupanca contaPoupanca;

    public Cliente(String nome, String CPF){
        this.nome = nome;
        this.CPF = CPF;
        this.IdCliente = id++;
    }

    public String getNome() {
        return nome;
    }

    public String getCPF() {
        return CPF;
    }

    public int getIdCliente() {
        return IdCliente;
    }

    public ContaCorrente getContaCorrente() {
        return contaCorrente;
    }

    public ContaPoupanca getContaPoupanca() {
        return contaPoupanca;
    }

    public void criarContaCorrente(){
        if(contaCorrente != null){
            throw new IllegalStateException("Cliente já possui Conta corrente");
        }
        contaCorrente = new ContaCorrente();
    }

    public void criarContaPoupanca(){
        if(contaPoupanca != null){
            throw new IllegalStateException("Cliente já possui Conta corrente");
        }
        contaPoupanca = new ContaPoupanca();
    }

    @Override
    public int compareTo(Cliente cliente) {
        return Integer.compare(this.IdCliente, cliente.getIdCliente());
    }

    @Override
    public String toString() {
        String cc = (contaCorrente != null) ? "Criada" : "Não criada";
        String cp = (contaPoupanca != null) ? "Criada" : "Não criada";

        return "ID: " + IdCliente +
                " | Nome: " + nome +
                " | CPF: " + CPF +
                " | Corrente: " + cc +
                " | Poupança: " + cp;
    }

}
