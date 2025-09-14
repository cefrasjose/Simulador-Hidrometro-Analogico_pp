package br.com.hidrometro.view;

import br.com.hidrometro.util.Configuracao;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Display {

    private final String pathSaidaCondicional; // caminho para a pasta "Medições_MATRICULA"

    public Display(Configuracao config) {
        //constroi o nome do diretorio de saída com base na matricula
        String matricula = config.getString("matricula.suap");
        this.pathSaidaCondicional = "Medições_" + matricula;

        try {
            Files.createDirectories(Paths.get(this.pathSaidaCondicional));
        } catch (IOException e) {
            System.err.println("Erro ao criar diretório de medições.");
            e.printStackTrace();
        }
    }

    public BufferedImage capturarTela(JPanel panel) {
        final BufferedImage[] imageHolder = {null};
        try {
            SwingUtilities.invokeAndWait(() -> {
                BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB); //mudei para RGB para JPEG
                Graphics2D g2d = image.createGraphics();
                panel.paint(g2d);
                g2d.dispose();
                imageHolder[0] = image;
            });
        } catch (Exception e) {
            System.err.println("Erro ao capturar a tela do painel: " + e.getMessage());
        }
        return imageHolder[0];
    }

    /*
     * Salva a imagem da medicao quando um metro cubico eh completado.
     * O nome do arquivo eh formatado como "dd.jpeg" e sobrescreve os antigos apos 99.
     * @param imagem A imagem a ser salva.
     * @param metroCubico O numero do metro cubico atual.
     */

    public void salvarImagemMetroCubico(BufferedImage imagem, int metroCubico) {
        if (imagem == null) {
            System.err.println("Não foi possível salvar imagem nula.");
            return;
        }
        try {
            //logica para nome do arquivo (01 a 99)
            int numeroArquivo = (metroCubico - 1) % 99 + 1;
            String nomeArquivo = String.format("%02d.jpeg", numeroArquivo);

            File arquivoSaida = new File(pathSaidaCondicional, nomeArquivo);
            ImageIO.write(imagem, "jpeg", arquivoSaida); //salva em JPEG
        } catch (IOException e) {
            System.err.println("Erro ao salvar a imagem da medição.");
            e.printStackTrace();
        }
    }
}