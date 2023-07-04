package com.schoolmanagement.utils;

import com.schoolmanagement.entity.abstracts.User;
import com.schoolmanagement.payload.request.abstracts.BaseUserRequest;

public class CheckParameterUpdateMethod {

    public static boolean checkParameter(User user, BaseUserRequest baseUserRequest){
        return user.getSsn().equalsIgnoreCase(baseUserRequest.getSsn()) ||
                user.getUsername().equalsIgnoreCase(baseUserRequest.getUsername()) ||
                user.getPhoneNumber().equalsIgnoreCase(baseUserRequest.getPhoneNumber());
    }
}
