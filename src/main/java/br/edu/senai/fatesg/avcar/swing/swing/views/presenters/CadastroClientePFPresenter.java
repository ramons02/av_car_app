package br.edu.senai.fatesg.avcar.swing.views.presenters;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import br.edu.senai.fatesg.avcar.business.clientes.ClienteController;
import br.edu.senai.fatesg.avcar.swing.views.CadastroClientePF;
import br.edu.senai.fatesg.avcar.swing.views.CadastroClientePJ;
import br.edu.senai.fatesg.avcar.swing.views.utils.ClienteFormUtil;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

@Component
public class CadastroClientePFPresenter {

    @Autowired
    private ClienteController clienteController;

    @Autowired
    private ApplicationContext ctx;

    private Long idClienteEmEdicao = null;

    public void initLogic(CadastroClientePF view) {
        configurarNavegacaoRadios(view);
    }

    private void configurarNavegacaoRadios(CadastroClientePF view) {
        javax.swing.ButtonGroup bg = new javax.swing.ButtonGroup();
        bg.add(view.getRadioButtonPessoaFisica());
        bg.add(view.getRadioButtonPessoaJuridica());
        
        view.getRadioButtonPessoaFisica().setSelected(true);

        view.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                view.getRadioButtonPessoaFisica().setSelected(true);
            }
        });
        
        view.getRadioButtonPessoaJuridica().addActionListener(e -> {
            view.getRadioButtonPessoaFisica().setSelected(true);
            JPanel novoPainel = ctx.getBean(CadastroClientePJ.class);
            trocarTela(view, novoPainel, "Cadastro de Cliente - Pessoa Jurídica");
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

    public void preencherParaEdicao(CadastroClientePF view, Long id) {
        this.idClienteEmEdicao = id;
        
        br.edu.senai.fatesg.avcar.business.clientes.ClienteDTO cliente = clienteController.buscarPorId(id).getBody();
        
        if (cliente != null) {
            view.getTextFieldNome().setText(cliente.getNome() != null ? cliente.getNome() : "");
            view.getTextFieldEndereco().setText(cliente.getEndereco() != null ? cliente.getEndereco() : "");
            view.getTextFieldBairro().setText(cliente.getBairro() != null ? cliente.getBairro() : "");
            view.getTextFieldCidade().setText(cliente.getCidade() != null ? cliente.getCidade() : "");
            if (cliente.getEstado() != null) view.getComboBoxEstado().setSelectedItem(cliente.getEstado());
            view.getTextFieldEmail().setText(cliente.getEmail() != null ? cliente.getEmail() : "");
            view.getFormattedTextFieldCEP().setText(cliente.getCep() != null ? cliente.getCep() : "");
            view.getFormattedTextFieldTelefone().setText(cliente.getTelefone() != null ? cliente.getTelefone() : "");
            view.getFormattedTextFieldCPF().setText(cliente.getDocumento() != null ? cliente.getDocumento() : "");
            
            view.getRadioButtonPessoaFisica().setSelected(true);
            view.getRadioButtonPessoaFisica().setEnabled(false);
            view.getRadioButtonPessoaJuridica().setEnabled(false);
        }
    }

    public void prepararParaNovo(CadastroClientePF view) {
        this.idClienteEmEdicao = null;
        limparCampos(view);
        view.getRadioButtonPessoaFisica().setEnabled(true);
        view.getRadioButtonPessoaJuridica().setEnabled(true);
        view.getRadioButtonPessoaFisica().setSelected(true);
    }

    public void salvarCliente(CadastroClientePF view) {
        try {
            String nome = view.getTextFieldNome().getText().trim();
            String cpfRaw = view.getFormattedTextFieldCPF().getText();
            String cepRaw = view.getFormattedTextFieldCEP().getText();
            String telRaw = view.getFormattedTextFieldTelefone().getText();

            String cpf = cpfRaw != null ? cpfRaw.replaceAll("[^0-9]", "") : "";
            String cep = cepRaw != null ? cepRaw.replaceAll("[^0-9]", "") : "";
            String tel = telRaw != null ? telRaw.replaceAll("[^0-9]", "") : "";

            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Nome é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (cpf.isEmpty() || cpf.length() != 11) {
                JOptionPane.showMessageDialog(view, "CPF inválido ou não preenchido!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (view.getTextFieldEndereco().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Endereço é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (view.getTextFieldBairro().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Bairro é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (view.getTextFieldCidade().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Cidade é obrigatória!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (view.getComboBoxEstado().getSelectedItem() == null || view.getComboBoxEstado().getSelectedItem().toString().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Estado é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (cep.isEmpty()) {
                JOptionPane.showMessageDialog(view, "CEP é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (view.getTextFieldEmail().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "E-mail é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (this.idClienteEmEdicao != null) {
                var req = new ClienteController.AtualizarClienteRequest(
                    nome, view.getTextFieldEndereco().getText().trim(), view.getTextFieldBairro().getText().trim(), 
                    view.getTextFieldCidade().getText().trim(), 
                    view.getComboBoxEstado().getSelectedItem() != null ? view.getComboBoxEstado().getSelectedItem().toString() : "", 
                    cep, tel, view.getTextFieldEmail().getText().trim(), cpf, null, null, null, null, null, null
                );
                clienteController.atualizar(this.idClienteEmEdicao, req);
            } else {
                var req = new ClienteController.CriarPFRequest(
                    nome, view.getTextFieldEndereco().getText().trim(), view.getTextFieldBairro().getText().trim(), 
                    view.getTextFieldCidade().getText().trim(), 
                    view.getComboBoxEstado().getSelectedItem() != null ? view.getComboBoxEstado().getSelectedItem().toString() : "", 
                    cep, tel, view.getTextFieldEmail().getText().trim(), cpf, null, null, null
                );
                clienteController.criarPF(req);
            }

            JOptionPane.showMessageDialog(view, "Cliente Pessoa Física salvo com sucesso!");
            cancelar(view);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos(CadastroClientePF view) {
        view.getTextFieldNome().setText("");
        view.getTextFieldEndereco().setText("");
        view.getTextFieldBairro().setText("");
        view.getTextFieldCidade().setText("");
        view.getTextFieldEmail().setText("");
        view.getFormattedTextFieldCEP().setValue(null);
        view.getFormattedTextFieldTelefone().setValue(null);
        view.getFormattedTextFieldCPF().setValue(null);
    }

    public void cancelar(CadastroClientePF view) {
        limparCampos(view);
        java.awt.Window win = javax.swing.SwingUtilities.getWindowAncestor(view);
        if (win != null) win.dispose();
    }
}
