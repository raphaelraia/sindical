package br.com.rtools.pessoa.beans;

import br.com.rtools.endereco.Bairro;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.endereco.DescricaoEndereco;
import br.com.rtools.endereco.Endereco;
import br.com.rtools.endereco.Logradouro;
import br.com.rtools.endereco.dao.BairroDao;
import br.com.rtools.endereco.dao.CidadeDao;
import br.com.rtools.endereco.dao.DescricaoEnderecoDao;
import br.com.rtools.endereco.dao.EnderecoDao;
import br.com.rtools.endereco.dao.LogradouroDao;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.FisicaImportacao;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaComplemento;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.PessoaProfissao;
import br.com.rtools.pessoa.Profissao;
import br.com.rtools.pessoa.TipoDocumento;
import br.com.rtools.pessoa.TipoEndereco;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.FisicaImportacaoDao;
import br.com.rtools.pessoa.dao.ProfissaoDao;
import br.com.rtools.pessoa.utilitarios.PessoaUtilitarios;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.sistema.ConfiguracaoUpload;
import br.com.rtools.sistema.Critica;
import br.com.rtools.sistema.SisProcesso;
import br.com.rtools.utilitarios.CEPService;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Mask;
import br.com.rtools.utilitarios.Upload;
import br.com.rtools.utilitarios.ValidaDocumentos;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;

@ManagedBean(name = "importacao")
@SessionScoped
@ApplicationScoped
public class ImportacaoBean implements Serializable {

    private FisicaImportacao fisicaImportacao = new FisicaImportacao();
    private List<FisicaImportacao> listFisicaImportacao;
    private String type = "fisica";
    private Boolean run = false;
    private Boolean cancel = false;
    private Thread thread;
    private Integer total;

    public void loadListFisicaImportacao() {
        listFisicaImportacao = new ArrayList();
    }

    public void uploadPessoaFisica(FileUploadEvent event) {
        type = "fisica";
        ConfiguracaoUpload configuracaoUpload = new ConfiguracaoUpload();
        configuracaoUpload.setArquivo(event.getFile().getFileName());
        configuracaoUpload.setDiretorio("arquivos/importacao/fisica/");
        configuracaoUpload.setEvent(event);
        configuracaoUpload.setSubstituir(true);
        configuracaoUpload.setRenomear("importacao.json");
        configuracaoUpload.setResourceFolder(true);
        Upload.enviar(configuracaoUpload, true);
    }

    public void proccess() {
        if (type.equals("fisica")) {
            run = true;
            fisica();
            // thread = new Thread(execute);
            // thread.start();
        }
    }

