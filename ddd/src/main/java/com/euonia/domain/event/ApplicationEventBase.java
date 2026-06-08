package com.euonia.domain.event;

/**
 * ApplicationEventBase is an abstract class that serves as a base implementation for application events. It extends the EventBase class and implements the ApplicationEvent interface, providing a common foundation for all application-specific events in the domain model.
 * This class can be extended by concrete event classes that represent specific events within the application, allowing for consistent handling and processing of application events while still providing the flexibility to define event-specific properties and behaviors as needed.
 */
public abstract class ApplicationEventBase extends EventBase implements ApplicationEvent {
}
