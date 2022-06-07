package com.example.documentviewerapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mukesh.MarkdownView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MdView extends Fragment {

    Uri DOCUMENT_URI;
    MarkdownView mdField;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mdview, container, false);
        mdField = view.findViewById(R.id.mdView);
        DOCUMENT_URI = Uri.parse(getArguments().getString("uri"));
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("uri", DOCUMENT_URI);
        startActivity(intent);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        InputStream in;
        try {
            in = getActivity().getContentResolver().openInputStream(DOCUMENT_URI);
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            StringBuilder total = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
            }
            String content = total.toString();
            mdField.setMarkDownText(content);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
