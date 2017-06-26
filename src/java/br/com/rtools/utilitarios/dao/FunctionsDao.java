package br.com.rtools.utilitarios.dao;

import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.financeiro.ConfiguracaoFinanceiro;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Moeda;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;

public class FunctionsDao extends DB {

    /**
     * Trazer o responsável
     *
     * @param idPessoa
     * @param decontoFolha
     * @return
     */
    public int responsavel(int idPessoa, boolean decontoFolha) {
        Integer idResponsavel = -1;
        try {
            String queryString = " SELECT func_responsavel(" + idPessoa + ", " + decontoFolha + ") ";
            Query query = getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                idResponsavel = Integer.parseInt(((List) query.getSingleResult()).get(0).toString());
                if (idResponsavel == 0) {
                    idResponsavel = -1;
                }
            }
        } catch (Exception e) {
            return -1;
        }
        return idResponsavel;
    }

    /**
     *
     * @param idPessoa
     * @param idServico
     * @param date
     * @param tipo (0 -> Valor (já calculado) - ), (1 -> Valor até o vencimento
     * (já calculado)), (2 -> Taxa até o vencimento (já calculado))
     * @param id_categoria
     * @return double valor
     */
    public double valorServico(int idPessoa, int idServico, Date date, int tipo, Integer id_categoria) {
        String dataString = DataHoje.converteData(date);
        String queryString = "SELECT func_valor_servico(" + idPessoa + ", " + idServico + ", '" + dataString + "', " + tipo + ", " + id_categoria + ") ";
        try {
            Query qry = getEntityManager().createNativeQuery(queryString);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                list = (List) qry.getSingleResult();
                double valor = Double.parseDouble(list.get(0).toString());
                return valor;
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    public double valorServicoCheio(int idPessoa, int idServico, Date date) {
        String dataString = DataHoje.converteData(date);
        String queryString = "SELECT func_valor_servico_cheio(" + idPessoa + ", " + idServico + ", '" + dataString + "') ";
        try {
            Query qry = getEntityManager().createNativeQuery(queryString);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                list = (List) qry.getSingleResult();
                double valor = Double.parseDouble(list.get(0).toString());
                return valor;
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    public double valorServicoCheio(Integer servico_id, Date date) {
        String dataString = DataHoje.converteData(date);
        String queryString = "SELECT func_valor_servico_cheio(" + 1 + ", " + servico_id + ", '" + dataString + "') ";
        try {
            Query qry = getEntityManager().createNativeQuery(queryString);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                list = (List) qry.getSingleResult();
                double valor = Double.parseDouble(list.get(0).toString());
                return valor;
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    public double multaDiariaLocadora(Integer filial_id, Date dtDevolucao) {
        String devolucao = DataHoje.converteData(dtDevolucao);
        String queryString = "SELECT func_multa_diaria_locadora(" + filial_id + ", '" + devolucao + "') ";
        try {
            Query qry = getEntityManager().createNativeQuery(queryString);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                list = (List) qry.getSingleResult();
                double valor = Double.parseDouble(list.get(0).toString());
                return valor;
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    /**
     * Retorna a idade da pessoa
     *
     * @param campoData --> Nome do campo
     * @param dataString --> Default current_date
     * @param idPessoa
     * @return
     */
    public int idade(String campoData, String dataString, int idPessoa) {
        int idade = 0;
        try {
            Query query = getEntityManager().createNativeQuery("SELECT func_idade(" + campoData + ", " + dataString + ") FROM pes_fisica WHERE id_pessoa = " + idPessoa);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                idade = Integer.parseInt(((List) query.getSingleResult()).get(0).toString());
            }
        } catch (Exception e) {
            idade = 0;
        }
        return idade;
    }

    /**
     * Retorna operações e linhas de comando passados via SQL
     *
     * @param script --> Nome da linha de comando
     * @return
     */
    public String scriptSimples(String script) {
        String retorno = "";
        try {
            Query query = getEntityManager().createNativeQuery("SELECT " + script);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                retorno = ((List) query.getSingleResult()).get(0).toString();
            }
        } catch (Exception e) {
            retorno = "";
        }
        return retorno;
    }

    /**
     * Retorna quantidade de vagas disponíveis para cadastro de turma
     *
     * @param turma ID da turma
     * @return int
     */
    public int vagasEscolaTurma(int turma) {
        int vagas = 0;
        try {
            Query query = getEntityManager().createNativeQuery("SELECT func_esc_turmas_vagas_disponiveis(" + turma + ");");
            List list = query.getResultList();
            if (!list.isEmpty()) {
                vagas = Integer.parseInt(((List) query.getSingleResult()).get(0).toString());
            }
        } catch (Exception e) {
            vagas = 0;
        }
        return vagas;
    }

    public boolean demissionaSocios(int id_grupo_cidade, int nr_quantidade_dias) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "SELECT func_demissiona_socios(" + id_grupo_cidade + ", " + nr_quantidade_dias + ");");
            List list = query.getResultList();
            boolean xbo;
            if (!list.isEmpty()) {
                xbo = (Boolean) ((List) query.getSingleResult()).get(0);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean incluiPessoaComplemento() {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "SELECT func_inclui_pessoa_complemento();"
            );
            List list = query.getResultList();
            boolean xbo;
            if (!list.isEmpty()) {
                xbo = (Boolean) ((List) query.getSingleResult()).get(0);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Pessoa titularDaPessoa(int id_pessoa) {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT func_titular_da_pessoa(" + id_pessoa + ");");
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return (Pessoa) new Dao().find(new Pessoa(), Integer.parseInt(((Vector) list.get(0)).get(0).toString()));
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Verificar se a pessoa esta inapinplente
     *
     * @param id_pessoa
     * @return
     */
    public Boolean inadimplente(Integer id_pessoa) {
        return inadimplente(id_pessoa, null);
    }

    /**
     * Verificar se a pessoa esta inapinplente
     *
     * @param id_pessoa
     * @param nr_carencia_dias
     * @return
     */
    public Boolean inadimplente(Integer id_pessoa, Integer nr_carencia_dias) {
        if (id_pessoa == -1) {
            return false;
        }
        ConfiguracaoFinanceiro cf = (ConfiguracaoFinanceiro) new Dao().find(new ConfiguracaoFinanceiro(), 1);
        if (cf == null) {
            return true;
        }
        if (nr_carencia_dias == null) {
            SociosDao sociosDao = new SociosDao();
            Socios socios = sociosDao.pesquisaSocioPorPessoaAtivo(id_pessoa);
            if (socios.getId() == -1) {
                nr_carencia_dias = cf.getCarenciaDias();
            } else {
                nr_carencia_dias = socios.getMatriculaSocios().getCategoria().getNrCarenciaBalcao();
            }
        }
        try {
            Query query = getEntityManager().createNativeQuery("SELECT func_inadimplente(" + id_pessoa + ", " + nr_carencia_dias + ")");
            query.setMaxResults(1);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                Boolean bool = Boolean.parseBoolean(((List) list.get(0)).get(0).toString());
                return bool;
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    /**
     * Gerar mensalidades
     *
     * @param id_pessoa
     * @param referencia
     */
    public void gerarMensalidades(Integer id_pessoa, String referencia) {
        gerarMensalidadesBoolean(id_pessoa, referencia);
    }

    /**
     * Gerar mensalidades boolean
     *
     * @param id_pessoa
     * @param referencia
     * @return
     */
    public Boolean gerarMensalidadesBoolean(Integer id_pessoa, String referencia) {
        try {
            Query query = getEntityManager().createNativeQuery("SELECT func_geramensalidades(" + id_pessoa + ", '" + referencia + "')");
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public Boolean gerarBoletoSocial(List<Movimento> lista_movimento, String vencimento) {
        String ids = "";
        for (Movimento movimento : lista_movimento) {
            if (ids.isEmpty()) {
                ids = "" + movimento.getId();
            } else {
                ids += ", " + movimento.getId();
            }
        }

        try {
            Query query = getEntityManager().createNativeQuery("select func_gerar_boleto_ass('{" + ids + "}','" + vencimento + "');");
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public Boolean homologacaoPrazo(Boolean trabalhado, Integer id_cidade, String data_demissao, Integer convencao_id) {
        try {
            Query query = getEntityManager().createNativeQuery(
                    "SELECT func_homologacao_prazo(" + trabalhado + ", " + id_cidade + ", '" + data_demissao + "', " + convencao_id + ");"
            );
            List list = query.getResultList();

            if (!list.isEmpty()) {
                return (Boolean) ((List) query.getSingleResult()).get(0);
            }
        } catch (Exception e) {
            e.getMessage();
            return true;
        }
        return true;
    }

    /**
     * Trazer o responsável
     *
     * @param pessoa_id
     * @return
     */
    public Integer quantidadeMesesDebitoArr(Integer pessoa_id) {
        Integer qtde = 0;
        try {
            String queryString = " SELECT func_inadimplente_meses_arr( " + pessoa_id + ") ";
            Query query = getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                qtde = Integer.parseInt(((List) query.getSingleResult()).get(0).toString());
            }
        } catch (NumberFormatException e) {
            return -1;
        }
        return qtde;
    }

    /**
     * Trazer o responsável
     *
     * @param pessoa_id
     * @param tipo_servico_id
     * @param referencia
     * @param valor
     * @param qtde_empregados
     * @return
     */
    public Double arrCalculaValorBoleto(Integer pessoa_id, Integer tipo_servico_id, String referencia, String valor, Integer qtde_empregados) {
        Double v = new Double(0);
        if (pessoa_id != -1 && tipo_servico_id != -1 && !referencia.isEmpty() && Moeda.converteUS$(valor) > 0) {
            try {
                String queryString = " SELECT func_arr_calcula_valor_boleto(" + pessoa_id + "," + tipo_servico_id + ",'" + referencia + "'," + valor + ", " + qtde_empregados + ") ";
                Query query = getEntityManager().createNativeQuery(queryString);
                List list = query.getResultList();
                if (!list.isEmpty()) {
                    v = Double.parseDouble(((List) query.getSingleResult()).get(0).toString());
                }
            } catch (Exception e) {
                return new Double(0);
            }

        }
        return v;
    }

    /**
     * Valor da Folha Empresa
     *
     * @param pessoa_id
     * @param tipo_servico_id
     * @param referencia
     * @param valor
     * @return
     */
    public Double arrCalculaValorFolha(Integer pessoa_id, Integer tipo_servico_id, String referencia, String valor) {
        Double v = new Double(0);
        if (pessoa_id != -1 && tipo_servico_id != -1 && !referencia.isEmpty() && Moeda.converteUS$(valor) > 0) {
            try {
                String queryString = " SELECT func_arr_calcula_valor_folha(" + pessoa_id + "," + tipo_servico_id + ",'" + referencia + "'," + valor + ") ";
                Query query = getEntityManager().createNativeQuery(queryString);
                List list = query.getResultList();
                if (!list.isEmpty()) {
                    v = Double.parseDouble(((List) query.getSingleResult()).get(0).toString());
                }
            } catch (Exception e) {
                return new Double(0);
            }
        }
        return v;
    }
}
