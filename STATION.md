# Station 01 — It won't start

**Block:** A, "make it start" · **Ticket:** `BACKLOG.md` 01, "Ship the weighted scorer" ·
**Fixed on:** `solution/station-01`

## What was wrong

Running `ConfTrackApplication` stopped during startup. Spring Boot's report in the Run window
said:

```
APPLICATION FAILED TO START

Parameter 3 of constructor in com.example.conftrack.service.FeedbackService
required a single bean, but 2 were found
```

Spring found two things that could be injected as a `FeedbackScorer`, and would not guess
between them.

## Why it happened

`WeightedFeedbackScorer` and `SimpleFeedbackScorer` both implement `FeedbackScorer`, and both
are Spring components. `FeedbackService` asked for "a `FeedbackScorer`", and there were two.

Neither class is a mistake. `SimpleFeedbackScorer` is the original scorer, kept because two
internal reports still quote its numbers. Finance had signed off the weighted one, and nobody had
told Spring which to use.

## Why it mattered

This is one of the most common ways a Spring application fails. The report names a constructor
parameter by number and two bean names, and only after the application has already failed. If
you do not read Java, or Spring, every day, it does not tell you where to look.

## How we found it

**Spring framework support: injection-point inspections, gutter icons and the beans diagram.**

1. We opened `FeedbackService`. The scorer parameter of its constructor was already underlined,
   before anything had run. Hovering it named both candidate beans.
2. The bean icon in the gutter beside the parameter listed both candidates and jumped to either
   one. Nobody had to search for a class name.
3. The **Spring** tool window's beans diagram showed `FeedbackScorer` with two arrows landing on
   it. That picture explains the error better than the report does.
4. **Alt+Enter** (⌥⏎ on macOS) on the underlined parameter offered the fix.

## The fix

Name the bean we want, on the constructor parameter in `FeedbackService`:

```java
@Qualifier("weightedFeedbackScorer") FeedbackScorer scorer
```

## How we knew it worked

The application started.

## Without IntelliJ IDEA Ultimate

Run it, read the error report, search the codebase for `implements FeedbackScorer`, and work out
which one was meant. All of that after the application has already failed.

## What you might notice next

Once the application starts, the log fills with `column f1_0.email does not exist`, and the
dashboard says `Request failed: 500`. That is a real bug, but it is Station 03's.

Station 02 makes no noise of its own. In `requests/dashboard.http`, run **"The settings the
application is running with"**: both of its tests fail.
