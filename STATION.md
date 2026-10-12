# Station 07 — The dependency is vulnerable

**Block:** D, "make it fast and safe" · **Ticket:** `BACKLOG.md` 07, "Ship the security fix" ·
**Fixed on:** `solution/station-07` (also `solution/block-d`)

## What was wrong

Nothing visible. The build was green, every check passed, and nothing was slow. But Security had
flagged CVE-2022-42889, "Text4Shell", across the company.

## Why it happened

`build.gradle` used `org.apache.commons:commons-text:1.9`, a version with that published
vulnerability. The application really does use it: `FeedbackService` escapes attendee comments
with `StringEscapeUtils` before they reach the dashboard.

## Why it mattered

There was no symptom, so nothing in the application would ever have told us. Usually you find out
from a security scan in CI, days later, in a ticket with no context, or in a client's security
review, where it is expensive and embarrassing.

## How we found it

**Package Checker.**

1. We opened `build.gradle`. The vulnerable dependency was already highlighted, with no scan to
   run and no report to wait for.
2. Hovering it showed the CVE, its severity, and a version that fixes it.
3. We changed the version, and the highlight disappeared as we typed.

This appears at the moment you add the dependency, not days later.

## The fix

In `build.gradle`:

```groovy
implementation 'org.apache.commons:commons-text:1.10.0'
```

## How we knew it worked

The highlight was gone, and the build still passed.

## Without IntelliJ IDEA Ultimate

You find out from somebody else's security scan, on their schedule.
