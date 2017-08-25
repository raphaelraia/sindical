package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.Categoria;
import br.com.rtools.associativo.ModeloCarteirinha;
import br.com.rtools.associativo.ModeloCarteirinhaCategoria;
import br.com.rtools.associativo.dao.ModeloCarteirinhaCategoriaDao;
import br.com.rtools.associativo.dao.SocioCarteirinhaDao;
import br.com.rtools.associativo.lista.ListModeloCarterinhaCategoria;
import br.com.rtools.impressao.CartaoSocial;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.ConfiguracaoUpload;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.Diretorio;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import static br.com.rtools.utilitarios.ImpressaoParaSocios.getConverteNullString;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.Upload;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.primefaces.event.FileUploadEvent;

@ManagedBean
@SessionScoped
public class ModeloCarteirinhaBean {

    private ModeloCarteirinha modeloCarteirinha;
    private Map<Integer, Integer> categoria_id;
    private Map<Integer, Integer> rotina_id;
    private List<ModeloCarteirinha> listModeloCarteirinha;
    private List<ModeloCarteirinhaCategoria> listModeloCarteirinhaCategoria;
    private List<ListModeloCarterinhaCategoria> listGeneric;

    @PostConstruct
    public void init() {
        modeloCarteirinha = new ModeloCarteirinha();
        categoria_id = new HashMap<>();
        rotina_id = new HashMap<>();
        listModeloCarteirinha = new ArrayList();
        listModeloCarteirinhaCategoria = new ArrayList();
        listGeneric = new ArrayList();
        loadListModeloCarteirinha();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("modeloCarteirinhaBean");
    }

    public void save() {
        if (modeloCarteirinha.getDescricao().isEmpty()) {
            GenericaMensagem.warn("Erro", "Digite um nome para o Modelo!");
            return;
        }
        if (modeloCarteirinha.getJasper().isEmpty()) {
            GenericaMensagem.warn("Erro", "Digite o caminho do Jasper!");
            return;
        }
        if (modeloCarteirinha.getId() == -1) {
            if (new Dao().save(modeloCarteirinha, true)) {
                modeloCarteirinha.setFoto(null);
                GenericaMensagem.info("Sucesso", "Registro inserido");
                loadListModeloCarteirinha();
                loadListModeloCarteirinhaCategoria();
            } else {
                GenericaMensagem.warn("Erro", "Ao adicionar registro!");
            }
        } else {
            if (new Dao().update(modeloCarteirinha, true)) {
                GenericaMensagem.info("Sucesso", "Registro atualizado");
                loadListModeloCarteirinha();
                loadListModeloCarteirinhaCategoria();
            } else {
                GenericaMensagem.warn("Erro", "Ao atualizar registro!");
            }
        }
    }

    public void saveModeloCarteirinhaCategoria() {
        saveModeloCarteirinhaCategoria(modeloCarteirinha);
    }

