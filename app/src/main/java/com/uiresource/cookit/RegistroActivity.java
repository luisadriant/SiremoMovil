package com.uiresource.cookit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.uiresource.cookit.modelo.Administrador;
import com.uiresource.cookit.modelo.Empresa;
import com.uiresource.cookit.modelo.Respuesta;
import com.uiresource.cookit.utilidades.ClienteRest;
import com.uiresource.cookit.utilidades.OnTaskCompleted;
import com.uiresource.cookit.utilidades.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegistroActivity extends BaseActivity implements View.OnClickListener, OnTaskCompleted {

    private static final int SOLICITUD_GUARDAR_ADMINISTRADOR = 2;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        setupToolbar(R.id.toolbarRegAdmi, "REGRISTO ADMINISTRADOR", R.color.colorPink, R.color.colorWhiteTrans, R.drawable.ic_arrow_back);

        // Locate the button in activity_main.xml
        //Button button = (Button) findViewById(R.id.btn_canc);

        // Capture button clicks
       /* button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent myIntent = new Intent(RegistroActivity.this,
                        LoginActivity.class);
                startActivity(myIntent);
            }
        });*/

        ((Button) findViewById(R.id.btn_reg)).setOnClickListener(this);
        spinner = (Spinner) findViewById(R.id.admGenero);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.generos, R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_reg:
                confirmarGuardarAdmin();
                break;
            default:
                break;
        }
    }

    /**
     * Procedimiento para validar y confirmar la transaccion,
     */
    private void confirmarGuardarAdmin() {
        String pregunta = "Esto seguro de realizar el registro?";
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.msj_confirmacion))
                .setMessage(pregunta)
                .setNegativeButton(android.R.string.cancel, null)//sin listener
                .setPositiveButton(getResources().getString(R.string.lbl_aceptar),
                        new DialogInterface.OnClickListener() {//un listener que al pulsar, solicite el WS de Transsaccion
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                guardarAdmin();
                            }
                        })
                .show();
    }
    private void guardarAdmin(){
        int id;
        try {
            //id = Integer.parseInt(((EditText) findViewById(R.id.admNombres)).getText().toString());
        }catch (Exception e){
           // Util.showMensaje(this, "Formato incorrecto en código, revisar...");
            //return;
        }
        String nombres = ((EditText) findViewById(R.id.admNombres)).getText().toString();
        String apellidos = ((EditText) findViewById(R.id.admApellidos)).getText().toString();
        String nombreusr = ((EditText) findViewById(R.id.admNombreUsr)).getText().toString();
        String telefono = ((EditText) findViewById(R.id.admTelefono)).getText().toString();
        String fechaNac = ((EditText) findViewById(R.id.admFechaNac)).getText().toString();
        String email = ((EditText) findViewById(R.id.admEmail)).getText().toString();
        String pasword = ((EditText) findViewById(R.id.admPass)).getText().toString();
        String genero= spinner.getSelectedItem().toString();

        String empnombre = ((EditText) findViewById(R.id.admNombreEmp)).getText().toString();
        String empdescripccion = ((EditText) findViewById(R.id.admDescripcionEmp)).getText().toString();
        String empruc = ((EditText) findViewById(R.id.admRucEmp)).getText().toString();
        String empdireccion = ((EditText) findViewById(R.id.admDireccionEmp)).getText().toString();
        String empcontacto = ((EditText) findViewById(R.id.admContactoEmp)).getText().toString();


        try {
            String URL = Util.URL_SRV + "registro/administrador";
            ClienteRest clienteRest = new ClienteRest(this);
            Empresa e = new Empresa();
            e.setNombre(empnombre);
            e.setDescripcion(empdescripccion);
            e.setRuc(empruc);
            e.setDireccion(empdireccion);
            e.setContacto(empcontacto);
            Administrador a = new Administrador();
            a.setNombres(nombres);
            a.setApellidos(apellidos);
            a.setNombreusuario(nombreusr);
            a.setTelefono(telefono);
            a.setEmail(email);
            a.setContrasenia(pasword);
            a.setGenero(genero);
            a.addEmpresa(e);
           // a.setFechanacimiento(new Date());
            clienteRest.doPost2(URL, a, "?fecha="+fechaNac, SOLICITUD_GUARDAR_ADMINISTRADOR, true);
        }catch(Exception e){
            Util.showMensaje(this, R.string.msj_error_clienrest);
            e.printStackTrace();
        }
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
            case SOLICITUD_GUARDAR_ADMINISTRADOR:
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

}
