package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final int GALLERY_REQUEST_CODE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 120;
    ImageView image;
    boolean fragmentin = false;
    ConstraintLayout allview;
    ImageView sampleimage;
    Fragment fragmentcanvas;
    public static final int CAMERA_CODE=101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = findViewById(R.id.image);
        allview=findViewById(R.id.allview);
      //  sampleimage=findViewById(R.id.sampleview);
        findViewById(R.id.canvasbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment prevfragment1 = getSupportFragmentManager().findFragmentById(R.id.fragmentplaceholder);
                Fragment prevfragment2 = getSupportFragmentManager().findFragmentById(R.id.canvasplaceholder);
                if(prevfragment1!=null)
                {
                    getSupportFragmentManager().beginTransaction().remove(prevfragment1).commit();
                }
                if(prevfragment2!=null)
                {
                    getSupportFragmentManager().beginTransaction().remove(prevfragment2).commit();
                }
                fragmentcanvas = new canvasfragment();
                fragmentin=true;
                getSupportFragmentManager().beginTransaction().replace(R.id.canvasplaceholder,fragmentcanvas).commit();
            }
        });
        findViewById(R.id.photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("photo", "onClick: " + "clicked");
                askpermission();
            }
        });

        findViewById(R.id.addtext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment prevfragment1 = getSupportFragmentManager().findFragmentById(R.id.fragmentplaceholder);
                Fragment prevfragment2 = getSupportFragmentManager().findFragmentById(R.id.canvasplaceholder);
                if(prevfragment1!=null)
                {
                    getSupportFragmentManager().beginTransaction().remove(prevfragment1).commit();
                }
                if(prevfragment2!=null)
                {
                    getSupportFragmentManager().beginTransaction().remove(prevfragment2).commit();
                }
                Fragment fragment = new textfragment();
                fragmentin = true;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentplaceholder, fragment).commit();
            }
        });
        findViewById(R.id.gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST_CODE);

            }
        });
        findViewById(R.id.grayscale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bm = ((BitmapDrawable) image.getDrawable()).getBitmap();
                bm = toGrayscale(bm);
                image.setImageBitmap(bm);
            }
        });
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveimage();
            }
        });
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);//getExternalStoragePublicDirectory();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Log.d("indside dispathc intent", "dispatchTakePictureIntent: ");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (true /*takePictureIntent.resolveActivity(getPackageManager()) != null*/) {
            // Create the File where the photo should go
            Log.d("photofile", "dispatchTakePictureIntent: ");
            File photoFile = null;
            try {
                Log.d("photofile ", "dispatchTakePictureIntent: ");
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("error", "dispatchTakePictureIntent: ");

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE) {
            if(data!=null)
            {
                Uri uri = data.getData();
                if(uri!=null)
                {
                    image.setImageURI(uri);

                }
            }

            }
        if (requestCode == REQUEST_IMAGE_CAPTURE) {

            File f = new File(currentPhotoPath);
            image.setImageURI(Uri.fromFile(f));
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Log.d("storing", "onActivityResult: ");
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);

        }
    }
//    public static Bitmap getBitmapFromView(View view) {
//        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
//                Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//        view.draw(canvas);
//        return bitmap;
//    }
private void askpermission() {
    if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
    {
        ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA},CAMERA_CODE);
    }
    else{
        Log.d("before dispatch", "askpermission: ");
        dispatchTakePictureIntent();
    }
}
public static Bitmap getBitmapFromView(View view) {
    //Define a bitmap with the same size as the view
    Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
    //Bind a canvas to it
    Canvas canvas = new Canvas(returnedBitmap);
    //Get the view's background
    Drawable bgDrawable =view.getBackground();
    if (bgDrawable!=null)
        //has background drawable, then draw it on the canvas
        bgDrawable.draw(canvas);
    else
        //does not have background drawable, then draw white background on the canvas
        canvas.drawColor(Color.WHITE);
    // draw the view on the canvas
    view.draw(canvas);
    //return the bitmap
    return returnedBitmap;
}
    public void saveimage() {
        Bitmap bitmap=null;
        if (fragmentin==false) {
       /* BitmapDrawable bitmapDrawable = (BitmapDrawable) image.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        */
            bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        }
        else {
            bitmap=getBitmapFromView(allview);
            Log.d("bruh", "saveimage: ");
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.canvasplaceholder,new android.app.Fragment())
                    .commit();


         //   sampleimage.setImageBitmap(bitmap);
        }
            /* STARTING TO USE OUTPUTSTREAMS TO CONVERT BITMAP INTO A IMAGE FILE OF PNG FORMAT*/
            FileOutputStream outputStream = null;
            // File file = Environment.getExternalStorageDirectory();
            //   File dir = new File(file.getAbsolutePath()+"/pics");
            // dir.mkdirs();
            // String filename=  String.format("%d.png",System.currentTimeMillis());
            // File outfile = new File(dir,filename);
            File f = null;
            try {
                f = createImageFile();

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                outputStream = new FileOutputStream(f);

            } catch (Exception e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            try {
                outputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // File f = new File(currentPhotoPath);
            //image.setImageURI(Uri.fromFile(f));
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Log.d("storing", "onActivityResult: ");
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        }
    }
