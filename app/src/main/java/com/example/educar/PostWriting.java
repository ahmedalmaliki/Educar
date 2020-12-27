package com.example.educar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;

import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;
import androidx.documentfile.provider.DocumentFile;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PostWriting extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar , toolbarB;
    private ImageButton backArrow, image_icon, new_doc_icon;
    private Button sendPost;
    private EditText textField;
    private FirebaseMethods firebaseMethods;
    private BottomAppBar bottomAppBar;
    private Short typeOfAttachment = 0;
    private List<Uri> mSelected;
    //Info of the attached document
    private Uri docUri;
    private DocumentFile documentFile;
    //
    private HashMap<String, Uri> mediaUriHashMap = new HashMap<String, Uri>();
    public static final int PICKER_REQUEST_CODE = 1;
    private static final int PICK_DOC = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_writing);

        image_icon = findViewById(R.id.image_icon);
        toolbar = findViewById(R.id.postingBar);
        backArrow = findViewById(R.id.backArrow);
        textField = findViewById(R.id.textField);
        sendPost = findViewById(R.id.sendPost);
        firebaseMethods = new FirebaseMethods();
        bottomAppBar = findViewById(R.id.bottomAppBar);
        toolbarB = findViewById(R.id.toolbarB);
        new_doc_icon = findViewById(R.id.new_doc_icon);
        setSupportActionBar(toolbar);

      //  Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(textField, InputMethodManager.SHOW_IMPLICIT);
        getSupportActionBar().setTitle(R.string.app_name);
        setSupportActionBar(toolbarB);
        getSupportActionBar().setTitle("");


        image_icon.setOnClickListener(this);
        backArrow.setOnClickListener(this);
        sendPost.setOnClickListener(this);
        new_doc_icon.setOnClickListener(this);
        bottomAppBar.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {

                return false;
            }
        });

        textField.addTextChangedListener(new TextWatcher() {
            boolean hint;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0  ) {
                    // no text, hint is visible
                    hint = true;
                    textField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);


                } else if(hint) {
                    // no hint, text is visible
                    hint = false;
                    textField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    enablePostButton();
                }


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0  ) {
                    // no text, hint is visible
                    hint = true;
                    textField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                    disablePostButton();
                }else if(hint) {
                    // no hint, text is visible
                    hint = false;
                    textField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    enablePostButton();

                }
            }



            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0  ) {
                    // no text, hint is visible
                    hint = true;
                    textField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                    disablePostButton();
                } else if(hint) {
                    // no hint, text is visible
                    hint = false;
                    textField.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                   enablePostButton();

                }
            }
        });

        textField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                } else {

                }
            }
        });

        TouchDelegateSettings();
    }

    private void TouchDelegateSettings() {
        final View parent = (View) backArrow.getParent();
        parent.post(new Runnable() {
            @Override
            public void run() {
                final Rect rect = new Rect();
                backArrow.getHitRect(rect);
                rect.top -= 100;
                rect.left -= 100;
                rect.bottom += 100;
                rect.right += 60;
                parent.setTouchDelegate(new TouchDelegate(rect, backArrow));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.post_menu, menu);
        getMenuInflater().inflate(R.menu.post_bottom_menu, menu);
        return true;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backArrow:
                moveToMainActivity();
                break;
            case R.id.sendPost:
                sendPost();
                break;
            case R.id.image_icon:
                onImageIconClick();
                break;
            case R.id.new_doc_icon:
                onNewDocIconClick();
                break;
        }
}

    private void sendPost() {
        if(typeOfAttachment == 1){
            firebaseMethods.sendMediaPostToDataBase(setUpPostInformation(), mediaUriHashMap);
            checkIfSuccessfullyPosted();
        }
        else if(typeOfAttachment == 2){
            firebaseMethods.sendDocPostToDataBase(setUpPostInformation(), documentFile.getName(), docUri);
            checkIfSuccessfullyPosted();
        }
        else {
            //The post is only caption with no attachments.
            firebaseMethods.sendCaptionOnlyPostToDataBase(setUpPostInformation());
            checkIfSuccessfullyPosted();
        }
    }

    private void onNewDocIconClick() {
        Dexter.withActivity(this)
                .withPermissions( Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showDocPicker();


                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showDocPicker() {
        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip", "application/vnd.android.package-archive"};

        Intent openDoc = new Intent(Intent.ACTION_GET_CONTENT);
        openDoc.setType("*/*");
        openDoc.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        openDoc.addCategory(Intent.CATEGORY_OPENABLE);
        openDoc.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(openDoc, PICK_DOC);
    }

    private void onImageIconClick() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            showImagePickerOptions();

                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showImagePickerOptions() {
        Matisse.from(PostWriting.this)
                .choose(MimeType.ofAll())
                .capture(true) //Do you have a photo function?
                .captureStrategy(new CaptureStrategy(true,getPackageName() + ".provider"))
                .countable(true)
                .showSingleMediaType(false)
                .maxSelectable(9)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(
                      getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(new Glide4Engine())
                .forResult(PICKER_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            //emptyUrisHashmap();
            mSelected = Matisse.obtainResult(data);
            typeOfAttachment = 1;
            int counter = 0;
            for (Uri uri: mSelected){
                String tempUriString = uri.toString().substring(25, 30);

                if (tempUriString.equals("video")){
                    mediaUriHashMap.put("video" + counter++, uri);
                }else {
                    mediaUriHashMap.put("image"+ counter++, uri);
                }
            }

        }
        else if (requestCode == PICK_DOC && resultCode == RESULT_OK){
            docUri  = data.getData();
            documentFile = DocumentFile.fromSingleUri(this, docUri);
            typeOfAttachment = 2;
        }
        enablePostButton();
    }
    private void enablePostButton(){
        sendPost.setTextColor(getResources().getColor(R.color.colorPrimary));
        sendPost.setEnabled(true);
    }
    private void disablePostButton(){
        sendPost.setTextColor(getResources().getColor(R.color.light_grey));
        sendPost.setEnabled(false);
    }
    /*private void emptyUrisHashmap(){
        uriHashMap.clear();
    }*/
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostWriting.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    private void checkIfSuccessfullyPosted() {
       finish();
    }


    private Post setUpPostInformation() {
        String caption = textField.getText().toString().trim();
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<String> emptyUrlsArray = new ArrayList<String>();
        Post post = new Post(caption, getCurrentDate(), emptyUrlsArray ,  "/", user_id);

        return post;
    }
    private String getCurrentDate(){
        SimpleDateFormat sdfDat = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String strDate = sdfDat.format(now);
        return strDate;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
    private void moveToMainActivity() {
        Intent intent = new Intent(PostWriting.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
    }

