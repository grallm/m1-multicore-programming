package TL2;

import TL2.interfaces.Register;
import TL2.interfaces.Transaction;

import java.util.*;

public class TransactionImpl implements Transaction
{
    /**
     * Transaction start date
     */
    private Date                            birthDate;
    private Date commitDate;
    /**
     * All written variables
     */
    private List<Register>              lws;
    /**
     * All read variables
     */
    private List<Register>              lrs;
    /**
     * All local variables
     *
     * Map<Original register, Copy register>
     */
    private Map<Register, Register> lc;

    @Override
    public void addToLws (Register original) {
        lws.add(original);
    }

    @Override
    public Date getBirthDate() {
        return birthDate;
    }

    /**
     * Set a copy with original register as key
     */
    @Override
    public void putCopy (Register original, Register copy) {
        lc.put(original, copy);
    }

    @Override
    public Register getCopy (Register original) {
        return lc.get(original);
    }

    @Override
    public void begin() {
        birthDate = new Date();
        lws = new ArrayList<>();
        lrs = new ArrayList<>();
        lc = new HashMap<>();
        commitDate = null;
    }

    @Override
    public synchronized void try_to_commit() throws AbortException
    {
        // Lock all lws
        for (Register register : lws) {
            register.setLocked(true);
        }

        // Check if no lrs are locked and date compatibility
        for (Register register : lrs) {
            if (register.isLocked() || register.getDate().after(this.birthDate)) {
                // Release all locks and abort
                for (Register registerLws : lws) {
                    registerLws.setLocked(false);
                }
                throw new AbortException("Register is locked or register date is after birth date");
            }
        }

        commitDate = new Date();

        // Update value and date of Write registers
        for (Register register : lws) {
            register.setValue(lc.get(register).getValue());
            register.setDate(commitDate);
            register.setLocked(false);
        }
    }

    @Override
    public boolean isCommited() {
        return commitDate != null;
    }
}
