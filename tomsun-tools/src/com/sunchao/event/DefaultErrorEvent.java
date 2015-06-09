package com.sunchao.event;

import java.util.Map;

/**
 * <p>
 * An event class that is used for reporting errors that occurred while
 * processing .
 * </p>
 * <p>
 * Some configuration implementations storage that can throw 
 * an exception on each access .This makes it impossible 
 * for a client to find out that something
 * went wrong.
 * </p>
 *
 * @author sunchao
 *
 */
public class DefaultErrorEvent extends Event {

	/**
	 * The serial uid of error event.
	 */
	private static final long serialVersionUID = -7529442914061094880L;
	/**
	 * The default error event type.
	 */
	public static final EventType<DefaultErrorEvent> ERROR = 
			new EventType<DefaultErrorEvent>(Event.ANY, "ERROR");

	/**the operation event type which occur an error  */
	private final EventType<?> errorOperationType ;
	/**the attachments which will be passed by and done by the  event listener  */
	private final Map<String, Object> attachment;
	/**the error which stored to reminder to the client */
	private final Throwable cause;
	
	/**
	 * Builds a new instance of error event,which is base class of the
	 * subClass which are error type.
	 * @param source
	 *              the event source.
	 * @param evType
	 *              the event type.
	 * @param operationType
	 *              the operation type which occur the error.
	 * @param attachment
	 *               the attachment passed by.
	 * @param cause
	 *               the stored throwable (error)
	 */
	public DefaultErrorEvent(Object source, EventType<? extends DefaultErrorEvent> evType, 
			EventType<?> operationType, Map<String, Object> attachment, Throwable cause) {
		super(source, evType);
		this.attachment = attachment;
		this.errorOperationType = operationType;
		this.cause = cause;
	}
	
	public Map<String, Object> getAttactName()
	{
		return this.attachment;
	}
	
	
	public EventType<?> getOperationType() 
	{
	    return this.errorOperationType;	
	}

	public Throwable getCause() 
	{
		return this.cause;
	}
}
