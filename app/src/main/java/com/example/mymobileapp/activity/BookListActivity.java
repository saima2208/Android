package com.example.mymobileapp.activity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mymobileapp.R;
import com.example.mymobileapp.adapter.BookAdapter;
import com.example.mymobileapp.model.Book;
import com.example.mymobileapp.service.ApiService;
import com.example.mymobileapp.util.BookDiffCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BookListActivity extends AppCompatActivity {
    private static final String TAG = "BookListActivity";
    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<Book> bookList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Enable the Up button (back arrow) in the ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        bookAdapter = new BookAdapter(this,bookList);
        recyclerView = findViewById(R.id.bookRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(bookAdapter);

        fetchBooks();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void fetchBooks() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.7:8081/")

                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<Book>> call = apiService.getAllBook();

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Book>> call, @NonNull Response<List<Book>> response) {
                if (response.isSuccessful()) {
                    List<Book> books = response.body();
                    assert books != null;
                    for (Book book1 : books) {
                        Log.d(TAG, "ID: " + book1.getId() + ", Name: "
                                + book1.getName() + ", Author_Name: " + book1.getAuthor_name() + ", Price: " + book1.getPrice());
                    }
                    DiffUtil.DiffResult result = DiffUtil.calculateDiff(new BookDiffCallback(bookList, books));
                    bookList.clear();
                    bookList.addAll(books);
                    bookAdapter.notifyDataSetChanged();
//                    result.dispatchUpdatesTo(employeeAdapter);
                } else {
                    Log.e(TAG, "API Response Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Book>> call, @NonNull Throwable t) {
                Log.e(TAG, "API Call Failed: " + t.getMessage());
            }
        });
    }
}