package TL2.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class ClockManager
{
    public static AtomicInteger clock;

    public ClockManager()
    {
        clock = new AtomicInteger(0);
    }
}
