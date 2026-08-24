# Station 07 — The page says "undefined"

**Block:** E, "fix the UI, together" · **Time:** ~12 minutes ·
**Fixed on:** `solution/station-07` (also `solution/block-e` and `solution`)

## The symptom

The dashboard loads, the rows are right, and the "Average rating" column says `undefined` for
every session. The API response is correct.

## The bug

`dashboard.ts` declares and reads `avgRating`. The `SessionSummary` DTO serialises
`averageRating`.

## The fix

In `dashboard.ts`, rename `avgRating` to `averageRating` in the interface and at the one place it
is read, then rebuild the frontend (see below).

## The tool, and how to drive it

**Full JavaScript/TypeScript/HTML/CSS support — effectively WebStorm inside the IDE you already
have open — driven through Code With Me by a remote attendee.**

1. **Hand the session to someone online.** Start Code With Me, send the link in the chat, give
   them control, and let them drive on the shared screen. This is the single best thing you can
   do for the remote half of a hybrid audience, and it demos a subscription feature while you do
   it.
2. In `SessionSummary.java`, put the caret on `averageRating` and press **⇧F6** (Rename). IDEA
   searches the TypeScript as well as the Java. Show the preview pane listing usages across both
   languages.
3. In `dashboard.ts`, show what the free tier does not have: completion on `response.json()`
   results, ⌘-click navigation, the TypeScript service flagging errors as you type, and
   breakpoints in the browser code that stop *inside the IDE*.
4. Optional and very effective: debug the frontend. Put a breakpoint in `render()`, reload the
   page, and inspect `row` — `averageRating` is there, `avgRating` is not.

**Without the subscription:** the `.ts` file is a text file. No completion, no navigation, no
refactoring, no JavaScript debugger. Two windows and two mental models.

## Rebuilding the dashboard

The page loads `dashboard.js`, compiled from `dashboard.ts`. Both are committed, so nobody needs
Node installed to run the app.

- If Node is available: `npm run build` (or let IDEA's TypeScript service compile on save).
- If it is not: the corrected `dashboard.js` is committed on this branch, so checking out
  `solution/station-07` gets you a working page either way.

**Read this before the day:** this is the one place where the plan and reality do not quite meet.
A browser cannot run `.ts`, so a real TypeScript station needs either a compile step or a
committed build output. Committed output is the lower-risk choice for a workshop and it is what
this repository does — but it does mean an attendee who edits the `.ts` without Node will not see
the page change until they check out the branch. Decide which trade-off you want and say it out
loud when you get here.

## How to run the room

- **Announce that this block belongs to the JS developers** at the start of the day. People stay
  engaged through the parts that are not theirs when they know their turn is coming.
- Pick your remote driver in advance and warn them. Volunteering someone cold on a hybrid call
  produces silence.
- Have a backup: if Code With Me will not connect, drive it yourself and say what you were going
  to demonstrate. Do not spend eight minutes debugging a screen-share.

## Proof it worked

Refresh the page. A number where `undefined` used to be. It is the most satisfying proof in the
session — end on it.
