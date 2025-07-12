package com.example.appprototype;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appprototype.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiViewModel extends ViewModel {
    private MutableLiveData<String> output = new MutableLiveData<>();
    //private static final String BASE_URL = "https://us-central1-aiplatform.googleapis.com/v1";
    // BASE_URL für Europa, hier: europe-west1 (Belgien)
    private static final String BASE_URL = "https://europe-west1-aiplatform.googleapis.com/v1";


    public MutableLiveData<String> init(Context context) {
        Log.d("API LOG", "API fun init");
        String textPrompt = "What is the biggest city in Germany? Answer in one word.";
        new LoadCredentialsTask(context, textPrompt).execute();
        Log.d("API LOG", "API init abgeschlossen");
        return output;
    }

    private class LoadCredentialsTask extends AsyncTask<Void, Void, String> {
        private Context context;
        private String textPrompt;

        LoadCredentialsTask(Context context, String textPrompt) {
            Log.d("API LOG", "API LoadCredentialsTask Konstruktor");
            this.context = context;
            this.textPrompt = textPrompt;
        }

        @Override
        protected String doInBackground(Void... voids) {
            Log.d("API LOG", "API Hintergrund-Task gestartet");
            try {
                // Lese die JSON Datei mit den Anmeldeinformationen
                Log.d("API LOG", "Lesen der JSON Datei");
                InputStream is = context.getResources().openRawResource(R.raw.application_default_credentials);
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                String credentialsJson = new String(buffer, StandardCharsets.UTF_8);

                // Konvertiere JSON zu einem Map
                Log.d("API LOG", "Konvertieren der JSON Datei");
                Gson gson = new Gson();
                JsonObject credentials = gson.fromJson(credentialsJson, JsonObject.class);

                // Extrahiere den access_token (z.B. via OAuth 2.0 Flow) - simuliert hier
                Log.d("API LOG", "Extrahieren des Access Token");
                String accessToken = fetchAccessToken(credentials);

                // Führe den API-Aufruf aus
                Log.d("API LOG", "API-Aufruf");
                return callVertexAiApi(accessToken, textPrompt);
            } catch (IOException e) {
                e.printStackTrace();
                return "Error loading credentials";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            output.setValue(result);
        }
    }

    private String fetchAccessToken(JsonObject credentials) throws IOException {
        Log.d("API LOG", "fun fetchAccessToken");
        String privateKey = credentials.get("private_key").getAsString();
        Log.d("API LOG", "Private Key: " + privateKey);
        String clientEmail = credentials.get("client_email").getAsString();
        Log.d("API LOG", "Client Email: " + clientEmail);

        // Erstelle JWT für den Zugriff
        Log.d("API LOG", "Erstelle JWT");
        String jwtToken = createJwtToken(clientEmail, privateKey);
        if (jwtToken == null) {
            throw new IOException("JWT Erstellung fehlgeschlagen");
        }
        Log.d("API LOG", "jwtToken: " + jwtToken);

        // Tausche JWT gegen ein OAuth2.0 Token
        Log.d("API LOG", "Tausche JWT");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                .add("assertion", jwtToken)
                .build();

        Log.d("API LOG", "New request builder");
        Request request = new Request.Builder()
                .url("https://oauth2.googleapis.com/token")
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            Log.d("API LOG", "Response Body: " + responseBody);
            if (response.isSuccessful()) {
                Log.d("API LOG", "Access Token erfolgreich");
                //String responseBody = response.body().string();
                JsonObject jsonResponse = new Gson().fromJson(responseBody, JsonObject.class);
                Log.d("API LOG", "fun end fetchAccessToken");
                return jsonResponse.get("access_token").getAsString();
            } else {
                Log.d("API LOG", "Access Token fehlgeschlagen: " + response.message());
                throw new IOException("Failed to get access token: " + response.message());
            }
        }
    }


    private String createJwtToken(String clientEmail, String privateKey) {
        Log.d("API LOG", "fun createJwtToken");
        long nowMillis = System.currentTimeMillis() / 1000; // Epoch in seconds
        long expMillis = nowMillis + 3600; // 1 Stunde Gültigkeit
        String header = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"RS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        Log.d("API LOG", "JWT Header: " + header);
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString((
                "{\"iss\":\"" + clientEmail + "\"," +
                        "\"scope\":\"https://www.googleapis.com/auth/cloud-platform\"," +
                        "\"aud\":\"https://oauth2.googleapis.com/token\"," +
                        "\"exp\":" + expMillis + "," +
                        "\"iat\":" + nowMillis + "}").getBytes(StandardCharsets.UTF_8));
        Log.d("API LOG", "JWT Payload: " + payload);
        String signature = createRsaSignature(header + "." + payload, privateKey);
        if (signature == null) {
            Log.d("API LOG", "JWT Signature Erstellung fehlgeschlagen");
            return null; // oder geeignete Fehlerbehandlung
        }
        Log.d("API LOG", "JWT Signature: " + signature);
        Log.d("API LOG", "fun end createJwtToken");
        return header + "." + payload + "." + signature;
    }

    private String createRsaSignature(String data, String privateKey) {
        Log.d("API LOG", "fun createRsaSignature");
        try {
            // Entfernen der PEM-Header und -Footer sowie Whitespaces
            privateKey = privateKey.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            // Base64-Dekodierung des Schlüssels
            byte[] keyBytes = Base64.getDecoder().decode(privateKey);

            // Erstellen des PrivateKey Objekts
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey key = keyFactory.generatePrivate(spec);

            // Signatur-Objekt initialisieren und Daten signieren
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(key);
            signature.update(data.getBytes(StandardCharsets.UTF_8));

            // Base64-encoding der Signatur ohne Padding
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signature.sign());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
            Log.d("API LOG", "createRsaSignature fehlgeschlagen: " + e.getMessage());
            e.printStackTrace();
            return null; // oder eine geeignete Fehlermeldung
        }
    }


