# java-filmorate
Template repository for Filmorate project.

https://dbdiagram.io/d/6589b8e389dea62799864990

![Untitled (1)](https://github.com/mFurtov/java-filmorate/assets/139979986/0e2ce3a7-154f-4d96-a4e5-eaefa43c6715)


1. Вывести весь список фильмов: 
```sql
 select * from films 
```

2. Вывести весь список пользователей: 
```sql
select * from users 
```

3. Вывести жанр фильма:
```sql
select f."name",
g."name" 
from geners as g
inner join film_gener fg on fg.ganer_id = g.id 
inner join films f on f.id = fg.film_id 
group by f."name" ,g."name" 
```

4. Вывести рейтинг фильма:
```sql
select f."name",
m."name" 
from films as f
inner join mpa as m on f.mpa  = m.id 
```

5. Вывести статус для связи «дружба» пользователя:
```sql
select u.login,
fs.status 
from users as u
left join friendship_status as fs on u.status_friendship = fs.id  
```

6. Вывести друзей всех пользователей:
```sql
select u.login,
u2.login 
from users as u
inner join friends as f on f.friend_id = u.id 
inner join users as u2 on f.user_id  = u2.id 
```

7. Вывести все фильмы которые нарваться пользователям:
```sql
select u.login,
f."name" 
from users as u
inner join film_liks as fl on u.id = fl.id_user 
inner join films as f on fl.id_film  = f.id 
```
