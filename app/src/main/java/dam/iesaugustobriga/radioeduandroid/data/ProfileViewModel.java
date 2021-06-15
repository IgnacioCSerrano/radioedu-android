package dam.iesaugustobriga.radioeduandroid.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import dam.iesaugustobriga.radioeduandroid.models.UsuarioReq;
import dam.iesaugustobriga.radioeduandroid.models.UsuarioRes;
import dam.iesaugustobriga.radioeduandroid.ui.dashboard.profile.ProfileFragment;

public class ProfileViewModel extends AndroidViewModel {

    private final ProfileRepository profileRepository;
    private final MutableLiveData<String> profilePicture;
    private final MutableLiveData<UsuarioRes> userProfile;

    public ProfileViewModel(@NonNull @NotNull Application application) {
        super(application);

        profileRepository = new ProfileRepository();
        profilePicture = new MutableLiveData<>();
        userProfile = new MutableLiveData<>();

        profileRepository.getProfilePicture(profilePicture);
        profileRepository.getUserProfile(userProfile);
    }

    public LiveData<String> getProfilePicture() { return profilePicture; }

    public LiveData<UsuarioRes> getUserProfile() { return userProfile; }

    public void uploadPhoto(ProfileFragment profileFragment, String encodedImage) {
        profileRepository.uploadPhoto(profileFragment, encodedImage);
    }

    public void updateProfileData(ProfileFragment profileFragment, UsuarioReq usuario) {
        profileRepository.updateProfileData(profileFragment, usuario);
    }

    public void updatePassword(ProfileFragment profileFragment, String curPassw, UsuarioReq usuario) {
        profileRepository.updatePassword(profileFragment, curPassw, usuario);
    }

    public void updateEmail(ProfileFragment profileFragment, String email) {
        profileRepository.updateEmail(profileFragment, email);
    }

    public void deleteAccount(ProfileFragment profileFragment) {
        profileRepository.deleteAccount(profileFragment);
    }

}
