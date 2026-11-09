# Reservas de aulas

API REST para gestionar aulas, profesores, franjas horarias y reservas de un centro educativo. Está desarrollada con Spring Boot, Spring Data JPA, MySQL y Lombok. La carpeta `frontend` contiene una interfaz funcional sin frameworks.

## Puesta en marcha

Se necesita un JDK 17 o superior, Maven (o el wrapper incluido) y MySQL. Crear una base de datos llamada `reservas_aulas` o dejar que el conector la cree y arrancar:

```bash
./mvnw spring-boot:run
```

La conexión se puede cambiar con `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`. La API queda disponible en `http://localhost:8080`; el frontend puede abrirse con un servidor estático desde `frontend` (por ejemplo, `python3 -m http.server 5500`) y espera la API en ese puerto.

## Endpoints

Cada recurso dispone de `GET /recurso`, `GET /recurso/{id}`, `POST`, `PUT /{id}` y `DELETE /{id}`. Los recursos son `/aulas`, `/profesores`, `/franjas-horarias` y `/reservas`. Las aulas admiten `GET /aulas?ordenadores=true` y `GET /aulas?capacidad=20`.

Las reservas rechazan fechas anteriores al día actual y combinaciones de aula, fecha y franja ya ocupadas. Al crear una reserva se completa automáticamente `fechaHoraRealizacion`. Un conflicto devuelve `409`, una validación incorrecta `400` y un recurso inexistente `404`.

## Decisiones y problemas encontrados

El proyecto inicial solo contenía el esqueleto generado por Spring Initializr, así que se eligió un modelo relacional sencillo con relaciones `ManyToOne` desde `Reserva`. La regla de disponibilidad se colocó en un servicio transaccional, en vez de en el controlador, para que también se aplique al actualizar.

Se añadió H2 únicamente para pruebas de contexto, evitando exigir un servidor MySQL durante los tests. La configuración de producción usa variables de entorno y `ddl-auto=update` para facilitar el desarrollo local. La IA propuso inicialmente una interfaz con framework; se descartó para respetar el requisito y se implementaron módulos nativos del navegador.

## Prompts utilizados y errores corregidos
- “Añade un frontend mínimo sin frameworks”.

Durante el desarrollo se corrigió el riesgo de aceptar reservas con entidades relacionadas inexistentes: el servicio comprueba aula, profesor y franja antes de persistir. También se mantuvo la fecha de creación original al actualizar una reserva.
