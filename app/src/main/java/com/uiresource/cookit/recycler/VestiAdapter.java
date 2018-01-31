package com.uiresource.cookit.recycler;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.uiresource.cookit.R;
import com.uiresource.cookit.utilidades.Util;
import com.uiresource.cookit.utils.CircleGlide;

import java.util.List;

/**
 * Created by Dytstudio.
 */

public class VestiAdapter extends RecyclerView.Adapter<VestiAdapter.MyViewHolder> {

    private List<Vestimenta> items;
    private Context context;

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView nomVesti, likesVesti;
        public RatingBar ratingBar;
        public ImageView imgVesti;

        public MyViewHolder(View view) {
            super(view);

            nomVesti = (TextView) view.findViewById(R.id.nomVesti);
            likesVesti = (TextView) view.findViewById(R.id.likeVesti);
            ratingBar = (RatingBar) view.findViewById(R.id.ratingBar_coment);
            imgVesti = (ImageView) view.findViewById(R.id.imgVesti);
        }


    }


    public VestiAdapter(List<Vestimenta> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vesti, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Vestimenta vestimenta = items.get(position);

        holder.nomVesti.setText(vestimenta.getDescripcion());
        holder.likesVesti.setText(""+vestimenta.getLikes());
        holder.ratingBar.setRating(Integer.parseInt(vestimenta.getRaiting()));
        Glide.with(context)
                .load(Uri.parse(Util.URL_IMG+vestimenta.getImagen()))
                .transform(new CircleGlide(context))
                .into(holder.imgVesti);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}