/*
    private String callVertexAiApi(String accessToken, String textPrompt) throws IOException {
        Log.d("API LOG", "fun callVertexAiApi");
        OkHttpClient client = new OkHttpClient();
        String url = BASE_URL + "/projects/named-griffin-418513/locations/us-central1/models/gemini-1.0-pro-002:predict";

        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("prompt", textPrompt);

        Gson gson = new Gson();
        String jsonBody = gson.toJson(bodyMap);
        Log.d("API LOG", "JSON Body: " + jsonBody);

        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                //.url(url)
                .url(BASE_URL + "/projects/named-griffin-418513/locations/us-central1/publishers/google/models/text-bison-001:predict")
                .post(body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            Log.d("API LOG", "Response Body: " + responseBody);
            Log.d("API LOG", "Status Code: " + response.code());
            Log.d("API LOG", "Headers: " + response.headers().toString());
            if (response.isSuccessful()) {
                Log.d("API LOG", "VertexAi call erfolgreich");
                return response.body().string();
            } else {
                Log.d("API LOG", "VertexAi call fehlgeschlagen");
                return "Request failed with code: " + response.code();
            }
        }
    }

 */

    private String callVertexAiApi(String accessToken, String textPrompt) throws IOException {
        OkHttpClient client = new OkHttpClient();
        // Korrekte JSON-Struktur für die Anfrage
        String jsonRequest = "{\"instances\": [{\"content\": \"" + textPrompt + "\"}]}";

        RequestBody body = RequestBody.create(
                jsonRequest,
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                //.url(BASE_URL + "/projects/named-griffin-418513/locations/europe-west1/models/text-bison-001:predict")
                .url(BASE_URL + "/projects/named-griffin-418513/locations/europe-west1/models/gemini-1.0-pro-002:predict")
                .post(body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            Log.d("API LOG", "Response Body: " + responseBody);
            Log.d("API LOG", "Status Code: " + response.code());
            Log.d("API LOG", "Headers: " + response.headers().toString());
            if (response.isSuccessful()) {
                return responseBody;
            } else {
                Log.d("API LOG", "VertexAi call fehlgeschlagen: " + response.message());
                return "Error: " + response.message();
            }
        }
    }



}
