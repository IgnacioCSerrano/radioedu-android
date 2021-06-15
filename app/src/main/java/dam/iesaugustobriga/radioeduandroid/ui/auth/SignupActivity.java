package dam.iesaugustobriga.radioeduandroid.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dam.iesaugustobriga.radioeduandroid.R;
import dam.iesaugustobriga.radioeduandroid.common.Constants;
import dam.iesaugustobriga.radioeduandroid.common.MyApp;
import dam.iesaugustobriga.radioeduandroid.data.VolleySingleton;
import dam.iesaugustobriga.radioeduandroid.databinding.ActivitySignupBinding;
import dam.iesaugustobriga.radioeduandroid.models.Centro;
import dam.iesaugustobriga.radioeduandroid.models.UsuarioReq;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private ActivitySignupBinding binding;
    
    private EditText etUsername, etEmail, etNombre, etApellidos, etPassword, etConfirmPassword;
    private TextView tvGoLogin;
    private ImageView ivShowPassw, ivShowPasswConf;
    private Spinner spinnerProv, spinnerLoc, spinnerCent;
    private CheckBox cbStudent;
    private Button btnSignup;
    private ProgressBar progressBar;
    private VolleySingleton volley;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        findViews();
        bindEvents();

        volley = VolleySingleton.getInstance();
        resetSpinners();
    }

    @Override
    public void onBackPressed() {
        goIntent(new Intent(SignupActivity.this, LoginActivity.class));
    }

    private class MyTextWatcher implements TextWatcher {
        private final EditText et;
        private final ImageView iv;
        private String textBefore;
        private String textAfter;

        private MyTextWatcher(EditText et, ImageView iv) {
            this.et = et;
            this.iv = iv;
        }

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
            if (et.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance()) && !textBefore.equals(textAfter)) {
                togglePasswordVisibility(et, iv,false);
            }
        }
    }

    private void findViews() {
        etUsername = binding.editTextUsername;
        etEmail = binding.editTextEmail;
        etNombre = binding.editTextName;
        etApellidos = binding.editTextSurname;
        etPassword = binding.editTextPassword;
        etConfirmPassword = binding.editTextConfirmPassword;
        ivShowPassw = binding.imageViewShowPassw;
        ivShowPasswConf = binding.imageViewShowPasswConf;
        spinnerProv = binding.spinnerProv;
        spinnerLoc = binding.spinnerLoc;
        spinnerCent = binding.spinnerCent;
        cbStudent = binding.checkBoxStudent;
        btnSignup = binding.buttonSignup;
        progressBar = binding.progressBar;
        tvGoLogin = binding.textViewGoLogin;
    }

    private void bindEvents() {
        cbStudent.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        ivShowPassw.setOnClickListener(this);
        ivShowPasswConf.setOnClickListener(this);
        tvGoLogin.setOnClickListener(this);
        spinnerProv.setOnItemSelectedListener(this);
        spinnerLoc.setOnItemSelectedListener(this);
        etPassword.addTextChangedListener(new MyTextWatcher(etPassword, ivShowPassw));
        etConfirmPassword.addTextChangedListener(new MyTextWatcher(etConfirmPassword, ivShowPasswConf));
    }

    private void toggleSpinnersVisibility(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        spinnerProv.setVisibility(visibility);
        spinnerLoc.setVisibility(visibility);
        spinnerCent.setVisibility(visibility);
        if (!show) {
            resetSpinners();
        }
    }

    private void toggleProgressVisivility(boolean startProcess) {
        if (startProcess) {
            btnSignup.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSignup.setVisibility(View.VISIBLE);
        }
    }

    private void togglePasswordVisibility(EditText et, ImageView iv, boolean show) {
        if (show) {
            iv.setImageResource(R.drawable.ic_close);
            et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            iv.setImageResource(R.drawable.ic_remove_red_eye);
            et.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        et.setSelection(et.getText().length());
    }

    private boolean checkForm(UsuarioReq u) {
        boolean correcto = true;
        if (u.getPassword().trim().isEmpty()) {
            etPassword.setError("¡Campo obligatorio!");
            etPassword.requestFocus();
            correcto = false;
        } else if (u.getPassword().length() < 6) {
            etPassword.setError("¡Contraseña debe tener al menos 6 caracteres!");
            etPassword.requestFocus();
            correcto = false;
        } else if (!u.getConfirmPassword().equals(u.getPassword())) {
            etConfirmPassword.setError("¡Ambas contraseñas no coinciden!");
            etConfirmPassword.requestFocus();
            correcto = false;
        }
        if (u.getApellidos().trim().isEmpty()) {
            etApellidos.setError("¡Campo obligatorio!");
            etApellidos.requestFocus();
            correcto = false;
        }
        if (u.getNombre().trim().isEmpty()) {
            etNombre.setError("¡Campo obligatorio!");
            etNombre.requestFocus();
            correcto = false;
        }
        if (u.getEmail().trim().isEmpty()) {
            etEmail.setError("¡Campo obligatorio!");
            etEmail.requestFocus();
            correcto = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(u.getEmail()).matches()) {
            etEmail.setError("¡Correo electrónico no válido!");
            etEmail.requestFocus();
            correcto = false;
        }
        if (u.getUsername().trim().isEmpty()) {
            etUsername.setError("¡Campo obligatorio!");
            etUsername.requestFocus();
            correcto = false;
        }
        return correcto;
    }

    private void insertUser(UsuarioReq u) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.AUTH_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            intent.putExtra("message", "Se ha enviado un enlace de verificación a la dirección " + u.getEmail() + ".");
                            goIntent(intent);
                        } else {
                            MyApp.showSnackbar(this, findViewById(R.id.layout),
                                    jsonObject.getString("message"), Snackbar.LENGTH_LONG);
                            toggleProgressVisivility(false);
                        }
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                        MyApp.showSnackbar(this, findViewById(R.id.layout), Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                        toggleProgressVisivility(false);
                    }
                }, error -> {
                    Log.e("ERROR", error.getMessage());
                    MyApp.showSnackbar(this, findViewById(R.id.layout), Constants.SERVER_FAIL, Snackbar.LENGTH_LONG);
                    toggleProgressVisivility(false);
                }
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("username", u.getUsername());
                params.put("password", u.getPassword());
                params.put("email", u.getEmail());
                params.put("nombre", u.getNombre());
                params.put("apellidos", u.getApellidos());
                if (u.getCodigoCentro() != 0) {
                    params.put("codigo-centro", String.valueOf(u.getCodigoCentro()));
                }
                params.put("signup", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

    private void signup() {
        UsuarioReq user = new UsuarioReq(
            etUsername.getText().toString(),
            etPassword.getText().toString(),
            etConfirmPassword.getText().toString(),
            etEmail.getText().toString(),
            etNombre.getText().toString(),
            etApellidos.getText().toString(),
            spinnerCent.getSelectedItem() == null
                    ? 0 : ((Centro) spinnerCent.getSelectedItem()).getCodigo()
        );

        if (checkForm(user)) {
            toggleProgressVisivility(true);
            insertUser(user);
        }
    }

    private void goIntent(Intent intent) {
        startActivity(intent);
        this.finish();
    }

    private void resetSpinners() {
        spinnerLoc.setAdapter(null);
        spinnerCent.setAdapter(null);
        volley.populateStringSpinner(spinnerProv, Constants.SCHOOL_URL, "provincia",
                "--selecciona provincia--");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int parentId = parent.getId();
        if (parentId == R.id.spinnerProv) {
            String provincia = (String) parent.getItemAtPosition(position);
            volley.populateStringSpinner(spinnerLoc, String.format("%s?%s=%s", Constants.SCHOOL_URL, Constants.SCHOOL_PARAM_PROV, provincia),
                    "localidad", "--selecciona localidad--");
        } else if (parentId == R.id.spinnerLoc) {
            String localidad = (String) parent.getItemAtPosition(position);
            volley.populateCentroSpinner(spinnerCent, String.format("%s?%s=%s", Constants.SCHOOL_URL, Constants.SCHOOL_PARAM_LOC, localidad),
                    "--selecciona centro--");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imageViewShowPassw) {
            togglePasswordVisibility(etPassword, ivShowPassw, etPassword.getTransformationMethod()
                    .equals(PasswordTransformationMethod.getInstance()));
        } else if (id == R.id.imageViewShowPasswConf) {
            togglePasswordVisibility(etConfirmPassword, ivShowPasswConf, etConfirmPassword.getTransformationMethod()
                    .equals(PasswordTransformationMethod.getInstance()));
        } else if (id == R.id.checkBoxStudent) {
            toggleSpinnersVisibility(cbStudent.isChecked());
        } else if (id == R.id.buttonSignup) {
            signup();
        } else if (id == R.id.textViewGoLogin) {
            goIntent(new Intent(SignupActivity.this, LoginActivity.class));
        }
    }

}