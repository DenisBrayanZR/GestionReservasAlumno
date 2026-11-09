package es.iesjuanbosco.reservasaulas.controller;

import es.iesjuanbosco.reservasaulas.model.Reserva;
import es.iesjuanbosco.reservasaulas.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/reservas")
@CrossOrigin
@RequiredArgsConstructor
public class ReservaController {
    private final ReservaService service;
    @GetMapping public List<Reserva> listar() { return service.listar(); }
    @GetMapping("/{id}") public Reserva obtener(@PathVariable Long id) { return service.obtener(id); }
    @PostMapping public ResponseEntity<Reserva> crear(@Valid @RequestBody Reserva value) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(value)); }
    @PutMapping("/{id}") public Reserva actualizar(@PathVariable Long id, @Valid @RequestBody Reserva value) {
        return service.actualizar(id, value); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> borrar(@PathVariable Long id) {
        service.borrar(id); return ResponseEntity.noContent().build(); }
}
