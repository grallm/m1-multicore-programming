package TL2.interfaces;

import TL2.AbortException;

public interface Register<T> {
    T read(Transaction<T> t) throws AbortException;
    void write(Transaction<T> t, T v) throws AbortException;
}
