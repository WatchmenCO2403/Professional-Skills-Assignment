package uk.watchmen.android;

public interface PhoneSubject
{
	void addObserver(PhoneObserver po);
	void removeObserver(PhoneObserver po);
	void notifyAll(PhoneEvent pe);
}