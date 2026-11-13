package com.example.pethealth.adapter;

import com.example.pethealth.R;
import com.example.pethealth.model.Desparasitacion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DesparasitacionAdapter extends RecyclerView.Adapter<DesparasitacionAdapter.DesparasitacionViewHolder> {

    public interface OnDesparasitacionListener {
        void onVerMascota(long mascotaId);
        void onEliminar(Desparasitacion desparasitacion);
    }

    private final List<Desparasitacion> listaDesparasitaciones;
    private final OnDesparasitacionListener listener;
    private final Context context;

    public DesparasitacionAdapter(List<Desparasitacion> listaDesparasitaciones,
                                  OnDesparasitacionListener listener,
                                  Context context) {
        this.listaDesparasitaciones = listaDesparasitaciones;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public DesparasitacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_control, parent, false);
        return new DesparasitacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DesparasitacionViewHolder holder, int position) {
        Desparasitacion desparasitacion = listaDesparasitaciones.get(position);

        holder.txtMascota.setText(desparasitacion.getMascotaNombre());
        holder.txtTipo.setText(context.getString(
                R.string.formato_producto_desparasitacion,
                desparasitacion.getProducto()
        ));
        holder.txtFecha.setText(context.getString(
                R.string.formato_fecha_desparasitacion,
                desparasitacion.getFecha()
        ));
        holder.txtEstado.setText(context.getString(
                R.string.formato_proxima_desparasitacion,
                desparasitacion.getProximaFecha()
        ));

        if (desparasitacion.getObservacion().trim().isEmpty()) {
            holder.txtObservacion.setVisibility(View.GONE);
        } else {
            holder.txtObservacion.setText(context.getString(
                    R.string.formato_observacion_desparasitacion,
                    desparasitacion.getObservacion()
            ));
            holder.txtObservacion.setVisibility(View.VISIBLE);
        }

        holder.btnVerMascota.setOnClickListener(v ->
                listener.onVerMascota(desparasitacion.getMascotaId()));

        // El módulo todavía no posee una pantalla de edición propia.
        holder.btnEditar.setVisibility(View.GONE);
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(desparasitacion));
    }

    @Override
    public int getItemCount() {
        return listaDesparasitaciones.size();
    }

    public static class DesparasitacionViewHolder extends RecyclerView.ViewHolder {
        TextView txtMascota, txtTipo, txtFecha, txtEstado, txtObservacion;
        Button btnVerMascota, btnEditar, btnEliminar;

        public DesparasitacionViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMascota = itemView.findViewById(R.id.txtItemControlMascota);
            txtTipo = itemView.findViewById(R.id.txtItemControlTipo);
            txtFecha = itemView.findViewById(R.id.txtItemControlFecha);
            txtEstado = itemView.findViewById(R.id.txtItemControlEstado);
            txtObservacion = itemView.findViewById(R.id.txtItemControlObservacion);
            btnVerMascota = itemView.findViewById(R.id.btnItemControlVerMascota);
            btnEditar = itemView.findViewById(R.id.btnItemControlEditar);
            btnEliminar = itemView.findViewById(R.id.btnItemControlEliminar);
        }
    }
}
