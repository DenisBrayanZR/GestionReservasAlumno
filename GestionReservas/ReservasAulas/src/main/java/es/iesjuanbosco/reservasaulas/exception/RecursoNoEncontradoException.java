package es.iesjuanbosco.reservasaulas.exception;

public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String recurso, Long id) {
        super(recurso + " con id " + id + " no encontrado");
    }
}
