package br.edu.senai.fatesg.avcar.swing.views.utils;

import javax.swing.JFrame;
import java.awt.EventQueue;

public class JanelaUtil {
    
    public static void aplicarPadraoMaximizado(JFrame janela) {
        janela.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                // 1. Pega as dimensões reais do monitor conectado
                java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                
                // 2. Garante que o tamanho de restauração nunca quebre o limite da tela física
                // Tenta 1024x768, mas se o monitor for ainda menor, usa o tamanho da tela com margem
                int width = Math.min(1024, screenSize.width - 60);
                int height = Math.min(768, screenSize.height - 60);
                
                // Ao abrir, primeiro definimos o tamanho de restauração seguro
                janela.setSize(width, height);
                // Em seguida centralizamos na tela
                janela.setLocationRelativeTo(null);
                // E logo após, maximizamos para tela cheia
                janela.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
    }

    public static void abrirPainelComoModal(java.awt.Window parent, String title, javax.swing.JPanel panel) {
        javax.swing.JDialog dialog = new javax.swing.JDialog(parent, title, java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
