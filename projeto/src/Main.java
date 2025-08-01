import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SaldoInsuficienteException {
        Banco banco = new Banco();
        Menu menu = new Menu();
        int op;
        Scanner scanner = new Scanner(System.in);

        do {
            menu.menuPrincipal();
            op = scanner.nextInt();
            scanner.nextLine(); // limpa o \n pendente

            switch (op) {
                case 1:
                    System.out.println("Digite o nome:");
                    String nome = scanner.nextLine();
                    System.out.println("Digite o CPF:");
                    String cpf = scanner.nextLine();

                    Cliente novo = banco.CriarConta(nome, cpf);
                    System.out.println("Cliente Criado!");
                    System.out.println("Nome: " + novo.getNome());
                    System.out.println("CPF: " + novo.getCPF());
                    System.out.println("ID: " + novo.getIdCliente());
                    break;

                case 2:
                    int op2;
                    Cliente clienteSelecionado = null;

                    do {
                        menu.menuPesquisa();
                        op2 = scanner.nextInt();
                        scanner.nextLine(); // limpa buffer

                        switch (op2) {
                            case 1:
                                System.out.print("Digite o ID: ");
                                int idBusca = scanner.nextInt();
                                scanner.nextLine();
                                clienteSelecionado = banco.buscarClienteId(idBusca);
                                break;
                            case 2:
                                System.out.print("Digite o nome: ");
                                String name = scanner.nextLine();
                                clienteSelecionado = banco.buscarClienteNome(name);
                                break;
                            case 0:
                                System.out.println("Voltando...");
                                break;
                            default:
                                System.out.println("Opção Inválida.");
                        }

                        if (clienteSelecionado != null) {
                            System.out.println("Cliente encontrado!");
                            int op3;
                            do {
                                menu.menuCliente();
                                op3 = scanner.nextInt();
                                scanner.nextLine();

                                switch (op3) {
                                    case 1:
                                        clienteSelecionado.criarContaCorrente();
                                        break;
                                    case 2:
                                        clienteSelecionado.criarContaPoupanca();
                                        break;

                                    case 3: // Conta Corrente
                                        if (clienteSelecionado.getContaCorrente() == null) {
                                            System.out.println("O cliente não possui conta corrente.");
                                            break;
                                        }

                                        int op4;
                                        do {
                                            menu.menuConta();
                                            op4 = scanner.nextInt();
                                            scanner.nextLine();

                                            switch (op4) {
                                                case 1 -> {
                                                    System.out.print("Digite o valor que deseja sacar: ");
                                                    double saque = scanner.nextDouble();
                                                    scanner.nextLine();
                                                    clienteSelecionado.getContaCorrente().sacar(saque);
                                                }
                                                case 2 -> {
                                                    System.out.print("Digite o valor do depósito: ");
                                                    double deposito = scanner.nextDouble();
                                                    scanner.nextLine();
                                                    clienteSelecionado.getContaCorrente().depositar(deposito);
                                                }
                                                case 3 -> {
                                                    System.out.print("Digite o ID da conta que deseja transferir: ");
                                                    int idDestino = scanner.nextInt();
                                                    scanner.nextLine();
                                                    Cliente transferencia = banco.buscarClienteId(idDestino);

                                                    System.out.print("Transferir para 1- Corrente ou 2- Poupança: ");
                                                    int conta = scanner.nextInt();
                                                    scanner.nextLine();

                                                    System.out.print("Digite o valor que deseja transferir: ");
                                                    double valor = scanner.nextDouble();
                                                    scanner.nextLine();

                                                    if (conta == 1)
                                                        clienteSelecionado.getContaCorrente().transferir(valor, transferencia.getContaCorrente());
                                                    else if (conta == 2)
                                                        clienteSelecionado.getContaCorrente().transferir(valor, transferencia.getContaPoupanca());
                                                }
                                                case 4 -> clienteSelecionado.getContaCorrente().ImprimirExtrato();
                                                case 0 -> System.out.println("Voltando...");
                                                default -> System.out.println("Opção inválida.");
                                            }
                                        } while (op4 != 0);
                                        break;

                                    case 4: // Conta Poupança
                                        if (clienteSelecionado.getContaPoupanca() == null) {
                                            System.out.println("O cliente não possui conta poupança.");
                                            break;
                                        }

                                        int op5;
                                        do {
                                            menu.menuConta();
                                            op5 = scanner.nextInt();
                                            scanner.nextLine();

                                            switch (op5) {
                                                case 1 -> {
                                                    System.out.print("Digite o valor que deseja sacar: ");
                                                    double saque = scanner.nextDouble();
                                                    scanner.nextLine();
                                                    clienteSelecionado.getContaPoupanca().sacar(saque);
                                                }
                                                case 2 -> {
                                                    System.out.print("Digite o valor do depósito: ");
                                                    double deposito = scanner.nextDouble();
                                                    scanner.nextLine();
                                                    clienteSelecionado.getContaPoupanca().depositar(deposito);
                                                }
                                                case 3 -> {
                                                    System.out.print("Digite o ID da conta que deseja transferir: ");
                                                    int idDestino = scanner.nextInt();
                                                    scanner.nextLine();
                                                    Cliente transferencia = banco.buscarClienteId(idDestino);

                                                    System.out.print("Transferir para 1- Corrente ou 2- Poupança: ");
                                                    int conta = scanner.nextInt();
                                                    scanner.nextLine();

                                                    System.out.print("Digite o valor que deseja transferir: ");
                                                    double valor = scanner.nextDouble();
                                                    scanner.nextLine();

                                                    if (conta == 1)
                                                        clienteSelecionado.getContaPoupanca().transferir(valor, transferencia.getContaCorrente());
                                                    else if (conta == 2)
                                                        clienteSelecionado.getContaPoupanca().transferir(valor, transferencia.getContaPoupanca());
                                                }
                                                case 4 -> clienteSelecionado.getContaPoupanca().ImprimirExtrato();
                                                case 0 -> System.out.println("Voltando...");
                                                default -> System.out.println("Opção inválida.");
                                            }
                                        } while (op5 != 0);
                                        break;
                                }
                            } while (op3 != 0);
                            break; // sai do menu de pesquisa
                        } else if (op2 == 1 || op2 == 2) {
                            System.out.println("Cliente não encontrado.");
                        }

                    } while (op2 != 0);
                    break;

                case 3:
                    banco.exibirClientes();
                    break;

                case 4:
                    double saldoGeral = banco.saldoGeral();
                    System.out.printf("Saldo total do banco: R$ %.2f%n", saldoGeral);
                    break;

                case 5:
                    banco.avancarMes();
                    System.out.println("Mês avançado com sucesso!");
                    break;

                case 0:
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        } while (op != 0);
    }
}
