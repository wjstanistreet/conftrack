# Solution

Every station fixed. This is the branch you push publicly after the session, and the branch you
walk the volunteers through three days before it.

**A note on its name.** The plan called this branch `solution`, but git stores branches as paths,
so a branch called `solution` cannot coexist with `solution/block-a`. It is `solution/complete`
here. If you would rather have a bare `solution`, rename the checkpoints to `checkpoint/block-a`
and friends.

The eight station write-ups are in `docs/stations/`. Each one covers the symptom, the bug, the
fix, which tool finds it, how to drive that tool, how to run the room, and the language-neutral
proof that it worked.

| # | Block | Station | Branch |
|---|-------|---------|--------|
| 00 | -- | Open the project | `main` |
| 01 | A | It won't start | `solution/station-01` |
| 02 | A | Nobody knows the key names | `solution/station-02`, `solution/block-a` |
| 03 | B | The schema doesn't match | `solution/station-03`, `solution/block-b` |
| 04 | C | The endpoint 500s, and writes need a login | `solution/station-04` |
| 05 | C | The right code, the wrong bean | `solution/station-05`, `solution/block-c` |
| 06 | D | The dashboard takes seconds | `solution/station-06` |
| 07 | D | The dependency is vulnerable | `solution/station-07`, `solution/block-d` |
| 08 | E | The page says "undefined" | `solution/station-08`, `solution/block-e`, `solution/complete` |

## The eight shortcuts to put on the cheat sheet

| Shortcut | What it does | Station |
|---|---|---|
| Double Shift | Search Everywhere | 00 |
| Ctrl/Cmd + click gutter icon | Navigate injection points | 01 |
| Ctrl + Space | Completion in YAML and properties | 02 |
| Ctrl + Enter | Execute statement in the SQL console | 03 |
| Alt + Enter | Generate an HTTP request from a mapping; add a missing variable to the env file | 04 |
| Alt + F8 | Evaluate expression against live beans | 05 |
| Run > Profile | Attach the profiler, then re-profile | 06 |
| Shift + F6 | Rename a TypeScript field and its usages together | 08 |

## Before the day

- Re-read `docs/stations/STATION-03.md` on `ddl-auto`, `STATION-08.md` on the TypeScript build
  step, and `STATION-07.md` on the CVE still being flagged. Those three carry decisions that are
  yours, not this repository's.
- Do the dry run with somebody who does not know Java.
- First `./gradlew build` on a real machine is the only true test of this repository: it was
  written without access to a Maven repository, so pin the Spring Boot version you actually
  standardise on rather than trusting the `3.4.5` in `build.gradle`.
- If the `h2` profile fails with "Unsupported Database: H2", add
  `implementation 'org.flywaydb:flyway-database-h2'` to `build.gradle`. Flyway 10 split its
  database support into modules and the exact split moves between versions.
- Station 04: in `requests/feedback.http`, press Alt + Enter on `{{qaPassword}}` and check that
  the IDE offers to add it to the private environment file. If your build does not, attendees
  create `requests/http-client.private.env.json` by hand, with the same environment names as
  `http-client.env.json`.
