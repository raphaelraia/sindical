package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.Caravana;
import br.com.rtools.associativo.CaravanaReservas;
import br.com.rtools.associativo.dao.CaravanaReservasDao;
import br.com.rtools.associativo.dao.PoltronasDao;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.BloqueioRotina;
import br.com.rtools.sistema.dao.BloqueioRotinaDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.SelectItemSort;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class OrganizarCaravanaBean implements Serializable {

    private Caravana caravana;
    private List<Reservas> listReservas;
    private BloqueioRotina bloqueioRotina;
    private Usuario usuario;

    @PostConstruct
    public void init() {
        caravana = new Caravana();
        listReservas = new ArrayList();
        bloqueioRotina = null;
        usuario = Usuario.getUsuario();
        new BloqueioRotinaDao().liberaRotinaBloqueada(142);
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("organizarCaravanaBean");
    }

    public BloqueioRotina getBloqueioRotina() {
        return bloqueioRotina;
    }

    public void setBloqueioRotina(BloqueioRotina bloqueioRotina) {
        this.bloqueioRotina = bloqueioRotina;
    }

    public void lock() {
        Dao dao = new Dao();
        BloqueioRotinaDao bloqueioRotinaDao = new BloqueioRotinaDao();
        bloqueioRotina = bloqueioRotinaDao.existRotinaCodigo(142, caravana.getId());
        if (bloqueioRotina == null) {
            bloqueioRotina = new BloqueioRotina();
            bloqueioRotina.setUsuario(usuario);
            bloqueioRotina.setRotina((Rotina) dao.find(new Rotina(), 142));
            bloqueioRotina.setPessoa(null);
            bloqueioRotina.setBloqueio(DataHoje.dataHoje());
            bloqueioRotina.setCodigo(caravana.getId());
            dao.save(bloqueioRotina, true);
        } else if (bloqueioRotina.getUsuario().getId() != ((Usuario) GenericaSessao.getObject("sessaoUsuario")).getId()) {
            GenericaMensagem.warn("Sistema", "Evento bloqueado para organizar poltronas: " + bloqueioRotina.getUsuario().getPessoa().getNome());
        }

    }

    public void unlock() {
        if (bloqueioRotina != null) {
            if (bloqueioRotina.getId() != -1) {
                Dao dao = new Dao();
                boolean s = dao.delete(bloqueioRotina, true);
                if (s) {
                    bloqueioRotina = null;
                }
                GenericaSessao.remove("organizarCaravanaBean");
            }
        }
    }

    public boolean isLocked() {
        if (bloqueioRotina != null) {
            if (bloqueioRotina.getId() != -1) {
                // GenericaMensagem.warn("Sistema", "Evento bloqueado para organizar poltronas: " + bloqueioRotina.getUsuario().getPessoa().getNome());
                return true;
            }
        }
        return false;
    }

    public Caravana getCaravana() {
        if (GenericaSessao.exists("caravanaPesquisa")) {
            caravana = (Caravana) GenericaSessao.getObject("caravanaPesquisa", true);
            bloqueioRotina = null;
            bloqueioRotina = new BloqueioRotinaDao().existRotinaCodigo(142, caravana.getId());
            if (bloqueioRotina == null) {
                lock();
            } else {
                GenericaMensagem.warn("Sistema", "Evento bloqueado para organizar poltronas: " + bloqueioRotina.getUsuario().getPessoa().getNome());
            }
            loadListaReservas();
        }
        return caravana;
    }

    public void setCaravana(Caravana caravana) {
        this.caravana = caravana;
    }

    public void loadListaReservas() {
        listReservas = new ArrayList();
        List<CaravanaReservas> list = new CaravanaReservasDao().findByCaravana(caravana.getId(), true);
        for (int i = 0; i < list.size(); i++) {
            listReservas.add(new Reservas(list.get(i), list.get(i).getPoltrona(), new ArrayList()));
        }
    }

    public List<Reservas> getListReservas() {
        return listReservas;
    }

    public void setListReservas(List<Reservas> listReservas) {
        this.listReservas = listReservas;
    }

    public void update() {
        for (int i = 0; i < listReservas.size(); i++) {
            new Dao().update(listReservas.get(i).getCaravanaReservas(), true);
        }
        loadListaReservas();
    }

    public class Reservas {

        private CaravanaReservas caravanaReservas;
        private Integer poltrona;
        private List<SelectItem> listPoltrona;

        public Reservas() {
            this.caravanaReservas = null;
            this.poltrona = null;
            this.listPoltrona = new ArrayList();
        }

        public Reservas(CaravanaReservas caravanaReservas, Integer poltrona, List<SelectItem> listPoltrona) {
            this.caravanaReservas = caravanaReservas;
            this.poltrona = poltrona;
            this.listPoltrona = listPoltrona;
        }

        public CaravanaReservas getCaravanaReservas() {
            return caravanaReservas;
        }

        public void setCaravanaReservas(CaravanaReservas caravanaReservas) {
            this.caravanaReservas = caravanaReservas;
        }

        public Integer getPoltrona() {
            return poltrona;
        }

        public void setPoltrona(Integer poltrona) {
            this.poltrona = poltrona;
        }

        public List<SelectItem> getListPoltrona() {
            if (listPoltrona.isEmpty()) {
                List<Integer> select = new PoltronasDao().listaPoltronasUsadas(caravana.getEvento().getId());
                boolean adc = true;
                String pol;
                if (poltrona != 0) {
                    listPoltrona.add(new SelectItem(poltrona, (poltrona < 10 ? ("0" + poltrona) : (poltrona + ""))));
                }
                for (int i = 1; i <= caravana.getQuantidadePoltronas(); i++) {
                    for (Integer select1 : select) {
                        if (i == select1) {
                            adc = false;
                            break;
                        }
                    }
                    if (adc) {
                        pol = "000" + i;
                        listPoltrona.add(new SelectItem(i, pol.substring(pol.length() - 2, pol.length())));
                    }
                    adc = true;
                }
                SelectItemSort.sort(listPoltrona);
            }
            return listPoltrona;
        }

        public void setListPoltrona(List<SelectItem> listPoltrona) {
            this.listPoltrona = listPoltrona;
        }

    }

}
