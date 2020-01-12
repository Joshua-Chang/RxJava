package com.example.rxjava.retrofit_okhttp_rxjava.retrofit_okhttp;

import io.reactivex.Observable;
import retrofit2.http.Body;

public interface IRequestNetwork {
    public Observable<RegisterResponse> registerAction(@Body RegisterRequest registerRequest);
    public Observable<LoginResponse> loginAction(@Body LoginRequest loginRequest);
}
