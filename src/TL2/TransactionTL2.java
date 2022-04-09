package TL2;

import TL2.interfaces.IRegisterTL2;
import TL2.interfaces.ITransactionTL2;

import java.util.*;

public class TransactionTL2<T> implements ITransactionTL2<T>
{
    /**
     * Transaction start date
     */
    private Date birthDate;
    private Date commitDate;
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
    public Date getBirthDate()
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
        birthDate = new Date();
        lws = new TreeSet<>();
        lrs = new ArrayList<>();
        lc = new HashMap<>();
        commitDate = null;
    }

    @Override
    public synchronized void try_to_commit() throws AbortException
    {
        // Lock all lws
        for (IRegisterTL2<?> register : lws)
        {
            register.lock();
        }

        // Check if no lrs are locked and date compatibility
        for (IRegisterTL2<?> register : lrs)
        {
            if (register.isLocked() || register.getDate().after(this.birthDate))
            {
                // Release all locks and abort
                for (IRegisterTL2<?> registerLws : lws)
                {
                    registerLws.unlock();
                }
                throw new AbortException("Register is locked or register date is after birth date");
            }
        }

        commitDate = new Date();

        // Update value and date of Write registers
        for (IRegisterTL2<T> register : lws)
        {
            register.setValue(lc.get(register).getValue());
            register.setDate(commitDate);
            register.unlock();
        }
    }

    @Override
    public boolean isCommited()
    {
        return commitDate != null;
    }
}
