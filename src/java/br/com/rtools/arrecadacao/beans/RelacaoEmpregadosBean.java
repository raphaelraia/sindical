package br.com.rtools.arrecadacao.beans;

import br.com.rtools.arrecadacao.RelacaoEmpregados;
import br.com.rtools.arrecadacao.RelacaoEmpregadosRef;
import br.com.rtools.arrecadacao.dao.RelacaoEmpregadosDao;
import br.com.rtools.arrecadacao.dao.RelacaoEmpregadosRefDao;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.ConfiguracaoUpload;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.Diretorio;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Upload;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import org.primefaces.event.FileUploadEvent;

@ManagedBean
@SessionScoped
public class RelacaoEmpregadosBean implements Serializable {

    private RelacaoEmpregadosRef relacaoEmpregadosRef;
    private List<RelacaoEmpregadosRef> listRelacaoEmpregadosRef;
    private List<SelectItem> listRelacao;
    private Integer idRelacao;
    private RelacaoEmpregados relacaoEmpregados;
    private List<RelacaoEmpregados> listRelacaoEmpregados;
    private Pessoa pessoa;
    private List listFiles;
    private Integer qtdeAnexo;
    private Boolean visible;

    public RelacaoEmpregadosBean() {
        relacaoEmpregadosRef = new RelacaoEmpregadosRef();
        relacaoEmpregados = new RelacaoEmpregados();
        listRelacaoEmpregadosRef = new ArrayList();
        listRelacaoEmpregados = new ArrayList();
        listFiles = new ArrayList();
        listRelacao = new ArrayList();
        qtdeAnexo = 0;
        visible = false;
        idRelacao = 0;
        loadListRelacao();
        pessoa = new Pessoa();
    }

    public void clear() {
        GenericaSessao.remove("relacaoEmpregadosBean");
    }

    public void save() {
        if (relacaoEmpregadosRef.getReferencia().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar uma referência válida!");
            return;
        }
        if (relacaoEmpregadosRef.getDtCarenciaEntrega() == null) {
            GenericaMensagem.warn("Validação", "Informar a data de carência!");
            return;
        }
        if (relacaoEmpregadosRef.getId() == null) {
            if (!new Dao().save(relacaoEmpregadosRef, true)) {
                GenericaMensagem.warn("Erro", "Ao inserir registro!");
                return;
            }
            listRelacaoEmpregadosRef = new ArrayList();
            GenericaMensagem.info("Sucesso", "Registro inserido");
            relacaoEmpregadosRef = new RelacaoEmpregadosRef();
        } else {
            if (!new Dao().update(relacaoEmpregadosRef, true)) {
                GenericaMensagem.warn("Erro", "Ao atualizar registro!");
                return;
            }
            relacaoEmpregadosRef = new RelacaoEmpregadosRef();
            listRelacaoEmpregadosRef = new ArrayList();
            GenericaMensagem.info("Sucesso", "Registro atualizado");

        }
    }

    public void delete() {
        if (!new Dao().delete(relacaoEmpregadosRef, true)) {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
            return;
        }
        relacaoEmpregadosRef = new RelacaoEmpregadosRef();
        listRelacaoEmpregadosRef = new ArrayList();
        GenericaMensagem.info("Sucesso", "Registro removido");
    }

    public void send() {
        relacaoEmpregadosRef = (RelacaoEmpregadosRef) new Dao().find(new RelacaoEmpregadosRef(), idRelacao);
        if (DataHoje.maiorData(new Date(), relacaoEmpregadosRef.getDtCarenciaEntrega())) {
            relacaoEmpregadosRef = new RelacaoEmpregadosRef();
            GenericaMensagem.warn("Validação", "Esse arquivo esta fora do envio de período");
            return;
        }
        visible = true;
        pessoa = new Pessoa();
        listFiles = new ArrayList();
        relacaoEmpregados = new RelacaoEmpregados();
    }

    public void edit(RelacaoEmpregadosRef ref) {
        relacaoEmpregadosRef = ref;
    }

    public void remove(RelacaoEmpregados re) {
        new Dao().delete(re, true);
        listRelacaoEmpregados = new ArrayList();
        GenericaMensagem.info("Sucesso", "Relação removida");
    }

    public RelacaoEmpregadosRef getRelacaoEmpregadosRef() {
        return relacaoEmpregadosRef;
    }

    public void setRelacaoEmpregadosRef(RelacaoEmpregadosRef relacaoEmpregadosRef) {
        this.relacaoEmpregadosRef = relacaoEmpregadosRef;
    }

    public RelacaoEmpregados getRelacaoEmpregados() {
        return relacaoEmpregados;
    }

