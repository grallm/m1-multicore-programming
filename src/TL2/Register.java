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

    public synchronized T read(Transaction t) throws AbortException {
        Register local = (Register) t.getFromLws(this.hashCode());

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
            t.setInLws(this.hashCode(), copy);

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
