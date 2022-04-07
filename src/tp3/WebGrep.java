package tp3;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WebGrep {
	private volatile static ExecutorService threadPool;

	private final static Lock lock = new ReentrantLock();

	private final static BlockingQueue<ParsedPage> prints = new LinkedBlockingQueue<>();

	/**
	 * Explored pages
	 */
	private final static ConcurrentSkipListSet<String> explored = new ConcurrentSkipListSet<String>();

	/*
	 *  Explore a page
	 */
	private static void explore(String address) {
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
						threadPool.submit(() -> explore(href));
				}
			}
		} catch (IOException e) {/*We could retry later...*/}
	}


	public static void main(String[] args) throws InterruptedException, IOException {
		// Initialize the program using the options given in argument
		if(args.length == 0) Tools.initialize("-celt --threads=1000 Nantes https://fr.wikipedia.org/wiki/Nantes");
		else Tools.initialize(args);

		System.out.println("Started with " + Tools.numberThreads() + " threads\n");

		// Create the Thread Pool
		threadPool = Executors.newFixedThreadPool(Tools.numberThreads());

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
		for(String address : Tools.startingURL())
			threadPool.submit(() -> explore(address));
	}
}
