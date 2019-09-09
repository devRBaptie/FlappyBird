package me.reece.flappybird;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class activity_high_score extends AppCompatActivity {

    TextView score_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        //_________________________FIREBASE______________________//

        score_tv = findViewById(R.id.textViewScore2);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                showData(dataSnapshot);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            if(ds != null)
            {
                try {
                    score_tv.setTextSize(40);
                    score_tv.setText(ds.getValue(String.class));
                }catch(Exception e){
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    score_tv.setText(e.toString());
                }
            }
        }
    }
}
