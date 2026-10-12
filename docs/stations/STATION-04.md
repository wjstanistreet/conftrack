# Station 04 — The endpoint 500s, and writes need a login

**Block:** C, "make the API behave" · **Ticket:** `BACKLOG.md` 04, "The closing session must
load, and take its first rating" · **Fixed on:** `solution/station-04`

## What was wrong

Feedback loaded for every session except one: session 41, "Closing remarks", returned 500. Once
that was fixed, posting a rating was refused with 401.

## Why it happened

Session 41 is on the programme, but nobody has rated it yet. `feedbackFor` worked out the average
rating with `.getAsDouble()`, which throws when there is nothing to average.

The 401 was not a bug. Writing to the API needs a login, and the second half of the station was
about handling that properly.

## Why it mattered

- **The edge case hid in plain sight.** It worked for 40 of 41 sessions. The one that failed was
  the newest, so the first person to find it would have been a real user.
- **The bug hides itself.** As soon as one rating arrives, the list is no longer empty and the 500
  goes away. That is how this kind of bug gets through testing.
- **Secrets need a home.** The QA password is needed to test the API, and must not be committed.

## How we found it

**The Endpoints window and the HTTP Client.**

1. The **Endpoints** window lists every route the application answers, eight of them, without
   opening a controller.
2. On `GET /api/sessions/{id}/feedback`, **Alt+Enter** generated a request into an `.http` file,
   inside the project and in version control.
3. We ran it and changed the id: session 1 gave 200, session 41 gave 500. Trying ids is exactly
   what a `.http` file is good at.
4. After the fix, the rating request gave 401, and its response said where a token comes from.
5. In "Log in as QA", `{{qaPassword}}` was flagged as unresolved. **Alt+Enter** added it to the
   **private** environment file, which git ignores. Shared settings go in one file, secrets in the
   other.
6. A response handler under the login saved the token for every other request:

   ```
   > {%
       client.global.set("token", response.body.token);
   %}
   ```

7. With `Authorization: Bearer {{token}}` under the `POST`, the rating went through: 201.
   `{{$random.email}}` gives a different attendee each time.
8. **Services → HTTP Client** keeps every call as history, with its response.

## The fix

In `FeedbackService.feedbackFor`:

```java
double average = given.stream()
        .mapToInt(Feedback::getRating)
        .average()
        .orElse(0.0);
```

`.orElse(0.0)` means "use 0 when there are no ratings to average". Then the token handler and the
`Authorization` header in `requests/feedback.http`, and the password in
`requests/http-client.private.env.json`.

## How we knew it worked

All three ticket 04 tests in `requests/feedback.http` went green. Session 41 loads, and shows its
first rating.

## Without IntelliJ IDEA Ultimate

A separate tool such as Postman or curl, requests written by hand, and the token copied out of
one response and pasted into the next. No link between a request and the code that serves it.

## Worth noticing

- `averageOf()`, just below `feedbackFor` in the same file, already works out an average safely.
  **Find Usages** on it would have shown that one method was not using it.
- Tokens live in the application's memory. Restart it and the `POST` gives 401 again until you
  re-run the login.
- The private environment file is not on any branch, by design. Everyone creates their own.
