package TL2.threadpool;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPool
{
    private BlockingQueue<?> queueWithPriority;
    private BlockingQueue<?> queueWithoutPriority;
    private List<PoolThreadRunnable> runnables = new ArrayList<>();
    private boolean isStopped = false;

    public ThreadPool(int noOfThreads)
    {
        queueWithPriority = new LinkedBlockingQueue<>();
        queueWithoutPriority = new LinkedBlockingQueue<>();

        for (int i = 0; i < noOfThreads; i++)
        {
            PoolThreadRunnable poolThreadRunnable = new PoolThreadRunnable(queueWithPriority, queueWithoutPriority);

            runnables.add(poolThreadRunnable);
        }

        for (PoolThreadRunnable runnable : runnables)
        {
            new Thread(runnable).start();
        }
    }

    public synchronized void execute() throws IllegalStateException
    {
        // TODO
    }

    public synchronized void stop()
    {
        this.isStopped = true;
        for (PoolThreadRunnable runnable : runnables)
        {
            runnable.doStop();
        }
    }

    public synchronized void waitUntilAllTasksFinished()
    {
        while (queueWithPriority.size() != 0 && queueWithoutPriority.size() != 0)
        {
            try
            {
                Thread.sleep(1);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
