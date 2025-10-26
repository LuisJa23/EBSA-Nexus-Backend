# 📚 Guía de Uso - Notificaciones Automáticas EBSA Nexus

## 🎯 Para Desarrolladores Frontend

Esta guía te ayudará a integrar las notificaciones automáticas en tu aplicación frontend.

---

## 🔔 Caso 1: Mostrar Notificaciones al Iniciar Sesión

Cuando un usuario inicia sesión, carga su resumen de notificaciones:

```javascript
// login.js
async function onLoginSuccess(userId) {
  try {
    // Obtener resumen de notificaciones
    const response = await fetch(
      `/api/v1/notifications/user/${userId}/summary`
    );
    const summary = await response.json();

    // Actualizar UI
    updateNotificationBadge(summary.unreadCount);
    displayRecentNotifications(summary.recentNotifications);

    console.log(`Usuario tiene ${summary.unreadCount} notificaciones sin leer`);
    console.log(`Total de notificaciones: ${summary.allNotifications.length}`);
  } catch (error) {
    console.error("Error cargando notificaciones:", error);
  }
}

// Actualizar badge en la UI
function updateNotificationBadge(count) {
  const badge = document.getElementById("notification-badge");
  if (count > 0) {
    badge.textContent = count;
    badge.style.display = "block";
  } else {
    badge.style.display = "none";
  }
}
```

---

## 🔔 Caso 2: Polling de Notificaciones Nuevas

Para mantener las notificaciones actualizadas en tiempo real (sin WebSockets):

```javascript
// notifications.js
let notificationCheckInterval;

function startNotificationPolling(userId, intervalSeconds = 30) {
  // Verificar nuevas notificaciones cada X segundos
  notificationCheckInterval = setInterval(async () => {
    try {
      const response = await fetch(`/api/v1/notifications/user/${userId}/unread/count`);
      const unreadCount = await response.json();

      // Actualizar badge
      updateNotificationBadge(unreadCount);

      // Si hay nuevas, puedes mostrar un toast
      if (unreadCount > 0) {
        checkForNewNotifications(userId);
      }
    } catch (error) {
      console.error('Error verificando notificaciones:', error);
    }
  }, intervalSeconds * 1000);
}

async function checkForNewNotifications(userId) {
  try {
    const response = await fetch(`/api/v1/notifications/user/${userId}/unread`);
    const notifications = await response.json();

    // Mostrar solo las más recientes
    const newNotifications = notifications.filter(n =>
      isRecent(n.createdAt, 60) // Últimos 60 segundos
    );

    newNotifications.forEach(notification => {
      showNotificationToast(notification);
    });
  } catch (error) {
    console.error('Error obteniendo notificaciones nuevas:', error);
  }
}

function isRecent(createdAt, seconds) {
  const notificationTime = new Date(createdAt);
  const now = new Date();
  const diffSeconds = (now - notificationTime) / 1000;
  return diffSeconds <= seconds;
}

function stopNotificationPolling() {
  if (notificationCheckInterval) {
    clearInterval(notificationCheckInterval);
  }
}

// Llamar al iniciar sesión
onLoginSuccess(userId) {
  startNotificationPolling(userId, 30); // Verificar cada 30 segundos
}

// Llamar al cerrar sesión
onLogout() {
  stopNotificationPolling();
}
```

---

## 🔔 Caso 3: Panel de Notificaciones

Mostrar todas las notificaciones del usuario:

