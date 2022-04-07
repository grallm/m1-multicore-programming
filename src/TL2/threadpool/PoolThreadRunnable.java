package TL2.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.FutureTask;

public class PoolThreadRunnable implements Runnable
{
    private Thread thread = null;
    private BlockingQueue<?> queueWithPriority;
    private BlockingQueue<?> queueWithoutPriority;
    private boolean isStopped = false;

    public PoolThreadRunnable(BlockingQueue<?> _queueWithPriority, BlockingQueue<?> _queueWithoutPriority)
    {
        queueWithPriority = _queueWithPriority;
        queueWithoutPriority = _queueWithoutPriority;
    }

    public void run()
    {
        this.thread = Thread.currentThread();

        while (!isStopped())
        {
            try
            {
                FutureTask<?> runnable;
                if (queueWithPriority.size() > 0)
                {
                    runnable = (FutureTask<?>) queueWithPriority.take();
                }
                else
                {
                    runnable = (FutureTask<?>) queueWithoutPriority.take();
                }
                runnable.run();
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
