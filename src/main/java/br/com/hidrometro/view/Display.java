package br.com.hidrometro.view;

import br.com.hidrometro.util.Configuracao;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Display {
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

    public void gerarImagem(double volume, boolean temAgua, boolean temAr) {
        int width = 400;
        int height = 200;

        BufferedImage imagem = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = imagem.createGraphics();

        //Configurações de renderização para melhor qualidade
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        //Fundo
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, 0, width, height);

        //Desenha os dígitos do medidor
        String volumeTexto = String.format("%09.3f", volume).replace(",", "");
        String parteInteira = volumeTexto.substring(0, 5);
        String parteDecimal = volumeTexto.substring(5);

        g2d.setFont(new Font("Courier New", Font.BOLD, 80));

        //Parte inteira (em branco)
        g2d.setColor(Color.WHITE);
        g2d.drawString(parteInteira, 20, 100);

        //Parte decimal (em vermelho)
        g2d.setColor(Color.RED);
        g2d.drawString(parteDecimal, 240, 100);

        //Unidade
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawString("m³", 300, 130);

        //Desenha status (Falta de água, Ar)
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        if (!temAgua) {
            g2d.setColor(Color.YELLOW);
            g2d.drawString("SEM FLUXO", 20, 180);
        } else if (temAr) {
            g2d.setColor(Color.CYAN);
            g2d.drawString("AR NA TUBULAÇÃO", 180, 180);
        }

        g2d.dispose();

        //Salva a imagem em um arquivo
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