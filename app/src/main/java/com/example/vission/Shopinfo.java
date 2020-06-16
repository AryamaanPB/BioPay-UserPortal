package com.example.vission;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Shopinfo extends AppCompatActivity {
 public static String shopname="";
 public static String monn="";
 public static String id="";
 TextView shpn,moneyt;
    DatabaseReference dtb;
    FirebaseDatabase dd;
    private byte[] encryptionKey={5,115,51,86,105,4,-31,-23,-68,88,17,20,3,-105,119,-53};
    private Cipher cipher, decipher;
    private SecretKeySpec secretKeySpec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopinfo);
        try {
            cipher=Cipher.getInstance("AES");
            decipher=Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        //adding=findViewById(R.id.adding);
        secretKeySpec=new SecretKeySpec(encryptionKey,"AES");
        dd=FirebaseDatabase.getInstance();
        dtb=dd.getReference(id);
        dtb.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    monn=dataSnapshot.child("Shop").child(shopname).child("Spent").getValue().toString();
                    System.out.println(monn);
                    System.out.println(shopname);
                    String sett=AESDecryptionMethod(monn);
                    System.out.println(sett);
                    //String k=moneyt.getText().toString();
                    moneyt.setText("Money Spent:"+" Rs."+sett);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        shpn=findViewById(R.id.shpn);
        moneyt=findViewById(R.id.label1);
        shpn.setText(shopname);


    }
    public String AESDecryptionMethod(String s)
    {
        byte[] encryptedbyte = new byte[0];
        try {
            encryptedbyte=s.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String decryptedstring=null;
        byte[] decryption;
        try {
            decipher.init(Cipher.DECRYPT_MODE,secretKeySpec);
            decryption=decipher.doFinal(encryptedbyte);
            decryptedstring=new String(decryption);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decryptedstring;
    }
}
