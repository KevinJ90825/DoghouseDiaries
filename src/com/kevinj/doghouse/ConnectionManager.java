package com.kevinj.doghouse;

import java.util.ArrayList;


public class ConnectionManager
{
	public static final int MAX_CONNECTIONS = 5;
	
	private ArrayList<Runnable> active = new ArrayList<Runnable>();
	private ArrayList<Runnable> queue = new ArrayList<Runnable>();
	
	public static ConnectionManager _instance;
	
	public static ConnectionManager getInstance()
	{
		if (_instance == null)
			_instance = new ConnectionManager();
		return _instance;
	}
	
	public void push(Runnable run)
	{
		queue.add(run);
		if (active.size() < MAX_CONNECTIONS)
			startNext();
	}
	
	private void startNext()
	{
		if(!queue.isEmpty())
		{
			Runnable next = queue.get(0);
			queue.remove(0);
			active.add(next);
			
			new Thread(next).start();
		}
	}
	
	public void didComplete(Runnable run)
	{
		active.remove(run);
		startNext();
	}
}