package dam.iesaugustobriga.radioeduandroid.ui.auth.recovery;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import dam.iesaugustobriga.radioeduandroid.databinding.FragmentValidateCodeBinding;

public class ValidateCodeFragment extends Fragment {

    private FragmentValidateCodeBinding binding;

    private TextView twWriteCode;
    private EditText etCode;
    private Button btnValCode;
    private ProgressBar progressBar;

    private VolleySingleton volley;
    private String email;

    public static ValidateCodeFragment newInstance(String email) {
        ValidateCodeFragment fragment = new ValidateCodeFragment();
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
        binding = FragmentValidateCodeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        findViews();
        bindEvents();

        twWriteCode.setText(String.format("%s %s", twWriteCode.getText(), email));

        volley = VolleySingleton.getInstance();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void findViews() {
        twWriteCode = binding.textViewWriteCode;
        etCode = binding.editTextCode;
        btnValCode = binding.buttonValidateCode;
        progressBar = binding.progressBar;
    }

    private void bindEvents() {
        btnValCode.setOnClickListener(v -> {
            String code = etCode.getText().toString();
            if (code.trim().isEmpty()) {
                etCode.setError("¡Escribe un valor en el campo!");
                etCode.requestFocus();
            } else if (code.length() != Constants.CODE_LENGTH) {
                etCode.setError("¡El código debe tener " + Constants.CODE_LENGTH + " dígitos!");
                etCode.requestFocus();
            } else {
                validateCode(code);
            }
        });
    }

    public void toggleProgressVisivility(boolean startProcess) {
        if (startProcess) {
            btnValCode.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            btnValCode.setVisibility(View.VISIBLE);
        }
    }

    private void validateCode(String code) {
        toggleProgressVisivility(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.AUTH_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            requireActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, ChangePasswordFragment.newInstance(email))
                                    .commit();
                        } else {
                            requireActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, RequestCodeFragment
                                            .newInstance("Código incorrecto. Vuelva a solicitar uno nuevo."))
                                    .commit();
                        }
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                        MyApp.showSnackbar(requireActivity(), requireActivity()
                                .findViewById(R.id.container), Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                        toggleProgressVisivility(false);
                    }
                }, error -> {
                    Log.e("ERROR", error.getMessage());
                    MyApp.showSnackbar(requireActivity(), requireActivity()
                            .findViewById(R.id.container), Constants.SERVER_FAIL, Snackbar.LENGTH_LONG);
                    toggleProgressVisivility(false);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("code", code);
                params.put("email", email);
                params.put("check-code", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

}