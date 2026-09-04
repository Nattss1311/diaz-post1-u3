# Post-contenido — Unidad 3: Patrones Estructurales en ConfUDES

## Descripción
Repositorio del post-contenido de la Unidad 3 de Patrones de Diseño de Software. Un único proyecto Spring Boot (`confudes-patrones-estructurales`) que resuelve cuatro necesidades reales del backend de ConfUDES, una plataforma de gestión de congresos académicos: registro de asistencia con un proveedor externo, emisión de certificados, mejoras opcionales sobre el certificado emitido y control de acceso a la descarga masiva.


## Cómo ejecutar
```
$ mvn clean package
$ mvn spring-boot:run
$ mvn test
```
## Decisiones de diseño
### Necesidad 1 — Registro de asistencia

Se aplicó el patrón Adapter.

Existe una incompatibilidad directa de interfaces entre el contrato interno del sistema (ServicioAsistencia) y la API que expone el SDK del proveedor externo (QRCheckClient). Los tipos de datos, nombres de métodos, firmas y códigos de respuesta no coinciden, y ninguna de las dos partes puede modificarse directamente porque el código interno está en producción y la librería externa pertenece a un tercero.

Se descartó la alternativa cercana Facade. La intención técnica de Facade es simplificar una interfaz compleja unificando el acceso a un subsistema compuesto por múltiples clases y capas. En este escenario no existe un subsistema complejo que unificar ni simplificar, sino una traducción 1 a 1 entre dos contratos de interfaz incompatibles (ServicioAsistencia hacia QRCheckClient). Por lo tanto, Facade no resuelve el problema de adaptar la firma entre los dos contratos existentes.

### Necesidad 2 — Emisión de certificados
Se aplicó el patrón Facade.

El controlador web (ControladorCertificados) sufría de un acoplamiento excesivo, ya que debía conocer, instanciar y orquestar directamente cuatro servicios distintos para ejecutar una sola tarea de negocio (emitir certificados). Esto saturaba la capa web con lógica de coordinación y obligaba a modificar la API pública si el flujo de emisión cambiaba.

Se descartó la alternativa cercana Adapter. El patrón Adapter se utiliza cuando existe un problema de incompatibilidad de contratos o tipos. En la Necesidad 2 no hay ninguna interfaz incompatible que adaptar, ya que los cuatro servicios funcionan correctamente con sus APIs actuales; el problema radica en la cantidad de colaboradores que el cliente debe orquestar. Un Adapter no resolvería el problema porque su meta es traducir firmas de métodos y no simplificar la orquestación de múltiples servicios.

### Necesidad 3 — Mejoras opcionales del certificado
[Pendiente — Se completará en la Parte 2]

### Necesidad 4 — Control de acceso a la descarga masiva
[Pendiente — Se completará en la Parte 2]

### Reflexión — Composite y Flyweight (opcional)
[Pendiente — Se completará en la Parte 2]

## Herramientas utilizadas
- Java 17, Spring Boot 3.2, Apache Maven, JUnit 5
- VS Code o IntelliJ IDEA, Git, GitHub

## Conclusiones

[Pendiente — Se redactará al finalizar ambas partes]