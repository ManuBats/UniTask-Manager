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

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etNombre, etEmail, etContrasena, etConfirmar;
    private MaterialButton btnRegistrar;
    private AuthRepository authRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        TokenManager tokenManager = new TokenManager(this);
        authRepo = new AuthRepository(this, tokenManager);

        etNombre = findViewById(R.id.et_registro_nombre);
        etEmail = findViewById(R.id.et_registro_email);
        etContrasena = findViewById(R.id.et_registro_contrasena);
        etConfirmar = findViewById(R.id.et_registro_confirmar);
        btnRegistrar = findViewById(R.id.btn_registrar);
        MaterialButton btnVolver = findViewById(R.id.btn_volver_login);

        btnVolver.setOnClickListener(v -> finish());

        btnRegistrar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();
            String confirmar = etConfirmar.getText().toString().trim();

            if (nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty() || confirmar.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!contrasena.equals(confirmar)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }
            btnRegistrar.setEnabled(false);
            authRepo.register(nombre, email, contrasena, new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess(Usuario usuario) {
                    runOnUiThread(() -> {
                        btnRegistrar.setEnabled(true);
                        Toast.makeText(RegisterActivity.this,
                                "Registro exitoso", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        btnRegistrar.setEnabled(true);
                        Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });
    }
}
