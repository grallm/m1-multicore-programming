package TL2;

public class TL2Transaction implements Transaction {
    public void begin() {

    }

    public void try_to_commit() throws AbortException {

    }

    public boolean isCommited() {
        return false;
    }
}
