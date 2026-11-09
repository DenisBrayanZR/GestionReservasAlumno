package es.iesjuanbosco.reservasaulas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class Aula {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String nombre;
    @Min(1)
    private Integer capacidad;
    private boolean ordenadores;
}
