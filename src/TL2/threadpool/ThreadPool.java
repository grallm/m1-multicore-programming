package TL2.threadpool;


import TL2.AbortException;
import TL2.interfaces.Transaction;
import tp3.Dictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPool
{
    private BlockingQueue<FutureTask<?>> queue;
    private List<PoolThreadRunnable> runnables = new ArrayList<>();
    private boolean isStopped = false;

    public ThreadPool(int noOfThreads)
    {
        queue = new LinkedBlockingQueue<>();

        for (int i = 0; i < noOfThreads; i++)
        {
            PoolThreadRunnable poolThreadRunnable = new PoolThreadRunnable(queue);

            runnables.add(poolThreadRunnable);
        }

        for (PoolThreadRunnable runnable : runnables)
        {
            new Thread(runnable).start();
        }
    }

    public synchronized void execute(Task t) throws IllegalStateException
    {
        FutureTask<Task> futureTask = new FutureTask<>(t);
        queue.offer(futureTask);
        if (this.isStopped) throw
                new IllegalStateException("ThreadPool is stopped");
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
        while (queue.size() != 0)
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
