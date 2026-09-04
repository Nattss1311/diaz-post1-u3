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


* **(1) ¿Cuál es el síntoma de diseño exacto que describe el enunciado?**
  Existe una **incompatibilidad estructural directa entre interfaces**: el contrato interno del sistema (`ServicioAsistencia`) no coincide en nombres de métodos, tipos de parámetros, firmas ni estructuras de retorno con la API expuesta por el SDK de un proveedor externo (`QRCheckClient`). Además, ninguno de los dos contratos puede ser modificado directamente, ya que el código interno está desplegado en producción y la librería externa pertenece a un tercero.

* **(2) ¿Qué patrón(es) de los vistos en la guía de la unidad podrían encajar y por qué?**
  Se aplicó el patrón **Adapter (Adaptador de Objetos)**. Es la solución adecuada porque actúa como un intermediario o puente que envuelve al cliente del SDK externo (`QRCheckClient`) y traduce dinámicamente sus llamadas, parámetros y respuestas para adaptarlos al contrato de la interfaz interna esperada (`ServicioAsistencia`), logrando la interoperabilidad sin alterar el código existente.

* **(3) ¿Cuál alternativa se descarta y con qué argumento técnico?**
  Se consideró y descartó la alternativa cercana **Facade (Fachada)**:
  * **Argumento técnico:** La intención de *Facade* es proporcionar una interfaz unificada y simplificada sobre un subsistema complejo integrado por múltiples clases y componentes. En este escenario no hay un subsistema con múltiples capas que simplificar, sino la necesidad explícita de adaptar un único contrato hacia otro (traducción 1 a 1 entre `ServicioAsistencia` y `QRCheckClient`). 
  * **Conclusión:** *Facade* cambiaría o crearía un nuevo punto de entrada en lugar de traducir exactamente las firmas del contrato que `ControladorCheckIn` ya exige usar en producción. Por ello, *Facade* no resuelve el problema de adaptar los contratos de interfaz existentes.

### Necesidad 2 — Emisión de certificados

* **(1) ¿Cuál es el síntoma de diseño exacto que describe el enunciado?**
  Existe un **acoplamiento excesivo en el cliente web** (`ControladorCertificados`), el cual debe conocer, instanciar y orquestar secuencialmente cuatro servicios independientes (`ValidadorAsistencia`, `GeneradorCertificadoPDF`, `FirmaDigitalService` y `EnvioCorreoService`) para ejecutar una única operación de negocio. Esto satura la capa de controladores con lógica de orquestación, violando la cohesión y exponiendo la complejidad interna del flujo de emisión.

* **(2) ¿Qué patrón(es) de los vistos en la guía de la unidad podrían encajar y por qué?**
  Se aplicó el patrón **Facade (Fachada)** mediante la clase `FachadaCertificados`. Es el patrón indicado porque proporciona una interfaz unificada y simplificada sobre un subsistema compuesto por múltiples servicios colaboradores. La fachada encapsula la secuencia completa de emisión (validar, generar, firmar y enviar por correo), permitiendo que el controlador interactúe con un único punto de entrada simple y desacoplado.

* **(3) ¿Cuál alternativa se descarta y con qué argumento técnico?**
  Se consideró y descartó la alternativa cercana **Adapter (Adaptador)**:
  * **Argumento técnico:** La intención técnica de *Adapter* es traducir y resolver incompatibilidades de contrato entre dos interfaces preexistentes que no pueden comunicarse directamente. En la Necesidad 2 no existen interfaces incompatibles ni errores de firmas entre clases, ya que los cuatro servicios funcionan correctamente con sus APIs actuales.
  * **Conclusión:** Aplicar un *Adapter* no resuelve el problema central, ya que la meta no es modificar o adaptar las firmas individuales de cada servicio, sino ocultar la complejidad de orquestar múltiples colaboradores detrás de una sola abstracción de alto nivel. Por ello, *Adapter* resulta inadecuado.

### Necesidad 3 — Mejoras opcionales del certificado

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

La implementación de patrones estructurales en el proyecto ConfUDES demostró que el verdadero reto del diseño de software no radica en la complejidad de la estructura, sino en identificar con precisión la intención técnica de cada patrón según la necesidad del negocio. La principal dificultad consistió en diferenciar entre patrones con diagramas de clases casi idénticos, como Adapter frente a Facade o Decorator frente a Proxy, donde la clave para tomar la decisión correcta fue evaluar si el objetivo era traducir contratos, simplificar orquestaciones, añadir comportamiento dinámico en tiempo de ejecución o interceptar llamadas para proteger recursos costosos. Comprender este criterio permitió resolver problemas reales de integración con SDKs de terceros, control de acceso por roles y flexibilidad en la generación de certificados sin alterar el código existente ni incurrir en una explosión combinatoria de subclases. En conclusión, priorizar la composición sobre la herencia y adherirse al principio Open/Closed garantizó una arquitectura desacoplada, mantenible y altamente extensible ante futuros requerimientos.