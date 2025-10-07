package br.com.hidrometro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorSimuladores extends JFrame {

    private static final int LIMITE_SIMULADORES = 5;
    private final List<Simulador> simuladores = new ArrayList<>();
    private final JButton btnNovoSimulador = new JButton("Novo Simulador");
    private final JButton btnFecharTodos = new JButton("Fechar Todos");
    private final JLabel lblStatus = new JLabel("Simuladores ativos: 0 / " + LIMITE_SIMULADORES);

    public GerenciadorSimuladores() {
        super("Gerenciador de Simuladores de Hidrômetro");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel painel = new JPanel(new FlowLayout());
        painel.add(btnNovoSimulador);
        painel.add(btnFecharTodos);
        add(painel, BorderLayout.CENTER);
        add(lblStatus, BorderLayout.SOUTH);

        btnNovoSimulador.addActionListener(e -> criarSimulador());
        btnFecharTodos.addActionListener(e -> fecharTodosSimuladores());
    }

    private void criarSimulador() {
        if (simuladores.size() >= LIMITE_SIMULADORES) {
            JOptionPane.showMessageDialog(this,
                    "Limite máximo de " + LIMITE_SIMULADORES + " simuladores atingido.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Simulador simulador = new Simulador();
        simuladores.add(simulador);

        // intercepta a criação da janela GUI
        SwingUtilities.invokeLater(() -> {
            simulador.iniciar();

            // espera a janela aparecer e adiciona listener
            Timer t = new Timer(500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (Frame f : Frame.getFrames()) {
                        if (f instanceof JFrame
                                && f.isVisible()
                                && f.getTitle().contains("Hidrômetro")) {
                            f.addWindowListener(new WindowAdapter() {
                                @Override
                                public void windowClosed(WindowEvent we) {
                                    simulador.parar();
                                    simuladores.remove(simulador);
                                    atualizarStatus();
                                }
                            });
                            ((Timer) e.getSource()).stop();
                            break;
                        }
                    }
                }
            });
            t.setRepeats(true);
            t.start();
        });

        atualizarStatus();
    }

    private void fecharTodosSimuladores() {
        for (Simulador s : new ArrayList<>(simuladores)) {
            s.parar();
        }

        for (Frame f : Frame.getFrames()) {
            if (f instanceof JFrame
                    && f.isVisible()
                    && f != this
                    && f.getTitle().contains("Hidrômetro")) {
                f.dispose();
            }
        }

        simuladores.clear();
        atualizarStatus();
    }

    private void atualizarStatus() {
        lblStatus.setText("Simuladores ativos: " + simuladores.size() + " / " + LIMITE_SIMULADORES);
    }
}
