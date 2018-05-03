package br.com.rtools.associativo.utils;

import br.com.rtools.associativo.ModeloCarteirinha;
import br.com.rtools.associativo.SocioCarteirinha;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.dao.SocioCarteirinhaDao;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class SocioCarteirinhaUtils {

    private String validadeDataInicial;
    private Integer validadeMeses;
    private Socios socios;
    private Pessoa pessoa;
    private ModeloCarteirinha modeloCarteirinha;
    private Integer categoria_id;
    private Integer rotina_id;

    public SocioCarteirinhaUtils() {
        validadeDataInicial = DataHoje.data();
        validadeMeses = 0;
        socios = null;
        pessoa = null;
        modeloCarteirinha = null;
        categoria_id = -1;
        rotina_id = 122;
    }

    public SocioCarteirinhaUtils(String validadeDataInicial, Integer validadeMeses, Socios socios, ModeloCarteirinha modeloCarteirinha, Integer categoria_id, Integer rotina_id) {
        this.validadeDataInicial = validadeDataInicial;
        this.validadeMeses = validadeMeses;
        this.socios = socios;
        this.modeloCarteirinha = modeloCarteirinha;
        this.categoria_id = categoria_id;
        this.rotina_id = rotina_id;
    }

    public SocioCarteirinhaUtils(String validadeDataInicial, Integer validadeMeses, Pessoa pessoa, ModeloCarteirinha modeloCarteirinha, Integer categoria_id, Integer rotina_id) {
        this.validadeDataInicial = validadeDataInicial;
        this.validadeMeses = validadeMeses;
        this.pessoa = pessoa;
        this.modeloCarteirinha = modeloCarteirinha;
        this.categoria_id = categoria_id;
        this.rotina_id = rotina_id;
    }

    /**
     * Grava uma carteirinha com modelo padrão
     *
     * @return
     */
    public boolean storeDefault() {
        Dao dao = new Dao();
        categoria_id = -1;
        rotina_id = 122;
        dao.openTransaction();
        return store(dao);
    }

    /**
     * Grava uma carteirinha com modelo padrão
     *
     * @param dao
     * @return
     */
    public boolean storeDefault(Dao dao) {
        categoria_id = -1;
        rotina_id = 122;
        return store(dao);
    }

    /**
     * Grava uma carteirinha, especificar modelo
     *
     * @return
     */
    public boolean store() {
        Dao dao = new Dao();
        dao.openTransaction();
        return store(dao, true);
    }

    /**
     * Grava uma carteirinha, especificar modelo
     *
     * @param dao
     * @return
     */
    public boolean store(Dao dao) {
        return store(dao, false);
    }

    public boolean store(Dao dao, Boolean auto_commit) {
        if (dao == null) {
            GenericaMensagem.warn("Erro", "NENHUMA TRANSAÇÃO ENCONTRADA!");
            return false;
        }

        if (pessoa == null && socios == null) {
            dao.rollback();
            return false;
        }

        String validadeCarteirinha = "";

        DataHoje dh = new DataHoje();
        SocioCarteirinha socioCarteirinha = new SocioCarteirinha();
        SocioCarteirinhaDao scd = new SocioCarteirinhaDao();
        // PESQUISA CARTEIRINHA SEM MODELO
        // String validadeCarteirinha = dh.incrementarMeses(validadeMeses, validadeDataInicial);

        if (socios != null && socios.getId() != -1) {
            validadeCarteirinha = dh.incrementarMeses(validadeMeses, validadeDataInicial);
            modeloCarteirinha = scd.pesquisaModeloCarteirinha(socios.getMatriculaSocios().getCategoria().getId(), rotina_id);
            pessoa = socios.getServicoPessoa().getPessoa();
        } else {
            validadeCarteirinha = dh.incrementarMeses(validadeMeses, validadeDataInicial);
        }
        ModeloCarteirinha mc;
        if (modeloCarteirinha == null) {
            mc = scd.pesquisaModeloCarteirinha(categoria_id, rotina_id);
        } else {
            mc = modeloCarteirinha;
        }
        if (mc == null) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "INFORMAR MODELO DA CARTEIRINHA!");
            return false;
        }
        // CRIA CARTEIRINHA CASO NÃO EXISTA
        SocioCarteirinha sc = scd.pesquisaCarteirinhaPessoa(pessoa.getId(), mc.getId());
        Boolean insert;
        if (sc == null || sc.getId() == -1) {
            socioCarteirinha.setDtEmissao(null);
            socioCarteirinha.setCartao(0);
            socioCarteirinha.setVia(1);
            socioCarteirinha.setValidadeCarteirinha(validadeCarteirinha);
            socioCarteirinha.setPessoa(pessoa);
            socioCarteirinha.setModeloCarteirinha(mc);
            insert = true;
        } else {
            if (socios == null || socios.getId() != -1) {
                if (!sc.isAtivo()) {
                    sc.setAtivo(true);
                }
                sc.setDtEmissao(null);
                sc.setVia(1);
                sc.setValidadeCarteirinha(validadeCarteirinha);
                if (!dao.update(sc)) {
                    dao.rollback();
                    GenericaMensagem.warn("Erro", "AO ATUALIZAR SÓCIO CARTEIRINHA!");
                    return false;
                }
            }
            if (auto_commit) {
                dao.commit();
            }
            return true;
        }

        if (!dao.save(socioCarteirinha)) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "AO ADICIONAR CARTEIRINHA!");
            return false;
        }
        if (insert) {
            socioCarteirinha.setCartao(socioCarteirinha.getId());
            if (!dao.update(socioCarteirinha)) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR SÓCIO CARTEIRINHA!");
                return false;
            }
        }
        if (auto_commit) {
            dao.commit();
        }
        return true;
    }

    /**
     * Deleta uma carteirinha com modelo padrão
     *
     * @return
     */
    public boolean inativeDefault() {
        Dao dao = new Dao();
        categoria_id = -1;
        rotina_id = 122;
        dao.openTransaction();
        return store(dao);
    }

    /**
     * Deleta uma carteirinha com modelo padrão
     *
     * @return
     */
    public boolean inative() {
        Dao dao = new Dao();
        categoria_id = -1;
        rotina_id = 122;
        dao.openTransaction();
        return update(dao, true);
    }

    public boolean inative(Dao dao) {
        return update(dao, false);
    }

    public boolean update(Dao dao, Boolean auto_commit) {
        if (dao == null) {
            GenericaMensagem.warn("Erro", "NENHUMA TRANSAÇÃO ENCONTRADA!");
            return false;
        }

        if (pessoa == null && socios == null) {
            dao.rollback();
            return false;
        }

        DataHoje dh = new DataHoje();
        SocioCarteirinha socioCarteirinha = new SocioCarteirinha();
        SocioCarteirinhaDao scd = new SocioCarteirinhaDao();
        if (socios != null && socios.getId() != -1) {
            modeloCarteirinha = scd.pesquisaModeloCarteirinha(socios.getMatriculaSocios().getCategoria().getId(), rotina_id);
            pessoa = socios.getServicoPessoa().getPessoa();
        }
        ModeloCarteirinha mc;
        if (modeloCarteirinha == null) {
            mc = scd.pesquisaModeloCarteirinha(categoria_id, rotina_id);
        } else {
            mc = modeloCarteirinha;
        }
        if (mc == null) {
            dao.rollback();
            GenericaMensagem.warn("Erro", "INFORMAR MODELO DA CARTEIRINHA!");
            return false;
        }
        // CRIA CARTEIRINHA CASO NÃO EXISTA
        SocioCarteirinha sc = scd.pesquisaCarteirinhaPessoa(pessoa.getId(), mc.getId());
        if (sc != null && sc.getId() != -1) {
            sc.setAtivo(false);
            sc.setDtEmissao(null);
            socioCarteirinha.setVia(1);
            socioCarteirinha.setPessoa(pessoa);
            socioCarteirinha.setModeloCarteirinha(mc);
            if (!dao.update(socioCarteirinha)) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "AO ATUALIZAR SÓCIO CARTEIRINHA!");
                return false;
            }
            if (auto_commit) {
                dao.commit();
            }
            return true;
        }
        if (auto_commit) {
            dao.commit();
        }
        return true;
    }

    public String getValidadeDataInicial() {
        return validadeDataInicial;
    }

    public void setValidadeDataInicial(String validadeDataInicial) {
        this.validadeDataInicial = validadeDataInicial;
    }

    public Integer getValidadeMeses() {
        return validadeMeses;
    }

    public void setValidadeMeses(Integer validadeMeses) {
        this.validadeMeses = validadeMeses;
    }

    public Socios getSocios() {
        return socios;
    }

    public void setSocios(Socios socios) {
        this.socios = socios;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public ModeloCarteirinha getModeloCarteirinha() {
        return modeloCarteirinha;
    }

    public void setModeloCarteirinha(ModeloCarteirinha modeloCarteirinha) {
        this.modeloCarteirinha = modeloCarteirinha;
    }

    public Integer getCategoria_id() {
        return categoria_id;
    }

    public void setCategoria_id(Integer categoria_id) {
        this.categoria_id = categoria_id;
    }

    public Integer getRotina_id() {
        return rotina_id;
    }

    public void setRotina_id(Integer rotina_id) {
        this.rotina_id = rotina_id;
    }

}
