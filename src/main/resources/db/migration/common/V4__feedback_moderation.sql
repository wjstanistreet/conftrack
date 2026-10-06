-- Comments a moderator has looked at. Most feedback has never been reviewed, so most
-- feedback rows have no row here.
create table feedback_moderation (
    feedback_id bigint      primary key references feedback (id),
    status      varchar(20) not null,
    reviewed_at timestamp   not null
);

insert into feedback_moderation (feedback_id, status, reviewed_at)
select id, 'APPROVED', submitted_at
from feedback
where mod(id, 7) = 0;
