package com.netflixcloneui.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.netflixcloneui.R;
import com.netflixcloneui.adapter.AvatarAdapter;
import com.netflixcloneui.utils.OnAvatarSelectedListener;

import java.util.Arrays;
import java.util.List;

public class EditAvatarBottomSheetFragment extends BottomSheetDialogFragment {
    private AvatarAdapter baseAvatarAdapter;
    private AvatarAdapter arcaneAvatarAdapter;
    private RecyclerView rcvClassic, rcvArcane, rcvOnePiece, rcvSquidGame, rcvStrangerThings;
    private MaterialCardView btnClose;
    private OnAvatarSelectedListener avatarSelectedListener;

    public void setOnAvatarSelectedListener(OnAvatarSelectedListener listener) {
        this.avatarSelectedListener = listener;
    }

    private final List<String> baseAvatars = Arrays.asList(
            "avatar_1", "avatar_2", "avatar_3"
    );

    private final List<String> arcaneAvatars = Arrays.asList(
            "avatar_arcane_1", "avatar_arcane_2", "avatar_arcane_3", "avatar_arcane_4", "avatar_arcane_5", "avatar_arcane_6"
    );

    private final List<String> onePieceAvatars = Arrays.asList(
            "avatar_onepiece_1", "avatar_onepiece_2", "avatar_onepiece_3", "avatar_onepiece_4", "avatar_onepiece_5", "avatar_onepiece_6"
    );

    private final List<String> squidGameAvatars = Arrays.asList(
            "avatar_squidgame_1", "avatar_squidgame_2", "avatar_squidgame_3", "avatar_squidgame_4"
    );

    private final List<String> strangerThingsAvatars = Arrays.asList(
            "avatar_strangerthings_1", "avatar_strangerthings_2", "avatar_strangerthings_3", "avatar_strangerthings_4", "avatar_strangerthings_5"
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_avatar_bottom_sheet, container, false);

        rcvClassic = view.findViewById(R.id.rcvClassic);
        rcvArcane = view.findViewById(R.id.rcvArcane);
        rcvOnePiece = view.findViewById(R.id.rcvOnePiece);
        rcvSquidGame = view.findViewById(R.id.rcvSquidGame);
        rcvStrangerThings = view.findViewById(R.id.rcvStrangerThings);
        btnClose = view.findViewById(R.id.btnClose);

        btnClose.setOnClickListener(v -> dismiss());

        setupRecyclerViews();

        return view;
    }

    private void setupRecyclerViews() {
        baseAvatarAdapter = new AvatarAdapter(baseAvatars, true, avatarName -> {
            if (avatarSelectedListener != null) {
                avatarSelectedListener.onAvatarSelected(avatarName);
                dismiss(); // đóng BottomSheet
            }
        });

        arcaneAvatarAdapter = new AvatarAdapter(arcaneAvatars, false,avatarName -> {
            if (avatarSelectedListener != null) {
                avatarSelectedListener.onAvatarSelected(avatarName);
                dismiss();
            }
        });

        rcvClassic.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rcvClassic.setAdapter(baseAvatarAdapter);

        rcvArcane.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rcvArcane.setAdapter(arcaneAvatarAdapter);

        rcvOnePiece.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rcvOnePiece.setAdapter(new AvatarAdapter(onePieceAvatars, false, avatarName -> {
            if (avatarSelectedListener != null) {
                avatarSelectedListener.onAvatarSelected(avatarName);
                dismiss();
            }
        }));

        rcvSquidGame.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rcvSquidGame.setAdapter(new AvatarAdapter(squidGameAvatars, false, avatarName -> {
            if (avatarSelectedListener != null) {
                avatarSelectedListener.onAvatarSelected(avatarName);
                dismiss();
            }
        }));

        rcvStrangerThings.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rcvStrangerThings.setAdapter(new AvatarAdapter(strangerThingsAvatars, false, avatarName -> {
            if (avatarSelectedListener != null) {
                avatarSelectedListener.onAvatarSelected(avatarName);
                dismiss();
            }
        }));
    }
}
