package br.com.hidrometro.model;

import br.com.hidrometro.util.Configuracao;
import java.util.Random;

public class RedeHidraulica {
    private double vazaoAtual;
    private double pressaoAtual; // NOVO: Atributo para pressão
    private boolean comAr;
    private boolean comAgua;
    private final Random random = new Random();
    private final Configuracao config;

    public RedeHidraulica(Configuracao config) {
        this.config = config;
        this.comAgua = true; //Começa com água
        this.pressaoAtual = config.getDouble("pressao.media"); // Inicializa a pressão
    }

    public void atualizarEstado() {
        //Simula falta de água
        if (random.nextDouble() < config.getDouble("probabilidade.falta.de.agua")) {
            comAgua = !comAgua; //Inverte o estado da água
            System.out.println(comAgua ? "AVISO: O fornecimento de água foi RESTABELECIDO." : "AVISO: Ocorreu uma FALTA DE ÁGUA.");
        }

        if (!comAgua) {
            vazaoAtual = 0;
            pressaoAtual = 0; //Se não tem água, não tem pressão
            comAr = false;
            return;
        }

        //Simula presença de ar
        comAr = random.nextDouble() < config.getDouble("probabilidade.presenca.de.ar");

        //Simula variação na vazão
        double vazaoMedia = config.getDouble("vazao.media");
        //Variação de até 30% da média, para mais ou para menos
        double variacaoVazao = (random.nextDouble() - 0.5) * (vazaoMedia * 0.3);
        this.vazaoAtual = Math.max(0, Math.min(100, vazaoMedia + variacaoVazao));

        //Simula variação na pressão
        double pressaoMedia = config.getDouble("pressao.media");
        double variacaoPressao = (random.nextDouble() - 0.5) * (pressaoMedia * 0.2); //Variação de 20%
        this.pressaoAtual = Math.max(0, pressaoMedia + variacaoPressao);
    }

    public double getVazaoAtual() {
        return vazaoAtual;
    }

    public double getPressaoAtual() { //Getter para pressão
        return pressaoAtual;
    }

    public boolean temAr() {
        return comAr;
    }

    public boolean temAgua() {
        return comAgua;
    }
}