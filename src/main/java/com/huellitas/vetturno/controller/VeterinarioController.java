package com.huellitas.vetturno.controller;

import com.huellitas.vetturno.dto.VeterinarioDTO;
import com.huellitas.vetturno.dto.VeterinarioRequest;
import com.huellitas.vetturno.service.VeterinarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/veterinarios")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    public VeterinarioController(VeterinarioService veterinarioService) {
        this.veterinarioService = veterinarioService;
    }

    @PostMapping
    public ResponseEntity<VeterinarioDTO> crear(@Valid @RequestBody VeterinarioRequest request) {
        VeterinarioDTO creado = veterinarioService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<VeterinarioDTO>> listar() {
        return ResponseEntity.ok(veterinarioService.listar());
    }
}
