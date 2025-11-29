package com.warehouse.warehousemanager.repository;

import com.warehouse.warehousemanager.entity.TrustLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrustLogRepository extends JpaRepository<TrustLog, Long> {
    List<TrustLog> findByUserId(Long userId);
    Page<TrustLog> findByUserId(Long userId, Pageable pageable);
    List<TrustLog> findByUsername(String username);
    Page<TrustLog> findByUsername(String username, Pageable pageable);
    List<TrustLog> findByResource(String resource);
    Page<TrustLog> findByResource(String resource, Pageable pageable);
    List<TrustLog> findByAction(String action);
    Page<TrustLog> findByAction(String action, Pageable pageable);
    List<TrustLog> findByDecisionResult(Boolean decisionResult);
    Page<TrustLog> findByDecisionResult(Boolean decisionResult, Pageable pageable);
    List<TrustLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    Page<TrustLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT t FROM TrustLog t WHERE t.userId = :userId AND t.timestamp BETWEEN :start AND :end")
    List<TrustLog> findByUserIdAndTimestampBetween(
        @Param("userId") Long userId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT t FROM TrustLog t WHERE t.userId = :userId AND t.timestamp BETWEEN :start AND :end")
    Page<TrustLog> findByUserIdAndTimestampBetween(
        @Param("userId") Long userId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        Pageable pageable
    );

    @Query("SELECT t FROM TrustLog t WHERE t.username = :username AND t.timestamp BETWEEN :start AND :end")
    List<TrustLog> findByUsernameAndTimestampBetween(
        @Param("username") String username,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT t FROM TrustLog t WHERE t.username = :username AND t.timestamp BETWEEN :start AND :end")
    Page<TrustLog> findByUsernameAndTimestampBetween(
        @Param("username") String username,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        Pageable pageable
    );

    @Query("SELECT t FROM TrustLog t WHERE " +
           "(:resource IS NULL OR t.resource = :resource) AND " +
           "(:action IS NULL OR t.action = :action) AND " +
           "(:decisionResult IS NULL OR t.decisionResult = :decisionResult) AND " +
           "(:userId IS NULL OR t.userId = :userId) AND " +
           "(:username IS NULL OR t.username = :username)")
    Page<TrustLog> findByFilters(
        @Param("resource") String resource,
        @Param("action") String action,
        @Param("decisionResult") Boolean decisionResult,
        @Param("userId") Long userId,
        @Param("username") String username,
        Pageable pageable
    );

}