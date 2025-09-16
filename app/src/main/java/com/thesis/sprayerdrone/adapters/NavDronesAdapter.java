package com.thesis.sprayerdrone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thesis.sprayerdrone.R;
import com.thesis.sprayerdrone.interfaces.DronesListener;
import com.thesis.sprayerdrone.models.Drones;

import java.util.List;

public class NavDronesAdapter extends RecyclerView.Adapter<NavDronesAdapter.ViewHolder> {
    Context mContext;
    List<Drones> dronesList;
    DronesListener listener;

    public NavDronesAdapter(Context mContext, List<Drones> dronesList, DronesListener listener) {
        this.mContext = mContext;
        this.dronesList = dronesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NavDronesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.item_drones, null, false);
        return new ViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull NavDronesAdapter.ViewHolder holder, int position) {
        Drones drones = dronesList.get(position);
        holder.txtDroneName.setText(String.format("%s", drones.getDeviceName()));
        holder.btnDeleteDrone.setOnClickListener(v -> listener.onDeleteListener(drones));
        holder.itemView.setOnClickListener(v -> listener.onClickListener(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return dronesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton btnDeleteDrone;
        TextView txtDroneName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnDeleteDrone = itemView.findViewById(R.id.btnDeleteDrone);
            txtDroneName = itemView.findViewById(R.id.txtDroneName);
        }
    }
}
