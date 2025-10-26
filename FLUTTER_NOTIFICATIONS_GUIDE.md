# 📱 Guía de Integración Flutter - Notificaciones EBSA Nexus

## 📋 Mapeo de Tipos de Notificación

### Tipos del Backend vs Frontend

El backend usa **strings personalizados** en los servicios (no el enum directo). Aquí está el mapeo correcto:

| Backend (String usado) | Backend (Enum)    | Flutter (Enum)       | Descripción                      |
| ---------------------- | ----------------- | -------------------- | -------------------------------- |
| `NEW_NOVELTY`          | -                 | `noveltyCreated`     | Nueva novedad reportada          |
| `NOVELTY_ASSIGNED`     | NOVELTY_ASSIGNED  | `noveltyAssigned`    | Novedad asignada a cuadrilla     |
| `STATUS_CHANGE`        | -                 | `statusChange`       | Cambio de estado de novedad      |
| `NOVELTY_COMPLETED`    | NOVELTY_COMPLETED | `noveltyCompleted`   | Novedad completada               |
| `COMPLETION_REJECTED`  | -                 | `completionRejected` | Completación rechazada           |
| `NOVELTY_CANCELLED`    | -                 | `noveltyCancelled`   | Novedad cancelada                |
| `NOVELTY_OVERDUE`      | -                 | `noveltyOverdue`     | Novedad vencida                  |
| `CREW_ASSIGNED`        | CREW_ASSIGNED     | `crewAssigned`       | Asignación/remoción de cuadrilla |
| `SYSTEM_ALERT`         | SYSTEM_ALERT      | `systemAlert`        | Alerta del sistema               |
| `REMINDER`             | REMINDER          | `reminder`           | Recordatorio                     |
| `GENERAL`              | GENERAL           | `general`            | Notificación general             |

---

## 🔧 Modelo de Notificación para Flutter

### notification_model.dart

```dart
import 'notification_type.dart';

class NotificationModel {
  final int id;
  final int userId;
  final int? noveltyId;
  final NotificationType type;
  final String title;
  final String message;
  final bool isRead;
  final DateTime? readAt;
  final DateTime createdAt;

  NotificationModel({
    required this.id,
    required this.userId,
    this.noveltyId,
    required this.type,
    required this.title,
    required this.message,
    required this.isRead,
    this.readAt,
    required this.createdAt,
  });

  /// Crea una instancia desde JSON del backend
  factory NotificationModel.fromJson(Map<String, dynamic> json) {
    return NotificationModel(
      id: json['id'] as int,
      userId: json['userId'] as int,
      noveltyId: json['noveltyId'] as int?,
      type: NotificationType.fromString(json['type'] as String),
      title: json['title'] as String,
      message: json['message'] as String,
      isRead: json['isRead'] as bool,
      readAt: json['readAt'] != null
          ? DateTime.parse(json['readAt'] as String)
          : null,
      createdAt: DateTime.parse(json['createdAt'] as String),
    );
  }

  /// Convierte a JSON para enviar al backend
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'noveltyId': noveltyId,
      'type': type.value,
      'title': title,
      'message': message,
      'isRead': isRead,
      'readAt': readAt?.toIso8601String(),
      'createdAt': createdAt.toIso8601String(),
    };
  }

  /// Crea una copia con campos modificados
  NotificationModel copyWith({
    int? id,
    int? userId,
    int? noveltyId,
    NotificationType? type,
    String? title,
    String? message,
    bool? isRead,
    DateTime? readAt,
    DateTime? createdAt,
  }) {
    return NotificationModel(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      noveltyId: noveltyId ?? this.noveltyId,
      type: type ?? this.type,
      title: title ?? this.title,
      message: message ?? this.message,
      isRead: isRead ?? this.isRead,
      readAt: readAt ?? this.readAt,
      createdAt: createdAt ?? this.createdAt,
    );
  }

  /// Marca la notificación como leída
  NotificationModel markAsRead() {
    return copyWith(
      isRead: true,
      readAt: DateTime.now(),
    );
  }

  /// Calcula el tiempo transcurrido desde la creación
  String get timeAgo {
    final now = DateTime.now();
    final difference = now.difference(createdAt);

    if (difference.inSeconds < 60) {
      return 'Ahora';
    } else if (difference.inMinutes < 60) {
      return 'Hace ${difference.inMinutes}m';
    } else if (difference.inHours < 24) {
      return 'Hace ${difference.inHours}h';
    } else if (difference.inDays < 7) {
      return 'Hace ${difference.inDays}d';
    } else {
      return '${createdAt.day}/${createdAt.month}/${createdAt.year}';
    }
  }

  @override
  String toString() {
    return 'NotificationModel(id: $id, type: ${type.value}, title: $title, isRead: $isRead)';
  }
}

/// Resumen de notificaciones (para login)
class NotificationSummary {
  final List<NotificationModel> allNotifications;
  final int unreadCount;
  final List<NotificationModel> recentNotifications;

  NotificationSummary({
    required this.allNotifications,
    required this.unreadCount,
    required this.recentNotifications,
  });

  factory NotificationSummary.fromJson(Map<String, dynamic> json) {
    return NotificationSummary(
      allNotifications: (json['allNotifications'] as List)
          .map((n) => NotificationModel.fromJson(n))
          .toList(),
      unreadCount: json['unreadCount'] as int,
      recentNotifications: (json['recentNotifications'] as List)
          .map((n) => NotificationModel.fromJson(n))
          .toList(),
    );
  }
}
```

