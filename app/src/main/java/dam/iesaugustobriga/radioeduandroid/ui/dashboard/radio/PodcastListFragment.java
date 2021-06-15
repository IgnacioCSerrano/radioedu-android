package dam.iesaugustobriga.radioeduandroid.ui.dashboard.radio;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dam.iesaugustobriga.radioeduandroid.R;
import dam.iesaugustobriga.radioeduandroid.common.Constants;
import dam.iesaugustobriga.radioeduandroid.data.RadioViewModel;
import dam.iesaugustobriga.radioeduandroid.databinding.FragmentPodcastListBinding;
import dam.iesaugustobriga.radioeduandroid.models.Podcast;
import dam.iesaugustobriga.radioeduandroid.models.Radio;

public class PodcastListFragment extends Fragment {

    private MyPodcastRecyclerViewAdapter adapter;
    private Radio radio;
    private List<Podcast> podcastList;
    private List<Podcast> podcastFavList;
    private RadioViewModel radioViewModel;

    private SwipeRefreshLayout swipeRefreshLayout;

    public PodcastListFragment() {
    }

    public static PodcastListFragment newInstance(Radio radio) {
        PodcastListFragment fragment = new PodcastListFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARG_RADIO, radio);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        radioViewModel = new ViewModelProvider(requireActivity())
                .get(RadioViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentPodcastListBinding binding = FragmentPodcastListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        assert getArguments() != null;
        radio = (Radio) getArguments().getSerializable(Constants.ARG_RADIO);

        ImageButton ibBack = binding.ibBack;
        SwitchCompat switchSub = binding.switchSub;
        CheckBox cbFav = binding.checkBoxFav;

        podcastFavList = new ArrayList<>();

        ibBack.setOnClickListener(v -> {
            requireActivity().setTitle(R.string.menu_home);
            ((NavigationView) (requireActivity().findViewById(R.id.nav_view))).getMenu().findItem(R.id.nav_home).setChecked(true);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_content_dashboard, RadioListFragment.newInstance())
                    .commit();
        });

        switchSub.setChecked(radio.isSuscrito());
        switchSub.setOnCheckedChangeListener((buttonView, isChecked) -> radioViewModel.subscribe(radio, isChecked, requireActivity()));

        cbFav.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                adapter.setData(podcastFavList);
            } else {
                adapter.setData(podcastList);
            }

        });

        // Establecer el adaptador

        Context ctx = root.getContext();

        RecyclerView recyclerView = root.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        recyclerView.setHasFixedSize(true);
        adapter = new MyPodcastRecyclerViewAdapter(getActivity(), podcastList, radio);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(ctx, R.color.colorPrimary));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true); // signal SwipeRefreshLayout to start the progress indicator
            loadData();
        });

        loadData();

        return root;
    }

    private void loadData() {
        radioViewModel.getPodcasts(radio.getId())
            .observe(requireActivity(), podcasts -> { // observer se queda a la espera de recibir la lista de datos
                swipeRefreshLayout.setRefreshing(false);
                podcastList = podcasts;
                adapter.setData(podcastList); // proceso de refresco
                filterFavList();
            });
    }

    public void filterFavList() {
        if (podcastList != null) {
            podcastFavList.clear();
            podcastFavList = podcastList.stream().filter(Podcast::isFavorito).collect(Collectors.toList());
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.context_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_refresh) {
            loadData();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}