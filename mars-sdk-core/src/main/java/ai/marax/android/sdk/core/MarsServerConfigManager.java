package ai.marax.android.sdk.core;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import ai.marax.android.sdk.core.util.Utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class MarsServerConfigManager {
    private static MarsServerConfigManager instance;
    private static SharedPreferences preferences;
    private static MarsServerConfig serverConfig;
    private static MarsConfig marsConfig;
    private Map<String, Object> integrationsMap = null;

    static MarsServerConfigManager getInstance(Application _application, String _writeKey, MarsConfig config) {
        if (instance == null) {
            MarsLogger.logDebug("Creating MarsServerConfigManager instance");
            instance = new MarsServerConfigManager(_application, _writeKey, config);
        }
        return instance;
    }

    private MarsServerConfigManager(Application _application, String _writeKey, MarsConfig _config) {
        preferences = _application.getSharedPreferences(Utils.MARS_PREFS, Context.MODE_PRIVATE);
        serverConfig = retrieveConfig();
        marsConfig = _config;
        boolean isConfigOutdated = isServerConfigOutDated();
        if (serverConfig == null) {
            MarsLogger.logDebug("Server config is not present in preference storage. downloading config");
            downloadConfig(_writeKey);
        } else {
            if (isConfigOutdated) {
                MarsLogger.logDebug("Server config is outdated. downloading config again");
                downloadConfig(_writeKey);
            } else {
                MarsLogger.logDebug("Server config found. Using existing config");
            }
        }
    }

    // update config if it is older than an day
    private boolean isServerConfigOutDated() {
        long lastUpdatedTime = preferences.getLong(Utils.MARS_SERVER_CONFIG_LAST_UPDATE_KEY, -1);
        MarsLogger.logDebug(String.format(Locale.US, "Last updated config time: %d", lastUpdatedTime));
        MarsLogger.logDebug(String.format(Locale.US, "ServerConfigInterval: %d", marsConfig.getConfigRefreshInterval()));
        if (lastUpdatedTime == -1) return true;

        long currentTime = System.currentTimeMillis();
        return (currentTime - lastUpdatedTime) > (marsConfig.getConfigRefreshInterval() * 60 * 60 * 1000);
    }

    private MarsServerConfig retrieveConfig() {
        String configJson = preferences.getString(Utils.MARS_SERVER_CONFIG_KEY, null);
        MarsLogger.logDebug(String.format(Locale.US, "MarsServerConfigManager: retrieveConfig: configJson: %s", configJson));
        if (configJson == null) return null;
        return new Gson().fromJson(configJson, MarsServerConfig.class);
    }

    private void downloadConfig(final String _writeKey) {
        // don't try to download anything if writeKey is not valid
        if (TextUtils.isEmpty(_writeKey)) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isDone = false;
                int retryCount = 0, retryTimeOut = 10;
                while (!isDone && retryCount <= 3) {
                    try {
                        String configUrl = Constants.CONFIG_PLANE_URL + "/sourceConfig";
                        MarsLogger.logDebug(String.format(Locale.US, "MarsServerConfigManager: downloadConfig: configUrl: %s", configUrl));
                        // create url object
                        URL url = new URL(configUrl);
                        // get connection object
                        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                        // set request method
                        httpConnection.setRequestMethod("GET");
                        // add basic auth_header
                        httpConnection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString((_writeKey + ":").getBytes("UTF-8"), Base64.DEFAULT));
                        // create connection
                        httpConnection.connect();
                        MarsLogger.logDebug(String.format(Locale.US, "MarsServerConfigManager: downloadConfig: response status code: %d", httpConnection.getResponseCode()));
                        if (httpConnection.getResponseCode() == 200) {
                            // get input stream from connection to get output from the server
                            BufferedInputStream bis = new BufferedInputStream(httpConnection.getInputStream());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            int res = bis.read();
                            // read response from the server
                            while (res != -1) {
                                baos.write((byte) res);
                                res = bis.read();
                            }

                            String configJson = baos.toString();
                            MarsLogger.logDebug(String.format(Locale.US, "MarsServerConfigManager: downloadConfig: configJson: %s", configJson));
                            // save config for future use
                            preferences.edit()
                                    .putLong(Utils.MARS_SERVER_CONFIG_LAST_UPDATE_KEY, System.currentTimeMillis())
                                    .putString(Utils.MARS_SERVER_CONFIG_KEY, configJson)
                                    .apply();

                            // update server config as well
                            serverConfig = new Gson().fromJson(configJson, MarsServerConfig.class);

                            // reset retry count
                            isDone = true;

                            MarsLogger.logInfo("MarsServerConfigManager: downloadConfig: server config download successful");
                        } else {
                            BufferedInputStream bis = new BufferedInputStream(httpConnection.getErrorStream());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            int res = bis.read();
                            // read response from the server
                            while (res != -1) {
                                baos.write((byte) res);
                                res = bis.read();
                            }
                            MarsLogger.logError("ServerError for FetchingConfig: " + baos.toString());
                            MarsLogger.logInfo("Retrying to download in " + retryTimeOut + "s");

                            retryCount += 1;
                            Thread.sleep(retryCount * retryTimeOut * 1000);
                        }
                    } catch (Exception ex) {
                        MarsLogger.logError(ex);
                        MarsLogger.logInfo("Retrying to download in " + retryTimeOut + "s");
                        retryCount += 1;
                        try {
                            Thread.sleep(retryCount * retryTimeOut * 1000);
                        } catch (InterruptedException e) {
                            MarsLogger.logError(e);
                        }
                    }
                }

            }
        }).start();
    }

    MarsServerConfig getConfig() {
        if (serverConfig == null) serverConfig = retrieveConfig();
        return serverConfig;
    }

    Map<String, Object> getIntegrations() {
        if (integrationsMap == null) {
            this.integrationsMap = new HashMap<>();
//            for (MarsServerDestination destination : serverConfig.source.destinations) {
//                if (!this.integrationsMap.containsKey(destination.destinationDefinition.definitionName))
//                    this.integrationsMap.put(destination.destinationDefinition.definitionName, destination.isDestinationEnabled);
//            }
        }
        return this.integrationsMap;
    }
}