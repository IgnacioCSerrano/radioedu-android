package dam.iesaugustobriga.radioeduandroid.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import java.util.Objects;

import dam.iesaugustobriga.radioeduandroid.R;
import dam.iesaugustobriga.radioeduandroid.common.MyApp;
import dam.iesaugustobriga.radioeduandroid.databinding.ActivityPasswordResetBinding;
import dam.iesaugustobriga.radioeduandroid.ui.auth.recovery.RequestCodeFragment;

public class PasswordResetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPasswordResetBinding binding = ActivityPasswordResetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        Button btnBack = binding.buttonBack;
        btnBack.setOnClickListener(v -> showAlert());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, RequestCodeFragment.newInstance(null))
                    .commitNow();
        }
    }

    @Override
    public void onBackPressed() {
        showAlert();
    }

    private void showAlert() {
        MyApp.buildAlertDialogClose(this,
                new Intent(PasswordResetActivity.this, LoginActivity.class),
                "¿Estás seguro de que deseas cancelar el proceso de recuperación de contraseña?");
    }

}