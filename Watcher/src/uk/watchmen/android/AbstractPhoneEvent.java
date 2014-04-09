package uk.watchmen.android;

public abstract class AbstractPhoneEvent implements PhoneEvent
{
	private final String number;

	public AbstractPhoneEvent(String number)
	{
		this.number = number;
	}
	public String getNumber()
	{
		return number;
	}
}