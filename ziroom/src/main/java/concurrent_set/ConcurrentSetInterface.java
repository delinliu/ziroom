package concurrent_set;

public interface ConcurrentSetInterface {

    /**
     * Add a value to the set if not exists.
     * Thread safe.
     * 
     * @param value
     */
    void add(String value);

    /**
     * Return the next value. If no next value, return the first value.
     * Thread safe.
     * 
     * @return
     */
    String next();
}