```javascript
// notification-panel.js
async function loadNotificationPanel(userId) {
  try {
    const response = await fetch(`/api/v1/notifications/user/${userId}`);
    const notifications = await response.json();

    const panel = document.getElementById("notification-panel");
    panel.innerHTML = "";

    if (notifications.length === 0) {
      panel.innerHTML = '<p class="empty">No tienes notificaciones</p>';
      return;
    }

    // Agrupar por tipo
    const grouped = groupByType(notifications);

    // Renderizar
    Object.entries(grouped).forEach(([type, notifs]) => {
      const section = createNotificationSection(type, notifs);
      panel.appendChild(section);
    });
  } catch (error) {
    console.error("Error cargando panel de notificaciones:", error);
  }
}

function groupByType(notifications) {
  return notifications.reduce((acc, notif) => {
    const type = notif.type || "GENERAL";
    if (!acc[type]) acc[type] = [];
    acc[type].push(notif);
    return acc;
  }, {});
}

function createNotificationSection(type, notifications) {
  const section = document.createElement("div");
  section.className = "notification-section";

  const title = document.createElement("h3");
  title.textContent = getTypeLabel(type);
  section.appendChild(title);

  notifications.forEach((notif) => {
    const item = createNotificationItem(notif);
    section.appendChild(item);
  });

  return section;
}

function createNotificationItem(notification) {
  const item = document.createElement("div");
  item.className = `notification-item ${
    notification.isRead ? "read" : "unread"
  }`;
  item.innerHTML = `
    <div class="notification-header">
      <span class="notification-title">${notification.title}</span>
      <span class="notification-time">${formatTime(
        notification.createdAt
      )}</span>
    </div>
    <div class="notification-message">${notification.message}</div>
    ${!notification.isRead ? '<div class="unread-indicator"></div>' : ""}
  `;

  // Marcar como leída al hacer click
  item.addEventListener("click", () => {
    markAsRead(notification.id);
    item.classList.remove("unread");
    item.classList.add("read");
  });

  return item;
}

function getTypeLabel(type) {
  const labels = {
    NOVELTY_ASSIGNED: "Novedades Asignadas",
    CREW_ASSIGNED: "Cuadrillas",
    NOVELTY_COMPLETED: "Novedades Completadas",
    STATUS_CHANGE: "Cambios de Estado",
    SYSTEM: "Sistema",
    GENERAL: "General",
  };
  return labels[type] || type;
}

function formatTime(timestamp) {
  const date = new Date(timestamp);
  const now = new Date();
  const diffMs = now - date;
  const diffMins = Math.floor(diffMs / 60000);

  if (diffMins < 1) return "Ahora";
  if (diffMins < 60) return `Hace ${diffMins}m`;

  const diffHours = Math.floor(diffMins / 60);
  if (diffHours < 24) return `Hace ${diffHours}h`;

  const diffDays = Math.floor(diffHours / 24);
  if (diffDays < 7) return `Hace ${diffDays}d`;

  return date.toLocaleDateString();
}
```

---

## 🔔 Caso 4: Marcar Notificación como Leída

```javascript
// actions.js
async function markAsRead(notificationId) {
  try {
    const response = await fetch(
      `/api/v1/notifications/${notificationId}/read`,
      {
        method: "PATCH",
      }
    );

    if (response.ok) {
      // Actualizar contador
      updateNotificationCount();
      console.log(`Notificación ${notificationId} marcada como leída`);
    }
  } catch (error) {
    console.error("Error marcando notificación como leída:", error);
  }
}

async function markAllAsRead(userId) {
  try {
    const response = await fetch(
      `/api/v1/notifications/user/${userId}/read-all`,
      {
        method: "PATCH",
      }
    );

    if (response.ok) {
      // Actualizar UI
      updateNotificationBadge(0);
      refreshNotificationPanel(userId);
      console.log("Todas las notificaciones marcadas como leídas");
    }
  } catch (error) {
    console.error("Error marcando todas como leídas:", error);
  }
}

async function updateNotificationCount() {
  const userId = getCurrentUserId(); // Tu función para obtener el userId
  try {
    const response = await fetch(
      `/api/v1/notifications/user/${userId}/unread/count`
    );
    const count = await response.json();
    updateNotificationBadge(count);
  } catch (error) {
    console.error("Error actualizando contador:", error);
  }
}
```

---

## 🔔 Caso 5: Toast de Notificaciones

Mostrar un toast cuando llega una nueva notificación:

```javascript
// toast.js
function showNotificationToast(notification) {
  // Crear elemento toast
  const toast = document.createElement("div");
  toast.className = "notification-toast";
  toast.innerHTML = `
    <div class="toast-icon">${getIconForType(notification.type)}</div>
    <div class="toast-content">
      <div class="toast-title">${notification.title}</div>
      <div class="toast-message">${notification.message}</div>
    </div>
    <button class="toast-close" onclick="closeToast(this)">×</button>
  `;

  // Agregar al DOM
  document.body.appendChild(toast);

  // Auto-cerrar después de 5 segundos
  setTimeout(() => {
    closeToast(toast);
  }, 5000);

  // Marcar como leída al hacer click
  toast.addEventListener("click", () => {
    markAsRead(notification.id);
    closeToast(toast);
  });
}

function getIconForType(type) {
  const icons = {
    NOVELTY_ASSIGNED: "📋",
    CREW_ASSIGNED: "👥",
    NOVELTY_COMPLETED: "✅",
    STATUS_CHANGE: "🔄",
    SYSTEM: "⚙️",
    GENERAL: "📢",
  };
  return icons[type] || "🔔";
}

function closeToast(toastElement) {
  toastElement.classList.add("closing");
  setTimeout(() => {
    toastElement.remove();
  }, 300);
}
```

