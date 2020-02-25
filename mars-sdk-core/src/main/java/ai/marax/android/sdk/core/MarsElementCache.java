package ai.marax.android.sdk.core;

import android.app.Application;

import java.util.Map;

/*
 * MarsContext is populated once and cached through out the lifecycle
 * */
class MarsElementCache {
    private static MarsContext cachedContext;

    private MarsElementCache() {
        // stop instantiating
    }

    static void initiate(Application application) {
        if (cachedContext == null) {
            MarsLogger.logDebug("MarsElementCache: initiating MarsContext");
            cachedContext = new MarsContext(application);
        }
    }

    static MarsContext getCachedContext() {
        return cachedContext;
    }

    static void reset() {
        cachedContext.updateTraits(null);
        persistTraits();
    }

    static void persistTraits() {
        cachedContext.persistTraits();
    }

    static void updateTraits(Map<String, Object> traits) {
        cachedContext.updateTraitsMap(traits);
    }
}


