drop all objects;

CREATE TABLE IF NOT EXISTS films (
  id integer generated by default as identity primary key,
  name varchar(50) NOT NULL,
  description varchar(200) NOT NULL,
  release_date DATE,
  duration integer,
  rate integer,
  mpa integer,
  mark integer
);

CREATE TABLE IF NOT EXISTS filme_genres (
  film_id integer,
  genre_id integer,
  PRIMARY KEY (film_id, genre_id),
  FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS genres (
  id integer generated by default as identity primary key,
  name varchar(20)
);

CREATE TABLE IF NOT EXISTS mpa (
  id integer generated by default as identity primary key,
  name varchar(10)
);

CREATE TABLE IF NOT EXISTS users (
  id integer generated by default as identity primary key,
  email varchar(50) UNIQUE NOT NULL,
  login varchar(20) UNIQUE NOT NULL,
  name varchar(20) NOT NULL,
  birthday DATE
);

CREATE TABLE IF NOT EXISTS friends (
  user_id integer,
  friend_id integer,
  PRIMARY KEY (user_id, friend_id),
  FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  FOREIGN KEY (friend_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_liks (
  id_user integer,
  id_film integer,
  PRIMARY KEY (id_user, id_film),
  FOREIGN KEY (id_user) REFERENCES users (id) ON DELETE CASCADE,
  FOREIGN KEY (id_film) REFERENCES films (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews (
    id integer generated by default as identity primary key,
    content varchar(500) NOT NULL,
    is_positive boolean,
    user_id integer,
    film_id integer,
    useful integer,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS directors (
  id integer generated by default as identity primary key,
  name varchar(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_director (
  film_id integer,
  director_id integer,
  PRIMARY KEY (film_id, director_id),
  FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE,
  FOREIGN KEY (director_id) REFERENCES directors (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS events (
  id integer generated BY DEFAULT AS identity PRIMARY KEY,
  user_id integer,
  operation_type varchar(6),
  friend_id integer DEFAULT NULL,
  film_id integer DEFAULT NULL,
  review_id integer DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS marks (
    film_id integer,
    user_id integer,
    mark integer,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

ALTER TABLE events ADD CONSTRAINT one_of_three_is_not_null
CHECK (
    ( CASE WHEN friend_id IS NULL THEN 0 ELSE 1 END
    + CASE WHEN film_id IS NULL THEN 0 ELSE 1 END
    + CASE WHEN review_id IS NULL THEN 0 ELSE 1 END
    ) = 1
);

ALTER TABLE reviews ADD FOREIGN KEY (film_id) REFERENCES films (id);

ALTER TABLE reviews ADD FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE filme_genres ADD FOREIGN KEY (film_id) REFERENCES films (id);

ALTER TABLE films ADD FOREIGN KEY (mpa) REFERENCES mpa (id);

ALTER TABLE friends ADD FOREIGN KEY (friend_id) REFERENCES users (id);

ALTER TABLE friends ADD FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE film_liks ADD FOREIGN KEY (id_film) REFERENCES films (id);

ALTER TABLE film_liks ADD FOREIGN KEY (id_user) REFERENCES users (id);

ALTER TABLE filme_genres ADD FOREIGN KEY (genre_id) REFERENCES genres (id);