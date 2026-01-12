package com.br.salesbuddy;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class MenuBottomSheet extends DialogFragment {

    private int userId;
    private boolean isRegisterScreen; // Variável para saber se estamos na tela de vendas

    // Adicionamos o boolean isRegisterScreen no construtor
    public static MenuBottomSheet newInstance(int userId, boolean isRegisterScreen) {
        MenuBottomSheet fragment = new MenuBottomSheet();
        Bundle args = new Bundle();
        args.putInt("ID_DO_LOJISTA", userId);
        args.putBoolean("IS_REGISTER_SCREEN", isRegisterScreen); // Salva a info
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt("ID_DO_LOJISTA", -1);
            isRegisterScreen = getArguments().getBoolean("IS_REGISTER_SCREEN", false);
        }
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams params = window.getAttributes();
                params.gravity = Gravity.TOP;
                params.dimAmount = 0.5f;
                params.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(params);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_menu_bottom_sheet, container, false);

        TextView btnOpcao1 = view.findViewById(R.id.tv_menu_register);
        TextView btnOpcao2 = view.findViewById(R.id.tv_menu_reprocess);
        TextView btnLogout = view.findViewById(R.id.tv_menu_logout);
        ImageView btnClose = view.findViewById(R.id.btn_close_menu);

        if (btnClose != null) btnClose.setOnClickListener(v -> dismiss());

        if (isRegisterScreen) {

            btnOpcao1.setText("HOME ");
            btnOpcao1.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("ID_DO_LOJISTA", userId);
                startActivity(intent);
                dismiss();
            });

            btnOpcao2.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), reprocessingAcitivity.class);
                intent.putExtra("ID_DO_LOJISTA", userId);
                startActivity(intent);
                dismiss();
            });

        } else {

            btnOpcao1.setText("REGISTRAR VENDA");
            btnOpcao1.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), registersalesActivity.class);
                intent.putExtra("ID_DO_LOJISTA", userId);
                startActivity(intent);
                dismiss();
            });

            btnOpcao2.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), reprocessingAcitivity.class);
                intent.putExtra("ID_DO_LOJISTA", userId);
                startActivity(intent);
                dismiss();
            });
        }

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), authenticationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(getActivity(), "Saiu com sucesso", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        return view;
    }
}