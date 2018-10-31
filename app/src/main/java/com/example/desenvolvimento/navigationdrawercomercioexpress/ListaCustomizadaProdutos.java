package com.example.desenvolvimento.navigationdrawercomercioexpress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Desenvolvimento on 18/01/2016.
 */
public class ListaCustomizadaProdutos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_produtos_customizada_completa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<Integer> icones = new ArrayList<>();
        icones.add(R.drawable.cataflam);
        icones.add(R.drawable.cataflam);
        icones.add(R.drawable.cataflam);
        icones.add(R.drawable.cataflam);
        icones.add(R.drawable.cataflam);
        icones.add(R.drawable.cataflam);

        List<String> descricao = new ArrayList<>();
        descricao.add("Cataflam 01");
        descricao.add("Cataflam 02");
        descricao.add("Cataflam 03");
        descricao.add("Cataflam 04");
        descricao.add("Cataflam 05");
        descricao.add("Cataflam 06");


        List<String> itensRestantes = new ArrayList<>();
        itensRestantes.add("01");
        itensRestantes.add("03");
        itensRestantes.add("04");
        itensRestantes.add("02");
        itensRestantes.add("02");
        itensRestantes.add("00");

        List<String> valorProdutos = new ArrayList<>();
        itensRestantes.add("01");
        itensRestantes.add("03");
        itensRestantes.add("04");
        itensRestantes.add("02");
        itensRestantes.add("02");
        itensRestantes.add("00");

        List<String> valorAtacado = new ArrayList<>();
        valorAtacado.add("01");
        valorAtacado.add("03");
        valorAtacado.add("04");
        valorAtacado.add("02");
        valorAtacado.add("02");
        valorAtacado.add("00");

        ListView lista = (ListView) findViewById(R.id.lista);

        View.OnClickListener myhandler = new View.OnClickListener() {
            public void onClick(View v) {
                // MY QUESTION STARTS HERE!!!
                // IF b1 do this
                // IF b2 do this
                // MY QUESTION ENDS HERE!!!
            }
        };

        ListaProdutosCustomizadaAdapter adapter = new ListaProdutosCustomizadaAdapter(this, icones, descricao, itensRestantes, valorProdutos, valorAtacado);
        lista.setAdapter(adapter);

    }
}
