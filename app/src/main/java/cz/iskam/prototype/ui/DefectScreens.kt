package cz.iskam.prototype.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.Chair
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Lan
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cz.iskam.prototype.AppState
import cz.iskam.prototype.Defect
import cz.iskam.prototype.DefectDraft
import cz.iskam.prototype.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max

private const val MAX_PHOTOS = 6

/** Decodes a picked image, scaled down so a handful of photos stay cheap to keep in memory. */
private fun loadPhoto(ctx: Context, uri: Uri, maxSide: Int = 1280): ImageBitmap? = runCatching {
    if (Build.VERSION.SDK_INT >= 28) {
        val src = ImageDecoder.createSource(ctx.contentResolver, uri)
        ImageDecoder.decodeBitmap(src) { dec, info, _ ->
            val scale = maxSide.toFloat() / max(info.size.width, info.size.height)
            if (scale < 1f) dec.setTargetSize((info.size.width * scale).toInt(), (info.size.height * scale).toInt())
            dec.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }.asImageBitmap()
    } else {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        ctx.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (max(bounds.outWidth, bounds.outHeight) / (sample * 2) >= maxSide) sample *= 2
        ctx.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
        }?.asImageBitmap()
    }
}.getOrNull()

@Composable
fun PhotoThumb(img: ImageBitmap, size: Dp, onOpen: () -> Unit, onRemove: (() -> Unit)? = null) {
    Box(Modifier.size(size)) {
        Image(
            img, "Fotka závady",
            Modifier.fillMaxSize().clip(RoundedCornerShape(18.dp)).clickable(onClick = onOpen),
            contentScale = ContentScale.Crop,
        )
        if (onRemove != null) {
            FilledIconButton(
                onClick = onRemove,
                shapes = IconButtonDefaults.shapes(),
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(28.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                ),
            ) { Icon(Icons.Rounded.Close, "Odebrat fotku", Modifier.size(16.dp)) }
        }
    }
}

/** Full-screen photo viewer. */
@Composable
fun PhotoViewer(img: ImageBitmap, onClose: () -> Unit) {
    Dialog(onClose, DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.Black.copy(alpha = .92f)).clickable(onClick = onClose)) {
            Image(img, "Fotka závady", Modifier.fillMaxSize().padding(16.dp), contentScale = ContentScale.Fit)
            FilledIconButton(
                onClick = onClose, shapes = IconButtonDefaults.shapes(),
                modifier = Modifier.align(Alignment.TopEnd).padding(24.dp),
            ) { Icon(Icons.Rounded.Close, "Zavřít") }
        }
    }
}

@Composable
private fun PhotoStrip(photos: List<ImageBitmap>, size: Dp, onRemove: ((Int) -> Unit)? = null) {
    var open by remember { mutableStateOf<ImageBitmap?>(null) }
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        photos.forEachIndexed { i, p -> PhotoThumb(p, size, onOpen = { open = p }, onRemove = onRemove?.let { { it(i) } }) }
    }
    open?.let { PhotoViewer(it) { open = null } }
}

/* ---------- Závady ---------- */

@Composable
fun ZavadyScreen(s: AppState) = ScreenScaffold(overlay = {
    Fab("Nahlásit závadu", Icons.Rounded.Add, Modifier.align(Alignment.BottomEnd).navigationBarsPadding().padding(18.dp)) {
        s.go(Screen.ZavadaNova)
    }
}) {
    AppBar("Závady") { s.back() }
    s.defects.forEach { d -> DefectCard(s, d) }
    Spacer(Modifier.heightIn(min = 70.dp))
}

@Composable
private fun DefectCard(s: AppState, d: Defect) {
    IskamCard(gap = 10.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LeadBox(d.icon)
            Column(Modifier.weight(1f)) {
                TitleText(d.title)
                Label("${d.cat} · nahlášeno ${d.date}" + if (d.photos.isNotEmpty()) " · ${d.photos.size}× foto" else "")
            }
        }
        if (d.photos.isNotEmpty()) PhotoStrip(d.photos, 72.dp)
        Steps(d.st)
        if (d.st < 2) {
            IskamButton(
                if (d.reminded) "Připomenuto" else "Pořád to nejde, připomenout",
                icon = if (d.reminded) Icons.Rounded.Done else Icons.Rounded.Campaign,
                style = if (d.reminded) Btn.Text else Btn.Tonal,
                enabled = !d.reminded,
            ) {
                val i = s.defects.indexOfFirst { it.id == d.id }
                s.defects[i] = d.copy(reminded = true)
                s.toast("Připomenuto správci budovy", Icons.Rounded.Campaign)
            }
        }
    }
}

