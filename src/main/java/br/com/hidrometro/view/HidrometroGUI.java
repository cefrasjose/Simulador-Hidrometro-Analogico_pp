package br.com.hidrometro.view;

import br.com.hidrometro.Simulador;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

public class HidrometroGUI extends JFrame {

    private BufferedImage hidrometroImage;
    private MedidorPanel medidorPanel;
    private JLabel statusLabel;
    private JLabel vazaoMediaLabel; //label para a vazao media

    public HidrometroGUI(Simulador simulador) {
        setTitle("Hidrômetro Digital");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 650);
        setLocationRelativeTo(null);

        carregarImagemHidrometro();

        medidorPanel = new MedidorPanel();
        add(medidorPanel, BorderLayout.CENTER);

        //painel de Controle para vazao
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.DARK_GRAY);
        JButton diminuirVazaoBtn = new JButton("-");
        JButton aumentarVazaoBtn = new JButton("+");
        vazaoMediaLabel = new JLabel("Vazão Média: 0.00 m³/h");
        vazaoMediaLabel.setForeground(Color.WHITE);

        controlPanel.add(new JLabel("Controlar Vazão Média: "){{setForeground(Color.WHITE);}});
        controlPanel.add(diminuirVazaoBtn);
        controlPanel.add(aumentarVazaoBtn);
        controlPanel.add(vazaoMediaLabel);
        add(controlPanel, BorderLayout.NORTH); //adiciona o painel no topo

        //acoes dos botoes
        aumentarVazaoBtn.addActionListener(e -> simulador.solicitarAumentoVazao(1.0)); //incremento de 1.0
        diminuirVazaoBtn.addActionListener(e -> simulador.solicitarDiminuicaoVazao(1.0)); //decremento de 1.0

        //painel de status
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(Color.DARK_GRAY);
        statusLabel = new JLabel("Status: Aguardando dados...");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(Color.WHITE);
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }

    /*Assinatura do metodo alterada para receber a vazao media*/
    public void atualizarDados(double leitura, double vazao, double pressao, double vazaoMedia, String status) {
        SwingUtilities.invokeLater(() -> {
            medidorPanel.setLeitura(leitura);
            medidorPanel.setVazao(vazao);
            medidorPanel.setPressao(pressao);
            statusLabel.setText("Status: " + status);

            //atualiza o label da vazao media
            DecimalFormat df = new DecimalFormat("#.00");
            vazaoMediaLabel.setText("Vazão Média: " + df.format(vazaoMedia) + " m³/h");

            medidorPanel.repaint();
        });
    }

    public MedidorPanel getMedidorPanel() {
        return medidorPanel;
    }

    private void carregarImagemHidrometro() {
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

    class MedidorPanel extends JPanel implements ActionListener {
        private double leituraAtual = 0.0, vazaoAtual = 0.0, pressaoAtual = 0.0;
        private double anguloPonteiroVazao = 0.0, anguloPonteiroPressao = 0.0;
        private Timer animationTimer;

        public void setLeitura(double leitura) { this.leituraAtual = leitura; }
        public void setVazao(double vazao) { this.vazaoAtual = vazao; }
        public void setPressao(double pressao) { this.pressaoAtual = pressao; }

        public MedidorPanel() {
            setBackground(Color.DARK_GRAY);
            animationTimer = new Timer(40, this);
            animationTimer.start();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            anguloPonteiroVazao += (vazaoAtual / 10.0) * 0.05;
            if (anguloPonteiroVazao > Math.PI * 2) anguloPonteiroVazao -= Math.PI * 2;
            anguloPonteiroPressao += (pressaoAtual / 5.0) * 0.05;
            if (anguloPonteiroPressao > Math.PI * 2) anguloPonteiroPressao -= Math.PI * 2;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (hidrometroImage != null) {
                int imgX = (getWidth() - hidrometroImage.getWidth()) / 2;
                int imgY = (getHeight() - hidrometroImage.getHeight()) / 2;
                g2d.drawImage(hidrometroImage, imgX, imgY, this);

                g2d.setFont(new Font("Consolas", Font.BOLD, 35));
                String strInteira = String.format("%03d", (int) leituraAtual);
                String strDecimal = String.format("%02d", (int) Math.round((leituraAtual - (int) leituraAtual) * 100));
                String leituraCompleta = strInteira + strDecimal;
                int xInicial = imgX + 180, yLeitura = imgY + 236, larguraCasa = 27;

                for (int i = 0; i < leituraCompleta.length(); i++) {
                    char digitoChar = leituraCompleta.charAt(i);
                    String digitoStr = String.valueOf(digitoChar);
                    g2d.setColor(i < 3 ? Color.BLACK : Color.RED);
                    FontMetrics fm = g2d.getFontMetrics();
                    int larguraDigito = fm.stringWidth(digitoStr);
                    int xDigito = xInicial + (i * larguraCasa) + (larguraCasa - larguraDigito) / 2;
                    g2d.drawString(digitoStr, xDigito, yLeitura);
                }

                //ponteiro dos litros
                AffineTransform oldTransform = g2d.getTransform();
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(3));
                g2d.translate(imgX + 338, imgY + 304);
                g2d.rotate(Math.toRadians((leituraAtual - Math.floor(leituraAtual)) * 360.0 - 90));
                g2d.drawLine(0, 0, 23, 0);
                g2d.setTransform(oldTransform);

                //ponteiro da vazao
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(2));
                g2d.translate(imgX + 258, imgY + 304);
                g2d.rotate(anguloPonteiroVazao);
                g2d.drawLine(0, 0, 23, 0);
                g2d.setTransform(oldTransform);

                //ponteiro pressao
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(3));
                g2d.translate(imgX + 259, imgY + 385);
                g2d.rotate(anguloPonteiroPressao);
                g2d.drawLine(0, 0, 23, 0);
                g2d.setTransform(oldTransform);

                //vazao e Pressao na janela
                if (statusLabel.getText().contains("NORMAL")) {
                    g2d.setFont(new Font("Arial", Font.PLAIN, 9));
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(String.format("Vazão: %.2f m³/h", vazaoAtual), imgX + 132, imgY + 287);
                    g2d.drawString(String.format("Pressão: %.1f bar", pressaoAtual), imgX + 138, imgY + 301);
                }
            }
        }
    }
}