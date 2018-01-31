package com.uiresource.cookit;

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
import com.uiresource.cookit.modelo.Comentario;
import com.uiresource.cookit.utilidades.Util;
import com.uiresource.cookit.utils.CircleGlide;

import java.util.List;

/**
 * Created by Dytstudio.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {

    private List<Comentario> items;
    private Context context;
    private String rating;

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView username, date, comment;
        public ImageView img1, img2, userphoto;
        RatingBar ratingBar;

        public MyViewHolder(View view) {
            super(view);

            username = (TextView) view.findViewById(R.id.tv_username);
            date = (TextView) view.findViewById(R.id.tv_date);
            comment = (TextView) view.findViewById(R.id.tv_text_comment);
            userphoto = (ImageView) view.findViewById(R.id.iv_user);
            ratingBar =(RatingBar) view.findViewById(R.id.ratingBar_coment);
        }

    }


    public CommentsAdapter(String rating, List<Comentario> items, Context context) {
        this.items = items;
        this.context = context;
        this.rating=rating;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Comentario itemComment = items.get(position);
        holder.username.setText(itemComment.getUsuario().getNombres()+" "+itemComment.getUsuario().getApellidos());
        holder.date.setText(itemComment.getUsuario().getEmail());
        holder.comment.setText(itemComment.getComentarios());
        Glide.with(context)
                .load(Uri.parse(Util.URL_IMG+itemComment.getVestimenta().getImagen()))
                .transform(new CircleGlide(context))
                .into(holder.userphoto);


        holder.ratingBar.setRating(Integer.parseInt(rating));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