---

## 🌐 Servicio de API para Notificaciones

### notification_service.dart

```dart
import 'dart:convert';
import 'package:http/http.dart' as http;
import 'notification_model.dart';
import 'notification_type.dart';

class NotificationService {
  final String baseUrl;
  final String? authToken;

  NotificationService({
    required this.baseUrl,
    this.authToken,
  });

  /// Headers comunes para las peticiones
  Map<String, String> get _headers {
    final headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    };
    if (authToken != null) {
      headers['Authorization'] = 'Bearer $authToken';
    }
    return headers;
  }

  /// Obtener resumen de notificaciones (para login)
  Future<NotificationSummary> getNotificationSummary(int userId) async {
    final url = Uri.parse('$baseUrl/api/v1/notifications/user/$userId/summary');
    final response = await http.get(url, headers: _headers);

    if (response.statusCode == 200) {
      return NotificationSummary.fromJson(json.decode(response.body));
    } else {
      throw Exception('Error al cargar resumen de notificaciones: ${response.statusCode}');
    }
  }

  /// Obtener todas las notificaciones de un usuario
  Future<List<NotificationModel>> getUserNotifications(int userId) async {
    final url = Uri.parse('$baseUrl/api/v1/notifications/user/$userId');
    final response = await http.get(url, headers: _headers);

    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      return data.map((n) => NotificationModel.fromJson(n)).toList();
    } else {
      throw Exception('Error al cargar notificaciones: ${response.statusCode}');
    }
  }

  /// Obtener notificaciones no leídas
  Future<List<NotificationModel>> getUnreadNotifications(int userId) async {
    final url = Uri.parse('$baseUrl/api/v1/notifications/user/$userId/unread');
    final response = await http.get(url, headers: _headers);

    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      return data.map((n) => NotificationModel.fromJson(n)).toList();
    } else {
      throw Exception('Error al cargar notificaciones no leídas: ${response.statusCode}');
    }
  }

  /// Contar notificaciones no leídas
  Future<int> countUnreadNotifications(int userId) async {
    final url = Uri.parse('$baseUrl/api/v1/notifications/user/$userId/unread/count');
    final response = await http.get(url, headers: _headers);

    if (response.statusCode == 200) {
      return int.parse(response.body);
    } else {
      throw Exception('Error al contar notificaciones no leídas: ${response.statusCode}');
    }
  }

  /// Obtener notificaciones por tipo
  Future<List<NotificationModel>> getNotificationsByType(
    int userId,
    NotificationType type,
  ) async {
    final url = Uri.parse('$baseUrl/api/v1/notifications/user/$userId/type/${type.value}');
    final response = await http.get(url, headers: _headers);

    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      return data.map((n) => NotificationModel.fromJson(n)).toList();
    } else {
      throw Exception('Error al cargar notificaciones por tipo: ${response.statusCode}');
    }
  }

  /// Marcar notificación como leída
  Future<NotificationModel> markAsRead(int notificationId) async {
    final url = Uri.parse('$baseUrl/api/v1/notifications/$notificationId/read');
    final response = await http.patch(url, headers: _headers);

    if (response.statusCode == 200) {
      return NotificationModel.fromJson(json.decode(response.body));
    } else {
      throw Exception('Error al marcar notificación como leída: ${response.statusCode}');
    }
  }

  /// Marcar todas las notificaciones como leídas
  Future<void> markAllAsRead(int userId) async {
    final url = Uri.parse('$baseUrl/api/v1/notifications/user/$userId/read-all');
    final response = await http.patch(url, headers: _headers);

    if (response.statusCode != 200) {
      throw Exception('Error al marcar todas como leídas: ${response.statusCode}');
    }
  }

  /// Eliminar una notificación
  Future<void> deleteNotification(int notificationId) async {
    final url = Uri.parse('$baseUrl/api/v1/notifications/$notificationId');
    final response = await http.delete(url, headers: _headers);

    if (response.statusCode != 204) {
      throw Exception('Error al eliminar notificación: ${response.statusCode}');
    }
  }

  /// Eliminar todas las notificaciones de un usuario
  Future<void> deleteAllNotifications(int userId) async {
    final url = Uri.parse('$baseUrl/api/v1/notifications/user/$userId');
    final response = await http.delete(url, headers: _headers);

    if (response.statusCode != 204) {
      throw Exception('Error al eliminar todas las notificaciones: ${response.statusCode}');
    }
  }

  /// Crear una notificación (solo para testing o casos especiales)
  Future<NotificationModel> createNotification({
    required int userId,
    required NotificationType type,
    required String title,
    required String message,
    int? noveltyId,
  }) async {
    final url = Uri.parse('$baseUrl/api/v1/notifications');
    final response = await http.post(
      url,
      headers: _headers,
      body: json.encode({
        'userId': userId,
        'noveltyId': noveltyId,
        'type': type.value,
        'title': title,
        'message': message,
      }),
    );

    if (response.statusCode == 200 || response.statusCode == 201) {
      return NotificationModel.fromJson(json.decode(response.body));
    } else {
      throw Exception('Error al crear notificación: ${response.statusCode}');
    }
  }
}
```

