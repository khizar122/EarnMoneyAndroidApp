package com.OCK.earn.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.OCK.earn.R;
import com.OCK.earn.model.AmazonModel;
import com.OCK.earn.model.ProfileModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.OCK.earn.model.Veriables.amazonImage;


public class AmazonFragment extends Fragment {
    private RadioGroup radioGroup;
    private Button withdrawBtn;
    private TextView coinsEt;
    DatabaseReference reference;
    FirebaseUser user;
    String name,email;
    private Dialog dialog;


    public AmazonFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_amazon, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        FirebaseAuth auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference().child("Users");
        loadData();
        clickListener();
    }
    private void init(View view)
    {

        radioGroup=view.findViewById(R.id.radioGroup);
        withdrawBtn=view.findViewById(R.id.submit_btn);
        coinsEt=view.findViewById(R.id.coinEt);

        dialog= new Dialog(getContext());
        dialog.setContentView(R.layout.loading_dialog);
        if (dialog.getWindow() !=null)
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
    }
    private void loadData()
    {
        reference.child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProfileModel model= snapshot.getValue(ProfileModel.class);
                    coinsEt.setText(String.valueOf(model.getCoins()));
                    name=model.getName();
                    email=model.getEmail();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
            }
        });

    }
    private void clickListener()
       {
           withdrawBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Dexter.withContext(getActivity()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                           .withListener(new MultiplePermissionsListener() {
                               @Override
                               public void onPermissionsChecked(MultiplePermissionsReport report) {
                                   if(report.areAllPermissionsGranted())
                                   {
                                       String filePath= Environment.getExternalStorageDirectory()+"/Earning Task/Amazon Gift Card";
                                       File file=new File(filePath);
                                       file.mkdirs();
                                       int currentCoins=Integer.parseInt(coinsEt.getText().toString());
                                       int checkedId=radioGroup.getCheckedRadioButtonId();
                                       switch (checkedId)
                                       {
                                           case R.id.amazon100:
                                               AmazonCard(100,currentCoins);
                                               break;
                                           case R.id.amazon200:
                                               AmazonCard(200,currentCoins);
                                               break;

                                       }
                                       
                                   }
                                   else 
                                   {
                                       Toast.makeText(getContext(), "Please Allow Permission", Toast.LENGTH_SHORT).show();
                                   }
                               }

                               @Override
                               public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                               }
                           }).check();


               }
           });
       }
       private void AmazonCard(int amazonCard,int currentCoins)
       {
           if (amazonCard==100)
           {
                if(currentCoins>=12000){
                    sendGiftCard(1);
                }
                else
                {
                    Toast.makeText(getContext(), "Insufficient Coins to Withdraw you need 12000 coins", Toast.LENGTH_SHORT).show();
                }
           }

           else if (amazonCard==200)
               {
                   if(currentCoins>=24000)
                   {
                       sendGiftCard(2);
                   }
                   else
                   {
                       Toast.makeText(getContext(), "Insufficient Coins to Withdraw you need 24000 coins", Toast.LENGTH_SHORT).show();
                   }

           }


       }
       DatabaseReference amazonRef;
        Query query;
       private void sendGiftCard(final int cardAmount)
       {
           dialog.show();
           amazonRef=FirebaseDatabase.getInstance().getReference().child("Gift Cards").child("Amazon");


           if (cardAmount==1)
           {
               query=amazonRef.orderByChild("amazon").equalTo(100);

           }
           else if (cardAmount==2)
           {
               query=amazonRef.orderByChild("amazon").equalTo(200);
           }

           query.addListenerForSingleValueEvent(new ValueEventListener() {
               @RequiresApi(api = Build.VERSION_CODES.KITKAT)
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   Random random= new Random();
                   int childCount=(int) snapshot.getChildrenCount();
                    int rand=random.nextInt(childCount);
                   Iterator iterator= snapshot.getChildren().iterator();

                   for (int i=0; i < rand; i++)
                   {
                    iterator.next();
                   }
                   DataSnapshot childSnap = (DataSnapshot) iterator.next();
                   AmazonModel model=childSnap.getValue(AmazonModel.class);
                   String id= model.getId();
                   String giftCard=model.getAmazonCode();
                   printAmazonCode(id,giftCard,cardAmount);
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {
                   Toast.makeText(getContext(), "Error:"+error.getMessage(), Toast.LENGTH_SHORT).show();
               dialog.dismiss();
               }
           });
       }
       @RequiresApi(api = Build.VERSION_CODES.KITKAT)
       private void printAmazonCode(String id, String amazonCode,int cardAmount)
       {
           updateDate(cardAmount,id,amazonCode);
           Date date= Calendar.getInstance().getTime();
           SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.ENGLISH);
           String currentTime=dateFormat.format(date);
           String text="Date:"+currentTime+"\n"+"\n Name:"+name+"\n"+"\n Email:"+email+"\n"+
                   "Redeem ID:"+id+""+"Amazon Claim Code:"+amazonCode+ "\n";


           PdfDocument pdfDocument= new PdfDocument();
           PdfDocument.PageInfo pageInfo=new PdfDocument.PageInfo.Builder(800,800,1).create();
           PdfDocument.Page page=pdfDocument.startPage(pageInfo);
           Paint paint=new Paint();
           page.getCanvas().drawText(text ,10,25,paint);
           pdfDocument.finishPage(page);
           String filePath= Environment.getExternalStorageDirectory()+"/Earning Task/Amazon Gift Card/"+System.currentTimeMillis()+user.getUid()+"AmazonCode.pdf";

           File file=new File(filePath);
           try {
               pdfDocument.writeTo(new FileOutputStream(file));

           } catch (FileNotFoundException e) {

               e.printStackTrace();
           } catch (IOException e) {

               e.printStackTrace();
           }
           pdfDocument.close();

           Intent intent = new Intent(Intent.ACTION_VIEW);
           intent.setDataAndType(Uri.fromFile(file),"application/pdf");
           intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

           try {
               startActivity(Intent.createChooser(intent,"Open with"));
           }catch (ActivityNotFoundException e)
           {
               Toast.makeText(getContext(), "Please install pdf reader", Toast.LENGTH_SHORT).show();
           }
       }
       private void updateDate(int cardAmount,String id,String amazonCode)
       {

           HashMap<String,Object> map=new HashMap<>();
           int currentCoins=Integer.parseInt(coinsEt.getText().toString());

            HashMap<String,Object> withdrawMap=new HashMap<>();

           if(cardAmount==1)
           {
               int updatedCoins=currentCoins-12000;
               map.put("coins",updatedCoins);
               withdrawMap.put("amount",100);
           }
           else if (cardAmount==2)
               {
                   int updatedCoins=currentCoins-24000;
                   map.put("coins",updatedCoins);
                   withdrawMap.put("amount",200);
           }
           reference.child(user.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {

                   if(task.isSuccessful())
                   {
                       Toast.makeText(getContext(), "Congracts", Toast.LENGTH_SHORT).show();
                   }
                dialog.dismiss();
               }
           });
            FirebaseDatabase.getInstance().getReference()
           .child("Gift Cards").child("Amazon")
                   .child(id).removeValue();

            DatabaseReference withReference=FirebaseDatabase.getInstance().getReference().child("Withdraw").child(user.getUid());
            String pushID=withReference.push().getKey();
           Date date = Calendar.getInstance().getTime();
           SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
           Calendar calendar=Calendar.getInstance();
           calendar.add(Calendar.DAY_OF_MONTH,0);
           Date previousDate=calendar.getTime();
           String dateString= dateFormat.format(previousDate);


           withdrawMap.put("type","amazon");
           withdrawMap.put("phone",amazonCode);
           withdrawMap.put("name",email);
           withdrawMap.put("id",pushID);
           withdrawMap.put("status","Completed");
           withdrawMap.put("image",amazonImage);
           withdrawMap.put("date",dateString);
           withdrawMap.put("uid",user.getUid());
           withReference.child(pushID).setValue(withdrawMap);
       }


}