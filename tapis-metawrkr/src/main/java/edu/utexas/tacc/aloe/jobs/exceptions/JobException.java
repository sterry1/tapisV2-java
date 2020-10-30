package edu.utexas.tacc.aloe.jobs.exceptions;

import edu.utexas.tacc.aloe.shared.exceptions.AloeException;

@SuppressWarnings("serial")
public class JobException extends AloeException {

	public JobException(String message)
	{
		super(message);
	}

	public JobException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
