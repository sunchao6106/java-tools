package com.sunchao.event;

import java.util.Map;

/**
 * <p>
 * The class represent the normal event and will normally
 * do with by the event listener.
 * </p>
 * @author sunchao
 *
 */
public class DefaultEvent extends Event {
	/** the serial uid    */
	private static final long serialVersionUID = 646165334792905113L;
	
	/** the default empty event type  */
	public static final EventType<DefaultEvent> EMPTY =
			 new EventType<DefaultEvent>(Event.ANY,"EMPTY");
	
	/** the attachment passed by the source */
	private final Map<String, Object> attachment;

	/**
	 * Builds an instance of default event, and the subClass overwrite the class
	 * with the need.
	 * @param source  
	 *              the event source which need by the jdk{@code java.util.EventObject}.
	 * @param evType
	 *              the event type.
	 * @param attachment
	 *              the attachment.
	 */
	public DefaultEvent(Object source, EventType<? extends DefaultEvent> evType,
			 Map<String, Object> attachment) {
		super(source, evType);
		this.attachment = attachment;
	}
	
	public Map<String, Object> getAttachment()
	{
		return this.attachment;
	}
	
	

}
