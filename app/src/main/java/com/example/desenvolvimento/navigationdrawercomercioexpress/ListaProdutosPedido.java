package com.example.desenvolvimento.navigationdrawercomercioexpress;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ListaProdutosPedido extends BaseAdapter {

    private Context context;
    private List<Integer> imagem;
    private List<String> descricao;
    private List<String> itensRestantes;
    private List<String> valorProdutos;

    public ListaProdutosPedido(Context context, List<Integer> imagem, List<String> descricao, List<String> itensRestantes, List<String> valorProdutos){
        this.context = context;
        this.imagem = imagem;
        this.descricao = descricao;
        this.itensRestantes = itensRestantes;
        this.valorProdutos = valorProdutos;
    }

    private class ViewHolder{
        ImageView imagemFruta;
        TextView textoDescricao;
        TextView numItensRestantes;
        TextView valor_produtos;
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
        ListaProdutosPedido.ViewHolder holder = null;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.lista_produtos_pedido, parent, false);
            holder = new ListaProdutosPedido.ViewHolder();

            //holder.imagemFruta = (ImageView) convertView.findViewById(R.id.icone);
            holder.textoDescricao = (TextView) convertView.findViewById(R.id.descricao_item);
            holder.numItensRestantes = (TextView) convertView.findViewById(R.id.itens_restantes);
            holder.valor_produtos = (TextView)convertView.findViewById(R.id.valor_produtos);

            convertView.setTag(holder);
        } else {
            holder = (ListaProdutosPedido.ViewHolder) convertView.getTag();
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

        holder.textoDescricao.setText(descricao.get(position));
        holder.numItensRestantes.setText(itensRestantes.get(position));
        holder.valor_produtos.setText(valorProdutos.get(position));


        return convertView;
    }
}
