package classes;

public class CL_Clientes {

    /*public String [] arr_CamposCliente = {banco.ID, banco.RZSOCIAL, banco.NMFANTASIA, banco.CEP, banco.ENDERECO,
            banco.NUMERO, banco.COMPLEMENTO, banco.BAIRRO, banco.UF, banco.CIDADE, banco.CNPJ,
            banco.TELEFONE, banco.TELEFONEADICIONAL, banco.FAX, banco.CONTATO, banco.EMAIL, banco.TIPCLIENTE,
            banco.VENDEDOR, banco.DTULTALTERACAO, banco.DTCADASTRO, banco.TIPOPESSOA, banco.FGSINCRONIZADO, banco.OBSCLIENTE,
            banco.CLASSIFICACAO};*/

    protected String id;
    protected String cdCliente;
    protected String nomeRzSocial;
    protected String nomeFantasia;
    protected String cep;
    protected String endereco;
    protected String numEndereco;
    protected String complemento;
    protected String bairro;
    protected String cidade;
    protected String uf;
    protected String cpfCnpj;
    protected String telefone;
    protected String telefoneAdicional;
    protected String fax;
    protected String nomeContato;
    protected String email;
    protected String tipoCliente;
    protected String vendedor;
    protected String tipoPessoa;
    protected String fgSincronizado;
    protected String observacao;
    protected String classificacao;
    protected String fgBloqueio;

    //INSCESTADUAL FIDELIDADE TIPOPRECO

    protected String inscEstadual;
    protected String fidelidade;
    protected String tipoPreco;

    protected String dtUltimaAlteracao;
    protected String dtCadastro;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCdCliente() {
        return cdCliente;
    }

    public void setCdCliente(String cdCliente) {
        this.cdCliente = cdCliente;
    }

    public String getNomeRzSocial() {
        return nomeRzSocial;
    }

    public void setNomeRzSocial(String nomeRzSocial) {
        this.nomeRzSocial = nomeRzSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumEndereco() {
        return numEndereco;
    }

    public void setNumEndereco(String numEndereco) {
        this.numEndereco = numEndereco;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTelefoneAdicional() {
        return telefoneAdicional;
    }

    public void setTelefoneAdicional(String telefoneAdicional) {
        this.telefoneAdicional = telefoneAdicional;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getNomeContato() {
        return nomeContato;
    }

    public void setNomeContato(String nomeContato) {
        this.nomeContato = nomeContato;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getFgSincronizado() {
        return fgSincronizado;
    }

    public void setFgSincronizado(String fgSincronizado) {
        this.fgSincronizado = fgSincronizado;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }

    public String getInscEstadual() {
        return inscEstadual;
    }

    public void setInscEstadual(String inscEstadual) {
        this.inscEstadual = inscEstadual;
    }

    public String getFidelidade() {
        return fidelidade;
    }

    public void setFidelidade(String fidelidade) {
        this.fidelidade = fidelidade;
    }

    public String getTipoPreco() {
        return tipoPreco;
    }

    public void setTipoPreco(String tipoPreco) {
        this.tipoPreco = tipoPreco;
    }

    public String getDtUltimaAlteracao() {
        return dtUltimaAlteracao;
    }

    public void setDtUltimaAlteracao(String dtUltimaAlteracao) {
        this.dtUltimaAlteracao = dtUltimaAlteracao;
    }

    public String getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(String dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public String getFgBloqueio() {
        return fgBloqueio;
    }

    public void setFgBloqueio(String fgBloqueio) {
        this.fgBloqueio = fgBloqueio;
    }
}
