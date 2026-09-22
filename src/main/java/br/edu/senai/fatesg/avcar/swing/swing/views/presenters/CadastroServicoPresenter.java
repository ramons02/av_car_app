package br.edu.senai.fatesg.avcar.swing.views.presenters;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import br.edu.senai.fatesg.avcar.business.servicos.ServicoController;
import br.edu.senai.fatesg.avcar.business.servicos.ServicoDTO;
import br.edu.senai.fatesg.avcar.swing.views.CadastroServico;
import br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil;
import javax.swing.JOptionPane;

@Component
public class CadastroServicoPresenter {

    @Autowired
    private ServicoController servicoController;

    private Long idEdicao = null;

    public void initLogic(CadastroServico view) {
        view.getFormattedTextFieldValorServico().setFormatterFactory(null);
        FormatadorUtil.setApenasMoeda(view.getFormattedTextFieldValorServico());
        
        view.getFormattedTextFieldGarantiaServico().setFormatterFactory(null);
        FormatadorUtil.setApenasNumeros(view.getFormattedTextFieldGarantiaServico(), 5);
        
        view.getFormattedTextFieldTempoEstimadoServico().setFormatterFactory(null);
        FormatadorUtil.setApenasNumeros(view.getFormattedTextFieldTempoEstimadoServico(), 5);
    }

    public void preencherParaEdicao(CadastroServico view, Long id) {
        this.idEdicao = id;
        ServicoDTO servico = servicoController.buscarPorId(id).getBody();
        if (servico != null) {
            view.getTextFieldNomeServico().setText(servico.getNomeServico() != null ? servico.getNomeServico() : "");
            view.getTextFieldDescricaoServico().setText(servico.getDescricaoServico() != null ? servico.getDescricaoServico() : "");
            view.getFormattedTextFieldValorServico().setValue(null);
            view.getFormattedTextFieldValorServico().setText(String.format(new java.util.Locale("pt", "BR"), "%.2f", servico.getValorServico()));
            view.getFormattedTextFieldGarantiaServico().setValue(null);
            view.getFormattedTextFieldGarantiaServico().setText(String.valueOf(servico.getGarantiaDias()));
            
            String tempo = servico.getTempoEstimado() != null ? servico.getTempoEstimado().replaceAll("[^0-9]", "") : "";
            if (!tempo.isEmpty()) {
                try {
                    view.getFormattedTextFieldTempoEstimadoServico().setValue(Long.parseLong(tempo));
                } catch (Exception e) {
                    view.getFormattedTextFieldTempoEstimadoServico().setText(tempo);
                }
            } else {
                view.getFormattedTextFieldTempoEstimadoServico().setValue(null);
                view.getFormattedTextFieldTempoEstimadoServico().setText("");
            }
        }
    }

    public void prepararParaNovo(CadastroServico view) {
        this.idEdicao = null;
        limparCampos(view);
    }

    public void salvarServico(CadastroServico view) {
        try {
            if (view.getTextFieldNomeServico().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "O nome do serviço é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (view.getTextFieldDescricaoServico().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "A descrição do serviço é obrigatória!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (view.getFormattedTextFieldValorServico().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "O valor do serviço é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (view.getFormattedTextFieldGarantiaServico().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "A garantia do serviço é obrigatória!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (view.getFormattedTextFieldTempoEstimadoServico().getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "O tempo estimado do serviço é obrigatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String nome = view.getTextFieldNomeServico().getText().trim();
            String desc = view.getTextFieldDescricaoServico().getText().trim();
            
            double valor = 0.0;
            Object valObj = view.getFormattedTextFieldValorServico().getValue();
            if (valObj instanceof Number) {
                valor = ((Number)valObj).doubleValue();
            } else if (valObj != null) {
                String str = valObj.toString().replace("R$", "").replace(".", "").replace(",", ".").trim();
                if (!str.isEmpty()) valor = Double.parseDouble(str);
            } else if (!view.getFormattedTextFieldValorServico().getText().trim().isEmpty()) {
                String str = view.getFormattedTextFieldValorServico().getText().replace("R$", "").replace(".", "").replace(",", ".").trim();
                if (!str.isEmpty()) valor = Double.parseDouble(str);
            }

            int garantia = 0;
            Object garObj = view.getFormattedTextFieldGarantiaServico().getValue();
            if (garObj instanceof Number) {
                garantia = ((Number)garObj).intValue();
            } else if (garObj != null) {
                garantia = Integer.parseInt(garObj.toString());
            } else if (!view.getFormattedTextFieldGarantiaServico().getText().trim().isEmpty()) {
                garantia = Integer.parseInt(view.getFormattedTextFieldGarantiaServico().getText().trim());
            }

            String tempo = "0";
            Object tempoObj = view.getFormattedTextFieldTempoEstimadoServico().getValue();
            if (tempoObj != null) {
                tempo = tempoObj.toString();
            } else if (!view.getFormattedTextFieldTempoEstimadoServico().getText().trim().isEmpty()) {
                tempo = view.getFormattedTextFieldTempoEstimadoServico().getText().trim();
            }

            ServicoController.ServicoRequest req = new ServicoController.ServicoRequest(nome, desc, valor, garantia, tempo);

            if (this.idEdicao == null) {
                servicoController.salvar(req);
                JOptionPane.showMessageDialog(view, "Serviço cadastrado com sucesso!");
            } else {
                servicoController.atualizar(this.idEdicao, req);
                JOptionPane.showMessageDialog(view, "Serviço atualizado com sucesso!");
            }

            cancelar(view);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erro ao salvar serviço: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos(CadastroServico view) {
        view.getTextFieldNomeServico().setText("");
        view.getTextFieldDescricaoServico().setText("");
        view.getFormattedTextFieldValorServico().setValue(null);
        view.getFormattedTextFieldValorServico().setText("");
        view.getFormattedTextFieldGarantiaServico().setValue(null);
        view.getFormattedTextFieldGarantiaServico().setText("");
        view.getFormattedTextFieldTempoEstimadoServico().setValue(null);
        view.getFormattedTextFieldTempoEstimadoServico().setText("");
    }

    public void cancelar(CadastroServico view) {
        java.awt.Window win = javax.swing.SwingUtilities.getWindowAncestor(view);
        if (win != null) win.dispose();
    }
}
