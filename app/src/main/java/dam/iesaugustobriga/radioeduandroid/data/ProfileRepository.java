package dam.iesaugustobriga.radioeduandroid.data;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

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
import dam.iesaugustobriga.radioeduandroid.common.SharedPreferencesManager;
import dam.iesaugustobriga.radioeduandroid.models.UsuarioReq;
import dam.iesaugustobriga.radioeduandroid.models.UsuarioRes;
import dam.iesaugustobriga.radioeduandroid.ui.auth.LoginActivity;
import dam.iesaugustobriga.radioeduandroid.ui.dashboard.profile.ProfileFragment;

public class ProfileRepository {

    private final VolleySingleton volley;

    private MutableLiveData<String> profilePictureRep;
    private MutableLiveData<UsuarioRes> userProfileRep;

    ProfileRepository() {
        volley = VolleySingleton.getInstance();
    }

    public void getProfilePicture(MutableLiveData<String> profilePicture) {
        profilePictureRep = profilePicture;
    }

    public void getUserProfile(MutableLiveData<UsuarioRes> userProfile) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.PROFILE_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response).getJSONObject("payload");
                        UsuarioRes usuario = new UsuarioRes(
                                jsonObject.getString("username"),
                                jsonObject.getString("email"),
                                jsonObject.getString("nombre"),
                                jsonObject.getString("apellidos"),
                                jsonObject.isNull("codigo_centro") ? 0 : jsonObject.getInt("codigo_centro"),
                                jsonObject.isNull("provincia") ? null : jsonObject.getString("provincia"),
                                jsonObject.isNull("localidad") ? null : jsonObject.getString("localidad"),
                                jsonObject.isNull("denominacion") ? null :jsonObject.getString("denominacion")
                        );
                        userProfile.setValue(usuario);
                        userProfileRep = userProfile;
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                        Toast.makeText(MyApp.getContext(), "No ha sido posible recuperar los datos de perfil.", Toast.LENGTH_LONG).show();
                    }
                }, error -> {
                    Log.e("ERROR", error.getMessage());
                    Toast.makeText(MyApp.getContext(), Constants.SERVER_FAIL, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("bearer-token", "Bearer " + SharedPreferencesManager.getStringValue(Constants.PREF_TOKEN));
                params.put("get-profile", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

    public void uploadPhoto(ProfileFragment profileFragment, String encodedImage) {
        profileFragment.picLoading();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.PROFILE_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            String imagePath = jsonObject.getString("payload");
                            SharedPreferencesManager.setStringValue(Constants.PREF_PICTURE_URL, imagePath);
                            profilePictureRep.setValue(imagePath);
                        } else {
                            MyApp.showSnackbar(profileFragment.requireActivity(),
                                    profileFragment.requireActivity().findViewById(R.id.nav_host_fragment_content_dashboard),
                                    Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                        }
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                        MyApp.showSnackbar(profileFragment.requireActivity(),
                                profileFragment.requireActivity().findViewById(R.id.nav_host_fragment_content_dashboard),
                                Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                    }
                    profileFragment.hideLoader();
                },
                error -> {
                    Log.e("ERROR", error.getMessage());
                    MyApp.showSnackbar(profileFragment.requireActivity(),
                            profileFragment.requireActivity().findViewById(R.id.nav_host_fragment_content_dashboard),
                            Constants.SERVER_FAIL, Snackbar.LENGTH_LONG);
                    profileFragment.hideLoader();
                }
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("bearer-token", "Bearer " + SharedPreferencesManager.getStringValue(Constants.PREF_TOKEN));
                params.put("data", encodedImage);
                params.put("upload-image", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        VolleySingleton.getInstance().addToRequestQueue(stringRequest);
    }

    public void updateProfileData(ProfileFragment profileFragment, UsuarioReq u) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.PROFILE_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            SharedPreferencesManager.setStringValue(Constants.PREF_USERNAME, u.getUsername());
                            getUserProfile(userProfileRep);
                        }
                        MyApp.showSnackbar(profileFragment.requireActivity(),
                                profileFragment.requireActivity().findViewById(R.id.nav_host_fragment_content_dashboard),
                                jsonObject.getString("message"), Snackbar.LENGTH_LONG);
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                        MyApp.showSnackbar(profileFragment.requireActivity(),
                                profileFragment.requireActivity().findViewById(R.id.nav_host_fragment_content_dashboard),
                                Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                    } finally {
                        profileFragment.hideLoader();
                    }
                }, error -> {
                    Log.e("ERROR", error.getMessage());
                    MyApp.showSnackbar(profileFragment.requireActivity(),
                            profileFragment.requireActivity().findViewById(R.id.nav_host_fragment_content_dashboard),
                            Constants.SERVER_FAIL, Snackbar.LENGTH_LONG);
                    profileFragment.hideLoader();
                }
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("bearer-token", "Bearer " + SharedPreferencesManager.getStringValue(Constants.PREF_TOKEN));
                params.put("current-username", SharedPreferencesManager.getStringValue(Constants.PREF_USERNAME));
                params.put("username", u.getUsername());
                params.put("nombre", u.getNombre());
                params.put("apellidos", u.getApellidos());
                if (u.getCodigoCentro() != 0) {
                    params.put("codigo-centro", String.valueOf(u.getCodigoCentro()));
                }
                params.put("update-profile-data", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

    public void updatePassword(ProfileFragment profileFragment, String curPassw, UsuarioReq u) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.PROFILE_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            getUserProfile(userProfileRep);
                        }
                        MyApp.showSnackbar(profileFragment.requireActivity(),
                                profileFragment.requireActivity().findViewById(R.id.nav_host_fragment_content_dashboard),
                                jsonObject.getString("message"), Snackbar.LENGTH_LONG);
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                        MyApp.showSnackbar(profileFragment.requireActivity(),
                                profileFragment.requireActivity().findViewById(R.id.nav_host_fragment_content_dashboard),
                                Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                    } finally {
                        profileFragment.hideLoader();
                    }
                }, error -> {
                    Log.e("ERROR", error.getMessage());
                    MyApp.showSnackbar(profileFragment.requireActivity(),
                            profileFragment.requireActivity().findViewById(R.id.nav_host_fragment_content_dashboard),
                            Constants.SERVER_FAIL, Snackbar.LENGTH_LONG);
                    profileFragment.hideLoader();
                }
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("bearer-token", "Bearer " + SharedPreferencesManager.getStringValue(Constants.PREF_TOKEN));
                params.put("current-password", curPassw);
                params.put("new-password", u.getPassword());
                params.put("confirm-password", u.getConfirmPassword());
                params.put("change-password", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

    public void updateEmail(ProfileFragment profileFragment, String email) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.PROFILE_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            SharedPreferencesManager.clearSharedPreferences();
                            Activity activity = profileFragment.getActivity();
                            Intent intent = new Intent(activity, LoginActivity.class);
                            intent.putExtra("message", "Se ha enviado un enlace de verificación a tu nueva dirección de correo electrónico.");
                            assert activity != null;
                            activity.startActivity(intent);
                            activity.finish();
                        } else {
                            MyApp.showSnackbar(profileFragment.requireActivity(),
                                    profileFragment.requireActivity().findViewById(R.id.nav_host_fragment_content_dashboard),
                                    jsonObject.getString("message"), Snackbar.LENGTH_LONG);
                        }
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                        MyApp.showSnackbar(profileFragment.requireActivity(),
                                profileFragment.requireActivity().findViewById(R.id.nav_host_fragment_content_dashboard),
                                Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                    } finally {
                        profileFragment.hideLoader();
                    }
                }, error -> {
                    Log.e("ERROR", error.getMessage());
                    MyApp.showSnackbar(profileFragment.requireActivity(),
                            profileFragment.requireActivity().findViewById(R.id.nav_host_fragment_content_dashboard),
                            Constants.SERVER_FAIL, Snackbar.LENGTH_LONG);
                    profileFragment.hideLoader();
                }
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("bearer-token", "Bearer " + SharedPreferencesManager.getStringValue(Constants.PREF_TOKEN));
                params.put("current-email", SharedPreferencesManager.getStringValue(Constants.PREF_EMAIL));
                params.put("email", email);
                params.put("change-email", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

    public void deleteAccount(ProfileFragment profileFragment) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.PROFILE_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            SharedPreferencesManager.clearSharedPreferences();
                            Activity activity = profileFragment.getActivity();
                            Intent intent = new Intent(activity, LoginActivity.class);
                            intent.putExtra("message", "Cuenta borrada con éxito.");
                            assert activity != null;
                            activity.startActivity(intent);
                            activity.finish();
                        } else {
                            MyApp.showSnackbar(profileFragment.requireActivity(),
                                    profileFragment.requireActivity().findViewById(R.id.nav_host_fragment_content_dashboard),
                                    Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                        }
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                        MyApp.showSnackbar(profileFragment.requireActivity(),
                                profileFragment.requireActivity().findViewById(R.id.nav_host_fragment_content_dashboard),
                                Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                    } finally {
                        profileFragment.hideLoader();
                    }
                }, error -> {
                    Log.e("ERROR", error.getMessage());
                    MyApp.showSnackbar(profileFragment.requireActivity(),
                            profileFragment.requireActivity().findViewById(R.id.nav_host_fragment_content_dashboard),
                            Constants.SERVER_FAIL, Snackbar.LENGTH_LONG);
                    profileFragment.hideLoader();
                }
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("bearer-token", "Bearer " + SharedPreferencesManager.getStringValue(Constants.PREF_TOKEN));
                params.put("delete-account", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

}
