# Station 09 — The new API isn't live yet

**Block:** F, "build against a contract" · **Ticket:** `BACKLOG.md` 09, "Show how many people
each room holds" · **Fixed on:** `solution/station-09` (also `solution/block-f` and
`solution/complete`)

## What was wrong

Facilities had published the spec for their new Venue API, in `specs/venue-api.yaml`, but the API
would not be live for a month. A colleague had already written the client. `GET /api/rooms`
listed the four rooms, and every `capacity` was `null`.

## Why it happened

Two things, one hiding behind the other:

1. **There was nothing to call.** `conftrack.venue.base-url` pointed at the real API, which did
   not exist yet.
2. **The client got the contract wrong.** `VenueClient` asked for `/rooms`. The spec publishes
   `/v1/rooms`. Nobody could see that until something was answering on the other end.

## Why it mattered

An API that exists only as a document, and a deadline that does not wait for it, is one of the
most common situations in real projects. Without something to call, the path bug would have
surfaced on go-live day, in production.

## How we found it

**OpenAPI support, the WireMock plugin and the HTTP Client.**

1. We opened `specs/venue-api.yaml`. The IDE understands it: the Structure view, `$ref`
   completion, and the endpoint in the **Endpoints** window.
2. The run icon in its gutter offers **Generate WireMock Stubs**. That gave us a stub with the
   method, the URL and the status code.
3. We saved it as `wiremock/mappings/venue-api.json`, and gave it a response: the status, a
   `Content-Type: application/json` header, and the four rooms from the spec's example. The
   generator writes only the status, and without the header the application cannot read the
   body as JSON.
4. **Run WireMock**, at the top of the stub file, started a server on port 8089. It appears in the
   **Services** window. WireMock reads the stub file when it starts, so it needs a re-run after
   any edit.
5. **Alt+Enter** on the stub's URL generated a request in the HTTP Client. It answered with the
   rooms: an API that does not exist yet, already working.
6. We pointed the application at the stub and restarted it. Capacity was **still** `null`.
7. The WireMock console printed **Request was not matched**, with the closest stub,
   `GET /v1/rooms`, beside what the application sent, `GET /rooms`, and
   `<<<<< URL does not match`. The application's warning in its log said the same.

## The fix

In `application.yml`, point the application at the stub:

```yaml
conftrack:
  venue:
    base-url: http://localhost:8089
```

In `VenueClient.capacities()`, change `.uri("/rooms")` to `.uri("/v1/rooms")`.

The finished stub is in `wiremock/mappings/venue-api.json`, and `requests/venue.http` calls it
directly.

## How we knew it worked

In `requests/rooms.http`, "The rooms on the programme" went green: Room 1 holds 120, Room 2 80,
Room 3 40 and Room 4 24. Stop WireMock and run it again: the capacities go back to `null`, and the
rest of the application carries on regardless.

## Without IntelliJ IDEA Ultimate

Write the stub JSON by hand from the spec, find and download WireMock, start it from a terminal,
and write the request yourself. Or wait a month for Facilities.

## Worth knowing

When the real API goes live, the only change is `base-url`. The stub stays useful for local
development and tests.
