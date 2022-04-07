import TL2.AbortException;
import TL2.TransactionTL2;
import TL2.interfaces.Transaction;
import tp3.Dictionary;

public class Main {
    public static void main (String[] args) {
        String[] dicWords = {"chameau","chameaux","chamelle","chamelles","chamelon","chamelons", "chat", "chaton", "chatons","chats","chatte","chattes"};

        Dictionary dic = new Dictionary();

        try {
            for (String str : dicWords) {
                Transaction t = new TransactionTL2();
                while (!t.isCommited()) {
                    t.begin();
                    dic.add(str, t);
                    t.try_to_commit();
                }
            }
        } catch (AbortException e) {
            e.printStackTrace();
        }
    }
}

