package ai.marax.android.sdk.core;

import android.os.Build;
import com.google.gson.annotations.SerializedName;

class MarsDeviceInfo {
    @SerializedName("id")
    private String deviceId;
    @SerializedName("manufacturer")
    private String manufacturer = Build.MANUFACTURER;
    @SerializedName("model")
    private String model = Build.MODEL;
    @SerializedName("name")
    private String name = Build.DEVICE;
    @SerializedName("type")
    private String type = "android";

    MarsDeviceInfo(String deviceId) {
        this.deviceId = deviceId;
    }

    String getDeviceId() {
        return deviceId;
    }
}