    public void saveModeloCarteirinhaCategoria(ModeloCarteirinha mc) {
        Dao dao = new Dao();
        ModeloCarteirinhaCategoria mcc = new ModeloCarteirinhaCategoria();
        mcc.setModeloCarteirinha(mc);
        Integer mc_id = null;
        Integer pos = null;
        for (int i = 0; i < listGeneric.size(); i++) {
            if (listGeneric.get(i).getModelo_categoria_id() == mc.getId()) {
                if (listGeneric.get(i).getRotina_id() != null) {
                    mcc.setRotina((Rotina) dao.find(new Rotina(), listGeneric.get(i).getRotina_id()));
                }
                if (listGeneric.get(i).getCategoria_id() != null) {
                    mcc.setCategoria((Categoria) dao.find(new Categoria(), listGeneric.get(i).getCategoria_id()));
                }
                mc_id = listGeneric.get(i).getModelo_categoria_id();
                pos = i;
                break;
            }
        }
        ModeloCarteirinhaCategoria exists = new SocioCarteirinhaDao().pesquisaModeloCarteirinhaCategoria(mc.getId(), (mcc.getCategoria() == null) ? -1 : mcc.getCategoria().getId(), mcc.getRotina().getId());
        if (exists != null) {
            GenericaMensagem.warn("Validação", "Modelo já existe!");
            return;
        }
        if (new Dao().save(mcc, true)) {
            GenericaMensagem.info("Sucesso", "Registro adicionado");
            List<ModeloCarteirinhaCategoria> list = loadListModeloCarteirinhaCategoria(mc_id);
            if (!list.isEmpty()) {
                listGeneric.get(pos).setListMCC(new ArrayList());
                listGeneric.get(pos).setListMCC(list);
                listGeneric.get(pos).setListCategoria(new ArrayList());
                listGeneric.get(pos).setListCategoria(listCategoria(mc_id, mcc.getRotina().getId()));
            }
        } else {
            GenericaMensagem.warn("Erro", "Ao adicionar registro!");
        }
    }

    public void clear() {
        GenericaSessao.remove("modeloCarteirinhaBean");
    }

    public void deleteModeloCarteirinha(ModeloCarteirinha mc) {
        Dao dao = new Dao();
        for (int i = 0; i < listModeloCarteirinhaCategoria.size(); i++) {
            new Dao().delete(listModeloCarteirinhaCategoria.get(i), true);
        }
        if (!new Dao().delete(mc, true)) {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
            dao.rollback();
            return;
        }
        loadListModeloCarteirinha();
        Integer mc_id = null;
        Integer pos = null;
        for (int i = 0; i < listGeneric.size(); i++) {
            if (listGeneric.get(i).getModelo_categoria_id() == mc.getId()) {
                mc_id = listGeneric.get(i).getModelo_categoria_id();
                pos = i;
                break;
            }
        }
        List<ModeloCarteirinhaCategoria> list = loadListModeloCarteirinhaCategoria(mc_id);
        if (!list.isEmpty()) {
            listGeneric.get(pos).setListMCC(new ArrayList());
            listGeneric.get(pos).setListMCC(list);
        }
        GenericaMensagem.info("Sucesso", "Registro excluído!");
    }

    public void deleteModeloCarteirinhaCategoria(ModeloCarteirinhaCategoria mcc) {
        if (!new Dao().delete(mcc, true)) {
            GenericaMensagem.warn("Erro", "Ao remover registro!");
            return;
        }
        Integer mc_id = null;
        Integer pos = null;
        for (int i = 0; i < listGeneric.size(); i++) {
            if (listGeneric.get(i).getModelo_categoria_id() == mcc.getModeloCarteirinha().getId()) {
                mc_id = listGeneric.get(i).getModelo_categoria_id();
                pos = i;
                break;
            }
        }
        List<ModeloCarteirinhaCategoria> list = loadListModeloCarteirinhaCategoria(mc_id);
        if (!list.isEmpty()) {
            listGeneric.get(pos).setListMCC(new ArrayList());
            listGeneric.get(pos).setListMCC(list);
        } else {
            listGeneric.get(pos).setListMCC(new ArrayList());
        }
        listGeneric.get(pos).setListCategoria(new ArrayList());
        listGeneric.get(pos).setListCategoria(listCategoria(mc_id, mcc.getRotina().getId()));
        GenericaMensagem.info("Sucesso", "Registro excluído!");
    }

    public void edit(ModeloCarteirinha mc) {
        modeloCarteirinha = (ModeloCarteirinha) new Dao().rebind(mc);
        loadListModeloCarteirinhaCategoria();
    }

    public ModeloCarteirinha getModeloCarteirinha() {
        return modeloCarteirinha;
    }

    public void setModeloCarteirinha(ModeloCarteirinha modeloCarteirinha) {
        this.modeloCarteirinha = modeloCarteirinha;
    }

