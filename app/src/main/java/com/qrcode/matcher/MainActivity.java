package com.qrcode.matcher;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    public static final String FTP_HOST = "ftp_host";
    public static final String FTP_PORT = "ftp_port";
    public static final String FTP_USERNAME = "ftp_username";
    public static final String FTP_PASSWORD = "ftp_password";

    private CheckBox checkboxManual;
    private TextView txtScanLabel;
    private TextInputLayout txtCtNrField, txtPartNr1Field, txtDNrField, txtQtty1Field;
    private TextInputEditText txtCtNr, txtPartNr1, txtDNr, txtQtty1;

    private TextInputLayout txtCNameField, txtPartNr2Field, txtCustNField, txtQtty2Field, txtOrderNrField;
    private TextInputEditText txtCName, txtPartNr2, txtCustN, txtQtty2, txtOrderNr;

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

        txtCtNrField = findViewById(R.id.txtCtNrField);
        txtPartNr1Field = findViewById(R.id.txtPartNr1Field);
        txtDNrField = findViewById(R.id.txtDNrField);
        txtQtty1Field = findViewById(R.id.txtQtty1Field);

        txtCNameField = findViewById(R.id.txtCNameField);
        txtPartNr2Field = findViewById(R.id.txtPartNr2Field);
        txtCustNField = findViewById(R.id.txtCustNField);
        txtQtty2Field = findViewById(R.id.txtQtty2Field);
        txtOrderNrField = findViewById(R.id.txtOrderNrField);
        
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
                onNew();
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
                showSettingsDialog();
            }
        });

        checkManual();
        initLeftScan();
        initRightScan();
    }

    private void showSettingsDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_settings, null);
        builder.setView(dialogView)
                .setCancelable(true);
        AlertDialog dialog = builder.create();

        TextInputEditText txtHost = dialogView.findViewById(R.id.txtHostAddress);
        TextInputEditText txtPortNumber = dialogView.findViewById(R.id.txtPortNumber);
        TextInputEditText txtUserName = dialogView.findViewById(R.id.txtUserName);
        TextInputEditText txtPassword = dialogView.findViewById(R.id.txtPassword);

        MaterialButton btnSave = dialogView.findViewById(R.id.btnSave);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);

        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        txtHost.setText(sharedPreferences.getString(FTP_HOST, ""));
        txtPortNumber.setText(sharedPreferences.getString(FTP_PORT, ""));
        txtUserName.setText(sharedPreferences.getString(FTP_USERNAME, ""));
        txtPassword.setText(sharedPreferences.getString(FTP_PASSWORD, ""));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String hostAddress = txtHost.getText().toString();
                String portNumber = txtPortNumber.getText().toString();
                String username = txtUserName.getText().toString();
                String password = txtPassword.getText().toString();

                SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(FTP_HOST, hostAddress);
                editor.putString(FTP_PORT, portNumber);
                editor.putString(FTP_USERNAME, username);
                editor.putString(FTP_PASSWORD, password);
                editor.apply();

                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
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


        ColorStateList greenColors = new ColorStateList(
            new int[][]{
                new int[]{android.R.attr.state_focused}, // Focused
                new int[]{-android.R.attr.state_enabled}, // Disabled
                new int[]{} // Default
            },
            new int[]{
                Color.GREEN,
                Color.GREEN,
                Color.GREEN
            }
        );

        ColorStateList redColors = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_focused}, // Focused
                        new int[]{-android.R.attr.state_enabled}, // Disabled
                        new int[]{} // Default
                },
                new int[]{
                        Color.RED,
                        Color.RED,
                        Color.RED
                }
        );

        int result = 0;
        if (strPartNr1.equals(strPartNr2)) {
            result += 1;
            txtPartNr1Field.setBoxStrokeColorStateList(greenColors);
            txtPartNr2Field.setBoxStrokeColorStateList(greenColors);
        } else {
            txtPartNr1Field.setBoxStrokeColorStateList(redColors);
            txtPartNr2Field.setBoxStrokeColorStateList(redColors);
        }

        if (strQtty1.equals(strQtty2)) {
            result += 1;
            txtQtty1Field.setBoxStrokeColorStateList(greenColors);
            txtQtty2Field.setBoxStrokeColorStateList(greenColors);
        } else {
            txtQtty1Field.setBoxStrokeColorStateList(redColors);
            txtQtty2Field.setBoxStrokeColorStateList(redColors);
        }

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

    private void onNew() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("New")
                .setMessage("Are you sure to create new one?")
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
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

        // Or create manually based on theme attributes
        int colorPrimary = MaterialColors.getColor(this, android.R.attr.colorPrimary, "DefaultPrimary");
        int colorOnSurface = MaterialColors.getColor(this, android.R.attr.colorControlNormal, "DefaultOnSurface");

        ColorStateList defaultStrokeColors = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_focused}, // Focused
                        new int[]{-android.R.attr.state_enabled}, // Disabled
                        new int[]{} // Default
                },
                new int[]{
                        colorPrimary,
                        colorOnSurface,
                        colorOnSurface
                }
        );

        txtPartNr1Field.setBoxStrokeColorStateList(defaultStrokeColors);
        txtPartNr2Field.setBoxStrokeColorStateList(defaultStrokeColors);
        txtQtty1Field.setBoxStrokeColorStateList(defaultStrokeColors);
        txtQtty2Field.setBoxStrokeColorStateList(defaultStrokeColors);

        btnNext.setEnabled(false);
        btnPlus1.setEnabled(false);
        btnPlus2.setEnabled(false);
    }

    private void upload() {
        new Thread(() -> {
            String host = "ftp.yourserver.com";
            String username = "your_username";
            String password = "your_password";
            int port = 21; // Default FTP port
            String remoteDir = "/uploads/";
            String localPath = "/storage/emulated/0/Download/myfile.jpg";

            boolean uploaded = FTPUploader.uploadFile(
                    host, username, password, port, remoteDir, localPath);

            runOnUiThread(() -> {
                if (uploaded) {
                    Toast.makeText(this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}