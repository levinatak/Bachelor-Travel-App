package com.example.appprototype;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Executors;

public class ApiViewModelTest extends ViewModel {

    private final MutableLiveData<String> result = new MutableLiveData<>();

    // Feste Variablen
    private final String projectId = "named-griffin-418513";
    private final String location = "us-central1";
    private final String modelName = "gemini-1.0-pro-002";
    private final String textPrompt = "What is the biggest city in Germany? Answer in one word.";

    // Lock-Objekt für Synchronisation
    private final Object lock = new Object();

    public Object getLock() {
        return lock;
    }

    public LiveData<String> getResult() {
        return result;
    }

    public void fetchTextInput() {
        Log.d("API LOG", "API fetchTextInput");
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String output = textInput(projectId, location, modelName, textPrompt);
                synchronized (lock) {
                    result.postValue(output);
                    lock.notifyAll();
                }
                Log.d("API LOG", "Result: " + result.getValue());
            } catch (Exception e) {
                synchronized (lock) {
                    result.postValue("Error: " + e.getMessage());
                    lock.notifyAll();
                }
                Log.d("API LOG", "Result: " + result.getValue());
            }
        });
    }

    /*
    private String textInput(String projectId, String location, String modelName, String textPrompt) {
        Log.d("API LOG", "textInput started");
        VertexAI vertexAI = null;
        try {
            Log.d("API LOG", "Creating VertexAI instance");
            String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
            if (credentialsPath == null || credentialsPath.isEmpty()) {
                throw new IllegalStateException("GOOGLE_APPLICATION_CREDENTIALS Umgebungsvariable ist nicht gesetzt oder leer.");
            }
            Log.d("API LOG", "GOOGLE_APPLICATION_CREDENTIALS: " + credentialsPath);

            // Prüfen, ob die Datei existiert
            File credentialsFile = new File(credentialsPath);
            if (!credentialsFile.exists()) {
                throw new IllegalStateException("Die Datei existiert nicht: " + credentialsPath);
            }

            // Laden Sie die Anmeldeinformationen
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));

            vertexAI = new VertexAI(projectId, location);
            Log.d("API LOG", "VertexAI instance created");

            GenerativeModel model = new GenerativeModel(modelName, vertexAI);
            Log.d("API LOG", "GenerativeModel instance created");

            GenerateContentResponse response = model.generateContent(textPrompt);
            Log.d("API LOG", "Content generated");

            String responseText = ResponseHandler.getText(response);
            Log.d("API LOG", "Response text: " + responseText);
            return responseText;
        } catch (Exception e) {
            Log.e("API LOG", "Error occurred: " + e.getMessage(), e);
            return "Error: " + e.getMessage();
        } finally {
            if (vertexAI != null) {
                try {
                    vertexAI.close();
                    Log.d("API LOG", "VertexAI instance closed");
                } catch (Exception e) {
                    Log.e("API LOG", "Error closing VertexAI instance: " + e.getMessage(), e);
                }
            }
        }
    }

     */

    private String textInput(String projectId, String location, String modelName, String textPrompt) {
        Log.d("API LOG", "textInput started");
        VertexAI vertexAI = null;
        try {
            Log.d("API LOG", "Creating VertexAI instance");
            vertexAI = new VertexAI(projectId, location);
            Log.d("API LOG", "VertexAI instance created");

            Log.d("API LOG", "Creating GenerativeModel instance");
            GenerativeModel model = new GenerativeModel(modelName, vertexAI);
            Log.d("API LOG", "GenerativeModel instance created");

            Log.d("API LOG", "Generating content with prompt: " + textPrompt);
            GenerateContentResponse response = model.generateContent(textPrompt);
            Log.d("API LOG", "Content generated");

            String responseText = ResponseHandler.getText(response);
            Log.d("API LOG", "Response text: " + responseText);
            return responseText;
        } catch (Exception e) {
            Log.e("API LOG", "Error occurred: " + e.getMessage(), e);
            return "Error: " + e.getMessage();
        } finally {
            if (vertexAI != null) {
                try {
                    vertexAI.close();
                    Log.d("API LOG", "VertexAI instance closed");
                } catch (Exception e) {
                    Log.e("API LOG", "Error closing VertexAI instance: " + e.getMessage(), e);
                }
            }
        }
    }

}
