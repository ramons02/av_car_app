package br.edu.senai.fatesg.avcar.swing.views.utils;

import org.springframework.context.ApplicationContext;
import javax.swing.JOptionPane;
import java.awt.Window;

/**
 * Utilitário Clean Code para manipular a abertura da tela de CadastroServico.
 */
public class ServicoFormUtil {

    public static void abrirTelaNovo(Window parent, ApplicationContext ctx) {
        try {
            br.edu.senai.fatesg.avcar.swing.views.CadastroServico panel = ctx.getBean(br.edu.senai.fatesg.avcar.swing.views.CadastroServico.class);
            panel.prepararParaNovo();
            JanelaUtil.abrirPainelComoModal(parent, "Novo Serviço", panel);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger("ErrorLog").log(java.util.logging.Level.SEVERE, "Erro capturado", e);
            JOptionPane.showMessageDialog(parent, "Falha Crítica ao abrir a tela: " + e.getMessage());
        }
    }

    public static void abrirTelaEdicao(Window parent, ApplicationContext ctx, Long idServico) {
        try {
            br.edu.senai.fatesg.avcar.swing.views.CadastroServico panel = ctx.getBean(br.edu.senai.fatesg.avcar.swing.views.CadastroServico.class);
            panel.preencherParaEdicao(idServico);
            JanelaUtil.abrirPainelComoModal(parent, "Editar Serviço", panel);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger("ErrorLog").log(java.util.logging.Level.SEVERE, "Erro capturado", e);
            JOptionPane.showMessageDialog(parent, "Falha Crítica ao abrir a tela: " + e.getMessage());
        }
    }
}
