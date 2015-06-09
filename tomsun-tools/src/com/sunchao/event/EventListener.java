package com.sunchao.event;
/**
 * <p>
 *     Definition of a generic event handler interface.
 * </p>
 * <p>
 *    The interface only define single {@code onEvent()} method.
 *    This simplifies the implementation of the customer event listeners,
 *    {@code EventListern} represent a functional interface.
 *
 * </p>
 * @author sunchao
 *
 * @param <T>
 *            the subClass of Event.
 *            which the type of events the event listener can process
 */
public interface EventListener<T extends Event> {
	/**
	 * Notifier this event hander when a arrival of an new event. Typically,
	 * event listeners are registered at an event source which providing an 
	 * {@link EventType}. The event type acts as a filter;all events matched by 
	 * the filter, were passed to the event listener. The type parameters 
	 * defined in the {@code EventType} and the interface guarantee that the events
	 * delivered to the handler are compatible with the concrete method signature
	 * of {@code onEvent()}
	 * @param event
	 *              the event
	 */
	 void onEvent(T event);

}
