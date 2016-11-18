package concurrent_set;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ConcurrentSet implements ConcurrentSetInterface {

    private volatile Set<String> oldData = new HashSet<>();
    private volatile Set<String> newData = new HashSet<>();
    private volatile Iterator<String> it = oldData.iterator();

    @Override
    public void add(String value) {
        synchronized (newData) {
            newData.add(value);
        }
    }

    @Override
    public String next() {
        synchronized (oldData) {
            if (it.hasNext()) {
                return it.next();
            } else {
                synchronized (newData) {
                    oldData.addAll(newData);
                    newData.clear();
                }
                it = oldData.iterator();
                return it.next();
            }
        }
    }

}
