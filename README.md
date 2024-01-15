# java-filmorate
Template repository for Filmorate project.

https://dbdiagram.io/d/6589b8e389dea62799864990

![Untitled-3](https://github.com/mFurtov/java-filmorate/assets/139979986/eabf7bc3-c452-4b85-874f-67cf9679e477)


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

8. Вывести списко запросов на дружбу:
```sql
select u.login,
u2.login,
fs.status
from users as u 
inner join friendship_status as fs on u.id = fs.user_id 
inner join users as u2 on fs.friend_id = u2.id
```
9. Вывести друзей позьавателя:
```sql
select *
from  users
join friends on users.id = friends.friend_id
where friends.user_id = 1;
```
