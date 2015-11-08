package com.wallet.utilis;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yoyowallet.yoyo.views.BarcodeView;

public class QR_code extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        getWindow().getDecorView().setBackgroundColor(Color.WHITE);

        BarcodeView  barcodeView = (BarcodeView) findViewById(R.id.barcode_view);
        barcodeView.showBarcodePayment();
    }
}
