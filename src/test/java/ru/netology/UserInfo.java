package ru.netology;

import lombok.Value;

@Value
public class UserInfo {
    String login;
    String password;
    String status;
}