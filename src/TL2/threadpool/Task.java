package TL2.threadpool;

import TL2.AbortException;

import java.util.concurrent.Callable;

public interface Task<T> extends Callable<T> {
    T call() throws AbortException;
}
