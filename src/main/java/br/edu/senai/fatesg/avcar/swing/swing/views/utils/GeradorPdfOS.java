package br.edu.senai.fatesg.avcar.swing.views.utils;

import br.edu.senai.fatesg.avcar.business.ordemservico.OrdemServicoDTO;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Element;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileOutputStream;
import java.awt.Desktop;
import java.awt.Color;

public class GeradorPdfOS {

    private static String garantirDiretorio(String tipo) {
        String userHome = System.getProperty("user.home");
        String sep = java.io.File.separator;
        java.io.File diretorioBase = new java.io.File(userHome + sep + "Documentos_AVCAR" + sep + "OS_" + tipo);
        if (!diretorioBase.exists()) {
            diretorioBase.mkdirs();
        }
        return diretorioBase.getAbsolutePath() + sep;
    }
    
    private static PdfPCell criarCelulaCabecalho(String texto, Font fonte, int align) {
        PdfPCell cell = new PdfPCell(new Paragraph(texto, fonte));
        cell.setBackgroundColor(new Color(50, 53, 55));
        cell.setHorizontalAlignment(align);
        cell.setPadding(8f);
        cell.setBorder(0);
        return cell;
    }

    private static PdfPCell criarCelulaZebrada(String texto, Font fonte, boolean zebrado, int align) {
        PdfPCell cell = new PdfPCell(new Paragraph(texto, fonte));
        if (zebrado) cell.setBackgroundColor(new Color(245, 245, 245));
        cell.setHorizontalAlignment(align);
        cell.setPadding(6f);
        cell.setBorder(0);
        cell.setBorderWidthBottom(0.5f);
        cell.setBorderColorBottom(new Color(220, 220, 220));
        return cell;
    }

