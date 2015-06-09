package com.sunchao.event;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DefaultEventSource implements EventSource{
	
	/**The list for managing the  registered event listeners */
	private EventListenerList eventListeners;
	
	/**The Object lock which controls the access of the counter of events  */
	private final Object lockDetailEventsCount = new Object();
	
	/**The counter of detail event number  */
	private int detailEvents;
	
	/**
	 * initialize the event listeners
	 */
	public DefaultEventSource()
	{
		initListeners();
	}
	
	 /**
	  * Return a collection of event listens for the defined
	  * event.Note:there use jdk generic type to controls the 
	  * the event listens which the event belongs to,so the
	  * collection will contains the defined registered event
	  * listeners with the event,also contains the event 
	  * listeners superclass(this event)'s event listeners.
	  * because all the event listeners belong to the father,
	  * and can used to the son.
	  * 
	  * @param eventType
	  *            the event type
	  * @return
	  *            the unmodifiable collection of event listens(snapshot)
	  */
	public <T extends Event> Collection<EventListener<? super T>> getEventListeners(
			EventType<T> eventType)
	{
	    List<EventListener<? super T>> result = 
	    		new LinkedList<EventListener<? super T>>();
	    for(EventListener<? super T> l : eventListeners
	    		.getEventListeners(eventType))
	    {
	    	result.add(l);
	    }
	    return Collections.unmodifiableCollection(result);
	}

	
	/**
	 * The method is used by access all the registrationData{@see EventListenerRegistration},
	 * which contains the tuple(event, event listener),also the method access the metadata
	 * without the event type,in other case, the method is independent of the event type.
	 * 
	 * @return
	 *        the metadata of the registration data.
	 */ 
	public List<EventListenerRegistrationData<?>> getEventListenerRegistrations()
	{
		return eventListeners.getRegistrations();
	}
	
	/** Return whether the detail event was generated */
	public boolean isDetailEvents()
	{
		return checkDetailEvents(0);
	}
	
	
	 /**
	  * The method recodes the counter of calls number
	  * @param enable
	  *              a flag which represent whether the details are enable
	  *              or disable.
	  */
	public void setDetailEvents(boolean enable)
	{
		synchronized(lockDetailEventsCount)
		{
			if (enable)
			{
				detailEvents++;
			}
			else
			{
				detailEvents--;
			}
		}
	}
	
	private void initListeners() 
	{
	    eventListeners = new EventListenerList();		
	}

	@Override
	public <T extends Event> void addEventListener(EventType<T> eventType,
			EventListener<? super T> listener) {
		eventListeners.addEventListener(eventType, listener);
		
	}

	@Override
	public <T extends Event> boolean removeEventListener(
			EventType<T> eventType, EventListener<? super T> listener) {
		return eventListeners.removeEventListener(eventType, listener);
	}
	
     /**
      * clear the list of event listener.
      */
	public void clearEventListeners()
	{
		eventListeners.clear();
	}
	
	 /**
	  * 
	  */
	public void clearErrorListeners()
	{
	    for(EventListenerRegistrationData<? extends DefaultErrorEvent> rgData : 
	    	eventListeners.getRegistrationsForSuperType(DefaultErrorEvent.ERROR))
	    {
	    	eventListeners.removeEventListener(rgData);
	    }
	}
	
	/**
	 * copy the event listeners from this.eventlistener.
	 * 
	 * @param source
	 *              the source which the copied event listeners
	 *              belong to.
	 */
	public void copyEventListeners(DefaultEventSource source)
	{
		if (source == null)
		{
			throw new IllegalArgumentException(
					"Target event source must not be null!");
		}
		source.eventListeners.addAll(eventListeners);
	}
	
	/**
	 * Make an even and delivers it to all the registered listeners, 
	 * firstly check sending an event whether allowed, and if there
	 * is listener which registered the event type existed.
	 * @param type
	 *             the event type.
	 * @param attachment
	 *             the attachment information.
	 */
	protected <T extends DefaultEvent> void fireEvent(final EventType<T> type,
		   final  Map<String, Object> attachment)
    {
		if (checkDetailEvents(-1)) 
		{
			EventListenerList.EventListenerIterator<T> it =
			      eventListeners.getEventListenerIterator(type);
			if (it.hasNext())
			{
				DefaultEvent defaultEvent = createEvent(type, attachment);
				while (it.hasNext())
				{
					it.invokeNext(defaultEvent);
				}
			}
		}
	}
	
	/**
	 * Creates an new event.
	 * 
	 * @param eventType
	 *             the event type.
	 * @param attachment
	 *             the attachment information.
	 * @return
	 *             the created event.
	 */
	protected <T extends DefaultEvent> DefaultEvent createEvent(
			EventType<T> eventType, Map<String, Object> attachment)
	{
		return new DefaultEvent(this, eventType, attachment);
	}
	
	/**
	 * Makes an new error(exception) event,and delivers to all the
	 * registered event listeners.
	 * @param type
	 *            the event type.
	 * @param opType
	 *            the operation which occurs an exception.
	 * @param attachment
	 *            the attachment information.
	 * @param cause
	 *            the stored exception.
	 */
	public <T extends DefaultErrorEvent> void fireError(EventType<T> type,
			EventType<?> opType, Map<String, Object> attachment, Throwable cause)
	{
         EventListenerList.EventListenerIterator<T> it = 
        		 eventListeners.getEventListenerIterator(type);	
         if (it.hasNext())
         {
        	 DefaultErrorEvent errorEvent = createErrorEvent(
        			 type, opType, attachment, cause);
        	 while (it.hasNext())
        	 {
        		 it.invokeNext(errorEvent);
        	 }
         }
	}
	
	/**
	 * Cope the event source.
	 */
	@Override
	protected DefaultEventSource clone() throws CloneNotSupportedException {
		DefaultEventSource copy = (DefaultEventSource) super.clone();
		copy.initListeners();
		return copy;
	}
	
	/**
	 * Creates an new error event.
	 * 
	 * @param type
	 *            the event type.
	 * @param opType
	 *            the operation type ocurrs
	 * @param attachment
	 *            the attachment information
	 * @param cause
	 *            the exception stored in the event.
	 * @return
	 *            an new error event.
	 */
	protected <T extends DefaultErrorEvent> DefaultErrorEvent createErrorEvent(
			EventType<? extends DefaultErrorEvent> type, EventType<?> opType, 
			Map<String, Object> attachment,Throwable cause)
	{
		 return new DefaultErrorEvent(this, type, opType, attachment, cause);
	}
	
	/**
	 * 
	 * @param limit
	 * @return
	 */
	private boolean checkDetailEvents(int limit)
	{
		synchronized (lockDetailEventsCount)
		{
			return detailEvents > limit;
		}
	}
}
