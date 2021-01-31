package com.practice.jiandongxiao.memorypractice;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class NumberLinkage extends AppCompatActivity {

    CustomDBHandler customDB = new CustomDBHandler(this);
    Button btn_edit;
    String input;
    int position;
    String[] numberList;
    private TextView textView1;
    private TextView textView2;
    private EditText editText;
    private ImageView imageView;
    private boolean changeImageTag;
    private File tempFile;
    private Uri imageUri;
    private ImageSaver imageSaver = new ImageSaver(NumberLinkage.this);
    private static final int CAMERA_PERMISSION_REQ_CODE = 1001;
    private static final int STORAGE_PERMISSION_REQ_CODE = 1002;
    private static final int FULL_PERMISSION_REQ_CODE = 1003;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_number_linkage);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        position = getIntent().getIntExtra("number", -1);

        numberList = customDB.DatabaseToString();

        textView1 = findViewById(R.id.linkage_number_title);
        textView1.setText("输入数字 \"" + position + "\" 的联想");

        textView2 = findViewById(R.id.linkage_image_title);
        textView2.setText("点击图片进行更改");

        editText = findViewById(R.id.linkage_text);
        editText.setText(numberList[position]);

        imageView = findViewById(R.id.linkage_image);

        imageSaver.setFileName("n_" + position + ".png");
        imageView.setImageBitmap(imageSaver.load());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImageTag = false;
                selectImage();
            }
        });

        btn_edit = findViewById(R.id.edit_linkage);

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input = editText.getText().toString();

                if (input.matches("")) {
                    Toast.makeText(getBaseContext(), "请输入联想！", Toast.LENGTH_SHORT).show();
                } else if (input.matches(numberList[position]) && !changeImageTag) {
                    Toast.makeText(getBaseContext(), "您未更改任何内容", Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put("_number", input);
                    SQLiteDatabase db = customDB.getWritableDatabase();
                    int num = db.update("number_link", cv, "_id=" + position, null);
                    if (num != 1) {
                        Toast.makeText(getBaseContext(), "错误！", Toast.LENGTH_SHORT).show();
                    } else {
                        Bitmap b = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        imageSaver.save(b);
                        Toast.makeText(getBaseContext(), "更新成功！", Toast.LENGTH_SHORT).show();
                    }

                    db.close();

                    if (tempFile != null && tempFile.exists()) {
                        Boolean result = tempFile.delete();
                        Log.d("Delete file", result + "");
                    }

                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);

                }
            }
        });

    }

    private void selectImage() {
        PackageManager pm = this.getPackageManager();

        List<String> listItems = new ArrayList<>();

        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            if (ContextCompat.checkSelfPermission(this, CAMERA) != PERMISSION_GRANTED &&
                    (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
            ) {
                String[] fullPermission = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, CAMERA};
                ActivityCompat.requestPermissions(this, fullPermission, FULL_PERMISSION_REQ_CODE);
            } else if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                String[] storagePermissions = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_PERMISSION_REQ_CODE);
            } else if (ContextCompat.checkSelfPermission(this, CAMERA) != PERMISSION_GRANTED) {

                listItems.add("从图库中选择");
                listItems.add("取消");
            } else {
                listItems.add("拍照");
                listItems.add("从图库中选择");
                listItems.add("取消");
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                String[] storagePermissions = {};
                ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_PERMISSION_REQ_CODE);
            } else {
                listItems.add("从图库中选择");
                listItems.add("取消");
            }
        }

        final CharSequence[] options = listItems.toArray(new CharSequence[listItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("上传联想图片");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("拍照")) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    // Note that this temp file is store in
                    //      /storage/emulated/0/Pictures/temp.JPEG
                    // This is different from ImageSaver directory
                    //      /data/user/0/com.practice.jiandongxiao.memorypractice/app_images
                    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    try {
                        tempFile = File.createTempFile("temp", ".JPEG", storageDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (tempFile != null) {
                        // Use FileProvider to avoid the issue of
                        // FileUriExposedException: file.jpg exposed beyond app through ClipData.Item.getUri()
                        imageUri = FileProvider.getUriForFile(
                                NumberLinkage.this,
                                "com.practice.jiandongxiao.memorypractice.fileprovider",
                                tempFile);
                        Log.d("URI Path", tempFile.getPath());
                        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(takePicture, 0);
                    }
                } else if (options[item].equals("从图库中选择")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);
                } else if (options[item].equals("取消")) {
                    dialog.dismiss();
                }
            }
        });

        if (options.length != 0) {
            builder.show();
        }

    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    private static Bitmap codec(Bitmap src, Bitmap.CompressFormat format,
                                int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);

        byte[] array = os.toByteArray();
        return BitmapFactory.decodeByteArray(array, 0, array.length);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap selectedImage = null;
        Bitmap finalImage;
        Bitmap compressedImage;

        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK) {
                        changeImageTag = true;
                        try {
                            selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        changeImageTag = true;
                        try {
                            selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }

            if (selectedImage != null) {
                finalImage = resize(selectedImage, 1000, 1000);
                compressedImage = codec(finalImage, Bitmap.CompressFormat.JPEG, 50);
                imageView.setImageBitmap(compressedImage);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == FULL_PERMISSION_REQ_CODE) {

            Log.d("Permission Array 0", permissions[0]);
            Log.d("Permission Array 1", permissions[1]);
            Log.d("Permission Array 2", permissions[2]);
            Log.d("Grant Array 0", String.valueOf(grantResults[0]));
            Log.d("Grant Array 1", String.valueOf(grantResults[1]));
            Log.d("Grant Array 2", String.valueOf(grantResults[2]));

            // first check if storage access is rejected, then check camera
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                boolean showStorageRationale = shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE);
                if (!showStorageRationale) {
                    Toast.makeText(this, "最低需要文件读写权限。\n" +
                            "你可去手机的【设置 -> 数字联想记忆训练 -> 权限】允许使用文件读写权限。", Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(this, "最低需要文件读写权限。", Toast.LENGTH_LONG)
                            .show();
                }
            } else if (grantResults[2] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this,
                        "文件读写权限获得许可。\n" +
                                "相机功能非强制性。" +
                                "你可去手机的【设置 -> 数字联想记忆训练 -> 权限】允许使用相机的权限。", Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(this,
                        "文件读写权限及相机权限获得许可。\n" +
                                "你可去手机的【设置 -> 数字联想记忆训练 -> 权限】更改各项权限。", Toast.LENGTH_LONG)
                        .show();
            }
        }

        if (requestCode == STORAGE_PERMISSION_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                if (!showRationale) {
                    // user also CHECKED "never ask again"
                    Toast.makeText(this, "最低需要文件读写权限。\n" +
                                    "你可去手机的【设置 -> 数字联想记忆训练 -> 权限】允许使用文件读写权限。",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "最低需要文件读写权限。", Toast.LENGTH_LONG)
                            .show();
                }
            } else {
                Toast.makeText(this,
                        "文件读写权限及相机权限获得许可。\n" +
                                "你可去手机的【设置 -> 数字联想记忆训练 -> 权限】更改各项权限。", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("Check Intent", String.valueOf(getIntent().getIntExtra("number", -1)));
                setResult(RESULT_OK, getIntent());
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
