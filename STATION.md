# Station 03 — The schema doesn't match

**Block:** B, "make it reach the database" · **Ticket:** `BACKLOG.md` 03, "Show the feedback for
a session" · **Fixed on:** `solution/station-03` (also `solution/block-b`)

## What was wrong

The application started, then every request that touched the database returned 500. The log said
`ERROR: column f1_0.email does not exist`.

## Why it happened

The database migration created the column `attendee_email`. The `Feedback` class, which maps that
table, asked for a column called `email`.

The application is set up not to check its mapping against the database at startup
(`ddl-auto: none`), so the mismatch only showed up the first time a query ran.

## Why it mattered

The code and the database disagreed, and nothing checked one against the other. It compiled, it
started, and it failed on first use. Anyone who has worked with a database has met this bug in
some form, in any language.

## How we found it

**Database tools, and schema-aware inspections in the code.**

1. In the **Database** tool window, we connected to the Postgres the application uses and
   browsed `public → feedback`. The column is `attendee_email`. Browsing a schema is free in
   IntelliJ IDEA now.
2. What the subscription adds: a SQL console (`select * from feedback limit 10;`, run with
   **Ctrl+Enter**), editing data in the grid, and exporting results.
3. With the data source attached to the project, the string `"email"` inside `@Column` in
   `Feedback.java` was underlined. The IDE checks a string in the code against the live schema,
   and **Alt+Enter** offers the real column name.

The same checks work inside `@Query` and in `.sql` files.

## The fix

In `Feedback.java`:

```java
@Column(name = "attendee_email", nullable = false)
```

## How we knew it worked

In `requests/feedback.http`, the test on "Feedback for a well attended session" went green, and
the dashboard stopped showing an error.

## Without IntelliJ IDEA Ultimate

A separate database client to see the schema, and your own eyes to compare it with the code.
Nothing tells you a string in the code is wrong until the query fails.

## Worth remembering

Look at the data first, then the code. Finding the truth from the database side works whatever
language the application is written in.
