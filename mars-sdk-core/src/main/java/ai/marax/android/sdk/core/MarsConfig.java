package ai.marax.android.sdk.core;

import android.text.TextUtils;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ai.marax.android.sdk.core.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/*
 * Config class for MarsClient
 * - endPointUri -> API endpoint for flushing events
 * - flushQueueSize -> maximum number of events to be batched
 * - dbCountThreshold -> maximum number of events to be persisted in local DB
 * - sleepTimeOut -> timeout for automatic flushing since last successful flush
 * - logLevel -> level of logging for debugging
 *
 * default values are set at Constants file
 *
 * */
public class MarsConfig {
    private String endPointUri;
    private int flushQueueSize;
    private int dbCountThreshold;
    private int sleepTimeOut;
    private int logLevel;
    private int configRefreshInterval;
    private List<MarsIntegration.Factory> factories;

    MarsConfig() {
        this(Constants.BASE_URL, Constants.FLUSH_QUEUE_SIZE, Constants.DB_COUNT_THRESHOLD, Constants.SLEEP_TIMEOUT, MarsLogger.MarsLogLevel.ERROR, Constants.CONFIG_REFRESH_INTERVAL, null);
    }

    private MarsConfig(String endPointUri, int flushQueueSize, int dbCountThreshold, int sleepTimeOut, int logLevel, int configRefreshInterval, List<MarsIntegration.Factory> factories) {
        MarsLogger.init(logLevel);

        if (TextUtils.isEmpty(endPointUri)) {
            MarsLogger.logError("endPointUri can not be null or empty. Set to default.");
            this.endPointUri = Constants.BASE_URL;
        } else if (!URLUtil.isValidUrl(endPointUri)) {
            MarsLogger.logError("Malformed endPointUri. Set to default");
            this.endPointUri = Constants.BASE_URL;
        } else {
            if (!endPointUri.endsWith("/")) endPointUri += "/";
            this.endPointUri = endPointUri;
        }

        if (flushQueueSize < Utils.MIN_FLUSH_QUEUE_SIZE || flushQueueSize > Utils.MAX_FLUSH_QUEUE_SIZE) {
            MarsLogger.logError("flushQueueSize is out of range. Min: 1, Max: 100. Set to default");
            this.flushQueueSize = Constants.FLUSH_QUEUE_SIZE;
        } else {
            this.flushQueueSize = flushQueueSize;
        }

        this.logLevel = logLevel;

        if (dbCountThreshold < 0) {
            MarsLogger.logError("invalid dbCountThreshold. Set to default");
            this.dbCountThreshold = Constants.DB_COUNT_THRESHOLD;
        } else {
            this.dbCountThreshold = dbCountThreshold;
        }

        if (configRefreshInterval > Utils.MAX_CONFIG_REFRESH_INTERVAL) {
            this.configRefreshInterval = Utils.MAX_CONFIG_REFRESH_INTERVAL;
        } else if (configRefreshInterval < Utils.MIN_CONFIG_REFRESH_INTERVAL) {
            this.configRefreshInterval = Utils.MIN_CONFIG_REFRESH_INTERVAL;
        } else {
            this.configRefreshInterval = configRefreshInterval;
        }

        if (sleepTimeOut < Utils.MIN_SLEEP_TIMEOUT) {
            MarsLogger.logError("invalid sleepTimeOut. Set to default");
            this.sleepTimeOut = Constants.SLEEP_TIMEOUT;
        } else {
            this.sleepTimeOut = sleepTimeOut;
        }

        if (factories != null && !factories.isEmpty()) {
            this.factories = factories;
        }
    }

    /**
     * @return endPointUrl (your data-plane url)
     */
    @NonNull
    public String getEndPointUri() {
        return endPointUri;
    }

    /**
     * @return flushQueueSize (# of events in a payload for v1/batch request)
     */
    public int getFlushQueueSize() {
        return flushQueueSize;
    }

    /**
     * @return dbCountThreshold (# of events to be kept in DB before deleting older events)
     */
    public int getDbCountThreshold() {
        return dbCountThreshold;
    }

    /**
     * @return sleepTimeOut (# of seconds to wait before sending a batch)
     */
    public int getSleepTimeOut() {
        return sleepTimeOut;
    }

    /**
     * @return logLevel (how much log we generate from the SDK)
     */
    public int getLogLevel() {
        return logLevel;
    }

    /**
     * @return configRefreshInterval (how often the server config should be fetched from the server)
     */
    public int getConfigRefreshInterval() {
        return configRefreshInterval;
    }

    /**
     * @return factories (list of native SDK factories integrated in the application)
     */
    @Nullable
    public List<MarsIntegration.Factory> getFactories() {
        return factories;
    }

    void setEndPointUri(String endPointUri) {
        this.endPointUri = endPointUri;
    }

    void setFlushQueueSize(int flushQueueSize) {
        this.flushQueueSize = flushQueueSize;
    }

