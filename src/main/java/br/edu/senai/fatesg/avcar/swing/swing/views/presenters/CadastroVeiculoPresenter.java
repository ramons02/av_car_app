package br.edu.senai.fatesg.avcar.swing.views.presenters;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import br.edu.senai.fatesg.avcar.business.clientes.ClienteController;
import br.edu.senai.fatesg.avcar.business.veiculos.VeiculoController;
import br.edu.senai.fatesg.avcar.swing.views.CadastroVeiculo;
import br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil;
import br.edu.senai.fatesg.avcar.swing.views.utils.VeiculoFormUtil;
import javax.swing.JOptionPane;

@Component
public class CadastroVeiculoPresenter {

    @Autowired
    private ClienteController clienteController;
    
    @Autowired
    private VeiculoController veiculoController;

    private Long idVeiculoEmEdicao = null;

    public void initLogic(CadastroVeiculo view) {
        recarregarCombos(view);
        
        FormatadorUtil.setCaixaAlta(view.getTextFieldPlaca(), 7);
        FormatadorUtil.setCaixaAlta(view.getTextFieldChassi(), 17);
        FormatadorUtil.setApenasNumeros(view.getTextFieldAnoFabricacao(), 4);
        FormatadorUtil.setApenasNumeros(view.getTextFieldAnoModelo(), 4);
    }

    private void recarregarCombos(CadastroVeiculo view) {
        VeiculoFormUtil.configurarComboMarcasEModelos(
            view.getComboBoxMarca(), view.getComboBoxModelo(), veiculoController
        );

        VeiculoFormUtil.configurarComboClientes(
            view.getComboBoxCliente(), clienteController
        );
    }

    public void preencherParaEdicao(CadastroVeiculo view, Long id) {
        recarregarCombos(view);
        this.idVeiculoEmEdicao = id;
        
        br.edu.senai.fatesg.avcar.business.veiculos.VeiculoDTO veiculo = veiculoController.buscarPorId(id).getBody();
        
        if (veiculo != null) {
            view.getTextFieldPlaca().setText(veiculo.getPlaca());
            view.getTextFieldChassi().setText(veiculo.getChassi());
            view.getTextFieldAnoFabricacao().setText(String.valueOf(veiculo.getAnoFabricacao()));
            view.getTextFieldAnoModelo().setText(String.valueOf(veiculo.getAnoModelo()));
            view.getTextFieldCor().setText(veiculo.getCor());
            view.getTextFieldAcessorios().setText(veiculo.getAcessorios());
            view.getFormattedTextFieldKMAtual().setValue(veiculo.getQuilometragem());

            boolean foundCliente = false;
            for (int i = 0; i < view.getComboBoxCliente().getItemCount(); i++) {
                Object item = view.getComboBoxCliente().getItemAt(i);
                if (item instanceof VeiculoFormUtil.ClienteItem) {
                    if (((VeiculoFormUtil.ClienteItem) item).id.equals(veiculo.getClienteId())) {
                        view.getComboBoxCliente().setSelectedIndex(i);
                        foundCliente = true;
                        break;
                    }
                }
            }
            
            if (!foundCliente && veiculo.getClienteId() != null) {
                try {
                    var cli = clienteController.buscarPorId(veiculo.getClienteId()).getBody();
                    if (cli != null) {
                        VeiculoFormUtil.ClienteItem itemInativo = new VeiculoFormUtil.ClienteItem(cli.getId(), cli.getNome() + " (INATIVO)");
                        view.getComboBoxCliente().addItem(itemInativo);
                        view.getComboBoxCliente().setSelectedItem(itemInativo);
                    }
                } catch (Exception ignored) {}
            }

            for (int i = 0; i < view.getComboBoxMarca().getItemCount(); i++) {
                Object item = view.getComboBoxMarca().getItemAt(i);
                if (item instanceof VeiculoFormUtil.MarcaItem) {
                    if (((VeiculoFormUtil.MarcaItem) item).nome.equals(veiculo.getMarcaNome())) {
                        view.getComboBoxMarca().setSelectedIndex(i);
                        break;
                    }
                }
            }

            for (int i = 0; i < view.getComboBoxModelo().getItemCount(); i++) {
                String modStr = view.getComboBoxModelo().getItemAt(i) != null ? view.getComboBoxModelo().getItemAt(i).toString() : "";
                if (modStr.contains("(" + veiculo.getModeloId() + ")")) {
                    view.getComboBoxModelo().setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    public void prepararParaNovo(CadastroVeiculo view) {
        recarregarCombos(view);
        this.idVeiculoEmEdicao = null;
        limparCampos(view);
    }

    public void salvarVeiculo(CadastroVeiculo view) {
        try {
            if (view.getTextFieldPlaca().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "A placa do veículo é obrigatória!");
                return;
            }
            
            String placaLimpa = view.getTextFieldPlaca().getText().replaceAll("[^A-Za-z0-9]", "").toUpperCase();
            if (placaLimpa.length() != 7) {
                JOptionPane.showMessageDialog(view, "A placa deve conter exatamente 7 caracteres (Padrão Mercosul ou Antigo).");
                return;
            }
            
            if (view.getTextFieldChassi().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "O chassi é obrigatório!");
                return;
            }
            if (view.getTextFieldAnoFabricacao().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "O ano de fabricação é obrigatório!");
                return;
            }
            if (view.getTextFieldAnoModelo().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "O ano do modelo é obrigatório!");
                return;
            }
            if (view.getTextFieldCor().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "A cor do veículo é obrigatória!");
                return;
            }
            if (view.getFormattedTextFieldKMAtual().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "A quilometragem é obrigatória!");
                return;
            }

