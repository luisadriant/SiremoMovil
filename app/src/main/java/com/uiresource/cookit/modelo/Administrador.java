package com.uiresource.cookit.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luisl on 1/9/2018.
 */

public class Administrador extends Persona{

    private List<Empresa> empresas;

    public List<Empresa> getEmpresas() {
        return empresas;
    }

    public void setEmpresas(List<Empresa> empresas) {
        this.empresas = empresas;
    }

    public void addEmpresa(Empresa e) {
        if (empresas==null)
            empresas=new ArrayList<Empresa>();
        empresas.add(e);
    }
}
