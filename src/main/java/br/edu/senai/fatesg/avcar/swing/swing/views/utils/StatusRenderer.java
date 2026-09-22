package br.edu.senai.fatesg.avcar.swing.views.utils;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

public class StatusRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
            
        // Pede para o Java renderizar a célula padrão primeiro
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        // Centraliza o texto do Status
        setHorizontalAlignment(CENTER);
        
        if (value != null) {
            String status = value.toString().toLowerCase();
            
            // Coloca a fonte em Negrito para dar um destaque de "Badge"
            c.setFont(c.getFont().deriveFont(Font.BOLD));
            
            // Só colore o texto se a linha NÃO estiver selecionada (para não sumir no fundo cinza da seleção)
            if (!isSelected) {
                // Lógica de Cores Modernas (Paleta Dark Mode)
                if (status.equals("ativo") || status.contains("finalizada")) {
                    c.setForeground(new Color(46, 204, 113)); // Verde Esmeralda vibrante
                } else if (status.equals("inativo") || status.contains("cancelada")) {
                    c.setForeground(new Color(231, 76, 60)); // Vermelho
                } else if (status.contains("aberta") || status.contains("orçamento") || status.contains("aguardando")) {
                    c.setForeground(new Color(241, 196, 15)); // Amarelo/Laranja
                } else if (status.contains("execução")) {
                    c.setForeground(new Color(52, 152, 219)); // Azul vibrante
                } else {
                    c.setForeground(table.getForeground()); // Cor padrão se não achar
                }
            } else {
                c.setForeground(Color.WHITE); // Se clicar na linha, o texto fica branco
            }
        }
        
        return c;
    }
}
