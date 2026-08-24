# Station 07 — The dependency is vulnerable

**Block:** D, "make it fast and safe" · **Time:** ~5 minutes ·
**Fixed on:** `solution/station-07` (also `solution/block-d`) · **CUT THIS FIRST if over time**

## The symptom

**Ticket:** `BACKLOG.md` 07, "Ship the security fix".

None. That is the entire point of the station. Nothing is failing, nothing is slow, and the
build is green.

## The bug

`build.gradle` pins `org.apache.commons:commons-text:1.9`, a version with a published CVE
(CVE-2022-42889, "Text4Shell"). `FeedbackService` uses `StringEscapeUtils` from it to escape
attendee comments.

## The fix

Bump the version to `1.10.0`.

## The tool, and how to drive it

**Package Checker.**

1. Open `build.gradle`. The vulnerable coordinate is highlighted inline, in the build file,
   right now — no scan to run, no report to wait for.
2. Hover it. The tooltip names the CVE, its severity, and the version that fixes it.
3. Change the version. The highlight disappears as you type.
4. Point out the real value: this appears **at the moment you would have added the dependency**,
   not days later in someone else's pipeline.

**Without the subscription:** you find out from a CI security scan, days later, on somebody
else's schedule, in a ticket with no context.

## How to run the room

- Five minutes, on the projector, everyone watching. It does not need to be a hands-on station,
  which is exactly why it is the first thing to cut.
- Worth one sentence for a consultancy audience: this is the kind of finding that is cheap in the
  IDE and expensive in a client's security review.

## Proof it worked

The highlight in `build.gradle` is gone and the build still passes.

## Facilitator notes

- Check before the day that this version still trips Package Checker in your pinned IDEA build.
  If JetBrains' data has moved on, swap in another known-vulnerable coordinate rather than
  standing in front of the room explaining why the highlight is missing.
- `commons-text` is genuinely used by the application, so this is not a dependency that exists
  only to be vulnerable. Keep it that way if you change the library.
