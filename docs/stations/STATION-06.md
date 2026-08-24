# Station 06 — The dashboard takes seconds

**Block:** D, "make it fast and safe" · **Time:** ~11 minutes · **Fixed on:** `solution/station-06`

## The symptom

**Ticket:** `BACKLOG.md` 06, "The dashboard is too slow".

The dashboard page loads, correctly, slowly. The timing readout under the Reload button says
about five and a half thousand milliseconds for 41 rows. The work is all CPU, so PostgreSQL and
the `h2` profile show much the same number.

## The bug

`summarise()` leaves out feedback a moderator rejected. It does that through `countable()`, a
helper in `FeedbackService` that, for every feedback row, scans the whole list of moderation
decisions looking for a rejection. About 50,000 rows times about 7,100 decisions is roughly 350
million checks per page load.

Nothing in the seed data is rejected, so the rule changes no number on the page. It only costs
time, which is exactly why a bug like this gets through review.

## The fix

In `countable()`, collect the rejected ids into a `Set` once, before the loop, and ask the set
for each row:

```java
Set<Long> rejected = decisions.stream()
        .filter(decision -> REJECTED.equals(decision.getStatus()))
        .map(Moderation::getFeedbackId)
        .collect(Collectors.toSet());
```

then `if (!rejected.contains(item.getId()))`. One lookup per row instead of one scan per row.

## The tool, and how to drive it

**The IntelliJ Profiler, attached to the running application in one click.**

1. Start the app. **Run ▸ Profile**, or attach the profiler to the running process from the Run
   widget — no agent to configure, no external tool to install.
2. Load the dashboard two or three times, then stop the recording.
3. Open the **flame graph**. The wide plateau is the hot path. Walk down it out loud: the
   controller, the service, `summarise`, and then `FeedbackService.countable`: a method this team
   wrote, far wider than anything around it. No Hibernate, no JDBC.
4. Right-click `countable` → **Focus on method**. Nearly all of it is the `anyMatch` lambda,
   called hundreds of millions of times. Double-click to jump straight to the line.
5. Apply the fix, restart, and **profile again**. Put the two flame graphs side by side.

**Without the subscription:** sprinkle timing logs, guess, redeploy, guess again — or reach for
an external profiler that nobody in the room has set up.

## How to run the room

- Show the *before* number from the dashboard page first (it is on screen, in milliseconds) so
  the improvement is a number everyone watched change.
- The flame graph is the single most visually persuasive artefact in the whole session. Give it
  screen time. Say nothing for a few seconds and let people read it.
- Ask the room which line is slow. Nobody can point at one: each check is cheap, and the cost is
  the loop around it. That is the thing the profiler sees and a code review does not.
- Nobody needs to read Java. "Scan a list for every row" versus "look it up in a set" is the same
  idea in every language in the room.
- Ask why the numbers did not change. Because nothing is rejected, so the rule only ever cost
  time. Slow and correct survives review; that is the point.

## Proof it worked

Reload the dashboard: the timing readout drops from about 5,500 ms to about 200 ms, and the
numbers on the page are identical. The re-profiled flame graph no longer has `countable` in it
to speak of. Two pictures, no code reading.

## Facilitator notes

- **Record a 90-second screen capture of this before the day.** Profiling is the most likely
  thing to misbehave live, and debugging your own demo in front of the room is the worst possible
  use of eleven minutes. If it wobbles, play the recording, explain it, move on.
- Pairs on the `h2` profile see the same story and nearly the same numbers, because the time is
  spent in Java, not in the database.
- Honest footnote worth saying aloud: the set fixes the hot loop, but the page still loads every
  feedback row to count it. The production version asks the database for the rejected ids, or
  better for the counts and averages, with a query. Someone will ask; having the answer ready
  buys you a lot of credibility with the senior people in the room.
- The seed data is deterministic, so every attendee's numbers match yours.
