
drop database if exists ziroom;
create database ziroom;
use ziroom;

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
	`historyHouseId` int unsigned not null,
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
	foreign key(historyHouseId) references history_house(id)
);

create table history_location(
	`id` int unsigned not null auto_increment,
	`historyHouseId` int unsigned not null,
	`houseId` varchar(16),
	`line` int,
	`stationName` varchar(32),
	`distance` int,
	primary key(id),
	foreign key(historyHouseId) references history_house(id)
);

create table history_price(
	`id` int unsigned not null auto_increment,
	`historyRoomId` int unsigned not null,
	`roomId` varchar(16),
	`rentPerMonth` int,
	`deposit` int,
	`servicePerYear` int,
	`desc` varchar(16),
	primary key(id),
	foreign key(historyRoomId) references history_room(id)
);