package me.felix.pgm.sample;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.b_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAction();
            }
        });

    }

    private void onAction() {
        Resources resources = getResources();
        File file = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        FileOutputStream fout = null;
        try {
            InputStream is = resources.openRawResource(R.raw.apollonian_gasket);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            PGMImage image = PGMImage.fromBitmap(bitmap);
            fout = new FileOutputStream(new File(file, "q.pgm"));
            if (image.save(fout)) {
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fout != null) {
                    fout.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}