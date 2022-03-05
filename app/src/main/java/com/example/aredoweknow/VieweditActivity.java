package com.example.aredoweknow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.aredoweknow.fragments_folder.Home;

import java.io.ByteArrayOutputStream;

public class VieweditActivity extends AppCompatActivity {
    private boolean editw = true;
    private boolean easd = true;

    public static EditText resulttextview;
    DatabaseHandler db;
    AppCompatImageButton backbtn;

    Button cameraBTN, galleryBTN, editBTN, saveBTN, image_Uri;

    ImageButton scanBTN;

    DatabaseHandler dataHandler;
    EditText name_field, barcode_field, description_field, quantity_field, price_field;
    Bitmap captureImage;
    Uri galler;
    ImageView imageView;
    CardView cardView;
    Toolbar toolbar;

    Intent intent;
    String name;
    String price;
    String barcode;
    String description;
    String quantity;
    Bitmap image;


    public static EditText static_namefield;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewedit);


        //Database
        dataHandler = new DatabaseHandler(this);
        //Static Barcode


        name_field = findViewById(R.id.itemname_val);
        barcode_field = findViewById(R.id.barcode_val2);
        description_field = findViewById(R.id.description_val);
        quantity_field = findViewById(R.id.quantity_val);
        price_field = findViewById(R.id.price_val);
        imageView = findViewById(R.id.image_val);

        static_namefield = barcode_field;

        name_field.setShowSoftInputOnFocus(false);
        barcode_field.setShowSoftInputOnFocus(false);
        description_field.setShowSoftInputOnFocus(false);
        quantity_field.setShowSoftInputOnFocus(false);
        price_field.setShowSoftInputOnFocus(false);


//--------------DISABLE TYPE

//        name_field.setText(getIntent().getStringExtra("name"));
////        barcode_field.setText(getIntent().getStringExtra("name"));
////        description_field.setText(getIntent().getStringExtra("name"));
//        quantity_field.setText(getIntent().getStringExtra("quan"));
//        price_field.setText(getIntent().getStringExtra("price"));

        intent = getIntent();
        name = intent.getStringExtra("name");
        price = intent.getStringExtra("price");
        barcode = intent.getStringExtra("barcode");
        description = intent.getStringExtra("descr");
        quantity = intent.getStringExtra("quant");
        image = intent.getParcelableExtra("image");


        imageView.setImageBitmap(image);
        name_field.setText(name);
        barcode_field.setText(barcode);
        description_field.setText(description);
        quantity_field.setText(quantity);
        price_field.setText(price);


        //--------------BACK TO DASHBOARD--------------
        backbtn = findViewById(R.id.view_back_btn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


//--------------CAMERA CODE
        if (ContextCompat.checkSelfPermission(VieweditActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(VieweditActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
        }

        //--------------------Open gallery
        galleryBTN = findViewById(R.id.gallery_btn2);
        galleryBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);
            }
        });

        //-------------------Open Camera for Image
        cameraBTN = findViewById(R.id.camera_btn2);
        cameraBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 1 wait ako na dito - ji
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });


        //-------------------Open Scanner
        scanBTN = findViewById(R.id.barscan_btn);
        scanBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VieweditActivity.this, Scanner.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Toast.makeText(this, requestCode + " | " + resultCode, Toast.LENGTH_SHORT).show();
//        System.out.println(requestCode + " | " + resultCode);

        if (data != null && resultCode == RESULT_OK) {
            System.out.println(requestCode + " | " + resultCode);
            try {
                if (requestCode == 3) {  //--> Choose from gallery
                    galler = data.getData();
//                image_Bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_Uri);
//                imageView.setImageURI(selectedImage);
                    performCrop(); //--> Requires Cropping the image
                }

                if (requestCode == PIC_CROP || requestCode == 100) { //3
                    captureImage = data.getExtras().getParcelable("data");
//                image_Bitmap = Bitmap.createScaledBitmap(image_Bitmap, 100, 100, false);

                    imageView.setImageBitmap(captureImage);
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.gray1));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //keep track of cropping intent
    final int PIC_CROP = 2;

    public void performCrop() {
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(image_Uri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 420); //256
            cropIntent.putExtra("outputY", 420);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);

            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, galler);
            startActivityForResult(cropIntent, PIC_CROP);
        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();

            galleryBTN = findViewById(R.id.gallery_btn2);
            galleryBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 2 wait ako na dito - ji
                }
            });

            scanBTN = findViewById(R.id.barscan_btn2);
            scanBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VieweditActivity.this, Scanner.class);
                    intent.putExtra("update", "viewing_item");
                    startActivity(intent);
                }
            });

            saveBTN = findViewById(R.id.save_btn);
            saveBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO save function here
                }
            });
        }

    }
    public void editBTN_clicked (View view){
            if (editw) { // --> cen type
                name_field.setEnabled(false);
                barcode_field.setEnabled(false);
                description_field.setEnabled(false);
                quantity_field.setEnabled(false);
                price_field.setEnabled(false);
                editw = false;

            } else { // --> CAN NOT TYPE
                name_field.setEnabled(true);
                barcode_field.setEnabled(true);
                description_field.setEnabled(true);
                quantity_field.setEnabled(true);
                price_field.setEnabled(true);
                editw = true;

            }
        }
    }
