package es.iesjuanbosco.reservasaulas.service;

import es.iesjuanbosco.reservasaulas.exception.*;
import es.iesjuanbosco.reservasaulas.model.Reserva;
import es.iesjuanbosco.reservasaulas.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final AulaRepository aulaRepository;
    private final ProfesorRepository profesorRepository;
    private final FranjaHorariaRepository franjaRepository;

    public List<Reserva> listar() { return reservaRepository.findAll(); }
    public Reserva obtener(Long id) { return reservaRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Reserva", id)); }

    @Transactional
    public Reserva crear(Reserva reserva) {
        validar(reserva, null);
        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva actualizar(Long id, Reserva datos) {
        Reserva existente = obtener(id);
        validar(datos, id);
        datos.setId(existente.getId());
        datos.setFechaHoraRealizacion(existente.getFechaHoraRealizacion());
        return reservaRepository.save(datos);
    }

    public void borrar(Long id) {
        if (!reservaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Reserva", id);
        }
        reservaRepository.deleteById(id);
    }

    private void validar(Reserva reserva, Long id) {
        if (reserva.getFechaReserva() == null || reserva.getFechaReserva().isBefore(LocalDate.now())) {
            throw new ReservaNoValidaException("La fecha de la reserva no puede ser anterior a la fecha actual");
        }
        if (reserva.getAula() == null || reserva.getAula().getId() == null
                || !aulaRepository.existsById(reserva.getAula().getId())) {
            throw new RecursoNoEncontradoException("Aula",
                    reserva.getAula() == null ? null : reserva.getAula().getId());
        }
        if (reserva.getProfesor() == null || reserva.getProfesor().getId() == null
                || !profesorRepository.existsById(reserva.getProfesor().getId())) {
            throw new RecursoNoEncontradoException("Profesor",
                    reserva.getProfesor() == null ? null : reserva.getProfesor().getId());
        }
        if (reserva.getFranjaHoraria() == null || reserva.getFranjaHoraria().getId() == null
                || !franjaRepository.existsById(reserva.getFranjaHoraria().getId())) {
            throw new RecursoNoEncontradoException("Franja horaria",
                    reserva.getFranjaHoraria() == null ? null : reserva.getFranjaHoraria().getId());
        }
        boolean ocupada = id == null
                ? reservaRepository.existsByAulaIdAndFechaReservaAndFranjaHorariaId(
                    reserva.getAula().getId(), reserva.getFechaReserva(), reserva.getFranjaHoraria().getId())
                : reservaRepository.existsByAulaIdAndFechaReservaAndFranjaHorariaIdAndIdNot(
                    reserva.getAula().getId(), reserva.getFechaReserva(), reserva.getFranjaHoraria().getId(), id);
        if (ocupada) {
            throw new ReservaNoValidaException("La franja horaria ya está ocupada para esa aula y fecha");
        }
    }
}
