# Souces :
[geeksforgeeks](https://www.geeksforgeeks.org/how-to-obtain-the-connection-information-programmatically-in-android/)

## NB: 
Third party apps can not use IMEI nor the serial number of a phone and other non-resettable device identifiers.

The only permissions that are able to use those is READ_PRIVILEGED_PHONE_STATE and that cannot be used by any third party apps - Manufacture and Software Applications. If you use that method you will get an error Security exception or get null .
