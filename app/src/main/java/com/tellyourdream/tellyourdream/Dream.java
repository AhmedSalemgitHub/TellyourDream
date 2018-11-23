package com.tellyourdream.tellyourdream;


import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;



public class Dream extends AppCompatActivity {


    public String current_mode;

    private String owner;
    private String dream;
    private String dreamDate;
    private int age;
    private String marriageStatus;
    private String Gender;
    private String Privacy;
    private String Reply;
    private String userEmail;
    private String replyStatus;
    private String openStatus;
    private String parentKey;

    private String oldDream;
    private String oldreplyStatus;
    private boolean editButtonClicked = false;

    EditText ownerEditText;
    EditText dreamEditText;
    EditText dreamEditEditText;
    TextView translation;


    Button send_button;
    Button edit_button;

    private AdView mAdView;

    private DatabaseReference mMessageDataBaseReference;

    boolean willBeClosed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        MobileAds.initialize(this,getString(R.string.adUnitID_owner));

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        FirebaseDatabase mFirebaseDataBase = FirebaseDatabase.getInstance();
        mMessageDataBaseReference = mFirebaseDataBase.getReference().child("dream");


        ownerEditText = findViewById(R.id.owner_edit_view);
        dreamEditText = findViewById(R.id.dream_edit_view);
        dreamEditEditText = findViewById(R.id.etDreamEdit);
        translation = findViewById(R.id.translation_text_view);


        send_button = findViewById(R.id.button_send);
        edit_button = findViewById(R.id.button_edit);


        String transferedprivacy = getIntent().getStringExtra("privacy");
        if (transferedprivacy != null) {
            Privacy = transferedprivacy ;
        }

        String transferedOwner = getIntent().getStringExtra("owner");
        if (transferedOwner != null) {
            owner = transferedOwner;
            ownerEditText.setText(transferedOwner);
        }
        String transferedDream= getIntent().getStringExtra("dreamDetails");
        if (transferedDream != null) {
            dreamEditText.setText(transferedDream);
            dream = transferedDream;
            oldDream = transferedDream;
        }
        String transferedAge= getIntent().getStringExtra("age");
        if (transferedAge != null) {
             age = Integer.parseInt(transferedAge) ;

        }
        String transferedMarriageStatus= getIntent().getStringExtra("marriageStatus");
        if (transferedMarriageStatus != null) {
            marriageStatus = transferedMarriageStatus ;
        }
        String transferedGender = getIntent().getStringExtra("gender");
        if (transferedGender != null) {
            Gender = transferedGender;
        }

        String transferedReplyStatus = getIntent().getStringExtra("replystatus");
        if (transferedReplyStatus != null) {
            replyStatus = transferedReplyStatus;
            oldreplyStatus = transferedReplyStatus;
        }
        String transferedOpenStatus = getIntent().getStringExtra("openedstatus");
        if (transferedOpenStatus != null) {
            openStatus = transferedOpenStatus;
        }

        String transferedUserEmail= getIntent().getStringExtra("userEmail");
        if (transferedUserEmail != null) {
            userEmail = transferedUserEmail;
        }
        String transferedReply = getIntent().getStringExtra("reply");
        if (transferedReply != null) {
            Reply = transferedReply;
            translation.setText(Reply);
        }

        parentKey = getIntent().getStringExtra("parent");

        String form_mode = getIntent().getStringExtra("mode");
        if (form_mode != null) {
            current_mode = form_mode;
            if (current_mode.equals("view")){ //view mode

                if(userEmail.equals(MainActivity.mEmail))//dream owner
                {
                    ownerEditText.setEnabled(false);
                    send_button.setVisibility(View.GONE);
                    dreamEditEditText.setVisibility(View.VISIBLE);
                    dreamEditText.setEnabled(false);
                    edit_button.setVisibility(View.VISIBLE);


                }else{ // normal view without buttons
                    send_button.setVisibility(View.INVISIBLE);
                    edit_button.setVisibility(View.INVISIBLE);
                    dreamEditEditText.setVisibility(View.GONE);
                }
            } else { //add mode
                ownerEditText.setEnabled(false);
                dreamEditText.setEnabled(true);
                dreamEditEditText.setVisibility(View.GONE);
                send_button.setVisibility(View.VISIBLE);
                edit_button.setVisibility(View.INVISIBLE);
            }
        }

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dream = dreamEditText.getText().toString();
                Calendar calendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                /*need to test it on api23*/
                dreamDate = currentDate.format(calendar.getTime());

                if (Privacy.equals("خاص")){
                    owner = "خاص";
                }

                myDreamItems current_dream = new myDreamItems(owner,dream,age,marriageStatus,Gender,dreamDate,replyStatus,openStatus,userEmail,Reply,parentKey);

                mMessageDataBaseReference.push().setValue(current_dream);
                // MainActivity.adapter.notifyDataSetChanged();
                Log.i("test","button action completed");
                finish();

//                Intent forward_dream = new Intent(Dream.this,MainActivity.class);
//                startActivity(forward_dream);
            }
        });

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dream = dreamEditText.getText().toString() + "\n"
                        + "تعديل:" + "\n"
                        + dreamEditEditText.getText().toString();
                editButtonClicked = true;


                if(!dream.equals(oldDream)){
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("dreamDetails", dream);
                    result.put("replystatus","غير مفسر");
                    result.put("openedstatus","مغلق");
                    mMessageDataBaseReference.child(parentKey).updateChildren(result);
                    finish();
                }else{
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("replystatus",oldreplyStatus);
                    result.put("openedstatus","مغلق");
                    mMessageDataBaseReference.child(parentKey).updateChildren(result);
                    finish();
                }
            }
        });


    }


//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.dream_screen_menu, menu);
//        MenuItem deleteButton = menu.findItem(R.id.delete_dream_menu);
//        if(userEmail.equals(MainActivity.mEmail)){
//            deleteButton.setVisible(true);
//        }
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        alert.setTitle("حذف");
//        alert.setMessage("هل انت متاكد انك تريد حذف هذا الحلم")
//                .setCancelable(false)
//                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        DatabaseReference DB = FirebaseDatabase.getInstance().getReference("dream").child(parentKey);
//                        DB.removeValue();
//                        willBeClosed = true;
//                        finish();
//                    }
//                    // return true;
//                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        AlertDialog alerton = alert.create();
//        alerton.show();
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mAdView != null) {
            mAdView.pause();
        }

        if(parentKey!= null){
            if(!editButtonClicked){
                if (!willBeClosed){
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("openedstatus","مغلق");
                    mMessageDataBaseReference.child(parentKey).updateChildren(result);
                    Log.i("test","onPause "  + parentKey);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

}
