package TL2.interfaces;

import TL2.AbortException;

import java.util.Date;


public interface IRegisterTL2<T> extends Register<T>, Comparable<IRegisterTL2<T>>
{
    boolean isLocked();

    void lock();
    void unlock();

    Date getDate();

    void setDate(Date date);

    T getValue();

    void setValue(T value);

    T read(ITransactionTL2 t) throws AbortException;
    void write(ITransactionTL2 t, T v) throws AbortException;
}
