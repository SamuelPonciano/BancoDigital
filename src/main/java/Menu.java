/**
 * Fornece os textos de menu da interface de linha de comando.
 *
 * Refatoração aplicada:
 *  - Métodos retornam String em vez de chamar System.out.println diretamente,
 *    tornando a classe testável e desacoplada da saída padrão.
 */
public class Menu {

    public String menuPrincipal() {
        return  "1- Criar Cliente\n" +
                "2- Acessar o cliente\n" +
                "3- Ver todos os Clientes\n" +
                "4- Ver Saldo geral do Banco\n" +
                "5- Avançar o mês\n" +
                "0- Sair";
    }

    public String menuCliente() {
        return  "1- Criar Conta corrente\n" +
                "2- Criar Conta poupança\n" +
                "3- Entrar em Conta corrente\n" +
                "4- Entrar em Conta poupança\n" +
                "0- Voltar";
    }

    public String menuConta() {
        return  "1- Sacar\n" +
                "2- Depositar\n" +
                "3- Transferir\n" +
                "4- Ver extrato\n" +
                "0- Voltar";
    }

    public String menuPesquisa() {
        return  "1- Pesquisar por ID\n" +
                "2- Pesquisar por nome";
    }
}
