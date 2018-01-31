package com.uiresource.cookit.modelo;

import com.uiresource.cookit.recycler.Vestimenta;

/**
 * Esta clase es la entidad Votos que contiene los getters y setters.
 * @author root
 */

public class Comentario {

	private int id;

	private String comentarios;

	private Usuario usuario;

	private Vestimenta vestimenta;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComentarios() {
		return comentarios;
	}

	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Vestimenta getVestimenta() {
		return vestimenta;
	}

	public void setVestimenta(Vestimenta vestimenta) {
		this.vestimenta = vestimenta;
	}
}