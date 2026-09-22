package br.edu.senai.fatesg.avcar.swing.views.presenters;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoController;
import br.edu.senai.fatesg.avcar.swing.views.CadastroDesconto;
import br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil;
import br.edu.senai.fatesg.avcar.swing.views.utils.MensagemUtil;
import javax.swing.SwingUtilities;

@Component
public class CadastroDescontoPresenter {

    @Autowired
    private OrdemServicoController ordemServicoController;

    private Long osId;

    public void initLogic(CadastroDesconto view) {
        FormatadorUtil.setApenasMoeda(view.getTextFieldAplicarDesconto());

        view.getButtonOk().addActionListener(e -> aplicarDesconto(view));
        view.getButtonCancelar().addActionListener(e -> fecharModal(view));
    }

    public void preparar(CadastroDesconto view, Long osId) {
        this.osId = osId;
        view.getTextFieldAplicarDesconto().setText("");
    }

    private void aplicarDesconto(CadastroDesconto view) {
        try {
            String texto = view.getTextFieldAplicarDesconto().getText().trim().replace(',', '.');
            if (texto.isEmpty()) {
                MensagemUtil.mostrarErro(view, "Informe o valor do desconto.");
                return;
            }
            
            double valorDesconto = Double.parseDouble(texto);
            if (valorDesconto < 0) {
                MensagemUtil.mostrarErro(view, "O desconto não pode ser negativo.");
                return;
            }

            ordemServicoController.aplicarDesconto(osId, valorDesconto);
            MensagemUtil.mostrarSucesso(view, "Desconto de R$ " + texto + " aplicado.");
            fecharModal(view);
        } catch (NumberFormatException ex) {
            MensagemUtil.mostrarErro(view, "Digite um número válido para o desconto.");
        } catch (Exception e) {
            MensagemUtil.mostrarErro(view, "Erro ao aplicar desconto: " + e.getMessage());
        }
    }

    private void fecharModal(CadastroDesconto view) {
        java.awt.Window win = SwingUtilities.getWindowAncestor(view);
        if (win != null) {
            win.dispose();
        }
    }
}
