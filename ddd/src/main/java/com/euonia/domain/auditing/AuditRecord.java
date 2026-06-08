package com.euonia.domain.auditing;

import com.euonia.domain.EntityBase;

import java.time.Instant;

public class AuditRecord<ID extends Comparable<ID>> extends EntityBase<ID> {
    private final String entityName;
    private final String entityId;
    private final String action;
    private final Instant timestamp;
    private String comment;
    private String userId;
    private String userName;

    public AuditRecord(String entityName, String entityId, String action, Instant timestamp) {
        this.entityName = entityName;
        this.entityId = entityId;
        this.action = action;
        this.timestamp = timestamp;
    }

    public AuditRecord(String entityName, String entityId, String action) {
        this(entityName, entityId, action, Instant.now());
    }

    public AuditRecord(Class<?> entityClass, String entityId, String action) {
        this(entityClass.getSimpleName(), entityId, action, Instant.now());
    }

    public String getEntityName() {
        return entityName;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getAction() {
        return action;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