    public void finish() {
        List<TipoEndereco> listTipoEndereco;
        if (type.equals("fisica")) {
            Dao dao = new Dao();
            FisicaDao fisicaDao = new FisicaDao();
            listTipoEndereco = dao.find("TipoEndereco", new int[]{1, 3, 4});
            listFisicaImportacao = new Dao().list(new FisicaImportacao());
            dao.openTransaction();
            for (FisicaImportacao fi : listFisicaImportacao) {
                try {
                    Boolean a = false;
                    if (fi.getDtHomologacao() == null) {
                        PessoaProfissao pessoaProfissao = new PessoaProfissao();
                        TipoDocumento tipoDocumento = null;
                        String d = "";
                        if (fi.getDocumento().isEmpty() || fi.getDocumento().equals("0")) {
                            if (fi.getDtNascimento() != null) {
                                if (fisicaDao.pesquisaFisicaPorNomeNascimento(fi.getNome(), fi.getDtNascimento()) != null) {
                                    continue;
                                }
                            }
                            d = fi.getDocumento();
                            tipoDocumento = (TipoDocumento) dao.find(new TipoDocumento(), 4);
                        } else {
                            if (fi.getDocumento().length() == 11) {
                                fi.reviseDocumento();
                                d = Mask.cpf(fi.getDocumento());
                            }
                            if (!fisicaDao.pesquisaFisicaPorDoc(d).isEmpty()) {
                                continue;
                            }
                            fi.setDocumento(d);
                            tipoDocumento = (TipoDocumento) dao.find(new TipoDocumento(), 1);

                        }
                        /**
                         * Pessoa
                         */
                        Pessoa pessoa = new Pessoa();
                        pessoa.setNome(fi.getNome());
                        pessoa.setDocumento(d);
                        pessoa.setTelefone1(fi.getTelefone1());
                        pessoa.setTelefone2(fi.getTelefone2());
                        pessoa.setTelefone3(fi.getTelefone3());
                        pessoa.setTelefone4(fi.getTelefone4());
                        pessoa.setEmail1(fi.getEmail1());
                        pessoa.setEmail2(fi.getEmail2());
                        pessoa.setEmail3(fi.getEmail3());
                        pessoa.setObs(fi.getObservacao());
                        pessoa.setSite(fi.getSite());
                        pessoa.setTipoDocumento(tipoDocumento);
                        if (!dao.save(pessoa)) {
                            dao.rollback();
                            GenericaMensagem.warn("ERRO", "Ao salvar pessoa. " + dao.EXCEPCION.getMessage());
                            return;
                        }
                        /**
                         * Física
                         */
                        Fisica fisica = new Fisica();
                        fisica.setPessoa(pessoa);
                        fisica.setRg(fi.getRg());
                        fisica.setPai(fi.getPai());
                        fisica.setMae(fi.getMae());
                        fisica.setPis(fi.getPis());
                        fisica.setNacionalidade(fi.getNacionalidade());
                        if (fi.getNaturalidadeObjeto() != null) {
                            fisica.setNaturalidade(fi.getNaturalidadeObjeto().getCidade() + " - " + fi.getNaturalidadeObjeto().getUf());
                        }
                        fisica.setSexo(fi.getSexo());
                        fisica.setEstadoCivil(fi.getEstado_civil());
                        fisica.setFoto(fi.getFoto());
                        fisica.setDtAposentadoria(fi.getDtAposentadoria());
                        fisica.setDtNascimento(fi.getDtNascimento());
                        fisica.setCarteira(fi.getCarteira());
                        fisica.setSerie(fi.getSerie());
                        fisica.setTituloEleitor(fi.getTitulo_eleitor());
                        fisica.setTituloZona(fi.getTitulo_zona());
                        fisica.setTituloSecao(fi.getTitulo_secao());
                        fisica.setNit("");
                        fisica.setOrgaoEmissaoRG(fi.getOrgao_emissao_rg());
                        fisica.setUfEmissaoRG(fi.getUf_emissao_rg());
                        if (!dao.save(fisica)) {
                            dao.rollback();
                            GenericaMensagem.warn("ERRO", "Ao salvar física. " + dao.EXCEPCION.getMessage());
                            return;
                        }
                        if (fi.getProfissao() != null) {
                            pessoaProfissao.setFisica(fisica);
                            pessoaProfissao.setProfissao(fi.getProfissaoObjeto());
                            if (!dao.save(pessoaProfissao)) {
                                dao.rollback();
                                GenericaMensagem.warn("ERRO", "Ao salvar pessoa profissao. " + dao.EXCEPCION.getMessage());
                                return;
                            }
                        }
                        if (fi.getEndereco() != null) {
                            for (int z = 0; z < listTipoEndereco.size(); z++) {
                                PessoaEndereco pessoaEndereco = new PessoaEndereco();
                                pessoaEndereco.setPessoa(pessoa);
                                pessoaEndereco.setComplemento(fi.getComplemento());
                                pessoaEndereco.setNumero(fi.getNumero());
                                pessoaEndereco.setTipoEndereco(listTipoEndereco.get(z));
                                pessoaEndereco.setEndereco(fi.getEndereco());
                                if (!dao.save(pessoaEndereco)) {
                                    dao.rollback();
                                    GenericaMensagem.warn("ERRO", "Ao salvar pessoa endereço. " + dao.EXCEPCION.getMessage());
                                    return;
                                }

                            }
                        }
                        PessoaComplemento pessoaComplemento = new PessoaComplemento();
                        pessoaComplemento.setObsAviso("ATUALIZAR ESTE CADASTRO!");
                        pessoaComplemento.setPessoa(pessoa);
                        if (!dao.save(pessoaComplemento)) {
                            dao.rollback();
                            GenericaMensagem.warn("ERRO", "Ao salvar pessoa complemento. " + dao.EXCEPCION.getMessage());
                            return;
                        }
                        fi.setDtHomologacao(new Date());
                        fi.setFisica(fisica);
                        if (!dao.update(fi)) {
                            dao.rollback();
                            GenericaMensagem.warn("ERRO", "Ao homologar essa importação. " + dao.EXCEPCION.getMessage());
                            return;
                        }
                    }
                } catch (Exception e) {
                    dao.rollback();
                    GenericaMensagem.warn("ERRO", "Ao realizar importação. " + e.getMessage());
                    return;
                }

            }
            dao.commit();
            GenericaMensagem.info("Sucesso", "IMPORTAÇÃO REALIZADA COM SUCESSO.");
            // thread = new Thread(execute);
            // thread.start();
        }
    }

