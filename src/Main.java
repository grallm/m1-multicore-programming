import TL2.AbortException;
import TL2.threadpool.DictionaryTask;
import TL2.threadpool.Task;
import TL2.threadpool.ThreadPool;
import TL2.utils.ClockManager;
import tp3.Dictionary;

import java.util.concurrent.ExecutorService;

public class Main
{
    private static volatile ExecutorService threadPool;

    public static void main(String[] args) throws AbortException
    {
        final ClockManager clock = new ClockManager();
        String[] dicWords = {"chameau", "chameaux", "chamelle", "chamelles", "chamelon", "chamelons", "chat", "chaton", "chatons", "chats", "chatte", "chattes"};
        Dictionary dic = new Dictionary();
        ThreadPool threadPool = new ThreadPool(10);

         for (String str : dicWords)
         {
             threadPool.execute(new DictionaryTask(str, dic));
         }

        System.out.println("Result :");
        dic.print();
    }
}
