/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.ConviteMovimento;
import br.com.rtools.associativo.ExameMedico;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.ValidadeExameMedico;
import br.com.rtools.associativo.dao.ExameMedicoDao;
import br.com.rtools.impressao.ConviteClube;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.seguranca.Modulo;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.SisPessoa;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class ExameMedicoBean implements Serializable {

    private List<LinhaValidadeExame> listaValidade = new ArrayList();
    private Fisica fisica = new Fisica();
    private SisPessoa sisPessoa = new SisPessoa();
    private List<ExameMedico> listaExameMedico = new ArrayList();
    private ExameMedico exameMedico = new ExameMedico();
    private String ids_departamento = "";
    private String dataEmissao = "";

    public ExameMedicoBean() {
        dataEmissao = DataHoje.data();
        String retorno = (String) GenericaSessao.getObject("urlRetorno");

        Modulo m = Modulo.get();
        if (m.getId() == 6) {
            ids_departamento = "12";
        }
        if (m.getId() == 7) {
            ids_departamento = "11, 16";
        }

        List<ValidadeExameMedico> result = new ExameMedicoDao().listaValidadeExameMedico(ids_departamento);

        for (ValidadeExameMedico linha_result : result) {
            listaValidade.add(
                    new LinhaValidadeExame(
                            retorno,
                            true,
                            linha_result
                    )
            );
        }

        loadListaExameMedico();
    }

    public final void loadListaExameMedico() {
        listaExameMedico.clear();

        ExameMedicoDao dao = new ExameMedicoDao();

        listaExameMedico = dao.listaExameMedico((fisica.getId() == -1 ? null : fisica.getPessoa().getId()), ids_departamento);
    }

    public void editar(ExameMedico em) {
        exameMedico = em;
    }

    public void salvar() {
        if (listaValidade.isEmpty()) {
            GenericaMensagem.warn("ATENÇÃO", "NENHUM DEPARTAMENTO COM VALIDADE FOI SELECIONADO!");
            return;
        }

        if (fisica.getId() == -1 && sisPessoa.getId() == -1) {
            GenericaMensagem.warn("ATENÇÃO", "SELECIONE UM SÓCIO OU CONVIDADO!");
            return;
        }
        if (dataEmissao.isEmpty()) {
            GenericaMensagem.warn("ATENÇÃO", "INFORMAR DATA DE EMISSÃO!");
            return;
        }
        Boolean salvo = false;
        ExameMedicoDao emDao = new ExameMedicoDao();
        Dao dao = new Dao();
        Boolean socio = true;
        dao.openTransaction();
        for (LinhaValidadeExame lve : listaValidade) {
            Date data = emDao.pesquisaDataUltimoExame(fisica.getPessoa().getId(), lve.getVem().getDepartamento().getId());
            if (data == null || DataHoje.maiorData(DataHoje.converte(dataEmissao), data)) {
                ExameMedico em = null;
                if (lve.getChkValidade()) {
                    if (fisica.getPessoa().getId() != -1) {
                        em = new ExameMedico(
                                -1,
                                fisica.getPessoa(),
                                lve.getVem().getDepartamento(),
                                DataHoje.converte(dataEmissao),
                                DataHoje.converte(new DataHoje().incrementarMeses(lve.vem.getMeses(), dataEmissao)),
                                null,
                                Usuario.getUsuario()
                        );
                    } else if (sisPessoa.getId() != -1) {
                        socio = false;
                        em = new ExameMedico(
                                -1,
                                null,
                                lve.getVem().getDepartamento(),
                                DataHoje.converte(dataEmissao),
                                DataHoje.converte(new DataHoje().incrementarMeses(lve.vem.getMesesConvidado(), dataEmissao)),
                                sisPessoa,
                                Usuario.getUsuario()
                        );

                    }
                    if (socio) {
                        if (fisica.getIdade() <= 0) {
                            GenericaMensagem.warn("ATENÇÃO", "SELECIONE UMA PESSOA FÍSICA!");
                            return;
                        }
                        if (!DataHoje.betweenAge(fisica.getIdade(), lve.getVem().getIdadeInicial(), lve.getVem().getIdadeFinal())) {
                            GenericaMensagem.warn("Validação", "PESSOA ENCONTRA-SE FORA DA FAIXA PARA ESTE EXAME!");
                            return;
                        }
                    } else {
                        if (sisPessoa.getIdade() <= 0) {
                            GenericaMensagem.warn("ATENÇÃO", "SELECIONE UMA PESSOA FÍSICA!");
                            return;
                        }

                        if (!DataHoje.betweenAge(sisPessoa.getIdade(), lve.getVem().getIdadeInicial(), lve.getVem().getIdadeFinal())) {
                            GenericaMensagem.warn("Validação", "PESSOA ENCONTRA-SE FORA DA FAIXA PARA ESTE EXAME!");
                            return;
                        }

                    }

                    if (!dao.save(em)) {
                        dao.rollback();
                        GenericaMensagem.error("ATENÇÃO", "NÃO FOI POSSÍVEL SALVAR EXAME, TENTE NOVAMENTE!");
                        return;
                    }

                    salvo = true;
                }
            } else {
                GenericaMensagem.warn("ATENÇÃO", "EXAME MÉDICO (" + lve.getVem().getDepartamento().getDescricao() + ") JÁ CADASTRADO PARA ESTE PERÍODO, DATA " + DataHoje.converteData(data));
            }
        }

        if (salvo) {
            dao.commit();
            GenericaMensagem.info("SUCESSO", "EXAME MÉDICO SALVO!");
        } else {
            dao.rollback();
        }

        loadListaExameMedico();
    }

    public void excluir() {
        if (exameMedico.getId() != -1) {
            Dao dao = new Dao();
            dao.openTransaction();
            if (!dao.delete(dao.find(exameMedico))) {
                dao.rollback();
                GenericaMensagem.error("ERRO", "NÃO FOI POSSÍVEL EXCLUIR EXAME, TENTE NOVAMENTE!");
                return;
            }

            dao.commit();
            dataEmissao = DataHoje.data();
            GenericaMensagem.info("SUCESSO", "EXAME DELETADO!");

            loadListaExameMedico();
        }
    }

    public void removerPessoa() {
        dataEmissao = DataHoje.data();
        fisica = new Fisica();
        sisPessoa = new SisPessoa();
        loadListaExameMedico();
    }

    public List<LinhaValidadeExame> getListaValidade() {
        return listaValidade;
    }

    public void setListaValidade(List<LinhaValidadeExame> listaValidade) {
        this.listaValidade = listaValidade;
    }

    public Fisica getFisica() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            fisica = (Fisica) GenericaSessao.getObject("fisicaPesquisa", true);
            loadListaExameMedico();
        }
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public List<ExameMedico> getListaExameMedico() {
        return listaExameMedico;
    }

    public void setListaExameMedico(List<ExameMedico> listaExameMedico) {
        this.listaExameMedico = listaExameMedico;

    }

    public SisPessoa getSisPessoa() {
        if (GenericaSessao.exists("sisPessoaPesquisa")) {
            sisPessoa = (SisPessoa) GenericaSessao.getObject("sisPessoaPesquisa", true);
        }
        return sisPessoa;
    }

    public void setSisPessoa(SisPessoa sisPessoa) {
        this.sisPessoa = sisPessoa;
    }

    public ExameMedico getExameMedico() {
        return exameMedico;
    }

    public void setExameMedico(ExameMedico exameMedico) {
        this.exameMedico = exameMedico;
    }

    public String getIds_departamento() {
        return ids_departamento;
    }

    public void setIds_departamento(String ids_departamento) {
        this.ids_departamento = ids_departamento;
    }

    public String getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(String dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public class LinhaValidadeExame {

        private String rotina;
        private Boolean chkValidade;
        private ValidadeExameMedico vem;

        public LinhaValidadeExame(String rotina, Boolean chkValidade, ValidadeExameMedico validadeExameMedico) {
            this.rotina = rotina;
            this.chkValidade = chkValidade;
            this.vem = validadeExameMedico;
        }

        public String getRotina() {
            return rotina;
        }

        public void setRotina(String rotina) {
            this.rotina = rotina;
        }

        public Boolean getChkValidade() {
            return chkValidade;
        }

        public void setChkValidade(Boolean chkValidade) {
            this.chkValidade = chkValidade;
        }

        public ValidadeExameMedico getVem() {
            return vem;
        }

        public void setVem(ValidadeExameMedico vem) {
            this.vem = vem;
        }
    }

    public void print(ExameMedico em) {
        if (em.getDtValidade().before(DataHoje.dataHoje())) {
            GenericaMensagem.warn("Validação", "DATA DE VÁLIDADE EXPIROU!");
            return;
        }
        Collection lista = parametroReciboExameMedico(em);
        Jasper.PART_NAME = "";
        Jasper.printReports("/Relatorios/RECIBO_EXAME_MEDICO.jasper", "RECIBO_EXAME_MEDICO", lista);
    }

    public class ReciboExameMedico {

        private Object foto;
        private Object nome;
        private Object validade;
        private Object obs;
        private Object rodape;
        private Object tipo;
        private Object departamento;
        private Object operador_nome;
        private Object numero_recibo;
        private Object codigo;

        public ReciboExameMedico() {
            this.foto = null;
            this.nome = null;
            this.validade = null;
            this.obs = null;
            this.rodape = null;
            this.tipo = null;
            this.departamento = null;
            this.operador_nome = null;
            this.numero_recibo = null;
            this.codigo = null;
        }

        public ReciboExameMedico(Object foto, Object nome, Object validade, Object obs, Object rodape, Object tipo, Object departamento, Object operador_nome, Object numero_recibo, Object codigo) {
            this.foto = foto;
            this.nome = nome;
            this.validade = validade;
            this.obs = obs;
            this.rodape = rodape;
            this.tipo = tipo;
            this.departamento = departamento;
            this.operador_nome = operador_nome;
            this.numero_recibo = numero_recibo;
            this.codigo = codigo;
        }

        public Object getFoto() {
            return foto;
        }

        public void setFoto(Object foto) {
            this.foto = foto;
        }

        public Object getNome() {
            return nome;
        }

        public void setNome(Object nome) {
            this.nome = nome;
        }

        public Object getValidade() {
            return validade;
        }

        public void setValidade(Object validade) {
            this.validade = validade;
        }

        public Object getRodape() {
            return rodape;
        }

        public void setRodape(Object rodape) {
            this.rodape = rodape;
        }

        public Object getObs() {
            return obs;
        }

        public void setObs(Object obs) {
            this.obs = obs;
        }

        public Object getTipo() {
            return tipo;
        }

        public void setTipo(Object tipo) {
            this.tipo = tipo;
        }

        public Object getDepartamento() {
            return departamento;
        }

        public void setDepartamento(Object departamento) {
            this.departamento = departamento;
        }

        public Object getOperador_nome() {
            return operador_nome;
        }

        public void setOperador_nome(Object operador_nome) {
            this.operador_nome = operador_nome;
        }

        public Object getNumero_recibo() {
            return numero_recibo;
        }

        public void setNumero_recibo(Object numero_recibo) {
            this.numero_recibo = numero_recibo;
        }

        public Object getCodigo() {
            return codigo;
        }

        public void setCodigo(Object codigo) {
            this.codigo = codigo;
        }

    }

    public Collection<ReciboExameMedico> parametroReciboExameMedico(ExameMedico em) {
        if (em.getId() == -1) {
            return new ArrayList();
        }
        Collection lista = new ArrayList();
        String nome = "";
        String tipo = null;
        Socios s = null;
        Object codigo = null;
        if (em.getSisPessoa() == null) {
            s = em.getPessoa().getSocios();
            nome = em.getPessoa().getNome();
            codigo = em.getPessoa().getId();
            if (s.getId() != -1) {
                tipo = "SÓCIO";
            } else {
                tipo = "NÃO SÓCIO";
            }
        } else {
            nome = em.getSisPessoa().getNome();
            tipo = "CONVIDADO";
        }
        String departamento = "";
        if (em.getDepartamento().getDescricao().toUpperCase().contains("CLUBE")) {
            departamento = "CLUBE";
        } else if (em.getDepartamento().getDescricao().toUpperCase().contains("ACADEMIA")) {
            departamento = "ACADEMIA";
        }
        lista.add(new ReciboExameMedico(
                ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoConvite.png"),
                nome,
                "De " + DataHoje.converteData(em.getDtEmissao()) + " até " + em.getDtValidadeString(),
                "",
                "",
                tipo,
                departamento,
                em.getOperador().getPessoa().getNome(),
                em.getId(),
                codigo
        ));
        return lista;
    }
}
