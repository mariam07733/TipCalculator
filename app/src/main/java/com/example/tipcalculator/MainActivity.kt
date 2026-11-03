package com.example.tipcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes // AJOUT
import androidx.annotation.StringRes // AJOUT
import androidx.annotation.VisibleForTesting // AJOUT
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row // AJOUT
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size // AJOUT
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth // AJOUT
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon // AJOUT
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch // AJOUT
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource // AJOUT
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction // AJOUT
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipcalculator.ui.theme.TipCalculatorTheme
import java.text.NumberFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TipCalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TipTimeLayout()
                }
            }
        }
    }
}

@Composable
fun TipTimeLayout() {
    var amountInput by remember { mutableStateOf("") } // MODIFIÉ : amountImput -> amountInput
    var tipInput by remember { mutableStateOf("") } // AJOUT : état du pourcentage
    var roundUp by remember { mutableStateOf(false) } // AJOUT : état du bouton bascule

    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val tipPercent = tipInput.toDoubleOrNull() ?: 0.0 // AJOUT
    val tip = calculateTip(amount, tipPercent, roundUp) // MODIFIÉ : 2 paramètres de plus

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 40.dp)
                .verticalScroll(rememberScrollState())
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.calculate_tip),
                modifier = Modifier
                    .padding(bottom = 16.dp, top = 40.dp)
                    .align(alignment = Alignment.Start)
            )
            // MODIFIÉ : label, icône et options clavier (Next) ajoutés
            EditNumberField(
                label = R.string.bill_amount,
                leadingIcon = R.drawable.money,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                value = amountInput,
                onValueChange = { amountInput = it },
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth()
            )
            // AJOUT : champ du pourcentage de pourboire (bouton Done)
            EditNumberField(
                label = R.string.how_was_the_service,
                leadingIcon = R.drawable.percent,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                value = tipInput,
                onValueChange = { tipInput = it },
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth()
            )
            // AJOUT : ligne "Round up tip?" avec le bouton bascule
            RoundTheTipRow(
                roundUp = roundUp,
                onRoundUpChanged = { roundUp = it },
                modifier = Modifier.padding(bottom = 32.dp)
            )
            Text(
                text = stringResource(R.string.tip_amount, tip),
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(150.dp))
        }
    }
}

/**
 * Calcule le pourboire selon la saisie et le formate selon la devise locale.
 * Exemple : "$10.00".
 */
@VisibleForTesting // AJOUT
internal fun calculateTip( // MODIFIÉ : private -> internal
    amount: Double,
    tipPercent: Double = 15.0,
    roundUp: Boolean // AJOUT
): String {
    var tip = tipPercent / 100 * amount // MODIFIÉ : val -> var
    if (roundUp) { // AJOUT
        tip = kotlin.math.ceil(tip)
    }
    return NumberFormat.getCurrencyInstance().format(tip)
}

@Composable
fun EditNumberField(
    @StringRes label: Int, // AJOUT
    @DrawableRes leadingIcon: Int, // AJOUT
    keyboardOptions: KeyboardOptions, // AJOUT
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(label)) }, // MODIFIÉ : utilise le paramètre label
        leadingIcon = { Icon(painter = painterResource(id = leadingIcon), contentDescription = null) }, // AJOUT
        singleLine = true,
        keyboardOptions = keyboardOptions, // MODIFIÉ : utilise le paramètre
        modifier = modifier
    )
}

// AJOUT : toute cette fonction
@Composable
fun RoundTheTipRow(
    roundUp: Boolean,
    onRoundUpChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .size(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(R.string.round_up_tip))
        Switch(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
            checked = roundUp,
            onCheckedChange = onRoundUpChanged
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TipTimeLayoutPreview() {
    TipCalculatorTheme {
        TipTimeLayout()
    }
}