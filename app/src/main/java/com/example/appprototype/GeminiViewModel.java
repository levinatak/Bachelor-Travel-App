package com.example.appprototype;

import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiViewModel extends ViewModel {

    private static final String TAG = "API LOG";
    private final MutableLiveData<String> responseLiveData = new MutableLiveData<>();
    private final MutableLiveData<LoadState> loadState = new MutableLiveData<>(LoadState.IDLE);
    private final Executor executor = Executors.newSingleThreadExecutor();

    private RouteViewModel rVM = new RouteViewModel();

    public enum LoadState {
        IDLE, LOADING, LOADED, FAILED
    }

    public LiveData<String> getResult() {
        return responseLiveData;
    }

    public LiveData<LoadState> getLoadState() {
        return loadState;
    }

    public void fetchGeminiResponse(String promptText) {
        Log.d(TAG, "Starting fetchGeminiResponse");

        // Setze den Ladezustand auf LOADING
        loadState.postValue(LoadState.LOADING);

        GenerativeModel gm = new GenerativeModel("gemini-1.5-pro", BuildConfig.apiKey);
        Log.d(TAG, "API Key: " + BuildConfig.apiKey);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText(promptText)
                .build();

        Log.d(TAG, "Content created: " + promptText);

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                handleSuccess(result.getText());
            }

            @Override
            public void onFailure(Throwable t) {
                handleError(t);
            }
        }, executor);
    }

    private void handleSuccess(String resultText) {
        Log.d(TAG, "API call successful. Result: " + resultText);
        responseLiveData.postValue(resultText);
        loadState.postValue(LoadState.LOADED);
        Log.d(TAG, "Response posted to LiveData and state set to LOADED.");
    }

    private void handleError(Throwable t) {
        Log.e(TAG, "API call failed", t);
        responseLiveData.postValue("Error: " + t.getMessage());
        loadState.postValue(LoadState.FAILED);
        Log.d(TAG, "Error response posted to LiveData and state set to FAILED.");
    }

    public void clearResult() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            responseLiveData.setValue(null);
            loadState.setValue(LoadState.IDLE);
        } else {
            responseLiveData.postValue(null);
            loadState.postValue(LoadState.IDLE);
        }
    }
}
