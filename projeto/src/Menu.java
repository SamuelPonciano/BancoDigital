public class Menu {
    public void menuPrincipal(){
        System.out.println("1- Criar Cliente");
        System.out.println("2- Acessar o cliente");
        System.out.println("3- Ver todos os Clientes");
        System.out.println("4- Ver Saldo geral do Banco");
        System.out.println("5- Avançar o mês");
        System.out.println("0- Sair");
    }

    public void menuCliente(){
        System.out.println("1- Criar Conta corrente");
        System.out.println("2- Criar Conta poupança");
        System.out.println("3- Entra em Conta corrente");
        System.out.println("4- Entrar em Conta Poupança");
        System.out.println("0- Voltar");
    }

    public void menuConta(){
        System.out.println("1- Sacar");
        System.out.println("2- Depositar");
        System.out.println("3- Transferir");
        System.out.println("4- Olhar meu extrato");
        System.out.println("0- Voltar");
    }

    public void menuPesquisa(){
        System.out.println("1- Pesquisar por id");
        System.out.println("2- Pesquisar por nome");
    }
}
