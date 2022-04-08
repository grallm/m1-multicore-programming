package TL2;

import TL2.interfaces.*;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RegisterTL2<T> implements IRegisterTL2<T>
{
    private final AtomicReference<T> value;
    private Date date;
    private final Lock lock;
    private boolean isLocked;

    /**
     * Constructor
     */
    public RegisterTL2(T value) {
        this.value = new AtomicReference<>(value);
        this.date = new Date();

        // true to have starvation-freedom
        this.lock = new ReentrantLock(true);
    }

    public void lock() {
        lock.lock();
        isLocked = true;
    }
    public void unlock() {
        lock.unlock();
        isLocked = false;
    }
    public boolean isLocked() {
        return isLocked;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public T getValue()
    {
        return value.get();
    }

    public void setValue(T value)
    {
        this.value.set(value);
    }

    public T read(Transaction t) throws AbortException {
        return this.read((TransactionTL2) t);
    }
    public T read(ITransactionTL2 t) throws AbortException
    {
        IRegisterTL2<?> local = t.getCopy(this);

        // Return local's value, if exists
        if (local != null) {
            return (T) local.getValue();
        } else {
            IRegisterTL2<T> copy = new RegisterTL2<T>(this.value.get());

            t.putCopy(this, copy);
            t.addToLrs(this);

            if (copy.getDate().after(t.getBirthDate())) {
                throw new AbortException("Copied date is after transaction birth date");
            }

            return (T) copy.getValue();
        }
    }

    public void write(Transaction t, T v) throws AbortException {
        this.write((TransactionTL2) t, v);
    }
    public void write(ITransactionTL2 t, T v) throws AbortException
    {
        t.putCopy(this, new RegisterTL2<>(v));
        t.addToLws(this);
    }

    @Override
    public int compareTo(IRegisterTL2<T> o) {
        return this.hashCode() > o.hashCode() ? 1 : -1;
    }
}