---

## 📱 Widget de Notificaciones

### notification_card.dart

```dart
import 'package:flutter/material.dart';
import 'notification_model.dart';

class NotificationCard extends StatelessWidget {
  final NotificationModel notification;
  final VoidCallback? onTap;
  final VoidCallback? onDelete;

  const NotificationCard({
    Key? key,
    required this.notification,
    this.onTap,
    this.onDelete,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: notification.isRead ? 0 : 2,
      color: notification.isRead ? Colors.white : Colors.blue.shade50,
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: _getColorForType(notification.type),
          child: Text(
            notification.type.icon,
            style: const TextStyle(fontSize: 20),
          ),
        ),
        title: Text(
          notification.title,
          style: TextStyle(
            fontWeight: notification.isRead ? FontWeight.normal : FontWeight.bold,
          ),
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 4),
            Text(
              notification.message,
              maxLines: 2,
              overflow: TextOverflow.ellipsis,
            ),
            const SizedBox(height: 4),
            Row(
              children: [
                Icon(Icons.access_time, size: 12, color: Colors.grey),
                const SizedBox(width: 4),
                Text(
                  notification.timeAgo,
                  style: TextStyle(fontSize: 12, color: Colors.grey),
                ),
                const SizedBox(width: 8),
                if (!notification.isRead)
                  Container(
                    width: 8,
                    height: 8,
                    decoration: BoxDecoration(
                      color: Colors.blue,
                      shape: BoxShape.circle,
                    ),
                  ),
              ],
            ),
          ],
        ),
        trailing: onDelete != null
            ? IconButton(
                icon: const Icon(Icons.delete_outline),
                onPressed: onDelete,
              )
            : null,
        onTap: onTap,
      ),
    );
  }

  Color _getColorForType(NotificationType type) {
    switch (type) {
      case NotificationType.noveltyCreated:
      case NotificationType.noveltyAssigned:
        return Colors.blue.shade100;
      case NotificationType.statusChange:
        return Colors.orange.shade100;
      case NotificationType.noveltyCompleted:
        return Colors.green.shade100;
      case NotificationType.completionRejected:
      case NotificationType.noveltyCancelled:
        return Colors.red.shade100;
      case NotificationType.noveltyOverdue:
        return Colors.deepOrange.shade100;
      case NotificationType.crewAssigned:
        return Colors.purple.shade100;
      case NotificationType.systemAlert:
        return Colors.amber.shade100;
      default:
        return Colors.grey.shade100;
    }
  }
}
```

