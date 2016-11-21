
drop database if exists ziroom_test;
create database ziroom_test;
use ziroom_test;

create table location(
	`id` int unsigned not null auto_increment,
	`houseId` varchar(16),
	`line` int,
	`stationName` varchar(32),
	`distance` int,
	primary key(id)
);

create table house(
	`id` int unsigned not null auto_increment,
	`houseId` varchar(16),
	`detailName` varchar(256),
	`notDetailName` varchar(256),
	`layout` varchar(16),
	`bedroom` int,
	`livingroom` int,
	`currentFloor` int,
	`totalFloor` int,
	primary key(id),
	unique key(houseId)
);

create table price(
	`id` int unsigned not null auto_increment,
	`roomId` varchar(16),
	`rentPerMonth` int,
	`deposit` int,
	`servicePerYear` int,
	`desc` varchar(16),
	primary key(id)
);

create table room(
	`id` int unsigned not null auto_increment,
	`houseId` varchar(16),
	`roomId` varchar(16),
	`number` varchar(16),
	`area` int,
	`orientation` varchar(16),
	`style` varchar(16),
	`styleVersion` int,
	`separateBalcony` boolean,
	`separateBathroom` boolean,
	`state` varchar(16),
	`begin` datetime,
	`end` datetime,
	primary key(id),
	unique key(roomId)
);


INSERT INTO `house` VALUES ('1', '1', 'detail name', 'not detail name', 'layout', '3', '1', '5', '10');
INSERT INTO `location` VALUES ('10', '1', '6', 'station name', '100');
INSERT INTO `location` VALUES ('20', '1', '6', 'station name 2', '200');
INSERT INTO `location` VALUES ('30', '1', '6', 'station name 3', '300');
INSERT INTO `price` VALUES ('100', '1000', '2000', '2000', '2100', '月付');
INSERT INTO `price` VALUES ('200', '1000', '1900', '1900', '2000', '季付');
INSERT INTO `price` VALUES ('300', '1000', '1900', '1900', '1900', '半年付');
INSERT INTO `price` VALUES ('400', '1000', '1900', '1900', '1800', '年付');
INSERT INTO `price` VALUES ('500', '2000', '3000', '3000', '3100', '月付');
INSERT INTO `price` VALUES ('600', '2000', '2900', '2900', '3000', '季付');
INSERT INTO `price` VALUES ('700', '2000', '2900', '2900', '2900', '半年付');
INSERT INTO `price` VALUES ('800', '2000', '2900', '2900', '2800', '年付');
INSERT INTO `room` VALUES ('10000', '1', '1000', 'number', '10', '南', '木棉', '4', '1', '0', 'Available', '2016-11-19 14:08:09', '2016-11-21 15:28:08');
INSERT INTO `room` VALUES ('20000', '1', '2000', 'number', '15', '南', '拿铁', '4', '0', '1', 'Unavailable', '2016-11-16 14:08:09', '2016-11-21 14:08:13');



create table history_house(
	`id` int unsigned not null auto_increment,
	`houseId` varchar(16),
	`detailName` varchar(256),
	`notDetailName` varchar(256),
	`layout` varchar(16),
	`bedroom` int,
	`livingroom` int,
	`currentFloor` int,
	`totalFloor` int,
	primary key(id)
);

create table history_room(
	`id` int unsigned not null auto_increment,
	`houseId` varchar(16),
	`roomId` varchar(16),
	`number` varchar(16),
	`area` int,
	`orientation` varchar(16),
	`style` varchar(16),
	`styleVersion` int,
	`separateBalcony` boolean,
	`separateBathroom` boolean,
	`state` varchar(16),
	`begin` datetime,
	`end` datetime,
	`historyHouseId` int unsigned not null,
	primary key(id),
	foreign key(historyHouseId) references history_house(id)
);

create table history_location(
	`id` int unsigned not null auto_increment,
	`houseId` varchar(16),
	`line` int,
	`stationName` varchar(32),
	`distance` int,
	`historyHouseId` int unsigned not null,
	primary key(id),
	foreign key(historyHouseId) references history_house(id)
);

create table history_price(
	`id` int unsigned not null auto_increment,
	`roomId` varchar(16),
	`rentPerMonth` int,
	`deposit` int,
	`servicePerYear` int,
	`desc` varchar(16),
	`historyRoomId` int unsigned not null,
	primary key(id),
	foreign key(historyRoomId) references history_room(id)
);