# Station 02 — Nobody knows the key names

**Block:** A, "make it start" · **Time:** ~10 minutes · **Fixed on:** `solution/station-02`
(also `solution/block-a`)

## The symptom

**Ticket:** `BACKLOG.md` 02, "Make the new thresholds take effect".

Nothing crashes, and nothing in the log mentions configuration. The business wants a minimum
rating of 2 and a 7 day reporting window, and the application runs on its defaults, 1 and 30.
The `conftrack.feedback` section of `application.yml` is empty.

The only visible symptom is the one you show them. In `requests/dashboard.http`, run
**"The settings the application is running with"**. Both of its tests fail, and the messages
say what the application is running with, what the ticket wants, and where the settings go.

**What this symptom is not:** the `column f1_0.email does not exist` error that fills the log as
soon as Station 01 is fixed. That belongs to Station 03. Point them at the settings request
instead.

## The bug

There isn't one in the code. The settings were never written, and the ticket gives the values but
not the key names. Nobody in the room knows them: `minimum-rating` and `recent-days` are keys
*this team invented*, in `FeedbackProperties`.

## The fix

Under `conftrack.feedback` in `application.yml`, add:

```yaml
minimum-rating: 2
recent-days: 7
```

## The tool, and how to drive it

**Spring configuration file support: completion, documentation and navigation in YAML.**

1. Open `application.yml` with the subscription active. Put the caret on an empty line under
   `conftrack.feedback:`, indented, and press **⌃Space**. IDEA lists the keys of *your own*
   `@ConfigurationProperties` class (`minimum-rating`, `recent-days`, `low-rating-weight`) with
   their types and defaults. Not just Spring's keys.
2. Pick `minimum-rating`, type `2`. Do the same for `recent-days`, `7`.
3. ⌘/Ctrl-click `minimum-rating`: it navigates straight to the field in `FeedbackProperties`
   that it binds to. YAML is no longer text.
4. Type a key wrong on purpose, such as `minimun-rating`. IDEA highlights it as unknown, before
   anyone runs anything. Without that, a typo here is silently ignored.

**Without the subscription:** YAML is a text file. You go and read `FeedbackProperties`, work out
the relaxed-binding name by hand, and find a typo only when the behaviour turns out wrong.

## How to run the room

- Before anyone presses ⌃Space, ask the room what the key for the minimum rating is called.
  Nobody knows. Then let completion answer.
- Point out that `minimum-rating` is a key *this team invented*. The IDE learned it from the
  `@ConfigurationProperties` class. That is the moment people realise it is not a hardcoded list.
- Step 4 is the story: who has lost an afternoon to a setting that turned out never to have been
  read?

## Proof it worked

Run the same settings request again. Both tests go green: `"minimumRating": 2` and
`"recentDays": 7`. Numbers on screen, no Java required. Restart the application first, because
configuration is only read at startup.

## Facilitator notes

- If you are running behind, this is the second station to cut (after Station 7). Its lesson
  survives as a thirty-second demo on the projector.
- `spring.main.allow-bean-definition-overriding: true` in this file is deliberate and is
  load-bearing for Station 5. Do not let a keen attendee "tidy" it away yet.
