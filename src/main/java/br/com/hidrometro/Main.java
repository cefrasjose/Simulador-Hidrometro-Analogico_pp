package br.com.hidrometro;

public class Main {
    public static void main(String[] args) {
        Simulador simulador = new Simulador();
        simulador.iniciar();

        // Adiciona um gancho de desligamento para parar o simulador graciosamente
        // quando o programa for encerrado (ex: com Ctrl+C no terminal).
        Runtime.getRuntime().addShutdownHook(new Thread(simulador::parar));
    }
}