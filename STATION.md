# Station 05 — The right code, the wrong bean

**Block:** C, "make the API behave" · **Ticket:** `BACKLOG.md` 05, "Unhappy ratings must count"
· **Fixed on:** `solution/station-05` (also `solution/block-c`)

## What was wrong

Sessions rated only 1s and 2s had a score of `0.0`, as if nobody had rated them, although their
average rating was not 0. The unhappy ratings were being thrown away.

`WeightedFeedbackScorer`, the class we chose by name at Station 01, read correctly. A breakpoint
in it never fired.

## Why it happened

Two overrides, neither visible in the code we were reading:

- `LocalScoringConfig` only applies under the `local` profile. It declares a bean *named*
  `weightedFeedbackScorer` that is really a `LenientFeedbackScorer`, which drops ratings of 2 and
  below. Because `allow-bean-definition-overriding` is `true`, it silently replaced the real one.
  The `@Qualifier` from Station 01 picks a bean by name, and the name now pointed somewhere else.
- `application-local.yml` set `low-rating-weight: 1.0`, which would have cancelled the weighting
  even if the right bean had won.

Both came in through one line in `application.yml`: `spring.profiles.include: local`.

## Why it mattered

Reading the code could not find this. Every file was correct on its own. The bug only existed in
what Spring assembled at runtime, from a copied flag, a local profile and a reused bean name.
Nobody wrote it on purpose, and nobody individually is at fault. That is what real production
incidents look like.

## How we found it

**The Spring Debugger.**

1. A breakpoint on the first line *inside* `FeedbackService.summarise()`, then debug the
   application and call `GET /api/dashboard/summary`.
2. Expanding `this.scorer`: the declared type is `FeedbackScorer`, and the actual instance was
   **`LenientFeedbackScorer`**. Nothing in the file we were reading said so.
3. **Alt+F8** (Evaluate Expression) on `scorer.describe()` returned
   `lenient(drops ratings <= 2)`. The running application said what it was.
4. The Spring Debugger's view of the application context showed which bean definition won, and
   `lowRatingWeight` resolving to `1.0` instead of its `1.5` default.

`GET /actuator/beans` in the HTTP Client is a second witness, if you want one.

## The fix

In `application.yml`, remove the `local` profile:

```yaml
spring:
  profiles:
    include: local
```

It was the only profile listed, so the whole `profiles` entry goes.

## How we knew it worked

Both "Scores count unhappy ratings" and "Unhappy ratings count extra" in
`requests/dashboard.http` went green. Session 1's score went from `0.0` to `1.0`, matching its
average rating, and 16 of the 41 sessions changed on the dashboard. The settings request showed
`lowRatingWeight` at `1.5`.

## Without IntelliJ IDEA Ultimate

Breakpoints tell you what your code did. Which bean Spring chose, and which property value won,
you have to infer, usually by adding logging and redeploying.

## Worth knowing

If you put the breakpoint on the `summarise()` *signature* line, it becomes a method breakpoint,
and the debugger stops in `FeedbackService$$SpringCGLIB$$0` with `this.scorer` as `null`. That is
a proxy Spring generates because the method is `@Transactional`. Move the breakpoint into the
method body, or press **F9** once to reach the real object. The proxy is one more thing Spring
does that the source code does not show.