    public List<SelectItem> listCategoria(Integer modelo_carteirinha_id, Integer rotina_id) {
        List<SelectItem> listCategoria = new ArrayList();
        List<Categoria> list = new ModeloCarteirinhaCategoriaDao().findNotInCategoriaByMCC(modelo_carteirinha_id, rotina_id);
        if (!list.isEmpty()) {
            listCategoria.add(new SelectItem(null, "Sem Categoria"));
            for (int i = 0; i < list.size(); i++) {
                listCategoria.add(new SelectItem(list.get(i).getId(), list.get(i).getCategoria()));
            }
        } else {
            listCategoria.add(new SelectItem(0, "Nenhuma Categoria encontrada", "0"));
        }
        return listCategoria;
    }

    public List<SelectItem> listRotina(Integer modelo_carteirinha_id) {
        List<SelectItem> listRotina = new ArrayList();
        List<Rotina> list = new ArrayList<>();
        list.add((Rotina) new Dao().find(new Rotina(), 170));
        list.add((Rotina) new Dao().find(new Rotina(), 122));
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                listRotina.add(new SelectItem(list.get(i).getId(), list.get(i).getRotina()));
            }
        } else {
            listRotina.add(new SelectItem(0, "Nenhuma Rotina encontrada", "0"));
        }
        return listRotina;
    }

    public void loadListModeloCarteirinha() {
        listModeloCarteirinha = new ArrayList();
        listModeloCarteirinha = new Dao().list(new ModeloCarteirinha());
        for (int i = 0; i < listModeloCarteirinha.size(); i++) {
            ListModeloCarterinhaCategoria generic = new ListModeloCarterinhaCategoria();
            generic.setListRotina(listRotina(listModeloCarteirinha.get(i).getId()));
            for (int x = 0; x < generic.getListRotina().size(); i++) {
                generic.setRotina_id((Integer) generic.getListRotina().get(x).getValue());
                break;
            }
            generic.setListCategoria(listRotina(listModeloCarteirinha.get(i).getId()));
            for (int x = 0; x < generic.getListCategoria().size(); i++) {
                generic.setCategoria_id((Integer) generic.getListCategoria().get(x).getValue());
                break;
            }
            generic.setModelo_categoria_id(listModeloCarteirinha.get(i).getId());
            generic.setListCategoria(listCategoria(listModeloCarteirinha.get(i).getId(), generic.getRotina_id()));
            generic.setListMCC(loadListModeloCarteirinhaCategoria(listModeloCarteirinha.get(i).getId()));
            listGeneric.add(generic);
        }
    }

    public void loadListModeloCarteirinhaCategoria() {
        listModeloCarteirinhaCategoria = loadListModeloCarteirinhaCategoria(modeloCarteirinha.getId());
    }

    public List loadListModeloCarteirinhaCategoria(Integer modelo_carteirinha_id) {
        return new ModeloCarteirinhaCategoriaDao().findByModeloCarteirinha(modelo_carteirinha_id);

    }

    public Integer categoria_id(Integer modelo_carteirinha_id) {
        try {
            Integer c_id = null;
            for (Map.Entry<Integer, Integer> entry : categoria_id.entrySet()) {
                if (Objects.equals(modelo_carteirinha_id, entry.getKey())) {
                    c_id = entry.getValue();
                }
            }
            return c_id;
        } catch (Exception e) {
            return null;
        }
    }

    public Integer rotina_id(Integer modelo_carteirinha_id) {
        try {
            Integer c_id = null;
            for (Map.Entry<Integer, Integer> entry : rotina_id.entrySet()) {
                if (Objects.equals(modelo_carteirinha_id, entry.getKey())) {
                    c_id = entry.getValue();
                }
            }
            return c_id;
        } catch (Exception e) {
            return null;
        }
    }

    public List<ModeloCarteirinha> getListModeloCarteirinha() {
        return listModeloCarteirinha;
    }

    public void setListModeloCarteirinha(List<ModeloCarteirinha> listModeloCarteirinha) {
        this.listModeloCarteirinha = listModeloCarteirinha;
    }

    public List<ModeloCarteirinhaCategoria> getListModeloCarteirinhaCategoria() {
        return listModeloCarteirinhaCategoria;
    }

    public void setListModeloCarteirinhaCategoria(List<ModeloCarteirinhaCategoria> listModeloCarteirinhaCategoria) {
        this.listModeloCarteirinhaCategoria = listModeloCarteirinhaCategoria;
    }

    /**
     * Lista ModeloCarteirinhaCategoria
     *
     * @param modelo_carteirinha_id
     * @return
     */
    public List<ModeloCarteirinhaCategoria> listMCC(Integer modelo_carteirinha_id) {
        return loadListModeloCarteirinhaCategoria(modelo_carteirinha_id);
    }

    public List<ListModeloCarterinhaCategoria> getListGeneric() {
        return listGeneric;
    }

    public void setListGeneric(List<ListModeloCarterinhaCategoria> listGeneric) {
        this.listGeneric = listGeneric;
    }

    public void upload(FileUploadEvent event) {
        if (modeloCarteirinha.getId() != -1) {
            UUID uuidX = UUID.randomUUID();
            String uuid = uuidX.toString().replace("-", "_");
            if (modeloCarteirinha.getFoto() != null && !modeloCarteirinha.getFoto().isEmpty()) {
                try {
                    File file = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("Imagens/ModeloCarteirinha/" + modeloCarteirinha.getFoto()));
                    if (file.exists()) {
                        file.delete();
                    }
                } catch (Exception e) {

                }
            }
            ConfiguracaoUpload cu = new ConfiguracaoUpload();
            cu.setArquivo(event.getFile().getFileName());
            cu.setDiretorio("Imagens/ModeloCarteirinha");
            cu.setSubstituir(true);
            String extension = event.getFile().getContentType().replace("image/", "");
            if (!extension.equals("jpg") && !extension.equals("jpeg") && !extension.equals("gif") && !extension.equals("png")) {
                GenericaMensagem.warn("Erro Sistema", "Extensão inválida!");
                return;
            }
            cu.setRenomear(uuid + "." + extension);
            cu.setEvent(event);
            if (Upload.enviar(cu, true)) {
                modeloCarteirinha.setFoto(uuid + "." + extension);
                new Dao().update(modeloCarteirinha, true);
            };
        }
    }

    public void printModel(ModeloCarteirinha modeloCarteirinha) {

        String logoCartao = "";
        File file_img = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/cartao.jpg"));
        String caminho_img = "";

        if (file_img.exists()) {
            caminho_img = file_img.getPath();
        }
        String assinatura = "";
        File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/assinatura.jpg"));
        if (f.exists()) {
            assinatura = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/assinatura.jpg");
        }
        String cartaoVerso = "";
        File fileCartaoVerso = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/cartao_verso.jpg"));
        if (fileCartaoVerso.exists()) {
            cartaoVerso = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/cartao_verso.jpg");
        }
        try {
            if (new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png")).exists()) {
                logoCartao = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png");
            }
        } catch (Exception e) {

        }
        try {
            if (new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/logo_preto_branco.png")).exists()) {
                logoCartao = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/logo_preto_branco.png");
            }
        } catch (Exception e) {

        }
        String[] imagensTipo = new String[]{"jpg", "jpeg", "png", "gif"};
        File foto_cartao = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("") + "resources/images/.png");

        for (String imagensTipo1 : imagensTipo) {
            File test = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/user_undefined.png"));
            if (test.exists()) {
                foto_cartao = test;
                break;
            }
        }
        Collection list = new ArrayList();
        list.add(
                new CartaoSocial(
                        "0", //                                                         CODIGO
                        "000000000000", //                                                       BARRAS 
                        "NOME", //  NOME
                        "EMPRESA", //  EMPRESA
                        "00.000.000/0000-00", //  CNPJ
                        "01/01/1900", //  DATA ADMISSAO
                        "01/01/1900", //  DATA VALIDADE
                        "CIDADE", //  CIDADE
                        "UF", //  UF
                        logoCartao, // LOGO
                        foto_cartao.getAbsolutePath(), // CAMINHO FOTO
                        "01/01/1900", // FILIAÇÃO
                        "PROFISSAO", // PROFISSÃO
                        "000.000.000-00", // CPF
                        "00.000.000-X", // RG
                        1, //    ID_PESSOA
                        "DESCRICAO DO ENDEREO", //                                                     ENDERECO
                        "CIDADE", //                                                    CIDADE
                        "BRASILEIRO", // NACIONALIDADE
                        "01/01/1900", // NASCIMENTO
                        "SOLTEIRO", // ESTADO CIVIL
                        "000000", // CARTEIRA
                        "001", // SERIE
                        caminho_img, //                                                  IMAGEM FUNDO
                        "", //                                              CÓDIGO FUNCIONAL
                        "SSP", // ÓRGÃO EXPEDITOR
                        "PARENTESCO", // PARENTESCO
                        "CATEGORIA", // CATEGORIA
                        "SP", //  FANTASIA
                        "TITULAR NOME", //                                                      TITULAR
                        "DEPENDENTE NOME", //                                                   DEPENDENTE
                        "FANTASIA EMPRESA - TITULAR", // FANTASIA EMPRESA - TITULAR
                        "002", //  CÓDIGO FUNCIONAL - TITULAR
                        1, // TITULAR ID
                        "GRUPO CATEGORIA", // GRUPO CATEGORIA
                        ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/imagemExtra.png"), // IMAGEM EXTRA
                        ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/imagemExtra2.png"), // IMAGEM EXTRA 2
                        (!("01/01/1900").isEmpty()) ? "( APOSENTADO )" : "", // DATA APOSENTADORIA
                        "0",
                        new ArrayList(),
                        assinatura,
                        cartaoVerso,
                        new ArrayList(),
                        new ArrayList()
                )
        );
        try {
            Diretorio.criar("downloads/carteirinhas");
            List ljasper = new ArrayList();
            JasperReport jasper;
            JasperReport jasperVerso;
            String subreport = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/DEPENDENTES.jasper");

            Map map = new HashMap();
        
            String mimeType = "application/pdf";

            String caminho = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/" + modeloCarteirinha.getJasper());
            if (caminho == null) {
                GenericaMensagem.error("Erro jasper: " + modeloCarteirinha.getJasper(), "Modelo não encontrado na pasta Relatório!");
            }

            File file = new File(
                    ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/" + modeloCarteirinha.getJasper())
            );
            //* ADD LISTA DE JASPERS *//
            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(list);
            jasper = (JasperReport) JRLoader.loadObject(file);
            if (subreport != null) {
                map.put("template_dir", subreport);
            }

            ljasper.add(Jasper.fillObject(jasper, map, dtSource));
            try {
                File fileVerso = new File(
                        ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/CARTAO_VERSO.jasper")
                );
                if (fileVerso.exists()) {
                    //* ADD LISTA DE JASPERS *//
                    dtSource = new JRBeanCollectionDataSource(list);
                    jasperVerso = (JasperReport) JRLoader.loadObject(fileVerso);
                    ljasper.add(Jasper.fillObject(jasperVerso, map, dtSource));
                }
            } catch (JRException ev) {
                GenericaMensagem.warn("Erro", "Ao imprimir verso do cartão! " + ev.getMessage());
                return;
            }

            Jasper.load();
            Jasper.PART_NAME = "";
            Jasper.PATH = "downloads";
            Jasper.printReports("cartao_social", ljasper);
        } catch (JRException e) {

        }
    }
}
