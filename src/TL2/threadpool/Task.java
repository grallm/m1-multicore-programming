package TL2.threadpool;

import TL2.TransactionTL2;
import TL2.interfaces.Transaction;
import tp3.Dictionary;

import java.util.concurrent.Callable;

public class Task implements Callable<Task>
{
    Transaction transaction;
    String string;
    Dictionary dictionary;

    public Task(String str, Transaction t, Dictionary dic)
    {
        transaction = t;
        string = str;
        dictionary = dic;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     */
    @Override
    public Task call()
    {
        return new Task(string, transaction, dictionary);
    }
}
