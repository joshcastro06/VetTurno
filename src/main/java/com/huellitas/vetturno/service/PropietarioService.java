package com.huellitas.vetturno.service;

import com.huellitas.vetturno.dto.PropietarioDTO;
import com.huellitas.vetturno.dto.PropietarioRequest;
import com.huellitas.vetturno.model.Propietario;
import com.huellitas.vetturno.repository.PropietarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropietarioService {

    private final PropietarioRepository propietarioRepository;

    public PropietarioService(PropietarioRepository propietarioRepository) {
        this.propietarioRepository = propietarioRepository;
    }

    @Transactional
    public PropietarioDTO crear(PropietarioRequest request) {
        Propietario propietario = new Propietario();
        propietario.setNombre(request.getNombre());
        propietario.setTelefono(request.getTelefono());
        propietario.setEmail(request.getEmail());

        Propietario guardado = propietarioRepository.save(propietario);
        return mapToDTO(guardado);
    }

    @Transactional(readOnly = true)
    public List<PropietarioDTO> listar() {
        return propietarioRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private PropietarioDTO mapToDTO(Propietario propietario) {
        return new PropietarioDTO(
                propietario.getId(),
                propietario.getNombre(),
                propietario.getTelefono(),
                propietario.getEmail()
        );
    }
}
