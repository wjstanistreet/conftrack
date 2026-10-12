# Station 08 — The page says "undefined"

**Block:** E, "fix the UI, together" · **Ticket:** `BACKLOG.md` 08, "The average rating column
is broken" · **Fixed on:** `solution/station-08` (also `solution/block-e`)

## What was wrong

The dashboard loaded, the rows were right, and the **Average rating** column said `undefined` for
every session. The API response was correct.

## Why it happened

The Java side sends the field as `averageRating` (in `SessionSummary.java`). The dashboard's
TypeScript, `dashboard.ts`, declared and read `avgRating`.

## Why it mattered

Two languages, two names, and nothing linking them. Each side looked correct on its own. There
was no error and no exception, just `undefined` on the screen, in front of whoever was presenting.

## How we found it

**Full JavaScript and TypeScript support, with Code With Me.**

1. We worked on it together through **Code With Me**, with one person driving the shared IDE.
2. In `dashboard.ts`, with the caret on `avgRating` in the `SessionSummary` interface, **Shift+F6**
   (Rename) changed the declaration and the place `render()` reads it, together.
3. Opening `SessionSummary.java` beside it showed the two names now match.
4. Along the way: completion on the API's results, **Ctrl+click** navigation, and the TypeScript
   checker flagging errors as you type.
5. A breakpoint in `render()` stops the browser's code *inside the IDE*. Inspecting `row` shows
   `averageRating` is there and `avgRating` is not.

## The fix

In `src/main/resources/static/dashboard.ts`, rename `avgRating` to `averageRating`, in the
interface and where `render()` reads it. Then rebuild the page:

```bash
npm run build
```

The page loads `dashboard.js`, which is compiled from `dashboard.ts`, so the change only shows
once it is rebuilt. Both files are committed, so the page works without Node.

## How we knew it worked

Refreshing the dashboard: a number where `undefined` used to be.

## Without IntelliJ IDEA Ultimate

The `.ts` file is a text file. No completion, no navigation, no rename across usages, and no
debugger for the browser code. Two windows, and two mental models.
