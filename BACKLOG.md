# ConfTrack backlog

These are this week's requests from the product owner. Work them **top to bottom**: 08 comes before
07 on purpose. Each ticket number is the station number in the workshop.

Every ticket says what the business wants, what actually happens, how you will know it is done,
and where to start looking. None of them says what the bug is. Finding that is the job, and each
station has an IDE tool that finds it quickly.

**Errors that are not your ticket.** All of these bugs are in the code from the start, so you will
sometimes see an error that belongs to a later ticket. The obvious one: once the application
starts, the log fills with `column f1_0.email does not exist`. That is ticket 03. Until you get
there, leave it alone.

The checks live in `requests/`. Open the file, click the green arrow beside a request, and read
the **Tests** tab of the result. A failing test says what the application did and what the ticket
expected.

---

## 01 — Ship the weighted scorer

**Request:** "Finance signed off the weighted scoring model. Make sure the feedback service uses
the weighted scorer."

**What happens:** The application does not start. `./gradlew bootRun` stops with an error during
startup.

**Done when:** The application starts, and <http://localhost:8080> serves the dashboard page. The
page may still say `Request failed: 500`. That is ticket 03.

**Start here:** The error in the Run window. Then open the class it names and see what the IDE
already knows about it.

---

## 02 — Make the new thresholds take effect

**Request:** "Feedback rated 2 or lower should count as unhappy, not just 1s. The weekly report
should cover the last 7 days, not 30. Nobody has configured either yet: please add both to
`application.yml`."

**What happens:** The application runs on its defaults: a minimum rating of 1 and a 30 day
window. The `conftrack.feedback` section of `application.yml` is empty, and nobody remembers
what the keys are called.

**Done when:** In `requests/dashboard.http`, both tests on **"The settings the application is
running with"** pass. Restart the application after any config change, because config is only
read at startup.

**Start here:** Run that request first. It needs no database, so it works now. Then open
`application.yml`, put the caret under `conftrack.feedback`, and let the IDE tell you what the keys
are called.

---

## 03 — Show the feedback for a session

**Request:** "Attendees and speakers need to see the feedback left for a session."

**What happens:** Every request that touches the database returns 500.

**Done when:** In `requests/feedback.http`, the test on **"Feedback for a well attended
session"** passes.

**Start here:** The error in the log names a column. Look at what the database actually has,
then at what the code asks it for.

---

## 04 — The closing session must load, and take its first rating

**Request:** "Closing remarks went on the programme this morning. Nobody has rated it yet, but its
feedback page must still load, showing a rating of 0. Then post its first rating, so we know it
takes one. Writes need the QA login: the password is `conftrack-qa`, and it must not be
committed."

**What happens:** Feedback for every session loads except one. Posting a rating is refused with
a 401.

**Done when:** In `requests/feedback.http`, both tests on **"Feedback for the last slot of the
day"** pass, and so does the test on **"First rating for the closing session"**.

**Start here:** The Endpoints window. Find the feedback endpoint and call it for session 41. For
the rating, start at **"Log in as QA"** and see which variable the IDE cannot resolve.

---

## 05 — Unhappy ratings must count

**Request:** "Some sessions that everyone hated have a score of 0, as if nobody had rated them.
Unhappy ratings must count in the score, and count extra. That is what the weighted model is
for."

**What happens:** Sessions rated only 1s and 2s score `0.0`, although their average rating is
not 0. The weighted scorer's code looks correct.

**Done when:** In `requests/dashboard.http`, both tests on **"Scores count unhappy ratings"**
pass.

**Start here:** Run that request. If the weighted scorer's code is right, find out which scorer
is actually running.

---

## 06 — The dashboard is too slow

**Request:** "The dashboard is too slow to put on screen in the Monday meeting. It needs to load
in under a second."

**What happens:** The page loads with the right numbers, slowly. The timing under the
**Reload** button says several seconds.

**Done when:** The timing under **Reload** is well under a second, on either database. Note
the number before you change anything.

**Start here:** The dashboard page. Then measure where the time goes rather than guess.

---

## 08 — Ship the security fix

**Request:** "Security flagged CVE-2022-42889, 'Text4Shell', across the company. Confirm we are
not shipping it, and fix it if we are."

**What happens:** Nothing. The build is green and every check passes.

**Done when:** No dependency in `build.gradle` has a known vulnerability, and the IDE shows no
warning against any of them.

**Start here:** `build.gradle`.

---

## 07 — The average rating column is broken

**Request:** "The dashboard's Average rating column is broken. Fix it before the demo."

**What happens:** The column says `undefined` for every session. The other columns are right.

**Done when:** Every row in the Average rating column shows a number.

**Start here:** Compare what the API sends (`GET /api/dashboard/summary`) with what the
dashboard's TypeScript expects.
