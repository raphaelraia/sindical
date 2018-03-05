package br.com.rtools.principal;

import br.com.rtools.sistema.Configuracao;
import br.com.rtools.sistema.conf.DataBase;
import br.com.rtools.utilitarios.GenericaRequisicao;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.GenericaString;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import oracle.toplink.essentials.config.CacheType;
import oracle.toplink.essentials.config.TopLinkProperties;
import oracle.toplink.essentials.ejb.cmp3.EntityManagerFactoryProvider;

public class DB {

    private EntityManager entidade;

    public EntityManager getEntityManager() {
        if (entidade == null) {
            if (!GenericaSessao.exists("conexao")) {
                String cliente = (String) GenericaSessao.getString("sessaoCliente");
                Configuracao configuracao = servidor(cliente.replace("/", ""));
                DataBase dataBase = new DataBase();
                dataBase.loadJson();
                Integer port = 5432;
                
                if (!dataBase.getHost().isEmpty()) {
                    configuracao.setHost(dataBase.getHost());
                }
                if (dataBase.getPort() != null && dataBase.getPort() != 0) {
                    port = dataBase.getPort();
                }
                if (!dataBase.getDatabase().isEmpty()) {
                    configuracao.setPersistence(dataBase.getDatabase());
                }
                if (!dataBase.getPassword().isEmpty()) {
                    configuracao.setSenha(dataBase.getPassword());
                }
                
                String user = dataBase.getUser();
                
                try {
                    Map properties = new HashMap();
                    properties.put(TopLinkProperties.CACHE_TYPE_DEFAULT, CacheType.SoftWeak);
                    properties.put(TopLinkProperties.JDBC_USER, user);
                    properties.put(TopLinkProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");
                    properties.put(TopLinkProperties.JDBC_DRIVER, "org.postgresql.Driver");
                    properties.put(TopLinkProperties.JDBC_PASSWORD, configuracao.getSenha());
                    properties.put(TopLinkProperties.JDBC_URL, "jdbc:postgresql://" + configuracao.getHost() + ":" + port + "/" + configuracao.getPersistence());
                    
                    // MOSTRAR QUERIES EXECUTADAS PELO SISTEMA ---
                    // properties.put(TopLinkProperties.LOGGING_LEVEL, "FINE");
                    // FIM ---
                    
                    EntityManagerFactory emf = Persistence.createEntityManagerFactory(configuracao.getPersistence(), properties);
                    String createTable = GenericaString.converterNullToString(GenericaRequisicao.getParametro("createTable"));
                    if (createTable.equals("criar")) {
                        properties.put(EntityManagerFactoryProvider.DDL_GENERATION, EntityManagerFactoryProvider.CREATE_ONLY);
                    }
                    entidade = emf.createEntityManager();
                    GenericaSessao.put("conexao", emf);
                } catch (Exception e) {
                    return null;
                }
            } else {
                try {
                    EntityManagerFactory emf = (EntityManagerFactory) GenericaSessao.getObject("conexao");
                    entidade = emf.createEntityManager();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return entidade;
    }

    public Configuracao servidor(String cliente) {
        Configuracao configuracao = new Configuracao();
        switch (cliente) {
            case "ComercioAraras":
            case "ComercioSertaozinho":
            case "FederacaoBH":
            case "SinpaaeRP":
            case "VestuarioLimeira":
            case "ComercioItapetininga":
            case "SeaacRP":
            case "MetalRP":
            case "ExtrativaRP":
            case "AlimentacaoArceburgo":
            case "FederacaoExtrativaSP":
            case "ExtrativaSP":
            case "HoteleiroRP":
            case "GaragistaRP":
            case "MetalBatatais":
            case "ServidoresRP":
            case "SeaacFranca":
            case "SincovagaSP":
            case "GraficosRP":
            case "ComercioSorocaba":
            case "TecelagemRP":
            case "CondominiosRP":
            case "ServidoresSerrana":
            case "SindiFarmaRP":
            case "SindiPetShopSP":
            case "ComercioPiracicaba":
            case "MarceneirosRP":
                configuracao.setCaminhoSistema(cliente);
                configuracao.setPersistence(cliente);
                //configuracao.setHost("192.168.1.102");
                // configuracao.setHost("192.168.1.100");
                configuracao.setHost("192.168.15.100");
                configuracao.setSenha("r#@tools");
                break;
            case "Rtools":
                configuracao.setCaminhoSistema(cliente);
                configuracao.setPersistence(cliente);
                //configuracao.setHost("192.168.1.102");
                //configuracao.setHost("192.168.1.100");
                configuracao.setHost("192.168.15.100");
                configuracao.setSenha("r#@tools");
//                configuracao.setHost("192.168.1.35");
//                configuracao.setSenha("*4qu4r10-");                
                break;
            case "ComercioLimeira":
                configuracao.setCaminhoSistema(cliente);
                configuracao.setPersistence(cliente);
                // IP LOCAL: 192.168.0.201
                // IP EXTERNO: 200.204.32.23
                // configuracao.setHost("192.168.0.201");
                configuracao.setSenha("r#@tools");
                break;
            case "Sinecol":
                configuracao.setCaminhoSistema("ComercioLimeira");
                configuracao.setPersistence("ComercioLimeira");
                // IP LOCAL: 192.168.0.201
                // IP EXTERNO: 200.204.32.23
                configuracao.setHost("200.204.32.23");
                configuracao.setSenha("r#@tools");
                break;
            case "NovaBase":
                configuracao.setCaminhoSistema(cliente);
                configuracao.setPersistence(cliente);
                configuracao.setHost("192.168.1.69");
                configuracao.setSenha("989899");
                break;
            case "ComercioRP":
                configuracao.setCaminhoSistema(cliente);
                configuracao.setPersistence("Sindical");
                configuracao.setHost("200.152.187.241");
                configuracao.setSenha("989899");
                break;
            default:
                if (cliente.equals("Sindical")) {
//                    cliente = "comerciolimeira";
//                    configuracao.setHost("localhost");
//                    configuracao.setSenha("989899");
                    // -- ATUAL
                    cliente = "ComercioRP";
                    //configuracao.setHost("192.168.1.102");
                    // configuracao.setHost("192.168.1.100");
                    configuracao.setHost("192.168.15.35");
                    // configuracao.setHost("192.168.1.35");
                    configuracao.setSenha("*4qu4r10-");
                }   //            } else {
//                if (cliente.equals("ServidoresRP")) {
//                    configuracao.setHost("localhost");
//                    configuracao.setSenha("989899");
//                }
//            }
                configuracao.setCaminhoSistema(cliente);
                configuracao.setPersistence(cliente);
                break;
        }
        return configuracao;
    }
    // COMÉRCIO LIMEIRA
//    public Configuracao servidor(String cliente) {
//        Configuracao configuracao = new Configuracao();
//        configuracao.setCaminhoSistema(cliente);
//        configuracao.setPersistence(cliente);
//        // IP LOCAL: 192.168.0.201
//        // IP EXTERNO: 200.204.32.23
//        configuracao.setHost("192.168.0.201");
//        configuracao.setSenha("r#@tools");
//        return configuracao;
//    }
    // COMÉRCIO RIBEIRÃO
//    public Configuracao servidor(String cliente) {
//        Configuracao configuracao = new Configuracao();
//        configuracao.setCaminhoSistema("Sindical");
//        configuracao.setHost("localhost");
//        configuracao.setSenha("989899");
//        configuracao.setPersistence(cliente);
//        return configuracao;
//    }
}
