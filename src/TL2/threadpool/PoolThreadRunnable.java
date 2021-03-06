package TL2.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.FutureTask;

public class PoolThreadRunnable<T> implements Runnable
{
    private Thread thread = null;
    private final BlockingQueue<FutureTask<T>> queue;
    private boolean isStopped = false;

    public PoolThreadRunnable(BlockingQueue<FutureTask<T>> _queue)
    {
        queue = _queue;
    }

    public void run()
    {
        this.thread = Thread.currentThread();

        while (!isStopped())
        {
            try
            {
                if (queue.size() > 0)
                {
                    queue.take().run();
                }
            }
            catch (Exception e)
            {
                //log or otherwise report exception,
                //but keep pool thread alive.
                System.out.println("Error : " + e);
            }
        }
    }

    public synchronized void doStop()
    {
        isStopped = true;
        //break pool thread out of dequeue() call.
        this.thread.interrupt();
    }

    public synchronized boolean isStopped()
    {
        return isStopped;
    }
}
