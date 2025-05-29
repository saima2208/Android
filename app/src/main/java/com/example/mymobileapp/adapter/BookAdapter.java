package com.example.mymobileapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymobileapp.R;
import com.example.mymobileapp.activity.AddBookActivity;
import com.example.mymobileapp.model.Book;
import com.example.mymobileapp.service.ApiService;
import com.example.mymobileapp.util.ApiClient;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    public Context context;
    private List<Book> bookList;

    private ApiService apiService;

    public BookAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
        this.apiService = ApiClient.getApiService();
    }

    @NonNull
    @Override
    public BookAdapter.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);

        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapter.BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.idText.setText(String.valueOf(book.getId()));
        holder.nameText.setText(book.getName());
        holder.authorText.setText(book.getAuthor_name());
        String priceText = String.valueOf(book.getPrice()); // If price is numeric
        holder.priceText.setText(priceText);


        holder.updateButton.setOnClickListener(v -> {
            Log.d("Update", "Update clicked for " + book.getName());
            Intent intent = new Intent(context, AddBookActivity.class);
            intent.putExtra("book", new Gson().toJson(book));
            context.startActivity(intent);
        });

        holder.deleteButton.setOnClickListener(v -> {
            Log.d("Delete", "Delete clicked for " + book.getName());
            new AlertDialog.Builder(context)
                    .setTitle("Delete")
                    .setMessage("Are you sure want to delete" + book.getName() + "?")
                    .setPositiveButton("Yes",
                            (DialogInterface dialog, int which) -> apiService.deleteBook(book.getId())
                                    .enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, @NonNull Response<Void> response) {
                                            if (response.isSuccessful()) {
                                                int adapterPosition = holder.getAdapterPosition();
                                                if (adapterPosition != RecyclerView.NO_POSITION) {
                                                    bookList.remove(adapterPosition);
                                                    notifyItemRemoved(adapterPosition);
                                                    notifyItemRangeChanged(adapterPosition, bookList.size());


                                                    Toast.makeText(context, "Delete successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                            Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                    }))
                    .setNegativeButton("Cancel", null)
                    .show();
        });


    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView idText,nameText, authorText, priceText;
        Button updateButton, deleteButton;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            idText = itemView.findViewById(R.id.idText);
            nameText = itemView.findViewById(R.id.nameText);
            authorText = itemView.findViewById(R.id.authorText);
            priceText = itemView.findViewById(R.id.priceText);
            updateButton = itemView.findViewById(R.id.edit);
            deleteButton = itemView.findViewById(R.id.delete);
        }
    }
}
