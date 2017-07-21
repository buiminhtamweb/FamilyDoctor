package android.familydoctor.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.familydoctor.Activity.MainActivity;
import android.familydoctor.Class.BenhNhan;
import android.familydoctor.R;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Au Nguyen on 7/14/2017.
 */

public class FragmentBenhNhan extends Fragment {


    private static String ARG_SECTION_NUMBER = "secion_number";

    public FragmentBenhNhan() {
    }

    public static FragmentBenhNhan newInstance(int sectionNumber) {
        FragmentBenhNhan fragment = new FragmentBenhNhan();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private DatabaseReference mDatabase;
    private FirebaseStorage firebaseStorage;
    final Context context = this.getContext();
    private ImageView imageView;

    private String id;

    Spinner NamSinh ;
    EditText HoTen ,SDT ,DiaChi ;
    Button  setData;
    ImageView img ;
    float x ;
    float y ;
    BenhNhan Us = new BenhNhan();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_benh_nhan, container, false);


        HoTen = (EditText) view.findViewById(R.id.HoTenE);
        NamSinh = (Spinner) view.findViewById(R.id.spNamSinhBenhNhan);
        SDT = (EditText) view.findViewById( R.id.SDTE);
        DiaChi = (EditText) view.findViewById(R.id.DiaChiE);
        img  = (ImageView) view.findViewById(R.id.ImgAvaE);
        setData = (Button) view.findViewById(R.id.Submit);


        List<String> namList = new ArrayList<>();
        for (int i= 1960; i< 2018; i++){

            namList.add(i+"");
        }

        ArrayAdapter aa= new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item, namList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        NamSinh.setAdapter(aa);

        String hoTen = HoTen.getText().toString();
        String namSinh = NamSinh.getSelectedItem().toString();

        String sdt = SDT.getText().toString();
        String diaChi = DiaChi.getText().toString();

        Us = new BenhNhan(hoTen,namSinh,sdt,diaChi);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        firebaseStorage = FirebaseStorage.getInstance();
        final String key = mDatabase.child("User").push().getKey();


        Intent bundle = getActivity().getIntent();

        id = bundle.getDataString();
        img = (ImageView) view.findViewById(R.id.ImgAvaE);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext()).setNeutralButton("Chụp ảnh mới", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 1);
                    }
                })
                        .setNegativeButton("Chọn ảnh từ thư viện", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent, 2);
                            }
                        })
                        .show();
            }
        });

        setData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Up Hình ảnh
                StorageReference storageReference = firebaseStorage.getReferenceFromUrl("gs://familydoctor-56b96.appspot.com/");
                StorageReference reference = storageReference.child("Users").child(key+"jpg");

                Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitMapData = stream.toByteArray();

                UploadTask uploadTask = reference.putBytes(bitMapData);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });

                mDatabase.child("Users").setValue(Us);

                if (uploadTask.isSuccessful()) {

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getContext(),"chưa up thong tin",Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
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

            Uri imageUri = data.getData();
            img.setImageURI(imageUri);
        }
    }
}