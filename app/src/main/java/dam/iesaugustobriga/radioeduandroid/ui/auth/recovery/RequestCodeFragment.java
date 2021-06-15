package dam.iesaugustobriga.radioeduandroid.ui.auth.recovery;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dam.iesaugustobriga.radioeduandroid.R;
import dam.iesaugustobriga.radioeduandroid.common.Constants;
import dam.iesaugustobriga.radioeduandroid.common.MyApp;
import dam.iesaugustobriga.radioeduandroid.data.VolleySingleton;
import dam.iesaugustobriga.radioeduandroid.databinding.FragmentRequestCodeBinding;

public class RequestCodeFragment extends Fragment {

    private FragmentRequestCodeBinding binding;

    private EditText etEmail;
    private Button btnReqCode;
    private ProgressBar progressBar;

    private VolleySingleton volley;

    public static RequestCodeFragment newInstance(String mensaje) {
        RequestCodeFragment fragment = new RequestCodeFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ARG_MESSAGE, mensaje);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRequestCodeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        assert getArguments() != null;
        String mensaje = getArguments().getString(Constants.ARG_MESSAGE);
        if (mensaje != null) {
            MyApp.showSnackbar(requireActivity(), requireActivity()
                    .findViewById(R.id.container), mensaje, Snackbar.LENGTH_LONG);
        }

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

    private void findViews() {
        etEmail = binding.editTextEmailRecovery;
        btnReqCode = binding.buttonRequestCode;
        progressBar = binding.progressBar;
    }

    private void bindEvents() {
        btnReqCode.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            if (email.trim().isEmpty()) {
                etEmail.setError("¡Escribe tu dirección de correo!");
                etEmail.requestFocus();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("¡Correo electrónico no válido!");
                etEmail.requestFocus();
            } else {
                requestCode(email);
            }
        });
    }

    public void toggleVisivilityProgress(boolean startProcess) {
        if (startProcess) {
            btnReqCode.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            btnReqCode.setVisibility(View.VISIBLE);
        }
    }

    private void requestCode(String email) {
        toggleVisivilityProgress(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.AUTH_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            requireActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, ValidateCodeFragment.newInstance(email))
                                    .commit();
                        } else {
                            MyApp.showSnackbar(requireActivity(), requireActivity()
                                    .findViewById(R.id.container), jsonObject.getString("message"), Snackbar.LENGTH_LONG);
                            toggleVisivilityProgress(false);
                        }
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                        MyApp.showSnackbar(requireActivity(), requireActivity()
                                .findViewById(R.id.container), Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                        toggleVisivilityProgress(false);
                    }
                }, error -> {
                    Log.e("ERROR", error.getMessage());
                    MyApp.showSnackbar(requireActivity(), requireActivity()
                            .findViewById(R.id.container), Constants.SERVER_FAIL, Snackbar.LENGTH_LONG);
                    toggleVisivilityProgress(false);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("check-email", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

}