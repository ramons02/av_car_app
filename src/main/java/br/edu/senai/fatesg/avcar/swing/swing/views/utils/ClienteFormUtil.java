package br.edu.senai.fatesg.avcar.swing.views.utils;

import br.edu.senai.fatesg.avcar.business.clientes.ClienteController;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ClienteFormUtil {

    // --- Lógica de Navegação entre PF e PJ ---
    
    public static void configurarNavegacaoRadios(
            JPanel painelAtual,
            javax.swing.JRadioButton radioPF,
            javax.swing.JRadioButton radioPJ,
            org.springframework.context.ApplicationContext ctx,
            boolean isPainelPF) {
        
        javax.swing.ButtonGroup bg = new javax.swing.ButtonGroup();
        bg.add(radioPF);
        bg.add(radioPJ);
        
        // Garante que a seleção inicial esteja correta
        if (isPainelPF) {
            radioPF.setSelected(true);
        } else {
            radioPJ.setSelected(true);
        }

        // Listener para garantir o estado visual correto quando o painel for exibido
        painelAtual.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                if (isPainelPF) {
                    radioPF.setSelected(true);
                } else {
                    radioPJ.setSelected(true);
                }
            }
        });
        
        if (isPainelPF) {
            radioPJ.addActionListener(e -> {
                // Retorna o botão visualmente para PF para quando este painel for reaberto
                radioPF.setSelected(true);
                JPanel novoPainel = ctx.getBean(br.edu.senai.fatesg.avcar.swing.views.CadastroClientePJ.class);
                trocarTela(painelAtual, novoPainel, "Cadastro de Cliente - Pessoa Jurídica");
            });
        } else {
            radioPF.addActionListener(e -> {
                // Retorna o botão visualmente para PJ para quando este painel for reaberto
                radioPJ.setSelected(true);
                JPanel novoPainel = ctx.getBean(br.edu.senai.fatesg.avcar.swing.views.CadastroClientePF.class);
                trocarTela(painelAtual, novoPainel, "Cadastro de Cliente - Pessoa Física");
            });
        }
    }
    
    private static void trocarTela(JPanel painelAtual, JPanel novoPainel, String novoTitulo) {
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

    // --- Lógica de Salvamento ---

    public static void salvarPessoaFisica(
            JPanel painel,
            ClienteController controller,
            Long idEmEdicao,
            String nome, String endereco, String bairro, String cidade, String estado,
            String cepRaw, String telefoneRaw, String email, String cpfRaw) {
        
        try {
            String cep = cepRaw != null ? cepRaw.replaceAll("[^0-9]", "") : "";
            String tel = telefoneRaw != null ? telefoneRaw.replaceAll("[^0-9]", "") : "";
            String cpf = cpfRaw != null ? cpfRaw.replaceAll("[^0-9]", "") : "";

            if (nome.trim().isEmpty() || cpf.trim().isEmpty()) {
                JOptionPane.showMessageDialog(painel, "Nome e CPF são obrigatórios!");
                return;
            }

            if (idEmEdicao != null) {
                var req = new ClienteController.AtualizarClienteRequest(
                    nome, endereco, bairro, cidade, estado, cep, tel, email, cpf, null, null, null, null, null, null
                );
                controller.atualizar(idEmEdicao, req);
            } else {
                var req = new ClienteController.CriarPFRequest(
                    nome, endereco, bairro, cidade, estado, cep, tel, email, cpf, null, null, null
                );
                controller.criarPF(req);
            }

            JOptionPane.showMessageDialog(painel, "Cliente Pessoa Física salvo com sucesso!");
            fecharJanela(painel);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(painel, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void salvarPessoaJuridica(
            JPanel painel,
            ClienteController controller,
            Long idEmEdicao,
            String nome, String endereco, String bairro, String cidade, String estado,
            String cepRaw, String telefoneRaw, String email, String cnpjRaw, String inscricaoEstadual) {
        
        try {
            String cep = cepRaw != null ? cepRaw.replaceAll("[^0-9]", "") : "";
            String tel = telefoneRaw != null ? telefoneRaw.replaceAll("[^0-9]", "") : "";
            String cnpj = cnpjRaw != null ? cnpjRaw.replaceAll("[^0-9]", "") : "";
            String ie = inscricaoEstadual.trim();

            if (nome.trim().isEmpty() || cnpj.trim().isEmpty()) {
                JOptionPane.showMessageDialog(painel, "Nome e CNPJ são obrigatórios!");
                return;
            }

            if (idEmEdicao != null) {
                var req = new ClienteController.AtualizarClienteRequest(
                    nome, endereco, bairro, cidade, estado, cep, tel, email, cnpj, null, null, ie, null, null, null
                );
                controller.atualizar(idEmEdicao, req);
            } else {
                var req = new ClienteController.CriarPJRequest(
                    nome, endereco, bairro, cidade, estado, cep, tel, email, cnpj, ie, null, null
                );
                controller.criarPJ(req);
            }

            JOptionPane.showMessageDialog(painel, "Cliente Pessoa Jurídica salvo com sucesso!");
            fecharJanela(painel);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(painel, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void fecharJanela(JPanel painel) {
        java.awt.Window window = SwingUtilities.getWindowAncestor(painel);
        if (window != null) {
            window.dispose();
        }
    }

    // --- Lógica de Abertura Inteligente (PF vs PJ) ---

    public static void abrirTelaEdicao(
            java.awt.Window parent,
            org.springframework.context.ApplicationContext ctx,
            Long idCliente,
            String tipoCliente) {
        
        if ("PJ".equalsIgnoreCase(tipoCliente)) {
            br.edu.senai.fatesg.avcar.swing.views.CadastroClientePJ panel = ctx.getBean(br.edu.senai.fatesg.avcar.swing.views.CadastroClientePJ.class);
            panel.preencherParaEdicao(idCliente);
            br.edu.senai.fatesg.avcar.swing.views.utils.JanelaUtil.abrirPainelComoModal(parent, "Editar Cliente - Pessoa Jurídica", panel);
        } else {
            br.edu.senai.fatesg.avcar.swing.views.CadastroClientePF panel = ctx.getBean(br.edu.senai.fatesg.avcar.swing.views.CadastroClientePF.class);
            panel.preencherParaEdicao(idCliente);
            br.edu.senai.fatesg.avcar.swing.views.utils.JanelaUtil.abrirPainelComoModal(parent, "Editar Cliente - Pessoa Física", panel);
        }
    }

    public static void abrirTelaNovo(
            java.awt.Window parent,
            org.springframework.context.ApplicationContext ctx) {
        
        // Novo cliente sempre abre a tela PF por padrão
        br.edu.senai.fatesg.avcar.swing.views.CadastroClientePF panel = ctx.getBean(br.edu.senai.fatesg.avcar.swing.views.CadastroClientePF.class);
        panel.prepararParaNovo();
        br.edu.senai.fatesg.avcar.swing.views.utils.JanelaUtil.abrirPainelComoModal(parent, "Novo Cliente - Pessoa Física", panel);
    }
}
