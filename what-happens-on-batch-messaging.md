

```text
What is the Operation ID
  Or in OpenTelemetry
    What is the traceparent?
How are these Single Messages
  Corellated back to Original Sender?

+---------------+                            +---------------+
|               |                            |               |                                       +-------------+
|               |                            |               |                                       |             |
|               |                            | REST ingest   |                                       |             |
| Senders       | ---------->                |     |         |                                       |             |
|               |                            |     |         |                                       |             |
|               | -------------->            |     |         |                xxxx      xxxx         |             |
|               |                            |     v         | Batch             x ---- x  ------->  |   xxxxxxxx  |
|               |                            |  xxxxxxxxx    | ----------->      x      x  Single    | xxx      xx |
|               | ----------------->         | xx       xxx  |                   xxxxxxxx  ------>   | x Event   x |
|               |                            | x Event    x  |                                       | x Hub     x |
|               |                            | x Hub      x  |                                       |  xConsumerx |
|               | -------------------->      | x Producer x  |                                       |   xxxxxxx   |
|               |                            | xxx      xxx  |                xxxx      xxxx         |             |
|               |                            |    xxxxxxx    | Batch             x ---- x  ------>   |             |
|               | ----------------------->   |               | ----------->      x      x   Single   |             |
|               |                            |               |                   xxxxxxxx   ----->   |             |
|               |         ^                  |               |                                       |             |
|               |         |                  |               |                              ----->   |             |
+---------------+         |                  +---------------+                                       +-------------+
                          |                                                      Event
                          |                                                      Hubs Partitions
                          |
                          |
                          |

                    Individual Operation Id
```


```text
What is the Operation ID
  Or in OpenTelemetry
    What is the traceparent?
How are these Single Messages
  Corellated back to Original Sender?

┌───────────────┐                            ┌───────────────┐
│               │                            │               │                                       ┌─────────────┐
│               │                            │               │                                       │             │
│               │                            │ REST ingest   │                                       │             │
│ Senders       │ ──────────►                │     │         │                                       │             │
│               │                            │     │         │                                       │             │
│               │ ──────────────►            │     │         │                xxxx      xxxx         │             │
│               │                            │     ▼         │ Batch             x ──── x  ───────►  │   xxxxxxxx  │
│               │                            │  xxxxxxxxx    │ ───────────►      x      x  Single    │ xxx      xx │
│               │ ─────────────────►         │ xx       xxx  │                   xxxxxxxx  ──────►   │ x Event   x │
│               │                            │ x Event    x  │                                       │ x Hub     x │
│               │                            │ x Hub      x  │                                       │  xConsumerx │
│               │ ────────────────────►      │ x Producer x  │                                       │   xxxxxxx   │
│               │                            │ xxx      xxx  │                xxxx      xxxx         │             │
│               │                            │    xxxxxxx    │ Batch             x ──── x  ──────►   │             │
│               │ ───────────────────────►   │               │ ───────────►      x      x   Single   │             │
│               │                            │               │                   xxxxxxxx   ─────►   │             │
│               │         ▲                  │               │                                       │             │
│               │         │                  │               │                              ─────►   │             │
└───────────────┘         │                  └───────────────┘                                       └─────────────┘
                          │                                                      Event
                          │                                                      Hubs Partitions
                          │
                          │
                          │

                    Individual Operation Id
```