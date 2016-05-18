/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.utilitarios;

import br.com.rtools.endereco.Endereco;
import br.com.rtools.pessoa.Cnae;
import br.com.rtools.pessoa.JuridicaReceita;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.TipoEndereco;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.pessoa.db.CnaeDB;
import br.com.rtools.pessoa.db.CnaeDBToplink;
import br.com.rtools.pessoa.db.TipoEnderecoDB;
import br.com.rtools.pessoa.db.TipoEnderecoDBToplink;
import br.com.rtools.sistema.ConfiguracaoCnpj;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

/**
 *
 * @author Claudemir Rtools
 */
public class JuridicaReceitaJSON {

    private final String documento;
    private String tipo;
    private final JuridicaReceita juridicaReceita;
    private final PesquisaCNPJ pesquisaCNPJ;

    public JuridicaReceitaJSON(String documento, String tipo) {
        this.pesquisaCNPJ = null;
        this.juridicaReceita = null;
        this.documento = documento;
        this.tipo = tipo;
    }

    public JuridicaReceitaJSON(JuridicaReceita juridicaReceita) {
        this.pesquisaCNPJ = null;
        this.juridicaReceita = juridicaReceita;
        this.documento = "";
        this.tipo = "";
    }

    public JuridicaReceitaJSON(PesquisaCNPJ pesquisaCNPJ) {
        this.pesquisaCNPJ = pesquisaCNPJ;
        this.juridicaReceita = null;
        this.documento = "";
        this.tipo = "padrao";
    }

    public JuridicaReceitaObject pesquisar() {
        try {
            JuridicaReceitaObject jro = new JuridicaReceitaObject();
            URL url;
            Charset charset = Charset.forName("UTF8");
            Integer status;
            String error;

            List<String> list_cnae = new ArrayList();
            List<String> list_cnae_sec = new ArrayList();

            switch (tipo) {
                case "wooki":
                    /*
                     * 0 (zero): requisição feita com sucesso.
                     * 1: requisição feita com sucesso, porém dados estão sendo atualizados; aguarde o tempo indicado e refaça exatamente a mesma requisição.
                     * 2: requisição feita com sucesso, porém dado requisitado não existe no site fonte.
                     * 3: erro, método inválido.
                     * 4: erro, parâmetro inválido; verifique os parâmetros informados.
                     * 5: erro, autenticação falhou; informe corretamente o usuário e senha
                     * 6: erro, você não possui um plano de acessos para realizar a requisição; adquira mais acessos.
                     * 7: erro, você não possui acessos restantes para realizar a requisição; adquira mais acessos.
                     * 8: erro, ocorreu um erro ao processar sua requisição; contate o suporte técnico.
                     * 9: erro, é necessário realizar todas as requisições utilizando oprotocolo HTTPS.
                     */
                    ConfiguracaoCnpj cc = (ConfiguracaoCnpj) new Dao().find(new ConfiguracaoCnpj(), 1);
                    if (cc == null) {
                        url = new URL("https://wooki.com.br/api/v1/cnpj/receitafederal?numero=" + documento + "&dias=90&usuario=rogerio@rtools.com.br&senha=989899");
                    } else if (cc.getEmail().isEmpty() || cc.getSenha().isEmpty()) {
                        url = new URL("https://wooki.com.br/api/v1/cnpj/receitafederal?numero=" + documento + "&dias=" + cc.getDias() + "&usuario=rogerio@rtools.com.br&senha=989899");
                    } else {
                        url = new URL("https://wooki.com.br/api/v1/cnpj/receitafederal?numero=" + documento + "&dias=" + cc.getDias() + "&usuario=" + cc.getEmail() + "&senha=" + cc.getSenha());
                    }

                    // URL url = new URL("https://wooki.com.br/api/v1/cnpj/receitafederal?numero=00000000000191&usuario=teste@wooki.com.br&senha=teste");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
                    con.setRequestMethod("GET");
                    con.connect();
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset))) {
                        String str = in.readLine();
                        JSONObject obj = new JSONObject(str);
                        status = obj.getInt("status");
                        error = obj.getString("msg");

                        // ERRO PARA FALTA DE CRÉDITOS
                        if (status == 7) {
                            jro.setStatus(-1);
                            error = "CONTATE O ADMINISTRADOR DO SISTEMA (STATUS 7)!";
                            jro.setMsg(error);

                            in.close();
                            con.disconnect();
                            return jro;
                        }

                        // ERRO PARA DEMAIS STATUS -- NÃO CONSEGUIU PESQUISAR
                        if (status != 0) {
                            jro.setStatus(-1);
                            jro.setMsg(error.toUpperCase());

                            in.close();
                            con.disconnect();
                            return jro;
                        }

                        int pos1 = obj.getString("atividade_principal").indexOf("(");
                        int pos2 = obj.getString("atividade_principal").indexOf(")");
                        String cnae = obj.getString("atividade_principal").substring(pos1 + 1, pos2);
                        list_cnae.add(cnae);

                        String cnae_sec = obj.getString("atividades_secundarias");
                        while (!cnae_sec.isEmpty()) {
                            try {
                                pos1 = cnae_sec.indexOf("(");
                                pos2 = cnae_sec.indexOf(")");
                                cnae = cnae_sec.substring(pos1 + 1, pos2);
                                cnae_sec = cnae_sec.substring(pos2 + 1);
                                list_cnae_sec.add(cnae);
                            } catch (Exception e) {
                                break;
                            }
                        }

                        jro = new JuridicaReceitaObject(
                                status, // status
                                error, // msg
                                obj.getString("nome_empresarial"),
                                obj.getString("titulo_estabelecimento"),
                                AnaliseString.mascaraCep(obj.getString("cep")),
                                obj.getString("logradouro"),
                                obj.getString("bairro"),
                                obj.getString("complemento"),
                                obj.getString("numero"),
                                obj.getString("atividade_principal"),
                                obj.getString("situacao_cadastral"),
                                DataHoje.converteData(DataHoje.converte(obj.getString("data_abertura"))),
                                obj.getString("atividades_secundarias"),
                                obj.getString("municipio"),
                                obj.getString("uf"),
                                obj.getString("email_rf"),
                                obj.getString("telefone_rf")
                        );
                        in.close();
                    }
                    con.disconnect();
                    break;
                case "gratis":
                    String readLine,
                     append = "";

