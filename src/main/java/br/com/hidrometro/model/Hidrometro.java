package br.com.hidrometro.model;

public class Hidrometro {
    private double volumeConsumidoM3;

    public Hidrometro() {
        this.volumeConsumidoM3 = 0.0;
    }

    public void registrarConsumo(double vazaoM3porHora, int deltaTimeSeg, boolean comAr, double fatorAr) {
        // converte a vazao de m³/h para m³/s
        double vazaoM3porSegundo = vazaoM3porHora / 3600.0;
        double volumeAdicional = vazaoM3porSegundo * deltaTimeSeg;

        // se houver ar, o consumo registrado eh maior
        if (comAr) {
            volumeAdicional *= fatorAr;
        }

        this.volumeConsumidoM3 += volumeAdicional;
    }

    public double getVolumeConsumidoM3() {
        return volumeConsumidoM3;
    }
}