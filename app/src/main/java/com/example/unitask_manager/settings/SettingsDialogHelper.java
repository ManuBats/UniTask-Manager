package com.example.unitask_manager.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.example.unitask_manager.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AlertDialog;

/**
 * Diálogos reutilizables de la sección Ajustes.
 */
public final class SettingsDialogHelper {

    public interface OnProfileSavedListener {
        void onProfileSaved(@NonNull String newName, @NonNull String newEmail);
    }

    private SettingsDialogHelper() {
    }

    public static void showEditProfileDialog(
            @NonNull Context context,
            @NonNull String currentName,
            @NonNull String currentEmail,
            @NonNull OnProfileSavedListener listener
    ) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_profile, null);
        TextInputLayout tilNombre = dialogView.findViewById(R.id.til_nombre);
        TextInputEditText etNombre = dialogView.findViewById(R.id.et_nombre);
        TextInputLayout tilEmail = dialogView.findViewById(R.id.til_email);
        TextInputEditText etEmail = dialogView.findViewById(R.id.et_email);
        etNombre.setText(currentName);
        etNombre.setSelection(currentName.length());
        etEmail.setText(currentEmail);

        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.settings_edit_profile_title)
                .setView(dialogView)
                .setNegativeButton(R.string.settings_cancel, null)
                .setPositiveButton(R.string.settings_save, null)
                .create();

        dialog.setOnShowListener(d -> {
            MaterialButton saveButton = (MaterialButton) dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            saveButton.setOnClickListener(v -> {
                String newName = etNombre.getText() != null
                        ? etNombre.getText().toString().trim()
                        : "";
                String newEmail = etEmail.getText() != null
                        ? etEmail.getText().toString().trim()
                        : "";
                if (newName.isEmpty()) {
                    tilNombre.setError(context.getString(R.string.settings_name_empty));
                    return;
                }
                if (newEmail.isEmpty()) {
                    tilEmail.setError(context.getString(R.string.settings_email_empty));
                    return;
                }
                tilNombre.setError(null);
                tilEmail.setError(null);
                listener.onProfileSaved(newName, newEmail);
                dialog.dismiss();
            });
        });
        dialog.show();
    }

    public static void showChangePasswordDialog(
            @NonNull Context context,
            @NonNull OnPasswordChangedListener listener
    ) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_profile, null);
        TextInputLayout tilPassword = dialogView.findViewById(R.id.til_nombre);
        tilPassword.setHint(context.getString(R.string.settings_password_hint));
        TextInputEditText etPassword = dialogView.findViewById(R.id.et_nombre);
        etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etPassword.setText("");

        TextInputLayout tilConfirm = dialogView.findViewById(R.id.til_email);
        tilConfirm.setHint(context.getString(R.string.settings_confirm_password_hint));
        TextInputEditText etConfirm = dialogView.findViewById(R.id.et_email);
        etConfirm.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etConfirm.setText("");

        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.settings_change_password_title)
                .setView(dialogView)
                .setNegativeButton(R.string.settings_cancel, null)
                .setPositiveButton(R.string.settings_save, null)
                .create();

        dialog.setOnShowListener(d -> {
            MaterialButton saveButton = (MaterialButton) dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            saveButton.setOnClickListener(v -> {
                String password = etPassword.getText() != null
                        ? etPassword.getText().toString().trim()
                        : "";
                String confirm = etConfirm.getText() != null
                        ? etConfirm.getText().toString().trim()
                        : "";
                if (password.isEmpty()) {
                    tilPassword.setError(context.getString(R.string.settings_password_empty));
                    return;
                }
                if (!password.equals(confirm)) {
                    tilConfirm.setError(context.getString(R.string.settings_password_mismatch));
                    return;
                }
                tilPassword.setError(null);
                tilConfirm.setError(null);
                listener.onPasswordChanged(password);
                dialog.dismiss();
            });
        });
        dialog.show();
    }

    public interface OnPasswordChangedListener {
        void onPasswordChanged(@NonNull String newPassword);
    }

    public static void showMessageDialog(
            @NonNull Context context,
            @StringRes int titleRes,
            @StringRes int messageRes
    ) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_settings_message, null);
        TextView tvMessage = dialogView.findViewById(R.id.tv_dialog_message);
        tvMessage.setText(messageRes);

        new MaterialAlertDialogBuilder(context)
                .setTitle(titleRes)
                .setView(dialogView)
                .setPositiveButton(R.string.settings_understood, null)
                .show();
    }
}
