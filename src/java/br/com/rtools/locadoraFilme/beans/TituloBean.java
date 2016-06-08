package br.com.rtools.locadoraFilme.beans;

import br.com.rtools.locadoraFilme.Catalogo;
import br.com.rtools.locadoraFilme.Genero;
import br.com.rtools.locadoraFilme.Titulo;
import br.com.rtools.locadoraFilme.dao.CatalogoDao;
import br.com.rtools.locadoraFilme.dao.TituloDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.dao.FilialDao;
import br.com.rtools.seguranca.MacFilial;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.ConfiguracaoUpload;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.Diretorio;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.SelectItemSort;
import br.com.rtools.utilitarios.Upload;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

@ManagedBean
@SessionScoped
public class TituloBean implements Serializable {

    private Titulo titulo;
    private Catalogo catalogo;
    private Usuario usuario;
    private String tipoPesquisa;
    private String descricaoPesquisa;
    private String porPesquisa;
    private String comoPesquisa;
    private Integer idGenero;
    private Integer idGeneroPesquisa;
    private List<SelectItem> listGenero;
    private List<Titulo> listTitulo;
    private String fotoPerfil;
    private String fotoArquivo;
    private String fotoTempPerfil;
    private String fotoTempArquivo;
    private String fileContent;
    private Part file;
    private String[] imagensTipo;
    private List<SelectItem> listFilial;
    private Integer idFilial;
    private List<Catalogo> listCatalogo;
    private Boolean habilitaGenero;
    private Integer faixaEtariaInicial;
    private Integer faixaEtariaFinal;
    private Boolean habilitaPesquisaFilial;

    @PostConstruct
    public void init() {
        titulo = new Titulo();
        catalogo = new Catalogo();
        usuario = new Usuario();
        descricaoPesquisa = "";
        tipoPesquisa = "titulo";
        porPesquisa = "descricao";
        comoPesquisa = "";
        idGenero = 0;
        listGenero = new ArrayList<>();
        listTitulo = new ArrayList<>();
        fotoPerfil = "";
        fotoArquivo = "";
        fotoTempPerfil = "";
        fotoTempArquivo = "";
        fileContent = "";
        file = null;
        imagensTipo = new String[]{"jpg", "jpeg", "png", "gif"};
        listFilial = new ArrayList();
        idFilial = 0;
        listCatalogo = new ArrayList<>();
        habilitaGenero = false;
        faixaEtariaInicial = 0;
        faixaEtariaFinal = 0;
        habilitaPesquisaFilial = false;
        if (GenericaSessao.exists("habilitaPesquisaFilial")) {
            habilitaPesquisaFilial = true;
            GenericaSessao.remove("habilitaPesquisaFilial");
        }
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("tituloPesquisa");
        clear();
    }

    public void clear() {
        GenericaSessao.remove("tituloBean");
    }

