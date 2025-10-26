# Configuración Visual de Postman - Paso a Paso

## 📸 Así Debe Verse Tu Postman

```
╔══════════════════════════════════════════════════════════════════════════╗
║  POSTMAN                                                          [─][□][×]║
╠══════════════════════════════════════════════════════════════════════════╣
║  File  Edit  View  Help                              [Import]  [Settings] ║
╠══════════════════════════════════════════════════════════════════════════╣
║  ┌─ Collections                                                           ║
║  │  📁 EBSA Nexus - Notifications API                                     ║
║  │    └─ 📄 1. CREATE - New Notification (START HERE)  <-- Seleccionado  ║
║  │    └─ 📄 2. CREATE - Notification with NoveltyId                       ║
║  │    └─ 📄 3. GET - User Notifications Summary                           ║
║  └────────────────────────────────────────────────────────────────────   ║
╠══════════════════════════════════════════════════════════════════════════╣
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐  ║
║  │ [POST ▼]  http://localhost:8080/api/v1/notifications      [Send ►]│  ║
║  └────────────────────────────────────────────────────────────────────┘  ║
║                                                                            ║
║  ┌─ Tabs ──────────────────────────────────────────────────────────────┐ ║
║  │  Params  Authorization  [Headers]  Body  Pre-request  Tests  Cookies│ ║
║  └────────────────────────────────────────────────────────────────────┘  ║
║                                                                            ║
║  🔵 Headers (2)                                          [Bulk Edit]       ║
║  ┌────────────────────────────────────────────────────────────────────┐  ║
║  │  ✓  Content-Type        application/json                           │  ║
║  │  ✓  Accept              application/json                           │  ║
║  └────────────────────────────────────────────────────────────────────┘  ║
║                                                                            ║
╚══════════════════════════════════════════════════════════════════════════╝
```

## 📸 Pestaña Body - Configuración Correcta

```
╔══════════════════════════════════════════════════════════════════════════╗
║  ┌─ Tabs ──────────────────────────────────────────────────────────────┐ ║
║  │  Params  Authorization  Headers  [Body]  Pre-request  Tests  Cookies│ ║
║  └────────────────────────────────────────────────────────────────────┘  ║
║                                                                            ║
║  Body:                                                                     ║
║    ○ none   ○ form-data   ○ x-www-form-urlencoded                        ║
║    ● raw    ○ binary                                    [JSON ▼]  <--     ║
║                                                          ^^^^^^^^^         ║
║                                                          IMPORTANTE!       ║
║  ┌────────────────────────────────────────────────────────────────────┐  ║
║  │  1   {                                                             │  ║
║  │  2     "userId": 1,                                                │  ║
║  │  3     "type": "INFO",                                             │  ║
║  │  4     "title": "Test notification",                               │  ║
║  │  5     "message": "This is a test message"                         │  ║
║  │  6   }                                                             │  ║
║  │  7                                                                 │  ║
║  └────────────────────────────────────────────────────────────────────┘  ║
║                                                                            ║
║                                            [Send ►]                        ║
╚══════════════════════════════════════════════════════════════════════════╝
```

## ✅ Respuesta Exitosa (201 Created)

```
╔══════════════════════════════════════════════════════════════════════════╗
║  Response                                                                  ║
║  ┌─ Status ─────────────────────────────────────────────────────────────┐║
║  │  Status: 201 Created  ✓            Time: 245 ms     Size: 172 bytes  │║
║  └────────────────────────────────────────────────────────────────────┘ ║
║                                                                            ║
║  Body   Cookies   Headers   Test Results                                  ║
║  [Pretty ▼]  [JSON ▼]                                                     ║
║  ┌────────────────────────────────────────────────────────────────────┐  ║
║  │  {                                                                 │  ║
║  │    "id": 1,                                                        │  ║
║  │    "userId": 1,                                                    │  ║
║  │    "noveltyId": null,                                              │  ║
║  │    "type": "INFO",                                                 │  ║
║  │    "title": "Test notification",                                   │  ║
║  │    "message": "This is a test message",                            │  ║
║  │    "isRead": false,                                                │  ║
║  │    "createdAt": "2025-10-26T02:39:36.530538633"                    │  ║
║  │  }                                                                 │  ║
║  └────────────────────────────────────────────────────────────────────┘  ║
╚══════════════════════════════════════════════════════════════════════════╝
```

---

## ❌ Configuración INCORRECTA (NO hagas esto)

### ❌ Error #1: Body en form-data

```
╔══════════════════════════════════════════════════════════════════════════╗
║  Body:                                                                     ║
║    ○ none   ● form-data   ○ x-www-form-urlencoded   ❌ INCORRECTO        ║
║    ○ raw    ○ binary                                                      ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐  ║
║  │  KEY              VALUE                       DESCRIPTION           │  ║
║  │  userId           1                                                 │  ║
║  │  type             INFO                                              │  ║
║  └────────────────────────────────────────────────────────────────────┘  ║
║                                                                            ║
║  ⚠️ ESTE ES EL ERROR MÁS COMÚN - NO uses form-data!                      ║
╚══════════════════════════════════════════════════════════════════════════╝
```

### ❌ Error #2: Raw pero con "Text" en lugar de "JSON"

