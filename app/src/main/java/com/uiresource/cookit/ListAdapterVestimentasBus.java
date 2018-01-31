package com.uiresource.cookit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.uiresource.cookit.modelo.Empresa;
import com.uiresource.cookit.modelo.Marca;
import com.uiresource.cookit.recycler.Vestimenta;
import com.uiresource.cookit.utilidades.Util;
import com.uiresource.cookit.utils.CircleGlide;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luisl on 1/11/2018.
 */

public class ListAdapterVestimentasBus extends BaseAdapter implements Filterable {
    // Declare Variables
    Context context;
    ArrayList<Vestimenta> Vestimetas;
    LayoutInflater inflater;
    CustomFilter filtro;
    ArrayList<Vestimenta> filtroList;

    public ListAdapterVestimentasBus(Context context, ArrayList<Vestimenta> vestimetas) {
        //super(context, R.layout.item_bus_vestimenta, vestimetas);
        this.context = context;
        this.Vestimetas = vestimetas;
        this.filtroList = vestimetas;
    }

    @Override
    public int getCount() {
        return Vestimetas.size();
    }

    @Override
    public Object getItem(int position) {
        return Vestimetas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Vestimetas.indexOf(getItem(position));
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_bus_vestimenta, null);
        }

        //Objeto Categoria a visualizar segun position
        final Vestimenta item = (Vestimenta) getItem(position);
        if (item != null) {

            // Recupera los elementos de vista y setea en funcion de valores de objeto
            TextView titulo = (TextView) view.findViewById(R.id.ivesdato1);
            TextView nombre = (TextView) view.findViewById(R.id.ivesdato2);
            ImageView imagen = (ImageView) view.findViewById(R.id.ves_imagen_bus);
            RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBarBus);
            TextView likes = (TextView) view.findViewById(R.id.likeVestiBus);

            Log.w(">>>>>", Util.URL_IMG+item.getImagen());
            if (titulo != null) {
                titulo.setText(item.getEmpresa().getNombre());
                nombre.setText(item.getDescripcion());
                ratingBar.setRating(Integer.parseInt(item.getRaiting()));
                likes.setText(item.getLikes()+"");
                Glide.with(context)
                        .load(Uri.parse(Util.URL_IMG+item.getImagen()))
                        .transform(new CircleGlide(context))
                        .into(imagen);
                /*
                File imagen1 = new File(Util.URL_IMG+item.getImagen());
                if(imagen1.exists()){
                    Bitmap bmp = BitmapFactory.decodeFile(imagen1.getAbsolutePath());
                    imagen.setImageBitmap(bmp);
                }*/

            }
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        if(filtro == null){
            filtro = new CustomFilter();
        }

        return filtro;
    }

    class CustomFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults resulst = new FilterResults();
            if(constraint != null && constraint.length()>0){
                //pasamos a mayusculas
                constraint = constraint.toString().toUpperCase();

                ArrayList<Vestimenta> filtro = new ArrayList<Vestimenta>();

                for(Integer i=0;i<filtroList.size();i++){
                    if(filtroList.get(i).getDescripcion().toUpperCase().contains(constraint) || filtroList.get(i).getEmpresa().getNombre().toUpperCase().contains(constraint)){
                        Vestimenta v= filtroList.get(i);
                        filtro.add(v);
                    }
                }
                resulst.count= filtro.size();
                resulst.values = filtro;
            }else{
                resulst.count= filtroList.size();
                resulst.values = filtroList;
            }

            return resulst;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Vestimetas = (ArrayList<Vestimenta>) results.values;
            notifyDataSetChanged();

        }
    }


}
