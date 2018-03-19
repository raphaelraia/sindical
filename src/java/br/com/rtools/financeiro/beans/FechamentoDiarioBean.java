package br.com.rtools.financeiro.beans;

import br.com.rtools.financeiro.ContaSaldo;
import br.com.rtools.financeiro.Plano5;
import br.com.rtools.financeiro.dao.FechamentoDiarioDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Claudemir Bicha
 */
@ManagedBean
@SessionScoped
public class FechamentoDiarioBean implements Serializable {

    private List<ObjectFechamentoDiario> listaFechamentoDiario = new ArrayList();
    private ObjectFechamentoDiario ofdEstornar = null;
    private String dataFechamento = "";
    private ControleAcessoBean cab = new ControleAcessoBean();

    public FechamentoDiarioBean() {
        loadListaFechamentoDiario();
        GenericaSessao.remove("dataRFD");
        cab = (ControleAcessoBean) GenericaSessao.getObject("controleAcessoBean");
    }

    public void fechar() {
        FechamentoDiarioDao fdao = new FechamentoDiarioDao();
        Dao dao = new Dao();

        if (dataFechamento.isEmpty()) {
            //data_fechamento = DataHoje.dataHoje();
            GenericaMensagem.error("Atenção", "Conta Saldo sem valor inicial!");
            return;
        }

        Date data = fdao.ultimaDataContaSaldo();
        if (DataHoje.converteData(data).equals(DataHoje.data())) {
            GenericaMensagem.warn("Atenção", "Fechamento já foi concluído para o dia de hoje!");
            return;
        }

        if (DataHoje.maiorData(data, DataHoje.dataHoje())) {
            GenericaMensagem.warn("Atenção", "Não existe dia para ser fechado!");
            return;
        }

        if (dataFechamento.equals(DataHoje.data())) {
            // SE true NÃO TEM PERMISSÃO
            if (cab.verificaPermissao("fechar_dia_hoje", 1)) {
                GenericaMensagem.fatal("Atenção", "Você não tem permissão para este Fechamento Diário!");
                return;
            }
        }

        List<Object> result = fdao.listaConcluirFechamentoDiario(dataFechamento, DataHoje.converteData(data));

        if (result.isEmpty()) {
            GenericaMensagem.error("Atenção", "Lista para Fechamento Diario vazia!");
            return;
        }

        List<String> string_logs = new ArrayList();

        Usuario usuario = Usuario.getUsuario();
        Filial filial = (Filial) dao.find(new Filial(), 1);

        dao.openTransaction();
        for (Object ob : result) {
            List linha = (List) ob;
            Plano5 pl5 = (Plano5) dao.find(new Plano5(), (Integer) linha.get(2));

            ContaSaldo cs = new ContaSaldo(
                    -1,
                    (Date) linha.get(0),
                    Moeda.converteUS$(Moeda.converteR$Double((Double) linha.get(1))),
                    pl5,
                    usuario,
                    filial,
                    null
            );

            if (!dao.save(cs)) {
                GenericaMensagem.error("Atenção", "Erro ao tentar salvar Conta Saldo!");
                dao.rollback();
                return;
            }

            string_logs.add(
                    "DATA BAIXA: " + DataHoje.converteData((Date) linha.get(0)) + " \n "
                    + "VALOR: " + Moeda.converteUS$(Moeda.converteR$Double((Double) linha.get(1))) + " \n "
                    + "CONTA: " + pl5.getConta() + " \n "
                    + "---------------------------------------------------------"
            );
        }

        dao.commit();

        NovoLog log = new NovoLog();
        String logs = "";
        for (String log_string : string_logs) {
            logs += log_string + " \n ";
        }
        log.save(logs);

        loadListaFechamentoDiario();
        GenericaMensagem.info("Sucesso", "Fechamento Concluído!");
    }

    public void selecionarFechar() {
        FechamentoDiarioDao fdao = new FechamentoDiarioDao();
        Date data = fdao.ultimaDataContaSaldo();
        dataFechamento = (data != null) ? DataHoje.converteData((Date) data) : "";
        if (!dataFechamento.isEmpty()) {
            DataHoje dh = new DataHoje();
            dataFechamento = dh.incrementarDias(1, dataFechamento);
        }
    }

    public void selecionarEstornar(ObjectFechamentoDiario ofd) {
        ofdEstornar = ofd;
    }

