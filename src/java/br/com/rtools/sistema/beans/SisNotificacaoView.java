package br.com.rtools.sistema.beans;

import br.com.rtools.principal.DBExternal;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public SisNotificacaoView() {
        listNotificacao = new ArrayList<>();
        try {
            loadNotificacao();
        } catch (SQLException ex) {
            Logger.getLogger(SisNotificacaoView.class.getName()).log(Level.SEVERE, null, ex);
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
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            if (sNotificacao.getHoraInicial() != null && !sNotificacao.getHoraInicial().isEmpty() && sNotificacao.getHoraFinal() != null && sNotificacao.getHoraFinal().isEmpty()) {
                                String data_inicial = dateFormat.format(sNotificacao.getDataInicial() + " " + sNotificacao.getHoraInicial());
                                String data_final = dateFormat.format(sNotificacao.getDataFinal() + " " + sNotificacao.getHoraFinal());
                                try {
                                    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date dtInicial = dateFormat.parse(data_inicial);
                                } catch (ParseException e) {

                                }
                                try {
                                    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date dtFinal = dateFormat.parse(data_final);
                                } catch (ParseException e) {

                                }
                            } else {
                            }
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

    public List<SNotificacao> getListNotificacao() {
        return listNotificacao;
    }

    public void setListNotificacao(List<SNotificacao> listNotificacao) {
        this.listNotificacao = listNotificacao;
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

}
