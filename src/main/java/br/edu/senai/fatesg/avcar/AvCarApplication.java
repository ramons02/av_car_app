package br.edu.senai.fatesg.avcar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.swing.*;

@SpringBootApplication
public class AvCarApplication implements ApplicationRunner {

    @Autowired
    private ApplicationContext ctx;

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        
        // --- INICIALIZAÇÃO DO TEMA DARK PREMIUM ---
        try {
            com.formdev.flatlaf.FlatDarkLaf.setup();
            // CORREÇÃO CROSS-PLATFORM: Usa a fonte nativa do S.O. (Ex: Ubuntu no Linux, Segoe no Win) 
            // no tamanho 14, garantindo que botões não fiquem cortados.
            javax.swing.UIManager.put("defaultFont", javax.swing.UIManager.getFont("Label.font").deriveFont(14f));
            javax.swing.UIManager.put("TextComponent.arc", 15);
            javax.swing.UIManager.put("Button.arc", 15);
            javax.swing.UIManager.put("Component.arc", 15);
            javax.swing.UIManager.put("TabbedPane.showTabSeparators", false);
            javax.swing.UIManager.put("TabbedPane.hasFullBorder", false);
            javax.swing.UIManager.put("TabbedPane.tabHeight", 45);
            javax.swing.UIManager.put("TabbedPane.tabInsets", new java.awt.Insets(0, 20, 0, 20));
            javax.swing.UIManager.put("TabbedPane.focusColor", new java.awt.Color(0,0,0,0));
            javax.swing.UIManager.put("TabbedPane.hoverColor", new java.awt.Color(70, 73, 75));
            javax.swing.UIManager.put("TabbedPane.selectedBackground", new java.awt.Color(43, 43, 43));
            
            // --- ESTILIZAÇÃO DAS TABELAS (ZEBRADO E HEADERS) ---
            javax.swing.UIManager.put("Table.alternateRowColor", new java.awt.Color(60, 63, 65));
            javax.swing.UIManager.put("TableHeader.font", javax.swing.UIManager.getFont("Label.font").deriveFont(java.awt.Font.BOLD, 13f));
            javax.swing.UIManager.put("TableHeader.background", new java.awt.Color(45, 48, 50));
            javax.swing.UIManager.put("TableHeader.foreground", new java.awt.Color(180, 185, 190));
            javax.swing.UIManager.put("TableHeader.separatorColor", new java.awt.Color(60, 63, 65));
            javax.swing.UIManager.put("TableHeader.bottomSeparatorColor", new java.awt.Color(80, 130, 180));
            
            // --- ESTILIZAÇÃO DE BOTÕES HOVER ---
            javax.swing.UIManager.put("Button.hoverBackground", new java.awt.Color(70, 73, 75));
            javax.swing.UIManager.put("Button.pressedBackground", new java.awt.Color(80, 85, 90));
        } catch (Exception ignored) {}
        
        SpringApplication.run(AvCarApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        SwingUtilities.invokeLater(() -> {
            // Abre o painel NOVO lado a lado, rodando com o Banco de Dados injetado!
            ctx.getBean(br.edu.senai.fatesg.avcar.swing.views.TelaPrincipalGUI.class).setVisible(true);
        });
    }
}
