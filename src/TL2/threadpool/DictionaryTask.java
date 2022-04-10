package TL2.threadpool;

import TL2.AbortException;
import TL2.TransactionTL2;
import TL2.interfaces.Transaction;
import tp3.Dictionary;

import java.util.concurrent.Callable;

public class DictionaryTask implements Task<Dictionary>
{
    String string;
    Dictionary dictionary;

    public DictionaryTask(String str, Dictionary dic)
    {
        string = str;
        dictionary = dic;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     */
    @Override
    public Dictionary call() throws AbortException
    {
        dictionary.add(string);
        return dictionary;
    }
}
