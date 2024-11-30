package com.tridoo.wallettracker.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.tridoo.wallettracker.R;
import com.tridoo.wallettracker.stock.StockController;
import org.jetbrains.annotations.NotNull;


public class StockFragment extends Fragment {

    private final StockController stockController;

    public StockFragment(Context context) {
        this.stockController = new StockController(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stock, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        stockController.fillView(view);
    }
}