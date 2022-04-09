package TL2;

import TL2.interfaces.IRegisterTL2;
import TL2.interfaces.ITransactionTL2;
import TL2.utils.ClockManager;

import java.util.*;

public class TransactionTL2<T> implements ITransactionTL2<T>
{
    /**
     * Transaction start date
     */
    private int birthDate;
    boolean isCommited;
    /**
     * All written variables
     */
    private SortedSet<IRegisterTL2<T>> lws;
    /**
     * All read variables
     */
    private List<IRegisterTL2<T>> lrs;
    /**
     * All local variables
     * <p>
     * Map<Original register, Copy register>
     */
    private Map<IRegisterTL2<T>, IRegisterTL2<T>> lc;

    @Override
    public void addToLws(IRegisterTL2<T> original)
    {
        lws.add(original);
    }

    public void addToLrs(IRegisterTL2<T> original)
    {
        lrs.add(original);
    }

    @Override
    public int getBirthDate()
    {
        return birthDate;
    }

    /**
     * Set a copy with original register as key
     */
    @Override
    public void putCopy(IRegisterTL2<T> original, IRegisterTL2<T> copy)
    {
        lc.put(original, copy);
    }

    @Override
    public IRegisterTL2<T> getCopy(IRegisterTL2<T> original)
    {
        return lc.get(original);
    }

    @Override
    public void begin()
    {
        birthDate = ClockManager.clock.get();
        isCommited = false;
        lws = new TreeSet<>();
        lrs = new ArrayList<>();
        lc = new HashMap<>();
    }

    @Override
    public synchronized void try_to_commit() throws AbortException
    {
        // Lock all lws
        for (IRegisterTL2<T> register : lws)
        {
            register.lock();
        }

        // Check if no lrs are locked and date compatibility
        for (IRegisterTL2<T> register : lrs)
        {
            if (register.isLocked() && lws.contains(register))
            {
                // Release all locks and abort
                releaseLocks();
                throw new AbortException("Abort: register is locked");
            }
        }

        // Check lock date compatibility
        for (IRegisterTL2<T> register : lrs)
        {
            // System.out.println("Register date: " + register.getDate());
            // System.out.println("Transaction date: " + birthDate);
            if (register.getDate() > birthDate)
            {
                // Release all locks and abort
                releaseLocks();
                throw new AbortException("Abort: Date is not compatible");
            }
        }

        int commitDate = ClockManager.clock.incrementAndGet();

        // Update value and date of Write registers
        for (IRegisterTL2<T> register : lws)
        {
            register.setValue(lc.get(register).getValue());
            register.setDate(commitDate);
        }

        // TODO: clear lc here ? needed ?

        releaseLocks();

        isCommited = true;
    }

    @Override
    public boolean isCommited()
    {
        return isCommited;
    }

    private void releaseLocks()
    {
        for (IRegisterTL2<T> register : lws)
        {
            if (register.isLocked())
                register.unlock();
        }

        for (IRegisterTL2<T> register : lrs)
        {
            if (register.isLocked())
                register.unlock();
        }
    }
}
