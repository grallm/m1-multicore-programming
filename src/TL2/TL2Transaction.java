package TL2;

import java.util.*;

public class TL2Transaction {
    /**
     * Transaction start date
     */
    private Date birthDate;
    /**
     * All written variables
     */
    private List<TL2Register> lws;
    /**
     * All read variables
     */
    private List<TL2Register> lrs;
    /**
     * All local variables
     *
     * Map<Original register, Copy register>
     */
    private Map<TL2Register, TL2Register> lx;

    public void addToLws (TL2Register original) {
        lws.add(original);
    }

    public Date getBirthDate() {
        return birthDate;
    }

    /**
     * Add a copy with original register as key
     */
    public void addToCopies (TL2Register original, TL2Register copy) {
        lx.put(original, copy);
    }
    public boolean hasCopy (TL2Register original) {
        return lx.get(original) != null;
    }

    public void begin() {
        birthDate = new Date();
        lws = new ArrayList<>();
        lrs = new ArrayList<>();
        lx = new HashMap<>();
    }

    public synchronized void try_to_commit() throws AbortException {
        // Lock all if available
        // for (Integer var : lrs) {
        //
        // }
    }

    public boolean isCommited() {
        return false;
    }
}
