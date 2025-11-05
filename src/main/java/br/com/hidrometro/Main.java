package br.com.hidrometro;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FacadeSHA facade = FacadeSHA.getInstancia();
            facade.getGerenciador().setVisible(true);
        });

        Thread cliThread = new Thread(ClienteCLI::start);
        cliThread.start();
    }
}
