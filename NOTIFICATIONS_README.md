# Módulo de Notificaciones - EBSA Nexus Backend

## Descripción

Este módulo proporciona un sistema completo de notificaciones para el backend de EBSA Nexus. Las notificaciones se almacenan en MySQL y se cargan automáticamente cuando el usuario inicia sesión.

## Arquitectura

El módulo sigue los principios de **Clean Architecture** con las siguientes capas:

### 1. Capa de Dominio (`domain`)

- **Entidad**: `Notification` - Entidad JPA que representa una notificación
- **Enum**: `NotificationType` - Tipos de notificaciones del sistema
- **Repositorio**: `NotificationRepository` - Interfaz de repositorio de dominio

### 2. Capa de Aplicación (`application`)

- **Servicio**: `NotificationApplicationService` - Lógica de negocio para gestión de notificaciones
- **Servicio**: `NoveltyNotificationService` - Creación automática de notificaciones para eventos de novedades
- **DTOs**:
  - `CreateNotificationRequest` - DTO para crear notificaciones
  - `NotificationResponse` - DTO de respuesta
  - `NotificationSummaryResponse` - Resumen de notificaciones para login
- **Mapper**: `NotificationMapper` - Mapeo entre entidades y DTOs

### 3. Capa de Infraestructura (`infrastructure`)

- **JPA Repository**: `JpaNotificationRepository` - Repositorio Spring Data JPA
- **Implementación**: `NotificationRepositoryImpl` - Implementación del repositorio de dominio

### 4. Capa de Presentación (`presentation`)

- **Controller**: `NotificationController` - Endpoints REST para notificaciones

## Tipos de Notificaciones

```java
public enum NotificationType {
    NOVELTY_CREATED,           // Nueva novedad creada
    NOVELTY_ASSIGNED,          // Novedad asignada
    NOVELTY_STATUS_CHANGED,    // Estado de novedad actualizado
    NOVELTY_COMPLETED,         // Novedad completada
    CREW_ASSIGNED,             // Cuadrilla asignada
    SYSTEM_ALERT,              // Alerta del sistema
    REMINDER,                  // Recordatorio
    GENERAL                    // Notificación general
}
```

## Endpoints API

### 1. Crear Notificación

```http
POST /api/v1/notifications
Content-Type: application/json

{
  "userId": 1,
  "noveltyId": 123,
  "type": "NOVELTY_CREATED",
  "title": "Nueva Novedad",
  "message": "Se ha creado una nueva novedad"
}
```

**Respuesta:**

```json
{
  "id": 1,
  "userId": 1,
  "noveltyId": 123,
  "type": "NOVELTY_CREATED",
  "title": "Nueva Novedad",
  "message": "Se ha creado una nueva novedad",
  "isRead": false,
  "createdAt": "2025-10-25T10:30:00"
}
```

### 2. Obtener Resumen de Notificaciones (Login)

```http
GET /api/v1/notifications/user/{userId}/summary
```

**Respuesta:**

```json
{
  "allNotifications": [...],
  "unreadCount": 5,
  "recentNotifications": [...]
}
```

### 3. Obtener Todas las Notificaciones de un Usuario

```http
GET /api/v1/notifications/user/{userId}
```

### 4. Obtener Notificaciones No Leídas

```http
GET /api/v1/notifications/user/{userId}/unread
```

### 5. Contar Notificaciones No Leídas

```http
GET /api/v1/notifications/user/{userId}/unread/count
```

**Respuesta:**

```json
5
```

### 6. Obtener Notificaciones por Tipo

```http
GET /api/v1/notifications/user/{userId}/type/{type}
```

### 7. Obtener Notificaciones de una Novedad

```http
GET /api/v1/notifications/novelty/{noveltyId}
```

### 8. Marcar Notificación como Leída

```http
PATCH /api/v1/notifications/{notificationId}/read
```

### 9. Marcar Todas como Leídas

```http
PATCH /api/v1/notifications/user/{userId}/read-all
```

