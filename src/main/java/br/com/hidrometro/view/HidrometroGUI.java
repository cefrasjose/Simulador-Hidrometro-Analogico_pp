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

    public HidrometroGUI() {
        setTitle("Hidrômetro Digital");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);

        carregarImagemHidrometro();

        medidorPanel = new MedidorPanel();
        add(medidorPanel, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(Color.DARK_GRAY);
        statusLabel = new JLabel("Status: Aguardando dados...");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(Color.WHITE);
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }

    public void atualizarDados(double leitura, double vazao, double pressao, String status) { // Adicionado 'pressao'
        SwingUtilities.invokeLater(() -> {
            medidorPanel.setLeitura(leitura);
            medidorPanel.setVazao(vazao);
            medidorPanel.setPressao(pressao); // NOVO: Passa a pressão
            statusLabel.setText("Status: " + status);
            medidorPanel.repaint(); // Redesenha o painel
        });
    }

    public MedidorPanel getMedidorPanel() {
        return medidorPanel;
    }

    private void carregarImagemHidrometro() {
        try {
            // Ajustado para carregar de resources via ClassLoader
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

    class MedidorPanel extends JPanel implements ActionListener {
        private double leituraAtual = 0.0;
        private double vazaoAtual = 0.0;
        private double pressaoAtual = 0.0; //Armazena a pressão

        //Ângulos para os ponteiros animados
        private double anguloPonteiroVazao = 0.0;
        private double anguloPonteiroPressao = 0.0; //Ângulo para o ponteiro de pressão

        public void setLeitura(double leitura) { this.leituraAtual = leitura; }
        public void setVazao(double vazao) { this.vazaoAtual = vazao; }
        public void setPressao(double pressao) { this.pressaoAtual = pressao; } //Setter para pressão

        private Timer animationTimer;

        public MedidorPanel() {
            setBackground(Color.DARK_GRAY);
            animationTimer = new Timer(40, this); // 25 FPS
            animationTimer.start();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //Animação do ponteiro de Vazão (centro, ponteiro menor)
            //Simula um "giro" mais rápido conforme a vazão aumenta
            anguloPonteiroVazao += (vazaoAtual / 10.0) * 0.05; //Ajuste o fator para a velocidade desejada
            if (anguloPonteiroVazao > Math.PI * 2) anguloPonteiroVazao -= Math.PI * 2;

            //Simula um "giro" mais rápido conforme a pressão aumenta
            anguloPonteiroPressao += (pressaoAtual / 5.0) * 0.05; //Ajuste o fator para a velocidade desejada
            if (anguloPonteiroPressao > Math.PI * 2) anguloPonteiroPressao -= Math.PI * 2;

            repaint(); //Redesenha o componente
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


            if (hidrometroImage != null) {
                //Centraliza a imagem do hidrômetro
                int imgX = (getWidth() - hidrometroImage.getWidth()) / 2;
                int imgY = (getHeight() - hidrometroImage.getHeight()) / 2;
                g2d.drawImage(hidrometroImage, imgX, imgY, this);

                g2d.setFont(new Font("Consolas", Font.BOLD, 35));//Ajuste no tamanho da fonte

                String strInteira = String.format("%03d", (int) leituraAtual);
                String strDecimal = String.format("%02d", (int) Math.round((leituraAtual - (int) leituraAtual) * 100));
                String leituraCompleta = strInteira + strDecimal;

                //Coordenadas base e dimensões das "casas" dos números
                int xInicial = imgX + 175; // Posição X
                int yLeitura = imgY + 236; // Posição Y
                int larguraCasa = 27;     // Largura de cada caixa de dígito

                //Loop para desenhar cada dígito individualmente
                for (int i = 0; i < leituraCompleta.length(); i++) {
                    char digitoChar = leituraCompleta.charAt(i);
                    String digitoStr = String.valueOf(digitoChar);

                    // Define a cor
                    if (i < 3) {
                        g2d.setColor(Color.BLACK);
                    } else {
                        g2d.setColor(Color.RED);
                    }

                    //Calcula a posição X para centralizar o dígito dentro da sua casa
                    FontMetrics fm = g2d.getFontMetrics();
                    int larguraDigito = fm.stringWidth(digitoStr);
                    int xDigito = xInicial + (i * larguraCasa) + (larguraCasa - larguraDigito) / 2;

                    //Desenha o dígito
                    g2d.drawString(digitoStr, xDigito, yLeitura);
                }

                //2. PONTEIRO DE LITROS (Superior direito)
                int centroPonteiroLitrosX = imgX + 337;
                int centroPonteiroLitrosY = imgY + 304;
                int comprimentoPonteiroLitros = 24;
                g2d.setColor(Color.RED);
                double anguloLitros = Math.toRadians((leituraAtual - Math.floor(leituraAtual)) * 360.0 - 90);

                AffineTransform oldTransform = g2d.getTransform();
                g2d.translate(centroPonteiroLitrosX, centroPonteiroLitrosY);
                g2d.rotate(anguloLitros);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawLine(0, 0, comprimentoPonteiroLitros, 0);
                g2d.setTransform(oldTransform);


                //3. PONTEIRO CENTRAL DE VAZÃO (Inferior direito)
                int centroPonteiroVazaoX = imgX + 258;
                int centroPonteiroVazaoY = imgY + 303;
                int comprimentoPonteiroVazao = 23;
                g2d.setColor(Color.RED);
                oldTransform = g2d.getTransform();
                g2d.translate(centroPonteiroVazaoX, centroPonteiroVazaoY);
                g2d.rotate(anguloPonteiroVazao);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(0, 0, comprimentoPonteiroVazao, 0);
                g2d.setTransform(oldTransform);

                //4. PONTEIRO DE PRESSÃO (Inferior esquerdo)
                int centroPonteiroPressaoX = imgX + 259;
                int centroPonteiroPressaoY = imgY + 385;
                int comprimentoPonteiroPressao = 25;
                g2d.setColor(Color.RED);
                oldTransform = g2d.getTransform();
                g2d.translate(centroPonteiroPressaoX, centroPonteiroPressaoY);
                g2d.rotate(anguloPonteiroPressao);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(0, 0, comprimentoPonteiroPressao, 0);
                g2d.setTransform(oldTransform);


                //5. Exibir Vazão e Pressão como texto
                if (statusLabel.getText().contains("NORMAL")) {
                    g2d.setFont(new Font("Calibri", Font.BOLD, 9));
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(String.format("Vazão: %.2f m³/h", vazaoAtual), imgX + 137, imgY + 285);
                    g2d.drawString(String.format("Pressão: %.1f bar", pressaoAtual), imgX + 138, imgY + 300);
                }
            }
        }
    }
}