package tp3;

import TL2.threadpool.ThreadPool;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WebGrep {
	private volatile ThreadPool threadPool;

	private final Lock lock = new ReentrantLock();

	private final BlockingQueue<ParsedPage> prints = new LinkedBlockingQueue<>();

	/**
	 * Explored pages
	 */
	private final ConcurrentSkipListSet<String> explored = new ConcurrentSkipListSet<String>();

	/*
	 *  Explore a page
	 */
	private String explore(String address) {
		try {
			/*
			 *  Check that the page was not already explored and adds it
			 *  The check and insertion must be atomic. Explain why. How to do it?
			 *
			 * 	Because if one searches, but another adds just after, may have 2 explores
			 * 	exploring same page.
			 */
			if(explored.add(address)) {
				// Parse the page to find matches and hypertext links
				ParsedPage page = Tools.parsePage(address);
				if(!page.matches().isEmpty()) {
					/*
					 * Thread safe print
					 */
					lock.lock();
					Tools.print(page);
					lock.unlock();

					// Recursively explore other pages
					for(String href : page.hrefs())
						threadPool.execute(() -> explore(href));
				}
			}
		} catch (IOException e) {/*We could retry later...*/}
		return address;
	}


	public WebGrep (String word, String address, ThreadPool threadPool) {
		// Initialize the program using the options given in argument
		Tools.initialize(String.format("-celt --threads=1000 %s %s", word, address));

		System.out.println("Started with " + Tools.numberThreads() + " threads\n");

		// Create the Thread Pool
		this.threadPool = threadPool;

		// Printing thread
		new Thread(() -> {
			while(true) {
				try {
					// Passive waiting with take
					Tools.print(prints.take());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		// Get the starting URL given in argument
		for(String startAddress : Tools.startingURL())
			threadPool.execute(() -> explore(startAddress));
	}
}
