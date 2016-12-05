package concurrent_set;

import java.util.Collection;

public interface ConcurrentSetInterface {

    /**
     * Add a value to the set if not exists.
     * Thread safe.
     * 
     * @param value
     */
    void add(String value);

    /**
     * Add all value of ids to the set.
     * Thread safe.
     * 
     * @param ids
     */
    void addAll(Collection<String> ids);

    /**
     * Return the next value. If no next value, return the first value.
     * Thread safe.
     * 
     * @return
     */
    String next();
    
    /**
     * Add a black value.
     * 
     * @param value
     */
    void addBlack(String value);

    int size();
}
