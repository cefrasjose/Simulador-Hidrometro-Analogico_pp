package br.com.hidrometro;

import java.util.HashMap;
import java.util.Map;

/**
 * Fachada Singleton para controlar o SHA (Simulador de Hidrômetros Automáticos)
 */
public class FacadeSHA {

    private static FacadeSHA instancia;

    private final Map<Integer, Simulador> simuladores = new HashMap<>();
    private final GerenciadorSimuladores gerenciador;

    private int intervaloSimulacao = 5; // segundos
    private boolean gerarImagens = true;

    private FacadeSHA() {
        gerenciador = new GerenciadorSimuladores();
    }

    public static synchronized FacadeSHA getInstancia() {
        if (instancia == null) {
            instancia = new FacadeSHA();
        }
        return instancia;
    }

    public GerenciadorSimuladores getGerenciador() {
        return gerenciador;
    }

    // 1️⃣ Define configurações globais
    public void configSimuladorSHA(int intervaloSegundos) {
        this.intervaloSimulacao = intervaloSegundos;
        System.out.println("[FachadaSHA] Configuração global: intervalo = " + intervaloSegundos + "s");
    }

    // 2️⃣ Cria e registra um novo simulador automaticamente
    public void criaSHA() {
        int idDisponivel = gerenciador.getProximoIdDisponivel();
        if (idDisponivel == -1) {
            System.out.println("[FachadaSHA] Limite máximo de simuladores atingido.");
            return;
        }

        Simulador sim = new Simulador(idDisponivel);
        simuladores.put(idDisponivel, sim);
        gerenciador.registrarSimulador(idDisponivel, sim);

        sim.iniciar();
        System.out.println("[FachadaSHA] Simulador " + idDisponivel + " criado e iniciado.");
    }

    // 3️⃣ Finaliza um simulador específico
    public void finalizaSHA(int id) {
        Simulador sim = simuladores.remove(id);
        if (sim != null) {
            sim.parar();
            gerenciador.removerSimulador(id);
            System.out.println("[FachadaSHA] Simulador " + id + " finalizado.");
        } else {
            System.out.println("[FachadaSHA] Nenhum simulador encontrado com ID " + id);
        }
    }

    // 4️⃣ Modifica a vazão de um simulador específico
    public void modificaVazaoSHA(int id, double novaVazao) {
        Simulador sim = simuladores.get(id);
        if (sim != null) {
            sim.solicitarAumentoVazao(novaVazao);
            System.out.println("[FachadaSHA] Vazão do simulador " + id + " alterada para " + novaVazao);
        } else {
            System.out.println("[FachadaSHA] Simulador " + id + " não encontrado.");
        }
    }

    // 5️⃣ Habilita ou desabilita a geração de imagens para um simulador específico
    public void habilitaGeracaoImagemSHA(int id, boolean habilitar) {
        Simulador sim = simuladores.get(id);
        if (sim != null) {
            sim.setGerarImagens(habilitar);
            System.out.println("[FachadaSHA] Geração de imagens para o simulador " + id + " foi " +
                    (habilitar ? "ativada." : "desativada."));
        } else {
            System.out.println("[FachadaSHA] Simulador " + id + " não encontrado.");
        }
    }

    // 🔚 Encerra todos os simuladores
    public void encerrarSistema() {
        simuladores.values().forEach(Simulador::parar);
        simuladores.clear();
        System.out.println("[FachadaSHA] Todos os simuladores foram encerrados.");
    }
}
