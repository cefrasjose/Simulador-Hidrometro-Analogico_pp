package br.com.hidrometro;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        //Garante que a criacao da GUI e o inicio do simulador ocorram na Thread de Eventos do Swing

        SwingUtilities.invokeLater(() -> {
            Simulador simulador = new Simulador();
            simulador.iniciar();
        });
    }
}