    public void estornar() {
        if (ofdEstornar == null) {
            GenericaMensagem.error("Erro", "Sem objeto para estorno!");
            return;
        }

        if (!ofdEstornar.getListObjectFechamentoDiarioDetalhe().isEmpty()) {
            List<String> string_logs = new ArrayList();
            Dao dao = new Dao();

            List<ContaSaldo> l_saldo = new FechamentoDiarioDao().listaFechamentoDiarioDetalheTodos(ofdEstornar.getDataString());

            if (!l_saldo.isEmpty()) {

                dao.openTransaction();
                for (ContaSaldo cs : l_saldo) {
                    if (!dao.delete(cs)) {
                        dao.rollback();
                        GenericaMensagem.error("Erro", "Não foi possível estornar Fechamento Diario!");
                        return;
                    }

                    string_logs.add(
                            "CONTA SALDO ID: " + cs.getId() + " \n "
                            + " VALOR: " + cs.getSaldoString() + " \n "
                            + " CONTA: " + cs.getPlano5().getConta() + " \n "
                            + "------------------------------------------------------------ "
                    );
                }

                dao.commit();
                NovoLog log = new NovoLog();
                String logs = "";
                for (String log_string : string_logs) {
                    logs += log_string + " \n ";
                }
                log.delete(logs);

            }
        }

        ofdEstornar = null;
        loadListaFechamentoDiario();
        GenericaMensagem.info("Sucesso", "Estorno de Fechamento Diario concluído!");
    }

    public final void loadListaFechamentoDiario() {
        listaFechamentoDiario.clear();

        List<Object> result = new FechamentoDiarioDao().listaFechamentoDiario();
        Dao dao = new Dao();

        Boolean est = true;
        for (Object ob : result) {
            List linha = (List) ob;

            List list_detalhe = new FechamentoDiarioDao().listaFechamentoDiarioDetalhe(DataHoje.converteData((Date) linha.get(0)));

            List<ObjectFechamentoDiarioDetalhe> list_obj_detalhe = new ArrayList();
            for (Object ob_detalhe : list_detalhe) {
                List linha_detalhe = (List) ob_detalhe;
                String estilo = "";
                
                if (linha_detalhe.get(6) != null) {
                    estilo = "color:blue; font-weight:bold; font-size: 12pt!important";
                } else {
                    switch ((Integer) linha_detalhe.get(7)) {
                        case 1:
                            estilo = "color:green;";
                            break;
                        case 2:
                            estilo = "color:red;";
                            break;
                        default:
                            break;
                    }
                }

                list_obj_detalhe.add(
                        new ObjectFechamentoDiarioDetalhe(
                                (ContaSaldo) dao.find(new ContaSaldo(), (Integer) linha_detalhe.get(0)),
                                (Plano5) dao.find(new Plano5(), (Integer) linha_detalhe.get(1)),
                                (Date) linha_detalhe.get(2),
                                (String) linha_detalhe.get(3),
                                (Double) linha_detalhe.get(4),
                                (String) linha_detalhe.get(5),
                                linha_detalhe.get(6),
                                linha_detalhe.get(7),
                                linha_detalhe.get(9),
                                estilo
                        )
                );
            }

            listaFechamentoDiario.add(
                    new ObjectFechamentoDiario(
                            (Date) linha.get(0),
                            (Double) linha.get(1),
                            list_obj_detalhe,
                            (result.size() == 1) ? false : est
                    )
            );

            est = false;
        }
    }

    public List<ObjectFechamentoDiario> getListaFechamentoDiario() {
        return listaFechamentoDiario;
    }

    public void setListaFechamentoDiario(List<ObjectFechamentoDiario> listaFechamentoDiario) {
        this.listaFechamentoDiario = listaFechamentoDiario;
    }

    public ObjectFechamentoDiario getOfdEstornar() {
        return ofdEstornar;
    }

    public void setOfdEstornar(ObjectFechamentoDiario ofdEstornar) {
        this.ofdEstornar = ofdEstornar;
    }

