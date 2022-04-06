package TL2.interfaces;

import TL2.AbortException;

import java.util.Date;

public interface ITransactionTL2 extends Transaction
{
    IRegisterTL2<?> getCopy(IRegisterTL2<?> original);

    void addToLws(IRegisterTL2<?> original);

    Date getBirthDate();

    void putCopy (IRegisterTL2<?> original, IRegisterTL2<?> copy);
}
