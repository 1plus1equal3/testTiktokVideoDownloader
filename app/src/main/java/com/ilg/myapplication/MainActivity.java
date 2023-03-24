package com.ilg.myapplication;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    String baseUrl = null;
    TiktokService service = Client.getInstance().getApi();
    EditText input;
    Button btn;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input = findViewById(R.id.input_link);
        btn = findViewById(R.id.btn);

        //Check permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            } else{
                ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 100);
            }
        }

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                btn.setOnClickListener(view -> {
                    baseUrl = String.valueOf(input.getText());
                    Log.e("URL", baseUrl);
                    callAPI();
                    Toast.makeText(MainActivity.this, "Get Video", Toast.LENGTH_SHORT).show();
                });

            }
        });
//        callAPI();
    }

    @SuppressLint("CheckResult")
    public void callAPI(){
        //Reset cookie
        Client.getInstance().cookieInterceptor.cookie = "";
        //Get cookie + video url
        Observable<ResponseBody> response = service.getURL(baseUrl);
        response.flatMap(responseBody1 -> {
            String responseStr = responseBody1.string();
            String cookies = Client.getInstance().cookieInterceptor.getCookie();
            String url = findString(responseStr);
            Log.e("Cookies ", cookies);
            Log.e("Url", url);
            String decode = url.replaceAll("\\\\u002F", "/");
            Log.e("Url decode", decode);
                    return service.getVideo(decode, cookies);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(responseBody2 -> {
                    Log.e("Response", "Success");
                    Log.e("Size", Client.getInstance().cookieInterceptor.size);
                    Log.e("Type", String.valueOf(responseBody2.contentType()));
                    //Save video
                    saveVideo(responseBody2, Integer.parseInt(Client.getInstance().cookieInterceptor.size));
                }, Throwable::printStackTrace);
    }

/*    public void outputFile(String input) throws IOException {
        try (FileOutputStream fos = this.openFileOutput("response.html", Context.MODE_PRIVATE)) {
            fos.write(input.getBytes());
        }
        File file = new File("/storage/emulated/0/Download" + "/" + "response.txt");
        FileWriter writer = new FileWriter(file);
        writer.write(input);
        }*/

        public void saveVideo(ResponseBody body, int size) {
            //create a dir for saved videos
            File file = new File(this.getExternalFilesDir(Environment.DIRECTORY_MOVIES),"Saved video");
            if (!file.mkdirs()) {
                Log.e("Error", "Directory not created");
            }

            //Save response as file
            String path = getExternalFilesDir(Environment.DIRECTORY_MOVIES) + "/" + "Saved video";
            Log.e("path", path);
            InputStream inputStream = body.byteStream();
            try {
                count++;
                FileOutputStream fos = new FileOutputStream(new File(path, "video_" + count + ".mp4"));
                int read;
                byte[] buffer = new byte[size];
                while ((read = inputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, read);
                }
                Log.e("Save", "Successful");
                fos.close();
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    public String findString(String str){
        String a = "downloadAddr";
        StringBuilder url = new StringBuilder();
        int b;
        if(!str.contains(a)) return null;
        b = str.indexOf(a) + 15;
        do{
            url.append(str.charAt(b));
            b++;
        } while (str.charAt(b) != '"');
        return url.toString();
    }
}