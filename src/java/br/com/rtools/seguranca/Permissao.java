package br.com.rtools.seguranca;

import br.com.rtools.seguranca.dao.PermissaoDao;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import javax.persistence.*;

@Entity
@Table(name = "seg_permissao")
@NamedQuery(name = "Permissao.pesquisaID", query = "select per from Permissao per where per.id=:pid")
public class Permissao implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JoinColumn(name = "id_modulo", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Modulo modulo;
    @JoinColumn(name = "id_rotina", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Rotina rotina;
    @JoinColumn(name = "id_evento", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Evento evento;

    public Permissao() {
        this.id = -1;
        this.modulo = new Modulo();
        this.rotina = new Rotina();
        this.evento = new Evento();
    }

    public Permissao(int id, Modulo modulo, Rotina rotina, Evento evento) {
        this.id = id;
        this.modulo = modulo;
        this.rotina = rotina;
        this.evento = evento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public Rotina getRotina() {
        return rotina;
    }

    public void setRotina(Rotina rotina) {
        this.rotina = rotina;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    // OPERAÇÕES
    public Boolean getInclusao() {
        if (this.id != -1) {
            return new PermissaoDao().pesquisaPermissaoModuloRotinaEvento(this.modulo.getId(), this.rotina.getId(), 1).getId() != -1;
        }
        return false;
    }

    public void setInclusao(Boolean inclusao) {
//        if (this.id != -1) {
//            Permissao p2 = new PermissaoDao().pesquisaPermissaoModuloRotinaEvento(this.modulo.getId(), this.rotina.getId(), 1);
//            if (p2.getId() == -1) {
//                if (new Dao().save(p2, true)) {
//                    GenericaMensagem.info("Sucesso", "Registro atualizado");
//                } else {
//                    GenericaMensagem.warn("Erro", "Ao atualizar registro! Possível causa: Permissão já vínculada / existe.");
//                }
//            } else if (new Dao().delete(p2, true)) {
//                GenericaMensagem.info("Sucesso", "Registro atualizado");
//            } else {
//                GenericaMensagem.warn("Erro", "Ao atualizar registro! Possível causa: Permissão já vínculada / existe.");
//            }
//        }
    }

    public Boolean getExclusao() {
        if (this.id != -1) {
            return new PermissaoDao().pesquisaPermissaoModuloRotinaEvento(this.modulo.getId(), this.rotina.getId(), 2).getId() != -1;
        }
        return false;
    }

    public void setExclusao(Boolean exclusao) {
//        if (this.id != -1) {
//            Permissao p2 = new PermissaoDao().pesquisaPermissaoModuloRotinaEvento(this.modulo.getId(), this.rotina.getId(), 2);
//            if (p2.getId() == -1) {
//                if (new Dao().save(p2, true)) {
//                    GenericaMensagem.info("Sucesso", "Registro atualizado");
//                } else {
//                    GenericaMensagem.warn("Erro", "Ao atualizar registro! Possível causa: Permissão já vínculada / existe.");
//                }
//            } else if (new Dao().delete(p2, true)) {
//                GenericaMensagem.info("Sucesso", "Registro atualizado");
//            } else {
//                GenericaMensagem.warn("Erro", "Ao atualizar registro! Possível causa: Permissão já vínculada / existe.");
//            }
//        }
    }

    public Boolean getAlteracao() {
        if (this.id != -1) {
            return new PermissaoDao().pesquisaPermissaoModuloRotinaEvento(this.modulo.getId(), this.rotina.getId(), 3).getId() != -1;
        }
        return false;
    }

    public void setAlteracao(Boolean alteracao) {
//        if (this.id != -1) {
//            Permissao p2 = new PermissaoDao().pesquisaPermissaoModuloRotinaEvento(this.modulo.getId(), this.rotina.getId(), 3);
//            if (p2.getId() == -1) {
//                if (new Dao().save(p2, true)) {
//                    GenericaMensagem.info("Sucesso", "Registro atualizado");
//                } else {
//                    GenericaMensagem.warn("Erro", "Ao atualizar registro! Possível causa: Permissão já vínculada / existe.");
//                }
//            } else if (new Dao().delete(p2, true)) {
//                GenericaMensagem.info("Sucesso", "Registro atualizado");
//            } else {
//                GenericaMensagem.warn("Erro", "Ao atualizar registro! Possível causa: Permissão já vínculada / existe.");
//            }
//        }
    }

    public Boolean getConsulta() {
        if (this.id != -1) {
            return new PermissaoDao().pesquisaPermissaoModuloRotinaEvento(this.modulo.getId(), this.rotina.getId(), 4).getId() != -1;
        }
        return false;
    }

    public void setConsulta(Boolean consulta) {
//        if (this.id != -1) {
//            Permissao p2 = new PermissaoDao().pesquisaPermissaoModuloRotinaEvento(this.modulo.getId(), this.rotina.getId(), 4);
//            if (p2.getId() == -1) {
//                if (new Dao().save(p2, true)) {
//                    GenericaMensagem.info("Sucesso", "Registro atualizado");
//                } else {
//                    GenericaMensagem.warn("Erro", "Ao atualizar registro! Possível causa: Permissão já vínculada / existe.");
//                }
//            } else if (new Dao().delete(p2, true)) {
//                GenericaMensagem.info("Sucesso", "Registro atualizado");
//            } else {
//                GenericaMensagem.warn("Erro", "Ao atualizar registro! Possível causa: Permissão já vínculada / existe.");
//            }
//        }
    }
}
