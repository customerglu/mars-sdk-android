package ai.marax.android.sdk.core;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.JavascriptInterface;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ai.marax.android.sdk.core.util.Utils;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/*
 * Primary class to be used in client
 * */
public class MarsClient {
    // singleton instance
    private static MarsClient instance;
    // repository instance
    private static EventRepository repository;
    private static Application application;

    /*
     * private constructor
     * */
    private MarsClient() {
        // message constructor initialization
    }


    /**
     * API for getting MarsClient instance with bare minimum
     *
     * @param context  Your Application context
     * @param writeKey Your Android WriteKey from Mars dashboard (https://app.marax.ai)
     * @return MarsClient instance to be used further
     */
    @NonNull
    public static MarsClient getInstance(@NonNull Context context, @Nullable String writeKey) {
        return getInstance(context, writeKey, new MarsConfig());
    }

    /**
     * API for getting MarsClient instance with custom values for settings through
     * MarsConfig.Builder
     *
     * @param context  Application context
     * @param writeKey Your Android WriteKey from Mars dashboard (https://app.marax.ai)
     * @param builder  Instance of MarsConfig.Builder for customised settings
     * @return MarsClient instance to be used further
     */
    @NonNull
    public static MarsClient getInstance(@NonNull Context context, @Nullable String writeKey, @NonNull MarsConfig.Builder builder) {
        return getInstance(context, writeKey, builder.build());
    }

    /**
     * API for getting <b>MarsClient</b> instance with custom values for settings through
     * MarsConfig.Builder
     *
     * @param context  Application context
     * @param writeKey Your Android WriteKey from Mars dashboard (https://app.mars.ai)
     * @param config   Instance of MarsConfig for customised settings
     * @return MarsClient instance to be used further
     */
    @NonNull
    public static MarsClient getInstance(@NonNull Context context, @Nullable String writeKey, @Nullable MarsConfig config) {
        // check if instance is already initiated
        if (instance == null) {
            // assert writeKey is not null or empty
            if (TextUtils.isEmpty(writeKey)) {
                MarsLogger.logError("MarsClient: getInstance: writeKey can not be null or empty");
            }
            // assert config is not null
            if (config == null) {
                config = new MarsConfig();
            } else {
                if (TextUtils.isEmpty(config.getEndPointUri())) {
                    config.setEndPointUri(Constants.BASE_URL);
                }
                if (config.getFlushQueueSize() < 0 || config.getFlushQueueSize() > 100) {
                    config.setFlushQueueSize(Constants.FLUSH_QUEUE_SIZE);
                }
                if (config.getDbCountThreshold() < 0) {
                    config.setDbCountThreshold(Constants.DB_COUNT_THRESHOLD);
                }
                // assure sleepTimeOut never goes below 10s
                if (config.getSleepTimeOut() < 10) {
                    config.setSleepTimeOut(10);
                }
            }

            // get application from provided context
            application = (Application) context.getApplicationContext();

            // initiate MarsClient instance
            instance = new MarsClient();

            // initiate EventRepository class
            if (application != null && writeKey != null) {
                repository = new EventRepository(application, writeKey, config);
            }
        }
        return instance;
    }

    /*
     * package private api to be used in EventRepository
     * */
    @Nullable
    static MarsClient getInstance() {
        return instance;
    }

    /**
     * Create <b>MarsClient</b> with Application context
     *
     * <b>Segment compatible API</b>
     *
     * @param context Application Context
     * @return instance of MarsClient
     */
    @NonNull
    public static MarsClient with(@NonNull Context context) {
        if (instance == null) {
            String writeKey = null;
            writeKey = Utils.getWriteKeyFromStrings(context);
            return getInstance(context, writeKey);
        }
        return instance;
    }

    /**
     * @return Application context
     */
    @Nullable
    public Application getApplication() {
        return application;
    }

    /**
     *
     * @param context
     * @return
     */
    public WebView drawOfferView(Context context) {
        WebView offerWebView = new WebView(context);

        WebSettings webSettings = offerWebView.getSettings();
        offerWebView.addJavascriptInterface(new MarsClient.WebAppInterface(context), "Android");
//        offerWebView.setHttpAuthUsernamePassword ("https://enn2ytpu410z.x.pipedream.net",  "test", "hoy", this.repository.getAuthHeaderString());
        webSettings.setJavaScriptEnabled(true);

        offerWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        offerWebView.loadUrl("https://nextjs-offer-constructs.now.sh");

        return offerWebView;
    }

