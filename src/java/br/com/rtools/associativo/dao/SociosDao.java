package br.com.rtools.associativo.dao;

import br.com.rtools.associativo.CategoriaDesconto;
import br.com.rtools.associativo.DescontoSocial;
import br.com.rtools.associativo.EventoServicoValor;
import br.com.rtools.associativo.SocioCarteirinha;
import br.com.rtools.associativo.Socios;
import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Moeda;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;
import oracle.toplink.essentials.exceptions.EJBQLException;

public class SociosDao extends DB {

    public List pesquisaTodos() {
        try {
            Query qry = getEntityManager().createQuery("select s from Socios s");
            return (qry.getResultList());
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public Socios pesquisaSocioPorId(int idServicoPessoa) {
        try {
            Query qry = getEntityManager().createQuery("SELECT S FROM Socios AS S WHERE S.servicoPessoa.id = :pid");
            qry.setParameter("pid", idServicoPessoa);
            return (Socios) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
            return new Socios();
        }
    }

    public List pesquisaSocios(String desc, String por, String como, String status) {
        List lista = new Vector<Object>();
        String textQuery = null;
        if (status.equals("inativo")) {
            status = "not";
        } else {
            status = "";
        }
        if (por.equals("nome")) {
            por = "nome";
            if (como.equals("P")) {
                desc = "%" + desc.toLowerCase().toUpperCase() + "%";
                textQuery = "select soc from Socios soc"
                        + " where UPPER(soc.servicoPessoa.pessoa.nome) like :desc "
                        + "   and soc.matriculaSocios.motivoInativacao is " + status + " null"
                        + " order by soc.servicoPessoa.pessoa.nome";
            } else if (como.equals("I")) {
                por = "nome";
                desc = desc.toLowerCase().toUpperCase() + "%";
                textQuery = "select soc from Socios soc"
                        + " where UPPER(soc.servicoPessoa.pessoa.nome) like :desc "
                        + "   and soc.matriculaSocios.motivoInativacao is " + status + " null"
                        + " order by soc.servicoPessoa.pessoa.nome";
            }
        }
        if (por.equals("documento")) {
            por = "documento";
            desc = desc.toLowerCase().toUpperCase() + "%";
            textQuery = "select soc from Socios soc"
                    + " where UPPER(soc.servicoPessoa.pessoa.documento) like :desc "
                    + "   and soc.matriculaSocios.motivoInativacao is " + status + " null"
                    + " order by soc.servicoPessoa.pessoa.nome";
        }
        try {
            Query qry = getEntityManager().createQuery(textQuery);
            if (!desc.equals("%%") && !desc.equals("%")) {
                qry.setParameter("desc", desc);
            }
            lista = qry.getResultList();
        } catch (Exception e) {
            lista = new Vector<Object>();
        }
        return lista;
    }

    public List<Socios> listaDependentes(int id_matricula) {
        try {
            Query qry = getEntityManager().createQuery("select s "
                    + "  from Socios s "
                    + " where s.parentesco.id <> 1 "
                    + "   and s.matriculaSocios.id = " + id_matricula
                    + "   and s.servicoPessoa.ativo = true"
                    + " order by s.servicoPessoa.pessoa.nome");
            return (qry.getResultList());
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList<Socios>();
        }
    }

    public List<Socios> listaDependentesInativos(int id_matricula) {
        try {
            Query qry = getEntityManager().createQuery("select s "
                    + "  from Socios s "
                    + " where s.parentesco.id <> 1 "
                    + "   and s.matriculaSocios.id = " + id_matricula
                    + "   and s.servicoPessoa.ativo = false"
                    + " order by s.servicoPessoa.pessoa.nome");
            return (qry.getResultList());
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList<Socios>();
        }
    }
//    public List pesquisaDependentes(int idPessoaSocio){
//        try{
//            Query qry = getEntityManager().createQuery("select s from Socios s " +
//                                                       " where s.parentesco.id <> 1 " +
//                                                       "   and s.matriculaSocios.id = ( " +
//                                                       "       select si.matriculaSocios.id from Socios si, ServicoPessoa sp " +
//                                                       "        where si.servicoPessoa.id = sp.id" +
//                                                       "          and si.parentesco.id = 1"+
//                                                       //"          and si.matriculaSocios.motivoInativacao is null " +
//                                                       "          and sp.pessoa.id = "+idPessoaSocio+" )");
//            return (qry.getResultList());
//        }catch(Exception e){
//            e.getMessage();
//            return null;
//        }
//    }

    public List pesquisaDependentesOrdenado(Integer matricula_id) {
        try {
            String queryString = ""
                    + "   SELECT S                                              \n"
                    + "     FROM Socios S                                       \n"
                    + "    WHERE S.parentesco.id <> 1                           \n"
                    + "      AND S.matriculaSocios.id = " + matricula_id + "    \n"
                    + "      AND S.servicoPessoa.ativo = true                   \n"
                    + " ORDER BY S.servicoPessoa.pessoa.nome ";

            Query qry = getEntityManager().createQuery(queryString);
            return (qry.getResultList());
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
    }

//    public List pesquisaDependentesOrdenado(int idPessoaSocio){
//        try{
//            Query qry = getEntityManager().createQuery("select s from Socios s " +
//                                                       " where s.parentesco.id <> 1 " +
//                                                       "   and s.matriculaSocios.id = ( " +
//                                                       "       select si.matriculaSocios.id from Socios si, ServicoPessoa sp " +
//                                                       "        where si.servicoPessoa.id = sp.id" +
//                                                       "          and si.parentesco.id = 1" +
//                                                       //"          and si.matriculaSocios.motivoInativacao is null " +
//                                                       "          and sp.pessoa.id = "+idPessoaSocio+" )" +
//                                                       " order by s.parentesco.id");
//            return (qry.getResultList());
//        }catch(Exception e){
//            e.getMessage();
//            return null;
//        }
//    }
    public Socios pesquisaSocioPorPessoa(int idPessoa) {
        Socios socios = new Socios();
        try {
            Query query = getEntityManager().createNativeQuery(
                    "  SELECT S.*                                                          \n"
                    + "  FROM soc_socios              AS S                                 \n"
                    + " INNER JOIN fin_servico_pessoa AS SP ON SP.id = S.id_servico_pessoa \n"
                    + " INNER JOIN pes_pessoa         AS P  ON P.id  = SP.id_pessoa        \n"
                    + "      WHERE SP.id_pessoa = " + idPessoa + "\n"
                    + "   ORDER BY SP.id", Socios.class);

            List<Socios> list = query.getResultList();

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getServicoPessoa().isAtivo()) {
                    return list.get(i);
                }
                socios = list.get(i);
            }

//            Query qry = getEntityManager().createQuery(""
//                    + " SELECT s "
//                    + "   FROM Socios s "
//                    + "  WHERE s.servicoPessoa.pessoa.id = :pid "
//                    + "  ORDER BY s.servicoPessoa.id DESC");
//            qry.setParameter("pid", idPessoa);
            //soc = (Socios) qry.setMaxResults(1).getSingleResult();
//            soc = (Socios) qry.getSingleResult();
        } catch (EJBQLException e) {
            e.getMessage();
        }
        return socios;
    }

    public Socios pesquisaSocioPorPessoaEMatriculaSocio(int idPessoa, int idMatriculaSocios) {
        Socios socio = new Socios();

        try {

            Query qry = getEntityManager().createNativeQuery(
                    "SELECT s.id "
                    + "  FROM soc_socios s "
                    + " INNER JOIN fin_servico_pessoa sp ON sp.id = s.id_servico_pessoa"
                    + " INNER JOIN pes_pessoa p ON p.id = sp.id_pessoa"
                    + " WHERE sp.id_pessoa = " + idPessoa
                    + "   AND s.id_matricula_socios = " + idMatriculaSocios
                    + " ORDER BY sp.id");

            List<Vector> lista = qry.getResultList();

            for (int i = 0; i < lista.size(); i++) {
                socio = (Socios) (new Dao()).find(new Socios(), (Integer) lista.get(i).get(0));
            }
        } catch (EJBQLException e) {
            e.getMessage();
        }
        return socio;
    }

    public Socios pesquisaSocioPorPessoaAtivoDocumento(String cpf) {
        Socios soc = new Socios();
        try {
            Query qry = getEntityManager().createQuery(
                    "  select s from Socios s"
                    + " where s.servicoPessoa.pessoa.documento = :pcpf"
                    + "   and s.servicoPessoa.ativo = true");
            qry.setParameter("pcpf", cpf);
            List list = qry.getResultList();
            if (!list.isEmpty() && list.size() == 1) {
                soc = (Socios) qry.getSingleResult();
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return soc;
    }

    public Socios pesquisaSocioPorPessoaAtivo(int idPessoa) {
        Socios soc = new Socios();
        try {
            Query qry = getEntityManager().createQuery(
                    "  select s from Socios s"
                    + " where s.servicoPessoa.pessoa.id = :pid"
                    + "   and s.servicoPessoa.ativo = true");
            qry.setParameter("pid", idPessoa);
            List list = qry.getResultList();
            if (!list.isEmpty() && list.size() == 1) {
                soc = (Socios) qry.getSingleResult();
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return soc;
    }

    public List<Socios> pesquisaSocioPorPessoaInativo(Integer pessoa_id) {
        return pesquisaSocioPorPessoaInativo(pessoa_id, false);
    }

    public List<Socios> pesquisaSocioPorPessoaInativo(Integer pessoa_id, Boolean titular) {
        try {
            String queryString = "        "
                    + "  SELECT S.*                                                     \n "
                    + "       FROM soc_socios AS S                                      \n "
                    + " INNER JOIN fin_servico_pessoa sp ON SP.id = S.id_servico_pessoa \n "
                    + "      WHERE SP.id_pessoa = " + pessoa_id + "                     \n "
                    + "        AND SP.is_ativo = false                                  \n ";
            if (titular != null && titular) {
                queryString += "  AND S.id_parentesco = 1  \n ";
            }
            queryString += " ORDER BY S.id_matricula_socios DESC ";
            if (titular != null && titular) {
                queryString += " LIMIT 1 ";
            }
            Query query = getEntityManager().createNativeQuery(queryString, Socios.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList<>();
    }

    public List<Socios> pesquisaSocioPorPessoaTitularInativo(int idPessoa) {
        try {
            Query query = getEntityManager().createQuery("SELECT S FROM Socios AS S WHERE S.servicoPessoa.pessoa.id = :pessoa AND S.servicoPessoa.ativo = false AND S.parentesco.id = 1 ORDER BY S.id DESC");
            query.setParameter("pessoa", idPessoa);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList<>();
    }

    public Socios pesquisaSocioTitularInativoPorPessoa(int idPessoa) {
        try {
            Query query = getEntityManager().createQuery("SELECT S FROM Socios AS S WHERE S.matriculaSocios.titular.id = :pessoa AND S.servicoPessoa.pessoa.id = :pessoaTitular AND S.servicoPessoa.ativo = false ORDER BY S.matriculaSocios.id DESC");
            query.setParameter("pessoa", idPessoa);
            query.setParameter("pessoaTitular", idPessoa);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return (Socios) list.get(0);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public List<Socios> listaSocioTitularInativoPorPessoa(int idPessoa) {
        try {
            Query query = getEntityManager().createQuery("SELECT S FROM Socios AS S WHERE S.servicoPessoa.pessoa.id = :pessoa AND S.servicoPessoa.ativo = false AND S.matriculaSocios.dtInativo IS NOT NULL ORDER BY S.matriculaSocios.dtInativo DESC");
            query.setParameter("pessoa", idPessoa);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    public List pesquisaSociosAtivos() {
        try {
            Query qry = getEntityManager().createQuery("select soc from Socios soc"
                    + " where soc.matriculaSocios.motivoInativacao is null");
            return (qry.getResultList());
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
    }

    public List pesquisaSociosInativos() {
        try {
            Query qry = getEntityManager().createQuery("select soc from Socios soc"
                    + " where soc.matriculaSocios.motivoInativacao is not null");
            return (qry.getResultList());
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
    }

    public Socios pesquisaSocioDoDependente(int idDependente) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select s "
                    + "  from Socios s "
                    + " where s.matriculaSocios.id = (select si.matriculaSocios.id "
                    + "                                 from Socios si "
                    + "                                where si.parentesco.id <> 1"
                    + "                                  and si.id = :pid )");
            qry.setParameter("pid", idDependente);
            return (Socios) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public Socios pesquisaSocioDoDependente(Pessoa pessoa) {
        try {
            Query qry = getEntityManager().createQuery("SELECT s FROM Socios s WHERE s.matriculaSocios.titular.id = :pid AND s.matriculaSocios.dtInativo IS NOT NULL");
//            Query qry = getEntityManager().createQuery(
//                    "select s "
//                    + "  from Socios s "
//                    + " where s.matriculaSocios.id = (select si.matriculaSocios.id "
//                    + "                                 from Socios si "
//                    + "                                where si.parentesco.id <> 1"
//                    + "                                  and si.servicoPessoa.pessoa.id = :pid )");
//            qry.setParameter("pid", pessoa.getId());
            return (Socios) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public double descontoSocioEve(int idPessoa, int idServico) {
        Query qry = null;
        DataHoje dh = new DataHoje();
        Socios socios = new Socios();
        CategoriaDesconto categoriaDesconto = new CategoriaDesconto();
        EventoServicoValor eveServicoValor = new EventoServicoValor();
        Fisica fisica = new Fisica();
        // PESQUISA PESSOA FISICA ------------------
        String textQry = "select f "
                + "  from Fisica f "
                + " where f.pessoa.id = " + idPessoa;
        try {
            qry = getEntityManager().createQuery(textQry);
            fisica = (Fisica) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
            return 0;
        }
        /// -----------

        // PESQUISA O SOCIO ---
        textQry = "select s "
                + "  from Socios s "
                + " where s.servicoPessoa.pessoa.id = " + idPessoa;
        try {
            qry = getEntityManager().createQuery(textQry);
            socios = (Socios) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
            return 0;
        }
        /// -----------

        // PESQUISA CATEGORIA DESCONTO -----
        textQry = "select cd "
                + "  from CategoriaDesconto cd"
                + " where cd.categoria.id = " + socios.getMatriculaSocios().getCategoria().getId()
                + "   and cd.servicoValor.servicos.id = " + idServico;
        try {
            qry = getEntityManager().createQuery(textQry);
            categoriaDesconto = (CategoriaDesconto) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
            return 0;
        }

        // PESQUISA EVE SERVICO VALOR -------------
        if (fisica.getNascimento().length() != 10) {
            return 0;
        }
        int idade = dh.calcularIdade(fisica.getDtNascimento());
        textQry = "select ev "
                + "  from EventoServicoValor ev "
                + " where ev.eventoServico.servicos.id = " + idServico
                + "   and ev.idadeInicial <= " + idade
                + "   and ev.idadeFinal >= " + idade;

        try {
            qry = getEntityManager().createQuery(textQry);
            eveServicoValor = (EventoServicoValor) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
            return 0;
        }

        // CALCULA VALOR COM DESCONTO ---
        if (categoriaDesconto.getDesconto() == 0) {
            return 0;
        }

        double soma = Moeda.multiplicar(eveServicoValor.getValor(), (Moeda.divisao(categoriaDesconto.getDesconto(), 100)));
        soma = Moeda.subtracao(eveServicoValor.getValor(), soma);
        return soma;
    }

    public List<SocioCarteirinha> pesquisaCarteirinhasPorPessoa(int id_pessoa, int id_modelo) {
        try {
            Query qry = getEntityManager().createQuery(
                    " SELECT sc "
                    + " FROM SocioCarteirinha sc "
                    + "WHERE sc.pessoa.id = " + id_pessoa
                    + "  AND sc.modeloCarteirinha.id = " + id_modelo);
            return (qry.getResultList());
        } catch (Exception e) {
            e.getMessage();
            return new ArrayList();
        }
    }

    public List pesquisaMotivoInativacao() {
        try {
            Query qry = getEntityManager().createQuery("select mi from SMotivoInativacao mi order by mi.descricao");
            return (qry.getResultList());
        } catch (Exception e) {
            //e.getMessage();
            return new ArrayList();
        }
    }

    public boolean socioDebito(int idPessoa) {
        try {
            Query query = getEntityManager().createNativeQuery(""
                    + "     SELECT *                                            "
                    + "       FROM fin_movimento AS m                           "
                    + " INNER JOIN fin_lote AS l ON l.id = m.id_lote            "
                    + "      WHERE m.id_pessoa = " + idPessoa + "               "
                    + "        AND dt_vencimento < CURRENT_DATE                 "
                    + "        AND id_baixa IS NULL                             "
                    + "        AND l.is_desconto_folha IS NULL                  "
            //+ "   GROUP BY m.id_pessoa                                  "
            );
            query.setMaxResults(1);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public List<DescontoSocial> listaDescontoSocial(int id_categoria) {
        try {
            Query query = getEntityManager().createQuery(
                    "SELECT ds FROM DescontoSocial ds WHERE ds.categoria.id = :id_categoria"
            );

            query.setParameter("id_categoria", id_categoria);
            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<ServicoPessoa> listaServicoPessoaPorDescontoSocial(Integer id_desconto_social, Integer id_pessoa) {
        try {
            String text_qry = "SELECT sp FROM ServicoPessoa sp WHERE sp.descontoSocial.id = " + id_desconto_social;
            if (id_pessoa != null) {
                text_qry += " AND sp.pessoa.id <> " + id_pessoa;
            }

            Query query = getEntityManager().createQuery(text_qry);

            return query.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    /**
     * Retorna o sócio somente se o mesmo estiver ativo e serviço pessoa ativo
     *
     * @param id Pessoa do serviço pessoa
     * @return
     */
    public Socios pesquisaTitularPorDependente(Integer id) {
        try {
            Query query = getEntityManager().createQuery("SELECT S FROM Socios AS S WHERE S.servicoPessoa.pessoa.id = :id AND S.matriculaSocios.dtInativo IS NULL AND S.servicoPessoa.ativo = true");
            query.setParameter("id", id);
            List list = query.getResultList();
            if (!list.isEmpty() && list.size() == 1) {
                return (Socios) query.getSingleResult();
            }
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * Retorna o sócio titular
     *
     * @param idPessoa da Pessoa
     * @return
     */
    public Socios pesquisaTitularPorPessoa(Integer idPessoa) {
        try {
            Query query = getEntityManager().createQuery("SELECT S FROM Socios AS S WHERE S.matriculaSocios.titular.id = :pessoa AND S.matriculaSocios.titular.id = S.servicoPessoa.pessoa.id AND S.matriculaSocios.dtInativo IS NULL AND S.servicoPessoa.ativo = true");
            query.setParameter("pessoa", idPessoa);
            List list = query.getResultList();
            if (!list.isEmpty() && list.size() == 1) {
                return (Socios) query.getSingleResult();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Retorna o sócio somente se o mesmo estiver ativo e serviço pessoa ativo
     *
     * @param pessoa (id) Pessoa
     * @return
     */
    public List listaPorPessoa(Integer pessoa) {
        try {
            Query query = getEntityManager().createQuery("SELECT S FROM Socios AS S WHERE S.servicoPessoa.pessoa.id = :pessoa");
            query.setParameter("pessoa", pessoa);
            List list = query.getResultList();
            if (!list.isEmpty() && list.size() == 1) {
                return list;
            }
        } catch (Exception e) {

        }
        return new ArrayList();
    }

    /**
     * Retorna o sócio somente se o mesmo estiver ativo e serviço pessoa ativo
     *
     * @param pessoa (id) Pessoa
     * @param todos
     * @return
     */
    public List listaPorPessoa(Integer pessoa, Boolean todos) {
        try {
            Query query = getEntityManager().createQuery("SELECT S FROM Socios AS S WHERE S.servicoPessoa.pessoa.id = :pessoa");
            query.setParameter("pessoa", pessoa);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {

        }
        return new ArrayList();
    }

    /**
     * Retorna o sócio somente se o mesmo estiver ativo e serviço pessoa ativo
     *
     * @param idServicoPessoa Serviço pessoa
     * @return
     */
    public Socios pesquisaSocioPorServicoPessoa(Integer idServicoPessoa) {
        try {
            Query query = getEntityManager().createQuery("SELECT S FROM Socios AS S WHERE S.servicoPessoa.id = :id");
            query.setParameter("id", idServicoPessoa);
            List list = query.getResultList();
            if (!list.isEmpty() && list.size() == 1) {
                return (Socios) query.getSingleResult();
            }
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * Retorna o sócios por pessoa empresa
     *
     * @param idJuridica Serviço pessoa
     * @return
     */
    public List pesquisaSocioPorEmpresa(Integer idJuridica) {
        try {
            String queryString = ""
                    + "     SELECT S.nome           AS nome,                    \n" // 0 - NOME
                    + "            S.matricula      AS matricula,               \n" // 1 - MATRÍCULA
                    + "            S.categoria      As categoria,               \n" // 2 - CATEGORIA
                    + "            S.filiacao       As filiacao,                \n" // 3 - FILIAÇÃO
                    + "            P.admissao       AS admissao,                \n" // 4 - ADMISSÃO
                    + "            S.desconto_folha AS desconto_folha           \n" // 5 - DESCONTO FOLHA
                    + "       FROM soc_socios_vw AS S                           \n"
                    + " INNER JOIN pes_pessoa_vw AS P ON P.codigo = S.codsocio  \n"
                    + "      WHERE P.e_id_pessoa = " + idJuridica + "                            \n";

            Query query = getEntityManager().createNativeQuery(queryString);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    /**
     * Retorna o sócio inativo por matricula
     *
     * @param idPessoa
     * @param idMatriculaSocios
     * @return
     */
    public Socios pesquisaDependenteInativoPorMatricula(Integer idPessoa, Integer idMatriculaSocios) {
        try {
            Query query = getEntityManager().createQuery(" SELECT S FROM Socios AS S WHERE S.servicoPessoa.pessoa.id = :pessoa AND S.matriculaSocios.id = :matriculaSocios AND S.servicoPessoa.ativo = false ");
            query.setParameter("pessoa", idPessoa);
            query.setParameter("matriculaSocios", idMatriculaSocios);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return (Socios) list.get(0);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Retorna o dependentes por titular e matrícula
     *
     * @param idMatriculaSocios
     * @return
     */
    public List pesquisaDependentePorMatricula(Integer idMatriculaSocios) {
        return pesquisaDependentePorMatricula(idMatriculaSocios, false);
    }

    /**
     * Retorna o dependentes por titular e matrícula
     *
     * @param idMatriculaSocios
     * @param com_titular
     * @return
     */
    public List pesquisaDependentePorMatricula(Integer idMatriculaSocios, Boolean com_titular) {
        try {
            String queryString = " ";
            if (com_titular) {
                queryString = ""
                        + "     SELECT S.*                                      \n"
                        + "       FROM soc_socios AS S                          \n"
                        + " INNER JOIN matr_socios MS ON MS.id = S.id_matricula_socios      \n"
                        + " INNER JOIN fin_servico_pessoa SP ON SP.id = S.id_servico_pessoa \n"
                        + " INNER JOIN pes_pessoa P ON P.id = SP.id_pessoa                  \n"
                        + "      WHERE S.id_matricula_socios =  " + idMatriculaSocios + "   \n"
                        + "        AND SP.is_ativo = true                                   \n"
                        + "        AND SP.id_pessoa <> MS.id_titular                        \n"
                        + "   ORDER BY (SP.id_pessoa = MS.id_titular) DESC, P.ds_nome  ASC  \n";
            } else {
                queryString = ""
                        + "     SELECT S.*                                      \n"
                        + "       FROM soc_socios AS S                          \n"
                        + " INNER JOIN matr_socios MS ON MS.id = S.id_matricula_socios      \n"
                        + " INNER JOIN fin_servico_pessoa SP ON SP.id = S.id_servico_pessoa \n"
                        + " INNER JOIN pes_pessoa P ON P.id = SP.id_pessoa                  \n"
                        + "      WHERE S.id_matricula_socios =  " + idMatriculaSocios + "   \n"
                        + "        AND SP.is_ativo = true                                   \n"
                        + "   ORDER BY (SP.id_pessoa = MS.id_titular) DESC, P.ds_nome  ASC  \n";
            }
            Query query = getEntityManager().createNativeQuery(queryString, Socios.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    /**
     * Retorna serviços pessoas que pertencem a sócios
     *
     * @param idPessoa
     * @return
     */
    public List listServicoPessoaInSociosByPessoa(Integer idPessoa) {
        try {
            Query query = getEntityManager().createQuery(" SELECT S.servicoPessoa FROM Socios AS S WHERE S.servicoPessoa.pessoa.id = :pessoa AND S.servicoPessoa.ativo = true ");
            query.setParameter("pessoa", idPessoa);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    /*
     * Verifica se existe mais de uma pessoa com mais de uma matrícula.
     * Mensagem: Constam a mesma pessoa mais de uma vez na mesma matrícula
     * @return
     */
    public Boolean existPessoasMesmaMatricula() {
        return !listPessoasMesmaMatricula(false, 1).isEmpty();
    }

    /*
     * Traz uma lista das pessoas com mais de uma matrícula.
     * Mensagem: Constam a mesma pessoa mais de uma vez na mesma matrícula
     * @return
     */
    public List listPessoasMesmaMatricula() {
        return listPessoasMesmaMatricula(true, null);
    }

    /*
     * Pesquisar se existe pessoas com mais de uma matrícula.
     * Mensagem: Constam a mesma pessoa mais de uma vez na mesma matrícula
     *
     * @param return_pessoas (true = traz a lista de pessoas / false = traz uma lista de id das pessoas)
     * @param limit (Se o limit null traz todos as pessoas)
     * @return
     */
    public List listPessoasMesmaMatricula(Boolean return_pessoas, Integer limit) {
        try {
            String queryString
                    = "     SELECT SP.id_pessoa                                             \n"
                    + "       FROM soc_socios AS S                                          \n"
                    + " INNER JOIN fin_servico_pessoa AS SP ON SP.id = S.id_servico_pessoa  \n"
                    + "   GROUP BY SP.id_pessoa,                                            \n"
                    + "            S.id_matricula_socios                                    \n"
                    + "     HAVING COUNT(*) > 1                                            ";
            Query query;
            if (return_pessoas) {
                queryString = " SELECT P.* FROM pes_pessoa AS P WHERE P.id IN (\n" + queryString + "\n) ORDER BY P.ds_nome ";
                query = getEntityManager().createNativeQuery(queryString, Pessoa.class);
            } else {
                query = getEntityManager().createNativeQuery(queryString);
            }
            if (limit != null && limit > 0) {
                query.setMaxResults(limit);
            }
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    /*
     * Verifica se existe Matrícula Ativa com id_servico_pessoa menor que último.
     * @return
     */
    public Boolean existMatriculaAtivaAtivacaoDesordenada() {
        return !listMatriculaAtivaAtivacaoDesordenada(false, 1).isEmpty();
    }

    /*
     * Traz uma lista Matrícula Ativa com id_servico_pessoa menor que último.
     * Mensagem: Constam a mesma pessoa mais de uma vez na mesma matrícula
     * @return
     */
    public List listMatriculaAtivaAtivacaoDesordenada() {
        return listMatriculaAtivaAtivacaoDesordenada(true, null);
    }

    /*
     * Matrícula Ativa com id_servico_pessoa menor que último, favor entrar em contato com nosso suporte técnico.
     * @return
     */
    public List listMatriculaAtivaAtivacaoDesordenada(Boolean return_pessoas, Integer limit) {
        try {
            String queryString
                    = "        SELECT SP.id_pessoa                                               \n"
                    + "         FROM soc_socios 	AS S                                     \n"
                    + "   INNER JOIN fin_servico_pessoa AS SP ON SP.id = S.id_servico_pessoa     \n"
                    + "   INNER JOIN (                                                           \n"
                    + "         SELECT SP.id_pessoa,                                             \n"
                    + "                max(SP.id) id_servico_pessoa                              \n"
                    + "           FROM soc_socios         AS S                                   \n"
                    + "     INNER JOIN fin_servico_pessoa AS SP ON SP.id = S.id_servico_pessoa   \n"
                    + "       GROUP BY SP.id_pessoa                                              \n"
                    + "    ) AS X ON X.id_pessoa = SP.id_pessoa                                  \n"
                    + "        WHERE SP.is_ativo = true                                          \n"
                    + "          AND S.id_parentesco = 1                                         \n"
                    + "          AND SP.id <> X.id_servico_pessoa                                \n";
            Query query;
            if (return_pessoas) {
                queryString = " SELECT P.* FROM pes_pessoa AS P WHERE P.id IN (\n" + queryString + "\n) ORDER BY P.ds_nome ";
                query = getEntityManager().createNativeQuery(queryString, Pessoa.class);
            } else {
                query = getEntityManager().createNativeQuery(queryString);
            }
            if (limit != null && limit > 0) {
                query.setMaxResults(limit);
            }
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List pesquisaSocios(String descricao, String por, String como) {
        if (descricao.isEmpty()) {
            return new ArrayList();
        }
        try {
            String textQuery = "";

            descricao = AnaliseString.normalizeLower(descricao);
            descricao = (como.equals("I") ? descricao + "%" : "%" + descricao + "%");

            String field = "";

            if (por.equals("nome")) {
                field = "p.ds_nome";
            }
            if (por.equals("email1")) {
                field = "p.ds_email1";
            }
            if (por.equals("email2")) {
                field = "p.ds_email2";
            }
            if (por.equals("rg")) {
                field = "f.ds_rg";
            }
            if (por.equals("cpf")) {
                field = "p.ds_documento";
            }

            int maxResults = 1000;
            if (descricao.length() == 1) {
                maxResults = 50;
            } else if (descricao.length() == 2) {
                maxResults = 150;
            } else if (descricao.length() == 3) {
                maxResults = 500;
            }

            switch (por) {
                case "endereco":
                    textQuery
                            = "      SELECT S.*                                                                                 \n"
                            + "        FROM pes_pessoa_endereco pesend                                                          \n"
                            + "  INNER JOIN pes_pessoa pes                      ON pes.id = pesend.id_pessoa                    \n"
                            + "  INNER JOIN soc_socios_vw AS SVW                ON SVW.codsocio = PES.id                        \n"
                            + "  INNER JOIN soc_socios AS S                     ON S.codsocio = PES.id                          \n"
                            + "  INNER JOIN end_endereco ende                   ON ende.id = pesend.id_endereco                 \n"
                            + "  INNER JOIN end_cidade cid                      ON cid.id = ende.id_cidade                      \n"
                            + "  INNER JOIN end_descricaoricao_endereco enddes  ON enddes.id = ende.id_descricaoricao_endereco  \n"
                            + "  INNER JOIN end_bairro bai                      ON bai.id = ende.id_bairro                      \n"
                            + "  INNER JOIN end_logradouro logr                 ON logr.id = ende.id_logradouro                 \n"
                            + "  INNER JOIN pes_fisica fis                      ON fis.id_pessoa = pes.id                       \n"
                            + "  WHERE (LOWER(FUNC_TRANSLATE(logr.ds_descricaoricao || ' ' || enddes.ds_descricaoricao || ', ' || bai.ds_descricaoricao || ', ' || cid.ds_cidade || ', ' || cid.ds_uf)) LIKE '%" + descricao + "%' \n"
                            + "     OR LOWER(FUNC_TRANSLATE(logr.ds_descricaoricao || ' ' || enddes.ds_descricaoricao || ', ' || cid.ds_cidade  || ', ' || cid.ds_uf)) LIKE '%" + descricao + "%'                       \n"
                            + "     OR LOWER(FUNC_TRANSLATE(logr.ds_descricaoricao || ' ' || enddes.ds_descricaoricao || ', ' || cid.ds_cidade  )) LIKE '%" + descricao + "%'                                           \n"
                            + "     OR LOWER(FUNC_TRANSLATE(logr.ds_descricaoricao || ' ' || enddes.ds_descricaoricao)) LIKE '%" + descricao + "%'                                                                      \n"
                            + "     OR LOWER(FUNC_TRANSLATE(enddes.ds_descricaoricao)) LIKE '%" + descricao + "%'                                                                                                       \n"
                            + "     OR LOWER(FUNC_TRANSLATE(cid.ds_cidade)) LIKE '%" + descricao + "%'                                                                                                                  \n"
                            + "     OR LOWER(FUNC_TRANSLATE(ende.ds_cep)) = '" + descricao + "'                                                                                                                         \n"
                            + "  )                                                                  \n"
                            + "  AND pesend.id_tipo_endereco = 1                                    \n"
                            + "  AND pes.id IN (                                                    \n"
                            + "         SELECT p2.id FROM fin_servico_pessoa sp                     \n"
                            + "          INNER JOIN soc_socios s ON sp.id = s.id_servico_pessoa     \n"
                            + "          INNER JOIN pes_pessoa p2 ON  p2.id = sp.id_pessoa          \n"
                            + "          WHERE sp.is_ativo = TRUE                                   \n"
                            + "  )                                                                  \n"
                            + "  ORDER BY pes.ds_nome LIMIT " + maxResults;
                    break;
                case "matricula":
                    textQuery
                            = "      SELECT S.*                                                         \n"
                            + "        FROM pes_fisica      AS F                                        \n"
                            + "  INNER JOIN pes_pessoa      AS P    ON P.id = f.id_pessoa               \n"
                            + "  INNER JOIN soc_socios_vw   AS SVW  ON SVW.codsocio = PES.id)           \n"
                            + "  INNER JOIN soc_socios      AS S    ON S.codsocio = PES.id              \n"
                            + "  WHERE P.id IN (                                                        \n"
                            + "         SELECT p2.id FROM fin_servico_pessoa sp                         \n"
                            + "          INNER JOIN soc_socios s ON sp.id = s.id_servico_pessoa         \n"
                            + "          INNER JOIN pes_pessoa p2 ON  p2.id = sp.id_pessoa              \n"
                            + "          INNER JOIN matr_socios ms ON  ms.id = s.id_matricula_socios    \n"
                            + "          WHERE sp.is_ativo = TRUE                                       \n"
                            + "            AND ms.nr_matricula = " + descricao.replace("%", "") + "     \n"
                            + "    )                                                                    \n"
                            + "  ORDER BY P.ds_nome LIMIT " + maxResults;
                    break;
                case "codigo":
                    textQuery
                            = "      SELECT S.*                                                         \n"
                            + "        FROM pes_fisica          AS F                                    \n"
                            + "  INNER JOIN pes_pessoa          AS p        ON P.id = F.id_pessoa       \n"
                            + "  INNER JOIN soc_socios_vw       AS SVW      ON SVW.codsocio = PES.id    \n"
                            + "  INNER JOIN soc_socios          AS S        ON S.codsocio = PES.id      \n"
                            + "  INNER JOIN pes_pessoa_empresa  AS PE       ON f.id = pe.id_fisica      \n"
                            + "  WHERE pe.ds_codigo LIKE '" + descricao + "'                            \n"
                            + "    AND p.id IN (                                                        \n"
                            + "         SELECT p2.id FROM fin_servico_pessoa sp                         \n"
                            + "          INNER JOIN soc_socios s ON sp.id = s.id_servico_pessoa         \n"
                            + "          INNER JOIN pes_pessoa p2 ON  p2.id = sp.id_pessoa              \n"
                            + "          WHERE sp.is_ativo = TRUE                                       \n"
                            + "    )                                                                    \n"
                            + "  ORDER BY P.ds_nome LIMIT " + maxResults;
                    break;
                default:
                    textQuery
                            = "      SELECT S.*                                                         \n"
                            + "        FROM pes_fisica         AS F                                     \n"
                            + "  INNER JOIN pes_pessoa          AS P        ON P.id = F.id_pessoa       \n"
                            + "  INNER JOIN soc_socios_vw       AS SVW      ON SVW.codsocio = PES.id    \n"
                            + "  INNER JOIN soc_socios          AS S        ON S.codsocio = PES.id      \n"
                            + "  WHERE LOWER(FUNC_TRANSLATE(" + field + ")) LIKE '" + descricao + "'    \n"
                            + "    AND p.id IN (                                                        \n"
                            + "         SELECT p2.id FROM fin_servico_pessoa sp                         \n"
                            + "          INNER JOIN soc_socios s ON sp.id = s.id_servico_pessoa         \n"
                            + "          INNER JOIN pes_pessoa p2 ON  p2.id = sp.id_pessoa              \n"
                            + "          WHERE sp.is_ativo = TRUE                                       \n"
                            + "    ) "
                            + "  ORDER BY P.ds_nome LIMIT " + maxResults;
                    break;
            }

            Query query = getEntityManager().createNativeQuery(textQuery, Socios.class);

            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List pesquisaHistoricoDeInativacao(Integer pessoa_id) {
        String queryString = ""
                + "    SELECT S.*                                                       \n"
                + "      FROM soc_socios AS S                                           \n"
                + "INNER JOIN matr_socios AS MS ON MS.id = S.id_matricula_socios        \n"
                + "INNER JOIN fin_servico_pessoa AS SP ON SP.id = S.id_servico_pessoa   \n"
                + "     WHERE MS.dt_inativo IS NOT NULL \n"
                + "       AND SP.is_ativo = false       \n"
                + "       AND SP.id_pessoa = ?          \n"
                + "  ORDER BY MS.dt_inativo DESC,       \n"
                + "           SP.id DESC                \n"
                + "     LIMIT 10";
        try {
            Query query = getEntityManager().createNativeQuery(queryString, Socios.class);
            query.setParameter("1", pessoa_id);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
