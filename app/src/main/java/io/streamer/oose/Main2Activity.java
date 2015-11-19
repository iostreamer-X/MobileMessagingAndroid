package io.streamer.oose;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Random;

public class Main2Activity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    static Context context;
    static String phone;
    static JSONArray contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Took me two days");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(new ArrayList<DataObject>(),this);
        mRecyclerView.setAdapter(mAdapter);

        phone = Util.loadPhone(this);
        Util.getContactsFromServer(this);
    }

    public static void addContact(String name, String phone){
        int[] colors={R.color.green,R.color.orange,R.color.pink,R.color.purple,R.color.saffron,R.color.sienna};
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(name.substring(0,1), context.getResources().getColor(colors[new Random().nextInt(colors.length)]));
        DataObject object = new DataObject(name,phone,drawable);
        ((MyRecyclerViewAdapter) mAdapter).addItem(object, mAdapter.getItemCount());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                TextView name = (TextView)v.findViewById(R.id.textView);
                TextView phone = (TextView)v.findViewById(R.id.textView2);
                Intent i = new Intent(getBaseContext(), Chat.class);
                i.putExtra("name",name.getText().toString());
                i.putExtra("phone",phone.getText().toString());
                startActivity(i);

            }
        });
    }
}
