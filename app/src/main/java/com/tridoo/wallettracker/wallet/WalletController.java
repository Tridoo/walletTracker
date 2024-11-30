package com.tridoo.wallettracker.wallet;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.tridoo.wallettracker.*;
import com.tridoo.wallettracker.common.TransactionDto;
import com.tridoo.wallettracker.stock.StockReader;
import com.tridoo.wallettracker.stock.StockReaderImpl;
import com.tridoo.wallettracker.view.PopupOpenerListener;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class WalletController {

    private WalletRepo repo;
    private final StockReader stockReader;

    private final Context context;

    private View view;

    private boolean isFilled;
    private final PopupOpenerListener clickListener;


    public WalletController(Context context) {
        this.context = context;
        repo = new WalletRepoImpl(context);
        stockReader = new StockReaderImpl();
        clickListener = new PopupOpenerListener(context, this, true);
    }

    public void setView(View view) {
        this.view = view;
    }

    //4 test
    public void setRepo(WalletRepo repo) {
        this.repo = repo;
    }

    public void fillView() {
        if (isFilled) {
            //oszczędność wywołań API
            refreshViewOffline();
            return;
        }
        TableLayout table = view.findViewById(R.id.tab_wallet);
        if (table.getChildCount() > 1)
            table.removeViews(1, table.getChildCount() - 1);

        List<WalletDto> walletData = getWalletViewData();
        walletData.forEach(dto -> addTableRow(table, dto));

        fillBalance(view, walletData);
        isFilled = true;
    }

    private void refreshViewOffline() {
        TableLayout table = view.findViewById(R.id.tab_wallet);
        if (table.getChildCount() > 1)
            table.removeViews(1, table.getChildCount() - 1);

        List<WalletEntry> wallet = repo.getWallet();

        List<WalletDto> walletData = wallet.stream()
                .map(entry -> new WalletDto(entry.getCurrencyId(), entry.getQuantity(), entry.getAvgPrice(),
                        Optional.ofNullable(PricesHolder.getInstance().getPrice(entry.getCurrencyId()))))
                .collect(Collectors.toList());

        walletData.forEach(dto -> addTableRow(table, dto));

        fillBalance(view, walletData);
    }

    private List<WalletDto> getWalletViewData() {
        List<WalletDto> result = new ArrayList<>();
        List<WalletEntry> wallet = repo.getWallet();
        Set<String> cryptoIds = wallet.stream()
                .map(WalletEntry::getCurrencyId)
                .collect(Collectors.toSet());

        Map<String, BigDecimal> workerResult = new HashMap<>();
        Thread worker = new Thread(new WalletWorker(stockReader, cryptoIds, workerResult));
        worker.start();
        try {
            worker.join();
        } catch (InterruptedException e) {
            Log.e(WalletController.class.getName(), e.getMessage(), e);
        }

        for (WalletEntry entry : wallet) {
            Optional<BigDecimal> price = Optional.ofNullable(workerResult.get(entry.getCurrencyId()));
            PricesHolder.getInstance().addPrice(entry.getCurrencyId(), price.orElse(BigDecimal.ZERO));
            result.add(new WalletDto(entry.getCurrencyId(), entry.getQuantity(), entry.getAvgPrice(), price));
        }
        return result;
    }

    public void buy(View popupView, boolean refreshView) {
        BigDecimal amount = getBigDecimalFromView(popupView, R.id.et_amount);
        if (amount.equals(BigDecimal.ZERO)) return;

        BigDecimal quantity = getBigDecimalFromView(popupView, R.id.et_quantity);
        BigDecimal price = getBigDecimalFromView(popupView, R.id.et_price);

        TextView tvId = popupView.findViewById(R.id.tv_id);
        String currencyId = tvId.getText().toString();

        PricesHolder.getInstance().addPrice(currencyId, price);
        repo.addTransaction(currencyId, quantity, amount);
        repo.updateWalletEntry(currencyId, quantity, amount);
        if (refreshView) refreshViewOffline();
    }

    public void sell(View popupView, boolean refreshView) {
        BigDecimal amount = getBigDecimalFromView(popupView, R.id.et_amount);
        if (amount.equals(BigDecimal.ZERO)) return;

        BigDecimal quantity = getBigDecimalFromView(popupView, R.id.et_quantity);
        BigDecimal price = getBigDecimalFromView(popupView, R.id.et_price);

        TextView tvId = popupView.findViewById(R.id.tv_id);
        String currencyId = tvId.getText().toString();

        PricesHolder.getInstance().addPrice(currencyId, price);

        Optional<WalletEntry> entry = repo.getWallet().stream().filter(e -> e.getCurrencyId().equals(currencyId)).findAny();
        if (!entry.isPresent()) return;
        BigDecimal walletQuantity = entry.get().getQuantity();
        if (quantity.compareTo(walletQuantity) > 0) {
            Log.i(WalletController.class.getName(), "próba nadsprzedaży");
            Toast.makeText(context, "Sell all", Toast.LENGTH_LONG).show();
            repo.sellAll(currencyId);
            refreshViewOffline();
            return;
        }

        repo.addTransaction(currencyId, quantity.negate(), amount.negate());
        repo.updateWalletEntry(currencyId, quantity.negate(), amount.negate());
        if (refreshView) refreshViewOffline();
    }

    @NotNull
    private BigDecimal getBigDecimalFromView(View popupView, int viewId) {
        TextView etQuantity = popupView.findViewById(viewId);
        return new BigDecimal(etQuantity.getText().toString());
    }

    public List<TransactionDto> getTransactions(String currencyId) {
        return repo.getTransactions(currencyId);
    }

    public void sellAll(String currencyId) {
        repo.sellAll(currencyId);
        refreshViewOffline();
    }

    private void addTableRow(TableLayout table, WalletDto dto) {
        TableRow tableRow = new TableRow(context);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        TextView currency = new TextView(context);
        currency.setText(dto.getCurrencyId());
        tableRow.addView(currency);

        TextView avgPrice = new TextView(context);
        avgPrice.setText(dto.getAvgPrice());
        tableRow.addView(avgPrice);

        TextView change = new TextView(context);
        change.setText(dto.getChange());
        tableRow.addView(change);

        TextView profit = new TextView(context);
        profit.setText(dto.getProfit().toString());
        tableRow.addView(profit);

        Button btn = new Button(context);
        btn.setText(":::");
        btn.setTag(R.string.currencyId, dto.getCurrencyId());
        btn.setTag(R.string.price, dto.getFormattedPrice());
        btn.setBackground(context.getResources().getDrawable(R.drawable.rounded_button));
        btn.setOnClickListener(clickListener);
        tableRow.addView(btn);

        table.addView(tableRow, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }

    private static void fillBalance(View view, List<WalletDto> walletData) {
        TextView tvBalance = view.findViewById(R.id.tv_balance);
        BigDecimal balance = walletData.stream()
                .map(WalletDto::getProfit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        tvBalance.setText(String.valueOf(balance.longValue()));
    }
}
