package es.iesjuanbosco.reservasaulas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class Profesor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String nombre;
    @NotBlank @Email
    private String email;
}
