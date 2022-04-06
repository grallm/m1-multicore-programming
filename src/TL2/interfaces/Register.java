package TL2.interfaces;

import TL2.AbortException;

import java.util.Date;


public interface Register<T>
{
    boolean isLocked();

    void setLocked(boolean locked);

    Date getDate();

    void setDate(Date date);

    T getValue();

    void setValue(Object value);

    public T read(Transaction t) throws AbortException;
    public void write(Transaction t, T v) throws AbortException;
}
