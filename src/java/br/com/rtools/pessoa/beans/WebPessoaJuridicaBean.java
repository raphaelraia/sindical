package br.com.rtools.pessoa.beans;

import br.com.rtools.pessoa.dao.JuridicaDao;
import br.com.rtools.pessoa.dao.PessoaDao;
import br.com.rtools.pessoa.dao.CnaeDao;
import br.com.rtools.pessoa.dao.TipoEnderecoDao;
import br.com.rtools.arrecadacao.CnaeConvencao;
import br.com.rtools.arrecadacao.Convencao;
import br.com.rtools.arrecadacao.GrupoCidade;
import br.com.rtools.arrecadacao.Oposicao;
import br.com.rtools.arrecadacao.dao.OposicaoDao;
import br.com.rtools.arrecadacao.dao.CnaeConvencaoDao;
import br.com.rtools.arrecadacao.dao.ConvencaoCidadeDao;
import br.com.rtools.endereco.Endereco;
import br.com.rtools.pessoa.*;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.sistema.ConfiguracaoCnpj;
import br.com.rtools.utilitarios.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
//import java.util.Vector;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
// import knu.ReceitaCNPJ;
// import knu.knu;

@ManagedBean
@SessionScoped
public class WebPessoaJuridicaBean implements Serializable {

    private Juridica juridica;
    private PessoaEndereco pessoaEndereco;
    private JuridicaReceita juridicaReceita;
    private ConfiguracaoCnpj configuracaoCnpj;
    private List<PessoaEndereco> listPessoaEndereco;
    private List<Oposicao> listOposicao;
    private GrupoCidade grupoCidade;
    private Convencao convencao;
    private CnaeConvencao cnaeConvencao;

    @PostConstruct
    public void init() {
        juridica = new Juridica();
        pessoaEndereco = new PessoaEndereco();
        juridicaReceita = new JuridicaReceita();
        configuracaoCnpj = (ConfiguracaoCnpj) new Dao().find(new ConfiguracaoCnpj(), 1);
    }

    @PreDestroy
    public void destroy() {
        clear();
    }

    public void clear() {
        GenericaSessao.remove("webPessoaJuridicaBean");
    }

