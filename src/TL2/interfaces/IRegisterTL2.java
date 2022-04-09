package TL2.interfaces;

import TL2.AbortException;

import java.util.Date;


public interface IRegisterTL2<T> extends Register<T>, Comparable<IRegisterTL2<T>>
{
    boolean isLocked();

    void lock();
    void unlock();

    int getDate();

    T getValue();

    void setValue(T value);

    T readTL2(ITransactionTL2<T> t) throws AbortException;
    void writeTL2(ITransactionTL2<T> t, T v) throws AbortException;

    void setDate(int commitDate);
}
