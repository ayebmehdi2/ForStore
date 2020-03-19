package com.mehdi.boutique;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterGifts extends RecyclerView.Adapter<AdapterGifts.holder> {


    private ArrayList<GIFT> data = null;
    public void swapAdapter(ArrayList<GIFT> gifts){
        if (data == gifts) return;
        data = gifts;
        this.notifyDataSetChanged();
    }

    public interface click{
        void clic(GIFT g);
    }

    private final click c;

    public AdapterGifts(click cc){
        c = cc;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gift, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        GIFT gift = data.get(position);
        if (gift == null) return;

        holder.type.setText(gift.getType());
        holder.status.setText(gift.getStatus());
        holder.name.setText(gift.getUsername());
        holder.time.setText(gift.getTime());


    }

    @Override
    public int getItemCount() {
        if (data == null){
            return 0;
        }
        return data.size();
    }

    class holder extends RecyclerView.ViewHolder{

        TextView type, status, name, time;

        public holder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            type = itemView.findViewById(R.id.type);
            status = itemView.findViewById(R.id.status);
            name =  itemView.findViewById(R.id.name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    c.clic(data.get(getAdapterPosition()));
                }
            });

        }
    }

}
