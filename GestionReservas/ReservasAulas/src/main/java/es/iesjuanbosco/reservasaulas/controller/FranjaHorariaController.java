package es.iesjuanbosco.reservasaulas.controller;

import es.iesjuanbosco.reservasaulas.exception.RecursoNoEncontradoException;
import es.iesjuanbosco.reservasaulas.model.FranjaHoraria;
import es.iesjuanbosco.reservasaulas.repository.FranjaHorariaRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping({"/franjas-horarias", "/franjas"})
@CrossOrigin
@RequiredArgsConstructor
public class FranjaHorariaController {
    private final FranjaHorariaRepository repository;
    @GetMapping public List<FranjaHoraria> listar() { return repository.findAll(); }
    @GetMapping("/{id}") public FranjaHoraria obtener(@PathVariable Long id) { return repository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Franja horaria", id)); }
    @PostMapping public ResponseEntity<FranjaHoraria> crear(@Valid @RequestBody FranjaHoraria value) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(value)); }
    @PutMapping("/{id}") public FranjaHoraria actualizar(@PathVariable Long id, @Valid @RequestBody FranjaHoraria value) {
        FranjaHoraria actual = obtener(id); value.setId(actual.getId()); return repository.save(value); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> borrar(@PathVariable Long id) {
        obtener(id); repository.deleteById(id); return ResponseEntity.noContent().build(); }
}
