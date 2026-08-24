-- ~50,000 rows, deterministic so every attendee sees the same numbers.
insert into feedback (session_id, attendee_email, rating, comments, submitted_at)
select 1 + (s % 40),
       'attendee' || (s % 900) || '@example.com',
       1 + (s * 7 % 5),
       'Feedback note ' || s,
       timestamp '2026-09-01 12:00:00' - (s % 45) * interval '1 day'
from generate_series(1, 50000) as s;
