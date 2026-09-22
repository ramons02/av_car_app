/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.senai.fatesg.avcar.swing.views.utils;

/**
 *
 * @author lucio-aguiar
 */
public class FormatadorUtil {

    public static String extrairNumeros(javax.swing.JFormattedTextField campo) {
        String texto = campo.getText();
        return (texto != null) ? texto.replaceAll("[^0-9]", "") : "";
    }
    
    public static String formatarCpfCnpj(String doc) {
        if (doc == null) return "";
        String numeros = doc.replaceAll("[^0-9]", "");
        if (numeros.length() == 11) {
            return numeros.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        } else if (numeros.length() == 14) {
            return numeros.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        }
        return doc; // Retorna sem máscara se for inválido
    }

    public static String formatarTelefone(String tel) {
        if (tel == null) return "";
        String numeros = tel.replaceAll("[^0-9]", "");
        
        // Se veio com o DDI do Brasil (55) salvo no banco
        if (numeros.length() == 13 && numeros.startsWith("55")) { // 55 + 11 dígitos
            return numeros.replaceFirst("(\\d{2})(\\d{2})(\\d{5})(\\d{4})", "+$1 ($2) $3-$4");
        } else if (numeros.length() == 12 && numeros.startsWith("55")) { // 55 + 10 dígitos
            return numeros.replaceFirst("(\\d{2})(\\d{2})(\\d{4})(\\d{4})", "+$1 ($2) $3-$4");
        } else if (numeros.length() == 11) {
            return numeros.replaceFirst("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        } else if (numeros.length() == 10) {
            return numeros.replaceFirst("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        }
        
        return tel; // Se tiver um tamanho esquisito, não quebra, só retorna original
    }

    public static String formatarPlaca(String placa) {
        if (placa == null) return "";
        placa = placa.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        if (placa.length() == 7) {
            // Verifica se é padrão antigo (3 letras seguidas de 4 números)
            if (placa.matches("[A-Z]{3}[0-9]{4}")) {
                return placa.substring(0, 3) + "-" + placa.substring(3);
            }
            // Verifica se é padrão Mercosul (3 letras, 1 numero, 1 letra, 2 numeros)
            if (placa.matches("[A-Z]{3}[0-9][A-Z][0-9]{2}")) {
                return placa; // Oficialmente sem hífen
            }
            // Se for outra coisa de 7 letras, bota hífen por garantia
            return placa.substring(0, 3) + "-" + placa.substring(3);
        }
        return placa.toUpperCase();
    }

    public static String formatarQuilometragem(int km) {
        return java.text.NumberFormat.getNumberInstance(new java.util.Locale("pt", "BR")).format(km) + " km";
    }

    public static String formatarMinutos(String minutos) {
        if (minutos == null || minutos.trim().isEmpty()) return "";
        if (minutos.toLowerCase().contains("min")) return minutos;
        return minutos + " min";
    }

    public static String formatarDias(int dias) {
        return dias + (dias == 1 ? " dia" : " dias");
    }

    /**
     * Limita a quantidade de caracteres de um JTextField e força tudo para MAIÚSCULO em tempo real.
     * Ideal para Placa (limite 7 ou 8) e Chassi (limite 17).
     */
    public static void setCaixaAlta(javax.swing.JTextField campo, int limite) {
        if (campo.getDocument() instanceof javax.swing.text.AbstractDocument doc) {
            doc.setDocumentFilter(new javax.swing.text.DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                    if (string == null) return;
                    if ((fb.getDocument().getLength() + string.length()) <= limite) {
                        super.insertString(fb, offset, string.toUpperCase(), attr);
                    }
                }
                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                    if (text == null) return;
                    if ((fb.getDocument().getLength() + text.length() - length) <= limite) {
                        super.replace(fb, offset, length, text.toUpperCase(), attrs);
                    }
                }
            });
        }
    }

    public static String formatarMoeda(double valor) {
        java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("pt", "BR"));
        return format.format(valor);
    }

    /**
     * Limita o campo para aceitar APENAS NÚMEROS. Ideal para campos de Dias, Quantidades, etc.
     */
    public static void setApenasNumeros(javax.swing.JTextField campo, int limiteMaximo) {
        if (campo.getDocument() instanceof javax.swing.text.AbstractDocument doc) {
            doc.setDocumentFilter(new javax.swing.text.DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                    if (string == null) return;
                    String apenasNumeros = string.replaceAll("[^0-9]", "");
                    if ((fb.getDocument().getLength() + apenasNumeros.length()) <= limiteMaximo) {
                        super.insertString(fb, offset, apenasNumeros, attr);
                    }
                }
                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                    if (text == null) return;
                    String apenasNumeros = text.replaceAll("[^0-9]", "");
                    if ((fb.getDocument().getLength() + apenasNumeros.length() - length) <= limiteMaximo) {
                        super.replace(fb, offset, length, apenasNumeros, attrs);
                    }
                }
            });
        }
    }

    /**
     * Limita o campo para aceitar números, vírgulas e pontos (ideal para Valor/Moeda).
     */
    public static void setApenasMoeda(javax.swing.JTextField campo) {
        if (campo.getDocument() instanceof javax.swing.text.AbstractDocument doc) {
            doc.setDocumentFilter(new javax.swing.text.DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                    if (string == null) return;
                    String apenasMoeda = string.replaceAll("[^0-9.,]", "");
                    super.insertString(fb, offset, apenasMoeda, attr);
                }
                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                    if (text == null) return;
                    String apenasMoeda = text.replaceAll("[^0-9.,]", "");
                    super.replace(fb, offset, length, apenasMoeda, attrs);
                }
            });
        }
    }
    public static String formatarDataHora(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "Não informada";
        return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public static String formatarData(java.time.LocalDate date) {
        if (date == null) return "Não informada";
        return date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
