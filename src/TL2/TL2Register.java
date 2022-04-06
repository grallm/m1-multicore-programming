package TL2;

import java.util.Date;

public class TL2Register<T> {
    private T value;
    private Date date;
    private boolean locked;

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public synchronized T read(TL2Transaction t) throws AbortException {
        TL2Register local = (TL2Register) t.getFromLws(this.hashCode());

        // Return local if exists
        if (local != null) {
            return (T) local.value;
        } else {
            TL2Register copy = null;
            try {
                copy = (TL2Register) this.clone();
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

    public synchronized void write(TL2Transaction t, T v) throws AbortException {
        TL2Register copy = null;
        try {
            copy = (TL2Register) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new AbortException();
        }
        t.setInLws(this.hashCode(), copy);
    }
}
