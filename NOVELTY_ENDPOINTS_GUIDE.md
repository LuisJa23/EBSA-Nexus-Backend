# 📋 Guía de Endpoints de Novedades

## 🔐 Configuración de Seguridad

Todos los endpoints de novedades **requieren autenticación JWT** y roles específicos.

---

## 📍 Endpoints Disponibles

### 1️⃣ **Buscar Todas las Novedades (Con Filtros)**
```http
GET /api/v1/novelties/search
```

**Roles permitidos:** `SUPERVISOR`, `TRABAJADOR`, `LIDER_CUADRILLA`, `ADMIN`

**Parámetros de búsqueda (query params):**
- `status` - Estado de la novedad (PENDING, ASSIGNED, IN_PROGRESS, RESOLVED, VERIFIED, CANCELLED)
- `priority` - Prioridad (LOW, MEDIUM, HIGH, CRITICAL)
- `areaId` - ID del área
- `crewId` - ID de la cuadrilla asignada
- `creatorId` - ID del creador
- `startDate` - Fecha de inicio (formato: yyyy-MM-dd)
- `endDate` - Fecha de fin (formato: yyyy-MM-dd)
- `page` - Número de página (default: 0)
- `size` - Tamaño de página (default: 10)
- `sort` - Campo de ordenamiento (default: createdAt)
- `direction` - Dirección (ASC, DESC)

**Ejemplo con cURL:**
```bash
curl -X GET "http://localhost:8080/api/v1/novelties/search?page=0&size=10" \
  -H "Authorization: Bearer {tu-token-jwt}" \
  -H "Content-Type: application/json"
```

**Ejemplo con filtros:**
```bash
curl -X GET "http://localhost:8080/api/v1/novelties/search?status=PENDING&priority=HIGH&page=0&size=20" \
  -H "Authorization: Bearer {tu-token-jwt}" \
  -H "Content-Type: application/json"
```

**Respuesta:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Fuga de agua",
      "description": "Fuga detectada en tubería principal",
      "status": "PENDING",
      "priority": "HIGH",
      "area": {...},
      "creator": {...},
      "assignedCrew": null,
      "createdAt": "2025-10-26T10:00:00",
      "updatedAt": "2025-10-26T10:00:00"
    }
  ],
  "totalElements": 15,
  "totalPages": 2,
  "currentPage": 0,
  "size": 10
}
```

---

### 2️⃣ **Obtener Novedades por Cuadrilla**
```http
GET /api/v1/novelties/crew/{crewId}
```

**Roles permitidos:** `TRABAJADOR`, `LIDER_CUADRILLA`, `SUPERVISOR`, `ADMIN`

**Ejemplo:**
```bash
curl -X GET "http://localhost:8080/api/v1/novelties/crew/1" \
  -H "Authorization: Bearer {tu-token-jwt}" \
  -H "Content-Type: application/json"
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "title": "Reparación cable",
    "status": "ASSIGNED",
    "priority": "MEDIUM",
    "assignedCrewId": 1,
    "createdAt": "2025-10-26T10:00:00"
  },
  {
    "id": 2,
    "title": "Mantenimiento transformador",
    "status": "IN_PROGRESS",
    "priority": "HIGH",
    "assignedCrewId": 1,
    "createdAt": "2025-10-26T11:00:00"
  }
]
```

---

### 3️⃣ **Obtener Novedades por Estado**
```http
GET /api/v1/novelties/status/{status}
```

**Roles permitidos:** `SUPERVISOR`, `ADMIN`

**Estados válidos:**
- `PENDING` - Pendiente de asignación
- `ASSIGNED` - Asignada a cuadrilla
- `IN_PROGRESS` - En progreso
- `RESOLVED` - Resuelta
- `VERIFIED` - Verificada
- `CANCELLED` - Cancelada

**Ejemplo:**
```bash
curl -X GET "http://localhost:8080/api/v1/novelties/status/PENDING" \
  -H "Authorization: Bearer {tu-token-jwt}" \
  -H "Content-Type: application/json"
```

---

### 4️⃣ **Obtener Detalle de Novedad**
```http
GET /api/v1/novelties/{noveltyId}
```

**Roles permitidos:** `SUPERVISOR`, `TRABAJADOR`, `LIDER_CUADRILLA`, `ADMIN`

**Ejemplo:**
```bash
curl -X GET "http://localhost:8080/api/v1/novelties/1" \
  -H "Authorization: Bearer {tu-token-jwt}" \
  -H "Content-Type: application/json"
```

**Respuesta (detallada):**
```json
{
  "id": 1,
  "title": "Fuga de agua",
  "description": "Fuga detectada en tubería principal",
  "status": "ASSIGNED",
  "priority": "HIGH",
  "area": {
    "id": 1,
    "name": "Zona Norte"
  },
  "creator": {
    "id": 5,
    "name": "Juan Pérez",
    "role": "SUPERVISOR"
  },
  "assignedCrew": {
    "id": 1,
    "name": "Cuadrilla A"
  },
  "images": [
    {
      "id": 1,
      "url": "https://storage.example.com/novelties/1/image1.jpg",
      "uploadedAt": "2025-10-26T10:00:00"
    }
  ],
  "statusHistory": [
    {
      "status": "PENDING",
      "timestamp": "2025-10-26T10:00:00",
      "user": "Juan Pérez"
    },
    {
      "status": "ASSIGNED",
      "timestamp": "2025-10-26T10:30:00",
      "user": "Admin"
    }
  ],
  "createdAt": "2025-10-26T10:00:00",
  "updatedAt": "2025-10-26T10:30:00"
}
```

---

### 5️⃣ **Crear Novedad**
```http
POST /api/v1/novelties
```

**Roles permitidos:** `SUPERVISOR`, `ADMIN`

**Content-Type:** `multipart/form-data` o `application/json`

**Ejemplo con JSON:**
```bash
curl -X POST "http://localhost:8080/api/v1/novelties" \
  -H "Authorization: Bearer {tu-token-jwt}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Nueva fuga",
    "description": "Fuga en sector 3",
    "priority": "HIGH",
    "areaId": 1
  }'
