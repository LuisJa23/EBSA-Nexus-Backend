# 🧪 Analytics API - Ejemplos de Prueba y Casos de Uso

## 📋 Índice

1. [Configuración Inicial](#configuración-inicial)
2. [Casos de Uso Comunes](#casos-de-uso-comunes)
3. [Ejemplos con cURL](#ejemplos-con-curl)
4. [Ejemplos con JavaScript/Fetch](#ejemplos-con-javascriptfetch)
5. [Ejemplos de Respuestas](#ejemplos-de-respuestas)

---

## Configuración Inicial

### 1. Obtener Token de Autenticación

```bash
# Login como Admin
curl -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Response:**

```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "role": "ADMIN"
    }
  }
}
```

### 2. Guardar el Token

```bash
export TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## Casos de Uso Comunes

### 📊 Caso 1: Dashboard del Administrador

**Objetivo**: Ver estadísticas generales del último mes

```bash
# Obtener dashboard completo de los últimos 30 días
START_DATE=$(date -u -v-30d +"%Y-%m-%dT00:00:00")
END_DATE=$(date -u +"%Y-%m-%dT23:59:59")

curl -X GET "http://localhost:8080/api/v1/analytics/dashboard?startDate=${START_DATE}&endDate=${END_DATE}" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

### 📈 Caso 2: Análisis de Tendencias Mensuales

**Objetivo**: Ver evolución de novedades por mes del año actual

```bash
# Tendencias mensuales del 2024
curl -X GET "http://localhost:8080/api/v1/analytics/novelties/trends?period=monthly&startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

### 🏆 Caso 3: Ranking de Mejores Cuadrillas

**Objetivo**: Identificar las 5 cuadrillas más eficientes

```bash
# Top 5 cuadrillas por tasa de completitud
curl -X GET "http://localhost:8080/api/v1/analytics/top-performers?type=crews&limit=5&sortBy=completionRate" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

### 👷 Caso 4: Evaluación de Desempeño Individual

**Objetivo**: Ver desempeño de un técnico específico

```bash
# Desempeño del usuario ID 10
curl -X GET "http://localhost:8080/api/v1/analytics/users/performance?userId=10" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

### 🗺️ Caso 5: Análisis Geográfico

**Objetivo**: Identificar municipios con más novedades pendientes

```bash
# Distribución por municipio
curl -X GET "http://localhost:8080/api/v1/analytics/novelties/by-municipality" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

### 📊 Caso 6: Reporte por Área

**Objetivo**: Estadísticas del área de Facturación

```bash
# Área FACTURACION (areaId = 1)
curl -X GET "http://localhost:8080/api/v1/analytics/novelties/overview?areaId=1" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

### 🔍 Caso 7: Auditoría de Cuadrilla Específica

**Objetivo**: Desempeño detallado de una cuadrilla

```bash
# Desempeño de la cuadrilla ID 3
curl -X GET "http://localhost:8080/api/v1/analytics/crews/performance?crewId=3" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

### 📅 Caso 8: Comparación Semanal

**Objetivo**: Ver tendencia semanal del último mes

```bash
# Tendencias semanales
START_DATE=$(date -u -v-30d +"%Y-%m-%dT00:00:00")
END_DATE=$(date -u +"%Y-%m-%dT23:59:59")

curl -X GET "http://localhost:8080/api/v1/analytics/novelties/trends?period=weekly&startDate=${START_DATE}&endDate=${END_DATE}" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## Ejemplos con cURL

### 1. Overview con Filtros Completos

```bash
curl -X GET "http://localhost:8080/api/v1/analytics/novelties/overview?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59&areaId=1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Accept: application/json"
```

### 2. Tendencias Diarias (Última Semana)

```bash
curl -X GET "http://localhost:8080/api/v1/analytics/novelties/trends?period=daily&startDate=2024-10-25T00:00:00&endDate=2024-11-01T23:59:59" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 3. Top 10 Usuarios por Tiempo Promedio

```bash
curl -X GET "http://localhost:8080/api/v1/analytics/top-performers?type=users&limit=10&sortBy=averageTime" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 4. Usuarios por Rol de Trabajo

```bash
# workRoleId = 1 (ejemplo: Técnico Eléctrico)
curl -X GET "http://localhost:8080/api/v1/analytics/users/performance?workRoleId=1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 5. Novedades Completadas por Municipio

```bash
curl -X GET "http://localhost:8080/api/v1/analytics/novelties/by-municipality?status=COMPLETADA" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## Ejemplos con JavaScript/Fetch

### Setup Base

```javascript
const API_BASE_URL = "http://localhost:8080/api/v1";
const token = localStorage.getItem("authToken");

const headers = {
  Authorization: `Bearer ${token}`,
  "Content-Type": "application/json",
};
```

### 1. Dashboard Completo

```javascript
async function fetchDashboard() {
  try {
    const response = await fetch(`${API_BASE_URL}/analytics/dashboard`, {
      method: "GET",
      headers: headers,
    });

    const result = await response.json();

    if (result.success) {
      console.log("Dashboard Data:", result.data);
      return result.data;
    }
  } catch (error) {
    console.error("Error fetching dashboard:", error);
  }
}
```

### 2. Estadísticas con Filtros

```javascript
async function fetchNoveltyStats(startDate, endDate, areaId = null) {
  const params = new URLSearchParams({
    startDate: startDate.toISOString(),
    endDate: endDate.toISOString(),
  });

  if (areaId) params.append("areaId", areaId);

  try {
    const response = await fetch(
      `${API_BASE_URL}/analytics/novelties/overview?${params}`,
      { method: "GET", headers: headers }
    );

    const result = await response.json();
    return result.data;
  } catch (error) {
    console.error("Error:", error);
  }
}

// Uso
const lastMonth = new Date();
lastMonth.setMonth(lastMonth.getMonth() - 1);
const stats = await fetchNoveltyStats(lastMonth, new Date(), 1);
```

### 3. Tendencias con Chart.js

```javascript
async function loadTrendsChart() {
  const response = await fetch(
    `${API_BASE_URL}/analytics/novelties/trends?period=monthly`,
    { method: "GET", headers: headers }
  );

  const result = await response.json();
  const trends = result.data.trends;

  // Preparar datos para Chart.js
  const chartData = {
    labels: trends.map((t) => t.period),
    datasets: [
      {
        label: "Creadas",
        data: trends.map((t) => t.created),
        borderColor: "rgb(75, 192, 192)",
        tension: 0.1,
      },
      {
        label: "Completadas",
        data: trends.map((t) => t.completed),
        borderColor: "rgb(54, 162, 235)",
        tension: 0.1,
      },
      {
        label: "Canceladas",
        data: trends.map((t) => t.cancelled),
        borderColor: "rgb(255, 99, 132)",
        tension: 0.1,
      },
    ],
  };

  // Crear gráfico
  new Chart(document.getElementById("trendsChart"), {
    type: "line",
    data: chartData,
  });
}
```

### 4. Top Performers con React

```jsx
import React, { useEffect, useState } from "react";

function TopPerformers({ type = "users", limit = 5 }) {
  const [performers, setPerformers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchTopPerformers = async () => {
      try {
        const response = await fetch(
          `${API_BASE_URL}/analytics/top-performers?type=${type}&limit=${limit}`,
          { method: "GET", headers }
        );

        const result = await response.json();
        if (result.success) {
          setPerformers(result.data.topPerformers);
        }
      } catch (error) {
        console.error("Error:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchTopPerformers();
  }, [type, limit]);

  if (loading) return <div>Cargando...</div>;

  return (
    <div className="top-performers">
      <h2>
        Top {limit} {type === "users" ? "Usuarios" : "Cuadrillas"}
      </h2>
      <table>
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Completadas</th>
            <th>Tasa de Completitud</th>
            <th>Tiempo Promedio (hrs)</th>
          </tr>
        </thead>
        <tbody>
          {performers.map((p) => (
            <tr key={p.id}>
              <td>{p.name}</td>
              <td>{p.completedNovelties}</td>
              <td>{p.completionRate.toFixed(2)}%</td>
              <td>{p.averageResolutionTimeHours.toFixed(1)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default TopPerformers;
```

### 5. Mapa de Distribución Geográfica

```javascript
async function loadMunicipalityMap() {
  const response = await fetch(
    `${API_BASE_URL}/analytics/novelties/by-municipality`,
    { method: "GET", headers: headers }
  );

  const result = await response.json();
  const data = result.data;

  // Crear mapa de calor
  const mapData = data.map((m) => ({
    name: m.municipality,
    value: m.totalNovelties,
    pending: m.pending,
    completed: m.completed,
  }));

  // Usar librería de mapas (ej: Google Maps, Leaflet)
  mapData.forEach((municipality) => {
    addMarkerToMap(municipality);
  });
}
```

---

## Ejemplos de Respuestas

### Dashboard Completo

```json
{
  "success": true,
  "data": {
    "overview": {
      "totalNovelties": 350,
      "byStatus": {
        "CREADA": 45,
        "EN_CURSO": 80,
        "COMPLETADA": 200,
        "CERRADA": 20,
        "CANCELADA": 5
      },
      "byArea": {
        "FACTURACION": 150,
        "CARTERA": 120,
        "PERDIDAS": 80
      },
      "byReason": {
        "ERROR_LECTURA": 180,
        "ACTUALIZACION_DATOS": 140,
        "OTROS": 30
      },
      "averageResolutionTimeHours": 26.5,
      "resolvedNovelties": 200,
      "pendingNovelties": 125
    },
    "trends": {
      "period": "monthly",
      "trends": [
        {
          "period": "2024-07",
          "created": 55,
          "completed": 50,
          "cancelled": 2
        },
        {
          "period": "2024-08",
          "created": 62,
          "completed": 58,
          "cancelled": 1
        },
        {
          "period": "2024-09",
          "created": 58,
          "completed": 55,
          "cancelled": 0
        },
        {
          "period": "2024-10",
          "created": 65,
          "completed": 60,
          "cancelled": 2
        }
      ]
    },
    "topCrews": [
      {
        "crewId": 3,
        "crewName": "Cuadrilla Alpha",
        "assignedNovelties": 85,
        "completedNovelties": 80,
        "pendingNovelties": 5,
        "averageResolutionTimeHours": 18.2,
        "completionRate": 94.12,
        "memberCount": 5
      },
      {
        "crewId": 1,
        "crewName": "Cuadrilla Beta",
        "assignedNovelties": 75,
        "completedNovelties": 68,
        "pendingNovelties": 7,
        "averageResolutionTimeHours": 22.5,
        "completionRate": 90.67,
        "memberCount": 4
      }
    ],
    "topUsers": [
      {
        "userId": 15,
        "fullName": "María García López",
        "workRole": "Técnico Senior",
        "noveltiesCreated": 40,
        "noveltiesCompleted": 35,
        "reportsGenerated": 35,
        "participationsInReports": 42,
        "averageResolutionTimeHours": 16.8
      },
      {
        "userId": 22,
        "fullName": "Carlos Rodríguez Pérez",
        "workRole": "Técnico Eléctrico",
        "noveltiesCreated": 38,
        "noveltiesCompleted": 32,
        "reportsGenerated": 32,
        "participationsInReports": 38,
        "averageResolutionTimeHours": 19.2
      }
    ],
    "byMunicipality": [
      {
        "municipality": "Tunja",
        "totalNovelties": 180,
        "completed": 150,
        "pending": 30
      },
      {
        "municipality": "Duitama",
        "totalNovelties": 95,
        "completed": 80,
        "pending": 15
      },
      {
        "municipality": "Sogamoso",
        "totalNovelties": 75,
        "completed": 60,
        "pending": 15
      }
    ]
  }
}
```

---

## 🔧 Utilidades Helper

### Formatear Fechas

```javascript
// Últimos N días
function getLastNDays(n) {
  const endDate = new Date();
  const startDate = new Date();
  startDate.setDate(startDate.getDate() - n);

  return {
    startDate: startDate.toISOString(),
    endDate: endDate.toISOString(),
  };
}

// Mes actual
function getCurrentMonth() {
  const now = new Date();
  const startDate = new Date(now.getFullYear(), now.getMonth(), 1);
  const endDate = new Date(
    now.getFullYear(),
    now.getMonth() + 1,
    0,
    23,
    59,
    59
  );

  return {
    startDate: startDate.toISOString(),
    endDate: endDate.toISOString(),
  };
}

// Año actual
function getCurrentYear() {
  const now = new Date();
  const startDate = new Date(now.getFullYear(), 0, 1);
  const endDate = new Date(now.getFullYear(), 11, 31, 23, 59, 59);

  return {
    startDate: startDate.toISOString(),
    endDate: endDate.toISOString(),
  };
}
```

### Error Handling

```javascript
async function fetchWithErrorHandling(url) {
  try {
    const response = await fetch(url, {
      method: "GET",
      headers: headers,
    });

    if (!response.ok) {
      if (response.status === 401) {
        // Token expirado
        console.error("Token expirado. Redirigir a login.");
        window.location.href = "/login";
        return null;
      }

      if (response.status === 403) {
        console.error("Sin permisos para acceder a este recurso.");
        return null;
      }

      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const result = await response.json();

    if (!result.success) {
      console.error("Error en respuesta:", result.message);
      return null;
    }

    return result.data;
  } catch (error) {
    console.error("Error en la petición:", error);
    return null;
  }
}
```

---

## 📊 Integración con Gráficos

### Ejemplo con ApexCharts

```javascript
import ApexCharts from "apexcharts";

async function renderPerformanceChart() {
  const data = await fetch(`${API_BASE_URL}/analytics/crews/performance`, {
    headers,
  }).then((r) => r.json());

  const crews = data.data;

  const options = {
    series: [
      {
        name: "Tasa de Completitud",
        data: crews.map((c) => c.completionRate.toFixed(2)),
      },
    ],
    chart: {
      type: "bar",
      height: 350,
    },
    xaxis: {
      categories: crews.map((c) => c.crewName),
    },
    yaxis: {
      title: { text: "Porcentaje (%)" },
    },
  };

  const chart = new ApexCharts(document.querySelector("#chart"), options);
  chart.render();
}
```

---

**Nota**: Todos estos ejemplos asumen que el servidor está corriendo en `localhost:8080`. Ajustar según tu configuración.
