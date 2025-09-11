package br.com.hidrometro;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        //Criar e iniciar o simulador na Event Dispatch Thread (EDT) para garantir a inicialização correta da GUI
        SwingUtilities.invokeLater(() -> {
            Simulador simulador = new Simulador();
            simulador.iniciar();
        });
    }
}