package com.euonia.domain.event;

/**
 * ApplicationEvent is a marker interface for events that are published within the application context. It extends the base Event interface and can be used to categorize events that are specific to the application's domain logic.
 * By implementing this interface, events can be easily identified and handled by event listeners that are designed to process application-specific events. This allows for better organization and separation of concerns within the event-driven architecture of the application.
 */
public interface ApplicationEvent extends Event {
}
