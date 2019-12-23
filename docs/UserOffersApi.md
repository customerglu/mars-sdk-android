# UserOffersApi

All URIs are relative to *https://sdk-test.marax.ai/sdk/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**fetchOffer**](UserOffersApi.md#fetchOffer) | **GET** /user-offer/{offerId}/{userId} | Fetch a single offer for user by offer id
[**paintOffer**](UserOffersApi.md#paintOffer) | **GET** /user-offer/template/{templateId}/{userId} | 
[**updateOffer**](UserOffersApi.md#updateOffer) | **PUT** /user-offer/{offerId}/{userId} | Update the offer details for the user

<a name="fetchOffer"></a>
# **fetchOffer**
> InlineResponse200 fetchOffer(userId, offerId)

Fetch a single offer for user by offer id

Returns a single offer for the user

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.UserOffersApi;


UserOffersApi apiInstance = new UserOffersApi();
String userId = "userId_example"; // String | will be removed after we start obtaining in headers
String offerId = "offerId_example"; // String | id for which that particular offer is needed
try {
    InlineResponse200 result = apiInstance.fetchOffer(userId, offerId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UserOffersApi#fetchOffer");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userId** | **String**| will be removed after we start obtaining in headers |
 **offerId** | **String**| id for which that particular offer is needed |

### Return type

[**InlineResponse200**](InlineResponse200.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="paintOffer"></a>
# **paintOffer**
> String paintOffer(userId, templateId)



### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.UserOffersApi;


UserOffersApi apiInstance = new UserOffersApi();
String userId = "userId_example"; // String | will be removed after we start obtaining in headers
String templateId = "templateId_example"; // String | id for which that particular template is needed
try {
    String result = apiInstance.paintOffer(userId, templateId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UserOffersApi#paintOffer");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userId** | **String**| will be removed after we start obtaining in headers |
 **templateId** | **String**| id for which that particular template is needed |

### Return type

**String**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: text/html

<a name="updateOffer"></a>
# **updateOffer**
> InlineResponse2001 updateOffer(body, userId, offerId)

Update the offer details for the user

Updates the &#x27;status&#x27; of the offer

### Example
```java
// Import classes:
//import io.swagger.client.ApiException;
//import io.swagger.client.api.UserOffersApi;


UserOffersApi apiInstance = new UserOffersApi();
Body body = new Body(); // Body | 
String userId = "userId_example"; // String | will be removed after we start obtaining in headers
String offerId = "offerId_example"; // String | id for which that particular offer is needed
try {
    InlineResponse2001 result = apiInstance.updateOffer(body, userId, offerId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling UserOffersApi#updateOffer");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**Body**](Body.md)|  |
 **userId** | **String**| will be removed after we start obtaining in headers |
 **offerId** | **String**| id for which that particular offer is needed |

### Return type

[**InlineResponse2001**](InlineResponse2001.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

