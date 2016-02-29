/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.utilitarios;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Sum {

    public static void main(String[] args) {

        final List<Object[]> lista = new ArrayList<>();
        lista.add(new Object[]{new BigDecimal("45"), new BigDecimal("100"), new BigDecimal("200")});
        lista.add(new Object[]{new BigDecimal("45"), new BigDecimal("200"), new BigDecimal("400")});
        lista.add(new Object[]{new BigDecimal("50"), new BigDecimal("30"), new BigDecimal("60")});

        final Map<BigDecimal, Object[]> total = calcularTotal(lista, 0, 1, 2);
        total.forEach((key, values) -> {
            System.out.print(key + ":");
            Arrays.stream(values).forEach(value -> System.out.print(" " + value));
            System.out.println();
        });

    }

    public static class Holder {

        /**
         * Segundo passo da redução.
         *
         * @return soma colunas selecionadas
         */
        public static Object[] combine(Object[] first, Object[] second) {
            return IntStream.range(0, first.length).mapToObj(i -> {
                final BigDecimal x = (BigDecimal) first[i];
                final BigDecimal y = (BigDecimal) second[i];
                return x.add(y);
            }).toArray();
        }

        final BigDecimal key;
        final Object[] selectedValues;

        /**
         * Passo do mapeamento.
         *
         * @param values entradas cruas
         * @param keyIndex indice da chave
         * @param valueIndexes indices selecionados
         */
        public Holder(Object[] values, int keyIndex, int... valueIndexes) {
            this.key = (BigDecimal) values[keyIndex];
            this.selectedValues = Arrays
                    .stream(valueIndexes)
                    .mapToObj(i -> values[i])
                    .toArray();
        }

        /**
         * Elemento neutro para a redução.
         *
         * @param size quantidade de colunas no elemento neutro
         */
        public Holder(int size) {
            this.key = null;
            this.selectedValues = new Object[size];
            Arrays.fill(selectedValues, BigDecimal.ZERO);
        }

        public Object[] getSelectedValues() {
            return selectedValues;
        }

        public BigDecimal getKey() {
            return key;
        }
    }

    public static Map<BigDecimal, Object[]> calcularTotal(List<Object[]> lista, Integer chave, int... valores) {
        final Map<BigDecimal, Object[]> results = lista
                .stream()
                .map(o -> new Holder(o, chave, valores))
                .collect(
                        Collectors.groupingBy(
                                Holder::getKey,
                                Collectors.reducing(
                                        new Holder(valores.length).getSelectedValues(),
                                        Holder::getSelectedValues,
                                        Holder::combine)));
        return results;
    }
}