**CSS para el toast**:

```css
.notification-toast {
  position: fixed;
  top: 20px;
  right: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  max-width: 400px;
  animation: slideIn 0.3s ease;
  z-index: 9999;
  cursor: pointer;
}

.notification-toast.closing {
  animation: slideOut 0.3s ease;
}

@keyframes slideIn {
  from {
    transform: translateX(400px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes slideOut {
  from {
    transform: translateX(0);
    opacity: 1;
  }
  to {
    transform: translateX(400px);
    opacity: 0;
  }
}

.toast-icon {
  font-size: 24px;
}

.toast-content {
  flex: 1;
}

.toast-title {
  font-weight: bold;
  margin-bottom: 4px;
}

.toast-message {
  font-size: 14px;
  color: #666;
}

.toast-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}
```

---

## 🔔 Caso 6: Filtrar Notificaciones por Tipo

```javascript
// filters.js
async function getNotificationsByType(userId, type) {
  try {
    const response = await fetch(
      `/api/v1/notifications/user/${userId}/type/${type}`
    );
    const notifications = await response.json();

    displayNotifications(notifications);
    console.log(
      `Mostrando ${notifications.length} notificaciones de tipo ${type}`
    );
  } catch (error) {
    console.error("Error filtrando notificaciones:", error);
  }
}

// Ejemplo de filtros en UI
function setupNotificationFilters(userId) {
  const filters = [
    { label: "Todas", type: null },
    { label: "Novedades Asignadas", type: "NOVELTY_ASSIGNED" },
    { label: "Cuadrillas", type: "CREW_ASSIGNED" },
    { label: "Completadas", type: "NOVELTY_COMPLETED" },
    { label: "Sistema", type: "SYSTEM" },
  ];

  const filterContainer = document.getElementById("notification-filters");

  filters.forEach((filter) => {
    const button = document.createElement("button");
    button.textContent = filter.label;
    button.onclick = () => {
      if (filter.type) {
        getNotificationsByType(userId, filter.type);
      } else {
        loadNotificationPanel(userId); // Todas
      }
    };
    filterContainer.appendChild(button);
  });
}
```

---

## 🎯 Componente React de Ejemplo

