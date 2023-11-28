package ru.yandex.practicum.filmorate.controller;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    FilmController filmController;
    @BeforeEach
    void starter(){

    }

    @Test
    void get(){
       FilmController filmController = new FilmController();
       filmController.findAll();
    }


}