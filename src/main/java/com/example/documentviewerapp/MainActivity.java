package com.example.documentviewerapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final int PICKFILE_RESULT_CODE = 2;
    Uri recentFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getIntent().getExtras();
        if (data != null)  {
            recentFile = (Uri) data.get("uri");
            getContentResolver().takePersistableUriPermission(recentFile, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        setContentView(R.layout.activity_main);
        findViewById(R.id.buttonRecent).setEnabled(recentFile != null);
    }

    public void launchView(Uri file) {
        String mimeType = getContentResolver().getType(file);
        Bundle args = new Bundle();
        args.putString("uri", file.toString());
        setContentView(R.layout.main);
        if (mimeType.equals("text/plain")) {
            TxtView t = new TxtView();
            t.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, t)
                    .commitNow();
        } else if (mimeType.equals("application/pdf")) {
            PdfView p = new PdfView();
            p.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, p)
                    .commitNow();
        } else if (mimeType.equals("text/markdown")) {
            MdView m = new MdView();
            m.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, m)
                    .commitNow();
        }
    }

    public void openRecent(View view) {
        launchView(recentFile);
    }

    public void openFile(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        String[] mimeTypes = {"application/pdf", "text/plain", "text/markdown"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(intent, PICKFILE_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedFile = data.getData();
            getContentResolver().takePersistableUriPermission(selectedFile, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            recentFile = selectedFile;
            launchView(selectedFile);
        }
    }

}