package br.edu.senai.fatesg.avcar.swing.views.utils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FiltroUtil {

    /**
     * Filtra uma lista genérica no front-end baseada em um termo de busca.
     * Isso permite pesquisar itens (ativos ou inativos) sem precisar de rotas
     * personalizadas no back-end para cada caso.
     * 
     * @param <T> O tipo do DTO.
     * @param listaOriginal A lista base que contém todos os itens carregados.
     * @param termo O termo que o usuário digitou na barra de pesquisa.
     * @param extrator Expressão lambda para extrair o texto alvo (ex: ClienteDTO::getNome).
     * @return Uma lista filtrada apenas com os resultados que contêm o termo (case-insensitive).
     */
    public static <T> List<T> filtrarPorNome(List<T> listaOriginal, String termo, Function<T, String> extrator) {
        if (listaOriginal == null) return java.util.Collections.emptyList();
        if (termo == null || termo.trim().isEmpty()) return listaOriginal;
        
        String termoLower = termo.trim().toLowerCase();
        
        return listaOriginal.stream()
            .filter(item -> {
                String valor = extrator.apply(item);
                return valor != null && valor.toLowerCase().contains(termoLower);
            })
            .collect(Collectors.toList());
    }
}
