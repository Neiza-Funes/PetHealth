package com.example.pethealth;

import android.app.Activity;
import android.app.AlertDialog;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class DetalleMascotaActivity extends Activity {

    public static final String EXTRA_MASCOTA_ID = "mascotaId";

    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    private static final int REQUEST_IMAGE_CAPTURE = 1002;

    private DatabaseHelper databaseHelper;
    private long mascotaId = -1;

    private TextView txtNombre;
    private TextView txtEspecie;
    private TextView txtEdad;
    private TextView txtRaza;

    private TextView txtHistorial1;
    private TextView txtHistorial2;
    private TextView txtHistorialVacio;

    private LinearLayout contenedorDetalleControles;
    private TextView txtControlesVacio;

    private LinearLayout contenedorDetalleVacunas;
    private TextView txtVacunasVacio;

    private Button btnEditar;
    private Button btnEliminar;

    private ImageView imgFotoMascota;
    private TextView txtEstadoFoto;
    private Button btnTomarFoto;
    private Button btnGuardarFoto;
    private Bitmap fotoTemporal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_mascota);

        databaseHelper = new DatabaseHelper(this);
        mascotaId = getIntent().getLongExtra(EXTRA_MASCOTA_ID, -1);

        Button btnVolver = findViewById(R.id.btnVolverMisMascotas);
        btnEditar = findViewById(R.id.btnEditarMascota);
        btnEliminar = findViewById(R.id.btnEliminarMascota);

        imgFotoMascota = findViewById(R.id.imgFotoMascota);
        txtEstadoFoto = findViewById(R.id.txtEstadoFoto);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnGuardarFoto = findViewById(R.id.btnGuardarFoto);

        txtNombre = findViewById(R.id.txtDetalleNombre);
        txtEspecie = findViewById(R.id.txtDetalleEspecie);
        txtEdad = findViewById(R.id.txtDetalleEdad);
        txtRaza = findViewById(R.id.txtDetalleRaza);

        txtHistorial1 = findViewById(R.id.txtHistorial1);
        txtHistorial2 = findViewById(R.id.txtHistorial2);
        txtHistorialVacio = findViewById(R.id.txtHistorialVacio);

        contenedorDetalleControles = findViewById(R.id.contenedorDetalleControles);
        txtControlesVacio = findViewById(R.id.txtControlesVacio);

        contenedorDetalleVacunas = findViewById(R.id.contenedorDetalleVacunas);
        txtVacunasVacio = findViewById(R.id.txtVacunasVacio);

        btnVolver.setOnClickListener(v -> finish());
        btnEditar.setOnClickListener(v -> abrirEdicion());
        btnEliminar.setOnClickListener(v -> confirmarEliminacion());
        btnTomarFoto.setOnClickListener(v -> verificarPermisoYTomarFoto());
        btnGuardarFoto.setOnClickListener(v -> guardarFotoMascota());

        boolean tieneIdSQLite = mascotaId != -1;
        btnEditar.setVisibility(tieneIdSQLite ? View.VISIBLE : View.GONE);
        btnEliminar.setVisibility(tieneIdSQLite ? View.VISIBLE : View.GONE);
        btnGuardarFoto.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDetalle();
    }

    private void cargarDetalle() {
        String nombre;
        String especie;
        String edad;
        String raza;
        String fotoPath = "";

        if (mascotaId != -1) {
            Mascota mascota = databaseHelper.obtenerMascotaPorId(mascotaId);
            if (mascota == null) {
                Toast.makeText(this, R.string.toast_mascota_no_encontrada, Toast.LENGTH_SHORT).show();
                volverAMisMascotas();
                return;
            }

            nombre = mascota.getNombre();
            especie = mascota.getEspecie();
            edad = String.valueOf(mascota.getEdad());
            raza = mascota.getRaza();
            fotoPath = mascota.getFotoPath();
        } else {
            // Compatibilidad con navegaciones antiguas que aún envíen datos directos.
            nombre = getIntent().getStringExtra("nombreMascota");
            especie = getIntent().getStringExtra("especieMascota");
            edad = getIntent().getStringExtra("edadMascota");
            raza = getIntent().getStringExtra("razaMascota");
        }

        if (nombre == null) nombre = "";
        if (especie == null) especie = "";
        if (edad == null) edad = "";
        if (raza == null) raza = "";

        mostrarDatosPrincipales(nombre, especie, edad, raza);
        mostrarFotoGuardada(fotoPath);
        limpiarInformacionSalud();
        mostrarHistorial(mascotaId, nombre);
        mostrarControles(mascotaId, nombre);
        mostrarVacunas(mascotaId, nombre);
    }

    private void mostrarDatosPrincipales(String nombre, String especie, String edad, String raza) {
        txtNombre.setText(nombre);

        if (!especie.isEmpty()) {
            txtEspecie.setText(getString(R.string.detalle_formato_especie, especie));
            txtEspecie.setVisibility(View.VISIBLE);
        } else {
            txtEspecie.setVisibility(View.GONE);
        }

        if (!edad.isEmpty()) {
            txtEdad.setText(getString(R.string.detalle_formato_edad, edad));
            txtEdad.setVisibility(View.VISIBLE);
        } else {
            txtEdad.setVisibility(View.GONE);
        }

        if (!raza.isEmpty()) {
            txtRaza.setText(getString(R.string.detalle_formato_raza, raza));
            txtRaza.setVisibility(View.VISIBLE);
        } else {
            txtRaza.setVisibility(View.GONE);
        }
    }

    private void limpiarInformacionSalud() {
        txtHistorial1.setVisibility(View.GONE);
        txtHistorial2.setVisibility(View.GONE);
        txtHistorialVacio.setVisibility(View.GONE);

        contenedorDetalleControles.removeAllViews();
        contenedorDetalleControles.setVisibility(View.GONE);
        txtControlesVacio.setVisibility(View.GONE);

        contenedorDetalleVacunas.removeAllViews();
        contenedorDetalleVacunas.setVisibility(View.GONE);
        txtVacunasVacio.setVisibility(View.GONE);
    }

    private void mostrarHistorial(long id, String nombre) {
        boolean esLuna = id == 1 || (id == -1 && nombre.equals(getString(R.string.mascota_luna)));
        boolean esMilo = id == 2 || (id == -1 && nombre.equals(getString(R.string.mascota_milo)));

        if (esLuna) {
            txtHistorial1.setText(R.string.luna_historial_vacuna);
            txtHistorial2.setText(R.string.luna_historial_desparasitacion);
            txtHistorial1.setVisibility(View.VISIBLE);
            txtHistorial2.setVisibility(View.VISIBLE);
        } else if (esMilo) {
            txtHistorial1.setText(R.string.milo_historial_desparasitacion);
            txtHistorial1.setVisibility(View.VISIBLE);
        } else {
            txtHistorialVacio.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarControles(long id, String nombre) {
        if (id != -1) {
            List<Control> controles = databaseHelper.obtenerControlesPorMascota(id);
            if (controles.isEmpty()) {
                txtControlesVacio.setVisibility(View.VISIBLE);
                return;
            }

            for (Control control : controles) {
                agregarControlVisual(
                        control.getTipo(),
                        control.getFecha(),
                        control.getEstado(),
                        control.getObservacion()
                );
            }
            return;
        }

        // Compatibilidad visual con navegaciones antiguas sin ID SQLite.
        if (nombre.equals(getString(R.string.mascota_luna))) {
            agregarControlVisual(
                    getString(R.string.luna_tipo_control_valor),
                    getString(R.string.luna_fecha_control_valor),
                    getString(R.string.estado_pendiente_valor),
                    ""
            );
        } else if (nombre.equals(getString(R.string.mascota_milo))) {
            agregarControlVisual(
                    getString(R.string.milo_tipo_control_valor),
                    getString(R.string.milo_fecha_control_valor),
                    getString(R.string.estado_pendiente_valor),
                    ""
            );
        } else {
            txtControlesVacio.setVisibility(View.VISIBLE);
        }
    }

    private void agregarControlVisual(
            String tipo,
            String fecha,
            String estado,
            String observacion) {

        View item = LayoutInflater.from(this)
                .inflate(R.layout.item_control_detalle, contenedorDetalleControles, false);

        TextView txtTipo = item.findViewById(R.id.txtDetalleItemControlTipo);
        TextView txtFecha = item.findViewById(R.id.txtDetalleItemControlFecha);
        TextView txtEstado = item.findViewById(R.id.txtDetalleItemControlEstado);
        TextView txtObservacion = item.findViewById(R.id.txtDetalleItemControlObservacion);

        txtTipo.setText(getString(R.string.formato_tipo_control, tipo));
        txtFecha.setText(getString(R.string.formato_fecha_control, fecha));
        txtEstado.setText(getString(R.string.formato_estado_control, estado));

        if (observacion == null || observacion.isEmpty()) {
            txtObservacion.setVisibility(View.GONE);
        } else {
            txtObservacion.setText(getString(R.string.formato_observacion_control, observacion));
            txtObservacion.setVisibility(View.VISIBLE);
        }

        contenedorDetalleControles.addView(item);
        contenedorDetalleControles.setVisibility(View.VISIBLE);
        txtControlesVacio.setVisibility(View.GONE);
    }

    private void mostrarVacunas(long id, String nombre) {
        if (id != -1) {
            List<Vacuna> vacunas = databaseHelper.obtenerVacunasPorMascota(id);
            if (vacunas.isEmpty()) {
                txtVacunasVacio.setVisibility(View.VISIBLE);
                return;
            }

            for (Vacuna vacuna : vacunas) {
                agregarVacunaVisual(
                        vacuna.getNombre(),
                        vacuna.getFecha(),
                        vacuna.getEstado()
                );
            }
            return;
        }

        // Compatibilidad visual para una navegación antigua sin ID SQLite.
        if (nombre.equals(getString(R.string.mascota_luna))) {
            agregarVacunaVisual(
                    getString(R.string.luna_vacuna_valor),
                    getString(R.string.luna_fecha_vacuna_valor),
                    getString(R.string.luna_estado_vacuna_valor)
            );
        } else if (nombre.equals(getString(R.string.mascota_milo))) {
            agregarVacunaVisual(
                    getString(R.string.milo_vacuna_valor),
                    getString(R.string.milo_fecha_vacuna_valor),
                    getString(R.string.milo_estado_vacuna_valor)
            );
        } else {
            txtVacunasVacio.setVisibility(View.VISIBLE);
        }
    }

    private void agregarVacunaVisual(String nombre, String fecha, String estado) {
        View item = LayoutInflater.from(this)
                .inflate(R.layout.item_vacuna_detalle, contenedorDetalleVacunas, false);

        TextView txtNombreVacuna = item.findViewById(R.id.txtDetalleItemVacunaNombre);
        TextView txtFechaVacuna = item.findViewById(R.id.txtDetalleItemVacunaFecha);
        TextView txtEstadoVacuna = item.findViewById(R.id.txtDetalleItemVacunaEstado);

        txtNombreVacuna.setText(getString(R.string.formato_vacuna, nombre));
        txtFechaVacuna.setText(getString(R.string.formato_fecha_vacuna, fecha));
        txtEstadoVacuna.setText(getString(R.string.formato_estado_vacuna, estado));

        contenedorDetalleVacunas.addView(item);
        contenedorDetalleVacunas.setVisibility(View.VISIBLE);
        txtVacunasVacio.setVisibility(View.GONE);
    }


    private void verificarPermisoYTomarFoto() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
            return;
        }

        requestPermissions(
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION
        );
    }

    private void abrirCamara() {
        Intent intentCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intentCamara.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, R.string.camara_no_disponible, Toast.LENGTH_SHORT).show();
            return;
        }

        startActivityForResult(intentCamara, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != REQUEST_CAMERA_PERMISSION) {
            return;
        }

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            Toast.makeText(this, R.string.permiso_camara_necesario, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_IMAGE_CAPTURE || resultCode != RESULT_OK) {
            return;
        }

        if (data == null || data.getExtras() == null) {
            Toast.makeText(this, R.string.error_obtener_foto, Toast.LENGTH_SHORT).show();
            return;
        }

        Object resultado = data.getExtras().get("data");
        if (!(resultado instanceof Bitmap)) {
            Toast.makeText(this, R.string.error_obtener_foto, Toast.LENGTH_SHORT).show();
            return;
        }

        fotoTemporal = (Bitmap) resultado;
        imgFotoMascota.setImageBitmap(fotoTemporal);
        txtEstadoFoto.setText(R.string.foto_tomada_no_guardada);
        btnGuardarFoto.setEnabled(mascotaId != -1);
    }

    private void guardarFotoMascota() {
        if (mascotaId == -1 || fotoTemporal == null) {
            btnGuardarFoto.setEnabled(false);
            return;
        }

        File archivoFoto = new File(getFilesDir(), "mascota_" + mascotaId + ".jpg");

        try (FileOutputStream salida = new FileOutputStream(archivoFoto)) {
            boolean comprimida = fotoTemporal.compress(Bitmap.CompressFormat.JPEG, 90, salida);
            salida.flush();

            if (!comprimida) {
                Toast.makeText(this, R.string.toast_error_guardar_foto, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (IOException e) {
            Toast.makeText(this, R.string.toast_error_guardar_foto, Toast.LENGTH_SHORT).show();
            return;
        }

        boolean actualizada = databaseHelper.actualizarFotoMascota(
                mascotaId,
                archivoFoto.getAbsolutePath()
        );

        if (!actualizada) {
            Toast.makeText(this, R.string.toast_error_guardar_foto, Toast.LENGTH_SHORT).show();
            return;
        }

        fotoTemporal = null;
        btnGuardarFoto.setEnabled(false);
        txtEstadoFoto.setText(R.string.foto_guardada);
        Toast.makeText(this, R.string.toast_foto_guardada, Toast.LENGTH_SHORT).show();
    }

    private void mostrarFotoGuardada(String fotoPath) {
        // Si acaba de tomarse una foto y todavía no se guarda, se conserva la vista previa.
        if (fotoTemporal != null) {
            imgFotoMascota.setImageBitmap(fotoTemporal);
            txtEstadoFoto.setText(R.string.foto_tomada_no_guardada);
            btnGuardarFoto.setEnabled(mascotaId != -1);
            return;
        }

        btnGuardarFoto.setEnabled(false);

        if (fotoPath == null || fotoPath.isEmpty()) {
            imgFotoMascota.setImageResource(R.drawable.ic_pethealth_logo);
            txtEstadoFoto.setText(R.string.foto_no_tomada);
            return;
        }

        File archivo = new File(fotoPath);
        if (!archivo.exists()) {
            imgFotoMascota.setImageResource(R.drawable.ic_pethealth_logo);
            txtEstadoFoto.setText(R.string.foto_no_tomada);
            return;
        }

        Bitmap fotoGuardada = BitmapFactory.decodeFile(archivo.getAbsolutePath());
        if (fotoGuardada == null) {
            imgFotoMascota.setImageResource(R.drawable.ic_pethealth_logo);
            txtEstadoFoto.setText(R.string.foto_no_tomada);
            return;
        }

        imgFotoMascota.setImageBitmap(fotoGuardada);
        txtEstadoFoto.setText(R.string.foto_guardada);
    }

    private void abrirEdicion() {
        if (mascotaId == -1) {
            return;
        }

        Intent intent = new Intent(DetalleMascotaActivity.this, EditarMascotaActivity.class);
        intent.putExtra(EditarMascotaActivity.EXTRA_MASCOTA_ID, mascotaId);
        startActivity(intent);
    }

    private void confirmarEliminacion() {
        if (mascotaId == -1) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.eliminar_mascota)
                .setMessage(R.string.confirmar_eliminar_mascota)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.eliminar, (dialog, which) -> eliminarMascota())
                .show();
    }

    private void eliminarMascota() {
        boolean eliminada = databaseHelper.eliminarMascota(mascotaId);
        if (!eliminada) {
            Toast.makeText(this, R.string.toast_error_eliminar_mascota, Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, R.string.toast_mascota_eliminada, Toast.LENGTH_SHORT).show();
        volverAMisMascotas();
    }

    private void volverAMisMascotas() {
        Intent intent = new Intent(DetalleMascotaActivity.this, MisMascotasActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
