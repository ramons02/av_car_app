package br.edu.senai.fatesg.avcar.swing.views.presenters;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import br.edu.senai.fatesg.avcar.business.parceiros.ParceiroExternoController;
import br.edu.senai.fatesg.avcar.business.parceiros.ParceiroDTO;
import br.edu.senai.fatesg.avcar.swing.views.CadatroParceiroExterno;
import br.edu.senai.fatesg.avcar.swing.views.utils.MensagemUtil;
import javax.swing.text.MaskFormatter;

@Component
public class CadastroParceiroExternoPresenter {

    @Autowired
    private ParceiroExternoController controller;

    private Long idAtual = null;

    public void initDados(CadatroParceiroExterno view) {
        try {
            MaskFormatter cnpjMask = new MaskFormatter("##.###.###/####-##");
            cnpjMask.install(view.getFormattedTextFieldCNPJ());
            
            MaskFormatter telMask = new MaskFormatter("(##) #####-####");
            telMask.install(view.getFormattedTextFieldTelefone());
        } catch (Exception e) {}
        
        view.getButtonSalvar().addActionListener(e -> salvarParceiro(view));
        view.getButtonCancelar().addActionListener(e -> fechar(view));
    }

    public void preparar(CadatroParceiroExterno view, Long id) {
        this.idAtual = id;
        view.getTextFieldNome().setText("");
        view.getFormattedTextFieldCNPJ().setValue(null);
        view.getFormattedTextFieldCNPJ().setText("");
        view.getTextFieldTipoServico().setText("");
        view.getFormattedTextFieldTelefone().setValue(null);
        view.getFormattedTextFieldTelefone().setText("");
        view.getTextFieldEmail().setText("");

        if (id != null) {
            ParceiroDTO c = controller.buscarPorId(id).getBody();
            if (c != null) {
                view.getTextFieldNome().setText(c.getNome());
                String cnpj = c.getCnpj();
                if (cnpj != null && cnpj.length() == 14) {
                    cnpj = cnpj.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
                }
                view.getFormattedTextFieldCNPJ().setText(cnpj);
                view.getTextFieldTipoServico().setText(c.getTipoServico());
                
                String t = c.getTelefone() != null ? c.getTelefone() : "";
                if (t.startsWith("55") && t.length() >= 12) t = t.substring(2);
                if (t.length() == 11) {
                    t = t.replaceFirst("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
                }
                view.getFormattedTextFieldTelefone().setText(t);
                
                view.getTextFieldEmail().setText(c.getEmail());
            }
        }
    }

    private void salvarParceiro(CadatroParceiroExterno view) {
        try {
            String nome = view.getTextFieldNome().getText().trim();
            String cnpjRaw = view.getFormattedTextFieldCNPJ().getText();
            String tipoServico = view.getTextFieldTipoServico().getText().trim();
            String telefoneRaw = view.getFormattedTextFieldTelefone().getText();
            String email = view.getTextFieldEmail().getText().trim();

            String cnpj = cnpjRaw != null ? cnpjRaw.replaceAll("[^0-9]", "") : "";
            String telefone = telefoneRaw != null ? telefoneRaw.replaceAll("[^0-9]", "") : "";

            if (nome.isEmpty()) {
                MensagemUtil.mostrarErro(view, "Nome é obrigatório!");
                return;
            }
            if (cnpj.length() != 14) {
                MensagemUtil.mostrarErro(view, "CNPJ é obrigatório e deve conter 14 dígitos!");
                return;
            }
            if (tipoServico.isEmpty()) {
                MensagemUtil.mostrarErro(view, "Tipo de Serviço é obrigatório!");
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
            
            String telefoneCompleto = "55" + telefone;

            ParceiroExternoController.ParceiroRequest req = new ParceiroExternoController.ParceiroRequest(
                nome, cnpj, tipoServico, telefoneCompleto, email, true
            );

            if (idAtual == null) {
                controller.salvar(req);
                MensagemUtil.mostrarSucesso(view, "Parceiro salvo com sucesso!");
            } else {
                controller.atualizar(idAtual, req);
                MensagemUtil.mostrarSucesso(view, "Parceiro atualizado com sucesso!");
            }
            fechar(view);
        } catch (Exception ex) {
            MensagemUtil.mostrarErro(view, "Erro ao salvar parceiro: " + ex.getMessage());
        }
    }

    private void fechar(CadatroParceiroExterno view) {
        java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(view);
        if (w != null) w.dispose();
    }
}
