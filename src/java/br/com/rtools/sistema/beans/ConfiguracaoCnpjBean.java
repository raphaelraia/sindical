package br.com.rtools.sistema.beans;

import br.com.rtools.seguranca.Registro;
import br.com.rtools.sistema.ConfiguracaoCnpj;
import br.com.rtools.sistema.TipoPesquisaCnpj;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

@ManagedBean
@ViewScoped
public class ConfiguracaoCnpjBean implements Serializable {

    private ConfiguracaoCnpj configuracaoCnpj;
    private Registro registro;
    private List<SelectItem> listaTipoPesquisa = new ArrayList();
    private Integer idTipoPesquisa = null;

    @PostConstruct
    public void init() {
        Dao dao = new Dao();
        loadListaTipoPesquisa();

        configuracaoCnpj = (ConfiguracaoCnpj) dao.find(new ConfiguracaoCnpj(), 1);

        if (configuracaoCnpj == null) {
            configuracaoCnpj = new ConfiguracaoCnpj();
            configuracaoCnpj.setTipoPesquisaCnpj((TipoPesquisaCnpj) new Dao().find(new TipoPesquisaCnpj(), 3));
            dao.save(configuracaoCnpj, true);
        }
        idTipoPesquisa = configuracaoCnpj.getTipoPesquisaCnpj().getId();
    }

    @PreDestroy
    public void destroy() {
        GenericaSessao.remove("configuracaoCnpjBean");
    }

    public void loadListaTipoPesquisa() {
        listaTipoPesquisa.clear();

        List<TipoPesquisaCnpj> result = new Dao().list(new TipoPesquisaCnpj());

        for (int i = 0; i < result.size(); i++) {
            listaTipoPesquisa.add(
                    new SelectItem(
                            result.get(i).getId(),
                            result.get(i).getDescricao(),
                            "" + result.get(i).getId()
                    )
            );
        }
    }

    public void update() {
        Dao dao = new Dao();
        if (configuracaoCnpj.getId() != -1) {
            configuracaoCnpj.setTipoPesquisaCnpj((TipoPesquisaCnpj) new Dao().find(new TipoPesquisaCnpj(), idTipoPesquisa));
            if (dao.update(configuracaoCnpj, true)) {
                configuracaoCnpj.setDataAtualizacao(DataHoje.dataHoje());
                GenericaMensagem.info("Sucesso", "Configurações aplicadas");
            } else {
                GenericaMensagem.warn("Erro", "Ao atualizar este registro!");
            }
        }
    }

    public ConfiguracaoCnpj getConfiguracaoCnpj() {
        return configuracaoCnpj;
    }

    public void setConfiguracaoCnpj(ConfiguracaoCnpj configuracaoCnpj) {
        this.configuracaoCnpj = configuracaoCnpj;
    }

    public void load() {

    }

    public Registro getRegistro() {
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public void onChange(TabChangeEvent event) {
        Tab activeTab = event.getTab();
    }

//    @Column(name = "is_cadastro_cnpj", columnDefinition = "boolean default false")
//    private boolean cadastroCnpj;
    public List<SelectItem> getListaTipoPesquisa() {
        return listaTipoPesquisa;
    }

    public void setListaTipoPesquisa(List<SelectItem> listaTipoPesquisa) {
        this.listaTipoPesquisa = listaTipoPesquisa;
    }

    public Integer getIdTipoPesquisa() {
        return idTipoPesquisa;
    }

    public void setIdTipoPesquisa(Integer idTipoPesquisa) {
        this.idTipoPesquisa = idTipoPesquisa;
    }

    public String getSaldo() throws ProtocolException, IOException {
        Charset charset = Charset.forName("UTF-8");
        Integer status;
        String statusBoolean = "OK";
        String error = "";
        String message = "";
        String query = "";
        String method = "GET";
        // 2235824594887334ABV16325666555
        ConfiguracaoCnpj cc = (ConfiguracaoCnpj) new Dao().find(new ConfiguracaoCnpj(), 1);
        if (cc == null) {
            GenericaMensagem.warn("Sistema", "Configuração do CNPJ não encontrada!");
            return null;
        }
        if (cc.getTipoPesquisaCnpj().getId() == 5) {
            query = "http://ws.hubdodesenvolvedor.com.br/v2/saldo/?";
            query += "info";
            query += "&";
            query += "token=" + URLEncoder.encode(cc.getToken(), "UTF-8");

        }
        URL url = new URL(query);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
// con.setRequestProperty("Content-length", String.valueOf(query.length()));
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setDoInput(true);
        con.setDoOutput(true);
        int responseCode = con.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
        String inputLine;
        StringBuilder response = new StringBuilder();
        try {
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
        } catch (Exception e) {

        }
        String rensponseString = response.toString();
        JSONObject result = new JSONObject(rensponseString);
        if (cc.getTipoPesquisaCnpj().getId() == 5) {
            try {
                statusBoolean = "" + result.getBoolean("status");
                error = result.getString("return");
            } catch (Exception e) {
                try {
                    statusBoolean = result.getString("status");
                    // error = result.getString("return");
                } catch (Exception e2) {
                }
            }
            try {
                message = result.getString("message");
            } catch (Exception e) {
            }
            //          ERRO PARA FALTA DE CRÉDITOS
            if (statusBoolean.equals("NOK") || statusBoolean.equals("false")) {
                return "CONTATE O ADMINISTRADOR DO SISTEMA (STATUS 7)!";
            }

            //          ERRO PARA DEMAIS STATUS -- NÃO CONSEGUIU PESQUISAR
            if (statusBoolean.equals("NOK") || statusBoolean.equals("false")) {
                return "NOK";
            }

            //          ERRO PARA DEMAIS STATUS -- NÃO CONSEGUIU PESQUISAR
            if (statusBoolean.equals("NOK")) {
                return ". TENTE NOVAMENTE MAIS TARDE!!! SISTEMA DA RECEITA ESTA APRESENTANDO INSTABILIDADE NO MOMENTO!!!. ";
            }

            JSONArray obj = result.getJSONArray("result");
            try {
                String r = result.getJSONArray("result").toString().replace("[", "");
                r = r.replace("]", "");
                JSONObject object = new JSONObject(r);
                return object.getString("saldo");
            } catch (JSONException e) {

            }

        }
        return "0";
    }

}