    void setDbCountThreshold(int dbCountThreshold) {
        this.dbCountThreshold = dbCountThreshold;
    }

    void setSleepTimeOut(int sleepTimeOut) {
        this.sleepTimeOut = sleepTimeOut;
    }

    void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    void setFactories(List<MarsIntegration.Factory> factories) {
        this.factories = factories;
    }

    /**
     * @return custom toString implementation for MarsConfig
     */
    @Override
    @NonNull
    public String toString() {
        return String.format(Locale.US, "MarsConfig: endPointUrl:%s | flushQueueSize: %d | dbCountThreshold: %d | sleepTimeOut: %d | logLevel: %d", endPointUri, flushQueueSize, dbCountThreshold, sleepTimeOut, logLevel);
    }


    /**
     * Builder class for MarsConfig
     */
    public static class Builder {
        private List<MarsIntegration.Factory> factories = new ArrayList<>();

        /**
         * @param factory : Instance of MarsIntegration.Factory (for more information visit https://developers.marax.ai)
         * @return MarsConfig.Builder
         */
        public Builder withFactory(@NonNull MarsIntegration.Factory factory) {
            this.factories.add(factory);
            return this;
        }

        /**
         * @param factories List of instances of MarsIntegration.Factory
         * @return MarsConfig.Builder
         */
        public Builder withFactories(@NonNull List<MarsIntegration.Factory> factories) {
            this.factories.addAll(factories);
            return this;
        }

        /**
         * @param factories List of instances of MarsIntegration.Factory
         * @return MarsConfig.Builder
         */
        public Builder withFactories(@NonNull MarsIntegration.Factory... factories) {
            Collections.addAll(this.factories, factories);
            return this;
        }

        private String endPointUri = Constants.BASE_URL;

        /**
         * @param endPointUri Your data-plane Url
         * @return MarsConfig.Builder
         */
        public Builder withEndPointUri(@NonNull String endPointUri) {
            if (TextUtils.isEmpty(endPointUri)) {
                MarsLogger.logError("endPointUri can not be null or empty. Set to default");
                return this;
            }
            if (!URLUtil.isValidUrl(endPointUri)) {
                MarsLogger.logError("Malformed endPointUri. Set to default");
                return this;
            }
            this.endPointUri = endPointUri;
            return this;
        }

        private int flushQueueSize = Constants.FLUSH_QUEUE_SIZE;

        /**
         * @param flushQueueSize No. of events you want to send in a batch (min = 1, max = 100)
         * @return MarsConfig.Builder
         */
        public Builder withFlushQueueSize(int flushQueueSize) {
            if (flushQueueSize < 1 || flushQueueSize > 100) {
                MarsLogger.logError("flushQueueSize is out of range. Min: 1, Max: 100. Set to default");
                return this;
            }
            this.flushQueueSize = flushQueueSize;
            return this;
        }

        private boolean isDebug = false;

        /**
         * @param isDebug Set it true to initialize SDK in debug mode
         * @return MarsConfig.Builder
         * @deprecated Use withLogLevel(int logLevel) instead
         */
        @Deprecated
        public Builder withDebug(boolean isDebug) {
            this.isDebug = isDebug;
            return this;
        }

        private int logLevel = MarsLogger.MarsLogLevel.NONE;

        /**
         * @param logLevel Determine how much log you want to generate.
         *                 Use MarsLogger.MarsLogLevel.NONE for production
         * @return MarsConfig.Builder
         */
        public Builder withLogLevel(int logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        private int dbThresholdCount = Constants.DB_COUNT_THRESHOLD;

        /**
         * @param dbThresholdCount No of events to be persisted in DB
         * @return MarsConfig.Builder
         */
        public Builder withDbThresholdCount(int dbThresholdCount) {
            this.dbThresholdCount = dbThresholdCount;
            return this;
        }

        private int sleepTimeout = Constants.SLEEP_TIMEOUT;

        /**
         * @param sleepCount No of seconds to wait before sending any batch
         * @return MarsConfig.Builder
         */
        public Builder withSleepCount(int sleepCount) {
            this.sleepTimeout = sleepCount;
            return this;
        }

        private int configRefreshInterval = Constants.CONFIG_REFRESH_INTERVAL;

        /**
         * @param configRefreshInterval How often you want to fetch the config from the server.
         *                              Min : 1 hr
         *                              Max : 24 hrs
         * @return MarsConfig.Builder
         */
        public Builder withConfigRefreshInterval(int configRefreshInterval) {
            this.configRefreshInterval = configRefreshInterval;
            return this;
        }

        /**
         * Finalize your config building
         *
         * @return MarsConfig
         */
        public MarsConfig build() {
            return new MarsConfig(this.endPointUri, this.flushQueueSize, this.dbThresholdCount, this.sleepTimeout, this.isDebug ? MarsLogger.MarsLogLevel.DEBUG : logLevel, this.configRefreshInterval, this.factories);
        }
    }
}

