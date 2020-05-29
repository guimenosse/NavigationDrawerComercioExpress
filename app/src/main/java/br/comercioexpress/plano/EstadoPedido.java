package br.comercioexpress.plano;

import java.io.Serializable;

/**
 * Created by Desenvolvimento on 20/01/2016.
 */
public class EstadoPedido implements Serializable {
    public String cliente;
    public static final String KEY = "dados";

    public EstadoPedido(String cliente){
        this.cliente = cliente;
    }
}