---

## 🎯 Provider/Controller de Notificaciones

### notification_provider.dart (usando Provider o Riverpod)

```dart
import 'package:flutter/foundation.dart';
import 'notification_model.dart';
import 'notification_service.dart';
import 'notification_type.dart';

class NotificationProvider extends ChangeNotifier {
  final NotificationService _service;
  final int userId;

  List<NotificationModel> _notifications = [];
  int _unreadCount = 0;
  bool _isLoading = false;
  String? _error;

  NotificationProvider({
    required NotificationService service,
    required this.userId,
  }) : _service = service;

  List<NotificationModel> get notifications => _notifications;
  int get unreadCount => _unreadCount;
  bool get isLoading => _isLoading;
  String? get error => _error;
  List<NotificationModel> get unreadNotifications =>
      _notifications.where((n) => !n.isRead).toList();

  /// Cargar todas las notificaciones
  Future<void> loadNotifications() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _notifications = await _service.getUserNotifications(userId);
      _unreadCount = _notifications.where((n) => !n.isRead).length;
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  /// Actualizar contador de no leídas
  Future<void> updateUnreadCount() async {
    try {
      _unreadCount = await _service.countUnreadNotifications(userId);
      notifyListeners();
    } catch (e) {
      // Silenciar error del contador
      debugPrint('Error updating unread count: $e');
    }
  }

  /// Marcar notificación como leída
  Future<void> markAsRead(int notificationId) async {
    try {
      final updated = await _service.markAsRead(notificationId);
      final index = _notifications.indexWhere((n) => n.id == notificationId);
      if (index != -1) {
        _notifications[index] = updated;
        _unreadCount = _notifications.where((n) => !n.isRead).length;
        notifyListeners();
      }
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }

  /// Marcar todas como leídas
  Future<void> markAllAsRead() async {
    try {
      await _service.markAllAsRead(userId);
      _notifications = _notifications
          .map((n) => n.copyWith(isRead: true, readAt: DateTime.now()))
          .toList();
      _unreadCount = 0;
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }

  /// Eliminar notificación
  Future<void> deleteNotification(int notificationId) async {
    try {
      await _service.deleteNotification(notificationId);
      _notifications.removeWhere((n) => n.id == notificationId);
      _unreadCount = _notifications.where((n) => !n.isRead).length;
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }

  /// Filtrar por tipo
  List<NotificationModel> getByType(NotificationType type) {
    return _notifications.where((n) => n.type == type).toList();
  }

  /// Limpiar error
  void clearError() {
    _error = null;
    notifyListeners();
  }
}
```

---

## 🚀 Uso en la Aplicación

### main.dart

```dart
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'notification_service.dart';
import 'notification_provider.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(
          create: (_) => NotificationProvider(
            service: NotificationService(
              baseUrl: 'http://localhost:8080',
              authToken: 'tu-token-jwt', // Obtener del login
            ),
            userId: 1, // Obtener del login
          )..loadNotifications(), // Cargar al iniciar
        ),
      ],
      child: MaterialApp(
        title: 'EBSA Nexus',
        home: HomeScreen(),
      ),
    );
  }
}
```

