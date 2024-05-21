package com.ucne.dadokotlin

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucne.dadokotlin.ui.theme.DadoKotlinTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DadoKotlinTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Favorite, contentDescription = null)
                                    Text(
                                        "  Casino Los Corruptos ", fontSize = 25.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                                    )
                                    Icon(Icons.Filled.LocationOn, contentDescription = null)
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Black,
                                titleContentColor = Color.Red
                            )
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    DiceApp()
                }
            }
        }
    }
}

@Composable
fun DiceApp() {
    var probabilidades by remember { mutableStateOf(DoubleArray(6) { 1.0 / 6 }) }
    var resultadoDado by remember { mutableStateOf<Int?>(null) }
    var showProbabilityDialog by remember { mutableStateOf(false) }
    var selectedNumber by remember { mutableStateOf("") }
    var newProbability by remember { mutableStateOf("") }
    var isSpinning by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(80.dp))
        Text(
            "Probabilidades actuales:",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        for (i in probabilidades.indices) {
            Text("Lado ${i + 1}: ${String.format("%.2f", probabilidades[i] * 100)}%")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showProbabilityDialog = true },
            shape = ShapeDefaults.Medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.LightGray
            )
        ) {
            Text("Cambiar Probabilidad")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (!isSpinning) {
                    coroutineScope.launch {
                        isSpinning = true
                        resultadoDado = rollDice(probabilidades)
                        delay(2000)
                        isSpinning = false
                    }
                }
            },
            shape = ShapeDefaults.Medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.Cyan
            )
            ) {
            Row{
                Text("Lanzar Dado", fontSize = 25.sp)
                Icon(
                    painter = painterResource(id = R.drawable.dado),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        DiceResult(resultadoDado, isSpinning)

        if (resultadoDado != null && !isSpinning) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                        append("El dado cayó en el número: ")
                    }
                    withStyle(style = SpanStyle(color = Color.Cyan)) {
                        append("#${resultadoDado}")
                    }
                }
            )
        }

        if (showProbabilityDialog) {
            ProbabilityDialog(
                probabilidades,
                onDismiss = { showProbabilityDialog = false },
                onSave = { numeroLado, porcentaje ->
                    val restante = 1 - porcentaje
                    for (i in probabilidades.indices) {
                        probabilidades[i] = if (i == numeroLado - 1) porcentaje else restante / 5
                    }
                    showProbabilityDialog = false
                },
                selectedNumber,
                onNumberChange = { selectedNumber = it },
                newProbability,
                onProbabilityChange = { newProbability = it }
            )
        }
    }
}

fun rollDice(probabilidades: DoubleArray): Int {
    val randomValue = Random.nextDouble()
    var acumulado = 0.0
    for (i in probabilidades.indices) {
        acumulado += probabilidades[i]
        if (randomValue < acumulado) {
            return i + 1
        }
    }
    return 1
}

@Composable
fun DiceResult(resultadoDado: Int?, isSpinning: Boolean) {
    val transition = rememberInfiniteTransition()
    val animatedValue by transition.animateFloat(
        initialValue = 1f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    val displayedNumber = if (isSpinning) animatedValue.toInt() else resultadoDado

    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayedNumber?.toString() ?: "",
            fontSize = 48.sp,
            color = Color.Black
        )
    }
}

@Composable
fun ProbabilityDialog(
    probabilidades: DoubleArray,
    onDismiss: () -> Unit,
    onSave: (Int, Double) -> Unit,
    selectedNumber: String,
    onNumberChange: (String) -> Unit,
    newProbability: String,
    onProbabilityChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Cambiar Probabilidades") },
        text = {
            Column {
                Text("Número del dado (1-6):")
                OutlinedTextField(
                    value = selectedNumber,
                    onValueChange = onNumberChange,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Nuevo porcentaje:")
                OutlinedTextField(
                    value = newProbability,
                    onValueChange = onProbabilityChange,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val numeroLado = selectedNumber.toIntOrNull()
                val porcentaje = newProbability.toDoubleOrNull()?.div(100.0)
                if (numeroLado != null && numeroLado in 1..6 && porcentaje != null && porcentaje in 0.0..1.0) {
                    onSave(numeroLado, porcentaje)
                }
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,
                    contentColor = Color.Black
                )
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.DarkGray,
                    contentColor = Color.LightGray
                )
            ) {
                Text("Cancelar")
            }
        }
    )
}
