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

import com.uiresource.cookit.modelo.Marca;
import com.uiresource.cookit.modelo.Respuesta;
import com.uiresource.cookit.recycler.Vestimenta;
import com.uiresource.cookit.utilidades.ClienteRest;
import com.uiresource.cookit.utilidades.OnTaskCompleted;
import com.uiresource.cookit.utilidades.Util;

import java.util.List;

public class NuevaVestimentaFragment extends Fragment implements OnTaskCompleted, View.OnClickListener {

    private static final int INGRESAR_VESTIMENTA = 1;
    private static final int SOLICITUD_MARCAS = 2;
    private Context globalContext = null;
    private Spinner color;
    private Spinner estilo;
    private Spinner genero;
    private Spinner tipo;
    private Spinner talla;
    private Spinner marca;
    private List<Marca> marcas;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getApplicationContext();
        globalContext=this.getActivity();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nueva_vestimenta, container, false);
    }
    @Override
    public void onResume() {
        super.onResume();
        ((Button) this.getActivity().findViewById(R.id.ves_reg_vestimenta)).setOnClickListener(this);
        color = (Spinner) this.getActivity().findViewById(R.id.ves_color);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this.getActivity(), R.array.Color, R.layout.support_simple_spinner_dropdown_item);
        color.setAdapter(adapter1);

        estilo = (Spinner) this.getActivity().findViewById(R.id.ves_estilo);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getActivity(), R.array.Estilo, R.layout.support_simple_spinner_dropdown_item);
        estilo.setAdapter(adapter2);

        genero = (Spinner) this.getActivity().findViewById(R.id.ves_genero);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this.getActivity(), R.array.GeneroV, R.layout.support_simple_spinner_dropdown_item);
        genero.setAdapter(adapter3);

        tipo = (Spinner) this.getActivity().findViewById(R.id.ves_tipo);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this.getActivity(), R.array.Tipo, R.layout.support_simple_spinner_dropdown_item);
        tipo.setAdapter(adapter4);

        talla = (Spinner) this.getActivity().findViewById(R.id.ves_talla);
        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(this.getActivity(), R.array.Talla, R.layout.support_simple_spinner_dropdown_item);
        talla.setAdapter(adapter5);

       // marca = (Spinner) this.getActivity().findViewById(R.id.ves_marca);
       // ArrayAdapter<CharSequence> adapter6 = ArrayAdapter.createFromResource(this.getActivity(), R.array.generos, R.layout.support_simple_spinner_dropdown_item);
       // marca.setAdapter(adapter6);

        consultaListadoMarcas();
        //((Button) this.getActivity().findViewById(R.id.NEbtn_reg)).setOnClickListener(this);

    }

    @Override
    public void onTaskCompleted(int idSolicitud, String result) {
        switch (idSolicitud){
            case INGRESAR_VESTIMENTA:
                if(result!=null){
                    try {
                        Respuesta res = ClienteRest.getResult(result, Respuesta.class);
                        Util.showMensaje(globalContext, res.getMensaje());
                        if (res.getCodigo() == 1) {
                            ((EditText) this.getActivity().findViewById(R.id.ves_descripcion)).setText("");
                            ((EditText) this.getActivity().findViewById(R.id.ves_precio)).setText("");
                            ((EditText) this.getActivity().findViewById(R.id.ves_imagen)).setText("");
                            onResume();
                            //this.getActivity().finish();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Util.showMensaje(globalContext,R.string.msj_error_clienrest_formato);
                    }
                }else
                    Util.showMensaje(globalContext,R.string.msj_error_clienrest);
                break;
                case SOLICITUD_MARCAS:
                if(result!=null){
                    try {
                        List<Marca> res =  ClienteRest.getResults(result, Marca.class);
                        //Util.showMensaje(globalContext, res.get(0).getNombre()+"<<<<<");
                        marcas=res;
                        marca = (Spinner) this.getActivity().findViewById(R.id.ves_marca);
                        marca.setAdapter(new ArrayAdapter<Marca>(globalContext, android.R.layout.simple_spinner_item, marcas));
                    }catch (Exception e){
                        Log.i("MainActivity", "Error en carga de categorias", e);
                        Util.showMensaje(globalContext, R.string.msj_error_clienrest_formato);
                    }
                }else
                    Util.showMensaje(globalContext, R.string.msj_error_clienrest);
                break;
            default:
                break;
        }

    }
    private void confirmarGuardarVes() {
        String pregunta = "Esto seguro de realizar el registro?";
        new AlertDialog.Builder(globalContext)
                .setTitle(getResources().getString(R.string.msj_confirmacion))
                .setMessage(pregunta)
                .setNegativeButton(android.R.string.cancel, null)//sin listener
                .setPositiveButton(getResources().getString(R.string.lbl_aceptar),
                        new DialogInterface.OnClickListener() {//un listener que al pulsar, solicite el WS de Transsaccion
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                guardarVes();
                            }
                        })
                .show();
    }
    private void guardarVes(){
        String descripcion = ((EditText) this.getActivity().findViewById(R.id.ves_descripcion)).getText().toString();
        String col = color.getSelectedItem().toString();
        String est = estilo.getSelectedItem().toString();
        String gen = genero.getSelectedItem().toString();
        String tip = tipo.getSelectedItem().toString();
        String tal =talla.getSelectedItem().toString();
        String precio1 = ((EditText) this.getActivity().findViewById(R.id.ves_precio)).getText().toString();
        Double precio = Double.parseDouble(precio1);
        String ima= ((EditText) this.getActivity().findViewById(R.id.ves_imagen)).getText().toString();
        Marca mar = (Marca) marca.getSelectedItem();

        try {
            String URL = Util.URL_SRV + "vestimenta/newvestimenta";
            ClienteRest clienteRest = new ClienteRest(this);
            Vestimenta vestimenta = new Vestimenta();
            vestimenta.setDescripcion(descripcion);
            vestimenta.setColor(col);
            vestimenta.setEstilo(est);
            vestimenta.setGenero(gen);
            vestimenta.setTipo(tip);
            vestimenta.setTalla(tal);
            vestimenta.setPrecio(precio);
            vestimenta.setImagen(ima);
            vestimenta.setMarca(mar);

            SharedPreferences miPreferencia = this.getActivity().getSharedPreferences("MisPreferencias", globalContext.MODE_PRIVATE);
            String idEmpresa = miPreferencia.getString("idEmpresa","No de pudo obtener");
            clienteRest.doPost2(URL, vestimenta,"?id="+idEmpresa , INGRESAR_VESTIMENTA, true);
        }catch(Exception e){
            Util.showMensaje(globalContext, R.string.msj_error_clienrest);
            e.printStackTrace();
        }
    }

    protected void consultaListadoMarcas() {
        try {
            String URL = Util.URL_SRV + "marca/getmarcas";
            ClienteRest clienteRest = new ClienteRest(this);
            clienteRest.doGet(URL, "", SOLICITUD_MARCAS, true);
        }catch(Exception e){
            Util.showMensaje(globalContext, R.string.msj_error_clienrest);
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ves_reg_vestimenta:
                confirmarGuardarVes();
                break;
            default:
                break;
        }
    }
}