### 10. Eliminar Notificación

```http
DELETE /api/v1/notifications/{notificationId}
```

### 11. Eliminar Todas las Notificaciones de un Usuario

```http
DELETE /api/v1/notifications/user/{userId}
```

## Uso desde el Frontend

### Al Iniciar Sesión

Cuando un usuario inicia sesión, el frontend debe llamar al endpoint de resumen:

```javascript
// Al login exitoso
const userId = loginResponse.userId;

// Cargar notificaciones
const notificationSummary = await fetch(
  `/api/v1/notifications/user/${userId}/summary`
).then((res) => res.json());

console.log(
  "Total notificaciones:",
  notificationSummary.allNotifications.length
);
console.log("No leídas:", notificationSummary.unreadCount);
console.log(
  "Recientes (7 días):",
  notificationSummary.recentNotifications.length
);
```

### Mostrar Badge de No Leídas

```javascript
// Obtener contador de no leídas
const unreadCount = await fetch(
  `/api/v1/notifications/user/${userId}/unread/count`
).then((res) => res.json());

// Actualizar badge en UI
setBadgeCount(unreadCount);
```

### Marcar como Leída al Abrir

```javascript
async function handleNotificationClick(notificationId) {
  // Marcar como leída
  await fetch(`/api/v1/notifications/${notificationId}/read`, {
    method: "PATCH",
  });

  // Actualizar UI
  refreshNotifications();
}
```

### Crear Notificación desde el Frontend

```javascript
async function createNotification(
  userId,
  type,
  title,
  message,
  noveltyId = null
) {
  const response = await fetch("/api/v1/notifications", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      userId,
      noveltyId,
      type,
      title,
      message,
    }),
  });

  return response.json();
}
```

## Notificaciones Automáticas

El sistema crea notificaciones automáticamente para los siguientes eventos:

### Eventos de Novedades:

1. **Creación de Novedad**: Se notifica a administradores
2. **Asignación de Cuadrilla a Novedad**: Se notifica a los miembros de la cuadrilla
3. **Cambio de Estado**: Se notifica al creador de la novedad
4. **Novedad Completada**: Se notifica al creador
5. **Completación Rechazada**: Se notifica a los miembros de la cuadrilla
6. **Novedad Cancelada**: Se notifica al creador
7. **Novedad Vencida**: Se notifica a administradores y cuadrilla asignada

### Eventos de Cuadrillas:

8. **Miembro Agregado a Cuadrilla**: Se notifica automáticamente al usuario cuando es agregado como miembro de una cuadrilla
9. **Líder Agregado a Cuadrilla**: Se notifica automáticamente al usuario cuando es agregado como líder de una cuadrilla
10. **Miembro Removido de Cuadrilla**: Se notifica automáticamente al usuario cuando es removido de una cuadrilla

### Eventos de Incidentes:

11. **Incidente Asignado a Cuadrilla**: Se notifica a TODOS los miembros de la cuadrilla cuando se les asigna un nuevo incidente

### Notificaciones de Cuadrillas

#### 1. Al Agregar Miembro o Líder

Cuando se agrega un miembro a una cuadrilla mediante el endpoint:

```http
POST /api/v1/crews/{crewId}/members
```

**El sistema automáticamente:**

- Crea una notificación para el usuario agregado
- La notificación indica que ha sido agregado a la cuadrilla
- Incluye el nombre de la cuadrilla y el tipo de asignación (miembro o líder)

**Ejemplo de notificación generada:**

```json
{
  "userId": 5,
  "type": "CREW_ASSIGNED",
  "title": "Asignado a Cuadrilla",
  "message": "Has sido agregado como miembro de la cuadrilla 'Alpha Team'."
}
```

#### 2. Al Remover Miembro de Cuadrilla

Cuando se remueve un miembro mediante:

```http
DELETE /api/v1/crews/{crewId}/members/{userId}
```

**El sistema automáticamente:**

- Crea una notificación para el usuario removido
- Indica que ha sido removido de la cuadrilla
- Especifica si era miembro regular o líder

