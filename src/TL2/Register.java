package TL2;

import java.util.Date;

public class Register<T> {
    private T value;
    private Date date;
    private boolean locked;

    public boolean isLocked() {
        return locked;
    }
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setValue(T value) {
        this.value = value;
    }
    public T getValue() {
        return value;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public Date getDate() {
        return date;
    }

    public synchronized T read(Transaction t) throws AbortException {
        Register local = (Register) t.getCopy(this);

        // Return local if exists
        if (local != null) {
            return (T) local.value;
        } else {
            Register copy = null;
            try {
                copy = (Register) this.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                throw new AbortException();
            }

            t.putCopy(this, copy);
            t.addToLws(this);

            if (copy.date.after(t.getBirthDate())) {
                throw new AbortException();
            }

            return (T) copy.value;
        }
    }

    public synchronized void write(Transaction t, T v) throws AbortException {
        try {
            t.putCopy(this, (Register) this.clone());
            t.addToLws(this);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new AbortException();
        }
    }
}
