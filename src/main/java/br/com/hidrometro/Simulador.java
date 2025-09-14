package br.com.hidrometro;

import br.com.hidrometro.model.Hidrometro;
import br.com.hidrometro.model.RedeHidraulica;
import br.com.hidrometro.util.Configuracao;
import br.com.hidrometro.view.Display;
import br.com.hidrometro.view.HidrometroGUI;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class Simulador {
    private final Configuracao config;
    private final RedeHidraulica rede;
    private final Hidrometro hidrometro;
    private final Display displayCaptura;
    private volatile boolean rodando = false;
    private Thread threadSimulacao;
    private HidrometroGUI tela;

    private int ultimoMetroCubicoSalvo = 0;

    public Simulador() {
        this.config = new Configuracao("config/parametros.properties");
        this.rede = new RedeHidraulica(config);
        this.hidrometro = new Hidrometro();
        this.displayCaptura = new Display(config);
    }

    public void iniciar() {
        if (rodando) return;
        rodando = true;
        tela = new HidrometroGUI(this);
        SwingUtilities.invokeLater(() -> tela.setVisible(true));

        threadSimulacao = new Thread(this::loopSimulacao);
        threadSimulacao.start();
        System.out.println("Simulador iniciado. Feche a janela para parar.");
    }

    public void solicitarAumentoVazao(double incremento) {
        rede.aumentarVazaoMedia(incremento);
    }

    public void solicitarDiminuicaoVazao(double incremento) {
        rede.diminuirVazaoMedia(incremento);
    }

    public void parar() {
        rodando = false;
    }

    private void loopSimulacao() {
        //intervalo em segundos (int) e em milissegundos (long)
        int intervaloSeg = config.getInt("intervalo.geracao.imagem.segundos");
        long intervaloMiliseg = intervaloSeg * 1000L;
        double fatorAr = config.getDouble("fator.consumo.com.ar");

        while (rodando) {
            long tempoInicio = System.currentTimeMillis();

            rede.atualizarEstado();

            //passa 'intervaloSeg' (int) para o metodo, como ele espera.
            hidrometro.registrarConsumo(rede.getVazaoAtual(), intervaloSeg, rede.temAr(), fatorAr);

            var status = "NORMAL";
            if (!rede.temAgua()) status = "SEM FLUXO";
            else if (rede.temAr()) status = "AR NA TUBULAÇÃO";

            tela.atualizarDados(
                    hidrometro.getVolumeConsumidoM3(),
                    rede.getVazaoAtual(),
                    rede.getPressaoAtual(),
                    rede.getVazaoMediaAtual(),
                    status
            );

            int metroCubicoAtual = (int) hidrometro.getVolumeConsumidoM3();
            if (metroCubicoAtual > ultimoMetroCubicoSalvo && metroCubicoAtual > 0) {
                ultimoMetroCubicoSalvo = metroCubicoAtual;

                tela.revalidate();
                tela.repaint();

                BufferedImage screenshot = displayCaptura.capturarTela(tela.getMedidorPanel());
                displayCaptura.salvarImagemMetroCubico(screenshot, metroCubicoAtual);
                System.out.println("--- IMAGEM SALVA: Medição de " + metroCubicoAtual + " m³ completada. ---");
            }

            System.out.printf("Leitura: %.3f m³ | Vazão: %.2f m³/h | Pressão: %.1f bar | Status: %s\n",
                    hidrometro.getVolumeConsumidoM3(),
                    rede.getVazaoAtual(),
                    rede.getPressaoAtual(),
                    status
            );

            long tempoDecorrido = System.currentTimeMillis() - tempoInicio;
            long tempoDeEspera = Math.max(0, intervaloMiliseg - tempoDecorrido);

            try {
                Thread.sleep(tempoDeEspera);
            } catch (InterruptedException e) {
                rodando = false;
                Thread.currentThread().interrupt();
            }
        }
    }
}