package com.wallet.utilis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.yoyowallet.yoyo.YoyoException;
import com.yoyowallet.yoyo.api.YoyoCallback;
import com.yoyowallet.yoyo.api.model.Card;
import com.yoyowallet.yoyo.api.model.SessionData;
import com.yoyowallet.yoyo.card.CardManager;
import com.yoyowallet.yoyo.card.YoyoAddCardActivity;
import com.yoyowallet.yoyo.login.LoginManager;

/**
 * Created by Costin on 07/11/2015.
 */
public class card_info extends Activity implements YoyoCallback<Card>{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_info);
        CardManager.register(this);
        addCard();

    }
    // When you want to start the add card process call:
    private void addCard(){
        Intent intent = YoyoAddCardActivity.createAddCardActivityIntent(this);
        startActivityForResult(intent, CardManager.REQUEST_CODE);
    }

    // In order to handle callbacks, you need the following


    @Override
    protected void onDestroy() {
        CardManager.unregister( this);
        super.onDestroy();
    }


    public void onSuccess(Card card) {
        // The user has successfully added the provided card to their account
    }



    public void onCancel() {
        // user has closed the addCard activity
    }


    public void onError(YoyoException exception, Card noData) {
        // an error occurred -> you may display an error
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CardManager.onActivityResult(requestCode, resultCode, data);
    }
}