**Ejemplo de notificación generada:**

```json
{
  "userId": 5,
  "type": "CREW_ASSIGNED",
  "title": "Removido de Cuadrilla",
  "message": "Has sido removido como miembro de la cuadrilla 'Alpha Team'."
}
```

#### 3. Al Asignar Incidente/Novedad a Cuadrilla

Cuando se asigna un incidente a una cuadrilla mediante:

```http
POST /api/v1/incidents/{incidentId}/assign
{
  "crewId": 10,
  "assignedBy": 1
}
```

**El sistema automáticamente:**

- Crea notificaciones para TODOS los miembros activos de la cuadrilla
- Cada miembro recibe una notificación personalizada
- El mensaje diferencia entre líder y miembros regulares

**Ejemplo de notificaciones generadas:**

Para el líder:

```json
{
  "userId": 5,
  "type": "NOVELTY_ASSIGNED",
  "title": "Nueva Novedad Asignada",
  "message": "Tu cuadrilla 'Alpha Team' ha sido asignada a un nuevo incidente (ID: 123). Revisa los detalles y coordina con tu equipo."
}
```

Para miembros regulares:

```json
{
  "userId": 6,
  "type": "NOVELTY_ASSIGNED",
  "title": "Nueva Novedad Asignada",
  "message": "La cuadrilla 'Alpha Team' ha sido asignada a un nuevo incidente (ID: 123). Revisa los detalles y coordina con tu equipo."
}
```

Si la asignación incluye notas iniciales:

```json
{
  "message": "Tu cuadrilla 'Alpha Team' ha sido asignada a un nuevo incidente (ID: 123). Revisa los detalles y coordina con tu equipo. Notas: Verificar fuga de agua en medidor."
}
```

### Notificaciones de Cambio de Estado de Novedad

El sistema también envía notificaciones cuando cambia el estado de una novedad:

#### 1. Novedad Creada

Se notifica a los administradores:

```json
{
  "type": "NEW_NOVELTY",
  "title": "Nueva novedad reportada",
  "message": "Se ha reportado una nueva novedad por el supervisor. Requiere asignación de cuadrilla."
}
```

#### 2. Cambio General de Estado

Se notifica al usuario que creó la novedad:

```json
{
  "type": "STATUS_CHANGE",
  "title": "Cambio de estado en novedad",
  "message": "La novedad ha cambiado de estado a: EN_CURSO"
}
```

#### 3. Novedad Completada

Se notifica al creador:

```json
{
  "type": "NOVELTY_COMPLETED",
  "title": "Novedad completada",
  "message": "La novedad ha sido marcada como completada. Pendiente de cierre administrativo."
}
```

#### 4. Completación Rechazada

Se notifica a los miembros de la cuadrilla asignada:

```json
{
  "type": "COMPLETION_REJECTED",
  "title": "Completación rechazada",
  "message": "La completación de la novedad ha sido rechazada. Requiere trabajo adicional"
}
```

#### 5. Novedad Cancelada

Se notifica al creador:

```json
{
  "type": "NOVELTY_CANCELLED",
  "title": "Novedad cancelada",
  "message": "La novedad ha sido cancelada. Revisa las observaciones para más detalles."
}
```

#### 6. Novedad Vencida

Se notifica a administradores y cuadrilla asignada:

```json
{
  "type": "NOVELTY_OVERDUE",
  "title": "Novedad vencida",
  "message": "La novedad ha superado la fecha estimada de resolución: 2025-10-30T10:00:00"
}
```

### Ventajas de las Notificaciones Automáticas

- ✅ **No requiere llamadas adicionales desde el frontend**: Las notificaciones se crean automáticamente
- ✅ **Más seguro**: Son llamadas internas, no requieren permisos especiales
- ✅ **Garantizado**: Siempre se crean las notificaciones cuando ocurre el evento
- ✅ **Consistente**: Sigue el mismo patrón en todo el sistema
- ✅ **No bloquea operaciones**: Si falla la notificación, la operación principal continúa
- ✅ **Comunicación en tiempo real**: Todos los afectados son notificados inmediatamente

