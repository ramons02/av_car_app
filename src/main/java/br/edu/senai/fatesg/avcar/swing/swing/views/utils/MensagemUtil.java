package br.edu.senai.fatesg.avcar.swing.views.utils;

import java.awt.Component;
import javax.swing.JOptionPane;

public class MensagemUtil {

    public static boolean confirmarInativacao(Component parent, String nomeEntidade) {
        int resposta = JOptionPane.showConfirmDialog(parent, 
                "Deseja inverter o status (Ativar/Inativar) de: " + nomeEntidade + "?", 
                "Confirmação de Status", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return resposta == JOptionPane.YES_OPTION;
    }

    public static void exibirAlertaBancoDeDados(Component parent) {
        JOptionPane.showMessageDialog(parent, 
            "Atenção: O Banco de Dados não está conectado!\nPara abrir essa tela, você precisa rodar o sistema pelo AvCarApplication.java (Botão Play Principal) e não pelo Shift+F6.", 
            "Modo Design", JOptionPane.WARNING_MESSAGE);
    }

    public static void mostrarErro(Component parent, String mensagem) {
        JOptionPane.showMessageDialog(parent, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public static void mostrarSucesso(Component parent, String mensagem) {
        JOptionPane.showMessageDialog(parent, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }
}
