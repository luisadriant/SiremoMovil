package com.uiresource.cookit.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uiresource.cookit.R;

import java.util.List;

/**
 * Created by Dytstudio.
 */

public class DescripcionVestiAdapter extends RecyclerView.Adapter<DescripcionVestiAdapter.MyViewHolder> {

    private List<ItemShopping> items;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView pieces, name;

        public MyViewHolder(View view) {
            super(view);

            pieces = (TextView) view.findViewById(R.id.tv_pieces);
            name = (TextView) view.findViewById(R.id.tv_name);
        }

    }


    public DescripcionVestiAdapter(List<ItemShopping> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public DescripcionVestiAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_descripcion, parent, false);

        return new DescripcionVestiAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DescripcionVestiAdapter.MyViewHolder holder, int position) {
        ItemShopping itemShopping = items.get(position);
        holder.name.setText(itemShopping.getName());
        holder.pieces.setText(itemShopping.getPieces());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}