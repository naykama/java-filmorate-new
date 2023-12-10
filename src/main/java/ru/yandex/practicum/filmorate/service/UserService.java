package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.FriendsValidException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Set;

@Service
public class UserService {
    private final User user;
//    private final InMemoryUserStorage;

    public UserService(User user) {
        this.user = user;
    }
    public void addFriends(Long friendsId){
        if(!user.getFriends().contains(friendsId)){
            throw new FriendsValidException("Нет друга с таким id: "+ friendsId);
        }
        user.getFriends().add(friendsId);
    }
    public void dellFriends(Long friendsId){
        if(!user.getFriends().contains(friendsId)){
            throw new FriendsValidException("Нет друга с таким id: "+ friendsId);
        }
        user.getFriends().remove(friendsId);
    }
//    public getFriendsList(Long id){
//        return user.getFriends();
//    }

}
