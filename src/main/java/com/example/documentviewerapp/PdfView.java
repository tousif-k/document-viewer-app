package com.example.documentviewerapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;

public class PdfView extends Fragment {

    Uri DOCUMENT_URI;

    ImageView pdfPageView;
    Button prevButton;
    Button nextButton;
    TextView pageNum;

    PdfRenderer renderer;
    PdfRenderer.Page page;
    int pageCount;
    int currPageNum = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pdfview, container, false);
        pdfPageView = view.findViewById(R.id.pdfView);
        prevButton = view.findViewById(R.id.prev);
        nextButton = view.findViewById(R.id.next);
        pageNum = view.findViewById(R.id.pageNum);
        DOCUMENT_URI = Uri.parse(getArguments().getString("uri"));

        try {
            ParcelFileDescriptor parcelFileDescriptor = getActivity().getContentResolver().openFileDescriptor(DOCUMENT_URI, "r");
            renderer = new PdfRenderer(parcelFileDescriptor);
            pageCount = renderer.getPageCount();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        prevButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                displayPage(page.getIndex() - 1);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                displayPage(page.getIndex() + 1);
            }
        });

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
        displayPage(currPageNum);
    }

    @Override
    public void onStop() {
        super.onStop();
        renderer.close();
    }

    private void displayPage(int index) {
        page = renderer.openPage(index);
        Bitmap mBitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
        prevButton.setEnabled(index > 0);
        nextButton.setEnabled(index + 1 < pageCount);
        pageNum.setText((index + 1) + " / " + pageCount);
        page.render(mBitmap,null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        pdfPageView.setImageBitmap(mBitmap);
        page.close();
    }

}
