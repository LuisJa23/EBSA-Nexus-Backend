# EBSA-Nexus-Backend

Estructura del proyecto??? 
EBSA-NEXUS-BACKEND/
│
├── src/
│   ├── main/
│   │   └── java/co/com/ebsa/ebsa_nexus/
│   │       ├── application/                 # Capa de aplicación (casos de uso)
│   │       │   ├── dto/
│   │       │   │   ├── request/             # Objetos de entrada (CreateUserRequest, LoginRequestDTO)
│   │       │   │   └── response/            # Objetos de salida (UserResponse, ErrorResponse)
│   │       │   └── service/                 # Servicios de aplicación (lógica de negocio específica)
│   │       │
│   │       ├── domain/                      # Capa de dominio (núcleo)
│   │       │   ├── entity/                  # Entidades empresariales (User, Role, Area, WorkRole)
│   │       │   ├── exception/               # Excepciones del dominio
│   │       │   └── repository/              # Interfaces de dominio (abstracción de persistencia)
│   │       │
│   │       ├── infrastructure/              # Capa de infraestructura
│   │       │   ├── config/                  # Configuración (SecurityConfig, JWT)
│   │       │   ├── repository/              # Implementaciones JPA
│   │       │   ├── persistence/             # Adaptadores de datos concretos
│   │       │   └── utils/                   # Utilitarios (JwtUtil, Mappers)
│   │       │
│   │       ├── presentation/                # Capa de presentación (entrada al sistema)
│   │       │   ├── controller/              # Controladores REST (AuthController, UserManagementController)
│   │       │   └── handler/                 # Manejadores de excepciones globales
│   │       │
│   │       └── EbsaNexusApplication.java    # Punto de entrada principal (Spring Boot)
│   │
│   └── resources/                           # Archivos de configuración (application.yml, SQL, etc.)
│
├── test/                                    # Pruebas unitarias e integración
│   └── co/com/ebsa/ebsa_nexus/
│       └── application/service/             # Ejemplo: UserManagementServiceTest.java
│
├── pom.xml                                  # Configuración de Maven
└── README.md