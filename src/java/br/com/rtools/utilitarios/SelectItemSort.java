/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.utilitarios;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.faces.model.SelectItem;

public class SelectItemSort {

    public static List<SelectItem> sort(List<SelectItem> list) {
        try {
            Collections.sort(list, (SelectItem sItem1, SelectItem sItem2) -> {
                String sItem1Label = sItem1.getLabel();
                String sItem2Label = sItem2.getLabel();
                return (sItem1Label.compareToIgnoreCase(sItem2Label));
            });
            return list;
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
