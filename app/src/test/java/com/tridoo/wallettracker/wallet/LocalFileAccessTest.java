package com.tridoo.wallettracker.wallet;

import android.content.Context;
import android.util.Log;
import com.tridoo.wallettracker.wallet.LocalFileAccess;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocalFileAccessTest {

    @Mock
    Context ctxMock;
    @Mock
    File fileMock;
    private LocalFileAccess access;

    private static MockedStatic<Log> log;
    @BeforeAll
    public static void init() {
        log = mockStatic(Log.class);
    }

    @AfterAll
    public static void close() {
        log.close();
    }

    @BeforeEach
    public void initEach(){
        access = new LocalFileAccess(ctxMock);
    }

    @Test
    public void walletFileMissing() {
        when(ctxMock.getFileStreamPath(anyString())).thenReturn(fileMock);
        when(fileMock.exists()).thenReturn(false);

        List<String> result = access.readSavedData("missing");
        assertEquals(0, result.size());
    }

}