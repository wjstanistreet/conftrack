# Station 01 — It won't start

**Block:** A, "make it start" · **Time:** ~10 minutes · **Fixed on:** `solution/station-01`

## The symptom

**Ticket:** `BACKLOG.md` 01, "Ship the weighted scorer".

`./gradlew bootRun` dies during startup with `NoUniqueBeanDefinitionException`: Spring found two
things that could be injected as a `FeedbackScorer` and will not guess between them.

## The bug

`WeightedFeedbackScorer` and `SimpleFeedbackScorer` are both `@Component`s implementing
`FeedbackScorer`. `FeedbackService` asks for "a `FeedbackScorer`" and there are two.

## The fix (this is on the handout — do not make anyone invent it)

In `FeedbackService`, name the bean you want on the constructor parameter:

```java
@Qualifier("weightedFeedbackScorer") FeedbackScorer scorer
```

## The tool, and how to drive it

**Spring framework support: injection-point inspections, gutter icons, the beans diagram.**

1. Open `FeedbackService`. Before running anything, the constructor parameter is underlined.
   Hover it: IDEA names *both* candidate beans in the tooltip.
2. Click the small bean gutter icon beside the parameter. It offers both candidates and
   navigates to either one. Nobody typed a class name or ran a search.
3. Open the **Spring** tool window → the beans diagram, and show `FeedbackScorer` with two
   arrows landing on it. This is the picture that makes the error make sense.
4. Use ⌥⏎ on the underlined parameter and let IDEA offer the qualifier itself.

**Without the subscription:** run it, read a sixty-line stack trace, search the codebase for
`implements FeedbackScorer`, then work out which one was meant. Do this first, on the projector,
with a stopwatch if you like. The contrast is the lesson.

## How to run the room

- Time-box **the tool**, not the solution: "four minutes with the Spring tool window."
- Ask the non-Java person to drive. They cannot read the stack trace, and they do not need to —
  the gutter icon does not care what language you know.
- Ask out loud: *"How would you have found this in a text editor?"* Let somebody answer. The
  answer is always some flavour of "grep and hope".

## Proof it worked

The application starts. That is it — a language-neutral, unmistakable green.

## Handing over to Station 02

Expect this question the moment the application starts: the log now shows
`ERROR: column f1_0.email does not exist`, and the dashboard says `Request failed: 500`. That is
**Station 03's** bug. Every bug is in the code from the start, and that one is simply the loudest.
Say so before anyone starts chasing it: *"That error is real, and we fix it in Block B. Leave it."*

Station 02 makes no noise of its own, so give the room the one thing that does. Open
`requests/dashboard.http` and run **"The settings the application is running with"**. It needs no
database, so it works now. Both tests go red:

- `minimumRating` is `1`, but ticket 02 wants `2`.
- `recentDays` is `30`, but ticket 02 wants `7`.

The failure messages say both are still on their defaults and point at `conftrack.feedback` in
`application.yml`. Open it: the section is empty, and nobody knows what the keys are called.
Station 02 starts there.

## Facilitator notes

- If someone fixes it with `@Primary` on `WeightedFeedbackScorer` instead, that is a correct
  answer, but it will interact with Station 5. Ask them to also apply the `@Qualifier` and
  explain that you have plans for that bean later.
- `SimpleFeedbackScorer` is not dead code — it is the original scorer, kept because two internal
  reports quote its numbers. Say so. Bugs that come with a plausible history are more convincing.
