package com.uiresource.cookit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.uiresource.cookit.modelo.Empresa;
import com.uiresource.cookit.modelo.Respuesta;
import com.uiresource.cookit.utilidades.ClienteRest;
import com.uiresource.cookit.utilidades.OnTaskCompleted;
import com.uiresource.cookit.utilidades.Util;

public class NuevaEmpresaFragment extends Fragment implements OnTaskCompleted, View.OnClickListener {

    private static final int INGRESAR_EMPRESA = 1;
    private Context globalContext = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getApplicationContext();
        globalContext=this.getActivity();
        //((Button) this.getActivity().findViewById(R.id.NEbtn_reg)).setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nueva_empresa, container, false);
    }
    @Override
    public void onResume() {
        super.onResume();
        ((Button) this.getActivity().findViewById(R.id.NEbtn_reg)).setOnClickListener(this);

    }

    @Override
    public void onTaskCompleted(int idSolicitud, String result) {
        switch (idSolicitud){
            case INGRESAR_EMPRESA:
                if(result!=null){
                    try {
                        Respuesta res = ClienteRest.getResult(result, Respuesta.class);
                        Util.showMensaje(globalContext, res.getMensaje());
                        if (res.getCodigo() == 1) {
                            ((EditText) this.getActivity().findViewById(R.id.NEadmNombreEmp)).setText("");
                            ((EditText) this.getActivity().findViewById(R.id.NEadmDescripcionEmp)).setText("");
                            ((EditText) this.getActivity().findViewById(R.id.NEadmRucEmp)).setText("");
                            ((EditText) this.getActivity().findViewById(R.id.NEadmDireccionEmp)).setText("");
                            ((EditText) this.getActivity().findViewById(R.id.NEadmContactoEmp)).setText("");
                            //this.getActivity().finish();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Util.showMensaje(globalContext,R.string.msj_error_clienrest_formato);
                    }
                }else
                    Util.showMensaje(globalContext,R.string.msj_error_clienrest);
                break;
            default:
                break;
        }

    }
    private void confirmarGuardarEmp() {
        String pregunta = "Esto seguro de realizar el registro?";
        new AlertDialog.Builder(globalContext)
                .setTitle(getResources().getString(R.string.msj_confirmacion))
                .setMessage(pregunta)
                .setNegativeButton(android.R.string.cancel, null)//sin listener
                .setPositiveButton(getResources().getString(R.string.lbl_aceptar),
                        new DialogInterface.OnClickListener() {//un listener que al pulsar, solicite el WS de Transsaccion
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                guardarEmp();
                            }
                        })
                .show();
    }
    private void guardarEmp(){
        String nombre = ((EditText) this.getActivity().findViewById(R.id.NEadmNombreEmp)).getText().toString();
        String descripcion = ((EditText) this.getActivity().findViewById(R.id.NEadmDescripcionEmp)).getText().toString();
        String ruc = ((EditText) this.getActivity().findViewById(R.id.NEadmRucEmp)).getText().toString();
        String direccion = ((EditText) this.getActivity().findViewById(R.id.NEadmDireccionEmp)).getText().toString();
        String contacto = ((EditText) this.getActivity().findViewById(R.id.NEadmContactoEmp)).getText().toString();

        try {
            String URL = Util.URL_SRV + "empresa/newempresa";
            ClienteRest clienteRest = new ClienteRest(this);
            Empresa e = new Empresa();
            e.setNombre(nombre);
            e.setDescripcion(descripcion);
            e.setRuc(ruc);
            e.setDireccion(direccion);
            e.setContacto(contacto);
            SharedPreferences miPreferencia = this.getActivity().getSharedPreferences("MisPreferencias", globalContext.MODE_PRIVATE);
            String idUsuario = miPreferencia.getString("idUsuario","No de pudo obtener");
            clienteRest.doPost2(URL, e,"?id="+idUsuario , INGRESAR_EMPRESA, true);
        }catch(Exception e){
            Util.showMensaje(globalContext, R.string.msj_error_clienrest);
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.NEbtn_reg:
                confirmarGuardarEmp();
                break;
            default:
                break;
        }
    }
}
