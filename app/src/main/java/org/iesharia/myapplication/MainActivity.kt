package org.iesharia.myapplication

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.iesharia.myapplication.ui.theme.MyApplicationTheme



class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                ) { innerPadding ->
                    MainActivity (
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@SuppressLint("Range")
@Composable
fun MainActivity(modifier: Modifier) {
    val context = LocalContext.current
    val db = DBHelper(context)

    var nameValue by remember { mutableStateOf("") }
    var ageValue by remember { mutableStateOf("") }
    var selectedName by remember { mutableStateOf("") }
    var selectedAge by remember { mutableStateOf("") }
    var items by remember { mutableStateOf(emptyList<Triple<Int, String, String>>()) }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Base de Datos",
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )
        Text(
            text = "Muuuuuy simple\nNombre/Edad",
            fontSize = 10.sp
        )

        // Campo para Nombre
        OutlinedTextField(
            value = nameValue,
            onValueChange = { nameValue = it },
            modifier = Modifier,
            textStyle = TextStyle(color = Color.DarkGray),
            label = { Text(text = "Nombre") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        // Campo para Edad
        OutlinedTextField(
            value = ageValue,
            onValueChange = { ageValue = it },
            modifier = Modifier,
            textStyle = TextStyle(color = Color.DarkGray),
            label = { Text(text = "Edad") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        val bModifier = Modifier.padding(20.dp)

        Row {
            Button(
                modifier = bModifier,
                onClick = {
                    db.addName(nameValue, ageValue)
                    Toast.makeText(
                        context,
                        "$nameValue adjuntado a la base de datos",
                        Toast.LENGTH_LONG
                    ).show()

                    nameValue = ""
                    ageValue = ""
                }
            ) {
                Text(text = "Añadir")
            }

            Button(
                modifier = bModifier,
                onClick = {
                    Log.d("MainActivity", "Mostrando registros desde la base de datos")
                    val cursor = db.getName()
                    val tempItems = mutableListOf<Triple<Int, String, String>>()
                    cursor?.let {
                        if (cursor.moveToFirst()) {
                            do {
                                val id = cursor.getInt(cursor.getColumnIndex(DBHelper.ID_COL))
                                val name = cursor.getString(cursor.getColumnIndex(DBHelper.NAME_COl))
                                val age = cursor.getString(cursor.getColumnIndex(DBHelper.AGE_COL))
                                tempItems.add(Triple(id, name, age))
                            } while (cursor.moveToNext())
                        }
                        cursor.close()
                    }
                    items = tempItems
                }
            ) {
                Text(text = "Mostrar")
            }
        }

        // Encabezado de la lista
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "ID", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "Nombre", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "Edad", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        // Lista de nombres y edades con selección
        LazyColumn(modifier = Modifier.padding(vertical = 8.dp)) {
            items(items) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = item.first.toString(), fontSize = 16.sp) // ID
                    Text(text = item.second, fontSize = 16.sp)           // Nombre
                    Text(text = item.third, fontSize = 16.sp)            // Edad

                    // Botón de eliminar
                    Button(
                        onClick = {
                            Log.d("MainActivity", "Eliminando registro")
                            db.onDelete(item.first)
                            Toast.makeText(context, "Registro eliminado", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text(text = "Eliminar")
                    }
                    // Botón de actualizar
                    Button(
                        onClick = {
                            Log.d("MainActivity", "Actualizando registro")
                            db.onUpdate(item.first)
                            Toast.makeText(context, "Registro actualizado", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text(text = "Actualizar")
                    }
                }
            }
        }
        // Mostrar selección
        if (selectedName.isNotEmpty() && selectedAge.isNotEmpty()) {
            Text(
                text = "Seleccionado: $selectedName, $selectedAge",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
