package br.com.hidrometro.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;

public class HidrometroGUI extends JFrame {

    private BufferedImage hidrometroImage;
    private MedidorPanel medidorPanel;
    private JLabel statusLabel; // Label para mostrar o status

    //Construtor
    public HidrometroGUI() {
        setTitle("Hidrômetro Digital");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);

        carregarImagemHidrometro();

        medidorPanel = new MedidorPanel();
        add(medidorPanel, BorderLayout.CENTER);

        //Painel para o status na parte inferior
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(Color.DARK_GRAY);
        statusLabel = new JLabel("Status: Aguardando dados...");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(Color.WHITE);
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }

    //O MÉTODO-CHAVE PARA A INTEGRAÇÃO
    /**
     * Atualiza os dados exibidos na tela do hidrômetro.
     * Este método é seguro para ser chamado de outras Threads.
     * @param leitura A leitura atual em m³.
     * @param vazao A vazão atual.
     * @param status O status do hidrômetro (ex: "Normal", "Vazamento").
     */
    public void atualizarDados(double leitura, double vazao, String status) {
        // Usa SwingUtilities.invokeLater para garantir que a atualização da GUI
        //aconteça na Thread de Eventos do Swing (EDT), evitando problemas de concorrência.
        SwingUtilities.invokeLater(() -> {
            medidorPanel.setLeitura(leitura);
            medidorPanel.setVazao(vazao);
            statusLabel.setText("Status: " + status);
            medidorPanel.repaint();
        });
    }

    private void carregarImagemHidrometro() {
        // (O código para carregar a imagem continua o mesmo da resposta anterior)
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("hidrometro.png");
            if (is != null) {
                hidrometroImage = ImageIO.read(is);
            } else {
                throw new IOException("Imagem 'hidrometro.png' não encontrada no classpath.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // A classe agora implementa ActionListener para responder ao Timer da animação
    class MedidorPanel extends JPanel implements ActionListener {
        private double leituraAtual = 0.0;
        private double vazaoAtual = 0.0; // Agora armazena a última vazão recebida
        private double anguloPonteiroVazao = 0.0;

        public void setLeitura(double leitura) { this.leituraAtual = leitura; }
        public void setVazao(double vazao) { this.vazaoAtual = vazao; }

        // O novo Timer para a animação
        private Timer animationTimer;

        public MedidorPanel() {
            // Inicializa o Timer para disparar a cada 40ms (aproximadamente 25 frames por segundo)
            // 'this' significa que o próprio MedidorPanel irá tratar o evento do timer
            setBackground(Color.DARK_GRAY);
            animationTimer = new Timer(40, this);
            animationTimer.start(); // Inicia a "engine" de animação
        }

        //Este método é chamado pelo seu SimuladorHidrometro a cada 1 segundo
        public void updateState(double leitura, double vazao) {
            this.leituraAtual = leitura;
            this.vazaoAtual = vazao;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (vazaoAtual > 0) {
                this.anguloPonteiroVazao += vazaoAtual * 0.01;
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (hidrometroImage != null) {
                int x = (getWidth() - hidrometroImage.getWidth()) / 2;
                int y = (getHeight() - hidrometroImage.getHeight()) / 2;
                g2d.drawImage(hidrometroImage, x, y, this);

                //Posição Y (altura)
                int yBase = y + (hidrometroImage.getHeight() / 2) - 20;
                //Posição X (horizontal)
                int xInicial = x + 180;
                //A distância (em pixels)
                int espacoEntreDigitos = 27;
                //Um espaço entre a parte inteira e a decimal
                int espacoExtraDecimal = 0;

                //1.Preparar Strings(3 inteiras, 2 decimais)
                int parteInteira = (int) leituraAtual;
                int parteDecimal = (int) Math.round((leituraAtual - parteInteira) * 100);

                String strInteira = String.format("%03d", parteInteira);
                String strDecimal = String.format("%02d", parteDecimal);
                String strUnidade = " m³";
                g2d.setFont(new Font("Arial", Font.BOLD, 30));

                //2.Desenhar a parte INTEIRA
                g2d.setColor(Color.BLACK);
                int currentX = xInicial;
                for (char digito : strInteira.toCharArray()) {
                    String digitoStr = String.valueOf(digito);
                    g2d.drawString(digitoStr, currentX, yBase);
                    //move a posição X para o próximo dígito
                    currentX += espacoEntreDigitos;
                }

                //3.Desenhar a parte DECIMAL
                g2d.setColor(Color.RED);
                currentX += espacoExtraDecimal;
                for (char digito : strDecimal.toCharArray()) {
                    String digitoStr = String.valueOf(digito);
                    g2d.drawString(digitoStr, currentX, yBase);
                    currentX += espacoEntreDigitos;
                }

                //Ponteiro Principal
                int centerX = x + hidrometroImage.getWidth() / 2;
                int centerY = y + hidrometroImage.getHeight() / 2;
                int ponteiroVolumeLength = 23;
                g2d.setColor(Color.RED);
                double anguloPonteiroVolume = (leituraAtual * Math.PI * 2);
                AffineTransform oldTransform = g2d.getTransform();
                g2d.translate(centerX + 82, centerY + 48);
                g2d.rotate(anguloPonteiroVolume);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawLine(0, 0, ponteiroVolumeLength, 0);
                g2d.setTransform(oldTransform);

                //Ponteiro de Vazão
                int ponteiroVazaoLength = 23;
                g2d.setColor(Color.RED);
                oldTransform = g2d.getTransform();
                g2d.translate(centerX + 1, centerY + 47);
                g2d.rotate(anguloPonteiroVazao);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(0, 0, ponteiroVazaoLength, 0);
                g2d.setTransform(oldTransform);
            }
        }
    }
}