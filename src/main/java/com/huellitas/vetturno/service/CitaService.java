package com.huellitas.vetturno.service;

import com.huellitas.vetturno.dto.CitaDTO;
import com.huellitas.vetturno.dto.CitaRequest;
import com.huellitas.vetturno.exception.RecursoNoEncontradoException;
import com.huellitas.vetturno.exception.ReglaNegocioException;
import com.huellitas.vetturno.model.Cita;
import com.huellitas.vetturno.model.Mascota;
import com.huellitas.vetturno.model.Veterinario;
import com.huellitas.vetturno.repository.CitaRepository;
import com.huellitas.vetturno.repository.MascotaRepository;
import com.huellitas.vetturno.repository.VeterinarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final MascotaRepository mascotaRepository;
    private final VeterinarioRepository veterinarioRepository;

    public CitaService(CitaRepository citaRepository,
                       MascotaRepository mascotaRepository,
                       VeterinarioRepository veterinarioRepository) {
        this.citaRepository = citaRepository;
        this.mascotaRepository = mascotaRepository;
        this.veterinarioRepository = veterinarioRepository;
    }

    @Transactional
    public CitaDTO crear(CitaRequest request) {
        if (request.getFechaHora() == null || !request.getFechaHora().isAfter(LocalDateTime.now())) {
            throw new ReglaNegocioException("La fecha y hora de la cita debe ser una fecha futura.");
        }

        Mascota mascota = mascotaRepository.findById(request.getMascotaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Mascota no encontrada con id: " + request.getMascotaId()));

        Veterinario veterinario = veterinarioRepository.findById(request.getVeterinarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Veterinario no encontrado con id: " + request.getVeterinarioId()));

        if (citaRepository.existsByVeterinarioIdAndFechaHora(veterinario.getId(), request.getFechaHora())) {
            throw new ReglaNegocioException("El veterinario " + veterinario.getNombre() + " ya tiene una cita programada para la fecha y hora seleccionada.");
        }

        Cita cita = new Cita();
        cita.setFechaHora(request.getFechaHora());
        cita.setMotivo(request.getMotivo());
        cita.setMascota(mascota);
        cita.setVeterinario(veterinario);

        Cita guardada = citaRepository.save(cita);
        return mapToDTO(guardada);
    }

    @Transactional(readOnly = true)
    public List<CitaDTO> listar() {
        return citaRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CitaDTO> listarPorVeterinario(Long veterinarioId) {
        if (!veterinarioRepository.existsById(veterinarioId)) {
            throw new RecursoNoEncontradoException("Veterinario no encontrado con id: " + veterinarioId);
        }

        return citaRepository.findByVeterinarioId(veterinarioId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private CitaDTO mapToDTO(Cita cita) {
        String propietarioNombre = null;
        if (cita.getMascota() != null && cita.getMascota().getPropietario() != null) {
            propietarioNombre = cita.getMascota().getPropietario().getNombre();
        }

        return new CitaDTO(
                cita.getId(),
                cita.getFechaHora(),
                cita.getMotivo(),
                cita.getMascota() != null ? cita.getMascota().getId() : null,
                cita.getMascota() != null ? cita.getMascota().getNombre() : null,
                propietarioNombre,
                cita.getVeterinario() != null ? cita.getVeterinario().getId() : null,
                cita.getVeterinario() != null ? cita.getVeterinario().getNombre() : null
        );
    }
}
