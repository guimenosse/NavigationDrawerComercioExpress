package br.comercioexpress.plano;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Desenvolvimento on 18/01/2016.
 */
public class ListaClientesCustomizadaAdapter extends BaseAdapter {

    private Context context;
    private List<String> codigo;
    private List<String> nomeRazaoSocial;
    private List<String> nomeFantasia;
    private List<String> telefone;
    private List<String> email;

    public ListaClientesCustomizadaAdapter(Context context, List<String> codigo, List<String> nomeRazaoSocial, List<String> nomeFantasia, List<String> telefone, List<String> email){
        this.context = context;
        this.codigo = codigo;
        this.nomeRazaoSocial = nomeRazaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.telefone = telefone;
        this.email = email;
    }

    private class ViewHolder{
        TextView textoCodigo;
        TextView textoNomeRazaoSocial;
        TextView textoNomeFantasia;
        TextView textoTelefone;
        TextView textoEmail;
    }

    @Override
    public int getCount() {
        return codigo.size();
    }

    @Override
    public Object getItem(int position) {
        return codigo.get(position);
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
            convertView = inflater.inflate(R.layout.lista_clientes_personalizada, parent, false);
            holder = new ViewHolder();

            //holder.imagemFruta = (ImageView) convertView.findViewById(R.id.icone);
            holder.textoCodigo = (TextView) convertView.findViewById(R.id.codigoClienteLista);
            holder.textoNomeRazaoSocial = (TextView) convertView.findViewById(R.id.nomeRazaoSocialClienteLista);
            holder.textoNomeFantasia = (TextView) convertView.findViewById(R.id.nomeFantasiaClienteLista);
            holder.textoTelefone = (TextView)convertView.findViewById(R.id.telefoneClienteLista);
            holder.textoEmail = (TextView)convertView.findViewById(R.id.emailClienteLista);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //holder.imagemFruta.setImageResource(imagem.get(position));

        holder.textoCodigo.setText(codigo.get(position));
        holder.textoNomeRazaoSocial.setText(nomeRazaoSocial.get(position));
        holder.textoNomeFantasia.setText(nomeFantasia.get(position));
        holder.textoTelefone.setText(telefone.get(position));
        holder.textoEmail.setText(email.get(position));


        return convertView;
    }



}
