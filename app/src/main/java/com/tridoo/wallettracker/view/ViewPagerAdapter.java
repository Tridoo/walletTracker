package com.tridoo.wallettracker.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.tridoo.wallettracker.MainActivity;
import com.tridoo.wallettracker.wallet.WalletFragment;
import org.jetbrains.annotations.NotNull;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final Context context;

    public ViewPagerAdapter(MainActivity activity) {
        super(activity);
        context = activity.getApplicationContext();
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new StockFragment(context);
            case 0:
            default:
                return new WalletFragment(context);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
