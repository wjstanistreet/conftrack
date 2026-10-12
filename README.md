# ConfTrack

A small internal service for tracking workshop sessions and the feedback attendees leave for
them.

It is also broken in nine different ways. That is deliberate. Every one of those nine bugs is
findable with a tool the IntelliJ IDEA subscription switches on.

---

## Before the day

1. Install IntelliJ IDEA Ultimate, the pinned version: **2026.2.3**.
2. Install JDK 21 (or let IDEA download it for you).
3. Install Git, and Docker Desktop. Check that `docker run hello-world` works.
4. Install Node.js, the current LTS (npm comes with it). Station 08 uses it to rebuild the
   dashboard.
5. In IntelliJ IDEA, install the **Spring Debugger** and **WireMock** plugins (**Settings |
   Plugins | Marketplace**). Stations 05 and 09 need them.
6. Clone this repository, check out `setup-check`, and open it in IntelliJ IDEA. Open
   `ConfTrackApplication` (<kbd>Shift</kbd> twice, type the name) and click the green arrow beside
   `main()`. Confirm you get an **OK** page at <http://localhost:8080>. (If the IDE will not
   cooperate, `./gradlew bootRun` from a terminal does the same job.)
7. Check out `main` and let IDEA finish syncing the Gradle project. Then, from the repository
   root, download what the rest of the day needs:

   ```bash
   npm ci
   docker pull postgres:16-alpine
   ```

   Finally, open the **Database** tool window, add a **PostgreSQL** data source, and click
   **Download missing driver files** when it offers. You do not need to connect.
8. Pop a thumbs up on the set-up check message.

Steps 6 and 7 matter more than they look: they download everything the workshop needs, so thirty
people are not all pulling it over the office wifi at the same moment.

## On the day

Check out `main`, then run the application **from the IDE**: open `ConfTrackApplication` and click
the green arrow beside `main()`. Running it this way, rather than from a terminal, is what lets
IntelliJ IDEA Ultimate attach its Spring tooling: the Services window, live beans and endpoints in
the Actuator tab, the Spring Debugger, and the profiler. Several stations depend on it.

If you need a terminal fallback:

```bash
git checkout main
./gradlew bootRun
```

Spring Boot starts PostgreSQL from `compose.yaml` for you. The dashboard is at
<http://localhost:8080>, the API lives under `/api`, and the `requests/` folder holds ready-made
HTTP Client requests.

**No Docker? No problem.** Run the whole application against an in-memory database instead. In
the IDE, open the run configuration for `ConfTrackApplication` (**Run | Edit Configurations…**),
put `h2` in **Active profiles**, and run it as before. From a terminal:

```bash
./gradlew bootRun --args='--spring.profiles.active=h2'
```

Same migrations, same seed data, same nine bugs. This is a fully supported way to do the
workshop, not a degraded one.

## The stations

Each station is one symptom and one tool. You are not being asked to write Java. You are being
asked to **find** the problem with the tool — the fix itself is on the handout, ready to paste.

**Start each station from [`BACKLOG.md`](BACKLOG.md).** It holds one ticket per station: what the
business wants, how you will know it is done, and where to start looking.

| # | Block | Symptom | The tool for the job |
|---|-------|---------|----------------------|
| 00 | — | Nothing is broken. Open the project and look at what appeared. | Search Everywhere, Spring tool window, Endpoints |
| 01 | A | It will not start. | Spring bean inspections, gutter icons, beans diagram |
| 02 | A | The business wants two new settings, and nobody knows what the keys are called: the settings request in `requests/dashboard.http` fails. | Spring configuration completion and navigation |
| 03 | B | Every endpoint that touches the database returns 500. | Database tools, SQL console, schema-aware inspections |
| 04 | C | One particular session id returns 500, and posting a rating is refused. | Endpoints window, HTTP Client: environments, auth, chaining |
| 05 | C | The score is wrong, and the code that computes it looks right. | Spring Debugger |
| 06 | D | The dashboard takes seconds to load. | IntelliJ Profiler |
| 07 | D | A dependency has a published CVE. | Package Checker |
| 08 | E | The dashboard shows `undefined` where a rating should be. | JavaScript/TypeScript support, Code With Me |
| 09 | F | Facilities' new Venue API is not live yet, and room capacities come back empty. | OpenAPI support, WireMock stubs, HTTP Client |

Every bug is in the code from the start, so a later station's error can show up before you reach
it. Once the application starts after Station 01, the log fills with
`column f1_0.email does not exist`. That is Station 03's bug, not Station 02's. Leave it for now.

## Checkpoint branches

Nobody gets left behind. Jumping to a checkpoint is a supported move, not a failure.

| Branch | Where it puts you |
|---|---|
| `setup-check` | The trivial pre-work app. Starts, serves an OK page, nothing else. |
| `main` | The broken application. The workshop starting point. |
| `solution/station-01` … `solution/station-09` | End of that station, with everything before it fixed. |
| `solution/block-a` … `solution/block-f` | End of that block. These are the "everyone catch up" points. |
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
  http-client.env.json      shared variables, one block per environment
  http-client.private.env.json   your own secrets; git ignores it, so you create it
```

## Ground rules

- **If you're pairing, the person who knows the language least drives.**
- Shouting "I'm stuck" is expected, not embarrassing.
- It's not a problem if you can't read the code, the tools are what's being demo'd.
