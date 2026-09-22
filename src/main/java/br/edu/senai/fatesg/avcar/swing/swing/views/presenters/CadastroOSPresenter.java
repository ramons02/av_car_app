package br.edu.senai.fatesg.avcar.swing.views.presenters;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoController;
import br.edu.senai.fatesg.avcar.business.veiculos.VeiculoController;
import br.edu.senai.fatesg.avcar.business.veiculos.VeiculoDTO;
import br.edu.senai.fatesg.avcar.business.colaboradores.ColaboradorController;
import br.edu.senai.fatesg.avcar.business.colaboradores.ColaboradorDTO;
import br.edu.senai.fatesg.avcar.swing.views.CadastroOS;
import br.edu.senai.fatesg.avcar.swing.views.utils.MensagemUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

@Component
public class CadastroOSPresenter {

    @Autowired
    private OrdemServicoController ordemServicoController;

    @Autowired
    private VeiculoController veiculoController;

    @Autowired
    private ColaboradorController colaboradorController;

    private List<Long> veiculoIds = new ArrayList<>();
    private List<Long> colaboradorIds = new ArrayList<>();

    public void initLogic(CadastroOS view) {
        view.getButtonSalvarCadastroOS().addActionListener(e -> salvarOS(view));
        view.getButtonCancelarCadastroOS().addActionListener(e -> cancelar(view));
        
        carregarVeiculos(view);
        carregarColaboradores(view);
        try {
            javax.swing.text.MaskFormatter mf = new javax.swing.text.MaskFormatter("##/##/####");
            mf.setPlaceholderCharacter('_');
            mf.install(view.getFormattedTextFieldDataEntradaCadastroOS());
        } catch (Exception ignored) {}
        view.getFormattedTextFieldDataEntradaCadastroOS().setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    public void prepararParaNovo(CadastroOS view) {
        carregarVeiculos(view);
        carregarColaboradores(view);
        view.getFormattedTextFieldDataEntradaCadastroOS().setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        view.getTextAreaDefeitoRelatadoCadastroOS().setText("");
    }

    private void carregarVeiculos(CadastroOS view) {
        try {
            List<VeiculoDTO> veiculos = veiculoController.listar(true).getBody();
            view.getComboBoxVeiculoCadastroOS().removeAllItems();
            veiculoIds.clear();
            if (veiculos != null) {
                for (VeiculoDTO v : veiculos) {
                    view.getComboBoxVeiculoCadastroOS().addItem(v.getPlaca() + " - " + v.getModeloNome());
                    veiculoIds.add(v.getId());
                }
            }
        } catch (Exception e) {
            MensagemUtil.mostrarErro(view, "Erro ao carregar veículos: " + e.getMessage());
        }
    }

    private void carregarColaboradores(CadastroOS view) {
        try {
            List<ColaboradorDTO> colaboradores = colaboradorController.listar(false).getBody();
            if (view.getComboBoxResponsavelCadastroOS() == null) return;
            view.getComboBoxResponsavelCadastroOS().removeAllItems();
            colaboradorIds.clear();
            
            // Opção vazia
            view.getComboBoxResponsavelCadastroOS().addItem("Selecione...");
            colaboradorIds.add(null);
            
            if (colaboradores != null) {
                for (ColaboradorDTO c : colaboradores) {
                    view.getComboBoxResponsavelCadastroOS().addItem(c.getNome());
                    colaboradorIds.add(c.getId());
                }
            }
        } catch (Exception e) {
            MensagemUtil.mostrarErro(view, "Erro ao carregar colaboradores: " + e.getMessage());
        }
    }

    private void salvarOS(CadastroOS view) {
        int index = view.getComboBoxVeiculoCadastroOS().getSelectedIndex();
        if (index < 0 || index >= veiculoIds.size()) {
            MensagemUtil.mostrarErro(view, "Selecione um veículo.");
            return;
        }

        Long veiculoId = veiculoIds.get(index);
        
        Long responsavelId = null;
        if (view.getComboBoxResponsavelCadastroOS() != null) {
            int colabIndex = view.getComboBoxResponsavelCadastroOS().getSelectedIndex();
            if (colabIndex > 0 && colabIndex < colaboradorIds.size()) {
                responsavelId = colaboradorIds.get(colabIndex);
            } else if (colabIndex == 0) {
                MensagemUtil.mostrarErro(view, "Selecione um responsável para a OS.");
                return;
            }
        }
        
        String dataEntrada = view.getFormattedTextFieldDataEntradaCadastroOS().getText().trim();
        String defeito = view.getTextAreaDefeitoRelatadoCadastroOS().getText().trim();

        if (dataEntrada.isEmpty()) {
            MensagemUtil.mostrarErro(view, "A data de entrada é obrigatória.");
            return;
        }
        
        if (defeito.isEmpty()) {
            MensagemUtil.mostrarErro(view, "O defeito relatado é obrigatório.");
            return;
        }

        try {
            LocalDate date = LocalDate.parse(dataEntrada, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            dataEntrada = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            MensagemUtil.mostrarErro(view, "Data de entrada inválida. Use o formato DD/MM/AAAA.");
            return;
        }

        try {
            OrdemServicoController.CriarOSRequest req = new OrdemServicoController.CriarOSRequest(
                veiculoId,
                responsavelId,
                dataEntrada,
                defeito,
                null // formaPagamento
            );

            ordemServicoController.criar(req);
            MensagemUtil.mostrarSucesso(view, "Ordem de Serviço criada com sucesso!");
            
            fecharJanela(view);
            
        } catch (Exception e) {
            MensagemUtil.mostrarErro(view, "Erro ao criar OS: " + e.getMessage());
        }
    }

    private void cancelar(CadastroOS view) {
        fecharJanela(view);
    }

    private void fecharJanela(CadastroOS view) {
        java.awt.Window win = javax.swing.SwingUtilities.getWindowAncestor(view);
        if (win != null) {
            win.dispose();
        }
    }
}
