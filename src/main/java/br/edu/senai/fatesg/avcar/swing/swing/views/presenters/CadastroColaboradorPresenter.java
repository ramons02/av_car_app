package br.edu.senai.fatesg.avcar.swing.views.presenters;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import br.edu.senai.fatesg.avcar.business.colaboradores.ColaboradorController;
import br.edu.senai.fatesg.avcar.business.colaboradores.ColaboradorDTO;
import br.edu.senai.fatesg.avcar.business.colaboradores.FuncaoDTO;
import br.edu.senai.fatesg.avcar.swing.views.CadastroColaborador;
import br.edu.senai.fatesg.avcar.swing.views.utils.MensagemUtil;

import javax.swing.JCheckBox;
import java.util.ArrayList;
import java.util.List;

@Component
public class CadastroColaboradorPresenter {

    @Autowired
    private ColaboradorController controller;

    private Long idAtual = null;
    private List<JCheckBox> checkBoxFuncoes = new ArrayList<>();

    public void initDados(CadastroColaborador view) {
        try {
            javax.swing.text.MaskFormatter cpfMask = new javax.swing.text.MaskFormatter("###.###.###-##");
            cpfMask.install(view.getFormattedTextFieldCPF());
            
            javax.swing.text.MaskFormatter telMask = new javax.swing.text.MaskFormatter("(##) #####-####");
            telMask.install(view.getFormattedTextFieldTelefone());
        } catch (Exception e) {}
        
        javax.swing.JPanel funcoesPanel = new javax.swing.JPanel();
        funcoesPanel.setLayout(new javax.swing.BoxLayout(funcoesPanel, javax.swing.BoxLayout.Y_AXIS));
        funcoesPanel.setOpaque(false);
        
        List<FuncaoDTO> funcoes = controller.listarFuncoes().getBody();
        checkBoxFuncoes.clear();
        if (funcoes != null) {
            for (FuncaoDTO f : funcoes) {
                JCheckBox cb = new JCheckBox(f.getFuncaoColaborador());
                cb.putClientProperty("idFuncao", f.getIdFuncao());
                cb.setOpaque(false);
                funcoesPanel.add(cb);
                checkBoxFuncoes.add(cb);
            }
        }
        view.getScrollPaneFuncoes().setViewportView(funcoesPanel);
        
        view.getButtonSalvar().addActionListener(e -> salvarColaborador(view));
        view.getButtonCancelar().addActionListener(e -> fechar(view));
    }

    public void preparar(CadastroColaborador view, Long id) {
        this.idAtual = id;
        view.getTextFieldNome().setText("");
        view.getFormattedTextFieldCPF().setValue(null);
        view.getFormattedTextFieldCPF().setText("");
        view.getFormattedTextFieldTelefone().setValue(null);
        view.getFormattedTextFieldTelefone().setText("");
        view.getTextFieldEmail().setText("");
        
        for (JCheckBox cb : checkBoxFuncoes) {
            cb.setSelected(false);
        }

        if (id != null) {
            ColaboradorDTO c = controller.buscarPorId(id).getBody();
            if (c != null) {
                view.getTextFieldNome().setText(c.getNome());
                view.getFormattedTextFieldCPF().setText(c.getCpf());
                
                String t = c.getTelefone() != null ? c.getTelefone() : "";
                if (t.startsWith("55") && t.length() >= 12) t = t.substring(2);
                view.getFormattedTextFieldTelefone().setText(t);
                
                view.getTextFieldEmail().setText(c.getEmail());
                
                if (c.getFuncoes() != null) {
                    for (String fName : c.getFuncoes()) {
                        for (JCheckBox cb : checkBoxFuncoes) {
                            if (fName.equals(cb.getText())) {
                                cb.setSelected(true);
                            }
                        }
                    }
                }
            }
        }
    }

    private void salvarColaborador(CadastroColaborador view) {
        try {
            String nome = view.getTextFieldNome().getText().trim();
            String cpfRaw = view.getFormattedTextFieldCPF().getText();
            String telefoneRaw = view.getFormattedTextFieldTelefone().getText();
            String email = view.getTextFieldEmail().getText().trim();

            String cpf = cpfRaw != null ? cpfRaw.replaceAll("[^0-9]", "") : "";
            String telefone = telefoneRaw != null ? telefoneRaw.replaceAll("[^0-9]", "") : "";

            if (nome.isEmpty()) {
                MensagemUtil.mostrarErro(view, "Nome é obrigatório!");
                return;
            }
            if (cpf.length() != 11) {
                MensagemUtil.mostrarErro(view, "CPF é obrigatório e deve ter 11 dígitos!");
                return;
            }
            if (telefone.length() < 10) {
                MensagemUtil.mostrarErro(view, "Telefone é obrigatório e deve ser válido!");
                return;
            }
            if (email.isEmpty()) {
                MensagemUtil.mostrarErro(view, "E-mail é obrigatório!");
                return;
            }

            String ddi = "55";
            String ddd = telefone.length() >= 2 ? telefone.substring(0, 2) : "";
            String numero = telefone.length() > 2 ? telefone.substring(2) : "";

            List<Long> funcaoIds = new ArrayList<>();
            for (JCheckBox cb : checkBoxFuncoes) {
                if (cb.isSelected()) {
                    funcaoIds.add((Long) cb.getClientProperty("idFuncao"));
                }
            }
            
            if (funcaoIds.isEmpty()) {
                MensagemUtil.mostrarErro(view, "Selecione pelo menos uma função.");
                return;
            }

            ColaboradorController.ColaboradorRequest req = new ColaboradorController.ColaboradorRequest(
                nome, cpf, ddi, ddd, numero, email, funcaoIds
            );

            if (idAtual == null) {
                controller.salvar(req);
                MensagemUtil.mostrarSucesso(view, "Colaborador salvo com sucesso!");
            } else {
                controller.atualizar(idAtual, req);
                MensagemUtil.mostrarSucesso(view, "Colaborador atualizado com sucesso!");
            }
            fechar(view);
        } catch (Exception ex) {
            MensagemUtil.mostrarErro(view, "Erro ao salvar colaborador: " + ex.getMessage());
        }
    }

    private void fechar(CadastroColaborador view) {
        java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(view);
        if (w != null) w.dispose();
    }
}
