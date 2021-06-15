package dam.iesaugustobriga.radioeduandroid.ui.dashboard.podcast;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import dam.iesaugustobriga.radioeduandroid.R;
import dam.iesaugustobriga.radioeduandroid.common.Constants;
import dam.iesaugustobriga.radioeduandroid.common.MyApp;
import dam.iesaugustobriga.radioeduandroid.common.SharedPreferencesManager;
import dam.iesaugustobriga.radioeduandroid.data.RadioViewModel;
import dam.iesaugustobriga.radioeduandroid.databinding.DialogCommentBinding;
import dam.iesaugustobriga.radioeduandroid.models.Comentario;

public class NewCommentDialogFragment extends DialogFragment implements View.OnClickListener {

    private DialogCommentBinding binding;
    
    private ImageView ivClose, ivAvatar;
    private Button btnAddComment, btnEditComment;
    private EditText etMensaje;

    private Comentario comentario;

    private RadioViewModel radioViewModel;

    public static NewCommentDialogFragment newInstance(Comentario comentario) {
        NewCommentDialogFragment dialog = new NewCommentDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARG_COMMENT, comentario);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle); // estilo personal para que diálogo ocupe toda la pantalla

        radioViewModel = new ViewModelProvider(requireActivity())
                .get(RadioViewModel.class);

        if (getArguments() != null) {
            comentario = (Comentario) getArguments().getSerializable(Constants.ARG_COMMENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, 
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DialogCommentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        findViews();
        bindEvents();

        if (comentario != null) {
            btnEditComment.setVisibility(View.VISIBLE);
            btnAddComment.setVisibility(View.GONE);
            etMensaje.setText(comentario.getMensaje());
            etMensaje.setSelection(etMensaje.getText().length());
        }

        Glide.with(requireActivity())
                .load(Constants.DOMAIN_URL + SharedPreferencesManager.getStringValue(Constants.PREF_PICTURE_URL))
                .into(ivAvatar);

        return root;
    }

    private void findViews() {
        ivClose = binding.imageViewClose;
        ivAvatar = binding.imageViewAvatar;
        btnAddComment = binding.buttonAddComment;
        btnEditComment = binding.buttonEditComment;
        etMensaje = binding.editTextMensaje;
    }

    private void bindEvents() {
        ivClose.setOnClickListener(this);
        btnAddComment.setOnClickListener(this);
        btnEditComment.setOnClickListener(this);
    }

    private void showDialogConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage("¿Quieres cerrar la ventana? El comentario no será guardado.")
                .setTitle("Cancelar envío");
        builder.setPositiveButton("Sí", (dialog, id) -> {
            dialog.dismiss();
            Objects.requireNonNull(getDialog()).dismiss();
        });
        builder.setNegativeButton("No", (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String mensaje = etMensaje.getText().toString();
        if (id == R.id.buttonAddComment) {
            if ( mensaje.isEmpty() ) {
                MyApp.showSnackbar(requireActivity(), binding.getRoot().findViewById(R.id.layout), "¡No has escrito nada!", Snackbar.LENGTH_SHORT);
            } else {
                assert getTag() != null;
                radioViewModel.comment(mensaje, Long.parseLong(getTag()), requireActivity());
                Objects.requireNonNull(getDialog()).dismiss();
            }
        } else if (id == R.id.buttonEditComment) {
            if ( mensaje.isEmpty() ) {
                MyApp.showSnackbar(requireActivity(), binding.getRoot().findViewById(R.id.layout), "¡No has escrito nada!", Snackbar.LENGTH_SHORT);
            } else {
                assert getTag() != null;
                radioViewModel.updateComment(comentario.getId(), mensaje, comentario.getIdPodcast(), requireActivity());
                Objects.requireNonNull(getDialog()).dismiss();
            }
        } else if (id == R.id.imageViewClose) {
            if (comentario != null || mensaje.isEmpty()) {
                Objects.requireNonNull(getDialog()).dismiss();
            } else {
                showDialogConfirm();
            }
        }
    }

}
