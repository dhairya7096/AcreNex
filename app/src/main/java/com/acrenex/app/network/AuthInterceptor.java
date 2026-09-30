package com.acrenex.app.network;

import com.acrenex.app.security.SecureStorage;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Context;

public class AuthInterceptor implements Interceptor {

    private final SecureStorage secureStorage;

    public AuthInterceptor(Context context) {

        secureStorage =
                new SecureStorage(
                        context.getApplicationContext()
                );
    }

    @Override
    public Response intercept(
            Chain chain
    ) throws IOException {

        Request original =
                chain.request();

        String token =
                secureStorage.getToken();

        Request.Builder builder =
                original.newBuilder();

        /*
         * Never add an empty Authorization header.
         */

        if (token != null &&
                !token.trim().isEmpty()) {

            builder.header(
                    "Authorization",
                    "Bearer " + token
            );
        }

        /*
         * Tell the server that AcreNex
         * expects JSON APIs.
         */

        builder.header(
                "Accept",
                "application/json"
        );

        return chain.proceed(
                builder.build()
        );
    }
}