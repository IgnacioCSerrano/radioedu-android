package dam.iesaugustobriga.radioeduandroid.data;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dam.iesaugustobriga.radioeduandroid.R;
import dam.iesaugustobriga.radioeduandroid.common.Constants;
import dam.iesaugustobriga.radioeduandroid.common.MyApp;
import dam.iesaugustobriga.radioeduandroid.common.SharedPreferencesManager;
import dam.iesaugustobriga.radioeduandroid.models.Comentario;
import dam.iesaugustobriga.radioeduandroid.models.Podcast;
import dam.iesaugustobriga.radioeduandroid.models.Radio;
import dam.iesaugustobriga.radioeduandroid.ui.dashboard.profile.ProfileFragment;

public class RadioRepository {

    private final VolleySingleton volley;

    private MutableLiveData<List<Radio>> radiosRep;
    private MutableLiveData<List<Podcast>> podcastsRep;
    private MutableLiveData<List<Comentario>> commentsRep;

    RadioRepository() {
        volley = VolleySingleton.getInstance();
    }

    public void getRadios(MutableLiveData<List<Radio>> radios) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.RADIO_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("payload");
                        List<Radio> list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Radio r = new Radio(
                                jsonArray.getJSONObject(i).getLong("id"),
                                jsonArray.getJSONObject(i).getString("nombre"),
                                jsonArray.getJSONObject(i).getString("imagen"),
                                jsonArray.getJSONObject(i).getString("denominacion"),
                                jsonArray.getJSONObject(i).getString("localidad"),
                         jsonArray.getJSONObject(i).getInt("suscrito") != 0
                            );
                            list.add(r);
                        }
                        radios.setValue(list);
                        radiosRep = radios;
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                        Toast.makeText(MyApp.getContext(), "No ha sido posible recuperar los datos de radios.", Toast.LENGTH_LONG).show();
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
                params.put("get-radios", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

    public void getPodcastsByRadioId(MutableLiveData<List<Podcast>> podcasts, long idRadio) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.RADIO_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("payload");
                        List<Podcast> list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Podcast p = new Podcast(
                                    jsonArray.getJSONObject(i).getLong("id"),
                                    jsonArray.getJSONObject(i).getString("imagen"),
                                    jsonArray.getJSONObject(i).getString("audio"),
                                    jsonArray.getJSONObject(i).getString("titulo"),
                                    jsonArray.getJSONObject(i).getString("cuerpo"),
                                    LocalDateTime.parse(jsonArray.getJSONObject(i).getString("fecha_creacion").replace(" ", "T")),
                                    jsonArray.getJSONObject(i).getInt("visitas"),
                                    jsonArray.getJSONObject(i).getInt("reproducciones"),
                                    jsonArray.getJSONObject(i).getInt("bloqueado") != 0,
                                    jsonArray.getJSONObject(i).getInt("favorito") != 0,
                                    jsonArray.getJSONObject(i).getInt("id_radio")
                            );
                            list.add(p);
                        }
                        podcasts.setValue(list);
                        podcastsRep = podcasts;
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                        Toast.makeText(MyApp.getContext(), "No ha sido posible recuperar los datos de podcasts.", Toast.LENGTH_LONG).show();
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
                params.put("id-radio", String.valueOf(idRadio));
                params.put("get-podcasts", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

    public void getPodcastById(long idPodcast, MutableLiveData<Podcast> podcast) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.RADIO_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("payload");
                        Podcast p = new Podcast(
                                jsonArray.getJSONObject(0).getLong("id"),
                                jsonArray.getJSONObject(0).getString("imagen"),
                                jsonArray.getJSONObject(0).getString("audio"),
                                jsonArray.getJSONObject(0).getString("titulo"),
                                jsonArray.getJSONObject(0).getString("cuerpo"),
                                LocalDateTime.parse(jsonArray.getJSONObject(0).getString("fecha_creacion").replace(" ", "T")),
                                jsonArray.getJSONObject(0).getInt("visitas"),
                                jsonArray.getJSONObject(0).getInt("reproducciones"),
                                jsonArray.getJSONObject(0).getInt("bloqueado") != 0,
                                jsonArray.getJSONObject(0).getInt("favorito") != 0,
                                jsonArray.getJSONObject(0).getInt("id_radio")
                        );
                        podcast.setValue(p);
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                        Toast.makeText(MyApp.getContext(), "No ha sido posible recuperar los datos del podcast.", Toast.LENGTH_LONG).show();
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
                params.put("id-podcast", String.valueOf(idPodcast));
                params.put("get-podcast", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

    public void getCommentsByPodcastId(MutableLiveData<List<Comentario>> comments, long idPodcast) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.RADIO_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("payload");
                        List<Comentario> list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Comentario c = new Comentario(
                                    jsonArray.getJSONObject(i).getLong("id"),
                                    jsonArray.getJSONObject(i).getString("mensaje"),
                                    LocalDateTime.parse(jsonArray.getJSONObject(i).getString("fecha_registro").replace(" ", "T")),
                                    jsonArray.getJSONObject(i).getLong("id_podcast"),
                                    jsonArray.getJSONObject(i).getLong("id_usuario"),
                                    jsonArray.getJSONObject(i).getString("username"),
                                    jsonArray.getJSONObject(i).getString("imagen"),
                                    jsonArray.getJSONObject(i).getString("rol")
                            );
                            list.add(c);
                        }
                        comments.setValue(list);
                        commentsRep = comments;
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                        Toast.makeText(MyApp.getContext(), "No ha sido posible recuperar los datos de comentarios.", Toast.LENGTH_LONG).show();
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
                params.put("id-podcast", String.valueOf(idPodcast));
                params.put("get-comments", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

    public void subscribe(Radio radio, boolean subscribe, Activity activity) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.RADIO_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            radio.setSuscrito(subscribe);
                            getRadios(radiosRep); // actualiza lista de radios
                        } else {
                            MyApp.showSnackbar(activity, activity.findViewById(R.id.nav_host_fragment_content_dashboard),
                                    Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                        }
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                        Toast.makeText(MyApp.getContext(), Constants.REQUEST_FAIL, Toast.LENGTH_LONG).show();
                        MyApp.showSnackbar(activity, activity.findViewById(R.id.nav_host_fragment_content_dashboard),
                                Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                    }
                }, error -> {
                    Log.e("ERROR", error.getMessage());
                    MyApp.showSnackbar(activity, activity.findViewById(R.id.nav_host_fragment_content_dashboard),
                        Constants.SERVER_FAIL, Snackbar.LENGTH_LONG);
                }
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("bearer-token", "Bearer " + SharedPreferencesManager.getStringValue(Constants.PREF_TOKEN));
                params.put("id-radio", String.valueOf(radio.getId()));
                params.put(subscribe ? "subscribe" : "unsubscribe", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

    public void like(Podcast podcast, boolean like, Activity activity) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.RADIO_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            getPodcastsByRadioId(podcastsRep, podcast.getIdRadio()); // actualiza lista de podcasts
                        } else {
                            MyApp.showSnackbar(activity, activity.findViewById(R.id.nav_host_fragment_content_dashboard),
                                    Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                        }
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                        MyApp.showSnackbar(activity, activity.findViewById(R.id.nav_host_fragment_content_dashboard),
                                Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                    }
                }, error -> {
                    Log.e("ERROR", error.getMessage());
                    MyApp.showSnackbar(activity, activity.findViewById(R.id.nav_host_fragment_content_dashboard),
                            Constants.SERVER_FAIL, Snackbar.LENGTH_LONG);
                }
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("bearer-token", "Bearer " + SharedPreferencesManager.getStringValue(Constants.PREF_TOKEN));
                params.put("id-podcast", String.valueOf(podcast.getId()));
                params.put(like ? "like" : "unlike", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

    public void incrementViewCount(Podcast podcast) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.RADIO_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            getPodcastsByRadioId(podcastsRep, podcast.getIdRadio()); // actualiza lista de podcasts
                        }
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                    }
                }, error -> Log.e("ERROR", error.getMessage())
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("bearer-token", "Bearer " + SharedPreferencesManager.getStringValue(Constants.PREF_TOKEN));
                params.put("id-podcast", String.valueOf(podcast.getId()));
                params.put("increment-view-count", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

    public void incrementPlayCount(Podcast podcast) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.RADIO_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            getPodcastsByRadioId(podcastsRep, podcast.getIdRadio()); // actualiza lista de podcasts
                        }
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                    }
                }, error -> Log.e("ERROR", error.getMessage())
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("bearer-token", "Bearer " + SharedPreferencesManager.getStringValue(Constants.PREF_TOKEN));
                params.put("id-podcast", String.valueOf(podcast.getId()));
                params.put("increment-play-count", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

    public void comment(String mensaje, long idPodcast, Activity activity) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.RADIO_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            getCommentsByPodcastId(commentsRep, idPodcast); // actualiza lista de comentarios
                        } else {
                            if (jsonObject.has("message")) {
                                ((FloatingActionButton) activity.findViewById(R.id.fab)).hide();
                                MyApp.showSnackbar(activity,
                                        activity.findViewById(R.id.nav_host_fragment_content_dashboard),
                                        jsonObject.getString("message"), Snackbar.LENGTH_LONG);
                            } else {
                                MyApp.showSnackbar(activity,
                                        activity.findViewById(R.id.nav_host_fragment_content_dashboard),
                                        Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                        MyApp.showSnackbar(activity, activity.findViewById(R.id.nav_host_fragment_content_dashboard),
                                Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                    }
                }, error -> {
                    Log.e("ERROR", error.getMessage());
                    MyApp.showSnackbar(activity, activity.findViewById(R.id.nav_host_fragment_content_dashboard),
                            Constants.SERVER_FAIL, Snackbar.LENGTH_LONG);
                }
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("bearer-token", "Bearer " + SharedPreferencesManager.getStringValue(Constants.PREF_TOKEN));
                params.put("mensaje", mensaje);
                params.put("id-podcast", String.valueOf(idPodcast));
                params.put("send-comment", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

    public void updateComment(long idComment, String mensaje, long idPodcast, Activity activity) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.RADIO_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            getCommentsByPodcastId(commentsRep, idPodcast); // actualiza lista de comentarios
                        } else {
                            MyApp.showSnackbar(activity, activity.findViewById(R.id.nav_host_fragment_content_dashboard),
                                    Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                        }
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                        MyApp.showSnackbar(activity, activity.findViewById(R.id.nav_host_fragment_content_dashboard),
                                Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                    }
                }, error -> {
                    Log.e("ERROR", error.getMessage());
                    MyApp.showSnackbar(activity, activity.findViewById(R.id.nav_host_fragment_content_dashboard),
                            Constants.SERVER_FAIL, Snackbar.LENGTH_LONG);
                }
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("bearer-token", "Bearer " + SharedPreferencesManager.getStringValue(Constants.PREF_TOKEN));
                params.put("id-comment", String.valueOf(idComment));
                params.put("mensaje", mensaje);
                params.put("update-comment", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

    public void deleteComment(long idComment, long idPodcast, Activity activity) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.RADIO_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            getCommentsByPodcastId(commentsRep, idPodcast); // actualiza lista de comentarios
                        } else {
                            MyApp.showSnackbar(activity, activity.findViewById(R.id.nav_host_fragment_content_dashboard),
                                    Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                        }
                    } catch (JSONException e) {
                        Log.e("ERROR", e.getMessage());
                        MyApp.showSnackbar(activity, activity.findViewById(R.id.nav_host_fragment_content_dashboard),
                                Constants.REQUEST_FAIL, Snackbar.LENGTH_LONG);
                    }
                }, error -> {
                    Log.e("ERROR", error.getMessage());
                MyApp.showSnackbar(activity, activity.findViewById(R.id.nav_host_fragment_content_dashboard),
                        Constants.SERVER_FAIL, Snackbar.LENGTH_LONG);
                }
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("bearer-token", "Bearer " + SharedPreferencesManager.getStringValue(Constants.PREF_TOKEN));
                params.put("id-comment", String.valueOf(idComment));
                params.put("delete-comment", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

    public void unsubAllRadios(ProfileFragment profileFragment) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.RADIO_URL,
                response -> {
                    Log.i("RESPONSE", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            getRadios(radiosRep); // actualiza lista de radios
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
                params.put("unsub-all-radios", "true");
                return params;
            }
        };
        stringRequest.setTag(Constants.TAG);
        volley.addToRequestQueue(stringRequest);
    }

}
