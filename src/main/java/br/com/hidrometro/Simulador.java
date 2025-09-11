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
    private final Display displayCaptura; //Renomeado para evitar confusão
    private volatile boolean rodando = false;
    private Thread threadSimulacao;
    private HidrometroGUI tela;

    public Simulador() {
        //Caminho do arquivo .properties ajustado para ser lido de resources
        this.config = new Configuracao("config/parametros.properties");
        this.rede = new RedeHidraulica(config);
        this.hidrometro = new Hidrometro();
        this.displayCaptura = new Display(config); //Classe Display agora captura a GUI
    }

    public void iniciar() {
        if (rodando) return;
        rodando = true;
        tela = new HidrometroGUI();
        SwingUtilities.invokeLater(() -> {
            tela.setVisible(true);
        });
        threadSimulacao = new Thread(this::loopSimulacao);
        threadSimulacao.start();
        System.out.println("Simulador iniciado. Pressione Ctrl+C para parar.");
    }

    public void parar() {
        rodando = false;
        try {
            if (threadSimulacao != null) { //Adicionado verificação para evitar NullPointerException
                threadSimulacao.join();
            }
            SwingUtilities.invokeLater(() -> { //Garante que a GUI seja fechada na EDT
                if (tela != null) { //Adicionado verificação
                    tela.dispose();
                }
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Simulador parado.");
    }

    private void loopSimulacao() {
        int intervaloSeg = config.getInt("intervalo.geracao.imagem.segundos");
        double fatorAr = config.getDouble("fator.consumo.com.ar");

        while (rodando) {
            rede.atualizarEstado();
            hidrometro.registrarConsumo(rede.getVazaoAtual(), intervaloSeg, rede.temAr(), fatorAr);

            var status = "NORMAL";
            if (!rede.temAgua()) {
                status = "SEM FLUXO";
            } else if (rede.temAr()) {
                status = "AR NA TUBULAÇÃO";
            }

            //Envia também a pressão para a GUI
            tela.atualizarDados(hidrometro.getVolumeConsumidoM3(), rede.getVazaoAtual(), rede.getPressaoAtual(), status);

            //Captura a tela da GUI e salva
            try {
                BufferedImage screenshot = displayCaptura.capturarTela(tela.getMedidorPanel());
                displayCaptura.salvarImagem(screenshot);
            } catch (Exception e) {
                System.err.println("Erro ao capturar ou salvar imagem da GUI: " + e.getMessage());
            }

            System.out.printf("Leitura: %.2f m³ | Vazão: %.2f m³/h | Pressão: %.1f bar | Status: %s\n",
                    hidrometro.getVolumeConsumidoM3(),
                    rede.getVazaoAtual(),
                    rede.getPressaoAtual(), //Imprime a pressão no console
                    status
            );

            try {
                Thread.sleep(intervaloSeg * 1000L);
            } catch (InterruptedException e) {
                rodando = false;
                Thread.currentThread().interrupt();
            }
        }
    }
}