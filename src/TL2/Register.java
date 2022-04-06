package TL2;

public interface Register<T> {
    public T read(Transaction t) throws AbortException;
    public void write(Transaction t, T v) throws AbortException;
}
