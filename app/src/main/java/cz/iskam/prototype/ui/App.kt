package cz.iskam.prototype.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.PhoneIphone
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.iskam.prototype.AppState
import cz.iskam.prototype.INFO
import cz.iskam.prototype.NavDir
import cz.iskam.prototype.Route
import cz.iskam.prototype.Screen
import cz.iskam.prototype.Sheet
import cz.iskam.prototype.TOTAL
import cz.iskam.prototype.kc
import kotlinx.coroutines.delay

@Composable
fun IskamApp(s: AppState, dark: Boolean) {
    IskamTheme(dark) {
        val cur = s.current
        BackHandler(enabled = s.stack.size > 1) { s.back() }
        Box(Modifier.fillMaxSize()) {
            val spatial = MaterialTheme.motionScheme.defaultSpatialSpec<androidx.compose.ui.unit.IntOffset>()
            AnimatedContent(
                targetState = cur,
                contentKey = { it.id },
                transitionSpec = {
                    when (s.dir) {
                        NavDir.Fwd -> (slideInHorizontally(spatial) { it / 7 } + fadeIn(tween(220))) togetherWith fadeOut(tween(200))
                        NavDir.Back -> (slideInHorizontally(spatial) { -it / 7 } + fadeIn(tween(220))) togetherWith fadeOut(tween(200))
                        NavDir.Fade -> (slideInVertically(spatial) { it / 40 } + fadeIn(tween(260))) togetherWith fadeOut(tween(180))
                    }
                },
                label = "screen",
            ) { route -> ScreenHost(s, route, dark) }

            AnimatedVisibility(
                visible = cur.screen.tab,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
            ) { BottomNav(s) }

            SnackHost(s, Modifier.align(Alignment.BottomCenter))
            s.sheet?.let { SheetHost(s, it) }
        }
    }
}

@Composable
private fun ScreenHost(s: AppState, r: Route, dark: Boolean) {
    when (r.screen) {
        Screen.Login -> LoginScreen(s)
        Screen.Home -> HomeScreen(s)
        Screen.Platby -> PlatbyScreen(s)
        Screen.Notif -> NotifScreen(s)
        Screen.More -> MoreScreen(s)
        Screen.Platba -> PlatbaScreen(s)
        Screen.Karta -> KartaScreen(s)
        Screen.Prevod -> PrevodScreen(s)
        Screen.Zaplaceno -> ZaplacenoScreen(s)
        Screen.Doklad -> DokladScreen(s, r.arg)
        Screen.Profil -> ProfilScreen(s, dark)
        Screen.Ubytovani -> UbytovaniScreen(s)
        Screen.Smlouva -> SmlouvaScreen(s)
        Screen.Zavady -> ZavadyScreen(s)
        Screen.ZavadaNova -> ZavadaNovaScreen(s)
        Screen.ZavadaOk -> ZavadaOkScreen(s)
        Screen.Rezervace -> RezervaceScreen(s)
        Screen.RezervaceNova -> RezervaceNovaScreen(s)
        Screen.Pokoje -> PokojeScreen(s)
        Screen.Kontakty -> KontaktyScreen(s)
        Screen.Napoveda -> NapovedaScreen(s)
    }
}

private data class Tab(val screen: Screen, val label: String, val on: ImageVector, val off: ImageVector)

private val TABS = listOf(
    Tab(Screen.Home, "Domů", Icons.Rounded.Home, Icons.Outlined.Home),
    Tab(Screen.Platby, "Platby", Icons.Rounded.Payments, Icons.Outlined.Payments),
    Tab(Screen.Notif, "Oznámení", Icons.Rounded.Notifications, Icons.Outlined.Notifications),
    Tab(Screen.More, "Více", Icons.Rounded.Menu, Icons.Rounded.Menu),
)

@Composable
private fun BottomNav(s: AppState) {
    ShortNavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
        TABS.forEach { t ->
            val selected = s.current.screen == t.screen
            ShortNavigationBarItem(
                selected = selected,
                onClick = { s.tab(t.screen) },
                icon = {
                    val icon = if (selected) t.on else t.off
                    if (t.screen == Screen.Notif && s.unread > 0) {
                        BadgedBox(badge = { Badge { Text(s.unread.toString()) } }) { Icon(icon, null) }
                    } else Icon(icon, null)
                },
                label = { Text(t.label) },
            )
        }
    }
}

