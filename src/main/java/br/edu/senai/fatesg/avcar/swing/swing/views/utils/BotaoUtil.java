package br.edu.senai.fatesg.avcar.swing.views.utils;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;

public class BotaoUtil {

    public static void aplicarEstiloPrimario(JButton btn) {
        if (btn == null) return;
        btn.setBackground(new Color(46, 125, 50));
        btn.setForeground(Color.WHITE);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD));
        btn.setFocusPainted(false);
        btn.putClientProperty("JButton.buttonType", "roundRect");
    }

    public static void aplicarEstiloPerigo(JButton btn) {
        if (btn == null) return;
        btn.setBackground(new Color(183, 28, 28));
        btn.setForeground(Color.WHITE);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD));
        btn.setFocusPainted(false);
    }

    public static void aplicarEstiloSecundario(JButton btn) {
        if (btn == null) return;
        btn.setBackground(new Color(55, 71, 79));
        btn.setForeground(new Color(200, 210, 220));
        btn.setFocusPainted(false);
    }
}
