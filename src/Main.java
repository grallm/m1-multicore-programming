import TL2.AbortException;
import TL2.TransactionTL2;
import TL2.interfaces.Transaction;
import TL2.threadpool.Task;
import TL2.threadpool.ThreadPool;
import TL2.utils.ClockManager;
import tp3.Dictionary;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main
{
    private static volatile ExecutorService threadPool;

    public static void main(String[] args) throws AbortException
    {
        final ClockManager clock = new ClockManager();
        String[] dicWords = {"chameau", "chameaux", "chamelle", "chamelles", "chamelon", "chamelons", "chat", "chaton", "chatons", "chats", "chatte", "chattes"};
        Dictionary dic = new Dictionary();

         for (String str : dicWords)
         {
             dic.add(str);
         }

        System.out.println("Result :");
        dic.print();
    }
}
