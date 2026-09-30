package com.acrenex.app.network;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;

import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {

    private static Retrofit retrofit;

    private ApiClient() {
        // Prevent object creation
    }

    public static Retrofit getClient(
            Context context
    ) {

        if (retrofit == null) {

            /*
             * Logging is intentionally disabled
             * for release builds.
             *
             * Do NOT log:
             * - passwords
             * - Proof IDs
             * - JWT tokens
             * - personal records
             */

            HttpLoggingInterceptor logging =
                    new HttpLoggingInterceptor();

            logging.setLevel(
                    HttpLoggingInterceptor.Level.NONE
            );

            AuthInterceptor authInterceptor =
                    new AuthInterceptor(
                            context
                    );

            OkHttpClient client =
                    new OkHttpClient.Builder()

                            .connectTimeout(
                                    ApiConfig.CONNECT_TIMEOUT_SECONDS,
                                    TimeUnit.SECONDS
                            )

                            .readTimeout(
                                    ApiConfig.READ_TIMEOUT_SECONDS,
                                    TimeUnit.SECONDS
                            )

                            .writeTimeout(
                                    ApiConfig.WRITE_TIMEOUT_SECONDS,
                                    TimeUnit.SECONDS
                            )

                            .addInterceptor(
                                    authInterceptor
                            )

                            .addInterceptor(
                                    logging
                            )

                            .build();

            retrofit =
                    new Retrofit.Builder()

                            .baseUrl(
                                    ApiConfig.BASE_URL
                            )

                            .client(client)

                            .addConverterFactory(
                                    GsonConverterFactory
                                            .create()
                            )

                            .build();
        }

        return retrofit;
    }

    public static void resetClient() {

        retrofit = null;
    }
}