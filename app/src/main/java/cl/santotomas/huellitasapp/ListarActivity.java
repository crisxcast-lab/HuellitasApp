package cl.santotomas.huellitasapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.List;

public class ListarActivity extends AppCompatActivity {

    public static final String EXTRA_MASCOTA_ID = "MASCOTA_ID";
    private RecyclerView rvMascotas;
    private MascotaAdapter adapter;
    private final List<Mascota> listaMascotas = new ArrayList<>();
    private FirebaseFirestore db;
    private ListenerRegistration listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar);

        db = FirebaseFirestore.getInstance();
        rvMascotas = findViewById(R.id.rvMascotas);
        FloatingActionButton fabAgregar = findViewById(R.id.fabAgregar);

        rvMascotas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MascotaAdapter(listaMascotas, mascota -> {
            Intent intent = new Intent(this, VerActivity.class);
            intent.putExtra(EXTRA_MASCOTA_ID, mascota.getId());
            startActivity(intent);
        });
        rvMascotas.setAdapter(adapter);

        fabAgregar.setOnClickListener(v -> startActivity(new Intent(this, AgregarActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        listener = db.collection("mascotas").addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                Toast.makeText(this, "Error Firestore: " + error.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
            if (snapshots != null) {
                listaMascotas.clear();
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    Mascota m = doc.toObject(Mascota.class);
                    if (m != null) {
                        m.setId(doc.getId());
                        listaMascotas.add(m);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null) {
            listener.remove();
            listener = null;
        }
    }
}