package com.tridoo.wallettracker.wallet;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.tridoo.wallettracker.R;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletControlerTest {

    @Mock
    private WalletRepo repoMock;
    @Mock
    private View viewMock;
    @Mock
    private TextView tvMock;
    @Mock
    private TextView tvMock2;
    @Mock
    private Context ctxMock;

    private static MockedStatic<Log> log;

    @Mock
    private TableLayout tblMock;

    @BeforeAll
    public static void init() {
        log = mockStatic(Log.class);
    }

    @AfterAll
    public static void close() {
        log.close();
    }

    @Test
    public void sellNotAllTest() {
        WalletController controller = new WalletController(ctxMock);
        controller.setRepo(repoMock);

        when(repoMock.getWallet()).thenReturn(getWallet());
        when(tvMock.getText()).thenReturn("11");
        when(tvMock2.getText()).thenReturn("btc");

        when(viewMock.findViewById(R.id.et_amount)).thenReturn(tvMock);
        when(viewMock.findViewById(R.id.et_quantity)).thenReturn(tvMock);
        when(viewMock.findViewById(R.id.et_price)).thenReturn(tvMock);

        when(viewMock.findViewById(R.id.tv_id)).thenReturn(tvMock2);

        controller.sell(viewMock, false);

        verify(repoMock).addTransaction("btc", BigDecimal.valueOf(11).negate(), BigDecimal.valueOf(11).negate());
        verify(repoMock).updateWalletEntry("btc", BigDecimal.valueOf(11).negate(), BigDecimal.valueOf(11).negate());
    }
    @Test
    public void buyTest() {
        WalletController controller = new WalletController(ctxMock);
        controller.setRepo(repoMock);

        when(tvMock.getText()).thenReturn("11");
        when(tvMock2.getText()).thenReturn("btc");

        when(viewMock.findViewById(R.id.et_amount)).thenReturn(tvMock);
        when(viewMock.findViewById(R.id.et_quantity)).thenReturn(tvMock);
        when(viewMock.findViewById(R.id.et_price)).thenReturn(tvMock);

        when(viewMock.findViewById(R.id.tv_id)).thenReturn(tvMock2);

        controller.buy(viewMock, false);

        verify(repoMock).addTransaction("btc", BigDecimal.valueOf(11), BigDecimal.valueOf(11));
        verify(repoMock).updateWalletEntry("btc", BigDecimal.valueOf(11), BigDecimal.valueOf(11));
    }

    @Test
    public void buyAndRefreshTest() {
        WalletController controller = new WalletController(ctxMock);
        controller.setRepo(repoMock);
        controller.setView(viewMock);

        when(tvMock.getText()).thenReturn("11");
        when(tvMock2.getText()).thenReturn("btc");

        when(viewMock.findViewById(R.id.et_amount)).thenReturn(tvMock);
        when(viewMock.findViewById(R.id.et_quantity)).thenReturn(tvMock);
        when(viewMock.findViewById(R.id.et_price)).thenReturn(tvMock);
        when(viewMock.findViewById(R.id.tv_id)).thenReturn(tvMock2);

        when(viewMock.findViewById(R.id.tab_wallet)).thenReturn(tblMock);
        when(tblMock.getChildCount()).thenReturn(0);

        when(viewMock.findViewById(R.id.tv_balance)).thenReturn(tvMock2);

        controller.buy(viewMock, true);

        verify(repoMock).addTransaction("btc", BigDecimal.valueOf(11), BigDecimal.valueOf(11));
        verify(repoMock).updateWalletEntry("btc", BigDecimal.valueOf(11), BigDecimal.valueOf(11));
        verify(repoMock).getWallet();
    }

    private List<WalletEntry> getWallet() {
        List<WalletEntry> result = new ArrayList<>();
        result.add(new WalletEntry("btc", BigDecimal.valueOf(12), BigDecimal.TEN));
        return result;
    }
}
