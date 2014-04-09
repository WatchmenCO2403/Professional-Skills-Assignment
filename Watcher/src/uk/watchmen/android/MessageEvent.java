package uk.watchmen.android;

public class MessageEvent extends AbstractPhoneEvent
{
	private final String message;

	public MessageEvent(String number, String message)
	{
		super(number);
		this.message = message;
	}
}