    public List<SelectItem> getListGenero() {
        if (listGenero.isEmpty()) {
            Dao dao = new Dao();
            List<Genero> list = (List<Genero>) dao.list(new Genero(), true);
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    idGenero = list.get(i).getId();
                    idGeneroPesquisa = null;
                }
                listGenero.add(new SelectItem(list.get(i).getId(), list.get(i).getDescricao()));
            }
        }
        return listGenero;
    }

    public synchronized void save() {
        if (listGenero.isEmpty()) {
            GenericaMensagem.warn("Validação", "Cadastrar gêneros!");
            return;
        }
        if (titulo.getDescricao().isEmpty()) {
            GenericaMensagem.warn("Validação", "Digite o nome do titulo!");
            return;
        }
        if (titulo.getIdadeMinima() < 0) {
            GenericaMensagem.warn("Validação", "Idade mínima deve ser maior ou igual a 0!");
            return;
        }
        if (titulo.getQtdePorEmbalagem() < 0) {
            GenericaMensagem.warn("Validação", "Quantidade por embalagem deve ser maior que 0!");
            return;
        }
        Dao dao = new Dao();
        titulo.setGenero((Genero) dao.find(new Genero(), idGenero));
        if (titulo.getDuracao().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar o tempo de duração!");
            return;
        }
        int hora = Integer.parseInt(titulo.getDuracao());
        if (hora <= 0) {
            GenericaMensagem.warn("Validação", "O tempo de duração deve ser superior a 0!");
            return;
        }
        int anoParametro = 1895;
        int ano = Integer.parseInt(titulo.getAnoLancamentoString());
        if (ano < anoParametro) {
            GenericaMensagem.warn("Validação", "O Ano de lançamento deve ser igual ou superior a 1895!");
            return;
        }
        NovoLog novoLog = new NovoLog();
        if (titulo.getId() == null) {
            TituloDao tituloDao = new TituloDao();
            if (tituloDao.exists(titulo.getDescricao())) {
                GenericaMensagem.warn("Validação", "Titulo já existe!");
                return;
            }
            if (dao.save(titulo, true)) {
                GenericaMensagem.info("Sucesso", "Registro inserido");
                novoLog.save(""
                        + "ID: " + titulo.getId()
                        + " - Gênero: (" + titulo.getGenero().getId() + ") - " + titulo.getGenero().getDescricao()
                        + " - Título: " + titulo.getDescricao()
                        + " - Formato: " + titulo.getFormato()
                        + " - Legenda: " + titulo.getLegenda()
                        + " - Duração: " + titulo.getDuracao()
                        + " - Ano: " + titulo.getAnoLancamento()
                        + " - Autor: " + titulo.getAutor()
                );
                saveImage();
                if (titulo.getBarras().isEmpty()) {
                    titulo.setBarras("" + titulo.getId());
                    if (!new Dao().update(titulo, true)) {
                        titulo.setBarras(null);
                    }
                }
            } else {
                GenericaMensagem.warn("Erro", "Ao inserir registro!");
            }
        } else {
            Titulo t = (Titulo) dao.find(titulo);
            String beforeUpdate
                    = "ID: " + t.getId()
                    + " - Gênero: (" + t.getGenero().getId() + ") - " + t.getGenero().getDescricao()
                    + " - Título: " + t.getDescricao()
                    + " - Formato: " + t.getFormato()
                    + " - Legenda: " + t.getLegenda()
                    + " - Duração: " + t.getDuracao()
                    + " - Ano: " + t.getAnoLancamento()
                    + " - Autor: " + t.getAutor();
            if (dao.update(titulo, true)) {
                GenericaMensagem.info("Sucesso", "Registro atualizado");
                novoLog.update(beforeUpdate,
                        "ID: " + titulo.getId()
                        + " - Gênero: (" + titulo.getGenero().getId() + ") - " + titulo.getGenero().getDescricao()
                        + " - Título: " + titulo.getDescricao()
                        + " - Formato: " + titulo.getFormato()
                        + " - Legenda: " + titulo.getLegenda()
                        + " - Duração: " + titulo.getDuracao()
                        + " - Ano: " + titulo.getAnoLancamento()
                        + " - Autor: " + titulo.getAutor()
                );
                saveImage();
            } else {
                GenericaMensagem.warn("Erro", "Ao atualizar registro!");
            }
        }
    }

    public synchronized void delete() {
        if (titulo.getId() != null) {
            Dao dao = new Dao();
            NovoLog novoLog = new NovoLog();
            if (dao.delete(titulo, true)) {
                GenericaMensagem.info("Sucesso", "Registro removido");
                novoLog.delete(""
                        + "ID: " + titulo.getId()
                        + " - Gênero: (" + titulo.getGenero().getId() + ") - " + titulo.getGenero().getDescricao()
                        + " - Título: " + titulo.getDescricao()
                        + " - Formato: " + titulo.getFormato()
                        + " - Legenda: " + titulo.getLegenda()
                        + " - Duração: " + titulo.getDuracao()
                        + " - Ano: " + titulo.getAnoLancamento()
                        + " - Autor: " + titulo.getAutor()
                );
                deleteImage();
                clear();
            } else {
                GenericaMensagem.warn("Erro", "Ao remover registro!");
            }
        } else {
            GenericaMensagem.warn("Validação", "Nenhum registro selecionado!");
        }
    }

    public void edit(Catalogo c) {
        catalogo = c;
        listFilial.add(new SelectItem(c.getFilial().getFilial().getPessoa().getNome()));
        idFilial = c.getFilial().getId();
    }

    public String edit(Titulo t) {
        String urlRetorno = (String) GenericaSessao.getString("urlRetorno");
        GenericaSessao.put("linkClicado", true);
        showImagem();
        listFilial.clear();
        listCatalogo.clear();
        idFilial = null;
        if (!GenericaSessao.exists("urlRetorno")) {
            titulo = t;
            idGenero = t.getGenero().getId();
            GenericaSessao.put("tituloPesquisa", titulo);
            return "titulo";
        } else {
            if (urlRetorno.equals("titulo")) {
                idGenero = t.getGenero().getId();
            } else if (getQuantidadeDisponivel(t.getId()) == 0) {
                GenericaMensagem.warn("Validação", "Não há quantidade disponível para locação!");
                return null;
            }
            titulo = t;
            GenericaSessao.put("tituloPesquisa", titulo);
            return (String) GenericaSessao.getString("urlRetorno");
        }
    }

    public synchronized void add() {
        CatalogoDao catalogoDao = new CatalogoDao();
        if (catalogo.getQuantidade() <= 0) {
            GenericaMensagem.warn("Validação", "Quantidade deve ser maior que 0!");
            return;
        }
        if (listFilial.isEmpty()) {
            GenericaMensagem.warn("Validação", "Cadastrar filiais!");
            return;
        }
        if (titulo.getId() == null) {
            GenericaMensagem.warn("Validação", "Cadastrar titulo!");
            return;
        }
        Dao dao = new Dao();
        catalogo.setFilial((Filial) dao.find(new Filial(), idFilial));
        catalogo.setTitulo(titulo);
        NovoLog novoLog = new NovoLog();
        if (catalogo.getId() == null) {
            if (!catalogoDao.verificaFilial(catalogo.getFilial(), catalogo.getTitulo())) {
                GenericaMensagem.warn("Validação", "Já existe esse catálogo para essa filial!");
                return;
            }
            if (dao.save(catalogo, true)) {
                GenericaMensagem.info("Sucesso", "Registro inserido");
                novoLog.save(""
                        + "ID: " + catalogo.getId()
                        + " - Filial: (" + catalogo.getFilial().getId() + ") - " + catalogo.getFilial().getFilial().getPessoa().getNome()
                        + " - Título: (" + catalogo.getTitulo().getId() + ") - " + catalogo.getTitulo().getDescricao()
                        + " - Quantidade: " + catalogo.getQuantidade()
                );
                listCatalogo.clear();
                catalogo = new Catalogo();
                listFilial.clear();
            } else {
                GenericaMensagem.warn("Erro", "Ao inserir registro!");
            }
        } else {
            Catalogo c = (Catalogo) dao.find(catalogo);
            String beforeUpdate = ""
                    + "ID: " + c.getId()
                    + " - Filial: (" + c.getFilial().getId() + ") - " + c.getFilial().getFilial().getPessoa().getNome()
                    + " - Título: (" + c.getTitulo().getId() + ") - " + c.getTitulo().getDescricao()
                    + " - Quantidade: " + c.getQuantidade();
            if (dao.update(catalogo, true)) {
                GenericaMensagem.info("Sucesso", "Registro atualizado");
                novoLog.update(beforeUpdate,
                        "ID: " + catalogo.getId()
                        + " - Filial: (" + catalogo.getFilial().getId() + ") - " + catalogo.getFilial().getFilial().getPessoa().getNome()
                        + " - Título: (" + catalogo.getTitulo().getId() + ") - " + catalogo.getTitulo().getDescricao()
                        + " - Quantidade: " + catalogo.getQuantidade()
                );
                catalogo = new Catalogo();
                listCatalogo.clear();
                listFilial.clear();
            } else {
                GenericaMensagem.warn("Erro", "Ao atualizar registro!");
            }
        }
    }

    public synchronized void remove(Catalogo c) {
        if (c.getId() != null) {
            Dao dao = new Dao();
            NovoLog novoLog = new NovoLog();
            if (dao.delete(c, true)) {
                GenericaMensagem.info("Sucesso", "Registro removido");
                novoLog.delete(""
                        + "ID: " + c.getId()
                        + " - Filial: (" + c.getFilial().getId() + ") - " + c.getFilial().getFilial().getPessoa().getNome()
                        + " - Título: (" + c.getTitulo().getId() + ") - " + c.getTitulo().getDescricao()
                        + " - Quantidade: " + c.getQuantidade()
                );
                catalogo = new Catalogo();
                listCatalogo.clear();
                listFilial.clear();
            } else {
                GenericaMensagem.warn("Erro", "Ao remover registro!");
            }
        } else {
            GenericaMensagem.warn("Validação", "Nenhum registro selecionado!");
        }
    }

    public void acaoPesquisaInicial() {
        listTitulo.clear();
        comoPesquisa = "I";
        find();
    }

    public void acaoPesquisaParcial() {
        listTitulo.clear();
        comoPesquisa = "P";
        find();
    }

    public void find() {
        TituloDao tituloDao = new TituloDao();
        if (GenericaSessao.exists("titulosNotIn")) {
            tituloDao.setNot_in(GenericaSessao.getString("titulosNotIn", true));
        }
        if (descricaoPesquisa.equals("")) {
            listTitulo = new ArrayList();
        } else {
            Integer gerero_id = null;
            if (habilitaGenero) {
                gerero_id = idGeneroPesquisa;
            }
            if (habilitaPesquisaFilial) {
                MacFilial mf = MacFilial.getAcessoFilial();
                if (mf != null && mf.getId() != -1) {
                    listTitulo = tituloDao.find(mf.getFilial().getId(), porPesquisa, comoPesquisa, descricaoPesquisa, gerero_id, faixaEtariaInicial, faixaEtariaFinal);
                } else {
                    listTitulo = tituloDao.find(porPesquisa, comoPesquisa, descricaoPesquisa, gerero_id, faixaEtariaInicial, faixaEtariaFinal);
                }
            } else {
                listTitulo = tituloDao.find(porPesquisa, comoPesquisa, descricaoPesquisa, gerero_id, faixaEtariaInicial, faixaEtariaFinal);
            }
        }
    }

    public List<Titulo> getListTitulo() {
        return listTitulo;
    }

    public String validaHora(String hora) {
        int n1 = 0;
        int n2 = 0;
        if (hora.length() == 1) {
            hora = "0" + hora + ":00";
        }

        if (hora.length() == 2) {
            if ((Integer.parseInt(hora) >= 0) && (Integer.parseInt(hora) <= 23)) {
                hora = hora + ":00";
            } else {
                hora = "";
            }
        } else if (hora.length() == 3) {
            n1 = Integer.parseInt(hora.substring(0, 2));
            String pontos = hora.substring(2, 3);

            if (((n1 >= 0) && (n1 <= 23)) && pontos.equals(":")) {
                hora = hora + "00";
            } else {
                hora = "";
            }
        } else if (hora.length() == 4) {
            n1 = Integer.parseInt(hora.substring(0, 2));
            n2 = Integer.parseInt(hora.substring(3, 4));
            String pontos = hora.substring(2, 3);

            if ((pontos.equals(":")) && ((n1 >= 0) && (n1 <= 23)) && ((n2 >= 0) && (n2 <= 5))) {
                hora = hora + "0";
            } else {
                hora = "";
            }
        } else if (hora.length() == 5) {
            n1 = Integer.parseInt(hora.substring(0, 2));
            n2 = Integer.parseInt(hora.substring(3, 5));
            String pontos = hora.substring(2, 3);

            if (!(((n1 >= 0) && (n1 <= 23)) && ((n2 >= 0) && (n2 <= 59)) && (pontos.equals(":")))) {
                hora = "";
            }
        }
        return hora;
    }

    public void validaDuracao() {
        this.titulo.setDuracao(this.validaHora(this.titulo.getDuracao()));
    }

    public Titulo getTitulo() {
        return titulo;
    }

    public void setTitulo(Titulo titulo) {
        this.titulo = titulo;
    }

    public String getComoPesquisa() {
        return comoPesquisa;
    }

    public void setComoPesquisa(String comoPesquisa) {
        this.comoPesquisa = comoPesquisa;
    }

    public String getDescricaoPesquisa() {
        return descricaoPesquisa;
    }

    public void setDescricaoPesquisa(String descricaoPesquisa) {
        this.descricaoPesquisa = descricaoPesquisa;
    }

    public void setListGenero(List<SelectItem> listGenero) {
        this.listGenero = listGenero;
    }

    public Integer getIdGenero() {
        return idGenero;
    }

    public void setIdGenero(Integer idGenero) {
        this.idGenero = idGenero;
    }

    public void setListTitulo(List<Titulo> listTitulo) {
        this.listTitulo = listTitulo;
    }

    public String getPorPesquisa() {
        return porPesquisa;
    }

    public void setPorPesquisa(String porPesquisa) {
        this.porPesquisa = porPesquisa;
    }

    public void upload(FileUploadEvent event) {
        String fotoTempCaminho = "locadora/titulo/" + getUsuario().getId();
        File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/temp/" + fotoTempCaminho + "/titulo.png"));
        if (f.exists()) {
            boolean delete = f.delete();
        } else {
            fotoTempPerfil = "";
        }
        ConfiguracaoUpload cu = new ConfiguracaoUpload();
        cu.setArquivo(event.getFile().getFileName());
        cu.setDiretorio("temp/locadora/titulo/" + getUsuario().getId());
        cu.setArquivo("titulo.png");
        cu.setSubstituir(true);
        cu.setRenomear("titulo.png");
        cu.setEvent(event);
        if (Upload.enviar(cu, true)) {
            fotoTempPerfil = "/Cliente/" + getCliente() + "/temp/" + fotoTempCaminho + "/titulo.png";
            fotoPerfil = "";
        } else {
            fotoTempPerfil = "";
            fotoPerfil = "";
        }
        PF.update("form_titulo:");

    }

    public void upload() {
        try {
            fileContent = new Scanner(file.getInputStream()).useDelimiter("\\A").next();
        } catch (IOException e) {
            // Error handling
        }
    }

    public void saveImage() {
        if (!Diretorio.criar("Imagens/locadora/titulo")) {
            return;
        }
        String arquivo = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/Imagens/locadora/titulo");
        boolean error = false;
        if (!fotoTempPerfil.equals("")) {
            File des = new File(arquivo + "/" + titulo.getId() + ".png");
            if (des.exists()) {
                des.delete();
            }
            File src = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(fotoTempPerfil));
            boolean rename = src.renameTo(des);
            fotoPerfil = "/Cliente/" + getCliente() + "/Imagens/locadora/titulo/" + titulo.getId() + ".png";
            fotoTempPerfil = "";

            if (!rename) {
                error = true;
            }
        }
        if (!error) {
            File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/temp/locadora/titulo/" + getUsuario().getId()));
            boolean delete = f.delete();
        }
        if (titulo.getFoto() == null) {
            Dao dao = new Dao();
            titulo.setFoto(new Date());
            dao.update(titulo, true);
        }
    }

    public void deleteImage() {
        boolean sucesso = false;
        File f;
        if (!fotoTempPerfil.equals("")) {
            f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/temp/locadora/titulo/" + getUsuario().getId() + "/titulo.png"));
            sucesso = f.delete();
        } else if (titulo.getId() != null) {
            f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/Imagens/locadora/titulo/" + titulo.getId() + ".png"));
            sucesso = f.delete();
        }
        if (sucesso) {
            fotoTempPerfil = "";
            fotoPerfil = "";
            RequestContext.getCurrentInstance().update(":form_titulo");
            if (titulo.getFoto() == null) {
                Dao dao = new Dao();
                titulo.setFoto(null);
                dao.delete(titulo, true);
            }
        }
    }

    public Usuario getUsuario() {
        if (GenericaSessao.exists("sessaoUsuario")) {
            usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        }
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getFotoArquivo() {
        return fotoArquivo;
    }

    public void setFotoArquivo(String fotoArquivo) {
        this.fotoArquivo = fotoArquivo;
    }

    public String getFotoTempPerfil() {
        return fotoTempPerfil;
    }

    public void setFotoTempPerfil(String fotoTempPerfil) {
        this.fotoTempPerfil = fotoTempPerfil;
    }

    public String getFotoTempArquivo() {
        return fotoTempArquivo;
    }

    public void setFotoTempArquivo(String fotoTempArquivo) {
        this.fotoTempArquivo = fotoTempArquivo;
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public String getCliente() {
        if (GenericaSessao.exists("sessaoCliente")) {
            return GenericaSessao.getString("sessaoCliente");
        }
        return "";
    }

    public void showImagem() {
        showImagem("");
    }

    public String showImagem(String imageName) {
        String fotoMemoria = "";
        String caminhoTemp = "/Cliente/" + getCliente() + "/Imagens/locadora/titulo";
        String arquivo = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(caminhoTemp);
        for (String imagensTipo1 : imagensTipo) {
            File f;
            if (imageName.isEmpty()) {
                f = new File(arquivo + "/" + titulo.getId() + "." + imagensTipo1);
            } else {
                f = new File(arquivo + "/" + imageName + "." + imagensTipo1);
            }
            if (f.exists()) {
                if (imageName.isEmpty()) {
                    fotoPerfil = caminhoTemp + "/" + titulo.getId() + "." + imagensTipo1;
                } else {
                    fotoMemoria = caminhoTemp + "/" + imageName + "." + imagensTipo1;
                }
                fotoTempPerfil = "";
                break;
            } else {
                fotoPerfil = "";
                fotoTempPerfil = "";
            }
        }
        return fotoMemoria;
    }

    public List<SelectItem> getListFilial() {
        if (titulo.getId() != null) {
            if (listFilial.isEmpty()) {
                FilialDao filialDao = new FilialDao();
                List<Filial> list = filialDao.findNotInByTabela("loc_titulo_filial", "id_titulo", "" + titulo.getId());

                for (int i = 0; i < list.size(); i++) {
                    if (i == 0) {
                        idFilial = list.get(i).getId();
                    }
                    listFilial.add(new SelectItem(list.get(i).getId(), list.get(i).getFilial().getPessoa().getNome()));
                }
                SelectItemSort.sort(listFilial);
            }
        }
        return listFilial;
    }

    public void setListFilial(List<SelectItem> listFilial) {
        this.listFilial = listFilial;
    }

    public Integer getIdFilial() {
        return idFilial;
    }

    public void setIdFilial(Integer idFilial) {
        this.idFilial = idFilial;
    }

    public Catalogo getCatalogo() {
        return catalogo;
    }

    public void setCatalogo(Catalogo catalogo) {
        this.catalogo = catalogo;
    }

    public List<Catalogo> getListCatalogo() {
        if (titulo.getId() != null) {
            if (listCatalogo.isEmpty()) {
                CatalogoDao catalogoDao = new CatalogoDao();
                listCatalogo = catalogoDao.findByTitulo(titulo.getId());
            }
        }
        return listCatalogo;
    }

    public void setListCatalogo(List<Catalogo> listCatalogo) {
        this.listCatalogo = listCatalogo;
    }

    public String getTipoPesquisa() {
        return tipoPesquisa;
    }

    public void setTipoPesquisa(String tipoPesquisa) {
        this.tipoPesquisa = tipoPesquisa;
    }

    public Integer getIdGeneroPesquisa() {
        return idGeneroPesquisa;
    }

    public void setIdGeneroPesquisa(Integer idGeneroPesquisa) {
        this.idGeneroPesquisa = idGeneroPesquisa;
    }

    public Boolean getHabilitaGenero() {
        return habilitaGenero;
    }

    public void setHabilitaGenero(Boolean habilitaGenero) {
        this.habilitaGenero = habilitaGenero;
    }

    public Integer getFaixaEtariaInicial() {
        return faixaEtariaInicial;
    }

    public void setFaixaEtariaInicial(Integer faixaEtariaInicial) {
        this.faixaEtariaInicial = faixaEtariaInicial;
    }

    public Integer getFaixaEtariaFinal() {
        return faixaEtariaFinal;
    }

    public void setFaixaEtariaFinal(Integer faixaEtariaFinal) {
        this.faixaEtariaFinal = faixaEtariaFinal;
    }

    public Boolean getHabilitaPesquisaFilial() {
        return habilitaPesquisaFilial;
    }

    public void setHabilitaPesquisaFilial(Boolean habilitaPesquisaFilial) {
        this.habilitaPesquisaFilial = habilitaPesquisaFilial;
    }

    public void listener(Integer tcase) {
        switch (tcase) {
            case 1:
                if(titulo.getId() == null) {
                    Titulo t;
                    if (habilitaPesquisaFilial) {
                        t = new TituloDao().findBarras(idFilial, titulo.getBarras());
                    } else {
                        t = new TituloDao().findBarras(null, titulo.getBarras());
                    }
                    if (t != null) {
                        if (!t.getBarras().equals(titulo.getBarras())) {
                            titulo.setBarras(null);
                        }
                        titulo = t;
                        listCatalogo.clear();
                        getListCatalogo();
                    }
                }
                break;
        }
    }

    public Integer getQuantidadeEstoqueFilial(Integer titulo_id) {
        return getQuantidadeEstoqueFilial(MacFilial.getAcessoFilial().getFilial().getId(), titulo_id);
    }

    public Integer getQuantidadeEstoqueFilial(Integer filial_id, Integer titulo_id) {
        Catalogo c = new CatalogoDao().find(filial_id, titulo_id);
        if (c == null) {
            return 0;
        }
        return c.getQuantidade();
    }

    public Integer getQuantidadeDisponivel(Integer titulo_id) {
        return getQuantidadeDisponivel(MacFilial.getAcessoFilial().getFilial().getId(), titulo_id);
    }

    public Integer getQuantidadeDisponivel(Integer filial_id, Integer titulo_id) {
        return new TituloDao().locadoraQuantidadeTituloDisponivel(filial_id, titulo_id);
    }

}