                    try {
                        url = new URL("http://receitaws.com.br/v1/cnpj/" + documento);
                        con = (HttpURLConnection) url.openConnection();
                        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
                        con.setRequestMethod("GET");
                        con.connect();
                        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset))) {
                            while ((readLine = in.readLine()) != null) {
                                append += readLine;
                            }
                            in.close();
                        }
                        con.disconnect();
                    } catch (IOException | JSONException e) {
                        status = -1;
                        error = e.getMessage();
                        if (error.contains("504")) {
                            error = "Tente novamente mais tarde, serviço temporariamente indisponível!";
                        }
                        jro.setStatus(status);
                        jro.setMsg(error);
                        return jro;
                    }

                    JSONObject obj = new JSONObject(append);
                    if (obj.getString("status").equals("ERROR")) {
                        status = -1;
                        error = obj.getString("message");

                        jro.setStatus(status);
                        jro.setMsg(error);
                        return jro;
                    } else {
                        // CONSEGUIU PESQUISAR
                        status = 0;
                        error = "";
                    }

                    try {
                        JSONArray cnaeArray = obj.getJSONArray("atividade_principal");
                        String cnaeString = "";
                        try {
                            for (int i = 0; i < cnaeArray.length(); ++i) {
                                JSONObject rec = cnaeArray.getJSONObject(i);
                                String code = rec.getString("code").replace(".", "").replace("-", "");
                                list_cnae.add(code);
                                cnaeString += rec.getString("text") + " (" + code + ") ";
                            }
                        } catch (Exception e) {

                        }
                        JSONArray cnaeArraySec = obj.getJSONArray("atividades_secundarias");
                        String cnaeStringSec = "";
                        try {
                            for (int i = 0; i < cnaeArraySec.length(); ++i) {
                                JSONObject rec = cnaeArraySec.getJSONObject(i);
                                String code = rec.getString("code").replace(".", "").replace("-", "");
                                list_cnae_sec.add(code);
                                cnaeStringSec += rec.getString("text") + " (" + code + ") ";
                            }
                        } catch (Exception e) {

                        }

                        jro = new JuridicaReceitaObject(
                                status,
                                error,
                                obj.getString("nome"),
                                obj.getString("fantasia"),
                                AnaliseString.mascaraCep(obj.getString("cep")),
                                obj.getString("logradouro"),
                                obj.getString("bairro"),
                                obj.getString("complemento"),
                                obj.getString("numero"),
                                cnaeString,
                                obj.getString("situacao"),
                                DataHoje.converteData(DataHoje.converte(obj.getString("abertura"))),
                                cnaeStringSec,
                                obj.getString("municipio"),
                                obj.getString("uf"),
                                obj.getString("email"),
                                obj.getString("telefone")
                        );
                    } catch (Exception e) {
                        GenericaMensagem.warn("Erro", e.getMessage());
                        jro.setStatus(-1);
                        jro.setMsg(e.getMessage());
                        return jro;
                    }
                    break;
                default:
                    HashMap hash = pesquisaCNPJ.confirmar();

                    if (!(Boolean) hash.get("status")) {
                        jro.setStatus(-2);
                        jro.setMsg(hash.get("mensagem").toString());
                        return jro;
                    }

                    if ((Boolean) hash.get("status") && hash.get("situacao_cadastral").toString().equals("BAIXADA")) {
                        jro.setStatus(-3);
                        jro.setMsg("Empresa Baixada no cadastro da Receita Federal, data da baixa: " + hash.get("data_situacao_cadastral").toString() + ", motivo da baixa: " + hash.get("motivo_situacao_cadastral").toString());
                        return jro;
                    }

                    String cnae = "Não definido",
                     cnaeSecundario = "";
                    if (!hash.get("cnae").toString().isEmpty()) {
                        cnae = hash.get("cnae").toString().substring(0, 10).replace(".", "").replace("-", "");
                        list_cnae.add(cnae);

                        cnae = hash.get("cnae").toString().substring(10, hash.get("cnae").toString().length()).replace("-", "") + " (" + cnae + ")";
                        cnae = cnae.trim();
                    }

                    if (!((List) hash.get("cnae_secundario_list")).isEmpty()) {
                        for (int i = 0; i < ((List) hash.get("cnae_secundario_list")).size(); ++i) {
                            String cnaex = ((List) hash.get("cnae_secundario_list")).get(i).toString().replace("[", "").replace("]", "").substring(0, 10).replace(".", "").replace("-", "");
                            list_cnae_sec.add(cnaex);

                            cnaeSecundario += ((List) hash.get("cnae_secundario_list")).get(i).toString().substring(12, ((List) hash.get("cnae_secundario_list")).get(i).toString().length()) + " (" + cnaex + ") ";
                        }
                        cnaeSecundario = cnaeSecundario.trim();
                    } else {
                        //cnaeSecundario = hash.get("cnae_secundario").toString();
                        cnaeSecundario = "";
                    }

                    jro = new JuridicaReceitaObject(
                            0,
                            "",
                            hash.get("nome_empresarial").toString(),
                            hash.get("fantasia").toString(),
                            AnaliseString.mascaraCep(hash.get("cep").toString()),
                            hash.get("logradouro").toString(),
                            hash.get("bairro").toString(),
                            hash.get("complemento").toString(),
                            hash.get("numero").toString(),
                            cnae,
                            hash.get("situacao_cadastral").toString(),
                            DataHoje.converteData(DataHoje.converte(hash.get("data_abertura").toString())),
                            cnaeSecundario,
                            hash.get("municipio").toString(),
                            hash.get("uf").toString(),
                            hash.get("endereco_eletronico").toString(),
                            hash.get("telefone").toString()
                    );

                    break;
            }

            String[] emails = (jro.getEmail_rf() == null || jro.getEmail_rf().isEmpty()) ? "".split("") : jro.getEmail_rf().toLowerCase().split(" ");
            String[] telefones = (jro.getTelefone_rf() == null || jro.getTelefone_rf().isEmpty()) ? "".split("") : jro.getTelefone_rf().toLowerCase().split(" / ");
            String email1 = "", email2 = "", email3 = "";
            String telefone1 = "", telefone2 = "", telefone3 = "";

            switch (emails.length) {
                case 1:
                    email1 = emails[0];
                    break;
                case 2:
                    email1 = emails[0];
                    email2 = emails[1];
                    break;
                case 3:
                    email1 = emails[0];
                    email2 = emails[1];
                    email3 = emails[2];
                    break;
            }

            switch (telefones.length) {
                case 1:
                    telefone1 = telefones[0];
                    break;
                case 2:
                    telefone1 = telefones[0];
                    telefone2 = telefones[1];
                    break;
                case 3:
                    telefone1 = telefones[0];
                    telefone2 = telefones[1];
                    telefone3 = telefones[2];
                    break;
            }

            CnaeDB dbc = new CnaeDBToplink();
            List<Cnae> listac = new ArrayList();
            for (String cnae_string : list_cnae) {
                listac.addAll(dbc.pesquisaCnae(cnae_string, "cnae", "I"));
            }

            List<Cnae> listac_sec = new ArrayList();
            for (String cnae_string : list_cnae_sec) {
                listac_sec.addAll(dbc.pesquisaCnae(cnae_string, "cnae", "I"));
            }

            PessoaEnderecoDao dbe = new PessoaEnderecoDao();
            String cep = jro.getCep();
            Endereco endereco = null;
            List<PessoaEndereco> listpe = new ArrayList();
            if (!cep.isEmpty()) {
                cep = cep.replace(".", "").replace("-", "");

                String descricao[] = AnaliseString.removerAcentos(jro.getLogradouro().replace("'", "")).split(" ");
                String bairros[] = AnaliseString.removerAcentos(jro.getBairro().replace("'", "")).split(" ");

                endereco = dbe.enderecoReceita(cep, descricao, bairros);

                if (endereco == null) {
                    CEPService cEPService = new CEPService();
                    cEPService.setCep(cep);
                    cEPService.procurar();
                    Endereco e = cEPService.getEndereco();
                    if (e.getId() != -1) {
                        endereco = e;
                    }
                }

                if (endereco != null) {
                    TipoEnderecoDB dbt = new TipoEnderecoDBToplink();
                    List tiposE = dbt.listaTipoEnderecoParaJuridica();
                    for (Object tiposE1 : tiposE) {
                        PessoaEndereco pe = new PessoaEndereco(
                                -1,
                                endereco,
                                (TipoEndereco) tiposE1,
                                null,
                                jro.getNumero(),
                                jro.getComplemento()
                        );

                        listpe.add(pe);
                    }
                }
            }

            jro.setEmail1(email1);
            jro.setEmail2(email2);
            jro.setEmail3(email3);

            jro.setTelefone1(telefone1);
            jro.setTelefone2(telefone2);
            jro.setTelefone3(telefone3);

            jro.setLista_cnae(listac);
            jro.setLista_cnae_secundario(listac_sec);

            jro.setEndereco(endereco);
            jro.setPessoaEndereco(listpe);

            return jro;
        } catch (IOException | JSONException e) {
            e.getMessage();
        }
        return null;
    }

    public JuridicaReceitaObject load() {
        List<String> list_cnae = new ArrayList();
        List<String> list_cnae_sec = new ArrayList();

        JuridicaReceitaObject jro = new JuridicaReceitaObject(
                0,
                "",
                juridicaReceita.getNome(),
                juridicaReceita.getFantasia(),
                juridicaReceita.getCep(),
                juridicaReceita.getDescricaoEndereco(),
                juridicaReceita.getBairro(),
                juridicaReceita.getComplemento(),
                juridicaReceita.getNumero(),
                juridicaReceita.getCnae(),
                juridicaReceita.getStatus(),
                DataHoje.converteData(juridicaReceita.getDtAbertura()),
                juridicaReceita.getCnaeSegundario(),
                juridicaReceita.getCidade(),
                juridicaReceita.getUf(),
                juridicaReceita.getEmail(),
                juridicaReceita.getTelefone()
        );

        String[] emails = (jro.getEmail_rf() == null || jro.getEmail_rf().isEmpty()) ? "".split("") : jro.getEmail_rf().toLowerCase().split(" ");
        String[] telefones = (jro.getTelefone_rf() == null || jro.getTelefone_rf().isEmpty()) ? "".split("") : jro.getTelefone_rf().toLowerCase().split(" / ");
        String email1 = "", email2 = "", email3 = "";
        String telefone1 = "", telefone2 = "", telefone3 = "";

        switch (emails.length) {
            case 1:
                email1 = emails[0];
                break;
            case 2:
                email1 = emails[0];
                email2 = emails[1];
                break;
            case 3:
                email1 = emails[0];
                email2 = emails[1];
                email3 = emails[2];
                break;
        }

        switch (telefones.length) {
            case 1:
                telefone1 = telefones[0];
                break;
            case 2:
                telefone1 = telefones[0];
                telefone2 = telefones[1];
                break;
            case 3:
                telefone1 = telefones[0];
                telefone2 = telefones[1];
                telefone3 = telefones[2];
                break;
        }

        int pos1, pos2;
        String cnae;
        
        if(!jro.getAtividade_principal().isEmpty()){
            pos1 = jro.getAtividade_principal().indexOf("(");
            pos2 = jro.getAtividade_principal().indexOf(")");
            cnae = jro.getAtividade_principal().substring(pos1 + 1, pos2);
            list_cnae.add(cnae);
        }

        String cnae_sec = jro.getAtividades_secundarias();
        while (!cnae_sec.isEmpty()) {
            try {
                pos1 = cnae_sec.indexOf("(");
                pos2 = cnae_sec.indexOf(")");
                cnae = cnae_sec.substring(pos1 + 1, pos2);
                cnae_sec = cnae_sec.substring(pos2 + 1);
                list_cnae_sec.add(cnae);
            } catch (Exception e) {
                break;
            }
        }

        CnaeDB dbc = new CnaeDBToplink();
        List<Cnae> listac = new ArrayList();
        for (String cnae_string : list_cnae) {
            listac.addAll(dbc.pesquisaCnae(cnae_string, "cnae", "I"));
        }

        List<Cnae> listac_sec = new ArrayList();
        for (String cnae_string : list_cnae_sec) {
            listac_sec.addAll(dbc.pesquisaCnae(cnae_string, "cnae", "I"));
        }

        PessoaEnderecoDao dbe = new PessoaEnderecoDao();
        String cep = jro.getCep();
        Endereco endereco = null;
        List<PessoaEndereco> listpe = new ArrayList();
        
        if (!cep.isEmpty()) {
            cep = cep.replace(".", "").replace("-", "");

            String descricao[] = AnaliseString.removerAcentos(jro.getLogradouro().replace("'", "")).split(" ");
            String bairros[] = AnaliseString.removerAcentos(jro.getBairro().replace("'", "")).split(" ");

            endereco = dbe.enderecoReceita(cep, descricao, bairros);

            if (endereco == null) {
                CEPService cEPService = new CEPService();
                cEPService.setCep(cep);
                cEPService.procurar();
                Endereco e = cEPService.getEndereco();
                if (e.getId() != -1) {
                    endereco = e;
                }
            }

            if (endereco != null) {
                TipoEnderecoDB dbt = new TipoEnderecoDBToplink();
                List tiposE = dbt.listaTipoEnderecoParaJuridica();
                for (Object tiposE1 : tiposE) {
                    PessoaEndereco pe = new PessoaEndereco(
                            -1,
                            endereco,
                            (TipoEndereco) tiposE1,
                            null,
                            jro.getNumero(),
                            jro.getComplemento()
                    );

                    listpe.add(pe);
                }
            }
        }
        
        jro.setEmail1(email1);
        jro.setEmail2(email2);
        jro.setEmail3(email3);

        jro.setTelefone1(telefone1);
        jro.setTelefone2(telefone2);
        jro.setTelefone3(telefone3);

        jro.setLista_cnae(listac);
        jro.setLista_cnae_secundario(listac_sec);

        jro.setEndereco(endereco);
        jro.setPessoaEndereco(listpe);
        return jro;
    }

    public class JuridicaReceitaObject {

        private Integer status;
        private String msg;
        private String nome_empresarial;
        private String titulo_estabelecimento;
        private String cep;
        private String logradouro;
        private String bairro;
        private String complemento;
        private String numero;
        private String atividade_principal;
        private String situacao_cadastral;
        private String data_abertura;
        private String atividades_secundarias;
        private String municipio;
        private String uf;
        private String email_rf;
        private String telefone_rf;
        private String email1;
        private String email2;
        private String email3;
        private String telefone1;
        private String telefone2;
        private String telefone3;
        private List<Cnae> lista_cnae;
        private List<Cnae> lista_cnae_secundario;
        private Endereco endereco;
        private List<PessoaEndereco> pessoaEndereco;

        public JuridicaReceitaObject() {

        }

        public JuridicaReceitaObject(Integer status, String msg, String nome_empresarial, String titulo_estabelecimento, String cep, String logradouro, String bairro, String complemento, String numero, String atividade_principal, String situacao_cadastral, String data_abertura, String atividades_secundarias, String municipio, String uf, String email_rf, String telefone_rf) {
            this.status = status;
            this.msg = msg;
            this.nome_empresarial = nome_empresarial;
            this.titulo_estabelecimento = titulo_estabelecimento;
            this.cep = cep;
            this.logradouro = logradouro;
            this.bairro = bairro;
            this.complemento = complemento;
            this.numero = numero;
            this.atividade_principal = atividade_principal;
            this.situacao_cadastral = situacao_cadastral;
            this.data_abertura = data_abertura;
            this.atividades_secundarias = atividades_secundarias;
            this.municipio = municipio;
            this.uf = uf;
            this.email_rf = email_rf;
            this.telefone_rf = telefone_rf;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getNome_empresarial() {
            return nome_empresarial;
        }

        public void setNome_empresarial(String nome_empresarial) {
            this.nome_empresarial = nome_empresarial;
        }

        public String getTitulo_estabelecimento() {
            return titulo_estabelecimento;
        }

        public void setTitulo_estabelecimento(String titulo_estabelecimento) {
            this.titulo_estabelecimento = titulo_estabelecimento;
        }

        public String getCep() {
            return cep;
        }

        public void setCep(String cep) {
            this.cep = cep;
        }

        public String getLogradouro() {
            return logradouro;
        }

        public void setLogradouro(String logradouro) {
            this.logradouro = logradouro;
        }

        public String getBairro() {
            return bairro;
        }

        public void setBairro(String bairro) {
            this.bairro = bairro;
        }

        public String getComplemento() {
            return complemento;
        }

        public void setComplemento(String complemento) {
            this.complemento = complemento;
        }

        public String getNumero() {
            return numero;
        }

        public void setNumero(String numero) {
            this.numero = numero;
        }

        public String getAtividade_principal() {
            return atividade_principal;
        }

        public void setAtividade_principal(String atividade_principal) {
            this.atividade_principal = atividade_principal;
        }

        public String getSituacao_cadastral() {
            return situacao_cadastral;
        }

        public void setSituacao_cadastral(String situacao_cadastral) {
            this.situacao_cadastral = situacao_cadastral;
        }

        public String getData_abertura() {
            return data_abertura;
        }

        public void setData_abertura(String data_abertura) {
            this.data_abertura = data_abertura;
        }

        public String getAtividades_secundarias() {
            return atividades_secundarias;
        }

        public void setAtividades_secundarias(String atividades_secundarias) {
            this.atividades_secundarias = atividades_secundarias;
        }

        public String getMunicipio() {
            return municipio;
        }

        public void setMunicipio(String municipio) {
            this.municipio = municipio;
        }

        public String getUf() {
            return uf;
        }

        public void setUf(String uf) {
            this.uf = uf;
        }

        public String getEmail_rf() {
            return email_rf;
        }

        public void setEmail_rf(String email_rf) {
            this.email_rf = email_rf;
        }

        public String getTelefone_rf() {
            return telefone_rf;
        }

        public void setTelefone_rf(String telefone_rf) {
            this.telefone_rf = telefone_rf;
        }

        public String getEmail1() {
            return email1;
        }

        public void setEmail1(String email1) {
            this.email1 = email1;
        }

        public String getEmail2() {
            return email2;
        }

        public void setEmail2(String email2) {
            this.email2 = email2;
        }

        public String getEmail3() {
            return email3;
        }

        public void setEmail3(String email3) {
            this.email3 = email3;
        }

        public String getTelefone1() {
            return telefone1;
        }

        public void setTelefone1(String telefone1) {
            this.telefone1 = telefone1;
        }

        public String getTelefone2() {
            return telefone2;
        }

        public void setTelefone2(String telefone2) {
            this.telefone2 = telefone2;
        }

        public String getTelefone3() {
            return telefone3;
        }

        public void setTelefone3(String telefone3) {
            this.telefone3 = telefone3;
        }

        public List<Cnae> getLista_cnae() {
            return lista_cnae;
        }

        public void setLista_cnae(List<Cnae> lista_cnae) {
            this.lista_cnae = lista_cnae;
        }

        public Endereco getEndereco() {
            return endereco;
        }

        public void setEndereco(Endereco endereco) {
            this.endereco = endereco;
        }

        public List<PessoaEndereco> getPessoaEndereco() {
            return pessoaEndereco;
        }

        public void setPessoaEndereco(List<PessoaEndereco> pessoaEndereco) {
            this.pessoaEndereco = pessoaEndereco;
        }

        public List<Cnae> getLista_cnae_secundario() {
            return lista_cnae_secundario;
        }

        public void setLista_cnae_secundario(List<Cnae> lista_cnae_secundario) {
            this.lista_cnae_secundario = lista_cnae_secundario;
        }

    }
}
