-- ~50,000 rows, deterministic so every attendee sees the same numbers.
insert into feedback (session_id, attendee_email, rating, comments, submitted_at)
select 1 + mod(x, 40),
       'attendee' || mod(x, 900) || '@example.com',
       1 + mod(x * 7, 5),
       'Feedback note ' || x,
       dateadd('DAY', -mod(x, 45), timestamp '2026-09-01 12:00:00')
from system_range(1, 50000);
