package br.edu.senai.fatesg.avcar.swing.views.utils;

import javax.swing.JTable;

public class TabelaUtil {

    public static void ocultarColuna(JTable tabela, int indice) {
        if (tabela != null && tabela.getColumnCount() > indice) {
            tabela.getColumnModel().removeColumn(tabela.getColumnModel().getColumn(indice));
        }
    }

    public static void liberarRedimensionamento(JTable tabela) {
        if (tabela != null) {
            for (int i = 0; i < tabela.getColumnCount(); i++) {
                tabela.getColumnModel().getColumn(i).setResizable(true);
            }
        }
    }

    public static void aplicarResponsividade(JTable tabela) {
        if (tabela == null) return;
        
        java.awt.Container parent = tabela.getParent();
        if (parent instanceof javax.swing.JViewport) {
            javax.swing.JScrollPane scrollPane = (javax.swing.JScrollPane) parent.getParent();
            
            // PREVINE QUE O GROUPLAYOUT DO NETBEANS CORTE A TABELA QUANDO A JANELA DIMINUI
            java.awt.Component current = scrollPane;
            while (current != null && !(current instanceof java.awt.Window)) {
                if (current instanceof javax.swing.JComponent) {
                    ((javax.swing.JComponent) current).setMinimumSize(new java.awt.Dimension(0, 0));
                }
                current = current.getParent();
            }
            
            scrollPane.addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        int viewportWidth = scrollPane.getViewport().getWidth();
                        
                        // Se a aba estiver oculta, o viewport é 0. Pegamos a largura da janela como referência
                        if (viewportWidth <= 0) {
                            java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(tabela);
                            if (window != null) {
                                viewportWidth = window.getWidth() - 50;
                            } else {
                                viewportWidth = 1024;
                            }
                        }
                        
                        // Verifica se a janela principal está maximizada
                        java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(tabela);
                        boolean isMaximized = false;
                        if (window instanceof java.awt.Frame) {
                            isMaximized = (((java.awt.Frame) window).getExtendedState() & java.awt.Frame.MAXIMIZED_BOTH) == java.awt.Frame.MAXIMIZED_BOTH;
                        }
                        
                        // Se houver espaço suficiente (telas grandes), a tabela preenche a tela sem scroll horizontal
                        if (isMaximized || viewportWidth > 1200) {
                            tabela.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
                            for (int i = 0; i < tabela.getColumnCount(); i++) {
                                javax.swing.table.TableColumn col = tabela.getColumnModel().getColumn(i);
                                col.setMinWidth(15);
                                col.setPreferredWidth(75); // Reseta o preferred width para o Java distribuir nativamente
                            }
                        } else {
                            // Janela restaurada (menor). Aplica a regra das colunas largas e barra horizontal
                            tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                            
                            // Garante que cada coluna ocupe um espaço decente
                            int larguraColuna = Math.max(300, viewportWidth / 3);
                            
                            for (int i = 0; i < tabela.getColumnCount(); i++) {
                                javax.swing.table.TableColumn col = tabela.getColumnModel().getColumn(i);
                                col.setPreferredWidth(larguraColuna);
                                col.setMinWidth(200); 
                            }
                        }
                        
                        tabela.revalidate();
                        tabela.repaint();
                    });
                }
            });
            
            // Dispara evento inicial
            java.awt.EventQueue.invokeLater(() -> {
                scrollPane.dispatchEvent(new java.awt.event.ComponentEvent(scrollPane, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
            });
        }
    }

    public static void definirLarguraFixa(JTable tabela, int indice, int larguraPixels) {
        if (tabela != null && tabela.getColumnCount() > indice) {
            tabela.getColumnModel().getColumn(indice).setMinWidth(larguraPixels);
            tabela.getColumnModel().getColumn(indice).setMaxWidth(larguraPixels);
            tabela.getColumnModel().getColumn(indice).setPreferredWidth(larguraPixels);
        }
    }

    public static void aplicarCorNoStatus(JTable tabela) {
        if (tabela != null) {
            for (int i = 0; i < tabela.getColumnCount(); i++) {
                if (tabela.getColumnName(i).equalsIgnoreCase("Status")) {
                    tabela.getColumnModel().getColumn(i).setCellRenderer(new StatusRenderer());
                    break;
                }
            }
        }
    }

    public record ImagemComTexto(String urlLocal, String texto) {
        @Override
        public String toString() { return texto; }
    }

    private static final java.util.HashMap<String, javax.swing.ImageIcon> logoCache = new java.util.HashMap<>();

    public static void aplicarRenderizadorDeImagem(javax.swing.JTable tabela, int colunaIndex) {
        if (tabela == null || colunaIndex < 0 || colunaIndex >= tabela.getColumnCount()) return;
        
        // Dá um "respiro" maior para as linhas da tabela
        if (tabela.getRowHeight() < 36) {
            tabela.setRowHeight(36);
        }
        
        tabela.getColumnModel().getColumn(colunaIndex).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
                setHorizontalAlignment(CENTER);
                // Margem invisível para não colar nas bordas
                setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 5, 2, 5));
                
                if (value instanceof ImagemComTexto iv) {
                    if (iv.urlLocal() != null && !iv.urlLocal().isBlank()) {
                        javax.swing.ImageIcon icon = logoCache.get(iv.urlLocal());
                        if (icon == null) {
                            try {
                                java.io.InputStream is = getClass().getClassLoader().getResourceAsStream(iv.urlLocal());
                                if (is != null) {
                                    java.awt.image.BufferedImage origem = javax.imageio.ImageIO.read(is);
                                    is.close();
                                    if (origem != null) {
                                        // Cálculo de Aspect Ratio
                                        int max = 28;
                                        int w = origem.getWidth();
                                        int h = origem.getHeight();
                                        double ratio = (double) w / h;
                                        if (w > h) {
                                            w = max;
                                            h = (int) (max / ratio);
                                        } else {
                                            h = max;
                                            w = (int) (max * ratio);
                                        }
                                        
                                        java.awt.image.BufferedImage redim = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                                        java.awt.Graphics2D g = redim.createGraphics();
                                        g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                                        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                                        g.drawImage(origem, 0, 0, w, h, null);
                                        g.dispose();
                                        icon = new javax.swing.ImageIcon(redim);
                                        logoCache.put(iv.urlLocal(), icon);
                                    }
                                }
                            } catch (Exception e) {}
                        }
                        if (icon != null) {
                            setIcon(icon);
                            setText("");
                            setToolTipText(iv.texto());
                            return this;
                        }
                    }
                    setIcon(null);
                    setText(iv.texto());
                    setToolTipText(null);
                } else if (value != null) {
                    setIcon(null);
                    setText(value.toString());
                }
                return this;
            }
        });
    }

    public static void adicionarBuscaEmTempoReal(javax.swing.JTextField campo, Runnable acaoBusca) {
        if (campo != null && acaoBusca != null) {
            campo.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { acaoBusca.run(); }
                @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { acaoBusca.run(); }
                @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { acaoBusca.run(); }
            });
        }
    }

    public static void centralizarColunas(JTable tabela, int... indices) {
        if (tabela != null) {
            javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (!isSelected && c instanceof javax.swing.JComponent) {
                        java.awt.Color alt = javax.swing.UIManager.getColor("Table.alternateRowColor");
                        if (alt != null && row % 2 != 0) {
                            c.setBackground(alt);
                        } else {
                            c.setBackground(table.getBackground());
                        }
                        ((javax.swing.JComponent) c).setOpaque(true);
                    }
                    return c;
                }
            };
            centerRenderer.setHorizontalAlignment(javax.swing.JLabel.CENTER);
            
            for (int indice : indices) {
                // Previne NullPointer e ArrayOutOfBounds, e não sobrescreve a coluna Status
                if (indice >= 0 && indice < tabela.getColumnCount() && !tabela.getColumnName(indice).equalsIgnoreCase("Status")) {
                    tabela.getColumnModel().getColumn(indice).setCellRenderer(centerRenderer);
                }
            }
        }
    }

    public static void centralizarTodasColunas(JTable tabela) {
        if (tabela != null) {
            int[] indices = new int[tabela.getColumnCount()];
            for (int i = 0; i < tabela.getColumnCount(); i++) {
                indices[i] = i;
            }
            centralizarColunas(tabela, indices);
        }
    }

    public static void alinharDireita(JTable tabela, int... indices) {
        if (tabela != null) {
            javax.swing.table.DefaultTableCellRenderer rightRenderer = new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (!isSelected && c instanceof javax.swing.JComponent) {
                        java.awt.Color alt = javax.swing.UIManager.getColor("Table.alternateRowColor");
                        if (alt != null && row % 2 != 0) {
                            c.setBackground(alt);
                        } else {
                            c.setBackground(table.getBackground());
                        }
                        ((javax.swing.JComponent) c).setOpaque(true);
                    }
                    return c;
                }
            };
            rightRenderer.setHorizontalAlignment(javax.swing.JLabel.RIGHT);
            
            for (int indice : indices) {
                if (indice >= 0 && indice < tabela.getColumnCount()) {
                    tabela.getColumnModel().getColumn(indice).setCellRenderer(rightRenderer);
                }
            }
        }
    }

    public static void alinharEsquerda(JTable tabela, int... indices) {
        if (tabela != null) {
            javax.swing.table.DefaultTableCellRenderer leftRenderer = new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (!isSelected && c instanceof javax.swing.JComponent) {
                        java.awt.Color alt = javax.swing.UIManager.getColor("Table.alternateRowColor");
                        if (alt != null && row % 2 != 0) {
                            c.setBackground(alt);
                        } else {
                            c.setBackground(table.getBackground());
                        }
                        ((javax.swing.JComponent) c).setOpaque(true);
                    }
                    return c;
                }
            };
            leftRenderer.setHorizontalAlignment(javax.swing.JLabel.LEFT);
            
            for (int indice : indices) {
                if (indice >= 0 && indice < tabela.getColumnCount()) {
                    tabela.getColumnModel().getColumn(indice).setCellRenderer(leftRenderer);
                }
            }
        }
    }

    public static class InativoRendererWrapper implements javax.swing.table.TableCellRenderer {
        private final javax.swing.table.TableCellRenderer original;

        public InativoRendererWrapper(javax.swing.table.TableCellRenderer original) {
            this.original = original;
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            java.awt.Component c;
            if (original != null) {
                c = original.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            } else {
                javax.swing.table.DefaultTableCellRenderer def = new javax.swing.table.DefaultTableCellRenderer();
                c = def.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }

            java.util.List<? extends br.edu.senai.fatesg.avcar.core.dtos.BaseDTO> currentList = 
                (java.util.List<? extends br.edu.senai.fatesg.avcar.core.dtos.BaseDTO>) table.getClientProperty("listaDados");

            int modelRow = table.convertRowIndexToModel(row);
            if (currentList != null && modelRow >= 0 && modelRow < currentList.size()) {
                if (!currentList.get(modelRow).isAtivo()) {
                    c.setForeground(new java.awt.Color(231, 76, 60)); // Vermelho
                    if (!isSelected) {
                        c.setBackground(new java.awt.Color(60, 25, 25)); // Fundo dark red sutil
                        if (c instanceof javax.swing.JComponent) {
                            ((javax.swing.JComponent) c).setOpaque(true);
                        }
                    }
                } else {
                    if (!isSelected) {
                        c.setForeground(table.getForeground());
                        if (c instanceof javax.swing.JComponent) {
                            java.awt.Color alt = javax.swing.UIManager.getColor("Table.alternateRowColor");
                            if (alt != null && row % 2 != 0) {
                                c.setBackground(alt);
                            } else {
                                c.setBackground(table.getBackground());
                            }
                            ((javax.swing.JComponent) c).setOpaque(true);
                        }
                    } else {
                        c.setForeground(table.getSelectionForeground());
                        c.setBackground(table.getSelectionBackground());
                        if (c instanceof javax.swing.JComponent) {
                            ((javax.swing.JComponent) c).setOpaque(true);
                        }
                    }
                }
            }
            return c;
        }
    }

    public static void aplicarCorVermelhaLinhasInativas(JTable tabela, java.util.List<? extends br.edu.senai.fatesg.avcar.core.dtos.BaseDTO> listaDados) {
        if (tabela == null || listaDados == null) return;
        
        tabela.putClientProperty("listaDados", listaDados);

        for (int i = 0; i < tabela.getColumnCount(); i++) {
            javax.swing.table.TableCellRenderer rendererAtual = tabela.getColumnModel().getColumn(i).getCellRenderer();
            if (rendererAtual == null) {
                rendererAtual = tabela.getDefaultRenderer(Object.class);
            }
            if (!(rendererAtual instanceof InativoRendererWrapper)) {
                tabela.getColumnModel().getColumn(i).setCellRenderer(new InativoRendererWrapper(rendererAtual));
            }
        }
    }

    public static void adicionarTooltipDataCompraPeca(JTable tabela, java.util.function.Supplier<java.util.List<br.edu.senai.fatesg.avcar.business.pecas.PecaDTO>> listaPecasSupplier) {
        if (tabela == null) return;
        tabela.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int row = tabela.rowAtPoint(e.getPoint());
                var lista = listaPecasSupplier.get();
                if (row >= 0 && lista != null && row < lista.size()) {
                    int modelRow = tabela.convertRowIndexToModel(row);
                    if (modelRow >= 0 && modelRow < lista.size()) {
                        br.edu.senai.fatesg.avcar.business.pecas.PecaDTO p = lista.get(modelRow);
                        if (p.getDataCompraPeca() != null) {
                            tabela.setToolTipText("Data da Compra: " + p.getDataCompraPeca().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                        } else {
                            tabela.setToolTipText("Data da Compra não informada");
                        }
                    }
                } else {
                    tabela.setToolTipText(null);
                }
            }
        });
    }
}
