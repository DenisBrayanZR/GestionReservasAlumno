package es.iesjuanbosco.reservasaulas.controller;

import es.iesjuanbosco.reservasaulas.exception.RecursoNoEncontradoException;
import es.iesjuanbosco.reservasaulas.model.Aula;
import es.iesjuanbosco.reservasaulas.repository.AulaRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/aulas")
@CrossOrigin
@RequiredArgsConstructor
public class AulaController {
    private final AulaRepository repository;

    @GetMapping
    public List<Aula> listar(@RequestParam(required = false) Boolean ordenadores,
                             @RequestParam(required = false) Integer capacidad) {
        if (ordenadores != null) return repository.findByOrdenadores(ordenadores);
        if (capacidad != null) return repository.findByCapacidadGreaterThan(capacidad);
        return repository.findAll();
    }
    @GetMapping("/{id}")
    public Aula obtener(@PathVariable Long id) { return repository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Aula", id)); }
    @PostMapping
    public ResponseEntity<Aula> crear(@Valid @RequestBody Aula aula) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(aula));
    }
    @PutMapping("/{id}")
    public Aula actualizar(@PathVariable Long id, @Valid @RequestBody Aula datos) {
        Aula aula = obtener(id); datos.setId(aula.getId()); return repository.save(datos);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        obtener(id); repository.deleteById(id); return ResponseEntity.noContent().build();
    }
}
