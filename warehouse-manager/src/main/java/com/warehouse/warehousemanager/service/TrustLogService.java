package com.warehouse.warehousemanager.service;

import com.warehouse.warehousemanager.dto.TrustLogDto;
import com.warehouse.warehousemanager.entity.TrustLog;
import com.warehouse.warehousemanager.mapper.TrustLogMapper;
import com.warehouse.warehousemanager.repository.TrustLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrustLogService {

    @Autowired
    private TrustLogRepository trustLogRepository;

    public TrustLog save(TrustLog trustLog) {
        return trustLogRepository.save(trustLog);
    }

    public List<TrustLogDto> findAll() {
        return trustLogRepository.findAll().stream()
                .map(TrustLogMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<TrustLogDto> findPaginated(Pageable pageable) {
        Page<TrustLog> trustLogPage = trustLogRepository.findAll(pageable);
        List<TrustLogDto> trustLogDtoList = trustLogPage.getContent().stream()
                .map(TrustLogMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(trustLogDtoList, pageable, trustLogPage.getTotalElements());
    }

    public Page<TrustLogDto> findPaginatedWithFilters(Pageable pageable, String resource, String action,
                                                      Boolean decisionResult, Long userId, String username) {
        Page<TrustLog> trustLogPage = trustLogRepository.findByFilters(resource, action, decisionResult, userId, username, pageable);
        List<TrustLogDto> trustLogDtoList = trustLogPage.getContent().stream()
                .map(TrustLogMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(trustLogDtoList, pageable, trustLogPage.getTotalElements());
    }

    public List<TrustLog> findByUserId(Long userId) {
        return trustLogRepository.findByUserId(userId);
    }

    public List<TrustLogDto> findByUserIdAsDto(Long userId) {
        return trustLogRepository.findByUserId(userId).stream()
                .map(TrustLogMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<TrustLogDto> findByUserIdPaginated(Long userId, Pageable pageable) {
        Page<TrustLog> trustLogPage = trustLogRepository.findByUserId(userId, pageable);
        List<TrustLogDto> trustLogDtoList = trustLogPage.getContent().stream()
                .map(TrustLogMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(trustLogDtoList, pageable, trustLogPage.getTotalElements());
    }

    public List<TrustLog> findByUsername(String username) {
        return trustLogRepository.findByUsername(username);
    }

    public List<TrustLogDto> findByUsernameAsDto(String username) {
        return trustLogRepository.findByUsername(username).stream()
                .map(TrustLogMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<TrustLogDto> findByUsernamePaginated(String username, Pageable pageable) {
        Page<TrustLog> trustLogPage = trustLogRepository.findByUsername(username, pageable);
        List<TrustLogDto> trustLogDtoList = trustLogPage.getContent().stream()
                .map(TrustLogMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(trustLogDtoList, pageable, trustLogPage.getTotalElements());
    }

    public List<TrustLog> findByResource(String resource) {
        return trustLogRepository.findByResource(resource);
    }

    public List<TrustLogDto> findByResourceAsDto(String resource) {
        return trustLogRepository.findByResource(resource).stream()
                .map(TrustLogMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<TrustLogDto> findByResourcePaginated(String resource, Pageable pageable) {
        Page<TrustLog> trustLogPage = trustLogRepository.findByResource(resource, pageable);
        List<TrustLogDto> trustLogDtoList = trustLogPage.getContent().stream()
                .map(TrustLogMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(trustLogDtoList, pageable, trustLogPage.getTotalElements());
    }

    public List<TrustLog> findByAction(String action) {
        return trustLogRepository.findByAction(action);
    }

    public List<TrustLogDto> findByActionAsDto(String action) {
        return trustLogRepository.findByAction(action).stream()
                .map(TrustLogMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<TrustLogDto> findByActionPaginated(String action, Pageable pageable) {
        Page<TrustLog> trustLogPage = trustLogRepository.findByAction(action, pageable);
        List<TrustLogDto> trustLogDtoList = trustLogPage.getContent().stream()
                .map(TrustLogMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(trustLogDtoList, pageable, trustLogPage.getTotalElements());
    }

    public List<TrustLog> findByDecisionResult(Boolean decisionResult) {
        return trustLogRepository.findByDecisionResult(decisionResult);
    }

    public List<TrustLogDto> findByDecisionResultAsDto(Boolean decisionResult) {
        return trustLogRepository.findByDecisionResult(decisionResult).stream()
                .map(TrustLogMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<TrustLogDto> findByDecisionResultPaginated(Boolean decisionResult, Pageable pageable) {
        Page<TrustLog> trustLogPage = trustLogRepository.findByDecisionResult(decisionResult, pageable);
        List<TrustLogDto> trustLogDtoList = trustLogPage.getContent().stream()
                .map(TrustLogMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(trustLogDtoList, pageable, trustLogPage.getTotalElements());
    }

    public List<TrustLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end) {
        return trustLogRepository.findByTimestampBetween(start, end);
    }

    public List<TrustLogDto> findByTimestampBetweenAsDto(LocalDateTime start, LocalDateTime end) {
        return trustLogRepository.findByTimestampBetween(start, end).stream()
                .map(TrustLogMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<TrustLogDto> findByTimestampBetweenPaginated(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        Page<TrustLog> trustLogPage = trustLogRepository.findByTimestampBetween(start, end, pageable);
        List<TrustLogDto> trustLogDtoList = trustLogPage.getContent().stream()
                .map(TrustLogMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(trustLogDtoList, pageable, trustLogPage.getTotalElements());
    }

    public List<TrustLog> findByUserIdAndTimestampBetween(Long userId, LocalDateTime start, LocalDateTime end) {
        return trustLogRepository.findByUserIdAndTimestampBetween(userId, start, end);
    }

    public List<TrustLogDto> findByUserIdAndTimestampBetweenAsDto(Long userId, LocalDateTime start, LocalDateTime end) {
        return trustLogRepository.findByUserIdAndTimestampBetween(userId, start, end).stream()
                .map(TrustLogMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<TrustLogDto> findByUserIdAndTimestampBetweenPaginated(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        Page<TrustLog> trustLogPage = trustLogRepository.findByUserIdAndTimestampBetween(userId, start, end, pageable);
        List<TrustLogDto> trustLogDtoList = trustLogPage.getContent().stream()
                .map(TrustLogMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(trustLogDtoList, pageable, trustLogPage.getTotalElements());
    }

    public List<TrustLog> findByUsernameAndTimestampBetween(String username, LocalDateTime start, LocalDateTime end) {
        return trustLogRepository.findByUsernameAndTimestampBetween(username, start, end);
    }

    public List<TrustLogDto> findByUsernameAndTimestampBetweenAsDto(String username, LocalDateTime start, LocalDateTime end) {
        return trustLogRepository.findByUsernameAndTimestampBetween(username, start, end).stream()
                .map(TrustLogMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<TrustLogDto> findByUsernameAndTimestampBetweenPaginated(String username, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        Page<TrustLog> trustLogPage = trustLogRepository.findByUsernameAndTimestampBetween(username, start, end, pageable);
        List<TrustLogDto> trustLogDtoList = trustLogPage.getContent().stream()
                .map(TrustLogMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(trustLogDtoList, pageable, trustLogPage.getTotalElements());
    }
}