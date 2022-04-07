package TL2.threadpool;

import TL2.TransactionTL2;
import TL2.interfaces.Transaction;

import java.util.concurrent.Callable;

public class Task implements Callable<Task>
{
    Transaction transaction;
    String string;

    public Task(String str, Transaction t)
    {
        transaction = t;
        string = str;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     */
    @Override
    public Task call()
    {
        return new Task(string, transaction);
    }
}
