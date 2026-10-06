# ConfTrack

A small internal service for tracking workshop sessions and the feedback attendees leave for
them. It is the demo application for the **IntelliJ IDEA Ultimate** workshop.

It is also broken in eight different ways. That is deliberate. Every one of those eight bugs is
findable in seconds with a tool the IntelliJ IDEA subscription switches on, and tedious to find
without one. That contrast is the whole workshop.

---

## Before the day

1. Install the pinned IntelliJ IDEA version and activate the company licence.
2. Install JDK 21 (or let IDEA download it for you).
3. Install Docker Desktop and check that `docker run hello-world` works.
4. Clone this repository, check out `setup-check`, run `./gradlew bootRun`, and confirm you get
   an **OK** page at <http://localhost:8080>.
5. Reply "setup done" to the pre-work email.

Step 4 matters more than it looks: it downloads every dependency the real project needs, so
thirty people are not all pulling Spring Boot over the office wifi at the same moment.

## On the day

```bash
git checkout main
./gradlew bootRun
```

Spring Boot starts PostgreSQL from `compose.yaml` for you. The dashboard is at
<http://localhost:8080>, the API lives under `/api`, and the `requests/` folder holds ready-made
HTTP Client requests.

**No Docker? No problem.** Run the whole application against an in-memory database instead:

```bash
./gradlew bootRun --args='--spring.profiles.active=h2'
```

Same migrations, same seed data, same eight bugs. This is a fully supported way to do the
workshop, not a degraded one. The one difference you will notice is Station 06: an in-memory
database answers queries so quickly that the dashboard takes about half a second rather than
eight. The profiler still shows exactly the same wide plateau, so the station works; the number
on the page is just less dramatic.

## The stations

Each station is one symptom and one tool. You are not being asked to write Java. You are being
asked to **find** the problem with the tool — the fix itself is on the handout, ready to paste.

| # | Block | Symptom | The tool for the job |
|---|-------|---------|----------------------|
| 00 | — | Nothing is broken. Open the project and look at what appeared. | Search Everywhere, Spring tool window, Endpoints |
| 01 | A | It will not start. | Spring bean inspections, gutter icons, beans diagram |
| 02 | A | A setting in `application.yml` has no effect: the settings request in `requests/dashboard.http` fails. | Spring configuration completion and validation |
| 03 | B | Every endpoint that touches the database returns 500. | Database tools, SQL console, schema-aware inspections |
| 04 | C | One particular session id returns 500. | Endpoints window, HTTP Client |
| 05 | C | The score is wrong, and the code that computes it looks right. | Spring Debugger |
| 06 | D | The dashboard takes seconds to load. | IntelliJ Profiler |
| 07 | E | The dashboard shows `undefined` where a rating should be. | JavaScript/TypeScript support, Code With Me |
| 08 | D | A dependency has a published CVE. | Package Checker |

Every bug is in the code from the start, so a later station's error can show up before you reach
it. Once the application starts after Station 01, the log fills with
`column f1_0.email does not exist`. That is Station 03's bug, not Station 02's. Leave it for now.

## Checkpoint branches

Nobody gets left behind. Jumping to a checkpoint is a supported move, not a failure.

| Branch | Where it puts you |
|---|---|
| `setup-check` | The trivial pre-work app. Starts, serves an OK page, nothing else. |
| `main` | The broken application. The workshop starting point. |
| `solution/station-01` … `solution/station-08` | End of that station, with everything before it fixed. |
| `solution/block-a` … `solution/block-e` | End of that block. These are the "everyone catch up" points. |
| `solution/complete` | Fully working. |

Every solution branch carries a `STATION.md` at the repository root explaining what that station
was for, which tool found it, and how to run that tool. `docs/stations/` accumulates all of them.

## Layout

```
src/main/java/com/example/conftrack/
  config/     the settings the app binds from application.yml, and one profile-specific bean
  domain/     the two things this app knows about: a Session and a piece of Feedback
  repo/       how those two things are read out of the database
  service/    the rules - in particular, how a pile of ratings becomes one score
  web/        the bit that answers HTTP requests, plus the shapes it answers with
src/main/resources/
  application.yml           settings
  db/migration/             Flyway migrations, including ~50,000 seeded feedback rows
  static/                   the TypeScript dashboard, no bundler, no build step to speak of
requests/                   HTTP Client requests, in version control next to the code they test
```

## Ground rules

- **You are pairing, and the person who knows the language least drives.** The other person may
  talk, point and explain, but not touch the keyboard.
- Shouting "I'm stuck" is expected, not embarrassing.
- No prizes for finishing first.
- If you cannot read the code, that is the demonstration, not a problem.
