package TL2.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPool<T>
{
    private final BlockingQueue<FutureTask<T>> queue;
    private final List<PoolThreadRunnable<T>> runnables = new ArrayList<>();
    private boolean isStopped = false;

    public ThreadPool(int noOfThreads)
    {
        queue = new LinkedBlockingQueue<>();

        for (int i = 0; i < noOfThreads; i++)
        {
            PoolThreadRunnable<T> poolThreadRunnable = new PoolThreadRunnable<>(queue);

            runnables.add(poolThreadRunnable);
        }

        for (PoolThreadRunnable<T> runnable : runnables)
        {
            new Thread(runnable).start();
        }
    }

    public synchronized void execute(Task<T> t) throws IllegalStateException
    {
        FutureTask<T> futureTask = new FutureTask<>(t);
        queue.offer(futureTask);
        if (this.isStopped) throw
                new IllegalStateException("ThreadPool is stopped");
    }

    public synchronized void stop()
    {
        this.isStopped = true;
        for (PoolThreadRunnable<T> runnable : runnables)
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
