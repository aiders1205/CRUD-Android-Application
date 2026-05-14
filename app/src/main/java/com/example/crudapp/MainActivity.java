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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private List<Item> itemList;
    private ItemAdapter itemAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Item");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_item, null);
        final EditText nameEditText = view.findViewById(R.id.editTextName);
        final EditText descriptionEditText = view.findViewById(R.id.editTextDescription);
        builder.setView(view);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description)) {
                    Toast.makeText(MainActivity.this, "Name and Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                db.addItem(new Item(name, description));
                loadItems();
                Toast.makeText(MainActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void showEditItemDialog(final Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Item");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_item, null);
        final EditText nameEditText = view.findViewById(R.id.editTextName);
        final EditText descriptionEditText = view.findViewById(R.id.editTextDescription);

        nameEditText.setText(item.getName());
        descriptionEditText.setText(item.getDescription());
        builder.setView(view);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description)) {
                    Toast.makeText(MainActivity.this, "Name and Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                item.setName(name);
                item.setDescription(description);
                db.updateItem(item);
                loadItems();
                Toast.makeText(MainActivity.this, "Item updated successfully", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void showDeleteItemDialog(final Item item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteItem(item);
                        loadItems();
                        Toast.makeText(MainActivity.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

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
    }
}
