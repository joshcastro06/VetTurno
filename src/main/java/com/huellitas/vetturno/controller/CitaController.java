package com.huellitas.vetturno.controller;

import com.huellitas.vetturno.dto.CitaDTO;
import com.huellitas.vetturno.dto.CitaRequest;
import com.huellitas.vetturno.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @PostMapping
    public ResponseEntity<CitaDTO> crear(@Valid @RequestBody CitaRequest request) {
        CitaDTO creada = citaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping
    public ResponseEntity<List<CitaDTO>> listar() {
        return ResponseEntity.ok(citaService.listar());
    }

    @GetMapping("/veterinario/{id}")
    public ResponseEntity<List<CitaDTO>> listarPorVeterinario(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.listarPorVeterinario(id));
    }
}
