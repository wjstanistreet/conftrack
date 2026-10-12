# ConfTrack — setup check

This branch exists for one reason: to prove your machine is ready before the workshop starts.

Open the project in IntelliJ IDEA, open `ConfTrackApplication` (<kbd>Shift</kbd> twice, type the
name) and click the green arrow beside `main()`. Running it from the IDE is how you will run it on
the day, because that is what lets IntelliJ IDEA Ultimate attach its Spring tooling.

If the IDE will not cooperate, run it from a terminal instead:

```bash
./gradlew bootRun
```

Then open <http://localhost:8080>. You should see a large **OK**.

That is the whole test. It also downloads every dependency the real project needs, which is why
we ask you to do it in advance — thirty people pulling Spring Boot over the office wifi at once
is how a hands-on workshop loses its first twenty minutes.

## What you need first

| | |
|---|---|
| IntelliJ IDEA Ultimate | the pinned version, **2026.2.3** |
| JDK 21 | or let IDEA download it for you |
| Git | to clone this repository and switch branches |
| Docker Desktop | check `docker run hello-world` works. **This branch does not need it — the workshop branch does.** |
| Node.js | the current LTS, with npm. **This branch does not need it — Station 08 does.** |
| Spring Debugger and WireMock plugins | in IDEA, **Settings \| Plugins \| Marketplace**. Stations 05 and 09 need them. |

Once you see the OK page, there is one more step, on the workshop branch. Check out `main`, let
IDEA finish syncing the Gradle project, and run this from the repository root:

```bash
npm ci
docker pull postgres:16-alpine
```

Then open the **Database** tool window, add a **PostgreSQL** data source, and click **Download
missing driver files** when it offers. You do not need to connect. That is everything the day
downloads, done ahead of time.

Then pop a thumbs up on the set-up check message.
