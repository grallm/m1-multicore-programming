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
    private Map<Integer, TL2Register> lws;
    /**
     * All read variables
     */
    private List<Integer> lrs;

    public TL2Register getFromLws(int id) {
        return lws.get(id);
    }

    public TL2Register setInLws(int id, TL2Register copy) {
        return lws.put(id, copy);
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void begin() {
        birthDate = new Date();
        lws = new HashMap<>();
        lrs = new ArrayList<>();
    }

    public void try_to_commit() throws AbortException {

    }

    public boolean isCommited() {
        return false;
    }
}
