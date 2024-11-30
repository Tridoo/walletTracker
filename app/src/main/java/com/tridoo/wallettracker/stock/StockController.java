package com.tridoo.wallettracker.stock;

import android.content.Context;
import android.util.Log;
import androidx.core.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.tridoo.wallettracker.R;
import com.tridoo.wallettracker.view.PopupOpenerListener;
import com.tridoo.wallettracker.wallet.WalletController;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StockController {

    private final StockReader reader;

    private final Context context;
    private final PopupOpenerListener clickListener;
    private Deque<Pair<String, BigDecimal>> topCryptocurrenciesQueue;

    public StockController(Context context) {
        this.context = context;
        topCryptocurrenciesQueue = new ArrayDeque<>();
        reader = new StockReaderImpl();
        WalletController walletController = new WalletController(context);
        clickListener = new PopupOpenerListener(context, walletController, false);
    }

    public void fillView(View view) {
        TableLayout table = view.findViewById(R.id.tab_stock);
        List<StockDto> currenciesList = new ArrayList<>(getCurrenciesList());
        currenciesList.sort((a, b) -> (int) (b.getRoundedPrice() - a.getRoundedPrice()));
        currenciesList.forEach(dto -> addTableRow(table, dto));
    }

    private Queue<StockDto> getCurrenciesList() {
        Thread topWorker = new Thread(new StockTopWorker(reader, topCryptocurrenciesQueue));
        topWorker.start();
        try {
            topWorker.join();
        } catch (InterruptedException e) {
            Log.e(StockController.class.getName(), e.getMessage(), e);
        }

        Queue<StockDto> result = new ConcurrentLinkedQueue<>();

        Thread worker1 = new Thread(new StockWorker(reader, this, result));
        Thread worker2 = new Thread(new StockWorker(reader, this, result));
        worker1.start();
        worker2.start();
        try {
            worker1.join();
            worker2.join();
        } catch (InterruptedException e) {
            Log.e(StockController.class.getName(), e.getMessage(), e);
        }

        return result;
    }

    public synchronized Pair<String, BigDecimal> getNextCurrency() {
        return topCryptocurrenciesQueue.poll();
    }

    private void addTableRow(TableLayout table, StockDto dto) {
        TableRow tableRow = new TableRow(context);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        TextView currency = new TextView(context);
        currency.setText(dto.getCurrencyId());
        tableRow.addView(currency);

        TextView price = new TextView(context);
        price.setText(dto.getPrice());
        tableRow.addView(price);

        TextView change1D = new TextView(context);
        change1D.setText(dto.getDayChange());
        tableRow.addView(change1D);

        TextView change1W = new TextView(context);
        change1W.setText(dto.getWeekChange());
        tableRow.addView(change1W);

        Button btn = new Button(context);
        btn.setText(":::");
        btn.setTag(R.string.currencyId, dto.getCurrencyId());
        btn.setTag(R.string.price, dto.getPrice());
        btn.setBackground(context.getResources().getDrawable(R.drawable.rounded_button));
        btn.setOnClickListener(clickListener);
        tableRow.addView(btn);

        table.addView(tableRow, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

}
