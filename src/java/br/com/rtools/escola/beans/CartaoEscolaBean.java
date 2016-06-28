package br.com.rtools.escola.beans;

import br.com.rtools.escola.MatriculaEscola;
import br.com.rtools.escola.dao.CartaoEscolaDao;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@ManagedBean
@SessionScoped
public class CartaoEscolaBean implements Serializable {

    private List<ListCartaoEscola> listCartaoEscolas;
    private Pessoa aluno;
    private String period;
    private String details;

    @PostConstruct
    public void init() {
        listCartaoEscolas = new ArrayList();
        aluno = new Pessoa();
        period = "nao_impressos";
        period = "nao_impressos";
        details = null;
        loadCartaoEscola(period);
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("cartaoEscolaBean");
    }

    public void close(String tcase) {
        if (tcase.equals("aluno")) {
            aluno = new Pessoa();
        }
    }

    public void print() {
        print(null);
    }

    public void print(ListCartaoEscola cartaoEscola) {
        Dao dao = new Dao();
        dao.openTransaction();
        try {
            List<ListCartaoEscola> listCartaoEscola = new ArrayList<>();
            if (cartaoEscola != null) {
                listCartaoEscola.add(cartaoEscola);
            } else {
                for (int i = 0; i < listCartaoEscolas.size(); i++) {
                    if (listCartaoEscolas.get(i).getSelected()) {
                        listCartaoEscola.add(listCartaoEscolas.get(i));
                        MatriculaEscola matriculaEscola = (MatriculaEscola) dao.find(new MatriculaEscola(), (Integer) listCartaoEscolas.get(i).getMatricula_escola_id());
                        matriculaEscola.setDtCartao(DataHoje.dataHoje());
                        if (!dao.update(matriculaEscola)) {
                            dao.rollback();
                            GenericaMensagem.warn("Erro de sistema", dao.EXCEPCION.getMessage());
                            return;
                        }
                    }
                }
            }
            Jasper.PATH = "";
            Map map = new HashMap();
            List listJasperExport = new ArrayList();
            FacesContext faces = FacesContext.getCurrentInstance();
            for (int i = 0; i < listCartaoEscola.size(); i++) {
                Servicos s = (Servicos) dao.find(new Servicos(), listCartaoEscola.get(i).getServico_id());
                if (s.getModeloCarteirinha() != null) {
                    if (!s.getModeloCarteirinha().getJasper().isEmpty()) {
                        JasperReport jr = (JasperReport) JRLoader.loadObject(new File(((ServletContext) faces.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/" + s.getModeloCarteirinha().getJasper())));
                        List l = new ArrayList();
                        l.add(listCartaoEscola.get(i));
                        JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(l);
                        listJasperExport.add(Jasper.fillObject(jr, null, dtSource));
                    }
                }
            }
            Jasper.printReports("modelo_carteirinha", listJasperExport);
            dao.commit();
            loadCartaoEscola(period);
        } catch (Exception e) {
            dao.rollback();
            GenericaMensagem.warn("Erro de sistema", e.getMessage());
        }

    }

    /**
     * nao_impressos, hoje, ultimos_30_dias e todos
     *
     * @param period
     */
    public void loadCartaoEscola(String period) {
        switch (period) {
            case "nao_impressos":
                details = "Não Impressos";
                break;
            case "hoje":
                details = "Hoje";
                break;
            case "ultimos_30_dias":
                details = "Ultimos 30 dias";
                break;
            case "todos":
                details = "Todos";
                break;
        }
        this.period = period;
        listCartaoEscolas.clear();
        Integer aluno_id = null;
        Integer cursos_id = null;
        if (aluno.getId() != -1) {
            aluno_id = aluno.getId();
        }
        Dao dao = new Dao();
        List list = new CartaoEscolaDao().find(aluno_id, cursos_id, period);
        try {
            for (int i = 0; i < list.size(); i++) {
                List o = (List) list.get(i);
                String foto = null;
                String imagem_fundo = null;
                for (int x = 0; x < 2; x++) {
                    try {
                        File file = null;
                        if (i == 0) {
                            file = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/" + o.get(2) + ".jpg"));
                        } else if (i == 1) {
                            file = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/" + o.get(2) + ".jpg"));
                        } else if (i == 2) {
                            file = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/" + o.get(2) + ".jpg"));
                        }
                        if (file != null && file.exists()) {
                            break;
                        }
                    } catch (Exception e) {
                        foto = null;
                    }
                }
                Servicos s = (Servicos) dao.find(new Servicos(), o.get(8));
                if (s.getModeloCarteirinha() != null) {
                    if (s.getModeloCarteirinha().getFoto() != null && !s.getModeloCarteirinha().getFoto().isEmpty()) {
                        imagem_fundo = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/ModeloCarteirinha/" + s.getModeloCarteirinha().getFoto());
                    }
                }
                listCartaoEscolas.add(new ListCartaoEscola(false, o.get(0), o.get(1), o.get(2), o.get(3), o.get(4), o.get(5), o.get(6), o.get(7), foto, imagem_fundo, o.get(8)));
            }
        } catch (Exception e) {
            GenericaMensagem.warn("Erro de Sistema", e.getMessage());
        }
    }

    public Pessoa getAluno() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            aluno = ((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa();
            loadCartaoEscola(period);
        }
        return aluno;
    }

    public void setAluno(Pessoa aluno) {
        this.aluno = aluno;
    }

    public List<ListCartaoEscola> getListCartaoEscolas() {
        return listCartaoEscolas;
    }

    public void setListCartaoEscolas(List<ListCartaoEscola> listCartaoEscolas) {
        this.listCartaoEscolas = listCartaoEscolas;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public class ListCartaoEscola {

        private Boolean selected;
        private Object matricula_escola_id;
        private Object data_impressao;
        private Object pessoa_id;
        private Object pessoa_nome;
        private Object servico_descricao;
        private Object fisica_nascimento;
        private Object fisica_rg;
        private Object validade;
        private Object pessoa_foto;
        private Object imagem_fundo;
        private Object servico_id;

        public ListCartaoEscola() {
            this.selected = false;
            this.matricula_escola_id = null;
            this.data_impressao = null;
            this.pessoa_id = null;
            this.pessoa_nome = null;
            this.servico_descricao = null;
            this.fisica_nascimento = null;
            this.fisica_rg = null;
            this.validade = null;
            this.pessoa_foto = null;
            this.imagem_fundo = null;
            this.servico_id = null;
        }

        /**
         *
         * @param selected
         * @param matricula_escola_id
         * @param data_impressao
         * @param pessoa_id
         * @param pessoa_nome
         * @param servico_descricao
         * @param fisica_nascimento
         * @param fisica_rg
         * @param validade
         * @param pessoa_foto
         * @param imagem_fundo
         * @param servico_id
         */
        public ListCartaoEscola(Boolean selected, Object matricula_escola_id, Object data_impressao, Object pessoa_id, Object pessoa_nome, Object servico_descricao, Object fisica_nascimento, Object fisica_rg, Object validade, Object pessoa_foto, Object imagem_fundo, Object servico_id) {
            this.selected = selected;
            this.matricula_escola_id = matricula_escola_id;
            this.data_impressao = data_impressao;
            this.pessoa_id = pessoa_id;
            this.pessoa_nome = pessoa_nome;
            this.servico_descricao = servico_descricao;
            this.fisica_nascimento = fisica_nascimento;
            this.fisica_rg = fisica_rg;
            this.validade = validade;
            this.pessoa_foto = pessoa_foto;
            this.imagem_fundo = imagem_fundo;
            this.servico_id = servico_id;
        }

        public Boolean getSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        public Object getMatricula_escola_id() {
            return matricula_escola_id;
        }

        public void setMatricula_escola_id(Object matricula_escola_id) {
            this.matricula_escola_id = matricula_escola_id;
        }

        public Object getData_impressao() {
            return data_impressao;
        }

        public void setData_impressao(Object data_impressao) {
            this.data_impressao = data_impressao;
        }

        public Object getPessoa_id() {
            return pessoa_id;
        }

        public void setPessoa_id(Object pessoa_id) {
            this.pessoa_id = pessoa_id;
        }

        public Object getPessoa_nome() {
            return pessoa_nome;
        }

        public void setPessoa_nome(Object pessoa_nome) {
            this.pessoa_nome = pessoa_nome;
        }

        public Object getServico_descricao() {
            return servico_descricao;
        }

        public void setServico_descricao(Object servico_descricao) {
            this.servico_descricao = servico_descricao;
        }

        public Object getFisica_nascimento() {
            return fisica_nascimento;
        }

        public void setFisica_nascimento(Object fisica_nascimento) {
            this.fisica_nascimento = fisica_nascimento;
        }

        public Object getFisica_rg() {
            return fisica_rg;
        }

        public void setFisica_rg(Object fisica_rg) {
            this.fisica_rg = fisica_rg;
        }

        public Object getValidade() {
            return validade;
        }

        public void setValidade(Object validade) {
            this.validade = validade;
        }

        public Object getPessoa_foto() {
            return pessoa_foto;
        }

        public void setPessoa_foto(Object pessoa_foto) {
            this.pessoa_foto = pessoa_foto;
        }

        public Object getImagem_fundo() {
            return imagem_fundo;
        }

        public void setImagem_fundo(Object imagem_fundo) {
            this.imagem_fundo = imagem_fundo;
        }

        public Object getServico_id() {
            return servico_id;
        }

        public void setServico_id(Object servico_id) {
            this.servico_id = servico_id;
        }

    }

}
