
package me.merdril.randombattle.config;

import java.util.TimerTask;
import java.util.concurrent.ThreadFactory;

public class SingleThreadFactory implements ThreadFactory
{
	private Runnable	currentlyRunning;
	
	@Override
	public Thread newThread(Runnable r)
	{
		FileAccessor fa = new FileAccessor();
		return null;
	}
	
}

class FileAccessor extends TimerTask
{
	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		
	}
	
}
