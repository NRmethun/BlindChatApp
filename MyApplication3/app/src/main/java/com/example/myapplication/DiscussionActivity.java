package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class DiscussionActivity extends AppCompatActivity {

    Button btnSendMsg ;
    EditText etMsg ;
    ListView lvDiscussion ;
    ArrayList<String> listConversation =new ArrayList<String>() ;
    ArrayAdapter arrayAdpt ;
    String UserName ,SelecTopic ,user_msg_key ;

    private DatabaseReference dbr  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        btnSendMsg=findViewById(R.id.btnSendMsg);
        etMsg =findViewById(R.id.etMessage) ;
        lvDiscussion=findViewById(R.id.lvDiscussion) ;
        arrayAdpt =new ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,listConversation) ;
        lvDiscussion.setAdapter(arrayAdpt) ;


        UserName =getIntent().getExtras().get("user_name").toString() ;
        SelecTopic=getIntent().getExtras().get("selected_topic").toString() ;
        setTitle("Topic : "+SelecTopic) ;



        dbr = FirebaseDatabase.getInstance().getReference().child(SelecTopic) ;



        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String ,Object> map =new HashMap<String , Object>() ;
                user_msg_key =dbr.push().getKey() ;
                dbr.updateChildren(map) ;
                DatabaseReference dbr2 =dbr.child(user_msg_key) ;
                Map<String, Object> map2 =new HashMap<String, Object>() ;
                map2.put("msg",etMsg.getText().toString() ) ;
                map2.put("user",UserName) ;
                dbr2.updateChildren(map2) ;



            }
        });


        dbr.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateConversation(dataSnapshot) ;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                updateConversation(dataSnapshot) ;
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;

    }

    public void getSpeechInput(View view) {

       Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    etMsg.setText(result.get(0));
                }
                break;
        }
    }


    public  void  updateConversation(DataSnapshot dataSnapshot)
    {
        String msg,user ;

        Iterator i =dataSnapshot.getChildren().iterator() ;

        while(i.hasNext())
        {
            msg =(String) ((DataSnapshot)i.next()).getValue() ;
            user=(String) ((DataSnapshot)i.next()).getValue() ;
           String  conversation =user + ": "+ msg ;

            arrayAdpt.insert( conversation, 0) ;
            arrayAdpt.notifyDataSetChanged();
        }
    }
}
