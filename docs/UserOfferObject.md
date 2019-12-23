# UserOfferObject

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**status** | [**StatusEnum**](#StatusEnum) | * accepted : User accepted the offer * rejected : User did not accept the offer  |  [optional]
**userId** | **String** |  |  [optional]
**offerId** | **String** |  |  [optional]
**createdAt** | **String** |  |  [optional]
**updatedAt** | **String** |  |  [optional]
**templateUrl** | **String** | URL to fetch the offer template for that particular user for that offer |  [optional]

<a name="StatusEnum"></a>
## Enum: StatusEnum
Name | Value
---- | -----
PENDING | &quot;pending&quot;
ACCEPTED | &quot;accepted&quot;
REJECTED | &quot;rejected&quot;
