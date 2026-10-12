# Station 00 — Open the project

**Block:** none · **Bug:** none

Nothing is broken yet. This station is about what IntelliJ IDEA Ultimate works out on its own,
the moment you open a plain Gradle project, before anyone writes or fixes anything.

## What we're doing

Opening ConfTrack and looking at what appears. The files are the same in any editor. What changes
is how much of the *application* the IDE understands: its beans, its URLs, its database and its
containers.

> The free tier tells you what your code says. The subscription tells you what your application
> is actually doing.

Every station after this one builds on that idea.

## What appears on its own

| What | Where | Why it matters |
|---|---|---|
| Spring tool window | left sidebar | Every bean in the application, and how they connect |
| Endpoints window | bottom | Every URL the application answers, without reading a controller |
| Autowiring gutter icons | editor gutter, e.g. in `FeedbackService` | Click to jump to what is actually injected |
| Spring Boot run configuration | run widget | Created for you, with profiles and environment variables to edit |
| Docker Compose service | Services window | `compose.yaml` is detected, and Postgres starts with the app |

The Endpoints window lists eight `/api` routes. If yours is empty, the Gradle import has not
finished yet, or the licence is not active.

## One shortcut to learn first

**Double Shift — Search Everywhere.** Try typing:

- `FeedbackService` — a class
- `/api/dashboard` — an endpoint
- `dashboard.ts` — a frontend file

Classes, URLs and files all come back from one box. You will use it at every station.

## Finding your way around

You do not need to read Java to follow the day. Three folders cover most of the application:

- `web/` — the part that answers HTTP requests
- `repo/` — the part that talks to the database
- `config/` — the part that holds settings

## Without IntelliJ IDEA Ultimate

The project opens as a correct, unremarkable Gradle project. No beans, no endpoints, no
understanding of what Spring will do when it runs. You find all of that out by reading.

## Next

Run `ConfTrackApplication` from the green arrow beside `main()`. It will not start. That is
Station 01.