```jsx
// NotificationPanel.jsx
import React, { useState, useEffect } from "react";

function NotificationPanel({ userId }) {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadNotifications();

    // Polling cada 30 segundos
    const interval = setInterval(loadNotifications, 30000);

    return () => clearInterval(interval);
  }, [userId]);

  const loadNotifications = async () => {
    try {
      const [notifResponse, countResponse] = await Promise.all([
        fetch(`/api/v1/notifications/user/${userId}`),
        fetch(`/api/v1/notifications/user/${userId}/unread/count`),
      ]);

      const notifs = await notifResponse.json();
      const count = await countResponse.json();

      setNotifications(notifs);
      setUnreadCount(count);
      setLoading(false);
    } catch (error) {
      console.error("Error loading notifications:", error);
      setLoading(false);
    }
  };

  const handleMarkAsRead = async (notificationId) => {
    try {
      await fetch(`/api/v1/notifications/${notificationId}/read`, {
        method: "PATCH",
      });

      // Actualizar localmente
      setNotifications((prev) =>
        prev.map((n) => (n.id === notificationId ? { ...n, isRead: true } : n))
      );
      setUnreadCount((prev) => Math.max(0, prev - 1));
    } catch (error) {
      console.error("Error marking as read:", error);
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await fetch(`/api/v1/notifications/user/${userId}/read-all`, {
        method: "PATCH",
      });

      setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
      setUnreadCount(0);
    } catch (error) {
      console.error("Error marking all as read:", error);
    }
  };

  if (loading) return <div>Cargando notificaciones...</div>;

  return (
    <div className="notification-panel">
      <div className="notification-header">
        <h2>Notificaciones {unreadCount > 0 && `(${unreadCount})`}</h2>
        {unreadCount > 0 && (
          <button onClick={handleMarkAllAsRead}>
            Marcar todas como leídas
          </button>
        )}
      </div>

      {notifications.length === 0 ? (
        <p>No tienes notificaciones</p>
      ) : (
        <div className="notification-list">
          {notifications.map((notification) => (
            <NotificationItem
              key={notification.id}
              notification={notification}
              onMarkAsRead={handleMarkAsRead}
            />
          ))}
        </div>
      )}
    </div>
  );
}

function NotificationItem({ notification, onMarkAsRead }) {
  return (
    <div
      className={`notification-item ${notification.isRead ? "read" : "unread"}`}
      onClick={() => onMarkAsRead(notification.id)}
    >
      <div className="notification-content">
        <h3>{notification.title}</h3>
        <p>{notification.message}</p>
        <span className="notification-time">
          {formatTime(notification.createdAt)}
        </span>
      </div>
      {!notification.isRead && <div className="unread-dot" />}
    </div>
  );
}

function formatTime(timestamp) {
  const date = new Date(timestamp);
  const now = new Date();
  const diffMs = now - date;
  const diffMins = Math.floor(diffMs / 60000);

  if (diffMins < 1) return "Ahora";
  if (diffMins < 60) return `Hace ${diffMins}m`;

  const diffHours = Math.floor(diffMins / 60);
  if (diffHours < 24) return `Hace ${diffHours}h`;

  return date.toLocaleDateString();
}

export default NotificationPanel;
```

---

## 🔧 Mejores Prácticas

### 1. Manejo de Errores

```javascript
async function safeApiCall(url, options = {}) {
  try {
    const response = await fetch(url, options);

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }

    return await response.json();
  } catch (error) {
    console.error("API Error:", error);
    // Mostrar mensaje al usuario
    showErrorToast("Error al cargar notificaciones");
    return null;
  }
}
```

### 2. Caché Local

```javascript
// Usar localStorage para cache de notificaciones
function cacheNotifications(userId, notifications) {
  const cache = {
    timestamp: Date.now(),
    data: notifications,
  };
  localStorage.setItem(`notifications_${userId}`, JSON.stringify(cache));
}

function getCachedNotifications(userId, maxAgeMs = 60000) {
  const cached = localStorage.getItem(`notifications_${userId}`);
  if (!cached) return null;

  const { timestamp, data } = JSON.parse(cached);
  const age = Date.now() - timestamp;

  return age < maxAgeMs ? data : null;
}
```

### 3. Debouncing para Polling

```javascript
let pollTimeout;

function schedulePoll(userId, delayMs = 30000) {
  clearTimeout(pollTimeout);
  pollTimeout = setTimeout(() => {
    checkForNotifications(userId);
    schedulePoll(userId, delayMs);
  }, delayMs);
}
```

---

## 📱 Integración con React Native / Mobile

```javascript
// Para apps móviles, considera:

// 1. Push Notifications
import messaging from "@react-native-firebase/messaging";

async function requestNotificationPermission() {
  const authStatus = await messaging().requestPermission();
  const enabled =
    authStatus === messaging.AuthorizationStatus.AUTHORIZED ||
    authStatus === messaging.AuthorizationStatus.PROVISIONAL;

  if (enabled) {
    console.log("Notification permission granted");
    // Obtener token del dispositivo
    const token = await messaging().getToken();
    // Enviar token al backend
    registerDeviceToken(userId, token);
  }
}

// 2. Manejar notificaciones en foreground
messaging().onMessage(async (remoteMessage) => {
  Alert.alert(
    remoteMessage.notification.title,
    remoteMessage.notification.body
  );
});
```

---

## 🎉 Resumen

Con estos ejemplos puedes:

- ✅ Mostrar notificaciones al login
- ✅ Actualizar notificaciones en tiempo real (polling)
- ✅ Crear un panel de notificaciones
- ✅ Mostrar toasts para notificaciones nuevas
- ✅ Filtrar por tipo
- ✅ Marcar como leídas
- ✅ Integrar con React o Vue

**El backend está listo, solo necesitas implementar el frontend! 🚀**