### Pantalla de Notificaciones

```dart
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'notification_provider.dart';
import 'notification_card.dart';

class NotificationsScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Notificaciones'),
        actions: [
          Consumer<NotificationProvider>(
            builder: (context, provider, _) {
              if (provider.unreadCount > 0) {
                return TextButton(
                  onPressed: () => provider.markAllAsRead(),
                  child: const Text(
                    'Marcar todas como leídas',
                    style: TextStyle(color: Colors.white),
                  ),
                );
              }
              return const SizedBox.shrink();
            },
          ),
        ],
      ),
      body: Consumer<NotificationProvider>(
        builder: (context, provider, _) {
          if (provider.isLoading) {
            return const Center(child: CircularProgressIndicator());
          }

          if (provider.error != null) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text('Error: ${provider.error}'),
                  ElevatedButton(
                    onPressed: () => provider.loadNotifications(),
                    child: const Text('Reintentar'),
                  ),
                ],
              ),
            );
          }

          if (provider.notifications.isEmpty) {
            return const Center(
              child: Text('No tienes notificaciones'),
            );
          }

          return RefreshIndicator(
            onRefresh: provider.loadNotifications,
            child: ListView.builder(
              itemCount: provider.notifications.length,
              itemBuilder: (context, index) {
                final notification = provider.notifications[index];
                return NotificationCard(
                  notification: notification,
                  onTap: () async {
                    if (!notification.isRead) {
                      await provider.markAsRead(notification.id);
                    }
                    // Navegar a detalle si es necesario
                  },
                  onDelete: () => provider.deleteNotification(notification.id),
                );
              },
            ),
          );
        },
      ),
    );
  }
}
```

### Badge de Notificaciones

```dart
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'notification_provider.dart';

class NotificationBadge extends StatelessWidget {
  final Widget child;

  const NotificationBadge({Key? key, required this.child}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Consumer<NotificationProvider>(
      builder: (context, provider, _) {
        return Stack(
          children: [
            child,
            if (provider.unreadCount > 0)
              Positioned(
                right: 0,
                top: 0,
                child: Container(
                  padding: const EdgeInsets.all(4),
                  decoration: BoxDecoration(
                    color: Colors.red,
                    shape: BoxShape.circle,
                  ),
                  constraints: const BoxConstraints(
                    minWidth: 20,
                    minHeight: 20,
                  ),
                  child: Text(
                    provider.unreadCount > 99 ? '99+' : '${provider.unreadCount}',
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 12,
                      fontWeight: FontWeight.bold,
                    ),
                    textAlign: TextAlign.center,
                  ),
                ),
              ),
          ],
        );
      },
    );
  }
}
```

---

## ⏰ Polling para Actualizar Notificaciones

```dart
import 'dart:async';
import 'notification_provider.dart';

class NotificationPollingService {
  final NotificationProvider provider;
  Timer? _timer;
  final Duration interval;

  NotificationPollingService({
    required this.provider,
    this.interval = const Duration(seconds: 30),
  });

  void start() {
    _timer?.cancel();
    _timer = Timer.periodic(interval, (_) {
      provider.updateUnreadCount();
    });
  }

  void stop() {
    _timer?.cancel();
    _timer = null;
  }

  void dispose() {
    stop();
  }
}
```

---

## ✅ Resumen

Tu `NotificationType` está **muy bien implementado** y sincronizado con el backend.

**Archivos que deberías crear**:

1. ✅ `notification_type.dart` - Ya lo tienes
2. `notification_model.dart` - Modelo de datos
3. `notification_service.dart` - Cliente API
4. `notification_provider.dart` - Gestión de estado
5. `notification_card.dart` - Widget visual
6. `notifications_screen.dart` - Pantalla completa

**Las notificaciones automáticas del backend funcionan**, solo necesitas implementar el polling o websockets en Flutter para actualizarlas en tiempo real.

¿Quieres que te ayude con algún archivo específico o tienes alguna pregunta sobre la implementación?
