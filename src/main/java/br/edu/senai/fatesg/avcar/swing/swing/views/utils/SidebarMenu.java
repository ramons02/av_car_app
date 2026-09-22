package br.edu.senai.fatesg.avcar.swing.views.utils;

import javax.swing.*;
import java.awt.*;

public class SidebarMenu extends JPanel {

    private final JTabbedPane targetTabbedPane;
    private final int WIDTH_STATIC = 240;
    
    private final String[] titulos = {
        "Visão Geral", "Clientes", "Veículos", "Serviços", 
        "Ordem de Serviços", "Peças", "Fornecedores Peças", 
        "Colaboradores", "Parceiros (Serviços)"
    };
    
    private final String[] arquivosIcones = {
        "/icons/dashboard.svg", "/icons/clientes.svg", "/icons/veiculos.svg", "/icons/servicos.svg", 
        "/icons/ordem.svg", "/icons/pecas.svg", "/icons/fornecedor.svg", 
        "/icons/colaboradores.svg", "/icons/parceiros.svg"
    };

    public SidebarMenu(JTabbedPane targetTabbedPane) {
        this.targetTabbedPane = targetTabbedPane;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(30, 33, 35));
        setPreferredSize(new Dimension(WIDTH_STATIC, 0));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(60, 63, 65)));
        
        // --- INÍCIO DA ESTILIZAÇÃO PREMIUM DA LOGO ---
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(new Color(30, 33, 35));
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Mantém o alinhamento esquerdo do BoxLayout para não recuar os botões
        
        JLabel logoSidebar = new JLabel();
        logoSidebar.setFont(new Font("Segoe UI", Font.BOLD, 30)); // Fonte um pouco maior
        
        // Texto bicolor: AV Branco (#FFFFFF) e CAR Vermelho (#E74C3C)
        logoSidebar.setText("<html><font color='#FFFFFF'>AV</font><font color='#E74C3C'> CAR</font></html>");
        logoSidebar.setHorizontalAlignment(SwingConstants.CENTER); // Centraliza perfeitamente dentro do painel
        
        logoPanel.add(logoSidebar, BorderLayout.CENTER);
        
        add(Box.createVerticalStrut(20)); // Margem superior antiga
        add(logoPanel);
        add(Box.createVerticalStrut(20)); // Margem inferior antes dos botões
        // --- FIM DA ESTILIZAÇÃO DA LOGO ---

        for (int i = 0; i < titulos.length; i++) {
            JButton btn = criarBotaoMenu(arquivosIcones[i], titulos[i], i);
            add(btn);
            add(Box.createVerticalStrut(2));
        }
    }

    private JButton criarBotaoMenu(String arquivoIcone, String texto, int index) {
        JButton btn = new JButton(texto) {
            @Override
            public void paintComponent(Graphics g) {
                if (targetTabbedPane.getSelectedIndex() == index) {
                    setBackground(new Color(55, 71, 79));
                } else {
                    setBackground(new Color(30, 33, 35));
                }
                super.paintComponent(g);
            }
        };
        
        try {
            java.net.URL url = getClass().getResource(arquivoIcone);
            if (url != null) {
                btn.setIcon(new com.formdev.flatlaf.extras.FlatSVGIcon(url).derive(20, 20));
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar SVG na Sidebar: " + e.getMessage());
        }

        btn.setForeground(Color.LIGHT_GRAY);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(15);
        btn.setMargin(new Insets(10, 20, 10, 10));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            targetTabbedPane.setSelectedIndex(index);
            getParent().repaint(); // update whole frame
        });

        return btn;
    }
}
