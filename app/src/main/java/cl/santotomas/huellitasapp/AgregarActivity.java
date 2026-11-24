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

public class AgregarActivity extends AppCompatActivity {

    private EditText etNombre, etEdad, etDescripcion;
    private Spinner spEspecie, spEstado;
    private CheckBox cbVacunado;
    private Button btnGuardar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);

        db = FirebaseFirestore.getInstance();
        etNombre = findViewById(R.id.etNombre);
        etEdad = findViewById(R.id.etEdad);
        etDescripcion = findViewById(R.id.etDescripcion);
        spEspecie = findViewById(R.id.spEspecie);
        spEstado = findViewById(R.id.spEstado);
        cbVacunado = findViewById(R.id.cbVacunado);
        btnGuardar = findViewById(R.id.btnGuardar);

        ArrayAdapter<String> espAd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Perro", "Gato", "Otro"});
        espAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEspecie.setAdapter(espAd);

        ArrayAdapter<String> estAd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Disponible", "En proceso", "Adoptado"});
        estAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(estAd);

        btnGuardar.setOnClickListener(v -> guardar());
    }

    private void guardar() {
        String nombre = etNombre.getText().toString().trim();
        String edadStr = etEdad.getText().toString().trim();
        String desc = etDescripcion.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) { etNombre.setError("Obligatorio"); return; }
        if (TextUtils.isEmpty(edadStr)) { etEdad.setError("Obligatorio"); return; }
        if (TextUtils.isEmpty(desc)) { etDescripcion.setError("Obligatorio"); return; }

        int edad = Integer.parseInt(edadStr);
        btnGuardar.setEnabled(false);

        Mascota m = new Mascota(nombre, spEspecie.getSelectedItem().toString(), edad, cbVacunado.isChecked(), spEstado.getSelectedItem().toString(), desc);
        db.collection("mascotas").add(m)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "Guardado en Firestore", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnGuardar.setEnabled(true);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}