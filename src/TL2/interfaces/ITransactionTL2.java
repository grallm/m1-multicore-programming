package TL2.interfaces;

import TL2.AbortException;

import java.util.Date;

public interface ITransactionTL2<T> extends Transaction
{
    IRegisterTL2<T> getCopy(IRegisterTL2<T> original);

    void addToLws(IRegisterTL2<T> original);

    Date getBirthDate();

    void putCopy (IRegisterTL2<T> original, IRegisterTL2<T> copy);
}
