package TL2;

import java.util.*;

public class Transaction {
    /**
     * Transaction start date
     */
    private Date birthDate;
    /**
     * All written variables
     */
    private List<Register> lws;
    /**
     * All read variables
     */
    private List<Register> lrs;
    /**
     * All local variables
     *
     * Map<Original register, Copy register>
     */
    private Map<Register, Register> lx;

    public void addToLws (Register original) {
        lws.add(original);
    }

    public Date getBirthDate() {
        return birthDate;
    }

    /**
     * Set a copy with original register as key
     */
    public void putCopy (Register original, Register copy) {
        lx.put(original, copy);
    }
    public Register getCopy (Register original) {
        return lx.get(original);
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
