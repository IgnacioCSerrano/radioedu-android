package dam.iesaugustobriga.radioeduandroid.ui.dashboard.podcast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dam.iesaugustobriga.radioeduandroid.common.Constants;
import dam.iesaugustobriga.radioeduandroid.common.MyApp;
import dam.iesaugustobriga.radioeduandroid.data.RadioViewModel;
import dam.iesaugustobriga.radioeduandroid.databinding.FragmentPodcastBinding;
import dam.iesaugustobriga.radioeduandroid.models.Comentario;
import dam.iesaugustobriga.radioeduandroid.models.Podcast;
import dam.iesaugustobriga.radioeduandroid.models.Radio;
import dam.iesaugustobriga.radioeduandroid.R;
import dam.iesaugustobriga.radioeduandroid.ui.dashboard.radio.PodcastListFragment;

public class PodcastFragment extends Fragment {

    private FragmentPodcastBinding binding;

    private ImageButton ibBack;
    private TextView tvTitulo, tvCuerpo, tvPlayerPosition, tvPlayerDuration;
    private ImageView ivImagen, ivLike, btnPlay, btnStop, btnPause, btnRew, btnForw;
    private SeekBar seekBar;

    private FloatingActionButton fab;

    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Runnable runnable;
    private boolean played;

    private MyCommentRecyclerViewAdapter adapter;
    private List<Comentario> comentarioList;
    private Podcast podcast;
    private Radio radio;
    private RadioViewModel radioViewModel;

    private SwipeRefreshLayout swipeRefreshLayout;

    public PodcastFragment() {
    }

    public static PodcastFragment newInstance(Podcast podcast, Radio radio) {
        PodcastFragment fragment = new PodcastFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARG_PODCAST, podcast);
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
        binding = FragmentPodcastBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        assert getArguments() != null;
        podcast = (Podcast) getArguments().getSerializable(Constants.ARG_PODCAST);
        radio = (Radio) getArguments().getSerializable(Constants.ARG_RADIO);

        findViews();
        bindEvents();

        // Establecer el adaptador

        Context ctx = root.getContext();

