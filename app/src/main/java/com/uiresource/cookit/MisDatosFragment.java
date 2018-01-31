package com.uiresource.cookit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.uiresource.cookit.modelo.Administrador;
import com.uiresource.cookit.modelo.Respuesta;
import com.uiresource.cookit.utilidades.ClienteRest;
import com.uiresource.cookit.utilidades.OnTaskCompleted;
import com.uiresource.cookit.utilidades.Util;


public class MisDatosFragment extends Fragment implements OnTaskCompleted, View.OnClickListener {
    private static final int SOLICITUD_ADMIN = 1;
    private static final int SOLICITUD_EDIT = 2;
    private Context globalContext = null;
    private Spinner spinner;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getApplicationContext();
        globalContext=this.getActivity();


    }
    @Override
    public void onResume() {
        super.onResume();
        spinner = (Spinner) this.getActivity().findViewById(R.id.DUusrGenero);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(globalContext, R.array.generos, R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        consultaAdministrador();
        ((Button) this.getActivity().findViewById(R.id.DUbtn_reg_usr)).setOnClickListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mis_datos, container, false);

    }
    protected void consultaAdministrador() {
        try {
            String URL = Util.URL_SRV + "login/datosadministrador";
            ClienteRest clienteRest = new ClienteRest(this);
            SharedPreferences miPreferencia = this.getActivity().getSharedPreferences("MisPreferencias", globalContext.MODE_PRIVATE);
            String idUsuario = miPreferencia.getString("idUsuario","No de pudo obtener");
            clienteRest.doGet(URL, "?id="+idUsuario, SOLICITUD_ADMIN, true);
        }catch(Exception e){
            Util.showMensaje(globalContext, R.string.msj_error_clienrest);
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskCompleted(int idSolicitud, String result) {
        Log.i("MainActivity", "" + result);
        switch (idSolicitud){
            case SOLICITUD_ADMIN:
                if(result!=null){
                    try {
                        Administrador administrador =  ClienteRest.getResult(result, Administrador.class);
                        TextView nombre = (TextView) this.getActivity().findViewById(R.id.DUusrNombres);
                        TextView apellidos = (TextView) this.getActivity().findViewById(R.id.DUusrApellidos);
                        TextView nusuario = (TextView) this.getActivity().findViewById(R.id.DUusrNombreUsr);
                        TextView telefono = (TextView) this.getActivity().findViewById(R.id.DUusrTelefono);
                        TextView fechan = (TextView) this.getActivity().findViewById(R.id.DUusrFechaNac);
                        TextView email = (TextView) this.getActivity().findViewById(R.id.DUusrEmail);
                        TextView pass = (TextView) this.getActivity().findViewById(R.id.DUusrPass);
                        TextView rpass = (TextView) this.getActivity().findViewById(R.id.DUusrRpass);

                        if(administrador.getNombres()!=null){
                            nombre.setText(administrador.getNombres());
                            apellidos.setText(administrador.getApellidos());
                            nusuario.setText(administrador.getNombreusuario());
                            telefono.setText(administrador.getTelefono());
                            fechan.setText(administrador.getFechanacimiento()+"");
                            email.setText(administrador.getEmail());
                            if(administrador.getGenero().equals("Masculino"))
                            spinner.setSelection(1);
                            else if(administrador.getGenero().equals("Femenino"))
                                spinner.setSelection(2);
                            pass.setText(administrador.getContrasenia());
                            rpass.setText(administrador.getContrasenia());


                        }else{
                            Util.showMensaje(globalContext, "Datos Incorrectos");
                        }

                    }catch (Exception e){
                        Log.i("MainActivity", "Error en carga de Usuarios", e);
                        Util.showMensaje(globalContext, R.string.msj_error_clienrest_formato);
                    }
                }else
                    Util.showMensaje(globalContext, R.string.msj_error_clienrest);
            break;
            case SOLICITUD_EDIT:
                if(result!=null){
                    try {
                        Respuesta res = ClienteRest.getResult(result, Respuesta.class);
                        Util.showMensaje(globalContext, res.getMensaje());
                        if (res.getCodigo() == 1) {
                            Util.showMensaje(globalContext,"Hecho!");
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
    private void confirmarEditarDatos() {
        String pregunta = "Esto seguro de realizar la actualizacion?";
        new AlertDialog.Builder(globalContext)
                .setTitle(getResources().getString(R.string.msj_confirmacion))
                .setMessage(pregunta)
                .setNegativeButton(android.R.string.cancel, null)//sin listener
                .setPositiveButton(getResources().getString(R.string.lbl_aceptar),
                        new DialogInterface.OnClickListener() {//un listener que al pulsar, solicite el WS de Transsaccion
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                editarDatos();
                            }
                        })
                .show();
    }
    private void editarDatos(){
        String nombres = ((EditText) this.getActivity().findViewById(R.id.DUusrNombres)).getText().toString();
        String apellidos = ((EditText) this.getActivity().findViewById(R.id.DUusrApellidos)).getText().toString();
        String nombreusr = ((EditText) this.getActivity().findViewById(R.id.DUusrNombreUsr)).getText().toString();
        String telefono = ((EditText) this.getActivity().findViewById(R.id.DUusrTelefono)).getText().toString();
        String fechaNac = ((EditText) this.getActivity().findViewById(R.id.DUusrFechaNac)).getText().toString();
        String email = ((EditText) this.getActivity().findViewById(R.id.DUusrEmail)).getText().toString();
        String pasword = ((EditText) this.getActivity().findViewById(R.id.DUusrPass)).getText().toString();
        String genero= spinner.getSelectedItem().toString();

        try {
            String URL = Util.URL_SRV + "registro/editadministrador";
            ClienteRest clienteRest = new ClienteRest(this);
            Administrador a = new Administrador();
            a.setNombres(nombres);
            a.setApellidos(apellidos);
            a.setNombreusuario(nombreusr);
            a.setTelefono(telefono);
            a.setEmail(email);
            a.setContrasenia(pasword);
            a.setGenero(genero);
            SharedPreferences miPreferencia = this.getActivity().getSharedPreferences("MisPreferencias", globalContext.MODE_PRIVATE);
            String idUsuario = miPreferencia.getString("idUsuario","No de pudo obtener");
            clienteRest.doPost2(URL, a,"?id="+idUsuario , SOLICITUD_EDIT, true);
        }catch(Exception e){
            Util.showMensaje(globalContext, R.string.msj_error_clienrest);
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.DUbtn_reg_usr:
                confirmarEditarDatos();
                break;
            default:
                break;
        }
    }
}
