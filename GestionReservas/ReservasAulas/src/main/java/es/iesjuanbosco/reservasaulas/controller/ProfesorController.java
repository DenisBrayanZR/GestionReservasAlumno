package es.iesjuanbosco.reservasaulas.controller;

import es.iesjuanbosco.reservasaulas.exception.RecursoNoEncontradoException;
import es.iesjuanbosco.reservasaulas.model.Profesor;
import es.iesjuanbosco.reservasaulas.repository.ProfesorRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/profesores")
@CrossOrigin
@RequiredArgsConstructor
public class ProfesorController {
    private final ProfesorRepository repository;
    @GetMapping public List<Profesor> listar() { return repository.findAll(); }
    @GetMapping("/{id}") public Profesor obtener(@PathVariable Long id) { return repository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Profesor", id)); }
    @PostMapping public ResponseEntity<Profesor> crear(@Valid @RequestBody Profesor value) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(value)); }
    @PutMapping("/{id}") public Profesor actualizar(@PathVariable Long id, @Valid @RequestBody Profesor value) {
        Profesor actual = obtener(id); value.setId(actual.getId()); return repository.save(value); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> borrar(@PathVariable Long id) {
        obtener(id); repository.deleteById(id); return ResponseEntity.noContent().build(); }
}
