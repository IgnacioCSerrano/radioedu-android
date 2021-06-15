package dam.iesaugustobriga.radioeduandroid.ui.dashboard.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import dam.iesaugustobriga.radioeduandroid.common.Constants;
import dam.iesaugustobriga.radioeduandroid.common.MyApp;
import dam.iesaugustobriga.radioeduandroid.common.SharedPreferencesManager;
import dam.iesaugustobriga.radioeduandroid.data.ProfileViewModel;
import dam.iesaugustobriga.radioeduandroid.data.RadioViewModel;
import dam.iesaugustobriga.radioeduandroid.data.VolleySingleton;
import dam.iesaugustobriga.radioeduandroid.databinding.FragmentProfileBinding;
import dam.iesaugustobriga.radioeduandroid.R;
import dam.iesaugustobriga.radioeduandroid.models.Centro;
import dam.iesaugustobriga.radioeduandroid.models.UsuarioReq;
import dam.iesaugustobriga.radioeduandroid.models.UsuarioRes;

public class ProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private FragmentProfileBinding binding;

    private ImageView ivAvatar, ivShowCurPassw, ivShowNewPassw, ivShowPasswConf;
    private EditText etUsername, etNombre, etApellidos, etCurrentPassword, etNewPassword, etConfirmPassword, etEmail;
    private Spinner spinnerProv, spinnerLoc, spinnerCent;
    private CheckBox cbStudent;
    private Button btnUnsubAll, btnSaveData, btnSavePassw, btnSaveEmail, btnDeleteAccount;
    private ProgressBar pbUnsub, pbAvatar, pbData, pbPassw, pbEmail, pbDelete;

    private UsuarioRes usuario;
    private boolean selectedSpinnerLoc;
    private boolean selectedSpinnerCent;

    private ProfileViewModel profileViewModel;
    private RadioViewModel radioViewModel;
    private VolleySingleton volley;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileViewModel = new ViewModelProvider(requireActivity())
                .get(ProfileViewModel.class);
        radioViewModel = new ViewModelProvider(requireActivity())
                .get(RadioViewModel.class);
        volley = VolleySingleton.getInstance();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        selectedSpinnerLoc = false;
        selectedSpinnerCent = false;

        findViews();
        bindEvents();
        resetSpinners();

        setProfilePicture(SharedPreferencesManager.getStringValue(Constants.PREF_PICTURE_URL));
        profileViewModel.getProfilePicture().observe(requireActivity(), this::setProfilePicture);

        profileViewModel.getUserProfile().observe(requireActivity(), u -> {
            usuario = u;
            etUsername.setText(u.getUsername());
            etNombre.setText(u.getNombre());
            etApellidos.setText(u.getApellidos());

            if (u.getCodigoCentro() != 0) {
                cbStudent.setChecked(true);
                toggleSpinnersVisibility(true);
            }

            etEmail.setText(u.getEmail());
        });

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
        ivAvatar = binding.imageViewAvatar;
        etUsername = binding.editTextUsername;
        etNombre = binding.editTextName;
        etApellidos = binding.editTextSurname;
        etCurrentPassword = binding.editTextCurrentPassword;
        etNewPassword = binding.editTextNewPassword;
        etConfirmPassword = binding.editTextConfirmPassword;
        etEmail = binding.editTextEmail;
        ivShowCurPassw = binding.imageViewShowCurrentPassw;
        ivShowNewPassw = binding.imageViewShowNewPassw;
        ivShowPasswConf = binding.imageViewShowPasswConf;
        spinnerProv = binding.spinnerProv;
        spinnerLoc = binding.spinnerLoc;
        spinnerCent = binding.spinnerCent;
        cbStudent = binding.checkBoxStudent;
        btnUnsubAll = binding.buttonUnsubAll;
        btnSaveData = binding.buttonSaveProfileData;
        btnSavePassw = binding.buttonSavePassword;
        btnSaveEmail = binding.buttonSaveEmail;
        btnDeleteAccount = binding.buttonDeleteAccount;
        pbUnsub = binding.progressBarUnsub;
        pbAvatar = binding.progressBarAvatar;
        pbData = binding.progressBarData;
        pbPassw = binding.progressBarPassw;
        pbEmail = binding.progressBarEmail;
        pbDelete = binding.progressBarDelete;
    }

    private void bindEvents() {
        ivAvatar.setOnClickListener(view -> {
            // Invocar método de comprobación de permisos
            checkPermissions();
        });

        cbStudent.setOnClickListener(v -> toggleSpinnersVisibility(cbStudent.isChecked()));

        spinnerProv.setOnItemSelectedListener(this);
        spinnerLoc.setOnItemSelectedListener(this);

        btnUnsubAll.setOnClickListener(v -> {
            showLoader(btnUnsubAll, pbUnsub);
            radioViewModel.unsubAllRadios(this);
        });

        btnSaveData.setOnClickListener(v -> {
            UsuarioReq u = new UsuarioReq();
            u.setUsername(etUsername.getText().toString());
            u.setNombre(etNombre.getText().toString());
            u.setApellidos(etApellidos.getText().toString());
            u.setCodigoCentro(cbStudent.isChecked() && spinnerCent.getSelectedItem() != null
                    ? ((Centro) spinnerCent.getSelectedItem()).getCodigo() : 0);
            if (checkDataForm(u)) {
                showLoader(btnSaveData, pbData);
                profileViewModel.updateProfileData(this, u);
            }
        });

        ivShowCurPassw.setOnClickListener(v-> togglePasswordVisibility(etCurrentPassword, ivShowCurPassw,
                etCurrentPassword.getTransformationMethod()
                        .equals(PasswordTransformationMethod.getInstance())));

        etCurrentPassword.addTextChangedListener(new MyTextWatcher(etCurrentPassword, ivShowCurPassw));

        ivShowNewPassw.setOnClickListener(v-> togglePasswordVisibility(etNewPassword, ivShowNewPassw,
                etNewPassword.getTransformationMethod()
                        .equals(PasswordTransformationMethod.getInstance())));

        etNewPassword.addTextChangedListener(new MyTextWatcher(etNewPassword, ivShowNewPassw));

        ivShowPasswConf.setOnClickListener(v-> togglePasswordVisibility(etConfirmPassword, ivShowPasswConf,
                etConfirmPassword.getTransformationMethod()
                        .equals(PasswordTransformationMethod.getInstance())));

        etConfirmPassword.addTextChangedListener(new MyTextWatcher(etConfirmPassword, ivShowPasswConf));

        btnSavePassw.setOnClickListener(v -> {
            UsuarioReq u = new UsuarioReq();
            String curPassw = etCurrentPassword.getText().toString();
            u.setPassword(etNewPassword.getText().toString());
            u.setConfirmPassword(etConfirmPassword.getText().toString());
            if (checkPasswForm(curPassw, u)) {
                showLoader(btnSavePassw, pbPassw);
                profileViewModel.updatePassword(this, curPassw, u);
            }
        });

        btnSaveEmail.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            if (checkEmailForm(email)) {
                showLoader(btnSaveEmail, pbEmail);
                profileViewModel.updateEmail(this, email);
            }
        });

        btnDeleteAccount.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);
            builder.setMessage("¿Estás seguro de que quieres borrar tu cuenta? Esta acción no se puede deshacer.");
            builder.setPositiveButton("Sí", (dialog, which) -> profileViewModel.deleteAccount(this));
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            MyApp.setAlertDialogStyle(dialog, getActivity());
            dialog.show();
        });
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

    public void picLoading() {
        pbAvatar.setVisibility(View.VISIBLE);
        ivAvatar.setVisibility(View.INVISIBLE);
    }

    private void showLoader(Button btn, ProgressBar pb) {
        btn.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.VISIBLE);
    }

    public void hideLoader() {
        pbUnsub.setVisibility(View.GONE);
        pbData.setVisibility(View.GONE);
        pbPassw.setVisibility(View.GONE);
        pbEmail.setVisibility(View.GONE);
        pbDelete.setVisibility(View.GONE);

        btnUnsubAll.setVisibility(View.VISIBLE);
        btnSaveData.setVisibility(View.VISIBLE);
        btnSavePassw.setVisibility(View.VISIBLE);
        btnSaveEmail.setVisibility(View.VISIBLE);
        btnDeleteAccount.setVisibility(View.VISIBLE);
    }

    private void setProfilePicture(String imagePath) {
        Glide.with(MyApp.getContext())
                .load(Constants.DOMAIN_URL + imagePath)
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // no hacer uso de memoria caché para evitar inconsistencias al actualizar foto
                .skipMemoryCache(true)
                .into(ivAvatar);
        pbAvatar.setVisibility(View.GONE);
        ivAvatar.setVisibility(View.VISIBLE);
    }

    private void resetSpinners() {
        spinnerLoc.setAdapter(null);
        spinnerCent.setAdapter(null);
        volley.populateStringSpinner(spinnerProv, Constants.SCHOOL_URL, "provincia", "--selecciona provincia--");
    }

    public void populateSpinnerProvSelected(UsuarioRes u) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.SCHOOL_URL,
                response -> {
                    try {
                        List<CharSequence> list = new ArrayList<>();
                        list.add(response.equals("[]") ? "" : "--selecciona provincia--");
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            list.add(jsonArray.getJSONObject(i).getString("provincia"));
                        }
                        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(MyApp.getContext(), android.R.layout.simple_spinner_item, list);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerProv.setAdapter(adapter);
                        spinnerProv.setSelection(adapter.getPosition(u.getProvinciaCentro()), false);
                        selectedSpinnerLoc = true;
                        populateSpinnerLocSelected(u);
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                    }
                }, error -> {
                    Log.e("ERROR", error.getMessage());
                    MyApp.showSnackbar(requireActivity(), requireActivity().findViewById(R.id.nav_host_fragment_content_dashboard),
                            Constants.SERVER_FAIL, Snackbar.LENGTH_LONG);
                }
        );
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

    public void populateSpinnerLocSelected(UsuarioRes u) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                String.format("%s?%s=%s", Constants.SCHOOL_URL, Constants.SCHOOL_PARAM_PROV, u.getProvinciaCentro()),
                response -> {
                    try {
                        List<CharSequence> list = new ArrayList<>();
                        list.add(response.equals("[]") ? "" : "--selecciona localidad--");
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            list.add(jsonArray.getJSONObject(i).getString("localidad"));
                        }
                        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(MyApp.getContext(), android.R.layout.simple_spinner_item, list);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerLoc.setAdapter(adapter);
                        spinnerLoc.setSelection(adapter.getPosition(u.getLocalidadCentro()), false);
                        selectedSpinnerCent = true;
                        populateSpinnerCentSelected(u);
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                    }
                }, error -> {
                    Log.e("ERROR", error.getMessage());
                    Toast.makeText(MyApp.getContext(), Constants.SERVER_FAIL, Toast.LENGTH_LONG).show();
                }
        );
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

    private void populateSpinnerCentSelected(UsuarioRes u) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                String.format("%s?%s=%s", Constants.SCHOOL_URL, Constants.SCHOOL_PARAM_LOC, u.getLocalidadCentro()),
                response -> {
                    try {
                        int selectedIndex = 0;
                        List<Centro> list = new ArrayList<>();
                        list.add(new Centro(0, response.equals("[]") ? "" : "--selecciona centro--"));
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            int codigo = jsonArray.getJSONObject(i).getInt("codigo");
                            String denominacion = jsonArray.getJSONObject(i).getString("denominacion");
                            selectedIndex = denominacion.equals(u.getNombreCentro()) ? i : selectedIndex;
                            list.add(new Centro(codigo, denominacion));
                        }
                        ArrayAdapter<Centro> adapter = new ArrayAdapter<>(MyApp.getContext(), android.R.layout.simple_spinner_item, list);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCent.setAdapter(adapter);
                        spinnerCent.setSelection(selectedIndex + 1, false);
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                    }
                }, error -> {
                    Log.e("ERROR", error.getMessage());
                    Toast.makeText(MyApp.getContext(), Constants.SERVER_FAIL, Toast.LENGTH_LONG).show();
                }
        );
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

    private void toggleSpinnersVisibility(boolean show) {
        if (!show) {
            resetSpinners();
        }

        if (usuario.getCodigoCentro() != 0) {
            populateSpinnerProvSelected(usuario);
        }

        int visibility = show ? View.VISIBLE : View.GONE;
        spinnerProv.setVisibility(visibility);
        spinnerLoc.setVisibility(visibility);
        spinnerCent.setVisibility(visibility);
    }

    private boolean checkDataForm(UsuarioReq u) {
        boolean correcto = true;
        if (u.getApellidos().trim().isEmpty()) {
            etApellidos.setError("¡Campo obligatorio!");
            etApellidos.requestFocus();
            correcto = false;
        }
        if (u.getNombre().isEmpty()) {
            etNombre.setError("¡Campo obligatorio!");
            etNombre.requestFocus();
            correcto = false;
        }
        if (u.getUsername().isEmpty()) {
            etUsername.setError("¡Campo obligatorio!");
            etUsername.requestFocus();
            correcto = false;
        }
        return correcto;
    }

    private boolean checkPasswForm(String curPassw, UsuarioReq u) {
        boolean correcto = true;
        if (u.getPassword().trim().isEmpty()) {
            etNewPassword.setError("¡Campo obligatorio!");
            etNewPassword.requestFocus();
            correcto = false;
        } else if (u.getPassword().length() < Constants.PASSW_LENGTH) {
            etNewPassword.setError("¡Contraseña debe tener al menos " + Constants.PASSW_LENGTH + " caracteres!");
            etNewPassword.requestFocus();
            correcto = false;
        } else if (!u.getConfirmPassword().equals(u.getPassword())) {
            etConfirmPassword.setError("¡Ambas contraseñas no coinciden!");
            etConfirmPassword.requestFocus();
            correcto = false;
        }
        if (curPassw.trim().isEmpty()) {
            etCurrentPassword.setError("¡Campo obligatorio!");
            etCurrentPassword.requestFocus();
            correcto = false;
        }
        return correcto;
    }

    private boolean checkEmailForm(String email) {
        boolean correcto = true;
        if (email.trim().isEmpty()) {
            etEmail.setError("¡Campo obligatorio!");
            etEmail.requestFocus();
            correcto = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("¡Correo electrónico no válido!");
            etEmail.requestFocus();
            correcto = false;
        } else if (email.equalsIgnoreCase(usuario.getEmail())) {
            correcto = false;
        }
        return correcto;
    }

    private void checkPermissions() {
        PermissionListener dialogOnDeniedPermisionListener =
                DialogOnDeniedPermissionListener.Builder
                        .withContext(getActivity())
                        .withTitle("Permisos")
                        .withMessage("Los permisos solicitados son necesarios para poder seleccionar una foto de perfil.")
                        .withButtonText("Aceptar")
                        .withIcon(R.mipmap.ic_launcher)
                        .build();

        PermissionListener allPermissionListener = new CompositePermissionListener(
                (PermissionListener) getActivity(), dialogOnDeniedPermisionListener // DashboardActivity.java (onPermissionGranted | onPermissionDenied)
        );

        Dexter.withContext(getActivity())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)  // permiso para leer fichero externo a almacenamiento (foto de galería para perfil)
                .withListener(allPermissionListener)
                .check();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int parentId = parent.getId();
        if (parentId == R.id.spinnerProv) {
            if (selectedSpinnerLoc) {
                selectedSpinnerLoc = false;
                return;
            }
            String provincia = (String) parent.getItemAtPosition(position);
            volley.populateStringSpinner(spinnerLoc, String.format("%s?%s=%s", Constants.SCHOOL_URL, Constants.SCHOOL_PARAM_PROV, provincia),
                    "localidad", "--selecciona localidad--");
        } else if (parentId == R.id.spinnerLoc) {
            if (selectedSpinnerCent) {
                selectedSpinnerCent = false;
                return;
            }
            String localidad = (String) parent.getItemAtPosition(position);
            volley.populateCentroSpinner(spinnerCent, String.format("%s?%s=%s", Constants.SCHOOL_URL, Constants.SCHOOL_PARAM_LOC, localidad),
                    "--selecciona centro--");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}