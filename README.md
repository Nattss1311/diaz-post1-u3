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

## Análisis de Diseño — Necesidad 3 (Punto de Decisión 3)

### (1) ¿Cuál es el síntoma de diseño exacto que describe el enunciado?
El síntoma es la necesidad de agregar responsabilidades y funcionalidades opcionales a un objeto en tiempo de ejecución de manera dinámicamente combinable, sin incurrir en una explosión combinatoria de clases ni en la modificación del código base o contratos existentes.

Específicamente, se requiere aplicar 3 mejoras opcionales (marca de agua, código QR y traducción al inglés) sobre un PDF ya emitido. Al haber $2^N$ combinaciones posibles ($2^3 = 8$ combinaciones), el diseño debe permitir activar ninguna, una o varias mejoras en cualquier orden sin alterar la lógica de emisión original (`FachadaCertificados`).

### (2) ¿Qué patrón(es) de los vistos en la guía de la unidad podrían encajar y por qué?
* **Patrón Decorador (*Decorator*):** Encaja perfectamente porque permite añadir responsabilidades adicionales a un objeto dinámicamente en tiempo de ejecución envolviendo (*wrapping*) la instancia base mediante la misma interfaz (`ServicioCertificados`). Cada mejora actúa como un "envoltorio" transparente e independiente que añade su comportamiento antes o después de delegar la llamada al objeto envuelto, cumpliendo el principio Open/Closed.

### (3) ¿Cuál(es) de ellos se descarta y con qué argumento técnico?
Se descartan dos alternativas comúnmente intentadas:
1. **Herencia directa (Una subclase por combinación):**
   * **Argumento técnico:** Genera una explosión de subclases ($2^N$). Para 3 mejoras se requerirían 8 subclases; para 5 mejoras, 32 subclases. Además, acopla las combinaciones de manera estática en tiempo de compilación, impidiendo activar o desactivar mejoras dinámicamente por evento.
2. **Parámetros booleanos / Banderas en el método `emitir()`:**
   * **Argumento técnico:** Viola explícitamente el Principio Open/Closed (SOLID) y el Principio de Responsabilidad Única (SRP). Cada nueva mejora obligaría a modificar la interfaz `ServicioCertificados`, la DTO `SolicitudCertificado` y la implementación base `FachadaCertificados` agregando bloques condicionales (`if/else`). Esto sobrecarga la clase base con lógica de presentación/formato de bajo nivel (`UtilidadesPDF`), incrementando el acoplamiento y dificultando las pruebas unitarias.

### Solución Implementada: Patrón Decorador
Se creó un decorador base abstracto que implementa `ServicioCertificados` y sostiene una referencia a `ServicioCertificados`, junto con los 3 decoradores concretos:
* `DecoradorCertificado.java` (Decorador Base)
* `DecoradorMarcaDeAgua.java`
* `DecoradorCodigoQR.java`
* `DecoradorTraduccionIngles.java`


### Necesidad 4 — Control de acceso a la descarga masiva

## Análisis de Diseño — Necesidad 4 (Punto de Decisión 4)

### (1) ¿Cuál es el síntoma de diseño exacto que describe el enunciado?
El síntoma es la necesidad de **controlar y restringir el acceso a una operación costosa y restringida** (descarga/emisión masiva que consume límites del proveedor de firma digital) basándose en roles (`ORGANIZADOR` o `ADMIN`), sin que los clientes (como el front-end) tengan que modificar su contrato ni conocer las reglas de autenticación o límites del proveedor.

### (2) ¿Qué patrón(es) de los vistos en la guía de la unidad podrían encajar y por qué?
* **Patrón Proxy (Proxy de Protección / Control de Acceso):** Es el patrón adecuado porque actúa como un intermediario transparente que implementa la misma interfaz (`ServicioCertificados`). Intercepta la llamada, evalúa la autorización mediante `ContextoUsuario` y **decide si delega o no** la ejecución al servicio real, bloqueando el acceso antes de invocar la operación costosa si el usuario no tiene permisos.

### (3) Diferencia de Intención entre Decorador (Necesidad 3) y Proxy (Necesidad 4)
Aunque ambas soluciones envuelven un objeto que implementa `ServicioCertificados` con otro que implementa la misma interfaz (siendo idénticas estructuralmente), su intención difiere:
* **Decorador (*Decorator*):** Añade capacidades o responsabilidades adicionales al resultado y **siempre termina delegando** en el objeto real envuelto.
* **Proxy (*Proxy*):** Controla la llamada y **decide si delega o no** la ejecución al objeto real, pudiendo sustituir o abortar por completo el acceso sin que la lógica costosa llegue a ejecutarse.

**¿Por qué no funcionaría intercambiarlos?**
* **Usar el patrón de la Necesidad 4 (Proxy) para las mejoras de la Necesidad 3:** Le quitaría flexibilidad al sistema, ya que el Proxy está pensado para controlar/representar un único objeto y dejaría sin resolver el problema de combinar libremente $2^N$ mejoras en cualquier orden en tiempo de ejecución.
* **Usar el patrón de la Necesidad 3 (Decorador) para el control de acceso de la Necesidad 4:** Ejecutaría obligatoriamente la operación costosa antes de evaluar si el usuario tiene permiso, desperdiciando recursos y sobrepasando los límites del proveedor de firma digital.

---

## Reflexión  — Composite y Flyweight

* **Estructura Jerárquica de la Agenda (Tracks, Sesiones, Actividades):** El patrón estructural que encaja de forma natural es **Composite**, ya que permite representar estructuras jerárquicas del tipo "árbol-de-partes" y tratar objetos individuales (actividades) y composiciones de objetos (sesiones y tracks) de manera uniforme a través de una misma interfaz.
* **Saturación de Memoria por Credenciales QR:** El patrón **Flyweight** no amerita ser aplicado en este escenario porque cada credencial QR posee **estado intrínseco/único e irrepetible** (datos personales del participante, ID de certificado y URL de verificación única), por lo cual no existe un estado compartido o extrapolable entre instancias.



## Herramientas utilizadas
- Java 17, Spring Boot 3.2, Apache Maven, JUnit 5
- VS Code o IntelliJ IDEA, Git, GitHub

## Conclusiones

[Pendiente — Se redactará al finalizar ambas partes]