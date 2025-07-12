package com.example.appprototype;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnWeiter).setOnClickListener(this::weiter);
        findViewById(R.id.btnOhneName).setOnClickListener(this::weiter);
    }

    public void weiter(View view) {
        if (view.getId() == R.id.btnWeiter) {
            handleNameInput();
        } else {
            showToast("Hallo Namenloser!");
            navigateToOverview();
        }
    }

    private void handleNameInput() {
        EditText nameEditText = findViewById(R.id.tfName);
        String nameStr = nameEditText.getText().toString().trim();

        if (nameStr.isEmpty()) {
            showToast("Gib einen Namen ein!");
        } else {
            showToast("Hallo " + nameStr + "!");
            navigateToOverview();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void navigateToOverview() {
        Intent intent = new Intent(this, OverviewActivity.class);
        startActivity(intent);
    }
}
