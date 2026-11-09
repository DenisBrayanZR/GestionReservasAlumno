package es.iesjuanbosco.reservasaulas.repository;

import es.iesjuanbosco.reservasaulas.model.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfesorRepository extends JpaRepository<Profesor, Long> {}
