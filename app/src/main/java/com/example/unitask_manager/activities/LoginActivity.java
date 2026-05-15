package com.example.unitask_manager.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.unitask_manager.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etContrasena;
    private MaterialButton btnIniciarSesion, btnIrRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_login_email);
        etContrasena = findViewById(R.id.et_login_contrasena);
        btnIniciarSesion = findViewById(R.id.btn_login);
        btnIrRegistro = findViewById(R.id.btn_ir_registro);

        btnIniciarSesion.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();
            if (email.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Inicio de sesión — Pendiente conectar DB", Toast.LENGTH_SHORT).show();
            }
        });

        btnIrRegistro.setOnClickListener(v ->
                Toast.makeText(this, "Ir a registro — Pendiente", Toast.LENGTH_SHORT).show());
    }
}
