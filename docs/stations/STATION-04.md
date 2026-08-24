# Station 04 — The endpoint 500s, and writes need a login

**Block:** C, "make the API behave" · **Time:** ~16 minutes · **Fixed on:** `solution/station-04`

## The symptom

**Ticket:** `BACKLOG.md` 04, "The closing session must load, and take its first rating".

`GET /api/sessions/{id}/feedback` works for most sessions and returns 500 for one particular id.
Once that is fixed, posting a rating returns 401.

## The bug

Session 41, "Closing remarks", is on the programme but has no feedback yet. `feedbackFor` computes
the average with `.getAsDouble()` on an empty `OptionalDouble`, which throws.

The 401 is not a bug. Writes to the API need a Bearer token from `POST /api/auth/login`, and the
second half of the station is meeting that requirement with the HTTP Client.

## The fix

`.getAsDouble()` → `.orElse(0.0)`. One token.

Then, in `requests/feedback.http`:

- a response handler on "Log in as QA" that saves the token:
  `client.global.set("token", response.body.token);`
- `Authorization: Bearer {{token}}` on every POST;
- `qaPassword` in `requests/http-client.private.env.json`, which git ignores. That file is not on
  this branch, by design: everyone creates their own.

## The tool, and how to drive it

**Endpoints window + HTTP Client.** This is QA's headline feature. Say so before you start.

1. Open the **Endpoints** tool window. Every route this application answers is listed — seven of
   them — without opening a single controller. Filter, and click through to the code.
2. Select `GET /api/sessions/{id}/feedback`, press **⌥⏎** and generate a request. It lands in an
   `.http` file, in the project, in version control.
3. Run it in place with the green arrow. Change the id and run again. Session 1 → 200. Session
   41 → 500. Fix it, rerun: 200 and an average of 0.
4. Run **"First rating for the closing session"** in `requests/feedback.http`: 401. Read the
   response body: it says where a token comes from.
5. Open **"Log in as QA"**. `{{qaPassword}}` is flagged as unresolved. Press **⌥⏎** on it and add
   it to the **private** environment file, with the password from the ticket. Open both env
   files side by side: shared settings in one, secrets in the other, and `.gitignore` keeps the
   second out of git. Run the login: 200, with a token in the body.
6. Under the login request, write the response handler with completion doing the typing:
   ```
   > {%
       client.global.set("token", response.body.token);
   %}
   ```
   Run the login again. The token is now a variable every request in the project can use.
7. Add `Authorization: Bearer {{token}}` under the rating request's `POST` line and run it: 201.
   Run it twice and point at `{{$random.email}}`: a different attendee each time.
8. Re-run the session 41 request: the count and the average have moved. Open **Services → HTTP
   Client** to show every call so far, with its response, kept as history.

**Without the subscription:** switch to Postman or curl, hand-write the request, copy the token
out of one response and paste it into the next, and lose every connection between the request and
the code that serves it.

## How to run the room

- **Give this station to QA.** Announce it as theirs at the start of the day and again here.
  Ideally a QA engineer drives it on the projector.
- The interesting question is not "why does it throw" — it is *"how would you have found which id
  breaks?"* The answer is: by trying ids, which is exactly what a `.http` file is good at.
- Ask why session 41 has no feedback. Somebody will say "because it hasn't happened yet". That is
  a requirements conversation, and it is the right one.
- Fix first, rate second. Rating session 41 before the fix also makes the 500 go away: the bug
  hides itself the moment real data arrives. The token in the way is what stops that happening by
  accident.
- Restart the application and the rating request is 401 again, though nothing in the file
  changed. Ask why. Tokens live in memory, so a restart forgets them: re-run the login.
- Ask QA how they share tokens and passwords in Postman today. The private env file is the answer
  to "how do I keep this in the repo without committing the secret".

## Proof it worked

All three ticket 04 tests in `requests/feedback.http` go green: both on "Feedback for the last
slot of the day", and "the first rating is accepted" on "First rating for the closing session".
Session 41 now shows its rating instead of 0. Same file, same click, different colour.

## Facilitator notes

- `feedbackFor` computes its average inline while `averageOf` right below it does the same thing
  safely. That inconsistency is deliberate and worth ten seconds: this is what real bugs look
  like, and Find Usages on `averageOf` is the thing that would have caught it.
- The `.http` files are checked in on `main`, so nobody has to generate one to keep up. The
  private env file is not, so somebody who jumps to `solution/station-04` still has to create it.
- The session 41 check allows for ratings: it expects an average of 0 only while nobody has rated
  it. On Postgres the ratings persist between runs, so the check stays green for the rest of the
  day.
- Writes are guarded by a small `BearerTokenFilter`, not Spring Security, so there is no login
  page and no CSRF to explain. Reads stay open, so the dashboard and the actuator are unaffected.
