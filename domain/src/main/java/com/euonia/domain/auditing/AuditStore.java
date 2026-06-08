package com.euonia.domain.auditing;

/**
 * Interface for storing audit records. Implementations can choose to store records in a database, file system, or any other storage mechanism.
 * The save method is generic and can handle any type of audit record that extends the AuditRecord interface, allowing for flexibility in the types of records that can be stored.
 * The ID type is also generic, allowing for different types of identifiers to be used for audit records, such as Long, String, UUID, etc.
 */
public interface AuditStore {
    <T extends AuditRecord<ID>, ID extends Comparable<ID>> void save(T record);
}
