package com.example.crudapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String CANCEL_LABEL = "Cancel";
    private static final String EMPTY_FIELDS_ERROR = "Title and Description cannot be empty";
    private DatabaseHelper db;
    private List<Item> itemList;
    private ItemAdapter itemAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private LinearLayout emptyStateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DatabaseHelper(this);
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(itemAdapter);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddItemDialog();
            }
        });

        emptyStateView = findViewById(R.id.emptyStateView);

        itemAdapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item item) {
                showEditItemDialog(item);
            }

            @Override
            public void onItemLongClick(Item item) {
                showDeleteItemDialog(item);
            }
        });

        loadItems();
    }

    private void loadItems() {
        itemList.clear();
        itemList.addAll(db.getAllItems());
        itemAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Note");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_item, null);
        final EditText nameEditText = view.findViewById(R.id.editTextName);
        final EditText descriptionEditText = view.findViewById(R.id.editTextDescription);
        final EditText categoryEditText = view.findViewById(R.id.editTextCategory);
        builder.setView(view);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                String category = categoryEditText.getText().toString().trim();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description)) {
                    Toast.makeText(MainActivity.this, EMPTY_FIELDS_ERROR, Toast.LENGTH_SHORT).show();
                    return;
                }

                db.addItem(new Item(name, description, category));
                loadItems();
                Toast.makeText(MainActivity.this, "Note added successfully", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(CANCEL_LABEL, null);
        builder.create().show();
    }

    private void showEditItemDialog(final Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Note");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_item, null);
        final EditText nameEditText = view.findViewById(R.id.editTextName);
        final EditText descriptionEditText = view.findViewById(R.id.editTextDescription);
        final EditText categoryEditText = view.findViewById(R.id.editTextCategory);

        nameEditText.setText(item.getName());
        descriptionEditText.setText(item.getDescription());
        categoryEditText.setText(item.getCategory());
        builder.setView(view);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                String category = categoryEditText.getText().toString().trim();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description)) {
                    Toast.makeText(MainActivity.this, EMPTY_FIELDS_ERROR, Toast.LENGTH_SHORT).show();
                    return;
                }

                item.setName(name);
                item.setDescription(description);
                item.setCategory(category);
                db.updateItem(item);
                loadItems();
                Toast.makeText(MainActivity.this, "Note updated successfully", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(CANCEL_LABEL, null);
        builder.create().show();
    }

    private void showDeleteItemDialog(final Item item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteItem(item);
                        loadItems();
                        Toast.makeText(MainActivity.this, "Note deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(CANCEL_LABEL, null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search notes...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchItems(newText);
                return true;
            }
        });
        return true;
    }

    private void searchItems(String query) {
        itemList.clear();
        if (query.isEmpty()) {
            itemList.addAll(db.getAllItems());
        } else {
            itemList.addAll(db.searchItems(query));
        }
        itemAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (itemList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateView.setVisibility(View.GONE);
        }
    }
}
