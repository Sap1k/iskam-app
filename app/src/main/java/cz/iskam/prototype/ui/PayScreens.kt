package cz.iskam.prototype.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Bed
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ConfirmationNumber
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhoneIphone
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.iskam.prototype.AppState
import cz.iskam.prototype.PENDING
import cz.iskam.prototype.Screen
import cz.iskam.prototype.Sheet
import cz.iskam.prototype.TOTAL
import cz.iskam.prototype.kc

@Composable
fun LoginScreen(s: AppState) {
    var nick by remember { mutableStateOf("jiri_kara") }
    var pass by remember { mutableStateOf("hlavnezemaspivo") }
    ScreenScaffold {
        Spacer(Modifier.height(56.dp))
        Column {
            Text("ISKAM", style = T.hxl.copy(fontSize = 52.sp, lineHeight = 54.sp))
            Text("(ale lepší)", fontSize = 18.sp, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.tertiary)
        }
        TitleText("Přihlaste se", Modifier.padding(top = 22.dp), size = 21.sp)
        OutlinedTextField(
            nick, { nick = it }, Modifier.fillMaxWidth(), label = { Text("Nickname") },
            leadingIcon = { Icon(Icons.Rounded.Person, null) }, singleLine = true, shape = RoundedCornerShape(14.dp),
        )
        OutlinedTextField(
            pass, { pass = it }, Modifier.fillMaxWidth(), label = { Text("Heslo") },
            leadingIcon = { Icon(Icons.Rounded.Lock, null) }, singleLine = true, shape = RoundedCornerShape(14.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )
        IskamButton("Přihlásit se", Modifier.padding(top = 8.dp), big = true, wide = true, loadMs = 700) { s.login() }
        IskamButton("Zapomenuté heslo?", Modifier.align(Alignment.CenterHorizontally), style = Btn.Text) {
            s.toast("Odkaz na obnovu hesla odešel na školní e-mail.")
        }
    }
}

@Composable
private fun QuickAction(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, shape: Shape, bg: Color, fg: Color, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        ShapeIcon(icon, shape, size = 62.dp, iconSize = 26.dp, bg = bg, fg = fg)
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, maxLines = 1)
    }
}

