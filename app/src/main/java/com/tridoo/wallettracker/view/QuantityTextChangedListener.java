package com.tridoo.wallettracker.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.tridoo.wallettracker.R;

import java.math.BigDecimal;

public class QuantityTextChangedListener implements TextWatcher {

    private final View popupView;

    public QuantityTextChangedListener(View popupView) {
        this.popupView = popupView;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!popupView.findViewById(R.id.et_quantity).hasFocus() || charSequence.length() == 0) return;
        EditText etPrice = popupView.findViewById(R.id.et_price);
        BigDecimal price = new BigDecimal(etPrice.getText().toString());
        BigDecimal quantity = new BigDecimal(charSequence.toString());

        TextView etQuantity = popupView.findViewById(R.id.et_amount);
        etQuantity.setText(price.multiply(quantity).toPlainString());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
