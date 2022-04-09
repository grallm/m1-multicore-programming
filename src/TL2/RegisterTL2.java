package TL2;

import TL2.interfaces.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class RegisterTL2<T> implements IRegisterTL2<T>
{
    private final AtomicReference<T> value;
    private volatile AtomicInteger date;
    private final ReentrantLock lock;

    /**
     * Constructor
     */
    public RegisterTL2(T value) {
        this.value = new AtomicReference<>(value);
        this.date = new AtomicInteger(0);

        // true to have starvation-freedom
        this.lock = new ReentrantLock(true);
    }

    public RegisterTL2(T value, int date) {
        this.value = new AtomicReference<>(value);
        this.date = new AtomicInteger(date);

        // true to have starvation-freedom
        this.lock = new ReentrantLock(true);
    }

    public void lock() {
        lock.lock();
    }
    public void unlock() {
        lock.unlock();
    }
    public boolean isLocked() {
        return lock.isLocked();
    }

    public int getDate() {
        return date.get();
    }

    public void setDate(int date) {
        this.date.set(date);
    }

    public T getValue()
    {
        return value.get();
    }

    public void setValue(T value)
    {
        this.value.set(value);
    }

    public T read(Transaction<T> t) throws AbortException
    {
        return this.readTL2((ITransactionTL2<T>) t);
    }
    public T readTL2(ITransactionTL2<T> t) throws AbortException
    {
        IRegisterTL2<T> local = t.getCopy(this);

        // Return local's value, if exists
        if (local == null)
        {
            local = new RegisterTL2<>(getValue(), getDate());

            t.putCopy(this, local);
            t.addToLrs(this);

            if (local.getDate() > t.getBirthDate())
            {
                throw new AbortException("Copied date is after transaction birth date");
            }

        }
        return local.getValue();
    }

    public void write(Transaction<T> t, T v) throws AbortException {
        this.writeTL2((ITransactionTL2<T>) t, v);
    }
    public void writeTL2(ITransactionTL2<T> t, T v)
    {
        if(t.getCopy(this) == null) // There is no local copy of this register
        {
            t.putCopy(this, new RegisterTL2<>(getValue(), getDate()));
        }

        IRegisterTL2<T> local = t.getCopy(this);
        // TODO: use proper compareTo
        if(local.getDate() == getDate() && local.getValue() == getValue()) // The local copy is the same as the current value
        {
            t.putCopy(this, new RegisterTL2<>(v, getDate()));
        }

        t.addToLws(this);
    }

    @Override
    public int compareTo(IRegisterTL2<T> o) {
        return this.hashCode() > o.hashCode() ? 1 : -1;
    }
}
