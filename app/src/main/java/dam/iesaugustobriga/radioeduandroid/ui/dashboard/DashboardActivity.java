package dam.iesaugustobriga.radioeduandroid.ui.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import dam.iesaugustobriga.radioeduandroid.R;
import dam.iesaugustobriga.radioeduandroid.common.Constants;
import dam.iesaugustobriga.radioeduandroid.common.MyApp;
import dam.iesaugustobriga.radioeduandroid.common.SharedPreferencesManager;
import dam.iesaugustobriga.radioeduandroid.data.ProfileViewModel;
import dam.iesaugustobriga.radioeduandroid.databinding.ActivityDashboardBinding;
import dam.iesaugustobriga.radioeduandroid.ui.dashboard.logout.LogoutFragment;
import dam.iesaugustobriga.radioeduandroid.ui.dashboard.podcast.NewCommentDialogFragment;
import dam.iesaugustobriga.radioeduandroid.ui.dashboard.profile.ProfileFragment;
import dam.iesaugustobriga.radioeduandroid.ui.dashboard.radio.RadioListFragment;

public class DashboardActivity extends AppCompatActivity implements PermissionListener {

    private ActivityDashboardBinding binding;
    private DrawerLayout mDrawer;
    private NavigationView mNav;

    private ProfileViewModel profileViewModel;

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        profileViewModel = new ViewModelProvider(this)
                .get(ProfileViewModel.class);

        Toolbar toolbar = binding.appBarDashboard.toolbar;
        setSupportActionBar(toolbar);

        mDrawer = binding.drawerLayout;
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        mNav = binding.navView;
        setupDrawerContent(mNav);
        setDefaultFragment();
        updateNavHeader();

        binding.appBarDashboard.fab.hide();
        /*
            El id del podcast abierto, almacenado en el tag del floating button,
            se propaga al dialog fragment nuevamente como tag de dicha vista
         */
        binding.appBarDashboard.fab.setOnClickListener(view -> new NewCommentDialogFragment()
                .show(getSupportFragmentManager(), view.getTag().toString()));

        profileViewModel = new ViewModelProvider(this)
                .get(ProfileViewModel.class);
    }

    @Override
    public void onBackPressed() {
        MyApp.buildAlertDialogClose(this, null, "¿Estás seguro de que deseas cerrar la aplicación?");
    }

    private void setDefaultFragment() {
        getSupportFragmentManager()
                .beginTransaction().replace(R.id.nav_host_fragment_content_dashboard, RadioListFragment.newInstance()).commit();
        MenuItem home = mNav.getMenu().findItem(R.id.nav_home);
        home.setChecked(true);
        setTitle(home.getTitle());
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        binding.appBarDashboard.fab.hide();

        // Especificar el fragmento a renderizar en base al elemento seleccionado 
        fragment = null;
        @SuppressWarnings("rawtypes") Class fragmentClass;
        int id = menuItem.getItemId();
        // Resource IDs will be non-final in future Android Gradle Plugin version, avoid using them in switch case statements
        if (id == R.id.nav_logout) {
            fragmentClass = LogoutFragment.class;
        } else if (id == R.id.nav_profile) {
            fragmentClass = ProfileFragment.class;
        } else {
            fragmentClass = RadioListFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        // Inserta un nuevo fragment reemplazando el actual
        FragmentManager fragmentManager = getSupportFragmentManager();
        assert fragment != null;
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_content_dashboard, fragment).commit();

        if (id != R.id.nav_logout) {
            // Resalta el elemento seleccionado por el NavigationView
            menuItem.setChecked(true);
        }

        // Establece el título del action bar
        setTitle(menuItem.getTitle());
        // Cierra el cajón de navegación
        mDrawer.closeDrawers();
    }

    private void updateNavHeader() {
        View headerView = mNav.getHeaderView(0);

        TextView tvUsername = headerView.findViewById(R.id.textViewUsername);
        TextView tvEmail = headerView.findViewById(R.id.textViewEmail);
        ImageView ivAvatar = headerView.findViewById(R.id.imageViewAvatar);

        tvUsername.setText(SharedPreferencesManager.getStringValue(Constants.PREF_USERNAME));
        tvEmail.setText(SharedPreferencesManager.getStringValue(Constants.PREF_EMAIL));
        setProfilePicture(SharedPreferencesManager.getStringValue(Constants.PREF_PICTURE_URL), ivAvatar);

        profileViewModel.getProfilePicture().observe(this, imagePath -> setProfilePicture(imagePath, ivAvatar));
        profileViewModel.getUserProfile().observe(this, u -> {
            tvUsername.setText(u.getUsername());
            tvEmail.setText(u.getEmail());
        });
    }

    private void setProfilePicture(String imagePath, ImageView iv) {
        Glide.with(this)
                .load(Constants.DOMAIN_URL + imagePath)
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // no hacer uso de memoria caché para evitar inconsistencias al actualizar foto
                .skipMemoryCache(true)
                .into(iv);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri imagenSeleccionada = data.getData(); // estructura: content://gallery/photos/...
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagenSeleccionada);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                            profileViewModel.uploadPhoto(((ProfileFragment) fragment), encodedImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Override
    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
        // Invocar selección de foto de galería
        Intent selectedPic = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // MediaStore.Images.Media.EXTERNAL_CONTENT_URI es URL de directorio principal donde se almacenan imagenes de dispositivo Android
        activityResultLauncher.launch(selectedPic);
    }

    @Override
    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
        MyApp.showSnackbar(this, findViewById(R.id.nav_host_fragment_content_dashboard), "No se puede seleccionar fotografía por falta de permisos.", Snackbar.LENGTH_LONG);
    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

    }

}