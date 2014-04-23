package uk.watchmen.mock;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import uk.watchmen.android.CallEvent;
import uk.watchmen.android.MessageEvent;
import uk.watchmen.android.PhoneEvent;
import uk.watchmen.android.PhoneObserver;
import uk.watchmen.android.PhoneSubject;

public class MockPhoneListener implements PhoneSubject, Runnable
{
	private List<PhoneObserver> observers = new ArrayList<PhoneObserver>();

	public void addObserver(PhoneObserver ob)
	{
		observers.add(ob);
	}
	public void removeObserver(PhoneObserver ob)
	{
		observers.remove(ob);
	}
	public void notifyAll(PhoneEvent event)
	{
		for(PhoneObserver ob : observers)
		{
			ob.notify(event);
		}
	}
	public void run()
	{
		while(true)
		{
			try
			{
				String[] text;
				PhoneEvent event = null;
				int server_port = 55554;
				byte[] message = new byte[1500];
				DatagramPacket p = new DatagramPacket(message, message.length);
				DatagramSocket s = new DatagramSocket(server_port);
				s.receive(p);
				text = new String(message, 0, p.getLength()).split("#",2);
				s.close();
				String number = text[0];
				if(text.length == 2)
				{
					String messageStr = text[1];
					event = new MessageEvent(number,messageStr);
				}
				else
				{
					event = new CallEvent(number);
				}
				notifyAll(event);
			}
			catch(IOException ioe)
			{
				throw new RuntimeException("Error while receiving message in MockPhoneListener",ioe);
			}
		}
	}
}