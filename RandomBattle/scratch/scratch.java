import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class scratch
{
	public static void main(String[] args)
	{
		long start = System.nanoTime();
		final List<Integer> test = Collections.synchronizedList(new LinkedList<Integer>());
		final AtomicInteger i = new AtomicInteger(0);
		final ReentrantReadWriteLock testLock = new ReentrantReadWriteLock(true);
		ArrayList<Thread> threads = new ArrayList<Thread>(4);
		for (int j = 0; j < 4; j++) {
			threads.add(new Thread(new Runnable() {
				@Override
				public void run()
				{
					System.out.println("Started " + Thread.currentThread());
					int count = 0;
					while (count < 10) {
						count++;
						testLock.writeLock().lock();
						test.add(i.incrementAndGet());
						testLock.writeLock().unlock();
					}
				}
			}));
		}
		for (Thread thread : threads)
			thread.start();
		for (Thread thread : threads)
			try {
				thread.join();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		for (Integer integer : test)
			System.out.println(integer);
		System.out.println("Done. Time taken: " + ((System.nanoTime() - start) / Math.pow(10.0, 9.0)) + " s");
	}
}
