package com.example.vission;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;
@IgnoreExtraProperties
public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    public String id="";
    TextView txt;
    DatabaseReference dtb;
    FirebaseDatabase dd;
    GoogleMap map;
    double lat, lon;
    Marker myMarker;
    CircleImageView profp;
    ImageView addon;
    private byte[] encryptionKey={5,115,51,86,105,4,-31,-23,-68,88,17,20,3,-105,119,-53};
    private Cipher cipher, decipher;
    private SecretKeySpec secretKeySpec;
    //FusedLocationProviderClient fusedLocationClient;
    ArrayList<Double> maplat = new ArrayList<Double>();
    ArrayList<Double> maplon = new ArrayList<Double>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        profp=findViewById(R.id.profp);
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
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        id=acct.getId();
        Uri url= acct.getPhotoUrl();
        Glide.with(MainActivity.this).load(url).into(profp);
        SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        System.out.println();
        System.out.println(id);
        System.out.println();
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        addon=findViewById(R.id.addon);
        addon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddMoney.class));
            }
        });

        txt=findViewById(R.id.moneyt);
        dd=FirebaseDatabase.getInstance();
        dtb=dd.getReference(id);
        dtb.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    dtb.child("Money").setValue(""+0);
                }
                String mon=dataSnapshot.child("Money").getValue().toString();
                txt.setText(AESDecryptionMethod(mon));
                //txt.setText(Objects.requireNonNull(dataSnapshot.child("Money").getValue()).toString());
               for(DataSnapshot ds : dataSnapshot.child("Shop").getChildren())
               {
                   double lat1=Double.parseDouble(AESDecryptionMethod(ds.child("lat").getValue().toString()));
                   maplat.add(lat1);
                   double lon1=Double.parseDouble(AESDecryptionMethod(ds.child("lon").getValue().toString()));
                   String shopname= ds.getKey();
                   maplon.add(lon1);
                   drawmap(lat1,lon1,shopname);
               }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
        @Override
        public void onMapReady (GoogleMap googleMap){
        map = googleMap;
        /*LatLng p1 = new LatLng(lat, lon);
        //LatLng p2= new LatLng(19.115179, 72.893380);
        map.addMarker(new MarkerOptions().position(p1).title("Xeno Pharma"));
        //map.addMarker(new MarkerOptions().position(p2).title("Thambi"));
        map.moveCamera(CameraUpdateFactory.newLatLng(p1));
        //map.moveCamera(CameraUpdateFactory.newLatLng(p2));*/
    }
    void drawmap(double lat1, double lon1, final String name)
    {
       // GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
       // String name=acct.getDisplayName();
        LatLng p1 = new LatLng(lat1, lon1);
        //LatLng p2= new LatLng(19.115179, 72.893380);
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Shopinfo.shopname=name;
                Shopinfo.id=id;
                startActivity(new Intent(MainActivity.this, Shopinfo.class));
            }
        });
        myMarker=map.addMarker(new MarkerOptions().position(p1).title(name).icon(bt(this,R.drawable.ic_25473)));
        //map.setOnMarkerClickListener(this);
        //map.addMarker(new MarkerOptions().position(p2).title("Thambi"));
        map.moveCamera(CameraUpdateFactory.newLatLng(p1));
        //map.moveCamera(CameraUpdateFactory.newLatLng(p2));
    }
    BitmapDescriptor bt(MainActivity cont, int vec)
    {
        Drawable img= ContextCompat.getDrawable(cont,vec);
        img.setBounds(0,0,img.getIntrinsicWidth(),img.getIntrinsicHeight());
        Bitmap bmp=Bitmap.createBitmap(img.getIntrinsicWidth(),img.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas cv=new Canvas(bmp);
        img.draw(cv);
        return BitmapDescriptorFactory.fromBitmap(bmp);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.equals(myMarker))
        {
            //startActivity(new Intent(MainActivity.this, shop.class));
            return true;
        }
        else
            return false;
    }
    public String AESEncryptionMethod(String s) {
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
