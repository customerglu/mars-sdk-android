package ai.marax.android.sdk.core;

import android.util.Log;

/*
 * Logger class for MarsClient library
 * */
public class MarsLogger {
    private static int logLevel = MarsLogLevel.INFO;
    private static final String TAG = "MarsSDK";

    static void init(int l) {
        if (l > MarsLogLevel.VERBOSE) l = MarsLogLevel.VERBOSE;
        else if (l < MarsLogLevel.NONE) l = MarsLogLevel.NONE;
        logLevel = l;
    }

    static void logError(Throwable throwable) {
        if (logLevel >= MarsLogLevel.ERROR) {
            Log.e(TAG, "Error: ", throwable);
        }
    }

    static void logError(Exception ex) {
        if (logLevel >= MarsLogLevel.ERROR) {
            Log.e(TAG, "Error: ", ex.getCause());
        }
    }

    static void logError(String message) {
        if (logLevel >= MarsLogLevel.ERROR) {
            Log.e(TAG, "Error: " + message);
        }
    }

    public static void logWarn(String message) {
        if (logLevel >= MarsLogLevel.WARN) {
            Log.w(TAG, "Warn: " + message);
        }
    }

    static void logInfo(String message) {
        if (logLevel >= MarsLogLevel.INFO) {
            Log.i(TAG, "Info: " + message);
        }
    }

    static void logDebug(String message) {
        if (logLevel >= MarsLogLevel.DEBUG) {
            Log.d(TAG, "Debug: " + message);
        }
    }

    public static class MarsLogLevel {
        public static final int VERBOSE = 5;
        public static final int DEBUG = 4;
        public static final int INFO = 3;
        public static final int WARN = 2;
        public static final int ERROR = 1;
        public static final int NONE = 0;
    }
}