    public void setRelacaoEmpregados(RelacaoEmpregados relacaoEmpregados) {
        this.relacaoEmpregados = relacaoEmpregados;
    }

//    public void upload() {
//        if (pessoa.getId() == -1) {
//            GenericaMensagem.warn("Validação", "Pesquisar uma pessoa!");
//            return;
//        }
//        relacaoEmpregados.setDtEntrega(new Date());
//        relacaoEmpregados.setRelacao(relacaoEmpregadosRef);
//        relacaoEmpregados.setPessoa(pessoa);
//    }
//    public void upload(FileUploadEvent event) {
//        ConfiguracaoUpload configuracaoUpload = new ConfiguracaoUpload();
//        configuracaoUpload.setArquivo(event.getFile().getFileName());
//        configuracaoUpload.setDiretorio("Arquivos/Anexos/Pendentes/ArquivoContabilidade");
//        configuracaoUpload.setEvent(event);
//        if (Upload.enviar(configuracaoUpload, true)) {
//            listFiles = new ArrayList();
//        }
//        getListFiles();
//    }
    public Pessoa getPessoa() {
        if (GenericaSessao.exists("juridicaPesquisa")) {
            pessoa = ((Juridica) GenericaSessao.getObject("juridicaPesquisa", true)).getPessoa();
        }
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<RelacaoEmpregadosRef> getListRelacaoEmpregadosRef() {
        if (listRelacaoEmpregadosRef.isEmpty()) {
            listRelacaoEmpregadosRef = new RelacaoEmpregadosRefDao().list();
        }
        return listRelacaoEmpregadosRef;
    }

    public void setListRelacaoEmpregadosRef(List<RelacaoEmpregadosRef> listRelacaoEmpregadosRef) {
        this.listRelacaoEmpregadosRef = listRelacaoEmpregadosRef;
    }

    // ARQUIVOS
    public List getListFiles() {
        listFiles.clear();
        if (relacaoEmpregados.getId() != null) {
            listFiles = Diretorio.listaArquivos("Arquivos/homologacao/" + relacaoEmpregados.getId());
        }
        return listFiles;
    }

    public void upload(FileUploadEvent event) {
        if (pessoa.getId() == -1) {
            GenericaMensagem.warn("Validação", "Pesquisar uma pessoa!");
            return;
        }
        if (relacaoEmpregadosRef.getId() == null) {
            GenericaMensagem.warn("Validação", "Informar a referência!");
            return;
        }
        if (relacaoEmpregados.getId() == null) {
            relacaoEmpregados.setDtEntrega(new Date());
            relacaoEmpregados.setRelacao(relacaoEmpregadosRef);
            relacaoEmpregados.setPessoa(pessoa);
            relacaoEmpregados.setOperador(((Usuario) GenericaSessao.getObject("sessaoUsuario")).getPessoa());
            if (!new Dao().save(relacaoEmpregados, true)) {
                GenericaMensagem.warn("Validação", "Relação já enviada!");
                return;
            }
        }
        ConfiguracaoUpload configuracaoUpload = new ConfiguracaoUpload();
        configuracaoUpload.setArquivo(event.getFile().getFileName());
        configuracaoUpload.setDiretorio("Arquivos/arrecadacao/relacao/empregados/" + relacaoEmpregados.getId());
        configuracaoUpload.setEvent(event);
        if (Upload.enviar(configuracaoUpload, true)) {
            listFiles.clear();
        }
        relacaoEmpregados = new RelacaoEmpregados();
        getListFiles();
        GenericaMensagem.info("Sucesso", "Relação enviada");
        visible = false;
        pessoa = new Pessoa();
        relacaoEmpregadosRef = new RelacaoEmpregadosRef();
        listRelacaoEmpregados = new ArrayList<>();

    }

    public void deleteFiles(int index) {
        String caminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/arrecadacao/relacao/empregados/" + relacaoEmpregados.getId() + "/" + (String) ((DataObject) listFiles.get(index)).getArgumento1());
        File fl = new File(caminho);
        fl.delete();
        listFiles.remove(index);
        listFiles.clear();
        getListFiles();
        // PF.update("form_recepcao_upload:id_grid_uploads");
        // PF.update("formRecepcao:id_btn_anexo");
    }

    public Integer getQtdeAnexo() {
        return qtdeAnexo;
    }

    public void setQtdeAnexo(Integer qtdeAnexo) {
        this.qtdeAnexo = qtdeAnexo;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public void clear(String tcase) {
        if (tcase.equals("pessoa")) {
            pessoa = new Pessoa();
        } else if (tcase.equals("close")) {
            visible = false;
            relacaoEmpregadosRef = new RelacaoEmpregadosRef();
            pessoa = new Pessoa();
            listFiles = new ArrayList();
            relacaoEmpregados = new RelacaoEmpregados();
        }
    }

    public List<RelacaoEmpregados> getListRelacaoEmpregados() {
        if (listRelacaoEmpregados.isEmpty()) {
            if (idRelacao != null) {
                listRelacaoEmpregados = new RelacaoEmpregadosDao().findByRelacao(idRelacao);
            }
        }
        return listRelacaoEmpregados;
    }

    public void setListRelacaoEmpregados(List<RelacaoEmpregados> listRelacaoEmpregados) {
        this.listRelacaoEmpregados = listRelacaoEmpregados;
    }

    public List<SelectItem> getListRelacao() {
        return listRelacao;
    }

    public void setListRelacao(List<SelectItem> listRelacao) {
        this.listRelacao = listRelacao;
    }

    public Integer getIdRelacao() {
        return idRelacao;
    }

    public void setIdRelacao(Integer idRelacao) {
        this.idRelacao = idRelacao;
    }

    public void loadListRelacao() {
        listRelacao = new ArrayList<>();
        List<RelacaoEmpregadosRef> list = new RelacaoEmpregadosRefDao().list();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idRelacao = list.get(i).getId();
            }
            listRelacao.add(new SelectItem(list.get(i).getId(), list.get(i).getReferencia() + " - CARÊNCIA: " + list.get(i).getCarenciaEntrega()));
        }
    }

}
