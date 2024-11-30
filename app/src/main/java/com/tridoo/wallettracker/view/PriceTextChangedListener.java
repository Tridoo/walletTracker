package com.tridoo.wallettracker.view;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.tridoo.wallettracker.R;

import java.math.BigDecimal;

public class PriceTextChangedListener implements TextWatcher {

    private final View popupView;

    public PriceTextChangedListener(View popupView) {
        this.popupView = popupView;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!popupView.findViewById(R.id.et_price).hasFocus()
                || charSequence.length() == 0) return;

        BigDecimal price = new BigDecimal(charSequence.toString());

        EditText etQuantity = popupView.findViewById(R.id.et_quantity);
        BigDecimal quantity = new BigDecimal(etQuantity.getText().toString());

        TextView etAmount = popupView.findViewById(R.id.et_amount);
        etAmount.setText(price.multiply(quantity).toPlainString());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
