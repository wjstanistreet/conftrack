# Station 00 — Open the project

**Block:** none · **Time:** 10 minutes · **Bug:** none

This is the free "wow" moment before any work starts. Nothing is broken here. The only thing
happening is that the IDE reads a plain Gradle project and quietly tells you what kind of
application it is.

## What to do

1. Open the project. Let the Gradle import finish.
2. Say nothing for a moment and let people watch the tool windows appear.

## What appears on its own

| What | Where | Why it matters |
|---|---|---|
| Spring tool window | left sidebar | Every bean in the application, and how they connect |
| Endpoints window | bottom | Every URL this application answers, without reading a controller |
| Autowiring gutter icons | editor gutter, e.g. `FeedbackService` | Click to jump to what is actually injected |
| Spring Boot run configuration | run widget | Created for you, with profile and env editing |
| Docker Compose service | Services window | `compose.yaml` detected; Postgres starts with the app |

Without the subscription this is a correct, unremarkable Gradle Java project. The files are the
same. What changed is how much of the application the IDE understood.

## How to run it

**Say the sentence the whole workshop hangs on, out loud, here:**

> "The free tier tells you what your code says. The subscription tells you what your application
> is actually doing."

Then teach exactly one shortcut and make everyone do it:

- **Double ⇧ — Search Everywhere.** Have them type `FeedbackService`, then `/api/dashboard`,
  then `dashboard.ts`. Classes, endpoints and frontend files all come back from one box.

## Sixty seconds of orientation

Open the Structure view and the project tree, and point at three things in plain English:

- `web/` — the bit that answers HTTP requests
- `repo/` — the bit that talks to the database
- `config/` — the bit that holds settings

That is most of the disorientation gone for anyone who does not write Java, and it demos the
navigation tooling while you do it.

## Volunteers

This is where you sweep the room for setup casualties — not later. Anyone whose Gradle import is
failing or whose Docker will not start gets moved to `--spring.profiles.active=h2` **now**.

## Proof it worked

The Endpoints window lists six `/api` routes. If somebody's is empty, their Gradle import has not
finished or their licence is not active. Fix that before Station 1.
