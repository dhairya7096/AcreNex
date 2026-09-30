package com.acrenex.app.network;

import com.google.gson.JsonObject;

import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // =====================================
    // HEALTH
    // =====================================

    @GET("health")
    Call<JsonObject> healthCheck();


    // =====================================
    // AUTHENTICATION
    // =====================================

    @POST("auth/login")
    Call<JsonObject> login(
            @Body JsonObject request
    );


    // =====================================
    // PROOF VERIFICATION
    // =====================================

    @POST("identity/verify")
    Call<JsonObject> verifyProof(
            @Body JsonObject request
    );


    // =====================================
    // CURRENT USER
    // =====================================

    @GET("users/me")
    Call<JsonObject> getCurrentUser();


    // =====================================
    // PARCEL SEARCH
    // =====================================

    @GET("parcels/{ulpin}")
    Call<JsonObject> getParcel(
            @Path("ulpin") String ulpin
    );


    // =====================================
    // PARCEL SEARCH
    // =====================================

    @GET("parcels/search")
    Call<JsonObject> searchParcels(
            @Query("query") String query
    );


    // =====================================
    // DOCUMENT ANALYSIS
    // =====================================

    @POST("documents/analyze")
    Call<JsonObject> analyzeDocument(
            @Body JsonObject request
    );


    // =====================================
    // AI RISK
    // =====================================

    @GET("parcels/{ulpin}/risk")
    Call<JsonObject> getRiskScore(
            @Path("ulpin") String ulpin
    );


    // =====================================
    // APPLICATIONS
    // =====================================

    @GET("applications")
    Call<JsonObject> getApplications();


    @POST("applications")
    Call<JsonObject> createApplication(
            @Body JsonObject request
    );


    // =====================================
    // NOTIFICATIONS
    // =====================================

    @GET("notifications")
    Call<JsonObject> getNotifications();
}