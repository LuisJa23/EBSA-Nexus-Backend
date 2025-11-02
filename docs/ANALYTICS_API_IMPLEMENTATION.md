# 📊 Analytics & Statistics API - Implementación

## ✅ Estado de Implementación

Todos los endpoints de analytics requeridos han sido implementados exitosamente.

## 📁 Estructura de Archivos Creados

```
src/main/java/co/com/ebsa/ebsa_nexus/
├── application/
│   ├── dto/response/analytics/
│   │   ├── NoveltyOverviewResponse.java
│   │   ├── NoveltyTrendResponse.java
│   │   ├── CrewPerformanceResponse.java
│   │   ├── UserPerformanceResponse.java
│   │   ├── MunicipalityDistributionResponse.java
│   │   ├── TopPerformersResponse.java
│   │   └── AnalyticsDashboardResponse.java
│   └── service/analytics/
│       └── AnalyticsApplicationService.java
├── domain/
│   └── repository/
│       └── AnalyticsRepository.java
├── infrastructure/
│   └── persistence/adapter/
│       └── AnalyticsRepositoryAdapter.java
└── presentation/
    └── controller/
        └── AnalyticsController.java
```

## 🔗 Endpoints Implementados

### Base URL: `/api/v1/analytics`

---

### 1. **Estadísticas Generales de Novedades**

```
GET /api/v1/analytics/novelties/overview
```

**Query Parameters:**

- `startDate` (opcional): Fecha inicio en formato ISO 8601
- `endDate` (opcional): Fecha fin en formato ISO 8601
- `areaId` (opcional): Filtrar por área específica

**Ejemplo de Request:**

```bash
curl -X GET "http://localhost:8080/api/v1/analytics/novelties/overview?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59" \
  -H "Authorization: Bearer {token}"
```

**Response:**

```json
{
  "success": true,
  "data": {
    "totalNovelties": 150,
    "byStatus": {
      "CREADA": 10,
      "EN_CURSO": 25,
      "COMPLETADA": 100,
      "CERRADA": 10,
      "CANCELADA": 5
    },
    "byArea": {
      "FACTURACION": 60,
      "CARTERA": 50,
      "PERDIDAS": 40
    },
    "byReason": {
      "ERROR_LECTURA": 80,
      "ACTUALIZACION_DATOS": 50,
      "OTROS": 20
    },
    "averageResolutionTimeHours": 24.5,
    "resolvedNovelties": 100,
    "pendingNovelties": 35
  }
}
```

---

### 2. **Tendencia Temporal de Novedades**

```
GET /api/v1/analytics/novelties/trends
```

**Query Parameters:**

- `period`: `daily`, `weekly`, `monthly` (default: `monthly`)
- `startDate` (opcional): Fecha inicio
- `endDate` (opcional): Fecha fin
- `areaId` (opcional): Filtrar por área

**Ejemplo de Request:**

```bash
curl -X GET "http://localhost:8080/api/v1/analytics/novelties/trends?period=monthly" \
  -H "Authorization: Bearer {token}"
```

**Response:**

```json
{
  "success": true,
  "data": {
    "period": "monthly",
    "trends": [
      {
        "period": "2024-01",
        "created": 45,
        "completed": 40,
        "cancelled": 2
      },
      {
        "period": "2024-02",
        "created": 52,
        "completed": 48,
        "cancelled": 1
      }
    ]
  }
}
```

---

### 3. **Rendimiento de Cuadrillas**

```
GET /api/v1/analytics/crews/performance
```

**Query Parameters:**

- `startDate` (opcional)
- `endDate` (opcional)
- `crewId` (opcional): Para una cuadrilla específica

**Ejemplo de Request:**

```bash
curl -X GET "http://localhost:8080/api/v1/analytics/crews/performance" \
  -H "Authorization: Bearer {token}"
```

**Response:**

```json
{
  "success": true,
  "data": [
    {
      "crewId": 1,
      "crewName": "Cuadrilla Alpha",
      "assignedNovelties": 50,
      "completedNovelties": 45,
      "pendingNovelties": 5,
      "averageResolutionTimeHours": 18.5,
      "completionRate": 90.0,
      "memberCount": 4
    }
  ]
}
```

---

### 4. **Rendimiento Individual de Usuarios**

```
GET /api/v1/analytics/users/performance
```

