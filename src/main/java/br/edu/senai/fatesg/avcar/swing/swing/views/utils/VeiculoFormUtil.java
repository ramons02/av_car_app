package br.edu.senai.fatesg.avcar.swing.views.utils;

import br.edu.senai.fatesg.avcar.business.clientes.ClienteController;
import br.edu.senai.fatesg.avcar.business.clientes.ClienteDTO;
import br.edu.senai.fatesg.avcar.business.veiculos.MarcaDTO;
import br.edu.senai.fatesg.avcar.business.veiculos.ModeloDTO;
import br.edu.senai.fatesg.avcar.business.veiculos.VeiculoController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;

public class VeiculoFormUtil {

    public static class MarcaItem {
        public final Long id;
        public final String nome;
        public final String logoUrl;

        public MarcaItem(Long id, String nome, String logoUrl) {
            this.id = id;
            this.nome = nome;
            this.logoUrl = logoUrl;
        }
        @Override
        public String toString() { return nome; }
    }

    public static class ClienteItem {
        public final Long id;
        public final String nome;

        public ClienteItem(Long id, String nome) {
            this.id = id;
            this.nome = nome;
        }
        @Override
        public String toString() { return nome; }
    }

    private static final HashMap<String, ImageIcon> logoCache = new HashMap<>();

    public static class MarcaListRenderer extends JLabel implements ListCellRenderer<MarcaItem> {
        public MarcaListRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends MarcaItem> list, MarcaItem value,
                int index, boolean isSelected, boolean cellHasFocus) {
            
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setFont(list.getFont());
            
            if (value == null || value.id == null) {
                setIcon(null);
                setText(value != null ? value.nome : "");
                setToolTipText(null);
                return this;
            }
            
            setToolTipText(value.nome);
            ImageIcon icon = null;
            if (value.logoUrl != null && !value.logoUrl.isBlank()) {
                if (logoCache.containsKey(value.logoUrl)) {
                    icon = logoCache.get(value.logoUrl);
                } else {
                    logoCache.put(value.logoUrl, null); // Cache anti-travamento
                    try {
                        var is = getClass().getClassLoader().getResourceAsStream(value.logoUrl);
                        if (is != null) {
                            BufferedImage origem = ImageIO.read(is);
                            is.close();
                            if (origem != null) {
                                BufferedImage redim = new BufferedImage(36, 28, BufferedImage.TYPE_INT_ARGB);
                                Graphics2D g = redim.createGraphics();
                                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                                g.drawImage(origem, 0, 0, 36, 28, null);
                                g.dispose();
                                icon = new ImageIcon(redim);
                                logoCache.put(value.logoUrl, icon);
                            }
                        }
                    } catch (Exception ignored) {}
                }
            }
            if (icon != null) {
                setIcon(icon);
                setText("");
            } else {
                setIcon(null);
                setText(value.nome);
            }
            return this;
        }
    }

    /**
     * Configura o ComboBox de Marcas, aplicando as imagens (logos) e populando do banco.
     * Também acopla um evento: ao escolher a Marca, ele carrega automaticamente os Modelos no cbModelo.
     */
    public static void configurarComboMarcasEModelos(
            JComboBox<MarcaItem> cbMarca, 
            JComboBox<String> cbModelo, 
            VeiculoController veiculoController) {
        
        // Aplica o design de imagens
        cbMarca.setRenderer(new MarcaListRenderer());
        
        // Esconde o (ID) do cbModelo na parte visual, mantendo no back-end
        cbModelo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    String str = value.toString();
                    if (str.contains("(")) {
                        str = str.substring(0, str.lastIndexOf('(')).trim();
                    }
                    setText(str);
                }
                return this;
            }
        });
        
        // Evento: Selecionou marca, carrega modelo
        cbMarca.addActionListener(e -> {
            MarcaItem selected = (MarcaItem) cbMarca.getSelectedItem();
            cbModelo.removeAllItems();
            cbModelo.addItem("Selecione...");
            if (selected != null && selected.id != null) {
                try {
                    List<ModeloDTO> modelos = veiculoController.listarModelos(selected.id).getBody();
                    if (modelos != null) {
                        for (ModeloDTO m : modelos) {
                            cbModelo.addItem(m.getNomeModelo() + " (" + m.getIdModelo() + ")");
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao carregar modelos: " + ex.getMessage());
                }
            }
        });

        // Carrega as marcas do banco no combobox
        cbMarca.removeAllItems();
        cbMarca.addItem(new MarcaItem(null, "Selecione...", null));
        try {
            List<MarcaDTO> marcas = veiculoController.listarMarcas().getBody();
            if (marcas != null) {
                for (MarcaDTO m : marcas) {
                    cbMarca.addItem(new MarcaItem(m.getIdMarca(), m.getNomeMarca(), m.getLogoUrl()));
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar marcas: " + ex.getMessage());
        }
    }

    /**
     * Configura e carrega todos os clientes no ComboBox correspondente.
     */
    public static void configurarComboClientes(JComboBox<ClienteItem> cbCliente, ClienteController clienteController) {
        cbCliente.removeAllItems();
        try {
            List<ClienteDTO> clientes = clienteController.listar(false).getBody();
            if (clientes != null) {
                for (ClienteDTO c : clientes) {
                    cbCliente.addItem(new ClienteItem(c.getId(), c.getNome()));
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar clientes: " + ex.getMessage());
        }
    }
    // ----------------------------------------------------
    // NAVEGAÇÃO E ABERTURA DA TELA
    // ----------------------------------------------------

    public static void abrirTelaNovo(java.awt.Window parent, org.springframework.context.ApplicationContext ctx) {
        try {
            br.edu.senai.fatesg.avcar.swing.views.CadastroVeiculo panel = ctx.getBean(br.edu.senai.fatesg.avcar.swing.views.CadastroVeiculo.class);
            panel.prepararParaNovo();
            br.edu.senai.fatesg.avcar.swing.views.utils.JanelaUtil.abrirPainelComoModal(parent, "Novo Veículo", panel);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger("ErrorLog").log(java.util.logging.Level.SEVERE, "Erro capturado", e);
            JOptionPane.showMessageDialog(parent, "Falha Crítica ao abrir a tela: " + e.getMessage());
        }
    }

    public static void abrirTelaEdicao(java.awt.Window parent, org.springframework.context.ApplicationContext ctx, Long idVeiculo) {
        try {
            br.edu.senai.fatesg.avcar.swing.views.CadastroVeiculo panel = ctx.getBean(br.edu.senai.fatesg.avcar.swing.views.CadastroVeiculo.class);
            panel.preencherParaEdicao(idVeiculo);
            br.edu.senai.fatesg.avcar.swing.views.utils.JanelaUtil.abrirPainelComoModal(parent, "Editar Veículo", panel);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger("ErrorLog").log(java.util.logging.Level.SEVERE, "Erro capturado", e);
            JOptionPane.showMessageDialog(parent, "Falha Crítica ao abrir a tela: " + e.getMessage());
        }
    }
}
