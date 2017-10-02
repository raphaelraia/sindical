package br.com.rtools.relatorios.dao;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.utilitarios.Debugs;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioCupomDao extends DB {

    private Relatorios relatorios;
    private RelatorioOrdem relatorioOrdem;

    public RelatorioCupomDao() {
        this.relatorios = null;
        this.relatorioOrdem = null;
    }

    public RelatorioCupomDao(Relatorios relatorios, RelatorioOrdem relatorioOrdem) {
        this.relatorios = relatorios;
        this.relatorioOrdem = relatorioOrdem;
    }

    public List find(Integer cupom_id, String tipoDataEmissao, String dataEmissaoInicial, String dataEmissaoFinal, String idadeInicial, String idadeFinal, String sexo, String inOperador, String inParentesco, String inCidade) {
        // CHAMADOS 1192
        if (relatorios == null || relatorios.getId() == null) {
            return new ArrayList();
        }
        try {
            String queryString = "";
            queryString += " -- RelatorioCupomDao->find()                \n";
            queryString += ""
                    + "    SELECT C.ds_descricao AS cupom_descricao,    \n" // 0
                    + "           to_char(dt_data, 'DD/MM/YYYY') AS data_evento, \n" // 1
                    + "           CM.dt_emissao  AS cupom_emissao,      \n" // 2
                    + "           P.ds_nome 	 AS pessoa_nome,        \n" // 3
                    + "           F.ds_sexo 	 AS pessoa_sexo,        \n" // 4
                    + "           func_idade(f.dt_nascimento, current_date) AS pessoa_idade,\n" // 5
                    + "           S.parentesco   AS parentesco,         \n" // 6 
                    + "           U.ds_login 	 AS usuario_login,      \n" // 7
                    + "           PO.ds_nome 	 AS operador_nome       \n" // 8
                    + "      FROM eve_cupom AS C                        \n"
                    + "INNER JOIN eve_cupom_movimento   AS CM ON CM.id_cupom    = C.id           \n"
                    + "INNER JOIN pes_pessoa            AS P  ON P.id           = CM.id_pessoa   \n"
                    + "INNER JOIN pes_fisica            AS F  ON F.id_pessoa    = P.id           \n"
                    + "INNER JOIN seg_usuario           AS U  ON U.id           = CM.id_operador \n"
                    + "INNER JOIN pes_pessoa            AS PO ON PO.id          = U.id_pessoa    \n"
                    + " LEFT JOIN soc_socios_vw         AS S  ON S.codsocio     = P.id           \n"
                    + " LEFT JOIN pes_pessoa_vw         AS PVW ON PVW.codigo    = s.titular      \n"
                    + "-- ORDER BY dt_data, cm.dt_emissao\n"
                    + "-- ORDER BY dt_data, p.ds_nome\n"
                    + "-- ORDER BY p.ds_nome\n";
            List listWhere = new ArrayList();
            if (!tipoDataEmissao.equals("todos")) {
                if (dataEmissaoInicial != null && !dataEmissaoFinal.isEmpty()) {
                    switch (tipoDataEmissao) {
                        case "igual":
                            listWhere.add(" CM.dt_emissao = '" + dataEmissaoInicial + "'");
                            break;
                        case "apartir":
                            listWhere.add(" CM.dt_emissao >= '" + dataEmissaoInicial + "'");
                            break;
                        case "ate":
                            listWhere.add(" CM.dt_emissao <= '" + dataEmissaoInicial + "'");
                            break;
                        case "faixa":
                            if (!dataEmissaoFinal.isEmpty()) {
                                listWhere.add(" CM.dt_emissao BETWEEN '" + dataEmissaoInicial + "' AND '" + dataEmissaoFinal + "'");
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
            if ((idadeInicial != null && idadeFinal != null)) {
                if (!idadeInicial.isEmpty() && !idadeFinal.isEmpty()) {
                    listWhere.add(" func_idade(f.dt_nascimento, current_date) BETWEEN " + idadeInicial + " AND " + idadeFinal);
                } else if (!idadeInicial.isEmpty() && idadeFinal.isEmpty()) {
                    listWhere.add(" func_idade(f.dt_nascimento, current_date) = '" + idadeInicial + "'");
                }
            }
            if (cupom_id != null) {
                listWhere.add(" C.id = " + cupom_id + "");
            }
            if (sexo != null && !sexo.isEmpty()) {
                listWhere.add(" F.ds_sexo = '" + sexo + "'");
            }
            // OPERADOR
            if (inOperador != null && !inOperador.isEmpty()) {
                listWhere.add(" CM.id_operador IN (" + inOperador + ")");
            }
            // PARENTESCO
            if (inParentesco != null && !inParentesco.isEmpty()) {
                listWhere.add(" S.id_parentesco IN (" + inParentesco + ")");
            }
            // PARENTESCO
            if (inCidade != null && !inCidade.isEmpty()) {
                listWhere.add(" PVW.id_cidade IN (" + inCidade + ")");
            }
            for (int i = 0; i < listWhere.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listWhere.get(i).toString() + " \n";
                } else {
                    queryString += " AND " + listWhere.get(i).toString() + " \n";
                }
            }
            if (relatorioOrdem != null) {
                queryString += " ORDER BY " + relatorioOrdem.getQuery();
            } else {
                queryString += " ORDER BY p.ds_nome, CM.dt_emissao";
            }
            Debugs.put("habilitaDebugQuery", queryString);
            Query query = getEntityManager().createNativeQuery(queryString);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    public Relatorios getRelatorios() {
        return relatorios;
    }

    public void setRelatorios(Relatorios relatorios) {
        this.relatorios = relatorios;
    }

    public RelatorioOrdem getRelatorioOrdem() {
        return relatorioOrdem;
    }

    public void setRelatorioOrdem(RelatorioOrdem relatorioOrdem) {
        this.relatorioOrdem = relatorioOrdem;
    }

}
