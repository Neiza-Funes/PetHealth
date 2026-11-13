package com.example.pethealth;

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
        void onEditar(long id);
        void onEliminar(Control control);
    }

    private final List<Control> listaDesparasitaciones;
    private final OnDesparasitacionListener listener;
    private final DatabaseHelper databaseHelper;
    private final Context context;

    public DesparasitacionAdapter(List<Control> listaDesparasitaciones, OnDesparasitacionListener listener, DatabaseHelper databaseHelper, Context context) {
        this.listaDesparasitaciones = listaDesparasitaciones;
        this.listener = listener;
        this.databaseHelper = databaseHelper;
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
        Control control = listaDesparasitaciones.get(position);
        Mascota mascota = databaseHelper.obtenerMascotaPorId(control.getMascotaId());

        if (mascota != null) {
            holder.txtMascota.setText(mascota.getNombre());
        } else {
            holder.txtMascota.setText("");
        }

        holder.txtTipo.setText(context.getString(R.string.formato_tipo_control, control.getTipo()));
        holder.txtFecha.setText(context.getString(R.string.formato_fecha_control, control.getFecha()));
        holder.txtEstado.setText(context.getString(R.string.formato_estado_control, control.getEstado()));

        if (control.getObservacion() == null || control.getObservacion().trim().isEmpty()) {
            holder.txtObservacion.setVisibility(View.GONE);
        } else {
            holder.txtObservacion.setText(context.getString(R.string.formato_observacion_control, control.getObservacion()));
            holder.txtObservacion.setVisibility(View.VISIBLE);
        }

        holder.btnVerMascota.setOnClickListener(v -> {});
        holder.btnEditar.setOnClickListener(v -> listener.onEditar(control.getId()));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminar(control));
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