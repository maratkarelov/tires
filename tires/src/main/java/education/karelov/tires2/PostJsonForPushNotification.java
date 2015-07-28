package education.karelov.tires2;

/**
 * Created by user on 24.07.15.
 */
public class PostJsonForPushNotification {
    String deviceId;
    String deviceName;

    public PostJsonForPushNotification(String deviceId, String deviceName) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
    }
}