**Query Parameters:**

- `startDate` (opcional)
- `endDate` (opcional)
- `userId` (opcional): Para un usuario específico
- `workRoleId` (opcional): Filtrar por rol de trabajo

**Ejemplo de Request:**

```bash
curl -X GET "http://localhost:8080/api/v1/analytics/users/performance?userId=10" \
  -H "Authorization: Bearer {token}"
```

**Response:**

```json
{
  "success": true,
  "data": [
    {
      "userId": 10,
      "fullName": "Juan Pérez",
      "workRole": "Técnico Eléctrico",
      "noveltiesCreated": 20,
      "noveltiesCompleted": 15,
      "reportsGenerated": 15,
      "participationsInReports": 18,
      "averageResolutionTimeHours": 20.5
    }
  ]
}
```

---

### 5. **Distribución Geográfica**

```
GET /api/v1/analytics/novelties/by-municipality
```

**Query Parameters:**

- `startDate` (opcional)
- `endDate` (opcional)
- `status` (opcional): Filtrar por estado

**Ejemplo de Request:**

```bash
curl -X GET "http://localhost:8080/api/v1/analytics/novelties/by-municipality" \
  -H "Authorization: Bearer {token}"
```

**Response:**

```json
{
  "success": true,
  "data": [
    {
      "municipality": "Tunja",
      "totalNovelties": 80,
      "completed": 70,
      "pending": 10
    },
    {
      "municipality": "Duitama",
      "totalNovelties": 50,
      "completed": 45,
      "pending": 5
    }
  ]
}
```

---

### 6. **Top Performers**

```
GET /api/v1/analytics/top-performers
```

**Query Parameters:**

- `type`: `users` o `crews` (default: `users`)
- `limit`: Cantidad de resultados (default: 10)
- `startDate` (opcional)
- `endDate` (opcional)
- `sortBy`: `completionRate`, `totalCompleted`, `averageTime` (default: `completionRate`)

**Ejemplo de Request:**

```bash
curl -X GET "http://localhost:8080/api/v1/analytics/top-performers?type=users&limit=5&sortBy=completionRate" \
  -H "Authorization: Bearer {token}"
```

**Response:**

```json
{
  "success": true,
  "data": {
    "type": "users",
    "topPerformers": [
      {
        "id": 11,
        "name": "María García",
        "completedNovelties": 22,
        "completionRate": 88.0,
        "averageResolutionTimeHours": 16.8
      }
    ]
  }
}
```

---

### 7. **Dashboard Consolidado**

```
GET /api/v1/analytics/dashboard
```

**Query Parameters:**

- `startDate` (opcional) - Si no se proporciona, usa últimos 6 meses
- `endDate` (opcional) - Si no se proporciona, usa fecha actual

**Ejemplo de Request:**

```bash
curl -X GET "http://localhost:8080/api/v1/analytics/dashboard" \
  -H "Authorization: Bearer {token}"
```

**Response:**

```json
{
  "success": true,
  "data": {
    "overview": {
      /* datos del endpoint 1 */
    },
    "trends": {
      /* datos del endpoint 2 (últimos 6 meses) */
    },
    "topCrews": [
      /* top 5 cuadrillas */
    ],
    "topUsers": [
      /* top 5 usuarios */
    ],
    "byMunicipality": [
      /* distribución geográfica */
    ]
  }
}
```

---

## 🔐 Seguridad

- ✅ Todos los endpoints requieren autenticación JWT
- ✅ Solo roles `ADMIN` y `AREA_MANAGER` tienen acceso
- ⚠️ Se recomienda implementar rate limiting (ej: 100 requests/minuto) a nivel de infraestructura

---

## 🛠️ Características Técnicas

### Arquitectura

- **Arquitectura Hexagonal/Clean Architecture**
- **Capa de Dominio**: Interfaces de repositorio (`AnalyticsRepository`)
- **Capa de Aplicación**: Lógica de negocio (`AnalyticsApplicationService`)
- **Capa de Infraestructura**: Implementación con JPA (`AnalyticsRepositoryAdapter`)
- **Capa de Presentación**: REST Controllers (`AnalyticsController`)

### Tecnologías Utilizadas

- Spring Boot
- Spring Data JPA
- EntityManager para queries nativas optimizadas
- Lombok para reducir boilerplate
- Transacciones read-only para mejor performance

