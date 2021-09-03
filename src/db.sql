CREATE TABLE `kindle_library` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` text,
  `price` double DEFAULT NULL,
  `author` varchar(45) DEFAULT NULL,
  `author_uuid` varchar(45) DEFAULT NULL,
  `item` text,
  `category` varchar(45) DEFAULT NULL,
  `sold_amount` int DEFAULT NULL,
  `likes` int DEFAULT NULL,
  `public` tinyint DEFAULT NULL,
  `date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `kindle_user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `player` varchar(45) DEFAULT NULL,
  `uuid` varchar(45) DEFAULT NULL,
  `book_id` int DEFAULT NULL,
  `likes` tinyint DEFAULT NULL,
  `buy_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;