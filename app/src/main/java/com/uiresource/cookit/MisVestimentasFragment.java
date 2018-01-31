package com.uiresource.cookit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.uiresource.cookit.modelo.Empresa;
import com.uiresource.cookit.recycler.Vestimenta;
import com.uiresource.cookit.utilidades.ClienteRest;
import com.uiresource.cookit.utilidades.OnTaskCompleted;
import com.uiresource.cookit.utilidades.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luisl on 1/11/2018.
 */

public class MisVestimentasFragment extends Fragment implements OnTaskCompleted {
    private static final int SOLICITUD_VESTIMENTAS = 1;
    private static final int SOLICITUD_EMPRESA = 2;
    private ListAdapterVestimentas listAdapterVest;
    private Context globalContext = null;



    @Override
    public void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        getActivity().getApplicationContext();
        globalContext=this.getActivity();


    }



    @Override
    public void onResume() {
        super.onResume();
        consultaListaVestimentas();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * Realiza la llamada al WS para consultar el listado de Categorias
     */
    protected void consultaListaVestimentas() {
        try {
            //  http://localhost:8080/miSiremo/srv/login/administrador?usuario=Admin&pass=1234
            //http://localhost:8080/miSiremo/srv/empresa/listaempresas
            String URL = Util.URL_SRV + "vestimenta/listavestimentas";
            ClienteRest clienteRest = new ClienteRest(this);
            SharedPreferences miPreferencia = this.getActivity().getSharedPreferences("MisPreferencias", globalContext.MODE_PRIVATE);
            String idEmpresa = miPreferencia.getString("idEmpresa","No de pudo obtener");
            clienteRest.doGet(URL, "?id="+idEmpresa, SOLICITUD_VESTIMENTAS, true);
        }catch(Exception e){
            Util.showMensaje(globalContext, R.string.msj_error_clienrest);
            e.printStackTrace();
        }
    }
    /**
     * Realiza la llamada al WS para consultar el listado de Categorias
     */
    protected void consultaEmpresa(int id) {
        try {
            String URL = Util.URL_SRV + "empresa/verempresa";
            ClienteRest clienteRest = new ClienteRest(this);
            clienteRest.doGet(URL, "?id="+id, SOLICITUD_EMPRESA, true);
        }catch(Exception e){
            Util.showMensaje(globalContext, R.string.msj_error_clienrest);
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_vestimentas, container, false);
    }

    @Override
    public void onTaskCompleted(int idSolicitud, String result) {
        Log.i("MainActivity", "" + result);
        switch (idSolicitud){
            case SOLICITUD_VESTIMENTAS:
                if(result!=null){
                    try {
                        //List<Empresa> res = ClienteRest.getResults(result, Empresa.class);
                        List<Vestimenta> ves = ClienteRest.getResults(result, Vestimenta.class);
                       // Util.showMensaje(globalContext, ves.get(0).getMarca().getNombre());
                        mostrarCategorias(ves);
                    }catch (Exception e){
                        Log.i("MainActivity", "Error en carga de categorias", e);
                        Util.showMensaje(globalContext, R.string.msj_error_clienrest_formato);
                    }
                }else
                    Util.showMensaje(globalContext, R.string.msj_error_clienrest);
                break;
            case SOLICITUD_EMPRESA:
                if(result!=null){
                    try {
                        Empresa res =  ClienteRest.getResult(result, Empresa.class);
                        Util.showMensaje(globalContext, res.toString());
                    }catch (Exception e){
                        Log.i("MainActivity", "Error en carga de categoria por ID", e);
                        Util.showMensaje(globalContext, R.string.msj_error_clienrest_formato);
                    }
                }else
                    Util.showMensaje(globalContext, R.string.msj_error_clienrest);
                break;
            default:
                break;
        }

    }
    public void mostrarCategorias(List<Vestimenta> list){
        ListView lista = (ListView) getView().findViewById(R.id.list_vetimenta);
        listAdapterVest = new ListAdapterVestimentas(globalContext, new ArrayList<Vestimenta>(list));
        lista.setAdapter(listAdapterVest);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                mostrarEmpresa(position);
            }
        });
    }

    private void mostrarEmpresa(int position){
        Vestimenta emp = listAdapterVest.getItem(position);
        consultaEmpresa(emp.getId());
    }

    private void llamarCrearCategoria(){
        // Intent intent = new Intent(this, CategoriaActivity.class);
        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //cierra cola de actividades
        // startActivity(intent);
    }
}
