package com.sunchao.event;

import java.util.EventObject;

@SuppressWarnings("serial")
public class Event extends EventObject{

	public static final EventType<Event> ANY =
			new EventType <Event>(null,"ANY"); 
	
	
	private static final String FMT_PROPERY = " %s=%s";
	
	private static final int BUF_SIZE = 256;
	
	private final EventType<? extends Event> eventType;
	
	public Event(Object source, EventType<? extends Event> evType) {
		
		super(source);
		if (evType == null)
		{
			throw new IllegalArgumentException("Event type must not be null !");
		}
		eventType = evType;
	}

	public EventType<? extends Event> getEventType() 
	{
		return eventType;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(BUF_SIZE);
		sb.append(getClass().getSimpleName());
		sb.append(" [");
		appendPropertyRepresentation(sb, "source", getSource());
		appendPropertyRepresentation(sb, "eventType", getEventType());
		sb.append(" ]");
		return sb.toString();
	}
	
	protected void appendPropertyRepresentation(StringBuilder buf,
			String property, Object value)
	{
        buf.append(String.format(FMT_PROPERY, property, String.valueOf(value)));		
	}

}
