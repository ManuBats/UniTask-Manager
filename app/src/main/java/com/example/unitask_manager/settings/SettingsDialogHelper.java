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
        void onProfileSaved(@NonNull String newName);
    }

    private SettingsDialogHelper() {
    }

    public static void showEditProfileDialog(
            @NonNull Context context,
            @NonNull String currentName,
            @NonNull OnProfileSavedListener listener
    ) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_profile, null);
        TextInputLayout tilNombre = dialogView.findViewById(R.id.til_nombre);
        TextInputEditText etNombre = dialogView.findViewById(R.id.et_nombre);
        etNombre.setText(currentName);
        etNombre.setSelection(currentName.length());

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
                if (newName.isEmpty()) {
                    tilNombre.setError(context.getString(R.string.settings_name_empty));
                    return;
                }
                tilNombre.setError(null);
                listener.onProfileSaved(newName);
                dialog.dismiss();
            });
        });
        dialog.show();
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
