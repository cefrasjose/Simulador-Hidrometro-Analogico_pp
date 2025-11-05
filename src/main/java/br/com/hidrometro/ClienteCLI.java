package br.com.hidrometro;

import java.util.Scanner;

public class ClienteCLI {
    public static void start() {
        FacadeSHA facade = FacadeSHA.getInstancia();
        Scanner sc = new Scanner(System.in);

        System.err.println("==== CLIENTE CLI - SISTEMA HIDRÔMETRO ====");

        while (true) {
            System.err.println("\n1) Configurar Simulação");
            System.err.println("2) Criar Novo SHA");
            System.err.println("3) Finalizar SHA");
            System.err.println("4) Modificar Vazão SHA");
            System.err.println("5) Habilitar/Desabilitar Geração de Imagens");
            System.err.println("0) Sair");
            System.err.print("Escolha: ");
            int op = sc.nextInt();

            switch (op) {
                case 1 -> {
                    System.err.print("Intervalo (segundos): ");
                    facade.configSimuladorSHA(sc.nextInt());
                }
                case 2 -> facade.criaSHA();
                case 3 -> {
                    System.err.print("ID do simulador: ");
                    facade.finalizaSHA(sc.nextInt());
                }
                case 4 -> {
                    System.err.print("ID do simulador: ");
                    int id = sc.nextInt();
                    System.err.print("Incremento de vazão: ");
                    facade.modificaVazaoSHA(id, sc.nextDouble());
                }
                case 5 -> {
                    System.err.print("ID do simulador: ");
                    int id = sc.nextInt();
                    System.err.print("1 = habilitar, 0 = desabilitar: ");
                    boolean habilitar = sc.nextInt() == 1;
                    facade.habilitaGeracaoImagemSHA(id, habilitar);
                }
                case 0 -> {
                    System.err.println("Encerrando Cliente CLI...");
                    facade.encerrarSistema();

                    try {
                        Thread.sleep(300); // 300ms
                    } catch (InterruptedException ignored) {}

                    System.err.println("Saindo agora.");
                    sc.close();
                    System.exit(0);
                }
                default -> System.err.println("Opção inválida!");
            }
        }
    }
}
