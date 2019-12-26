package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
