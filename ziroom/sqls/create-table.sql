
drop database if exists ziroom;
create database ziroom;
use ziroom;

create table location(
	`id` int unsigned not null auto_increment,
	`houseId` int,
	`line` int,
	`stationName` varchar(32),
	`distance` int,
	primary key(id)
);

create table house(
	`id` int unsigned not null auto_increment,
	`houseId` int,
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
	`roomId` int,
	`rentPerMonth` int,
	`deposit` int,
	`servicePerYear` int,
	`desc` varchar(16),
	primary key(id)
);

create table room(
	`id` int unsigned not null auto_increment,
	`houseId` int,
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






create table history_house(
	`id` int unsigned not null auto_increment,
	`houseId` int,
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
	`historyHouseId` int unsigned not null,
	`houseId` int,
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
	foreign key(historyHouseId) references history_house(id)
);

create table history_location(
	`id` int unsigned not null auto_increment,
	`historyHouseId` int unsigned not null,
	`houseId` int,
	`line` int,
	`stationName` varchar(32),
	`distance` int,
	primary key(id),
	foreign key(historyHouseId) references history_house(id)
);

create table history_price(
	`id` int unsigned not null auto_increment,
	`historyRoomId` int unsigned not null,
	`roomId` int,
	`rentPerMonth` int,
	`deposit` int,
	`servicePerYear` int,
	`desc` varchar(16),
	primary key(id),
	foreign key(historyRoomId) references history_room(id)
);