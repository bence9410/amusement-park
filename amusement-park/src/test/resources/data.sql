insert into amusement_park(id, name, capital, total_area, entrance_fee) values (10,'test park 10', 5000,5000,200);

insert into amusement_park(id, name, capital, total_area, entrance_fee) values (11,'test park 11', 5000,5000,200);

insert into machine(id, fantasy_name, minimum_required_age, number_of_seats, price, size_of_machine, ticket_price, type, amusement_park_id) values (10, 'Titanic', 10, 10, 200, 200, 20, 'SHIP', 10);

insert into visitor(email, authority, date_of_birth, date_of_sign_up, password, spending_money) values('test@gmail.com', 'ROLE_VISITOR', '1994-10-22', '2020-05-06 15:33:03.894', '$2a$10$kySxWnr0wtD6KKV59TKHIO/N3G58IpSfRn7a7cvZ.5TpaWUXaHus6', 1000);

insert into visitor(email, authority, date_of_birth, date_of_sign_up, password, spending_money, amusement_park_id) values('inPark@gmail.com', 'ROLE_VISITOR', '1994-10-22', '2020-05-06 15:33:03.894', '$2a$10$kySxWnr0wtD6KKV59TKHIO/N3G58IpSfRn7a7cvZ.5TpaWUXaHus6', 1000, 10);

insert into visitor(email, authority, date_of_birth, date_of_sign_up, password, spending_money, amusement_park_id, machine_id) values('onMachine@gmail.com', 'ROLE_VISITOR', '1994-10-22', '2020-05-06 15:33:03.894', '$2a$10$kySxWnr0wtD6KKV59TKHIO/N3G58IpSfRn7a7cvZ.5TpaWUXaHus6', 1000, 10, 10);

insert into amusement_park_know_visitor(date_of_first_enter, amusement_park_id, visitor_email) values('2020-05-06 15:33:03.894', 10, 'inPark@gmail.com');

insert into amusement_park_know_visitor(date_of_first_enter, amusement_park_id, visitor_email) values('2020-05-06 15:33:03.894', 10, 'onMachine@gmail.com');

insert into guest_book_registry(id, date_of_registry, text_of_registry, amusement_park_id, visitor_email) values(10, '2020-05-06 15:33:03.894', 'Amazeing.', 10, 'inPark@gmail.com');

insert into guest_book_registry(date_of_registry, text_of_registry, amusement_park_id, visitor_email) values('2020-05-06 15:33:03.894', 'Very good.', 10, 'inPark@gmail.com');
