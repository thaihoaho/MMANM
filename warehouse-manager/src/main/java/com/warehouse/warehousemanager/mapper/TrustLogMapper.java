package com.warehouse.warehousemanager.mapper;

import com.warehouse.warehousemanager.dto.TrustLogDto;
import com.warehouse.warehousemanager.entity.TrustLog;

public class TrustLogMapper {

    public static TrustLogDto toDto(TrustLog trustLog) {
        if (trustLog == null) {
            return null;
        }

        return new TrustLogDto(
            trustLog.getId(),
            trustLog.getUserId(),
            trustLog.getUsername(),
            trustLog.getResource(),
            trustLog.getAction(),
            trustLog.getIpAddress(),
            trustLog.getTrustScore(),
            trustLog.getDecisionResult(),
            trustLog.getReason(),
            trustLog.getTimestamp()
        );
    }
}