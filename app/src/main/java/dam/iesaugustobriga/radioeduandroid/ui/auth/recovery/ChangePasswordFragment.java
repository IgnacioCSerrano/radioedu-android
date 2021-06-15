package dam.iesaugustobriga.radioeduandroid.ui.auth.recovery;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dam.iesaugustobriga.radioeduandroid.R;
import dam.iesaugustobriga.radioeduandroid.common.Constants;
import dam.iesaugustobriga.radioeduandroid.common.MyApp;
import dam.iesaugustobriga.radioeduandroid.data.VolleySingleton;
import dam.iesaugustobriga.radioeduandroid.databinding.FragmentPasswChangeBinding;
import dam.iesaugustobriga.radioeduandroid.ui.auth.LoginActivity;

public class ChangePasswordFragment extends Fragment {

    private FragmentPasswChangeBinding binding;

    private EditText etPassword, etConfirmPassword;
    private ImageView ivShowPassw, ivShowPasswConf;
    private Button btnChangePassw;
    private ProgressBar progressBar;

    private VolleySingleton volley;
    private String password, confirmPassword, email;

    public static ChangePasswordFragment newInstance(String email) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString(Constants.ARG_EMAIL);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPasswChangeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        findViews();
        bindEvents();

        volley = VolleySingleton.getInstance();
        
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
        etPassword = binding.editTextPassword;
        etConfirmPassword = binding.editTextConfirmPassword;
        ivShowPassw = binding.imageViewShowPassw;
        ivShowPasswConf = binding.imageViewShowPasswConf;
        btnChangePassw = binding.buttonChangePassw;
        progressBar = binding.progressBar;
    }

    private void bindEvents() {
        ivShowPassw.setOnClickListener(v-> togglePasswordVisibility(etPassword, ivShowPassw,
                etPassword.getTransformationMethod()
                        .equals(PasswordTransformationMethod.getInstance())));

        ivShowPasswConf.setOnClickListener(v -> togglePasswordVisibility(etConfirmPassword, ivShowPasswConf,
                etConfirmPassword.getTransformationMethod()
                        .equals(PasswordTransformationMethod.getInstance())));

        etPassword.addTextChangedListener(new MyTextWatcher(etPassword, ivShowPassw));
        etConfirmPassword.addTextChangedListener(new MyTextWatcher(etConfirmPassword, ivShowPasswConf));

        btnChangePassw.setOnClickListener(v -> {
            password = etPassword.getText().toString();
            confirmPassword = etConfirmPassword.getText().toString();
            if (checkForm(password, confirmPassword)) {
                changePassword(email);
            }
        });
    }

    private void toggleProgressVisivility(boolean startProcess) {
        if (startProcess) {
            btnChangePassw.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            btnChangePassw.setVisibility(View.VISIBLE);
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

    private boolean checkForm(String passw, String passwConf) {
        boolean correcto = true;
        if (passw.trim().isEmpty()) {
            etPassword.setError("¡Campo obligatorio!");
            etPassword.requestFocus();
            correcto = false;
        } else if (passw.length() < Constants.PASSW_LENGTH) {
            etPassword.setError("¡Contraseña debe tener al menos " + Constants.PASSW_LENGTH + " caracteres!");
            etPassword.requestFocus();
            correcto = false;
        } else if (!passwConf.equals(passw)) {
            etConfirmPassword.setError("¡Ambas contraseñas no coinciden!");
            etConfirmPassword.requestFocus();
            correcto = false;
        }
        return correcto;
    }

    private void changePassword(String email) {
        toggleProgressVisivility(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.AUTH_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.putExtra("message", "Contraseña modificada correctamente. Puede usarla ahora para iniciar sesión.");
                            startActivity(intent);
                            requireActivity().finish();
                        } else {
                            MyApp.showSnackbar(requireActivity(), requireActivity().findViewById(R.id.container), Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                            toggleProgressVisivility(false);
                        }
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                        MyApp.showSnackbar(requireActivity(), requireActivity().findViewById(R.id.container), Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                        toggleProgressVisivility(false);
                    }
                }, error -> {
                    Log.e("ERROR", error.getMessage());
            MyApp.showSnackbar(requireActivity(), requireActivity().findViewById(R.id.container), Constants.SERVER_FAIL, Snackbar.LENGTH_LONG);
                    toggleProgressVisivility(false);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("new-password", password);
                params.put("confirm-password", confirmPassword);
                params.put("email", email);
                params.put("change-password", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

}