package br.edu.senai.fatesg.avcar.swing.views.presenters;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoController;
import br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoDTO;
import br.edu.senai.fatesg.avcar.datastructures.FilaEsperaOS;
import br.edu.senai.fatesg.avcar.swing.views.CadastroFilaDeEspera;
import br.edu.senai.fatesg.avcar.swing.views.utils.MensagemUtil;
import javax.swing.SwingUtilities;

@Component
public class CadastroFilaDeEsperaPresenter {

    @Autowired
    private OrdemServicoController ordemServicoController;

    // Use singleton queue for the application
    private static final FilaEsperaOS<OrdemServicoDTO> FILA = new FilaEsperaOS<>();

    public void initLogic(CadastroFilaDeEspera view) {
        view.getButtonAddNumOSPrioridade().addActionListener(e -> adicionarFila(view));
        view.getToggleButtonRemoverFila().addActionListener(e -> removerFila(view));
        view.getButtonProxFila().addActionListener(e -> proximaFila(view));
        view.getButtonLimparFila().addActionListener(e -> limparFila(view));
        view.getButtonFecharFilaAtend().addActionListener(e -> fechar(view));
    }

    public void carregarDados(CadastroFilaDeEspera view) {
        view.getButtonAddNumOSPrioridade().setText("Adicionar à Fila");
        view.getButtonProxFila().setText("Próxima OS");
        view.getButtonLimparFila().setText("Esvaziar Fila");
        view.getToggleButtonRemoverFila().setText("Atender OS (Remover)");
        view.getLabelNumOS().setText("Selecione a OS:");

        view.getComboBoxOSFila().removeAllItems();
        var lista = ordemServicoController.listar().getBody();
        if (lista != null) {
            for (var os : lista) {
                String status = os.getStatus();
                if (status != null && (status.equalsIgnoreCase("EM_ORCAMENTO") || status.equalsIgnoreCase("Em orçamento") || 
                                       status.equalsIgnoreCase("EM_EXECUCAO") || status.equalsIgnoreCase("Em execução") || 
                                       status.equalsIgnoreCase("Aguardando peça"))) {
                    view.getComboBoxOSFila().addItem(os);
                }
            }
        }
        
        view.getComboBoxOSFila().setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof OrdemServicoDTO) {
                    var os = (OrdemServicoDTO) value;
                    setText("#" + os.getNumeroOs() + " | " + os.getVeiculo() + " | " + os.getStatus());
                }
                return this;
            }
        });
        
        atualizarAreaDeTexto(view);
    }

    private void atualizarAreaDeTexto(CadastroFilaDeEspera view) {
        view.getTextAreaSaidaFilaAtend().setText("");
        var itens = FILA.listar();
        if (itens.isEmpty()) {
            view.getTextAreaSaidaFilaAtend().setText("Fila vazia.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < itens.size(); i++) {
            var os = itens.get(i);
            sb.append((i + 1)).append("º - #")
              .append(os.getNumeroOs()).append(" | ")
              .append(os.getVeiculo()).append(" | ")
              .append(os.getStatus()).append("\n");
        }
        view.getTextAreaSaidaFilaAtend().setText(sb.toString());
    }

    private void adicionarFila(CadastroFilaDeEspera view) {
        OrdemServicoDTO os = (OrdemServicoDTO) view.getComboBoxOSFila().getSelectedItem();
        if (os == null) {
            MensagemUtil.mostrarErro(view, "Selecione uma OS na lista.");
            return;
        }
        FILA.enqueue(os);
        atualizarAreaDeTexto(view);
    }

    private void removerFila(CadastroFilaDeEspera view) {
        if (FILA.isEmpty()) {
            MensagemUtil.mostrarErro(view, "A fila já está vazia.");
            return;
        }
        OrdemServicoDTO removido = FILA.dequeue();
        MensagemUtil.mostrarSucesso(view, "A seguinte OS foi retirada da fila para atendimento:\nOS #" + removido.getNumeroOs() + " | Veículo: " + removido.getVeiculo());
        atualizarAreaDeTexto(view);
    }

    private void proximaFila(CadastroFilaDeEspera view) {
        if (FILA.isEmpty()) {
            MensagemUtil.mostrarErro(view, "A fila está vazia.");
            return;
        }
        OrdemServicoDTO prox = FILA.peek();
        MensagemUtil.mostrarSucesso(view, "A próxima OS a ser atendida é a:\nOS #" + prox.getNumeroOs() + " | Veículo: " + prox.getVeiculo());
    }

    private void limparFila(CadastroFilaDeEspera view) {
        while (!FILA.isEmpty()) {
            FILA.dequeue();
        }
        atualizarAreaDeTexto(view);
        MensagemUtil.mostrarSucesso(view, "A fila de atendimento foi esvaziada com sucesso.");
    }

    private void fechar(CadastroFilaDeEspera view) {
        java.awt.Window win = SwingUtilities.getWindowAncestor(view);
        if (win != null) {
            win.dispose();
        }
    }
}
