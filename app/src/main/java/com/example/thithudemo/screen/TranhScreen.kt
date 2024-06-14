package com.example.thithudemo.screen

import android.provider.CalendarContract.Colors
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.room.Room
import coil.compose.rememberAsyncImagePainter
import com.example.thithudemo.model.TranhModel
import com.example.thithudemo.roomdb.TranhDB

@Composable
fun TranhScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val db = Room.databaseBuilder(
        context,
        TranhDB::class.java, "tranh-db"
    ).allowMainThreadQueries().build()

    var listTranhs by remember { mutableStateOf(db.tranhDAO().getAll()) }
    var editingTranh by remember { mutableStateOf<TranhModel?>(null) }
    var showingAddTranhDialog by remember { mutableStateOf(false) }
    var showingTranhDetail by remember { mutableStateOf<TranhModel?>(null) }


    /// Gia dien
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = "Quản lý Tranh",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
        Button(
            onClick = { showingAddTranhDialog = true },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Thêm Tranh")
        }

        //// Cac truomg du lieu hien thi o day
        LazyVerticalGrid(columns = GridCells.Fixed(3),
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp) ) {
            items(listTranhs) { tranh ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp).background(color = Color.Gray)
                        .clickable { showingTranhDetail = tranh }) {
                    tranh.photoPath?.let { photoPath ->
                        if (photoPath.isNotEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(photoPath),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp) // Adjust size as needed
                                    .padding(end = 8.dp).clip(RoundedCornerShape(20.dp)) // Bo tròn góc của hình ảnh// Add some padding
                            )
                        }

                    }
                    Text(
                        text = "ID: ${tranh.uid}",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Tên: ${tranh.nameTranh}",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
//                    Text(modifier = Modifier.weight(1f), text = tranh.price.toString())
//                    Text(modifier = Modifier.weight(1f), text = tranh.statusTranh.toString())
                    Button(onClick = { editingTranh = tranh }) {
                        Text(text = "Sửa")
                    }
                    Button(onClick = {
                        db.tranhDAO().delete(tranh)
                        listTranhs = db.tranhDAO().getAll()
                    }) {
                        Text(text = "Xóa")
                    }
                }
                Divider()
            }


            ///closer lazzy

        }
        // khai bao 3 nut action
        if (showingAddTranhDialog) {
            AddTranhDialog(
                onDismiss = { showingAddTranhDialog = false },
                onSave = { newTranh ->
                    db.tranhDAO().insert(newTranh)
                    listTranhs = db.tranhDAO().getAll()
                    showingAddTranhDialog = false
                }
            )
        }

        editingTranh?.let { tranh ->
            EditTranhDialog(
                tranh = tranh,
                onDismiss = { editingTranh = null },
                onSave = { updatedTranh ->
                    db.tranhDAO().update(updatedTranh)
                    listTranhs = db.tranhDAO().getAll()
                    editingTranh = null
                }
            )
        }

        showingTranhDetail?.let { tranh ->
            TranhDetailScreen(
                tranh = tranh,
                onDismiss = { showingTranhDetail = null }
            )
        }

    }
}

@Composable
fun EditTranhDialog(tranh: TranhModel, onDismiss: () -> Unit, onSave: (TranhModel)->Unit) {
    var nameTranh by remember { mutableStateOf(tranh.nameTranh ?: "") }
    var price by remember { mutableStateOf(tranh.price?.toString() ?: "0") }
    var statusTranh by remember { mutableStateOf(tranh.statusTranh ?: false) }
    var photoPath by remember { mutableStateOf(tranh.photoPath ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Sửa Sinh viên") },
        text = {
            Column {
                TextField(
                    value = nameTranh,
                    onValueChange = { nameTranh = it },
                    label = { Text("Họ tên") }
                )

                TextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Điểm TB") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = statusTranh,
                        onCheckedChange = { statusTranh = it }
                    )
                    Text(text = "Đã ra trường")
                }
                TextField(
                    value = photoPath,
                    onValueChange = { photoPath = it },
                    label = { Text("Đường dẫn ảnh") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val updatedStudent = tranh.copy(
                    nameTranh = nameTranh,
                    price = price.toFloatOrNull() ?: 0f,
                    statusTranh = statusTranh,
                    photoPath = photoPath
                )
                onSave(updatedStudent)
            }) {
                Text("Lưu")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}


@Composable
fun AddTranhDialog(onDismiss: () -> Unit, onSave: (TranhModel) -> Unit) {
    var nameTranh by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var statusTranh by remember { mutableStateOf(false) }
    var photoPath by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf("") }
    var priceError by remember { mutableStateOf("") }
    var photoPathError by remember { mutableStateOf("") }

    // Validation function
    fun validateInputs(): Boolean {
        var isValid = true

        if (nameTranh.isBlank()) {
            nameError = "Tên tranh không được để trống"
            isValid = false
        } else {
            nameError = ""
        }

        if (price.isBlank()) {
            priceError = "Giá không được để trống"
            isValid = false
        } else if (price.toFloatOrNull() == null) {
            priceError = "Giá phải là một số"
            isValid = false
        } else {
            priceError = ""
        }

        if (photoPath.isBlank()) {
            photoPathError = "Đường dẫn ảnh không được để trống"
            isValid = false
        } else {
            photoPathError = ""
        }

        return isValid
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Thêm Tranh") },
        text = {
            Column {
                TextField(
                    value = nameTranh,
                    onValueChange = { nameTranh = it },
                    label = { Text("Tên tranh") },
                    isError = nameError.isNotEmpty()
                )
                if (nameError.isNotEmpty()) {
                    Text(
                        text = nameError,
                        color = Color.Red,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                TextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Giá") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = priceError.isNotEmpty()
                )
                if (priceError.isNotEmpty()) {
                    Text(
                        text = priceError,
                        color = Color.Red,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = statusTranh,
                        onCheckedChange = { statusTranh = it }
                    )
                    Text(text = "Trạng thái")
                }

                TextField(
                    value = photoPath,
                    onValueChange = { photoPath = it },
                    label = { Text("Đường dẫn ảnh") },
                    isError = photoPathError.isNotEmpty()
                )
                if (photoPathError.isNotEmpty()) {
                    Text(
                        text = photoPathError,
                        color = Color.Red,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (validateInputs()) {
                    val newTranh = TranhModel(
                        nameTranh = nameTranh,
                        price = price.toFloatOrNull() ?: 0f,
                        statusTranh = statusTranh,
                        photoPath = photoPath
                    )
                    onSave(newTranh)
                }
            }) {
                Text("Lưu")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@Composable
fun TranhDetailScreen(tranh: TranhModel, onDismiss: () -> Unit) {
// Trien khai alert
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "chi tiet tranh") },
        text = {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Họ tên: ${tranh.nameTranh}")
                Text(text = "Điểm TB: ${tranh.price}")
                Text(text = "Đã ra trường: ${if (tranh.statusTranh == true) "Có" else "Không"}")
                tranh.photoPath?.let {
                    if (it.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(it),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}


