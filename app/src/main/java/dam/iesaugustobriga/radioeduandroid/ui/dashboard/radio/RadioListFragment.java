package dam.iesaugustobriga.radioeduandroid.ui.dashboard.radio;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

import org.jetbrains.annotations.NotNull;

import java.util.List;

import dam.iesaugustobriga.radioeduandroid.R;
import dam.iesaugustobriga.radioeduandroid.data.RadioViewModel;
import dam.iesaugustobriga.radioeduandroid.databinding.FragmentRadioListBinding;
import dam.iesaugustobriga.radioeduandroid.models.Radio;

public class RadioListFragment extends Fragment {

    private MyRadioRecyclerViewAdapter adapter;
    private List<Radio> radioList;
    private RadioViewModel radioViewModel;

    private SwipeRefreshLayout swipeRefreshLayout;

    public RadioListFragment() {
    }

    public static RadioListFragment newInstance() {
        RadioListFragment fragment = new RadioListFragment();
        Bundle args = new Bundle();
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
        FragmentRadioListBinding binding = FragmentRadioListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Establecer el adaptador

        Context ctx = root.getContext();

        RecyclerView recyclerView = binding.list;
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        recyclerView.setHasFixedSize(true);
        adapter = new MyRadioRecyclerViewAdapter(getActivity(), radioList);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(ctx, R.color.colorPrimary));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true); // signal SwipeRefreshLayout to start the progress indicator
            loadNewData();
        });

        loadData();

        return root;
    }

    private void loadData() {
        radioViewModel.clearPodcastList();
        radioViewModel.getRadios().observe(requireActivity(), radios -> { // observer se queda a la espera de recibir la lista de datos
            radioList = radios;
            adapter.setData(radioList); // proceso de refresco
        });
    }

    private void loadNewData() {
        radioViewModel.clearPodcastList();
        radioViewModel.getNewRadios()
            .observe(requireActivity(), radios -> { // observer se queda a la espera de recibir la lista de datos
                swipeRefreshLayout.setRefreshing(false);
                radioList = radios;
                adapter.setData(radioList); // proceso de refresco
            });
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
            loadNewData();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}