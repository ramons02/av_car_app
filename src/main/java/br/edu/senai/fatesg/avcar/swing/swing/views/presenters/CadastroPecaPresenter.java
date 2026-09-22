package br.edu.senai.fatesg.avcar.swing.views.presenters;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import br.edu.senai.fatesg.avcar.business.pecas.PecaController;
import br.edu.senai.fatesg.avcar.business.fornecedores.FornecedorController;
import br.edu.senai.fatesg.avcar.business.fornecedores.FornecedorDTO;
import br.edu.senai.fatesg.avcar.business.pecas.PecaDTO;
import br.edu.senai.fatesg.avcar.swing.views.CadastroPeca;
import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class CadastroPecaPresenter {

    @Autowired
    private PecaController pecaController;

    @Autowired
    private FornecedorController fornecedorController;

    private Long idEdicao = null;
    private List<FornecedorDTO> listaFornecedores;

    public void initLogic(CadastroPeca view) {
        try {
            javax.swing.text.MaskFormatter maskData = new javax.swing.text.MaskFormatter("##/##/####");
            maskData.setPlaceholderCharacter('_');
            maskData.install(view.getFormattedTextFieldDataCompra());

            view.getFormattedTextFieldPrecoCusto().setFormatterFactory(null);
            br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil.setApenasMoeda(view.getFormattedTextFieldPrecoCusto());
            
            view.getFormattedTextFieldPrecoVenda().setFormatterFactory(null);
            br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil.setApenasMoeda(view.getFormattedTextFieldPrecoVenda());
            
            br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil.setApenasNumeros(view.getTextFieldEstoque(), 6);
            br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil.setApenasNumeros(view.getTextFieldGarantia(), 4);
            br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil.setApenasNumeros(view.getTextFieldCodNacional(), 15);
            br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil.setApenasNumeros(view.getTextFieldCodInterno(), 15);
            
            // Listeners
            view.getButtonSalvar().addActionListener(e -> salvarPeca(view));
            view.getButtonCancelar().addActionListener(e -> {
                java.awt.Window win = javax.swing.SwingUtilities.getWindowAncestor(view);
                if (win != null) win.dispose();
            });
        } catch (Exception e) {
            java.util.logging.Logger.getLogger("ErrorLog").log(java.util.logging.Level.SEVERE, "Erro capturado", e);
        }
    }

    public void preparar(CadastroPeca view, Long id) {
        this.idEdicao = id;
        view.getTextFieldCodNacional().setText("");
        view.getTextFieldCodInterno().setText("");
        view.getTextFieldNome().setText("");
        view.getTextFieldDescricao().setText("");
        view.getTextFieldFabricante().setText("");
        view.getTextFieldCategoria().setText("");
        view.getFormattedTextFieldPrecoCusto().setText("");
        view.getFormattedTextFieldPrecoCusto().setValue(null);
        view.getFormattedTextFieldPrecoVenda().setText("");
        view.getFormattedTextFieldPrecoVenda().setValue(null);
        view.getTextFieldEstoque().setText("");
        view.getTextFieldGarantia().setText("180");
        view.getFormattedTextFieldDataCompra().setText("");
        
        carregarComboFornecedores(view);

        if (id != null) {
            PecaDTO p = pecaController.buscarPorId(id).getBody();
            if (p != null) {
                view.getTextFieldCodNacional().setText(String.valueOf(p.getCodigoNacional()));
                view.getTextFieldCodInterno().setText(p.getCodigoInterno() != null ? p.getCodigoInterno() : "");
                view.getTextFieldNome().setText(p.getNome() != null ? p.getNome() : "");
                view.getTextFieldDescricao().setText(p.getDescricao() != null ? p.getDescricao() : "");
                view.getTextFieldFabricante().setText(p.getFabricante() != null ? p.getFabricante() : "");
                view.getTextFieldCategoria().setText(p.getCategoria() != null ? p.getCategoria() : "");
                view.getFormattedTextFieldPrecoCusto().setText(String.format("%.2f", p.getPrecoCusto()));
                view.getFormattedTextFieldPrecoVenda().setText(String.format("%.2f", p.getPrecoVenda()));
                view.getTextFieldEstoque().setText(String.valueOf(p.getQuantidadeEstoque()));
                view.getTextFieldGarantia().setText(String.valueOf(p.getGarantiaPeca()));
                if (p.getDataCompraPeca() != null) {
                    view.getFormattedTextFieldDataCompra().setText(p.getDataCompraPeca().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
                if (p.getFornecedorId() != null && listaFornecedores != null) {
                    for (int i = 0; i < listaFornecedores.size(); i++) {
                        if (listaFornecedores.get(i).getId().equals(p.getFornecedorId())) {
                            view.getComboBoxFornecedor().setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void carregarComboFornecedores(CadastroPeca view) {
        view.getComboBoxFornecedor().removeAllItems();
        listaFornecedores = fornecedorController.listar(false).getBody();
        if (listaFornecedores != null) {
            for (FornecedorDTO f : listaFornecedores) {
                view.getComboBoxFornecedor().addItem(f.getRazaoSocial() + " (" + f.getCnpj() + ")");
            }
        }
    }

    private void salvarPeca(CadastroPeca view) {
        try {
            if (view.getTextFieldCodNacional().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "O código nacional é obrigatório!");
                return;
            }
            if (view.getTextFieldNome().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "O nome da peça é obrigatório!");
                return;
            }
            if (view.getTextFieldFabricante().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "O fabricante é obrigatório!");
                return;
            }
            if (view.getTextFieldCategoria().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "A categoria é obrigatória!");
                return;
            }
            if (view.getFormattedTextFieldPrecoCusto().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "O preço de custo é obrigatório!");
                return;
            }
            if (view.getFormattedTextFieldPrecoVenda().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "O preço de venda é obrigatório!");
                return;
            }
            if (view.getTextFieldEstoque().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "O estoque é obrigatório!");
                return;
            }
            if (view.getTextFieldGarantia().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "A garantia é obrigatória!");
                return;
            }
            if (view.getComboBoxFornecedor().getSelectedIndex() < 0) {
                JOptionPane.showMessageDialog(view, "O fornecedor é obrigatório!");
                return;
            }

            String dtStr = view.getFormattedTextFieldDataCompra().getText().replace("_", "").replace("/", "").trim();
            if (dtStr.isEmpty()) {
                JOptionPane.showMessageDialog(view, "A data da compra é obrigatória!");
                return;
            }

            long codNac = 0;
            if (!view.getTextFieldCodNacional().getText().trim().isEmpty()) {
                codNac = Long.parseLong(view.getTextFieldCodNacional().getText().trim());
            }

            double custo = 0;
            if (!view.getFormattedTextFieldPrecoCusto().getText().trim().isEmpty()) {
                custo = Double.parseDouble(view.getFormattedTextFieldPrecoCusto().getText().replace("R$", "").replace(".", "").replace(",", ".").trim());
            }

            double venda = 0;
            if (!view.getFormattedTextFieldPrecoVenda().getText().trim().isEmpty()) {
                venda = Double.parseDouble(view.getFormattedTextFieldPrecoVenda().getText().replace("R$", "").replace(".", "").replace(",", ".").trim());
            }

            int estoque = 0;
            if (!view.getTextFieldEstoque().getText().trim().isEmpty()) {
                estoque = Integer.parseInt(view.getTextFieldEstoque().getText().trim());
            }

            int garan = 180;
            if (!view.getTextFieldGarantia().getText().trim().isEmpty()) {
                garan = Integer.parseInt(view.getTextFieldGarantia().getText().trim());
            }

            LocalDate dataC = null;
            dtStr = view.getFormattedTextFieldDataCompra().getText().replace("_", "").replace("/", "").trim();
            if (!dtStr.isEmpty()) {
                dataC = LocalDate.parse(view.getFormattedTextFieldDataCompra().getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }

            Long fornId = null;
            if (view.getComboBoxFornecedor().getSelectedIndex() >= 0 && listaFornecedores != null) {
                fornId = listaFornecedores.get(view.getComboBoxFornecedor().getSelectedIndex()).getId();
            }

            PecaController.PecaRequest req = new PecaController.PecaRequest(
                codNac, view.getTextFieldCodInterno().getText().trim(), view.getTextFieldNome().getText().trim(),
                view.getTextFieldDescricao().getText().trim(), view.getTextFieldFabricante().getText().trim(),
                view.getTextFieldCategoria().getText().trim(), custo, venda, estoque, garan, dataC, fornId
            );

            if (this.idEdicao == null) {
                pecaController.salvar(req);
                JOptionPane.showMessageDialog(view, "Peça cadastrada com sucesso!");
            } else {
                pecaController.atualizar(this.idEdicao, req);
                JOptionPane.showMessageDialog(view, "Peça atualizada com sucesso!");
            }

            java.awt.Window win = javax.swing.SwingUtilities.getWindowAncestor(view);
            if (win != null) win.dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Verifique os campos numéricos (Código, Preço, Estoque).");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Erro ao salvar: " + ex.getMessage());
        }
    }
}
