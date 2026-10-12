insert into conf_session (id, title, speaker, room, starts_at)
select s,
       'Session ' || s || ': ' || (array['Testing in anger','Refactoring legacy code','Observability basics',
                                         'Contract testing','Pairing that works','Query plans for humans'])[1 + (s % 6)],
       (array['A. Okafor','B. Lindqvist','C. Moreau','D. Patel','E. Rossi'])[1 + (s % 5)],
       'Room ' || (1 + (s % 4)),
       timestamp '2026-09-01 09:00:00' + (s % 5) * interval '1 day' + (s % 6) * interval '1 hour'
from generate_series(1, 40) as s;

-- Session 41 is on the programme but nobody has submitted feedback for it yet.
insert into conf_session (id, title, speaker, room, starts_at)
values (41, 'Closing remarks', 'A. Okafor', 'Room 1', timestamp '2026-09-05 16:30:00');

select setval(pg_get_serial_sequence('conf_session', 'id'), 41);
