# Declaración sobre el Uso de Inteligencia Artificial

---

##  Herramienta de IA Utilizada
* **Modelo / Asistente:** Gemini y Github Copilot.
* **Modalidad de uso:** Asistencia conversacional en vivo durante el ciclo de desarrollo.

---

##  Finalidad del Uso
Las herramientas de IA fueron utilizadas exclusivamente como apoyo técnico y de pensamiento para las siguientes tareas:
1. **Resolución de errores de compilación y Git:** Permitieron solucionar conflictos dentro de Git (purga de historial, resets de commits erroneos), errores de Gradle y configuración de rutas de paquetes.
2. **Manejo de la seguridad:** Sirvieron de guía para excluir credenciales privadas (`google-services.json` y la **API KEY** de google maps) mediante `.gitignore` y restringir las reglas de Firestore.
3. **Estructura de arquitectura y componentes:** Ayudaron a comprender e implementar un patrón de `Adapter` (`RecyclerView`), configurar el `SupportMapFragment` y capturar excepciones de red en Cloud Firestore sin *crasheos*.
4. **Obtención de credenciales de Google Maps:** Asistieron paso a paso para la extracción de la huella digital **SHA-1** e integración restringida de la API Key.

---

##  Ejemplos de Consultas Realizadas

1. **Consulta sobre GPS y permisos:**
- *"Si el usuario acepta los permisos de la app, ¿el GPS de su dispositivo se enciende automáticamente?"*
2. **Consulta sobre Firestore y manejo de red:**
- *"Un requerimiento para la app es que pueda leer los datos de Firestore, mostrando y manejando los errores de red sin que se cierre la aplicación. ¿Cómo funciona ese proceso?"*
3. **Consulta sobre arquitectura de UI:**
- *"¿Qué es un RecordAdapter? Intento utilizarlo en la pantalla para cargar los datos desde Firestore pero no sé qué es y qué rol tiene en este proceso."*
4. **Consulta sobre Git / Historial:**
- *"Me equivoqué con el contenido de un commit. ¿Cómo puedo deshacerlo sin borrar los cambios realizados?"*
5. **Consulta sobre Google Maps SDK:**
- *"¿Cómo obtengo la huella digital SHA-1 en Android Studio cuando no me aparece la tarea signingReport en Gradle?"*

---

##  Revisión, Verificación y Aportes del Estudiante

Todos los fragmentos de código, sugerencias de configuración y arquitecturas provistos por la IA fueron **revisados, probados, adaptados e integrados manualmente** antes de su publicación en Github:

* **Escribir el código final:** Se implementaron los Activitys (`MainActivity`, `CreateRecord`, `ReadRecords`, `ViewMap`, etc) ajustándolos a la lógica del proyecto.
* **Pruebas y simulación:** Se ejecutaron pruebas reales en un dispositivo físico para comprobar la captura del sensor utilizado (acelerómetro), la lectura de coordenadas GPS y la caída de red.
* **Seguridad y Cloud Console:** Se administraron manualmente las cuentas de Google Cloud Console y Firebase para configurar las restricciones de dominio, firmas SHA-1 y la base de datos Firestore.