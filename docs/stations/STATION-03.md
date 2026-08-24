# Station 03 — The schema doesn't match

**Block:** B, "make it reach the database" · **Time:** ~20 minutes ·
**Fixed on:** `solution/station-03` (also `solution/block-b`)

## The symptom

**Ticket:** `BACKLOG.md` 03, "Show the feedback for a session".

The application starts fine. Then every endpoint that touches the database returns 500, with
`ERROR: column f1_0.email does not exist` somewhere in the log.

## The bug

The Flyway migration creates the column `attendee_email`. The `Feedback` entity maps it as
`@Column(name = "email")`.

## The fix

In `Feedback`, `@Column(name = "email")` → `@Column(name = "attendee_email")`. One string.

## The tool, and how to drive it

**Database tools: connection, schema browsing, SQL console, data editor, and schema-aware
inspections in Java.** This is the sharpest free-versus-paid contrast in the whole session, so
run the two-column split slowly and deliberately.

1. **First, show what is free.** Open the Database tool window, connect to the Postgres running
   in Docker, expand `public` → `feedback`, and browse the columns. Say plainly: *"All of this is
   free now. This is not the pitch."*
2. **Now try to do something with it.** Open a query console and run
   `select * from feedback limit 10;` with **⌃⏎**. Open the table and edit a cell in the data
   grid. Export the result set. None of that is available without the subscription. That is a
   five-second demo that lands harder than any slide.
3. **Then the part people do not expect.** With the data source attached to the project, go back
   to `Feedback.java`. The string `"email"` inside `@Column` is underlined — IntelliJ is
   checking a Java string literal against a live database schema. ⌥⏎ offers the real column name.
4. Same trick inside `@Query` JPQL and inside `.sql` files.

## How to run the room

- The QA engineers and the JS developers know exactly what a column-name mismatch is. Say that
  out loud: this station needs no Java at all.
- Have the driver find the truth in the database first (`select * from feedback`), *then* look at
  the Java. Finding it from the data side is the more transferable habit.
- Expect stragglers here. This is why the break sits at 1:20, right after this block — volunteers
  spend it moving people to `solution/block-b`.

## Proof it worked

In `requests/feedback.http`, the test on "Feedback for a well attended session" goes green:
`GET {{baseUrl}}/api/sessions/1/feedback` returns 200 with a list of ratings. The dashboard
page stops showing an error row.

## Facilitator notes

- **A deliberate deviation from the plan.** `spring.jpa.hibernate.ddl-auto` is `none`, not
  `validate`. With `validate`, Hibernate's schema check can fire *before* the bean wiring error
  of Station 1, which would spoil the running order. As `none`, the mismatch surfaces on the
  first query instead — which also fits Block B's title better. If you would rather have the
  startup failure, set it to `validate` and re-test the order of Stations 1 to 3.
- On the `h2` profile the same bug reproduces identically; the error text differs slightly.
- Have the Postgres data source already configured on the backup laptop. Setting up a connection
  live in front of thirty people is dead air.
