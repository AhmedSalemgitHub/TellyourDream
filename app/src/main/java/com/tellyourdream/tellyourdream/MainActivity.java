package com.tellyourdream.tellyourdream;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.app.AlertDialog;


import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "test";

    //for the sharedpreferences
    static SharedPreferences mPreferences;


    /* initialize variables */
    public static final int RC_SIGN_IN = 1;
    public static String mUserName = "user";
    public static String mEmail = "null";


    //creating the list of dreams
    public static MyAdapter adapter;
    public ListView listView;
    public static ArrayList<myDreamItems> mainDreamList;

    //the database of dreams
    public static DatabaseReference mMessageDataBaseReference;
    Query query;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ChildEventListener mChiledEventListener;
    FirebaseUser user;

    /*settings variables*/
    private String setting_name, setting_age, setting_gender, setting_marital, setting_privacy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        /* initialize fire base variables */
        FirebaseDatabase mFirebaseDataBase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mMessageDataBaseReference = mFirebaseDataBase.getReference().child("dream");

        query = mMessageDataBaseReference.orderByChild("ownerEmail").equalTo(mEmail);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //user signed in
                    onSignedInInitialized(user.getDisplayName(),user.getEmail());
                    mUserName = user.getDisplayName();
                    mEmail =firebaseAuth.getCurrentUser().getEmail();
                } else {
                    onSignedOutCleanUp();
                    // Choose authentication providers
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build()

                    );
                    // Create and launch sign-in intent
                    startActivityForResult(

                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        //get the data from the setting screen
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setting_name = mPreferences.getString("full_name","");
        setting_age = mPreferences.getString("age","");
        setting_gender = mPreferences.getString("gender","");
        setting_marital = mPreferences.getString("marriage","");
        setting_privacy = mPreferences.getString("private","");


        // Initialize message ListView and its adapter
        mainDreamList = new ArrayList<myDreamItems>();
        Log.i("saving","array list created");
        adapter = new MyAdapter(this, mainDreamList);
        Log.i("saving","adapter created and array pluged");
        attachedDataBaseReadListener();
        Log.i("saving","attach listener finished");

        listView = findViewById(R.id.list);
        Log.i("saving","list view attached to xml");
        listView.setAdapter(adapter);
        Log.i("saving","adapter assigned");


        /*open the dreams from the list */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final myDreamItems currentSelected = mainDreamList.get(position);
                DatabaseReference CurrentKey = mMessageDataBaseReference.child(currentSelected.getParentKey()).child("openedstatus");

                CurrentKey.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String openstatus = dataSnapshot.getValue().toString();

                        if (openstatus.equals("مفتوح")) {
                            Toast.makeText(MainActivity.this, "هذا الحلم مفتوح من احد المفسرين", Toast.LENGTH_LONG).show();
                        } else {
                            Intent openDream = new Intent(MainActivity.this, Dream.class);

                            openDream.putExtra("owner", currentSelected.getOwner());
                            openDream.putExtra("dreamDetails", currentSelected.getDreamDetails());
                            openDream.putExtra("age", currentSelected.getAge());
                            openDream.putExtra("marriageStatus", currentSelected.getMaritalStatus());
                            openDream.putExtra("gender", currentSelected.getGender());
                            openDream.putExtra("time", currentSelected.getDreamTime());
                            openDream.putExtra("openedstatus", currentSelected.getOpenedstatus());
                            openDream.putExtra("replystatus", currentSelected.getReplystatus());
                            openDream.putExtra("userEmail", currentSelected.getOwnerEmail());
                            openDream.putExtra("reply", currentSelected.getReply());
                            openDream.putExtra("parent", currentSelected.getParentKey());
                            openDream.putExtra("mode", "view");

                            HashMap<String, Object> result = new HashMap<>();
                            result.put("openedstatus", "مفتوح");
                            mMessageDataBaseReference.child(currentSelected.getParentKey()).updateChildren(result);
                            adapter.notifyDataSetChanged();

                            startActivity(openDream);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
    /*the log in process*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        Log.i("test", "on activity result");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Log.i("test", "requestCode " + requestCode);
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "signed in !", Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "signed in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /*attach the listener for the changes from the firebase*/
    private void attachedDataBaseReadListener() {
        Log.i("test", "attached data base reader");
        if (mChiledEventListener == null) {
            mChiledEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    myDreamItems dreams = dataSnapshot.getValue(myDreamItems.class);
                    dreams.setParentKey(dataSnapshot.getRef().getKey().toString());
                    mainDreamList.add(dreams);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        String key = dataSnapshot.getKey();
                        for (myDreamItems dream : mainDreamList){
                            if(key.equals(dream.getParentKey())){
                                mainDreamList.remove(dream);
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mMessageDataBaseReference.addChildEventListener(mChiledEventListener);

        }
    }

    private void detachedDataBaseReadListener() {
        listView.onSaveInstanceState();
        adapter.clear();
        Log.i("test", "detached data base listener");
        if (mChiledEventListener != null) {
            mMessageDataBaseReference.removeEventListener(mChiledEventListener);
            mChiledEventListener = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // this tho make the program start in Arabic
        Locale locale = new Locale("Ar");
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

        //attachedDataBaseReadListener();
    }

    @Override
    protected void onPause() {
        Log.i(TAG,"onPause");
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        detachedDataBaseReadListener();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.add_dream_menu:
                if(getSettingData()){
                    Intent adddream = new Intent(MainActivity.this, Dream.class);

                    adddream.putExtra("owner", setting_name);
                    adddream.putExtra("dream", "add details");
                    adddream.putExtra("date", "add details");
                    adddream.putExtra("gender",setting_gender);
                    adddream.putExtra("age",setting_age);
                    adddream.putExtra("marriageStatus",setting_marital);
                    adddream.putExtra("userEmail", mEmail);
                    adddream.putExtra("reply","لم يتم التفسير");
                    adddream.putExtra("replystatus","غير مفسر");
                    adddream.putExtra("openedstatus","مغلق");
                    adddream.putExtra("privacy",setting_privacy);
                    adddream.putExtra("mode", "edit");
                    startActivity(adddream);
                }else{
                    Toast.makeText(this,R.string.error_no_data,Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.Log_out_menu:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Log out !!");
                alert.setMessage("Your are logging out , click yes to Exit")
                        .setCancelable(false)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.clear();
                        AuthUI.getInstance().signOut(MainActivity.this);

                    }
                   // return true;
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alerton = alert.create();

                alerton.show();
                return true;
            case R.id.Profile_menu:
                Intent settingsIntent = new Intent(this, setting.class);
                startActivity(settingsIntent);
                return true;

            case R.id.my_dreams:
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        adapter.clear();
                        for (DataSnapshot queryDream : dataSnapshot.getChildren()){
                            myDreamItems qDream = queryDream.getValue(myDreamItems.class);
                            qDream.setParentKey(queryDream.getRef().getKey());
                            adapter.add(qDream);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return true;

            case R.id.my_how_to_use:
                Intent howTo = new Intent(MainActivity.this, how_to.class);
                startActivity(howTo);

                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    private boolean getSettingData() {
        boolean result = false;
        if (
                (!setting_name.equals("empty") && !setting_name.isEmpty()) &&
                (!setting_age.equals("empty") && !setting_age.isEmpty()) &&
                (!setting_gender.equals("empty") && !setting_gender.isEmpty()) &&
                (!setting_marital.equals("empty") && !setting_marital.isEmpty()) &&
                (!setting_privacy.equals("empty") && !setting_privacy.isEmpty())
                )
        {
            result = true;
        }
        return result;
    }

    private void onSignedInInitialized(String mUserName,String userMail) {
        mUserName = setting_name;
        mEmail = userMail;
        Log.i("test", "signedininitializer");
        attachedDataBaseReadListener();
    }

    private void onSignedOutCleanUp() {
        mUserName = "user";
        adapter.clear();
        Log.i("test", "adapter cleaned");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


}