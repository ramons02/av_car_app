package br.edu.senai.fatesg.avcar.swing.views.presenters;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import br.edu.senai.fatesg.avcar.business.colaboradores.ColaboradorController;
import br.edu.senai.fatesg.avcar.business.colaboradores.ColaboradorDTO;
import br.edu.senai.fatesg.avcar.business.fornecedores.FornecedorController;
import br.edu.senai.fatesg.avcar.business.fornecedores.FornecedorDTO;
import br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoController;
import br.edu.senai.fatesg.avcar.business.pecas.PecaController;
import br.edu.senai.fatesg.avcar.business.pecas.PecaDTO;
import br.edu.senai.fatesg.avcar.business.servicos.ItemServicoDTO;
import br.edu.senai.fatesg.avcar.business.pecas.ItemPecaDTO;
import br.edu.senai.fatesg.avcar.business.servicos.ServicoController;
import br.edu.senai.fatesg.avcar.business.servicos.ServicoDTO;
import br.edu.senai.fatesg.avcar.business.servicos.ServicoExternoDTO;
import br.edu.senai.fatesg.avcar.business.parceiros.ParceiroExternoController;
import br.edu.senai.fatesg.avcar.business.parceiros.ParceiroExternoDTO;
import br.edu.senai.fatesg.avcar.swing.views.CadastroItemOS;
import br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil;
import br.edu.senai.fatesg.avcar.swing.views.utils.MensagemUtil;

import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

@Component
public class CadastroItemOSPresenter {

    @Autowired private OrdemServicoController ordemServicoController;
    @Autowired private ServicoController servicoController;
    @Autowired private PecaController pecaController;
    @Autowired private ColaboradorController colaboradorController;
    @Autowired private FornecedorController fornecedorController;
    @Autowired private ParceiroExternoController parceiroExternoController;

    private Long osId;
    private java.util.List<Long> idsServicos = new java.util.ArrayList<>();
    private java.util.List<Long> idsPecas = new java.util.ArrayList<>();
    private java.util.List<Long> idsExternos = new java.util.ArrayList<>();

    public void initLogic(CadastroItemOS view) {
        view.getButtonAdicionarItemServico().addActionListener(e -> adicionarItemServico(view));
        view.getButtonRemoverItemServico().addActionListener(e -> removerItemServico(view));
        view.getButtonFecharItenOS().addActionListener(e -> fecharModal(view));

        view.getButtonAdicionarPeca().addActionListener(e -> adicionarPeca(view));
        view.getButtonRemoverPeca().addActionListener(e -> removerPeca(view));
        view.getButtonFecharPecas().addActionListener(e -> fecharModal(view));

        view.getButtonAdicionarServExternos().addActionListener(e -> adicionarServExternos(view));
        view.getButtonRemoverServExternos().addActionListener(e -> removerServExternos(view));
        view.getButtonFecharServExternos().addActionListener(e -> fecharModal(view));
    }

    public void preparar(CadastroItemOS view, Long osId) {
        this.osId = osId;
        carregarDados(view);
    }

    private void carregarDados(CadastroItemOS view) {
        carregarServicos(view);
        carregarPecas(view);
        carregarExternos(view);
    }

    private void carregarServicos(CadastroItemOS view) {
        try {
            var lista = ordemServicoController.listarItensServico(osId).getBody();
            DefaultTableModel model = (DefaultTableModel) view.getTableServicos().getModel();
            model.setRowCount(0);
            idsServicos.clear();
            if (lista != null) {
                for (ItemServicoDTO is : lista) {
                    model.addRow(new Object[]{
                        is.getServicoNome(), is.getQuantidade(),
                        String.format("R$ %.2f", is.getValorUnitario()),
                        String.format("R$ %.2f", is.getSubtotal())
                    });
                    idsServicos.add(is.getId());
                }
            }
        } catch (Exception e) {
            MensagemUtil.mostrarErro(view, "Erro ao carregar serviços: " + e.getMessage());
        }
    }

    private void carregarPecas(CadastroItemOS view) {
        try {
            var lista = ordemServicoController.listarItensPeca(osId).getBody();
            DefaultTableModel model = (DefaultTableModel) view.getTablePecas().getModel();
            model.setRowCount(0);
            idsPecas.clear();
            if (lista != null) {
                for (ItemPecaDTO ip : lista) {
                    model.addRow(new Object[]{
                        ip.getPecaNome(), ip.getQuantidade(),
                        String.format("R$ %.2f", ip.getValorUnitario()),
                        String.format("R$ %.2f", ip.getSubtotal())
                    });
                    idsPecas.add(ip.getId());
                }
            }
        } catch (Exception e) {
            MensagemUtil.mostrarErro(view, "Erro ao carregar peças: " + e.getMessage());
        }
    }