## Base de Datos

La tabla `notifications` en MySQL tiene la siguiente estructura:

```sql
CREATE TABLE notifications (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT UNSIGNED NOT NULL,
  novelty_id BIGINT UNSIGNED NULL,
  type VARCHAR(50) NOT NULL,
  title VARCHAR(200) NOT NULL,
  message TEXT NOT NULL,
  is_read TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  INDEX idx_notifications_user_id (user_id),
  INDEX idx_notifications_is_read (is_read),
  INDEX idx_notifications_created_at (created_at),
  INDEX idx_notifications_novelty_id (novelty_id),

  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (novelty_id) REFERENCES novelties(id) ON DELETE SET NULL
);
```

## Validaciones

- El título no puede exceder 200 caracteres
- El mensaje no puede exceder 2000 caracteres
- El userId es obligatorio
- El tipo de notificación es obligatorio
- No se pueden crear notificaciones para usuarios inexistentes

## Manejo de Errores

El controlador maneja los siguientes errores:

- `400 Bad Request`: Datos inválidos en la petición
- `404 Not Found`: Notificación o usuario no encontrado
- `500 Internal Server Error`: Error interno del servidor

## Testing

Ejemplo de test unitario:

```java
@SpringBootTest
class NotificationApplicationServiceTest {

    @Autowired
    private NotificationApplicationService notificationService;

    @Test
    void shouldCreateNotification() {
        Notification notification = notificationService.createNotification(
            1L,
            "NOVELTY_CREATED",
            "Test Notification",
            "This is a test",
            null
        );

        assertNotNull(notification.getId());
        assertEquals("Test Notification", notification.getTitle());
        assertFalse(notification.getIsRead());
    }
}
```

## Configuración

No se requiere configuración adicional. El módulo está completamente integrado con:

- Spring Boot
- Spring Data JPA
- MySQL
- Jakarta Validation

## Seguridad

**Recomendaciones:**

1. Implementar autenticación JWT para proteger los endpoints
2. Validar que el usuario solo pueda acceder a sus propias notificaciones
3. Implementar rate limiting para prevenir spam
4. Sanitizar mensajes para prevenir XSS

## Pruebas en Postman

### Configuración Inicial

1. **Crear una colección** llamada "EBSA Nexus - Notifications"
2. **URL Base**: `http://localhost:8080/api/v1/notifications`
3. **Headers comunes** (en Collection):
   ```
   Content-Type: application/json
   Accept: application/json
   ```

### Prueba 1: Obtener Resumen de Notificaciones (Login)

**Endpoint más importante para el login del frontend**

```http
GET http://localhost:8080/api/v1/notifications/user/1/summary
```

**Resultado esperado** (200 OK):

```json
{
  "allNotifications": [
    {
      "id": 5,
      "userId": 1,
      "noveltyId": null,
      "type": "SYSTEM",
      "title": "Sistema Actualizado",
      "message": "El sistema EBSA Nexus ha sido actualizado exitosamente a la versión 2.0. Nuevas funcionalidades disponibles.",
      "priority": "MEDIUM",
      "isRead": true,
      "readAt": "2025-10-26T02:18:45.000+00:00",
      "createdAt": "2025-10-25T02:18:45.000+00:00"
    }
  ],
  "unreadCount": 0,
  "recentNotifications": [...]
}
```

### Prueba 2: Crear Nueva Notificación

```http
POST http://localhost:8080/api/v1/notifications
Content-Type: application/json

{
  "userId": 3,
  "noveltyId": 1,
  "type": "NOVELTY_ASSIGNED",
  "title": "Novedad Urgente Asignada",
  "message": "Se te ha asignado una novedad urgente en el sector norte. Requiere atención inmediata.",
  "priority": "HIGH"
}
```

**Resultado esperado** (200 OK):

