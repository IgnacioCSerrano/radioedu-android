package dam.iesaugustobriga.radioeduandroid.data;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import dam.iesaugustobriga.radioeduandroid.models.Comentario;
import dam.iesaugustobriga.radioeduandroid.models.Podcast;
import dam.iesaugustobriga.radioeduandroid.models.Radio;
import dam.iesaugustobriga.radioeduandroid.ui.dashboard.profile.ProfileFragment;

public class RadioViewModel extends AndroidViewModel {

    private final RadioRepository radioRepository;
    private final MutableLiveData<List<Radio>> radios;
    private final MutableLiveData<List<Podcast>> podcasts;
    private final MutableLiveData<Podcast> podcast;
    private final MutableLiveData<List<Comentario>> comments;

    public RadioViewModel(@NonNull @NotNull Application application) {
        super(application);

        radioRepository = new RadioRepository();
        radios = new MutableLiveData<>();
        podcasts = new MutableLiveData<>();
        podcast = new MutableLiveData<>();
        comments = new MutableLiveData<>();

        radioRepository.getRadios(radios);
    }

    public LiveData<List<Radio>> getRadios() {
        return radios;
    }

    public LiveData<List<Radio>> getNewRadios() {
        radioRepository.getRadios(radios);
        return radios;
    }

    public LiveData<List<Podcast>> getPodcasts(long idRadio) {
        radioRepository.getPodcastsByRadioId(podcasts, idRadio);
        return podcasts;
    }

    public LiveData<List<Comentario>> getComments(long idPodcast) {
        radioRepository.getCommentsByPodcastId(comments, idPodcast);
        return comments;
    }

    public LiveData<Podcast> getPodcast(long idPodcast) {
        radioRepository.getPodcastById(idPodcast, podcast);
        return podcast;
    }

    public void subscribe(Radio radio, boolean subscribe, Activity activity) {
        radioRepository.subscribe(radio, subscribe, activity);
    }

    public void like(Podcast podcast, boolean like, Activity activity) {
        radioRepository.like(podcast, like, activity);
    }

    public void incrementViewCount(Podcast podcast) {
        radioRepository.incrementViewCount(podcast);
    }

    public void incrementPlayCount(Podcast podcast) {
        radioRepository.incrementPlayCount(podcast);
    }

    public void comment(String mensaje, long idPodcast, Activity activity) {
        radioRepository.comment(mensaje, idPodcast, activity);
    }

    public void updateComment(long idComment, String mensaje, long idPodcast, Activity activity) {
        radioRepository.updateComment(idComment, mensaje, idPodcast, activity);
    }

    public void deleteComment(long idComment, long idPodcast, Activity activity) {
        radioRepository.deleteComment(idComment, idPodcast, activity);
    }

    public void unsubAllRadios(ProfileFragment profileFragment) {
        radioRepository.unsubAllRadios(profileFragment);
    }

    public void clearPodcastList() {
        podcasts.setValue(null);
    }

}
