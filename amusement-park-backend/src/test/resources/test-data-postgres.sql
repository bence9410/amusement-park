insert into photo(photo)
values (lo_from_bytea(0, 'data:image/jpeg;base64,test1'));

insert into photo(photo)
values (lo_from_bytea(0, 'data:image/jpeg;base64,test2'));

insert into photo(photo)
values (lo_from_bytea(0, 'data:image/jpeg;base64,test3'));

insert into users(name, authority, date_of_birth, date_of_sign_up, password, money, coupon, is_activated_coupon,
                  photo_id)
values ('Bence', 'ROLE_ADMIN', '1994-10-22', '2020-05-06 15:33:00',
        '$2a$10$dti34l30HkqgVLgmSD26GeO5uO4EOxA4ttVgyPuRPJ1WVrzgJTpE6', 1000, 0, false,
        (select id from photo order by id limit 1) );

insert into amusement_park(name, entrance_fee, owner_name)
values ('Bence''s park', 100, 'Bence');

insert into machine(fantasy_name, minimum_required_age, ticket_price, video, video_length_in_seconds, amusement_park_id)
values ('Retro carousel', 12, 15, 'asd', 5, (select id from amusement_park));

insert into machine(fantasy_name, minimum_required_age, ticket_price, video, video_length_in_seconds, amusement_park_id)
values ('Magical dodgem', 12, 20, 'asd', 5, (select id from amusement_park));

insert into machine(fantasy_name, minimum_required_age, ticket_price, video, video_length_in_seconds, amusement_park_id)
values ('Electric gocart', 16, 25, 'asd', 5, (select id from amusement_park));

insert into machine(fantasy_name, minimum_required_age, ticket_price, video, video_length_in_seconds, amusement_park_id)
values ('Titanic', 14, 30, 'asd', 5, (select id from amusement_park));

insert into machine(fantasy_name, minimum_required_age, ticket_price, video, video_length_in_seconds, amusement_park_id)
values ('Super roller coaster', 14, 30, 'asd', 5, (select id from amusement_park));

insert into amusement_park_know_user(date_of_first_enter, amusement_park_id, user_name)
values ('2020-05-06 15:33:00', (select id from amusement_park), 'Bence');

insert into guest_book_registry(id, date_of_registry, text_of_registry, amusement_park_id, user_name)
values (10, '2020-05-06 15:33:03.894', 'Amazing.', (select id from amusement_park), 'Bence');

insert into users(name, authority, date_of_birth, date_of_sign_up, password, money, coupon, is_activated_coupon,
                  photo_id)
values ('creator', 'ROLE_CREATOR', '1994-10-22', '2020-05-06 15:33:03.894',
        '$2a$10$dti34l30HkqgVLgmSD26GeO5uO4EOxA4ttVgyPuRPJ1WVrzgJTpE6', 1000, 0,
        false, (select id from photo order by id limit 1 offset 2) );

insert into amusement_park(name, entrance_fee, owner_name)
values ('test park 100', 200, 'creator');

insert into amusement_park(name, entrance_fee, owner_name)
values ('test park 101', 200, 'Bence');

insert into machine(fantasy_name, minimum_required_age, ticket_price, video, video_length_in_seconds, amusement_park_id)
values ('test Titanic', 10, 20, 'asd', 5, (select id from amusement_park where name = 'test park 100'));

insert into machine(fantasy_name, minimum_required_age, ticket_price, video, video_length_in_seconds, amusement_park_id)
values ('test2 Titanic', 10, 20, 'asd', 5, (select id from amusement_park where name = 'test park 101'));

insert into users(name, authority, date_of_birth, date_of_sign_up, password, money, coupon, is_activated_coupon,
                  photo_id)
values ('visitor', 'ROLE_VISITOR', '1994-10-22', '2020-05-06 15:33:03.894',
        '$2a$10$dti34l30HkqgVLgmSD26GeO5uO4EOxA4ttVgyPuRPJ1WVrzgJTpE6', 1000, 0, false,
        (select id from photo order by id limit 1 offset 1) );

insert into users(name, authority, date_of_birth, date_of_sign_up, password, money, coupon, is_activated_coupon,
                  amusement_park_id)
values ('inPark', 'ROLE_VISITOR', '1994-10-22', '2020-05-06 15:33:03.894',
        '$2a$10$dti34l30HkqgVLgmSD26GeO5uO4EOxA4ttVgyPuRPJ1WVrzgJTpE6', 1000, 0,
        false, (select id from amusement_park where name = 'test park 100'));

insert into users(name, authority, date_of_birth, date_of_sign_up, password, money, coupon, is_activated_coupon,
                  amusement_park_id,
                  machine_id)
values ('onMachine', 'ROLE_VISITOR', '1994-10-22', '2020-05-06 15:33:03.894',
        '$2a$10$dti34l30HkqgVLgmSD26GeO5uO4EOxA4ttVgyPuRPJ1WVrzgJTpE6', 1000, 0,
        false, (select id from amusement_park where name = 'test park 100'),
        (select id from machine where fantasy_name = 'test Titanic'));

insert into amusement_park_know_user(date_of_first_enter, amusement_park_id, user_name)
values ('2020-05-06 15:33:03.894', (select id from amusement_park where name = 'test park 100'), 'inPark');

insert into amusement_park_know_user(date_of_first_enter, amusement_park_id, user_name)
values ('2020-05-06 15:33:03.894', (select id from amusement_park where name = 'test park 100'), 'onMachine');

insert into guest_book_registry(date_of_registry, text_of_registry, amusement_park_id, user_name)
values ('2020-05-06 15:33:03.894', 'test Amazing.', (select id from amusement_park where name = 'test park 100'),
        'inPark');

insert into guest_book_registry(date_of_registry, text_of_registry, amusement_park_id, user_name)
values ('2020-05-06 15:33:03.894', 'test Very good.', (select id from amusement_park where name = 'test park 100'),
        'inPark');
