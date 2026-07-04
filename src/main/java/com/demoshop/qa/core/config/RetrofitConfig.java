package com.demoshop.qa.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

@Configuration
public class RetrofitConfig {

    @Autowired
    private AppConfig appConfig;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    public OkHttpClient baseOkHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(new AllureOkHttp3())
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    public Retrofit retrofit(OkHttpClient baseOkHttpClient, ObjectMapper objectMapper) {
        return new Retrofit.Builder()
                .baseUrl(appConfig.getBaseUrl() + "/")
                .client(baseOkHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();
    }

    /**
     * Creates a Retrofit instance that attaches Bearer token on every request.
     */
    public Retrofit authenticatedRetrofit(String token) {
        Interceptor authInterceptor = chain -> {
            Request request = chain.request().newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(request);
        };

        OkHttpClient authClient = baseOkHttpClient().newBuilder()
                .addInterceptor(authInterceptor)
                .build();

        return new Retrofit.Builder()
                .baseUrl(appConfig.getBaseUrl() + "/")
                .client(authClient)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper()))
                .build();
    }

    public <T> T createService(Class<T> serviceClass) {
        return retrofit(baseOkHttpClient(), objectMapper()).create(serviceClass);
    }

    public <T> T createAuthenticatedService(Class<T> serviceClass, String token) {
        return authenticatedRetrofit(token).create(serviceClass);
    }
}
