package br.edu.senai.fatesg.avcar.swing.views.presenters;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import br.edu.senai.fatesg.avcar.business.fornecedores.FornecedorController;
import br.edu.senai.fatesg.avcar.business.fornecedores.FornecedorDTO;
import br.edu.senai.fatesg.avcar.swing.views.CadastroFornecedor;
import javax.swing.text.MaskFormatter;
import javax.swing.JOptionPane;

@Component
public class CadastroFornecedorPresenter {

    @Autowired
    private FornecedorController fornecedorController;

    private Long idAtual = null;

    public void initLogic(CadastroFornecedor view) {
        try {
            MaskFormatter maskCNPJ = new MaskFormatter("##.###.###/####-##");
            maskCNPJ.install(view.getFormattedTextFieldCNPJ());
            
            MaskFormatter maskCEP = new MaskFormatter("#####-###");
            maskCEP.install(view.getFormattedTextFieldCEP());
            
            MaskFormatter maskNumero = new MaskFormatter("#####-####");
            maskNumero.install(view.getFormattedTextFieldNumero());
        } catch (Exception e) {}
        
        view.getComboBoxUF().removeAllItems();
        String[] ufs = {"AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};
        for(String uf : ufs) view.getComboBoxUF().addItem(uf);

        view.getButtonSalvar().addActionListener(e -> salvarFornecedor(view));
        view.getButtonCancelar().addActionListener(e -> fechar(view));
    }

    public void preparar(CadastroFornecedor view, Long id) {
        this.idAtual = id;
        if (id == null) {
            view.getTextFieldRazaoSocial().setText("");
            view.getFormattedTextFieldCNPJ().setValue(null);
            view.getFormattedTextFieldCNPJ().setText("");
            view.getTextFieldDDI().setText("+55");
            view.getTextFieldDDD().setText("");
            view.getFormattedTextFieldNumero().setValue(null);
            view.getFormattedTextFieldNumero().setText("");
            view.getTextFieldEmail().setText("");
            view.getTextFieldEndereco().setText("");
            view.getTextFieldBairro().setText("");
            view.getTextFieldCidade().setText("");
            view.getComboBoxUF().setSelectedIndex(8); // GO
            view.getFormattedTextFieldCEP().setValue(null);
            view.getFormattedTextFieldCEP().setText("");
        } else {
            FornecedorDTO f = fornecedorController.buscarPorId(id).getBody();
            if (f != null) {
                view.getTextFieldRazaoSocial().setText(f.getRazaoSocial());
                
                String cnpj = f.getCnpj();
                if (cnpj != null && cnpj.length() == 14) {
                    cnpj = cnpj.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
                }
                view.getFormattedTextFieldCNPJ().setText(cnpj);
                
                view.getTextFieldDDI().setText(f.getDdi());
                view.getTextFieldDDD().setText(f.getDdd());
                
                String numero = f.getNumeroFornecedor();
                if (numero != null && numero.length() == 9) {
                    numero = numero.replaceFirst("(\\d{5})(\\d{4})", "$1-$2");
                }
                view.getFormattedTextFieldNumero().setText(numero);
                view.getTextFieldEmail().setText(f.getEmail());
                view.getTextFieldEndereco().setText(f.getEnderecoFornecedor());
                view.getTextFieldBairro().setText(f.getBairroFornecedor());
                view.getTextFieldCidade().setText(f.getCidadeFornecedor());
                view.getComboBoxUF().setSelectedItem(f.getEstadoFornecedor());
                String cepStr = String.format("%08d", f.getCepFornecedor());
                view.getFormattedTextFieldCEP().setText(cepStr);
            }
        }
    }

    private void salvarFornecedor(CadastroFornecedor view) {
        try {
            String razaoSocial = view.getTextFieldRazaoSocial().getText().trim();
            String cnpj = view.getFormattedTextFieldCNPJ().getText().replaceAll("[^0-9]", "");
            String ddd = view.getTextFieldDDD().getText().trim();
            String numero = view.getFormattedTextFieldNumero().getText().replaceAll("[^0-9]", "");
            String email = view.getTextFieldEmail().getText().trim();
            String endereco = view.getTextFieldEndereco().getText().trim();
            String bairro = view.getTextFieldBairro().getText().trim();
            String cidade = view.getTextFieldCidade().getText().trim();
            String cepTexto = view.getFormattedTextFieldCEP().getText().replaceAll("[^0-9]", "");
            String uf = view.getComboBoxUF().getSelectedItem() != null ? view.getComboBoxUF().getSelectedItem().toString() : "";

            if (razaoSocial.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Razão Social é obrigatória!");
                return;
            }
            if (cnpj.length() != 14) {
                JOptionPane.showMessageDialog(view, "CNPJ é obrigatório e deve ter 14 dígitos!");
                return;
            }
            if (ddd.isEmpty() || numero.length() < 8) {
                JOptionPane.showMessageDialog(view, "DDD e Número de telefone são obrigatórios!");
                return;
            }
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(view, "E-mail é obrigatório!");
                return;
            }
            if (endereco.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Endereço é obrigatório!");
                return;
            }
            if (bairro.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Bairro é obrigatório!");
                return;
            }
            if (cidade.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Cidade é obrigatória!");
                return;
            }
            if (uf.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Estado é obrigatório!");
                return;
            }
            if (cepTexto.length() != 8) {
                JOptionPane.showMessageDialog(view, "CEP é obrigatório e deve ser válido!");
                return;
            }

            int cep = Integer.parseInt(cepTexto);

            FornecedorController.FornecedorRequest req = new FornecedorController.FornecedorRequest(
                razaoSocial,
                cnpj,
                view.getTextFieldDDI().getText().trim(),
                ddd,
                numero,
                email,
                endereco,
                bairro,
                cidade,
                uf,
                cep
            );

            if (idAtual == null) {
                fornecedorController.salvar(req);
                JOptionPane.showMessageDialog(view, "Fornecedor salvo com sucesso!");
            } else {
                fornecedorController.atualizar(idAtual, req);
                JOptionPane.showMessageDialog(view, "Fornecedor atualizado com sucesso!");
            }
            
            fechar(view);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erro ao salvar: " + ex.getMessage());
        }
    }

    private void fechar(CadastroFornecedor view) {
        java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(view);
        if (w != null) w.dispose();
    }
}
