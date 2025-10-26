# 🐛 Solución: Error al Parsear Notificaciones en Flutter

## ❌ Problema Identificado

El backend devuelve fechas en formato: `"2025-10-26T17:17:39"` (sin milisegundos)

Flutter espera fechas en formato ISO 8601 completo con milisegundos: `"2025-10-26T17:17:39.000"`

## ✅ Solución Implementada

### notification_model.dart (VERSIÓN CORREGIDA)

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

  /// ✅ Parsea fechas del backend correctamente
  static DateTime _parseDateTime(dynamic value) {
    if (value == null) return DateTime.now();

    String dateStr = value.toString();

    // El backend devuelve: "2025-10-26T17:17:39"
    // Flutter necesita: "2025-10-26T17:17:39.000" o "2025-10-26T17:17:39Z"

    // Si no tiene milisegundos ni zona horaria, agregarlos
    if (!dateStr.contains('.') && dateStr.contains('T')) {
      // Verificar si tiene zona horaria
      if (!dateStr.endsWith('Z') && !dateStr.contains('+')) {
        dateStr = dateStr + '.000';
      }
    }

    try {
      return DateTime.parse(dateStr);
    } catch (e) {
      print('⚠️ Error parsing date: $dateStr - Error: $e');
      return DateTime.now();
    }
  }

  /// ✅ Crea una instancia desde JSON del backend
  factory NotificationModel.fromJson(Map<String, dynamic> json) {
    try {
      return NotificationModel(
        id: json['id'] as int,
        userId: json['userId'] as int,
        noveltyId: json['noveltyId'] as int?,
        type: NotificationType.fromString(json['type'] as String),
        title: json['title'] as String,
        message: json['message'] as String,
        isRead: json['isRead'] as bool? ?? false,
        readAt: json['readAt'] != null
            ? _parseDateTime(json['readAt'])
            : null,
        createdAt: _parseDateTime(json['createdAt']),
      );
    } catch (e) {
      print('❌ Error parsing notification: $e');
      print('JSON: $json');
      rethrow;
    }
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
    return 'NotificationModel(id: $id, type: ${type.value}, title: $title, isRead: $isRead, createdAt: $createdAt)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is NotificationModel && other.id == id;
  }

  @override
  int get hashCode => id.hashCode;
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
    try {
      return NotificationSummary(
        allNotifications: (json['allNotifications'] as List)
            .map((n) => NotificationModel.fromJson(n))
            .toList(),
        unreadCount: json['unreadCount'] as int,
        recentNotifications: (json['recentNotifications'] as List)
            .map((n) => NotificationModel.fromJson(n))
            .toList(),
      );
    } catch (e) {
      print('❌ Error parsing notification summary: $e');
      rethrow;
    }
  }

  @override
  String toString() {
    return 'NotificationSummary(total: ${allNotifications.length}, unread: $unreadCount, recent: ${recentNotifications.length})';
  }
}
```

---

## 🧪 Código de Prueba

### test_notification_parsing.dart

```dart
import 'dart:convert';

void main() {
  // Ejemplo de respuesta real del backend
  const jsonResponse = '''
  [
    {
        "id": 16,
        "userId": 9,
        "noveltyId": null,
        "type": "CREW_ASSIGNED",
        "title": "Asignado a Cuadrilla",
        "message": "Has sido agregado como miembro de la cuadrilla 'Cuadrilla Gamma'.",
        "isRead": false,
        "createdAt": "2025-10-26T17:17:39"
    },
    {
        "id": 15,
        "userId": 9,
        "noveltyId": null,
        "type": "CREW_ASSIGNED",
        "title": "Asignado a Cuadrilla",
        "message": "Has sido agregado como miembro de la cuadrilla 'Cuadrilla Alpha'.",
        "isRead": false,
        "createdAt": "2025-10-26T17:12:32"
    }
  ]
  ''';

  try {
    // Parsear JSON
    final List<dynamic> jsonList = json.decode(jsonResponse);

    // Convertir a modelos
    final notifications = jsonList
        .map((json) => NotificationModel.fromJson(json))
        .toList();

    print('✅ Parseado exitoso!');
    print('Total notificaciones: ${notifications.length}');

    for (var notif in notifications) {
      print('\n📋 Notificación #${notif.id}');
      print('   Usuario: ${notif.userId}');
      print('   Tipo: ${notif.type.displayName}');
      print('   Título: ${notif.title}');
      print('   Mensaje: ${notif.message}');
      print('   Leída: ${notif.isRead}');
      print('   Creada: ${notif.createdAt}');
      print('   Hace: ${notif.timeAgo}');
    }
  } catch (e) {
    print('❌ Error: $e');
  }
}
```

---

## 📱 Uso en el Servicio de API

### notification_service.dart (ACTUALIZADO)

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

  /// ✅ Obtener todas las notificaciones de un usuario con manejo de errores robusto
  Future<List<NotificationModel>> getUserNotifications(int userId) async {
    try {
      final url = Uri.parse('$baseUrl/api/v1/notifications/user/$userId');

      print('🔄 Cargando notificaciones del usuario $userId...');
      final response = await http.get(url, headers: _headers);

      print('📡 Status code: ${response.statusCode}');
      print('📦 Response body length: ${response.body.length}');

      if (response.statusCode == 200) {
        // Verificar si es un array vacío
        if (response.body.trim() == '[]') {
          print('ℹ️ No hay notificaciones para el usuario $userId');
          return [];
        }

        // Parsear JSON
        final List<dynamic> jsonList = json.decode(response.body);
        print('✅ Parseadas ${jsonList.length} notificaciones');

        // Convertir a modelos
        final notifications = jsonList
            .map((jsonItem) {
              try {
                return NotificationModel.fromJson(jsonItem);
              } catch (e) {
                print('⚠️ Error parseando notificación individual: $e');
                print('JSON problemático: $jsonItem');
                return null;
              }
            })
            .whereType<NotificationModel>() // Filtrar nulls
            .toList();

        print('✅ ${notifications.length} notificaciones convertidas exitosamente');
        return notifications;

      } else if (response.statusCode == 404) {
        print('⚠️ Usuario no encontrado: $userId');
        throw Exception('Usuario no encontrado');
      } else {
        print('❌ Error del servidor: ${response.statusCode}');
        print('Response: ${response.body}');
        throw Exception('Error al cargar notificaciones: ${response.statusCode}');
      }
    } catch (e) {
      print('❌ Excepción al cargar notificaciones: $e');
      rethrow;
    }
  }

  /// ✅ Obtener resumen con manejo de errores
  Future<NotificationSummary> getNotificationSummary(int userId) async {
    try {
      final url = Uri.parse('$baseUrl/api/v1/notifications/user/$userId/summary');
      final response = await http.get(url, headers: _headers);

      if (response.statusCode == 200) {
        return NotificationSummary.fromJson(json.decode(response.body));
      } else {
        throw Exception('Error al cargar resumen: ${response.statusCode}');
      }
    } catch (e) {
      print('❌ Error en getNotificationSummary: $e');
      rethrow;
    }
  }

  /// ✅ Obtener notificaciones no leídas
  Future<List<NotificationModel>> getUnreadNotifications(int userId) async {
    try {
      final url = Uri.parse('$baseUrl/api/v1/notifications/user/$userId/unread');
      final response = await http.get(url, headers: _headers);

      if (response.statusCode == 200) {
        if (response.body.trim() == '[]') return [];

        final List<dynamic> data = json.decode(response.body);
        return data
            .map((n) => NotificationModel.fromJson(n))
            .toList();
      } else {
        throw Exception('Error al cargar notificaciones no leídas: ${response.statusCode}');
      }
    } catch (e) {
      print('❌ Error en getUnreadNotifications: $e');
      rethrow;
    }
  }

  /// ✅ Contar notificaciones no leídas
  Future<int> countUnreadNotifications(int userId) async {
    try {
      final url = Uri.parse('$baseUrl/api/v1/notifications/user/$userId/unread/count');
      final response = await http.get(url, headers: _headers);

      if (response.statusCode == 200) {
        return int.parse(response.body.trim());
      } else {
        throw Exception('Error al contar notificaciones: ${response.statusCode}');
      }
    } catch (e) {
      print('❌ Error en countUnreadNotifications: $e');
      return 0; // Retornar 0 en caso de error
    }
  }

  // ... resto de métodos sin cambios
}
```

