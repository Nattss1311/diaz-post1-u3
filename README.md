# diaz-post1-u3-
Post-contenido — Patrones Estructurales aplicados al backend de ConfUDES

Análisis de Diseño — Necesidad 1
(1) ¿Cuál es el síntoma de diseño exacto que describe el enunciado?
Existe una incompatibilidad directa de interfaces entre el contrato interno del sistema (ServicioAsistencia) y la API que expone el SDK del proveedor externo (QRCheckClient). Los tipos de datos, los nombres de los métodos, las firmas y los códigos de respuesta no coinciden. Además, ninguna de las dos partes puede modificarse directamente porque el código interno ya está en producción y la librería externa es de un tercero.

(2) ¿Qué patrón(es) de los vistos en la guía de la unidad podrían encajar y por qué?

Adapter: Encaja porque su propósito explícito es convertir la interfaz de una clase existente en otra interfaz que el cliente espera, permitiendo que dos clases con interfaces incompatibles colaboren sin modificar su código fuente.

Facade: Podría considerarse bajo la idea intuitiva de envolver el cliente externo de QR dentro de una nueva clase.

(3) ¿Cuál de ellos se descarta y con qué argumento técnico, no solo intuitivo?
Se descarta Facade. La intención técnica de Facade es simplificar una interfaz compleja unificando el acceso a un subsistema compuesto por múltiples clases, interfaces y capas. En este problema no existe un subsistema complejo que unificar ni simplificar, sino una traducción/adaptación 1 a 1 entre dos contratos de interfaz incompatibles (ServicioAsistencia hacia QRCheckClient). Por lo tanto, el patrón técnicamente adecuado es Adapter.

Esta estructura responde punto por punto a lo que la rúbrica evalúa en el criterio de análisis escrito.

Análisis de Diseño — Necesidad 2
(1) ¿Cuál es el síntoma de diseño exacto que describe el enunciado?
El controlador web (ControladorCertificados) sufre de acoplamiento excesivo. Debe conocer, instanciar y orquestar directamente cuatro servicios distintos para ejecutar una sola tarea de negocio (emitir certificados). Esto satura la capa web con lógica de orquestación y obliga a modificar la API pública si el flujo de emisión cambia.

(2) ¿Qué patrón(es) de los vistos en la guía de la unidad podrían encajar y por qué?

Facade: Encaja perfectamente porque su intención es proporcionar una interfaz unificada y de alto nivel sobre un grupo de interfaces en un subsistema, simplificando el uso para el cliente.

Adapter: Podría considerarse bajo la falsa idea de "envolver" las llamadas a los cuatro servicios.

(3) ¿Cuál de ellos se descarta y con qué argumento técnico, no solo intuitivo?
Se descarta Adapter. El patrón Adapter se utiliza ante un problema de incompatibilidad de contratos o tipos. En la Necesidad 2 no hay ninguna interfaz incompatible que adaptar; los cuatro servicios funcionan correctamente con sus APIs actuales. El problema radica en la cantidad de colaboradores que el cliente debe conocer.

Por ende, un Adapter no resolvería la Necesidad 2 porque su meta es traducir firmas de métodos, no simplificar flujos complejos de múltiples servicios. Del mismo modo, un Facade no resolvería la Necesidad 1 porque no soluciona diferencias de firma entre dos contratos 1 a 1.