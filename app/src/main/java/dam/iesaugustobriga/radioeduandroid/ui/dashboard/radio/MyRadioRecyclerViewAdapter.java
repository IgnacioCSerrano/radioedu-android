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
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import dam.iesaugustobriga.radioeduandroid.common.Constants;
import dam.iesaugustobriga.radioeduandroid.models.Radio;
import dam.iesaugustobriga.radioeduandroid.databinding.FragmentRadioItemBinding;
import dam.iesaugustobriga.radioeduandroid.R;

import java.util.List;

public class MyRadioRecyclerViewAdapter extends RecyclerView.Adapter<MyRadioRecyclerViewAdapter.ViewHolder> {

    private final Context ctx;
    private List<Radio> mValues;

    public MyRadioRecyclerViewAdapter(Context c, List<Radio> items) {
        ctx = c;
        mValues = items;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentRadioItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull final ViewHolder holder, int position) {
        if (mValues != null) {
            holder.mItem = mValues.get(position);
            holder.tvRadio.setText(holder.mItem.getNombre());
            holder.tvCentro.setText(String.format("%s, (%s)", holder.mItem.getNombreCentro(), holder.mItem.getLocalidadCentro()));

            Glide.with(ctx)
                    .load(Constants.DOMAIN_URL + holder.mItem.getImagen())
                    .centerCrop()
                    .dontAnimate()
                    .into(holder.ivImagen);

            holder.mView.setOnClickListener(v -> {
                Activity activity = (Activity) ctx;
                activity.setTitle(holder.mItem.getNombre());
                activity.findViewById(R.id.list).setVisibility(View.GONE);
                activity.findViewById(R.id.progressBarRadio).setVisibility(View.VISIBLE);
                ((NavigationView) activity.findViewById(R.id.nav_view)).getMenu().findItem(R.id.nav_home).setChecked(false);
                ((FragmentActivity) ctx).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_content_dashboard, PodcastListFragment.newInstance(holder.mItem))
                        .commit();
            });
        }
    }

    public void setData(List<Radio> radioList) {
        this.mValues = radioList;
        notifyDataSetChanged(); // repintar la lista del adapter (refresco)
    }

    @Override
    public int getItemCount() {
        return mValues != null ? mValues.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView ivImagen;
        public final TextView tvRadio;
        public final TextView tvCentro;
        public Radio mItem;

        public ViewHolder(FragmentRadioItemBinding binding) {
            super(binding.getRoot());
            mView = binding.getRoot();
            ivImagen = binding.imageViewRadioImg;
            tvRadio = binding.textViewRadio;
            tvCentro = binding.textViewCentro;
        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + " '" + tvRadio.getText() + "'";
        }
    }
}