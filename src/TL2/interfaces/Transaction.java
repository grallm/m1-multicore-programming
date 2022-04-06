package TL2.interfaces;

import TL2.AbortException;

import java.util.Date;

public interface Transaction
{
    public void begin();
    public void try_to_commit() throws AbortException;
    public boolean isCommited();

    public Register<?> getCopy(Register<?> original);

    void addToLws(Register<?> original);

    Date getBirthDate();

    public void putCopy (Register<?> original, Register<?> copy);
}
