package es.iesjuanbosco.reservasaulas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalTime;

@Entity
@Getter @Setter @NoArgsConstructor
public class FranjaHoraria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private LocalTime horaInicio;
    @NotNull
    private LocalTime horaFin;
}
