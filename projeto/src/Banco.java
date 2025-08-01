import java.util.ArrayList;
import java.util.List;

public class Banco {
    List<Cliente> clienteList;

    public Banco(){
        this.clienteList = new ArrayList<>();
    }

    public Cliente CriarConta(String nome, String CPF){
        Cliente novo = new Cliente(nome, CPF);
        clienteList.add(novo);
        return novo;
    }

    public void avancarMes(){
        for(Cliente c : clienteList){
            if(c.getContaCorrente() != null){
                c.getContaCorrente().passarMes();
            }
            if(c.getContaPoupanca() != null){
                c.getContaPoupanca().passarMes();
            }
        }
    }

    public Cliente buscarClienteId(int id){
        return clienteList.stream().filter(c -> c.getIdCliente() == id).findFirst().orElse(null);
    }

    public Cliente buscarClienteNome(String nome){
        return clienteList.stream().filter(c -> c.getNome().equalsIgnoreCase(nome)).findFirst().orElse(null);
    }

    public void exibirClientes(){
        for(Cliente c : clienteList) System.out.println(c);
    }

    public Double saldoGeral(){
        return clienteList.stream().mapToDouble(c -> (c.getContaPoupanca() != null ? c.getContaPoupanca().saldo : 0) + (c.getContaCorrente() != null ? c.getContaCorrente().saldo : 0) ).sum();
    }


}
