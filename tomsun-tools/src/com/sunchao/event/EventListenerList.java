package com.sunchao.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
/**
 * <p>
 * A class for managing event listeners for an event source.
 * </p>
 * <p>
 * This class allows registering an arbitrary number of event listeners for
 * specific event types. Event types are specified using the {@link EventType}
 * class. Due to the type parameters in method signatures, it is guaranteed that
 * registered listeners are compatible with the event types they are interested
 * in.
 * </p>
 * <p>
 * There are also methods for firing events. Here all registered listeners are
 * determined - based on the event type specified at registration  - which
 * should receive the event to be fired. So basically, the event type at
 * listener registration serves as a filter list. Because of the
 * hierarchical nature of event types it can be determined in a fine-grained way
 * which events are propagated to which listeners. It is also possible to
 * register a listener multiple times for different event types.
 * </p>
 * <p>
 * Implementation note: This class is thread-safe.
 * </p>
 *
 */
public class EventListenerList {
	
	/** The list hold metadata of tuple of({@link Event} , {@link EventListener})      */
	private final List<EventListenerRegistrationData<?>> listeners;
	
	/**
	 * Creates a new instance of {@link EventListenerList}}
	 */
	public EventListenerList() {
		/**
		 * There use the {@code CopyOnWriterArrayList}, because most of the event listeners
		 * added in the initialization phase, so in the concurrent. the collection perform can well.
		 * thread-safe. 
		 */
		listeners = 
				new CopyOnWriteArrayList<EventListenerRegistrationData<?>>();
	}
	
	/**
	 * the method also permit dynamic add the event listener.
	 * add the flexibility of component.
	 * 
	 * @param type
	 *           the event type.
	 * @param listener
	 *            the event listener which match to the event.
	 */
	public <T extends Event> void addEventListener(EventType<T> type,
			EventListener<? super T> listener)// <?  super T> to assure the father's event listener also fit to the son
	{
	
		listeners.add(new EventListenerRegistrationData<T>(type, listener));
	}
	
	public <T extends Event> void addEventListener(
			EventListenerRegistrationData<T> reData)
	{
	       if(reData == null)
	       {
	    	   throw new IllegalArgumentException(
	    			   "EventListenerRegistrationData must not be null!");
	       }
	       
	       listeners.add(reData);
	}
	
	public <T extends Event> boolean removeEventListener(
			EventType<T> eventType, EventListener<? super T> listener)
	{
	       return !(listener == null || eventType  == null)
		          && removeEventListener(new EventListenerRegistrationData<T>
		          (eventType, listener));
	}

	public <T extends Event> boolean removeEventListener(
			EventListenerRegistrationData<T> eventListenerRegistrationData) 
	{
		
		   return listeners.remove(eventListenerRegistrationData);
	}
	
	/**
	 * The method invoke the registered event listeners to
	 * handle the event ,one by one. And if there is not event
	 * listener exist matched to the event, nothing will happen.
	 * 
	 * @param event
	 *            the event need be handled.
	 */
	public void fire(Event event) {
		
		if (event == null)
		{
			throw new IllegalArgumentException(
					"Event to be fired must not be null!");
		}
		
		for (EventListenerIterator<? extends Event>  iterator = 
				getEventListenerIterator(event.getEventType()); iterator
				.hasNext();)
		{
			iterator.invokeNextListenerUnchecked(event);
		}
	}

	/**
	 * Get the iterator of the {@code EventListenerList} decorate with
	 * the underlying {@code CopyOnWriteArrayList} 
	 * 
	 * @param eventType
	 *            the event type
	 * @return
	 *       the iterator of this class.
	 */
	public <T extends Event> Iterable<EventListener<? super T>> getEventListeners(
		    final EventType<T> eventType)
    {
		return new Iterable<EventListener<? super T>>() 
		{
			@Override
			public Iterator<EventListener<? super T>> iterator()
			{
				return getEventListenerIterator(eventType);
			}
		};
	}
	
	/**
	 * @param eventType
	 *             event type
	 * @return
	 *       the iterator of event listener.
	 */
	public <T extends Event> EventListenerIterator<T> getEventListenerIterator(
			EventType<T> eventType)
	{
	     return new EventListenerIterator<T>(listeners.iterator(), eventType);	
	}
	
	/**
	 * Get the collection of the metadata information which contains
	 * the tuple(event, event listener),and return the snapshot of
	 * the collection.
	 * @return
	 *        the metadata information for registered snapshot.
	 */
	public List<EventListenerRegistrationData<?>> getRegistrations()
	{
	     return Collections.unmodifiableList(listeners);	
	}
	
