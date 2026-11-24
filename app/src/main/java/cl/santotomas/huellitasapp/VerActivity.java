package cl.santotomas.huellitasapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class VerActivity extends AppCompatActivity {

    private TextView tvNombre, tvEspecie, tvEdad, tvVacunado, tvEstado, tvDescripcion;
    private FirebaseFirestore db;
    private String mascotaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver);

        db = FirebaseFirestore.getInstance();
        mascotaId = getIntent().getStringExtra(ListarActivity.EXTRA_MASCOTA_ID);
        if (mascotaId == null) { finish(); return; }

        tvNombre = findViewById(R.id.tvDetalleNombre);
        tvEspecie = findViewById(R.id.tvDetalleEspecie);
        tvEdad = findViewById(R.id.tvDetalleEdad);
        tvVacunado = findViewById(R.id.tvDetalleVacunado);
        tvEstado = findViewById(R.id.tvDetalleEstado);
        tvDescripcion = findViewById(R.id.tvDetalleDescripcion);
        Button btnEditar = findViewById(R.id.btnEditar);
        Button btnEliminar = findViewById(R.id.btnEliminar);

        btnEditar.setOnClickListener(v -> {
            Intent i = new Intent(this, EditarActivity.class);
            i.putExtra(ListarActivity.EXTRA_MASCOTA_ID, mascotaId);
            startActivity(i);
        });

        btnEliminar.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Eliminar mascota")
                .setMessage("¿Seguro que deseas eliminar este registro?")
                .setPositiveButton("Eliminar", (d, w) -> eliminar())
                .setNegativeButton("Cancelar", null)
                .show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        db.collection("mascotas").document(mascotaId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                Mascota m = doc.toObject(Mascota.class);
                if (m != null) {
                    tvNombre.setText("Nombre: " + m.getNombre());
                    tvEspecie.setText("Especie: " + m.getEspecie());
                    tvEdad.setText("Edad: " + m.getEdad() + " años");
                    tvVacunado.setText("Vacunado: " + (m.isVacunado() ? "Sí" : "No"));
                    tvEstado.setText("Estado: " + m.getEstado());
                    tvDescripcion.setText("Descripción: " + m.getDescripcion());
                }
            } else {
                finish();
            }
        });
    }

    private void eliminar() {
        db.collection("mascotas").document(mascotaId).delete().addOnSuccessListener(u -> {
            Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}