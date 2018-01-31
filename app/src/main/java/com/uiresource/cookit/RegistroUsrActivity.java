package com.uiresource.cookit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.uiresource.cookit.modelo.Respuesta;
import com.uiresource.cookit.modelo.Usuario;
import com.uiresource.cookit.utilidades.ClienteRest;
import com.uiresource.cookit.utilidades.OnTaskCompleted;
import com.uiresource.cookit.utilidades.Util;


public class RegistroUsrActivity extends BaseActivity  implements OnTaskCompleted {
    private static final int SOLICITUD_GUARDAR_USUARIO = 2;
    Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usr);
        setupToolbar(R.id.toolbarRegUsu, "REGRISTO USUARIO", R.color.colorPink, R.color.colorWhiteTrans, R.drawable.ic_arrow_back);

        // Locate the button in activity_main.xml
       Button btnRegistrar = (Button) findViewById(R.id.btn_reg_usr);

        // Capture button clicks
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                confirmarGuardarUsr();
                // Start NewActivity.class
                //Intent myIntent = new Intent(RegistroUsrActivity.this,
              //          LoginActivity.class);
               // startActivity(myIntent);
            }
        });


        spinner = (Spinner) findViewById(R.id.usrGenero);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.generos, R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
     * * Con este codigo se hace parara poner la flecha de regreso atras como whatsapp,
     * ojo en el manifets con esta linea  (android:parentActivityName=".VentanaChat") y
     * verificar como esta todo en el manifest
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home: //hago un case por si en un futuro agrego mas opcione

                // Intent ventanaLogeo = new Intent(this, LoginActivity.class);

                //  startActivity(ventanaLogeo);
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return  super.onOptionsItemSelected(item);
    }


    @Override
    public void onTaskCompleted(int idSolicitud, String result) {
        switch (idSolicitud){
            case SOLICITUD_GUARDAR_USUARIO:
                if(result!=null){
                    try {
                        Respuesta res = ClienteRest.getResult(result, Respuesta.class);
                        Util.showMensaje(this, res.getMensaje());
                        if (res.getCodigo() == 1) {
                            finish();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Util.showMensaje(this,R.string.msj_error_clienrest_formato);
                    }
                }else
                    Util.showMensaje(this,R.string.msj_error_clienrest);
                break;
            default:
                break;
        }

    }
    /**
     * Procedimiento para validar y confirmar la transaccion,
     */
    private void confirmarGuardarUsr() {
        String pregunta = "Esto seguro de realizar el registro?";
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.msj_confirmacion))
                .setMessage(pregunta)
                .setNegativeButton(android.R.string.cancel, null)//sin listener
                .setPositiveButton(getResources().getString(R.string.lbl_aceptar),
                        new DialogInterface.OnClickListener() {//un listener que al pulsar, solicite el WS de Transsaccion
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                guardarUsr();
                            }
                        })
                .show();
    }
    private void guardarUsr(){
        String nombres = ((EditText) findViewById(R.id.usrNombres)).getText().toString();
        String apellidos = ((EditText) findViewById(R.id.usrApellidos)).getText().toString();
        String nombreusr = ((EditText) findViewById(R.id.usrNombreUsr)).getText().toString();
        String telefono = ((EditText) findViewById(R.id.usrTelefono)).getText().toString();
        String fechaNac = ((EditText) findViewById(R.id.usrFechaNac)).getText().toString();
        String email = ((EditText) findViewById(R.id.usrEmail)).getText().toString();
        String pasword = ((EditText) findViewById(R.id.usrPass)).getText().toString();
        String genero= spinner.getSelectedItem().toString();

        try {
            String URL = Util.URL_SRV + "registro/usuario";
            ClienteRest clienteRest = new ClienteRest(this);
            Usuario usuario = new Usuario();
            usuario.setNombres(nombres);
            usuario.setApellidos(apellidos);
            usuario.setNombreusuario(nombreusr);
            usuario.setTelefono(telefono);
            usuario.setEmail(email);
            usuario.setContrasenia(pasword);
            usuario.setGenero(genero);
            // a.setFechanacimiento(new Date());
            clienteRest.doPost2(URL, usuario, "?fecha="+fechaNac, SOLICITUD_GUARDAR_USUARIO, true);
        }catch(Exception e){
            Util.showMensaje(this, R.string.msj_error_clienrest);
            e.printStackTrace();
        }
    }
}
