# Souces :
[Android Developers](https://developer.android.com/about/versions/10/privacy/changes)
[geeksforgeeks](https://www.geeksforgeeks.org/how-to-obtain-the-connection-information-programmatically-in-android/)

## Restriction on IMEI and serial Number

Starting in Android 10, apps must have the READ_PRIVILEGED_PHONE_STATE privileged permission in order to access the device's non-resettable identifiers, which include both IMEI and serial number.

Third-party apps installed from the Google Play Store cannot declare privileged permissions.

If your app doesn't have the permission and you try asking for information about non-resettable identifiers anyway, the platform's response varies based on target SDK version:
  - If your app targets Android 10 or higher, a SecurityException occurs.
  - If your app targets Android 9 (API level 28) or lower, the method returns null or placeholder data if the app has the READ_PHONE_STATE permission. Otherwise, a SecurityException occurs.

You have to used another unique identifier for this like Android ID : It unique 64 bit hex no for device
ANDROID_ID is unique to each combination of app-signing key, user, and device. Values of ANDROID_ID are scoped by signing key and user. The value may change if a factory reset is performed on the device or if an APK signing key changes.

## Restriction on enabling and disabling Wi-Fi
Apps targeting Android 10 or higher cannot enable or disable Wi-Fi. The WifiManager.setWifiEnabled() method always returns false.

If you need to prompt users to enable and disable Wi-Fi, use a settings panel.