```

---

### 6️⃣ **Asignar Cuadrilla a Novedad**
```http
POST /api/v1/novelties/{noveltyId}/assign
```

**Roles permitidos:** `ADMIN`

**Ejemplo:**
```bash
curl -X POST "http://localhost:8080/api/v1/novelties/1/assign" \
  -H "Authorization: Bearer {tu-token-jwt}" \
  -H "Content-Type: application/json" \
  -d '{
    "crewId": 1,
    "assignmentNotes": "Asignación urgente"
  }'
```

---

### 7️⃣ **Iniciar Trabajo en Novedad**
```http
PUT /api/v1/novelties/{noveltyId}/start
```

**Roles permitidos:** `TRABAJADOR`, `LIDER_CUADRILLA`

**Ejemplo:**
```bash
curl -X PUT "http://localhost:8080/api/v1/novelties/1/start" \
  -H "Authorization: Bearer {tu-token-jwt}" \
  -H "Content-Type: application/json"
```

---

### 8️⃣ **Marcar Novedad como Resuelta**
```http
PUT /api/v1/novelties/{noveltyId}/resolve
```

**Roles permitidos:** `TRABAJADOR`, `LIDER_CUADRILLA`

**Ejemplo:**
```bash
curl -X PUT "http://localhost:8080/api/v1/novelties/1/resolve" \
  -H "Authorization: Bearer {tu-token-jwt}" \
  -H "Content-Type: application/json" \
  -d '{
    "resolutionNotes": "Tubería reparada exitosamente"
  }'
```

---

### 9️⃣ **Verificar Resolución (Admin)**
```http
PUT /api/v1/novelties/{noveltyId}/verify
```

**Roles permitidos:** `ADMIN`

**Ejemplo:**
```bash
curl -X PUT "http://localhost:8080/api/v1/novelties/1/verify?approved=true&verificationNotes=Verificado+correctamente" \
  -H "Authorization: Bearer {tu-token-jwt}" \
  -H "Content-Type: application/json"
```

---

### 🔟 **Cancelar Novedad (Admin)**
```http
PUT /api/v1/novelties/{noveltyId}/cancel
```

**Roles permitidos:** `ADMIN`

**Ejemplo:**
```bash
curl -X PUT "http://localhost:8080/api/v1/novelties/1/cancel?cancellationReason=Duplicada" \
  -H "Authorization: Bearer {tu-token-jwt}" \
  -H "Content-Type: application/json"
```

---

## 🔑 Obtener Token JWT

Para usar cualquiera de estos endpoints, primero necesitas autenticarte:

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "tu_usuario",
    "password": "tu_contraseña"
  }'
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "username": "tu_usuario",
  "role": "ADMIN"
}
```

Usa el `token` en el header `Authorization: Bearer {token}` para todas las peticiones.

---

## 📱 Integración con Flutter

### Ejemplo de servicio en Dart:

```dart
class NoveltyService {
  final String baseUrl = 'http://localhost:8080/api/v1/novelties';
  final String token = 'tu-token-jwt';

  // Obtener todas las novedades
  Future<List<Novelty>> getAllNovelties({
    int page = 0,
    int size = 10,
    String? status,
  }) async {
    final uri = Uri.parse('$baseUrl/search').replace(queryParameters: {
      'page': page.toString(),
      'size': size.toString(),
      if (status != null) 'status': status,
    });

    final response = await http.get(
      uri,
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return (data['content'] as List)
          .map((json) => Novelty.fromJson(json))
          .toList();
    }
    throw Exception('Failed to load novelties');
  }

  // Obtener novedades por cuadrilla
  Future<List<Novelty>> getNoveltyByCrew(int crewId) async {
    final response = await http.get(
      Uri.parse('$baseUrl/crew/$crewId'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return (data as List)
          .map((json) => Novelty.fromJson(json))
          .toList();
    }
    throw Exception('Failed to load crew novelties');
  }
}
```

---

## ✅ Resumen

- ✅ **Todos los endpoints requieren autenticación JWT**
- ✅ **Cada endpoint tiene roles específicos** definidos con `@PreAuthorize`
- ✅ **Para obtener TODAS las novedades**: usa `/api/v1/novelties/search`
- ✅ **Para filtrar por cuadrilla**: usa `/api/v1/novelties/crew/{crewId}`
- ✅ **Para filtrar por estado**: usa `/api/v1/novelties/status/{status}`
- ✅ **La configuración de seguridad es correcta** - no necesita cambios adicionales

---

## 🐛 Troubleshooting

### Error 401 Unauthorized
- ❌ Token no incluido o inválido
- ✅ Verifica que el header `Authorization: Bearer {token}` esté presente

### Error 403 Forbidden
- ❌ Usuario no tiene el rol necesario
- ✅ Verifica que tu usuario tenga el rol correcto para el endpoint

### Error 404 Not Found
- ❌ Endpoint incorrecto
- ✅ Verifica la URL: `/api/v1/novelties/...`

---

**Última actualización:** 26 de octubre de 2025
