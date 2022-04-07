import tp3.Dictionary;

public class Main {
    public static void main (String[] args) {
        String[] dicWords = {"chameau","chameaux","chamelle","chamelles","chamelon","chamelons", "chat", "chaton", "chatons","chats","chatte","chattes"};

        Dictionary dic = new Dictionary();

        for (String str : dicWords) {
            dic.add(str);
        }

        dic.getFull();
    }
}