private val CATS = listOf(
    "Voda" to Icons.Rounded.WaterDrop, "Elektřina" to Icons.Rounded.Bolt, "Topení" to Icons.Rounded.Thermostat,
    "Internet" to Icons.Rounded.Lan, "Nábytek" to Icons.Rounded.Chair, "Jiné" to Icons.Rounded.MoreHoriz,
)

@Composable
fun ZavadaNovaScreen(s: AppState) {
    val d = s.draft
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val left = MAX_PHOTOS - d.photos.size

    fun addPhotos(new: List<ImageBitmap>) {
        if (new.isEmpty()) return
        s.draft = s.draft.copy(photos = (s.draft.photos + new).take(MAX_PHOTOS))
    }

    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(MAX_PHOTOS)) { uris ->
        if (uris.isNotEmpty()) scope.launch {
            val imgs = withContext(Dispatchers.IO) { uris.mapNotNull { loadPhoto(ctx, it) } }
            addPhotos(imgs)
            if (imgs.size < uris.size) s.toast("Některou fotku se nepodařilo načíst")
        }
    }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        if (bmp != null) addPhotos(listOf(bmp.asImageBitmap()))
    }

    ScreenScaffold {
        AppBar("Nahlásit závadu") { s.back() }
        Crumbs("Závady", "Nová")
        TitleText("Co nefunguje?")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            CATS.forEach { (c, ic) -> IskamChip(c, d.cat == c, icon = ic) { s.draft = s.draft.copy(cat = c) } }
        }
        OutlinedTextField(
            d.text, { s.draft = s.draft.copy(text = it) }, Modifier.fillMaxWidth().heightIn(min = 120.dp),
            label = { Text("Popis") }, placeholder = { Text("Např. kape kohoutek v kuchyňce, nejde zavřít") },
            shape = RoundedCornerShape(14.dp), minLines = 4,
        )
        ItemsBox(listOf(ItemRow("Místo", "Squat 01, Koleje na Káráku")))

        Row(verticalAlignment = Alignment.CenterVertically) {
            TitleText("Fotky", Modifier.weight(1f))
            Label("${d.photos.size}/$MAX_PHOTOS")
        }
        AnimatedVisibility(d.photos.isNotEmpty()) {
            PhotoStrip(d.photos, 96.dp) { i -> s.draft = s.draft.copy(photos = s.draft.photos.filterIndexed { k, _ -> k != i }) }
        }
        if (d.photos.isEmpty()) Body("Fotka pomůže správci pochopit, co se děje. Klidně víc.", size = 14.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            IskamButton("Vyfotit", Modifier.weight(1f), icon = Icons.Rounded.PhotoCamera, style = Btn.Tonal, enabled = left > 0) {
                try { camera.launch(null) } catch (_: ActivityNotFoundException) { s.toast("Tady není žádná appka na focení") }
            }
            IskamButton("Z galerie", Modifier.weight(1f), icon = Icons.Rounded.PhotoLibrary, style = Btn.Outlined, enabled = left > 0) {
                gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }

        Fill()
        IskamButton("Odeslat", big = true, wide = true, enabled = d.cat != null, loadMs = 900) {
            val cur = s.draft
            val ic = CATS.first { it.first == cur.cat }.second
            s.defects.add(
                0,
                Defect("d${System.nanoTime()}", cur.cat!!, ic, cur.text.trim().ifEmpty { "${cur.cat}: bez popisu" }, "30. 10.", 0, photos = cur.photos),
            )
            s.draft = DefectDraft()
            s.replace(Screen.ZavadaOk)
        }
    }
}

@Composable
fun ZavadaOkScreen(s: AppState) = ScreenScaffold(center = true) {
    val cs = MaterialTheme.colorScheme
    Seal(Icons.Rounded.Build, Shapes.cookie6, cs.tertiaryContainer, cs.onTertiaryContainer)
    H("Nahlášeno")
    Body("Správce to uvidí ještě dnes. Stav sleduj v Závadách, o změně ti dáme vědět.", align = TextAlign.Center)
    Column(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        IskamButton("Zobrazit závady", big = true, wide = true) { s.replace(Screen.Zavady) }
        IskamButton("Domů", style = Btn.Text) { s.tab(Screen.Home) }
    }
}
