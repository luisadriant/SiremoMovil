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


public class EmpresaVesFragment extends Fragment implements OnTaskCompleted, View.OnClickListener {

    private static final int INGRESAR_EMPRESA = 1;
    private static final int SOLICITUD_EMPRESA = 2;
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
        return inflater.inflate(R.layout.fragment_ves_empresa, container, false);
    }
    @Override
    public void onResume() {
        super.onResume();
        ((Button) this.getActivity().findViewById(R.id.Vbtn_empresa)).setOnClickListener(this);
        consultaEmpresa();

    }

    protected void consultaEmpresa() {
        try {
            //  http://localhost:8080/miSiremo/srv/login/administrador?usuario=Admin&pass=1234
            //http://localhost:8080/miSiremo/srv/empresa/listaempresas
            String URL = Util.URL_SRV + "empresa/verempresa";
            ClienteRest clienteRest = new ClienteRest(this);
            SharedPreferences miPreferencia = this.getActivity().getSharedPreferences("MisPreferencias", globalContext.MODE_PRIVATE);
            String idEmpresa = miPreferencia.getString("idEmpresa","No de pudo obtener");
            clienteRest.doGet(URL, "?id="+idEmpresa, SOLICITUD_EMPRESA, true);
        }catch(Exception e){
            Util.showMensaje(globalContext, R.string.msj_error_clienrest);
            e.printStackTrace();
        }
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
                            consultaEmpresa();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Util.showMensaje(globalContext,R.string.msj_error_clienrest_formato);
                    }
                }else
                    Util.showMensaje(globalContext,R.string.msj_error_clienrest);
                break;
            case SOLICITUD_EMPRESA:
                if(result!=null){
                    try {
                        Empresa empresa = ClienteRest.getResult(result, Empresa.class);
                        //Util.showMensaje(globalContext, res.getMensaje());
                        if (empresa.getId() != 0) {
                            ((EditText) this.getActivity().findViewById(R.id.VadmNombreEmp)).setText(empresa.getNombre());
                            ((EditText) this.getActivity().findViewById(R.id.VadmDescripcionEmp)).setText(empresa.getDescripcion());
                            ((EditText) this.getActivity().findViewById(R.id.VadmRucEmp)).setText(empresa.getRuc());
                            ((EditText) this.getActivity().findViewById(R.id.VadmDireccionEmp)).setText(empresa.getDireccion());
                            ((EditText) this.getActivity().findViewById(R.id.VadmContactoEmp)).setText(empresa.getContacto());
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
        SharedPreferences miPreferencia = this.getActivity().getSharedPreferences("MisPreferencias", globalContext.MODE_PRIVATE);
        String id = miPreferencia.getString("idEmpresa","No de pudo obtener");
        String nombre = ((EditText) this.getActivity().findViewById(R.id.VadmNombreEmp)).getText().toString();
        String descripcion = ((EditText) this.getActivity().findViewById(R.id.VadmDescripcionEmp)).getText().toString();
        String ruc = ((EditText) this.getActivity().findViewById(R.id.VadmRucEmp)).getText().toString();
        String direccion = ((EditText) this.getActivity().findViewById(R.id.VadmDireccionEmp)).getText().toString();
        String contacto = ((EditText) this.getActivity().findViewById(R.id.VadmContactoEmp)).getText().toString();

        try {
            String URL = Util.URL_SRV + "empresa/editempresa";
            ClienteRest clienteRest = new ClienteRest(this);
            Empresa e = new Empresa();
            e.setId(Integer.parseInt(id));
            e.setNombre(nombre);
            e.setDescripcion(descripcion);
            e.setRuc(ruc);
            e.setDireccion(direccion);
            e.setContacto(contacto);
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
            case R.id.Vbtn_empresa:
                confirmarGuardarEmp();
                break;
            default:
                break;
        }
    }
}
