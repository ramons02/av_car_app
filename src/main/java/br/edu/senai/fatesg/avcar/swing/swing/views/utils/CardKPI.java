package br.edu.senai.fatesg.avcar.swing.views.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

/**
 * Componente de Card KPI (Key Performance Indicator) para a Dashboard.
 *
 * Desenha um card com gradiente escuro, barra de destaque colorida no topo,
 * título centralizado e valor com fonte que se auto-ajusta ao tamanho
 * disponível, garantindo responsividade de 1024×768 até resoluções maiores.
 *
 * Compatível com o tema FlatLaf Dark já configurado na aplicação.
 *
 * @author lucio-aguiar
 */
public class CardKPI extends JPanel {

    private String titulo;
    private String valor;
    private Color corDestaque;

    // ── Cores do tema Dark (harmonizadas com FlatDarkLaf) ──
    private static final Color COR_FUNDO_INICIO = new Color(50, 53, 55);
    private static final Color COR_FUNDO_FIM    = new Color(40, 42, 44);
    private static final Color COR_BORDA        = new Color(65, 68, 70);
    private static final Color COR_TITULO       = new Color(155, 158, 160);
    private static final Color COR_SOMBRA       = new Color(0, 0, 0, 60);

    // ── Constantes de layout e fonte ──
    private static final int ARC = 18;
    private static final int BARRA_TOPO = 4;
    private static final int MARGEM = 4;
    private static final int FONTE_VALOR_MAX = 56;
    private static final int FONTE_VALOR_MIN = 16;
    private static final int FONTE_TITULO = 12;

    /**
     * Cria um novo Card KPI.
     *
     * @param titulo       Texto exibido no topo do card (será convertido para MAIÚSCULAS)
     * @param valorInicial Valor numérico/monetário inicial exibido no centro
     * @param corDestaque  Cor semântica do card (barra de destaque + valor)
     */
    public CardKPI(String titulo, String valorInicial, Color corDestaque) {
        this.titulo = titulo.toUpperCase();
        this.valor = valorInicial;
        this.corDestaque = corDestaque;
        setOpaque(false);
        setPreferredSize(new Dimension(200, 140));
        setMinimumSize(new Dimension(100, 90));
    }

    /**
     * Atualiza o valor exibido no card e redesenha automaticamente.
     *
     * @param novoValor Novo valor a ser exibido (ex: "12", "R$ 45.230,00")
     */
    public void atualizarValor(String novoValor) {
        this.valor = novoValor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int w = getWidth();
        int h = getHeight();

        // ── 1. Sombra sutil (offset 2px para baixo-direita) ──
        RoundRectangle2D sombra = new RoundRectangle2D.Float(
                MARGEM + 2, MARGEM + 2, w - MARGEM * 2, h - MARGEM * 2, ARC, ARC);
        g2.setColor(COR_SOMBRA);
        g2.fill(sombra);

        // ── 2. Fundo com gradiente vertical (escuro sutil) ──
        RoundRectangle2D card = new RoundRectangle2D.Float(
                MARGEM, MARGEM, w - MARGEM * 2 - 1, h - MARGEM * 2 - 1, ARC, ARC);
        g2.setPaint(new GradientPaint(0, 0, COR_FUNDO_INICIO, 0, h, COR_FUNDO_FIM));
        g2.fill(card);

        // ── 3. Barra de destaque colorida no topo ──
        Shape clipOriginal = g2.getClip();
        g2.setClip(card);
        g2.setColor(corDestaque);
        g2.fillRect(MARGEM, MARGEM, w - MARGEM * 2, BARRA_TOPO);
        g2.setClip(clipOriginal);

        // ── 4. Borda sutil ──
        g2.setColor(COR_BORDA);
        g2.setStroke(new BasicStroke(1f));
        g2.draw(card);

        // ── 5. Título (centralizado, parte superior) ──
        Font fTitulo = new Font("Segoe UI", Font.BOLD, FONTE_TITULO);
        g2.setFont(fTitulo);
        g2.setColor(COR_TITULO);
        FontMetrics fmT = g2.getFontMetrics();
        int tituloX = (w - fmT.stringWidth(titulo)) / 2;
        int tituloY = MARGEM + BARRA_TOPO + 18 + fmT.getAscent();
        g2.drawString(titulo, tituloX, tituloY);

        // ── 6. Valor com auto-fonte (centralizado, área restante) ──
        int areaLargura = w - MARGEM * 2 - 30;
        int tamanhoFonte = calcularTamanhoFonte(g2, valor, areaLargura);
        Font fValor = new Font("Segoe UI", Font.BOLD, tamanhoFonte);
        g2.setFont(fValor);
        g2.setColor(corDestaque);
        FontMetrics fmV = g2.getFontMetrics();
        int valorX = (w - fmV.stringWidth(valor)) / 2;

        // Centraliza verticalmente entre o título e a base do card
        int topoAreaValor = tituloY + fmT.getDescent() + 4;
        int baseAreaValor = h - MARGEM * 2;
        int valorY = topoAreaValor + (baseAreaValor - topoAreaValor) / 2
                + fmV.getAscent() / 2 - fmV.getDescent() / 2;
        g2.drawString(valor, valorX, valorY);

        g2.dispose();
    }

    /**
     * Calcula o maior tamanho de fonte que faz o texto caber na largura máxima.
     * Itera de FONTE_VALOR_MAX até FONTE_VALOR_MIN, testando com FontMetrics.
     */
    private int calcularTamanhoFonte(Graphics2D g2, String texto, int larguraMaxima) {
        for (int s = FONTE_VALOR_MAX; s >= FONTE_VALOR_MIN; s--) {
            Font f = new Font("Segoe UI", Font.BOLD, s);
            if (g2.getFontMetrics(f).stringWidth(texto) <= larguraMaxima) {
                return s;
            }
        }
        return FONTE_VALOR_MIN;
    }
}