    private void carregarExternos(CadastroItemOS view) {
        try {
            var lista = ordemServicoController.listarServicosExternos(osId).getBody();
            DefaultTableModel model = (DefaultTableModel) view.getTableServExternos().getModel();
            model.setRowCount(0);
            idsExternos.clear();
            if (lista != null) {
                for (ServicoExternoDTO se : lista) {
                    model.addRow(new Object[]{
                        se.getId(),
                        se.getParceiroNome(), se.getDescricao(),
                        FormatadorUtil.formatarMoeda(se.getValor()),
                        se.getGarantiaDias()
                    });
                    idsExternos.add(se.getId());
                }
            }
        } catch (Exception e) {
            MensagemUtil.mostrarErro(view, "Erro ao carregar serv. externos: " + e.getMessage());
        }
    }

    private void adicionarItemServico(CadastroItemOS view) {
        try {
            List<ServicoDTO> servicos = servicoController.listar(false).getBody();
            if (servicos == null || servicos.isEmpty()) {
                MensagemUtil.mostrarErro(view, "Nenhum serviço cadastrado.");
                return;
            }
            var nomes = servicos.stream().map(ServicoDTO::getNomeServico).toArray(String[]::new);
            String escolha = (String) JOptionPane.showInputDialog(view,
                "Selecione o serviço:", "Adicionar Serviço",
                JOptionPane.QUESTION_MESSAGE, null, nomes, nomes[0]);
            if (escolha == null) return;

            ServicoDTO sel = servicos.stream()
                .filter(s -> escolha.equals(s.getNomeServico())).findFirst().orElse(null);
            if (sel == null) return;
            double valor = sel.getValorServico();

            JTextField tfQtd = new JTextField("1", 10);
            FormatadorUtil.setApenasNumeros(tfQtd, 5);
            int opt = JOptionPane.showConfirmDialog(view, new Object[]{"Quantidade:", tfQtd},
                "Quantidade", JOptionPane.OK_CANCEL_OPTION);
            if (opt != JOptionPane.OK_OPTION) return;
            String qtdStr = tfQtd.getText().trim();
            if (qtdStr.isEmpty()) return;
            int qtd = Integer.parseInt(qtdStr);

            List<ColaboradorDTO> colaboradores = colaboradorController.listar(false).getBody();
            if (colaboradores == null || colaboradores.isEmpty()) {
                MensagemUtil.mostrarErro(view, "Nenhum colaborador cadastrado.");
                return;
            }
            var nomesColab = colaboradores.stream().map(ColaboradorDTO::getNome).toArray(String[]::new);
            String escolhaColab = (String) JOptionPane.showInputDialog(view,
                "Selecione o responsável:", "Colaborador",
                JOptionPane.QUESTION_MESSAGE, null, nomesColab, nomesColab[0]);
            if (escolhaColab == null) return;
            ColaboradorDTO selColab = colaboradores.stream()
                .filter(c -> escolhaColab.equals(c.getNome())).findFirst().orElse(null);
            if (selColab == null) return;

            OrdemServicoController.ItemServicoRequest req = new OrdemServicoController.ItemServicoRequest(
                sel.getId(), qtd, valor, null, null, null, selColab.getId());
            ordemServicoController.adicionarItemServico(osId, req);
            carregarServicos(view);
        } catch (Exception e) {
            MensagemUtil.mostrarErro(view, "Erro: " + e.getMessage());
        }
    }

    private void removerItemServico(CadastroItemOS view) {
        int linha = view.getTableServicos().getSelectedRow();
        if (linha < 0) {
            MensagemUtil.mostrarErro(view, "Selecione um item para remover.");
            return;
        }
        Long itemId = idsServicos.get(linha);
        int confirm = JOptionPane.showConfirmDialog(view, "Remover este item?", "Confirmar",
            JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            ordemServicoController.removerItemServico(osId, itemId);
            ((DefaultTableModel) view.getTableServicos().getModel()).removeRow(linha);
            idsServicos.remove(linha);
        } catch (Exception e) {
            MensagemUtil.mostrarErro(view, "Erro ao remover: " + e.getMessage());
        }
    }

    private void adicionarPeca(CadastroItemOS view) {
        try {
            List<PecaDTO> pecas = pecaController.listar(false).getBody();
            if (pecas == null || pecas.isEmpty()) {
                MensagemUtil.mostrarErro(view, "Nenhuma peça cadastrada.");
                return;
            }
            var nomes = pecas.stream().map(PecaDTO::getNome).toArray(String[]::new);
            String escolha = (String) JOptionPane.showInputDialog(view,
                "Selecione a peça:", "Adicionar Peça",
                JOptionPane.QUESTION_MESSAGE, null, nomes, nomes[0]);
            if (escolha == null) return;

            PecaDTO sel = pecas.stream()
                .filter(s -> escolha.equals(s.getNome())).findFirst().orElse(null);
            if (sel == null) return;
            double preco = sel.getPrecoVenda();

            JTextField tfQtd = new JTextField("1", 10);
            FormatadorUtil.setApenasNumeros(tfQtd, 5);
            int opt = JOptionPane.showConfirmDialog(view, new Object[]{"Quantidade:", tfQtd},
                "Quantidade", JOptionPane.OK_CANCEL_OPTION);
            if (opt != JOptionPane.OK_OPTION) return;
            String qtdStr = tfQtd.getText().trim();
            if (qtdStr.isEmpty()) return;
            int qtd = Integer.parseInt(qtdStr);

            OrdemServicoController.ItemPecaRequest req = new OrdemServicoController.ItemPecaRequest(
                sel.getId(), qtd, preco);
            ordemServicoController.adicionarItemPeca(osId, req);
            carregarPecas(view);
        } catch (Exception e) {
            MensagemUtil.mostrarErro(view, "Erro: " + e.getMessage());
        }
    }

