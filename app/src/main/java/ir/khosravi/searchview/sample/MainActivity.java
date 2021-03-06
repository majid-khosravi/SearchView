package ir.khosravi.searchview.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import ir.khosravi.general.searchview.SearchView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.text);
        SearchView searchView = findViewById(R.id.search_bar);
        if(textView != null)
        searchView.setOnTextChangedListener(textView::setText);
    }
}