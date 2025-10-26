# Guía Paso a Paso: Configurar Postman para Notificaciones

## ✅ Configuración Correcta - POST Crear Notificación

### Paso 1: Crear Nueva Petición

1. Abre Postman
2. Click en "New" o el botón "+"
3. Selecciona "HTTP Request"

### Paso 2: Configurar Método y URL

```
Método: POST
URL: http://localhost:8080/api/v1/notifications
```

**⚠️ IMPORTANTE**: NO pongas espacios antes o después de la URL

### Paso 3: Configurar Headers

Click en la pestaña **"Headers"** y agrega:

| KEY          | VALUE            |
| ------------ | ---------------- |
| Content-Type | application/json |
| Accept       | application/json |

### Paso 4: Configurar Body

1. Click en la pestaña **"Body"**
2. Selecciona el radio button **"raw"** (NO form-data, NO x-www-form-urlencoded)
3. En el dropdown a la derecha de "raw", selecciona **"JSON"** (NO Text)
4. Pega exactamente este JSON:

```json
{
  "userId": 1,
  "type": "INFO",
  "title": "Test notification",
  "message": "This is a test message"
}
```

### Paso 5: Enviar la Petición

1. Click en el botón azul **"Send"**
2. Espera la respuesta

### ✅ Respuesta Exitosa (Status: 201 Created)

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

---

## 🔍 Verificar Si Postman Está Configurado Correctamente

### Checklist Visual

Antes de hacer click en "Send", verifica que veas esto:

```
┌──────────────────────────────────────────────────────────────┐
│ [POST ▼] http://localhost:8080/api/v1/notifications  [Send] │
├──────────────────────────────────────────────────────────────┤
│ Params │ Authorization │ Headers │ Body │ Pre-req │ Tests   │
├──────────────────────────────────────────────────────────────┤
│                                                               │
│ Body:  ○ none  ○ form-data  ○ x-www-form-urlencoded          │
│        ● raw   ○ binary     [JSON ▼]                         │
│                                                               │
│   1  {                                                        │
│   2    "userId": 1,                                           │
│   3    "type": "INFO",                                        │
│   4    "title": "Test notification",                         │
│   5    "message": "This is a test message"                    │
│   6  }                                                        │
│                                                               │
└──────────────────────────────────────────────────────────────┘
```

---

## 🐛 Troubleshooting: Error 500 en Postman

### Problema Común #1: Body no está en formato JSON

**❌ INCORRECTO**:

- Body en "form-data"
- Body en "x-www-form-urlencoded"
- Body en "raw" pero con "Text" seleccionado

**✅ CORRECTO**:

- Body en "raw"
- Dropdown muestra "JSON"

### Problema Común #2: Headers Faltantes

Verifica en la pestaña "Headers" que veas:

```
Content-Type: application/json   [✓]
Accept: application/json          [✓]
```

Si no están, agrégalos manualmente.

### Problema Común #3: JSON Mal Formateado

**❌ INCORRECTO**:

```json
{
  'userId': 1,           // Comillas simples
  "type": INFO,          // Sin comillas en el valor
  "title": Test,         // Sin comillas
  message: "test"        // Sin comillas en la key
}
```

**✅ CORRECTO**:

```json
{
  "userId": 1,
  "type": "INFO",
  "title": "Test notification",
  "message": "This is a test message"
}
```

### Problema Común #4: Campos Faltantes

**Campos OBLIGATORIOS** (deben estar todos):

- `userId` (número, ej: 1)
- `type` (string, ej: "INFO")
- `title` (string, ej: "Test notification")
- `message` (string, ej: "This is a test message")

**Campos OPCIONALES**:

- `noveltyId` (número, puede ser null)

---

## 🎯 Importar Configuración Correcta desde cURL

Si sigues teniendo problemas, usa esta función de Postman:

### Método 1: Importar desde cURL

1. En Postman, click en **"Import"** (arriba a la izquierda)
2. Selecciona la pestaña **"Raw text"**
3. Pega este comando completo:

```bash
curl -X POST http://localhost:8080/api/v1/notifications -H "Content-Type: application/json" -d '{"userId": 1, "type": "INFO", "title": "Test notification", "message": "This is a test message"}'
```

4. Click en **"Continue"**
5. Click en **"Import"**
6. Postman creará automáticamente la petición con la configuración correcta

### Método 2: Usar Postman Console para Debug

1. En Postman, abre la **Console** (View → Show Postman Console, o botón abajo a la izquierda)
2. Envía tu petición
3. En la Console verás exactamente qué se envió
4. Busca estas secciones:
   - **Request Headers**: debe incluir `Content-Type: application/json`
   - **Request Body**: debe ser el JSON válido
   - **Response**: verás el error exacto del servidor

---

## 📱 Ejemplos Adicionales de Peticiones

### Ejemplo 1: Notificación con NoveltyId

```json
{
  "userId": 2,
  "noveltyId": 1,
  "type": "NOVELTY_ASSIGNED",
  "title": "Nueva Novedad Asignada",
  "message": "Se te ha asignado la novedad #1 para revisión"
}
```

### Ejemplo 2: Notificación de Sistema

```json
{
  "userId": 3,
  "type": "SYSTEM",
  "title": "Mantenimiento Programado",
  "message": "El sistema estará en mantenimiento el domingo de 2-4 AM"
}
```

### Ejemplo 3: Notificación de Advertencia

```json
{
  "userId": 1,
  "type": "WARNING",
  "title": "Acción Requerida",
  "message": "Tu contraseña expirará en 7 días. Por favor actualízala."
}
```

---

## 🧪 Probar que el Backend Funciona

### Test Rápido con cURL (desde Terminal)

```bash
curl -X POST http://localhost:8080/api/v1/notifications \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "type": "INFO",
    "title": "Test",
    "message": "Testing"
  }'
```

**Si esto funciona pero Postman no, el problema está 100% en la configuración de Postman.**

---

## 📊 Verificar en la Base de Datos

Después de crear una notificación (exitosa o no), verifica en la BD:

```bash
docker exec -it ebsa-nexus-db mysql -u ebsa_user -p'ebsa_password' mydb -e "SELECT * FROM notifications ORDER BY id DESC LIMIT 3;"
```

Si ves la notificación aquí, significa que el POST funcionó.

---

## 🆘 Si Nada Funciona

1. **Reinicia Postman** completamente
2. **Verifica que el backend esté corriendo**:

   ```bash
   docker ps
   ```

   Debe mostrar `ebsa-nexus-backend` con estado "healthy"

3. **Revisa los logs del backend**:

   ```bash
   docker logs ebsa-nexus-backend --tail 50
   ```

4. **Prueba con otro cliente HTTP**:

   - Insomnia
   - Thunder Client (extensión de VS Code)
   - O simplemente usa cURL desde la terminal

5. **Exporta la colección de Postman** y compártela para revisión

---

## 📞 Contacto

Si después de seguir esta guía el error persiste, reporta:

1. La configuración exacta de tu petición en Postman (captura de pantalla)
2. El error completo que recibes
3. Los últimos 50 logs del backend
4. Si el comando cURL funciona o no
