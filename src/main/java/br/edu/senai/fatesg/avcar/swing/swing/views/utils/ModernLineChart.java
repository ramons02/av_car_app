package br.edu.senai.fatesg.avcar.swing.views.utils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.LinkedHashMap;

public class ModernLineChart extends JPanel {
    private Map<String, Double> dados = new LinkedHashMap<>();

    public ModernLineChart() {
        setOpaque(false);
        setPreferredSize(new Dimension(500, 350));
    }

    public void atualizarDados(Map<String, Double> dados) {
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
        String title = "Faturamento Mensal (Evolução)";
        FontMetrics fmTitle = g2.getFontMetrics();
        g2.drawString(title, (w - fmTitle.stringWidth(title)) / 2, 30);

        if (dados == null || dados.size() < 2) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g2.setColor(new Color(100, 100, 100));
            g2.drawString("Dados insuficientes", w / 2 - 60, h / 2);
            g2.dispose();
            return;
        }

        int paddingX = 40;
        int paddingRight = 40;
        int paddingTop = 80;
        int paddingBottom = 50;
        int chartW = w - paddingX - paddingRight;
        int chartH = h - paddingTop - paddingBottom;

        double max = 0;
        for (Double val : dados.values()) {
            if (val > max) max = val;
        }
        // Deixa 30% de margem no topo para os números não baterem no teto
        max = max * 1.3; 
        if (max == 0) max = 100; // Escala padrão se tudo for zero

        // 1. DESENHAR LINHAS DE GRADE (GRIDLINES)
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5.0f}, 0.0f)); // Pontilhado
        g2.setColor(new Color(60, 63, 65, 150));
        
        // Linha do Topo (Max)
        g2.drawLine(paddingX, paddingTop, paddingX + chartW, paddingTop);
        // Linha do Meio (50%)
        g2.drawLine(paddingX, paddingTop + (chartH / 2), paddingX + chartW, paddingTop + (chartH / 2));
        // Linha da Base (Eixo X - Sólida)
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(new Color(80, 83, 85));
        g2.drawLine(paddingX, paddingTop + chartH, paddingX + chartW, paddingTop + chartH); 

        // Rótulos do Eixo Y nas Gridlines (Opcional, sutil)
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.setColor(new Color(120, 125, 130));
        g2.drawString(formatarMoedaResumida(max), 5, paddingTop + 4);
        g2.drawString(formatarMoedaResumida(max / 2), 5, paddingTop + (chartH / 2) + 4);

        int numPoints = dados.size();
        int stepX = chartW / (numPoints - 1);

        int[] xPoints = new int[numPoints];
        int[] yPoints = new int[numPoints];

        int i = 0;
        for (Map.Entry<String, Double> entry : dados.entrySet()) {
            String label = entry.getKey();
            double value = entry.getValue();

            xPoints[i] = paddingX + (i * stepX);
            yPoints[i] = paddingTop + chartH - (int) Math.round((value / max) * chartH);

            // Eixo X Labels (Meses)
            g2.setColor(new Color(180, 185, 190));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, xPoints[i] - fm.stringWidth(label) / 2, paddingTop + chartH + 20);
            
            i++;
        }

        // 2. DESENHAR ÁREA (GRADIENTE GLASSMORPHISM)
        Polygon area = new Polygon();
        area.addPoint(xPoints[0], paddingTop + chartH); 
        for (int j = 0; j < numPoints; j++) {
            area.addPoint(xPoints[j], yPoints[j]);
        }
        area.addPoint(xPoints[numPoints - 1], paddingTop + chartH); 
        
        GradientPaint gp = new GradientPaint(
            0, paddingTop, new Color(46, 204, 113, 160), // Verde vibrante forte no topo
            0, paddingTop + chartH, new Color(46, 204, 113, 10)  // Quase transparente na base
        );
        g2.setPaint(gp);
        g2.fillPolygon(area);

        // 3. DESENHAR LINHA PRINCIPAL
        g2.setColor(new Color(46, 204, 113)); // Verde Neon
        g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawPolyline(xPoints, yPoints, numPoints);

        // 4. DESENHAR BOLOTAS E RÓTULOS DE VALORES (FLOATING LABELS)
        for (int j = 0; j < numPoints; j++) {
            // Bolota
            boolean ultimoPonto = (j == numPoints - 1);
            int raio = ultimoPonto ? 14 : 10; // Destaca o mês atual sendo um pouco maior
            int off = raio / 2;
            
            g2.setColor(new Color(30, 32, 34)); // Fundo
            g2.fillOval(xPoints[j] - off, yPoints[j] - off, raio, raio);
            g2.setColor(ultimoPonto ? Color.WHITE : new Color(46, 204, 113)); // Borda branca se for atual
            g2.setStroke(new BasicStroke(ultimoPonto ? 2.5f : 2.0f));
            g2.drawOval(xPoints[j] - off, yPoints[j] - off, raio, raio);

            // Rótulo de Valor (apenas se for maior que zero ou se for o último mês)
            double val = (Double) dados.values().toArray()[j];
            if (val > 0 || ultimoPonto) {
                String strValor = formatarMoedaResumida(val);
                g2.setFont(new Font("Segoe UI", ultimoPonto ? Font.BOLD : Font.PLAIN, ultimoPonto ? 13 : 11));
                g2.setColor(ultimoPonto ? Color.WHITE : new Color(200, 205, 210));
                
                FontMetrics fmV = g2.getFontMetrics();
                int labelX = xPoints[j] - (fmV.stringWidth(strValor) / 2);
                int labelY = yPoints[j] - 15; // Flutuando acima
                
                // Evita que o texto passe do limite esquerdo/direito
                if (labelX < 10) labelX = 10;
                if (labelX + fmV.stringWidth(strValor) > w - 10) labelX = w - 10 - fmV.stringWidth(strValor);
                
                g2.drawString(strValor, labelX, labelY);
            }
        }

        g2.dispose();
    }

    private String formatarMoedaResumida(double valor) {
        if (valor == 0) return "R$ 0";
        java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("pt", "BR"));
        nf.setMaximumFractionDigits(0); // Tira os centavos para ficar clean
        return nf.format(valor);
    }
}
