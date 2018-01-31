package com.uiresource.cookit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import com.uiresource.cookit.modelo.Administrador;
import com.uiresource.cookit.modelo.Empresa;
import com.uiresource.cookit.modelo.Marca;
import com.uiresource.cookit.modelo.Usuario;
import com.uiresource.cookit.utilidades.ClienteRest;
import com.uiresource.cookit.utilidades.OnTaskCompleted;
import com.uiresource.cookit.utilidades.Util;


import java.util.List;

public class LoginActivity extends AppCompatActivity implements  OnTaskCompleted {
    private static final int SOLICITUD_USUARIO = 1;
    private static final int SOLICITUD_ADMIN = 2;
    private static final int SOLICITUD_ESTILO = 3;

    private TextView txtRegistrarse, txtRegistrarseAdmi;
    private Button btnIniciarSesion;
    private EditText nomUsuario, contrasenaUsuario;
    private Spinner estilo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        btnIniciarSesion = (Button)findViewById(R.id.btnIniciarSesion);
        txtRegistrarse = (TextView)findViewById(R.id.txtRegitrarseUsu);
        txtRegistrarseAdmi = (TextView)findViewById(R.id.txtRegistrarseAdmi);

        nomUsuario = (EditText)findViewById(R.id.nomUsuario);
        contrasenaUsuario = (EditText)findViewById(R.id.contrasenaLogin);

//si ya se logeo un vez ya lo deja entrar directamente a la otra ventana.
      /*  SharedPreferences miPreferencia = getSharedPreferences("MisPreferencias",Context.MODE_PRIVATE);
        Boolean estadoLogeo = miPreferencia.getBoolean("estadoLogeo", false);

        if(estadoLogeo){
            //va a dejar ingresar a la otra ventana.

            Intent ventanaPrincipal = new Intent();
            ventanaPrincipal.setClass(getApplicationContext(), Main.class);
            ventanaPrincipal.putExtra("usuario",nomUsuario.getText().toString());

            startActivity(ventanaPrincipal);
            finish();
        }*/

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultaUsuario();
            }
        });




        //evento del boton registrar
        txtRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ventanaRegistro = new Intent(getApplicationContext(),RegistroUsrActivity.class);
                startActivity(ventanaRegistro);

            }
        });


        //evento del boton recuperar contraseÃ±a
        txtRegistrarseAdmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent ventanaRegistroAdmi = new Intent(getApplicationContext(),RegistroActivity.class);
                startActivity(ventanaRegistroAdmi);

            }
        });


    }


    protected void consultaAdministrador() {
        try {
            String usuario = nomUsuario.getText().toString();
            String pass = contrasenaUsuario.getText().toString();
            String URL = Util.URL_SRV + "login/administrador";
            ClienteRest clienteRest = new ClienteRest(this);
            clienteRest.doGet(URL, "?usuario="+usuario+"&pass="+pass, SOLICITUD_ADMIN, true);
        }catch(Exception e){
            Util.showMensaje(this, R.string.msj_error_clienrest);
            e.printStackTrace();
        }
    }
    protected void consultaUsuario() {
        try {
            String usuario = nomUsuario.getText().toString();
            String pass = contrasenaUsuario.getText().toString();
            String URL = Util.URL_SRV + "login/usuario";
            ClienteRest clienteRest = new ClienteRest(this);
            clienteRest.doGet(URL, "?usuario="+usuario+"&pass="+pass, SOLICITUD_USUARIO, true);
        }catch(Exception e){
            Util.showMensaje(this, R.string.msj_error_clienrest);
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
                        if(administrador.getNombres()!=null){

                            Util.showMensaje(this, "Hola "+administrador.getNombres());
                            //aqui va a guardarse con el sharedPreference los datos del usuario.
                            SharedPreferences miPreferencia = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = miPreferencia.edit();
                            editor.putString("idUsuario", administrador.getId()+"");
                            editor.commit();
                            Intent myIntent = new Intent(LoginActivity.this, EmpresasActivity.class);
                            startActivity(myIntent);
                            this.finish();
                        }else{
                            Util.showMensaje(this, "Datos Incorrectos");
                        }

                    }catch (Exception e){
                        Log.i("MainActivity", "Error en carga de Usuarios", e);
                        Util.showMensaje(this, R.string.msj_error_clienrest_formato);
                    }
                }else
                    Util.showMensaje(this, R.string.msj_error_clienrest);
                break;
            case SOLICITUD_USUARIO:
                if(result!=null){
                    try {
                        Usuario usuario =  ClienteRest.getResult(result, Usuario.class);
                        if(usuario.getNombres()!=null) {
                            if(usuario.getEstilo()==null){
                                SharedPreferences miPreferencia = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = miPreferencia.edit();
                                editor.putString("idUsuario", usuario.getId() + "");
                                editor.commit();
                                showInputDialog();
                            }else {
                                Util.showMensaje(this, "Hola " + usuario.getNombres());
                                //aqui va a guardarse con el sharedPreference los datos del usuario.
                                SharedPreferences miPreferencia = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = miPreferencia.edit();
                                editor.putString("idUsuario", usuario.getId() + "");
                                editor.putString("nomUsuario", usuario.getNombres() + " " + usuario.getApellidos() + "");
                                editor.commit();

                                Intent ventanaPrincipal = new Intent();
                                ventanaPrincipal.setClass(getApplicationContext(), Main.class);
                                startActivity(ventanaPrincipal);

                                this.finish();
                            }
                        }else{
                            consultaAdministrador();
                        }

                    }catch (Exception e){
                        Log.i("MainActivity", "Error en carga de Usuarios", e);
                        Util.showMensaje(this, R.string.msj_error_clienrest_formato);
                    }
                }else
                    Util.showMensaje(this, R.string.msj_error_clienrest);
                break;
            case SOLICITUD_ESTILO:
                if(result!=null){
                    try {
                        Usuario usuario =  ClienteRest.getResult(result, Usuario.class);
                        if(usuario.getNombres()!=null) {

                            Util.showMensaje(this, "Hola " + usuario.getNombres());
                            //aqui va a guardarse con el sharedPreference los datos del usuario.
                            SharedPreferences miPreferencia = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = miPreferencia.edit();
                            editor.putString("idUsuario", usuario.getId()+"");
                            editor.putString("nomUsuario", usuario.getNombres()+" "+usuario.getApellidos()+"");
                            editor.commit();

                            Intent ventanaPrincipal = new Intent();
                            ventanaPrincipal.setClass(getApplicationContext(), Main.class);
                            startActivity(ventanaPrincipal);

                            this.finish();
                        }else{
                            consultaAdministrador();
                        }

                    }catch (Exception e){
                        Log.i("MainActivity", "Error en carga de Usuarios", e);
                        Util.showMensaje(this, R.string.msj_error_clienrest_formato);
                    }
                }else
                    Util.showMensaje(this, R.string.msj_error_clienrest);
                break;
            default:
                break;
        }

    }

    protected void showInputDialog() {


        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View promptView = layoutInflater.inflate(R.layout.dialog_estilo, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);


        estilo = (Spinner) promptView.findViewById(R.id.spinner_estilo);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.Estilo, R.layout.support_simple_spinner_dropdown_item);
        estilo.setAdapter(adapter2);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        editarUsuario();
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
    private void editarUsuario(){
        SharedPreferences miPreferencia = this.getSharedPreferences("MisPreferencias", this.MODE_PRIVATE);
        String id = miPreferencia.getString("idUsuario","No de pudo obtener");
        try {
            String URL = Util.URL_SRV + "login/setestilo";
            String est=estilo.getSelectedItem().toString();
            ClienteRest clienteRest = new ClienteRest(this);
            clienteRest.doGet(URL, "?id="+id+"&est="+est, SOLICITUD_ESTILO, true);
        }catch(Exception e){
            Util.showMensaje(this, R.string.msj_error_clienrest);
            e.printStackTrace();
        }
    }

}