```json
{
  "id": 9,
  "userId": 3,
  "noveltyId": 1,
  "type": "NOVELTY_ASSIGNED",
  "title": "Novedad Urgente Asignada",
  "message": "Se te ha asignado una novedad urgente en el sector norte. Requiere atención inmediata.",
  "priority": "HIGH",
  "isRead": false,
  "readAt": null,
  "createdAt": "2025-10-26T02:30:00.000+00:00"
}
```

### Prueba 3: Obtener Notificaciones No Leídas

```http
GET http://localhost:8080/api/v1/notifications/user/3/unread
```

**Resultado esperado** (200 OK):

```json
[
  {
    "id": 1,
    "userId": 3,
    "noveltyId": 1,
    "type": "NOVELTY_ASSIGNED",
    "title": "Nueva Novedad Asignada",
    "message": "Se ha asignado una nueva novedad a tu cuadrilla Alpha...",
    "priority": "HIGH",
    "isRead": false,
    "readAt": null,
    "createdAt": "2025-10-26T02:18:45.000+00:00"
  },
  {
    "id": 9,
    "userId": 3,
    "noveltyId": 1,
    "type": "NOVELTY_ASSIGNED",
    "title": "Novedad Urgente Asignada",
    "message": "Se te ha asignado una novedad urgente...",
    "priority": "HIGH",
    "isRead": false,
    "readAt": null,
    "createdAt": "2025-10-26T02:30:00.000+00:00"
  }
]
```

### Prueba 4: Contar Notificaciones No Leídas

```http
GET http://localhost:8080/api/v1/notifications/user/3/unread/count
```

**Resultado esperado** (200 OK):

```json
2
```

### Prueba 5: Marcar Notificación como Leída

```http
PATCH http://localhost:8080/api/v1/notifications/1/read
```

**Resultado esperado** (200 OK):

```json
{
  "id": 1,
  "userId": 3,
  "noveltyId": 1,
  "type": "NOVELTY_ASSIGNED",
  "title": "Nueva Novedad Asignada",
  "message": "Se ha asignado una nueva novedad a tu cuadrilla Alpha...",
  "priority": "HIGH",
  "isRead": true,
  "readAt": "2025-10-26T02:35:00.000+00:00",
  "createdAt": "2025-10-26T02:18:45.000+00:00"
}
```

### Prueba 6: Obtener Todas las Notificaciones de un Usuario

```http
GET http://localhost:8080/api/v1/notifications/user/2
```

### Prueba 7: Obtener Notificaciones por Tipo

```http
GET http://localhost:8080/api/v1/notifications/user/3/type/NOVELTY_ASSIGNED
```

### Prueba 8: Obtener Notificaciones de una Novedad

```http
GET http://localhost:8080/api/v1/notifications/novelty/1
```

### Prueba 9: Marcar Todas como Leídas

```http
PATCH http://localhost:8080/api/v1/notifications/user/3/read-all
```

**Resultado esperado** (200 OK):

```json
{
  "message": "Todas las notificaciones marcadas como leídas",
  "updatedCount": 2
}
```

### Prueba 10: Eliminar una Notificación

```http
DELETE http://localhost:8080/api/v1/notifications/9
```

**Resultado esperado** (204 No Content)

### Prueba 11: Eliminar Todas las Notificaciones de un Usuario

```http
DELETE http://localhost:8080/api/v1/notifications/user/3
```

**Resultado esperado** (204 No Content)

### Flujo de Prueba Completo Sugerido

1. **Obtener resumen inicial** (Prueba 1) - Usuario ID: 3
2. **Contar no leídas** (Prueba 4) - Debe mostrar las notificaciones sin leer
3. **Crear nueva notificación** (Prueba 2) - Agregar una notificación de prueba
4. **Verificar no leídas nuevamente** (Prueba 4) - El contador debe aumentar
5. **Marcar una como leída** (Prueba 5) - Marcar la notificación ID: 1
6. **Verificar contador** (Prueba 4) - El contador debe disminuir
7. **Marcar todas como leídas** (Prueba 9) - Marcar todas
8. **Verificar contador** (Prueba 4) - Debe ser 0
9. **Obtener resumen final** (Prueba 1) - Verificar estado final

