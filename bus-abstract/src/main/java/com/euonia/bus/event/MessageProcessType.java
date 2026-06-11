package com.euonia.bus.event;

/**
 * MessageProcessType is an enumeration that defines the types of message processing events that can occur in a messaging system.
 * It includes three values: SEND, DISPATCH, and RECEIVE, which represent the different stages of message processing.
 * This enum can be used to categorize and handle events related to message processing in a consistent manner across the messaging system.
 */
public enum MessageProcessType {
    SEND,
    DELIVERED,
    RECEIVED
}
