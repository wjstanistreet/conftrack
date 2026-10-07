# ConfTrack backlog

These are this week's requests from the product owner. Work them **top to bottom**. Each
ticket number is the station number in the workshop.

Every ticket says what the business wants, what actually happens, how you will know it is done,
and where to start looking. None of them says what the bug is. Finding that is the job, and each
station has an IDE tool that finds it quickly.

Every ticket also shows **the change**: which file, and the code to type, with the one thing the
IDE finds for you left as a blank, `________`. You do not need to know Java to make the change,
and you cannot make it without the tool.

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

**The change:** one annotation on the scorer parameter of the `FeedbackService` constructor.

```java
@Qualifier("________") FeedbackScorer scorer
```

The blank is the name of a bean. The gutter icon and the inspection on that parameter list the
candidates. **Alt+Enter** adds the import.

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

**The change:** two lines in `application.yml`, indented under `feedback:`.

```yaml
conftrack:
  feedback:
    ________: 2
    ________: 7
```

The blanks are the key names. **Ctrl+Space** under `feedback:` lists them, with what each one
means.

---

## 03 — Show the feedback for a session

**Request:** "Attendees and speakers need to see the feedback left for a session."

**What happens:** Every request that touches the database returns 500.

**Done when:** In `requests/feedback.http`, the test on **"Feedback for a well attended
session"** passes.

**Start here:** The error in the log names a column. Look at what the database actually has,
then at what the code asks it for.

**The change:** one value in one annotation, on the attendee's email field in `Feedback.java`.

```java
@Column(name = "________", nullable = false)
```

The blank is the column's real name. The **Database** tool window shows what the table actually
has.

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

**The change:** three small pieces.

- In `FeedbackService.java`, method `feedbackFor`: the last call on the line that works out the
  average changes.

  ```java
  double average = given.stream()
          .mapToInt(Feedback::getRating)
          .average()
          .________;
  ```

  Delete the last call, type `.` and press **Ctrl+Space**: pick the one that takes a value to
  fall back on, and give it `0.0`.
- In `requests/http-client.private.env.json`, the password, which git ignores. **Alt+Enter** on the
  red `{{qaPassword}}` creates the file for you.

  ```json
  { "local": { "qaPassword": "________" } }
  ```

  The blank is the password in the request above. On the in-memory database, the environment
  is `local-h2` rather than `local`.
- In `requests/feedback.http`: save the token from the login, then send it with every `POST`.
  Under **"Log in as QA"**:

  ```http
  > {%
      client.global.set("token", response.body.________);
  %}
  ```

  and directly under each `POST` line:

  ```http
  Authorization: Bearer {{token}}
  ```

  The blank is the name of the field the login sends back. Run the login once and read the
  response.

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

**The change:** no code to type. You will **delete** one entry, a few lines long, under
`spring:` in `application.yml`. Which one, and why, is what the Spring Debugger shows you.

---

## 06 — The dashboard is too slow

**Request:** "The dashboard is too slow to put on screen in the Monday meeting. It needs to load
in under a second."

**What happens:** The page loads with the right numbers, slowly. The timing under the
**Reload** button says several seconds.

**Done when:** The timing under **Reload** is well under a second, on either database. Note
the number before you change anything.

**Start here:** The dashboard page. Then measure where the time goes rather than guess.

**The change:** in one method of `FeedbackService.java`. Which method is what the Profiler shows
you. Before its loop, build a set:

```java
Set<Long> rejected = decisions.stream()
        .filter(decision -> ________)
        .map(________)
        .collect(Collectors.toSet());
```

then, inside the loop, replace the `boolean rejected = …` statement and the `if (!rejected)` line
under it with this one line:

```java
if (!rejected.contains(item.getId())) {
```

The first blank is the status check from the lines you are replacing. The second is the getter
that gives a decision's feedback id: type `Moderation::` and press **Ctrl+Space**. **Alt+Enter**
adds the imports.

---

## 07 — Ship the security fix

**Request:** "Security flagged CVE-2022-42889, 'Text4Shell', across the company. Confirm we are
not shipping it, and fix it if we are."

**What happens:** Nothing. The build is green and every check passes.

**Done when:** No dependency in `build.gradle` has a known vulnerability, and the IDE shows no
warning against any of them.

**Start here:** `build.gradle`.

**The change:** one version number on one line of `build.gradle`.

```groovy
implementation 'org.apache.commons:commons-text:________'
```

The blank is a version without the vulnerability. Hover the highlighted line: the IDE names one.

---

## 08 — The average rating column is broken

**Request:** "The dashboard's Average rating column is broken. Fix it before the demo."

**What happens:** The column says `undefined` for every session. The other columns are right.

**Done when:** Every row in the Average rating column shows a number.

**Start here:** Compare what the API sends (`GET /api/dashboard/summary`) with what the
dashboard's TypeScript expects.

**The change:** one name, in two places in `src/main/resources/static/dashboard.ts`: the field
in the `SessionSummary` interface, and where `render()` reads it.

```ts
    ________: number;
```

```ts
<td class="num">${ratingText(row.________)}</td>
```

The blank is the name the API actually sends. Put the caret on the old name and press
**Shift+F6** to change both at once. Then rebuild the page with `npm run build`. No Node on your
machine? Tell the facilitator, because the page only changes once it is rebuilt.
