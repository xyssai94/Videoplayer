package com.example.lenovo.camera;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecognizerIntentActivity extends AppCompatActivity{

    private Button btnReconizer;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private EditText instruction;
    private TextView sentence;


    private static final String TAG="CameraPreview";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognizer_intent);

        requestMultiplePermissions();

        btnReconizer=(Button) this.findViewById(R.id.btnRecognizer);
        btnReconizer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                sentence =(TextView)findViewById(R.id.sentence);
                sentence.setText("");

                try{
                    //use Intent to activate speech window
                    Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    //free form and web search model
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    //indicate it is working
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Recoginizing...");
                    //start activity
                    startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
                }catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Can't find any voice equipments", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private static final int REQUEST_CODE = 1;
    private void requestMultiplePermissions(){
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};
        requestPermissions(permissions, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        instruction =(EditText)findViewById(R.id.instruction);
        sentence =(TextView)findViewById(R.id.sentence);
        sentence.setText("");
        String sInstruction=instruction.getText().toString();
        if(sInstruction.equals("")){
            sInstruction="hello world";
        }
        boolean flag=false;
        //get data from google voice
        if(requestCode==VOICE_RECOGNITION_REQUEST_CODE && resultCode==RESULT_OK){
            //obtain phrases and sentences you just said
            ArrayList<String> results=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String resultString="";
            for(int i=0;i<results.size();i++){
                resultString+=results.get(i)+"//";
                if(results.get(i).contains(sInstruction)){ //equals(sInstruction)
                    flag=true;
                }
            }
            //Toast.makeText(this, resultString , 1).show();
            sentence.setText(resultString);
            if(flag==true){
                Intent intent=new Intent(this,MainActivity.class);
                startActivity(intent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
