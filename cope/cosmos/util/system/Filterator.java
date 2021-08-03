package cope.cosmos.util.system;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class Filterator implements Iterator {

    private Iterator iterator;
    private Predicate predicate;
    private Object nextObject;
    private boolean nextObjectSet = false;

    public Filterator() {}

    public Filterator(Iterator iterator) {
        this.iterator = iterator;
    }

    public Filterator(Iterator iterator, Predicate predicate) {
        this.iterator = iterator;
        this.predicate = predicate;
    }

    public boolean hasNext() {
        return this.nextObjectSet || this.setNextObject();
    }

    public Object next() {
        if (!this.nextObjectSet && !this.setNextObject()) {
            throw new NoSuchElementException();
        } else {
            this.nextObjectSet = false;
            return this.nextObject;
        }
    }

    public void remove() {
        if (this.nextObjectSet) {
            throw new IllegalStateException("remove() cannot be called");
        } else {
            this.iterator.remove();
        }
    }

    public Iterator getIterator() {
        return this.iterator;
    }

    public void setIterator(Iterator iterator) {
        this.iterator = iterator;
        this.nextObject = null;
        this.nextObjectSet = false;
    }

    public Predicate getPredicate() {
        return this.predicate;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
        this.nextObject = null;
        this.nextObjectSet = false;
    }

    private boolean setNextObject() {
        while (true) {
            if (this.iterator.hasNext()) {
                Object object = this.iterator.next();

                if (!this.predicate.test(object)) {
                    continue;
                }

                this.nextObject = object;
                this.nextObjectSet = true;
                return true;
            }

            return false;
        }
    }
}
