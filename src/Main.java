import TL2.AbortException;
import TL2.TransactionTL2;
import TL2.interfaces.Transaction;
import TL2.threadpool.Task;
import TL2.threadpool.ThreadPool;
import tp3.Dictionary;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main
{
    private static volatile ExecutorService threadPool;

    public static void main(String[] args)
    {
        // threadPool = Executors.newFixedThreadPool(10);
        String[] dicWords = {"chameau"/*, "chameaux", "chamelle", "chamelles", "chamelon", "chamelons", "chat", "chaton", "chatons", "chats", "chatte", "chattes"*/};
        Dictionary dic = new Dictionary();

         // for (String str : dicWords)
         // {
         //     Transaction t = new TransactionTL2();
         //     Task task = new Task(str, t, dic);
         //     threadPool.submit(() -> {
         //         while (!t.isCommited())
         //         {
         //             try
         //             {
         //                 t.begin();
         //                 dic.add(str, t);
         //                 t.try_to_commit();
         //             }
         //             catch (AbortException e)
         //             {
         //                 e.printStackTrace();
         //             }
         //         }
         //     });
         // }
        // try {
        //     for (String str : dicWords) {
        //         Transaction t = new TransactionTL2();
        //         while (!t.isCommited()) {
        //             t.begin();
        //             dic.add(str);
        //             t.try_to_commit();
        //         }
        //     }
        // } catch (AbortException e) {
        //     e.printStackTrace();
        // }
        for (String str : dicWords) {
            dic.add(str);
        }

        System.out.println("Result :");
        // System.out.println(dic.getWords());
        System.out.println(dic.toString());
    }
}
