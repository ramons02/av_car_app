package br.edu.senai.fatesg.avcar.swing.views.utils;

import javax.swing.*;
import java.awt.*;

public class ModernBarChart extends JPanel {
    private double valorPecas;
    private double valorMaoObra;
    private double valorServicosExternos;

    public ModernBarChart() {
        setOpaque(false);
        setPreferredSize(new Dimension(400, 350));
    }

    public void atualizarDados(double pecas, double maoObra, double servicos) {
        this.valorPecas = pecas;
        this.valorMaoObra = maoObra;
        this.valorServicosExternos = servicos;
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

        // Título Principal
        g2.setColor(new Color(220, 223, 228));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        String title = "Faturamento por Categoria";
        FontMetrics fmTitle = g2.getFontMetrics();
        g2.drawString(title, (w - fmTitle.stringWidth(title)) / 2, 30);

        // Área do gráfico
        int paddingX = 40;
        int paddingBottom = 60;
        int paddingTop = 90;
        int chartH = h - paddingBottom - paddingTop;
        int chartW = w - (paddingX * 2);

        // Encontrar valor máximo para escalar as colunas
        double max = Math.max(valorPecas, Math.max(valorMaoObra, valorServicosExternos));
        if (max == 0) max = 1; // Previne divisão por zero

        // Desenhar eixos / Linhas de grade
        g2.setColor(new Color(60, 63, 65));
        g2.drawLine(paddingX, paddingTop + chartH, paddingX + chartW, paddingTop + chartH); // Eixo X

        // Colunas
        int numCols = 3;
        int espacoEntreCols = 30;
        int larguraColuna = (chartW - (espacoEntreCols * (numCols + 1))) / numCols;
        
        desenharColuna(g2, 0, "Peças", valorPecas, max, paddingX, paddingTop, chartH, larguraColuna, espacoEntreCols, new Color(74, 144, 226)); // Azul
        desenharColuna(g2, 1, "Mão de Obra", valorMaoObra, max, paddingX, paddingTop, chartH, larguraColuna, espacoEntreCols, new Color(240, 173, 78)); // Laranja
        desenharColuna(g2, 2, "Serv. Ext", valorServicosExternos, max, paddingX, paddingTop, chartH, larguraColuna, espacoEntreCols, new Color(155, 89, 182)); // Roxo

        g2.dispose();
    }

    private void desenharColuna(Graphics2D g2, int index, String label, double valor, double max, int paddingX, int paddingTop, int chartH, int larguraColuna, int espacoEntreCols, Color cor) {
        int alturaReal = (int) Math.round((valor / max) * chartH);
        
        int x = paddingX + espacoEntreCols + (index * (larguraColuna + espacoEntreCols));
        int y = paddingTop + chartH - alturaReal;

        // Desenha a coluna com bordas arredondadas no topo
        g2.setColor(cor);
        if (alturaReal > 10) {
            g2.fillRoundRect(x, y, larguraColuna, alturaReal, 10, 10);
            // Corrige arredondamento na base desenhando um retangulo sobreposto
            g2.fillRect(x, y + 10, larguraColuna, alturaReal - 10);
        } else if (alturaReal > 0) {
            g2.fillRect(x, y, larguraColuna, alturaReal);
        } else {
            // Pequeno risco se for zero
            g2.fillRoundRect(x, paddingTop + chartH - 2, larguraColuna, 2, 2, 2);
        }

        // Rótulo da Coluna (Eixo X)
        g2.setColor(new Color(180, 185, 190));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        FontMetrics fm = g2.getFontMetrics();
        int labelX = x + (larguraColuna - fm.stringWidth(label)) / 2;
        g2.drawString(label, labelX, paddingTop + chartH + 20);
        
        // Rótulo Flutuante do Valor no Topo da Coluna
        String strValor = formatarMoedaResumida(valor);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fmV = g2.getFontMetrics();
        int valorX = x + (larguraColuna - fmV.stringWidth(strValor)) / 2;
        int valorY = y - 15;
        if (valorY < 50) valorY = 50; // Respeita o espaço do título
        // Se for zero, ajusta para ficar acima da pequena linha
        if (valor == 0) valorY = paddingTop + chartH - 10;
        g2.drawString(strValor, valorX, valorY);
    }
    
    private String formatarMoedaResumida(double valor) {
        if (valor == 0) return "R$ 0";
        java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("pt", "BR"));
        nf.setMaximumFractionDigits(0);
        return nf.format(valor);
    }
}
