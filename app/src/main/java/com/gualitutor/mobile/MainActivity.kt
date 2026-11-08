package com.gualitutor.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF14243A)
private val Blue = Color(0xFF2463A6)
private val Pale = Color(0xFFF3F6FA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GualiTutorApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GualiTutorApp() {
    var selectedLesson by remember { mutableStateOf("Introducción a Java") }
    var code by remember { mutableStateOf("public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Hola, Guali Tutor!\");\n    }\n}") }
    var console by remember { mutableStateOf("Consola lista. Escribe tu código y presiona Compilar.") }
    var compiled by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }
    var jdkPath by remember { mutableStateOf("/usr/lib/jvm/java-21-openjdk-amd64") }
    val lessons = listOf("Introducción a Java", "Variables y tipos de datos", "Condicionales", "Ciclos")

    MaterialTheme(colorScheme = lightColorScheme(primary = Blue, background = Pale)) {
        Scaffold(topBar = {
            TopAppBar(
                title = { Text("Guali Tutor", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Navy),
                actions = {
                    TextButton(onClick = { showHistory = !showHistory }) { Text("Historial", color = Color.White) }
                    TextButton(onClick = { showHelp = !showHelp }) { Text("Ayuda", color = Color.White) }
                }
            )
        }) { padding ->
            Column(
                modifier = Modifier.padding(padding).fillMaxSize().background(Pale)
                    .verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Lección activa", color = Blue, fontWeight = FontWeight.Bold)
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = selectedLesson, onValueChange = {}, readOnly = true,
                        label = { Text("Seleccionar lección") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        lessons.forEach { lesson ->
                            DropdownMenuItem(text = { Text(lesson) }, onClick = {
                                selectedLesson = lesson
                                expanded = false
                            })
                        }
                    }
                }
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(14.dp)) {
                        Text(selectedLesson, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text("En esta lección aprenderás conceptos fundamentales de Java mediante ejemplos y práctica guiada.")
                    }
                }
                Text("Editor de código Java", color = Blue, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = code, onValueChange = { code = it; compiled = false },
                    modifier = Modifier.fillMaxWidth().height(190.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                    label = { Text("Escribe tu código") }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = {
                        compiled = code.contains("class") && code.contains("main")
                        console = if (compiled) "Compilación de demostración: estructura básica detectada."
                        else "Error: revisa la declaración de clase y el método main."
                    }, modifier = Modifier.weight(1f)) { Text("Compilar") }
                    Button(
                        onClick = { console = if (compiled) "Ejecución de demostración:\nHola, Guali Tutor!"
                        else "Primero debes compilar correctamente." },
                        enabled = compiled, modifier = Modifier.weight(1f)
                    ) { Text("Ejecutar") }
                }
                Text("Consola", color = Blue, fontWeight = FontWeight.Bold)
                Card(colors = CardDefaults.cardColors(containerColor = Navy), shape = RoundedCornerShape(10.dp)) {
                    Text(console, color = Color(0xFFE5F1FF), modifier = Modifier.fillMaxWidth().padding(14.dp), fontSize = 13.sp)
                }
                Text("Configuración", color = Blue, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = jdkPath, onValueChange = { jdkPath = it },
                    label = { Text("Ruta del JDK") }, modifier = Modifier.fillMaxWidth())
                if (showHistory) {
                    Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(14.dp)) {
                        Text("Historial de actividad", fontWeight = FontWeight.Bold)
                        Text("• Sesión actual — $selectedLesson")
                        Text("• Última acción: ${if (compiled) "Compilación correcta" else "Edición de código"}")
                    } }
                }
                if (showHelp) {
                    Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(14.dp)) {
                        Text("Ayuda", fontWeight = FontWeight.Bold)
                        Text("1. Selecciona una lección. 2. Escribe código Java. 3. Presiona Compilar. 4. Si la compilación es correcta, presiona Ejecutar.")
                    } }
                }
                Text("Versión académica de interfaz móvil — Fase 3", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}
