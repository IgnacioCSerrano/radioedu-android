package dam.iesaugustobriga.radioeduandroid.common;

public class Constants {

    // COMÚN

    public static final String TAG          = "RADIO_EDU";
    public static final String SERVER_FAIL  = "Error al establecer la conexión con el servidor. Inténtalo de nuevo más tarde.";
    public static final String REQUEST_FAIL = "Algo ha ido mal. Inténtalo de nuevo más tarde.";

    // SEGURIDAD

    public static final int CODE_LENGTH     = 6;
    public static final int PASSW_LENGTH    = 6;

    // RUTAS

    public static final String DOMAIN_URL   = "http://10.0.2.2/radioedu/";
    public static final String API_URL      = DOMAIN_URL + "android/";
    public static final String SCHOOL_URL   = API_URL + "get-school.php";
    public static final String AUTH_URL     = API_URL + "access-process.php";
    public static final String RADIO_URL    = API_URL + "radio-process.php";
    public static final String PROFILE_URL  = API_URL + "profile-process.php";

    public static final String SCHOOL_PARAM_LOC     = "localidad";
    public static final String SCHOOL_PARAM_PROV    = "provincia";

    // PREFERENCIAS

    public static final String APP_SETTINGS_FILE    = "RADIOEDU_SETTINGS_FILE";
    public static final String PREF_TOKEN           = "PREF_TOKEN";
    public static final String PREF_ID              = "PREF_ID";
    public static final String PREF_USERNAME        = "PREF_USERNAME";
    public static final String PREF_EMAIL           = "PREF_EMAIL";
    public static final String PREF_PICTURE_URL     = "PREF_PICTURE_URL";

    // ARGUMENTOS

    public static final String ARG_RADIO    = "ARG_RADIO";
    public static final String ARG_PODCAST  = "ARG_PODCAST";
    public static final String ARG_COMMENT  = "ARG_COMMENT";
    public static final String ARG_EMAIL    = "ARG_EMAIL";
    public static final String ARG_MESSAGE  = "ARG_MESSAGE";

    // FIREBASE PUSH NOTIFICATION

    public static final String CHANNEL_ID   = "RADIO_EDU_ID";
    public static final String CHANNEL_NAME = "RADIO_EDU_NAME";
    public static final String CHANNEL_DESC = "RADIO_EDU_DESC";

}
