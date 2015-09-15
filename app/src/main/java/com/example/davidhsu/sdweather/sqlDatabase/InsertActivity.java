package com.example.davidhsu.sdweather.sqlDatabase;

/**
 * Created by David Hsu on 2015/9/15.
 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.davidhsu.sdweather.R;

public class InsertActivity extends AppCompatActivity {
    private ImageView ivSpot;
    private Spinner spClothes;
    private Spinner spType;
    private Spinner spSleeve;
    private Spinner spMaterial;
    private MySQLiteOpenHelper helper;
    private byte[] image;
    private File file;
    private static final int REQUEST_TAKE_PICTURE = 0;
    private static final int REQUEST_PICK_IMAGE = 1;

    private String[] type = new String[] {"衣服類", "褲子類"};
    private String[] type1 = new String[]{"襯衫","T恤","毛衣","外套"};
    private String[][] type2 = new String[][]{{"襯衫","T恤","毛衣","外套"},{"牛仔褲","西裝褲","運動褲","裙子","休閒褲"}};
    private Context context;

    ArrayAdapter<String> adapter ;

    ArrayAdapter<String> adapter2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_activity);
        findViews();
        if (helper == null) {
            helper = new MySQLiteOpenHelper(this);
        }
    }

    private void findViews() {
        ivSpot = (ImageView) findViewById(R.id.ivSpot);

        context = this;

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, type);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spClothes = (Spinner) findViewById(R.id.spClothes);
        spClothes.setAdapter(adapter);
        spClothes.setOnItemSelectedListener(listener);

        adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, type1);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType = (Spinner) findViewById(R.id.spType);
        spType.setAdapter(adapter2);



        spSleeve = (Spinner) findViewById(R.id.spSleeve);
        String[] sleeve = {"長", "短"};
        ArrayAdapter<String> adapterSleeve = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sleeve);
        adapterSleeve
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSleeve.setAdapter(adapterSleeve);
        spSleeve.setSelection(0, true);
        spSleeve.setOnItemSelectedListener(listener);

        spMaterial = (Spinner) findViewById(R.id.spMaterial);
        String[] material = {"棉", "絲","人工纖維"};
        ArrayAdapter<String> adapterMaterial = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, material);
        adapterMaterial
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMaterial.setAdapter(adapterMaterial);
        spMaterial.setSelection(0, true);
        spMaterial.setOnItemSelectedListener(listener);

    }

    Spinner.OnItemSelectedListener listener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(
                AdapterView<?> parent, View view, int pos, long id) {
            pos = spClothes.getSelectedItemPosition();
            adapter2 = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, type2[pos]);
            spType.setAdapter(adapter2);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public void onTakePictureClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        if (isIntentAvailable(this, intent)) {
            startActivityForResult(intent, REQUEST_TAKE_PICTURE);
        } else {
            Toast.makeText(this, R.string.msg_NoCameraAppsFound,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public void onLoadPictureClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PICTURE:
                    Bitmap picture;
                    float imagew = 300;
                    float imageh = 300;

                    BitmapFactory.Options bitmapFactoryOptions = new BitmapFactory.Options();
                    bitmapFactoryOptions.inJustDecodeBounds = true;
                    picture = BitmapFactory.decodeFile(file.getPath(), bitmapFactoryOptions);

                    int yRatio = (int)Math.ceil(bitmapFactoryOptions.outHeight/imageh);
                    int xRatio = (int)Math.ceil(bitmapFactoryOptions.outWidth/imagew);

                    if (yRatio > 1 || xRatio > 1){
                        if (yRatio > xRatio) {
                            bitmapFactoryOptions.inSampleSize = yRatio;
                            Toast.makeText(this,
                                    "OK",
                                    Toast.LENGTH_LONG).show();
                        }
                        else {
                            bitmapFactoryOptions.inSampleSize = xRatio;
                            Toast.makeText(this,
                                    "OK",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(this,
                                "inSampleSize = 1",
                                Toast.LENGTH_LONG).show();
                    }

                    bitmapFactoryOptions.inJustDecodeBounds = false;
                    picture = BitmapFactory.decodeFile(file.getPath(), bitmapFactoryOptions);
                    ivSpot.setImageBitmap(picture);
                    ByteArrayOutputStream out1 = new ByteArrayOutputStream();
                    picture.compress(Bitmap.CompressFormat.JPEG, 100, out1);
                    image = out1.toByteArray();
                    break;
                case REQUEST_PICK_IMAGE:
                    Uri uri = data.getData();
                    String[] columns = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(uri, columns,
                            null, null, null);
                    if (cursor.moveToFirst()) {
                        String imagePath = cursor.getString(0);
                        cursor.close();
                        float imagew2 = 300;
                        float imageh2 = 300;
                        BitmapFactory.Options bitmapFactoryOptions2 = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bitmapFactoryOptions2);
                        int yRatio2 = (int)Math.ceil(bitmapFactoryOptions2.outHeight/imageh2);
                        int xRatio2 = (int)Math.ceil(bitmapFactoryOptions2.outWidth/imagew2);

                        if (yRatio2 > 1 || xRatio2 > 1){
                            if (yRatio2 > xRatio2) {
                                bitmapFactoryOptions2.inSampleSize = yRatio2;
                                Toast.makeText(this,
                                        "OK",
                                        Toast.LENGTH_LONG).show();
                            }
                            else {
                                bitmapFactoryOptions2.inSampleSize = xRatio2;
                                Toast.makeText(this,
                                        "OK",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Toast.makeText(this,
                                    "inSampleSize = 1",
                                    Toast.LENGTH_LONG).show();
                        }

                        bitmapFactoryOptions2.inJustDecodeBounds = false;
                        bitmap = BitmapFactory.decodeFile(imagePath, bitmapFactoryOptions2);
                        ivSpot.setImageBitmap(bitmap);
                        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out2);
                        image = out2.toByteArray();
                    }
                    break;
            }
        }
    }

    public void onFinishInsertClick(View view) {
        String clothes = spClothes.getSelectedItem()
                .toString().trim();
        String type = spType.getSelectedItem()
                .toString().trim();
        String sleeve = spSleeve.getSelectedItem()
                .toString().trim();
        String material = spMaterial.getSelectedItem()
                .toString().trim();

        if (image == null) {
            Toast.makeText(this, R.string.msg_NoImage,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Spot spot = new Spot(clothes, type, sleeve, material, image);
        long rowId = helper.insert(spot);
        if (rowId != -1) {
            Toast.makeText(this, R.string.msg_InsertSuccess,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.msg_InsertFail,
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public void onCancelClick(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (helper != null) {
            helper.close();
        }
    }
}