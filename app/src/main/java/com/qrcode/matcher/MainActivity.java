package com.qrcode.matcher;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String FTP_HOST = "ftp_host";
    private static final String FTP_PORT = "ftp_port";
    private static final String FTP_USERNAME = "ftp_username";
    private static final String FTP_PASSWORD = "ftp_password";
    private static final String IS_MANUAL = "is_manual";
    private static final String SCANNED_NUMBER = "scanned_number";


    private TextView txtScanLabel;
    private TextView txtScannedNumber;
    private TextInputEditText txtCtNr, txtPartNr1, txtDNr, txtQtty1;

    private TextInputEditText txtCName, txtPartNr2, txtCustN, txtQtty2, txtOrderNr;

    private RecyclerView listSmallLabels, listBigLabels;
    private AppCompatButton btnPlus1, btnPlus2;
    private AppCompatButton btnNext;

//    private ArrayList<HashMap<String, String>> arrSmallLabels = new ArrayList<>();
//    private ArrayList<HashMap<String, String>> arrBigLabels = new ArrayList<>();
    LabelsAdapter smallListAdapter = new LabelsAdapter(new ArrayList<>());
    LabelsAdapter bigListAdapter = new LabelsAdapter(new ArrayList<>());

    private ActivityResultLauncher<String> storagePermissionLauncher;
    private ActivityResultLauncher<Intent> manageStorageLauncher;

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

        txtScanLabel = findViewById(R.id.txtScanLabel);
        txtScannedNumber = findViewById(R.id.txtScannedNumber);

        txtCtNr = findViewById(R.id.txtCtNr);
        txtPartNr1 = findViewById(R.id.txtPartNr1);
        txtDNr = findViewById(R.id.txtDNr);
        txtQtty1 = findViewById(R.id.txtQtty1);

        txtCName = findViewById(R.id.txtCName);
        txtPartNr2 = findViewById(R.id.txtPartNr2);
        txtCustN = findViewById(R.id.txtCustN);
        txtQtty2 = findViewById(R.id.txtQtty2);
        txtOrderNr = findViewById(R.id.txtOrderNr);

        listSmallLabels = findViewById(R.id.smallLabelListView);
        listBigLabels = findViewById(R.id.bigLabelListView);
        btnPlus1 = findViewById(R.id.btnPlus1);
        btnPlus2 = findViewById(R.id.btnPlus2);

        smallListAdapter = new LabelsAdapter(new ArrayList<>());
        bigListAdapter = new LabelsAdapter(new ArrayList<>());

        listSmallLabels.setLayoutManager(new LinearLayoutManager(this));
        listSmallLabels.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        listSmallLabels.setAdapter(smallListAdapter);

        listBigLabels.setLayoutManager(new LinearLayoutManager(this));
        listBigLabels.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        listBigLabels.setAdapter(bigListAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        int scannedNumber = sharedPreferences.getInt(SCANNED_NUMBER, 0);
        txtScannedNumber.setText(String.valueOf(scannedNumber));

        AppCompatButton btnViewData = findViewById(R.id.btnViewData);
        btnNext = findViewById(R.id.btnNext);
        AppCompatButton btnNew = findViewById(R.id.btnNew);
        AppCompatButton btnReset = findViewById(R.id.btnReset);

        btnPlus1.setOnClickListener(view -> {
            showAddLabelDialog(true);
        });

        btnPlus2.setOnClickListener(view -> {
            showAddLabelDialog(false);
        });

        btnViewData.setOnClickListener(view -> {
            Intent intent = new Intent(this, ViewDataActivity.class);
            startActivity(intent);
        });

        btnNext.setOnClickListener(view -> next());

        btnNew.setOnClickListener(view -> onNew());

        btnReset.setOnClickListener(view -> reset());

        findViewById(R.id.btnSettings).setOnClickListener(view -> showSettingsDialog());

        checkManual();
        initLeftScan();
        initRightScan();

        storagePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Toast.makeText(this, "Storage Permission Allowed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                });

        manageStorageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (Environment.isExternalStorageManager()) {
                            Toast.makeText(this, "Storage Permission Allowed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Manage Storage Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        checkPermissions();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                storagePermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!android.os.Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + getPackageName()));
                manageStorageLauncher.launch(intent);
            }
        } else {
            Dexter.withContext(getApplicationContext())
                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        }
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
        CheckBox checkboxManual = dialogView.findViewById(R.id.checkboxManual);

        MaterialButton btnSave = dialogView.findViewById(R.id.btnSave);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);

        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        txtHost.setText(sharedPreferences.getString(FTP_HOST, ""));
        txtPortNumber.setText(String.valueOf(sharedPreferences.getInt(FTP_PORT, 21)));
        txtUserName.setText(sharedPreferences.getString(FTP_USERNAME, ""));
        txtPassword.setText(sharedPreferences.getString(FTP_PASSWORD, ""));
        checkboxManual.setChecked(sharedPreferences.getBoolean(IS_MANUAL, false));

        btnSave.setOnClickListener(view -> {

            String hostAddress = txtHost.getText().toString();
            String portNumber = txtPortNumber.getText().toString();
            int port = 0;
            if (!portNumber.isEmpty()) {
                port = Integer.parseInt(portNumber);
            }
            String username = txtUserName.getText().toString();
            String password = txtPassword.getText().toString();
            boolean isManual = checkboxManual.isChecked();

            SharedPreferences sharedPreferences1 = getSharedPreferences(getPackageName(), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            editor.putString(FTP_HOST, hostAddress);
            editor.putInt(FTP_PORT, port);
            editor.putString(FTP_USERNAME, username);
            editor.putString(FTP_PASSWORD, password);
            editor.putBoolean(IS_MANUAL, isManual);
            editor.apply();

            checkManual();

            dialog.dismiss();
        });

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

    private void checkManual() {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        boolean isManual = sharedPreferences.getBoolean(IS_MANUAL, false);
        if (isManual) {
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

        txtCtNr.post(() -> txtCtNr.setSelection(txtCtNr.getText().length()));

        txtCtNr.setOnKeyListener((view, keyCode, event) -> {

            if (keyCode==KeyEvent.KEYCODE_ENTER)
            {
                // Just ignore the [Enter] key
                return true;
            }
            // Handle all other keys in the default way
            return (keyCode == KeyEvent.KEYCODE_ENTER);
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

        txtPartNr1.addTextChangedListener(new TextWatcher() {
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

        txtQtty1.addTextChangedListener(new TextWatcher() {
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

    private void initRightScan() {
        txtCName.setFocusable(true);

        txtCName.post(() -> txtCName.setSelection(txtCName.getText().length()));

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
            txtPartNr1.setBackgroundTintList(greenColors);
            txtPartNr2.setBackgroundTintList(greenColors);
        } else {
            txtPartNr1.setBackgroundTintList(redColors);
            txtPartNr2.setBackgroundTintList(redColors);
        }

        if (strQtty1.equals(strQtty2)) {
            result += 1;
            txtQtty1.setBackgroundTintList(greenColors);
            txtQtty2.setBackgroundTintList(greenColors);
        } else {
            txtQtty1.setBackgroundTintList(redColors);
            txtQtty2.setBackgroundTintList(redColors);
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

    private void showAddLabelDialog(boolean isLeft) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_label, null);
        builder.setView(dialogView)
                .setCancelable(true);
        AlertDialog dialog = builder.create();

        TextInputLayout cartonNameField = dialogView.findViewById(R.id.txtCartonNameField);
        TextInputLayout partNrField = dialogView.findViewById(R.id.txtPartNrField);
        TextInputLayout dNrField = dialogView.findViewById(R.id.txtDNrField);
        TextInputLayout quantityField = dialogView.findViewById(R.id.txtQuantityField);
        TextInputLayout orderNrField = dialogView.findViewById(R.id.txtOrderNrField);

        TextInputEditText txtDialogCartonName = dialogView.findViewById(R.id.txtCartonName);
        TextInputEditText txtDialogPartNr = dialogView.findViewById(R.id.txtPartNr);
        TextInputEditText txtDialogDNr = dialogView.findViewById(R.id.txtDNr);
        TextInputEditText txtDialogQuantity = dialogView.findViewById(R.id.txtQuantity);
        TextInputEditText txtDialogOrderNr = dialogView.findViewById(R.id.txtOrderNr);

        MaterialButton btnAdd = dialogView.findViewById(R.id.btnAdd);
        MaterialButton btnClear = dialogView.findViewById(R.id.btnClear);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);

        if (isLeft) {
            orderNrField.setVisibility(GONE);
            cartonNameField.setHint(getString(R.string.ct_nr));
            partNrField.setHint(getString(R.string.part_nr));
            dNrField.setHint(getString(R.string.d_nr));
            quantityField.setHint(R.string.qtty);
        } else {
            orderNrField.setVisibility(VISIBLE);
            cartonNameField.setHint(getString(R.string.c_name));
            partNrField.setHint(getString(R.string.part_nr));
            dNrField.setHint(getString(R.string.cust_n));
            quantityField.setHint(R.string.qtty);
            orderNrField.setHint(R.string.order_nr);
        }

        txtDialogCartonName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (txtDialogCartonName.getText().toString().isEmpty()) {
                    btnAdd.setEnabled(false);
                } else {
                    btnAdd.setEnabled(true);
                }
            }
        });

        btnAdd.setOnClickListener(view -> {

            String cartonName = txtDialogCartonName.getText().toString();
            String partNr = txtDialogPartNr.getText().toString();
            String dNr = txtDialogDNr.getText().toString();
            String quantity = txtDialogQuantity.getText().toString();
            String orderNr = isLeft ? "" : txtDialogOrderNr.getText().toString();

            if (isLeft) {
                HashMap<String, String> smallLabelData = new HashMap<>();
                smallLabelData.put(Utils.CARTON_NR, txtCtNr.getText().toString());
                smallLabelData.put(Utils.PART_NR, txtPartNr1.getText().toString());
                smallLabelData.put(Utils.D_NR, txtDNr.getText().toString());
                smallLabelData.put(Utils.QUANTITY, txtQtty1.getText().toString());
                smallListAdapter.addItem(smallLabelData);

                txtCtNr.setText(cartonName);
                txtPartNr1.setText(partNr);
                txtDNr.setText(dNr);
                txtQtty1.setText(quantity);
            } else {
                HashMap<String, String> bigLabelData = new HashMap<>();
                bigLabelData.put(Utils.CARTON_NR, txtCName.getText().toString());
                bigLabelData.put(Utils.PART_NR, txtPartNr2.getText().toString());
                bigLabelData.put(Utils.CUST_N, txtCustN.getText().toString());
                bigLabelData.put(Utils.QUANTITY, txtQtty2.getText().toString());
                bigLabelData.put(Utils.ORDER_NR, txtOrderNr.getText().toString());
                bigListAdapter.addItem(bigLabelData);

                txtCName.setText(cartonName);
                txtPartNr2.setText(partNr);
                txtCustN.setText(dNr);
                txtQtty2.setText(quantity);
                txtOrderNr.setText(orderNr);
            }

            dialog.dismiss();
        });

        btnClear.setOnClickListener(view -> {
            txtDialogCartonName.setText("");
            txtDialogPartNr.setText("");
            txtDialogDNr.setText("");
            txtDialogQuantity.setText("");
            if (!isLeft) {
                txtDialogOrderNr.setText("");
            }
        });

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

    private void onNew() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("New")
                .setMessage("Are you sure to create new one?")
                .setNegativeButton("Yes", (dialogInterface, i) -> {
                    if (upload()) {
                        // set scanned number to 0
                        SharedPreferences.Editor editor = getSharedPreferences(getPackageName(), MODE_PRIVATE).edit();
                        editor.putInt(SCANNED_NUMBER, 0);
                        editor.apply();
                        txtScannedNumber.setText(String.valueOf(0));

                        reset();
                    }

                    dialogInterface.dismiss();
                })
                .setPositiveButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    private void next() {

        HashMap<String, String> smallLabelData = new HashMap<>();
        smallLabelData.put(Utils.CARTON_NR, txtCtNr.getText().toString());
        smallLabelData.put(Utils.PART_NR, txtPartNr1.getText().toString());
        smallLabelData.put(Utils.D_NR, txtDNr.getText().toString());
        smallLabelData.put(Utils.QUANTITY, txtQtty1.getText().toString());
        smallListAdapter.addItem(smallLabelData);

        HashMap<String, String> bigLabelData = new HashMap<>();
        bigLabelData.put(Utils.CARTON_NR, txtCName.getText().toString());
        bigLabelData.put(Utils.PART_NR, txtPartNr2.getText().toString());
        bigLabelData.put(Utils.CUST_N, txtCustN.getText().toString());
        bigLabelData.put(Utils.QUANTITY, txtQtty2.getText().toString());
        bigLabelData.put(Utils.ORDER_NR, txtOrderNr.getText().toString());
        bigListAdapter.addItem(bigLabelData);

        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        int scannedNumber = sharedPreferences.getInt(SCANNED_NUMBER, 0);
        saveData(generateStringToSave(scannedNumber));

        // increase Scanned Number
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SCANNED_NUMBER, scannedNumber + 1);
        editor.apply();
        txtScannedNumber.setText(String.valueOf(scannedNumber + 1));

        reset();
    }

    private String generateStringToSave(int scannedNumber) {
        StringBuilder result = new StringBuilder();

        HashMap<String, String> smallLabel = smallListAdapter.getItem(0);
        String ctNr = smallLabel.getOrDefault(Utils.CARTON_NR, "");
        String partNr1 = smallLabel.getOrDefault(Utils.PART_NR, "");
        String dNr = smallLabel.getOrDefault(Utils.D_NR, "");
        String qtty1 = smallLabel.getOrDefault(Utils.QUANTITY, "");
        String strSmallLabel = String.format(Locale.getDefault(),
                "%-11s ; SCAN%03d ; %-12s ; %-14s ; %-14s ; %-12s;\n",
                "SmallLabel", scannedNumber + 1, ctNr, partNr1, dNr, qtty1);
        result.append(strSmallLabel);

        HashMap<String, String> bigLabel = bigListAdapter.getItem(0);
        String cName = bigLabel.getOrDefault(Utils.CARTON_NR, "");
        String partNr2 = bigLabel.getOrDefault(Utils.PART_NR, "");
        String custN = bigLabel.getOrDefault(Utils.CUST_N, "");
        String qtty2 = bigLabel.getOrDefault(Utils.QUANTITY, "");
        String orderNr = bigLabel.getOrDefault(Utils.ORDER_NR, "");
        String strBigLabel = String.format(Locale.getDefault(),
                "%-11s ; SCAN%03d ; %-12s ; %-14s ; %-14s ; %-12s; %-20s;\n",
                "BigLabel", scannedNumber + 1, cName, partNr2, custN, qtty2, orderNr);
        result.append(strBigLabel);

        for (int i = 1; i < smallListAdapter.getTotalItemsCount(); i++) {
            smallLabel = smallListAdapter.getItem(i);

            ctNr = smallLabel.getOrDefault(Utils.CARTON_NR, "");
            partNr1 = smallLabel.getOrDefault(Utils.PART_NR, "");
            dNr = smallLabel.getOrDefault(Utils.D_NR, "");
            qtty1 = smallLabel.getOrDefault(Utils.QUANTITY, "");

            strSmallLabel = String.format(Locale.getDefault(),
                    "%-11s ; SCAN%03d ; %-12s ; %-14s ; %-14s ; %-12s;\n",
                    "SmallLabel+", scannedNumber + 1, ctNr, partNr1, dNr, qtty1);

            result.append(strSmallLabel);
        }

        for (int i = 1; i < bigListAdapter.getTotalItemsCount(); i++) {
            bigLabel = bigListAdapter.getItem(i);
            cName = bigLabel.getOrDefault(Utils.CARTON_NR, "");
            partNr2 = bigLabel.getOrDefault(Utils.PART_NR, "");
            custN = bigLabel.getOrDefault(Utils.CUST_N, "");
            qtty2 = bigLabel.getOrDefault(Utils.QUANTITY, "");
            orderNr = bigLabel.getOrDefault(Utils.ORDER_NR, "");

            strBigLabel = String.format(Locale.getDefault(),
                    "%-11s ; SCAN%03d ; %-12s ; %-14s ; %-14s ; %-12s; %-20s;\n",
                    "BigLabel+", scannedNumber + 1, cName, partNr2, custN, qtty2, orderNr);

            result.append(strBigLabel);
        }
        return result.toString();
    }

    private void saveData(String strData) {

        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy", Locale.getDefault());
        String strDate = format.format(new Date());
        String fileName = String.format(Locale.getDefault(), "SCAN%s.txt", strDate);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = Utils.getDocumentsDirectory(this);
            File file = new File(dir, fileName);
            try (FileWriter writer = new FileWriter(file, true)) {
                writer.append(strData);
                System.out.println("File saved to: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("External storage not available.");
        }

    }

    private void reset() {
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

        smallListAdapter.clear();
        bigListAdapter.clear();

        txtPartNr1.setBackgroundTintList(txtCtNr.getBackgroundTintList());
        txtPartNr2.setBackgroundTintList(txtCtNr.getBackgroundTintList());
        txtQtty1.setBackgroundTintList(txtCtNr.getBackgroundTintList());
        txtQtty2.setBackgroundTintList(txtCtNr.getBackgroundTintList());

        btnNext.setEnabled(false);
        btnPlus1.setEnabled(false);
        btnPlus2.setEnabled(false);
    }

    private boolean upload() {

        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String host = sharedPreferences.getString(FTP_HOST, "");
        String username = sharedPreferences.getString(FTP_USERNAME, "");
        String password = sharedPreferences.getString(FTP_PASSWORD, "");
        int port = sharedPreferences.getInt(FTP_PORT, 21); // Default FTP port = 21

        if (host.isEmpty() || !isValidUrl(host) || !isValidPort(port)) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Error")
                    .setMessage("Please set valid FTP server url and port number in Settings.")
                    .setPositiveButton("Close", (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
            return false;
        }


        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy", Locale.getDefault());
        String strDate = format.format(new Date());
        String fileName = String.format(Locale.getDefault(), "SCAN%s.txt", strDate);

        File dir = Utils.getDocumentsDirectory(this);
        File file = new File(dir, fileName);
        String localPath = file.getAbsolutePath();

        new Thread(() -> {
            String remoteDir = "/uploads/";
//            String localPath = "/storage/emulated/0/Download/myfile.jpg";

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
        return true;
    }

    private boolean isValidUrl(String url) {
        return url != null && Patterns.WEB_URL.matcher(url).matches();
    }

    private boolean isValidPort(int port) {
        return port >= 1 && port <= 65535;
    }
}