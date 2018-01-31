package com.uiresource.cookit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.uiresource.cookit.modelo.Empresa;
import com.uiresource.cookit.modelo.Marca;
import com.uiresource.cookit.recycler.Vestimenta;
import com.uiresource.cookit.utilidades.ClienteRest;
import com.uiresource.cookit.utilidades.OnTaskCompleted;
import com.uiresource.cookit.utilidades.Util;
import com.uiresource.cookit.utils.CircleGlide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luisl on 1/11/2018.
 */

public class ListAdapterVestimentas extends ArrayAdapter<Vestimenta>  implements OnTaskCompleted {
    //Contenxto de la aplicacion que relaciona al ListView y el Adpater
    private Context context;
    private static final int ELIMINARVES = 1;

    /**
     * Inicializacion
     *
     * @param context Contexto de la App desde la que se invoca
     * @param items   //Coleccion de objetos a presentar
     */
    public ListAdapterVestimentas(Context context, ArrayList<Vestimenta> items) {
        super(context, R.layout.item_vestimenta, items);
        this.context = context;
    }

    /**
     * View a presentar en pantalla correspondiente a un item de ListView
     *
     * @param position    //Indice del ListItem
     * @param convertView //Contexto o contenedor de View
     * @param parent      //Contendor padre
     * @return  Objeto View
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_vestimenta, null);
        }

        //Objeto Categoria a visualizar segun position
        final Vestimenta item = getItem(position);
        if (item != null) {
            Button button = (Button) view.findViewById(R.id.ivesEdit);
            // Capture button clicks
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    SharedPreferences miPreferencia = context.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = miPreferencia.edit();
                    editor.putString("idVestimenta", item.getId()+"");
                    editor.commit();
                    showInputDialog(item.getDescripcion(), item.getColor(), item.getEstilo(), item.getGenero(), item.getTipo(), item.getTalla(), item.getMarca(), item.getPrecio(), item.getImagen());
                    //Toast toast1 = Toast.makeText(context, "hola :P"+item.getNombre(), Toast.LENGTH_SHORT);
                    //toast1.show();
                }
            });
            Button button3 = (Button) view.findViewById(R.id.ivesEliminar);
            // Capture button clicks
            button3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    SharedPreferences miPreferencia = context.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = miPreferencia.edit();
                    editor.putString("idVestimenta", item.getId()+"");
                    editor.commit();
                    confirmarEliminar();
                    //Toast toast = Toast.makeText(context, "Eliminar", Toast.LENGTH_LONG);
                    //toast.show();

                }
            });
            // Recupera los elementos de vista y setea en funcion de valores de objeto
            TextView titulo = (TextView) view.findViewById(R.id.ivesdato1);
            TextView nombre = (TextView) view.findViewById(R.id.ivesdato2);
            ImageView imagen = (ImageView) view.findViewById(R.id.ves_imagen);
            Glide.with(context)
                    .load(Uri.parse(Util.URL_IMG+item.getImagen()))
                    .transform(new CircleGlide(context))
                    .into(imagen);
            if (titulo != null) {
                titulo.setText(item.getDescripcion());
                nombre.setText(item.getEstilo());
            }
        }
        return view;
    }

    protected void showInputDialog(String desc, String col, String est, String gen, String tip, String tal, Marca mar, Double pre, String img) {
        Spinner color;
        Spinner estilo;
        Spinner genero;
        Spinner tipo;
        Spinner talla;
        Spinner marca;
        List<Marca> marcas;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View promptView = layoutInflater.inflate(R.layout.dialog_ves_edit, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        color = (Spinner) promptView.findViewById(R.id.ves_color_edit);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(context, R.array.Color, R.layout.support_simple_spinner_dropdown_item);
        color.setAdapter(adapter1);

        estilo = (Spinner) promptView.findViewById(R.id.ves_estilo_edit);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(context, R.array.Estilo, R.layout.support_simple_spinner_dropdown_item);
        estilo.setAdapter(adapter2);

        genero = (Spinner) promptView.findViewById(R.id.ves_genero_edit);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(context, R.array.GeneroV, R.layout.support_simple_spinner_dropdown_item);
        genero.setAdapter(adapter3);

        tipo = (Spinner) promptView.findViewById(R.id.ves_tipo_edit);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(context, R.array.Tipo, R.layout.support_simple_spinner_dropdown_item);
        tipo.setAdapter(adapter4);

        talla = (Spinner) promptView.findViewById(R.id.ves_talla_edit);
        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(context, R.array.Talla, R.layout.support_simple_spinner_dropdown_item);
        talla.setAdapter(adapter5);

        TextView descripcion = (TextView) promptView.findViewById(R.id.ves_descripcion_edit);
        TextView precio = (TextView) promptView.findViewById(R.id.ves_precio_edit);
        TextView imagen = (TextView) promptView.findViewById(R.id.ves_imagen_edit);
        descripcion.setText(desc);
        precio.setText(pre+"");
        imagen.setText(img);



        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Toast toast1 = Toast.makeText(context, "hecho", Toast.LENGTH_SHORT);
                        // toast1.show();
                        Empresa em=new Empresa();
                       // confirmarGuardarEmp();

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void confirmarEliminar() {
        String pregunta = "Esto seguro de eliminar";
        new android.app.AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.msj_confirmacion))
                .setMessage(pregunta)
                .setNegativeButton(android.R.string.cancel, null)//sin listener
                .setPositiveButton(context.getResources().getString(R.string.lbl_aceptar),
                        new DialogInterface.OnClickListener() {//un listener que al pulsar, solicite el WS de Transsaccion
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                EliminarVes();
                            }
                        })
                .show();
    }

    private void EliminarVes(){
        SharedPreferences miPreferencia = context.getSharedPreferences("MisPreferencias", context.MODE_PRIVATE);
        String id = miPreferencia.getString("idVestimenta","No de pudo obtener");
        try {
            String URL = Util.URL_SRV + "vestimenta/delvestimenta";
            ClienteRest clienteRest = new ClienteRest(this);
            clienteRest.doGet(URL, "?id="+id , ELIMINARVES, true);
        }catch(Exception e){
            Util.showMensaje(context, R.string.msj_error_clienrest);
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskCompleted(int idSolicitud, String result) {
        Log.i("MainActivity", "" + result);
        switch (idSolicitud){
            case ELIMINARVES:
                if(result!=null){
                    try {
                        Empresa res =  ClienteRest.getResult(result, Empresa.class);
                        Util.showMensaje(context, "Eliminado");
                    }catch (Exception e){
                        Log.i("MainActivity", "Error en carga de categoria por ID", e);
                        Util.showMensaje(context, R.string.msj_error_clienrest_formato);
                    }
                }else
                    Util.showMensaje(context, R.string.msj_error_clienrest);
                break;
            default:
                break;
        }

    }
}
