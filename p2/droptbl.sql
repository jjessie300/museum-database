-- Include your drop table DDL statements in this file.
-- Make sure to terminate each statement with a semicolon (;)

-- LEAVE this statement on. It is required to connect to your database.
CONNECT TO COMP421;

-- Remember to put the drop table ddls for the tables with foreign key references
--    BEFORE the ddls to drop the parent tables (reverse of the creation order).

-- relationship tables
DROP TABLE Reservation;
DROP TABLE Organizer;
DROP TABLE Tour_Exhibitions;
DROP TABLE Artifact_Genre;
DROP TABLE Exhibition_Genre;
DROP TABLE Creates;
DROP TABLE Displays;

-- subclasses and weak entities
DROP TABLE Paintings;
DROP TABLE Photographs;
DROP TABLE Sculpture;
DROP TABLE TourDates;

-- entities
DROP TABLE Tours;
DROP TABLE Exhibitions;
DROP TABLE Visitor;
DROP TABLE Museum_Personnel;
DROP TABLE Artifacts;
DROP TABLE Genres;
DROP TABLE Artists;
