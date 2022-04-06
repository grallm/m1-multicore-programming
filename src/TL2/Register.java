package TL2;

public interface Register<T> {
    T read(Transaction t) throws AbortException;
    void write(Transaction t, T v) throws AbortException;
}
