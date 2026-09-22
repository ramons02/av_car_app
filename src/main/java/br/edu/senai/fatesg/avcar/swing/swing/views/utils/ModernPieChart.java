package br.edu.senai.fatesg.avcar.swing.views.utils;

import javax.swing.*;
import java.awt.*;

public class ModernPieChart extends JPanel {

    private int totalOS;
    private int osAbertas;

    public ModernPieChart(int totalOS, int osAbertas) {
        this.totalOS = totalOS;
        this.osAbertas = osAbertas;
        setOpaque(false);
        setPreferredSize(new Dimension(400, 350));
    }

    public void atualizarDados(int totalOS, int osAbertas) {
        this.totalOS = totalOS;
        this.osAbertas = osAbertas;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        // Ativa o Anti-Aliasing para bordas perfeitamente lisas
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        
        // Fundo do Card Arredondado
        g2.setColor(new Color(50, 53, 55));
        g2.fillRoundRect(0, 0, w - 1, h - 1, 20, 20);
        g2.setColor(new Color(75, 78, 80));
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 20, 20);
        
        // Título Principal
        g2.setColor(new Color(220, 223, 228));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        String title = "Status das Ordens de Serviço";
        FontMetrics fmTitle = g2.getFontMetrics();
        g2.drawString(title, (w - fmTitle.stringWidth(title)) / 2, 30);

        // Geometria do Gráfico
        int chartSize = Math.min(w, h) - 100;
        int x = (w - chartSize) / 2;
        int y = 60; // Deslocamento abaixo do título

        if (totalOS <= 0) {
            // Sem dados (Gráfico Cinza Vazio)
            g2.setColor(new Color(60, 63, 65));
            g2.fillArc(x, y, chartSize, chartSize, 0, 360);
        } else {
            int osFechadas = totalOS - osAbertas;
            int angleAbertas = (int) Math.round((double) osAbertas / totalOS * 360);
            int percAbertas = (int) Math.round((double) osAbertas / totalOS * 100);
            int percFechadas = 100 - percAbertas;

            // Fundo / Fechadas (Verde)
            g2.setColor(new Color(92, 184, 92));
            g2.fillArc(x, y, chartSize, chartSize, 0, 360);
            
            // Abertas (Laranja) desenhado por cima, a partir do topo (90 graus)
            g2.setColor(new Color(240, 173, 78));
            g2.fillArc(x, y, chartSize, chartSize, 90, -angleAbertas);

            // Escrever Porcentagem Fechadas (Verde)
            if (percFechadas > 0) {
                desenharTextoNaFatia(g2, percFechadas + "%", x, y, chartSize, 90 - angleAbertas - ((360 - angleAbertas) / 2));
            }
            
            // Escrever Porcentagem Abertas (Laranja)
            if (percAbertas > 0) {
                desenharTextoNaFatia(g2, percAbertas + "%", x, y, chartSize, 90 - (angleAbertas / 2));
            }
        }

        // Legenda Inferior
        int legendY = y + chartSize + 30;
        desenharLegenda(g2, "Abertas", new Color(240, 173, 78), w / 2 - 120, legendY);
        desenharLegenda(g2, "Concluídas", new Color(92, 184, 92), w / 2 + 20, legendY);

        g2.dispose();
    }
    
    private void desenharTextoNaFatia(Graphics2D g2, String texto, int x, int y, int size, int midAngle) {
        double rad = Math.toRadians(midAngle);
        int radius = size / 2;
        // Posição do texto: no meio do raio da fatia
        int textRadius = (int) (radius * 0.60); 
        
        int cx = x + radius;
        int cy = y + radius;
        
        int textX = cx + (int) (textRadius * Math.cos(rad));
        int textY = cy - (int) (textRadius * Math.sin(rad)); // Y cresce para baixo no Swing
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, textX - fm.stringWidth(texto) / 2, textY + fm.getAscent() / 2 - 4);
    }

    private void desenharLegenda(Graphics2D g2, String texto, Color cor, int startX, int y) {
        g2.setColor(cor);
        g2.fillRoundRect(startX, y - 12, 16, 16, 4, 4);
        g2.setColor(new Color(220, 223, 228));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2.drawString(texto, startX + 25, y);
    }
}
