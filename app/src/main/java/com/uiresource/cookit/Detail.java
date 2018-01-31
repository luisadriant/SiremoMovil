package com.uiresource.cookit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.uiresource.cookit.modelo.Administrador;
import com.uiresource.cookit.modelo.Comentario;
import com.uiresource.cookit.modelo.Empresa;
import com.uiresource.cookit.modelo.Respuesta;
import com.uiresource.cookit.recycler.ItemShopping;
import com.uiresource.cookit.recycler.DescripcionVestiAdapter;
import com.uiresource.cookit.recycler.RecyclerTouchListener;
import com.uiresource.cookit.utilidades.ClienteRest;
import com.uiresource.cookit.utilidades.OnTaskCompleted;
import com.uiresource.cookit.utilidades.Util;

import java.util.ArrayList;
import java.util.List;

public class Detail extends BaseActivity implements OnTaskCompleted {
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private TextView nomVesti, likesVesti, empresaVesti;
    private RecyclerView recyclerView;
    private DescripcionVestiAdapter miAdapter;
    private RatingBar ratingbar;
    private RecyclerView recyclerViewComments;


    private static final int SOLICITUD_VOTO = 10;
    private static final int SOLICITUD_TU_VOTO = 20;
    private static final int SOLICITUD_COMENTARIOS = 30;
    private static final int SOLICITUD_COMENTAR = 40;
    private CommentsAdapter mAdapterComments;
    private String idVestimenta;
    private String descripcion;
    private String likes;
    private String rating;
    private String talla;
    private String color;
    private String estilo;
    private String marca;
    private String precio;
    private String imagen;
    private String empresa;
    private String direccion;
    private String contacto;

    private String idUsuario;
    private EditText comentario;
    private ImageButton send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setupToolbar(R.id.toolbar, "Descripcion Vestimenta", R.color.colorPinkDark, android.R.color.transparent, R.drawable.ic_arrow_back);

        Bundle datosLLegan = this.getIntent().getExtras();
        idVestimenta = datosLLegan.getString("id");
        descripcion = datosLLegan.getString("descripcion");
        likes = datosLLegan.getString("likes");
        rating = datosLLegan.getString("rating");
        talla = datosLLegan.getString("talla");
        color = datosLLegan.getString("color");
        estilo = datosLLegan.getString("estilo");
        precio = datosLLegan.getString("precio");
        imagen = datosLLegan.getString("imagen");

        marca = datosLLegan.getString("marca");
        empresa = datosLLegan.getString("empresa");
        direccion = datosLLegan.getString("direccion");
        contacto = datosLLegan.getString("contacto");
//        Toast.makeText(getApplicationContext(),"ent="+ID,Toast.LENGTH_LONG).show();

