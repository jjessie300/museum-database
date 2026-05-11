-- Include your create table DDL statements in this file.
-- Make sure to terminate each statement with a semicolon (;)

-- LEAVE this statement on. It is required to connect to your database.
CONNECT TO COMP421;

-- Remember to put the create table ddls for the tables with foreign key references
--    ONLY AFTER the parent tables have already been created.

-- entity tables
CREATE TABLE Artists (
    artist_id INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    birth DATE,
    PRIMARY KEY (artist_id)
);

CREATE TABLE Genres (
    genre_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (genre_name)
);

CREATE TABLE Museum_Personnel (
    employee_id INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY (employee_id)
);

CREATE TABLE Visitor (
    email VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(200),
    birth DATE,
    PRIMARY KEY (email)
);

CREATE TABLE Artifacts (
    artifact_id INTEGER NOT NULL,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    date_of_creation INTEGER, -- YEAR, could be unknown
    PRIMARY KEY (artifact_id)
);

CREATE TABLE Paintings (
    artifact_id INTEGER NOT NULL,
    medium VARCHAR(50),
    paint_type VARCHAR(50),
    PRIMARY KEY (artifact_id),
    FOREIGN KEY (artifact_id) REFERENCES Artifacts(artifact_id)
      ON DELETE CASCADE
);

CREATE TABLE Photographs (
    artifact_id INTEGER NOT NULL,
    film_type VARCHAR(50),
    PRIMARY KEY (artifact_id),
    FOREIGN KEY (artifact_id) REFERENCES Artifacts(artifact_id)
      ON DELETE CASCADE
);

CREATE TABLE Sculpture (
    artifact_id INTEGER NOT NULL,
    material VARCHAR(50),
    dimensions VARCHAR(100),
    PRIMARY KEY (artifact_id),
    FOREIGN KEY (artifact_id) REFERENCES Artifacts(artifact_id)
      ON DELETE CASCADE
);

CREATE TABLE Exhibitions (
    exhibition_id INTEGER NOT NULL,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    PRIMARY KEY (exhibition_id),
    CHECK (start_date < end_date) -- constraint
);

CREATE TABLE Tours (
    tour_id INTEGER NOT NULL,
    description VARCHAR(1000),
    cost DECIMAL(10,2) CHECK (cost >= 0), -- constraint
    employee_id INTEGER NOT NULL, -- must have a guide
    PRIMARY KEY (tour_id),
    FOREIGN KEY (employee_id) REFERENCES Museum_Personnel(employee_id)
);

CREATE TABLE TourDates (
    tour_id INTEGER NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    PRIMARY KEY (tour_id, date, start_time),
    FOREIGN KEY (tour_id) REFERENCES Tours(tour_id)
);

-- relationship tables
CREATE TABLE Displays (
    artifact_id INTEGER NOT NULL,
    exhibition_id INTEGER NOT NULL,
    room VARCHAR(50),
    PRIMARY KEY (artifact_id),
    FOREIGN KEY (artifact_id) REFERENCES Artifacts(artifact_id) ON DELETE CASCADE,
    FOREIGN KEY (exhibition_id) REFERENCES Exhibitions(exhibition_id) ON DELETE CASCADE
);

CREATE TABLE Creates (
    artifact_id INTEGER NOT NULL,
    artist_id INTEGER NOT NULL,
    PRIMARY KEY (artifact_id, artist_id),
    FOREIGN KEY (artifact_id) REFERENCES Artifacts(artifact_id) ON DELETE CASCADE,
    FOREIGN KEY (artist_id) REFERENCES Artists(artist_id) ON DELETE CASCADE
);

CREATE TABLE Exhibition_Genre (
    exhibition_id INTEGER NOT NULL,
    genre_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (exhibition_id, genre_name),
    FOREIGN KEY (exhibition_id) REFERENCES Exhibitions(exhibition_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_name) REFERENCES Genres(genre_name) ON DELETE CASCADE
);

CREATE TABLE Artifact_Genre (
    artifact_id INTEGER NOT NULL,
    genre_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (artifact_id, genre_name),
    FOREIGN KEY (artifact_id) REFERENCES Artifacts(artifact_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_name) REFERENCES Genres(genre_name) ON DELETE CASCADE
);

CREATE TABLE Tour_Exhibitions (
    exhibition_id INTEGER NOT NULL,
    tour_id INTEGER NOT NULL,
    PRIMARY KEY (exhibition_id, tour_id),
    FOREIGN KEY (exhibition_id) REFERENCES Exhibitions(exhibition_id) ON DELETE CASCADE,
    FOREIGN KEY (tour_id) REFERENCES Tours(tour_id) ON DELETE CASCADE
);

CREATE TABLE Organizer (
    employee_id INTEGER NOT NULL,
    tour_id INTEGER NOT NULL,
    PRIMARY KEY (employee_id, tour_id),
    FOREIGN KEY (employee_id) REFERENCES Museum_Personnel(employee_id) ON DELETE CASCADE,
    FOREIGN KEY (tour_id) REFERENCES Tours(tour_id) ON DELETE CASCADE
);

CREATE TABLE Reservation (
    email VARCHAR(100) NOT NULL,
    tour_id INTEGER NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    booking_date DATE NOT NULL,
    PRIMARY KEY (email, tour_id, date, start_time),
    FOREIGN KEY (email) REFERENCES Visitor(email) ON DELETE CASCADE,
    FOREIGN KEY (tour_id, date, start_time) REFERENCES TourDates(tour_id, date, start_time) ON DELETE CASCADE
);


