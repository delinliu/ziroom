package concurrent_set;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ConcurrentSet implements ConcurrentSetInterface {

    private volatile Set<String> oldData = new HashSet<>();
    private volatile Set<String> newData = new HashSet<>();
    private volatile Iterator<String> it = oldData.iterator();
    private volatile Set<String> blackData = new HashSet<>();

    @Override
    public void add(String value) {
        synchronized (newData) {
            newData.add(value);
        }
    }

    @Override
    public void addBlack(String value) {
        synchronized (blackData) {
            blackData.add(value);
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
                    synchronized (blackData) {
                        oldData.removeAll(blackData);
                    }
                    newData.clear();
                }
                it = oldData.iterator();
                return it.next();
            }
        }
    }

    @Override
    public void addAll(Collection<String> ids) {
        synchronized (newData) {
            newData.addAll(ids);
        }
    }

    @Override
    public int size() {
        synchronized (oldData) {
            synchronized (newData) {
                oldData.addAll(newData);
                synchronized (blackData) {
                    oldData.removeAll(blackData);
                }
                newData.clear();
                it = oldData.iterator();
                return oldData.size();
            }
        }
    }

}