	/**
	 * The method intent to get metadata which contains the tuple
	 * (event, super event type), which contains the super event type direct
	 * and  indirectly and return  as a list.
	 * 
	 * @param eventType
	 *            the event type specified.
	 * @return
     *         the list contains the metadata information.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Event> List<EventListenerRegistrationData<? extends T>>  getRegistrationsForSuperType(
			EventType<T> eventType)
	{
	    Map<EventType<?>, Set<EventType<?>>> superTypes = 
	    		new HashMap<EventType<?>, Set<EventType<?>>>();
	    List<EventListenerRegistrationData<? extends T>> results =
	    		new LinkedList<EventListenerRegistrationData<? extends T>>();
	    
	    for (EventListenerRegistrationData<?> reg : listeners)
	    {
	    	Set<EventType<?>> base = superTypes.get(reg.getEventType());
	    	if (base == null)
	    	{
	    		base  = EventType.fetchSuperEventType(reg.getEventType());
	    		superTypes.put(reg.getEventType(), base);
	    	}
	    	if (base.contains(eventType))//the event type check assure 
	    	{
	    		EventListenerRegistrationData<? extends T> result =
	    				(EventListenerRegistrationData<? extends T>) reg;
	    		results.add(result);
	    	}
	    }
	   return results;
	}
	
	public void addAll(EventListenerList c)
	{
		if (c == null)
		{
			throw new IllegalArgumentException(
				"List to be copied must not be null!");
		}
		
		for (EventListenerRegistrationData<?> regData : c.getRegistrations())
		{
			addEventListener(regData);
		}
	}
	
	
	public void clear() 
	{
	   listeners.clear();	
	}
	
	/**
	 * the callback method call by the iterator
	 * of the event listeners. invoke the event
	 * listener to handle the event.
	 * 
	 * @param listener
	 *           the registered event listener
	 * @param event
	 *           the event which need be handled
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void callListener(EventListener<?> listener, Event event)
	{
	   EventListener rowListener = listener	;
	   rowListener.onEvent(event);
	}
	
	/**
	 * <p>
	 * The class represent the iterator of event listener.
	 * </p>
	 * <p>
	 * when the event was delivered to the event listeners which
	 * were registered on the event,and iterator all of the listeners
	 * to handle the event.
	 * </p>
	 * 
	 * @author sunchao
	 *
	 * @param <T>
	 *          the subclass of event
	 */
	public static class EventListenerIterator<T extends Event> implements 
	          Iterator<EventListener<? super T>> {
		/** the underlying iterator of the underlying list {@link EventListenerList.CopyOnWriterArrayList} */
		private final Iterator<EventListenerRegistrationData<?>> underlyingIterator;
		
		/** the base event type of the event */
		private final EventType<T> baseEventType;
		
		/** a set of event type which base on the baseEventType and contains the  super event types */
		private final Set<EventType<?>> acceptedTypes;
		
		/** the next event listener which will be return and handle the event */
		private EventListener<? super T> nextElement;
		
		/**
		 * 
		 * @param it
		 * @param base
		 */
		private EventListenerIterator(
				Iterator<EventListenerRegistrationData<?>> it, EventType<T> base)
		{
			this.underlyingIterator = it;
			this.baseEventType = base;
			this.acceptedTypes = EventType.fetchSuperEventType(base);
			initNextElement();
		}


		@Override
		public boolean hasNext() 
		{
			return nextElement != null;
		}

		@Override
		public EventListener<? super T> next()
		{
			if (nextElement == null)
			{
				throw new NoSuchElementException("No more event listeners!");
			}
			
			EventListener<? super T> result = nextElement;
			initNextElement();
			return result;
		}

		@Override
		public void remove() 
		{
		   throw  new UnsupportedOperationException(
				   "Removing elements is not supported!");	
		}
		
		/** 
		 * directly to invoke the next event listener
		 * to handle the event
		 * @param event
		 */
		public void invokeNext(Event event) 
		{
			validateEvent(event);
			invokeNextListenerUnchecked(event);
		}
		
		/** 
		 * the method be called by the iterator method{@code next()}
		 * to initialize the next element which will return to
		 * the client.
		 */
		private void initNextElement() 
		{
		    nextElement = null;
		    while (underlyingIterator.hasNext() && nextElement == null)
		    {
		    	EventListenerRegistrationData<?> regData =
		    			underlyingIterator.next();
		    	if (acceptedTypes.contains(regData.getEventType()))
		    	{
		    		nextElement = castListener(regData);
		    	}
		    }
		}
		
		/**
		 * the method is used to validate the variable event
		 * whether suite to the event listener.
		 * @param event
		 *          the event parameter.
		 */
		private void validateEvent(Event event)
		{
			if(event == null
					|| !EventType.fetchSuperEventType(event.getEventType()).contains(
							baseEventType))
			{
				throw new IllegalArgumentException(
						"Event incomptible with listener iteration: " + event);
			}
		}
		
		private void invokeNextListenerUnchecked(Event event) 
		{
			EventListener<? super T> listener = next();
			callListener(listener, event);
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private EventListener<? super T> castListener(
				EventListenerRegistrationData<?> regData)
		{
		     EventListener listener = regData.getListener();
		     return listener;
		}
	}
}
