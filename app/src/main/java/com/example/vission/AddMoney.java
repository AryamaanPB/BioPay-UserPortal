package com.example.vission;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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

public class AddMoney extends AppCompatActivity {
Button addmoney;
EditText money;
DatabaseReference dtb;
FirebaseDatabase dd;
ImageView addmoneyt;
    private byte[] encryptionKey={5,115,51,86,105,4,-31,-23,-68,88,17,20,3,-105,119,-53};
    private Cipher cipher, decipher;
    private SecretKeySpec secretKeySpec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);
        addmoneyt=findViewById(R.id.addmoneyt);
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
        money=findViewById(R.id.moneyt);
        addmoney=findViewById(R.id.addmoney);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(AddMoney.this);
        final String id=acct.getId();
        dd=FirebaseDatabase.getInstance();
        dtb=dd.getReference(id);
        addmoney.setOnClickListener(new View.OnClickListener() {
            int mon=0;
            //String encrypttt="demo";
            @Override
            public void onClick(View v) {
                dtb.addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String m=AESDecryptionMethod(dataSnapshot.child("Money").getValue().toString());
                        mon=Integer.parseInt(money.getText().toString())+Integer.parseInt(m);
                        System.out.println(m+" "+mon+" 1");
                        //encrypttt=AESEncryptionMethod(""+mon);
                        //System.out.println(encrypttt);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //dtb.child("Money").setValue(encrypttt);
                dtb.child("Money").setValue(AESEncryptionMethod(""+mon));
            }
        });
        addmoneyt.setOnClickListener(new View.OnClickListener() {
            int mon=0;
            String encrypttt="demo";
            @Override
            public void onClick(View v) {
                dtb.addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String m=AESDecryptionMethod(dataSnapshot.child("Money").getValue().toString());
                         mon=Integer.parseInt(money.getText().toString())+Integer.parseInt(m);
                        System.out.println(m+" "+mon);
                        encrypttt=AESEncryptionMethod(""+mon+" 2");
                        System.out.println(encrypttt);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //dtb.child("Money").setValue(encrypttt);
                //dtb.child("Money").setValue(AESEncryptionMethod(""+mon));
            }
        });
    }
    private String AESEncryptionMethod(String s) {
        byte[] stringbyte=s.getBytes();
        byte[] encryptedbyte= new byte[stringbyte.length];
        try {
            cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
            encryptedbyte=cipher.doFinal(stringbyte);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        String returnstring=null;
        try {
            returnstring = new String(encryptedbyte,"ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return returnstring;
    }
    private String AESDecryptionMethod(String s)
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
