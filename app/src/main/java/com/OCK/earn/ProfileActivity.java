package com.OCK.earn;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.OCK.earn.model.ProfileModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView nameEt;
    private TextView phoneEt;
    private TextView emailEt;
    private TextView coinsEt;
    private Button Profile_update;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private static final int IMAGE_PICKER = 1;
    private Uri picUri;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileImage = findViewById(R.id.profile_Image);
        emailEt = findViewById(R.id.emailProf);
        phoneEt = findViewById(R.id.phoneProf);
        nameEt = findViewById(R.id.nameProf);
        TextView shareEt = findViewById(R.id.shareEt);
        TextView redeemHistoryEt = findViewById(R.id.redeemHistoryEt);
        TextView logoutEt = findViewById(R.id.LogoutEt);
        coinsEt = findViewById(R.id.coinEt);
        ImageButton imageEditButton = findViewById(R.id.image_edit);
        Profile_update = findViewById(R.id.profile_update);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        loadInterstitialAd();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCancelable(false);
        reference.child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ProfileModel model = snapshot.getValue(ProfileModel.class);
                        assert model != null;
                        nameEt.setText(model.getName());
                        phoneEt.setText(model.getPhone());
                        emailEt.setText(model.getEmail());
                        coinsEt.setText(String.valueOf(model.getCoins()));
                        Glide.with(getApplicationContext())
                                .load(model.getImage())
                                .timeout(6000)
                                .placeholder(R.drawable.profile)
                                .into(profileImage);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProfileActivity.this, "Error:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
        logoutEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            }
        });
        redeemHistoryEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
        shareEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareBody = "Check out the best Earning App Download" + getString(R.string.app_name) +
                        "from Play Store\n" +
                        "https://play.google.com/store/app/details?id=" +
                        getPackageName();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                intent.setType("text/plain");
                startActivity(intent);
            }
        });

        imageEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(ProfileActivity.this)
                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setType("image/*");
                                    startActivityForResult(intent, IMAGE_PICKER);
                                } else {
                                    Toast.makeText(ProfileActivity.this, "Please Allow all Permission", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                            }
                        }).check();
            }
        });
        Profile_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER && resultCode == RESULT_OK) {
            if (data != null) {
                picUri = data.getData();
                Profile_update.setVisibility(View.VISIBLE);
            }
        }
    }

    private void uploadImage() {
        if (picUri == null) {
            return;
        }
        String fileName = user.getUid() + ".jpg";

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReference().child("Images/" + fileName);

        progressDialog.show();

        storageReference.putFile(picUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageUrl = uri.toString();
                                uplImageUrlToDatabase();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        long totalSi = taskSnapshot.getTotalByteCount();
                        long transferSi = taskSnapshot.getBytesTransferred();
                        long totalSize = (totalSi / 1024);
                        long transferSize = (transferSi / 1024);
                        progressDialog.setMessage("Uploaded" + ((int) transferSize) + "KB /" + ((int) totalSize) + "KB");
                    }
                });
    }

    private void uplImageUrlToDatabase() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("image", imageUrl);
        reference.child(user.getUid())
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Profile_update.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    }
                });
    }

    private void loadInterstitialAd() {
        // private InterstitialAd interstitialAd;
        com.facebook.ads.InterstitialAd mInterstitial = new com.facebook.ads.InterstitialAd(this, getString(R.string.fb_interstitial_id));
        mInterstitial.loadAd();
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(it);
        finish();


    }
}