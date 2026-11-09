package es.iesjuanbosco.reservasaulas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor
public class Reserva {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull @FutureOrPresent
    private LocalDate fechaReserva;
    @Column(nullable = false, updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime fechaHoraRealizacion;
    @ManyToOne(optional = false)
    @NotNull
    private Aula aula;
    @ManyToOne(optional = false)
    @NotNull
    private Profesor profesor;
    @ManyToOne(optional = false)
    @NotNull
    private FranjaHoraria franjaHoraria;

    @PrePersist
    void completarFechaHoraRealizacion() {
        if (fechaHoraRealizacion == null) {
            fechaHoraRealizacion = LocalDateTime.now();
        }
    }
}
