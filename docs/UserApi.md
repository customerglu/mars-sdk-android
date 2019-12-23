# UserApi

All URIs are relative to *https://sdk-test.marax.ai/sdk/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**registerDeviceToken**](UserApi.md#registerDeviceToken) | **POST** /user/firebase/android | Register a new device
[**updateDeviceToken**](UserApi.md#updateDeviceToken) | **PUT** /user/firebase/android | Update the firebase token for a device

<a name="registerDeviceToken"></a>
# **registerDeviceToken**
> InlineResponse2002 registerDeviceToken(body)

Register a new device

New devices are registered 

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.UserApi;


UserApi apiInstance = new UserApi();
Body2 body = new Body2(); // Body2 | 
try {
    InlineResponse2002 result = apiInstance.registerDeviceToken(body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UserApi#registerDeviceToken");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**Body2**](Body2.md)|  |

### Return type

[**InlineResponse2002**](InlineResponse2002.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="updateDeviceToken"></a>
# **updateDeviceToken**
> InlineResponse2001 updateDeviceToken(body)

Update the firebase token for a device

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.UserApi;


UserApi apiInstance = new UserApi();
Body1 body = new Body1(); // Body1 | 
try {
    InlineResponse2001 result = apiInstance.updateDeviceToken(body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UserApi#updateDeviceToken");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**Body1**](Body1.md)|  |

### Return type

[**InlineResponse2001**](InlineResponse2001.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

