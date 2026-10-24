package com.huellitas.vetturno.dto;

import java.time.LocalDateTime;

public class CitaRequest {

    private LocalDateTime fechaHora;
    private String motivo;
    private Long mascotaId;
    private Long veterinarioId;

    public CitaRequest() {
    }

    public CitaRequest(LocalDateTime fechaHora, String motivo, Long mascotaId, Long veterinarioId) {
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.mascotaId = mascotaId;
        this.veterinarioId = veterinarioId;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Long getMascotaId() {
        return mascotaId;
    }

    public void setMascotaId(Long mascotaId) {
        this.mascotaId = mascotaId;
    }

    public Long getVeterinarioId() {
        return veterinarioId;
    }

    public void setVeterinarioId(Long veterinarioId) {
        this.veterinarioId = veterinarioId;
    }
}
