package com.uiresource.cookit.utilidades;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by luisl on 1/9/2018.
 */

public class Util {
    //URL del Sitio Web primario de los WS para la aplicacion
    public static final String URL_SRV = "http://35.196.50.180:8081/miSiremo/srv/";
    public static final String URL_IMG = "http://35.196.50.180:8081/miSiremo/faces/Plantilla_Principal/";
    /**http://192.168.1.11:8080/miSiremo/srv/empresa/verempresa?id=1
     * Permite mostrar un mensaje Toast en pantalla,
     * @param id    ID de recurso String.xml
     */
    public static void showMensaje(Context context, int id){
        String mensaje = context.getResources().getString(id);
        Toast toast = Toast.makeText(context, mensaje, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Permite mostrar un mensaje Toast en pantalla,
     * @param mensaje    Texto del mensaje a mostrar
     */
    public static void showMensaje(Context context, String mensaje){
        Toast toast = Toast.makeText(context, mensaje, Toast.LENGTH_LONG);
        toast.show();
    }
}
