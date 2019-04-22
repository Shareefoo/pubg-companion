package com.shareefoo.pubgcompanion.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.shareefoo.pubgcompanion.R;
import com.shareefoo.pubgcompanion.data.SpManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayerNameDialogActivity extends AppCompatActivity {

    @BindView(R.id.editText_playerName)
    EditText editTextPlayerName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog_username);
        setFinishOnTouchOutside(false);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_search)
    public void search(View view) {
        String playerName = editTextPlayerName.getText().toString();
        if (!TextUtils.isEmpty(playerName)) {
            Intent intent = new Intent();
            intent.putExtra(SpManager.KEY_PLAYER_NAME, playerName);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            editTextPlayerName.setError("Enter player name");
        }
    }

}
