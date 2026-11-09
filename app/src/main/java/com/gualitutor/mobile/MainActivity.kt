package com.gualitutor.mobile

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray

private val Navy = Color(0xFF14243A)
private val Blue = Color(0xFF2463A6)
private val Pale = Color(0xFFF3F6FA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GualiTutorApp(applicationContext) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GualiTutorApp(context: Context) {
    val lessons = listOf(
        "Introducción a Java",
        "Variables y tipos de datos",
        "Condicionales",
        "Ciclos"
    )

    var selectedLesson by remember { mutableStateOf(lessons.first()) }
    var lessonOpen by remember { mutableStateOf(true) }
    var code by remember {
        mutableStateOf(
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        System.out.println(\"Hola, Guali Tutor!\");\n" +
            "    }\n}"
        )
    }
    var console by remember { mutableStateOf("Consola lista. Escribe tu código y presiona Compilar.") }
    var compiled by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    var showLessons by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }
    var jdkPath by remember { mutableStateOf("/usr/lib/jvm/java-21-openjdk-amd64") }
    var history by remember { mutableStateOf(loadHistory(context)) }

    fun record(action: String) {
        val updated = listOf("$action — $selectedLesson") + history
        history = updated.take(30)
        saveHistory(context, history)
    }

    MaterialTheme(colorScheme = lightColorScheme(primary = Blue, background = Pale)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Guali Tutor", color = Color.White, fontWeight = FontWeight.Bold)
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Navy),
                    actions = {
                        Box {
                            TextButton(onClick = { menuExpanded = true }) {
                                Text("Menú ☰", color = Color.White)
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Lecciones") },
                                    onClick = { menuExpanded = false; showLessons = true }
                                )
                                DropdownMenuItem(
                                    text = { Text("Configuración") },
                                    onClick = { menuExpanded = false; showSettings = true }
                                )
                                DropdownMenuItem(
                                    text = { Text("Historial") },
                                    onClick = { menuExpanded = false; showHistory = true }
                                )
                                DropdownMenuItem(
                                    text = { Text("Ayuda") },
                                    onClick = { menuExpanded = false; showHelp = true }
                                )
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Pale)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Lección activa", color = Blue, fontWeight = FontWeight.Bold)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { showLessons = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(selectedLesson)
                    }
                    OutlinedButton(onClick = { lessonOpen = !lessonOpen }) {
                        Text(if (lessonOpen) "Cerrar" else "Abrir")
                    }
                }

                if (lessonOpen) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(Modifier.padding(14.dp)) {
                            Text(
                                selectedLesson,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                lessonDescription(selectedLesson)
                            )
                        }
                    }
                } else {
                    Text("La lección está cerrada. Presiona Abrir para consultar su contenido.")
                }

                Text("Editor de código Java", color = Blue, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = code,
                    onValueChange = {
                        code = it
                        compiled = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp),
                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                    label = { Text("Escribe tu código Java") }
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            compiled = code.contains("class") &&
                                code.contains("main") &&
                                code.contains("{") &&
                                code.contains("}")
                            console = if (compiled) {
                                "Validación de demostración correcta. La estructura básica fue detectada."
                            } else {
                                "Error de demostración: revisa la clase, el método main y las llaves."
                            }
                            record(if (compiled) "Validación de código correcta" else "Validación de código con error")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Compilar")
                    }

                    Button(
                        onClick = {
                            console = "Salida de demostración:\nHola, Guali Tutor!"
                            record("Ejecución de demostración")
                        },
                        enabled = compiled,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ejecutar")
                    }
                }

                Text("Consola", color = Blue, fontWeight = FontWeight.Bold)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Navy),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        console,
                        color = Color(0xFFE5F1FF),
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        fontSize = 13.sp
                    )
                }

                Text(
                    "Usa el menú para abrir Lecciones, Configuración, Historial y Ayuda.",
                    color = Color.DarkGray,
                    fontSize = 12.sp
                )
                Text(
                    "Versión académica de interfaz móvil — Fase 3",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }

        if (showLessons) {
            AlertDialog(
                onDismissRequest = { showLessons = false },
                title = { Text("Lecciones disponibles") },
                text = {
                    Column {
                        lessons.forEach { lesson ->
                            TextButton(
                                onClick = {
                                    selectedLesson = lesson
                                    lessonOpen = true
                                    compiled = false
                                    console = "Lección abierta: $lesson"
                                    record("Apertura de lección")
                                    showLessons = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(lesson)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLessons = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }

        if (showSettings) {
            AlertDialog(
                onDismissRequest = { showSettings = false },
                title = { Text("Configuración") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Ruta de referencia del JDK")
                        OutlinedTextField(
                            value = jdkPath,
                            onValueChange = { jdkPath = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Ruta del JDK") }
                        )
                        Text(
                            "Nota: este campo guarda una ruta de referencia. Android no puede cambiar el JDK del equipo desde la aplicación.",
                            fontSize = 12.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        record("Consulta de configuración")
                        showSettings = false
                    }) { Text("Guardar") }
                },
                dismissButton = {
                    TextButton(onClick = { showSettings = false }) { Text("Cancelar") }
                }
            )
        }

        if (showHistory) {
            AlertDialog(
                onDismissRequest = { showHistory = false },
                title = { Text("Historial de actividad") },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (history.isEmpty()) {
                            Text("Todavía no hay actividades registradas.")
                        } else {
                            history.forEach { item ->
                                Text("• $item", modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showHistory = false }) { Text("Cerrar") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        history = emptyList()
                        saveHistory(context, history)
                    }) { Text("Limpiar") }
                }
            )
        }

        if (showHelp) {
            AlertDialog(
                onDismissRequest = { showHelp = false },
                title = { Text("Ayuda de Guali Tutor") },
                text = {
                    Text(
                        "1. Abre el menú Lecciones y selecciona una lección.\n" +
                        "2. Consulta el contenido.\n" +
                        "3. Escribe código Java en el editor.\n" +
                        "4. Presiona Compilar para validar la estructura de demostración.\n" +
                        "5. Si la validación es correcta, presiona Ejecutar para mostrar la salida de ejemplo.\n" +
                        "6. Consulta tus actividades en Historial."
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showHelp = false }) { Text("Entendido") }
                }
            )
        }
    }
}

private fun lessonDescription(lesson: String): String = when (lesson) {
    "Variables y tipos de datos" ->
        "Aprende a declarar variables y utilizar tipos de datos básicos en Java."
    "Condicionales" ->
        "Aprende a tomar decisiones en un programa utilizando estructuras condicionales."
    "Ciclos" ->
        "Aprende a repetir instrucciones mediante estructuras de control."
    else ->
        "Conoce los fundamentos de Java y la estructura básica de un programa."
}

private fun loadHistory(context: Context): List<String> {
    val raw = context.getSharedPreferences("guali_tutor", Context.MODE_PRIVATE)
        .getString("history", "[]") ?: "[]"
    return try {
        val json = JSONArray(raw)
        List(json.length()) { index -> json.getString(index) }
    } catch (_: Exception) {
        emptyList()
    }
}

private fun saveHistory(context: Context, items: List<String>) {
    val json = JSONArray()
    items.forEach { json.put(it) }
    context.getSharedPreferences("guali_tutor", Context.MODE_PRIVATE)
        .edit()
        .putString("history", json.toString())
        .apply()
}