    /**
     * WebInterface class
     *
     * For binding with the javascript
     *
     */
    public class WebAppInterface {
        Context mContext;


        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {

            mContext = c;
        }
        /** Show a toast from the web page */
        @JavascriptInterface
        public void killView(String toast) {
            Map<String,Object> properties = new HashMap<>();
            properties.put("offer_accepted", "no");
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
            track("OFFER_ACCEPTED", new MarsProperty().putValue(properties));
        }
    }

    /**
     * Register device with firebase token and deviceID
     *
     *
     *
     * @param token    Name of the event you want to track
     * @param deviceId Firebase Instance Id //deviceId
     */
    public void registerDevice(@NonNull String token, @NonNull String deviceId) {

        final String tempAuthToken = this.repository.getAuthHeaderString();
        final String maraxUrl = this.repository.getMaraxUrl();
        SharedPreferences pref = getApplication().getSharedPreferences(Utils.MARS_PREFS, Context.MODE_PRIVATE);
        pref.getString("maraxId", "undefined");

        final Map<String,Object> deviceProperties = new HashMap<>();
        deviceProperties.put("deviceId", deviceId);
        deviceProperties.put("authType", "token");
        deviceProperties.put("authValue", token);
        deviceProperties.put("pnService", "firebase");
        deviceProperties.put("osType", "android");
        deviceProperties.put("timezone", Utils.getTimeZone());
        deviceProperties.put("maraxId",pref.getString("maraxId", "undefined"));




        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isDone = false;
                int retryCount = 0, retryTimeOut = 10;
                Log.i("insideRun", "okay");
                while (!isDone && retryCount <= 3) {
                    try {
                        String configUrl = maraxUrl + "v1/user/device";

                        MarsLogger.logDebug(String.format(Locale.US, "MarsServerConfigManager: Hitting registration endpoint: configUrl: %s", configUrl));

                        // create url object
                        URL url = new URL(configUrl);
                        // get connection object
                        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                        // set request method
                        httpConnection.setRequestMethod("POST");
                        httpConnection.setDoOutput(true);
                        // add basic auth_header
                        httpConnection.setRequestProperty("Authorization", "Basic " +  tempAuthToken);
                        httpConnection.setRequestProperty("Content-Type", "application/json");

                        OutputStream os = httpConnection.getOutputStream();
                        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                        JSONObject postData = new JSONObject(deviceProperties);
                        osw.write(postData.toString());
                        osw.flush();
                        osw.close();
                        os.close();

                        // create connection
                        httpConnection.connect();
                        MarsLogger.logDebug(String.format(Locale.US, "MarsServerConfigManager: registerDevice: response status code: %d", httpConnection.getResponseCode()));
                        if (httpConnection.getResponseCode() == 200) {
                            BufferedInputStream bis = new BufferedInputStream(httpConnection.getInputStream());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            int res = bis.read();
                            // read response from the server
                            while (res != -1) {
                                baos.write((byte) res);
                                res = bis.read();
                            }
                            // finally return response when reading from server is completed
                            JsonObject convertedObject = new Gson().fromJson(baos.toString(), JsonObject.class);

                            String mId = convertedObject.getAsJsonObject("data").get("maraxId").getAsString();

//                            String mId = convertedObject.get("success").getAsString();

                            MarsLogger.logDebug(String.format(Locale.US, "MarsServerConfigManager: registerDevice: response maraxId : %s", mId));

                            // Add/update the maraxId in SharedPreferences
                            SharedPreferences preferences = getApplication().getSharedPreferences(Utils.MARS_PREFS, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("maraxId", mId);
                            editor.apply();

                            isDone = true;
                            MarsLogger.logInfo("MarsServerConfigManager: Successfully updated the token");
                            MarsLogger.logDebug(String.format(Locale.US, "MarsServerConfigManager: registerDevice: updated maraxId: %s", preferences.getString("maraxId", "undefined")));

                        } else {
                            BufferedInputStream bis = new BufferedInputStream(httpConnection.getErrorStream());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            int res = bis.read();
                            // read response from the server
                            while (res != -1) {
                                baos.write((byte) res);
                                res = bis.read();
                            }
                            // finally return response when reading from server is completed
                            MarsLogger.logError("MarsServerConfigManager: registerDevice: ServerError: " + baos.toString());
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


    /**
     * Update the user attributes
     *
     * @param properties    Map of all the attributes to be updated/added
     */
    public void updateAttributes(@NonNull  Map<String,Object> properties) {
        final String tempAuthToken = this.repository.getAuthHeaderString();
        final String maraxUrl = this.repository.getMaraxUrl();
        SharedPreferences preferences = getApplication().getSharedPreferences(Utils.MARS_PREFS, Context.MODE_PRIVATE);
        String mId = preferences.getString("maraxId", "undefined");

        final Map<String,Object> updateAttributeData = new HashMap<String, Object>();
        updateAttributeData.put("maraxId", mId);
        updateAttributeData.put("integration", "mars");
        updateAttributeData.put("attributes", properties);


        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isDone = false;
                int retryCount = 0, retryTimeOut = 10;
                Log.i("insideRun", "okay");
                while (!isDone && retryCount <= 3) {
                    try {
                        String configUrl = maraxUrl + "v1/user/attribute";

                        String writeKeyTemp = tempAuthToken;
                        MarsLogger.logDebug(String.format(Locale.US, "MarsServerConfigManager: Hitting update attribute endpoint: configUrl: %s", configUrl));

                        // create url object
                        URL url = new URL(configUrl);
                        // get connection object
                        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                        // set request method
                        httpConnection.setRequestMethod("POST");
                        httpConnection.setDoOutput(true);
                        // add basic auth_header
                        httpConnection.setRequestProperty("Authorization", "Basic " +  tempAuthToken);
                        httpConnection.setRequestProperty("Content-Type", "application/json");

                        OutputStream os = httpConnection.getOutputStream();
                        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                        JSONObject postData = new JSONObject(updateAttributeData);
                        osw.write(postData.toString());
                        osw.flush();
                        osw.close();
                        os.close();

                        // create connection
                        httpConnection.connect();
                        MarsLogger.logDebug(String.format(Locale.US, "MarsServerConfigManager: updateAttributes: response status code: %d", httpConnection.getResponseCode()));
                        if (httpConnection.getResponseCode() == 200) {
                            BufferedInputStream bis = new BufferedInputStream(httpConnection.getInputStream());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            int res = bis.read();
                            // read response from the server
                            while (res != -1) {
                                baos.write((byte) res);
                                res = bis.read();
                            }
                            // finally return response when reading from server is completed
                            JsonObject convertedObject = new Gson().fromJson(baos.toString(), JsonObject.class);

                            String success = convertedObject.get("success").getAsString();

                            Log.i("attrUpdateSuccess", convertedObject.get("success").getAsString());
                           MarsLogger.logDebug(String.format(Locale.US, "MarsServerConfigManager: updateAttributes: response success : %s", success));

                            isDone = true;
                            MarsLogger.logInfo("MarsServerConfigManager: Successfully updated the attributes");

                        } else {
                            BufferedInputStream bis = new BufferedInputStream(httpConnection.getErrorStream());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            int res = bis.read();
                            // read response from the server
                            while (res != -1) {
                                baos.write((byte) res);
                                res = bis.read();
                            }
                            // finally return response when reading from server is completed
                            MarsLogger.logError("EventRepository: updateAttributes: ServerError: " + baos.toString());
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

    /**
     * Track your event using MarsMessageBuilder
     *
     * @param builder instance of MarsMessageBuilder
     */
    public void track(@NonNull MarsMessageBuilder builder) {
        track(builder.build());
    }

    /**
     * Track your event using MarsMessage
     *
     * @param message MarsMessage you want to track. Use MarsMessageBuilder to create the message object
     */
    public void track(@NonNull MarsMessage message) {
        message.setType(MessageType.TRACK);
        if (repository != null) repository.dump(message);
    }

    /**
     * Track your event with eventName
     *
     * <b>Segment compatible API</b>
     *
     * @param event Name of the event you want to track
     */
    public void track(@NonNull String event) {
        track(new MarsMessageBuilder().setEventName(event).build());
    }

    /**
     * Track your event with name and properties
     *
     * <b>Segment compatible API</b>
     *
     * @param event    Name of the event you want to track
     * @param property MarsProperty object you want to pass with the track call
     */
    public void track(@NonNull String event, @Nullable MarsProperty property) {
        track(new MarsMessageBuilder().setEventName(event).setProperty(property).build());
    }

    /**
     * Track your event
     *
     * <b>Segment compatible API</b>
     *
     * @param event    Name of the event you want to track
     * @param property MarsProperty object you want to pass with the track call
     * @param option   Options related to this track call
     */
    public void track(@NonNull String event, @Nullable MarsProperty property, @Nullable MarsOption option) {
        track(new MarsMessageBuilder()
                .setEventName(event)
                .setProperty(property)
                .setMarsOption(option)
                .build());
    }

    /**
     * Record screen view with MarsMessageBuilder
     *
     * @param builder instance of MarsMessageBuilder
     */
    public void screen(@NonNull MarsMessageBuilder builder) {
        screen(builder.build());
    }

    /**
     * Record screen view with MarsMessage
     *
     * @param message instance of MarsMessage
     */
    public void screen(@NonNull MarsMessage message) {
        message.setType(MessageType.SCREEN);
        if (repository != null) repository.dump(message);
    }

    /**
     * Record screen view with screen name
     *
     * <b>Segment compatible API</b>
     *
     * @param screenName Name of the screen
     */
    public void screen(@NonNull String screenName) {
        MarsProperty property = new MarsProperty();
        property.put("name", screenName);
        screen(new MarsMessageBuilder().setEventName(screenName).setProperty(property).build());
    }

    /**
     * Record screen view with screen name and object
     *
     * @param screenName Name of the screen
     * @param property   MarsProperty object you want to pass with the screen call
     */
    public void screen(@NonNull String screenName, @Nullable MarsProperty property) {
        if (property == null) property = new MarsProperty();
        property.put("name", screenName);
        screen(new MarsMessageBuilder().setEventName(screenName).setProperty(property).build());
    }

    /**
     * Record screen view
     *
     * @param screenName Name of the screen
     * @param category   Name of the category of the screen
     * @param property   MarsProperty object you want to pass with the screen call
     * @param option     Options related to this screen call
     */
    public void screen(@NonNull String screenName, @NonNull String category, @Nullable MarsProperty property, @Nullable MarsOption option) {
        if (property == null) property = new MarsProperty();
        property.put("category", category);
        property.put("name", screenName);
        screen(new MarsMessageBuilder().setEventName(screenName).setProperty(property).setMarsOption(option).build());
    }

    /**
     * Record screen view
     *
     * @param screenName Name of the screen
     * @param property   MarsProperty object you want to pass with the screen call
     * @param option     Options related to this screen call
     */
    public void screen(@NonNull String screenName, @Nullable MarsProperty property, @Nullable MarsOption option) {
        screen(new MarsMessageBuilder()
                .setEventName(screenName)
                .setProperty(property)
                .setMarsOption(option)
                .build());
    }

    /**
     * Identify your user
     *
     * @param message instance of MarsMessage
     */
    public void identify(@NonNull MarsMessage message) {
        // update cached traits and persist
        MarsElementCache.updateTraits(message.getTraits());
        MarsElementCache.persistTraits();

        // set message type to identify
        message.setType(MessageType.IDENTIFY);

        // dump to repository
        if (repository != null) repository.dump(message);
    }

    /**
     * Identify your user
     *
     * <b>Segment compatible API</b>
     *
     * @param traits MarsTraits object
     * @param option MarsOption object
     */
    public void identify(@NonNull MarsTraits traits, @Nullable MarsOption option) {
        MarsMessage message = new MarsMessageBuilder()
                .setEventName(MessageType.IDENTIFY)
                .setUserId(traits.getId())
                .setMarsOption(option)
                .build();
        message.updateTraits(traits);
        identify(message);
    }

    /**
     * Identify your user
     *
     * @param traits MarsTraits object
     */
    public void identify(@NonNull MarsTraits traits) {
        identify(traits, null);
    }

    /**
     * Identify your user
     *
     * @param builder MarsTraitsBuilder object
     */
    public void identify(@NonNull MarsTraitsBuilder builder) {
        identify(builder.build());
    }

    /**
     * Identify your user
     *
     * <b>Segment compatible API</b>
     *
     * @param userId userId of your User
     */
    public void identify(@NonNull String userId) {
        identify(new MarsTraitsBuilder().setId(userId));
    }

    /**
     * Identify your user
     *
     * <b>Segment compatible API</b>
     *
     * @param userId userId of your user
     * @param traits Other user properties using MarsTraits class
     * @param option Extra options using MarsOption class
     */
    public void identify(@NonNull String userId, @Nullable MarsTraits traits, @Nullable MarsOption option) {
        // create new traits object from cache if supplied traits is null
        if (traits == null) traits = new MarsTraits();
        traits.putId(userId);
        identify(traits, option);
    }

    public void alias(String event) {
        alias(event, null);
    }

    public void alias(String event, MarsOption option) {
        // TODO:  yet to be decided
    }

    public void group(String groupId) {
        group(groupId, null);
    }

    public void group(String groupId, MarsTraits traits) {
        group(groupId, traits, null);
    }

    public void group(String groupId, MarsTraits traits, MarsOption option) {
        // TODO:  yet to be decided
    }

    /**
     * Set your MarsClient instance
     *
     * <b>Segment compatible API</b>
     *
     * @param _instance MarsClient instance
     */
    public static void setSingletonInstance(@NonNull MarsClient _instance) {
        instance = _instance;
    }

    /**
     * Get the auto-populated MarsContext back
     *
     * @return cached MarsContext object
     */
    public MarsContext getMarsContext() {
        return MarsElementCache.getCachedContext();
    }


//    public EventRepository getSnapShot() {
//        return repository;
//    }

    /**
     * Reset SDK
     */
    public void reset() {
        MarsElementCache.reset();
        if (repository != null) repository.reset();
    }

    /**
     * Register Native SDK callback for custom implementation
     *
     * @param key      Native SDK key like Google Analytics, Amplitude, Adjust
     * @param callback MarsClient.Callback object
     */
    public void onIntegrationReady(String key, MarsClient.Callback callback) {
        if (repository != null) repository.onIntegrationReady(key, callback);
    }

    public void optOut() {
        if (repository != null) repository.optOut();
    }

    /**
     * MarsClient.Callback for getting callback when native SDK integration is ready
     */
    public interface Callback {
        void onReady(Object instance);
    }

    public void shutdown() {
        if (repository != null) repository.shutdown();
    }

    /*
     * MarsClient.Builder for building MarsClient with context, writeKey, endPointUrl
     * */
    public static class Builder {
        private Application application;
        private String writeKey;
        private String maraxId;

        /**
         * @param context Your Application context
         */
        public Builder(Context context) {
            this(context, null);
        }

        /**
         * @param context  Your Application context
         * @param writeKey Your Android WriteKey from Mars dashboard (https://app.marax.ai)
         */
        public Builder(Context context, String writeKey) {
            if (context == null) {
                MarsLogger.logError("context can not be null");
                return;
            }

            if (TextUtils.isEmpty(writeKey)) {
                MarsLogger.logDebug("WriteKey is not specified in constructor. looking for string file");
                writeKey = Utils.getWriteKeyFromStrings(context);
            }

            if (TextUtils.isEmpty(writeKey)) {
                MarsLogger.logError("WriteKey can not be null or empty");
                return;
            }
            this.application = (Application) context.getApplicationContext();
            this.writeKey = writeKey;

        }

        private boolean trackLifecycleEvents = false;

        /**
         * @return get your builder back
         */
        public MarsClient.Builder trackApplicationLifecycleEvents() {
            this.trackLifecycleEvents = true;
            return this;
        }

        private boolean recordScreenViews = false;

        /**
         * @return get your builder back
         */
        public MarsClient.Builder recordScreenViews() {
            this.recordScreenViews = true;
            return this;
        }

        private MarsConfig config;

        /**
         * @param config instance of your MarsConfig
         * @return get your builder back
         */
        public MarsClient.Builder withMarsConfig(MarsConfig config) {
            this.config = config;
            return this;
        }

        /**
         * @param builder instance of your MarsConfig.Builder
         * @return get your builder back
         */
        public MarsClient.Builder withMarsConfigBuilder(MarsConfig.Builder builder) {
            this.config = builder.build();
            return this;
        }

        private int logLevel;

        /**
         * @param logLevel set how much log SDK should generate
         *                 Permitted values :
         *                 VERBOSE = 5;
         *                 DEBUG = 4;
         *                 INFO = 3;
         *                 WARN = 2;
         *                 ERROR = 1;
         *                 NONE = 0;
         * @return get your builder back
         */
        public MarsClient.Builder logLevel(int logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        /**
         * @return build your MarsClient to be used
         */
        public MarsClient build() {
            return getInstance(this.application, this.writeKey, this.config);
        }
    }

}
