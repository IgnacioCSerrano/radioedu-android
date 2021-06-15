package dam.iesaugustobriga.radioeduandroid.ui.dashboard.radio;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import dam.iesaugustobriga.radioeduandroid.R;
import dam.iesaugustobriga.radioeduandroid.common.Constants;
import dam.iesaugustobriga.radioeduandroid.models.Podcast;
import dam.iesaugustobriga.radioeduandroid.databinding.FragmentPodcastItemBinding;
import dam.iesaugustobriga.radioeduandroid.models.Radio;
import dam.iesaugustobriga.radioeduandroid.ui.dashboard.podcast.PodcastFragment;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyPodcastRecyclerViewAdapter extends RecyclerView.Adapter<MyPodcastRecyclerViewAdapter.ViewHolder> {

    private final Context ctx;
    private final Radio radio;
    private List<Podcast> mValues;

    public MyPodcastRecyclerViewAdapter(Context c, List<Podcast> items, Radio r) {
        ctx = c;
        mValues = items;
        radio = r;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentPodcastItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull final ViewHolder holder, int position) {
        if (mValues != null) {
            holder.mItem = mValues.get(position);
            holder.tvPodcast.setText(holder.mItem.getTitulo());
            holder.tvFecha.setText(holder.mItem.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            Glide.with(ctx)
                    .load(Constants.DOMAIN_URL + holder.mItem.getImagen())
                    .centerCrop()
                    .dontAnimate()
                    .into(holder.ivImagen);

            holder.mView.setOnClickListener(v -> {
                Activity activity = (Activity) ctx;
                activity.findViewById(R.id.switchSub).setVisibility(View.GONE);
                activity.findViewById(R.id.checkBoxFav).setVisibility(View.GONE);
                activity.findViewById(R.id.list).setVisibility(View.GONE);
                activity.findViewById(R.id.progressBarPodcast).setVisibility(View.VISIBLE);
                ((FragmentActivity) ctx).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_content_dashboard, PodcastFragment.newInstance(holder.mItem, radio))
                        .commit();
            });
        }
    }

    public void setData(List<Podcast> podcastList) {
        this.mValues = podcastList;
        notifyDataSetChanged(); // repintar la lista del adapter (refresco)
    }

    @Override
    public int getItemCount() {
        return mValues != null ? mValues.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView ivImagen;
        public final TextView tvPodcast;
        public final TextView tvFecha;
        public Podcast mItem;

        public ViewHolder(FragmentPodcastItemBinding binding) {
            super(binding.getRoot());
            mView = binding.getRoot();
            ivImagen = binding.imageViewRadioImg;
            tvPodcast = binding.textViewTitulo;
            tvFecha = binding.textViewFechaPod;
        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + " '" + tvPodcast.getText() + "'";
        }
    }
}