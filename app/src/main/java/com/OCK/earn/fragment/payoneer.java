package com.OCK.earn.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.ValueEventListener;
import com.OCK.earn.R;
import com.OCK.earn.model.ProfileModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.OCK.earn.model.Veriables.paytmImage;

public class payoneer extends Fragment {
    private EditText paytm_username,number_paytm;
    private TextView coinsEt;
    private Button send_btn;
    private RadioGroup radioGroup;
    private Dialog dialog;
    private DatabaseReference reference;
    private FirebaseUser user;

    public payoneer() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.payoneer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        loadData();
        clickListener();
    }
    private void init(View view)
    {

        radioGroup=view.findViewById(R.id.radioGroup);
        send_btn=view.findViewById(R.id.submit_btn);
        coinsEt=view.findViewById(R.id.coinEt);
        paytm_username=view.findViewById(R.id.paytm_username);
        number_paytm=view.findViewById(R.id.number_paytm);
        dialog= new Dialog(getContext());
        dialog.setContentView(R.layout.loading_dialog);
        if (dialog.getWindow() !=null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        reference= FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseAuth auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

    }
    private void loadData()
    {
        reference.child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ProfileModel model= snapshot.getValue(ProfileModel.class);
                        coinsEt.setText(String.valueOf(model.getCoins()));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error"+error.getMessage(), Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                        dialog.dismiss();
                    }
                });

    }
    private void clickListener()
    {
        send_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dialog.show();
                String name=paytm_username.getText().toString();
                String phone=number_paytm.getText().toString();
                if(TextUtils.isEmpty(name)||TextUtils.isEmpty(phone))
                {
                    Toast.makeText(getContext(), "Fill The Fields", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                int id=radioGroup.getCheckedRadioButtonId();
                switch (id)
                {
                    case R.id.paytm50:
                        checkCoins(name,phone,50);
                        break;
                    case R.id.paytm100:
                        checkCoins(name,phone,100);
                        break;
                }

            }
        });
    }
    DatabaseReference withdrafRef;
    private void checkCoins(final String name,final String phone,int amount)
    {

        int current=Integer.parseInt(coinsEt.getText().toString().trim());
        if(amount==50) {
            if (current < 10000) {
                Toast.makeText(getContext(), "You do not have Enough coins", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }
            sendWithdrawRequest(name,phone,amount);
        }
        if(amount==100) {
            if (current < 20000) {
                Toast.makeText(getContext(), "You do not have Enough coins", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }
            sendWithdrawRequest(name,phone,amount);
        }
    }
    private void sendWithdrawRequest(final String name,final String phone,int amount)
    {

        withdrafRef=FirebaseDatabase.getInstance().getReference().child("Withdraw").child(user.getUid());
        String id=withdrafRef.push().getKey();
        HashMap<String,Object> map=new HashMap<>();

        if (amount==50) {
            map.put("amount", 50);
        }
        if (amount==100) {
            map.put("amount", 100);
        }

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,0);
        Date previousDate=calendar.getTime();
        String dateString= dateFormat.format(previousDate);

        map.put("phone",phone);
        map.put("name",name);
        map.put("id",id);
        map.put("type","Mobile Account");
        map.put("date",dateString);
        map.put("status","Pending");
        map.put("image",paytmImage);
        map.put("uid",user.getUid());
        int current=Integer.parseInt(coinsEt.getText().toString().trim());

        int update=0;
        if(amount==50) {
            update = current - 10000;
        }
        if(amount==100)
        {
            update = current - 20000;
        }
        HashMap<String,Object> userMap=new HashMap<>();
        userMap.put("coins",update);
        reference.child(user.getUid()).updateChildren(userMap);
        withdrafRef.child(id).setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(getContext(), "Request Send Successfully", Toast.LENGTH_SHORT).show();
                            number_paytm.setText("");
                            paytm_username.setText("");
                            dialog.dismiss();
                        }
                    }
                });
    }
}