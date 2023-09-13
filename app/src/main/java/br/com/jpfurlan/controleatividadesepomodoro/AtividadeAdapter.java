package br.com.jpfurlan.controleatividadesepomodoro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class AtividadeAdapter extends BaseAdapter {
    private Context context;
    private List<Atividade> atividades;

    private static class AtividadeHolder {
        public TextView textViewValorNome;
        public TextView textViewValorPrioridade;
        public TextView textViewValorStatus;
        public TextView textViewValorComplexidade;
        public TextView textViewValorTipo;
    }

    public AtividadeAdapter(Context context, List<Atividade> atividades) {
        this.context = context;
        this.atividades = atividades;
    }

    @Override
    public int getCount() {
        return atividades.size();
    }


    @Override
    public Object getItem(int i) {
        return atividades.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        AtividadeHolder holder;
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.linha_lista_atividades, viewGroup, false);

            holder = new AtividadeHolder();

            holder.textViewValorNome = view.findViewById(R.id.textViewValorNome);
            holder.textViewValorPrioridade = view.findViewById(R.id.textViewValorPrioridade);
            holder.textViewValorStatus = view.findViewById(R.id.textViewValorStatus);
            holder.textViewValorComplexidade = view.findViewById(R.id.textViewValorComplexidade);
            holder.textViewValorTipo = view.findViewById(R.id.textViewValorTipo);


            view.setTag(holder);
        } else {
            holder = (AtividadeHolder) view.getTag();
        }
        holder.textViewValorNome.setText(atividades.get(i).getNome());
        holder.textViewValorPrioridade.setText(atividades.get(i).getPrioridade());
        holder.textViewValorStatus.setText(atividades.get(i).getStatus());
        holder.textViewValorComplexidade.setText(atividades.get(i).getComplexidade());
        holder.textViewValorTipo.setText(atividades.get(i).getTipo());
        return view;
    }



}
