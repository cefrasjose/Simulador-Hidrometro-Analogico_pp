package br.com.hidrometro;

import br.com.hidrometro.model.Hidrometro;
import br.com.hidrometro.model.RedeHidraulica;
import br.com.hidrometro.util.Configuracao;
import br.com.hidrometro.view.Display;
import br.com.hidrometro.view.HidrometroGUI;

import javax.swing.*;

public class Simulador {
    private final Configuracao config;
    private final RedeHidraulica rede;
    private final Hidrometro hidrometro;
    private final Display display;
    private volatile boolean rodando = false;
    private Thread threadSimulacao;
    private HidrometroGUI tela;

    public Simulador() {
        this.config = new Configuracao("config/parametros.properties");
        this.rede = new RedeHidraulica(config);
        this.hidrometro = new Hidrometro();
        this.display = new Display(config);
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
            threadSimulacao.join(); // Espera a thread terminar
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Simulador parado.");
    }

    private void loopSimulacao() {
        int intervaloSeg = config.getInt("intervalo.geracao.imagem.segundos");
        double fatorAr = config.getDouble("fator.consumo.com.ar");

        while (rodando) {
            //1.Atualiza o estado da rede (vazão, ar, falta de água)
            rede.atualizarEstado();

            //2.Registra o consumo no hidrômetro
            hidrometro.registrarConsumo(rede.getVazaoAtual(), intervaloSeg, rede.temAr(), fatorAr);
            var status = !rede.temAgua() ? "SEM ÁGUA" : (rede.temAr() ? "COM AR" : "NORMAL");

            //3.Gera a imagem do display com os novos dados
            tela.atualizarDados(hidrometro.getVolumeConsumidoM3(), rede.getVazaoAtual(), status);
            display.gerarImagem(hidrometro.getVolumeConsumidoM3(), rede.temAgua(), rede.temAr());

            //4.Imprime log no console
            System.out.printf("Leitura: %.2f m³ | Vazão: %.2f m³/h | Status: %s\n",
                    hidrometro.getVolumeConsumidoM3(),
                    rede.getVazaoAtual(),
                    status
            );

            //5.Aguarda o próximo ciclo
            try {
                Thread.sleep(intervaloSeg * 1000L);
            } catch (InterruptedException e) {
                rodando = false;
                Thread.currentThread().interrupt();
            }
        }
    }
}