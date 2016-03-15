/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.utilitarios;

import br.com.rtools.endereco.Endereco;
import br.com.rtools.pessoa.Cnae;
import br.com.rtools.pessoa.PessoaEmpresa;
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
    private final String tipo;

    public JuridicaReceitaJSON(String documento, String tipo) {
        this.documento = documento;
        this.tipo = tipo;
    }

    public JuridicaReceitaObject pesquisar() {
        try {
            JuridicaReceitaObject jro = new JuridicaReceitaObject();
            URL url;
            Charset charset = Charset.forName("UTF8");
            int status = 0;
            String error = "";
            if (tipo.equals("wooki")) {
                ConfiguracaoCnpj cc = (ConfiguracaoCnpj) new Dao().find(new ConfiguracaoCnpj(), 1);
                Integer dias = cc.getDias();
                for (int i = 0; i < 20; i++) {
                    if (cc == null) {
                        url = new URL("https://wooki.com.br/api/v1/cnpj/receitafederal?numero=" + documento + "&dias=60&usuario=rogerio@rtools.com.br&senha=989899");
                    } else if (cc.getEmail().isEmpty() || cc.getSenha().isEmpty()) {
                        url = new URL("https://wooki.com.br/api/v1/cnpj/receitafederal?numero=" + documento + "&dias=" + dias + "&usuario=rogerio@rtools.com.br&senha=989899");
                    } else {
                        url = new URL("https://wooki.com.br/api/v1/cnpj/receitafederal?numero=" + documento + "&dias=" + dias + "&usuario=" + cc.getEmail() + "&senha=" + cc.getSenha());
                    }

                    //URL url = new URL("https://wooki.com.br/api/v1/cnpj/receitafederal?numero=00000000000191&usuario=teste@wooki.com.br&senha=teste");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
                    con.setRequestMethod("GET");
                    con.connect();
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), charset))) {
                        String str = in.readLine();
                        JSONObject obj = new JSONObject(str);
                        status = obj.getInt("status");
                        error = obj.getString("msg");

                        if (status == 1) {
                            if (dias > 360) {
                                status = -1;
                                error = "NÃO CONSEGUIU PESQUISAR EM VÁRIAS TENTATIVAS";
                            } else {
                                dias += 30;
                                con.disconnect();
                                continue;
                            }
                        }
                        
                        // FALTA DE CRÉDITOS
                        if (status == 7) {
                            error = "CONTATE O ADMINISTRADOR DO SISTEMA (STATUS 7)!";
                            in.close();
                            con.disconnect();
                            jro.setStatus(status);
                            jro.setMsg(error);
                            return jro;
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
                }
            } else {
                String readLine = "";
                String append = "";
                try {
                    url = new URL("http://receitaws.com.br/v1/cnpj/" + documento);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
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

                }

                JSONObject obj = new JSONObject(append);
                if (obj.getString("status").equals("ERROR")) {
                    status = -1;
                    error = obj.getString("message");
                }
                try {
                    JSONArray cnaeArray = obj.getJSONArray("atividade_principal");
                    String cnaeString = "";
                    try {
                        for (int i = 0; i < cnaeArray.length(); ++i) {
                            JSONObject rec = cnaeArray.getJSONObject(i);
                            String code = rec.getString("code").replace(".", "");
                            code = code.replace("-", "");
                            cnaeString += rec.getString("text") + " (" + code + ") ";
                        }
                    } catch (Exception e) {

                    }
                    JSONArray cnaeArraySec = obj.getJSONArray("atividades_secundarias");
                    String cnaeStringSec = "";
                    try {
                        for (int i = 0; i < cnaeArraySec.length(); ++i) {
                            JSONObject rec = cnaeArraySec.getJSONObject(i);
                            String code = rec.getString("code").replace(".", "");
                            code = code.replace("-", "");
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
                }
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

            String result[] = (jro.getAtividade_principal() == null || jro.getAtividade_principal().isEmpty()) ? "".split("") : jro.getAtividade_principal().toLowerCase().split(" ");
            CnaeDB dbc = new CnaeDBToplink();
            String cnaex = result[result.length - 1].replace("(", "").replace(")", "");
            List<Cnae> listac = dbc.pesquisaCnae(cnaex, "cnae", "I");

            PessoaEnderecoDao dbe = new PessoaEnderecoDao();
            String cep = jro.getCep();
            cep = cep.replace(".", "").replace("-", "");

            String descricao[] = AnaliseString.removerAcentos(jro.getLogradouro().replace("'", "")).split(" ");
            String bairros[] = AnaliseString.removerAcentos(jro.getBairro().replace("'", "")).split(" ");

            Endereco endereco = dbe.enderecoReceita(cep, descricao, bairros);

            if (endereco == null) {
                CEPService cEPService = new CEPService();
                cEPService.setCep(cep);
                cEPService.procurar();
                Endereco e = cEPService.getEndereco();
                if (e.getId() != -1) {
                    endereco = e;
                }
            }

            List<PessoaEndereco> listpe = new ArrayList();

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

            jro.setEmail1(email1);
            jro.setEmail2(email2);
            jro.setEmail3(email3);

            jro.setTelefone1(telefone1);
            jro.setTelefone2(telefone2);
            jro.setTelefone3(telefone3);

            jro.setLista_cnae(listac);

            jro.setEndereco(endereco);
            jro.setPessoaEndereco(listpe);

            return jro;
        } catch (IOException | JSONException e) {
            e.getMessage();
        }
        return null;
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

    }
}
