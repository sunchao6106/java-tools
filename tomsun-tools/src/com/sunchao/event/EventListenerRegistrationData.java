package com.sunchao.event;


/**
 *<p>
 * A data class holding information about an event listener registration.
 *</p> 
 *<p>
 *An instance of this class stores all information required to determine
 *whether a specific event listener is to be invoked for a given event. The
 *class is used internally by{@link EventListenerList}, but is also useful in
 *general when information about event listeners is to be stored.
 *</p>
 *<p>
 *Implementation note: Instance of this class are immutable and safely be 
 *shared between multiple threads or components
 *</p>
 * @author sunchao
 *
 * @param <T>
 */

public final class EventListenerRegistrationData <T extends Event> {
    /**  The event type */
	private final EventType<T> eventType;
	
	 /** The event listener */
	private final EventListener<? super T> listener;
	
	/**
	 *creates a new instance of {@code EventListenerRegistrationData} 
	 *
	 * @param type
	 * @param listener
	 */
	public EventListenerRegistrationData(EventType<T> type,
			EventListener<? super T> listener)
	{
	     if(type == null)
	     {
	    	 throw new IllegalArgumentException("Event type must not null");
	     }
	     if(listener == null)
	     {
	    	 throw new IllegalArgumentException("Listener to be registered must not be null!");
	     }
	    	 
	     this.eventType = type;
	     this.listener = listener;
	}
	
	
	public EventType<T>  getEventType() {
		return  this.eventType;
	}
	
	public EventListener<? super T> getListener() {
		return this.listener;
	}
	
	@Override
	public int hashCode() {
		int result  = eventType.hashCode();
		result = 31 * result + listener.hashCode();
		return result;
	}
	
	@Override 
	public boolean equals(Object obj) {
		if (this == obj)
		{
			return true;
		}
		if(!(obj instanceof EventListenerRegistrationData))
		{
			return false;
		}
		
		EventListenerRegistrationData<?> c =  
				(EventListenerRegistrationData<?>) obj;
		return getListener() == c.getListener() 
				&& getEventType() == c.getEventType();
	}
}
