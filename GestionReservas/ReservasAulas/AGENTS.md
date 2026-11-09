# Instrucciones para agentes

- Mantener la separación por capas: entidades en `model`, repositorios Spring Data, servicios para reglas de negocio y controladores REST.
- Usar nombres de endpoints y mensajes en español, coherentes con el dominio de reservas.
- No guardar secretos en el repositorio. La conexión MySQL se configura mediante `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`.
- Antes de entregar cambios, ejecutar `./mvnw test` y comprobar que las validaciones de reservas siguen activas.
- El proyecto compila con JDK 17 o superior; es necesario instalar un JDK completo, no solo un JRE.
- El frontend debe continuar usando únicamente HTML, CSS y JavaScript sin frameworks.
