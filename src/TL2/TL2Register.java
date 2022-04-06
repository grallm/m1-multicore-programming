package TL2;

import java.util.Date;

public class TL2Register<T> implements Register<T> {
    private T value;
    private Date date;

    public synchronized T read(Transaction t) throws AbortException {
        return value;
    }

    public synchronized void write(Transaction t, T v) throws AbortException {

    }
}
