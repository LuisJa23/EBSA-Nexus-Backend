# EBSA-Nexus-Backend

Sistema de gestión de novedades para EBSA con módulo completo de Analytics y Estadísticas.

## ✨ Características Principales

- 🔐 Autenticación y Autorización con JWT
- 📋 Gestión de Novedades y Asignaciones
- 👥 Gestión de Cuadrillas y Miembros
- 📊 **Analytics y Estadísticas Completas** (Nuevo)
- 🔔 Sistema de Notificaciones
- 📝 Generación de Reportes
- 🗄️ Arquitectura Hexagonal/Clean Architecture

## 📊 Módulo de Analytics (Nuevo)

El sistema incluye un módulo completo de analytics con 7 endpoints:

1. **Dashboard Consolidado** - Todas las métricas en un solo lugar
2. **Estadísticas Generales** - Overview de novedades
3. **Tendencias Temporales** - Análisis por día/semana/mes
4. **Rendimiento de Cuadrillas** - Métricas por equipo
5. **Rendimiento de Usuarios** - Métricas individuales
6. **Distribución Geográfica** - Análisis por municipio
7. **Top Performers** - Rankings de mejor desempeño

### 🚀 Quick Start Analytics

```bash
# 1. Obtener token
curl -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 2. Ver dashboard
curl -X GET "http://localhost:8080/api/v1/analytics/dashboard" \
  -H "Authorization: Bearer {token}"
```

Ver guía completa: [ANALYTICS_QUICKSTART.md](ANALYTICS_QUICKSTART.md)

## 📁 Estructura del proyecto

EBSA-NEXUS-BACKEND/
│
├── src/
│ ├── main/
│ │ └── java/co/com/ebsa/ebsa_nexus/
│ │ ├── application/ # Capa de aplicación (casos de uso)
│ │ │ ├── dto/
│ │ │ │ ├── request/ # Objetos de entrada (CreateUserRequest, LoginRequestDTO)
│ │ │ │ └── response/ # Objetos de salida (UserResponse, ErrorResponse)
│ │ │ └── service/ # Servicios de aplicación (lógica de negocio específica)
│ │ │
│ │ ├── domain/ # Capa de dominio (núcleo)
│ │ │ ├── entity/ # Entidades empresariales (User, Role, Area, WorkRole)
│ │ │ ├── exception/ # Excepciones del dominio
│ │ │ └── repository/ # Interfaces de dominio (abstracción de persistencia)
│ │ │
│ │ ├── infrastructure/ # Capa de infraestructura
│ │ │ ├── config/ # Configuración (SecurityConfig, JWT)
│ │ │ ├── repository/ # Implementaciones JPA
│ │ │ ├── persistence/ # Adaptadores de datos concretos
│ │ │ └── utils/ # Utilitarios (JwtUtil, Mappers)
│ │ │
│ │ ├── presentation/ # Capa de presentación (entrada al sistema)
│ │ │ ├── controller/ # Controladores REST (AuthController, UserManagementController)
│ │ │ └── handler/ # Manejadores de excepciones globales
│ │ │
│ │ └── EbsaNexusApplication.java # Punto de entrada principal (Spring Boot)
│ │
│ └── resources/ # Archivos de configuración (application.yml, SQL, etc.)
│
├── test/ # Pruebas unitarias e integración
│ └── co/com/ebsa/ebsa_nexus/
│ └── application/service/ # Ejemplo: UserManagementServiceTest.java
│
├── docs/ # Documentación
│ ├── ANALYTICS_API_IMPLEMENTATION.md # Documentación completa de Analytics
│ ├── ANALYTICS_EXAMPLES.md # Ejemplos de código y casos de uso
│ └── ANALYTICS_SUMMARY.md # Resumen de implementación
│
├── postman/ # Colecciones de Postman
│ └── EBSA-Nexus-Analytics-API.postman_collection.json
│
├── docker/ # Configuración Docker
│ └── mysql/init/
│ └── 04-analytics-indexes.sql # Índices de optimización
│
├── pom.xml # Configuración de Maven
├── ANALYTICS_QUICKSTART.md # Guía de inicio rápido
└── README.md

## 🚀 Inicio Rápido

### Prerrequisitos

- Java 17+
- Maven 3.8+
- MySQL 8.0+
- Docker (opcional)

### Instalación

1. **Clonar el repositorio**

   ```bash
   git clone https://github.com/tu-org/EBSA-Nexus-Backend.git
   cd EBSA-Nexus-Backend
   ```

2. **Configurar base de datos**

   ```bash
   # Con Docker
   docker-compose up -d

   # O manualmente en MySQL
   mysql -u root -p < docker/mysql/init/01-schema.sql
   mysql -u root -p < docker/mysql/init/02-data.sql
   mysql -u root -p < docker/mysql/init/04-analytics-indexes.sql
   ```