    public void cancel() {
        if (type.equals("fisica")) {
            run = false;
        }

    }

    private final Runnable execute = new Runnable() {
        @Override
        public void run() {
            // fisica();
        }
    };

    public void fisica() {
        Rotina r = new Rotina().get();
        SisProcesso sisProcesso = new SisProcesso();
        sisProcesso.start();
        try {
            String json = "";
            File file = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/resources/cliente/" + GenericaSessao.getString("sessaoCliente").toLowerCase() + "/arquivos/importacao/fisica/importacao.json"));
            Gson gson = new Gson();
            if (file.exists()) {
                json = FileUtils.readFileToString(file);
                // json = json.replace("[", "{");
                // json = json.replace("]", "}");
                // json = gson.toJson(json);
            }
            List<FisicaImportacao> list = new ArrayList();
            list = gson.fromJson(json, new TypeToken<List<FisicaImportacao>>() {
            }.getType()); // retorna um objeto User.
            total = list.size();
            boolean error = true;
            // System.out.println(String.format("%d-%s", list.toString()));
            Dao dao = new Dao();
            EnderecoDao enderecoDao = new EnderecoDao();
            List<Critica> listCritica = new ArrayList();
            List listNacionalidade = PessoaUtilitarios.loadListPaises();
            Registro registro = Registro.get();
            Endereco enderecoPrincipal = registro.getFilial().getPessoa().getPessoaEndereco().getEndereco();
            for (int i = 0; i < list.size(); i++) {
                try {
                    if (new FisicaImportacaoDao().find(list.get(i).getNome(), list.get(i).getDocumento()).isEmpty()) {
                        dao.openTransaction();
                        DescricaoEndereco descricaoEndereco = null;
                        Bairro bairro = null;
                        Cidade cidade = null;
                        Profissao profissao = null;
                        list.get(i).reviseDocumento();
                        list.get(i).revisePIS();
                        list.get(i).reviseSexo();
                        list.get(i).reviseEstadoCivil();
                        list.get(i).reviseCNPJ();
                        list.get(i).reviseTelefone();
                        list.get(i).reviseCEP();
                        list.get(i).reviseCTPS();
                        list.get(i).getDtNascimento();
                        list.get(i).getDtAposentadoria();
                        list.get(i).getDtFiliacao();
                        list.get(i).getDtInativacao();
                        list.get(i).getDtCriacao();
                        String d = Mask.cpf(list.get(i).getDocumento());
                        if (!list.get(i).getDocumento().isEmpty() && !list.get(i).getDocumento().equals("0")) {
                            if (!ValidaDocumentos.isValidoCPF(list.get(i).getDocumento())) {
                                listCritica.add(new Critica(r, list.get(i).getDocumento(), list.get(i).getNome(), "CPF INVÁLIDO! " + list.get(i).getDocumento()));
                            } else if (list.get(i).getDocumento().length() == 11) {
                                list.get(i).setDocumento(d);
                            } else {
                                listCritica.add(new Critica(r, list.get(i).getDocumento(), list.get(i).getNome(), "CPF COM ERROS NÃO DETECTADO(S)! " + d));
                            }
                        }
                        if (!list.get(i).getPis().isEmpty()) {
                            if (!ValidaDocumentos.isValidoPIS(list.get(i).getPis())) {
                                listCritica.add(new Critica(r, list.get(i).getDocumento(), list.get(i).getNome(), "PIS INVÁLIDO! " + list.get(i).getPis()));
                            } else {
                                list.get(i).setPis(Mask.pis(list.get(i).getPis()));
                            }
                        }
                        if (!list.get(i).getNaturalidade().isEmpty()) {
                            String n[] = list.get(i).getNaturalidade().split("-");
                            if (n.length == 0) {
                                n = list.get(i).getNaturalidade().split("/");
                            }
                            String city = list.get(i).getNaturalidade();
                            String state = enderecoPrincipal.getCidade().getUf();
                            if (n.length > 1) {
                                city = list.get(i).replace(n[0]).toUpperCase().trim();
                                state = list.get(i).replace(n[1]).toUpperCase().trim();
                                if (state.isEmpty()) {
                                    state = enderecoPrincipal.getCidade().getUf();
                                }
                            }
                            Cidade naturalidade = new CidadeDao().find(city, state);
                            if (naturalidade == null) {
                                List<Cidade> listCidade = new CidadeDao().findByCidade(city);
                                if (listCidade.isEmpty()) {
                                    listCritica.add(new Critica(r, list.get(i).getDocumento(), list.get(i).getNome(), "NATURALIDADE NÃO EXISTE NO SISTEMA! " + list.get(i).getNaturalidade()));
                                } else {
                                    naturalidade = listCidade.get(0);
                                    list.get(i).setNaturalidadeObjeto(naturalidade);
                                }
                            } else {
                                list.get(i).setNaturalidadeObjeto(naturalidade);
                            }
                        }
                        if (list.get(i).getNacionalidade().isEmpty()) {
                            list.get(i).setNacionalidade("Brasileira(o)");
                        } else {
                            Boolean nacionalidadeExiste = false;
                            for (int z = 0; z < listNacionalidade.size(); z++) {
                                if (listNacionalidade.get(z).toString().toUpperCase().contains(list.get(i).getNacionalidade().toUpperCase())) {
                                    list.get(i).setNacionalidade(listNacionalidade.get(z).toString());
                                    nacionalidadeExiste = true;
                                    break;
                                }
                            }
                            if (!nacionalidadeExiste) {
                                listCritica.add(new Critica(r, list.get(i).getDocumento(), list.get(i).getNome(), "NÚMERO NACIONALIDADE NÃO EXISTE! " + list.get(i).getNacionalidade()));
                            }
                        }
                        if (list.get(i).getProfissao().isEmpty()) {
                            profissao = (Profissao) dao.find(new Profissao(), 0);
                        } else {
                            profissao = new ProfissaoDao().find(list.get(i).getProfissao());
                            if (profissao == null) {
                                profissao = (Profissao) dao.find(new Profissao(), 0);
                            }
                        }
                        list.get(i).setProfissaoObjeto(profissao);
                        Boolean saveEndereco = true;
                        Endereco endereco = new Endereco();
                        if (!list.get(i).getLogradouro().isEmpty() || !list.get(i).getDescricao_endereco().isEmpty() || !list.get(i).getBairro().isEmpty() || !list.get(i).getCidade().isEmpty()) {
                            list.get(i).setDescricao_endereco(list.get(i).getDescricao_endereco().replace("'", " "));
                            List<Endereco> listEndereco = new ArrayList();
                            if (!list.get(i).getLogradouro().isEmpty() && !list.get(i).getDescricao_endereco().isEmpty() && !list.get(i).getBairro().isEmpty() && !list.get(i).getCidade().isEmpty()) {
                                listEndereco = enderecoDao.pesquisaEnderecoDes(list.get(i).getUf(), list.get(i).getCidade(), list.get(i).getLogradouro(), list.get(i).getDescricao_endereco(), "T");
                                endereco = new Endereco();
                            }
                            if (list.get(i).getNumero().isEmpty()) {
                                saveEndereco = false;
                                listCritica.add(new Critica(r, list.get(i).getDocumento(), list.get(i).getNome(), "NÚMERO NÃO INFORMADO!"));
                            }
                            Logradouro logradouro = null;
                            if (!listEndereco.isEmpty()) {
                                for (int z = 0; z < listEndereco.size(); z++) {
                                    if (listEndereco.get(z).getCep().endsWith(list.get(i).getCep())) {
                                        endereco = listEndereco.get(z);
                                        list.get(i).setCep(endereco.getCep());
                                        break;
                                    }
                                }
                                if (endereco.getId() == -1) {
                                    endereco = new Endereco();
                                    endereco = listEndereco.get(0);
                                    if (endereco != null && endereco.getId() != -1) {
                                        if (endereco.getCep().isEmpty()) {
                                            list.get(i).setCep(endereco.getCep());
                                        }
                                    }
                                }
                            } else {
                                if (list.get(i).getCep().isEmpty()) {
                                    saveEndereco = false;
                                    listCritica.add(new Critica(r, list.get(i).getDocumento(), list.get(i).getNome(), "CEP NÃO INFORMADO!"));
                                } else {
                                    CEPService cEPService = new CEPService();
                                    cEPService.setCep(list.get(i).getCep());
                                    cEPService.procurar(dao);
                                    if (cEPService.getEndereco() == null) {
                                        endereco = new Endereco();
                                    } else {
                                        endereco = cEPService.getEndereco();
                                    }
                                }

                                if (saveEndereco) {
                                    if (endereco.getId() == -1) {
                                        if (list.get(i).getLogradouro().isEmpty()) {
                                            saveEndereco = false;
                                            listCritica.add(new Critica(r, list.get(i).getDocumento(), list.get(i).getNome(), "LOGRADOURO NÃO INFORMADO!"));
                                        } else {
                                            logradouro = new LogradouroDao().find(list.get(i).getLogradouro());
                                            if (logradouro == null) {
                                                saveEndereco = false;
                                                listCritica.add(new Critica(r, list.get(i).getDocumento(), list.get(i).getNome(), "LOGRADOURO NÃO EXISTE NO SISTEMA! " + list.get(i).getLogradouro()));
                                                // logradouro = (Logradouro) dao.find(new Logradouro(), 0);
                                            }
                                        }
                                        if (list.get(i).getDescricao_endereco().isEmpty()) {
                                            descricaoEndereco = (DescricaoEndereco) dao.find(new DescricaoEndereco(), 0);
                                        } else {
                                            descricaoEndereco = new DescricaoEnderecoDao().find(list.get(i).getDescricao_endereco());
                                            if (descricaoEndereco == null) {
                                                descricaoEndereco = new DescricaoEndereco();
                                                descricaoEndereco.setAtivo(false);
                                                descricaoEndereco.setDescricao(list.get(i).getDescricao_endereco().trim());
                                                if (!dao.save(descricaoEndereco)) {
                                                    dao.rollback();
                                                    return;
                                                }
                                            }
                                        }
                                        if (list.get(i).getBairro().isEmpty()) {
                                            bairro = (Bairro) dao.find(new Bairro(), 0);
                                        } else {
                                            bairro = new BairroDao().find(list.get(i).getBairro());
                                            if (bairro == null) {
                                                bairro = new Bairro();
                                                bairro.setAtivo(false);
                                                bairro.setDescricao(list.get(i).getBairro().trim());
                                                if (!dao.save(bairro)) {
                                                    dao.rollback();
                                                    return;
                                                }
                                            }
                                        }
                                        if (list.get(i).getCidade().isEmpty()) {
                                            saveEndereco = false;
                                            listCritica.add(new Critica(r, list.get(i).getDocumento(), list.get(i).getNome(), "CIDADE NÃO INFORMADA!"));
                                            cidade = (Cidade) dao.find(new Cidade(), 0);
                                        } else {
                                            cidade = new CidadeDao().find(list.get(i).getCidade(), list.get(i).getUf());
                                            if (cidade == null) {
                                                saveEndereco = false;
                                                // cidade = (Cidade) dao.find(new Cidade(), 0);
                                                listCritica.add(new Critica(r, list.get(i).getDocumento(), list.get(i).getNome(), "CIDADE NÃO EXISTE NO SISTEMA! " + list.get(i).getCidade()));
                                            }
                                        }
                                    }
                                }

                            }

                            if ((endereco != null && endereco.getId() == -1) || endereco == null && saveEndereco) {
                                if (list.get(i).getCep().isEmpty()) {
                                    saveEndereco = false;
                                    listCritica.add(new Critica(r, list.get(i).getDocumento(), list.get(i).getNome(), "CEP NÃO ENCONTRADO!"));
                                }
                                endereco = new Endereco();
                                endereco.setCep(list.get(i).getCep());
                                endereco.setAtivo(false);
                                endereco.setLogradouro(logradouro);
                                endereco.setBairro(bairro);
                                endereco.setCidade(cidade);
                                endereco.setDescricaoEndereco(descricaoEndereco);
                                if (endereco.getId() == -1 && saveEndereco) {
                                    if (!dao.save(endereco)) {
                                        dao.rollback();
                                        return;
                                    }
                                }
                            } else if (endereco.getId() != -1) {
                                list.get(i).setEndereco(endereco);
                            }

                        }

                        if (!dao.save(list.get(i))) {
                            dao.rollback();
                            return;
                        }
                        dao.commit();
                    }
                } catch (Exception e2) {
                    e2.getMessage();
                }
                if (!run) {
                    break;
                }
            }
            for (int i = 0; i < listCritica.size(); i++) {
                new Dao().save(listCritica.get(i), true);
            }
            sisProcesso.finish();
            // Saída: id: 123; first name: Wektabyte
        } catch (Exception e) {
            e.getMessage();
        }
        run = false;

    }

