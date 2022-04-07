import TL2.AbortException;
import TL2.TransactionTL2;
import TL2.interfaces.Transaction;
import TL2.threadpool.Task;
import TL2.threadpool.ThreadPool;
import tp3.Dictionary;

public class Main
{
    public static void main(String[] args)
    {
        ThreadPool threadPool = new ThreadPool(10);

        String[] dicWords = {"chameau", "chameaux", "chamelle", "chamelles", "chamelon", "chamelons", "chat", "chaton", "chatons", "chats", "chatte", "chattes"};

        for (String str : dicWords)
        {
            // add to threapool
            Transaction t = new TransactionTL2();
            Task task = new Task(str, t);
            threadPool.execute(task);
        }
    }
}
