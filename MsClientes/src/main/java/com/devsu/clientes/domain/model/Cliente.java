package com.devsu.clientes.domain.model;

public class Cliente extends Auditable {
    private Long id;
    private Persona persona;
    private String clienteId;
    private String contrasena;
    private Boolean estado;

    public Cliente() {
    }

    public Cliente(Long id, Persona persona, String clienteId, String contrasena, Boolean estado) {
        this.id = id;
        this.persona = persona;
        this.clienteId = clienteId;
        this.contrasena = contrasena;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}