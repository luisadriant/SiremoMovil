package com.uiresource.cookit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.uiresource.cookit.modelo.Empresa;
import com.uiresource.cookit.recycler.Vestimenta;
import com.uiresource.cookit.utilidades.ClienteRest;
import com.uiresource.cookit.utilidades.OnTaskCompleted;
import com.uiresource.cookit.utilidades.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class BuscadorActivity extends BaseActivity implements OnTaskCompleted {
    private static final int SOLICITUD_VESTIMENTAS = 1;
    private  ListAdapterVestimentasBus listAdapterVestimentasBus;
    private ListView lista;
    @Override
    public void onResume() {
        super.onResume();
        consultaListaVestimentas();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscador);
        setupToolbar(R.id.toolbarBuscar, "BUSCAR", R.color.colorPink, R.color.colorWhiteTrans, R.drawable.ic_arrow_back);
       // EditText inputSearch = (EditText) findViewById(R.id.bus_view);
       EditText searchView = (EditText) findViewById(R.id.bus_view);
        /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listAdapterVestimentasBus.getFilter().filter(newText);
                return false;
            }
        });*/

        searchView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                Toast.makeText(getApplicationContext(), ""+cs, Toast.LENGTH_SHORT).show();
                BuscadorActivity.this.listAdapterVestimentasBus.getFilter().filter(cs.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override
            public void afterTextChanged(Editable arg0) {}
        });
    }

    protected void consultaListaVestimentas() {
        try {
            //  http://localhost:8080/miSiremo/srv/login/administrador?usuario=Admin&pass=1234
            //http://localhost:8080/miSiremo/srv/empresa/listaempresas
            String URL = Util.URL_SRV + "principal/buscador";
            ClienteRest clienteRest = new ClienteRest(this);
            clienteRest.doGet(URL, "", SOLICITUD_VESTIMENTAS, true);
        }catch(Exception e){
            Util.showMensaje(this, R.string.msj_error_clienrest);
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskCompleted(int idSolicitud, String result) {
        Log.i("MainActivity", "" + result);
        switch (idSolicitud){
            case SOLICITUD_VESTIMENTAS:
                if(result!=null){
                    try {
                        List<Vestimenta> ves = ClienteRest.getResults(result, Vestimenta.class);
                        // Util.showMensaje(globalContext, ves.get(0).getMarca().getNombre());
                        mostrarCategorias(ves);
                    }catch (Exception e){
                        Log.i("MainActivity", "Error en carga de categorias", e);
                        Util.showMensaje(this, R.string.msj_error_clienrest_formato);
                    }
                }else
                    Util.showMensaje(this, R.string.msj_error_clienrest);
                break;
            default:
                break;
        }
    }
    public void mostrarCategorias(List<Vestimenta> list){
        lista = (ListView) findViewById(R.id.list_bus);
        listAdapterVestimentasBus = new ListAdapterVestimentasBus(this, new ArrayList<Vestimenta>(list));
        lista.setAdapter(listAdapterVestimentasBus);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                //Toast.makeText(getApplicationContext(), "click sobre ", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "click sobre " + i, Toast.LENGTH_SHORT).show();
                Vestimenta vestimenta = (Vestimenta) listAdapterVestimentasBus.getItem(i);
                Intent descVesti = new Intent();
                descVesti.setClass(getApplicationContext(), Detail.class);
                descVesti.putExtra("id", ""+vestimenta.getId());
                descVesti.putExtra("descripcion", vestimenta.getDescripcion());
                descVesti.putExtra("likes", ""+vestimenta.getLikes());
                descVesti.putExtra("rating",vestimenta.getRaiting() );
                descVesti.putExtra("talla",  vestimenta.getTalla());
                descVesti.putExtra("color",  vestimenta.getColor());
                descVesti.putExtra("estilo",  vestimenta.getEstilo());
                descVesti.putExtra("precio",  ""+vestimenta.getPrecio());
                descVesti.putExtra("imagen",  Util.URL_IMG+vestimenta.getImagen());
                descVesti.putExtra("marca", vestimenta.getMarca().getNombre());
                descVesti.putExtra("empresa", vestimenta.getEmpresa().getNombre());
                descVesti.putExtra("direccion", vestimenta.getEmpresa().getDireccion());
                descVesti.putExtra("contacto", vestimenta.getEmpresa().getContacto());
                startActivity(descVesti);
            }
        });


        //  lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      //      @Override
      //      public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
      //         Toast.makeText(getApplicationContext(), ">>>>"+listAdapterVestimentasBus.getItem(position), Toast.LENGTH_SHORT).show();
      //      }
      //  });
    }

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
}