    public void findJuridicaReceita() {
        Boolean save = false;
        if (juridica.getPessoa().getDocumento().isEmpty()) {
            GenericaMensagem.warn("Validação", "Informar documento!");
            return;
        }
        Dao dao = new Dao();
        if (configuracaoCnpj == null || configuracaoCnpj.getLocal()) {
            if (juridica.getId() != -1) {
                return;
            }
            if (juridica.getPessoa().getDocumento().isEmpty()) {
                return;
            }

            String documento = AnaliseString.extrairNumeros(juridica.getPessoa().getDocumento());
//
//            if (!validaTipoDocumento(2, documento)) {
//                GenericaMensagem.warn("Atenção", "Documento inválido!");
//                return;
//            }
            JuridicaDao dbj = new JuridicaDao();
            List listDocumento = dbj.pesquisaJuridicaPorDoc(juridica.getPessoa().getDocumento());
            for (int i = 0; i < listDocumento.size(); i++) {
                if (!listDocumento.isEmpty()) {
                    juridica = (Juridica) listDocumento.get(0);
                    loadPessoaEndereco();
                    loadCnaeConvencao();
                    loadGrupoCidade();
                    loadOposicao();
                    return;
                }
            }

            PessoaDao db = new PessoaDao();

            juridicaReceita = db.pesquisaJuridicaReceita(documento);
            if (juridicaReceita.getPessoa() != null && juridicaReceita.getPessoa().getId() != -1) {
                GenericaMensagem.warn("Atenção", "Pessoa já cadastrada no Sistema!");
                return;
            }
            if (juridicaReceita.getId() == -1) {
                URL url;
                try {
                    if (configuracaoCnpj == null) {
                        url = new URL("https://wooki.com.br/api/v1/cnpj/receitafederal?numero=" + documento + "&dias=" + configuracaoCnpj.getDias() + "&usuario=rogerio@rtools.com.br&senha=989899");
                    } else if (configuracaoCnpj.getEmail().isEmpty() || configuracaoCnpj.getSenha().isEmpty()) {
                        url = new URL("https://wooki.com.br/api/v1/cnpj/receitafederal?numero=" + documento + "&dias=" + configuracaoCnpj.getDias() + "&usuario=rogerio@rtools.com.br&senha=989899");
                    } else {
                        url = new URL("https://wooki.com.br/api/v1/cnpj/receitafederal?numero=" + documento + "&dias=" + configuracaoCnpj.getDias() + "&usuario=" + configuracaoCnpj.getEmail() + "&senha=" + configuracaoCnpj.getSenha());
                    }
                    //URL url = new URL("https://wooki.com.br/api/v1/cnpj/receitafederal?numero=00000000000191&usuario=teste@wooki.com.br&senha=teste");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                        String str = in.readLine();
                        JSONObject obj = new JSONObject(str);
                        int status = obj.getInt("status");
                        String error = obj.getString("msg");

                        if (status == 6) {
                            GenericaMensagem.warn("Atenção", "Limite de acessos excedido!");
                            return;
                        }

                        if (status == 1) {
                            GenericaMensagem.info("Atenção", "Atualizando esse CNPJ na receita, pesquise novamente em 30 segundos!");
                            return;
                        }

                        if (status != 0) {
                            GenericaMensagem.error("Erro", error);
                            return;
                        }

                        juridicaReceita.setNome(obj.getString("nome_empresarial"));
                        juridicaReceita.setFantasia(obj.getString("titulo_estabelecimento"));
                        juridicaReceita.setDocumento(documento);
                        juridicaReceita.setCep(AnaliseString.mascaraCep(obj.getString("cep")));
                        juridicaReceita.setDescricaoEndereco(obj.getString("logradouro"));
                        juridicaReceita.setBairro(obj.getString("bairro"));
                        juridicaReceita.setComplemento(obj.getString("complemento"));
                        juridicaReceita.setNumero(obj.getString("numero"));
                        juridicaReceita.setCnae(obj.getString("atividade_principal"));
                        juridicaReceita.setPessoa(null);
                        juridicaReceita.setStatus(obj.getString("situacao_cadastral"));
                        juridicaReceita.setDtAbertura(DataHoje.converte(obj.getString("data_abertura")));
                        juridicaReceita.setCnaeSegundario(obj.getString("atividades_secundarias"));
                        juridicaReceita.setCidade(obj.getString("municipio"));
                        juridicaReceita.setUf(obj.getString("uf"));
                        juridicaReceita.setEmail(obj.getString("email_rf"));
                        juridicaReceita.setTelefone(obj.getString("telefone_rf"));

                        dao.openTransaction();

                        if (!dao.save(juridicaReceita)) {
                            GenericaMensagem.warn("Erro", "Erro ao Salvar pesquisa!");
                            dao.rollback();
                            return;
                        }
                        in.close();
                        save = true;
                    }
                } catch (IOException | JSONException e) {

                }
            }

            juridica.getPessoa().setNome(juridicaReceita.getNome().toUpperCase());
            juridica.setFantasia(juridicaReceita.getFantasia().toUpperCase());
            juridica.setDtAbertura(juridicaReceita.getDtAbertura());

            String emails[] = (juridicaReceita.getEmail() == null) ? "".split("") : juridicaReceita.getEmail().toLowerCase().split(" ");
            String telefones[] = (juridicaReceita.getTelefone() == null) ? "".split("") : juridicaReceita.getTelefone().split(" / ");

            if (!emails[0].isEmpty()) {
                juridica.setContabilidade(dbj.pesquisaContabilidadePorEmail(emails[0]));
                if (juridica.getContabilidade() != null) {
                    // nomeContabilidade = juridica.getContabilidade().getPessoa().getNome();
                }
            }

            switch (emails.length) {
                case 1:
                    juridica.getPessoa().setEmail1(emails[0]);
                    break;
                case 2:
                    juridica.getPessoa().setEmail1(emails[0]);
                    juridica.getPessoa().setEmail2(emails[1]);
                    break;
                case 3:
                    juridica.getPessoa().setEmail1(emails[0]);
                    juridica.getPessoa().setEmail2(emails[1]);
                    juridica.getPessoa().setEmail3(emails[2]);
                    break;
            }

            switch (telefones.length) {
                case 1:
                    juridica.getPessoa().setTelefone1(telefones[0]);
                    break;
                case 2:
                    juridica.getPessoa().setTelefone1(telefones[0]);
                    juridica.getPessoa().setTelefone2(telefones[1]);
                    break;
                case 3:
                    juridica.getPessoa().setTelefone1(telefones[0]);
                    juridica.getPessoa().setTelefone2(telefones[1]);
                    juridica.getPessoa().setTelefone3(telefones[2]);
                    break;
            }

            String result[] = juridicaReceita.getCnae().split(" ");
            CnaeDao dbc = new CnaeDao();
            String cnaex = result[result.length - 1].replace("(", "").replace(")", "");
            List<Cnae> listac = dbc.pesquisaCnae(cnaex, "cnae", "I");

            if (listac.isEmpty()) {
                GenericaMensagem.warn("Erro", "Erro ao pesquisar CNAE");
                dao.rollback();
                return;
            }
            // retornaCnaeReceita(listac.get(0));

            PessoaEnderecoDao dbe = new PessoaEnderecoDao();

            String cep = juridicaReceita.getCep();
            cep = cep.replace(".", "").replace("-", "");

            String descricao[] = AnaliseString.removerAcentos(juridicaReceita.getDescricaoEndereco()).split(" ");
            String bairros[] = AnaliseString.removerAcentos(juridicaReceita.getBairro()).split(" ");

            Endereco endereco = dbe.enderecoReceita(cep, descricao, bairros);
            if (endereco != null) {
                TipoEnderecoDao dbt = new TipoEnderecoDao();
                List tiposE = dbt.listaTipoEnderecoParaJuridica();
                for (Object tiposE1 : tiposE) {
                    pessoaEndereco.setEndereco(endereco);
                    pessoaEndereco.setTipoEndereco((TipoEndereco) tiposE1);
                    pessoaEndereco.setPessoa(juridica.getPessoa());
                    pessoaEndereco.setNumero(juridicaReceita.getNumero());
                    pessoaEndereco.setComplemento(juridicaReceita.getComplemento());
                    pessoaEndereco = new PessoaEndereco();
                }
            } else {
                String msg = "Endereço não encontrado no Sistema - CEP: " + juridicaReceita.getCep() + " DESC: " + juridicaReceita.getDescricaoEndereco() + " BAIRRO: " + juridicaReceita.getBairro();
                GenericaMensagem.warn("Atenção", msg);
                dao.rollback();
                return;
            }
        }
        if (save) {
            if (juridica.getId() == -1) {
                if (!dao.save(juridica.getPessoa())) {
                    GenericaMensagem.warn("Erro", "Ao salvar pessoa!");
                    return;
                }
                if (!dao.save(juridica)) {
                    GenericaMensagem.warn("Erro", "Ao salvar pessoa jurídica!");
                    dao.rollback();
                    return;
                }
                for (int i = 0; i < listPessoaEndereco.size(); i++) {
                    listPessoaEndereco.get(i).setPessoa(juridica.getPessoa());
                    if (!dao.save(listPessoaEndereco.get(i))) {
                        GenericaMensagem.warn("Erro", "Ao salvar pessoa jurídica!");
                        return;
                    }
                }
                dao.commit();
            }
        }
        loadPessoaEndereco();
        loadCnaeConvencao();
        loadGrupoCidade();
        loadOposicao();
    }

    public void loadPessoaEndereco() {
        listPessoaEndereco = new ArrayList<>();
        PessoaEnderecoDao db = new PessoaEnderecoDao();
        listPessoaEndereco = db.pesquisaEndPorPessoa(juridica.getPessoa().getId());
    }

    public void loadGrupoCidade() {
        ConvencaoCidadeDao dbCon = new ConvencaoCidadeDao();
        if (convencao.getId() != -1 && !listPessoaEndereco.isEmpty()) {
            grupoCidade = dbCon.pesquisaGrupoCidadeJuridica(convencao.getId(), ((PessoaEndereco) listPessoaEndereco.get(3)).getEndereco().getCidade().getId());
        }
    }

    public void loadCnaeConvencao() {
        CnaeConvencaoDao dbCnae = new CnaeConvencaoDao();
        if (juridica.getCnae() != null && juridica.getCnae().getId() != -1) {
            cnaeConvencao = dbCnae.pesquisaCnaeComConvencao(juridica.getCnae().getId());
            convencao = cnaeConvencao.getConvencao();
        } else {
            cnaeConvencao = new CnaeConvencao();
        }
    }

    public void loadOposicao() {
        listOposicao = new ArrayList<>();
        listOposicao = new OposicaoDao().listaOposicaoEmpresaID(juridica.getId());
    }

    public void accordion(TabChangeEvent event) {
    }

    public void pesquisaDocumento() {
        JuridicaDao db = new JuridicaDao();
        if (!juridica.getPessoa().getDocumento().isEmpty()) {
            List<Juridica> lista = db.pesquisaJuridicaPorDoc(juridica.getPessoa().getDocumento());
            if (!lista.isEmpty()) {
                GenericaMensagem.warn("Erro", "Esse documento já existe para: " + lista.get(0).getPessoa().getNome());
            }
        }
    }

    public Juridica getJuridica() {
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

    public PessoaEndereco getPessoaEndereco() {
        return pessoaEndereco;
    }

    public void setPessoaEndereco(PessoaEndereco pessoaEndereco) {
        this.pessoaEndereco = pessoaEndereco;
    }

    public JuridicaReceita getJuridicaReceita() {
        return juridicaReceita;
    }

    public void setJuridicaReceita(JuridicaReceita juridicaReceita) {
        this.juridicaReceita = juridicaReceita;
    }

    public ConfiguracaoCnpj getConfiguracaoCnpj() {
        return configuracaoCnpj;
    }

    public void setConfiguracaoCnpj(ConfiguracaoCnpj configuracaoCnpj) {
        this.configuracaoCnpj = configuracaoCnpj;
    }

    public List<PessoaEndereco> getListPessoaEndereco() {
        return listPessoaEndereco;
    }

    public void setListPessoaEndereco(List<PessoaEndereco> listPessoaEndereco) {
        this.listPessoaEndereco = listPessoaEndereco;
    }

    public GrupoCidade getGrupoCidade() {
        return grupoCidade;
    }

    public void setGrupoCidade(GrupoCidade grupoCidade) {
        this.grupoCidade = grupoCidade;
    }

    public Convencao getConvencao() {
        return convencao;
    }

    public void setConvencao(Convencao convencao) {
        this.convencao = convencao;
    }

    public CnaeConvencao getCnaeConvencao() {
        return cnaeConvencao;
    }

    public void setCnaeConvencao(CnaeConvencao cnaeConvencao) {
        this.cnaeConvencao = cnaeConvencao;
    }

    public List<Oposicao> getListOposicao() {
        return listOposicao;
    }

    public void setListOposicao(List<Oposicao> listOposicao) {
        this.listOposicao = listOposicao;
    }
}