3. **Configurar application.properties**

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/mydb
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

4. **Compilar y ejecutar**
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

La aplicación estará disponible en: `http://localhost:8080`

## 📖 Documentación

### General

- [README.md](README.md) - Este archivo
- [ANALYTICS_QUICKSTART.md](ANALYTICS_QUICKSTART.md) - Guía rápida de Analytics

### Analytics API

- [docs/ANALYTICS_API_IMPLEMENTATION.md](docs/ANALYTICS_API_IMPLEMENTATION.md) - Documentación técnica completa
- [docs/ANALYTICS_EXAMPLES.md](docs/ANALYTICS_EXAMPLES.md) - Ejemplos prácticos y código
- [docs/ANALYTICS_SUMMARY.md](docs/ANALYTICS_SUMMARY.md) - Resumen ejecutivo

### Testing

- [postman/](postman/) - Colecciones de Postman para testing

## 🔗 Endpoints Principales

### Autenticación

```
POST /api/v1/auth/login          # Login de usuario
POST /api/v1/auth/register       # Registro de usuario
```

### Novedades

```
GET    /api/v1/novelties         # Listar novedades
POST   /api/v1/novelties         # Crear novedad
GET    /api/v1/novelties/{id}    # Ver novedad
PUT    /api/v1/novelties/{id}    # Actualizar novedad
DELETE /api/v1/novelties/{id}    # Eliminar novedad
```

### Analytics (Nuevo) ⭐

```
GET /api/v1/analytics/dashboard                    # Dashboard completo
GET /api/v1/analytics/novelties/overview           # Estadísticas generales
GET /api/v1/analytics/novelties/trends             # Tendencias temporales
GET /api/v1/analytics/crews/performance            # Rendimiento cuadrillas
GET /api/v1/analytics/users/performance            # Rendimiento usuarios
GET /api/v1/analytics/novelties/by-municipality    # Distribución geográfica
GET /api/v1/analytics/top-performers               # Top performers
```

Ver documentación completa de endpoints en: [docs/ANALYTICS_API_IMPLEMENTATION.md](docs/ANALYTICS_API_IMPLEMENTATION.md)

## 🧪 Testing

### Con Postman

1. Importar colección: `postman/EBSA-Nexus-Analytics-API.postman_collection.json`
2. Configurar variable `token` después del login
3. Ejecutar requests

### Con cURL

```bash
# Login
TOKEN=$(curl -s -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}' \
  | jq -r '.data.token')

# Probar dashboard
curl -X GET "http://localhost:8080/api/v1/analytics/dashboard" \
  -H "Authorization: Bearer ${TOKEN}"
```

## 🛠️ Tecnologías

- **Framework**: Spring Boot 3.x
- **Base de Datos**: MySQL 8.0
- **Seguridad**: Spring Security + JWT
- **ORM**: Spring Data JPA / Hibernate
- **Build**: Maven
- **Java**: 17+
- **Arquitectura**: Hexagonal/Clean Architecture

## 📊 Características del Módulo Analytics

### Métricas Calculadas

- ✅ Total de novedades
- ✅ Distribución por estado, área y motivo
- ✅ Tiempo promedio de resolución
- ✅ Tendencias temporales (diarias/semanales/mensuales)
- ✅ Rendimiento de cuadrillas y usuarios
- ✅ Distribución geográfica
- ✅ Rankings de desempeño

### Optimizaciones

- ✅ Queries SQL nativas optimizadas
- ✅ 8 índices nuevos para mejorar performance
- ✅ Transacciones de solo lectura
- ✅ Cálculos agregados en base de datos
- ✅ Cacheo de resultados (recomendado implementar)

## 🔐 Seguridad

- Autenticación JWT obligatoria
- Autorización basada en roles
- Solo ADMIN y AREA_MANAGER pueden acceder a analytics
- Rate limiting recomendado (implementar en producción)

## 🤝 Contribución

1. Fork el proyecto
2. Crear feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push al branch (`git push origin feature/AmazingFeature`)
5. Abrir Pull Request

## 📝 Licencia

Este proyecto está licenciado bajo [MIT License](LICENSE)

## 👥 Autores

- **EBSA Nexus Team** - Desarrollo inicial

## 📧 Soporte

Para preguntas o soporte:

- Email: support@ebsa-nexus.com
- Documentación: [docs/](docs/)
- Issues: GitHub Issues

---

**Última actualización**: Noviembre 2025  
**Versión**: 1.0.0  
**Estado**: ✅ Producción Ready
