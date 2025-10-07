package br.com.hidrometro;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        // garante a criacao da GUI e o inicio do simulador ocorram na Thread de Eventos do Swing

        SwingUtilities.invokeLater(() -> {
            GerenciadorSimuladores gerenciador = new GerenciadorSimuladores();
            gerenciador.setVisible(true);
        });
    }
}