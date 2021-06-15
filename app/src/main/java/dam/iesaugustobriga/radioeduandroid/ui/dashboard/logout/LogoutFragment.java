package dam.iesaugustobriga.radioeduandroid.ui.dashboard.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import dam.iesaugustobriga.radioeduandroid.ui.auth.LoginActivity;
import dam.iesaugustobriga.radioeduandroid.common.SharedPreferencesManager;

public class LogoutFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
        SharedPreferencesManager.clearSharedPreferences();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        Objects.requireNonNull(requireActivity()).finish();
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}