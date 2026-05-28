package com.euonia.core;

/**
 * SnowflakeId is a distributed unique ID generator inspired by Twitter's Snowflake algorithm.
 * It generates 64-bit unique IDs based on the current timestamp, a worker ID, a datacenter ID, and a sequence number.
 * The generated IDs are sortable by time and can be generated in a distributed environment without coordination between nodes.
 * The structure of the generated ID is as follows:
 * - 41 bits for the timestamp (in milliseconds since a custom epoch)
 * - 5 bits for the datacenter ID
 * - 5 bits for the worker ID
 * - 12 bits for the sequence number
 * This implementation allows for up to 1024 unique IDs per millisecond per worker and supports up to 32 datacenters and 32 workers per datacenter.
 * The custom epoch is set to January 1, 2021, which allows for a long lifespan of the ID generation without running out of bits for the timestamp.
 */
@SuppressWarnings("unused")
public final class SnowflakeId {
    private static final long EPOCH = 1609459200000L; // 2021-01-01 00:00:00 UTC
    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private final long workerId;
    private final long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private SnowflakeId(long workerId, long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker ID must be between 0 and %d", MAX_WORKER_ID));
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("Datacenter ID must be between 0 and %d", MAX_DATACENTER_ID));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public static synchronized SnowflakeId getInstance(long workerId, long datacenterId) {
        return new SnowflakeId(workerId, datacenterId);
    }

    public static synchronized SnowflakeId getInstance() {
        return new SnowflakeId(0, 0);
    }

    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT)
            | (datacenterId << DATACENTER_ID_SHIFT)
            | (workerId << WORKER_ID_SHIFT)
            | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }
}

