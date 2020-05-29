package classes;

public class CL_Filial {

    protected String id;
    protected String cdFilial;
    protected String nomeFilial;
    protected String autorizaTrocaFilial;
    protected int totalFiliais;
    protected String precoIndividualizado;

    public CL_Filial(){
        this.id = "";
        this.cdFilial = "";
        this.nomeFilial = "";
        this.autorizaTrocaFilial = "N";
        this.totalFiliais = 0;
        this.precoIndividualizado = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCdFilial() {
        return cdFilial;
    }

    public void setCdFilial(String cdFilial) {
        this.cdFilial = cdFilial;
    }

    public String getNomeFilial() {
        return nomeFilial;
    }

    public void setNomeFilial(String nomeFilial) {
        this.nomeFilial = nomeFilial;
    }

    public String getAutorizaTrocaFilial() {
        return autorizaTrocaFilial;
    }

    public void setAutorizaTrocaFilial(String autorizaTrocaFilial) {
        this.autorizaTrocaFilial = autorizaTrocaFilial;
    }

    public int getTotalFiliais() {
        return totalFiliais;
    }

    public void setTotalFiliais(int totalFiliais) {
        this.totalFiliais = totalFiliais;
    }

    public String getPrecoIndividualizado() {
        return precoIndividualizado;
    }

    public void setPrecoIndividualizado(String precoIndividualizado) {
        this.precoIndividualizado = precoIndividualizado;
    }
}
