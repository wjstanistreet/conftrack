insert into conf_session (id, title, speaker, room, starts_at)
select x,
       'Session ' || x || ': ' || case mod(x, 6)
            when 0 then 'Testing in anger'
            when 1 then 'Refactoring legacy code'
            when 2 then 'Observability basics'
            when 3 then 'Contract testing'
            when 4 then 'Pairing that works'
            else 'Query plans for humans' end,
       case mod(x, 5)
            when 0 then 'A. Okafor'
            when 1 then 'B. Lindqvist'
            when 2 then 'C. Moreau'
            when 3 then 'D. Patel'
            else 'E. Rossi' end,
       'Room ' || (1 + mod(x, 4)),
       dateadd('HOUR', mod(x, 6), dateadd('DAY', mod(x, 5), timestamp '2026-09-01 09:00:00'))
from system_range(1, 40);

-- Session 41 is on the programme but nobody has submitted feedback for it yet.
insert into conf_session (id, title, speaker, room, starts_at)
values (41, 'Closing remarks', 'A. Okafor', 'Room 1', timestamp '2026-09-05 16:30:00');

alter table conf_session alter column id restart with 42;
