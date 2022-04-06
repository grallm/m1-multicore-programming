package TL2;

import TL2.interfaces.Register;
import TL2.interfaces.Transaction;

import java.util.Date;

public class RegisterImpl<T> implements TL2.interfaces.Register<T>
{
    private T value;
    private Date date;
    private boolean locked;

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public Date getDate()
    {
        return date;
    }

    @Override
    public void setDate(Date date)
    {
        this.date = date;
    }

    @Override
    public T getValue()
    {
        return value;
    }

    @Override
    public void setValue(Object value)
    {
        this.value = (T) value;
    }

    @Override
    public synchronized T read(Transaction t) throws AbortException
    {
        Register<?> local = t.getCopy(this);

        // Return local if exists
        if (local != null) {
            return (T) local;
        } else {
            Register<?> copy = null;
            try {
                copy = (Register<?>) this.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                throw new AbortException("Can't clone this register");
            }

            t.putCopy(this, copy);
            t.addToLws(this);

            if (copy.getDate().after(t.getBirthDate())) {
                throw new AbortException("Copied date is after transaction birth date");
            }

            return (T) copy;
        }
    }

    @Override
    public synchronized void write(Transaction t, T v) throws AbortException
    {
        try {
        t.putCopy(this, (Register<?>) this.clone());
        t.addToLws(this);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new AbortException("Can't clone this register");
        }
    }
}
