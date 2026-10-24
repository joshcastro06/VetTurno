package com.huellitas.vetturno.service;

import com.huellitas.vetturno.dto.VeterinarioDTO;
import com.huellitas.vetturno.dto.VeterinarioRequest;
import com.huellitas.vetturno.model.Veterinario;
import com.huellitas.vetturno.repository.VeterinarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;

    public VeterinarioService(VeterinarioRepository veterinarioRepository) {
        this.veterinarioRepository = veterinarioRepository;
    }

    @Transactional
    public VeterinarioDTO crear(VeterinarioRequest request) {
        Veterinario veterinario = new Veterinario();
        veterinario.setNombre(request.getNombre());
        veterinario.setEspecialidad(request.getEspecialidad());

        Veterinario guardado = veterinarioRepository.save(veterinario);
        return mapToDTO(guardado);
    }

    @Transactional(readOnly = true)
    public List<VeterinarioDTO> listar() {
        return veterinarioRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private VeterinarioDTO mapToDTO(Veterinario veterinario) {
        return new VeterinarioDTO(
                veterinario.getId(),
                veterinario.getNombre(),
                veterinario.getEspecialidad()
        );
    }
}
