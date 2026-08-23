package com.homeservices.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class HorarioDisponible {

    private final LocalDate fecha;
    private final LocalTime hora;
    private final String valor;
    private final String etiqueta;

    public HorarioDisponible(LocalDate fecha, LocalTime hora, String valor, String etiqueta) {
        this.fecha = fecha;
        this.hora = hora;
        this.valor = valor;
        this.etiqueta = etiqueta;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public String getValor() {
        return valor;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
