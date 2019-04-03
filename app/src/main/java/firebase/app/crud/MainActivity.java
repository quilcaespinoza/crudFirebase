package firebase.app.crud;

import android.content.Intent;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import firebase.app.crud.Model.Cliente;

public class MainActivity extends AppCompatActivity {

    private List<Cliente> clienteList = new ArrayList<Cliente>();
    ArrayAdapter<Cliente> clienteArrayAdapter;

    EditText nombre, direccion, telefono;
    ListView datospersonas;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Cliente cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nombre = findViewById(R.id.txtnombre);
        direccion = findViewById(R.id.txtdireccion);
        telefono = findViewById(R.id.txttelefono);

        datospersonas = findViewById(R.id.lv_datospersonas);

        iniciarconexionfirebase();
        listardatos();

        datospersonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cliente = (Cliente) parent.getItemAtPosition(position);
                nombre.setText(cliente.getNombre());
                direccion.setText(cliente.getDireccion());
                telefono.setText(cliente.getTelefono());
            }
        });
    }

    private void listardatos() {
        databaseReference.child("Cliente").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clienteList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Cliente c = snapshot.getValue(Cliente.class);
                    clienteList.add(c);

                    clienteArrayAdapter = new ArrayAdapter<Cliente>(MainActivity.this, android.R.layout.simple_list_item_1, clienteList);
                    datospersonas.setAdapter(clienteArrayAdapter);
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
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_datos, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String nom = nombre.getText().toString();
        String dir = direccion.getText().toString();
        String tel = telefono.getText().toString();
        switch (item.getItemId()){
            case R.id.icon_add: {
                if (nom.equals("")||dir.equals("")||tel.equals("")){
                    validacion();
                }else {
                    Cliente p = new Cliente();
                    p.setId(UUID.randomUUID().toString());
                    p.setNombre(nom);
                    p.setDireccion(dir);
                    p.setTelefono(tel);
                    databaseReference.child("Cliente").child(p.getId()).setValue(p);
                    Toast.makeText(this, "Agregado con Exito", Toast.LENGTH_LONG).show();
                    limpiar();
                }
                break;
            }
            case R.id.icon_guardar: {
                Cliente C = new Cliente();
                C.setId(cliente.getId());
                C.setNombre(nombre.getText().toString().trim());
                C.setDireccion(direccion.getText().toString().trim());
                C.setTelefono(telefono.getText().toString().trim());
                databaseReference.child("Cliente").child(C.getId()).setValue(C);
                Toast.makeText(this, "Actualizado con Exito", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.icon_eliminar: {
                Cliente c = new Cliente();
                c.setId(cliente.getId());
                databaseReference.child("Cliente").child(c.getId()).removeValue();
                Toast.makeText(this, "Eliminado con Exito", Toast.LENGTH_LONG).show();
                limpiar();
                break;
            }
            case R.id.icon_articulo: {
                Intent intent = new Intent(MainActivity.this, Articulo.class);
                startActivity(intent);
                break;
            }
            case R.id.icon_compra: {
                Intent intent = new Intent(MainActivity.this, Compra.class);
                startActivity(intent);
                break;
            }

            default:break;
        }
        return true;
    }

    private void limpiar() {
        nombre.setText("");
        direccion.setText("");
        telefono.setText("");
    }

    private void validacion() {
        String nom = nombre.getText().toString();
        String dir = direccion.getText().toString();
        String tel = telefono.getText().toString();
        if (nom.equals("")){
            nombre.setError("required");
        }else if (dir.equals("")){
            direccion.setError("required");
        }else if (tel.equals("")){
            telefono.setError("required");
        }
    }
}
