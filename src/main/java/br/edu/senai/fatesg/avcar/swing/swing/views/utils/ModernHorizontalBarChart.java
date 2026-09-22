package br.edu.senai.fatesg.avcar.swing.views.utils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.LinkedHashMap;

public class ModernHorizontalBarChart extends JPanel {
    private Map<String, Integer> dados = new LinkedHashMap<>();

    public ModernHorizontalBarChart() {
        setOpaque(false);
        setPreferredSize(new Dimension(400, 300));
    }

    public void atualizarDados(Map<String, Integer> dados) {
        this.dados = dados;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
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

        // Título
        g2.setColor(new Color(220, 223, 228));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        String title = "Marcas Mais Atendidas";
        FontMetrics fmTitle = g2.getFontMetrics();
        g2.drawString(title, (w - fmTitle.stringWidth(title)) / 2, 30);

        if (dados == null || dados.isEmpty()) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g2.setColor(new Color(100, 100, 100));
            g2.drawString("Sem dados", w / 2 - 30, h / 2);
            g2.dispose();
            return;
        }

        int paddingX = 80;
        int paddingRight = 40;
        int paddingTop = 70;
        int paddingBottom = 40;
        int chartW = w - paddingX - paddingRight;
        int chartH = h - paddingTop - paddingBottom;

        int max = 0;
        for (Integer val : dados.values()) {
            if (val > max) max = val;
        }
        if (max == 0) max = 1;

        int numBars = dados.size();
        int rowHeight = chartH / numBars;
        int barHeight = Math.min(rowHeight - 10, 30); // max thickness 30

        int i = 0;
        for (Map.Entry<String, Integer> entry : dados.entrySet()) {
            String label = entry.getKey();
            int value = entry.getValue();

            int y = paddingTop + (i * rowHeight) + (rowHeight - barHeight) / 2;

            // Rótulo da Marca (Eixo Y)
            g2.setColor(new Color(180, 185, 190));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            String displayLabel = label;
            if (displayLabel == null || displayLabel.isBlank()) displayLabel = "Outros";
            if (displayLabel.length() > 8) displayLabel = displayLabel.substring(0, 8) + ".";
            g2.drawString(displayLabel, paddingX - fm.stringWidth(displayLabel) - 10, y + barHeight - 8);

            // Calcula largura
            int barWidth = (int) Math.round(((double) value / max) * chartW);

            // Desenha Fundo da Barra (Cinza Escuro)
            g2.setColor(new Color(60, 63, 65));
            g2.fillRoundRect(paddingX, y, chartW, barHeight, 10, 10);

            // Desenha a Barra de Valor (Azul Claro)
            g2.setColor(new Color(86, 172, 230));
            if (barWidth > 0) {
                g2.fillRoundRect(paddingX, y, barWidth, barHeight, 10, 10);
            }

            // Escreve o valor no fim da barra
            g2.setColor(Color.WHITE);
            g2.drawString(String.valueOf(value), paddingX + barWidth + 5, y + barHeight - 8);

            i++;
        }

        g2.dispose();
    }
}
