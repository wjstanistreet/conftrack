# Station 02 — Nobody knows the key names

**Block:** A, "make it start" · **Ticket:** `BACKLOG.md` 02, "Make the new thresholds take
effect" · **Fixed on:** `solution/station-02` (also `solution/block-a`)

## What was wrong

The business wanted feedback rated 2 or lower to count as unhappy, and the weekly report to cover
7 days. The application was still running on its defaults: a minimum rating of 1 and a 30 day
window. The `conftrack.feedback` section of `application.yml` was empty.

Nothing crashed, and nothing in the log mentioned configuration. The only symptom was the settings
request in `requests/dashboard.http`, whose two tests failed.

## Why it happened

The settings had never been written, and the ticket gave the values but not the key names. Nobody
knew them, because they are not Spring's keys: `minimum-rating` and `recent-days` were invented by
this team, in `FeedbackProperties`.

## Why it mattered

Configuration fails silently. A missing or misspelt key is not an error: the application runs on
its defaults, and nobody notices until the numbers look wrong. Keys a team invented are not in any
documentation you can search.

## How we found it

**Spring configuration support: completion, documentation and navigation in YAML.**

1. In `application.yml`, on an empty indented line under `conftrack.feedback:`, we pressed
   **Ctrl+Space**. The IDE listed the keys of the team's own `@ConfigurationProperties` class,
   with their types and defaults.
2. We picked `minimum-rating` and `recent-days`, and gave them the ticket's values.
3. **Ctrl+click** (⌘-click on macOS) on a key jumps to the field in `FeedbackProperties` it binds
   to.
4. Typing a key wrong, such as `minimun-rating`, gets it highlighted as unknown before anything
   runs.

## The fix

Under `conftrack.feedback` in `application.yml`:

```yaml
minimum-rating: 2
recent-days: 7
```

Then restart the application, because configuration is only read at startup.

## How we knew it worked

Both tests on "The settings the application is running with" went green: `minimumRating` 2 and
`recentDays` 7.

## Without IntelliJ IDEA Ultimate

YAML is a text file. You read `FeedbackProperties`, work out the key names by hand, and find a
typo only when the behaviour turns out to be wrong.
