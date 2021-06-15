package dam.iesaugustobriga.radioeduandroid.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dam.iesaugustobriga.radioeduandroid.R;
import dam.iesaugustobriga.radioeduandroid.common.Constants;
import dam.iesaugustobriga.radioeduandroid.common.MyApp;
import dam.iesaugustobriga.radioeduandroid.common.SharedPreferencesManager;
import dam.iesaugustobriga.radioeduandroid.data.VolleySingleton;
import dam.iesaugustobriga.radioeduandroid.databinding.ActivityLoginBinding;
import dam.iesaugustobriga.radioeduandroid.ui.dashboard.DashboardActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityLoginBinding binding;

    private EditText etHandle, etPassword;
    private Button btnLogin;
    private ImageView ivShowPassw;
    private TextView tvGoSignup, tvForgotPassw;
    private ProgressBar progressBar;
    private VolleySingleton volley;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            MyApp.showSnackbar(this, findViewById(R.id.layout),
                    extras.getString("message"), Snackbar.LENGTH_LONG);
        }

        findViews();
        bindEvents();

        volley = VolleySingleton.getInstance();
    }

    private void findViews() {
        etHandle = binding.editTextHandle;
        etPassword = binding.editTextPassword;
        ivShowPassw = binding.imageViewShowPassw;
        btnLogin = binding.buttonLogin;
        progressBar = binding.progressBar;
        tvGoSignup = binding.textViewGoSignup;
        tvForgotPassw = binding.textViewForgotPassw;
    }

    private void bindEvents() {
        ivShowPassw.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        tvGoSignup.setOnClickListener(this);
        tvForgotPassw.setOnClickListener(this);

        etPassword.addTextChangedListener(new TextWatcher() {
            String textBefore, textAfter;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textBefore = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textAfter = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance()) && !textBefore.equals(textAfter)) {
                    togglePasswordVisibility(etPassword, false);
                }
            }
        });
    }

    private boolean checkForm(String handle, String password) {
        boolean correcto = true;
        if (password.trim().isEmpty()) {
            etPassword.setError("¡Campo obligatorio!");
            etPassword.requestFocus();
            correcto = false;
        }
        if (handle.trim().isEmpty()) {
            etHandle.setError("¡Campo obligatorio!");
            etHandle.requestFocus();
            correcto = false;
        }
        return correcto;
    }

    private void toggleProgressVisivility(boolean startProcess) {
        if (startProcess) {
            btnLogin.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
        }
    }

    private void togglePasswordVisibility(EditText et, boolean show) {
        if (show) {
            ivShowPassw.setImageResource(R.drawable.ic_close);
            et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            ivShowPassw.setImageResource(R.drawable.ic_remove_red_eye);
            et.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        et.setSelection(et.getText().length());
    }

    private void login() {
        String handle = etHandle.getText().toString();
        String password = etPassword.getText().toString();
        if (checkForm(handle, password)) {
            toggleProgressVisivility(true);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.AUTH_URL,
                    response -> {
                        Log.i("RESPONSE", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {
                                JSONObject user = jsonObject.getJSONObject("user");
                                SharedPreferencesManager.setPreferences(user);
                                storeFirebaseToken(user.getString("id"));
                                goIntent(new Intent(LoginActivity.this, DashboardActivity.class));
                            } else {
                                MyApp.showSnackbar(this, findViewById(R.id.layout),
                                        jsonObject.getString("message"), Snackbar.LENGTH_LONG);
                                toggleProgressVisivility(false);
                            }
                        } catch (JSONException e) {
                            Log.e("ERROR", e.getMessage());
                            MyApp.showSnackbar(this, findViewById(R.id.layout),
                                    Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                            toggleProgressVisivility(false);
                        }
                    }, error -> {
                        Log.e("ERROR", error.getMessage());
                        MyApp.showSnackbar(this, findViewById(R.id.layout),
                                Constants.SERVER_FAIL, Snackbar.LENGTH_LONG);
                        toggleProgressVisivility(false);
                    }
            ) {
                @Override
                protected Map<String,String> getParams() {
                    Map<String,String> params = new HashMap<>();
                    params.put("handle", handle);
                    params.put("password", password);
                    params.put("login", "true");
                    return params;
                }
            };
            stringRequest.setTag(Constants.TAG);
            volley.addToRequestQueue(stringRequest);
        }
    }

    private void updateToken(String id, String token) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.AUTH_URL,
                response -> Log.i("RESPONSE", response),
                error -> Log.e("ERROR", error.getMessage())
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("id-sub", id);
                params.put("fb-token", token);
                params.put("store-fb-token", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

    private void storeFirebaseToken(String id) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("ERROR", "Error al recuperar token de registro FCM",
                                task.getException());
                        return;
                    }
                    updateToken(id, task.getResult()); // token de registro FCM
                });

        // Crear canal de notificación para dispositivos de versión superior o igual a la actual

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID,
                    Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(Constants.CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void goIntent(Intent intent) {
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imageViewShowPassw) {
            togglePasswordVisibility(etPassword, etPassword.getTransformationMethod()
                    .equals(PasswordTransformationMethod.getInstance()));
        } else if (id == R.id.buttonLogin) {
            login();
        } else if (id == R.id.textViewGoSignup) {
            goIntent(new Intent(LoginActivity.this, SignupActivity.class));
        } else if (id == R.id.textViewForgotPassw) {
            goIntent(new Intent(LoginActivity.this, PasswordResetActivity.class));
        }
    }

}