package com.devsu.cuentas.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente_local")
public class ClienteLocal extends Auditable {

    @Id
    @Column(name = "cliente_id", length = 50)
    private String clienteId;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Boolean estado = true;

    public ClienteLocal() {
    }

    public ClienteLocal(String clienteId, String nombre, Boolean estado) {
        this.clienteId = clienteId;
        this.nombre = nombre;
        this.estado = estado;
    }

    // Getters and Setters

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}