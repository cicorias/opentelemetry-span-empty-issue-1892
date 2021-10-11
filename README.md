# Overview

OpenTelemetry tracing is the future of Application Insights and Azure Monitor for custom and automatic telemetry. This solution is intended to help clear up some of the configuration options and provide a way to watch tracking and end-to-end correlation identifiers.

The [`./docs`](docs) folder has scenarios that are desired to be explained on how Application Insights and OpenTelemetry tracks cross tiers.


## Architecture
This is essentially a simple 3 tier application that probably should be the successor to [https://github.com/cicorias/space-spring-front-2-back-appinsights](https://github.com/cicorias/space-spring-front-2-back-appinsights)

## TODO:
- add kubernetes deployment in order to verify when running in azure getting Application Map Support
- add persist to Cosmos DB to extend end-to-end example
- add a ReactJS front-end -- take from the other project [Spring-front-2-back-appinsights](https://github.com/cicorias/space-spring-front-2-back-appinsights)

## Introduction

This is a Java Spring Boot application that provides:
- Web Application REST endpoint - `POST /messages`
- Event Hub Producer/Sender - bound to the Web Application
- Event Hub Consumer - pulls from same Event Hub

The solution makes uses of Spring Profiles and configuration to allow both "sides" of the Event Hub to be run in the same process, or different. 

It is combined to make deployment simple as a single image and solution. It is purely for education and diagnosing or debugging issues.

## Using Spring Gradle `bootRun`

if running from the command line load `.env` file:

```
set -o allexport; source .env; set +o allexport
./gradlew bootRun
```

## Profiles

There are two profiles defined, and with spring they can be run together -- see the `build.gradle` file and the `bootRun` section as an example; or look at the `./vscode/launch.json` settings for examples that profide the command line switch.

## VS Code Launch Configurations

- base-both
- web-only
- consumer-only
- Web and Consumer - Different processes


## Environment and setup

Create or Update a `.env` file - start with the `.env.example`

To run that in a bash session use:
```
set -o allexport; source .env; set +o allexport
```

### Example `.env` file
```
APPLICATIONINSIGHTS_CONNECTION_STRING=InstrumentationKey=<get key>
EH_CONNECTION_STRING="<get root key with listen and send>"
EH_EVENT_HUB=<create an event hub>
EH_CONSUMER_GROUP=<use $Default or create a consumer group>
STORAGE_KEY=<get storage account key for below storage account>
STORAGE_ACCOUNT=<put storage account name here>
STORAGE_CONTAINER=<put container unique to developer or env>
```

## Run with VS code -- 
Run with VS Code -- launch one of the configurations, or the **compound** launch configuration that starts two processes - the first with the web site; the second with the Event Hub consumer


## Sending Sample Data

There are two scripts in the `/scripts/` path that will use `curl` to send `POST` data.

Of the two, the `sendTracePost.sh` is more interesting and functional

This script generates a W3C traceparent formatted HTTP header and injects it into the request. Using this starting script, the intent is to be able to provide full front-2-back tracking using the generated correlation id. 

>NOTE: Currently, the traceparent that is provided is NOT propagating through Event Hubs and front-2-back correlation tracking is NOT happening...

```bash
#!/usr/bin/env bash

# set -eox

# see spec: https://www.w3.org/TR/trace-context
# version-format   = trace-id "-" parent-id "-" trace-flags
# trace-id         = 32HEXDIGLC  ; 16 bytes array identifier. All zeroes forbidden
# parent-id        = 16HEXDIGLC  ; 8 bytes array identifier. All zeroes forbidden
# trace-flags      = 2HEXDIGLC   ; 8 bit flags. Currently, only one bit is used. See below for detail

VERSION="00" # fixed in spec at 00
TRACE_ID="$(cat /dev/urandom | tr -dc 'a-f0-9' | fold -w 32 | head -n 1)"
PARENT_ID="00$(cat /dev/urandom | tr -dc 'a-f0-9' | fold -w 14 | head -n 1)"
TRACE_FLAG="01"   # sampled
TRACE_PARENT="$VERSION-$TRACE_ID-$PARENT_ID-$TRACE_FLAG"
TRACE_STATE="mystate"

MESSAGE="Lorem ipsum $(cat /dev/urandom | tr -dc 'a-zA-Z' | fold -w 20 | head -n 1)"

URL="http://localhost:8080/messages"

echo "sending to host $URL"
echo "sending a trace-parent of: $TRACE_PARENT"


curl --location --request POST "${URL}" \
--header "traceparent: $TRACE_PARENT" \
--header "Content-Type: application/json" \
--data-raw "{\"message\":\"$MESSAGE\",\"traceparent\":\"$TRACE_PARENT\" }"


```





## Notes:

- eh link [https://gist.github.com/lmolkova/e4215c0f44a49ef824983382762e6b92](https://gist.github.com/lmolkova/e4215c0f44a49ef824983382762e6b92)
- [https://opentelemetry.io/docs/java/manual_instrumentation/#context-propagation](https://opentelemetry.io/docs/java/manual_instrumentation/#context-propagation)