package classes;

public class CL_Produtos {

    protected String id;
    protected String cdProduto;
    protected String descricao;
    protected String complementoDescricao;
    protected String estoqueAtual;
    protected String vlUnitario;
    protected String vlAtacado;
    protected String descMaxPermitido;
    protected String descMaxPermitidoA;
    protected String descMaxPermitidoB;
    protected String descMaxPermitidoC;
    protected String descMaxPermitidoD;
    protected String descMaxPermitidoE;
    protected String descMaxPermitidoFidelidade;
    protected String cdRefEstoque;

    protected String qtdeDisponivel;

    protected String dtUltimaAlteracao;

    public CL_Produtos(){
        this.id = "";
        this.cdProduto = "";
        this.descricao = "";
        this.complementoDescricao = "";
        this.estoqueAtual = "";
        this.vlUnitario = "";
        this.vlAtacado = "";
        this.descMaxPermitido = "";
        this.descMaxPermitidoA = "";
        this.descMaxPermitidoB = "";
        this.descMaxPermitidoC = "";
        this.descMaxPermitidoD = "";
        this.descMaxPermitidoE = "";
        this.descMaxPermitidoFidelidade = "";
        this.qtdeDisponivel = "";
        this.cdRefEstoque = "";
        this.dtUltimaAlteracao = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCdProduto() {
        return cdProduto;
    }

    public void setCdProduto(String cdProduto) {
        this.cdProduto = cdProduto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getComplementoDescricao() {
        return descricao;
    }

    public void setComplementoDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEstoqueAtual() {
        return estoqueAtual;
    }

    public void setEstoqueAtual(String estoqueAtual) {
        this.estoqueAtual = estoqueAtual;
    }

    public String getVlUnitario() {
        return vlUnitario;
    }

    public void setVlUnitario(String vlUnitario) {
        this.vlUnitario = vlUnitario;
    }

    public String getVlAtacado() {
        return vlAtacado;
    }

    public void setVlAtacado(String vlAtacado) {
        this.vlAtacado = vlAtacado;
    }

    public String getDescMaxPermitido() {
        return descMaxPermitido;
    }

    public void setDescMaxPermitido(String descMaxPermitido) {
        this.descMaxPermitido = descMaxPermitido;
    }

    public String getDescMaxPermitidoA() {
        return descMaxPermitidoA;
    }

    public void setDescMaxPermitidoA(String descMaxPermitidoA) {
        this.descMaxPermitidoA = descMaxPermitidoA;
    }

    public String getDescMaxPermitidoB() {
        return descMaxPermitidoB;
    }

    public void setDescMaxPermitidoB(String descMaxPermitidoB) {
        this.descMaxPermitidoB = descMaxPermitidoB;
    }

    public String getDescMaxPermitidoC() {
        return descMaxPermitidoC;
    }

    public void setDescMaxPermitidoC(String descMaxPermitidoC) {
        this.descMaxPermitidoC = descMaxPermitidoC;
    }

    public String getDescMaxPermitidoD() {
        return descMaxPermitidoD;
    }

    public void setDescMaxPermitidoD(String descMaxPermitidoD) {
        this.descMaxPermitidoD = descMaxPermitidoD;
    }

    public String getDescMaxPermitidoE() {
        return descMaxPermitidoE;
    }

    public void setDescMaxPermitidoE(String descMaxPermitidoE) {
        this.descMaxPermitidoE = descMaxPermitidoE;
    }

    public String getDescMaxPermitidoFidelidade() {
        return descMaxPermitidoFidelidade;
    }

    public void setDescMaxPermitidoFidelidade(String descMaxPermitidoFidelidade) {
        this.descMaxPermitidoFidelidade = descMaxPermitidoFidelidade;
    }

    public String getQtdeDisponivel() {
        return qtdeDisponivel;
    }

    public void setQtdeDisponivel(String qtdeDisponivel) {
        this.qtdeDisponivel = qtdeDisponivel;
    }

    public String getCdRefEstoque() {
        return cdRefEstoque;
    }

    public void setCdRefEstoque(String cdRefEstoque) {
        this.cdRefEstoque = cdRefEstoque;
    }

    public String getDtUltimaAlteracao() {
        return dtUltimaAlteracao;
    }

    public void setDtUltimaAlteracao(String dtUltimaAlteracao) {
        this.dtUltimaAlteracao = dtUltimaAlteracao;
    }
}
