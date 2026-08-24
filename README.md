# ConfTrack — setup check

This branch exists for one reason: to prove your machine is ready before the workshop starts.

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
| IntelliJ IDEA | the pinned version, with the company licence active |
| JDK 21 | or let IDEA download it for you |
| Docker Desktop | check `docker run hello-world` works. **This branch does not need it — the workshop branch does.** |

**No seat on the licence?** Reply to the pre-work email now. Do **not** start your one-time
30-day trial yet — you want it during the workshop, not before it.

Once you see the OK page, reply "setup done". Then `git checkout main` on the day.
