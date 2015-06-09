package com.sunchao.event;

import java.util.HashSet;
import java.util.Set;
/**
 * <p>The class representing a type of event</p>
 * @author sunchao
 *
 * @param <T> the event which extends event
 */
public class EventType<T extends Event> {
	
	/**  The format output of the event type for{@code toString()}. */
	private static final String FMT_TO_STRING = "%s [ %s ]";
	
	/** Stores the super event type of this type. */
	private final EventType<? super Event> superEventType;
	
	/** The name of the type */
	private final String name;
    /**
     * Build a new instance of {@code EventType} and initialize the instance with
     * the superEventType and the type name, if the superEventType is null, that represents
     * the event type is the root type.
     * @param superEventType
     *                      the super type of this.
     * @param eventName
     *                     the name of event type.
     */
	public EventType(EventType<? super Event> superEventType, String eventName)
	{
		this.superEventType = superEventType;
		this.name = eventName;
	}
	
	/**
	 * @return 
	 *          the super type of this
	 */
	public EventType<? super T> getSuperType()
	{
	      return this.superEventType;	
	}
	/**
	 * 
	 * @return
	 *         return the event type's name
	 */
	public String getName()
	{
		return this.name;
	}
	
	@Override
	public String toString()
	{
		return String.format(FMT_TO_STRING, getClass().getSimpleName(), getName());
	}
	
	
	/**
	 * Returns all of the super event types of the specified event type,
	 * which contains the directed and the indirected super event, until 
	 * "null", if the specified event type is <b>null</b>,return a <a>empty</a>
	 *  set .
	 * 
	 * @param eventType
	 *                 the specified event type.
	 * @return
	 *          the set which contains all of the super types;
	 */
	public static Set<EventType<?>> fetchSuperEventType(EventType<?> eventType)
	{
		Set<EventType<?>> set = new HashSet<EventType<?>>();
		EventType<?> currentType = eventType;
		while (currentType != null)
		{
			set.add(currentType);
			currentType = currentType.getSuperType();
		}
		return set;
	}
	
	/**
	 * Checks the event type is derived from another type. The implementation
	 * tests whether the {@code baseType} directly or indirectly super of the 
	 * {@code derivedType}. if one of the two parameters is null, and the reult
	 * return <b>false</b>
	 * @param derivedType
	 * @param baseType
	 * @return whether the derivedType is subType of the baseType
	 */
	public static boolean isInstanceOf(EventType<?> derivedType,
			EventType<?> baseType)
   {
		EventType<?> currentType = derivedType;
		while(currentType != null)
		{
			if(currentType == baseType)
				return true;
			currentType = currentType.getSuperType();
		}
		return false;
   }
}
