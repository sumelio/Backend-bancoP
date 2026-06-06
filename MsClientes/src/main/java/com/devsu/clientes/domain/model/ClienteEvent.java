package com.devsu.clientes.domain.model;

import java.time.ZonedDateTime;

public class ClienteEvent {
    private String clienteId;
    private String nombre;
    private Boolean estado;
    private String eventType; // CREATED, UPDATED, DELETED
    private ZonedDateTime timestamp;

    public ClienteEvent() {
    }

    public ClienteEvent(String clienteId, String nombre, Boolean estado, String eventType, ZonedDateTime timestamp) {
        this.clienteId = clienteId;
        this.nombre = nombre;
        this.estado = estado;
        this.eventType = eventType;
        this.timestamp = timestamp;
    }

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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }
}