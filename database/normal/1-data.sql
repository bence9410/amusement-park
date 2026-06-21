insert into photo(photo)
values (lo_from_bytea(0, 'data:image/jpeg;base64,test0'));

insert into users(name, authority, date_of_birth, date_of_sign_up, password, money, coupon, is_activated_coupon,
                  photo_id)
values ('Bence', 'ROLE_ADMIN', '1994-10-22', '2020-05-06 15:33:00',
        '$2a$10$dti34l30HkqgVLgmSD26GeO5uO4EOxA4ttVgyPuRPJ1WVrzgJTpE6', 1000, 0, false,
        (select id from photo order by id limit 1) );

insert into amusement_park(name, entrance_fee, owner_name)
values ('Bence''s park', 5, 'Bence');

insert into machine(fantasy_name, minimum_required_age, ticket_price, video, video_length_in_seconds, amusement_park_id)
values ('Retro carousel', 12, 3, 'oNY_R3MmIbM', 329, (select id from amusement_park));

insert into machine(fantasy_name, minimum_required_age, ticket_price, video, video_length_in_seconds, amusement_park_id)
values ('Magical dodgem', 12, 1, 'FATfO8ScbCI', 116, (select id from amusement_park));

insert into machine(fantasy_name, minimum_required_age, ticket_price, video, video_length_in_seconds, amusement_park_id)
values ('Electric gocart', 16, 1, 'Qa2kYagOCiw', 185, (select id from amusement_park));

insert into machine(fantasy_name, minimum_required_age, ticket_price, video, video_length_in_seconds, amusement_park_id)
values ('Titanic', 14, 2, 'UYWkF0BATDc', 229, (select id from amusement_park));

insert into machine(fantasy_name, minimum_required_age, ticket_price, video, video_length_in_seconds, amusement_park_id)
values ('Super roller coaster', 14, 2, 's9njwl_VzZA', 217, (select id from amusement_park));

insert into amusement_park_know_user(date_of_first_enter, amusement_park_id, user_name)
values ('2020-05-06 15:33:00', (select id from amusement_park), 'Bence');

insert into guest_book_registry(date_of_registry, text_of_registry, amusement_park_id, user_name)
values ('2020-05-06 15:33:03.894', 'Amazing.', (select id from amusement_park), 'Bence');