    public String getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(String dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public ControleAcessoBean getCab() {
        return cab;
    }

    public void setCab(ControleAcessoBean cab) {
        this.cab = cab;
    }

    public class ObjectFechamentoDiario {

        private Date data;
        private Double saldo;
        private List<ObjectFechamentoDiarioDetalhe> listObjectFechamentoDiarioDetalhe;
        private Boolean estornar;

        public ObjectFechamentoDiario(Date data, Double saldo, List<ObjectFechamentoDiarioDetalhe> listObjectFechamentoDiarioDetalhe, Boolean estornar) {
            this.data = data;
            this.saldo = saldo;
            this.listObjectFechamentoDiarioDetalhe = listObjectFechamentoDiarioDetalhe;
            this.estornar = estornar;
        }

        public Date getData() {
            return data;
        }

        public void setData(Date data) {
            this.data = data;
        }

        public String getDataString() {
            return DataHoje.converteData(data);
        }

        public void setDataString(String dataString) {
            this.data = DataHoje.converte(dataString);
        }

        public Double getSaldo() {
            return saldo;
        }

        public void setSaldo(Double saldo) {
            this.saldo = saldo;
        }

        public String getSaldoString() {
            return Moeda.converteR$Double(saldo);
        }

        public void setSaldoString(String saldoString) {
            this.saldo = Moeda.converteUS$(saldoString);
        }

        public List<ObjectFechamentoDiarioDetalhe> getListObjectFechamentoDiarioDetalhe() {
            return listObjectFechamentoDiarioDetalhe;
        }

        public void setListObjectFechamentoDiarioDetalhe(List<ObjectFechamentoDiarioDetalhe> listObjectFechamentoDiarioDetalhe) {
            this.listObjectFechamentoDiarioDetalhe = listObjectFechamentoDiarioDetalhe;
        }

        public Boolean getEstornar() {
            return estornar;
        }

        public void setEstornar(Boolean estornar) {
            this.estornar = estornar;
        }

    }

    public class ObjectFechamentoDiarioDetalhe {

        private ContaSaldo contaSaldo;
        private Plano5 plano5;
        private Date data;
        private String conta;
        private Double saldo;
        private String classificador;
        private Object caixaBanco;
        private Object id_p4;
        private Object conta1;
        private Object estilo;

        public ObjectFechamentoDiarioDetalhe(ContaSaldo contaSaldo, Plano5 plano5, Date data, String conta, Double saldo, String classificador, Object caixaBanco, Object id_p4, Object conta1, Object estilo) {
            this.contaSaldo = contaSaldo;
            this.plano5 = plano5;
            this.data = data;
            this.conta = conta;
            this.saldo = saldo;
            this.classificador = classificador;
            this.classificador = classificador;
            this.caixaBanco = caixaBanco;
            this.id_p4 = id_p4;
            this.conta1 = conta1;
            this.estilo = estilo;
        }

        public Date getData() {
            return data;
        }

        public void setData(Date data) {
            this.data = data;
        }

        public String getDataString() {
            return DataHoje.converteData(data);
        }

        public void setDataString(String dataString) {
            this.data = DataHoje.converte(dataString);
        }

        public String getConta() {
            return conta;
        }

        public void setConta(String conta) {
            this.conta = conta;
        }

        public Double getSaldo() {
            return saldo;
        }

        public void setSaldo(Double saldo) {
            this.saldo = saldo;
        }

        public String getSaldoString() {
            return Moeda.converteR$Double(saldo);
        }

        public void setSaldoString(String saldoString) {
            this.saldo = Moeda.converteUS$(saldoString);
        }

        public ContaSaldo getContaSaldo() {
            return contaSaldo;
        }

        public void setContaSaldo(ContaSaldo contaSaldo) {
            this.contaSaldo = contaSaldo;
        }

        public Plano5 getPlano5() {
            return plano5;
        }

        public void setPlano5(Plano5 plano5) {
            this.plano5 = plano5;
        }

        public String getClassificador() {
            return classificador;
        }

        public void setClassificador(String classificador) {
            this.classificador = classificador;
        }

        public Object getCaixaBanco() {
            return caixaBanco;
        }

        public void setCaixaBanco(Object caixaBanco) {
            this.caixaBanco = caixaBanco;
        }

        public Object getId_p4() {
            return id_p4;
        }

        public void setId_p4(Object id_p4) {
            this.id_p4 = id_p4;
        }

        public Object getConta1() {
            return conta1;
        }

        public void setConta1(Object conta1) {
            this.conta1 = conta1;
        }

        public Object getEstilo() {
            return estilo;
        }

        public void setEstilo(Object estilo) {
            this.estilo = estilo;
        }

    }

}
