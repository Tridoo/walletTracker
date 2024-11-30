package com.tridoo.wallettracker.wallet;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.tridoo.wallettracker.R;
import org.jetbrains.annotations.NotNull;

public class WalletFragment extends Fragment {

    private final WalletController walletController;

    public WalletFragment(Context context) {
        walletController = new WalletController(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        walletController.setView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        walletController.fillView();
    }
}