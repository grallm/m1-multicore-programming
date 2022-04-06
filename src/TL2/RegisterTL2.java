package TL2;

import TL2.interfaces.*;

import java.util.Date;

public class RegisterTL2<T> implements IRegisterTL2<T>
{
    private T value;
    private Date date;
    private boolean locked;

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
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

        // Return local's value, if exists
        if (local != null) {
            return (T) local.getValue();
        } else {
            IRegisterTL2<?> copy = null;
            try {
                copy = (IRegisterTL2<?>) this.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                throw new AbortException("Can't clone this register");
            }

            t.putCopy(this, copy);
            t.addToLws(this);

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
}