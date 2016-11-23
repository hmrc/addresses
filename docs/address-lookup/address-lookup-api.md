Address Lookup Microservice API
===============================

The address lookup microservice is one of HMRC's proofing and validation services that assist with cleansing
user input data. This can greatly reduce cost downstream that might otherwise arise from data inconsistencies
and ambiguities.

There are currently two API versions:

 * [v1](v1/address-lookup-api.md) - v1 is now deprecated
 * [v2](v2/address-lookup-api.md) - v2 has a more uniform response document

All new development should use the latest version available.

To constrain costs, old versions will be rotated off once they have become obsolete. At least two versions
will be maintained (the latest and the previous one), but there is no promise to maintain anything older.
