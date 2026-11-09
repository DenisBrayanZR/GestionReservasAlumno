package es.iesjuanbosco.reservasaulas.repository;

import es.iesjuanbosco.reservasaulas.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    boolean existsByAulaIdAndFechaReservaAndFranjaHorariaId(
            Long aulaId, LocalDate fechaReserva, Long franjaHorariaId);
    boolean existsByAulaIdAndFechaReservaAndFranjaHorariaIdAndIdNot(
            Long aulaId, LocalDate fechaReserva, Long franjaHorariaId, Long id);
}
