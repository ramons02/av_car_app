package br.edu.senai.fatesg.avcar.swing.views.presenters;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import br.edu.senai.fatesg.avcar.business.clientes.ClienteController;
import br.edu.senai.fatesg.avcar.swing.views.CadastroClientePF;
import br.edu.senai.fatesg.avcar.swing.views.CadastroClientePJ;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

@Component
public class CadastroClientePJPresenter {

    @Autowired
    private ClienteController clienteController;

    @Autowired
    private ApplicationContext ctx;

    private Long idClienteEmEdicao = null;

    public void initLogic(CadastroClientePJ view) {
        configurarNavegacaoRadios(view);
    }

    private void configurarNavegacaoRadios(CadastroClientePJ view) {
        javax.swing.ButtonGroup bg = new javax.swing.ButtonGroup();
        bg.add(view.getRadioButtonPessoaFisicaPJ());
        bg.add(view.getRadioButtonPessoaJuridicaPJ());
        
        view.getRadioButtonPessoaJuridicaPJ().setSelected(true);

        view.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                view.getRadioButtonPessoaJuridicaPJ().setSelected(true);
            }
        });
        
        view.getRadioButtonPessoaFisicaPJ().addActionListener(e -> {
            view.getRadioButtonPessoaJuridicaPJ().setSelected(true);
            JPanel novoPainel = ctx.getBean(CadastroClientePF.class);
            trocarTela(view, novoPainel, "Cadastro de Cliente - Pessoa Física");
        });
    }

    private void trocarTela(JPanel painelAtual, JPanel novoPainel, String novoTitulo) {
        java.awt.Window window = SwingUtilities.getWindowAncestor(painelAtual);
        if (window instanceof JDialog) {
            JDialog dialog = (JDialog) window;
            dialog.setTitle(novoTitulo);
            dialog.setContentPane(novoPainel);
            dialog.revalidate();
            dialog.repaint();
            dialog.pack();
        }
    }

    public void preencherParaEdicao(CadastroClientePJ view, Long id) {
        this.idClienteEmEdicao = id;
        
        br.edu.senai.fatesg.avcar.business.clientes.ClienteDTO cliente = clienteController.buscarPorId(id).getBody();
        
        if (cliente != null) {
            view.getTextFieldNomePJ().setText(cliente.getNome() != null ? cliente.getNome() : "");
            view.getTextFieldEnderecoPJ().setText(cliente.getEndereco() != null ? cliente.getEndereco() : "");
            view.getTextFieldBairroPJ().setText(cliente.getBairro() != null ? cliente.getBairro() : "");
            view.getTextFieldCidadePJ().setText(cliente.getCidade() != null ? cliente.getCidade() : "");
            if (cliente.getEstado() != null) view.getComboBoxEstadoPJ().setSelectedItem(cliente.getEstado());
            view.getTextFieldEmailPJ().setText(cliente.getEmail() != null ? cliente.getEmail() : "");
            view.getFormattedTextFieldCEPPJ().setText(cliente.getCep() != null ? cliente.getCep() : "");
            view.getFormattedTextFieldTelefonePJ().setText(cliente.getTelefone() != null ? cliente.getTelefone() : "");
            view.getFormattedTextFieldCNPJPJ().setText(cliente.getDocumento() != null ? cliente.getDocumento() : "");
            view.getFormattedTextFieldInscricaoEstadualPJ().setText(cliente.getInscricaoEstadual() != null ? cliente.getInscricaoEstadual() : "");
            
            view.getRadioButtonPessoaJuridicaPJ().setSelected(true);
            view.getRadioButtonPessoaFisicaPJ().setEnabled(false);
            view.getRadioButtonPessoaJuridicaPJ().setEnabled(false);
        }
    }

    public void prepararParaNovo(CadastroClientePJ view) {
        this.idClienteEmEdicao = null;
        limparCampos(view);
        view.getRadioButtonPessoaFisicaPJ().setEnabled(true);
        view.getRadioButtonPessoaJuridicaPJ().setEnabled(true);
        view.getRadioButtonPessoaJuridicaPJ().setSelected(true);
    }

    public void salvarCliente(CadastroClientePJ view) {
        try {
            String nome = view.getTextFieldNomePJ().getText().trim();
            String cnpjRaw = view.getFormattedTextFieldCNPJPJ().getText();
            String cepRaw = view.getFormattedTextFieldCEPPJ().getText();
            String telRaw = view.getFormattedTextFieldTelefonePJ().getText();

            String cnpj = cnpjRaw != null ? cnpjRaw.replaceAll("[^0-9]", "") : "";
            String cep = cepRaw != null ? cepRaw.replaceAll("[^0-9]", "") : "";
            String tel = telRaw != null ? telRaw.replaceAll("[^0-9]", "") : "";

            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Nome é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (cnpj.isEmpty() || cnpj.length() != 14) {
                JOptionPane.showMessageDialog(view, "CNPJ inválido ou não preenchido!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (view.getTextFieldEnderecoPJ().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Endereço é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (view.getTextFieldBairroPJ().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Bairro é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (view.getTextFieldCidadePJ().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Cidade é obrigatória!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (view.getComboBoxEstadoPJ().getSelectedItem() == null || view.getComboBoxEstadoPJ().getSelectedItem().toString().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Estado é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (cep.isEmpty()) {
                JOptionPane.showMessageDialog(view, "CEP é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (view.getTextFieldEmailPJ().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "E-mail é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (this.idClienteEmEdicao != null) {
                var req = new ClienteController.AtualizarClienteRequest(
                    nome, view.getTextFieldEnderecoPJ().getText().trim(), view.getTextFieldBairroPJ().getText().trim(), 
                    view.getTextFieldCidadePJ().getText().trim(), 
                    view.getComboBoxEstadoPJ().getSelectedItem() != null ? view.getComboBoxEstadoPJ().getSelectedItem().toString() : "", 
                    cep, tel, view.getTextFieldEmailPJ().getText().trim(), cnpj, null, null, 
                    view.getFormattedTextFieldInscricaoEstadualPJ().getText().trim(), null, null, null
                );
                clienteController.atualizar(this.idClienteEmEdicao, req);
            } else {
                var req = new ClienteController.CriarPJRequest(
                    nome, view.getTextFieldEnderecoPJ().getText().trim(), view.getTextFieldBairroPJ().getText().trim(), 
                    view.getTextFieldCidadePJ().getText().trim(), 
                    view.getComboBoxEstadoPJ().getSelectedItem() != null ? view.getComboBoxEstadoPJ().getSelectedItem().toString() : "", 
                    cep, tel, view.getTextFieldEmailPJ().getText().trim(), cnpj, 
                    view.getFormattedTextFieldInscricaoEstadualPJ().getText().trim(), null, null
                );
                clienteController.criarPJ(req);
            }

            JOptionPane.showMessageDialog(view, "Cliente Pessoa Jurídica salvo com sucesso!");
            cancelar(view);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos(CadastroClientePJ view) {
        view.getTextFieldNomePJ().setText("");
        view.getTextFieldEnderecoPJ().setText("");
        view.getTextFieldBairroPJ().setText("");
        view.getTextFieldCidadePJ().setText("");
        view.getTextFieldEmailPJ().setText("");
        view.getFormattedTextFieldCEPPJ().setValue(null);
        view.getFormattedTextFieldTelefonePJ().setValue(null);
        view.getFormattedTextFieldCNPJPJ().setValue(null);
        view.getFormattedTextFieldInscricaoEstadualPJ().setValue(null);
    }

    public void cancelar(CadastroClientePJ view) {
        limparCampos(view);
        java.awt.Window win = javax.swing.SwingUtilities.getWindowAncestor(view);
        if (win != null) win.dispose();
    }
}
