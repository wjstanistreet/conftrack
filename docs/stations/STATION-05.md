# Station 05 — The right code, the wrong bean

**Block:** C, "make the API behave" · **Time:** ~11 minutes ·
**Fixed on:** `solution/station-05` (also `solution/block-c`)

## The symptom

**Ticket:** `BACKLOG.md` 05, "Unhappy ratings must count".

The scores do not add up. Sessions whose feedback is all 1s and 2s show a score of `0.0`, as if
nobody had rated them at all, while their `averageRating` says otherwise. The unhappy ratings are
being thrown away. `WeightedFeedbackScorer` — the class you qualified by name back at Station 1 —
reads correctly, and a breakpoint in it never gets hit.

## The bug

Two overrides, both invisible in the source you are looking at:

- `LocalScoringConfig` is `@Profile("local")` and declares a bean *named*
  `weightedFeedbackScorer` that returns a `LenientFeedbackScorer`. Because
  `spring.main.allow-bean-definition-overriding` is `true`, it silently replaces the scanned
  component. Your `@Qualifier` resolves by name, and the name now points somewhere else.
- `application-local.yml` sets `low-rating-weight: 1.0`, neutering the weighting even if the
  right bean had won.

Both arrive because `spring.profiles.include` lists `local`.

## The fix

In `application.yml`, remove `spring.profiles.include: local`. It is the only profile listed, so
the whole `profiles` entry goes.

## The tool, and how to drive it

**The Spring Debugger.** This is the station that proves the thesis sentence, so do not rush it.

1. Put a line breakpoint on the first line *inside* `FeedbackService.summarise()`, not on its
   signature line, and run in debug. Hit `GET /api/dashboard/summary`. The top frame should read
   `FeedbackService.summarise`, not `FeedbackService$$SpringCGLIB$$0` (see the facilitator notes).
2. In the debugger, expand `this.scorer`. The declared type is `FeedbackScorer`; the **actual
   instance is `LenientFeedbackScorer`**. Nothing in the file you were reading says that.
3. **⌥F8** — Evaluate expression — and run `scorer.describe()` against the live bean. It answers
   `lenient(drops ratings <= 2)`. The application tells you what it is, out loud.
4. Open the Spring Debugger's application-context view: actual bean instances, which definition
   won, resolved property values, active transactions, and the JDBC statements really being
   issued. Show `lowRatingWeight` resolving to `1.0`, not its `1.5` default in `FeedbackProperties`.
5. Cross-check with `GET {{baseUrl}}/actuator/beans` in the HTTP Client if you want a second
   witness.

**Without the subscription:** breakpoints tell you what your code did. Which bean Spring chose,
which property value won, and what SQL was really issued, you infer — usually by adding logging
and redeploying.

## How to run the room

- Ask people to *predict* what `this.scorer` will be before you expand it. Predict-then-reveal is
  worth more than any explanation.
- This is where the sceptic in the room converts, because this is the one thing they cannot do
  with a text editor and a stack trace.
- Language-neutral throughout: nobody needs to read Java to see one class name where they
  expected another.

## Proof it worked

`GET {{baseUrl}}/api/sessions/1` — the `score` goes from `0.0` to `1.0`, matching its
`averageRating`: the 1-star ratings are counted instead of thrown away. On the dashboard, 16 of
the 41 sessions change. `GET {{baseUrl}}/api/dashboard/settings` shows `lowRatingWeight` at `1.5`.
Both "Scores count unhappy ratings" and "Unhappy ratings count extra" in
`requests/dashboard.http` go green.

Do not promise the room that the score will fall *below* the average. In the seed data every
session's ratings share a single value, so the weighting has nothing to pull against.

## Facilitator notes

- Keep `allow-bean-definition-overriding: true`. It is what makes the override silent instead of
  a startup error, and "inherited from the service this was copied from" is the honest reason
  such a flag exists in real projects.
- Good discussion: whose fault is this bug? Nobody's, individually. That is the point.
- Somebody will click the gutter on the `summarise()` signature line. That makes a *method*
  breakpoint, which also fires in overrides, and because `summarise()` is `@Transactional` Spring
  has overridden it in a CGLIB proxy. The debugger stops in `FeedbackService$$SpringCGLIB$$0`: line
  `-1`, "variable types not available", and `this.scorer` is `null`, because the proxy was built
  without running the constructor. Fix: move the breakpoint into the method body, or press **F9**
  once to reach the real bean. Better still, use it: the proxy is one more thing Spring does that
  the source you are reading does not show, which is this station's whole argument.
