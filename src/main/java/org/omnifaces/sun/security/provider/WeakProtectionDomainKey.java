package org.omnifaces.sun.security.provider;

import static java.lang.System.identityHashCode;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.security.ProtectionDomain;

/**
 * A weak key for a ProtectionDomain.
 */
public class WeakProtectionDomainKey extends WeakReference<Key> {
    /**
     * Saved value of the referent's identity hash code, to maintain a consistent hash code after the
     * referent has been cleared
     */
    private final int hash;

    /**
     * A key representing a null ProtectionDomain.
     */
    private static final Key NULL_KEY = new Key(identityHashCode(new ProtectionDomain(null, null)));

    /**
     * Create a new WeakProtectionDomain with the specified domain and registered with a queue.
     */
    WeakProtectionDomainKey(ProtectionDomain pd, ReferenceQueue<Key> rq) {
        this((pd == null ? NULL_KEY : new Key(identityHashCode(pd))), rq);
    }

    WeakProtectionDomainKey(ProtectionDomain pd) {
        this(pd == null ? NULL_KEY : new Key(identityHashCode(pd)));
    }

    private WeakProtectionDomainKey(Key key, ReferenceQueue<Key> rq) {
        super(key, rq);
        hash = key.hashCode();
    }

    private WeakProtectionDomainKey(Key key) {
        super(key);
        hash = key.hashCode();
    }

    /**
     * Returns the identity hash code of the original referent.
     */
    @Override
    public int hashCode() {
        return hash;
    }

    /**
     * Returns true if the given object is an identical WeakProtectionDomainKey instance, or, if this
     * object's referent has not been cleared and the given object is another WeakProtectionDomainKey
     * instance with an identical non-null referent as this one.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof WeakProtectionDomainKey) {
            Object referent = get();
            return (referent != null) && (referent == ((WeakProtectionDomainKey) obj).get());
        } else {
            return false;
        }
    }
}