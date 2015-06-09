package com.sunchao.event;

/**
 * <p>
 * An interface for configuration implementation which support registration of
 * event listeners
 * </p>
 * <p>
 * Through the methods provided by interface it is possible to  register and
 * remove listeners for different events supported by the library. The event
 * type to be handled by a listener must be provided; the specified event listener
 * must be compatible with this event type. By using generic type parameters, the
 * compiler can check this.
 * </p>
 * @author sunchao
 *
 */
public interface EventSource {
	/**
	 * Adds an event listener for the specified event type.This listener is 
	 * notified about events of this type and all its sub types.
	 * 
	 * @param eventType 
	 *                 the event type(must not be<b>null</b>)
	 * @param listener
	 *                 the listener to be registered(must not be <b>null</b>)
	 * @throws IllegalArgumentException
	 *                  if a required parameter is null.                
	 */
	<T extends Event> void addEventListener(EventType<T> eventType,
			EventListener<? super T> listener);
	
	/**
	 * Removes the event listener registration for the given event type and
	 * listener. An event listener instance may be registered multiple times for
	 * different event types. Therefore, when removing a listener the event type
	 * of the registration in question was removed. A value of <b>false</b>
	 * means that no such combination of event type and listener was found
	 * @param eventType
	 *                 the event type
	 * @param listener
	 *                 the event listener to be removed
	 * @return
	 *                 whether a listener registration was removed
	 */
	<T extends Event> boolean removeEventListener(EventType<T> eventType,
			EventListener<? super T> listener);

}
