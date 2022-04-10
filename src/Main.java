import TL2.AbortException;
import TL2.threadpool.DictionaryTask;
import TL2.threadpool.Task;
import TL2.threadpool.ThreadPool;
import TL2.utils.ClockManager;
import tp3.Dictionary;
import tp3.WebGrep;

import java.util.concurrent.ExecutorService;

public class Main
{
    private static volatile ExecutorService threadPool;

    public static void main(String[] args) throws AbortException
    {
        final ClockManager clock = new ClockManager();
        Dictionary dic = new Dictionary();
        ThreadPool threadPool = new ThreadPool(10);

        new WebGrep("Nantes", "https://fr.wikipedia.org/wiki/Nantes", threadPool);

        System.out.println("Result :");
        dic.print();
    }
}
