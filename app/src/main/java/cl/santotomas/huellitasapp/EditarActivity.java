package cl.santotomas.huellitasapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EditarActivity extends AppCompatActivity {

    private static final String[] ESPECIES = {"Perro", "Gato", "Otro"};
    private static final String[] ESTADOS = {"Disponible", "En proceso", "Adoptado"};

    private EditText etNombre, etEdad, etDescripcion;
    private Spinner spEspecie, spEstado;
    private CheckBox cbVacunado;
    private Button btnActualizar;
    private ArrayAdapter<String> espAd, estAd;
    private FirebaseFirestore db;
    private String mascotaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);

        db = FirebaseFirestore.getInstance();
        mascotaId = getIntent().getStringExtra(ListarActivity.EXTRA_MASCOTA_ID);
        if (mascotaId == null) { finish(); return; }

        etNombre = findViewById(R.id.etEditarNombre);
        etEdad = findViewById(R.id.etEditarEdad);
        etDescripcion = findViewById(R.id.etEditarDescripcion);
        spEspecie = findViewById(R.id.spEditarEspecie);
        spEstado = findViewById(R.id.spEditarEstado);
        cbVacunado = findViewById(R.id.cbEditarVacunado);
        btnActualizar = findViewById(R.id.btnActualizar);

        espAd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ESPECIES);
        espAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEspecie.setAdapter(espAd);

        estAd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ESTADOS);
        estAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(estAd);

        db.collection("mascotas").document(mascotaId).get().addOnSuccessListener(doc -> {
            Mascota m = doc.toObject(Mascota.class);
            if (m != null) {
                etNombre.setText(m.getNombre());
                etEdad.setText(String.valueOf(m.getEdad()));
                etDescripcion.setText(m.getDescripcion());
                cbVacunado.setChecked(m.isVacunado());
                int pe = espAd.getPosition(m.getEspecie());
                if (pe >= 0) spEspecie.setSelection(pe);
                int ps = estAd.getPosition(m.getEstado());
                if (ps >= 0) spEstado.setSelection(ps);
            }
        });

        btnActualizar.setOnClickListener(v -> actualizar());
    }

    private void actualizar() {
        String nombre = etNombre.getText().toString().trim();
        String edadStr = etEdad.getText().toString().trim();
        String desc = etDescripcion.getText().toString().trim();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(edadStr) || TextUtils.isEmpty(desc)) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        btnActualizar.setEnabled(false);
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", nombre);
        map.put("especie", spEspecie.getSelectedItem().toString());
        map.put("edad", Integer.parseInt(edadStr));
        map.put("vacunado", cbVacunado.isChecked());
        map.put("estado", spEstado.getSelectedItem().toString());
        map.put("descripcion", desc);

        db.collection("mascotas").document(mascotaId).update(map)
                .addOnSuccessListener(u -> {
                    Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnActualizar.setEnabled(true);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}