        RecyclerView recyclerView = binding.list;
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        recyclerView.setHasFixedSize(true);
        adapter = new MyCommentRecyclerViewAdapter(getActivity(), comentarioList);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(ctx, R.color.colorPrimary));

        setPodcastData();
        setMediaPlayer();

        radioViewModel.incrementViewCount(podcast);

        /*
            En la creación de vista se recupera el podcast de la base de datos para comprobar si ha
            sido bloqueado/desbloqueado recientemente y se oculta/muestra el botón de envío de
            comentario respectivamente
        */
        radioViewModel.getPodcast(podcast.getId()).observe(requireActivity(), p -> {
            if (p.isBloqueado()) {
                fab.hide();
            } else {
                fab.setTag(podcast.getId());
                fab.show();
            }
        });

        return root;
    }

    private void loadComments() {
        radioViewModel.clearPodcastList();
        radioViewModel.getComments(podcast.getId()).observe(requireActivity(), comentarios -> { // observer se queda a la espera de recibir la lista de datos
            swipeRefreshLayout.setRefreshing(false);
            comentarioList = comentarios;
            adapter.setData(comentarioList); // proceso de refresco
        });
    }

    private void setPodcastData() {
        tvTitulo.setText(podcast.getTitulo().toUpperCase());
        Glide.with(MyApp.getContext())
                .load(Constants.DOMAIN_URL + podcast.getImagen())
                .centerCrop()
                .dontAnimate()
                .into(ivImagen);
        toggleHeartIcon(podcast.isFavorito());
        tvCuerpo.setText(Html.fromHtml(podcast.getCuerpo()));
        loadComments();
    }

    private void loadNewPodcast() {
        radioViewModel.getPodcast(podcast.getId()).observe(requireActivity(), p -> {
            podcast = p;
            if (isAdded()) { // returns true if the fragment is currently added to its activity
                setPodcastData();
            }
        });
    }

    /*
        A MediaPlayer can consume valuable system resources. Therefore, you should always take
        extra precautions to make sure you are not hanging on to a MediaPlayer instance longer
        than necessary. When you are done with it, you should always call release() to make sure
        any system resources allocated to it are properly released.
     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mediaPlayer.release();
        mediaPlayer = null;
        binding = null;
        handler.removeCallbacks(runnable);
    }


    private void findViews() {
        fab = requireActivity().findViewById(R.id.fab);
        ibBack = binding.ibBack;
        tvTitulo = binding.textViewTitulo;
        ivImagen = binding.imageViewPodcastImg;
        ivLike = binding.imageViewLike;
        tvCuerpo = binding.textViewCuerpo;
        tvPlayerPosition = binding.textViewPlayerPos;
        tvPlayerDuration = binding.textViewPlayerDur;
        seekBar = binding.seekBar;
        btnPlay = binding.imageViewPlay;
        btnStop = binding.imageViewStop;
        btnPause = binding.imageViewPause;
        btnRew = binding.imageViewRew;
        btnForw = binding.imageViewForw;
        swipeRefreshLayout = binding.swipeRefreshLayout;
    }

    private void bindEvents() {
        ibBack.setOnClickListener(v -> {
            fab.hide();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_content_dashboard, PodcastListFragment.newInstance(radio))
                    .commit();
        });

        ivLike.setOnClickListener(v -> {
            toggleHeartIcon(!podcast.isFavorito());
            podcast.setFavorito(!podcast.isFavorito());
            radioViewModel.like(podcast, podcast.isFavorito(), requireActivity());
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true); // signal SwipeRefreshLayout to start the progress indicator
            loadNewPodcast();
        });

    }

    private void setMediaPlayer() {
        played = false;
        mediaPlayer = MediaPlayer.create(requireActivity(), Uri.parse(Constants.DOMAIN_URL + podcast.getAudio()));
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this, 500);
            }
        };
        int duration = mediaPlayer.getDuration();
        seekBar.setMax(duration);
        tvPlayerDuration.setText(convertFormat(duration));

        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(250);
        animation.setInterpolator(new LinearInterpolator());

        btnPlay.setOnClickListener(v -> {
            v.setVisibility(View.GONE);
            btnPause.setVisibility(View.VISIBLE);
            mediaPlayer.start();
            handler.postDelayed(runnable, 0);

            if (!played) {
                radioViewModel.incrementPlayCount(podcast);
                played = true;
            }
        });

        btnStop.setOnClickListener(v -> {
            if (mediaPlayer.getCurrentPosition() > 0) {
                try {
                    handler.postDelayed(runnable, 0);
                    mediaPlayer.stop();
                    mediaPlayer.prepare();
                    restartMediaPlayer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnPause.setOnClickListener(v -> {
            v.setVisibility(View.GONE);
            btnPlay.setVisibility(View.VISIBLE);
            mediaPlayer.pause();
            handler.removeCallbacks(runnable);
        });

        btnForw.setOnClickListener(v -> {
            int currentPosition = mediaPlayer.getCurrentPosition();
            if (mediaPlayer.isPlaying() && duration != currentPosition) {
                currentPosition += 5000;
                tvPlayerPosition.setText(convertFormat(currentPosition));
                mediaPlayer.seekTo(currentPosition);
                v.startAnimation(animation);
            }
        });

        btnRew.setOnClickListener(v -> {
            int currentPosition = mediaPlayer.getCurrentPosition();
            if (mediaPlayer.isPlaying() && currentPosition > 5000) {
                currentPosition -= 5000;
                tvPlayerPosition.setText(convertFormat(currentPosition));
                mediaPlayer.seekTo(currentPosition);
                v.startAnimation(animation);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
                tvPlayerPosition.setText(convertFormat(mediaPlayer.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaPlayer.setOnCompletionListener(mp -> restartMediaPlayer());
    }

    @SuppressLint("DefaultLocale")
    private String convertFormat(int duration) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(duration),
                TimeUnit.MILLISECONDS.toMinutes(duration) % 60,
                TimeUnit.MILLISECONDS.toSeconds(duration) % 60);
    }

    private void restartMediaPlayer() {
        btnPause.setVisibility(View.GONE);
        btnPlay.setVisibility(View.VISIBLE);
        mediaPlayer.seekTo(0);
    }

    private void toggleHeartIcon(boolean filled) {
        if (filled) {
            ivLike.setColorFilter(ContextCompat.getColor(requireActivity(), android.R.color.transparent));
            ivLike.setImageResource(R.drawable.ic_like_full);
        } else {
            ivLike.setImageResource(R.drawable.ic_like_empty);
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
            loadNewPodcast();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}