package br.com.rtools.associativo.utils;

import br.com.rtools.arrecadacao.beans.ConfiguracaoArrecadacaoBean;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.dao.MovimentoDao;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.dao.FisicaDao;
import br.com.rtools.pessoa.dao.PessoaEnderecoDao;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.utilitarios.SegurancaUtilitariosBean;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.Reports;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class PlanilhasSocialUtils implements Serializable {

    public void print(List<Movimento> movimentos) {
        try {
            Collection collection = new ArrayList();
            PessoaEnderecoDao pesEndDB = new PessoaEnderecoDao();
            PessoaEndereco pe;

            String swap[] = new String[35];
            Pessoa pessoa;

            if (movimentos.isEmpty()) {
                return;
            }

            pessoa = movimentos.get(0).getPessoa();

            Fisica fisica;
            try {
                fisica = new FisicaDao().pesquisaFisicaPorPessoa(pessoa.getId());
                swap[0] = fisica.getPessoa().getNome();
                swap[1] = fisica.getPessoa().getDocumento();
            } catch (Exception e) {
                swap[0] = "";
                swap[1] = "";
            }

            try {
                pe = pesEndDB.pesquisaEndPorPessoaTipo(pessoa.getId(), 3);
                swap[2] = pe.getEndereco().getEnderecoSimplesToString();
                swap[3] = pe.getNumero();
                swap[4] = pe.getComplemento();
                swap[5] = pe.getEndereco().getBairro().getDescricao();
                swap[6] = pe.getEndereco().getCidade().getCidade();
                swap[7] = pe.getEndereco().getCidade().getUf();
                swap[8] = pe.getEndereco().getCep().substring(0, 5) + "-" + pe.getEndereco().getCep().substring(5);
                swap[9] = pessoa.getTelefone1();
            } catch (Exception e) {
                swap[2] = "";
                swap[3] = "";
                swap[4] = "";
                swap[5] = "";
                swap[6] = "";
                swap[7] = "";
                swap[8] = "";
                swap[9] = "";
            }

            MovimentoDao dbm = new MovimentoDao();
            for (int i = 0; i < movimentos.size(); i++) {
                List<Vector> lAcres = dbm.pesquisaAcrescimo(movimentos.get(i).getId());

                BigDecimal valor, multa, juros, correcao, desconto;

                valor = new BigDecimal(movimentos.get(i).getValor());
                multa = new BigDecimal(movimentos.get(i).getMulta());
                juros = new BigDecimal(movimentos.get(i).getJuros());
                correcao = new BigDecimal(movimentos.get(i).getCorrecao());
                desconto = new BigDecimal(movimentos.get(i).getDesconto());
//                if (lAcres.isEmpty()) {
//                } else {
//                    valor = new BigDecimal((Double) lAcres.get(0).get(0));
//                    multa = new BigDecimal((Double) lAcres.get(0).get(1));
//                    juros = new BigDecimal((Double) lAcres.get(0).get(2));
//                    correcao = new BigDecimal((Double) lAcres.get(0).get(3));
//                    desconto = new BigDecimal((Double) lAcres.get(0).get(4));
//                }

                BigDecimal valor_calculado
                        = new BigDecimal(
                                Moeda.soma(
                                        movimentos.get(i).getValor(), Moeda.subtracao(
                                        Moeda.soma(
                                                Moeda.soma(multa.doubleValue(), juros.doubleValue()), correcao.doubleValue()), desconto.doubleValue()
                                )
                                )
                        );
                if (movimentos.get(i).getTipoServico().getId() == 4) {
                    collection.add(new PlanilhaDebitosSocial(
                            swap[0], // nome
                            swap[1], // documento
                            swap[2], // endereco
                            swap[3], // numero
                            swap[4], // complemento
                            swap[5], // bairro
                            swap[6], // cidade
                            swap[7], // cep
                            swap[8], // uf
                            swap[9], // telefone,
                            movimentos.get(i).getBeneficiario().getNome(), // beneficiário
                            "", // obs
                            movimentos.get(i).getDtVencimento(), // vencto
                            valor_calculado, // vlr pagar
                            valor,
                            multa,
                            juros,
                            correcao,
                            desconto,
                            movimentos.get(i).getServicos().getDescricao(), // servico
                            movimentos.get(i).getTipoServico().getDescricao(),
                            movimentos.get(i).getReferencia(),
                            Usuario.getUsuario().getPessoa().getNome()
                    ));
                } else {
                    collection.add(new PlanilhaDebitosSocial(
                            swap[0], // nome
                            swap[1], // documento
                            swap[2], // endereco
                            swap[3], // numero
                            swap[4], // complemento
                            swap[5], // bairro
                            swap[6], // cidade
                            swap[7], // cep
                            swap[8], // uf
                            swap[9], // telefone
                            movimentos.get(i).getBeneficiario().getNome(), // beneficiário
                            "", // obs
                            movimentos.get(i).getDtVencimento(), // vencto
                            valor_calculado, // vlr pagar
                            valor,
                            multa,
                            juros,
                            correcao,
                            desconto,
                            movimentos.get(i).getServicos().getDescricao(), // servico
                            movimentos.get(i).getTipoServico().getDescricao(),
                            movimentos.get(i).getReferencia(),
                            Usuario.getUsuario().getPessoa().getNome()
                    ));
                }

            }
//            
            ConfiguracaoArrecadacaoBean cab = new ConfiguracaoArrecadacaoBean();
            cab.init();

            Reports reports = new Reports();
            reports.HEADER = true;
            reports.TITLE = "PLANILHA DE DÉBITOS SOCIAL";
            reports.COLLECTION = collection;
            reports.JASPER_FILE = "PLANILHA_DE_DEBITO_SOCIAL";
            reports.JASPER_NAME = "planilha_" + movimentos.get(0).getPessoa().getNome();
            reports.FILIAL = new SegurancaUtilitariosBean().getMacFilial().getFilial();
            reports.print();

        } catch (Exception erro) {
            System.err.println("O arquivo não foi gerado corretamente! Erro: " + erro.getMessage());
        }
    }

    public class PlanilhaDebitosSocial {

        private Object nome;
        private Object documento;
        private Object endereco;
        private Object numero;
        private Object complemento;
        private Object bairro;
        private Object cidade;
        private Object uf;
        private Object cep;
        private Object telefone;
        private Object beneficiario;
        private Object observacao;
        private Object vencimento;
        private Object valor_calculado;
        private Object valor;
        private Object multa;
        private Object juros;
        private Object correcao;
        private Object desconto;
        private Object servico;
        private Object tipo_servico;
        private Object referencia;
        private Object usuario;

        public PlanilhaDebitosSocial(Object nome, Object documento, Object endereco, Object numero, Object complemento, Object bairro, Object cidade, Object uf, Object cep, Object telefone, Object beneficiario, Object observacao, Object vencimento, Object valor_calculado, Object valor, Object multa, Object juros, Object correcao, Object desconto, Object servico, Object tipo_servico, Object referencia, Object usuario) {
            this.nome = nome;
            this.documento = documento;
            this.endereco = endereco;
            this.numero = numero;
            this.complemento = complemento;
            this.bairro = bairro;
            this.cidade = cidade;
            this.uf = uf;
            this.cep = cep;
            this.telefone = telefone;
            this.beneficiario = beneficiario;
            this.observacao = observacao;
            this.vencimento = vencimento;
            this.valor_calculado = valor_calculado;
            this.valor = valor;
            this.multa = multa;
            this.juros = juros;
            this.correcao = correcao;
            this.desconto = desconto;
            this.servico = servico;
            this.tipo_servico = tipo_servico;
            this.referencia = referencia;
            this.usuario = usuario;
        }

        public Object getNome() {
            return nome;
        }

        public void setNome(Object nome) {
            this.nome = nome;
        }

        public Object getDocumento() {
            return documento;
        }

        public void setDocumento(Object documento) {
            this.documento = documento;
        }

        public Object getEndereco() {
            return endereco;
        }

        public void setEndereco(Object endereco) {
            this.endereco = endereco;
        }

        public Object getNumero() {
            return numero;
        }

        public void setNumero(Object numero) {
            this.numero = numero;
        }

        public Object getComplemento() {
            return complemento;
        }

        public void setComplemento(Object complemento) {
            this.complemento = complemento;
        }

        public Object getBairro() {
            return bairro;
        }

        public void setBairro(Object bairro) {
            this.bairro = bairro;
        }

        public Object getCidade() {
            return cidade;
        }

        public void setCidade(Object cidade) {
            this.cidade = cidade;
        }

        public Object getUf() {
            return uf;
        }

        public void setUf(Object uf) {
            this.uf = uf;
        }

        public Object getCep() {
            return cep;
        }

        public void setCep(Object cep) {
            this.cep = cep;
        }

        public Object getTelefone() {
            return telefone;
        }

        public void setTelefone(Object telefone) {
            this.telefone = telefone;
        }

        public Object getObservacao() {
            return observacao;
        }

        public void setObservacao(Object observacao) {
            this.observacao = observacao;
        }

        public Object getVencimento() {
            return vencimento;
        }

        public void setVencimento(Object vencimento) {
            this.vencimento = vencimento;
        }

        public Object getValor_calculado() {
            return valor_calculado;
        }

        public void setValor_calculado(Object valor_calculado) {
            this.valor_calculado = valor_calculado;
        }

        public Object getValor() {
            return valor;
        }

        public void setValor(Object valor) {
            this.valor = valor;
        }

        public Object getMulta() {
            return multa;
        }

        public void setMulta(Object multa) {
            this.multa = multa;
        }

        public Object getJuros() {
            return juros;
        }

        public void setJuros(Object juros) {
            this.juros = juros;
        }

        public Object getCorrecao() {
            return correcao;
        }

        public void setCorrecao(Object correcao) {
            this.correcao = correcao;
        }

        public Object getDesconto() {
            return desconto;
        }

        public void setDesconto(Object desconto) {
            this.desconto = desconto;
        }

        public Object getServico() {
            return servico;
        }

        public void setServico(Object servico) {
            this.servico = servico;
        }

        public Object getTipo_servico() {
            return tipo_servico;
        }

        public void setTipo_servico(Object tipo_servico) {
            this.tipo_servico = tipo_servico;
        }

        public Object getReferencia() {
            return referencia;
        }

        public void setReferencia(Object referencia) {
            this.referencia = referencia;
        }

        public Object getUsuario() {
            return usuario;
        }

        public void setUsuario(Object usuario) {
            this.usuario = usuario;
        }

        public Object getBeneficiario() {
            return beneficiario;
        }

        public void setBeneficiario(Object beneficiario) {
            this.beneficiario = beneficiario;
        }

    }

}
