package com.ipvg.georegistrov2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private List<RecordEstructure> listaRegistros;

    public RecordAdapter(List<RecordEstructure> listaRegistros) {
        this.listaRegistros = listaRegistros;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registro, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        RecordEstructure registro = listaRegistros.get(position);

        holder.tvTitulo.setText(registro.getTitulo());
        holder.tvCategoria.setText("Categoría: " + registro.getCategoria());
        holder.tvFecha.setText(registro.getFechaHora());
        holder.tvDescripcion.setText(registro.getDescripcion());
        holder.tvCoordenadas.setText(String.format(Locale.getDefault(), "GPS: Lat: %.4f | Lon: %.4f", registro.getLatitud(), registro.getLongitud()));
    }

    @Override
    public int getItemCount() {
        return listaRegistros != null ? listaRegistros.size() : 0;
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo;
        TextView tvCategoria;
        TextView tvFecha;
        TextView tvDescripcion;
        TextView tvCoordenadas;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvItemTitulo);
            tvCategoria = itemView.findViewById(R.id.tvItemCategoria);
            tvFecha = itemView.findViewById(R.id.tvItemFecha);
            tvDescripcion = itemView.findViewById(R.id.tvItemDescripcion);
            tvCoordenadas = itemView.findViewById(R.id.tvItemCoordenadas);
        }
    }
}