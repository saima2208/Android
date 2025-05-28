package com.example.mymobileapp.activity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.mymobileapp.R;
import com.example.mymobileapp.model.Book;
import com.example.mymobileapp.service.ApiService;
import com.example.mymobileapp.util.ApiClient;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBookActivity extends AppCompatActivity {

    private EditText nameBox, authorNameBox, priceBox;
    private Button btnadd;
    private ApiService apiService = ApiClient.getApiService();
    private boolean isEditMode = false;
    private int bookId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_book);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Enable the Up button (back arrow) in the ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        nameBox = findViewById(R.id.nameBox);
        authorNameBox = findViewById(R.id.authorNameBox);
        priceBox = findViewById(R.id.priceBox);
        btnadd = findViewById(R.id.addButton);

        Intent intent = getIntent();
        if (getIntent().hasExtra("book")) {
            Book book = new Gson()
                    .fromJson(intent.getStringExtra("book"), Book.class);
            bookId = book.getId();

            nameBox.setText(book.getName());
            authorNameBox.setText(book.getAuthor_name());
            priceBox.setText(String.valueOf(book.getPrice()));
            btnadd.setText(R.string.update);
            isEditMode = true;
        }

        btnadd.setOnClickListener(v -> saveOrUpdateBook());

    }

    private void saveOrUpdateBook() {
        String name = nameBox.getText().toString().trim();

        String authorName = authorNameBox.getText().toString().trim();

        double price = Double.parseDouble(priceBox.getText().toString().trim());

        Book book = new Book();
        if (isEditMode) {
            book.setId(bookId);
        }
        book.setName(name);
        book.setAuthor_name(authorName);
        book.setPrice(price);


        Call<Book> call;
        if (isEditMode) {
            call = apiService.updateBook(bookId, book);
        } else {
            call = apiService.saveBook(book);
        }

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Book> call, @NonNull Response<Book> response) {
                if (response.isSuccessful()) {
                    String message;
                    if (isEditMode)
                        message = "Book update successfully!";
                    else
                        message = "Book saved successfully!";
                    Toast.makeText(AddBookActivity.this, message, Toast.LENGTH_SHORT).show();
                    clearForm();
                    Intent intent = new Intent(AddBookActivity.this, BookListActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AddBookActivity.this, "Failed to save employee "
                            + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Book> call, @NonNull Throwable t) {
                Toast.makeText(AddBookActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void clearForm() {
        nameBox.setText("");
        authorNameBox.setText("");
        priceBox.setText("");

    }
}