@Composable
private fun SnackHost(s: AppState, modifier: Modifier) {
    val snack = s.snack
    var shown by remember { mutableStateOf(snack) }
    if (snack != null) shown = snack
    LaunchedEffect(snack?.stamp) {
        if (snack != null) { delay(3400); if (s.snack?.stamp == snack.stamp) s.snack = null }
    }
    AnimatedVisibility(
        visible = snack != null,
        modifier = modifier
            .navigationBarsPadding()
            .padding(start = 14.dp, end = 14.dp, bottom = if (s.current.screen.tab) 92.dp else 24.dp),
        enter = slideInVertically(MaterialTheme.motionScheme.fastSpatialSpec()) { it / 2 } + fadeIn(),
        exit = fadeOut(),
    ) {
        shown?.let { sn ->
            Snackbar(shape = RoundedCornerShape(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(sn.icon, null, Modifier.size(20.dp))
                    Text(sn.text, fontSize = 14.5.sp)
                }
            }
        }
    }
}

@Composable
private fun SheetHost(s: AppState, sheet: Sheet) {
    ModalBottomSheet(
        onDismissRequest = { s.sheet = null },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            when (sheet) {
                is Sheet.Info -> {
                    val (title, text) = INFO.getValue(sheet.key)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Rounded.Info, null, Modifier.size(28.dp), tint = MaterialTheme.colorScheme.primary)
                        H(title, 24.sp)
                    }
                    Body(text, size = 16.sp)
                    IskamButton("Rozumím", wide = true) { s.sheet = null }
                }
                is Sheet.Wallet -> {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(if (sheet.name == "Apple Pay") Icons.Rounded.PhoneIphone else Icons.Rounded.AccountBalanceWallet, null, Modifier.size(28.dp))
                        TitleText(sheet.name, size = 19.sp)
                    }
                    ItemsBox(listOf(ItemRow("ISKAM · Koleje VŠE", kc(TOTAL)), ItemRow("Karta", "•••• 4242")))
                    IskamButton("Potvrdit platbu", icon = Icons.Rounded.Fingerprint, big = true, wide = true, loadMs = 1100) { s.finishPay(sheet.name) }
                }
                Sheet.EndStay -> {
                    var pick by remember { mutableIntStateOf(1) }
                    H("Ukončit ubytování?", 24.sp)
                    Body("Vyber, ke kdy chceš odejít. Hospodářka pak navrhne termín předání pokoje a vrácení jistiny.")
                    androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("30. 11.", "31. 12.", "31. 1.", "Jiné datum").forEachIndexed { i, d -> IskamChip(d, pick == i) { pick = i } }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        IskamButton("Zrušit", Modifier.weight(1f), style = Btn.Outlined) { s.sheet = null }
                        IskamButton("Odeslat žádost", Modifier.weight(1f), style = Btn.Danger, loadMs = 900) {
                            s.sheet = null; s.ending = true
                            s.toast("Žádost o ukončení odeslána hospodářce", Icons.AutoMirrored.Rounded.Send)
                        }
                    }
                }
                Sheet.Msg -> {
                    var text by remember { mutableStateOf("") }
                    val focus = remember { FocusRequester() }
                    LaunchedEffect(Unit) { delay(350); runCatching { focus.requestFocus() } }
                    H("Zpráva hospodářce", 24.sp)
                    OutlinedTextField(
                        text, { text = it }, Modifier.fillMaxWidth().heightIn(min = 110.dp).focusRequester(focus),
                        placeholder = { Text("Dobrý den, potřeboval bych…") }, shape = RoundedCornerShape(14.dp), minLines = 4,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        IskamButton("Zrušit", Modifier.weight(1f), style = Btn.Outlined) { s.sheet = null }
                        IskamButton("Odeslat", Modifier.weight(1f), icon = Icons.AutoMirrored.Rounded.Send, loadMs = 700) {
                            s.sheet = null
                            s.toast("Odesláno. Odpověď přijde do Oznámení.", Icons.AutoMirrored.Rounded.Send)
                        }
                    }
                }
            }
        }
    }
}
