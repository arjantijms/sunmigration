package org.omnifaces.sun.security.provider;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.security.PermissionCollection;
import java.security.ProtectionDomain;
import java.util.concurrent.ConcurrentHashMap;

import org.omnifaces.sun.misc.JavaSecurityProtectionDomainAccess.ProtectionDomainCache;

/**
 * A cache of ProtectionDomains and their Permissions.
 *
 * This class stores ProtectionDomains as weak keys in a ConcurrentHashMap with additional support
 * for checking and removing weak keys that are no longer in use. There can be cases where the
 * permission collection may have a chain of strong references back to the ProtectionDomain, which
 * ordinarily would prevent the entry from being removed from the map. To address that, we wrap the
 * permission collection in a SoftReference so that it can be reclaimed by the garbage collector due
 * to memory demand.
 */
public class PDCache implements ProtectionDomainCache {
    private final ConcurrentHashMap<WeakProtectionDomainKey, SoftReference<PermissionCollection>> pdMap = new ConcurrentHashMap<>();
    private final ReferenceQueue<Key> queue = new ReferenceQueue<>();

    @Override
    public void put(ProtectionDomain pd, PermissionCollection pc) {
        processQueue(queue, pdMap);
        WeakProtectionDomainKey weakPd = new WeakProtectionDomainKey(pd, queue);
        pdMap.put(weakPd, new SoftReference<>(pc));
    }

    @Override
    public PermissionCollection get(ProtectionDomain pd) {
        processQueue(queue, pdMap);
        WeakProtectionDomainKey weakPd = new WeakProtectionDomainKey(pd);
        SoftReference<PermissionCollection> sr = pdMap.get(weakPd);
        return (sr == null) ? null : sr.get();
    }

    /**
     * Removes weak keys from the map that have been enqueued on the reference queue and are no longer
     * in use.
     */
    private static void processQueue(ReferenceQueue<Key> queue, ConcurrentHashMap<? extends WeakReference<Key>, ?> pdMap) {
        Reference<? extends Key> ref;
        while ((ref = queue.poll()) != null) {
            pdMap.remove(ref);
        }
    }
}