    private void removerPeca(CadastroItemOS view) {
        int linha = view.getTablePecas().getSelectedRow();
        if (linha < 0) {
            MensagemUtil.mostrarErro(view, "Selecione uma peça para remover.");
            return;
        }
        Long itemId = idsPecas.get(linha);
        int confirm = JOptionPane.showConfirmDialog(view, "Remover este item?", "Confirmar",
            JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            ordemServicoController.removerItemPeca(osId, itemId);
            ((DefaultTableModel) view.getTablePecas().getModel()).removeRow(linha);
            idsPecas.remove(linha);
        } catch (Exception e) {
            MensagemUtil.mostrarErro(view, "Erro ao remover: " + e.getMessage());
        }
    }

    private void adicionarServExternos(CadastroItemOS view) {
        try {
            List<br.edu.senai.fatesg.avcar.business.parceiros.ParceiroDTO> parceiros = parceiroExternoController.listar(false).getBody();
            if (parceiros == null || parceiros.isEmpty()) {
                MensagemUtil.mostrarErro(view, "Nenhum parceiro cadastrado.");
                return;
            }
            var nomes = parceiros.stream().map(br.edu.senai.fatesg.avcar.business.parceiros.ParceiroDTO::getNome).toArray(String[]::new);
            String escolha = (String) JOptionPane.showInputDialog(view,
                "Selecione o parceiro:", "Adicionar Serv. Externo",
                JOptionPane.QUESTION_MESSAGE, null, nomes, nomes[0]);
            if (escolha == null) return;

            br.edu.senai.fatesg.avcar.business.parceiros.ParceiroDTO sel = parceiros.stream()
                .filter(s -> escolha.equals(s.getNome())).findFirst().orElse(null);
            if (sel == null) return;

            String desc = JOptionPane.showInputDialog(view, "Descrição do serviço:");
            if (desc == null || desc.isBlank()) return;

            JTextField tfValor = new JTextField(10);
            FormatadorUtil.setApenasMoeda(tfValor);
            int optValor = JOptionPane.showConfirmDialog(view, new Object[]{"Valor R$:", tfValor},
                "Valor", JOptionPane.OK_CANCEL_OPTION);
            if (optValor != JOptionPane.OK_OPTION) return;
            String valorStr = tfValor.getText().trim();
            if (valorStr.isEmpty()) return;
            double valor = Double.parseDouble(valorStr.replace(',', '.'));

            JTextField tfDias = new JTextField("90", 10);
            FormatadorUtil.setApenasNumeros(tfDias, 5);
            int optDias = JOptionPane.showConfirmDialog(view, new Object[]{"Prazo de garantia (dias):", tfDias},
                "Garantia", JOptionPane.OK_CANCEL_OPTION);
            if (optDias != JOptionPane.OK_OPTION) return;
            String diasStr = tfDias.getText().trim();
            if (diasStr.isEmpty()) return;
            int dias = Integer.parseInt(diasStr);

            OrdemServicoController.ServicoExternoRequest req = new OrdemServicoController.ServicoExternoRequest(
                sel.getId(), desc, valor, dias);
            ordemServicoController.adicionarServicoExterno(osId, req);
            carregarExternos(view);
        } catch (Exception e) {
            MensagemUtil.mostrarErro(view, "Erro: " + e.getMessage());
        }
    }

    private void removerServExternos(CadastroItemOS view) {
        int linha = view.getTableServExternos().getSelectedRow();
        if (linha < 0) {
            MensagemUtil.mostrarErro(view, "Selecione um serviço para remover.");
            return;
        }
        Long itemId = idsExternos.get(linha);
        int confirm = JOptionPane.showConfirmDialog(view, "Remover este item?", "Confirmar",
            JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            ordemServicoController.removerServicoExterno(osId, itemId);
            ((DefaultTableModel) view.getTableServExternos().getModel()).removeRow(linha);
            idsExternos.remove(linha);
        } catch (Exception e) {
            MensagemUtil.mostrarErro(view, "Erro ao remover: " + e.getMessage());
        }
    }

    private void fecharModal(CadastroItemOS view) {
        java.awt.Window win = SwingUtilities.getWindowAncestor(view);
        if (win != null) {
            win.dispose();
        }
    }
}