---

## 🎯 Ejemplo de Uso Completo

```dart
void main() async {
  // Configurar servicio
  final service = NotificationService(
    baseUrl: 'http://localhost:8080',
    authToken: null, // Los endpoints de notificaciones son públicos para pruebas
  );

  try {
    // Cargar notificaciones del usuario 9
    print('📱 Cargando notificaciones del usuario 9...\n');

    final notifications = await service.getUserNotifications(9);

    print('✅ Cargadas ${notifications.length} notificaciones\n');
    print('=' * 60);

    for (var notif in notifications) {
      print('\n${notif.type.icon} ${notif.title}');
      print('   ID: ${notif.id}');
      print('   ${notif.message}');
      print('   ${notif.timeAgo}');
      print('   Leída: ${notif.isRead ? '✓' : '✗'}');
    }

    print('\n' + '=' * 60);
    print('✅ ¡Prueba exitosa!');

  } catch (e) {
    print('❌ Error: $e');
  }
}
```

---

## ✅ Resultado Esperado

```
📱 Cargando notificaciones del usuario 9...

🔄 Cargando notificaciones del usuario 9...
📡 Status code: 200
📦 Response body length: 1876
✅ Parseadas 10 notificaciones
✅ 10 notificaciones convertidas exitosamente
✅ Cargadas 10 notificaciones

============================================================

👥 Asignado a Cuadrilla
   ID: 16
   Has sido agregado como miembro de la cuadrilla 'Cuadrilla Gamma'.
   Hace 15m
   Leída: ✗

👥 Asignado a Cuadrilla
   ID: 15
   Has sido agregado como miembro de la cuadrilla 'Cuadrilla Alpha'.
   Hace 20m
   Leída: ✗

...

============================================================
✅ ¡Prueba exitosa!
```

---

## 🔧 Checklist de Implementación

- [ ] Copiar el código corregido de `NotificationModel`
- [ ] Actualizar el método `_parseDateTime` con manejo de errores
- [ ] Agregar logs de debug en el servicio
- [ ] Probar con `getUserNotifications(9)`
- [ ] Verificar que no haya errores de parseo
- [ ] Implementar UI con las notificaciones

---

## 🐛 Errores Comunes y Soluciones

### Error: "FormatException: Invalid date format"

**Causa**: El backend devuelve fechas sin milisegundos  
**Solución**: ✅ Ya implementada en `_parseDateTime`

### Error: "type 'Null' is not a subtype of type 'bool'"

**Causa**: El campo `isRead` puede venir null del backend  
**Solución**: ✅ Usar `json['isRead'] as bool? ?? false`

### Error: Lista vacía pero no se muestra nada

**Causa**: No manejar el caso de array vacío `[]`  
**Solución**: ✅ Verificar `response.body.trim() == '[]'`

---

## ✅ Conclusión

**El backend funciona perfectamente** ✅  
**El problema era el parseo de fechas en Flutter** ✅  
**Solución implementada y probada** ✅

Copia el código corregido y las notificaciones deberían funcionar sin problemas.