```
╔══════════════════════════════════════════════════════════════════════════╗
║  Body:                                                                     ║
║    ○ none   ○ form-data   ○ x-www-form-urlencoded                        ║
║    ● raw    ○ binary                                    [Text ▼]  ❌      ║
║                                                                            ║
║  ┌────────────────────────────────────────────────────────────────────┐  ║
║  │  {                                                                 │  ║
║  │    "userId": 1,                                                    │  ║
║  │    "type": "INFO"                                                  │  ║
║  │  }                                                                 │  ║
║  └────────────────────────────────────────────────────────────────────┘  ║
║                                                                            ║
║  ⚠️ Debe decir "JSON" no "Text"!                                          ║
╚══════════════════════════════════════════════════════════════════════════╝
```

### ❌ Error #3: JSON mal formateado

```
╔══════════════════════════════════════════════════════════════════════════╗
║  Body: raw  [JSON ▼]                                                      ║
║  ┌────────────────────────────────────────────────────────────────────┐  ║
║  │  {                                                                 │  ║
║  │    'userId': 1,              ❌ Comillas simples                   │  ║
║  │    "type": INFO,             ❌ Sin comillas en valor              │  ║
║  │    "title": Test             ❌ Sin comillas                       │  ║
║  │    message: "test"           ❌ Sin comillas en key                │  ║
║  │  }                                                                 │  ║
║  └────────────────────────────────────────────────────────────────────┘  ║
║                                                                            ║
║  ⚠️ JSON inválido - usa comillas dobles en todo!                          ║
╚══════════════════════════════════════════════════════════════════════════╝
```

---

## 🎯 Checklist Visual Rápido

Antes de hacer click en "Send", tu Postman debe verse así:

```
┌─────────────────────────────────────────────────────────┐
│ [POST ▼]  http://localhost:8080/api/v1/notifications   │ ✓
├─────────────────────────────────────────────────────────┤
│ Headers:                                                │
│   ✓ Content-Type: application/json                     │ ✓
│   ✓ Accept: application/json                           │ ✓
├─────────────────────────────────────────────────────────┤
│ Body:  ● raw   [JSON ▼]                                 │ ✓
│                                                         │
│   {                                                     │
│     "userId": 1,                                        │ ✓
│     "type": "INFO",                                     │ ✓
│     "title": "Test notification",                       │ ✓
│     "message": "This is a test message"                 │ ✓
│   }                                                     │
└─────────────────────────────────────────────────────────┘
```

Si tu configuración se ve **exactamente** así, debería funcionar.

---

## 🔍 Postman Console - Ver Qué Se Está Enviando

```
╔══════════════════════════════════════════════════════════════════════════╗
║  POSTMAN CONSOLE                                                [─][□][×] ║
╠══════════════════════════════════════════════════════════════════════════╣
║  ▶ POST http://localhost:8080/api/v1/notifications  201 Created  245 ms  ║
║    └─ Request Headers                                                     ║
║         Content-Type: application/json           ✓ Correcto              ║
║         Accept: application/json                 ✓ Correcto              ║
║         User-Agent: PostmanRuntime/7.32.3                                ║
║         Host: localhost:8080                                             ║
║                                                                           ║
║    └─ Request Body                                                        ║
║         {"userId":1,"type":"INFO","title":"Test notification"...}        ║
║         ✓ JSON válido                                                    ║
║                                                                           ║
║    └─ Response (201 Created)                                             ║
║         {"id":1,"userId":1,"noveltyId":null,"type":"INFO"...}            ║
║         ✓ Éxito                                                          ║
╚══════════════════════════════════════════════════════════════════════════╝
```

Para abrir la Console:

- Botón en la parte inferior izquierda de Postman
- O: View → Show Postman Console
- O: Atajo de teclado: `Cmd+Alt+C` (Mac) o `Ctrl+Alt+C` (Windows)

---

## 🚀 Acciones Rápidas

### Si ves esto en Postman:

```
Status: 201 Created ✓
```

**¡Perfecto! Todo funciona correctamente.**

### Si ves esto:

```
Status: 500 Internal Server Error
```

**Revisa la configuración:**

1. ¿Body en "raw" + "JSON"?
2. ¿Headers correctos?
3. ¿JSON válido?
4. Compara con la configuración correcta arriba ☝️

### Si ves esto:

```
Status: 400 Bad Request
```

**Problema en los datos:**

- Verifica que todos los campos obligatorios estén presentes
- Verifica que userId sea un número
- Verifica que no haya campos extra o mal escritos

---

## 📱 Importar Colección (Más Fácil)

En lugar de configurar manualmente:

1. En Postman: **Import** (arriba izquierda)
2. Arrastra: `EBSA_Nexus_Notifications.postman_collection.json`
3. Click: **Import**
4. Listo - Todo preconfigurado ✅

---

## 💡 Tip Pro

Después de importar la colección, puedes:

- Editar el userId en el body (cambia de 1 a 2, 3, etc.)
- Cambiar el type (INFO, WARNING, SYSTEM, etc.)
- Probar con diferentes títulos y mensajes
- Ver todos los endpoints disponibles en la colección

Todos los endpoints están preconfigurados y listos para usar.
