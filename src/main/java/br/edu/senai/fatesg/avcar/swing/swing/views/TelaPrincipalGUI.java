/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package br.edu.senai.fatesg.avcar.swing.views;

/**
 *
 * @author lucio-aguiar
 */
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class TelaPrincipalGUI extends javax.swing.JFrame {

    @Autowired
    private ApplicationContext springContext;
    
    @Autowired
    private br.edu.senai.fatesg.avcar.business.clientes.ClienteController clienteController;

    @Autowired
    private br.edu.senai.fatesg.avcar.business.veiculos.VeiculoController veiculoController;

    @Autowired
    private br.edu.senai.fatesg.avcar.business.servicos.ServicoController servicoController;

    @Autowired
    private br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoController ordemServicoController;

    @Autowired
    private br.edu.senai.fatesg.avcar.business.pecas.PecaController pecaController;
    
    @Autowired
    private br.edu.senai.fatesg.avcar.business.fornecedores.FornecedorController fornecedorController;

    @Autowired
    private br.edu.senai.fatesg.avcar.business.colaboradores.ColaboradorController colaboradorController;

    @Autowired
    private br.edu.senai.fatesg.avcar.business.parceiros.ParceiroExternoController parceiroExternoController;

    /**
     * Creates new form TelaPrincipalGUI
     */
    private java.util.List<br.edu.senai.fatesg.avcar.business.clientes.ClienteDTO> listaClientesAtual = new java.util.ArrayList<>();
    private java.util.List<br.edu.senai.fatesg.avcar.business.servicos.ServicoDTO> listaServicosAtual = new java.util.ArrayList<>();
    private java.util.List<br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoDTO> listaOSAtual = new java.util.ArrayList<>();
    private java.util.List<br.edu.senai.fatesg.avcar.business.pecas.PecaDTO> listaPecasAtual = new java.util.ArrayList<>();
    private java.util.List<br.edu.senai.fatesg.avcar.business.fornecedores.FornecedorDTO> listaFornecedoresAtual = new java.util.ArrayList<>();
    private java.util.List<br.edu.senai.fatesg.avcar.business.colaboradores.ColaboradorDTO> listaColaboradoresAtual = new java.util.ArrayList<>();
    private java.util.List<br.edu.senai.fatesg.avcar.business.parceiros.ParceiroDTO> listaParceirosAtual = new java.util.ArrayList<>();

    // Cards KPI da Dashboard (gerenciados por código, substituem os painéis do Designer)
    private br.edu.senai.fatesg.avcar.swing.views.utils.CardKPI cardTotalOS;
    private br.edu.senai.fatesg.avcar.swing.views.utils.CardKPI cardFaturamento;
    private br.edu.senai.fatesg.avcar.swing.views.utils.CardKPI cardDescontos;
    private br.edu.senai.fatesg.avcar.swing.views.utils.CardKPI cardOSAbertas;
    private javax.swing.JPanel painelCentroDashboard;
    private br.edu.senai.fatesg.avcar.swing.views.utils.ModernPieChart chartDashboard;
    private br.edu.senai.fatesg.avcar.swing.views.utils.ModernBarChart chartBarDashboard;
    private br.edu.senai.fatesg.avcar.swing.views.utils.ModernHorizontalBarChart chartHBarDashboard;
    private br.edu.senai.fatesg.avcar.swing.views.utils.ModernLineChart chartLineDashboard;

    @jakarta.annotation.PostConstruct
    public void initDados() {
        carregarTabelaClientes();
        carregarTabelaVeiculos();
        carregarTabelaServicos();
        carregarTabelaOS();
        carregarTabelaPecas();
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.adicionarTooltipDataCompraPeca(jTablePecas, () -> listaPecasAtual);
        
        // 3. Configura os Cards KPI e atualiza os dados da Dashboard após o Spring injetar os beans
        configurarDashboard();
        atualizarDashboard();
        
        // Listener para atualizar em tempo real quando o usuário voltar para a aba Visão Geral
        jTabbedPaneVisaoGeral.addChangeListener(e -> {
            if (jTabbedPaneVisaoGeral.getSelectedIndex() == 0) {
                atualizarDashboard();
            }
        });
    }


    public void carregarTabelaClientes() {
        if (clienteController == null) return; // Evita erro se abrir no modo Design
        
        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) jTableClientes.getModel();
        modelo.setNumRows(0);
        
        boolean verInativos = false;
        if (jRadioButtonVisualizarClientesInativos != null) {
            verInativos = jRadioButtonVisualizarClientesInativos.isSelected();
        }
        
        // Verifica se há algo digitado na busca
        String termo = "";
        if (jTextFieldPesquisarCliente != null) {
            termo = jTextFieldPesquisarCliente.getText().trim();
        }
        
        // Puxa TODOS do banco de dados (respeitando o filtro do checkbox de inativos)
        listaClientesAtual = clienteController.listar(verInativos).getBody();
        
        // Aplica o filtro de texto na memória utilizando o nosso Utils Genérico
        if (!termo.isEmpty()) {
            listaClientesAtual = br.edu.senai.fatesg.avcar.swing.views.utils.FiltroUtil.filtrarPorNome(
                listaClientesAtual, termo, br.edu.senai.fatesg.avcar.business.clientes.ClienteDTO::getNome);
        }
        
        for (br.edu.senai.fatesg.avcar.business.clientes.ClienteDTO c : listaClientesAtual) {
            modelo.addRow(new Object[]{
                c.getId(),
                c.getNome(),
                c.getTipo(),
                br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil.formatarCpfCnpj(c.getDocumento()),
                br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil.formatarTelefone(c.getTelefone()),
                c.getEmail()
            });
        }
        
        // Aplica o utilitário que pinta a linha de vermelho caso o cliente esteja inativo
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarCorVermelhaLinhasInativas(jTableClientes, listaClientesAtual);
    }

    public void carregarTabelaVeiculos() {
        if (veiculoController == null) return;
        
        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) jTableVeiculos.getModel();
        modelo.setNumRows(0);
        
        String termo = "";
        if (jTextFieldPesquisarVeiculo != null) {
            termo = jTextFieldPesquisarVeiculo.getText().trim();
        }
        
        java.util.List<br.edu.senai.fatesg.avcar.business.veiculos.VeiculoDTO> lista;
        if (termo.isEmpty()) {
            lista = veiculoController.listar(false).getBody();
        } else {
            lista = veiculoController.buscarPorPlaca(termo).getBody();
        }
        
        if (lista != null) {
            for (br.edu.senai.fatesg.avcar.business.veiculos.VeiculoDTO v : lista) {
                modelo.addRow(new Object[]{
                    v.getId(),
                    br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil.formatarPlaca(v.getPlaca()),
                    (v.getChassi() != null ? v.getChassi().toUpperCase() : ""),
                    v.getAnoFabricacao(),
                    v.getAnoModelo(),
                    v.getCor(),
                    br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil.formatarQuilometragem(v.getQuilometragem()),
                    new br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.ImagemComTexto(v.getMarcaLogoUrl(), v.getMarcaNome()),
                    v.getModeloNome(),
                    v.getClienteNome()
                });
            }
        }
    }

    public void carregarTabelaServicos() {
        if (servicoController == null) return;
        
        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) jTableServicos.getModel();
        modelo.setNumRows(0);
        
        boolean verInativos = false;
        if (jRadioButtonVisualizarServicosInativos != null) {
            verInativos = jRadioButtonVisualizarServicosInativos.isSelected();
        }
        
        String termo = "";
        if (jTextFieldPesquisarServicos != null) {
            termo = jTextFieldPesquisarServicos.getText().trim();
        }
        
        listaServicosAtual = servicoController.listar(verInativos).getBody();
        
        if (!termo.isEmpty() && listaServicosAtual != null) {
            listaServicosAtual = br.edu.senai.fatesg.avcar.swing.views.utils.FiltroUtil.filtrarPorNome(
                listaServicosAtual, termo, br.edu.senai.fatesg.avcar.business.servicos.ServicoDTO::getNomeServico);
        }
        
        if (listaServicosAtual != null) {
            for (br.edu.senai.fatesg.avcar.business.servicos.ServicoDTO s : listaServicosAtual) {
                modelo.addRow(new Object[]{
                    s.getId(),
                    s.getNomeServico(),
                    s.getDescricaoServico(),
                    br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil.formatarMoeda(s.getValorServico()),
                    br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil.formatarDias(s.getGarantiaDias()),
                    br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil.formatarMinutos(s.getTempoEstimado())
                });
            }
        }
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarCorVermelhaLinhasInativas(jTableServicos, listaServicosAtual);
    }

    public void carregarTabelaOS() {
        if (ordemServicoController == null) return;
        
        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) jTableOS.getModel();
        modelo.setNumRows(0);
        
        String termo = "";
        if (jTextFieldPesquisarOS != null) {
            termo = jTextFieldPesquisarOS.getText().trim();
        }
        
        listaOSAtual = ordemServicoController.listar().getBody();
        
        if (listaOSAtual != null) {
            java.util.List<br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoDTO> filtrados = listaOSAtual;
            
            if (!termo.isEmpty()) {
                String t = termo.toLowerCase();
                filtrados = listaOSAtual.stream()
                    .filter(os -> 
                        (os.getVeiculo() != null && os.getVeiculo().toLowerCase().contains(t)) ||
                        (os.getNumeroOs() != null && os.getNumeroOs().toString().contains(t)) ||
                        (os.getStatus() != null && os.getStatus().toLowerCase().contains(t))
                    ).toList();
            }
            
            for (br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoDTO os : filtrados) {
                modelo.addRow(new Object[]{
                    os.getId(),
                    os.getNumeroOs(),
                    os.getVeiculo(),
                    os.getStatus(),
                    br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil.formatarDataHora(os.getDataAbertura()),
                    String.format("R$ %.2f", os.getValorTotal()),
                    os.getValorDesconto() > 0 ? String.format("R$ %.2f", os.getValorDesconto()) : ""
                });
            }
        }
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarCorNoStatus(jTableOS);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.centralizarColunas(jTableOS, 0, 1, 2, 3, 4, 5, 6);
        
        atualizarDashboard(); // Sincroniza a guia Visão Geral em tempo real sempre que uma OS for salva ou avançada!
    }

    public void carregarTabelaPecas() {
        if (pecaController == null || jTablePecas == null) return;
        
        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) jTablePecas.getModel();
        modelo.setNumRows(0);
        
        boolean verInativas = false;
        if (jRadioButtonVisualizarPecaExcluida != null) {
            verInativas = jRadioButtonVisualizarPecaExcluida.isSelected();
        }
        
        String termo = "";
        if (jTextFieldPesquisarPecas != null) {
            termo = jTextFieldPesquisarPecas.getText().trim();
        }
        
        listaPecasAtual = pecaController.listar(verInativas).getBody();
        
        if (!termo.isEmpty() && listaPecasAtual != null) {
            final String t = termo.toLowerCase();
            listaPecasAtual = listaPecasAtual.stream().filter(p -> 
                String.valueOf(p.getCodigoNacional()).contains(t) || 
                (p.getNome() != null && p.getNome().toLowerCase().contains(t))
            ).collect(java.util.stream.Collectors.toList());
        }
        
        if (listaPecasAtual != null) {
            for (br.edu.senai.fatesg.avcar.business.pecas.PecaDTO p : listaPecasAtual) {
                modelo.addRow(new Object[]{
                    p.getId(),
                    p.getCodigoNacional(),
                    p.getNome(),
                    p.getFabricante(),
                    p.getCategoria(),
                    br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil.formatarMoeda(p.getPrecoCusto()),
                    br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil.formatarMoeda(p.getPrecoVenda()),
                    p.getQuantidadeEstoque(),
                    p.getGarantiaPeca(),
                    p.getFornecedorNome() != null ? p.getFornecedorNome() : "N/D"
                });
            }
        }
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarCorVermelhaLinhasInativas(jTablePecas, listaPecasAtual);
    }

    public void carregarTabelaFornecedores() {
        if (fornecedorController == null || jTableFornecedores == null) return;
        
        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) jTableFornecedores.getModel();
        modelo.setNumRows(0);
        
        boolean verInativas = false;
        if (jRadioButtonVisualizarFornecedorInativo != null) {
            verInativas = jRadioButtonVisualizarFornecedorInativo.isSelected();
        }
        
        String termo = "";
        if (jTextFieldPesquisarFornecedor != null) {
            termo = jTextFieldPesquisarFornecedor.getText().trim();
        }
        
        listaFornecedoresAtual = fornecedorController.listar(verInativas).getBody();
        
        if (!termo.isEmpty() && listaFornecedoresAtual != null) {
            final String t = termo.toLowerCase();
            listaFornecedoresAtual = listaFornecedoresAtual.stream().filter(f -> 
                (f.getRazaoSocial() != null && f.getRazaoSocial().toLowerCase().contains(t)) || 
                (f.getCnpj() != null && f.getCnpj().contains(t))
            ).collect(java.util.stream.Collectors.toList());
        }
        
        if (listaFornecedoresAtual != null) {
            for (br.edu.senai.fatesg.avcar.business.fornecedores.FornecedorDTO f : listaFornecedoresAtual) {
                String cnpjFormatado = f.getCnpj();
                if (cnpjFormatado != null && cnpjFormatado.length() == 14) {
                    cnpjFormatado = cnpjFormatado.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
                }
                String telefone = (f.getDdi() != null ? f.getDdi() : "") + " (" + (f.getDdd() != null ? f.getDdd() : "") + ") " + (f.getNumeroFornecedor() != null ? f.getNumeroFornecedor() : "");
                String endereco = (f.getEnderecoFornecedor() != null ? f.getEnderecoFornecedor() : "") + ", " + (f.getBairroFornecedor() != null ? f.getBairroFornecedor() : "") + " - " + (f.getCidadeFornecedor() != null ? f.getCidadeFornecedor() : "") + "/" + (f.getEstadoFornecedor() != null ? f.getEstadoFornecedor() : "");
                modelo.addRow(new Object[]{
                    f.getId(),
                    f.getRazaoSocial(),
                    cnpjFormatado,
                    telefone,
                    f.getEmail(),
                    endereco
                });
            }
        }
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarCorVermelhaLinhasInativas(jTableFornecedores, listaFornecedoresAtual);
    }

    public void carregarTabelaColaboradores() {
        if (colaboradorController == null || jTableColaboradores == null) return;
        
        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) jTableColaboradores.getModel();
        modelo.setNumRows(0);
        
        boolean verInativas = false;
        if (jRadioButtonVisualizarColaboradores != null) {
            verInativas = jRadioButtonVisualizarColaboradores.isSelected();
        }
        
        String termo = "";
        if (jTextFieldPesquisarColaboradores != null) {
            termo = jTextFieldPesquisarColaboradores.getText().trim();
        }
        
        listaColaboradoresAtual = colaboradorController.listar(verInativas).getBody();
        
        if (!termo.isEmpty() && listaColaboradoresAtual != null) {
            final String t = termo.toLowerCase();
            listaColaboradoresAtual = listaColaboradoresAtual.stream().filter(c -> 
                (c.getNome() != null && c.getNome().toLowerCase().contains(t)) || 
                (c.getCpf() != null && c.getCpf().contains(t))
            ).collect(java.util.stream.Collectors.toList());
        }
        
        if (listaColaboradoresAtual != null) {
            for (br.edu.senai.fatesg.avcar.business.colaboradores.ColaboradorDTO c : listaColaboradoresAtual) {
                String cpfFormatado = c.getCpf();
                if (cpfFormatado != null && cpfFormatado.length() == 11) {
                    cpfFormatado = cpfFormatado.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
                }
                String telefone = c.getTelefone() != null ? c.getTelefone() : "";
                if (telefone.startsWith("55") && telefone.length() >= 12) {
                    telefone = "(" + telefone.substring(2, 4) + ") " + telefone.substring(4);
                }
                
                String funcoes = "N/D";
                if (c.getFuncoes() != null && !c.getFuncoes().isEmpty()) {
                    funcoes = String.join(", ", c.getFuncoes());
                }

                modelo.addRow(new Object[]{
                    c.getId(),
                    c.getNome(),
                    cpfFormatado,
                    telefone,
                    c.getEmail(),
                    funcoes
                });
            }
        }
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarCorVermelhaLinhasInativas(jTableColaboradores, listaColaboradoresAtual);
    }

    public void carregarTabelaParceiros() {
        if (parceiroExternoController == null || jTableParceiros == null) return;
        
        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) jTableParceiros.getModel();
        modelo.setNumRows(0);
        
        boolean verInativas = false;
        if (jRadioButtonVisualizarParceiros != null) {
            verInativas = jRadioButtonVisualizarParceiros.isSelected();
        }
        
        String termo = "";
        if (jTextFieldPesquisarParceiros != null) {
            termo = jTextFieldPesquisarParceiros.getText().trim();
        }
        
        listaParceirosAtual = parceiroExternoController.listar(verInativas).getBody();
        
        if (!termo.isEmpty() && listaParceirosAtual != null) {
            final String t = termo.toLowerCase();
            listaParceirosAtual = listaParceirosAtual.stream().filter(p -> 
                (p.getNome() != null && p.getNome().toLowerCase().contains(t)) || 
                (p.getCnpj() != null && p.getCnpj().contains(t)) ||
                (p.getTipoServico() != null && p.getTipoServico().toLowerCase().contains(t))
            ).collect(java.util.stream.Collectors.toList());
        }
        
        if (listaParceirosAtual != null) {
            for (br.edu.senai.fatesg.avcar.business.parceiros.ParceiroDTO p : listaParceirosAtual) {
                String cnpjFormatado = p.getCnpj();
                if (cnpjFormatado != null && cnpjFormatado.length() == 14) {
                    cnpjFormatado = cnpjFormatado.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
                }
                String telefone = p.getTelefone() != null ? p.getTelefone() : "";
                if (telefone.startsWith("55") && telefone.length() >= 12) {
                    telefone = "(" + telefone.substring(2, 4) + ") " + telefone.substring(4);
                }

                modelo.addRow(new Object[]{
                    p.getId(),
                    p.getNome(),
                    cnpjFormatado,
                    p.getTipoServico(),
                    telefone,
                    p.getEmail()
                });
            }
        }
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarCorVermelhaLinhasInativas(jTableParceiros, listaParceirosAtual);
    }


    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            carregarTabelaClientes();
            carregarTabelaVeiculos();
            carregarTabelaServicos();
            carregarTabelaOS();
            carregarTabelaPecas();
            carregarTabelaFornecedores();
            carregarTabelaColaboradores();
            carregarTabelaParceiros();
        }
    }

    // --- EVENTOS CUSTOMIZADOS ---
    private void configurarEventosTabelaVeiculos() {
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.adicionarBuscaEmTempoReal(
            jTextFieldPesquisarVeiculo, 
            this::carregarTabelaVeiculos
        );

        // Ao selecionar uma linha, ativa o botão Editar
        jTableVeiculos.getSelectionModel().addListSelectionListener(e -> {
            boolean linhaSelecionada = jTableVeiculos.getSelectedRow() != -1;
            jButtonEditarVeiculo.setEnabled(linhaSelecionada);
        });

        // Duplo clique simula o clique no botão editar
        jTableVeiculos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && jTableVeiculos.getSelectedRow() != -1) {
                    jButtonEditarVeiculo.doClick();
                }
            }
        });
    }

    private void configurarEventosTabelaClientes() {
        // Busca em tempo real injetada via Utils (Clean Code)
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.adicionarBuscaEmTempoReal(
            jTextFieldPesquisarCliente, 
            this::carregarTabelaClientes
        );
        
        // Ao clicar em uma linha, acende os botões de Editar e Excluir APENAS se o cliente estiver ativo
        jTableClientes.getSelectionModel().addListSelectionListener(e -> {
            boolean linhaSelecionada = jTableClientes.getSelectedRow() != -1;
            boolean ativo = false;
            
            if (linhaSelecionada && listaClientesAtual != null) {
                int modelRow = jTableClientes.convertRowIndexToModel(jTableClientes.getSelectedRow());
                if (modelRow >= 0 && modelRow < listaClientesAtual.size()) {
                    ativo = listaClientesAtual.get(modelRow).isAtivo();
                }
            }
            
            jButtonEditarCliente.setEnabled(linhaSelecionada && ativo);
            
            // O botão inativar/ativar deve estar sempre disponível se uma linha estiver selecionada!
            jButtonInativarCliente.setEnabled(linhaSelecionada);
        });

        // Duplo Clique na linha aciona o botão de Editar automaticamente, mas só se ele estiver habilitado!
        jTableClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && jTableClientes.getSelectedRow() != -1) {
                    if (jButtonEditarCliente.isEnabled()) {
                        jButtonEditarCliente.doClick(); // Simula o clique no botão Editar
                    }
                }
            }
        });
    }

    private void configurarEventosTabelaServicos() {
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.adicionarBuscaEmTempoReal(
            jTextFieldPesquisarServicos, 
            this::carregarTabelaServicos
        );

        jTableServicos.getSelectionModel().addListSelectionListener(e -> {
            boolean linhaSelecionada = jTableServicos.getSelectedRow() != -1;
            boolean ativo = false;
            
            if (linhaSelecionada && listaServicosAtual != null) {
                int modelRow = jTableServicos.convertRowIndexToModel(jTableServicos.getSelectedRow());
                if (modelRow >= 0 && modelRow < listaServicosAtual.size()) {
                    ativo = listaServicosAtual.get(modelRow).isAtivo();
                }
            }
            
            jButtonEditarServiço.setEnabled(linhaSelecionada && ativo);
            jButtonInativaServiço.setEnabled(linhaSelecionada);
        });

        jTableServicos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && jTableServicos.getSelectedRow() != -1) {
                    if (jButtonEditarServiço.isEnabled()) {
                        jButtonEditarServiço.doClick();
                    }
                }
            }
        });

        // Configurando as ações dos botões do Serviço manualmente para não mexer no designer
        jButtonNovoServico.addActionListener(e -> {
            br.edu.senai.fatesg.avcar.swing.views.utils.ServicoFormUtil.abrirTelaNovo(this, springContext);
            carregarTabelaServicos();
        });

        jButtonEditarServiço.addActionListener(e -> {
            int linha = jTableServicos.getSelectedRow();
            if (linha != -1 && springContext != null) {
                int modelRow = jTableServicos.convertRowIndexToModel(linha);
                Long idServico = (Long) jTableServicos.getModel().getValueAt(modelRow, 0);
                br.edu.senai.fatesg.avcar.swing.views.utils.ServicoFormUtil.abrirTelaEdicao(this, springContext, idServico);
                carregarTabelaServicos();
            }
        });

        jButtonInativaServiço.addActionListener(e -> {
            int linha = jTableServicos.getSelectedRow();
            if (linha != -1 && servicoController != null) {
                int modelRow = jTableServicos.convertRowIndexToModel(linha);
                Long id = (Long) jTableServicos.getModel().getValueAt(modelRow, 0);
                String nome = (String) jTableServicos.getModel().getValueAt(modelRow, 1);
                if (br.edu.senai.fatesg.avcar.swing.views.utils.MensagemUtil.confirmarInativacao(this, nome)) {
                    servicoController.toggleStatus(id);
                    carregarTabelaServicos();
                }
            }
        });
        
        jRadioButtonVisualizarServicosInativos.addActionListener(e -> carregarTabelaServicos());
    }

    private void configurarEventosTabelaPecas() {
        if (jTablePecas == null) return;
        
        jTablePecas.getSelectionModel().addListSelectionListener(e -> {
            boolean linhaSelecionada = (jTablePecas.getSelectedRow() != -1);
            boolean ativo = false;
            
            if (linhaSelecionada && listaPecasAtual != null) {
                int modelRow = jTablePecas.convertRowIndexToModel(jTablePecas.getSelectedRow());
                if (modelRow >= 0 && modelRow < listaPecasAtual.size()) {
                    ativo = listaPecasAtual.get(modelRow).isAtivo();
                }
            }
            
            if (jButtonEditarPeca != null) jButtonEditarPeca.setEnabled(linhaSelecionada && ativo);
            if (jButtonInativaExcluir != null) jButtonInativaExcluir.setEnabled(linhaSelecionada);
        });

        jTablePecas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && jTablePecas.getSelectedRow() != -1) {
                    if (jButtonEditarPeca != null && jButtonEditarPeca.isEnabled()) {
                        jButtonEditarPeca.doClick();
                    }
                }
            }
        });

        if (jButtonNovaPeca != null) {
            jButtonNovaPeca.addActionListener(e -> {
                CadastroPeca painel = springContext.getBean(CadastroPeca.class);
                painel.preparar(null);
                br.edu.senai.fatesg.avcar.swing.views.utils.JanelaUtil.abrirPainelComoModal(this, "Nova Peça", painel);
                carregarTabelaPecas();
            });
        }
        
        if (jButtonEditarPeca != null) {
            jButtonEditarPeca.addActionListener(e -> {
                int linha = jTablePecas.getSelectedRow();
                if (linha >= 0 && springContext != null) {
                    int modelRow = jTablePecas.convertRowIndexToModel(linha);
                    Long id = (Long) jTablePecas.getModel().getValueAt(modelRow, 0);
                    CadastroPeca painel = springContext.getBean(CadastroPeca.class);
                    painel.preparar(id);
                    br.edu.senai.fatesg.avcar.swing.views.utils.JanelaUtil.abrirPainelComoModal(this, "Editar Peça", painel);
                    carregarTabelaPecas();
                }
            });
        }

        if (jButtonInativaExcluir != null) {
            jButtonInativaExcluir.addActionListener(e -> {
                int linha = jTablePecas.getSelectedRow();
                if (linha != -1 && pecaController != null) {
                    int modelRow = jTablePecas.convertRowIndexToModel(linha);
                    Long id = (Long) jTablePecas.getModel().getValueAt(modelRow, 0);
                    String nome = (String) jTablePecas.getModel().getValueAt(modelRow, 2);
                    if (br.edu.senai.fatesg.avcar.swing.views.utils.MensagemUtil.confirmarInativacao(this, nome)) {
                        pecaController.toggleStatus(id);
                        carregarTabelaPecas();
                    }
                }
            });
        }
        
        if (jTextFieldPesquisarPecas != null) {
            br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.adicionarBuscaEmTempoReal(jTextFieldPesquisarPecas, this::carregarTabelaPecas);
        }
        if (jRadioButtonVisualizarPecaExcluida != null) {
            jRadioButtonVisualizarPecaExcluida.addActionListener(e -> carregarTabelaPecas());
        }
    }

    private void configurarEventosTabelaFornecedores() {
        if (jTableFornecedores == null) return;
        
        jTableFornecedores.getSelectionModel().addListSelectionListener(e -> {
            boolean linhaSelecionada = (jTableFornecedores.getSelectedRow() != -1);
            boolean ativo = false;
            
            if (linhaSelecionada && listaFornecedoresAtual != null) {
                int modelRow = jTableFornecedores.convertRowIndexToModel(jTableFornecedores.getSelectedRow());
                if (modelRow >= 0 && modelRow < listaFornecedoresAtual.size()) {
                    ativo = listaFornecedoresAtual.get(modelRow).isAtivo();
                }
            }
            
            if (jButtonEditarFornecedor != null) jButtonEditarFornecedor.setEnabled(linhaSelecionada && ativo);
            if (jButtonInativarFornecedor != null) jButtonInativarFornecedor.setEnabled(linhaSelecionada);
        });

        jTableFornecedores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && jTableFornecedores.getSelectedRow() != -1) {
                    if (jButtonEditarFornecedor != null && jButtonEditarFornecedor.isEnabled()) {
                        jButtonEditarFornecedor.doClick();
                    }
                }
            }
        });

        if (jButtonNovoFornecedor != null) {
            jButtonNovoFornecedor.addActionListener(e -> {
                CadastroFornecedor painel = springContext.getBean(CadastroFornecedor.class);
                painel.preparar(null);
                br.edu.senai.fatesg.avcar.swing.views.utils.JanelaUtil.abrirPainelComoModal(this, "Novo Fornecedor", painel);
                carregarTabelaFornecedores();
            });
        }
        
        if (jButtonEditarFornecedor != null) {
            jButtonEditarFornecedor.addActionListener(e -> {
                int linha = jTableFornecedores.getSelectedRow();
                if (linha >= 0 && springContext != null) {
                    int modelRow = jTableFornecedores.convertRowIndexToModel(linha);
                    Long id = (Long) jTableFornecedores.getModel().getValueAt(modelRow, 0);
                    CadastroFornecedor painel = springContext.getBean(CadastroFornecedor.class);
                    painel.preparar(id);
                    br.edu.senai.fatesg.avcar.swing.views.utils.JanelaUtil.abrirPainelComoModal(this, "Editar Fornecedor", painel);
                    carregarTabelaFornecedores();
                }
            });
        }

        if (jButtonInativarFornecedor != null) {
            jButtonInativarFornecedor.addActionListener(e -> {
                int linha = jTableFornecedores.getSelectedRow();
                if (linha != -1 && fornecedorController != null) {
                    int modelRow = jTableFornecedores.convertRowIndexToModel(linha);
                    Long id = (Long) jTableFornecedores.getModel().getValueAt(modelRow, 0);
                    String nome = (String) jTableFornecedores.getModel().getValueAt(modelRow, 1);
                    if (br.edu.senai.fatesg.avcar.swing.views.utils.MensagemUtil.confirmarInativacao(this, nome)) {
                        fornecedorController.toggleStatus(id);
                        carregarTabelaFornecedores();
                    }
                }
            });
        }
        
        if (jTextFieldPesquisarFornecedor != null) {
            br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.adicionarBuscaEmTempoReal(jTextFieldPesquisarFornecedor, this::carregarTabelaFornecedores);
        }
        if (jRadioButtonVisualizarFornecedorInativo != null) {
            jRadioButtonVisualizarFornecedorInativo.addActionListener(e -> carregarTabelaFornecedores());
        }
    }

    private void configurarEventosTabelaColaboradores() {
        if (jTableColaboradores == null) return;
        
        jTableColaboradores.getSelectionModel().addListSelectionListener(e -> {
            boolean linhaSelecionada = (jTableColaboradores.getSelectedRow() != -1);
            boolean ativo = false;
            
            if (linhaSelecionada && listaColaboradoresAtual != null) {
                int modelRow = jTableColaboradores.convertRowIndexToModel(jTableColaboradores.getSelectedRow());
                if (modelRow >= 0 && modelRow < listaColaboradoresAtual.size()) {
                    ativo = listaColaboradoresAtual.get(modelRow).isAtivo();
                }
            }
            
            if (jButtonEditarColaborador != null) jButtonEditarColaborador.setEnabled(linhaSelecionada && ativo);
            if (jButtonInativarColaborador != null) jButtonInativarColaborador.setEnabled(linhaSelecionada);
        });

        jTableColaboradores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && jTableColaboradores.getSelectedRow() != -1) {
                    if (jButtonEditarColaborador != null && jButtonEditarColaborador.isEnabled()) {
                        jButtonEditarColaborador.doClick();
                    }
                }
            }
        });

        if (jButtonNovoColaborador != null) {
            jButtonNovoColaborador.addActionListener(e -> {
                CadastroColaborador painel = springContext.getBean(CadastroColaborador.class);
                painel.preparar(null);
                br.edu.senai.fatesg.avcar.swing.views.utils.JanelaUtil.abrirPainelComoModal(this, "Novo Colaborador", painel);
                carregarTabelaColaboradores();
            });
        }
        
        if (jButtonEditarColaborador != null) {
            jButtonEditarColaborador.addActionListener(e -> {
                int linha = jTableColaboradores.getSelectedRow();
                if (linha >= 0 && springContext != null) {
                    int modelRow = jTableColaboradores.convertRowIndexToModel(linha);
                    Long id = (Long) jTableColaboradores.getModel().getValueAt(modelRow, 0);
                    CadastroColaborador painel = springContext.getBean(CadastroColaborador.class);
                    painel.preparar(id);
                    br.edu.senai.fatesg.avcar.swing.views.utils.JanelaUtil.abrirPainelComoModal(this, "Editar Colaborador", painel);
                    carregarTabelaColaboradores();
                }
            });
        }

        if (jButtonInativarColaborador != null) {
            jButtonInativarColaborador.addActionListener(e -> {
                int linha = jTableColaboradores.getSelectedRow();
                if (linha != -1 && colaboradorController != null) {
                    int modelRow = jTableColaboradores.convertRowIndexToModel(linha);
                    Long id = (Long) jTableColaboradores.getModel().getValueAt(modelRow, 0);
                    String nome = (String) jTableColaboradores.getModel().getValueAt(modelRow, 1);
                    if (br.edu.senai.fatesg.avcar.swing.views.utils.MensagemUtil.confirmarInativacao(this, nome)) {
                        colaboradorController.toggleStatus(id);
                        carregarTabelaColaboradores();
                    }
                }
            });
        }
        
        if (jTextFieldPesquisarColaboradores != null) {
            br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.adicionarBuscaEmTempoReal(jTextFieldPesquisarColaboradores, this::carregarTabelaColaboradores);
        }
        if (jRadioButtonVisualizarColaboradores != null) {
            jRadioButtonVisualizarColaboradores.addActionListener(e -> carregarTabelaColaboradores());
        }
    }

    private void configurarEventosTabelaParceiros() {
        if (jTableParceiros == null) return;
        
        jTableParceiros.getSelectionModel().addListSelectionListener(e -> {
            boolean linhaSelecionada = (jTableParceiros.getSelectedRow() != -1);
            boolean ativo = false;
            
            if (linhaSelecionada && listaParceirosAtual != null) {
                int modelRow = jTableParceiros.convertRowIndexToModel(jTableParceiros.getSelectedRow());
                if (modelRow >= 0 && modelRow < listaParceirosAtual.size()) {
                    ativo = listaParceirosAtual.get(modelRow).isAtivo();
                }
            }
            
            if (jButtonEditarParceiro != null) jButtonEditarParceiro.setEnabled(linhaSelecionada && ativo);
            if (jButtonInativarParceiro != null) jButtonInativarParceiro.setEnabled(linhaSelecionada);
        });

        jTableParceiros.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && jTableParceiros.getSelectedRow() != -1) {
                    if (jButtonEditarParceiro != null && jButtonEditarParceiro.isEnabled()) {
                        jButtonEditarParceiro.doClick();
                    }
                }
            }
        });

        if (jButtonNovoParceiro != null) {
            jButtonNovoParceiro.addActionListener(e -> {
                CadatroParceiroExterno painel = springContext.getBean(CadatroParceiroExterno.class);
                painel.preparar(null);
                br.edu.senai.fatesg.avcar.swing.views.utils.JanelaUtil.abrirPainelComoModal(this, "Novo Parceiro Externo", painel);
                carregarTabelaParceiros();
            });
        }
        
        if (jButtonEditarParceiro != null) {
            jButtonEditarParceiro.addActionListener(e -> {
                int linha = jTableParceiros.getSelectedRow();
                if (linha >= 0 && springContext != null) {
                    int modelRow = jTableParceiros.convertRowIndexToModel(linha);
                    Long id = (Long) jTableParceiros.getModel().getValueAt(modelRow, 0);
                    CadatroParceiroExterno painel = springContext.getBean(CadatroParceiroExterno.class);
                    painel.preparar(id);
                    br.edu.senai.fatesg.avcar.swing.views.utils.JanelaUtil.abrirPainelComoModal(this, "Editar Parceiro Externo", painel);
                    carregarTabelaParceiros();
                }
            });
        }

        if (jButtonInativarParceiro != null) {
            jButtonInativarParceiro.addActionListener(e -> {
                int linha = jTableParceiros.getSelectedRow();
                if (linha != -1 && parceiroExternoController != null) {
                    int modelRow = jTableParceiros.convertRowIndexToModel(linha);
                    Long id = (Long) jTableParceiros.getModel().getValueAt(modelRow, 0);
                    String nome = (String) jTableParceiros.getModel().getValueAt(modelRow, 1);
                    if (br.edu.senai.fatesg.avcar.swing.views.utils.MensagemUtil.confirmarInativacao(this, nome)) {
                        parceiroExternoController.toggleStatus(id);
                        carregarTabelaParceiros();
                    }
                }
            });
        }
        
        if (jTextFieldPesquisarParceiros != null) {
            br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.adicionarBuscaEmTempoReal(jTextFieldPesquisarParceiros, this::carregarTabelaParceiros);
        }
        if (jRadioButtonVisualizarParceiros != null) {
            jRadioButtonVisualizarParceiros.addActionListener(e -> carregarTabelaParceiros());
        }
    }

    private void configurarEventosTabelaOS() {
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.adicionarBuscaEmTempoReal(
            jTextFieldPesquisarOS, 
            this::carregarTabelaOS
        );

        jTableOS.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            private int sortColumn = -1;
            private boolean sortAsc = true;
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = jTableOS.columnAtPoint(e.getPoint());
                if (col == sortColumn) {
                    sortAsc = !sortAsc;
                } else {
                    sortColumn = col;
                    sortAsc = true;
                }
                if (col < 0 || col > 6 || listaOSAtual == null || listaOSAtual.isEmpty()) return;
                java.util.Comparator<br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoDTO> comp = switch (col) {
                    case 0 -> java.util.Comparator.comparing(br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoDTO::getId, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
                    case 1 -> java.util.Comparator.comparing(br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoDTO::getNumeroOs, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
                    case 2 -> java.util.Comparator.comparing(br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoDTO::getVeiculo, java.util.Comparator.nullsLast(String::compareTo));
                    case 3 -> java.util.Comparator.comparing(br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoDTO::getStatus, java.util.Comparator.nullsLast(String::compareTo));
                    case 4 -> java.util.Comparator.comparing(br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoDTO::getDataAbertura, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
                    case 5 -> java.util.Comparator.comparing(br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoDTO::getValorTotal, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
                    case 6 -> java.util.Comparator.comparing(br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoDTO::getValorDesconto, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
                    default -> throw new IllegalArgumentException();
                };
                if (!sortAsc) comp = comp.reversed();
                br.edu.senai.fatesg.avcar.datastructures.OrdenacaoOS.quickSort(listaOSAtual, comp);
                
                javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) jTableOS.getModel();
                modelo.setRowCount(0);
                for (var os : listaOSAtual) {
                    modelo.addRow(new Object[]{
                        os.getId(),
                        os.getNumeroOs(),
                        os.getVeiculo(),
                        os.getStatus(),
                        br.edu.senai.fatesg.avcar.swing.views.utils.FormatadorUtil.formatarDataHora(os.getDataAbertura()),
                        String.format("R$ %.2f", os.getValorTotal()),
                        os.getValorDesconto() > 0 ? String.format("R$ %.2f", os.getValorDesconto()) : ""
                    });
                }
                br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarCorNoStatus(jTableOS);
            }
        });
        
        jTableOS.getSelectionModel().addListSelectionListener(e -> {
            boolean linhaSelecionada = jTableOS.getSelectedRow() != -1;
            jButtonGerenciarItensOS.setEnabled(linhaSelecionada);
            jButtonAvancarStatusOS.setEnabled(linhaSelecionada);
            
            boolean habilitarExtra = false;
            if (linhaSelecionada && listaOSAtual != null) {
                int modelRow = jTableOS.convertRowIndexToModel(jTableOS.getSelectedRow());
                if (modelRow >= 0 && modelRow < listaOSAtual.size()) {
                    String status = listaOSAtual.get(modelRow).getStatus();
                    if ("EXECUCAO".equalsIgnoreCase(status) || "AGUARDANDO_PAGAMENTO".equalsIgnoreCase(status) || "Em andamento".equalsIgnoreCase(status)) {
                        habilitarExtra = true;
                    }
                }
            }

        });
    }

    public TelaPrincipalGUI() {
        initComponents();
        
        // Aplica o super estilo nativo do FlatLaf (Bypass na trava de Fonte do NetBeans)
        if (jLabelLogo != null) {
            java.awt.Color accent = javax.swing.UIManager.getColor("Component.accentColor");
            if (accent == null) accent = new java.awt.Color(41, 128, 185); // fallback
            
            // Pega a fonte dinâmica padrão do sistema/tema, deixa em negrito e tamanho 22
            java.awt.Font baseFont = javax.swing.UIManager.getFont("h1.font");
            if (baseFont == null) baseFont = jLabelLogo.getFont();
            jLabelLogo.setFont(baseFont.deriveFont(java.awt.Font.BOLD, 22f));
            
            // Aplica um texto puro (sem HTML para evitar que o Swing corte a Label por causa da altura de 31px)
            jLabelLogo.setIcon(null); 
        }
        
        // Define o Ícone da Janela e Barra de Tarefas (Compatível com Ubuntu/Linux e macOS)
        try {
            java.net.URL iconURL = getClass().getResource("/logos/avcar_icone_janela.png");
            if (iconURL != null) {
                java.awt.Image icone = new javax.swing.ImageIcon(iconURL).getImage();
                
                // 1. Cria uma imagem 16x16 transparente para "esconder" o ícone da barra de título
                java.awt.image.BufferedImage iconeTransparente = new java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                
                // 2. Define uma lista de ícones. O Windows usa o 16x16 (transparente) para o título, e o maior (icone real) para a barra de tarefas
                java.util.List<java.awt.Image> icones = new java.util.ArrayList<>();
                icones.add(iconeTransparente);
                icones.add(icone);
                
                setIconImages(icones);
                
                // 3. Tenta forçar a mudança do ícone na Dock/Barra de Tarefas do Ubuntu/Linux
                if (java.awt.Taskbar.isTaskbarSupported()) {
                    java.awt.Taskbar taskbar = java.awt.Taskbar.getTaskbar();
                    if (taskbar.isSupported(java.awt.Taskbar.Feature.ICON_IMAGE)) {
                        taskbar.setIconImage(icone);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar icone da janela: " + e.getMessage());
        }

        // 1. Aplica padrões UX de tela cheia e centralização
        br.edu.senai.fatesg.avcar.swing.views.utils.JanelaUtil.aplicarPadraoMaximizado(this);
        
        // 2. Oculta a coluna ID (Índice 0) dos bastidores de TODAS as tabelas
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.ocultarColuna(jTableClientes, 0);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.ocultarColuna(jTableVeiculos, 0);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.ocultarColuna(jTableServicos, 0);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.ocultarColuna(jTableOS, 0);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.ocultarColuna(jTablePecas, 0);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.ocultarColuna(jTableFornecedores, 0);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.ocultarColuna(jTableColaboradores, 0);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.ocultarColuna(jTableParceiros, 0);
        
        // 3. Aplica cores no Status automaticamente (A função procura a coluna "Status" sozinha)
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarCorNoStatus(jTableClientes);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarCorNoStatus(jTableVeiculos);
        
        // 4. Configura os eventos de Mouse para a Tabela de Clientes
        configurarEventosTabelaClientes();
        configurarEventosTabelaVeiculos();
        configurarEventosTabelaServicos();
        configurarEventosTabelaOS();
        configurarEventosTabelaPecas();
        configurarEventosTabelaFornecedores();
        configurarEventosTabelaColaboradores();
        configurarEventosTabelaParceiros();
        
        // 5. Centraliza TODAS as colunas de todas as tabelas (conforme solicitado pelo usuário)
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.centralizarTodasColunas(jTableClientes);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.centralizarTodasColunas(jTableVeiculos);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.centralizarTodasColunas(jTableServicos);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.centralizarTodasColunas(jTableOS);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.centralizarTodasColunas(jTablePecas);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.centralizarTodasColunas(jTableFornecedores);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.centralizarTodasColunas(jTableColaboradores);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.centralizarTodasColunas(jTableParceiros);
        
        // 6. Alinha à esquerda colunas específicas (nomes, descrições, etc)
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.alinharEsquerda(jTableClientes, 0, 4); // Nome, E-mail
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.alinharEsquerda(jTableVeiculos, 8); // Cliente
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.alinharEsquerda(jTableServicos, 0, 1); // Nome, Descrição
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.alinharEsquerda(jTableOS, 1); // Veículo
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.alinharEsquerda(jTablePecas, 1, 8); // Nome, Fornecedor
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.alinharEsquerda(jTableFornecedores, 0, 3, 4); // Razão Social, E-mail, Endereço
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.alinharEsquerda(jTableColaboradores, 0); // Nome
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.alinharEsquerda(jTableParceiros, 0, 4); // Nome, E-mail
        
        // Aplica o renderizador de logotipo da marca (índice visual 6)
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarRenderizadorDeImagem(jTableVeiculos, 6);
        
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarCorNoStatus(jTableServicos);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarCorNoStatus(jTableOS);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarCorNoStatus(jTablePecas);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarCorNoStatus(jTableColaboradores);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarCorNoStatus(jTableParceiros);
        
        // 3. Define larguras fixas (Você pode descomentar e ajustar as colunas conforme necessário)
        // br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.definirLarguraFixa(jTableClientes, 1, 80); // Coluna Tipo
        // br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.definirLarguraFixa(jTableClientes, 2, 150); // Coluna Documento
        
        // Remove travas de redimensionamento do NetBeans para permitir o ajuste fluido
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.liberarRedimensionamento(jTableClientes);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.liberarRedimensionamento(jTableVeiculos);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.liberarRedimensionamento(jTableServicos);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.liberarRedimensionamento(jTableOS);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.liberarRedimensionamento(jTablePecas);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.liberarRedimensionamento(jTableFornecedores);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.liberarRedimensionamento(jTableColaboradores);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.liberarRedimensionamento(jTableParceiros);
        
        // Aplica a responsividade inteligente (3 colunas com scroll em tela menor, sem scroll em tela cheia)
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarResponsividade(jTableClientes);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarResponsividade(jTableVeiculos);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarResponsividade(jTableServicos);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarResponsividade(jTableOS);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarResponsividade(jTablePecas);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarResponsividade(jTableFornecedores);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarResponsividade(jTableColaboradores);
        br.edu.senai.fatesg.avcar.swing.views.utils.TabelaUtil.aplicarResponsividade(jTableParceiros);
        
        // --- MELHORIAS VISUAIS (PRIORIDADE 1) E SIDEBAR ---
        aplicarMelhoriasVisuaisESidebar();
    }
    
    private void aplicarMelhoriasVisuaisESidebar() {
        // 1. Sidebar Inteligente (Substitui Tabs Nativas)
        getContentPane().removeAll();
        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(new br.edu.senai.fatesg.avcar.swing.views.utils.SidebarMenu(jTabbedPaneVisaoGeral), java.awt.BorderLayout.WEST);
        getContentPane().add(jTabbedPaneVisaoGeral, java.awt.BorderLayout.CENTER);
        
        // Esconde a área de tabs original do JTabbedPane
        jTabbedPaneVisaoGeral.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) { return 0; }
            @Override protected int calculateTabAreaWidth(int tabPlacement, int vertRunCount, int maxTabWidth) { return 0; }
        });
        
        revalidate();
        repaint();

        // 2. Cores Semânticas nos Botões
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloPrimario(jButtonNovoCliente);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloPrimario(jButtonNovoVeiculo);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloPrimario(jButtonNovoServico);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloPrimario(jButtonNovaOS);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloPrimario(jButtonNovaPeca);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloPrimario(jButtonNovoFornecedor);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloPrimario(jButtonNovoColaborador);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloPrimario(jButtonNovoParceiro);
        
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloSecundario(jButtonEditarCliente);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloSecundario(jButtonEditarVeiculo);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloSecundario(jButtonEditarServiço);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloSecundario(jButtonEditarPeca);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloSecundario(jButtonEditarFornecedor);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloSecundario(jButtonEditarColaborador);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloSecundario(jButtonEditarParceiro);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloSecundario(jButtonGerenciarItensOS);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloSecundario(jButtonFilaAtendimento);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloSecundario(jButtonAvancarStatusOS);

        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloPerigo(jButtonInativarCliente);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloPerigo(jButtonInativaServiço);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloPerigo(jButtonInativaExcluir);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloPerigo(jButtonInativarFornecedor);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloPerigo(jButtonInativarColaborador);
        br.edu.senai.fatesg.avcar.swing.views.utils.BotaoUtil.aplicarEstiloPerigo(jButtonInativarParceiro);

        // 3. Typos
        if (jButtonNovaPeca != null) jButtonNovaPeca.setText("+ Nova Peça");
        if (jButtonNovoVeiculo != null) jButtonNovoVeiculo.setText("+ Novo Veículo");
        
        if (jRadioButtonVisualizarClientesInativos != null) jRadioButtonVisualizarClientesInativos.setText("Exibir Inativos");
        if (jRadioButtonVisualizarServicosInativos != null) jRadioButtonVisualizarServicosInativos.setText("Exibir Inativos");
        if (jRadioButtonVisualizarPecaExcluida != null) jRadioButtonVisualizarPecaExcluida.setText("Exibir Inativos");
        if (jRadioButtonVisualizarFornecedorInativo != null) jRadioButtonVisualizarFornecedorInativo.setText("Exibir Inativos");
        if (jRadioButtonVisualizarColaboradores != null) jRadioButtonVisualizarColaboradores.setText("Exibir Inativos");
        if (jRadioButtonVisualizarParceiros != null) jRadioButtonVisualizarParceiros.setText("Exibir Inativos");
        
        // 4. Placeholders
        if (jTextFieldPesquisarCliente != null) jTextFieldPesquisarCliente.putClientProperty("JTextField.placeholderText", "🔍 Buscar por nome...");
        if (jTextFieldPesquisarVeiculo != null) jTextFieldPesquisarVeiculo.putClientProperty("JTextField.placeholderText", "🔍 Buscar por placa...");
        if (jTextFieldPesquisarOS != null) jTextFieldPesquisarOS.putClientProperty("JTextField.placeholderText", "🔍 Buscar OS...");
        if (jTextFieldPesquisarServicos != null) jTextFieldPesquisarServicos.putClientProperty("JTextField.placeholderText", "🔍 Buscar serviço...");
        if (jTextFieldPesquisarPecas != null) jTextFieldPesquisarPecas.putClientProperty("JTextField.placeholderText", "🔍 Buscar peça...");
        if (jTextFieldPesquisarFornecedor != null) jTextFieldPesquisarFornecedor.putClientProperty("JTextField.placeholderText", "🔍 Buscar fornecedor...");
        if (jTextFieldPesquisarColaboradores != null) jTextFieldPesquisarColaboradores.putClientProperty("JTextField.placeholderText", "🔍 Buscar colaborador...");
        if (jTextFieldPesquisarParceiros != null) jTextFieldPesquisarParceiros.putClientProperty("JTextField.placeholderText", "🔍 Buscar parceiro...");

        // 5. Correção de Layout Bugado nas Guias OS e Peças (Evita mexer no .form do NetBeans)
        corrigirLayoutTab(jPanel4, jLabelGestaodeOS, jSeparator4, jLabelPesquisarOS, jTextFieldPesquisarOS, jScrollPane4, jPanel10, null, jButtonNovaOS, jButtonFilaAtendimento);
        corrigirLayoutTab(jPanel5, jLabelGestaodePecas, jSeparator5, jLabelPesquisarPecas, jTextFieldPesquisarPecas, jScrollPane5, null, jRadioButtonVisualizarPecaExcluida, jButtonNovaPeca, jButtonEditarPeca, jButtonInativaExcluir);
    }

    private void corrigirLayoutTab(javax.swing.JPanel panel, javax.swing.JLabel title, javax.swing.JSeparator sep, javax.swing.JLabel searchLbl, javax.swing.JTextField searchField, javax.swing.JScrollPane scroll, javax.swing.JPanel footer, javax.swing.JRadioButton radio, javax.swing.JButton... btns) {
        panel.removeAll();
        panel.setLayout(new java.awt.BorderLayout(0, 15));
        
        javax.swing.JPanel top = new javax.swing.JPanel(new java.awt.BorderLayout(0, 15));
        top.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        javax.swing.JPanel header = new javax.swing.JPanel(new java.awt.BorderLayout(0, 5));
        header.add(title, java.awt.BorderLayout.NORTH);
        header.add(sep, java.awt.BorderLayout.SOUTH);
        top.add(header, java.awt.BorderLayout.NORTH);
        
        javax.swing.JPanel search = new javax.swing.JPanel(new java.awt.BorderLayout(10, 0));
        search.add(searchLbl, java.awt.BorderLayout.WEST);
        search.add(searchField, java.awt.BorderLayout.CENTER);
        
        javax.swing.JPanel actions = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        for (javax.swing.JButton b : btns) {
            if (b != null) actions.add(b);
        }
        if (radio != null) actions.add(radio);
        search.add(actions, java.awt.BorderLayout.EAST);
        
        top.add(search, java.awt.BorderLayout.CENTER);
        
        panel.add(top, java.awt.BorderLayout.NORTH);
        panel.add(scroll, java.awt.BorderLayout.CENTER);
        if (footer != null) panel.add(footer, java.awt.BorderLayout.SOUTH);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPaneVisaoGeral = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabelValorTotalOS = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabelValorFaturamento = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabelValorDescontos2 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabelValorOSAbertas2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableClientes = new javax.swing.JTable();
        jLabelPesquisarCliente = new javax.swing.JLabel();
        jTextFieldPesquisarCliente = new javax.swing.JTextField();
        jRadioButtonVisualizarClientesInativos = new javax.swing.JRadioButton();
        jLabelGestaodeClientesCadastrados = new javax.swing.JLabel();
        jButtonNovoCliente = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jButtonInativarCliente = new javax.swing.JButton();
        jButtonEditarCliente = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabelGestaodeVeiculos = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabelPesquisarVeiculo = new javax.swing.JLabel();
        jTextFieldPesquisarVeiculo = new javax.swing.JTextField();
        jButtonNovoVeiculo = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableVeiculos = new javax.swing.JTable();
        jButtonEditarVeiculo = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabelGestaodeServicos = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabelPesquisarServicos = new javax.swing.JLabel();
        jTextFieldPesquisarServicos = new javax.swing.JTextField();
        jButtonNovoServico = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableServicos = new javax.swing.JTable();
        jRadioButtonVisualizarServicosInativos = new javax.swing.JRadioButton();
        jButtonEditarServiço = new javax.swing.JButton();
        jButtonInativaServiço = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabelGestaodeOS = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabelPesquisarOS = new javax.swing.JLabel();
        jTextFieldPesquisarOS = new javax.swing.JTextField();
        jButtonNovaOS = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableOS = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jButtonGerenciarItensOS = new javax.swing.JButton();
        jButtonAvancarStatusOS = new javax.swing.JButton();
        jButtonFilaAtendimento = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabelGestaodePecas = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jLabelPesquisarPecas = new javax.swing.JLabel();
        jTextFieldPesquisarPecas = new javax.swing.JTextField();
        jButtonNovaPeca = new javax.swing.JButton();
        jButtonEditarPeca = new javax.swing.JButton();
        jButtonInativaExcluir = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTablePecas = new javax.swing.JTable();
        jRadioButtonVisualizarPecaExcluida = new javax.swing.JRadioButton();
        jPanel6 = new javax.swing.JPanel();
        jLabelGestaodeFornecedores = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        jLabelPesquisarFornecedor = new javax.swing.JLabel();
        jTextFieldPesquisarFornecedor = new javax.swing.JTextField();
        jButtonNovoFornecedor = new javax.swing.JButton();
        jButtonInativarFornecedor = new javax.swing.JButton();
        jButtonEditarFornecedor = new javax.swing.JButton();
        jRadioButtonVisualizarFornecedorInativo = new javax.swing.JRadioButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTableFornecedores = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jLabelGestaodeColaboradores = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        jLabelPesquisarColaboradores = new javax.swing.JLabel();
        jTextFieldPesquisarColaboradores = new javax.swing.JTextField();
        jButtonNovoColaborador = new javax.swing.JButton();
        jButtonInativarColaborador = new javax.swing.JButton();
        jButtonEditarColaborador = new javax.swing.JButton();
        jRadioButtonVisualizarColaboradores = new javax.swing.JRadioButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTableColaboradores = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jLabelGestaodeParceiros = new javax.swing.JLabel();
        jLabelPesquisarParceiros = new javax.swing.JLabel();
        jTextFieldPesquisarParceiros = new javax.swing.JTextField();
        jButtonNovoParceiro = new javax.swing.JButton();
        jButtonInativarParceiro = new javax.swing.JButton();
        jButtonEditarParceiro = new javax.swing.JButton();
        jRadioButtonVisualizarParceiros = new javax.swing.JRadioButton();
        jSeparator8 = new javax.swing.JSeparator();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTableParceiros = new javax.swing.JTable();
        jLabelLogo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/logos/avcar_logo.png")).getImage());
        setMinimumSize(new java.awt.Dimension(1000, 650));

        jTabbedPaneVisaoGeral.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel11.setPreferredSize(new java.awt.Dimension(280, 180));

        jLabelValorTotalOS.setFont(new java.awt.Font("Liberation Sans", 1, 100)); // NOI18N
        jLabelValorTotalOS.setText("0");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(116, Short.MAX_VALUE)
                .addComponent(jLabelValorTotalOS)
                .addGap(106, 106, 106))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabelValorTotalOS)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel12.setPreferredSize(new java.awt.Dimension(280, 180));

        jLabelValorFaturamento.setFont(new java.awt.Font("Liberation Sans", 1, 100)); // NOI18N
        jLabelValorFaturamento.setText("0,00");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jLabelValorFaturamento)
                .addContainerGap(102, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabelValorFaturamento)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel13.setPreferredSize(new java.awt.Dimension(280, 180));

        jLabelValorDescontos2.setFont(new java.awt.Font("Liberation Sans", 1, 100)); // NOI18N
        jLabelValorDescontos2.setText("0,00");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(76, Short.MAX_VALUE)
                .addComponent(jLabelValorDescontos2)
                .addGap(59, 59, 59))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabelValorDescontos2)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel14.setPreferredSize(new java.awt.Dimension(280, 180));

        jLabelValorOSAbertas2.setFont(new java.awt.Font("Liberation Sans", 1, 100)); // NOI18N
        jLabelValorOSAbertas2.setText("0");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(125, 125, 125)
                .addComponent(jLabelValorOSAbertas2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabelValorOSAbertas2)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        jLabel1.setText("Total de OS");

        jLabel2.setText("Valor Faturamento");

        jLabel3.setText("Valor dos Descontos");

        jLabel4.setText("Quantidade OS Abertas");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                        .addGap(149, 149, 149)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 184, Short.MAX_VALUE)
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(162, 162, 162))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 198, Short.MAX_VALUE)))
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, 883, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 758, Short.MAX_VALUE)
                        .addGap(125, 125, 125)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(1339, Short.MAX_VALUE))
        );

        jTabbedPaneVisaoGeral.addTab("Visão Geral", jPanel9);

        jTableClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nome", "Tipo", "CPF / CNPJ", "Telefone", "E-mail"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableClientes);
        if (jTableClientes.getColumnModel().getColumnCount() > 0) {
            jTableClientes.getColumnModel().getColumn(0).setResizable(false);
            jTableClientes.getColumnModel().getColumn(1).setResizable(false);
            jTableClientes.getColumnModel().getColumn(2).setResizable(false);
            jTableClientes.getColumnModel().getColumn(3).setResizable(false);
            jTableClientes.getColumnModel().getColumn(4).setResizable(false);
            jTableClientes.getColumnModel().getColumn(5).setResizable(false);
        }

        jLabelPesquisarCliente.setText("Pesquisar");

        jRadioButtonVisualizarClientesInativos.setText("Visualizar Clientes Inativos");
        jRadioButtonVisualizarClientesInativos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonVisualizarClientesInativosActionPerformed(evt);
            }
        });

        jLabelGestaodeClientesCadastrados.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        jLabelGestaodeClientesCadastrados.setText("Gestão de Clientes Cadastrados");

        jButtonNovoCliente.setText("+ Novo Cliente");
        jButtonNovoCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNovoClienteActionPerformed(evt);
            }
        });

        jButtonInativarCliente.setText("Ativar / Inativar Cliente");
        jButtonInativarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInativarClienteActionPerformed(evt);
            }
        });

        jButtonEditarCliente.setText("Editar Cliente");
        jButtonEditarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditarClienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabelGestaodeClientesCadastrados)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPesquisarCliente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldPesquisarCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonNovoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonInativarCliente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonEditarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonVisualizarClientesInativos))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabelGestaodeClientesCadastrados)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButtonVisualizarClientesInativos, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldPesquisarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelPesquisarCliente)
                        .addComponent(jButtonNovoCliente)
                        .addComponent(jButtonInativarCliente)
                        .addComponent(jButtonEditarCliente)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1509, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPaneVisaoGeral.addTab("Clientes", jPanel1);

        jLabelGestaodeVeiculos.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        jLabelGestaodeVeiculos.setText("Gestão de Veículos");

        jLabelPesquisarVeiculo.setText("Pesquisar");

        jButtonNovoVeiculo.setText("+ Novo Veiculo");
        jButtonNovoVeiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNovoVeiculoActionPerformed(evt);
            }
        });

        jTableVeiculos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Placa", "Chassi", "Ano Fabricação", "Ano Modelo", "Cor", "KM", "Marca", "Modelo", "Cliente"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTableVeiculos);
        if (jTableVeiculos.getColumnModel().getColumnCount() > 0) {
            jTableVeiculos.getColumnModel().getColumn(0).setResizable(false);
            jTableVeiculos.getColumnModel().getColumn(1).setResizable(false);
            jTableVeiculos.getColumnModel().getColumn(2).setResizable(false);
            jTableVeiculos.getColumnModel().getColumn(3).setResizable(false);
            jTableVeiculos.getColumnModel().getColumn(4).setResizable(false);
            jTableVeiculos.getColumnModel().getColumn(5).setResizable(false);
            jTableVeiculos.getColumnModel().getColumn(6).setResizable(false);
            jTableVeiculos.getColumnModel().getColumn(7).setResizable(false);
            jTableVeiculos.getColumnModel().getColumn(8).setResizable(false);
            jTableVeiculos.getColumnModel().getColumn(9).setResizable(false);
        }

        jButtonEditarVeiculo.setText("Editar Veículo");
        jButtonEditarVeiculo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditarVeiculoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jLabelGestaodeVeiculos)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabelPesquisarVeiculo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldPesquisarVeiculo, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonNovoVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonEditarVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator2))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabelGestaodeVeiculos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonNovoVeiculo)
                    .addComponent(jLabelPesquisarVeiculo)
                    .addComponent(jTextFieldPesquisarVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonEditarVeiculo))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1509, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPaneVisaoGeral.addTab("Veículos", jPanel2);

        jLabelGestaodeServicos.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        jLabelGestaodeServicos.setText("Gestão de Serviços");

        jLabelPesquisarServicos.setText("Pesquisar");

        jButtonNovoServico.setText("+ Novo Serviço");

        jTableServicos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome", "Descrição", "Valor", "Garandia (Dias)", "Tempo Estimado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTableServicos);
        if (jTableServicos.getColumnModel().getColumnCount() > 0) {
            jTableServicos.getColumnModel().getColumn(0).setResizable(false);
            jTableServicos.getColumnModel().getColumn(1).setResizable(false);
            jTableServicos.getColumnModel().getColumn(2).setResizable(false);
            jTableServicos.getColumnModel().getColumn(3).setResizable(false);
            jTableServicos.getColumnModel().getColumn(4).setResizable(false);
            jTableServicos.getColumnModel().getColumn(5).setResizable(false);
        }

        jRadioButtonVisualizarServicosInativos.setText("Visualizar Serviços Inativos");

        jButtonEditarServiço.setText("Editar Serviço");

        jButtonInativaServiço.setText("Inativar Serviço");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addComponent(jLabelGestaodeServicos)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabelPesquisarServicos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldPesquisarServicos, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonNovoServico, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonEditarServiço, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonInativaServiço, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButtonVisualizarServicosInativos)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator3))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabelGestaodeServicos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonNovoServico)
                    .addComponent(jLabelPesquisarServicos)
                    .addComponent(jTextFieldPesquisarServicos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButtonVisualizarServicosInativos)
                    .addComponent(jButtonEditarServiço)
                    .addComponent(jButtonInativaServiço))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1509, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPaneVisaoGeral.addTab("Serviços", jPanel3);

        jLabelGestaodeOS.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        jLabelGestaodeOS.setText("Gestão de OS (Ordem de Serviços)");

        jLabelPesquisarOS.setText("Pesquisar");

        jTextFieldPesquisarOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPesquisarOSActionPerformed(evt);
            }
        });

        jButtonNovaOS.setText("+ Nova OS");
        jButtonNovaOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNovaOSActionPerformed(evt);
            }
        });

        jTableOS.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Número OS", "Veículo", "Status da OS", "Data e Hora Abertura", "Valor", "Desconto"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(jTableOS);
        if (jTableOS.getColumnModel().getColumnCount() > 0) {
            jTableOS.getColumnModel().getColumn(0).setResizable(false);
            jTableOS.getColumnModel().getColumn(1).setResizable(false);
            jTableOS.getColumnModel().getColumn(2).setResizable(false);
            jTableOS.getColumnModel().getColumn(3).setResizable(false);
            jTableOS.getColumnModel().getColumn(4).setResizable(false);
            jTableOS.getColumnModel().getColumn(5).setResizable(false);
            jTableOS.getColumnModel().getColumn(6).setResizable(false);
        }

        jButtonGerenciarItensOS.setText("Gerenciar Itens OS");
        jButtonGerenciarItensOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGerenciarItensOSActionPerformed(evt);
            }
        });

        jButtonAvancarStatusOS.setText("Avançar Status OS");
        jButtonAvancarStatusOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAvancarStatusOSActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonGerenciarItensOS, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonAvancarStatusOS, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel10Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jButtonGerenciarItensOS, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE))
                    .addComponent(jButtonAvancarStatusOS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jButtonFilaAtendimento.setText("Fila de Atendimento");
        jButtonFilaAtendimento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFilaAtendimentoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonNovaOS)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonFilaAtendimento)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(3, 3, 3)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGap(6, 6, 6)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane4)
                                .addComponent(jLabelGestaodeOS)
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addComponent(jLabelPesquisarOS)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jTextFieldPesquisarOS, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)))))
                    .addGap(3, 3, 3)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonNovaOS)
                    .addComponent(jButtonFilaAtendimento))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1460, Short.MAX_VALUE)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(3, 3, 3)
                    .addComponent(jLabelGestaodeOS)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelPesquisarOS)
                        .addComponent(jTextFieldPesquisarOS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1436, Short.MAX_VALUE)
                    .addGap(76, 76, 76)))
        );

        jTabbedPaneVisaoGeral.addTab("Ordem de Serviços", jPanel4);

        jLabelGestaodePecas.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        jLabelGestaodePecas.setText("Gestão de Peças");

        jLabelPesquisarPecas.setText("Código Nacional");

        jButtonNovaPeca.setText("+ Novo Peça");

        jButtonEditarPeca.setText("Editar Peça");

        jButtonInativaExcluir.setText("Inativar Peça");
        jButtonInativaExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInativaExcluirActionPerformed(evt);
            }
        });

        jTablePecas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código Nacional", "Código Internacional", "Nome", "Fabricante", "Categoria", "Preço de Custo", "Preço de Venda", "Estoque", "Granatia", "Fornecedor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(jTablePecas);
        if (jTablePecas.getColumnModel().getColumnCount() > 0) {
            jTablePecas.getColumnModel().getColumn(0).setResizable(false);
            jTablePecas.getColumnModel().getColumn(1).setResizable(false);
            jTablePecas.getColumnModel().getColumn(2).setResizable(false);
            jTablePecas.getColumnModel().getColumn(3).setResizable(false);
            jTablePecas.getColumnModel().getColumn(4).setResizable(false);
            jTablePecas.getColumnModel().getColumn(5).setResizable(false);
            jTablePecas.getColumnModel().getColumn(6).setResizable(false);
            jTablePecas.getColumnModel().getColumn(7).setResizable(false);
            jTablePecas.getColumnModel().getColumn(8).setResizable(false);
        }

        jRadioButtonVisualizarPecaExcluida.setText("Visualizar peça Excluida");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonNovaPeca, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonEditarPeca, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonInativaExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonVisualizarPecaExcluida)
                .addContainerGap(1225, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(3, 3, 3)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addGap(6, 6, 6)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane5)
                                .addComponent(jLabelGestaodePecas)
                                .addGroup(jPanel5Layout.createSequentialGroup()
                                    .addComponent(jLabelPesquisarPecas)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jTextFieldPesquisarPecas, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE))))
                        .addComponent(jSeparator5, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addGap(3, 3, 3)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonNovaPeca)
                    .addComponent(jButtonEditarPeca)
                    .addComponent(jButtonInativaExcluir)
                    .addComponent(jRadioButtonVisualizarPecaExcluida))
                .addContainerGap(1530, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(3, 3, 3)
                    .addComponent(jLabelGestaodePecas)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelPesquisarPecas)
                        .addComponent(jTextFieldPesquisarPecas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1509, Short.MAX_VALUE)
                    .addGap(3, 3, 3)))
        );

        jTabbedPaneVisaoGeral.addTab("Peças", jPanel5);

        jLabelGestaodeFornecedores.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        jLabelGestaodeFornecedores.setText("Gestão de Fornecedores");

        jLabelPesquisarFornecedor.setText("Pesquisar");

        jButtonNovoFornecedor.setText("+ Novo Fornecedor");

        jButtonInativarFornecedor.setText("Inativar Fornecedor");

        jButtonEditarFornecedor.setText("Editar Fornecedor");

        jRadioButtonVisualizarFornecedorInativo.setText("Ver Inativos/Excluídos");

        jTableFornecedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Razão Social", "CNPJ", "Telefone", "E-mail", "Endereço"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(jTableFornecedores);
        if (jTableFornecedores.getColumnModel().getColumnCount() > 0) {
            jTableFornecedores.getColumnModel().getColumn(0).setResizable(false);
            jTableFornecedores.getColumnModel().getColumn(1).setResizable(false);
            jTableFornecedores.getColumnModel().getColumn(2).setResizable(false);
            jTableFornecedores.getColumnModel().getColumn(3).setResizable(false);
            jTableFornecedores.getColumnModel().getColumn(4).setResizable(false);
        }

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabelGestaodeFornecedores)
                        .addGap(0, 2010, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane6)
                        .addContainerGap())))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator6))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPesquisarFornecedor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldPesquisarFornecedor, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonNovoFornecedor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonInativarFornecedor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonEditarFornecedor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonVisualizarFornecedorInativo))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabelGestaodeFornecedores)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPesquisarFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPesquisarFornecedor)
                    .addComponent(jButtonNovoFornecedor)
                    .addComponent(jButtonInativarFornecedor)
                    .addComponent(jButtonEditarFornecedor)
                    .addComponent(jRadioButtonVisualizarFornecedorInativo))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 1509, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPaneVisaoGeral.addTab("Fornecedores Peças", jPanel6);

        jLabelGestaodeColaboradores.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        jLabelGestaodeColaboradores.setText("Gestão de Colaboradores");

        jLabelPesquisarColaboradores.setText("Pesquisar");

        jButtonNovoColaborador.setText("+ Novo Colaborador");

        jButtonInativarColaborador.setText("Inativar Colaborador");

        jButtonEditarColaborador.setText("Editar Colaborador");

        jRadioButtonVisualizarColaboradores.setText("Ver Inativos/Excluídos");

        jTableColaboradores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Nome", "CPF", "Telefone", "E-mail", "Funções"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(jTableColaboradores);
        if (jTableColaboradores.getColumnModel().getColumnCount() > 0) {
            jTableColaboradores.getColumnModel().getColumn(0).setResizable(false);
            jTableColaboradores.getColumnModel().getColumn(1).setResizable(false);
            jTableColaboradores.getColumnModel().getColumn(2).setResizable(false);
            jTableColaboradores.getColumnModel().getColumn(3).setResizable(false);
            jTableColaboradores.getColumnModel().getColumn(4).setResizable(false);
        }

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabelGestaodeColaboradores)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jScrollPane7)
                        .addContainerGap())))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator7))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPesquisarColaboradores)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldPesquisarColaboradores, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonNovoColaborador)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonInativarColaborador)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonEditarColaborador)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonVisualizarColaboradores))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabelGestaodeColaboradores)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPesquisarColaboradores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPesquisarColaboradores)
                    .addComponent(jButtonNovoColaborador)
                    .addComponent(jButtonInativarColaborador)
                    .addComponent(jButtonEditarColaborador)
                    .addComponent(jRadioButtonVisualizarColaboradores))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 1509, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPaneVisaoGeral.addTab("Colaboradores", jPanel7);

        jLabelGestaodeParceiros.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        jLabelGestaodeParceiros.setText("Gestão de Parceiros (Serviços)");

        jLabelPesquisarParceiros.setText("Pesquisar");

        jButtonNovoParceiro.setText("+ Novo Parceiro");

        jButtonInativarParceiro.setText("Inativar Parceiro");

        jButtonEditarParceiro.setText("Editar Parceiro");

        jRadioButtonVisualizarParceiros.setText("Ver Inativos/Excluídos");
        jRadioButtonVisualizarParceiros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonVisualizarParceirosActionPerformed(evt);
            }
        });

        jTableParceiros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nome", "CNPJ", "Tipo de Serviço", "Telefone", "E-mail"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane8.setViewportView(jTableParceiros);
        if (jTableParceiros.getColumnModel().getColumnCount() > 0) {
            jTableParceiros.getColumnModel().getColumn(0).setResizable(false);
            jTableParceiros.getColumnModel().getColumn(1).setResizable(false);
            jTableParceiros.getColumnModel().getColumn(2).setResizable(false);
            jTableParceiros.getColumnModel().getColumn(3).setResizable(false);
            jTableParceiros.getColumnModel().getColumn(4).setResizable(false);
            jTableParceiros.getColumnModel().getColumn(5).setResizable(false);
        }

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabelGestaodeParceiros)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addComponent(jScrollPane8)
                        .addContainerGap())))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator8))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPesquisarParceiros)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldPesquisarParceiros, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonNovoParceiro)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonInativarParceiro)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonEditarParceiro)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonVisualizarParceiros))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabelGestaodeParceiros)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPesquisarParceiros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPesquisarParceiros)
                    .addComponent(jButtonNovoParceiro)
                    .addComponent(jButtonInativarParceiro)
                    .addComponent(jButtonEditarParceiro)
                    .addComponent(jRadioButtonVisualizarParceiros))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 1509, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPaneVisaoGeral.addTab("Parceiros (Serviços)", jPanel8);

        jLabelLogo.setFont(new java.awt.Font("DejaVu Sans Condensed", 1, 24)); // NOI18N
        jLabelLogo.setForeground(java.awt.Color.gray);
        jLabelLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelLogo.setText("AV CAR");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPaneVisaoGeral)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPaneVisaoGeral)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonNovoClienteActionPerformed(java.awt.event.ActionEvent evt) {
        if (springContext != null) {
            br.edu.senai.fatesg.avcar.swing.views.utils.ClienteFormUtil.abrirTelaNovo(this, springContext);
            carregarTabelaClientes();
        } else {
            br.edu.senai.fatesg.avcar.swing.views.utils.MensagemUtil.exibirAlertaBancoDeDados(this);
        }
    }

    private void jButtonInativarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonInativarClienteActionPerformed
        int linha = jTableClientes.getSelectedRow();
        if (linha != -1 && clienteController != null) {
            Long id = (Long) jTableClientes.getModel().getValueAt(linha, 0);
            String nome = (String) jTableClientes.getModel().getValueAt(linha, 1);
            
            if (br.edu.senai.fatesg.avcar.swing.views.utils.MensagemUtil.confirmarInativacao(this, nome)) {
                clienteController.toggleStatus(id);
                carregarTabelaClientes();
            }
        }
    }//GEN-LAST:event_jButtonInativarClienteActionPerformed

    private void jButtonEditarClienteActionPerformed(java.awt.event.ActionEvent evt) {
        int linha = jTableClientes.getSelectedRow();
        if (linha != -1 && springContext != null) {
            Long id = (Long) jTableClientes.getModel().getValueAt(linha, 0);
            String tipo = (String) jTableClientes.getModel().getValueAt(linha, 2);
            
            br.edu.senai.fatesg.avcar.swing.views.utils.ClienteFormUtil.abrirTelaEdicao(this, springContext, id, tipo);
            
            carregarTabelaClientes();
        }
    }

    private void jRadioButtonVisualizarClientesInativosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonVisualizarClientesInativosActionPerformed
        // Como o método carregarTabelaClientes já lê o estado deste botão, nós só precisamos chamá-lo de novo!
        carregarTabelaClientes();
    }//GEN-LAST:event_jRadioButtonVisualizarClientesInativosActionPerformed

    private void jButtonNovoVeiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNovoVeiculoActionPerformed
        // Método vazio a ser implementado futuramente
        br.edu.senai.fatesg.avcar.swing.views.utils.VeiculoFormUtil.abrirTelaNovo(this, springContext);
        carregarTabelaVeiculos();
    }//GEN-LAST:event_jButtonNovoVeiculoActionPerformed

    private void jButtonEditarVeiculoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditarVeiculoActionPerformed
        int linha = jTableVeiculos.getSelectedRow();
        if (linha != -1 && springContext != null) {
            // Busca a linha real no Model (caso a tabela esteja ordenada)
            int modelRow = jTableVeiculos.convertRowIndexToModel(linha);
            // Pega o ID diretamente do Model (onde ele ainda existe na coluna 0)
            Long idVeiculo = (Long) jTableVeiculos.getModel().getValueAt(modelRow, 0); 
            
            br.edu.senai.fatesg.avcar.swing.views.utils.VeiculoFormUtil.abrirTelaEdicao(this, springContext, idVeiculo);
            carregarTabelaVeiculos();
        }
    }//GEN-LAST:event_jButtonEditarVeiculoActionPerformed

    private void jButtonNovaOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNovaOSActionPerformed
        if (springContext != null) {
            br.edu.senai.fatesg.avcar.swing.views.CadastroOS cadastroOS = springContext.getBean(br.edu.senai.fatesg.avcar.swing.views.CadastroOS.class);
            cadastroOS.prepararParaNovo();
            br.edu.senai.fatesg.avcar.swing.views.utils.JanelaUtil.abrirPainelComoModal(this, "Nova Ordem de Serviço", cadastroOS);
            carregarTabelaOS();
        }
    }//GEN-LAST:event_jButtonNovaOSActionPerformed

    private void jTextFieldPesquisarOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPesquisarOSActionPerformed
        carregarTabelaOS();
    }//GEN-LAST:event_jTextFieldPesquisarOSActionPerformed

    private void jButtonGerenciarItensOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGerenciarItensOSActionPerformed
        int linhaSelecionada = jTableOS.getSelectedRow();
        if (linhaSelecionada == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Selecione uma Ordem de Serviço na tabela.");
            return;
        }
        
        try {
            int modelRow = jTableOS.convertRowIndexToModel(linhaSelecionada);
            br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoDTO osSelecionada = listaOSAtual.get(modelRow);
            
            br.edu.senai.fatesg.avcar.swing.views.CadastroItemOS cadastroItem = springContext.getBean(br.edu.senai.fatesg.avcar.swing.views.CadastroItemOS.class);
            cadastroItem.preparar(osSelecionada.getId());
            br.edu.senai.fatesg.avcar.swing.views.utils.JanelaUtil.abrirPainelComoModal(this, "Gerenciar Itens da OS #" + osSelecionada.getNumeroOs(), cadastroItem);
            
            carregarTabelaOS();
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Erro ao abrir tela de itens: " + e.getMessage());
        }
    }//GEN-LAST:event_jButtonGerenciarItensOSActionPerformed

    private void jButtonAvancarStatusOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAvancarStatusOSActionPerformed
        int linhaSelecionada = jTableOS.getSelectedRow();
        if (linhaSelecionada == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Selecione uma Ordem de Serviço na tabela.");
            return;
        }

        int resposta = javax.swing.JOptionPane.showConfirmDialog(this, 
                "Deseja realmente avançar o status desta OS?", 
                "Confirmar Avanço", 
                javax.swing.JOptionPane.YES_NO_OPTION);
                
        if (resposta != javax.swing.JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int modelRow = jTableOS.convertRowIndexToModel(linhaSelecionada);
            br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoDTO osSelecionada = listaOSAtual.get(modelRow);
            String statusAtual = osSelecionada.getStatus();

            if ("ABERTA".equalsIgnoreCase(statusAtual) || "Aberta".equalsIgnoreCase(statusAtual)) {
                // Passo 5: Avança para ORCAMENTO e gera PDF
                osSelecionada = ordemServicoController.avancarOrcamento(osSelecionada.getId()).getBody();
                
                var servicos = ordemServicoController.listarItensServico(osSelecionada.getId()).getBody();
                var pecas = ordemServicoController.listarItensPeca(osSelecionada.getId()).getBody();
                var servicosExternos = ordemServicoController.listarServicosExternos(osSelecionada.getId()).getBody();
                
                br.edu.senai.fatesg.avcar.swing.views.utils.GeradorPdfOS.gerarPdfOrcamento(osSelecionada, servicos, pecas, servicosExternos);
                
                carregarTabelaOS();
                javax.swing.JOptionPane.showMessageDialog(this, "Status avançado para 'Em orçamento' e PDF gerado com sucesso.");
                
            } else if ("EM_ORCAMENTO".equalsIgnoreCase(statusAtual) || "Em orçamento".equalsIgnoreCase(statusAtual) || "Aguardando peça".equalsIgnoreCase(statusAtual)) {
                // Passo 6: Abre tela de Itens antes de avançar para EXECUCAO
                br.edu.senai.fatesg.avcar.swing.views.CadastroItemOS cadastroItem = springContext.getBean(br.edu.senai.fatesg.avcar.swing.views.CadastroItemOS.class);
                cadastroItem.preparar(osSelecionada.getId());
                br.edu.senai.fatesg.avcar.swing.views.utils.JanelaUtil.abrirPainelComoModal(this, "Gerenciar Itens da OS", cadastroItem);
                
                // Agora envia comando pro backend avançar para EXECUCAO
                osSelecionada = ordemServicoController.avancarExecucao(osSelecionada.getId()).getBody();
                carregarTabelaOS();
                javax.swing.JOptionPane.showMessageDialog(this, "Status da OS avançado para 'Em execução' com sucesso.");
                
            } else if ("EM_EXECUCAO".equalsIgnoreCase(statusAtual) || "Em execução".equalsIgnoreCase(statusAtual)) {
                // Passo 7: Fluxo Final de Garantia, Desconto e Finalização
                br.edu.senai.fatesg.avcar.swing.views.CadastroGarantia telaGarantia = springContext.getBean(br.edu.senai.fatesg.avcar.swing.views.CadastroGarantia.class);
                telaGarantia.preparar(osSelecionada.getId());
                br.edu.senai.fatesg.avcar.swing.views.utils.JanelaUtil.abrirPainelComoModal(this, "Aplicar Garantia Adicional", telaGarantia);
                
                br.edu.senai.fatesg.avcar.swing.views.CadastroDesconto telaDesconto = springContext.getBean(br.edu.senai.fatesg.avcar.swing.views.CadastroDesconto.class);
                telaDesconto.preparar(osSelecionada.getId());
                br.edu.senai.fatesg.avcar.swing.views.utils.JanelaUtil.abrirPainelComoModal(this, "Aplicar Desconto", telaDesconto);

                var itensServicos = ordemServicoController.listarItensServico(osSelecionada.getId()).getBody();
                var itensPecas = ordemServicoController.listarItensPeca(osSelecionada.getId()).getBody();
                var itensExternos = ordemServicoController.listarServicosExternos(osSelecionada.getId()).getBody();
                
                java.util.List<Double> valServicos = itensServicos != null ? itensServicos.stream().map(br.edu.senai.fatesg.avcar.business.servicos.ItemServicoDTO::getSubtotal).collect(java.util.stream.Collectors.toList()) : new java.util.ArrayList<>();
                java.util.List<Double> valPecas = itensPecas != null ? itensPecas.stream().map(br.edu.senai.fatesg.avcar.business.pecas.ItemPecaDTO::getSubtotal).collect(java.util.stream.Collectors.toList()) : new java.util.ArrayList<>();
                java.util.List<Double> valExternos = itensExternos != null ? itensExternos.stream().map(br.edu.senai.fatesg.avcar.business.servicos.ServicoExternoDTO::getValor).collect(java.util.stream.Collectors.toList()) : new java.util.ArrayList<>();
                
                // Pega valorDesconto do DTO atualizado
                br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoDTO dtoDesconto = ordemServicoController.buscarPorId(osSelecionada.getId()).getBody();
                double descontoAplicado = dtoDesconto != null ? dtoDesconto.getValorDesconto() : 0.0;
                
                double valorCalculado = br.edu.senai.fatesg.avcar.datastructures.CalculoOS.calcularValorTotal(valServicos, valPecas, valExternos, descontoAplicado);
                
                int resp = javax.swing.JOptionPane.showConfirmDialog(this, 
                    "Confirmar finalização da OS?\nValor Total (cálculo recursivo): R$ " + String.format("%.2f", valorCalculado), 
                    "Finalizar OS", javax.swing.JOptionPane.YES_NO_OPTION);
                if (resp != javax.swing.JOptionPane.YES_OPTION) {
                    return;
                }

                // Avança para FINALIZADA
                osSelecionada = ordemServicoController.finalizar(osSelecionada.getId()).getBody();
                
                // Atualiza DTO antes de gerar PDF final
                osSelecionada = ordemServicoController.buscarPorId(osSelecionada.getId()).getBody();
                
                var servicos = ordemServicoController.listarItensServico(osSelecionada.getId()).getBody();
                var pecas = ordemServicoController.listarItensPeca(osSelecionada.getId()).getBody();
                var servicosExternos = ordemServicoController.listarServicosExternos(osSelecionada.getId()).getBody();
                var garantias = ordemServicoController.calcularGarantia(osSelecionada.getId()).getBody();
                
                // Gera o PDF de Entrega
                br.edu.senai.fatesg.avcar.swing.views.utils.GeradorPdfOS.gerarPdfEntrega(osSelecionada, servicos, pecas, servicosExternos, garantias);
                
                carregarTabelaOS();
                javax.swing.JOptionPane.showMessageDialog(this, "OS Finalizada com sucesso! Relatório de Entrega gerado.");
                
            } else {
                // Para outros fluxos ou finalizada
                javax.swing.JOptionPane.showMessageDialog(this, "Esta OS está no status '" + statusAtual + "' e não pode ser avançada daqui.");
            }
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Erro ao avançar status: " + e.getMessage());
        }
    }//GEN-LAST:event_jButtonAvancarStatusOSActionPerformed

    private void jButtonFilaAtendimentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFilaAtendimentoActionPerformed
        try {
            br.edu.senai.fatesg.avcar.swing.views.CadastroFilaDeEspera telaFila = springContext.getBean(br.edu.senai.fatesg.avcar.swing.views.CadastroFilaDeEspera.class);
            telaFila.carregarDados();
            br.edu.senai.fatesg.avcar.swing.views.utils.JanelaUtil.abrirPainelComoModal(this, "Fila de Atendimento", telaFila);
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Erro ao abrir Fila de Atendimento: " + e.getMessage());
        }
    }//GEN-LAST:event_jButtonFilaAtendimentoActionPerformed

    private void jButtonInativaExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonInativaExcluirActionPerformed
        // Método vazio a ser implementado futuramente
    }//GEN-LAST:event_jButtonInativaExcluirActionPerformed

    private void jRadioButtonVisualizarParceirosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonVisualizarParceirosActionPerformed
        // Método vazio a ser implementado futuramente
    }//GEN-LAST:event_jRadioButtonVisualizarParceirosActionPerformed

    /**
     * @param args the command line arguments
     */

    /**
     * Substitui os painéis do Designer (jPanel11-14) por Cards KPI responsivos.
     * Chamado uma única vez no PostConstruct. Os cards se auto-redimensionam
     * de 1920×1080 até 1024×768 sem cortar o conteúdo.
     */
    private void configurarDashboard() {
        // Remove os componentes originais do Designer (jPanel11-14, jLabel1-4)
        jPanel9.removeAll();
        jPanel9.setLayout(new java.awt.BorderLayout());

        // Painel com GridLayout responsivo: 1 linha × 4 colunas, gap de 16px
        javax.swing.JPanel painelCards = new javax.swing.JPanel(new java.awt.GridLayout(1, 4, 16, 0));
        painelCards.setOpaque(false);
        painelCards.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 20, 20, 20));

        // Cores semânticas (harmônicas com FlatDarkLaf)
        java.awt.Color corBranco   = new java.awt.Color(220, 223, 228);
        java.awt.Color corVerde    = new java.awt.Color(92, 184, 92);
        java.awt.Color corLaranja  = new java.awt.Color(240, 173, 78);
        java.awt.Color corVermelho = new java.awt.Color(217, 83, 79);

        // Cria os 4 Cards KPI com auto-fonte
        cardTotalOS     = new br.edu.senai.fatesg.avcar.swing.views.utils.CardKPI("Total de OS", "0", corBranco);
        cardFaturamento = new br.edu.senai.fatesg.avcar.swing.views.utils.CardKPI("Faturamento", "R$ 0,00", corVerde);
        cardDescontos   = new br.edu.senai.fatesg.avcar.swing.views.utils.CardKPI("Descontos", "R$ 0,00", corLaranja);
        cardOSAbertas   = new br.edu.senai.fatesg.avcar.swing.views.utils.CardKPI("OS Abertas", "0", corVermelho);

        painelCards.add(cardTotalOS);
        painelCards.add(cardFaturamento);
        painelCards.add(cardDescontos);
        painelCards.add(cardOSAbertas);

        jPanel9.add(painelCards, java.awt.BorderLayout.NORTH);
        
        chartDashboard = new br.edu.senai.fatesg.avcar.swing.views.utils.ModernPieChart(0, 0);
        chartBarDashboard = new br.edu.senai.fatesg.avcar.swing.views.utils.ModernBarChart();
        chartHBarDashboard = new br.edu.senai.fatesg.avcar.swing.views.utils.ModernHorizontalBarChart();
        chartLineDashboard = new br.edu.senai.fatesg.avcar.swing.views.utils.ModernLineChart();
        
        painelCentroDashboard = new javax.swing.JPanel(new java.awt.GridLayout(2, 2, 30, 30));
        painelCentroDashboard.setOpaque(false);
        painelCentroDashboard.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        painelCentroDashboard.add(chartDashboard);
        painelCentroDashboard.add(chartBarDashboard);
        painelCentroDashboard.add(chartHBarDashboard);
        painelCentroDashboard.add(chartLineDashboard);
        
        jPanel9.add(painelCentroDashboard, java.awt.BorderLayout.CENTER);
        
        jPanel9.revalidate();
        jPanel9.repaint();
    }

    /**
     * Atualiza os valores dos Cards KPI com dados do Backend.
     * CLEAN CODE: A Tela não sabe como calcular. Ela apenas pede os dados processados!
     */
    private void atualizarDashboard() {
        try {
            if (ordemServicoController == null || cardTotalOS == null) return;

            br.edu.senai.fatesg.avcar.business.ordemservico.DashboardDTO dashboard =
                    ordemServicoController.obterResumoDashboard().getBody();

            if (dashboard == null) return;

            java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("pt", "BR"));

            cardTotalOS.atualizarValor(String.valueOf(dashboard.getTotalOS()));
            cardFaturamento.atualizarValor(nf.format(dashboard.getFaturamentoTotal()));
            cardDescontos.atualizarValor(nf.format(dashboard.getDescontosTotal()));
            cardOSAbertas.atualizarValor(String.valueOf(dashboard.getOsAbertas()));

            // Atualiza o Gráfico em tempo real!
            if (chartDashboard != null) {
                chartDashboard.atualizarDados(dashboard.getTotalOS(), dashboard.getOsAbertas());
            }
            
            if (chartBarDashboard != null) {
                double totalPecas = 0;
                double totalMaoObra = 0;
                double totalServExterno = 0;
                
                // Gráfico 3: Curva de Faturamento (Últimos 6 meses)
                java.time.YearMonth mesAtual = java.time.YearMonth.now();
                java.util.Map<java.time.YearMonth, Double> meses = new java.util.TreeMap<>();
                
                // Preenche os últimos 6 meses com ZERO para garantir que o gráfico sempre desenhe
                for (int i = 5; i >= 0; i--) {
                    meses.put(mesAtual.minusMonths(i), 0.0);
                }
                
                java.util.List<br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoDTO> osList = ordemServicoController.listar().getBody();
                if (osList != null) {
                    for (br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoDTO os : osList) {
                        boolean isFaturada = false;
                        if (os.getStatus() != null) {
                            String st = os.getStatus().toUpperCase();
                            if (st.equals("FINALIZADA") || st.equals("FINALIZADO") || st.equals("PAGA")) {
                                isFaturada = true;
                            }
                        }
                        
                        // Atualiza Gráfico de Categorias APENAS com OS faturadas
                        if (isFaturada) {
                            totalPecas += os.getValorTotalPecas();
                            totalMaoObra += os.getValorMaoObra();
                            totalServExterno += os.getValorServicoExterno();
                        }
                        
                        // Atualiza Gráfico Mensal APENAS com OS faturadas
                        if (isFaturada && os.getDataAbertura() != null) {
                            java.time.YearMonth ym = java.time.YearMonth.from(os.getDataAbertura());
                            if (meses.containsKey(ym)) {
                                double somaBruta = os.getValorMaoObra() + os.getValorTotalPecas() + os.getValorServicoExterno();
                                double valorReal = somaBruta - os.getValorDesconto();
                                meses.put(ym, meses.get(ym) + valorReal);
                            }
                        }
                    }
                }
                
                chartBarDashboard.atualizarDados(totalPecas, totalMaoObra, totalServExterno);
                
                java.util.Map<String, Double> faturamentoMensal = new java.util.LinkedHashMap<>();
                // O Locale pt-BR garante que fique "jan", "fev", "mar"
                java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("MMM", new java.util.Locale("pt", "BR"));
                for (java.util.Map.Entry<java.time.YearMonth, Double> entry : meses.entrySet()) {
                    String labelMes = entry.getKey().format(dtf);
                    // Capitaliza a primeira letra (Ex: "Jan")
                    labelMes = labelMes.substring(0, 1).toUpperCase() + labelMes.substring(1);
                    faturamentoMensal.put(labelMes, entry.getValue());
                }
                if (chartLineDashboard != null) chartLineDashboard.atualizarDados(faturamentoMensal);
            }
            
            // Gráfico 4: Top Marcas
            if (chartHBarDashboard != null && veiculoController != null) {
                java.util.List<br.edu.senai.fatesg.avcar.business.veiculos.VeiculoDTO> veiculos = veiculoController.listar(false).getBody();
                java.util.Map<String, Integer> marcasMap = new java.util.HashMap<>();
                if (veiculos != null) {
                    for (br.edu.senai.fatesg.avcar.business.veiculos.VeiculoDTO v : veiculos) {
                        String marca = v.getMarcaNome();
                        marcasMap.put(marca, marcasMap.getOrDefault(marca, 0) + 1);
                    }
                }
                java.util.Map<String, Integer> topMarcas = marcasMap.entrySet().stream()
                    .sorted(java.util.Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10)
                    .collect(java.util.stream.Collectors.toMap(
                        java.util.Map.Entry::getKey, 
                        java.util.Map.Entry::getValue, 
                        (e1, e2) -> e1, 
                        java.util.LinkedHashMap::new
                    ));
                chartHBarDashboard.atualizarDados(topMarcas);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar métricas da dashboard: " + e.getMessage());
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAvancarStatusOS;
    private javax.swing.JButton jButtonEditarCliente;
    private javax.swing.JButton jButtonEditarColaborador;
    private javax.swing.JButton jButtonEditarFornecedor;
    private javax.swing.JButton jButtonEditarParceiro;
    private javax.swing.JButton jButtonEditarPeca;
    private javax.swing.JButton jButtonEditarServiço;
    private javax.swing.JButton jButtonEditarVeiculo;
    private javax.swing.JButton jButtonFilaAtendimento;
    private javax.swing.JButton jButtonGerenciarItensOS;
    private javax.swing.JButton jButtonInativaExcluir;
    private javax.swing.JButton jButtonInativaServiço;
    private javax.swing.JButton jButtonInativarCliente;
    private javax.swing.JButton jButtonInativarColaborador;
    private javax.swing.JButton jButtonInativarFornecedor;
    private javax.swing.JButton jButtonInativarParceiro;
    private javax.swing.JButton jButtonNovaOS;
    private javax.swing.JButton jButtonNovaPeca;
    private javax.swing.JButton jButtonNovoCliente;
    private javax.swing.JButton jButtonNovoColaborador;
    private javax.swing.JButton jButtonNovoFornecedor;
    private javax.swing.JButton jButtonNovoParceiro;
    private javax.swing.JButton jButtonNovoServico;
    private javax.swing.JButton jButtonNovoVeiculo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelGestaodeClientesCadastrados;
    private javax.swing.JLabel jLabelGestaodeColaboradores;
    private javax.swing.JLabel jLabelGestaodeFornecedores;
    private javax.swing.JLabel jLabelGestaodeOS;
    private javax.swing.JLabel jLabelGestaodeParceiros;
    private javax.swing.JLabel jLabelGestaodePecas;
    private javax.swing.JLabel jLabelGestaodeServicos;
    private javax.swing.JLabel jLabelGestaodeVeiculos;
    private javax.swing.JLabel jLabelLogo;
    private javax.swing.JLabel jLabelPesquisarCliente;
    private javax.swing.JLabel jLabelPesquisarColaboradores;
    private javax.swing.JLabel jLabelPesquisarFornecedor;
    private javax.swing.JLabel jLabelPesquisarOS;
    private javax.swing.JLabel jLabelPesquisarParceiros;
    private javax.swing.JLabel jLabelPesquisarPecas;
    private javax.swing.JLabel jLabelPesquisarServicos;
    private javax.swing.JLabel jLabelPesquisarVeiculo;
    private javax.swing.JLabel jLabelValorDescontos2;
    private javax.swing.JLabel jLabelValorFaturamento;
    private javax.swing.JLabel jLabelValorOSAbertas2;
    private javax.swing.JLabel jLabelValorTotalOS;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jRadioButtonVisualizarClientesInativos;
    private javax.swing.JRadioButton jRadioButtonVisualizarColaboradores;
    private javax.swing.JRadioButton jRadioButtonVisualizarFornecedorInativo;
    private javax.swing.JRadioButton jRadioButtonVisualizarParceiros;
    private javax.swing.JRadioButton jRadioButtonVisualizarPecaExcluida;
    private javax.swing.JRadioButton jRadioButtonVisualizarServicosInativos;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JTabbedPane jTabbedPaneVisaoGeral;
    private javax.swing.JTable jTableClientes;
    private javax.swing.JTable jTableColaboradores;
    private javax.swing.JTable jTableFornecedores;
    private javax.swing.JTable jTableOS;
    private javax.swing.JTable jTableParceiros;
    private javax.swing.JTable jTablePecas;
    private javax.swing.JTable jTableServicos;
    private javax.swing.JTable jTableVeiculos;
    private javax.swing.JTextField jTextFieldPesquisarCliente;
    private javax.swing.JTextField jTextFieldPesquisarColaboradores;
    private javax.swing.JTextField jTextFieldPesquisarFornecedor;
    private javax.swing.JTextField jTextFieldPesquisarOS;
    private javax.swing.JTextField jTextFieldPesquisarParceiros;
    private javax.swing.JTextField jTextFieldPesquisarPecas;
    private javax.swing.JTextField jTextFieldPesquisarServicos;
    private javax.swing.JTextField jTextFieldPesquisarVeiculo;
    // End of variables declaration//GEN-END:variables
}
