package br.com.rtools.relatorios.beans;

import br.com.rtools.relatorios.RelatorioOrdem;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.relatorios.dao.RelatorioDao;
import br.com.rtools.relatorios.dao.RelatorioMovimentosNaoGeradosDao;
import br.com.rtools.relatorios.dao.RelatorioOrdemDao;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.sistema.SisProcesso;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.Filters;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class RelatorioMovimentosNaoGeradosBean implements Serializable {

    private List<Filters> listFilters;

    private List<SelectItem> listRelatorio;
    private Integer idRelatorio;

    private List<SelectItem> listRelatorioOrdem;
    private Integer idRelatorioOrdem;
    private String referencia;
    private String referenciaAnterior;

    @PostConstruct
    public void init() {
        new Jasper().init();
        listFilters = new ArrayList();
        listRelatorio = new ArrayList<>();
        idRelatorio = null;
        referencia = DataHoje.referencia();
        referenciaAnterior = DataHoje.dataReferencia(DataHoje.data());
        loadRelatorio();
        loadRelatorioOrdem();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("relatorioMovimentosNaoGeradosBean");
    }
    
    public void atualizaReferencia(){
        referenciaAnterior = DataHoje.dataReferencia("01/"+referencia);
    }

    public void print() {
        print(false);
    }

    public void print(Boolean tags) {
//        if (look()) {
//            GenericaMensagem.warn("Validação", "Selecione um filtro!");
//            return;
//        }
        if (referencia.isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar referência!");
            return;
        }
        SisProcesso sisProcesso = new SisProcesso();
        sisProcesso.start();
        Relatorios r = getRelatorios();

        if (r == null) {
            return;
        }

        List<ObjectJasper> oj = new ArrayList();
        sisProcesso.startQuery();

        RelatorioMovimentosNaoGeradosDao rmngd = new RelatorioMovimentosNaoGeradosDao(r.getId(), null);
        List list = rmngd.find(referencia);

        sisProcesso.finishQuery();
        for (int i = 0; i < list.size(); i++) {
            List o = (List) list.get(i);
            oj.add(new ObjectJasper(o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), o.get(5)));
        }

        if (list.isEmpty()) {
            GenericaMensagem.warn("Mensagem", "Nenhum registro encontrado!");
            return;
        }

        Jasper.EXPORT_TO = true;
        //Jasper.TITLE = r.getNome().toUpperCase();
        Jasper.TITLE = "Movimentos gerados em " + referenciaAnterior + " que não foram gerados em " + referencia;
        Jasper.printReports(r.getJasper(), r.getNome(), (Collection) oj);

        sisProcesso.setProcesso(r.getNome());
        sisProcesso.finish();
    }

    // LOAD
    public void loadRelatorio() {
        listRelatorio = new ArrayList();
        if (listRelatorio.isEmpty()) {
            Rotina r = new Rotina().get();
            List<Relatorios> list = new ArrayList<>();
            if (r != null) {
                list = (List<Relatorios>) new RelatorioDao().pesquisaTipoRelatorio(r.getId());
            }
            if (!list.isEmpty()) {
                idRelatorio = list.get(0).getId();
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPrincipal()) {
                    idRelatorio = list.get(i).getId();
                }
                listRelatorio.add(new SelectItem(list.get(i).getId(), list.get(i).getRotina().getRotina()));
            }
            loadRelatorioOrdem();
        }
    }

    public void loadRelatorioOrdem() {
        listRelatorioOrdem = new ArrayList();
        if (idRelatorio != null) {
            RelatorioOrdemDao relatorioOrdemDao = new RelatorioOrdemDao();
            List<RelatorioOrdem> list = relatorioOrdemDao.findAllByRelatorio(idRelatorio);
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    idRelatorioOrdem = list.get(i).getId();
                }
                listRelatorioOrdem.add(new SelectItem(list.get(i).getId(), list.get(i).getNome()));
            }
        }
    }

    public void load() {
        loadRelatorioOrdem();
    }

    public boolean look() {
//        for (int i = 0; i < listFilters.size(); i++) {
//            if (listFilters.get(i).getActive()) {
//                return false;
//            }
//        }
        return true;
    }

    // GETTERS AND SETTERS
    public List<SelectItem> getListRelatorios() {
        return listRelatorio;
    }

    public void setListRelatorios(List<SelectItem> listRelatorio) {
        this.listRelatorio = listRelatorio;
    }

    public List<SelectItem> getListRelatorioOrdem() {
        return listRelatorioOrdem;
    }

    public void setListRelatorioOrdem(List<SelectItem> listRelatorioOrdem) {
        this.listRelatorioOrdem = listRelatorioOrdem;
    }

    /**
     * 0 CAIXA / BANCO; 1 Data
     *
     * @return
     */
    public List<Filters> getListFilters() {
        return listFilters;
    }

    public void setListFilters(List<Filters> listFilters) {
        this.listFilters = listFilters;
    }

    public List<SelectItem> getListRelatorio() {
        return listRelatorio;
    }

    public void setListRelatorio(List<SelectItem> listRelatorio) {
        this.listRelatorio = listRelatorio;
    }

    public Integer getIdRelatorio() {
        return idRelatorio;
    }

    public void setIdRelatorio(Integer idRelatorio) {
        this.idRelatorio = idRelatorio;
    }

    public Integer getIdRelatorioOrdem() {
        return idRelatorioOrdem;
    }

    public void setIdRelatorioOrdem(Integer idRelatorioOrdem) {
        this.idRelatorioOrdem = idRelatorioOrdem;
    }

    public Relatorios getRelatorios() {
        Relatorios r = null;
        if (!listRelatorio.isEmpty()) {
            r = new RelatorioDao().pesquisaRelatorios(idRelatorio);
        }
        return r;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getReferenciaAnterior() {
        return referenciaAnterior;
    }

    public void setReferenciaAnterior(String referenciaAnterior) {
        this.referenciaAnterior = referenciaAnterior;
    }

    public class ObjectJasper {

        private Object responsavel_id;
        private Object responsavel_nome;
        private Object titular_id;
        private Object titular_nome;
        private Object matricula;
        private Object categoria;

        public ObjectJasper(Object responsavel_id, Object responsavel_nome, Object titular_id, Object titular_nome, Object matricula, Object categoria) {
            this.responsavel_id = responsavel_id;
            this.responsavel_nome = responsavel_nome;
            this.titular_id = titular_id;
            this.titular_nome = titular_nome;
            this.matricula = matricula;
            this.categoria = categoria;
        }

        public Object getResponsavel_id() {
            return responsavel_id;
        }

        public void setResponsavel_id(Object responsavel_id) {
            this.responsavel_id = responsavel_id;
        }

        public Object getResponsavel_nome() {
            return responsavel_nome;
        }

        public void setResponsavel_nome(Object responsavel_nome) {
            this.responsavel_nome = responsavel_nome;
        }

        public Object getTitular_id() {
            return titular_id;
        }

        public void setTitular_id(Object titular_id) {
            this.titular_id = titular_id;
        }

        public Object getTitular_nome() {
            return titular_nome;
        }

        public void setTitular_nome(Object titular_nome) {
            this.titular_nome = titular_nome;
        }

        public Object getMatricula() {
            return matricula;
        }

        public void setMatricula(Object matricula) {
            this.matricula = matricula;
        }

        public Object getCategoria() {
            return categoria;
        }

        public void setCategoria(Object categoria) {
            this.categoria = categoria;
        }
    }
}