    public static void gerarPdfOrcamento(OrdemServicoDTO os, 
                                         java.util.List<br.edu.senai.fatesg.avcar.business.servicos.ItemServicoDTO> servicos, 
                                         java.util.List<br.edu.senai.fatesg.avcar.business.pecas.ItemPecaDTO> pecas,
                                         java.util.List<br.edu.senai.fatesg.avcar.business.servicos.ServicoExternoDTO> servicosExternos) {
        try {
            String dir = garantirDiretorio("Orcamentos");
            String fileName = dir + "Orcamento_OS_" + os.getNumeroOs() + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font tableHeadFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);

            Paragraph header = new Paragraph("AV CAR AUTO SERVICE", titleFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Paragraph titulo = new Paragraph("ORÇAMENTO - ORDEM DE SERVIÇO #" + os.getNumeroOs(), subtitleFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            document.add(new Paragraph("Veículo/Placa: " + (os.getVeiculo() != null ? os.getVeiculo() : "Não informado") + " | KM Atual: _______", normalFont));
            document.add(new Paragraph("Aberto/Responsável pela OS: " + (os.getColaboradorNome() != null ? os.getColaboradorNome() : "Não selecionado"), normalFont));
            document.add(new Paragraph("Data de Abertura: " + FormatadorUtil.formatarDataHora(os.getDataAbertura()), normalFont));
            document.add(new Paragraph("Defeito Relatado: " + (os.getDefeitoRelatado() != null ? os.getDefeitoRelatado() : "N/A"), normalFont));
            if (os.getFormaPagamento() != null && !os.getFormaPagamento().isEmpty()) {
                document.add(new Paragraph("Forma de Pagamento (Previsão): " + os.getFormaPagamento(), normalFont));
            }
            
            document.add(new Paragraph(" "));
            
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 4f, 2f, 2f});
            table.setSpacingBefore(10f);
            table.setSpacingAfter(15f);
            table.addCell(criarCelulaCabecalho("Tipo", tableHeadFont, Element.ALIGN_LEFT));
            table.addCell(criarCelulaCabecalho("Descrição", tableHeadFont, Element.ALIGN_LEFT));
            table.addCell(criarCelulaCabecalho("Qtd / Valor", tableHeadFont, Element.ALIGN_CENTER));
            table.addCell(criarCelulaCabecalho("Subtotal", tableHeadFont, Element.ALIGN_CENTER));

            double calcMaoObra = 0.0;
            double calcPecas = 0.0;
            double calcServicoExterno = 0.0;
            boolean zebrado = false;

            if (servicos != null) {
                for (var s : servicos) {
                    calcMaoObra += s.getSubtotal();
                    table.addCell(criarCelulaZebrada("Mão de Obra", normalFont, zebrado, Element.ALIGN_LEFT));
                    table.addCell(criarCelulaZebrada(s.getServicoNome(), normalFont, zebrado, Element.ALIGN_LEFT));
                    table.addCell(criarCelulaZebrada(s.getQuantidade() + " x R$ " + String.format("%.2f", s.getValorUnitario()), normalFont, zebrado, Element.ALIGN_RIGHT));
                    table.addCell(criarCelulaZebrada("R$ " + String.format("%.2f", s.getSubtotal()), boldFont, zebrado, Element.ALIGN_RIGHT));
                    zebrado = !zebrado;
                }
            }
            if (pecas != null) {
                for (var p : pecas) {
                    calcPecas += p.getSubtotal();
                    table.addCell(criarCelulaZebrada("Peça", normalFont, zebrado, Element.ALIGN_LEFT));
                    table.addCell(criarCelulaZebrada(p.getPecaNome(), normalFont, zebrado, Element.ALIGN_LEFT));
                    table.addCell(criarCelulaZebrada(p.getQuantidade() + " x R$ " + String.format("%.2f", p.getValorUnitario()), normalFont, zebrado, Element.ALIGN_RIGHT));
                    table.addCell(criarCelulaZebrada("R$ " + String.format("%.2f", p.getSubtotal()), boldFont, zebrado, Element.ALIGN_RIGHT));
                    zebrado = !zebrado;
                }
            }
            if (servicosExternos != null) {
                for (var se : servicosExternos) {
                    calcServicoExterno += se.getValor();
                    table.addCell(criarCelulaZebrada("Terc./Externo", normalFont, zebrado, Element.ALIGN_LEFT));
                    table.addCell(criarCelulaZebrada(se.getDescricao() != null ? se.getDescricao() : se.getParceiroNome(), normalFont, zebrado, Element.ALIGN_LEFT));
                    table.addCell(criarCelulaZebrada("1 x R$ " + String.format("%.2f", se.getValor()), normalFont, zebrado, Element.ALIGN_RIGHT));
                    table.addCell(criarCelulaZebrada("R$ " + String.format("%.2f", se.getValor()), boldFont, zebrado, Element.ALIGN_RIGHT));
                    zebrado = !zebrado;
                }
            }
            document.add(table);

            PdfPTable tableTotal = new PdfPTable(2);
            tableTotal.setWidthPercentage(100);
            tableTotal.setWidths(new float[]{7f, 3f});
            
            tableTotal.addCell(criarCelulaZebrada("Subtotal Mão de Obra:", normalFont, false, Element.ALIGN_RIGHT));
            tableTotal.addCell(criarCelulaZebrada(String.format("R$ %.2f", calcMaoObra), normalFont, false, Element.ALIGN_RIGHT));
            
            tableTotal.addCell(criarCelulaZebrada("Subtotal Peças:", normalFont, false, Element.ALIGN_RIGHT));
            tableTotal.addCell(criarCelulaZebrada(String.format("R$ %.2f", calcPecas), normalFont, false, Element.ALIGN_RIGHT));
            
            if (calcServicoExterno > 0) {
                tableTotal.addCell(criarCelulaZebrada("Subtotal Terceiros:", normalFont, false, Element.ALIGN_RIGHT));
                tableTotal.addCell(criarCelulaZebrada(String.format("R$ %.2f", calcServicoExterno), normalFont, false, Element.ALIGN_RIGHT));
            }
            
            double totalGeral = calcMaoObra + calcPecas + calcServicoExterno;
            
            if (os.getValorDesconto() > 0) {
                tableTotal.addCell(criarCelulaZebrada("Desconto Aplicado:", boldFont, false, Element.ALIGN_RIGHT));
                tableTotal.addCell(criarCelulaZebrada(String.format("- R$ %.2f", os.getValorDesconto()), boldFont, false, Element.ALIGN_RIGHT));
                totalGeral -= os.getValorDesconto();
            }

            PdfPCell cTotalLabel = criarCelulaZebrada("VALOR TOTAL:", totalFont, true, Element.ALIGN_RIGHT);
            cTotalLabel.setBorderWidthBottom(0);
            PdfPCell cTotalValue = criarCelulaZebrada(String.format("R$ %.2f", totalGeral), totalFont, true, Element.ALIGN_RIGHT);
            cTotalValue.setBorderWidthBottom(0);
            tableTotal.addCell(cTotalLabel);
            tableTotal.addCell(cTotalValue);
            
            document.add(tableTotal);

            document.add(new Paragraph(" \n"));
            Paragraph alerta = new Paragraph("Atenção: Orçamento válido por 15 dias. Após este prazo, os valores de peças estão sujeitos a reajustes conforme tabela dos fornecedores.", normalFont);
            alerta.setAlignment(Element.ALIGN_CENTER);
            document.add(alerta);

            document.add(new Paragraph(" \n\n"));
            Paragraph assinatura = new Paragraph("_____________________________________________________\nAssinatura do Cliente / De Acordo", normalFont);
            assinatura.setAlignment(Element.ALIGN_CENTER);
            document.add(assinatura);

            document.close();
            abrirPdf(fileName);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger("ErrorLog").log(java.util.logging.Level.SEVERE, "Erro capturado", e);
            JOptionPane.showMessageDialog(null, "Erro ao gerar PDF de Orçamento: " + e.getMessage());
        }
    }

    public static void gerarPdfEntrega(OrdemServicoDTO os, 
                                       java.util.List<br.edu.senai.fatesg.avcar.business.servicos.ItemServicoDTO> servicos, 
                                       java.util.List<br.edu.senai.fatesg.avcar.business.pecas.ItemPecaDTO> pecas,
                                       java.util.List<br.edu.senai.fatesg.avcar.business.servicos.ServicoExternoDTO> servicosExternos,
                                       java.util.List<br.edu.senai.fatesg.avcar.business.ordemservico.GarantiaDTO> garantias) {
        try {
            String dir = garantirDiretorio("Entregas");
            String fileName = dir + "Entrega_OS_" + os.getNumeroOs() + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font tableHeadFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);

            Paragraph header = new Paragraph("AV CAR AUTO SERVICE", titleFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Paragraph titulo = new Paragraph("TERMO DE ENTREGA - OS FINALIZADA #" + os.getNumeroOs(), subtitleFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            document.add(new Paragraph("Veículo/Placa: " + (os.getVeiculo() != null ? os.getVeiculo() : "Não informado") + " | KM Atual: _______", normalFont));
            document.add(new Paragraph("Aberto/Responsável pela OS: " + (os.getColaboradorNome() != null ? os.getColaboradorNome() : "Não selecionado"), normalFont));
            document.add(new Paragraph("Data de Abertura: " + FormatadorUtil.formatarDataHora(os.getDataAbertura()) + "   |   Data Fechamento: " + (os.getDataFinalizacao() != null ? FormatadorUtil.formatarDataHora(os.getDataFinalizacao()) : ""), normalFont));
            if (os.getFormaPagamento() != null && !os.getFormaPagamento().isEmpty()) {
                document.add(new Paragraph("Forma de Pagamento Utilizada: " + os.getFormaPagamento(), normalFont));
            }
            
            document.add(new Paragraph(" "));
            
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 4f, 2f, 2f});
            table.setSpacingBefore(10f);
            table.setSpacingAfter(15f);
            table.addCell(criarCelulaCabecalho("Tipo", tableHeadFont, Element.ALIGN_LEFT));
            table.addCell(criarCelulaCabecalho("Descrição", tableHeadFont, Element.ALIGN_LEFT));
            table.addCell(criarCelulaCabecalho("Qtd / Valor", tableHeadFont, Element.ALIGN_CENTER));
            table.addCell(criarCelulaCabecalho("Subtotal", tableHeadFont, Element.ALIGN_CENTER));

            double calcMaoObra = 0.0;
            double calcPecas = 0.0;
            double calcServicoExterno = 0.0;
            boolean zebrado = false;

            if (servicos != null) {
                for (var s : servicos) {
                    calcMaoObra += s.getSubtotal();
                    table.addCell(criarCelulaZebrada("Mão de Obra", normalFont, zebrado, Element.ALIGN_LEFT));
                    table.addCell(criarCelulaZebrada(s.getServicoNome(), normalFont, zebrado, Element.ALIGN_LEFT));
                    table.addCell(criarCelulaZebrada(s.getQuantidade() + " x R$ " + String.format("%.2f", s.getValorUnitario()), normalFont, zebrado, Element.ALIGN_RIGHT));
                    table.addCell(criarCelulaZebrada("R$ " + String.format("%.2f", s.getSubtotal()), boldFont, zebrado, Element.ALIGN_RIGHT));
                    zebrado = !zebrado;
                }
            }
            if (pecas != null) {
                for (var p : pecas) {
                    calcPecas += p.getSubtotal();
                    table.addCell(criarCelulaZebrada("Peça", normalFont, zebrado, Element.ALIGN_LEFT));
                    table.addCell(criarCelulaZebrada(p.getPecaNome(), normalFont, zebrado, Element.ALIGN_LEFT));
                    table.addCell(criarCelulaZebrada(p.getQuantidade() + " x R$ " + String.format("%.2f", p.getValorUnitario()), normalFont, zebrado, Element.ALIGN_RIGHT));
                    table.addCell(criarCelulaZebrada("R$ " + String.format("%.2f", p.getSubtotal()), boldFont, zebrado, Element.ALIGN_RIGHT));
                    zebrado = !zebrado;
                }
            }
            if (servicosExternos != null) {
                for (var se : servicosExternos) {
                    calcServicoExterno += se.getValor();
                    table.addCell(criarCelulaZebrada("Terc./Externo", normalFont, zebrado, Element.ALIGN_LEFT));
                    table.addCell(criarCelulaZebrada(se.getDescricao() != null ? se.getDescricao() : se.getParceiroNome(), normalFont, zebrado, Element.ALIGN_LEFT));
                    table.addCell(criarCelulaZebrada("1 x R$ " + String.format("%.2f", se.getValor()), normalFont, zebrado, Element.ALIGN_RIGHT));
                    table.addCell(criarCelulaZebrada("R$ " + String.format("%.2f", se.getValor()), boldFont, zebrado, Element.ALIGN_RIGHT));
                    zebrado = !zebrado;
                }
            }
            document.add(table);

            PdfPTable tableTotal = new PdfPTable(2);
            tableTotal.setWidthPercentage(100);
            tableTotal.setWidths(new float[]{7f, 3f});
            
            tableTotal.addCell(criarCelulaZebrada("Subtotal Mão de Obra:", normalFont, false, Element.ALIGN_RIGHT));
            tableTotal.addCell(criarCelulaZebrada(String.format("R$ %.2f", calcMaoObra), normalFont, false, Element.ALIGN_RIGHT));
            tableTotal.addCell(criarCelulaZebrada("Subtotal Peças:", normalFont, false, Element.ALIGN_RIGHT));
            tableTotal.addCell(criarCelulaZebrada(String.format("R$ %.2f", calcPecas), normalFont, false, Element.ALIGN_RIGHT));
            
            if (calcServicoExterno > 0) {
                tableTotal.addCell(criarCelulaZebrada("Subtotal Terceiros:", normalFont, false, Element.ALIGN_RIGHT));
                tableTotal.addCell(criarCelulaZebrada(String.format("R$ %.2f", calcServicoExterno), normalFont, false, Element.ALIGN_RIGHT));
            }
            
            double totalGeral = calcMaoObra + calcPecas + calcServicoExterno;
            
            if (os.getValorDesconto() > 0) {
                tableTotal.addCell(criarCelulaZebrada("Desconto Aplicado:", boldFont, false, Element.ALIGN_RIGHT));
                tableTotal.addCell(criarCelulaZebrada(String.format("- R$ %.2f", os.getValorDesconto()), boldFont, false, Element.ALIGN_RIGHT));
                totalGeral -= os.getValorDesconto();
            }

            PdfPCell cTotalLabel = criarCelulaZebrada("VALOR FINAL A PAGAR:", totalFont, true, Element.ALIGN_RIGHT);
            cTotalLabel.setBorderWidthBottom(0);
            PdfPCell cTotalValue = criarCelulaZebrada(String.format("R$ %.2f", totalGeral), totalFont, true, Element.ALIGN_RIGHT);
            cTotalValue.setBorderWidthBottom(0);
            tableTotal.addCell(cTotalLabel);
            tableTotal.addCell(cTotalValue);
            document.add(tableTotal);

            document.add(new Paragraph(" \n"));
            
            if (garantias != null && !garantias.isEmpty()) {
                document.add(new Paragraph("Garantias dos Itens:", boldFont));
                PdfPTable garTable = new PdfPTable(3);
                garTable.setWidthPercentage(100);
                garTable.setSpacingBefore(5f);
                garTable.addCell(criarCelulaCabecalho("Item", tableHeadFont, Element.ALIGN_LEFT));
                garTable.addCell(criarCelulaCabecalho("Vencimento", tableHeadFont, Element.ALIGN_LEFT));
                garTable.addCell(criarCelulaCabecalho("Mecânico", tableHeadFont, Element.ALIGN_LEFT));
                
                boolean garZebra = false;
                for (var g : garantias) {
                    garTable.addCell(criarCelulaZebrada(g.getItem(), normalFont, garZebra, Element.ALIGN_LEFT));
                    garTable.addCell(criarCelulaZebrada(g.getDataVencimento() != null ? FormatadorUtil.formatarDataHora(g.getDataVencimento()) : "N/A", normalFont, garZebra, Element.ALIGN_LEFT));
                    garTable.addCell(criarCelulaZebrada(g.getColaboradorNome() != null ? g.getColaboradorNome() : "Geral", normalFont, garZebra, Element.ALIGN_LEFT));
                    garZebra = !garZebra;
                }
                document.add(garTable);
            } else {
                document.add(new Paragraph("Garantia da OS (Adicional): " + os.getGarantia() + " dias.", boldFont));
            }
            
            document.add(new Paragraph(" \n\n"));
            Paragraph cdc = new Paragraph("Declaro ter recebido o veículo com os serviços executados a contento, bem como os itens de uso e acessórios em perfeito estado. Ciente das garantias legais conforme art. 26 do Código de Defesa do Consumidor.", normalFont);
            cdc.setAlignment(Element.ALIGN_CENTER);
            document.add(cdc);

            document.add(new Paragraph(" \n\n"));
            Paragraph assinatura = new Paragraph("_____________________________________________________\nAssinatura do Cliente / De Acordo", normalFont);
            assinatura.setAlignment(Element.ALIGN_CENTER);
            document.add(assinatura);

            document.close();
            abrirPdf(fileName);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger("ErrorLog").log(java.util.logging.Level.SEVERE, "Erro capturado", e);
            JOptionPane.showMessageDialog(null, "Erro ao gerar PDF de Entrega: " + e.getMessage());
        }
    }
    
    private static void abrirPdf(String path) {
        try {
            File file = new File(path);
            if (file.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception e) {
            java.util.logging.Logger.getLogger("ErrorLog").log(java.util.logging.Level.SEVERE, "Erro capturado", e);
        }
    }
}
