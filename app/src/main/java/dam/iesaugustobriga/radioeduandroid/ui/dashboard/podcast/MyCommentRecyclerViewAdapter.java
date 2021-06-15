package dam.iesaugustobriga.radioeduandroid.ui.dashboard.podcast;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
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
import dam.iesaugustobriga.radioeduandroid.common.MyApp;
import dam.iesaugustobriga.radioeduandroid.common.SharedPreferencesManager;
import dam.iesaugustobriga.radioeduandroid.data.RadioViewModel;
import dam.iesaugustobriga.radioeduandroid.models.Comentario;
import dam.iesaugustobriga.radioeduandroid.databinding.FragmentCommentBinding;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyCommentRecyclerViewAdapter extends RecyclerView.Adapter<MyCommentRecyclerViewAdapter.ViewHolder> {

    private final Context ctx;
    private List<Comentario> mValues;
    private RadioViewModel radioViewModel;

    public MyCommentRecyclerViewAdapter(Context c, List<Comentario> items) {
        ctx = c;
        mValues = items;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        radioViewModel = new ViewModelProvider((ViewModelStoreOwner) ctx)
                .get(RadioViewModel.class);
        return new ViewHolder(FragmentCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull final ViewHolder holder, int position) {
        if (mValues != null) {
            holder.mItem = mValues.get(position);

            if (holder.mItem.getRol().equalsIgnoreCase("administrador")) {
                holder.ivAvatar.setImageResource(R.drawable.ic_admin_account);
                holder.tvUsername.setText(R.string.admin);
            } else {
                Glide.with(ctx)
                        .load(Constants.DOMAIN_URL + holder.mItem.getImagen())
                        .centerCrop()
                        .dontAnimate()
                        .into(holder.ivAvatar);
                holder.tvUsername.setText(holder.mItem.getNombreUsuario());
            }

            holder.tvFecha.setText(holder.mItem.getFechaRegistro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy | HH:mm:ss")));
            holder.tvMensaje.setText(holder.mItem.getMensaje());

            if (String.valueOf(holder.mItem.getIdUsuario()).equals(SharedPreferencesManager.getStringValue(Constants.PREF_ID))) {
                holder.ivEdit.setVisibility(View.VISIBLE);
                holder.ivDelete.setVisibility(View.VISIBLE);

                holder.ivEdit.setOnClickListener(v -> NewCommentDialogFragment.newInstance(holder.mItem)
                        .show(((FragmentActivity) ctx).getSupportFragmentManager(), "NewCommentDialogFragment"));

                holder.ivDelete.setOnClickListener(v -> {
                    Activity activity = (Activity) ctx;
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setCancelable(false);
                    builder.setMessage("¿Estás seguro de que deseas borrar el mensaje?");
                    builder.setPositiveButton("Sí", (dialog, which) -> {
                        radioViewModel.deleteComment(holder.mItem.getId(), holder.mItem.getIdPodcast(), (Activity) ctx);
                        dialog.dismiss();
                    });
                    builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

                    AlertDialog dialog = builder.create();
                    MyApp.setAlertDialogStyle(dialog, activity);
                    dialog.show();
                });
            } else {
                holder.ivEdit.setVisibility(View.GONE);
                holder.ivDelete.setVisibility(View.GONE);
            }
        }
    }

    public void setData(List<Comentario> comentarioList) {
        this.mValues = comentarioList;
        notifyDataSetChanged(); // repintar la lista del adapter (refresco)
    }

    @Override
    public int getItemCount() {
        return mValues != null ? mValues.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView ivAvatar;
        public final TextView tvUsername;
        public final TextView tvFecha;
        public final TextView tvMensaje;
        public final ImageView ivEdit;
        public final ImageView ivDelete;
        public Comentario mItem;

        public ViewHolder(FragmentCommentBinding binding) {
            super(binding.getRoot());
            mView = binding.getRoot();
            ivAvatar = binding.imageViewAvatar;
            tvUsername = binding.textViewUsername;
            tvFecha = binding.textViewFechaCom;
            tvMensaje = binding.textViewMessage;
            ivEdit = binding.imageViewEdit;
            ivDelete = binding.imageViewDelete;
        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + " '" + tvUsername.getText() + "'";
        }
    }
}