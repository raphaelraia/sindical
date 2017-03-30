package br.com.rtools.associativo.beans;

import br.com.rtools.associativo.Caravana;
import br.com.rtools.associativo.CaravanaReservas;
import br.com.rtools.associativo.beans.VendasCaravanaBean.ListaReservas;
import br.com.rtools.associativo.dao.CaravanaReservasDao;
import br.com.rtools.associativo.dao.PoltronasDao;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.sistema.BloqueioRotina;
import br.com.rtools.sistema.dao.BloqueioRotinaDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Jasper;
import br.com.rtools.utilitarios.SelectItemSort;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                boolean s = new Dao().delete(bloqueioRotina, true);
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

    public void print() {
        List<FichaReservas> listFichaReservas = new ArrayList();
        for (Reservas r : listReservas) {
            FichaReservas fichaReservas = new FichaReservas();
            fichaReservas.setLocal(caravana.getEvento().getDescricaoEvento().getDescricao());
            fichaReservas.setPeriodo("De " + caravana.getDataEmbarqueIda() + " à " + caravana.getDataEmbarqueRetorno());
            fichaReservas.setEntrada(caravana.getHoraEmbarqueIda());
            fichaReservas.setSaida(caravana.getHoraEmbarqueRetorno());
            fichaReservas.setResponsavel_nome(r.getCaravanaReservas().getVenda().getResponsavel().getNome());
            fichaReservas.setResponsavel_documento(r.getCaravanaReservas().getVenda().getResponsavel().getDocumento());
            fichaReservas.setDias(DataHoje.calculoDosDias(caravana.getDtEmbarqueIda(), caravana.getDtEmbarqueRetorno()));
            fichaReservas.setCategoria(r.getCaravanaReservas().getVenda().getResponsavel().getSocios().getMatriculaSocios().getCategoria().getCategoria());
            fichaReservas.setQuantidade_poltronas(caravana.getQuantidadePoltronas());
            Fisica passageiro = r.getCaravanaReservas().getPessoa().getFisica();
            fichaReservas.setPassageiro_nome(r.getCaravanaReservas().getPessoa().getNome());
            String documento = r.getCaravanaReservas().getPessoa().getDocumento();
            if (documento.isEmpty()) {
                documento = passageiro.getRg();
            }
            fichaReservas.setPoltrona(r.getCaravanaReservas().getPoltrona());
            fichaReservas.setPassageiro_documento(documento);
            fichaReservas.setSexo(passageiro.getSexo());
            fichaReservas.setNascimento(passageiro.getNascimento());
            fichaReservas.setIdade(passageiro.getIdade());
            fichaReservas.setObservacao(r.getCaravanaReservas().getVenda().getObservacao());
            listFichaReservas.add(fichaReservas);
        }
        List<Integer> select = new ArrayList();
        Reservas reserva = listReservas.get(0);
        select = new PoltronasDao().listaPoltronasUsadas(reserva.getCaravanaReservas().getVenda().getEvento().getId());
        String poltronas_disponiveis = "";
        if (!listReservas.isEmpty()) {
            boolean adc = true;
            String pol = "";
            String pol2 = "";
            int j = 0;
            for (int i = 1; i <= reserva.getCaravanaReservas().getVenda().getCaravana().getQuantidadePoltronas(); i++) {
                for (Integer select1 : select) {
                    if (i == select1) {
                        adc = false;
                        break;
                    }
                }
                if (adc) {
                    pol = "000" + i;
                    if (i < 10) {
                        pol2 = "0" + i;
                    } else {
                        pol2 = "" + i;
                    }
                    if (j == 0) {
                        poltronas_disponiveis += " | " + pol2 + " ";
                    } else {
                        poltronas_disponiveis += "| " + pol2 + " ";
                    }
                    j++;
                    if (j == 14) {
                        j = 0;
                        poltronas_disponiveis += "\n";
                    }
                }
                adc = true;
            }
            if (!poltronas_disponiveis.isEmpty()) {
                poltronas_disponiveis += "|";
            }
        }
        Jasper.IS_HEADER = true;
        Jasper.IS_HEADER_PARAMS = true;
        Jasper.FILIAL = (Filial) new Dao().find(new Filial(), 1);
        Map map = new HashMap();
        map.put("poltronas_disponiveis", poltronas_disponiveis);
        Jasper.printReports("CARAVANA_RESERVAS.jasper", "Ficha de reservas", listFichaReservas, map);
    }

    public class FichaReservas {

        private Object local;
        private Object periodo;
        private Object entrada;
        private Object saida;
        private Object dias;
        private Object quantidade_poltronas;
        private Object responsavel_nome;
        private Object responsavel_documento;
        private Object passageiro_nome;
        private Object passageiro_documento;
        private Object categoria;
        private Object sexo;
        private Object idade;
        private Object nascimento;
        private Object poltrona;
        private Object observacao;

        public FichaReservas() {
            this.local = null;
            this.periodo = null;
            this.entrada = null;
            this.saida = null;
            this.dias = null;
            this.quantidade_poltronas = null;
            this.responsavel_nome = null;
            this.responsavel_documento = null;
            this.passageiro_nome = null;
            this.passageiro_documento = null;
            this.categoria = null;
            this.sexo = null;
            this.idade = null;
            this.nascimento = null;
            this.poltrona = null;
            this.observacao = null;
        }

        public FichaReservas(Object local, Object periodo, Object entrada, Object saida, Object dias, Object quantidade_poltronas, Object responsavel_nome, Object responsavel_documento, Object passageiro_nome, Object passageiro_documento, Object categoria, Object sexo, Object idade, Object nascimento, Object poltrona, Object observacao) {
            this.local = local;
            this.periodo = periodo;
            this.entrada = entrada;
            this.saida = saida;
            this.dias = dias;
            this.quantidade_poltronas = quantidade_poltronas;
            this.responsavel_nome = responsavel_nome;
            this.responsavel_documento = responsavel_documento;
            this.passageiro_nome = passageiro_nome;
            this.passageiro_documento = passageiro_documento;
            this.categoria = categoria;
            this.sexo = sexo;
            this.idade = idade;
            this.nascimento = nascimento;
            this.poltrona = poltrona;
            this.observacao = observacao;
        }

        public Object getLocal() {
            return local;
        }

        public void setLocal(Object local) {
            this.local = local;
        }

        public Object getPeriodo() {
            return periodo;
        }

        public void setPeriodo(Object periodo) {
            this.periodo = periodo;
        }

        public Object getEntrada() {
            return entrada;
        }

        public void setEntrada(Object entrada) {
            this.entrada = entrada;
        }

        public Object getSaida() {
            return saida;
        }

        public void setSaida(Object saida) {
            this.saida = saida;
        }

        public Object getDias() {
            return dias;
        }

        public void setDias(Object dias) {
            this.dias = dias;
        }

        public Object getQuantidade_poltronas() {
            return quantidade_poltronas;
        }

        public void setQuantidade_poltronas(Object quantidade_poltronas) {
            this.quantidade_poltronas = quantidade_poltronas;
        }

        public Object getResponsavel_nome() {
            return responsavel_nome;
        }

        public void setResponsavel_nome(Object responsavel_nome) {
            this.responsavel_nome = responsavel_nome;
        }

        public Object getResponsavel_documento() {
            return responsavel_documento;
        }

        public void setResponsavel_documento(Object responsavel_documento) {
            this.responsavel_documento = responsavel_documento;
        }

        public Object getPassageiro_nome() {
            return passageiro_nome;
        }

        public void setPassageiro_nome(Object passageiro_nome) {
            this.passageiro_nome = passageiro_nome;
        }

        public Object getPassageiro_documento() {
            return passageiro_documento;
        }

        public void setPassageiro_documento(Object passageiro_documento) {
            this.passageiro_documento = passageiro_documento;
        }

        public Object getCategoria() {
            return categoria;
        }

        public void setCategoria(Object categoria) {
            this.categoria = categoria;
        }

        public Object getSexo() {
            return sexo;
        }

        public void setSexo(Object sexo) {
            this.sexo = sexo;
        }

        public Object getIdade() {
            return idade;
        }

        public void setIdade(Object idade) {
            this.idade = idade;
        }

        public Object getNascimento() {
            return nascimento;
        }

        public void setNascimento(Object nascimento) {
            this.nascimento = nascimento;
        }

        public Object getPoltrona() {
            return poltrona;
        }

        public void setPoltrona(Object poltrona) {
            this.poltrona = poltrona;
        }

        public Object getObservacao() {
            return observacao;
        }

        public void setObservacao(Object observacao) {
            this.observacao = observacao;
        }
    }

}
