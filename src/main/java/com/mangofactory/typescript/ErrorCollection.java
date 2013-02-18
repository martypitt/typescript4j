package com.mangofactory.typescript;

public class ErrorCollection {

	private static ErrorCollection instance;
	public static ErrorCollection getInstance()
	{
		if (instance == null)
		{
			instance = new ErrorCollection();
		}
		return instance;
	}
	public void addMessage(Object start, Object len, Object messsage, Object block)
	{
		System.out.print("Hello?");
	}
}
