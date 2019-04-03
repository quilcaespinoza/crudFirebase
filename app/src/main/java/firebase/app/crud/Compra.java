package firebase.app.crud;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import firebase.app.crud.Model.Articulos;
import firebase.app.crud.Model.Cliente;
import firebase.app.crud.Model.Compras;

public class Compra extends AppCompatActivity implements View.OnClickListener {

    EditText fecha, caUni;
    Spinner nomclie, descarticulo;
    ImageButton btncalendario;
    ListView listcompra;
    Compras compras;

    private List<Cliente> clienteList = new ArrayList<Cliente>();
    ArrayAdapter<Cliente> clienteArrayAdapter;

    private List<Articulos> articuloList  = new ArrayList<Articulos>();
    ArrayAdapter<Articulos> articuloArrayAdapter;

    private List<Compras> comprasList  = new ArrayList<Compras>();
    ArrayAdapter<Compras> comprasArrayAdapter;

    private static final String CERO = "0";
    private static final String BARRA = "/";
    public final Calendar c = Calendar.getInstance();

    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int year = c.get(Calendar.YEAR);

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compra);

        fecha = findViewById(R.id.txtfecha);
        caUni = findViewById(R.id.txtunidad);
        nomclie = findViewById(R.id.spncliente);
        descarticulo = findViewById(R.id.spncompra);
        btncalendario = findViewById(R.id.btnfecha);
        btncalendario.setOnClickListener(this);

        listcompra = findViewById(R.id.lv_datoscompra);

        iniciarconexionfirebase();
        listarspinnercliente();
        listarspinnerarticulo();

        mostrarcompras();

        listcompra.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                compras = (Compras) parent.getItemAtPosition(position);
                fecha.setText(compras.getFecha());
                caUni.setText(compras.getCantidad());
                nomclie.getSelectedItem();
                descarticulo.getSelectedItem();

                String nomcli = nomclie.getSelectedItem().toString();
            }
        });
    }

    private void mostrarcompras() {
        databaseReference.child("Compras").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comprasList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Compras c = snapshot.getValue(Compras.class);
                    comprasList.add(c);

                    comprasArrayAdapter = new ArrayAdapter<Compras>(Compra.this, android.R.layout.simple_list_item_1, comprasList);
                    listcompra.setAdapter(comprasArrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void listarspinnerarticulo() {
        databaseReference.child("Articulo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                articuloList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren() ){
                    Articulos a = snapshot.getValue(Articulos.class);
                    articuloList.add(a);

                    articuloArrayAdapter = new ArrayAdapter<Articulos>(Compra.this, android.R.layout.simple_spinner_item,articuloList);
                    descarticulo.setAdapter(articuloArrayAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void listarspinnercliente() {
        databaseReference.child("Cliente").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clienteList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Cliente c = snapshot.getValue(Cliente.class);
                    clienteList.add(c);
                    clienteArrayAdapter= new ArrayAdapter<Cliente>(Compra.this,  android.R.layout.simple_spinner_item, clienteList);
                    nomclie.setAdapter(clienteArrayAdapter);
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
        getMenuInflater().inflate(R.menu.menu_compras, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String data = fecha.getText().toString();
        String uni = caUni.getText().toString();
        String nomcli = nomclie.getSelectedItem().toString();
        String descart = descarticulo.getSelectedItem().toString();

        switch (item.getItemId()){
            case R.id.icon_add: {
                if (data.equals("")||uni.equals("")){
                    validaciones();
                }else {
                    Compras c = new Compras();
                    c.setId(UUID.randomUUID().toString());
                    c.setFecha(data);
                    c.setCantidad(uni);
                    c.setNomclie(nomcli);
                    c.setDescompra(descart);
                    databaseReference.child("Compras").child(c.getId()).setValue(c);
                    Toast.makeText(this, "Agregado con Exito", Toast.LENGTH_LONG).show();
                    limpiar();

                }
                break;
            }
            case R.id.icon_guardar:{
                Compras cs = new Compras();
                cs.setId(compras.getId());
                cs.setFecha(fecha.getText().toString().trim());
                cs.setCantidad(caUni.getText().toString().trim());
                cs.setNomclie(nomclie.getSelectedItem().toString().trim());
                cs.setDescompra(descarticulo.getSelectedItem().toString().trim());
                databaseReference.child("Compras").child(cs.getId()).setValue(cs);
                Toast.makeText(this, "Actualizado con Exito", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.icon_eliminar:{
                Compras cr = new Compras();
                cr.setId(compras.getId());
                databaseReference.child("Compras").child(cr.getId()).removeValue();
                Toast.makeText(this, "Eliminado con Exito", Toast.LENGTH_LONG).show();
                limpiar();
                break;
            }
        }
        return true;
    }

    private void limpiar() {
        fecha.setText("");
        caUni.setText("");
    }

    private void validaciones() {
        String data = fecha.getText().toString();
        String uni = caUni.getText().toString();
        if (data.equals("")){
            fecha.setError("required");
        }else if (uni.equals("")){
            caUni.setError("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnfecha:
                obtenerFecha();
                break;
        }
    }

    private void obtenerFecha() {
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                final int mesActual = month + 1;

                String diaFormateado = (dayOfMonth < 10) ? CERO + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);

                String mesFormateado = (mesActual < 10) ? CERO + String.valueOf(mesActual) : String.valueOf(mesActual);
                fecha.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);


            }

        }, year, mes, dia);
        recogerFecha.show();

    }
}
