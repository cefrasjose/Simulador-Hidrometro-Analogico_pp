package br.com.hidrometro.model;

import br.com.hidrometro.util.Configuracao;
import java.util.Random;

public class RedeHidraulica {
    private double vazaoAtual;
    private double pressaoAtual;
    private boolean comAr;
    private boolean comAgua;
    private final Random random = new Random();
    private final Configuracao config;

    //azao media agora eh um atributo mutavel
    private double vazaoMediaAtual;

    public RedeHidraulica(Configuracao config) {
        this.config = config;
        this.comAgua = true;
        this.vazaoMediaAtual = config.getDouble("vazao.media"); //inicializa com valor do arquivo
        this.pressaoAtual = config.getDouble("pressao.media");
    }

    //metodos sincronizados para alterar a vazao media com seguranca
    public synchronized void aumentarVazaoMedia(double incremento) {
        this.vazaoMediaAtual += incremento;
        System.out.printf(">> Vazão Média ajustada para: %.2f m³/h\n", this.vazaoMediaAtual);
    }

    public synchronized void diminuirVazaoMedia(double incremento) {
        this.vazaoMediaAtual = Math.max(0, this.vazaoMediaAtual - incremento); //nao permite vazao negativa
        System.out.printf(">> Vazão Média ajustada para: %.2f m³/h\n", this.vazaoMediaAtual);
    }

    public synchronized double getVazaoMediaAtual() {
        return this.vazaoMediaAtual;
    }

    public void atualizarEstado() {
        if (random.nextDouble() < config.getDouble("probabilidade.falta.de.agua")) {
            comAgua = !comAgua;
            System.out.println(comAgua ? "AVISO: O fornecimento de água foi RESTABELECIDO." : "AVISO: Ocorreu uma FALTA DE ÁGUA.");
        }

        if (!comAgua) {
            vazaoAtual = 0;
            pressaoAtual = 0;
            comAr = false;
            return;
        }

        comAr = random.nextDouble() < config.getDouble("probabilidade.presenca.de.ar");

        //simula variacao na vazao usando o atributo vazaoMediaAtual
        double variacaoVazao = (random.nextDouble() - 0.5) * (this.vazaoMediaAtual * 0.3);
        this.vazaoAtual = Math.max(0, this.vazaoMediaAtual + variacaoVazao);

        double pressaoMedia = config.getDouble("pressao.media");
        double variacaoPressao = (random.nextDouble() - 0.5) * (pressaoMedia * 0.2);
        this.pressaoAtual = Math.max(0, pressaoMedia + variacaoPressao);
    }

    public double getVazaoAtual() {
        return vazaoAtual;
    }

    public double getPressaoAtual() {
        return pressaoAtual;
    }

    public boolean temAr() {
        return comAr;
    }

    public boolean temAgua() {
        return comAgua;
    }
}