package TL2.threadpool;

import TL2.AbortException;
import TL2.TransactionTL2;
import TL2.interfaces.Transaction;
import tp3.Dictionary;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.FutureTask;

public class PoolThreadRunnable implements Runnable
{
    private Thread thread = null;
    private BlockingQueue<FutureTask<?>> queue;
    private boolean isStopped = false;

    public PoolThreadRunnable(BlockingQueue<FutureTask<?>> _queue)
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
                    FutureTask<Task> runnable = (FutureTask<Task>) queue.take();
                    runnable.run();
                    Transaction t = runnable.get().transaction;
                    String str = runnable.get().string;
                    Dictionary dic = runnable.get().dictionary;

                    while (!t.isCommited())
                    {
                        try
                        {
                            t.begin();
                            dic.add(str/*, t*/);
                            t.try_to_commit();
                        }
                        catch (AbortException e)
                        {
                            e.printStackTrace();
                        }
                    }
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