            int af = Integer.parseInt(view.getTextFieldAnoFabricacao().getText().trim());
            int am = Integer.parseInt(view.getTextFieldAnoModelo().getText().trim());
            int km = Integer.parseInt(view.getFormattedTextFieldKMAtual().getText().trim().replace(".", "").replace(",", ""));
            
            Long modeloId = null;
            if (view.getComboBoxModelo().getSelectedItem() != null) {
                String modStr = view.getComboBoxModelo().getSelectedItem().toString();
                if (modStr.contains("(")) {
                    int start = modStr.lastIndexOf('(') + 1;
                    int end = modStr.lastIndexOf(')');
                    modeloId = Long.parseLong(modStr.substring(start, end));
                }
            }
            
            if (modeloId == null) {
                JOptionPane.showMessageDialog(view, "Selecione uma marca e um modelo válidos!");
                return;
            }
            
            Long clienteId = null;
            Object selectedCliente = view.getComboBoxCliente().getSelectedItem();
            if (selectedCliente != null) {
                if (selectedCliente instanceof VeiculoFormUtil.ClienteItem) {
                    clienteId = ((VeiculoFormUtil.ClienteItem) selectedCliente).id;
                }
            }
            
            if (clienteId == null) {
                JOptionPane.showMessageDialog(view, "Selecione o cliente dono do veículo!");
                return;
            }

            VeiculoController.VeiculoRequest req = new VeiculoController.VeiculoRequest(
                placaLimpa, view.getTextFieldChassi().getText(), af, am, view.getTextFieldCor().getText(), 
                km, view.getTextFieldAcessorios().getText(), modeloId, clienteId
            );

            if (idVeiculoEmEdicao == null) {
                veiculoController.salvar(req);
                JOptionPane.showMessageDialog(view, "Veículo cadastrado com sucesso!");
            } else {
                veiculoController.atualizar(idVeiculoEmEdicao, req);
                JOptionPane.showMessageDialog(view, "Veículo atualizado com sucesso!");
            }
            cancelar(view);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Anos e Quilometragem devem ser números inteiros.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erro ao salvar: " + e.getMessage());
        }
    }

    private void limparCampos(CadastroVeiculo view) {
        view.getTextFieldPlaca().setText("");
        view.getTextFieldChassi().setText("");
        view.getTextFieldAnoFabricacao().setText("");
        view.getTextFieldAnoModelo().setText("");
        view.getTextFieldCor().setText("");
        view.getTextFieldAcessorios().setText("");
        view.getFormattedTextFieldKMAtual().setValue(null);
        if (view.getComboBoxMarca().getItemCount() > 0) view.getComboBoxMarca().setSelectedIndex(0);
        if (view.getComboBoxCliente().getItemCount() > 0) view.getComboBoxCliente().setSelectedIndex(0);
    }

    public void cancelar(CadastroVeiculo view) {
        limparCampos(view);
        java.awt.Window win = javax.swing.SwingUtilities.getWindowAncestor(view);
        if (win != null) win.dispose();
    }
}
