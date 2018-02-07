package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.dao.ServicoCategoriaDao;
import br.com.rtools.associativo.Categoria;
import br.com.rtools.associativo.GrupoCategoria;
import br.com.rtools.associativo.Parentesco;
import br.com.rtools.associativo.ServicoCategoria;
import br.com.rtools.financeiro.ServicoRotina;
import br.com.rtools.financeiro.Servicos;
import br.com.rtools.financeiro.dao.ServicoRotinaDao;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.model.SelectItem;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class CategoriaBean implements Serializable {

    private Categoria categoria;
    private Integer idGrupoCategoria;
    private Integer idServico;
    private List<Categoria> listCategoria;
    private List<SelectItem> listGrupoCategoria;
    private List<SelectItem> listServicos;
    private boolean limpar;
    private List list;
    private List<ObjectServicoCategoria> listObjectServicoCategoria;

    @PostConstruct
    public void init() {
        categoria = new Categoria();
        idGrupoCategoria = null;
        listObjectServicoCategoria = new ArrayList();
        listCategoria = new ArrayList();
        listGrupoCategoria = new ArrayList();
        limpar = false;
        list = new ArrayList();
        listServicos = new ArrayList();
        loadListServicos();
        loadListGrupoCategoria();
        loadListCategoria();
        loadListOSC();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("categoriaBean");
        GenericaSessao.remove("pesquisaCategoria");
    }

    public void save() {
        if (categoria.getCategoria().isEmpty()) {
            GenericaMensagem.warn("Validação", "Digite uma categoria!");
            return;
        }
        ServicoCategoria servicoCategoria = new ServicoCategoria();
        Dao dao = new Dao();
        if (idServico == null) {
            categoria.setServicoTaxaMatricula(null);
        } else {
            categoria.setServicoTaxaMatricula((Servicos) dao.find(new Servicos(), idServico));
            if (categoria.getNrTaxaMatriculaParcelas() < 1) {
                GenericaMensagem.warn("Validação", "Digite o numero de parcelas para essa taxa maior que 0 (zero)!");
                return;
            }
        }
        NovoLog novoLog = new NovoLog();
        if (categoria.getId() == -1) {
            categoria.setGrupoCategoria((GrupoCategoria) dao.find(new GrupoCategoria(), idGrupoCategoria));
            if (dao.save(categoria, true)) {
                novoLog.save(
                        "ID: " + categoria.getId()
                        + " - Categoria: " + categoria.getCategoria()
                        + " - Grupo Categoria: (" + categoria.getGrupoCategoria().getId() + ") " + categoria.getGrupoCategoria().getGrupoCategoria()
                        + " - Carência Balcão: " + categoria.getNrCarenciaBalcao()
                        + " - Desc. Folha: " + categoria.getNrCarenciaDescFolha()
                        + " - Empresa Obrigatória: " + categoria.isEmpresaObrigatoria()
                        + " - Dias: [Dom][" + categoria.isUsaClubeDomingo() + "]"
                        + "[Seg][" + categoria.isUsaClubeSegunda() + "]"
                        + "[Ter][" + categoria.isUsaClubeTerca() + "]"
                        + "[Qua][" + categoria.isUsaClubeQuarta() + "]"
                        + "[Qui][" + categoria.isUsaClubeQuinta() + "]"
                        + "[Sex][" + categoria.isUsaClubeSexta() + "]"
                        + "[Sab][" + categoria.isUsaClubeSabado() + "]"
                );
                GenericaMensagem.info("Sucesso", "Registro inserido");
                limpar = false;
            } else {
                GenericaMensagem.warn("Erro", "Ao inserir registro");
            }
//            for (int i = 0; i < list.size(); i++) {
//                if (Integer.parseInt(listServicos.get(Integer.parseInt((String) ((DataObject) list.get(i)).getArgumento1())).getDescription()) != -1) {
//                    servicoCategoria.setCategoria(categoria);
//                    servicoCategoria.setParentesco((Parentesco) ((DataObject) list.get(i)).getArgumento0());
//                    servicoCategoria.setServicos((Servicos) new Dao().find(new Servicos(), Integer.parseInt(
//                            listServicos.get(Integer.parseInt((String) ((DataObject) list.get(i)).getArgumento1())).getDescription())));
//                    dao.save(servicoCategoria, true);
//                    servicoCategoria = new ServicoCategoria();
//                }
//            }
            for (int i = 0; i < listObjectServicoCategoria.size(); i++) {
                if (listObjectServicoCategoria.get(i).getIdServico2() != null) {
                    servicoCategoria.setCategoria(categoria);
                    servicoCategoria.setParentesco(listObjectServicoCategoria.get(i).getParentesco());
                    servicoCategoria.setServicos((Servicos) new Dao().find(new Servicos(), listObjectServicoCategoria.get(i).getIdServico2()));
                    dao.save(servicoCategoria, true);
                    servicoCategoria = new ServicoCategoria();
                }
            }
            loadListOSC();
        } else {
            categoria.setGrupoCategoria((GrupoCategoria) dao.find(new GrupoCategoria(), idGrupoCategoria));
            Categoria c = (Categoria) dao.find(new Categoria(), categoria.getId());
            String beforeUpdate
                    = "ID: " + c.getId()
                    + " - Categoria: " + c.getCategoria()
                    + " - Grupo Categoria: (" + c.getGrupoCategoria().getId() + ") " + c.getGrupoCategoria().getGrupoCategoria()
                    + " - Carência Balcão: " + c.getNrCarenciaBalcao()
                    + " - Desc. Folha: " + c.getNrCarenciaDescFolha()
                    + " - Empresa Obrigatória: " + c.isEmpresaObrigatoria()
                    + " - Dias: [Dom][" + c.isUsaClubeDomingo() + "]"
                    + "[Seg][" + c.isUsaClubeSegunda() + "]"
                    + "[Ter][" + c.isUsaClubeTerca() + "]"
                    + "[Qua][" + c.isUsaClubeQuarta() + "]"
                    + "[Qui][" + c.isUsaClubeQuinta() + "]"
                    + "[Sex][" + c.isUsaClubeSexta() + "]"
                    + "[Sab][" + c.isUsaClubeSabado() + "]";
            if (dao.update(categoria, true)) {
                novoLog.update(beforeUpdate,
                        "ID: " + categoria.getId()
                        + " - Categoria: " + categoria.getCategoria()
                        + " - Grupo Categoria: (" + categoria.getGrupoCategoria().getId() + ") " + categoria.getGrupoCategoria().getGrupoCategoria()
                        + " - Carência Balcão: " + categoria.getNrCarenciaBalcao()
                        + " - Desc. Folha: " + categoria.getNrCarenciaDescFolha()
                        + " - Empresa Obrigatória: " + categoria.isEmpresaObrigatoria()
                        + " - Dias: [Dom][" + categoria.isUsaClubeDomingo() + "]"
                        + "[Seg][" + categoria.isUsaClubeSegunda() + "]"
                        + "[Ter][" + categoria.isUsaClubeTerca() + "]"
                        + "[Qua][" + categoria.isUsaClubeQuarta() + "]"
                        + "[Qui][" + categoria.isUsaClubeQuinta() + "]"
                        + "[Sex][" + categoria.isUsaClubeSexta() + "]"
                        + "[Sab][" + categoria.isUsaClubeSabado() + "]"
                );
                GenericaMensagem.info("Sucesso", "Registro atualizado");
            } else {
                GenericaMensagem.warn("Erro", "Ao atualizar registro");
            }
            for (int i = 0; i < listObjectServicoCategoria.size(); i++) {
                servicoCategoria = (ServicoCategoria) listObjectServicoCategoria.get(i).getServicoCategoria();
                if (servicoCategoria == null) {
                    servicoCategoria = new ServicoCategoria();
                }
                if (listObjectServicoCategoria.get(i).getIdServico2() == null) {
                    if (servicoCategoria.getId() != -1) {
                        dao.delete(servicoCategoria, true);
                    }
                } else {
                    servicoCategoria.setServicos((Servicos) new Dao().find(new Servicos(), listObjectServicoCategoria.get(i).getIdServico2()));
                    servicoCategoria.setCategoria(categoria);
                    servicoCategoria.setParentesco((Parentesco) listObjectServicoCategoria.get(i).getParentesco());
                    if (servicoCategoria.getId() == -1) {
                        dao.save(servicoCategoria, true);
                    } else {
                        dao.update(servicoCategoria, true);
                    }
                }
            }
            loadListOSC();
        }
        loadListCategoria();
    }

    public void delete() {
        if (categoria.getId() == -1) {
            return;
        }
        NovoLog novoLog = new NovoLog();
        Dao dao = new Dao();
        dao.openTransaction();
        for (int i = 0; i < listObjectServicoCategoria.size(); i++) {
            if (listObjectServicoCategoria.get(i).getServicoCategoria() != null && listObjectServicoCategoria.get(i).getServicoCategoria().getId() != -1) {
                if (!dao.delete(listObjectServicoCategoria.get(i).getServicoCategoria())) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "Ao remover serviço categoria!");
                    return;
                }

            }
        }
        if (dao.delete(categoria)) {
            dao.commit();
            novoLog.delete(
                    "ID: " + categoria.getId()
                    + " - Categoria: " + categoria.getCategoria()
                    + " - Grupo Categoria: (" + categoria.getGrupoCategoria().getId() + ") " + categoria.getGrupoCategoria().getGrupoCategoria()
                    + " - Carência Balcão: " + categoria.getNrCarenciaBalcao()
                    + " - Desc. Folha: " + categoria.getNrCarenciaDescFolha()
                    + " - Empresa Obrigatória: " + categoria.isEmpresaObrigatoria()
                    + " - Dias: [Dom][" + categoria.isUsaClubeDomingo() + "]"
                    + "[Seg][" + categoria.isUsaClubeSegunda() + "]"
                    + "[Ter][" + categoria.isUsaClubeTerca() + "]"
                    + "[Qua][" + categoria.isUsaClubeQuarta() + "]"
                    + "[Qui][" + categoria.isUsaClubeQuinta() + "]"
                    + "[Sex][" + categoria.isUsaClubeSexta() + "]"
                    + "[Sab][" + categoria.isUsaClubeSabado() + "]"
            );
            limpar = true;
            GenericaMensagem.info("Sucesso", "Registro excluído");
        } else {
            dao.rollback();
            GenericaMensagem.warn("Erro", "Ao excluir registro");
        }
        idServico = null;
        loadListGrupoCategoria();
        loadListCategoria();
        loadListServicos();
        categoria = new Categoria();
        loadListOSC();
    }

    public void clear() {
        clear(1);
    }

    public void clear(int tcase) {
        if (tcase == 1) {
            GenericaSessao.remove("categoriaBean");
        }
    }

    public String edit(Categoria c) {
        categoria = c;
        GenericaSessao.put("pesquisaCategoria", categoria);
        GenericaSessao.put("linkClicado", true);
        list.clear();
        idGrupoCategoria = c.getGrupoCategoria().getId();
        idServico = null;
        if (c.getServicoTaxaMatricula() != null) {
            idServico = c.getServicoTaxaMatricula().getId();
        }
        loadListOSC();
        return (String) GenericaSessao.getString("urlRetorno");
    }

    public void updateServicos(Integer servico_id) {
//        if (servico_id == null) {
//            return;
//        }
//        if (((Parentesco) ((DataObject) list.get(index)).getArgumento0()).getId() == 1) {
//            for (int i = 0; i < list.size(); i++) {
//                if (Integer.parseInt(String.valueOf(((DataObject) list.get(i)).getArgumento1())) == 0) {
//                    ((DataObject) list.get(i)).setArgumento1(Integer.parseInt(String.valueOf(((DataObject) list.get(index)).getArgumento1())));
//                }
//            }
//        }
    }

    public void updateServicos(ObjectServicoCategoria osc) {
        if (osc.getIdServico2() == null) {
            if (osc.getServicoCategoria().getId() == -1) {

            } else {

            }
        } else {

        }
    }

    public List<Categoria> getListCategoria() {
        return listCategoria;
    }

    public List<SelectItem> getListGrupoCategoria() {
        return listGrupoCategoria;
    }

    public void loadListCategoria() {
        listCategoria = new Dao().list(new Categoria(), true);
    }

    public void loadListGrupoCategoria() {
        listGrupoCategoria = new ArrayList();
        idGrupoCategoria = null;
        Dao dao = new Dao();
        List<GrupoCategoria> listGC = (List<GrupoCategoria>) dao.list(new GrupoCategoria(), true);
        for (int i = 0; i < listGC.size(); i++) {
            if (i == 0) {
                idGrupoCategoria = listGC.get(i).getId();
            }
            listGrupoCategoria.add(new SelectItem(listGC.get(i).getId(), listGC.get(i).getGrupoCategoria()));
        }
    }

    public void setListGrupoCategoria(List<SelectItem> listGrupoCategoria) {
        this.listGrupoCategoria = listGrupoCategoria;
    }

    public List<SelectItem> getListServicos() {
        return listServicos;
    }

    public void setListServicos(List<SelectItem> listServicos) {
        this.listServicos = listServicos;
    }

    public List getListParentescos() {
        if (list.isEmpty()) {
            DataObject dtObj = null;
            ServicoCategoriaDao dbSeC = new ServicoCategoriaDao();
            List<ServicoCategoria> listaSerCat = dbSeC.pesquisaServCatPorId(categoria.getId());
            List<Parentesco> listaPar = new Dao().list(new Parentesco(), true);
            if (listaSerCat.isEmpty()) {
                for (int i = 0; i < listaPar.size(); i++) {
                    dtObj = new DataObject(listaPar.get(i), 0, new ServicoCategoria(), null, null, null);
                    list.add(dtObj);
                }
            } else {
                int index = 0;
                boolean temServico = false;
                for (int i = 0; i < listaPar.size(); i++) {
                    for (int x = 0; x < listaSerCat.size(); x++) {
                        if (Objects.equals(listaPar.get(i).getId(), listaSerCat.get(x).getParentesco().getId())) {
                            for (int w = 0; w < listServicos.size(); w++) {
                                if (listServicos.get(w).getValue() != null) {
                                    if (listaSerCat.get(x).getServicos().getId() == (Integer.parseInt(listServicos.get(w).getValue().toString()))) {
                                        index = w;
                                        temServico = true;
                                        break;
                                    }
                                }
                            }
                            dtObj = new DataObject(listaPar.get(i), index, listaSerCat.get(x), null, null, null);
                            break;
                        }
                    }
                    if (!temServico) {
                        dtObj = new DataObject(listaPar.get(i), index, new ServicoCategoria(), null, null, null);
                    }
                    temServico = false;
                    index = 0;
                    list.add(dtObj);
                }
            }
        }
        return list;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Integer getIdGrupoCategoria() {
        return idGrupoCategoria;
    }

    public void setIdGrupoCategoria(Integer idGrupoCategoria) {
        this.idGrupoCategoria = idGrupoCategoria;
    }

    public void setListCategoria(List<Categoria> listCategoria) {
        this.listCategoria = listCategoria;
    }

    public boolean isLimpar() {
        return limpar;
    }

    public void setLimpar(boolean limpar) {
        this.limpar = limpar;
    }

    public void semanaToda() {
        categoria.setUsaClubeDomingo(true);
        categoria.setUsaClubeSegunda(true);
        categoria.setUsaClubeTerca(true);
        categoria.setUsaClubeQuarta(true);
        categoria.setUsaClubeQuinta(true);
        categoria.setUsaClubeSexta(true);
        categoria.setUsaClubeSabado(true);
    }

    public void loadListServicos() {
        listServicos = new ArrayList();
        List<ServicoRotina> listSR = new ServicoRotinaDao().findAllByRotina(new Rotina().get().getId());
        idServico = null;
        listServicos.add(new SelectItem(null, " -- NENHUM -- ", "-1"));
        for (int i = 0; i < listSR.size(); i++) {
            listServicos.add(new SelectItem(listSR.get(i).getServicos().getId(), listSR.get(i).getServicos().getDescricao()));
        }

    }

    public Integer getIdServico() {
        return idServico;
    }

    public void setIdServico(Integer idServico) {
        this.idServico = idServico;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public void loadListOSC() {
        listObjectServicoCategoria = new ArrayList();
        ServicoCategoriaDao scd = new ServicoCategoriaDao();
        List<Parentesco> listParentesco = new Dao().list(new Parentesco(), true);

        for (int i = 0; i < listParentesco.size(); i++) {
            ServicoCategoria sc = null;
            if (categoria.getId() != -1) {
                sc = scd.find(listParentesco.get(i).getId(), categoria.getId());
            }
            if (sc == null) {
                listObjectServicoCategoria.add(new ObjectServicoCategoria(listParentesco.get(i), null, null));
            } else {
                listObjectServicoCategoria.add(new ObjectServicoCategoria(listParentesco.get(i), sc, sc.getServicos().getId()));
            }
        }

//        List<ServicoCategoria> listaSerCat = dbSeC.pesquisaServCatPorId(categoria.getId());
//        if (listaSerCat.isEmpty()) {
//            for (int i = 0; i < listaPar.size(); i++) {
//            }
//        } else {
//            Integer servico_id = null;
//            boolean temServico = false;
//            for (int i = 0; i < listaPar.size(); i++) {
//                for (int x = 0; x < listaSerCat.size(); x++) {
//                    if (Objects.equals(listaPar.get(i).getId(), listaSerCat.get(x).getParentesco().getId())) {
//                        for (int w = 0; w < listServicos.size(); w++) {
//                            if (listServicos.get(w).getValue() != null) {
//                                if (listaSerCat.get(x).getServicos().getId() == (Integer.parseInt(listServicos.get(w).getValue().toString()))) {
//                                    servico_id = listaSerCat.get(x).getServicos().getId();
//                                    temServico = true;
//                                    break;
//                                }
//                            }
//                        }
//                        listObjectServicoCategoria.add(new ObjectServicoCategoria(listaPar.get(i), listaSerCat.get(x), servico_id));
//                        break;
//                    }
//                }
//                if (!temServico) {
//                    listObjectServicoCategoria.add(new ObjectServicoCategoria(listaPar.get(i), new ServicoCategoria(), servico_id));
//                }
//                temServico = false;
//                servico_id = null;
//            }
//        }
    }

    public List<ObjectServicoCategoria> getListObjectServicoCategoria() {
        return listObjectServicoCategoria;
    }

    public void setListObjectServicoCategoria(List<ObjectServicoCategoria> listObjectServicoCategoria) {
        this.listObjectServicoCategoria = listObjectServicoCategoria;
    }

    public class ObjectServicoCategoria {

        private Parentesco parentesco;
        private ServicoCategoria servicoCategoria;
        private Integer idServico2;

        public ObjectServicoCategoria() {
            this.parentesco = null;
            this.servicoCategoria = new ServicoCategoria();
            this.idServico2 = null;
        }

        public ObjectServicoCategoria(Parentesco parentesco, ServicoCategoria servicoCategoria, Integer idServico2) {
            this.servicoCategoria = servicoCategoria;
            this.idServico2 = idServico2;
            this.parentesco = parentesco;
        }

        public ServicoCategoria getServicoCategoria() {
            return servicoCategoria;
        }

        public void setServicoCategoria(ServicoCategoria servicoCategoria) {
            this.servicoCategoria = servicoCategoria;
        }

        public Integer getIdServico2() {
            return idServico2;
        }

        public void setIdServico2(Integer idServico2) {
            this.idServico2 = idServico2;
        }

        public Parentesco getParentesco() {
            return parentesco;
        }

        public void setParentesco(Parentesco parentesco) {
            this.parentesco = parentesco;
        }

    }
}
