package br.com.hidrometro;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorSimuladores extends JFrame {

    private static final int LIMITE_SIMULADORES = 5;
    private final List<Simulador> simuladores = new ArrayList<>();
    private final List<Integer> idsDisponiveis = new ArrayList<>();

    private final JButton btnNovoSimulador = new JButton("Novo Simulador");
    private final JButton btnFecharTodos = new JButton("Fechar Todos");
    private final JLabel lblStatus = new JLabel();

    public GerenciadorSimuladores() {
        super("Gerenciador de Simuladores de Hidrômetro");

        for (int i = 1; i <= LIMITE_SIMULADORES; i++) idsDisponiveis.add(i);

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

        atualizarStatus();
    }

    public synchronized Simulador criarSimulador() {
        if (idsDisponiveis.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Limite máximo de simuladores atingido.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int id = idsDisponiveis.remove(0);
        Simulador sim = new Simulador(id);
        registrarSimulador(id, sim);

        sim.iniciar();
        return sim;
    }

    public synchronized void fecharTodosSimuladores() {
        new ArrayList<>(simuladores).forEach(Simulador::parar);
        simuladores.clear();
        idsDisponiveis.clear();
        for (int i = 1; i <= LIMITE_SIMULADORES; i++) idsDisponiveis.add(i);
        atualizarStatus();
    }

    public synchronized Simulador getSimuladorPorId(int id) {
        for (Simulador s : simuladores) {
            if (s.getIdHidrometro() == id) return s;
        }
        return null;
    }

    public synchronized int getProximoIdDisponivel() {
        if (idsDisponiveis.isEmpty()) return -1;
        return idsDisponiveis.get(0);
    }

    public synchronized void registrarSimulador(int id, Simulador simulador) {
        simuladores.add(simulador);
        idsDisponiveis.remove((Integer) id);

        simulador.setOnClose(() -> {
            removerSimulador(id);
            atualizarStatus();
        });

        atualizarStatus();
    }

    public synchronized void removerSimulador(int id) {
        simuladores.removeIf(s -> s.getIdHidrometro() == id);
        if (!idsDisponiveis.contains(id)) idsDisponiveis.add(id);
        idsDisponiveis.sort(Integer::compareTo);
        atualizarStatus();
    }

    private void atualizarStatus() {
        lblStatus.setText("Simuladores ativos: " + simuladores.size() + " / " + LIMITE_SIMULADORES);
    }
}
