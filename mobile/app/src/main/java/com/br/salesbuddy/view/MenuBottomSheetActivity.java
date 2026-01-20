package com.br.salesbuddy.view;

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

import com.br.salesbuddy.R;
import com.br.salesbuddy.contract.MenuContract;
import com.br.salesbuddy.presenter.MenuPresenter;

public class MenuBottomSheetActivity extends DialogFragment implements MenuContract.View {

    private MenuContract.Presenter presenter;
    private TextView btnOpcao1;
    private TextView btnOpcao2;
    private TextView btnLogout;
    private ImageView btnClose;

    public static MenuBottomSheetActivity newInstance(int userId, boolean isRegisterScreen) {
        MenuBottomSheetActivity fragment = new MenuBottomSheetActivity();
        Bundle args = new Bundle();
        args.putInt("ID_DO_LOJISTA", userId);
        args.putBoolean("IS_REGISTER_SCREEN", isRegisterScreen);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        presenter = new MenuPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_menu_bottom_sheet, container, false);

        btnOpcao1 = view.findViewById(R.id.tv_menu_register);
        btnOpcao2 = view.findViewById(R.id.tv_menu_reprocess);
        btnLogout = view.findViewById(R.id.tv_menu_logout);
        btnClose = view.findViewById(R.id.btn_close_menu);

        int userId = -1;
        boolean isRegisterScreen = false;
        if (getArguments() != null) {
            userId = getArguments().getInt("ID_DO_LOJISTA", -1);
            isRegisterScreen = getArguments().getBoolean("IS_REGISTER_SCREEN", false);
        }

        presenter.init(userId, isRegisterScreen);

        btnOpcao1.setOnClickListener(v -> presenter.onOption1Clicked());
        btnOpcao2.setOnClickListener(v -> presenter.onOption2Clicked());
        btnLogout.setOnClickListener(v -> presenter.onLogoutClicked());
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> presenter.onCloseClicked());
        }

        return view;
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

    @Override
    public void setOption1Text(String text) {
        if (btnOpcao1 != null) {
            btnOpcao1.setText(text);
        }
    }

    @Override
    public void navigateToHome(int userId) {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("ID_DO_LOJISTA", userId);
        startActivity(intent);
    }

    @Override
    public void navigateToRegisterSales(int userId) {
        Intent intent = new Intent(getActivity(), RegisterSalesActivity.class);
        intent.putExtra("ID_DO_LOJISTA", userId);
        startActivity(intent);
    }

    @Override
    public void navigateToReprocess(int userId) {
        Intent intent = new Intent(getActivity(), ReprocessingActivity.class);
        intent.putExtra("ID_DO_LOJISTA", userId);
        startActivity(intent);
    }

    @Override
    public void navigateToLogin() {
        Intent intent = new Intent(getActivity(), AuthenticationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToConnectionError() {

    }

    @Override
    public void closeMenu() {
        dismiss();
    }
}