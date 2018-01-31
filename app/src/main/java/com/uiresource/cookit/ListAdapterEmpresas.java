package com.uiresource.cookit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uiresource.cookit.modelo.Empresa;
import com.uiresource.cookit.modelo.Respuesta;
import com.uiresource.cookit.utilidades.ClienteRest;
import com.uiresource.cookit.utilidades.OnTaskCompleted;
import com.uiresource.cookit.utilidades.Util;

import java.util.ArrayList;

/**
 * Created by luisl on 1/11/2018.
 */

public class ListAdapterEmpresas extends ArrayAdapter<Empresa> implements OnTaskCompleted {
    //Contenxto de la aplicacion que relaciona al ListView y el Adpater
    private Context context;
    TextView nombre;
    TextView descripcion ;
    TextView ruc;
    TextView direccion;
    TextView contacto;
    private static final int ELIMINAR_EMPRESA = 2;
    private static final int EDITAR_EMPRESA = 1;

    /**
     * Inicializacion
     *
     * @param context Contexto de la App desde la que se invoca
     * @param items   //Coleccion de objetos a presentar
     */
    public ListAdapterEmpresas(Context context, ArrayList<Empresa> items) {
        super(context, R.layout.item_empresa, items);
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
            view = inflater.inflate(R.layout.item_empresa, null);
        }

        //Objeto Categoria a visualizar segun position
        final Empresa item = getItem(position);
        if (item != null) {
            Button button = (Button) view.findViewById(R.id.iempEdit);
            // Capture button clicks
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    SharedPreferences miPreferencia = context.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = miPreferencia.edit();
                    editor.putString("idEmpresa", item.getId()+"");
                    editor.commit();
                    showInputDialog(item.getNombre(), item.getDescripcion(), item.getRuc(), item.getDireccion(), item.getContacto());
                    //Toast toast1 = Toast.makeText(context, "hola :P"+item.getNombre(), Toast.LENGTH_SHORT);
                    //toast1.show();
                }
            });


            Button button2 = (Button) view.findViewById(R.id.iempAdministrar);
            // Capture button clicks
            button2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    SharedPreferences miPreferencia = context.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = miPreferencia.edit();
                    editor.putString("idEmpresa", item.getId()+"");
                    editor.commit();
                    Intent myIntent = new Intent(context, VestimentasActivity.class);
                    context.startActivity(myIntent);

                }
            });
            Button button3 = (Button) view.findViewById(R.id.iempEliminar);
            // Capture button clicks
            button3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    Toast toast = Toast.makeText(context, "Eliminar", Toast.LENGTH_LONG);
                    toast.show();

                }
            });



            // Recupera los elementos de vista y setea en funcion de valores de objeto
            TextView titulo = (TextView) view.findViewById(R.id.inomEmpresa);
            TextView nombre = (TextView) view.findViewById(R.id.iempRuc);

            if (titulo != null) {
                titulo.setText(item.getNombre());
                nombre.setText(item.getRuc());
            }
        }
        return view;
    }

    protected void showInputDialog(String nomb, String desc, String ru, String dir, String con) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View promptView = layoutInflater.inflate(R.layout.dialog_empresa_edit, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
         nombre = (TextView) promptView.findViewById(R.id.editNombreEmp);
         descripcion = (TextView) promptView.findViewById(R.id.editDescripcionEmp);
         ruc = (TextView) promptView.findViewById(R.id.editRucEmp);
         direccion = (TextView) promptView.findViewById(R.id.editDireccionEmp);
         contacto = (TextView) promptView.findViewById(R.id.editContactoEmp);

        nombre.setText(nomb);
        descripcion.setText(desc);
        ruc.setText(ru);
        direccion.setText(dir);
        contacto.setText(con);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        confirmarGuardarEmp();

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
    private void confirmarGuardarEmp() {
        String pregunta = "Esto seguro de realizar el registro?";
        new android.app.AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.msj_confirmacion))
                .setMessage(pregunta)
                .setNegativeButton(android.R.string.cancel, null)//sin listener
                .setPositiveButton(context.getResources().getString(R.string.lbl_aceptar),
                        new DialogInterface.OnClickListener() {//un listener que al pulsar, solicite el WS de Transsaccion
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                guardarEmp();
                            }
                        })
                .show();
    }
    private void guardarEmp(){
        SharedPreferences miPreferencia = context.getSharedPreferences("MisPreferencias", context.MODE_PRIVATE);
        String id = miPreferencia.getString("idEmpresa","No de pudo obtener");
        String nombreE = nombre.getText().toString();
        String descripcionE = descripcion.getText().toString();
        String rucE = ruc.getText().toString();
        String direccionE = direccion.getText().toString();
        String contactoE = contacto.getText().toString();

        try {
            String URL = Util.URL_SRV + "empresa/editempresa";
            ClienteRest clienteRest = new ClienteRest(this);
            Empresa e = new Empresa();
            e.setId(Integer.parseInt(id));
            e.setNombre(nombreE);
            e.setDescripcion(descripcionE);
            e.setRuc(rucE);
            e.setDireccion(direccionE);
            e.setContacto(contactoE);
            String idUsuario = miPreferencia.getString("idUsuario","No de pudo obtener");
            clienteRest.doPost2(URL, e,"?id="+idUsuario , EDITAR_EMPRESA, true);
        }catch(Exception e){
            Util.showMensaje(context, R.string.msj_error_clienrest);
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskCompleted(int idSolicitud, String result) {
        switch (idSolicitud){
            case EDITAR_EMPRESA:
                if(result!=null){
                    try {
                        Respuesta res = ClienteRest.getResult(result, Respuesta.class);
                        Util.showMensaje(context, res.getMensaje());
                        if (res.getCodigo() == 1) {

                            //consultaEmpresa();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Util.showMensaje(context,R.string.msj_error_clienrest_formato);
                    }
                }else
                    Util.showMensaje(context,R.string.msj_error_clienrest);
                break;
            default:
                break;
        }

    }



}
