package br.com.rtools.sistema.beans;

import br.com.rtools.financeiro.dao.ServicosDao;
import br.com.rtools.principal.DBExternal;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.sistema.SisNotificacao;
import br.com.rtools.sistema.dao.SisNotificacaoClienteDao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Moeda;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class SisNotificacaoView implements Serializable {

    private List<SNotificacao> listNotificacao;
    
    private List<SNotificacaoServico> listaNotificacaoServico;

    public SisNotificacaoView() {
        listNotificacao = new ArrayList<>();
        listaNotificacaoServico = new ArrayList();
        try {
            loadNotificacao();
            loadNotificacaoLocal();
            
            loadNotificacaoServico();
        } catch (SQLException ex) {
            Logger.getLogger(SisNotificacaoView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void imprimirNotificacaoServico(){
        Jasper.printReports("/Relatorios/SERVICO_AVISO.jasper", "Serviços não quitados", listaNotificacaoServico);
    }
    
    public final void loadNotificacaoServico(){
        listaNotificacaoServico.clear();
        
        MacFilial mf =  MacFilial.getAcessoFilial();
        
        if (mf != null && mf.getId() != -1){
            ServicosDao sdao = new ServicosDao();
            
            if (mf.getDepartamento() != null){
                
                List result = sdao.listaServicoAvisoMovimento(mf.getDepartamento().getId());
                
                for (int i = 0; i < result.size(); i++) {
                    listaNotificacaoServico.add(new SNotificacaoServico(
                            (Integer) ((List) result.get(i)).get(0), 
                            (Integer) ((List) result.get(i)).get(1),
                            ((List) result.get(i)).get(2).toString(), 
                            ((List) result.get(i)).get(3).toString(), 
                            (Date)((List) result.get(i)).get(4), 
                            (Double) ((List) result.get(i)).get(5))
                    );
                }
            
            }
        }
    }
    
    public final void loadNotificacao() throws SQLException {
        if (!GenericaSessao.getString("sessaoCliente").equals("ComercioLimeira") && !GenericaSessao.getString("sessaoCliente").equals("Sindical")) {
            try {
                DBExternal dbe = new DBExternal();
                if (dbe.getConnection() != null) {
                    try {
                        String string = ""
                                + "     SELECT 1    AS id, \n"
                                + "            NCAT.ds_descricao AS categoria_descricao,                \n"
                                + "            C.ds_identifica AS cliente,                              \n"
                                + "            to_char(N.dt_cadastro, 'dd/MM/yyyy') AS data_cadastro,   \n"
                                + "            to_char(N.dt_cadastro, 'HH24:ii') AS hora_cadastro,      \n"
                                + "            to_char(N.dt_inicial, 'dd/MM/yyyy') AS inicio,           \n"
                                + "            to_char(N.dt_final, 'dd/MM/yyyy') AS fim,                \n"
                                + "            to_char(N.dt_inicial, 'HH24:MI') AS hora_inicio,         \n"
                                + "            to_char(N.dt_final, 'HH24:MI') AS hora_fim,              \n"
                                + "            N.ds_titulo AS titulo,                                   \n"
                                + "            N.ds_observacao AS observacao,                           \n"
                                + "            N.is_destaque AS destaque                                \n"
                                + "       FROM sis_notificacao_cliente AS NC                            \n"
                                + " INNER JOIN sis_notificacao AS N ON N.id = NC.id_notificacao         \n"
                                + " INNER JOIN sis_notificacao_categoria AS NCAT ON NCAT.id = N.id_notificacao_categoria\n"
                                + " INNER JOIN sis_configuracao AS C ON C.id = NC.id_configuracao                       \n"
                                + "      WHERE C.ds_identifica = '" + GenericaSessao.getString("sessaoCliente") + "'    \n"
                                + "        AND N.is_ativo = true                                                        \n"
                                + "        AND current_timestamp >= N.dt_inicial                                        \n"
                                + "        AND current_timestamp <= N.dt_final  ";
                        ResultSet resultSet = dbe.getStatment().executeQuery(string);
                        while (resultSet.next()) {
                            SNotificacao sNotificacao = new SNotificacao();
                            sNotificacao.setId(resultSet.getInt("id"));
                            sNotificacao.setCategoria(resultSet.getString("categoria_descricao"));
                            sNotificacao.setDataCadastro(resultSet.getString("data_cadastro"));
                            sNotificacao.setHoraCadastro(resultSet.getString("hora_cadastro"));
                            sNotificacao.setDataInicial(resultSet.getString("inicio"));
                            sNotificacao.setDataFinal(resultSet.getString("fim"));
                            sNotificacao.setHoraInicial(resultSet.getString("hora_inicio"));
                            sNotificacao.setHoraFinal(resultSet.getString("hora_fim"));
                            sNotificacao.setTitulo(resultSet.getString("titulo"));
                            sNotificacao.setObservacao(resultSet.getString("observacao"));
                            try {
                                sNotificacao.setAtivo(resultSet.getBoolean("ativo"));
                            } catch (Exception e) {

                            }
                            try {
                                sNotificacao.setDestaque(resultSet.getBoolean("destaque"));
                            } catch (Exception e) {

                            }
//                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                            if (sNotificacao.getHoraInicial() != null && !sNotificacao.getHoraInicial().isEmpty() && sNotificacao.getHoraFinal() != null && sNotificacao.getHoraFinal().isEmpty()) {
//                                String data_inicial = dateFormat.format(sNotificacao.getDataInicial() + " " + sNotificacao.getHoraInicial());
//                                String data_final = dateFormat.format(sNotificacao.getDataFinal() + " " + sNotificacao.getHoraFinal());
//                                try {
//                                    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                    Date dtInicial = dateFormat.parse(data_inicial);
//                                } catch (ParseException e) {
//
//                                }
//                                try {
//                                    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                    Date dtFinal = dateFormat.parse(data_final);
//                                } catch (ParseException e) {
//
//                                }
//                            } else {
//                            }
                            listNotificacao.add(sNotificacao);
                        }
                    } catch (SQLException exception) {
                        dbe.closeStatment();
                    }
                    dbe.getStatment().close();
                }
            } catch (Exception e) {

            }
        }
    }

    public final void loadNotificacaoLocal() throws SQLException {
        try {
            SisNotificacaoClienteDao sncd = new SisNotificacaoClienteDao();
            List<SisNotificacao> list = sncd.findAll();
            for (int i = 0; i < list.size(); i++) {
                SNotificacao sNotificacao = new SNotificacao();
                sNotificacao.setId(list.get(i).getId());
                sNotificacao.setCategoria(list.get(i).getSisNotificacaoCategoria().getDescricao());
                sNotificacao.setDataCadastro(list.get(i).getCadastroString());
                sNotificacao.setHoraCadastro(DataHoje.livre(list.get(i).getDtCadastro(), "HH:mm"));
                sNotificacao.setDataInicial(list.get(i).getInicialString());
                sNotificacao.setDataFinal(list.get(i).getFinalString());
                sNotificacao.setHoraInicial(list.get(i).getHoraInicial());
                sNotificacao.setHoraFinal(list.get(i).getHoraFinal());
                sNotificacao.setTitulo(list.get(i).getTitulo());
                sNotificacao.setObservacao(list.get(i).getObservacao());
                sNotificacao.setAtivo(list.get(i).getAtivo());
                sNotificacao.setDestaque(list.get(i).getDestaque());
                listNotificacao.add(sNotificacao);
            }
        } catch (Exception e) {

        }
    }

    public List<SNotificacao> getListNotificacao() {
        return listNotificacao;
    }

    public void setListNotificacao(List<SNotificacao> listNotificacao) {
        this.listNotificacao = listNotificacao;
    }

    public class SNotificacaoServico {
        private Integer movimento_id;
        private Integer pessoa_id;
        private String nome;
        private String servico;
        private Date vencimento;
        private Double valor;

        public SNotificacaoServico(Integer movimento_id, Integer pessoa_id, String nome, String servico, Date vencimento, Double valor) {
            this.movimento_id = movimento_id;
            this.pessoa_id = pessoa_id;
            this.nome = nome;
            this.servico = servico;
            this.vencimento = vencimento;
            this.valor = valor;
        }
        
        public Integer getMovimento_id() {
            return movimento_id;
        }

        public void setMovimento_id(Integer movimento_id) {
            this.movimento_id = movimento_id;
        }

        public Integer getPessoa_id() {
            return pessoa_id;
        }

        public void setPessoa_id(Integer pessoa_id) {
            this.pessoa_id = pessoa_id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getServico() {
            return servico;
        }

        public void setServico(String servico) {
            this.servico = servico;
        }

        public String getVencimentoString() {
            return DataHoje.converteData(vencimento);
        }
        
        public Date getVencimento() {
            return vencimento;
        }

        public void setVencimento(Date vencimento) {
            this.vencimento = vencimento;
        }

        public String getValorString() {
            return Moeda.converteR$Double(valor);
        }

        public Double getValor() {
            return valor;
        }

        public void setValor(Double valor) {
            this.valor = valor;
        }
    
    }
    
    public class SNotificacao {

        private Integer id;
        private String titulo;
        private String observacao;
        private String categoria;
        private String dataCadastro;
        private String horaCadastro;
        private String dataInicial;
        private String dataFinal;
        private String horaInicial;
        private String horaFinal;
        private Boolean ativo;
        private Boolean destaque;

        public SNotificacao() {
            this.id = 0;
            this.titulo = "";
            this.observacao = "";
            this.categoria = "";
            this.dataCadastro = "";
            this.horaCadastro = "";
            this.dataInicial = "";
            this.dataFinal = "";
            this.horaInicial = "";
            this.horaFinal = "";
            this.ativo = false;
            this.destaque = false;
        }

        public SNotificacao(Integer id, String titulo, String observacao, String categoria, String dataCadastro, String horaCadastro, String dataInicial, String dataFinal, String horaInicial, String horaFinal, Boolean ativo, Boolean destaque) {
            this.id = id;
            this.titulo = titulo;
            this.observacao = observacao;
            this.categoria = categoria;
            this.dataCadastro = dataCadastro;
            this.horaCadastro = horaCadastro;
            this.dataInicial = dataInicial;
            this.dataFinal = dataFinal;
            this.horaInicial = horaInicial;
            this.horaFinal = horaFinal;
            this.ativo = ativo;
            this.destaque = destaque;
        }

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getObservacao() {
            return observacao;
        }

        public void setObservacao(String observacao) {
            this.observacao = observacao;
        }

        public String getCategoria() {
            return categoria;
        }

        public void setCategoria(String categoria) {
            this.categoria = categoria;
        }

        public String getDataCadastro() {
            return dataCadastro;
        }

        public void setDataCadastro(String dataCadastro) {
            this.dataCadastro = dataCadastro;
        }

        public String getHoraCadastro() {
            return horaCadastro;
        }

        public void setHoraCadastro(String horaCadastro) {
            this.horaCadastro = horaCadastro;
        }

        public String getDataInicial() {
            return dataInicial;
        }

        public void setDataInicial(String dataInicial) {
            this.dataInicial = dataInicial;
        }

        public String getDataFinal() {
            return dataFinal;
        }

        public void setDataFinal(String dataFinal) {
            this.dataFinal = dataFinal;
        }

        public String getHoraInicial() {
            return horaInicial;
        }

        public void setHoraInicial(String horaInicial) {
            this.horaInicial = horaInicial;
        }

        public String getHoraFinal() {
            return horaFinal;
        }

        public void setHoraFinal(String horaFinal) {
            this.horaFinal = horaFinal;
        }

        public Boolean getAtivo() {
            return ativo;
        }

        public void setAtivo(Boolean ativo) {
            this.ativo = ativo;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Boolean getDestaque() {
            return destaque;
        }

        public void setDestaque(Boolean destaque) {
            this.destaque = destaque;
        }

    }

    public List<SNotificacaoServico> getListaNotificacaoServico() {
        return listaNotificacaoServico;
    }

    public void setListaNotificacaoServico(List<SNotificacaoServico> listaNotificacaoServico) {
        this.listaNotificacaoServico = listaNotificacaoServico;
    }

}
