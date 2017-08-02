package android.familydoctor.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.familydoctor.Class.BacSi;
import android.familydoctor.Class.BenhNhan;
import android.familydoctor.service.GPSTracker;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.familydoctor.R;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.key;

/**
 * Created by buimi on 6/19/2017.
 */

public class CapNhatThongTinCaNhan_Act extends AppCompatActivity{

    private DatabaseReference mDatabase;
    private FirebaseStorage firebaseStorage;

    private Spinner NamSinh;
    private EditText HoTen, DiaChi;
    private Button setData,cancel;
    private ImageView img;

    private BacSi bacSi = new BacSi();
    private BenhNhan benhNhan = new BenhNhan();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capnhatthongtin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        HoTen = (EditText) findViewById(R.id.HoTenE);
        NamSinh = (Spinner) findViewById(R.id.spNamSinh);
        DiaChi = (EditText) findViewById(R.id.DiaChiE);
        img = (ImageView) findViewById(R.id.ImgAvaE);
        setData = (Button) findViewById(R.id.Submit);
        cancel = (Button) findViewById(R.id.Cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        List<String> namList = new ArrayList<>();
        for (int i = 1960; i < 2018; i++) {
            namList.add(i + "");
        }
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, namList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        NamSinh.setAdapter(aa);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();

        img = (ImageView) findViewById(R.id.ImgAvaE);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(CapNhatThongTinCaNhan_Act.this).setNeutralButton("Chụp ảnh mới", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 1);
                    }
                })
                        .setNegativeButton("Chọn ảnh từ thư viện", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                intent.putExtra("crop", "true");
                                intent.putExtra("scale", true);
                                intent.putExtra("outputX", 256);
                                intent.putExtra("outputY", 256);
                                intent.putExtra("aspectX", 1);
                                intent.putExtra("aspectY", 1);
                                intent.putExtra("return-data", true);
                                startActivityForResult(intent, 2);
                            }
                        })
                        .show();
            }
        });

        if (LoginPhone.dinhDanh == 1){
            //bac si
            capNhatBacSi();
        }else if (LoginPhone.dinhDanh == 2){
            //Benh nhan
            capNhatBenhNhan();
        }


    }

    private void capNhatBenhNhan(){

        //SHow thông tin hiện tại
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference checksDoctor = root.child("User_BacSi").child(LoginPhone.sdt_key);
        DatabaseReference checksPanter = root.child("User_BenhNhan").child(LoginPhone.sdt_key);
        checksPanter.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                benhNhan = dataSnapshot.getValue(BenhNhan.class);
                HoTen.setText(benhNhan.getHoTenBenhNhan());
                DiaChi.setText(benhNhan.getDiaChiBenhNhan());
                Picasso.with(getApplicationContext()).load(benhNhan.getUriHinhAnhBenhNhan()).into(img);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //Up thông tin bệnh nhân
        setData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hoTen = HoTen.getText().toString();
                String namSinh = NamSinh.getSelectedItem().toString();
                String diaChi = DiaChi.getText().toString();

                benhNhan.setHoTenBenhNhan(hoTen);
                benhNhan.setNamSinhBenhNhan(namSinh);
                benhNhan.setDiaChiBenhNhan(diaChi);

                //Up Hình ảnh
                StorageReference storageReference = firebaseStorage.getReferenceFromUrl("gs://familydoctor-56b96.appspot.com/");
                StorageReference reference = storageReference.child(System.currentTimeMillis() + "jpg");

                Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitMapData = stream.toByteArray();

                UploadTask uploadTask = reference.putBytes(bitMapData);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "lổi không đăng kí thông tin được", Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        benhNhan.setUriHinhAnhBenhNhan(downloadUrl.toString());
                        mDatabase.child("User_BenhNhan").child(LoginPhone.sdt_key).setValue(benhNhan);

                        Toast.makeText(CapNhatThongTinCaNhan_Act.this,"Đã cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }

    private void capNhatBacSi(){
        //Show thông tin hiện tại
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference checksDoctor = root.child("User_BacSi").child(LoginPhone.sdt_key);
        checksDoctor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bacSi = dataSnapshot.getValue(BacSi.class);
                HoTen.setText(bacSi.getHoTenBacSi());
                DiaChi.setText(bacSi.getDiaChiBacSi());
                Picasso.with(getApplicationContext()).load(bacSi.getUriHinhAnhBacSi()).into(img);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        // Up thông tin
        setData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String hoTen = HoTen.getText().toString();
                String namSinh = NamSinh.getSelectedItem().toString();
                String diaChi = DiaChi.getText().toString();

                bacSi.setHoTenBacSi(hoTen);
                bacSi.setNamSinhBacSi(namSinh);
                bacSi.setDiaChiBacSi(diaChi);

                //Up Hình ảnh
                StorageReference storageReference = firebaseStorage.getReferenceFromUrl("gs://familydoctor-56b96.appspot.com/");
                StorageReference reference = storageReference.child(System.currentTimeMillis() + "jpg");

                Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitMapData = stream.toByteArray();

                UploadTask uploadTask = reference.putBytes(bitMapData);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "lổi không đăng kí thông tin được", Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        benhNhan.setUriHinhAnhBenhNhan(downloadUrl.toString());

                        mDatabase.child("User_BacSi").child(LoginPhone.sdt_key).setValue(bacSi);

                        Toast.makeText(CapNhatThongTinCaNhan_Act.this,"Đã cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                        finish();
//                        startActivity(new Intent(CapNhatThongTinCaNhan_Act.this,MainActivity.class));
                    }
                });

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1) {
            // lay hinh thu nho cua hinh vua chup
            Bitmap hinh = (Bitmap) data.getExtras().get("data");
            img.setImageBitmap(hinh);
        }

        if (resultCode == RESULT_OK && requestCode == 2) {
            final Bundle extras = data.getExtras();
            if (extras != null) {
                //Get image
                Bitmap newProfilePic = extras.getParcelable("data");
                img.setImageBitmap(newProfilePic);
            }

        }
    }

}
