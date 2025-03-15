package com.duc.chatting.base;

public interface Resource<T> {
    void onSuccess(T response);
    void onFailure(Throwable t);
}
