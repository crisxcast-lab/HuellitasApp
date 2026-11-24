package cl.santotomas.huellitasapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MascotaAdapter extends RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder> {

    public interface OnMascotaClickListener {
        void onMascotaClick(Mascota mascota);
    }

    private final List<Mascota> listaMascotas;
    private final OnMascotaClickListener listener;

    public MascotaAdapter(List<Mascota> listaMascotas, OnMascotaClickListener listener) {
        this.listaMascotas = listaMascotas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MascotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mascota, parent, false);
        return new MascotaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MascotaViewHolder holder, int position) {
        Mascota m = listaMascotas.get(position);
        holder.tvNombre.setText(m.getNombre());
        holder.tvEspecieEdad.setText(m.getEspecie() + " • " + m.getEdad() + " años");
        holder.tvEstado.setText("Estado: " + m.getEstado());
        holder.tvVacunado.setText(m.isVacunado() ? "Vacunado: Sí" : "Vacunado: No");
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && m.getId() != null) listener.onMascotaClick(m);
        });
    }

    @Override
    public int getItemCount() { return listaMascotas.size(); }

    static class MascotaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvEspecieEdad, tvEstado, tvVacunado;
        MascotaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvEspecieEdad = itemView.findViewById(R.id.tvEspecieEdad);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvVacunado = itemView.findViewById(R.id.tvVacunado);
        }
    }
}