### Colección de Postman (JSON)

Puedes importar esta colección directamente en Postman:

```json
{
  "info": {
    "name": "EBSA Nexus - Notifications",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "1. Get User Notification Summary (Login)",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/api/v1/notifications/user/3/summary",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "v1", "notifications", "user", "3", "summary"]
        }
      }
    },
    {
      "name": "2. Create Notification",
      "request": {
        "method": "POST",
        "header": [{ "key": "Content-Type", "value": "application/json" }],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"userId\": 3,\n  \"noveltyId\": 1,\n  \"type\": \"NOVELTY_ASSIGNED\",\n  \"title\": \"Novedad Urgente Asignada\",\n  \"message\": \"Se te ha asignado una novedad urgente en el sector norte. Requiere atención inmediata.\",\n  \"priority\": \"HIGH\"\n}"
        },
        "url": {
          "raw": "http://localhost:8080/api/v1/notifications",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "v1", "notifications"]
        }
      }
    },
    {
      "name": "3. Get Unread Notifications",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/api/v1/notifications/user/3/unread",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "v1", "notifications", "user", "3", "unread"]
        }
      }
    },
    {
      "name": "4. Count Unread Notifications",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/api/v1/notifications/user/3/unread/count",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "v1", "notifications", "user", "3", "unread", "count"]
        }
      }
    },
    {
      "name": "5. Mark as Read",
      "request": {
        "method": "PATCH",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/api/v1/notifications/1/read",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "v1", "notifications", "1", "read"]
        }
      }
    },
    {
      "name": "6. Mark All as Read",
      "request": {
        "method": "PATCH",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/api/v1/notifications/user/3/read-all",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "v1", "notifications", "user", "3", "read-all"]
        }
      }
    }
  ]
}
```

### Tips para Probar

1. **Verifica que el backend esté corriendo**: `http://localhost:8080`
2. **Usa usuarios existentes en la BD**: IDs 1-8 según los datos iniciales
3. **Verifica los datos iniciales**: Revisa la tabla `notifications` en MySQL
4. **Prueba diferentes tipos**: SYSTEM, NOVELTY_ASSIGNED, NOVELTY_UPDATE, etc.
5. **Prueba diferentes prioridades**: HIGH, MEDIUM, LOW
6. **Monitorea los logs** del backend para ver las operaciones

### Errores Comunes

- **403 Forbidden**: El endpoint está protegido por Spring Security
  - **Solución**: Los endpoints de notificaciones están configurados como públicos para pruebas
  - **Si persiste**: Verifica que el backend se haya reiniciado después de la configuración
  - **Para producción**: Deberás agregar autenticación JWT en los headers
- **404 Not Found**: Usuario o notificación no existe
- **400 Bad Request**: Datos inválidos en el body
- **500 Internal Server Error**: Error en el servidor (revisar logs)

### Troubleshooting: Postman vs cURL

Si `curl` funciona pero Postman da error 500, revisa lo siguiente:

#### ✅ Configuración Correcta en Postman para POST

1. **Método HTTP**: Debe ser `POST` (no GET)

2. **URL**: `http://localhost:8080/api/v1/notifications`

   - Sin espacios al inicio o final
   - Sin barras diagonales extras

3. **Headers** (pestaña Headers):

   ```
   Content-Type: application/json
   Accept: application/json
   ```

4. **Body** (pestaña Body):

   - Selecciona: `raw`
   - En el dropdown de la derecha selecciona: `JSON`
   - Pega este JSON exacto:

   ```json
   {
     "userId": 1,
     "type": "INFO",
     "title": "Test notification",
     "message": "This is a test message"
   }
   ```

5. **NO uses**:
   - x-www-form-urlencoded
   - form-data
   - GraphQL
   - Ningún otro formato

#### ⚠️ Errores Comunes de Postman

