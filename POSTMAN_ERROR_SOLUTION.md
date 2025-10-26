# 🚀 SOLUCIÓN: Error 500 en Postman (pero cURL funciona)

## ✅ Confirmado: El Backend Funciona Correctamente

He verificado que el endpoint **SÍ funciona** usando `curl`:

```bash
# ✅ Este comando funciona perfectamente
curl -X POST http://localhost:8080/api/v1/notifications \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "type": "INFO",
    "title": "Test notification",
    "message": "This is a test message"
  }'

# Respuesta (201 Created):
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

**La notificación se creó exitosamente en la base de datos.**

---

## 🎯 El Problema: Configuración Incorrecta de Postman

Si `curl` funciona pero Postman da error 500, el problema está en **cómo configuraste la petición en Postman**.

### Errores Comunes en Postman:

1. ❌ Body no está en formato `raw` + `JSON`
2. ❌ Headers `Content-Type: application/json` faltante
3. ❌ JSON mal formateado (comillas simples, campos sin comillas, etc.)
4. ❌ Método HTTP incorrecto (GET en lugar de POST)

---

## 🔧 SOLUCIÓN RÁPIDA: Importa la Colección Pre-configurada

### Opción 1: Importar Colección JSON (RECOMENDADO)

1. Abre Postman
2. Click en **"Import"** (arriba a la izquierda)
3. Selecciona la pestaña **"File"**
4. Arrastra el archivo **`EBSA_Nexus_Notifications.postman_collection.json`** (está en la raíz del proyecto)
5. Click en **"Import"**
6. Ve a la carpeta "EBSA Nexus - Notifications API"
7. Ejecuta la primera petición: **"1. CREATE - New Notification (START HERE)"**
8. Click en **"Send"**

**Deberías recibir un 201 Created** ✅

---

### Opción 2: Importar desde cURL

1. En Postman, click en **"Import"**
2. Selecciona **"Raw text"**
3. Pega este comando:

```bash
curl -X POST http://localhost:8080/api/v1/notifications -H "Content-Type: application/json" -d '{"userId": 1, "type": "INFO", "title": "Test notification", "message": "This is a test message"}'
```

4. Click en **"Continue"** → **"Import"**
5. Click en **"Send"**

---

### Opción 3: Configurar Manualmente (Paso a Paso)

Si prefieres configurar manualmente, sigue **EXACTAMENTE** estos pasos:

#### 1. Método y URL

```
POST http://localhost:8080/api/v1/notifications
```

#### 2. Headers (Pestaña "Headers")

```
Content-Type: application/json
Accept: application/json
```

#### 3. Body (Pestaña "Body")

- Selecciona: **● raw** (radio button)
- Dropdown a la derecha: **JSON** (NO Text)
- Pega este JSON exacto:

```json
{
  "userId": 1,
  "type": "INFO",
  "title": "Test notification",
  "message": "This is a test message"
}
```

#### 4. Click en "Send"

**Respuesta esperada: 201 Created**

---

## 📋 Checklist de Validación

Antes de hacer click en "Send", verifica:

- [ ] Método es **POST** (no GET)
- [ ] URL es exactamente: `http://localhost:8080/api/v1/notifications`
- [ ] Header `Content-Type: application/json` está presente
- [ ] Body está en modo **"raw"** (no form-data)
- [ ] El dropdown del body dice **"JSON"** (no Text)
- [ ] El JSON tiene comillas dobles `"` (no comillas simples `'`)
- [ ] Todos los campos obligatorios están presentes: `userId`, `type`, `title`, `message`

---

## 🐛 Debug: ¿Qué Está Enviando Postman?

Si aún tienes problemas:

1. Abre la **Postman Console** (botón abajo a la izquierda, o View → Show Postman Console)
2. Envía tu petición
3. En la Console, expande la petición
4. Revisa:
   - **Request Headers**: debe incluir `Content-Type: application/json`
   - **Request Body**: debe ser el JSON válido
   - **Response Body**: verás el error exacto

---

## 📚 Archivos Creados

He creado 3 archivos para ayudarte:

1. **`POSTMAN_SETUP_GUIDE.md`** - Guía paso a paso con troubleshooting
2. **`EBSA_Nexus_Notifications.postman_collection.json`** - Colección preconfigurada para importar
3. **`NOTIFICATIONS_README.md`** - Actualizado con sección de troubleshooting

---

## ✅ Verificar que el Backend Está Corriendo

```bash
# Ver contenedores activos
docker ps

# Debe mostrar:
# ebsa-nexus-backend   Up X minutes (healthy)
```

---

## 🎓 Próximos Pasos

1. **Importa la colección JSON** en Postman
2. **Prueba la primera petición** (CREATE New Notification)
3. Si funciona, prueba el resto de endpoints
4. Lee el `POSTMAN_SETUP_GUIDE.md` para más detalles

---

## 💡 Tip: Comparar con cURL

Si Postman sigue fallando, compara exactamente qué está enviando:

**En Postman Console verás:**

```
POST http://localhost:8080/api/v1/notifications
Headers:
  Content-Type: application/json
Body:
  {"userId":1,"type":"INFO",...}
```

**Debe ser idéntico al comando cURL que funciona.**

---

## 📞 Si Nada Funciona

1. Reinicia Postman completamente
2. Prueba con otro cliente HTTP (Insomnia, Thunder Client)
3. O simplemente usa `curl` desde la terminal
4. Comparte una captura de pantalla de tu configuración de Postman

---

## ✅ Resumen

- ✅ **Backend funciona correctamente** (verificado con curl)
- ✅ **Base de datos funciona** (notificación se guardó)
- ✅ **Endpoints están públicos** (sin autenticación requerida)
- ❌ **Problema está en Postman** (configuración incorrecta)
- ✅ **Solución**: Importar colección preconfigurada