@Composable
fun HomeScreen(s: AppState) {
    val cs = MaterialTheme.colorScheme
    ScreenScaffold(withNav = true) {
        Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Ahoj, Jiří!", Modifier.weight(1f), style = T.hxl)
            Avatar { s.go(Screen.Profil) }
        }
        if (s.paid) {
            IskamCard(container = cs.primaryContainer, contentColor = cs.onPrimaryContainer, padding = PaddingValues(20.dp)) {
                CardTitle(Icons.Rounded.Verified, "Máš zaplaceno 👍", tint = cs.primary, size = 21.sp)
                CardDesc("Další platba: 1. 11. · připomeneme se 29. 10.")
                IskamButton("Potvrzení o platbě", Modifier.padding(top = 4.dp), icon = Icons.AutoMirrored.Rounded.ReceiptLong, style = Btn.Tonal) {
                    s.go(Screen.Doklad, "now")
                }
            }
        } else {
            IskamCard(container = cs.errorContainer, contentColor = cs.onErrorContainer, padding = PaddingValues(20.dp), gap = 10.dp) {
                CardTitle(Icons.Rounded.Error, "Nemáš zaplaceno!", tint = cs.error, size = 21.sp)
                CardDesc("Celkem ${kc(TOTAL)} · splatnost byla před 4 dny 😬")
                IskamButton("Zaplatit vše", icon = Icons.Rounded.Bolt, style = Btn.Danger, big = true, wide = true) { s.go(Screen.Platba) }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val w = Modifier.weight(1f)
            QuickAction("Závada", Icons.Rounded.Build, Shapes.cookie9, cs.primaryContainer, cs.onPrimaryContainer, w) { s.go(Screen.ZavadaNova) }
            QuickAction("Rezervace", Icons.Rounded.EventAvailable, Shapes.cookie6, cs.tertiaryContainer, cs.onTertiaryContainer, w) { s.go(Screen.RezervaceNova) }
            QuickAction("Volné pokoje", Icons.Rounded.Bed, Shapes.clover8, cs.secondaryContainer, cs.onSecondaryContainer, w) { s.go(Screen.Pokoje) }
            QuickAction("Nápověda", Icons.AutoMirrored.Rounded.Help, Shapes.sunny, cs.surfaceContainerHighest, cs.onSurface, w) { s.go(Screen.Napoveda) }
        }
        if (!s.signed) {
            IskamCard(container = cs.tertiaryContainer, contentColor = cs.onTertiaryContainer, onClick = { s.go(Screen.Smlouva) }) {
                CardTitle(Icons.Rounded.Draw, "Smlouva čeká na podpis", tint = cs.tertiary, chevron = true)
                CardDesc("Na další období. Podepíšeš za minutu přímo tady.")
            }
        }
        IskamCard(onClick = { s.go(Screen.Ubytovani) }) {
            CardTitle(Icons.Rounded.Bed, "Ubytování", chevron = true)
            CardDesc("Koleje na Káráku\nSquat 01 · 8 (0) spolubydlících", cs.onSurfaceVariant)
        }
        if (s.res.isNotEmpty()) {
            IskamCard(onClick = { s.go(Screen.Rezervace) }) {
                CardTitle(Icons.Rounded.Event, "Rezervace", chevron = true)
                CardDesc(s.res.joinToString("\n") { "${it.room} · ${it.time}" }, cs.onSurfaceVariant)
            }
        }
        val open = s.defects.filter { it.st < 2 }
        if (open.isNotEmpty()) {
            IskamCard(onClick = { s.go(Screen.Zavady) }) {
                CardTitle(Icons.Rounded.Build, "Závady", chevron = true)
                CardDesc("${open.size}× se řeší · ${open.first().title}", cs.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun PlatbyScreen(s: AppState) {
    val cek = s.segPay == "cek"
    ScreenScaffold(withNav = true) {
        H("Platby", modifier = Modifier.padding(top = 10.dp))
        Crumbs("Platby", if (cek) "Čekající" else "Provedené")
        Segmented(listOf("cek" to "Čekající", "hist" to "Provedené"), s.segPay) { s.segPay = it }
        if (cek) {
            if (s.paid) {
                EmptyState(Icons.Rounded.Savings, Shapes.cookie9, "Nic nedlužíš", "Další platba bude 1. 11. Připomeneme se.")
            } else {
                ItemsBox(
                    PENDING.map { ItemRow(it.k, kc(it.v), it.info) } + ItemRow("Celkem", kc(TOTAL), sum = true),
                    onInfo = { s.sheet = Sheet.Info(it) },
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Pill("Po splatnosti 4 dny", PillKind.Err, Icons.Rounded.Schedule)
                    InfoButton { s.sheet = Sheet.Info("splatnost") }
                }
                IskamButton("Zaplatit vše · ${kc(TOTAL)}", icon = Icons.Rounded.Bolt, style = Btn.Danger, big = true, wide = true) { s.go(Screen.Platba) }
            }
        } else {
            val rows = buildList {
                if (s.paid) add(LRow(Icons.Rounded.CheckCircle, "Říjen 2026", "Zaplaceno ${s.paidAt}", value = kc(TOTAL)) { s.go(Screen.Doklad, "now") })
                s.history.forEach { h -> add(LRow(Icons.Rounded.CheckCircle, h.m, "Zaplaceno ${h.date}", value = kc(h.sum)) { s.go(Screen.Doklad, h.id) }) }
            }
            SegmentedList(*rows.toTypedArray())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatTile(kc(s.history.sumOf { it.sum } + if (s.paid) TOTAL else 0), "zaplaceno od června", Modifier.weight(1f))
                StatTile(kc(5000), "jistina", Modifier.weight(1f)) { InfoButton { s.sheet = Sheet.Info("jistina") } }
            }
        }
    }
}

@Composable
fun PlatbaScreen(s: AppState) = ScreenScaffold {
    AppBar("") { s.back() }
    H("Zaplatit")
    ItemsBox(PENDING.map { ItemRow(it.k, kc(it.v)) } + ItemRow("Celkem", kc(TOTAL), sum = true))
    TitleText("Jak chceš platit?", Modifier.padding(top = 4.dp))
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        IskamButton("Apple Pay", icon = Icons.Rounded.PhoneIphone, style = Btn.Outlined, wide = true, alignStart = true) { s.sheet = Sheet.Wallet("Apple Pay") }
        IskamButton("Google Pay", icon = Icons.Rounded.AccountBalanceWallet, style = Btn.Outlined, wide = true, alignStart = true) { s.sheet = Sheet.Wallet("Google Pay") }
        IskamButton("Kartou", icon = Icons.Rounded.CreditCard, wide = true, alignStart = true) { s.go(Screen.Karta) }
        IskamButton("Převodem (QR kód)", icon = Icons.Rounded.AccountBalance, style = Btn.Outlined, wide = true, alignStart = true) { s.go(Screen.Prevod) }
        IskamButton("Kreditové poukázky", icon = Icons.Rounded.ConfirmationNumber, style = Btn.Outlined, wide = true, alignStart = true) {
            s.toast("Kreditové poukázky zatím neumíme. Možná příště.")
        }
    }
    Row(Modifier.padding(start = 6.dp)) {
        Text("bitcoin / monero", style = T.label, color = MaterialTheme.colorScheme.outline, textDecoration = TextDecoration.LineThrough)
        Text(" (možná příště)", style = T.label, color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
fun KartaScreen(s: AppState) {
    var save by remember { mutableStateOf(true) }
    ScreenScaffold {
        AppBar("") { s.back() }
        H("Kartou")
        FakeField("Číslo karty", "4242 4242 4242 4242", icon = Icons.Rounded.CreditCard)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FakeField("Platnost", "12/28", Modifier.weight(1f))
            FakeField("CVV", "•••", Modifier.weight(1f))
        }
        CheckRow(save, "Zapamatovat kartu pro příště") { save = !save }
        Fill()
        IskamButton("Zaplatit ${kc(TOTAL)}", big = true, wide = true, loadMs = 1200) { s.finishPay("Kartou") }
    }
}

// The "payment" QR is a real, scannable QR code... for youtube.com/watch?v=0ro6yiJqKTU
private val QR_ROWS = listOf(
    "111111101101101000111101001111111",
    "100000101010001001000001101000001",
    "101110100101111010100000001011101",
    "101110101110000000100101101011101",
    "101110100101110010100111101011101",
    "100000100100101111100101001000001",
    "111111101010101010101010101111111",
    "000000001111000111110010100000000",
    "101101110000001111100101101001011",
    "101101001011011011111011001101111",
    "000110110110010010101111000111011",
    "011100000111100111001111111101010",
    "010100111100001110011111110101000",
    "010111011001011000001001010101010",
    "011110101111111000111000101110100",
    "011101000000011100000110011111100",
    "001110111101010000001111011011100",
    "011001010100111100000001001011001",
    "001110110011001100101100000110100",
    "100101011110100011100010111010011",
    "110100111001000010010010100001101",
    "110100010100010111001110000001001",
    "001001101111100100111011011100111",
    "011110010111000100101010101000001",
    "100110101111000011010010111111001",
    "000000001010101110110110100011010",
    "111111101001011001110101101010010",
    "100000101100100101110001100011101",
    "101110100110001011100011111110111",
    "101110101110010010010011100101011",
    "101110101110101111001111111100000",
    "100000100111111001101010000110001",
    "111111101001101011110101001011100",
)

@Composable
private fun FakeQr() {
    Box(Modifier.size(170.dp).background(Color.White, RoundedCornerShape(12.dp)).padding(10.dp)) {
        Canvas(Modifier.size(150.dp)) {
            val m = size.width / QR_ROWS.size
            QR_ROWS.forEachIndexed { i, row ->
                row.forEachIndexed { j, c ->
                    if (c == '1') drawRect(Color(0xFF111111), Offset(j * m, i * m), Size(m + .5f, m + .5f))
                }
            }
        }
    }
}

@Composable
fun PrevodScreen(s: AppState) = ScreenScaffold {
    AppBar("") { s.back() }
    H("Převodem")
    Body("Naskenuj QR kód v bankovní appce. Platbu spárujeme podle variabilního symbolu, obvykle do druhého dne.")
    IskamCard(padding = PaddingValues(22.dp), gap = 14.dp, align = Alignment.CenterHorizontally) {
        FakeQr()
        ItemsBox(listOf(ItemRow("Částka", kc(TOTAL)), ItemRow("Variabilní symbol", "2610014350")), Modifier.background(Color.Transparent))
    }
    IskamButton("Mám zaplaceno", big = true, wide = true, loadMs = 1200) { s.finishPay("Převodem") }
}

@Composable
fun ZaplacenoScreen(s: AppState) = ScreenScaffold(center = true) {
    Seal(Icons.Rounded.Check, Shapes.cookie12)
    H("Zaplaceno!", 38.sp, align = TextAlign.Center)
    Body("Odešlo ${kc(TOTAL)} (${s.method}).\nMáš klid až do listopadu.", size = 16.sp, align = TextAlign.Center)
    Column(Modifier.fillMaxWidth().padding(top = 18.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        IskamButton("Zpět domů", big = true, wide = true) { s.tab(Screen.Home) }
        IskamButton("Zobrazit potvrzení", icon = Icons.AutoMirrored.Rounded.ReceiptLong, style = Btn.Text) { s.go(Screen.Doklad, "now") }
    }
}

@Composable
fun DokladScreen(s: AppState, id: String?) {
    val cs = MaterialTheme.colorScheme
    data class D(val m: String, val sum: Int, val date: String, val how: String, val items: List<Pair<String, Int>>)
    val h = if (id == "now") D("Říjen 2026", TOTAL, s.paidAt ?: "", s.method ?: "", PENDING.map { it.k to it.v })
    else s.history.first { it.id == id }.let { D(it.m, it.sum, it.date, it.how, it.items) }
    ScreenScaffold {
        AppBar("Potvrzení o platbě") { s.back() }
        IskamCard(container = cs.primaryContainer, contentColor = cs.onPrimaryContainer, padding = PaddingValues(20.dp), gap = 4.dp) {
            Text(h.m, style = T.label, color = cs.onPrimaryContainer.copy(alpha = .8f))
            Text(kc(h.sum), style = T.hxl)
            Spacer(Modifier.height(6.dp))
            Pill("Přijato ${h.date}", PillKind.Ok, Icons.Rounded.Check, container = cs.surface)
        }
        ItemsBox(h.items.map { ItemRow(it.first, kc(it.second)) } + ItemRow("Způsob", h.how) + ItemRow("Plátce", "Jiří · Squat 01"))
        IskamButton("Poslat na e-mail", icon = Icons.Rounded.Mail, style = Btn.Tonal, wide = true) {
            s.toast("Potvrzení jsme poslali na tvůj školní e-mail.", Icons.Rounded.Mail)
        }
    }
}
