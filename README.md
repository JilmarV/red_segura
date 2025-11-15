# Red Segura

Red Segura es una aplicacion Android desarrollada con Jetpack Compose que centraliza la creacion, seguimiento y moderacion de reportes ciudadanos de seguridad. El cliente movil ofrece dos experiencias diferenciadas: vecinos que registran incidentes con evidencias multimedia y administradores que validan cada reporte desde un panel especializado con notificaciones almacenadas en Firebase.

## Tabla de contenidos
1. [Vision general](#vision-general)
2. [Caracteristicas clave](#caracteristicas-clave)
3. [Arquitectura y stack](#arquitectura-y-stack)
4. [Estructura del proyecto](#estructura-del-proyecto)
5. [Modelo de datos en Firestore](#modelo-de-datos-en-firestore)
6. [Requisitos previos](#requisitos-previos)
7. [Configuracion obligatoria](#configuracion-obligatoria)
8. [Ejecucion](#ejecucion)
9. [Navegacion y pantallas](#navegacion-y-pantallas)
10. [Flujo de reportes](#flujo-de-reportes)
11. [Pruebas y calidad](#pruebas-y-calidad)
12. [Limitaciones conocidas](#limitaciones-conocidas)
13. [Contribuir](#contribuir)
14. [Licencia](#licencia)

## Vision general

- **Dominio**: seguridad ciudadana, registro de incidentes geolocalizados y difusion de eventos preventivos.
- **Tecnologia**: aplicacion 100 % Compose con integracion a Firebase Firestore, Analytics y servicios nativos como camara, galeria e informacion de ubicacion.
- **Usuarios objetivo**: ciudadanos (rol `CLIENT`) y operadores municipales (rol `ADMIN`) almacenados en la coleccion `usuarios`.
- **Estado actual**: prototipo funcional con navegacion, consumo de Firestore, mapas Mapbox y formularios principales en espanol.

## Caracteristicas clave

| Rol | Capacidades principales |
| --- | --- |
| Ciudadano (`home`, `reports`, `event`, `notification`, `profile`) | Registro y acceso con correo/clave, creacion de reportes con imagenes y marcador en mapa (`CreateReportScreen`), consulta de feed publico (`HomeScreen`), calendario de eventos (`EventScreen`), panel lateral con cierre de sesion (`SideMenu`) y modificacion basica de datos. |
| Administrador (`homeadmin`) | Verificacion y rechazo de reportes pendientes (`HomeAdminScreen`), registro de motivo de rechazo (`CancelReasonScreen`), generacion de notificaciones de estado, acceso al perfil administrativo y utilidades del menu lateral (`SideMenuAdmin`). |

Otros aspectos destacables:

- Boton de ayuda en `HomeScreen` listo para integrarse con lineas de emergencia.
- Registro con validacion basica y creacion de documentos en Firestore (`RegisterScreen`).
- Plantillas de eventos (`CreateEventScreen`) y detalle (`ReportDetailScreen`) listas para conectarse a datos reales.
- Notificaciones basadas en documentos de Firestore (`NotificationScreen`).

## Arquitectura y stack

- **UI**: Jetpack Compose (Material y Material3) con Navigation Compose (`app/src/main/java/com/example/taller1/ui/theme/NavGraph.kt`).
- **Estado**: `remember`, `mutableStateOf` y `LaunchedEffect` para obtener datos de Firestore.
- **Datos y remoto**: `FirestoreService` (`app/src/main/java/com/example/taller1/firebase/FirestoreService.kt`) concentra todas las operaciones sobre `usuarios`, `reportes` y `notificaciones`.
- **Modelado**: data classes en `app/src/main/java/com/example/taller1/model` (Report, ReportState, Category, Location, ReportNotification, etc.).
- **Sesion**: `UserSession` y `CurrentUser` almacenan temporalmente al usuario autenticado en memoria.
- **Dependencias clave**:
  - Kotlin 2.0.0 + Android Gradle Plugin 8.4.1 + Compose BOM 2024.04.01.
  - Firebase BOM 33.12.0 (Analytics, Firestore, Auth).
  - Mapbox Maps Compose 11.11.0 para mapas interactivos.
  - Coil 2.1.0 para previsualizar imagenes locales.
  - Material Icons y Navigation Compose 2.7.5.

Parametros de compilacion: `minSdk 24`, `targetSdk 35`, `compileSdk 34`, JVM 11 (se recomienda instalar JDK 17 por compatibilidad con AGP 8.4).

## Estructura del proyecto

```
red_segura/
├── app/
│   ├── build.gradle.kts
│   ├── google-services.json
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/taller1/
│       │   │   ├── MainActivity.kt
│       │   │   ├── data/ (UserSession, CurrentUser)
│       │   │   ├── model/ (Report, Category, Role, etc.)
│       │   │   ├── firebase/FirestoreService.kt
│       │   │   └── ui/theme/ (pantallas Compose, menus, navegacion)
│       │   └── res/ (strings, drawables, temas)
│       ├── test/java/.../ExampleUnitTest.kt
│       └── androidTest/java/.../ExampleInstrumentedTest.kt
├── build.gradle.kts
├── gradle.properties
├── gradle/libs.versions.toml
└── settings.gradle.kts
```

## Modelo de datos en Firestore

| Coleccion | Campos minimos | Descripcion |
| --- | --- | --- |
| `usuarios` | `id`, `name`, `address`, `email`, `password`, `role`, `location.latitud`, `location.longitud` | Se crea desde `RegisterScreen`. El campo `role` controla la navegacion (usa `CLIENT` o `ADMIN`). |
| `reportes` | `id`, `title`, `description`, `state` (`PENDING`, `ACCEPTED`, `REJECTED`), `images` (lista de URLs), `location.latitud`, `location.longitud`, `fecha`, `userId`, `category` (`Category` enum), `rejectionReason` opcional | Base del feed ciudadano y del panel administrativo. |
| `notificaciones` | `userId`, `reportId`, `state`, `message`, `motivo`, `timestamp` | Se crean al aceptar o rechazar reportes desde `FirestoreService.createReportResultNotification`. |

Ejemplo de documento en `reportes`:

```json
{
  "id": "rep_001",
  "title": "Hurto en la calle 9",
  "description": "Se reporta hurto a las 8pm en la esquina del parque.",
  "state": "PENDING",
  "images": [
    "https://firebasestorage.googleapis.com/..."
  ],
  "location": { "latitud": 4.5409, "longitud": -75.6653 },
  "fecha": "22/11/2025 21:35",
  "userId": "user_123",
  "category": "ROBO_VEHICULO",
  "rejectionReason": null
}
```

## Requisitos previos

- Android Studio Koala Feature Drop (o superior) con Gradle 8.4.1.
- JDK 17 instalado y configurado en Android Studio.
- Android SDK Platform 24-35 y Build Tools actualizados.
- Cuenta de Firebase con Firestore habilitado.
- Cuenta de Mapbox con access token publico vigente.
- Dispositivo o emulador con Google Play Services y permisos de ubicacion activos.

## Configuracion obligatoria

### 1. Firebase

1. Crea un proyecto en [Firebase Console](https://console.firebase.google.com/) y agrega una app Android con `applicationId` `com.example.taller1`.
2. Descarga `google-services.json` y coloca el archivo en `app/google-services.json`. El repositorio incluye un ejemplo, pero debes reemplazarlo con tus credenciales.
3. Habilita Cloud Firestore y crea las colecciones `usuarios`, `reportes` y `notificaciones`.
4. Define reglas segun tu entorno. Ejemplo para desarrollo:

    ```txt
    rules_version = '2';
    service cloud.firestore {
      match /databases/{database}/documents {
        match /{document=**} {
          allow read, write: if request.time < timestamp.date(2025, 12, 31);
        }
      }
    }
    ```

5. (Opcional) Configura Firebase Authentication si planeas reemplazar el inicio de sesion manual que hoy usa `FirestoreService.login`.

### 2. Mapbox

1. Genera un access token publico en [account.mapbox.com](https://account.mapbox.com/).
2. Declara las claves en `local.properties`:

    ```
    MAPBOX_DOWNLOADS_TOKEN=pk.tu_token_de_descarga
    MAPBOX_ACCESS_TOKEN=pk.tu_token_publico
    ```

3. Expone el token en `app/src/main/res/values/strings.xml`:

    ```xml
    <string name="mapbox_access_token">${MAPBOX_ACCESS_TOKEN}</string>
    ```

4. Declara el meta dato dentro de `<application>` en `app/src/main/AndroidManifest.xml`:

    ```xml
    <meta-data
        android:name="MAPBOX_ACCESS_TOKEN"
        android:value="@string/mapbox_access_token" />
    ```

5. Verifica que el dispositivo tenga conexion estable para cargar los estilos de Mapbox.

### 3. Permisos de ubicacion y camara

- El manifiesto ya solicita `ACCESS_COARSE_LOCATION` y `ACCESS_FINE_LOCATION`. Falta pedirlos en tiempo de ejecucion antes de mostrar `MapboxMap`; puedes hacerlo en `MainActivity` o dentro de `CreateReportScreen`.
- `ActivityResultContracts.TakePicturePreview` no requiere permisos extra, pero si decides persistir archivos deberas solicitar `READ_MEDIA_IMAGES` (Android 13+) o `READ_EXTERNAL_STORAGE` (Android 12-).

## Ejecucion

### Opcion A: Android Studio

1. Clona el repositorio y abre la carpeta `red_segura` en Android Studio.
2. Sincroniza Gradle (`File > Sync Project with Gradle Files`).
3. Selecciona un dispositivo o emulador y pulsa **Run 'app'**.
4. Supervisa los registros en Logcat para revisar respuestas de Firestore, Mapbox y Compose.

### Opcion B: Gradle por linea de comandos

```bash
# Compila la aplicacion
./gradlew assembleDebug

# Instala el APK en un dispositivo conectado
./gradlew installDebug

# Ejecuta analisis estatico
./gradlew lintDebug
```

En Windows reemplaza `./gradlew` por `gradlew.bat`.

## Navegacion y pantallas

| Ruta | Composable | Descripcion |
| --- | --- | --- |
| `login` | `LoginScreen` (`app/src/main/java/com/example/taller1/ui/theme/LoginScreen.kt`) | Autenticacion basica contra Firestore y redireccion segun `Role`. |
| `register` | `RegisterScreen` | Alta de ciudadanos, crea documentos en `usuarios`. |
| `forgotPassword` | `ForgotPassword` | Formulario de recuperacion (accion pendiente). |
| `dataModification` | `DataModificationScreen` | Edicion local del perfil; aun no persiste. |
| `home` | `SideMenuScreen` + `HomeScreen` | Feed general de reportes via `FirestoreService.getReports`. |
| `profile` | `ProfileScreen` | Perfil del ciudadano y accesos a edicion/eliminacion. |
| `reports` | `ReportTabsContent` | Pestanas "Mis reportes" (placeholder) y "Crear reporte" (`CreateReportScreen`). |
| `event` | `EventScreen` | Calendario mensual y lista de eventos cargados en memoria. |
| `notification` | `NotificationScreen` | Consulta de documentos en `notificaciones` filtrados por usuario. |
| `detalle_reporte` | `ReportDetailScreen` | Plantilla de detalle con comentarios estaticos. |
| `admin_profile` | `AdminProfileScreen` | Perfil del operador. |
| `homeadmin` | `HomeAdminScreen` | Panel de revision de reportes pendientes con acciones de verificacion y rechazo. |
| `cancel_reason/{reportId}/{userId}` | `CancelReasonScreen` | Solicita motivo antes de rechazar un reporte. |

El menu lateral (`SideMenu.kt`) maneja la navegacion y el cierre de sesion para ciudadanos; `SideMenuAdmin.kt` replica la experiencia para operadores con accesos especificos.

## Flujo de reportes

1. **Registro** (`RegisterScreen`): crea un documento en `usuarios` con rol `CLIENT`.
2. **Inicio de sesion** (`LoginScreen`): consulta Firestore por correo y clave, luego llena `UserSession`.
3. **Creacion** (`CreateReportScreen`): el ciudadano captura titulo, descripcion, categoria, imagen y punto en el mapa. Se genera un documento en `reportes` con estado `PENDING`.
4. **Moderacion** (`HomeAdminScreen`): los operadores leen `reportes` filtrando por `state == PENDING` (`FirestoreService.getPendingReports`) y aprueban o rechazan.
5. **Notificacion** (`FirestoreService.createReportResultNotification`): al aceptar o rechazar se inserta un documento en `notificaciones` con el estado final.
6. **Seguimiento** (`NotificationScreen`): los ciudadanos revisan el resultado y, si aplica, el motivo de rechazo.
7. **Rechazo con motivo** (`CancelReasonScreen`): obliga a detallar la causa antes de actualizar `reportes`.

## Pruebas y calidad

- **Unit tests** (`app/src/test/java/com/example/taller1/ExampleUnitTest.kt`): ejecuta `./gradlew testDebugUnitTest`.
- **Pruebas instrumentadas** (`app/src/androidTest/.../ExampleInstrumentedTest.kt`): requieren un emulador, corre `./gradlew connectedDebugAndroidTest`.
- **Lint**: `./gradlew lintDebug` revisa problemas comunes de Android y Compose.
- **Revisiones manuales sugeridas**:
  - Crear un usuario cliente y confirmar acceso al menu lateral.
  - Editar el campo `role` en Firestore para simular un administrador y probar `homeadmin`.
  - Generar reportes y validar que `notificaciones` se llena correctamente tras aprobar o rechazar.

> Las pruebas automatizadas incluidas son plantillas generadas por Android Studio; anade casos que cubran `FirestoreService`, validaciones de formularios y navegacion.

## Limitaciones conocidas

- El inicio de sesion usa consultas directas a Firestore en texto plano (sin Firebase Auth ni hash de claves).
- `UserSession` y `CurrentUser` conservan datos solo en memoria y se pierden al cerrar la app.
- `NotificationScreen` usa `UserSession.currentUser?.id`; ajusta el tipo para que compile y considera usar listeners en tiempo real.
- `CreateReportScreen` aun no:
  - Persiste las coordenadas seleccionadas ni valida permisos de ubicacion.
  - Sube imagenes a Firebase Storage (solo guarda URIs locales).
  - Utiliza `isUploading` o `uploadError` para indicar estados de carga.
- `MyReportScreen`, `ReportDetailScreen`, `ProfileScreen`, `AdminProfileScreen`, `EventScreen` y `CreateEventScreen` muestran datos estaticos.
- `ForgotPassword` no dispara ningun flujo real.
- El boton "AYUDA" de `HomeScreen` no dispara acciones.
- Falta manejo de reintentos y errores de red (por ejemplo, escucha en tiempo real con `addSnapshotListener`).

## Contribuir

1. Crea un branch a partir de `main`.
2. Asegurate de que `./gradlew lintDebug testDebugUnitTest` finalicen correctamente antes de subir cambios.
3. Abre un Pull Request describiendo la funcionalidad, capturas y pasos de prueba manual.
4. Mantener el estilo en Kotlin + Compose e incluye documentacion relevante en este README.

## Licencia

Define aqui la licencia del proyecto (por ejemplo MIT, Apache-2.0 o uso interno). Mientras se decide, todo el codigo tiene fines academicos y requiere autorizacion previa antes de distribuirse.
