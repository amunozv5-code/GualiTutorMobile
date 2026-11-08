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