    public String getPath() {
        return "resources/cliente/" + GenericaSessao.getString("sessaoCliente").toLowerCase() + "/arquivos/importacao/fisica/importacao.json";
    }

    public FisicaImportacao getFisicaImportacao() {
        return fisicaImportacao;
    }

    public void setFisicaImportacao(FisicaImportacao fisicaImportacao) {
        this.fisicaImportacao = fisicaImportacao;
    }

    public List<FisicaImportacao> getListFisicaImportacao() {
        return listFisicaImportacao;
    }

    public void setListFisicaImportacao(List<FisicaImportacao> listFisicaImportacao) {
        this.listFisicaImportacao = listFisicaImportacao;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getRun() {
        return run;
    }

    public void setRun(Boolean run) {
        this.run = run;
    }

    public Integer getTotal() {
        if (total == null || total == 0) {
            String json = "";
            File file = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/resources/cliente/" + GenericaSessao.getString("sessaoCliente").toLowerCase() + "/arquivos/importacao/fisica/importacao.json"));
            Gson gson = new Gson();
            if (file.exists()) {
                try {
                    json = FileUtils.readFileToString(file);
                    // json = json.replace("[", "{");
                    // json = json.replace("]", "}");
                    // json = gson.toJson(json);
                } catch (IOException ex) {
                    Logger.getLogger(ImportacaoBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            List<FisicaImportacao> list = new ArrayList();
            list = gson.fromJson(json, new TypeToken<List<FisicaImportacao>>() {
            }.getType()); // retorna um objeto User.
            total = list.size();
        }
        return total;
    }

    public Integer getProccessed() {
        return new FisicaImportacaoDao().count();
    }

    public Integer getPercent() {
        try {
            int proccessed = getProccessed() * 100;
            return proccessed / getTotal();
        } catch (Exception e) {
            return 0;
        }
    }
}
