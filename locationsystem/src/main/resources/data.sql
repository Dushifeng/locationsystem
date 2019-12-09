create table if not exists message (
            id int primary key auto_increment,
            dev_mac varchar(45) not null,
            frequency int not null,
            rssi int not null,
            ap_mac varchar(45) not null,
            timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
            ) character set utf8 collate utf8_bin;|
drop procedure IF exists del_data;|
create procedure del_data(IN `date_inter` int)
BEGIN
    DELETE FROM `location_system`.message WHERE created_on < DATE_SUB(CURDATE(),INTERVAL date_inter DAY);
END;|
DROP EVENT IF EXISTS del_event;|
create event del_event
on schedule
EVERY 1 day
do call del_data()



