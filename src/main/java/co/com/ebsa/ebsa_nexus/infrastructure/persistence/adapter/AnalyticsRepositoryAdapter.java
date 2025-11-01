package co.com.ebsa.ebsa_nexus.infrastructure.persistence.adapter;

import co.com.ebsa.ebsa_nexus.domain.enums.NoveltyStatus;
import co.com.ebsa.ebsa_nexus.domain.repository.AnalyticsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Adapter implementing AnalyticsRepository using JPA and native queries.
 * 
 * @author EBSA Nexus Team
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class AnalyticsRepositoryAdapter implements AnalyticsRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Long countNovelties(LocalDateTime startDate, LocalDateTime endDate, Long areaId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM novelties WHERE 1=1");
        
        if (startDate != null) {
            sql.append(" AND created_at >= :startDate");
        }
        if (endDate != null) {
            sql.append(" AND created_at <= :endDate");
        }
        if (areaId != null) {
            sql.append(" AND area_id = :areaId");
        }
        
        Query query = entityManager.createNativeQuery(sql.toString());
        
        if (startDate != null) query.setParameter("startDate", startDate);
        if (endDate != null) query.setParameter("endDate", endDate);
        if (areaId != null) query.setParameter("areaId", areaId);
        
        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public Map<String, Long> countByStatus(LocalDateTime startDate, LocalDateTime endDate, Long areaId) {
        StringBuilder sql = new StringBuilder(
            "SELECT status, COUNT(*) as count FROM novelties WHERE 1=1"
        );
        
        if (startDate != null) sql.append(" AND created_at >= :startDate");
        if (endDate != null) sql.append(" AND created_at <= :endDate");
        if (areaId != null) sql.append(" AND area_id = :areaId");
        sql.append(" GROUP BY status");
        
        Query query = entityManager.createNativeQuery(sql.toString());
        
        if (startDate != null) query.setParameter("startDate", startDate);
        if (endDate != null) query.setParameter("endDate", endDate);
        if (areaId != null) query.setParameter("areaId", areaId);
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        return results.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()
            ));
    }

    @Override
    public Map<String, Long> countByArea(LocalDateTime startDate, LocalDateTime endDate) {
        StringBuilder sql = new StringBuilder(
            "SELECT a.code, COUNT(n.id) as count " +
            "FROM areas a " +
            "LEFT JOIN novelties n ON a.id = n.area_id"
        );
        
        if (startDate != null || endDate != null) {
            sql.append(" WHERE ");
            List<String> conditions = new ArrayList<>();
            if (startDate != null) conditions.add("n.created_at >= :startDate");
            if (endDate != null) conditions.add("n.created_at <= :endDate");
            sql.append(String.join(" AND ", conditions));
        }
        
        sql.append(" GROUP BY a.code");
        
        Query query = entityManager.createNativeQuery(sql.toString());
        
        if (startDate != null) query.setParameter("startDate", startDate);
        if (endDate != null) query.setParameter("endDate", endDate);
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        return results.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()
            ));
    }

    @Override
    public Map<String, Long> countByReason(LocalDateTime startDate, LocalDateTime endDate, Long areaId) {
        StringBuilder sql = new StringBuilder(
            "SELECT reason, COUNT(*) as count FROM novelties WHERE 1=1"
        );
        
        if (startDate != null) sql.append(" AND created_at >= :startDate");
        if (endDate != null) sql.append(" AND created_at <= :endDate");
        if (areaId != null) sql.append(" AND area_id = :areaId");
        sql.append(" GROUP BY reason");
        
        Query query = entityManager.createNativeQuery(sql.toString());
        
        if (startDate != null) query.setParameter("startDate", startDate);
        if (endDate != null) query.setParameter("endDate", endDate);
        if (areaId != null) query.setParameter("areaId", areaId);
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        return results.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()
            ));
    }

    @Override
    public Double calculateAverageResolutionTime(LocalDateTime startDate, LocalDateTime endDate, Long areaId) {
        StringBuilder sql = new StringBuilder(
            "SELECT AVG(TIMESTAMPDIFF(HOUR, created_at, completed_at)) as avg_hours " +
            "FROM novelties " +
            "WHERE status = 'COMPLETADA' AND completed_at IS NOT NULL"
        );
        
        if (startDate != null) sql.append(" AND created_at >= :startDate");
        if (endDate != null) sql.append(" AND created_at <= :endDate");
        if (areaId != null) sql.append(" AND area_id = :areaId");
        
        Query query = entityManager.createNativeQuery(sql.toString());
        
        if (startDate != null) query.setParameter("startDate", startDate);
        if (endDate != null) query.setParameter("endDate", endDate);
        if (areaId != null) query.setParameter("areaId", areaId);
        
        Object result = query.getSingleResult();
        return result != null ? ((Number) result).doubleValue() : 0.0;
    }

    @Override
    public Long countResolvedNovelties(LocalDateTime startDate, LocalDateTime endDate, Long areaId) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM novelties WHERE status = 'COMPLETADA'"
        );
        
        if (startDate != null) sql.append(" AND created_at >= :startDate");
        if (endDate != null) sql.append(" AND created_at <= :endDate");
        if (areaId != null) sql.append(" AND area_id = :areaId");
        
        Query query = entityManager.createNativeQuery(sql.toString());
        
        if (startDate != null) query.setParameter("startDate", startDate);
        if (endDate != null) query.setParameter("endDate", endDate);
        if (areaId != null) query.setParameter("areaId", areaId);
        
        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public Long countPendingNovelties(LocalDateTime startDate, LocalDateTime endDate, Long areaId) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM novelties WHERE status IN ('CREADA', 'EN_CURSO')"
        );
        
        if (startDate != null) sql.append(" AND created_at >= :startDate");
        if (endDate != null) sql.append(" AND created_at <= :endDate");
        if (areaId != null) sql.append(" AND area_id = :areaId");
        
        Query query = entityManager.createNativeQuery(sql.toString());
        
        if (startDate != null) query.setParameter("startDate", startDate);
        if (endDate != null) query.setParameter("endDate", endDate);
        if (areaId != null) query.setParameter("areaId", areaId);
        
        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public List<Map<String, Object>> getNoveltyTrends(String period, LocalDateTime startDate, LocalDateTime endDate, Long areaId) {
        String dateFormat;
        switch (period.toLowerCase()) {
            case "daily":
                dateFormat = "%Y-%m-%d";
                break;
            case "weekly":
                dateFormat = "%Y-%u";
                break;
            case "monthly":
            default:
                dateFormat = "%Y-%m";
                break;
        }
        
        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "  DATE_FORMAT(created_at, '" + dateFormat + "') as period, " +
            "  COUNT(*) as created, " +
            "  SUM(CASE WHEN status = 'COMPLETADA' THEN 1 ELSE 0 END) as completed, " +
            "  SUM(CASE WHEN status = 'CANCELADA' THEN 1 ELSE 0 END) as cancelled " +
            "FROM novelties WHERE 1=1"
        );
        
        if (startDate != null) sql.append(" AND created_at >= :startDate");
        if (endDate != null) sql.append(" AND created_at <= :endDate");
        if (areaId != null) sql.append(" AND area_id = :areaId");
        sql.append(" GROUP BY period ORDER BY period");
        
        Query query = entityManager.createNativeQuery(sql.toString());
        
        if (startDate != null) query.setParameter("startDate", startDate);
        if (endDate != null) query.setParameter("endDate", endDate);
        if (areaId != null) query.setParameter("areaId", areaId);
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        return results.stream()
            .map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("period", row[0]);
                map.put("created", ((Number) row[1]).longValue());
                map.put("completed", ((Number) row[2]).longValue());
                map.put("cancelled", ((Number) row[3]).longValue());
                return map;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getCrewPerformance(LocalDateTime startDate, LocalDateTime endDate, Long crewId) {
        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "  c.id as crew_id, " +
            "  c.name as crew_name, " +
            "  COUNT(DISTINCT na.novelty_id) as assigned_novelties, " +
            "  COUNT(DISTINCT CASE WHEN n.status = 'COMPLETADA' THEN n.id END) as completed_novelties, " +
            "  COUNT(DISTINCT CASE WHEN n.status IN ('CREADA', 'EN_CURSO') THEN n.id END) as pending_novelties, " +
            "  AVG(CASE WHEN n.status = 'COMPLETADA' AND n.completed_at IS NOT NULL " +
            "    THEN TIMESTAMPDIFF(HOUR, n.created_at, n.completed_at) END) as avg_resolution_hours, " +
            "  (COUNT(DISTINCT cm.user_id)) as member_count " +
            "FROM crews c " +
            "LEFT JOIN novelty_assignments na ON c.id = na.assigned_crew_id " +
            "LEFT JOIN novelties n ON na.novelty_id = n.id " +
            "LEFT JOIN crew_members cm ON c.id = cm.crew_id AND cm.left_at IS NULL " +
            "WHERE c.deleted_at IS NULL"
        );
        
        if (crewId != null) sql.append(" AND c.id = :crewId");
        if (startDate != null) sql.append(" AND n.created_at >= :startDate");
        if (endDate != null) sql.append(" AND n.created_at <= :endDate");
        sql.append(" GROUP BY c.id, c.name");
        
        Query query = entityManager.createNativeQuery(sql.toString());
        
        if (crewId != null) query.setParameter("crewId", crewId);
        if (startDate != null) query.setParameter("startDate", startDate);
        if (endDate != null) query.setParameter("endDate", endDate);
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        return results.stream()
            .map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("crewId", ((Number) row[0]).longValue());
                map.put("crewName", row[1]);
                map.put("assignedNovelties", ((Number) row[2]).longValue());
                map.put("completedNovelties", ((Number) row[3]).longValue());
                map.put("pendingNovelties", ((Number) row[4]).longValue());
                map.put("averageResolutionTimeHours", row[5] != null ? ((Number) row[5]).doubleValue() : 0.0);
                map.put("memberCount", ((Number) row[6]).intValue());
                
                Long assigned = ((Number) row[2]).longValue();
                Long completed = ((Number) row[3]).longValue();
                map.put("completionRate", assigned > 0 ? (completed * 100.0 / assigned) : 0.0);
                
                return map;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getUserPerformance(LocalDateTime startDate, LocalDateTime endDate, Long userId, Long workRoleId) {
        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "  u.id as user_id, " +
            "  CONCAT(u.first_name, ' ', u.last_name) as full_name, " +
            "  wr.name as work_role, " +
            "  COUNT(DISTINCT CASE WHEN n.created_by = u.id THEN n.id END) as created_novelties, " +
            "  COUNT(DISTINCT CASE WHEN n.status = 'COMPLETADA' AND cm.user_id = u.id THEN n.id END) as completed_novelties, " +
            "  COUNT(DISTINCT nr.id) as reports_generated, " +
            "  COUNT(DISTINCT rp.report_id) as participations, " +
            "  AVG(CASE WHEN n.status = 'COMPLETADA' AND n.completed_at IS NOT NULL AND cm.user_id = u.id " +
            "    THEN TIMESTAMPDIFF(HOUR, n.created_at, n.completed_at) END) as avg_resolution_hours " +
            "FROM users u " +
            "LEFT JOIN work_roles wr ON u.work_role_id = wr.id " +
            "LEFT JOIN novelties n ON u.id = n.created_by " +
            "LEFT JOIN novelty_reports nr ON u.id = nr.generated_by " +
            "LEFT JOIN report_participants rp ON u.id = rp.user_id " +
            "LEFT JOIN crew_members cm ON u.id = cm.user_id AND cm.left_at IS NULL " +
            "WHERE u.active = 1"
        );
        
        if (userId != null) sql.append(" AND u.id = :userId");
        if (workRoleId != null) sql.append(" AND u.work_role_id = :workRoleId");
        if (startDate != null) sql.append(" AND n.created_at >= :startDate");
        if (endDate != null) sql.append(" AND n.created_at <= :endDate");
        sql.append(" GROUP BY u.id, u.first_name, u.last_name, wr.name");
        
        Query query = entityManager.createNativeQuery(sql.toString());
        
        if (userId != null) query.setParameter("userId", userId);
        if (workRoleId != null) query.setParameter("workRoleId", workRoleId);
        if (startDate != null) query.setParameter("startDate", startDate);
        if (endDate != null) query.setParameter("endDate", endDate);
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        return results.stream()
            .map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("userId", ((Number) row[0]).longValue());
                map.put("fullName", row[1]);
                map.put("workRole", row[2] != null ? row[2] : "N/A");
                map.put("noveltiesCreated", ((Number) row[3]).longValue());
                map.put("noveltiesCompleted", ((Number) row[4]).longValue());
                map.put("reportsGenerated", ((Number) row[5]).longValue());
                map.put("participationsInReports", ((Number) row[6]).longValue());
                map.put("averageResolutionTimeHours", row[7] != null ? ((Number) row[7]).doubleValue() : 0.0);
                return map;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getNoveltyByMunicipality(LocalDateTime startDate, LocalDateTime endDate, NoveltyStatus status) {
        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "  municipality, " +
            "  COUNT(*) as total_novelties, " +
            "  SUM(CASE WHEN status = 'COMPLETADA' THEN 1 ELSE 0 END) as completed, " +
            "  SUM(CASE WHEN status IN ('CREADA', 'EN_CURSO') THEN 1 ELSE 0 END) as pending " +
            "FROM novelties WHERE 1=1"
        );
        
        if (startDate != null) sql.append(" AND created_at >= :startDate");
        if (endDate != null) sql.append(" AND created_at <= :endDate");
        if (status != null) sql.append(" AND status = :status");
        sql.append(" GROUP BY municipality ORDER BY total_novelties DESC");
        
        Query query = entityManager.createNativeQuery(sql.toString());
        
        if (startDate != null) query.setParameter("startDate", startDate);
        if (endDate != null) query.setParameter("endDate", endDate);
        if (status != null) query.setParameter("status", status.name());
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        return results.stream()
            .map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("municipality", row[0]);
                map.put("totalNovelties", ((Number) row[1]).longValue());
                map.put("completed", ((Number) row[2]).longValue());
                map.put("pending", ((Number) row[3]).longValue());
                return map;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getTopCrews(Integer limit, LocalDateTime startDate, LocalDateTime endDate, String sortBy) {
        String orderClause = switch (sortBy != null ? sortBy.toLowerCase() : "completionrate") {
            case "totalcompleted" -> "completed_novelties DESC";
            case "averagetime" -> "avg_resolution_hours ASC";
            default -> "completion_rate DESC";
        };
        
        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "  c.id, " +
            "  c.name, " +
            "  COUNT(DISTINCT CASE WHEN n.status = 'COMPLETADA' THEN n.id END) as completed_novelties, " +
            "  COUNT(DISTINCT na.novelty_id) as assigned_novelties, " +
            "  AVG(CASE WHEN n.status = 'COMPLETADA' AND n.completed_at IS NOT NULL " +
            "    THEN TIMESTAMPDIFF(HOUR, n.created_at, n.completed_at) END) as avg_resolution_hours, " +
            "  CASE WHEN COUNT(DISTINCT na.novelty_id) > 0 " +
            "    THEN (COUNT(DISTINCT CASE WHEN n.status = 'COMPLETADA' THEN n.id END) * 100.0 / COUNT(DISTINCT na.novelty_id)) " +
            "    ELSE 0 END as completion_rate " +
            "FROM crews c " +
            "LEFT JOIN novelty_assignments na ON c.id = na.assigned_crew_id " +
            "LEFT JOIN novelties n ON na.novelty_id = n.id " +
            "WHERE c.deleted_at IS NULL"
        );
        
        if (startDate != null) sql.append(" AND n.created_at >= :startDate");
        if (endDate != null) sql.append(" AND n.created_at <= :endDate");
        sql.append(" GROUP BY c.id, c.name");
        sql.append(" HAVING assigned_novelties > 0");
        sql.append(" ORDER BY ").append(orderClause);
        sql.append(" LIMIT :limit");
        
        Query query = entityManager.createNativeQuery(sql.toString());
        
        if (startDate != null) query.setParameter("startDate", startDate);
        if (endDate != null) query.setParameter("endDate", endDate);
        query.setParameter("limit", limit != null ? limit : 10);
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        return results.stream()
            .map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", ((Number) row[0]).longValue());
                map.put("name", row[1]);
                map.put("completedNovelties", ((Number) row[2]).longValue());
                map.put("completionRate", row[5] != null ? ((Number) row[5]).doubleValue() : 0.0);
                map.put("averageResolutionTimeHours", row[4] != null ? ((Number) row[4]).doubleValue() : 0.0);
                return map;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getTopUsers(Integer limit, LocalDateTime startDate, LocalDateTime endDate, String sortBy) {
        String orderClause = switch (sortBy != null ? sortBy.toLowerCase() : "completionrate") {
            case "totalcompleted" -> "completed_novelties DESC";
            case "averagetime" -> "avg_resolution_hours ASC";
            default -> "completion_rate DESC";
        };
        
        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "  u.id, " +
            "  CONCAT(u.first_name, ' ', u.last_name) as full_name, " +
            "  COUNT(DISTINCT CASE WHEN n.status = 'COMPLETADA' AND cm.user_id = u.id THEN n.id END) as completed_novelties, " +
            "  COUNT(DISTINCT CASE WHEN cm.user_id = u.id THEN na.novelty_id END) as assigned_novelties, " +
            "  AVG(CASE WHEN n.status = 'COMPLETADA' AND n.completed_at IS NOT NULL AND cm.user_id = u.id " +
            "    THEN TIMESTAMPDIFF(HOUR, n.created_at, n.completed_at) END) as avg_resolution_hours, " +
            "  CASE WHEN COUNT(DISTINCT CASE WHEN cm.user_id = u.id THEN na.novelty_id END) > 0 " +
            "    THEN (COUNT(DISTINCT CASE WHEN n.status = 'COMPLETADA' AND cm.user_id = u.id THEN n.id END) * 100.0 / COUNT(DISTINCT CASE WHEN cm.user_id = u.id THEN na.novelty_id END)) " +
            "    ELSE 0 END as completion_rate " +
            "FROM users u " +
            "LEFT JOIN crew_members cm ON u.id = cm.user_id AND cm.left_at IS NULL " +
            "LEFT JOIN novelty_assignments na ON cm.crew_id = na.assigned_crew_id " +
            "LEFT JOIN novelties n ON na.novelty_id = n.id " +
            "WHERE u.active = 1"
        );
        
        if (startDate != null) sql.append(" AND n.created_at >= :startDate");
        if (endDate != null) sql.append(" AND n.created_at <= :endDate");
        sql.append(" GROUP BY u.id, u.first_name, u.last_name");
        sql.append(" HAVING assigned_novelties > 0");
        sql.append(" ORDER BY ").append(orderClause);
        sql.append(" LIMIT :limit");
        
        Query query = entityManager.createNativeQuery(sql.toString());
        
        if (startDate != null) query.setParameter("startDate", startDate);
        if (endDate != null) query.setParameter("endDate", endDate);
        query.setParameter("limit", limit != null ? limit : 10);
        
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        return results.stream()
            .map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", ((Number) row[0]).longValue());
                map.put("name", row[1]);
                map.put("completedNovelties", ((Number) row[2]).longValue());
                map.put("completionRate", row[5] != null ? ((Number) row[5]).doubleValue() : 0.0);
                map.put("averageResolutionTimeHours", row[4] != null ? ((Number) row[4]).doubleValue() : 0.0);
                return map;
            })
            .collect(Collectors.toList());
    }
}
