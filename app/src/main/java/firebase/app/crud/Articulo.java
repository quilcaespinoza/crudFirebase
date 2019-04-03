package firebase.app.crud;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import firebase.app.crud.Model.Articulos;

public class Articulo extends AppCompatActivity {

    private List<Articulos> articuloList  = new ArrayList<Articulos>();
    ArrayAdapter<Articulos> articuloArrayAdapter;



    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Articulos articulos;

    EditText descripcion, precio, cantidad;
    ListView listarticulos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articulo);

        descripcion = findViewById(R.id.txtfecha);
        precio = findViewById(R.id.txtprecioAr);
        cantidad = findViewById(R.id.txtcantidadAr);

        listarticulos = findViewById(R.id.lv_datosarticulos);

        iniciarconexionfirebase();
        mostrararticulos();

        listarticulos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                articulos = (Articulos) parent.getItemAtPosition(position);
                descripcion.setText(articulos.getDescripcion());
                precio.setText(articulos.getPrecio());
                cantidad.setText(articulos.getCantidad());
            }
        });

    }

    private void mostrararticulos() {
        databaseReference.child("Articulo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                articuloList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren() ){
                    Articulos a = snapshot.getValue(Articulos.class);
                    articuloList.add(a);

                    articuloArrayAdapter = new ArrayAdapter<Articulos>(Articulo.this, android.R.layout.simple_list_item_1,articuloList);
                    listarticulos.setAdapter(articuloArrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void iniciarconexionfirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_articulo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String desc = descripcion.getText().toString();
        String pre = precio.getText().toString();
        String can = cantidad.getText().toString();
        switch (item.getItemId()){
            case R.id.icon_add: {
                if (desc.equals("")||pre.equals("")||can.equals("")){
                    validaciones();
                }else {
                    Articulos a = new Articulos();
                    a.setId(UUID.randomUUID().toString());
                    a.setDescripcion(desc);
                    a.setPrecio(pre);
                    a.setCantidad(can);
                    databaseReference.child("Articulo").child(a.getId()).setValue(a);
                    Toast.makeText(this, "Agregado con Exito", Toast.LENGTH_LONG).show();
                    limpiar();
                }
                break;
            }
            case R.id.icon_guardar: {
                Articulos ar = new Articulos();
                ar.setId(articulos.getId());
                ar.setDescripcion(descripcion.getText().toString().trim());
                ar.setPrecio(precio.getText().toString().trim());
                ar.setCantidad(cantidad.getText().toString().trim());
                databaseReference.child("Articulo").child(ar.getId()).setValue(ar);
                Toast.makeText(this, "Actualizado con Exito", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.icon_eliminar: {
                Articulos ar = new Articulos();
                ar.setId(articulos.getId());
                databaseReference.child("Articulo").child(ar.getId()).removeValue();
                Toast.makeText(this, "Eliminado con Exito", Toast.LENGTH_LONG).show();
                limpiar();
                break;
            }
            default:break;
        }
        return true;
    }

    private void limpiar() {
        descripcion.setText("");
        precio.setText("");
        cantidad.setText("");
    }

    private void validaciones() {
        String desc = descripcion.getText().toString();
        String pre = precio.getText().toString();
        String can = cantidad.getText().toString();
        if (desc.equals("")){
            descripcion.setError("required");
        }else if (pre.equals("")){
            precio.setError("required");
        }else if (can.equals("")){
            cantidad.setError("required");
        }
    }
}
