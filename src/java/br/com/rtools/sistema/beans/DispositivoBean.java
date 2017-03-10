package br.com.rtools.sistema.beans;

import br.com.rtools.pessoa.BiometriaServidor;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.dao.BiometriaDao;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.controleUsuario.ControleAcessoWebService;
import br.com.rtools.seguranca.dao.MacFilialDao;
import br.com.rtools.sistema.Dispositivo;
import br.com.rtools.sistema.TipoDispositivo;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaRequisicao;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.webservice.classes.WSHeaders;
import br.com.rtools.webservice.classes.WSStatus;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.primefaces.json.JSONException;

@ManagedBean
@SessionScoped
public class DispositivoBean implements Serializable {

    private Dispositivo dispositivo;
    private List<Dispositivo> listDispositivo;
    private Integer idTipoDispositivo;
    private List<SelectItem> listTipoDispositivo;
    private Integer idFilial;
    private List<SelectItem> listFilial;

    @PostConstruct
    public void init() {
        dispositivo = new Dispositivo();
        loadListDispositivo();
        loadListTipoDispositivo();
        loadListFilial();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("dispositivoBean");
    }

    public void clear() {
        GenericaSessao.remove("dispositivoBean");
    }

    public void listener(String tcase) {
        if (tcase.equals("mac")) {
            if (!dispositivo.getMac().isEmpty()) {
                if (!dispositivo.getMac().matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")) {
                    GenericaMensagem.warn("Validação", "INFORMAR UM MAC VÁLIDO!");
                    return;
                }
                dispositivo.setMacFilial(new MacFilialDao().pesquisaMac(dispositivo.getMac()));
            }
        }
    }

    public void loadListDispositivo() {
        listDispositivo = new ArrayList();
        listDispositivo = new Dao().list(new Dispositivo());
    }

    public void loadListTipoDispositivo() {
        listTipoDispositivo = new ArrayList();
        List<TipoDispositivo> list = new Dao().list(new TipoDispositivo(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idTipoDispositivo = list.get(i).getId();
            }
            listTipoDispositivo.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
        }
    }

    public void loadListFilial() {
        listFilial = new ArrayList();
        listFilial.add(new SelectItem(null, "-- NÃO ATRIBUIR --"));
        List<Filial> list = new Dao().list(new Filial(), true);
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                idFilial = list.get(i).getId();
            }
            listFilial.add(new SelectItem(list.get(i).getId(), list.get(i).getFilial().getPessoa().getNome()));
        }
    }

    public void edit(Dispositivo d) {
        dispositivo = (Dispositivo) new Dao().rebind(d);
        idTipoDispositivo = dispositivo.getTipoDispositivo().getId();
        idFilial = dispositivo.getFilial().getId();
    }

    public void remove(Dispositivo d) {
        if (new Dao().delete(d, true)) {
            GenericaMensagem.info("Sucesso", "Registro removido");
            loadListDispositivo();
        } else {
            GenericaMensagem.warn("Erro", "Ao remover este registro!");
        }
        dispositivo = new Dispositivo();
        loadListDispositivo();
    }

    public void save() {
        Dao dao = new Dao();
        if (dispositivo.getNome().isEmpty()) {
            GenericaMensagem.warn("Validação", "INFORMAR NOME/APELIDO!");
            return;
        }
        if (!dispositivo.getMac().isEmpty()) {
            if (!dispositivo.getMac().matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")) {
                GenericaMensagem.warn("Validação", "INFORMAR UM MAC VÁLIDO!");
                return;
            }
        }
        if (listTipoDispositivo.isEmpty()) {
            GenericaMensagem.warn("Validação", "CADASTRAR TIPO DE DISPOSITIVO!");
            return;
        }
        dispositivo.setTipoDispositivo((TipoDispositivo) dao.find(new TipoDispositivo(), idTipoDispositivo));
        if (idFilial != null) {
            dispositivo.setFilial((Filial) dao.find(new Filial(), idFilial));
        } else {
            dispositivo.setFilial(null);
        }
        listener("mac");
        dispositivo.setMacFilial(new MacFilialDao().pesquisaMac(dispositivo.getMac()));
        if (dispositivo.getId() == null) {
            if (dao.save(dispositivo, true)) {
                GenericaMensagem.info("Sucesso", "Registro inserido");
                loadListDispositivo();
                dispositivo = new Dispositivo();
            } else {
                GenericaMensagem.warn("Erro", "Ao atualizar este registro!");
            }
        } else if (dao.update(dispositivo, true)) {
            GenericaMensagem.info("Sucesso", "Registro atualizado");
            loadListDispositivo();
            dispositivo = new Dispositivo();
        } else {
            GenericaMensagem.warn("Erro", "Ao atualizar este registro!");
        }
    }

    public Dispositivo getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(Dispositivo dispositivo) {
        this.dispositivo = dispositivo;
    }

    public List<Dispositivo> getListDispositivo() {
        return listDispositivo;
    }

    public void setListDispositivo(List<Dispositivo> listDispositivo) {
        this.listDispositivo = listDispositivo;
    }

    public Integer getIdTipoDispositivo() {
        return idTipoDispositivo;
    }

    public void setIdTipoDispositivo(Integer idTipoDispositivo) {
        this.idTipoDispositivo = idTipoDispositivo;
    }

    public List<SelectItem> getListTipoDispositivo() {
        return listTipoDispositivo;
    }

    public void setListTipoDispositivo(List<SelectItem> listTipoDispositivo) {
        this.listTipoDispositivo = listTipoDispositivo;
    }

    public Integer getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(Integer idFilial) {
        this.idFilial = idFilial;
    }

    public List<SelectItem> getListFilial() {
        return listFilial;
    }

    public void setListFilial(List<SelectItem> listFilial) {
        this.listFilial = listFilial;
    }

}
