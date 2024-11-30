package com.tridoo.wallettracker.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.tridoo.wallettracker.R;
import com.tridoo.wallettracker.common.TransactionDto;
import com.tridoo.wallettracker.wallet.WalletController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class PopupOpenerListener implements View.OnClickListener {

    private final Context context;

    private final WalletController controller;

    private String currencyId;
    private String price;
    private final boolean refreshView;

    public PopupOpenerListener(Context context, WalletController controller, boolean refreshView) {
        this.context = context;
        this.controller = controller;
        this.refreshView = refreshView;
    }

    @Override
    public void onClick(View view) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup, null);
        currencyId = (String) view.getTag(R.string.currencyId);
        price = (String) view.getTag(R.string.price);

        fillTextViews(popupView);

        List<TransactionDto> transactions = getTransactions(currencyId);
        fillTransactions(popupView, transactions);

        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        Button btnBuy = popupView.findViewById(R.id.btn_buy);
        btnBuy.setOnClickListener(btn1 -> {
            controller.buy(popupView, refreshView);
            popupWindow.dismiss();
        });

        Button btnCancel = popupView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(btn2 -> popupWindow.dismiss());

        Button btnSell = popupView.findViewById(R.id.btn_sell);
        btnSell.setOnClickListener(btn3 -> {
            controller.sell(popupView, refreshView);
            popupWindow.dismiss();
        });

        Button btnSellAll = popupView.findViewById(R.id.btn_sell_all);
        btnSellAll.setOnClickListener(btn4 -> {
            controller.sellAll(currencyId);
            popupWindow.dismiss();
        });

        if (transactions.isEmpty()) {
            hideButtons(popupView, btnSell, btnSellAll);
        }

    }

    private void fillTextViews(View popupView) {
        TextView tvId = popupView.findViewById(R.id.tv_id);
        tvId.setText(currencyId);

        EditText etPrice = popupView.findViewById(R.id.et_price);
        etPrice.setText(price);
        etPrice.addTextChangedListener(new PriceTextChangedListener(popupView));

        EditText etQuantity = popupView.findViewById(R.id.et_quantity);
        etQuantity.addTextChangedListener(new QuantityTextChangedListener(popupView));
    }

    private void fillTransactions(View popupView, List<TransactionDto> transactions) {
        ScrollView sv = popupView.findViewById(R.id.sv_transactions);
        sv.setVisibility(View.VISIBLE);
        sv.getLayoutParams().height = Math.min(800, (transactions.size() + 4) * 50);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        TableLayout table = popupView.findViewById(R.id.tl_transactions);

        for (TransactionDto dto : transactions) {
            LocalDateTime time = Instant.ofEpochMilli(dto.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();

            TableRow tableRow = new TableRow(context);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            TextView tv1 = new TextView(context);
            tv1.setText(time.format(formatter));
            tv1.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            tableRow.addView(tv1);

            TextView tv2 = new TextView(context);
            tv2.setText(dto.getQuantity().toPlainString());
            tv2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tableRow.addView(tv2);

            TextView tv3 = new TextView(context);
            tv3.setText(dto.getValue().toPlainString());
            tv3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tableRow.addView(tv3);

            table.addView(tableRow, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    private List<TransactionDto> getTransactions(String currencyId) {
        return controller.getTransactions(currencyId);
    }

    private static void hideButtons(View popupView, Button btnSell, Button btnSellAll) {
        btnSellAll.setVisibility(View.INVISIBLE);
        btnSell.setVisibility(View.INVISIBLE);
        popupView.findViewById(R.id.btn_sell).setVisibility(View.INVISIBLE);
        popupView.findViewById(R.id.sv_transactions).setVisibility(View.INVISIBLE);
    }
}
