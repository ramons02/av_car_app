package br.edu.senai.fatesg.avcar.swing.views.presenters;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoController;
import br.edu.senai.fatesg.avcar.swing.views.CadastroGarantia;
import br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil;
import br.edu.senai.fatesg.avcar.swing.views.utils.MensagemUtil;
import javax.swing.SwingUtilities;

@Component
public class CadastroGarantiaPresenter {

    @Autowired
    private OrdemServicoController ordemServicoController;

    private Long osId;

    public void initLogic(CadastroGarantia view) {
        // Limita a 4 dígitos para dias (ex: 9999 dias)
        FormatadorUtil.setApenasNumeros(view.getTextFieldQdteValor(), 4);

        view.getButtonOk().addActionListener(e -> aplicarGarantia(view));
        view.getButtonCancelar().addActionListener(e -> fecharModal(view));
    }

    public void preparar(CadastroGarantia view, Long osId) {
        this.osId = osId;
        view.getTextFieldQdteValor().setText("");
    }

    private void aplicarGarantia(CadastroGarantia view) {
        try {
            String texto = view.getTextFieldQdteValor().getText().trim();
            if (texto.isEmpty()) {
                MensagemUtil.mostrarErro(view, "Informe a quantidade de dias de garantia.");
                return;
            }
            
            int dias = Integer.parseInt(texto);
            if (dias < 0) {
                MensagemUtil.mostrarErro(view, "A quantidade de dias não pode ser negativa.");
                return;
            }

            ordemServicoController.aplicarGarantia(osId, dias);
            MensagemUtil.mostrarSucesso(view, "Garantia adicional de " + dias + " dias aplicada com sucesso.");
            fecharModal(view);
        } catch (NumberFormatException ex) {
            MensagemUtil.mostrarErro(view, "Digite um número válido para os dias de garantia.");
        } catch (Exception e) {
            MensagemUtil.mostrarErro(view, "Erro ao aplicar garantia: " + e.getMessage());
        }
    }

    private void fecharModal(CadastroGarantia view) {
        java.awt.Window win = SwingUtilities.getWindowAncestor(view);
        if (win != null) {
            win.dispose();
        }
    }
}
