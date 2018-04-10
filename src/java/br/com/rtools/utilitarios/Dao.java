package br.com.rtools.utilitarios;

import br.com.rtools.principal.DB;
import br.com.rtools.seguranca.Usuario;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import oracle.toplink.essentials.exceptions.DatabaseException;
import oracle.toplink.essentials.exceptions.EJBQLException;
import oracle.toplink.essentials.exceptions.TopLinkException;

public class Dao extends DB implements DaoInterface {

    /**
     * Mostra a exceção ocorrida
     */
    public static Exception EXCEPCION = null;

    /**
     * <p>
     * <strong>Open Transaction</strong></p>
     *
     * @author Bruno
     */
    @Override
    public void openTransaction() {
        getEntityManager().getTransaction().begin();
    }

    /**
     * <p>
     * <strong>Commit</strong></p>
     *
     * @author Bruno
     */
    @Override
    public void commit() {
        if (activeSession()) {
            try {
                getEntityManager().getTransaction().commit();
            } catch (Exception e) {
                EXCEPCION = e;
                // getEntityManager().getTransaction().setRollbackOnly();
                if (GenericaSessao.exists("habilitaLog")) {
                    if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                        GenericaMensagem.fatal("LOG", "Exceção gerada " + EXCEPCION.getMessage());
                        PF.update("header:form_log");
                    }
                }
            }
        } else {
            rollback();
        }
    }

    /**
     * <p>
     * <strong>Rollback</strong></p>
     *
     * @author Bruno
     */
    @Override
    public void rollback() {
        getEntityManager().getTransaction().rollback();
    }

    @Override
    public void flush() {
        getEntityManager().flush();
    }

    /**
     * <p>
     * <strong>Active Session</strong></p>
     *
     * @author Bruno
     *
     * @return true or false
     */
    @Override
    public boolean activeSession() {
        return getEntityManager().getTransaction().isActive();
    }

    /**
     * <p>
     * <strong>Save</strong></p>
     *
     * @param object
     *
     * @author Bruno
     *
     * @return true or false
     */
    @Override
    public boolean save(final Object object) {
        if (!activeSession()) {
            return false;
        }
        try {
            getEntityManager().persist(object);
            getEntityManager().flush();
            return true;
        } catch (Exception e) {
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            EXCEPCION = e;
            if (GenericaSessao.exists("habilitaLog")) {
                if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                    GenericaMensagem.fatal("LOG", "Exceção gerada " + EXCEPCION.getMessage());
                    PF.update("header:form_log");
                }
            }
            return false;
        }
    }

    /**
     * <p>
     * <strong>Save transaction automatic</strong></p>
     *
     * @param object
     * @param transactionComplete
     * @author Bruno
     *
     * @return true or false
     */
    @Override
    public boolean save(final Object object, boolean transactionComplete) {
        if (activeSession()) {
            return false;
        }
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().persist(object);
            getEntityManager().flush();
            getEntityManager().getTransaction().commit();
            return true;
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            EXCEPCION = e;
            if (GenericaSessao.exists("habilitaLog")) {
                if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                    GenericaMensagem.fatal("LOG", "Exceção gerada " + EXCEPCION.getMessage());
                    PF.update("header:form_log");
                }
            }
            return false;
        }
    }

    /**
     * <p>
     * <strong>Update</strong></p>
     *
     * @param objeto
     * @author Bruno
     *
     * @return true or false
     */
    @Override
    public boolean update(final Object objeto) {
        if (objeto == null) {
            return false;
        }
        if (!activeSession()) {
            return false;
        }

        Class classe = objeto.getClass();
        Integer id;
        try {
            Method metodo = classe.getMethod("getId", new Class[]{});
            id = (Integer) metodo.invoke(objeto, (Object[]) null);
            if (id == -1 || id == null) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Objeto esta passando -1");
                return false;
            }
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage());
            return false;
        }

        try {
            getEntityManager().merge(objeto);
            getEntityManager().flush();
            return true;
        } catch (EJBQLException e) {
            EXCEPCION = e;
            if (GenericaSessao.exists("habilitaLog")) {
                if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                    GenericaMensagem.fatal("LOG", "Exceção gerada " + EXCEPCION.getMessage());
                    PF.update("header:form_log");
                }
            }
        }
        return false;
    }

    /**
     * <p>
     * <strong>Updatetransaction automatic</strong></p>
     *
     * @param objeto
     * @param transactionComplete
     *
     * @author Bruno
     *
     * @return true or false
     */
    @Override
    public boolean update(final Object objeto, boolean transactionComplete) {
        if (objeto == null) {
            return false;
        }
        if (activeSession()) {
            return false;
        }
        Class classe = objeto.getClass();
        Integer id;
        try {
            Method metodo = classe.getMethod("getId", new Class[]{});
            id = (Integer) metodo.invoke(objeto, (Object[]) null);
            if (id == -1 || id == null) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Objeto esta passando -1");
                return false;
            }
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage());
            return false;
        }
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().merge(objeto);
            getEntityManager().flush();
            getEntityManager().getTransaction().commit();
            return true;
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            EXCEPCION = e;
            if (GenericaSessao.exists("habilitaLog")) {
                if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                    GenericaMensagem.fatal("LOG", "Exceção gerada " + EXCEPCION.getMessage());
                    PF.update("header:form_log");
                }
            }
            return false;
        }
    }

    /**
     * <p>
     * <strong>Delete</strong></p>
     *
     * @param object
     * @author Bruno
     *
     * @return true or false
     */
    @Override
    public boolean delete(final Object object) {
        if (!activeSession()) {
            return false;
        }
        try {
            getEntityManager().remove(find(object));
            getEntityManager().flush();
            return true;
        } catch (Exception e) {
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            EXCEPCION = e;
            if (GenericaSessao.exists("habilitaLog")) {
                if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                    GenericaMensagem.fatal("LOG", "Exceção gerada " + EXCEPCION.getMessage());
                    PF.update("header:form_log");
                }
            }
            return false;
        }
    }

    /**
     * <p>
     * <strong>Delete automatic</strong></p>
     *
     * @param object
     * @param transactionComplete
     *
     * @author Bruno
     *
     * @return true or false
     */
    @Override
    public boolean delete(final Object object, boolean transactionComplete) {
        if (activeSession()) {
            return false;
        }
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().remove(find(object));
            getEntityManager().flush();
            getEntityManager().getTransaction().commit();
            return true;
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            EXCEPCION = e;
            if (GenericaSessao.exists("habilitaLog")) {
                if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                    GenericaMensagem.fatal("LOG", "Exceção gerada " + EXCEPCION.getMessage());
                    PF.update("header:form_log");
                }
            }
            return false;
        }
    }

    /**
     * <p>
     * <strong>Delete Error Code</strong></p>
     *
     * @param object
     *
     * @author Bruno
     *
     * @return String code error
     */
    public ErrorCodeDao deleteErrorCode(final Object object) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().remove(find(object));
            getEntityManager().flush();
            getEntityManager().getTransaction().rollback();
            return null;
        } catch (DatabaseException e) {
            getEntityManager().getTransaction().rollback();
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            return ErrorCodeDao.databaseExceptionMessage(e);
        } catch (TopLinkException e) {
            getEntityManager().getTransaction().rollback();
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            return ErrorCodeDao.toplinkExceptionMessage(e);
        } catch (PersistenceException e) {
            getEntityManager().getTransaction().rollback();
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            return ErrorCodeDao.persistenceExceptionMessage(e);
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            return ErrorCodeDao.exceptionMessage(e);
        }
    }

    @Override
    public Object rebind(Object object) {
        if (object == null) {
            return object;
        }
        openTransaction();
        try {
            object = find(object);
            getEntityManager().merge(object);
            getEntityManager().refresh(object);
            getEntityManager().flush();
            commit();
        } catch (Exception e) {
            rollback();
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            EXCEPCION = e;
            if (GenericaSessao.exists("habilitaLog")) {
                if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                    GenericaMensagem.fatal("LOG", "Exceção gerada " + EXCEPCION.getMessage());
                    PF.update("header:form_log");
                }
            }
        }
        return object;
    }

    public Object rebindList(Object object) {
        openTransaction();
        try {
            getEntityManager().merge(object);
            getEntityManager().refresh(object);
            getEntityManager().flush();
            commit();
        } catch (Exception e) {
            rollback();
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            EXCEPCION = e;
            if (GenericaSessao.exists("habilitaLog")) {
                if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                    GenericaMensagem.fatal("LOG", "Exceção gerada " + EXCEPCION.getMessage());
                    PF.update("header:form_log");
                }
            }
        }
        return object;
    }

    @Override
    public void refresh(Object object) {
        if (object == null) {
            return;
        }
        try {
            openTransaction();
            object = find(object);
            getEntityManager().merge(object);
            getEntityManager().refresh(object);
            if (!getEntityManager().getTransaction().isActive()) {
                return;
            }
            getEntityManager().flush();
            commit();
        } catch (Exception e) {
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            EXCEPCION = e;
            if (GenericaSessao.exists("habilitaLog")) {
                if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                    GenericaMensagem.fatal("LOG", "Exceção gerada " + EXCEPCION.getMessage());
                    PF.update("header:form_log");
                }
            }
        }
    }

    public void refreshList(Object object) {
        try {
            openTransaction();
            getEntityManager().merge(object);
            getEntityManager().refresh(object);
            if (!getEntityManager().getTransaction().isActive()) {
                return;
            }
            getEntityManager().flush();
            commit();
        } catch (Exception e) {
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            EXCEPCION = e;
            if (GenericaSessao.exists("habilitaLog")) {
                if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                    GenericaMensagem.fatal("LOG", "Exceção gerada " + EXCEPCION.getMessage());
                    PF.update("header:form_log");
                }
            }
        }
    }

    /**
     * <p>
     * <strong>Find Object</strong></p>
     * <p>
     * <strong>Exemplo:</strong>User user = new User(1, "Paul"); find(user);</p>
     *
     * @param object (Nome do objeto String)
     *
     * @author Bruno
     *
     * @return Object
     */
    @Override
    public Object find(final Object object) {
        return find(object, null);
    }

    /**
     * <p>
     * <strong>Find Object</strong></p>
     * <p>
     * <strong>Exemplo:</strong>find("User" or new User(), objectId); </p>
     *
     * @param object (Nome do objeto String)
     * @param objectId (Id a ser pesquisado)
     *
     * @author Bruno
     *
     * @return Object
     */
    @Override
    public Object find(Object object, final Object objectId) {
        if (object == null) {
            return null;
        }

        if (objectId == null) {
            Integer id;
            try {
                Class classe = object.getClass();
                Method metodo = classe.getMethod("getId", new Class[]{});
                id = (Integer) metodo.invoke(object, (Object[]) null);
                if (id == -1 || id == null) {
                    return null;
                }
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
                return null;
            }
            object = getEntityManager().find(object.getClass(), id);
        } else {
            try {
                if (object.getClass().getSimpleName().equals("String")) {
                    List list = find(object.toString(), new int[]{Integer.parseInt(objectId.toString())});
                    if (!list.isEmpty()) {
                        object = list.get(0);
                    }
                } else {
                    object = getEntityManager().find(object.getClass(), objectId);
                }
            } catch (Exception e) {
                Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
                EXCEPCION = e;
                if (GenericaSessao.exists("habilitaLog")) {
                    if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                        GenericaMensagem.fatal("LOG", "Exceção gerada " + EXCEPCION.getMessage());
                        PF.update("header:form_log");
                    }
                }
                return null;
            }
        }
        return object;
    }

    /**
     * <p>
     * <strong>Find Object</strong></p>
     * <p>
     * <strong>Exemplo:</strong>find("User", new int[]{1,2,3,4,5}); </p>
     *
     * @param id (Lista de ids)
     * @param className (Nome da classe)
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List find(String className, int id[]) {
        return find(className, id, "", null);
    }

    /**
     * *
     *
     * @param className
     * @param id
     * @param field
     * @return
     */
    public Object find(String className, int id, String field) {
        List list = find(className, new int[]{id}, field, null);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    /**
     * <p>
     * <strong>Find Object</strong></p>
     * <p>
     * <strong>Exemplo:</strong>find("User", new int[]{1,2,3,4,5}, "id_people");
     * </p>
     *
     * @param id (Lista de ids)
     * @param className (Nome da classe)
     * @param field (Faz a consulta utilizando outro campo que não id como
     * parâmetro)
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List find(String className, int id[], String field) {
        return find(className, id, field, "");
    }

    /**
     * <p>
     * <strong>Find Object</strong></p>
     * <p>
     * <strong>Exemplo:</strong>find("User", new int[]{1,2,3,4,5}, "id_people");
     * </p>
     *
     * @param id (Lista de ids)
     * @param className (Nome da classe)
     * @param field (Faz a consulta utilizando outro campo que não id como
     * parâmetro)
     *
     * @author Bruno
     * @param order (Use Alias OB.column_name)
     *
     * @return List
     */
    public List find(String className, int id[], String field, String order) {
        String stringCampo = "id";
        if (field != null && !field.isEmpty()) {
            stringCampo = field;
        }
        if (order == null || order.isEmpty()) {
            order = "OB.id";
        } else {
            if (!order.contains("OB")) {
                order = "OB." + order;
            }
        }
        String queryPesquisaString = "";
        for (int i = 0; i < id.length; i++) {
            if (i == 0) {
                queryPesquisaString = Integer.toString(id[i]);
            } else {
                queryPesquisaString += ", " + Integer.toString(id[i]);
            }
        }
        String queryString = "SELECT OB FROM " + className + " OB WHERE OB." + stringCampo + " IN (" + queryPesquisaString + ") ORDER BY " + order;
        Query query = getEntityManager().createQuery(queryString);
        List list = query.getResultList();
        if (list.isEmpty()) {
            return list;
        }
        return list;
    }

    /**
     * <p>
     * <strong>List</strong></p>
     * <p>
     * <strong>Exemplo:</strong> list(new User()).</p>
     *
     * @param className (Nome do objeto String)
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List list(Object className) {
        String name = className.getClass().getSimpleName();
        return list(name);
    }

    /**
     * <p>
     * <strong>List</strong></p>
     * <p>
     * <strong>Exemplo:</strong> list("User").</p>
     *
     * @param className (Nome do objeto String)
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List list(String className) {
        List result = new ArrayList();
        String queryString = "SELECT OB FROM " + className + " AS OB";
        try {
            Query qry = getEntityManager().createQuery(queryString);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                result = qry.getResultList();
            }
        } catch (Exception e) {
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            EXCEPCION = e;
            if (GenericaSessao.exists("habilitaLog")) {
                if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                    GenericaMensagem.fatal("LOG", "Exceção gerada " + EXCEPCION.getMessage());
                    PF.update("header:form_log");
                }
            }
        }
        return result;
    }

    @Override
    public List list(Object className, boolean order) {
        return list(className.getClass().getSimpleName(), order);
    }

    /**
     * <p>
     * <strong>List</strong></p>
     * <p>
     * <strong>Exemplo:</strong> list("User", boolean (true or false)).</p>
     *
     * @param className (Nome do objeto String)
     * @param order [Se o resultado deve ser ordenado (Verificar se a namedQuery
     * esta na Classe/Entidade)]
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List list(String className, boolean order) {
        try {
            Query query = getEntityManager().createNamedQuery(className + ".findAll");
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            EXCEPCION = e;
            if (GenericaSessao.exists("habilitaLog")) {
                if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                    GenericaMensagem.fatal("LOG", "Exceção gerada " + EXCEPCION.getMessage());
                    PF.update("header:form_log");
                }
            }
            return new ArrayList();
        }
        return new ArrayList();
    }

    /**
     * <p>
     * <strong>List Query</strong></p>
     * <p>
     * <strong>Exemplo:</strong> E@NamedQuery(name = "Object.find", query =
     * "SELECT O FROM Object AS O WHERE O.id = :p1") Uso: listQuery(Object,
     * find, {1}) Exemplo 2 @NamedQuery(name = "Object.find", query = "SELECT O
     * FROM Object AS O WHERE O.id = :p1 AND O.description = :p2") Uso:
     * listQuery(Object, find, {1, 'Feliz'}).</p>
     *
     * @param className (Nome do objeto)
     * @param find (Nome da NamedQuery dentro do objeto)
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List listQuery(Object className, String find) {
        return listQuery(className.getClass().getSimpleName(), find, new Object[]{});
    }

    /**
     * <p>
     * <strong>List Query</strong></p>
     * <p>
     * <strong>Exemplo:</strong> E@NamedQuery(name = "Object.find", query =
     * "SELECT O FROM Object AS O WHERE O.id = :p1") Uso: listQuery(Object,
     * find, {1}) Exemplo 2 @NamedQuery(name = "Object.find", query = "SELECT O
     * FROM Object AS O WHERE O.id = :p1 AND O.description = :p2") Uso:
     * listQuery(Object, find, {1, 'Feliz'}).</p>
     *
     * @param className (Nome do objeto)
     * @param find (Nome da NamedQuery dentro do objeto)
     * @param params (Cria se parâmetros organizados para realizar a consulta)
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List listQuery(Object className, String find, Object[] params) {
        return listQuery(className.getClass().getSimpleName(), find, params);
    }

    /**
     * <p>
     * <strong>List Query</strong></p>
     * <p>
     * <strong>Exemplo:</strong> E@NamedQuery(name = "Object.find", query =
     * "SELECT O FROM Object AS O WHERE O.id = :p1") Uso: listQuery("Object",
     * find, {1}) Exemplo 2 @NamedQuery(name = "Object.find", query = "SELECT O
     * FROM Object AS O WHERE O.id = :p1 AND O.description = :p2") Uso:
     * listQuery(Object, find, {1, 'Feliz'}).</p>
     *
     * @param className (Nome do objeto)
     * @param find (Nome da NamedQuery dentro do objeto)
     * @param params (Cria se parâmetros organizados para realizar a consulta)
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List listQuery(String className, String find, Object[] params) {
        try {
            Query query = getEntityManager().createNamedQuery(className + "." + find);
            int y = 1;
            for (Object param : params) {
                if (Types.isInteger(param)) {
                    try {
                        query.setParameter("p" + y, Integer.parseInt((String) param));
                    } catch (Exception e) {
                        query.setParameter("p" + y, param);
                    }
                } else if (Types.isDouble(param)) {
                    query.setParameter("p" + y, Double.parseDouble((String) param));
                } else {
                    query.setParameter("p" + y, (String) param);
                }
                y++;
            }
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            EXCEPCION = e;
            if (GenericaSessao.exists("habilitaLog")) {
                if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                    GenericaMensagem.fatal("LOG", "Exceção gerada " + EXCEPCION.getMessage());
                    PF.update("header:form_log");
                }
            }
            return new ArrayList();
        }
        return new ArrayList();
    }

    /**
     * <p>
     * <strong>Live List</strong></p>
     * <p>
     * <strong>Exemplos:</strong>Exemplo 1: liveList(SELECT U FROM User AS U);
     * Set nativeQuery = true. Caso não encontre nenhum resultado retorna uma
     * lista vazia; Se houver algum erro retorna lista vazia;</p>
     *
     * @param queryString
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List liveList(String queryString) {
        return liveList(queryString, false, 0);
    }

    /**
     * <p>
     * <strong>Live List</strong></p>
     * <p>
     * <strong>Exemplos:</strong>Exemplo 1: liveList(SELECT U FROM User AS U);
     * Set nativeQuery = true Exemplo 2: liveList(select * from user as u,
     * true); Set maxResults = 5. Caso não encontre nenhum resultado retorna uma
     * lista vazia; Se houver algum erro retorna lista vazia;</p>
     *
     * @param queryString
     * @param nativeQuery
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List liveList(String queryString, boolean nativeQuery) {
        return liveList(queryString, nativeQuery, 0);
    }

    /**
     * <p>
     * <strong>Live List</strong></p>
     * <p>
     * <strong>Exemplos:</strong>Exemplo 1: liveList(SELECT U FROM User AS U);
     * Set nativeQuery = true Exemplo 2: liveList(select * from user as u,
     * true); Set maxResults = 5 Exemplo 3: liveList(SELECT U FROM User AS U,
     * true, 5) Caso não encontre nenhum resultado retorna uma lista vazia; Se
     * houver algum erro retorna lista vazia;</p>
     *
     * @param queryString
     * @param nativeQuery
     * @param maxResults
     *
     * @author Bruno
     *
     * @return List
     */
    @Override
    public List liveList(String queryString, boolean nativeQuery, int maxResults) {
        try {
            Query query;
            if (nativeQuery) {
                query = getEntityManager().createNativeQuery(queryString);
            } else {
                query = getEntityManager().createQuery(queryString);
            }
            if (maxResults > 0) {
                query.setMaxResults(maxResults);
            }
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            EXCEPCION = e;
            if (GenericaSessao.exists("habilitaLog")) {
                if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                    GenericaMensagem.fatal("LOG", "Exceção gerada " + EXCEPCION.getMessage());
                    PF.update("header:form_log");
                }
            }
        }
        return new ArrayList();
    }

    /**
     * <p>
     * <strong>Live Object</strong></p>
     * <p>
     * <strong>Exemplos:</strong>Exemplo 1: liveSingle(SELECT U FROM User AS U);
     * Set nativeQuery = true; Retorna somente um resultado, se houver mais de
     * um retornará null; Caso não encontre nenhum resultado retorna null;</p>
     *
     * @param queryString
     *
     * @author Bruno
     *
     * @return Object
     */
    @Override
    public Object liveSingle(String queryString) {
        return liveSingle(queryString, false);
    }

    /**
     * <p>
     * <strong>Live Object</strong></p>
     * <p>
     * <strong>Exemplos:</strong>Exemplo 1: liveSingle(SELECT U FROM User AS U);
     * Set nativeQuery = true Exemplo 2: liveSingle(select * from user as u,
     * true); Retorna somente um resultado, se houver mais de um retornará null;
     * Caso não encontre nenhum resultado retorna null;</p>
     *
     * @param queryString
     * @param nativeQuery
     *
     *
     * @author Bruno
     *
     * @return Object
     */
    @Override
    public Object liveSingle(String queryString, boolean nativeQuery) {
        try {
            Query query;
            if (nativeQuery) {
                query = getEntityManager().createNativeQuery(queryString);
            } else {
                query = getEntityManager().createQuery(queryString);
            }
            List list = query.getResultList();
            if (!list.isEmpty()) {
                if (list.size() == 1) {
                    return query.getSingleResult();
                }
                return null;
            }
        } catch (Exception e) {
            Logger.getLogger(Dao.class.getName()).log(Level.WARNING, e.getMessage());
            EXCEPCION = e;
            if (GenericaSessao.exists("habilitaLog")) {
                if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                    GenericaMensagem.fatal("LOG", "Exceção gerada " + EXCEPCION.getMessage());
                    PF.update("header:form_log");
                }
            }
        }
        return null;
    }

    public boolean executeQueryObject(String textQuery) {
        try {
            Object xvalor = getEntityManager().createNativeQuery(textQuery).getSingleResult();
            if (xvalor != null) {
                return true;
            } else {
                if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                    GenericaMensagem.fatal("LOG", "Exception - Message: Erro ao executar: " + textQuery);
                    PF.update("form_log:i_messages");
                }
                return false;
            }
        } catch (Exception e) {
            EXCEPCION = e;
            if (GenericaSessao.exists("habilitaLog")) {
                if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                    GenericaMensagem.fatal("LOG", EXCEPCION.getMessage());
                    PF.update("form_log:i_messages");
                }
            }
            return false;
        }
    }

    public boolean executeQueryVetor(String textQuery) {
        try {
            List<List> valor = getEntityManager().createNativeQuery(textQuery).getResultList();
            if ((Integer) valor.get(0).get(0) > 0) {
                return true;
            } else {
                if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                    GenericaMensagem.fatal("LOG", "Exception - Message: Erro ao executar: " + textQuery);
                    PF.update("form_log:i_messages");
                }
                return false;
            }
        } catch (Exception e) {
            EXCEPCION = e;
            if (GenericaSessao.exists("habilitaLog")) {
                if (Usuario.getUsuario().getId() == 1 && GenericaSessao.getBoolean("habilitaLog")) {
                    GenericaMensagem.fatal("LOG", EXCEPCION.getMessage());
                    PF.update("form_log:i_messages");
                }
            }
            return false;
        }
    }

    public void close() {
        getEntityManager().close();
    }

    public boolean existsDescription(String description, String field, String object) {
        try {
            Query qry = getEntityManager().createNativeQuery("SELECT OB.* FROM " + object + " OB WHERE func_translate(UPPER(OB." + field + ")) LIKE func_translate(UPPER('" + description + "'))");
            if (!qry.getResultList().isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public Boolean executeQuery(String textQuery) {
        try {
            int valor = getEntityManager().createNativeQuery(textQuery).executeUpdate();
            return valor > 0;

        } catch (Exception e) {
            return false;
        }
    }
}
