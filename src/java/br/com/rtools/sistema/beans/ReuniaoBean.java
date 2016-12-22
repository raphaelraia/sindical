package br.com.rtools.sistema.beans;

import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.sistema.Reuniao;
import br.com.rtools.sistema.ReuniaoPresenca;
import br.com.rtools.sistema.dao.ReuniaoPresencaDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class ReuniaoBean implements Serializable {

    private Reuniao reuniao;
    private List<Reuniao> listReuniao;
    private ReuniaoPresenca reuniaoPresenca;
    private List<ReuniaoPresenca> listReuniaoPresenca;
    private Pessoa pessoa;
    private String type;

    @PostConstruct
    public void init() {
        reuniao = new Reuniao();
        pessoa = new Pessoa();
        reuniaoPresenca = new ReuniaoPresenca();
        listReuniao = new ArrayList<>();
        listReuniaoPresenca = new ArrayList<>();
        loadListReuniao();
        loadListReuniaoPresenca();
        type = "todos";
    }

    @PreDestroy
    public void destroy() {
        clear();
    }

    public void clear() {
        GenericaSessao.remove("reuniaoBean");
    }

    public void loadListReuniao() {
        listReuniao = new ArrayList();
        listReuniao = new Dao().list(new Reuniao());
    }

    public void loadListReuniaoPresenca() {
        listReuniaoPresenca = new ArrayList();
        if (reuniao.getId() != null) {
            listReuniaoPresenca = new ReuniaoPresencaDao().findByReuniao(reuniao.getId());
        }
    }

    public void print() {
        List<ObjectPauta> listObjectPautas = new ArrayList();
        for (int i = 0; i < listReuniaoPresenca.size(); i++) {
            String status = "";
            if (DataHoje.maiorData(DataHoje.dataHoje(), listReuniaoPresenca.get(i).getReuniao().getDtReuniao()) || DataHoje.dataHoje() == listReuniaoPresenca.get(i).getReuniao().getDtReuniao()) {
                if (listReuniaoPresenca.get(i).getDtPresenca() == null) {
                    status = "AUSENTE";
                } else {
                    status = "PRESENTE";
                }
            }
            listObjectPautas.add(
                    new ObjectPauta(
                            listReuniaoPresenca.get(i).getReuniao().getTitulo(),
                            listReuniaoPresenca.get(i).getReuniao().getDescricao(),
                            (status.isEmpty() ? null : listReuniaoPresenca.get(i).getReuniao().getPauta()),
                            listReuniaoPresenca.get(i).getReuniao().getOperador().getPessoa().getNome(),
                            listReuniaoPresenca.get(i).getPessoa().getNome(),
                            listReuniaoPresenca.get(i).getPessoa().getDocumento(),
                            listReuniaoPresenca.get(i).getReuniao().getCriacao(),
                            listReuniaoPresenca.get(i).getReuniao().getReuniao(),
                            listReuniaoPresenca.get(i).getReuniao().getHorario(),
                            status,
                            (status.isEmpty() ? null : listReuniaoPresenca.get(i).getDtPresenca())
                    )
            );

        }
        Jasper.printReports("PAUTA.jasper", "pauta", listObjectPautas);
    }

    public String edit(Reuniao r) {
        GenericaSessao.remove("inPessoas");
        reuniao = (Reuniao) new Dao().rebind(r);
        loadListReuniaoPresenca();
        ChamadaPaginaBean.link();
        return "reuniao";
    }

    public void save() {
        Dao dao = new Dao();
        if (reuniao.getDtReuniao() == null) {
            GenericaMensagem.warn("Validação", "Informar data da reunião!");
            return;
        }
        if (reuniao.getTitulo().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar titulo da reunião!");
            return;
        }
        dao.openTransaction();
        if (reuniao.getId() == null) {
            reuniao.setOperador(Usuario.getUsuario());
            if (!dao.save(reuniao)) {
                GenericaMensagem.warn("Erro", "Ao inserir registro!");
                dao.rollback();
            }
            listReuniaoPresenca = new ArrayList();
            inPessoas();
            for (int i = 0; i < listReuniaoPresenca.size(); i++) {
                if (!dao.save(listReuniaoPresenca.get(i))) {
                    GenericaMensagem.warn("Erro", "Ao remover registro!");
                    dao.rollback();
                    return;
                }
            }
            dao.commit();
            GenericaMensagem.info("Sucesso", "Registro inserido!");
            loadListReuniao();
            loadListReuniaoPresenca();
        } else {
            if (!dao.update(reuniao)) {
                GenericaMensagem.warn("Erro", "Ao atualizar registro!");
                dao.rollback();
                return;
            }
            for (int i = 0; i < listReuniaoPresenca.size(); i++) {
                if (listReuniaoPresenca.get(i).getId() == null) {
                    if (!dao.save(listReuniaoPresenca.get(i))) {
                        GenericaMensagem.warn("Erro", "Ao remover registro!");
                        dao.rollback();
                        return;
                    }
                } else if (!dao.update(listReuniaoPresenca.get(i))) {
                    GenericaMensagem.warn("Erro", "Ao remover registro!");
                    dao.rollback();
                    return;
                }
            }
            dao.commit();
            GenericaMensagem.info("Sucesso", "Registro atualizado!");
            loadListReuniao();
            loadListReuniaoPresenca();
        }
    }

    public void delete() {
        Dao dao = new Dao();
        dao.openTransaction();
        for (int i = 0; i < listReuniaoPresenca.size(); i++) {
            if (!dao.delete(listReuniaoPresenca.get(i))) {
                GenericaMensagem.warn("Erro", "Ao remover registro!");
                dao.rollback();
                return;
            }
        }
        if (dao.delete(reuniao)) {
            GenericaMensagem.info("Sucesso", "Registro removido!");
            dao.commit();
            reuniao = new Reuniao();
            reuniaoPresenca = new ReuniaoPresenca();
            listReuniaoPresenca = new ArrayList();
            pessoa = new Pessoa();
        } else {
            dao.rollback();
            GenericaMensagem.warn("Erro", "Ao remover registro!");
        }
    }

    public void add() {
        if (pessoa.getId() == -1) {
            GenericaMensagem.warn("Validação", "Informar pessoa!");
            return;
        }
        reuniaoPresenca = new ReuniaoPresenca();
        reuniaoPresenca.setReuniao(reuniao);
        reuniaoPresenca.setOperador(Usuario.getUsuario());
        reuniaoPresenca.setPessoa(pessoa);
        boolean add = true;
        for (int x = 0; x < listReuniaoPresenca.size(); x++) {
            if (listReuniaoPresenca.get(x).getPessoa().getId() == pessoa.getId()) {
                GenericaMensagem.warn("Validação", "Pessoa já cadastrada!");
                break;
            }
        }
        new Dao().save(reuniaoPresenca, true);
        listReuniaoPresenca.add(reuniaoPresenca);
        GenericaMensagem.info("Sucesso", "Registro inserido!");
        reuniaoPresenca = new ReuniaoPresenca();
        pessoa = new Pessoa();
    }

    public void inPessoas() {
        if (GenericaSessao.exists("inPessoas") && reuniao.getId() != null) {
            String in_pessoas[] = GenericaSessao.getString("inPessoas", true).split(",");
            Usuario operador = Usuario.getUsuario();
            Dao dao = new Dao();
            for (int i = 0; i < in_pessoas.length; i++) {
                ReuniaoPresenca rp = new ReuniaoPresenca();
                rp.setReuniao(reuniao);
                rp.setOperador(operador);
                rp.setPessoa((Pessoa) dao.find(new Pessoa(), Integer.parseInt(in_pessoas[i])));
                boolean add = true;
                for (int x = 0; x < listReuniaoPresenca.size(); x++) {
                    if (listReuniaoPresenca.get(x).getPessoa().getId() == rp.getPessoa().getId()) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    listReuniaoPresenca.add(rp);
                }
            }
        }
    }

    public Reuniao getReuniao() {
        inPessoas();
        return reuniao;
    }

    public void confirmaPresenca(ReuniaoPresenca rp) {
        if (rp.getDtPresenca() != null) {
            GenericaMensagem.warn("Validação", "Presença já confirmada!");
            return;
        }
        rp.setOperador(Usuario.getUsuario());
        rp.setDtPresenca(new Date());
        new Dao().update(rp, true);
        GenericaMensagem.info("Sucesso", "Presença confirmada!");
        loadListReuniaoPresenca();
    }

    public void removePresenca(ReuniaoPresenca rp) {
        if (rp.getDtPresenca() != null) {
            rp.setDtPresenca(null);
            new Dao().update(rp, true);
            loadListReuniaoPresenca();
            return;
        }
        new Dao().delete(rp, true);
        listReuniaoPresenca.remove(rp);
        GenericaMensagem.info("Sucesso", "Presença removida!");
    }

    public void setReuniao(Reuniao reuniao) {
        this.reuniao = reuniao;
    }

    public List<Reuniao> getListReuniao() {
        return listReuniao;
    }

    public void setListReuniao(List<Reuniao> listReuniao) {
        this.listReuniao = listReuniao;
    }

    public ReuniaoPresenca getReuniaoPresenca() {
        return reuniaoPresenca;
    }

    public void setReuniaoPresenca(ReuniaoPresenca reuniaoPresenca) {
        this.reuniaoPresenca = reuniaoPresenca;
    }

    public List<ReuniaoPresenca> getListReuniaoPresenca() {
        return listReuniaoPresenca;
    }

    public void setListReuniaoPresenca(List<ReuniaoPresenca> listReuniaoPresenca) {
        this.listReuniaoPresenca = listReuniaoPresenca;
    }

    public Pessoa getPessoa() {
        if (GenericaSessao.exists("fisicaPesquisa")) {
            pessoa = ((Fisica) GenericaSessao.getObject("fisicaPesquisa", true)).getPessoa();
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public class ObjectPauta {

        private Object titulo;
        private Object descricao;
        private Object pauta;
        private Object responsavel_nome;
        private Object pessoa_nome;
        private Object pessoa_documento;
        private Object data_criacao;
        private Object data_reuniao;
        private Object horario;
        private Object status;
        private Object data_confirmacao;

        public ObjectPauta() {
            this.titulo = null;
            this.descricao = null;
            this.pauta = null;
            this.responsavel_nome = null;
            this.pessoa_nome = null;
            this.pessoa_documento = null;
            this.data_criacao = null;
            this.data_reuniao = null;
            this.horario = null;
            this.status = null;
            this.data_confirmacao = null;
        }

        public ObjectPauta(Object titulo, Object descricao, Object pauta, Object responsavel_nome, Object pessoa_nome, Object pessoa_documento, Object data_criacao, Object data_reuniao, Object horario, Object status, Object data_confirmacao) {
            this.titulo = titulo;
            this.descricao = descricao;
            this.pauta = pauta;
            this.responsavel_nome = responsavel_nome;
            this.pessoa_nome = pessoa_nome;
            this.pessoa_documento = pessoa_documento;
            this.data_criacao = data_criacao;
            this.data_reuniao = data_reuniao;
            this.horario = horario;
            this.status = status;
            this.data_confirmacao = data_confirmacao;
        }

        public Object getTitulo() {
            return titulo;
        }

        public void setTitulo(Object titulo) {
            this.titulo = titulo;
        }

        public Object getDescricao() {
            return descricao;
        }

        public void setDescricao(Object descricao) {
            this.descricao = descricao;
        }

        public Object getPauta() {
            return pauta;
        }

        public void setPauta(Object pauta) {
            this.pauta = pauta;
        }

        public Object getResponsavel_nome() {
            return responsavel_nome;
        }

        public void setResponsavel_nome(Object responsavel_nome) {
            this.responsavel_nome = responsavel_nome;
        }

        public Object getPessoa_nome() {
            return pessoa_nome;
        }

        public void setPessoa_nome(Object pessoa_nome) {
            this.pessoa_nome = pessoa_nome;
        }

        public Object getPessoa_documento() {
            return pessoa_documento;
        }

        public void setPessoa_documento(Object pessoa_documento) {
            this.pessoa_documento = pessoa_documento;
        }

        public Object getData_criacao() {
            return data_criacao;
        }

        public void setData_criacao(Object data_criacao) {
            this.data_criacao = data_criacao;
        }

        public Object getData_reuniao() {
            return data_reuniao;
        }

        public void setData_reuniao(Object data_reuniao) {
            this.data_reuniao = data_reuniao;
        }

        public Object getHorario() {
            return horario;
        }

        public void setHorario(Object horario) {
            this.horario = horario;
        }

        public Object getStatus() {
            return status;
        }

        public void setStatus(Object status) {
            this.status = status;
        }

        public Object getData_confirmacao() {
            return data_confirmacao;
        }

        public void setData_confirmacao(Object data_confirmacao) {
            this.data_confirmacao = data_confirmacao;
        }

    }
}
