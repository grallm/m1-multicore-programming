package TL2.interfaces;

import TL2.AbortException;

public interface Transaction {
    void begin();
    void try_to_commit() throws AbortException;
    boolean isCommited();
}
