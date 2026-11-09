package es.iesjuanbosco.reservasaulas.repository;

import es.iesjuanbosco.reservasaulas.model.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AulaRepository extends JpaRepository<Aula, Long> {
    List<Aula> findByOrdenadores(boolean ordenadores);
    List<Aula> findByCapacidadGreaterThan(Integer capacidad);
}
