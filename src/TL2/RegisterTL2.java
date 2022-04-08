package TL2;

import TL2.interfaces.*;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RegisterTL2<T> implements IRegisterTL2<T>
{
    private T value;
    private Date date;
    private final Lock lock;
    private boolean isLocked;

    /**
     * Constructor
     */
    public RegisterTL2(T value) {
        this.value = value;
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
        return value;
    }

    public void setValue(T value)
    {
        this.value = (T) value;
    }

    public T read(Transaction t) throws AbortException {
        return this.read((TransactionTL2) t);
    }
    public T read(ITransactionTL2 t) throws AbortException
    {
        IRegisterTL2<?> local = t.getCopy(this);
        // System.out.println("read " + this);

        // Return local's value, if exists
        if (local != null) {
            return (T) local.getValue();
        } else {
            IRegisterTL2<T> copy = null;
            try {
                copy = (IRegisterTL2<T>) this.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                throw new AbortException("Can't clone this register");
            }

            t.putCopy(this, copy);
            t.addToLws(this);

            // System.out.println(this.date.toInstant().toEpochMilli());
            // System.out.println(copy.getDate().toInstant().toEpochMilli());
            // System.out.println(t.getBirthDate().toInstant().toEpochMilli());
            // System.out.println(copy.getDate().after(t.getBirthDate()));
            // System.out.println(copy.getDate().before(t.getBirthDate()));
            // System.out.println(copy.getDate().compareTo(t.getBirthDate()));
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
        try {
            t.putCopy(this, (IRegisterTL2<?>) this.clone());
            t.addToLws(this);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new AbortException("Can't clone this register");
        }
    }

    @Override
    public int compareTo(IRegisterTL2<T> o) {
        return this.hashCode() > o.hashCode() ? 1 : -1;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        System.out.println("clone");
        RegisterTL2<T> clone = (RegisterTL2<T>) super.clone();

        clone.date = (Date) this.date.clone();
        clone.value = this.getValue();
        return clone;
    }
}
