package classes;

public class CL_Configuracao {

    protected String fgControlaEstoquePedido;
    protected String fgPrecoIndividualizado;

    public CL_Configuracao(){

        this.fgControlaEstoquePedido = "";
        this.fgPrecoIndividualizado = "";
    }

    public String getFgControlaEstoquePedido() {
        return fgControlaEstoquePedido;
    }

    public void setFgControlaEstoquePedido(String fgControlaEstoquePedido) {
        this.fgControlaEstoquePedido = fgControlaEstoquePedido;
    }

    public String getFgPrecoIndividualizado() {
        return fgPrecoIndividualizado;
    }

    public void setFgPrecoIndividualizado(String fgPrecoIndividualizado) {
        this.fgPrecoIndividualizado = fgPrecoIndividualizado;
    }
}
