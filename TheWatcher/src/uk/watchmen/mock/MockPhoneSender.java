package uk.watchmen.mock;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MockPhoneSender
{
	public static void main(String[] args) throws IOException
	{	
		String message = args[1];
		if(args.length > 2)
		{
			message += "#" + args[2];	
		}
		int server_port = 55554;
		DatagramSocket s = new DatagramSocket();
		InetAddress local = InetAddress.getByName(args[0]);
		int msg_length=message.length();
		byte[] packet = message.getBytes();
		DatagramPacket p = new DatagramPacket(packet, msg_length,local,server_port);
		s.send(p);
	}
}