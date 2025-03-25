package com.qrcode.matcher;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private CheckBox checkboxManual;
    private TextView txtScanLabel;
    private EditText txtCtNr, txtPartNr1, txtDNr, txtQtty1;
    private EditText txtCName, txtPartNr2, txtCustN, txtQtty2, txtOrderNr;
    private AppCompatButton btnPlus1, btnPlus2;
    private AppCompatButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkboxManual = findViewById(R.id.checkboxManual);
        txtScanLabel = findViewById(R.id.txtScanLabel);

        txtCtNr = findViewById(R.id.txtCtNr);
        txtPartNr1 = findViewById(R.id.txtPartNr1);
        txtDNr = findViewById(R.id.txtDNr);
        txtQtty1 = findViewById(R.id.txtQtty1);

        txtCName = findViewById(R.id.txtCName);
        txtPartNr2 = findViewById(R.id.txtPartNr2);
        txtCustN = findViewById(R.id.txtCustN);
        txtQtty2 = findViewById(R.id.txtQtty2);
        txtOrderNr = findViewById(R.id.txtOrderNr);

        btnPlus1 = findViewById(R.id.btnPlus1);
        btnPlus2 = findViewById(R.id.btnPlus2);

        checkboxManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkManual();
            }
        });

        AppCompatButton btnViewData = findViewById(R.id.btnViewData);
        btnNext = findViewById(R.id.btnNext);
        AppCompatButton btnNew = findViewById(R.id.btnNew);
        AppCompatButton btnReset = findViewById(R.id.btnReset);

        btnViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });

        findViewById(R.id.btnSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        checkManual();
        initLeftScan();
        initRightScan();
    }

    private void checkManual() {
        if (checkboxManual.isChecked()) {
            txtCtNr.setEnabled(true);
            txtPartNr1.setEnabled(true);
            txtDNr.setEnabled(true);
            txtQtty1.setEnabled(true);

            txtCName.setEnabled(true);
            txtPartNr2.setEnabled(true);
            txtCustN.setEnabled(true);
            txtQtty2.setEnabled(true);
            txtOrderNr.setEnabled(true);
        } else {
            txtCtNr.setEnabled(false);
            txtPartNr1.setEnabled(false);
            txtDNr.setEnabled(false);
            txtQtty1.setEnabled(false);

            txtCName.setEnabled(false);
            txtPartNr2.setEnabled(false);
            txtCustN.setEnabled(false);
            txtQtty2.setEnabled(false);
            txtOrderNr.setEnabled(false);
        }
    }

    private void initLeftScan() {
        txtCtNr.setFocusable(true);
        txtCtNr.requestFocus();

        txtCtNr.post(new Runnable() {
            @Override
            public void run() {
                txtCtNr.setSelection(txtCtNr.getText().length());
            }
        });

        txtCtNr.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {

                if (keyCode==KeyEvent.KEYCODE_ENTER)
                {
                    // Just ignore the [Enter] key
                    return true;
                }
                // Handle all other keys in the default way
                return (keyCode == KeyEvent.KEYCODE_ENTER);
            }
        });
        txtCtNr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int count = txtCtNr.getText().toString().length() - txtCtNr.getText().toString().replaceAll("\\;","").length();

                if(count==4) {
                    txtPartNr1.setText(txtCtNr.getText().toString().split(";")[1]);
                    txtDNr.setText(txtCtNr.getText().toString().split(";")[2]);
                    txtQtty1.setText(txtCtNr.getText().toString().split(";")[3]);
                    txtCtNr.setText(txtCtNr.getText().toString().split(";")[0]);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initRightScan() {
        txtCName.setFocusable(true);
        txtCName.requestFocus();

        txtCName.post(new Runnable() {
            @Override
            public void run() {
                txtCName.setSelection(txtCName.getText().length());
            }
        });

        txtCName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {

                if (keyCode==KeyEvent.KEYCODE_ENTER)
                {
                    // Just ignore the [Enter] key
                    return true;
                }
                // Handle all other keys in the default way
                return (keyCode == KeyEvent.KEYCODE_ENTER);
            }
        });
        txtCName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int count = txtCName.getText().toString().length() - txtCName.getText().toString().replaceAll("\\;","").length();

                if(count == 5) {
                    txtPartNr2.setText(txtCName.getText().toString().split(";")[1]);
                    txtCustN.setText(txtCName.getText().toString().split(";")[2]);
                    txtQtty2.setText(txtCName.getText().toString().split(";")[3]);
                    txtOrderNr.setText(txtCName.getText().toString().split(";")[4]);
                    txtCName.setText(txtCName.getText().toString().split(";")[0]);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtPartNr2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!(txtPartNr2.getText().toString().isEmpty())) {
                    compare();
                }
            }
        });

        txtQtty2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!(txtQtty2.getText().toString().isEmpty())) {
                    compare();
                }
            }
        });
    }

    private void compare() {
        String strPartNr1 = txtPartNr1.getText().toString();
        String strPartNr2 = txtPartNr2.getText().toString();
        String strQtty1 = txtQtty1.getText().toString();
        String strQtty2 = txtQtty2.getText().toString();

        int result = 0;
        if (strPartNr1.equals(strPartNr2)) {
            result += 1;
            txtPartNr1.setBackgroundColor(Color.GREEN);
            txtPartNr2.setBackgroundColor(Color.GREEN);
        } else {
            txtPartNr1.setBackgroundColor(Color.RED);
            txtPartNr2.setBackgroundColor(Color.RED);
        }
        txtPartNr1.setTextColor(Color.WHITE);
        txtPartNr2.setTextColor(Color.WHITE);

        if (strQtty1.equals(strQtty2)) {
            result += 1;
            txtQtty1.setBackgroundColor(Color.GREEN);
            txtQtty2.setBackgroundColor(Color.GREEN);
        } else {
            txtQtty1.setBackgroundColor(Color.RED);
            txtQtty2.setBackgroundColor(Color.RED);
        }

        txtQtty1.setTextColor(Color.WHITE);
        txtQtty2.setTextColor(Color.WHITE);

        if (result == 2) {
            btnNext.setEnabled(true);
            txtScanLabel.setText(getString(R.string.match));
            txtScanLabel.setBackgroundColor(Color.GREEN);
            txtScanLabel.setTextColor(Color.WHITE);

            btnPlus1.setEnabled(false);
            btnPlus2.setEnabled(false);
        } else {
            btnNext.setEnabled(false);
            txtScanLabel.setText(getString(R.string.not_match));
            txtScanLabel.setBackgroundColor(Color.RED);
            txtScanLabel.setTextColor(Color.WHITE);

            btnPlus1.setEnabled(true);
            btnPlus2.setEnabled(true);
        }
    }

    private void next() {
        // upload


        reset();
    }

    private void reset() {
        checkboxManual.setChecked(false);
        txtScanLabel.setText(getString(R.string.scan_label));
        txtScanLabel.setBackgroundColor(Color.TRANSPARENT);
        txtScanLabel.setTextColor(Color.BLACK);

        txtCtNr.setText("");
        txtPartNr1.setText("");
        txtDNr.setText("");
        txtQtty1.setText("");

        txtCName.setText("");
        txtPartNr2.setText("");
        txtCustN.setText("");
        txtQtty2.setText("");
        txtOrderNr.setText("");

        txtPartNr1.setBackgroundResource(R.drawable.ed_bg);
        txtPartNr2.setBackgroundResource(R.drawable.ed_bg);
        txtQtty1.setBackgroundResource(R.drawable.ed_bg);
        txtQtty2.setBackgroundResource(R.drawable.ed_bg);
        txtPartNr1.setTextColor(Color.BLACK);
        txtPartNr2.setTextColor(Color.BLACK);
        txtQtty1.setTextColor(Color.BLACK);
        txtQtty2.setTextColor(Color.BLACK);

        btnNext.setEnabled(false);
        btnPlus1.setEnabled(false);
        btnPlus2.setEnabled(false);
    }
}