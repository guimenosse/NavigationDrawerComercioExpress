package classes;

public class CL_ItemPedido {

    protected String id;
    protected String numPedido;
    protected String cdProduto;
    protected String descricao;
    protected String qtde;
    protected String percDesconto;
    protected String vlMaxDescPermitido;
    protected String vlDesconto;
    protected String vlUnitario;
    protected String vlLiquido;
    protected String vlTotal;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumPedido() {
        return numPedido;
    }

    public void setNumPedido(String numPedido) {
        this.numPedido = numPedido;
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

    public String getQtde() {
        return qtde;
    }

    public void setQtde(String qtde) {
        this.qtde = qtde;
    }

    public String getPercDesconto() {
        return percDesconto;
    }

    public void setPercDesconto(String percDesconto) {
        this.percDesconto = percDesconto;
    }

    public String getVlMaxDescPermitido() {
        return vlMaxDescPermitido;
    }

    public void setVlMaxDescPermitido(String vlMaxDescPermitido) {
        this.vlMaxDescPermitido = vlMaxDescPermitido;
    }

    public String getVlDesconto() {
        return vlDesconto;
    }

    public void setVlDesconto(String vlDesconto) {
        this.vlDesconto = vlDesconto;
    }

    public String getVlUnitario() {
        return vlUnitario;
    }

    public void setVlUnitario(String vlUnitario) {
        this.vlUnitario = vlUnitario;
    }

    public String getVlLiquido() {
        return vlLiquido;
    }

    public void setVlLiquido(String vlLiquido) {
        this.vlLiquido = vlLiquido;
    }

    public String getVlTotal() {
        return vlTotal;
    }

    public void setVlTotal(String vlTotal) {
        this.vlTotal = vlTotal;
    }
}
