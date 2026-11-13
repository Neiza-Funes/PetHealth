package com.example.pethealth.mascota;

import com.example.pethealth.R;
import com.example.pethealth.database.DatabaseHelper;
import com.example.pethealth.model.Mascota;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MisMascotasActivity extends Activity {

    private DatabaseHelper databaseHelper;
    private LinearLayout contenedorMascotas;
    private TextView txtSinMascotas;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_mascotas);

        databaseHelper = new DatabaseHelper(this);
        contenedorMascotas = findViewById(R.id.contenedorMascotas);
        txtSinMascotas = findViewById(R.id.txtSinMascotas);
        Button btnRegistrarMascota = findViewById(R.id.btnRegistrarMascotaLista);

        btnRegistrarMascota.setOnClickListener(v -> {
            Intent intent = new Intent(MisMascotasActivity.this, RegistrarMascotaActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarMascotasAsincrono();
    }

    private void cargarMascotasAsincrono() {
        executorService.execute(() -> {
            List<Mascota> mascotas = databaseHelper.obtenerMascotas();
            mainHandler.post(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                mostrarMascotas(mascotas);
            });
        });
    }

    private void mostrarMascotas(List<Mascota> mascotas) {
        contenedorMascotas.removeAllViews();
        txtSinMascotas.setVisibility(mascotas.isEmpty() ? View.VISIBLE : View.GONE);

        for (Mascota mascota : mascotas) {
            View tarjeta = getLayoutInflater().inflate(
                    R.layout.item_mascota,
                    contenedorMascotas,
                    false
            );

            ImageView imgPerfil = tarjeta.findViewById(R.id.imgItemFotoMascota);
            TextView txtNombre = tarjeta.findViewById(R.id.txtItemNombreMascota);
            TextView txtEspecie = tarjeta.findViewById(R.id.txtItemEspecieMascota);
            TextView txtEdad = tarjeta.findViewById(R.id.txtItemEdadMascota);
            TextView txtRaza = tarjeta.findViewById(R.id.txtItemRazaMascota);
            Button btnVerDetalles = tarjeta.findViewById(R.id.btnItemVerMascota);

            mostrarFotoPerfil(imgPerfil, mascota.getFotoPath());
            txtNombre.setText(mascota.getNombre());
            txtEspecie.setText(getString(R.string.formato_especie, mascota.getEspecie()));
            txtEdad.setText(getString(R.string.formato_edad, String.valueOf(mascota.getEdad())));

            if (!mascota.getRaza().isEmpty()) {
                txtRaza.setText(getString(R.string.formato_raza, mascota.getRaza()));
                txtRaza.setVisibility(View.VISIBLE);
            } else {
                txtRaza.setVisibility(View.GONE);
            }

            btnVerDetalles.setOnClickListener(v -> abrirDetalle(mascota.getId()));
            contenedorMascotas.addView(tarjeta);
        }
    }

    private void mostrarFotoPerfil(ImageView imageView, String fotoPath) {
        if (fotoPath == null || fotoPath.isEmpty()) {
            imageView.setImageResource(R.drawable.ic_pethealth_logo);
            return;
        }

        File archivoFoto = new File(fotoPath);
        if (!archivoFoto.exists()) {
            imageView.setImageResource(R.drawable.ic_pethealth_logo);
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(archivoFoto.getAbsolutePath());
        if (bitmap == null) {
            imageView.setImageResource(R.drawable.ic_pethealth_logo);
            return;
        }

        imageView.setImageBitmap(bitmap);
    }

    private void abrirDetalle(long mascotaId) {
        Intent intent = new Intent(MisMascotasActivity.this, DetalleMascotaActivity.class);
        intent.putExtra(DetalleMascotaActivity.EXTRA_MASCOTA_ID, mascotaId);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        executorService.shutdown();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
