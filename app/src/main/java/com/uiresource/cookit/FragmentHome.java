package com.uiresource.cookit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uiresource.cookit.modelo.Empresa;
import com.uiresource.cookit.recycler.VestiAdapter;
import com.uiresource.cookit.recycler.RecyclerTouchListener;
import com.uiresource.cookit.recycler.Vestimenta;
import com.uiresource.cookit.utilidades.ClienteRest;
import com.uiresource.cookit.utilidades.OnTaskCompleted;
import com.uiresource.cookit.utilidades.Util;

import java.util.ArrayList;
import java.util.List;



public class FragmentHome extends Fragment implements OnTaskCompleted{
    //private List<Vestimenta> itemList = new ArrayList<>();
    private RecyclerView recyclerView;
    private VestiAdapter miAdapter;
    private AppCompatActivity appCompatActivity;
    private static final int SOLICITUD_VESTIMENTAS = 20;
    private Context globalContext = null;
    private static final int SOLICITUD_EMPRESAS = 2;

    public FragmentHome(){
        setHasOptionsMenu(true);
    }
    public void onCreate(Bundle a){
        super.onCreate(a);
        setHasOptionsMenu(true);
        globalContext=this.getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        consultaVestimentas();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null, false);

        ((Main)getActivity()).setupToolbar(R.id.toolbar, "RECOMENDACION", R.color.colorPink, R.color.colorWhiteTrans, R.drawable.ic_burger);

        consultaVestimentas();

        try {

            Thread.sleep(2000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        return view;
    }

    public void cargarDatosRecomen(final List<Vestimenta> listaVestimentas){

        miAdapter = new VestiAdapter(listaVestimentas, getActivity());
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(miAdapter);

        appCompatActivity = (AppCompatActivity) getActivity();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {


                Intent descVesti = new Intent();
                descVesti.setClass(getContext(), Detail.class);
                descVesti.putExtra("id", ""+listaVestimentas.get(position).getId());
                descVesti.putExtra("descripcion", listaVestimentas.get(position).getDescripcion());
                descVesti.putExtra("likes",  ""+listaVestimentas.get(position).getLikes());
                descVesti.putExtra("rating", listaVestimentas.get(position).getRaiting() );
                descVesti.putExtra("talla",  listaVestimentas.get(position).getTalla());
                descVesti.putExtra("color",  listaVestimentas.get(position).getColor());
                descVesti.putExtra("estilo",  listaVestimentas.get(position).getEstilo());
                descVesti.putExtra("precio",  ""+listaVestimentas.get(position).getPrecio());
                descVesti.putExtra("imagen",  Util.URL_IMG+listaVestimentas.get(position).getImagen());

                descVesti.putExtra("marca", listaVestimentas.get(position).getMarca().getNombre());
                descVesti.putExtra("empresa", listaVestimentas.get(position).getEmpresa().getNombre());
                descVesti.putExtra("direccion", listaVestimentas.get(position).getEmpresa().getDireccion());
                descVesti.putExtra("contacto", listaVestimentas.get(position).getEmpresa().getContacto());

                startActivity(descVesti);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public void onTaskCompleted(int idSolicitud, String result) {
        switch (idSolicitud){
            case SOLICITUD_VESTIMENTAS:
                if(result!=null){
                    try {
                        List<Vestimenta> res = ClienteRest.getResults(result, Vestimenta.class);

                        cargarDatosRecomen(res);

                    }catch (Exception e){
                        Log.w("ERRRROOOOOOOORR", "Error en carga de vetimentas", e);
                        Util.showMensaje(getContext(), R.string.msj_error_clienrest_formato);
                    }
                }else
                    Util.showMensaje(getContext(), R.string.msj_error_clienrest);
                break;

            default:
                break;
        }
    }

    protected void consultaVestimentas() {
        try {
            String URL = Util.URL_SRV + "principal/recomendacion";
            ClienteRest clienteRest = new ClienteRest(this);
            SharedPreferences miPreferencia = this.getActivity().getSharedPreferences("MisPreferencias", globalContext.MODE_PRIVATE);
            String idUsuario = miPreferencia.getString("idUsuario","No de pudo obtener");
            clienteRest.doGet(URL, "?idUsuario="+idUsuario, SOLICITUD_VESTIMENTAS, true);
        }catch(Exception e){
            Util.showMensaje(globalContext,  R.string.msj_error_clienrest);
            e.printStackTrace();
        }
    }
}

