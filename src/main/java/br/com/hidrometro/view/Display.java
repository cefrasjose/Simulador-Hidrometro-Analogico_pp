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

public class Display { //Classe para CAPTURAR e SALVAR a imagem da GUI

    private final String pathSaida;
    private final String formato;
    private final String prefixo;
    private int contadorImagens = 0;

    public Display(Configuracao config) {
        this.pathSaida = config.getString("path.saida.imagens");
        this.formato = config.getString("formato.imagem");
        this.prefixo = config.getString("prefixo.nome.imagem");

        //Garante que o diretório de saída exista
        try {
            Files.createDirectories(Paths.get(this.pathSaida));
        } catch (IOException e) {
            System.err.println("Erro ao criar diretório de saída de imagens.");
            e.printStackTrace();
        }
    }

    /**
     * Captura a imagem de um componente Swing (MedidorPanel).
     * @param panel O JPanel a ser capturado.
     * @return Um BufferedImage contendo a renderização do painel.
     */
    public BufferedImage capturarTela(JPanel panel) {
        //Garante que a captura ocorra na EDT
        final BufferedImage[] imageHolder = {null};
        try {
            SwingUtilities.invokeAndWait(() -> {
                BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = image.createGraphics();
                panel.paint(g2d); //Pinta o painel no BufferedImage
                g2d.dispose();
                imageHolder[0] = image;
            });
        } catch (Exception e) {
            System.err.println("Erro ao capturar a tela do painel: " + e.getMessage());
        }
        return imageHolder[0];
    }

    /**
     * Salva uma BufferedImage em um arquivo.
     * @param imagem A imagem a ser salva.
     */
    public void salvarImagem(BufferedImage imagem) {
        if (imagem == null) {
            System.err.println("Não foi possível salvar imagem nula.");
            return;
        }
        try {
            String nomeArquivo = String.format("%s%05d.%s", prefixo, ++contadorImagens, formato);
            File arquivoSaida = new File(pathSaida, nomeArquivo);
            ImageIO.write(imagem, formato, arquivoSaida);
        } catch (IOException e) {
            System.err.println("Erro ao salvar a imagem do display.");
            e.printStackTrace();
        }
    }
}