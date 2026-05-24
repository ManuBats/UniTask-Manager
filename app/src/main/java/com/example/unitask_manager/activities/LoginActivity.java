package com.example.unitask_manager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unitask_manager.R;
import com.example.unitask_manager.data.local.TokenManager;
import com.example.unitask_manager.data.repository.AuthRepository;
import com.example.unitask_manager.models.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etContrasena;
    private MaterialButton btnIniciarSesion, btnIrRegistro;
    private AuthRepository authRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        TokenManager tokenManager = new TokenManager(this);
        authRepo = new AuthRepository(this, tokenManager);

        etEmail = findViewById(R.id.et_login_email);
        etContrasena = findViewById(R.id.et_login_contrasena);
        btnIniciarSesion = findViewById(R.id.btn_login);
        btnIrRegistro = findViewById(R.id.btn_ir_registro);

        btnIniciarSesion.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();
            if (email.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            btnIniciarSesion.setEnabled(false);
            authRepo.login(email, contrasena, new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess(Usuario usuario) {
                    runOnUiThread(() -> {
                        btnIniciarSesion.setEnabled(true);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        btnIniciarSesion.setEnabled(true);
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });

        btnIrRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
