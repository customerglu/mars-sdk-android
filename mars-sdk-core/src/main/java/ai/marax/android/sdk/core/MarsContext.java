package ai.marax.android.sdk.core;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import ai.marax.android.sdk.core.util.Utils;

import java.util.Locale;
import java.util.Map;

class MarsContext {
    @SerializedName("app")
    private MarsApp app;
    @SerializedName("traits")
    private Map<String, Object> traits;
    @SerializedName("library")
    private MarsLibraryInfo libraryInfo;
    @SerializedName("os")
    private MarsOSInfo osInfo;
    @SerializedName("screen")
    private MarsScreenInfo screenInfo;
    @SerializedName("userAgent")
    private String userAgent;
    @SerializedName("locale")
    private String locale = Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry();
    @SerializedName("device")
    private MarsDeviceInfo deviceInfo;
    @SerializedName("network")
    private MarsNetwork networkInfo;
    @SerializedName("timezone")
    private String timezone = Utils.getTimeZone();
    @SerializedName("maraxId")
    private String maraxId;

    private MarsContext() {
        // stop instantiating without application instance.
        // cachedContext is used every time, once initialized
    }

    MarsContext(Application application) {
        String deviceId = Utils.getDeviceId(application);

        this.app = new MarsApp(application);

        // get saved traits from prefs. if not present create new one and save
        SharedPreferences preferences = application.getSharedPreferences(Utils.MARS_PREFS, Context.MODE_PRIVATE);

        String traitsJson = preferences.getString(Utils.MARS_TRAITS_KEY, null);

        MarsLogger.logDebug(String.format(Locale.US, "Traits from persistence storage%s", traitsJson));
        if (traitsJson == null) {
            MarsTraits traits = new MarsTraits(deviceId);
            traitsJson = new Gson().toJson(traits);
            this.traits = Utils.convertToMap(traitsJson);
            preferences.edit().putString(Utils.MARS_TRAITS_KEY, traitsJson).apply();
            MarsLogger.logDebug("New traits has been saved");
        } else {
            this.traits = Utils.convertToMap(traitsJson);
            MarsLogger.logDebug("Using old traits from persistence");
        }

//        this.maraxId = mId;
        this.screenInfo = new MarsScreenInfo(application);
        this.userAgent = System.getProperty("http.agent");
        this.deviceInfo = new MarsDeviceInfo(deviceId);
        this.networkInfo = new MarsNetwork(application);
        this.osInfo = new MarsOSInfo();
        this.libraryInfo = new MarsLibraryInfo();
    }

    void updateTraits(MarsTraits traits) {
        // if traits is null reset the traits to a new one with only anonymousId
        if (traits == null) {
            traits = new MarsTraits(this.getDeviceId());
        }

        // convert the whole traits to map and take care of the extras
        Map<String, Object> traitsMap = Utils.convertToMap(new Gson().toJson(traits));
        if (traits.getExtras() != null) traitsMap.putAll(traits.getExtras());

        // update traits object here
        this.traits = traitsMap;
    }

    void persistTraits() {
        // persist updated traits to sharedPreference
        try {
            if (MarsClient.getInstance() != null && MarsClient.getInstance().getApplication() != null) {
                SharedPreferences preferences = MarsClient.getInstance()
                        .getApplication()
                        .getSharedPreferences(Utils.MARS_PREFS, Context.MODE_PRIVATE);
                preferences.edit().putString(Utils.MARS_TRAITS_KEY, new Gson().toJson(this.traits)).apply();
            }
        } catch (NullPointerException ex) {
            MarsLogger.logError(ex);
        }
    }

    Map<String, Object> getTraits() {
        return traits;
    }

    void updateTraitsMap(Map<String, Object> traits) {
        this.traits = traits;
    }

    String getDeviceId() {
        return deviceInfo.getDeviceId();
    }
    String getMaraxId() {
        SharedPreferences preferences = MarsClient.getInstance()
                .getApplication()
                .getSharedPreferences(Utils.MARS_PREFS, Context.MODE_PRIVATE);
        String maraxId = preferences.getString("maraxId", "undefined");
        Log.i("FromContext", maraxId);
        return maraxId;
    }
}
