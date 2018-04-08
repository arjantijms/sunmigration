package org.omnifaces.sun.security.provider;

/**
 * Used for storing ProtectionDomains as keys in a Map.
 */
public class Key {

    private final int identityHashCode;

    public Key(int identityHashCode) {
        this.identityHashCode = identityHashCode;
    }

    @Override
    public int hashCode() {
        return identityHashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Key)) {
            return false;
        }

        return identityHashCode == ((Key) obj).identityHashCode;
    }
}