1. **Error 500 con Postman pero cURL funciona**:

   - **Causa**: Headers incorrectos o formato de body incorrecto
   - **Solución**: Verifica que en Body esté seleccionado `raw` y `JSON`

2. **JSON no válido**:

   - **Causa**: Comillas dobles en lugar de comillas simples, o caracteres especiales
   - **Solución**: Usa el JSON exacto de arriba

3. **Campos faltantes**:
   - **Campos obligatorios**: `userId`, `type`, `title`, `message`
   - **Campos opcionales**: `noveltyId`

#### 🔧 Comando cURL que Funciona (para referencia)

```bash
curl -X POST http://localhost:8080/api/v1/notifications \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "type": "INFO",
    "title": "Test notification",
    "message": "This is a test message"
  }'
```

**Respuesta esperada (201 Created)**:

```json
{
  "id": 1,
  "userId": 1,
  "noveltyId": null,
  "type": "INFO",
  "title": "Test notification",
  "message": "This is a test message",
  "isRead": false,
  "createdAt": "2025-10-26T02:39:36.530538633"
}
```

#### 🎯 Captura de Pantalla de Postman (Configuración Correcta)

```
┌─────────────────────────────────────────────────────────────┐
│ POST ▼  http://localhost:8080/api/v1/notifications   [Send] │
├─────────────────────────────────────────────────────────────┤
│ Params  Authorization  Headers  Body  Pre-request  Tests    │
├─────────────────────────────────────────────────────────────┤
│ Headers (2)                                                  │
│ ✓ Content-Type          application/json                    │
│ ✓ Accept                application/json                    │
├─────────────────────────────────────────────────────────────┤
│ Body:  none  form-data  x-www-form-urlencoded  ●raw  binary │
│        JSON ▼                                                │
│ {                                                            │
│   "userId": 1,                                               │
│   "type": "INFO",                                            │
│   "title": "Test notification",                             │
│   "message": "This is a test message"                        │
│ }                                                            │
└─────────────────────────────────────────────────────────────┘
```

#### 📋 Checklist de Validación

Antes de enviar la petición desde Postman, verifica:

- [ ] Método es POST (no GET)
- [ ] URL es exactamente: `http://localhost:8080/api/v1/notifications`
- [ ] Header `Content-Type: application/json` está presente
- [ ] Body está en modo `raw` (no form-data)
- [ ] El dropdown del body dice `JSON` (no Text)
- [ ] El JSON es válido (usa un validador JSON si es necesario)
- [ ] Los campos `userId`, `type`, `title`, `message` están presentes
- [ ] El backend está corriendo (`docker ps` muestra ebsa-nexus-backend como healthy)

#### 🐛 Debug: Ver qué Está Enviando Postman

En Postman, después de enviar la petición:

1. Ve a la pestaña "Console" (abajo a la izquierda)
2. Busca la petición POST que hiciste
3. Expándela para ver los headers y el body exactos que se enviaron
4. Compáralos con el comando cURL que funciona

#### 💡 Alternativa: Importar desde cURL

Postman puede importar comandos cURL directamente:

1. En Postman, ve a `Import` (botón arriba a la izquierda)
2. Selecciona "Raw text"
3. Pega el comando cURL completo:
   ```bash
   curl -X POST http://localhost:8080/api/v1/notifications \
     -H "Content-Type: application/json" \
     -d '{"userId": 1, "type": "INFO", "title": "Test notification", "message": "This is a test message"}'
   ```
4. Click en "Continue" y luego "Import"
5. Postman creará la petición con la configuración correcta

## Próximas Mejoras

- [ ] WebSocket para notificaciones en tiempo real
- [ ] Soporte para notificaciones push
- [ ] Categorías personalizables de notificaciones
- [ ] Preferencias de notificación por usuario
- [ ] Plantillas de notificaciones
- [ ] Programación de notificaciones
- [ ] Notificaciones por email opcional

## Soporte

Para problemas o consultas, contactar al equipo de desarrollo de EBSA Nexus.
