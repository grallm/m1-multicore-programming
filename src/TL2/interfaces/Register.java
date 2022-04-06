package TL2.interfaces;

import TL2.AbortException;

public interface Register<T> {
    T read(Transaction t) throws AbortException;
    void write(Transaction t, T v) throws AbortException;
}