        nomVesti = (TextView)findViewById(R.id.nomVestiDesc);
        likesVesti = (TextView)findViewById(R.id.likesVestiDesc);
        empresaVesti = (TextView)findViewById(R.id.empresaVestiDesc);
        comentario = (EditText) findViewById(R.id.txt_micoment);
        send =(ImageButton) findViewById(R.id.bt_send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmarEnviarComentario();
            }
        });

        nomVesti.setText(descripcion);
        likesVesti.setText(likes);
        empresaVesti.setText(empresa);

        SharedPreferences miPreferencia = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        idUsuario = miPreferencia.getString("idUsuario","no se pudo obtener el id o no existe");

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerShopping);

        recyclerViewComments = (RecyclerView) findViewById(R.id.recyclerComment);
        miAdapter = new DescripcionVestiAdapter(generateShopping(), this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(miAdapter);

        //RatingBar ratingBar =(RatingBar) findViewById(R.id.ratingBar_coment);
        //ratingBar.setNumStars(2);
      //  mAdapterComments = new CommentsAdapter(generateComments(), this);
      //  LinearLayoutManager mLayoutManagerComment = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
      //  recyclerViewComments.setLayoutManager(mLayoutManagerComment);
      //  recyclerViewComments.setItemAnimator(new DefaultItemAnimator());
      //  recyclerViewComments.setAdapter(mAdapterComments);


        final ImageView image = (ImageView) findViewById(R.id.image);
        Glide.with(this).load(Uri.parse(imagen)).into(image);

        ratingbar = (RatingBar) findViewById(R.id.ratingBarDesc);
        ratingbar.setStepSize(1);

        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float voto, boolean fromUser) {


                enviarVoto(Integer.parseInt(idUsuario), Integer.parseInt(idVestimenta), (int) voto);

                Util.showMensaje(getApplicationContext(),"Usted votò '"+(int)voto+"'");
            }
        });

        tuVotacion(Integer.parseInt(idUsuario),Integer.parseInt(idVestimenta));



    }
    @Override
    public void onResume() {
        super.onResume();
        consultaComentarios();
    }
    protected void consultaComentarios() {
        try {
            String URL = Util.URL_SRV + "comentario/getcomentarios";
            ClienteRest clienteRest = new ClienteRest(this);
            clienteRest.doGet(URL, "?idVestimenta="+idVestimenta, SOLICITUD_COMENTARIOS, true);
        }catch(Exception e){
            Util.showMensaje(this,  R.string.msj_error_clienrest);
            e.printStackTrace();
        }
    }
    public List<ItemShopping> generateShopping(){
        List<ItemShopping> itemList = new ArrayList<>();
        String pieces[] = {"Talla: ", "Color: ", "Estilo: ", "Marca: ", "Precio: "," Direccion: "," Contacto: "};
        String name[] = {talla, color, estilo, marca, precio, direccion, contacto};
        for (int i = 0; i<name.length; i++){
            ItemShopping item = new ItemShopping();
            item.setPieces(pieces[i]);
            item.setName(name[i]);
            itemList.add(item);
        }
        return itemList;
    }


    protected void tuVotacion(int idUsuario, int idVestimenta) {
        try {
            String URL = Util.URL_SRV + "descripcion/tuVotacion";
            ClienteRest clienteRest = new ClienteRest(this);
            clienteRest.doGet(URL, "?idUsuario="+idUsuario+"&idVestimenta="+idVestimenta, SOLICITUD_TU_VOTO, true);

        }catch(Exception e){
            Util.showMensaje(getApplicationContext(), R.string.msj_error_clienrest);
            e.printStackTrace();
        }
    }

    protected void enviarVoto(int idUsuario, int idVestimenta, int voto) {
        try {
            String URL = Util.URL_SRV + "descripcion/guardarVoto";
            ClienteRest clienteRest = new ClienteRest(this);
            clienteRest.doGet(URL, "?voto="+voto+"&idUsuario="+idUsuario+"&idVestimenta="+idVestimenta, SOLICITUD_VOTO, true);

        }catch(Exception e){
            Util.showMensaje(getApplicationContext(), R.string.msj_error_clienrest);
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskCompleted(int idSolicitud, String result) {

        Log.w("LLegada de mensaje", "Este es el mensje que llego a detalle="+result);
        switch (idSolicitud){
            case SOLICITUD_VOTO:
                if(result!=null){
                    try {
                       // List<Vestimenta> res = ClienteRest.getResults(result, Vestimenta.class);

                        //cargarDatosRecomen(res);

                    }catch (Exception e){
                        Log.w("ERRRROOOOOOOORR", "Error en carga de vetimentas", e);
                        Util.showMensaje(getApplicationContext(), R.string.msj_error_clienrest_formato);
                    }
                }

                break;
            case SOLICITUD_TU_VOTO:
                if(result!=null){
                    try {
                        ratingbar.setRating(Integer.parseInt(result));

                    }catch (Exception e){
                        Log.w("ERRRROOOOOOOORR", "Error en carga de tu voto", e);
                        Util.showMensaje(getApplicationContext(), R.string.msj_error_clienrest_formato);
                    }
                }
                break;
            case SOLICITUD_COMENTARIOS:
                if(result!=null){
                    try {
                        List<Comentario> com = ClienteRest.getResults(result, Comentario.class);
                        cargarDatosComentario(com);

                    }catch (Exception e){
                        Log.w("ERRRROOOOOOOORR", "Error en carga de comentarios", e);
                        Util.showMensaje(this, R.string.msj_error_clienrest_formato);
                    }
                }else
                    Util.showMensaje(this, R.string.msj_error_clienrest);
                break;
            case SOLICITUD_COMENTAR:
                if(result!=null){
                    try {
                        Respuesta res = ClienteRest.getResult(result, Respuesta.class);
                        Util.showMensaje(this, "echo");
                        comentario.setText("");
                        onResume();
                    }catch (Exception e){
                        Log.w("ERRRROOOOOOOORR", "Error en carga de comentarios", e);
                        Util.showMensaje(this, R.string.msj_error_clienrest_formato);
                    }
                }else
                    Util.showMensaje(this, R.string.msj_error_clienrest);
                break;
            default:
                break;
        }
    }
    public void cargarDatosComentario(final List<Comentario> listaComentarios){

        mAdapterComments = new CommentsAdapter(rating,listaComentarios, this);
        LinearLayoutManager mLayoutManagerComment = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewComments.setLayoutManager(mLayoutManagerComment);
        recyclerViewComments.setItemAnimator(new DefaultItemAnimator());
        recyclerViewComments.setAdapter(mAdapterComments);


        recyclerViewComments.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void ConfirmarEnviarComentario() {
        String pregunta = "Esto seguro de realizar el comentario";
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.msj_confirmacion))
                .setMessage(pregunta)
                .setNegativeButton(android.R.string.cancel, null)//sin listener
                .setPositiveButton(getResources().getString(R.string.lbl_aceptar),
                        new DialogInterface.OnClickListener() {//un listener que al pulsar, solicite el WS de Transsaccion
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                GuardarComentario();
                            }
                        })
                .show();
    }

    private void GuardarComentario(){

        try {
            Respuesta r=new Respuesta();
            r.setCodigo(2);
            r.setMensaje(comentario.getText().toString());
            String URL = Util.URL_SRV + "comentario/setcomentarios";
            ClienteRest clienteRest = new ClienteRest(this);
            SharedPreferences miPreferencia = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
            String idUsuario = miPreferencia.getString("idUsuario","No de pudo obtener");
            clienteRest.doPost2(URL, r, "?idVestimenta="+idVestimenta+"&idUsuario="+idUsuario, SOLICITUD_COMENTAR, true);
        }catch(Exception e){
            Util.showMensaje(this, R.string.msj_error_clienrest);
            e.printStackTrace();
        }
    }
}
