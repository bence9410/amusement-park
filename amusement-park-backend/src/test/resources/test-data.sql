insert into visitor(email, authority, date_of_birth, date_of_sign_up, password, spending_money)
values ('nembence1994@gmail.com', 'ROLE_ADMIN', '1994-10-22', '2020-05-06 15:33:00',
        '$2a$10$dti34l30HkqgVLgmSD26GeO5uO4EOxA4ttVgyPuRPJ1WVrzgJTpE6', 1000);

insert into visitor(email, authority, date_of_birth, date_of_sign_up, password, spending_money)
values ('fenicser85@gmail.com', 'ROLE_VISITOR', '1995-05-10', '2020-05-06 15:33:00',
        '$2a$10$dti34l30HkqgVLgmSD26GeO5uO4EOxA4ttVgyPuRPJ1WVrzgJTpE6', 1000);

insert into visitor(email, authority, date_of_birth, date_of_sign_up, password, spending_money)
values ('aladin@gmail.com', 'ROLE_VISITOR', '1992-11-25', '2020-05-06 15:33:00',
        '$2a$10$dti34l30HkqgVLgmSD26GeO5uO4EOxA4ttVgyPuRPJ1WVrzgJTpE6', 1000);

insert into amusement_park(name, capital, total_area, entrance_fee)
values ('Bence''s park', 30000, 5000, 100);

insert into machine(fantasy_name, minimum_required_age, number_of_seats, price, size_of_machine, ticket_price, type,
                    amusement_park_id)
values ('Retro carousel', 12, 20, 500, 200, 15, 'CAROUSEL', (select id from amusement_park));

insert into machine(fantasy_name, minimum_required_age, number_of_seats, price, size_of_machine, ticket_price, type,
                    amusement_park_id)
values ('Magical dodgem', 12, 15, 350, 250, 20, 'DODGEM', (select id from amusement_park));

insert into machine(fantasy_name, minimum_required_age, number_of_seats, price, size_of_machine, ticket_price, type,
                    amusement_park_id)
values ('Electric gocart', 16, 10, 450, 300, 25, 'GOKART', (select id from amusement_park));

insert into machine(fantasy_name, minimum_required_age, number_of_seats, price, size_of_machine, ticket_price, type,
                    amusement_park_id)
values ('Titanic', 14, 30, 600, 400, 30, 'SHIP', (select id from amusement_park));

insert into machine(fantasy_name, minimum_required_age, number_of_seats, price, size_of_machine, ticket_price, type,
                    amusement_park_id)
values ('Super roller coaster', 14, 20, 550, 450, 30, 'ROLLER_COASTER', (select id from amusement_park));

insert into amusement_park_know_visitor(date_of_first_enter, amusement_park_id, visitor_email)
values ('2020-05-06 15:33:00', (select id from amusement_park), 'nembence1994@gmail.com');

insert into amusement_park_know_visitor(date_of_first_enter, amusement_park_id, visitor_email)
values ('2020-05-06 15:33:00', (select id from amusement_park), 'fenicser85@gmail.com');

insert into guest_book_registry(id, date_of_registry, text_of_registry, amusement_park_id, visitor_email)
values (10, '2020-05-06 15:33:03.894', 'Amazing.', (select id from amusement_park), 'nembence1994@gmail.com');

insert into guest_book_registry(date_of_registry, text_of_registry, amusement_park_id, visitor_email)
values ('2020-05-06 15:33:03.894', 'Very good.', (select id from amusement_park), 'fenicser85@gmail.com');

insert into amusement_park(name, capital, total_area, entrance_fee)
values ('test park 100', 5000, 5000, 200);

insert into amusement_park(name, capital, total_area, entrance_fee)
values ('test park 101', 5000, 5000, 200);

insert into machine(fantasy_name, minimum_required_age, number_of_seats, price, size_of_machine, ticket_price, type,
                    amusement_park_id)
values ('test Titanic', 10, 10, 200, 200, 20, 'SHIP', (select id from amusement_park where name = 'test park 100'));

insert into machine(fantasy_name, minimum_required_age, number_of_seats, price, size_of_machine, ticket_price, type,
                    amusement_park_id)
values ('test2 Titanic', 10, 10, 200, 200, 20, 'SHIP', (select id from amusement_park where name = 'test park 101'));

insert into visitor(email, authority, date_of_birth, date_of_sign_up, password, spending_money)
values ('test@gmail.com', 'ROLE_VISITOR', '1994-10-22', '2020-05-06 15:33:03.894',
        '$2a$10$kySxWnr0wtD6KKV59TKHIO/N3G58IpSfRn7a7cvZ.5TpaWUXaHus6', 1000);

insert into visitor(email, authority, date_of_birth, date_of_sign_up, password, spending_money, amusement_park_id)
values ('inPark@gmail.com', 'ROLE_VISITOR', '1994-10-22', '2020-05-06 15:33:03.894',
        '$2a$10$kySxWnr0wtD6KKV59TKHIO/N3G58IpSfRn7a7cvZ.5TpaWUXaHus6', 1000,
        (select id from amusement_park where name = 'test park 100'));

insert into visitor(email, authority, date_of_birth, date_of_sign_up, password, spending_money, amusement_park_id,
                    machine_id)
values ('onMachine@gmail.com', 'ROLE_VISITOR', '1994-10-22', '2020-05-06 15:33:03.894',
        '$2a$10$kySxWnr0wtD6KKV59TKHIO/N3G58IpSfRn7a7cvZ.5TpaWUXaHus6', 1000,
        (select id from amusement_park where name = 'test park 100'),
        (select id from machine where fantasy_name = 'test Titanic'));

insert into amusement_park_know_visitor(date_of_first_enter, amusement_park_id, visitor_email)
values ('2020-05-06 15:33:03.894', (select id from amusement_park where name = 'test park 100'), 'inPark@gmail.com');

insert into amusement_park_know_visitor(date_of_first_enter, amusement_park_id, visitor_email)
values ('2020-05-06 15:33:03.894', (select id from amusement_park where name = 'test park 100'), 'onMachine@gmail.com');

insert into guest_book_registry(date_of_registry, text_of_registry, amusement_park_id, visitor_email)
values ('2020-05-06 15:33:03.894', 'test Amazing.', (select id from amusement_park where name = 'test park 100'),
        'inPark@gmail.com');

insert into guest_book_registry(date_of_registry, text_of_registry, amusement_park_id, visitor_email)
values ('2020-05-06 15:33:03.894', 'test Very good.', (select id from amusement_park where name = 'test park 100'),
        'inPark@gmail.com');
