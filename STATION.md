# Station 06 — The dashboard takes seconds

**Block:** D, "make it fast and safe" · **Ticket:** `BACKLOG.md` 06, "The dashboard is too slow"
· **Fixed on:** `solution/station-06`

## What was wrong

The dashboard showed the right numbers, slowly: about 5,500 ms for 41 rows, according to the
timing under the **Reload** button. Postgres and the in-memory database gave much the same number.

Most people's first guess was the database. It was not.

## Why it happened

`summarise()` leaves out feedback that a moderator rejected. It does that through `countable()`:

- it goes through about **50,000** feedback rows;
- for each one, it reads the whole list of about **7,100** moderation decisions, looking for a
  rejection;
- 50,000 × 7,100 is about **350 million checks**, on every page load.

Picture a bouncer with 50,000 guests in the queue, who reads the whole 7,100-name banned list top
to bottom for every guest. The fix is a list you can look a name up in instantly.

## Why it mattered

1. **It grows faster than the data.** Feedback and moderation decisions both grow every week.
   Double both, and the page is about four times slower.
2. **It costs everyone.** Each load keeps a CPU core busy for seconds. A room full of people
   opening the dashboard before a meeting would slow the whole server down.
3. **Review and tests do not catch it.** Every line looks fine. Nothing in the data is rejected,
   so the numbers are right. Tests with 20 rows run in milliseconds.

## How we found it

**The IntelliJ Profiler.**

1. We noted the timing under **Reload** first, so we would have a before and after.
2. We attached the profiler to the running application, with nothing to install or configure.
3. We reloaded the dashboard two or three times, then stopped the recording.
4. In the **flame graph**, width is time, and each bar calls the bars above it. Following the
   widest path: the request thread → `DashboardController.summary` →
   `FeedbackService.summarise` → **`FeedbackService.countable`**, almost the whole width. There
   was barely any database time in the picture.
5. **Focus on method** on `countable` showed nearly all of it in one lambda, the `anyMatch` that
   scans the decisions. From there, the profiler jumps straight to the line.

No single line is slow. Each check takes nanoseconds, and the cost is the loop around it. A
profiler can see that; reading the code cannot.

## The fix

In `countable()`, collect the rejected ids into a `Set` once, before the loop:

```java
Set<Long> rejected = decisions.stream()
        .filter(decision -> REJECTED.equals(decision.getStatus()))
        .map(Moderation::getFeedbackId)
        .collect(Collectors.toSet());
```

then, inside the loop, `if (!rejected.contains(item.getId()))`. One lookup per row instead of
one scan per row.

## How we knew it worked

The timing dropped from about 5,500 ms to about 200 ms, with identical numbers on the page.
Profiling again, `countable` has all but disappeared from the flame graph.

## Without IntelliJ IDEA Ultimate

Add timing logs, guess, redeploy, guess again. Or set up an external profiler.

## Worth knowing

- The same bug exists in every language: `list.includes()` inside a loop in JavaScript, or
  `x in some_list` inside a loop in Python. So does the fix: a set or a map.
- The set fixes the hot loop, but the page still loads every feedback row just to count it. A
  production version would ask the database for the counts and averages with a query, instead of
  loading every row.
