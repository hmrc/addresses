Address Lookup Microservice API (v2)
====================================

## What's Changed in v2

There is a minor change in the response schema. Now, country and subdivision are both represented in the same way, i.e. with a `{code,name}` object.

Nothing else has changed. The subdivision remains optional and the country remains required.

The new behaviour is only reached via the new URLs, which contain `/v2/` and are described below.

Over time, clients are expected to adopt v2. Support for v1 is likely to be dropped eventually.

## 1. UK Address JSON Object

To avoid later repetition, the common format of each UK address returned in responses from the
service is described first. The actual REST responses *contain* these documents in arrays, and
are described elsewhere.

Please note that the string `maxLength` values in the schema are preliminary and
**must not** be used for designing database schemas etc.

See [the UK address schema (v2)](uk-address-object.json) and address [example 1](uk-address-object-sample1.json).
Suitable Scala case classes are available in
[address-reputation-store](https://github.com/hmrc/address-reputation-store/tree/master/src/main/scala/uk/gov/hmrc/address/v2).

## 2. General Error Conditions

Status codes as per [RFC-7231 section 6](https://tools.ietf.org/html/rfc7231#section-6):

 - *400-Bad request*: when input parameters are not valid, which is when
    - a required header is absent,
    - a required parameter is absent,
    - a parameter is sent with an incorrect value,
    - an unexpected parameter is sent.
 - *404-Not found*: when an unknown URI is accessed.
 - *405-Bad method*: when an unacceptable method is used.
 - *500-Internal server error*: if a fault happened in the address lookup application
 - *500-Internal server error*: if the address lookup application could not access its database

No upstream services are consumed, so 502 will never be returned.

In each error case, the body will be a short message intended to assist the developer (not to
be shown to end users), with `Content-Type: text/plain`.


## 3. UK Address Lookup By ID

This endpoint returns an address by its ID. It will support queries for the same address at different stages
of its lifecycle, although this is not yet implemented.

URL:

 - `{contextPath}/v2/uk/addresses/nnnnnnnnnn`
 - `{contextPath}/v2/gb/addresses/nnnnnnnnnn` (alias for `uk`)

Example URLs:

 - `{contextPath}/v2/uk/addresses/GB1234512345`

Methods:

 - `GET`

Headers:

 - `User-Agent` (required): *string*

   This identifies the origin of the request so that usage patterns can be tracked. The value will be a short
   string containing some code-name of the originating service, e.g. `yta`. It must not contain '/' to avoid
   problems with default User-Agent values.
   It will be used for reporting. All requests from a given origin must carry the same code-name.

 - `X-Hmrc-Origin` (alternative): *string*

   The is an alternative to `User-agent`; only one of these is required.

 - `Accept-Language` (optional): two-letter ISO639-1 case-insensitive code list

   Example:

     - `Accept-Language: cy, en`

   If no match was made, the default response will be sent, which will typically be English.

   Note that clients of this service that have user-facing UIs may pass the Accept-Language
   header sent by the user-agent through directly.

Query params:

 - (none)

Status codes:

 - *200-OK* when the lookup was successful.
 - *404-Not found* when the lookup was unsuccessful.
 - others as in section 1.

Response:

 - `Content-Type: application/json`
 - Expiry and cache control headers will be set appropriately.
 - The body will be a _JSON object_ containing *one* [UK Address Object (v2)](uk-address-object.json).
     [Example response (v2)](uk-address-object-sample1.json)


## 4. UK Address Search

Two methods are provided for searching for addresses. In both cases, a list of zero or more
addresses is returned. The response format is a _JSON array_ containing *zero or more*
[UK Address Objects (v2)](uk-address-object.json). [Example response (v2)](address-response-sample1.json)

### 4A. Lookup by UPRN

This is a simple query endpoint that searches for the address(es) of a given UPRN.

URL:

 - `{contextPath}/v2/uk/addresses`
 - `{contextPath}/v2/gb/addresses` (alias for `uk`)

Example URL:

 - `{contextPath}/v2/uk/addresses?uprn=1234512345`

Methods:

 - `GET`

Headers:

 - `User-Agent` (required): *string*

   This identifies the origin of the request so that usage patterns can be tracked. The value will be a short
   string containing some code-name of the originating service, e.g. `yta`. It must not contain '/' to avoid
   problems with default User-Agent values.
   It will be used for reporting. All requests from a given origin must carry the same code-name.

 - `X-Hmrc-Origin` (alternative): *string*

   The is an alternative to `User-agent`; only one of these is required.

 - `Accept-Language` (optional): two-letter ISO639-1 case-insensitive code list

   Example:

     - `Accept-Language: cy, en`

   If no match was made, the default response will be sent, which will typically be English.

   Note that clients of this service that have user-facing UIs may pass the Accept-Language
   header sent by the user-agent through directly.

Query params:

 - `uprn` (required).

Status codes:

 - *200-OK* when the postcode search was successful (n.b. response might be `[]`)
 - others as in section 1.

Response:

 - `Content-Type: application/json`
 - Expiry and cache control headers will be set appropriately.
 - The body will be a _JSON array_ containing *zero or more* [UK Address Objects (v2)](uk-address-object.json).
     [Example response (v2)](address-response-sample1.json)


### 4B. Lookup by Postcode

This is a simple query endpoint that searches for addresses at a given postcode.

URL:

 - `{contextPath}/v2/uk/addresses`
 - `{contextPath}/v2/gb/addresses` (alias for `uk`)

Example URL:

 - `{contextPath}/v2/uk/addresses?postcode=AA1+1ZZ&filter=The+Rectory`

Methods:

 - `GET`

Headers:

 - `User-Agent` (required): *string*

   This identifies the origin of the request so that usage patterns can be tracked. The value will be a short
   string containing some code-name of the originating service, e.g. `yta`. It must not contain '/' to avoid
   problems with default User-Agent values.
   It will be used for reporting. All requests from a given origin must carry the same code-name.

 - `X-Hmrc-Origin` (alternative): *string*

   The is an alternative to `User-agent`; only one of these is required.

 - `Accept-Language` (optional): two-letter ISO639-1 case-insensitive code list

   Example:

     - `Accept-Language: cy, en`

   If no match was made, the default response will be sent, which will typically be English.

   Note that clients of this service that have user-facing UIs may pass the Accept-Language
   header sent by the user-agent through directly.

Query params:

 - `postcode` (required) in the usual Royal Mail format, all uppercase. The internal space may be omitted.
 - `filter` (optional): a sub-string match on any of the address lines.

Status codes:

 - *200-OK* when the postcode search was successful (n.b. response might be `[]`)
 - others as in section 1.

Response:

 - `Content-Type: application/json`
 - Expiry and cache control headers will be set appropriately.
 - The body will be a _JSON array_ containing *zero or more* [UK Address Objects (v2)](uk-address-object.json).
     [Example response (v2)](address-response-sample1.json)


## 5. BFPO Address Lookup By Postcode or BFPO Number

URL:

 - `{contextPath}/bfpo/addresses`

Example URLs:

 - `{contextPath}/bfpo/addresses?postcode=BF1+0AX&filter=2014`
 - `{contextPath}/bfpo/addresses?bfpo=105&filter=2014`

Methods:

 - `GET`

Headers:

 - `User-Agent` (required): *string*

   This identifies the origin of the request so that usage patterns can be tracked. The value will be a short
   string containing some code-name of the originating service, e.g. `yta`. It must not contain '/' to avoid
   problems with default User-Agent values.
   It will be used for reporting. All requests from a given origin must carry the same code-name.

Query params:

 - `postcode` (required): the value must match the regex shown in the address schema above.
 - `bfpo` (alternative to postcode): a BFPO number; these normally don't contain non-numeric characters, but there
   are some unusual cases that do.
 - `filter` (optional): a sub-string match on any of the address lines.

Status codes:

 - *200-OK* when the BFPO search was successful (n.b. response might be `[]`)
 - others as in section 1.

Response:

 - `Content-Type: application/json`
 - Expiry and cache control headers will be set appropriately.
 - The body will be a JSON array containing *zero or more* BFPO Address Response Objects.
   [Example response](bfpo-response-sample1.json).

The response format, in [Orderly](http://orderly-json.org/), is:

```
object {
	string operation?;
	array [ string ] lines;
	string postcode;
	string bfpoNo;
}
```

and the equivalent Scala representation might be

```
case class BFPO(operation: Option[String], lines: List[String], postcode: String, bfpoNo: String)
```

## 6. Liveness Test

URL:

 - `{contextPath}/ping`

Methods:

 - `GET`
 - `HEAD`

Headers:

 - (none)

Query params:

 - (none)

Status codes:

 - *200-OK*

Response:

 - `Content-Type: text/plain`
 - Expiry will be immediate and caching will be disabled.
 - The body will be a short text string that will describe the provenance of the server
   build including the Git version number etc.


## 7. Other Information

* Accept-Language header used here is as per [RFC-3066](https://tools.ietf.org/html/rfc3066)
   and [RFC-7231 5.3.5](https://tools.ietf.org/html/rfc7231#section-5.3.5).

* Query parameters must be correctly encoded: see
    [URL Encoding](https://en.wikipedia.org/wiki/Query_string#URL_encoding).

* Language codes are as per [ISO639](https://en.wikipedia.org/wiki/ISO_639)

* Country codes are as per the UK [Country Register](https://country.register.gov.uk/). This provides a list
    of two-letter country codes that is very similar to [ISO3166-1](https://en.wikipedia.org/wiki/ISO_3166-1).

* Country subdivisions are as per [ISO3166-2](https://en.wikipedia.org/wiki/ISO_3166-2).
  These are GB-ENG, GB-SCT, GB-NIR, GB-WLS.
  
Jersey, Guernsey and IoM do not have a subdivision and their country code is set to JE, GG, and IM accordingly.
