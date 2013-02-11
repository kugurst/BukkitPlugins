
package me.merdril.randombattle.config;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <code>public class DelayedSingleThreadExecutor</code>
 * <p>
 * This class executes a given Runnable with the assumption that the Runnables it accepts are all
 * the same. The utility of this class is found, for example, when one is writing a persistent and
 * frequently accessed data structure (large or not) to disk for retrieval at a later time. In this
 * scenario, one can "save" the data structure as many times in a given time span as they want, with
 * the knowledge that the disk I/O will not be tasked by all these constant writes, and a single
 * write will take place. <br />
 * If the thread in charge of executing a thread has died because of the code in the run method of
 * the passed Runnable, a new Thread will be created.
 * </p>
 * @author Merdril
 */
public class DelayedSingleThreadExecutor
{
	private Thread	                        currentlyRunning, executeThread;
	private LinkedBlockingQueue<Runnable>	runQueue;
	private ConcurrentLinkedDeque<Runnable>	executeQueue;
	private AtomicBoolean	                shutdown;
	private AtomicLong	                    delay	= new AtomicLong(2000);
	
	/**
	 * <code>public DelayedSingleThreadExecutor()</code>
	 * <p>
	 * Constructs the default DelayedSingleThreadExecutor (the one used in this plugin) with a
	 * delayed execution of 2 seconds. It postpones shutdown of the JVM until the completion of the
	 * last submitted Runnable.
	 * </p>
	 */
	public DelayedSingleThreadExecutor()
	{
		// Initialize all dependencies
		runQueue = new LinkedBlockingQueue<Runnable>();
		executeQueue = new ConcurrentLinkedDeque<Runnable>();
		shutdown = new AtomicBoolean(false);
		currentlyRunning = new Thread(new ThreadExecutor());
		currentlyRunning.setDaemon(true);
		currentlyRunning.start();
		executeThread = new Thread(new ThreadDelayer());
		executeThread.setDaemon(true);
		executeThread.start();
		
		// Register a shutdown hook since all off our threads are deamons, and thus will prompt the
		// JVM to shutdown when all other non-daemon threads have completed, which isn't what we
		// want, as we still want to complete the last queued task
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run()
			{
				// Tell all threads we are shutting down
				shutdown.set(true);
				executeThread.interrupt();
				try {
					executeThread.join();
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				// The execute thread is in two situations. 1) It is waiting on the lock, so we can
				// interrupt
				// it, or 2) it is executing, and it will terminate on its own.
				if (currentlyRunning.getState() == Thread.State.WAITING) {
					currentlyRunning.interrupt();
					try {
						currentlyRunning.join();
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else {
					try {
						currentlyRunning.join();
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// Execute the last queued task
				Runnable r = executeQueue.pollLast();
				if (r != null)
					r.run();
				// Or perhaps we terminated at precisely the last non-semicolon piece of code in the
				// run method of ThreadDelayers, in which case, runQueue would have it
				else {
					r = runQueue.poll();
					if (r != null)
						r.run();
				}
			}
		}));
	}
	
	/**
	 * <code>public DelayedSingleThreadExecutor()</code>
	 * <p>
	 * Constructs a DelayedSingleThreadExecutor (the one used in this plugin) with a specified
	 * delayed execution. It postpones shutdown of the JVM until the completion of the last
	 * submitted Runnable.
	 * </p>
	 * @param delay
	 *            - A long specifying how long to delay the execution of a giving task (and thus the
	 *            window to check for a new task). Calls Math.abs(long) on the given delay.
	 */
	public DelayedSingleThreadExecutor(long delay)
	{
		this();
		this.delay = new AtomicLong(Math.abs(delay));
	}
	
	/**
	 * <code>public DelayedSingleThreadExecutor()</code>
	 * <p>
	 * Constructs a DelayedSingleThreadExecutor (the one used in this plugin) with a specified
	 * delayed execution. If complete is true, it will postpone shutdown of the JVM until the
	 * completion of the last submitted Runnable.
	 * </p>
	 * @param delay
	 *            - A long specifying how long to delay the execution of a giving task (and thus the
	 *            window to check for a new task). Calls Math.abs(long) on the given delay.
	 * @param complete
	 *            - A boolean specifying whether or not to postpone shutdown of the server until the
	 *            completion of the final task. If true, this class will postpone shutdown of the
	 *            server. If false, it will not.
	 */
	public DelayedSingleThreadExecutor(long delay, boolean complete)
	{
		runQueue = new LinkedBlockingQueue<Runnable>();
		executeQueue = new ConcurrentLinkedDeque<Runnable>();
		shutdown = new AtomicBoolean(false);
		currentlyRunning = new Thread(new ThreadExecutor());
		currentlyRunning.setDaemon(true);
		currentlyRunning.start();
		executeThread = new Thread(new ThreadDelayer());
		executeThread.setDaemon(true);
		executeThread.start();
		
		if (complete)
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run()
				{
					// Tell all threads we are shutting down
					shutdown.set(true);
					executeThread.interrupt();
					try {
						executeThread.join();
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
					// The execute thread is in two situations. 1) It is waiting on the lock, so we
					// can
					// interrupt
					// it, or 2) it is executing, and it will terminate on its own.
					if (currentlyRunning.getState() == Thread.State.WAITING) {
						currentlyRunning.interrupt();
						try {
							currentlyRunning.join();
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					else {
						try {
							currentlyRunning.join();
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					// Execute the last queued task
					Runnable r = executeQueue.pollLast();
					if (r != null)
						r.run();
					// Or perhaps we terminated at precisely the last non-semicolon piece of code in
					// the
					// run method of ThreadDelayers, in which case, runQueue would have it
					else {
						r = runQueue.poll();
						if (r != null)
							r.run();
					}
				}
			}));
	}
	
	/**
	 * <code>public void execute</code>
	 * <p>
	 * Schedules the given task for execution. Execution of the given task will complete after the
	 * completion of the previous task, and if there is no task received within the delay period and
	 * before the completion of the previous task.
	 * </p>
	 * @param r
	 *            - The {@link Runnable} to run. The run method is called on the passed in Runnable.
	 */
	public void execute(Runnable r)
	{
		// First, make sure the currentlyRunning thread hasn't died
		if (!shutdown.get()) {
			if (!currentlyRunning.isAlive()) {
				currentlyRunning = new Thread(new ThreadExecutor());
				currentlyRunning.setDaemon(true);
				currentlyRunning.start();
			}
		}
		synchronized (executeQueue) {
			executeQueue.add(r);
			// To be more intelligent on the time we spent delayed, let's check to see if the
			// execute thread is waiting to be notified
			if (executeThread.getState() == Thread.State.WAITING)
				// We would only have obtained executeQueue's monitor if the thread is
				executeQueue.notify();
			// Or if it's sleeping, in which case we can reset its delay loop (However, there is no
			// guarantee that the delayer thread will go to sleep before its execution is paused,
			// and this thread continues. In which case the delayer thread will sleep for the full
			// delay period, and then do so a second time when it finds that a new item has been
			// added to the queue)
			else if (executeThread.getState() == Thread.State.TIMED_WAITING)
				executeThread.interrupt();
		}
	}
	
	private class ThreadDelayer implements Runnable
	{
		@Override
		public void run()
		{
			// Delay loop
			while (true) {
				// Before we do anything, let's make sure we aren't shutting down
				if (shutdown.get())
					return;
				Runnable r = null;
				synchronized (executeQueue) {
					r = executeQueue.peekLast();
					// the queue is empty, wait to be notified
					if (r == null) {
						try {
							executeQueue.wait();
						}
						catch (InterruptedException e) {
							// We will only be interrupted at this point in execution if we are
							// shutting down
							return;
						}
						// The queue is not empty, so peek the last
						r = executeQueue.peekLast();
					}
				}
				// Delay execution
				try {
					Thread.sleep(delay.get());
				}
				catch (InterruptedException e) {
					// If shutdown isn't true, then we were interrupted because a new objected was
					// added to the queue
					if (!shutdown.get())
						continue;
					// Otherwise, we were interrupted because we are shutting down.
					return;
				}
				// If the runnable we have is the same as the last, clear the run queue and add this
				// runnable
				synchronized (executeQueue) {
					if (r.equals(executeQueue.peekLast())) {
						// An alternative to calling join on currentlyRunning. Simply clears the
						// -blocking- queue so
						runQueue.clear();
						runQueue.add(r);
						executeQueue.clear();
					} // Otherwise, just repeat the loop
				}
			}
		}
		
	}
	
	private class ThreadExecutor implements Runnable
	{
		@Override
		public void run()
		{
			// Just loop forever
			while (true) {
				if (shutdown.get())
					return;
				Runnable r = null;
				try {
					r = runQueue.take();
				}
				catch (InterruptedException e) {
					return;
				}
				r.run();
			}
		}
		
	}
}
