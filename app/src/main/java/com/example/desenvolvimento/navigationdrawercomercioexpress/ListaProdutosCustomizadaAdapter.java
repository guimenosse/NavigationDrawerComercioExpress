package com.example.desenvolvimento.navigationdrawercomercioexpress;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Desenvolvimento on 18/01/2016.
 */
public class ListaProdutosCustomizadaAdapter extends BaseAdapter {

    private Context context;
    private List<Integer> imagem;
    private List<String> codigo;
    private List<String> descricao;
    private List<String> itensRestantes;
    private List<String> valorProdutos;
    private List<String> valorAtacado;

    public ListaProdutosCustomizadaAdapter(Context context, List<Integer> imagem, List<String> codigo, List<String> descricao, List<String> itensRestantes, List<String> valorProdutos, List<String> valorAtacado){
        this.context = context;
        this.imagem = imagem;
        this.codigo = codigo;
        this.descricao = descricao;
        this.itensRestantes = itensRestantes;
        this.valorProdutos = valorProdutos;
        this.valorAtacado = valorAtacado;
    }

    private class ViewHolder{
        ImageView imagemFruta;
        TextView textoCodigo;
        TextView textoDescricao;
        TextView numItensRestantes;
        TextView valor_produtos;
        TextView valor_atacado;
    }

    @Override
    public int getCount() {
        return imagem.size();
    }

    @Override
    public Object getItem(int position) {
        return imagem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.lista_produtos_customizada, parent, false);
            holder = new ViewHolder();

            //holder.imagemFruta = (ImageView) convertView.findViewById(R.id.icone);
            holder.textoCodigo = (TextView) convertView.findViewById(R.id.codigo_item);
            holder.textoDescricao = (TextView) convertView.findViewById(R.id.descricao_item);
            holder.numItensRestantes = (TextView) convertView.findViewById(R.id.itens_restantes);
            holder.valor_produtos = (TextView)convertView.findViewById(R.id.valor_produtos);
            holder.valor_atacado = (TextView)convertView.findViewById(R.id.valor_atacado);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //holder.imagemFruta.setImageResource(imagem.get(position));

        try {
            if (Integer.parseInt(itensRestantes.get(position)) <= 0) {
                holder.textoDescricao.setTextColor(Color.RED);
            } else {
                holder.textoDescricao.setTextColor(Color.GRAY);
            }
        }catch (Exception e){
            holder.textoDescricao.setTextColor(Color.GRAY);
        }

        holder.textoCodigo.setText(codigo.get(position));
        holder.textoDescricao.setText(descricao.get(position));
        holder.numItensRestantes.setText(itensRestantes.get(position));
        holder.valor_produtos.setText(valorProdutos.get(position));
        holder.valor_atacado.setText(valorAtacado.get(position));


        return convertView;
    }



}