### Optimizaciones

- ✅ Queries SQL nativas optimizadas
- ✅ Uso de índices en la base de datos
- ✅ Transacciones de solo lectura (`@Transactional(readOnly = true)`)
- ✅ Cálculos agregados en base de datos (no en memoria)
- ✅ StringBuilder para construcción dinámica de queries
- ✅ Mapeo eficiente de resultados con streams

---

## 🧪 Cómo Probar

### 1. Compilar el Proyecto

```bash
./mvnw clean compile
```

### 2. Ejecutar la Aplicación

```bash
./mvnw spring-boot:run
```

### 3. Obtener Token JWT

```bash
curl -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password"
  }'
```

### 4. Probar Endpoints

Usar el token obtenido en el header `Authorization: Bearer {token}`

**Ejemplo con Postman:**

1. Crear nueva request GET
2. URL: `http://localhost:8080/api/v1/analytics/dashboard`
3. Headers: `Authorization: Bearer {tu_token}`
4. Send

---

## 📊 Queries SQL Base

Las queries SQL implementadas incluyen:

### Tiempo Promedio de Resolución

```sql
SELECT AVG(TIMESTAMPDIFF(HOUR, created_at, completed_at))
FROM novelties
WHERE status = 'COMPLETADA' AND completed_at IS NOT NULL;
```

### Rendimiento por Cuadrilla

```sql
SELECT
  c.id, c.name,
  COUNT(DISTINCT na.novelty_id) as assigned,
  COUNT(DISTINCT CASE WHEN n.status = 'COMPLETADA' THEN n.id END) as completed
FROM crews c
LEFT JOIN novelty_assignments na ON c.id = na.assigned_crew_id
LEFT JOIN novelties n ON na.novelty_id = n.id
WHERE c.deleted_at IS NULL
GROUP BY c.id, c.name;
```

### Rendimiento Individual

```sql
SELECT
  u.id,
  CONCAT(u.first_name, ' ', u.last_name) as full_name,
  COUNT(DISTINCT CASE WHEN n.created_by = u.id THEN n.id END) as created,
  COUNT(DISTINCT nr.id) as reports_generated
FROM users u
LEFT JOIN novelties n ON u.id = n.created_by
LEFT JOIN novelty_reports nr ON u.id = nr.generated_by
WHERE u.active = 1
GROUP BY u.id;
```

---

## 🚀 Próximos Pasos

### Mejoras Recomendadas

1. ✅ Implementar caché con Redis para queries frecuentes
2. ✅ Agregar paginación a endpoints que retornan listas grandes
3. ✅ Implementar exportación de reportes (PDF, Excel)
4. ✅ Agregar más filtros avanzados
5. ✅ Implementar WebSockets para actualizaciones en tiempo real
6. ✅ Agregar tests unitarios e integración

### Rate Limiting (Recomendado)

Agregar en `application.properties`:

```properties
# Rate Limiting
spring.cloud.gateway.routes[0].filters[0]=RequestRateLimiter
spring.cloud.gateway.routes[0].filters[0].args.redis-rate-limiter.replenishRate=100
spring.cloud.gateway.routes[0].filters[0].args.redis-rate-limiter.burstCapacity=200
```

---

## 📝 Notas Importantes

1. **Fechas en ISO 8601**: Usar formato `2024-01-01T00:00:00`
2. **Time Zone**: Las fechas se manejan en UTC por defecto
3. **Performance**: Para rangos de fechas muy amplios, considerar paginación
4. **Caché**: Los datos se calculan en tiempo real, considerar implementar caché
5. **Permisos**: Solo ADMIN y AREA_MANAGER pueden acceder

---

## 🐛 Troubleshooting

### Error 403 Forbidden

- Verificar que el token JWT sea válido
- Confirmar que el usuario tenga rol ADMIN o AREA_MANAGER

### Response vacío

- Verificar que existan datos en el rango de fechas especificado
- Revisar logs del servidor para errores SQL

### Query lenta

- Verificar índices en la base de datos
- Reducir el rango de fechas
- Considerar implementar caché

---

## 📧 Contacto

Para dudas o soporte técnico, contactar al equipo de desarrollo EBSA Nexus.

---

**Versión**: 1.0  
**Última actualización**: Noviembre 2025  
**Autor**: EBSA Nexus Team
