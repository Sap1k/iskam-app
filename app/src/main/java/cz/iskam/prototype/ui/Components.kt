package cz.iskam.prototype.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.iskam.prototype.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/* ---------- M3 Expressive shape library (stands in for the prototype's SVG blobs) ---------- */

object Shapes {
    val cookie9: Shape @Composable get() = MaterialShapes.Cookie9Sided.toShape()
    val cookie6: Shape @Composable get() = MaterialShapes.Cookie6Sided.toShape()
    val cookie12: Shape @Composable get() = MaterialShapes.Cookie12Sided.toShape()
    val clover8: Shape @Composable get() = MaterialShapes.Clover8Leaf.toShape()
    val sunny: Shape @Composable get() = MaterialShapes.Sunny.toShape()
    val softBurst: Shape @Composable get() = MaterialShapes.SoftBurst.toShape()
    val flower: Shape @Composable get() = MaterialShapes.Flower.toShape()
    val gem: Shape @Composable get() = MaterialShapes.Gem.toShape()
}

@Composable
fun rememberSpin(durationMs: Int): Float {
    val tr = rememberInfiniteTransition(label = "spin")
    val v by tr.animateFloat(0f, 360f, infiniteRepeatable(tween(durationMs, easing = LinearEasing)), label = "rot")
    return v
}

/* ---------- screen scaffold ---------- */

@Composable
fun ScreenScaffold(
    withNav: Boolean = false,
    center: Boolean = false,
    overlay: @Composable BoxScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        val h = maxHeight
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = h)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(start = 18.dp, end = 18.dp, top = 8.dp, bottom = if (withNav) 104.dp else 24.dp),
                verticalArrangement = if (center) Arrangement.spacedBy(14.dp, Alignment.CenterVertically) else Arrangement.spacedBy(14.dp),
                horizontalAlignment = if (center) Alignment.CenterHorizontally else Alignment.Start,
                content = content,
            )
        }
        overlay()
    }
}

@Composable
fun ColumnScope.Fill() = Spacer(Modifier.weight(1f))

/* ---------- text helpers ---------- */

@Composable
fun H(text: String, size: TextUnit = 28.sp, modifier: Modifier = Modifier, align: TextAlign? = null, color: Color = Color.Unspecified) =
    Text(text, modifier, color = color, style = (if (size >= 32.sp) T.hxl else T.hl).copy(fontSize = size, lineHeight = size * 1.1f), textAlign = align)

@Composable
fun Body(text: String, modifier: Modifier = Modifier, size: TextUnit = 15.sp, align: TextAlign? = null) =
    Text(text, modifier, color = MaterialTheme.colorScheme.onSurfaceVariant, style = T.body.copy(fontSize = size, lineHeight = size * 1.47f), textAlign = align)

@Composable
fun Label(text: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) =
    Text(text, modifier, color = color, style = T.label)

@Composable
fun TitleText(text: String, modifier: Modifier = Modifier, size: TextUnit = 16.5.sp) =
    Text(text, modifier, style = T.title.copy(fontSize = size))

@Composable
fun Overline(text: String) =
    Text(text.uppercase(), Modifier.padding(top = 6.dp), color = MaterialTheme.colorScheme.primary, style = T.over)

@Composable
fun Crumbs(parent: String, current: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(parent, fontSize = 13.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(current, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
    }
}

/* ---------- app bar & icon buttons ---------- */

@Composable
fun AppBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.SemiBold) },
        navigationIcon = { RoundIconButton(Icons.AutoMirrored.Rounded.ArrowBack, "Zpět", onBack) },
        windowInsets = WindowInsets(0),
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        modifier = Modifier.offset(x = (-10).dp),
    )
}

@Composable
fun RoundIconButton(icon: ImageVector, desc: String?, onClick: () -> Unit, modifier: Modifier = Modifier, iconSize: Dp = 24.dp, tint: Color = LocalContentColor.current) {
    IconButton(onClick = onClick, shapes = IconButtonDefaults.shapes(), modifier = modifier) {
        Icon(icon, desc, Modifier.size(iconSize), tint = tint)
    }
}

@Composable
fun InfoButton(onClick: () -> Unit) {
    IconButton(onClick = onClick, shapes = IconButtonDefaults.shapes(), modifier = Modifier.size(32.dp)) {
        Icon(Icons.Rounded.Info, "Co to je", Modifier.size(19.dp), tint = MaterialTheme.colorScheme.primary)
    }
}

/* ---------- cards ---------- */

@Composable
fun IskamCard(
    modifier: Modifier = Modifier,
    container: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    outlined: Boolean = false,
    padding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
    gap: Dp = 6.dp,
    align: Alignment.Horizontal = Alignment.Start,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)
    val inner: @Composable ColumnScope.() -> Unit = {
        Column(Modifier.fillMaxWidth().padding(padding), verticalArrangement = Arrangement.spacedBy(gap), horizontalAlignment = align, content = content)
    }
    val m = modifier.fillMaxWidth()
    when {
        outlined && onClick != null -> OutlinedCard(onClick, m, shape = shape, border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant), content = inner)
        outlined -> OutlinedCard(m, shape = shape, border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant), content = inner)
        onClick != null -> Card(onClick, m, shape = shape, colors = CardDefaults.cardColors(container, contentColor), content = inner)
        else -> Card(m, shape = shape, colors = CardDefaults.cardColors(container, contentColor), content = inner)
    }
}

@Composable
fun CardTitle(icon: ImageVector, text: String, tint: Color = MaterialTheme.colorScheme.primary, chevron: Boolean = false, size: TextUnit = 16.5.sp) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icon, null, Modifier.size(if (size > 18.sp) 24.dp else 22.dp), tint = tint)
        Text(text, Modifier.weight(1f), fontSize = size, fontWeight = FontWeight.SemiBold)
        if (chevron) Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, Modifier.size(22.dp), tint = MaterialTheme.colorScheme.outline)
    }
}

@Composable
fun CardDesc(text: String, color: Color = LocalContentColor.current.copy(alpha = .8f)) =
    Text(text, color = color, fontSize = 14.sp, lineHeight = 19.6.sp)

/* ---------- buttons: native M3 Expressive buttons with press shape-morph + LoadingIndicator ---------- */

enum class Btn { Filled, Danger, Tonal, Outlined, OutlinedRed, Text }

@Composable
fun IskamButton(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    style: Btn = Btn.Filled,
    big: Boolean = false,
    wide: Boolean = false,
    enabled: Boolean = true,
    alignStart: Boolean = false,
    loadMs: Long = 0,
    onClick: () -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val h = if (big) ButtonDefaults.MediumContainerHeight else ButtonDefaults.MinHeight + 8.dp
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val click: () -> Unit = {
        if (loadMs > 0) {
            loading = true
            scope.launch { delay(loadMs); loading = false; onClick() }
        } else onClick()
    }
    val m = modifier.then(if (wide) Modifier.fillMaxWidth() else Modifier).heightIn(min = h)
    val shapes = ButtonDefaults.shapesFor(h)
    val pad = ButtonDefaults.contentPaddingFor(h)
    val content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit = {
        if (loading) {
            LoadingIndicator(Modifier.size(if (big) 36.dp else 30.dp), color = LocalContentColor.current)
        } else {
            if (icon != null) {
                Icon(icon, null, Modifier.size(ButtonDefaults.iconSizeFor(h)))
                Spacer(Modifier.width(ButtonDefaults.iconSpacingFor(h)))
            }
            Text(text, style = ButtonDefaults.textStyleFor(h), fontWeight = FontWeight.SemiBold, maxLines = 1)
            if (alignStart) Spacer(Modifier.weight(1f))
        }
    }
    val en = enabled && !loading
    when (style) {
        Btn.Filled -> Button(click, shapes, m, en, contentPadding = pad, content = content)
        Btn.Danger -> Button(click, shapes, m, en, colors = ButtonDefaults.buttonColors(cs.error, cs.onError), contentPadding = pad, content = content)
        Btn.Tonal -> FilledTonalButton(click, shapes, m, en, contentPadding = pad, content = content)
        Btn.Outlined -> OutlinedButton(click, shapes, m, en, contentPadding = pad, border = BorderStroke(1.5.dp, cs.outlineVariant), content = content)
        Btn.OutlinedRed -> OutlinedButton(
            click, shapes, m, en, colors = ButtonDefaults.outlinedButtonColors(contentColor = cs.error),
            contentPadding = pad, border = BorderStroke(1.5.dp, cs.outlineVariant), content = content,
        )
        Btn.Text -> TextButton(click, ButtonDefaults.shapes(), modifier, en, content = content)
    }
}

@Composable
fun Fab(text: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Icon(icon, null)
        Spacer(Modifier.width(10.dp))
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

/* ---------- lists: native segmented list items ---------- */

enum class Lead { Sec, Ter, Err, Pri }

@Composable
fun leadColors(lead: Lead): Pair<Color, Color> {
    val cs = MaterialTheme.colorScheme
    return when (lead) {
        Lead.Sec -> cs.secondaryContainer to cs.onSecondaryContainer
        Lead.Ter -> cs.tertiaryContainer to cs.onTertiaryContainer
        Lead.Err -> cs.errorContainer to cs.onErrorContainer
        Lead.Pri -> cs.primaryContainer to cs.onPrimaryContainer
    }
}

@Composable
fun LeadBox(icon: ImageVector, lead: Lead = Lead.Sec, size: Dp = 40.dp, corner: Dp = 14.dp, iconSize: Dp = 22.dp) {
    val (bg, fg) = leadColors(lead)
    Box(Modifier.size(size).background(bg, RoundedCornerShape(corner)), contentAlignment = Alignment.Center) {
        Icon(icon, null, Modifier.size(iconSize), tint = fg)
    }
}

class LRow(
    val icon: ImageVector,
    val title: String,
    val sub: String? = null,
    val lead: Lead = Lead.Sec,
    val value: String? = null,
    val chevron: Boolean = true,
    val trailing: (@Composable () -> Unit)? = null,
    val onClick: (() -> Unit)? = null,
)

@Composable
fun SegmentedList(vararg rows: LRow?) {
    val list = rows.filterNotNull()
    val colors = ListItemDefaults.segmentedColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    Column(verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
        list.forEachIndexed { i, r ->
            val shapes = ListItemDefaults.segmentedShapes(i, list.size)
            val leading: @Composable () -> Unit = { LeadBox(r.icon, r.lead) }
            val supporting: (@Composable () -> Unit)? = r.sub?.let { s -> { Text(s) } }
            val trailing: @Composable () -> Unit = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (r.value != null) Text(r.value, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    when {
                        r.trailing != null -> r.trailing.invoke()
                        r.chevron -> Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.outline)
                    }
                }
            }
            val title: @Composable () -> Unit = { Text(r.title) }
            if (r.onClick != null) SegmentedListItem(
                onClick = r.onClick, shapes = shapes, leadingContent = leading, supportingContent = supporting,
                trailingContent = trailing, colors = colors, content = title,
            ) else SegmentedListItem(
                shapes = shapes, leadingContent = leading, supportingContent = supporting,
                trailingContent = trailing, colors = colors, content = title,
            )
        }
    }
}

/* ---------- key/value table (.items) ---------- */

data class ItemRow(
    val label: String,
    val value: String = "",
    val info: String? = null,
    val sum: Boolean = false,
    val content: (@Composable () -> Unit)? = null,
)

@Composable
fun ItemsBox(rows: List<ItemRow>, modifier: Modifier = Modifier, onInfo: (String) -> Unit = {}) {
    Column(
        modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 18.dp, vertical = 4.dp)
    ) {
        rows.forEachIndexed { i, r ->
            if (i > 0) HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                Modifier.fillMaxWidth().heightIn(min = 46.dp).padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                val fs = if (r.sum) 17.sp else 15.sp
                val fw = if (r.sum) FontWeight.ExtraBold else FontWeight.Normal
                Text(r.label, Modifier.weight(1f), fontSize = fs, fontWeight = fw)
                if (r.info != null) InfoButton { onInfo(r.info) }
                if (r.content != null) r.content.invoke()
                else Text(r.value, fontSize = fs, fontWeight = fw, textAlign = TextAlign.End)
            }
        }
    }
}

/* ---------- chips, connected toggle group, pills ---------- */

@Composable
fun IskamChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(38.dp),
        label = {
            Text(
                text, fontWeight = FontWeight.SemiBold, maxLines = 1,
                textDecoration = if (enabled) null else TextDecoration.LineThrough,
            )
        },
        leadingIcon = when {
            selected -> ({ Icon(Icons.Rounded.Check, null, Modifier.size(FilterChipDefaults.IconSize)) })
            icon != null -> ({ Icon(icon, null, Modifier.size(FilterChipDefaults.IconSize)) })
            else -> null
        },
    )
}

/** Connected button group (M3 Expressive) used as the segmented control. */
@Composable
fun Segmented(options: List<Pair<String, String>>, selected: String, onSelect: (String) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)) {
        options.forEachIndexed { i, (key, label) ->
            val on = key == selected
            ToggleButton(
                checked = on,
                onCheckedChange = { onSelect(key) },
                modifier = Modifier.weight(1f).heightIn(min = 44.dp),
                shapes = when (i) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
                colors = ToggleButtonDefaults.colors(
                    checkedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    checkedContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
            ) {
                if (on) {
                    Icon(Icons.Rounded.Check, null, Modifier.size(ToggleButtonDefaults.IconSize))
                    Spacer(Modifier.width(ToggleButtonDefaults.IconSpacing))
                }
                Text(label, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

enum class PillKind { Ok, Warn, New, Err }

@Composable
fun Pill(text: String, kind: PillKind, icon: ImageVector? = null, container: Color? = null) {
    val cs = MaterialTheme.colorScheme
    val ex = LocalExtraColors.current
    val (bg, fg) = when (kind) {
        PillKind.Ok -> cs.primaryContainer to cs.onPrimaryContainer
        PillKind.Warn -> ex.warnC to ex.onWarnC
        PillKind.New -> cs.tertiaryContainer to cs.onTertiaryContainer
        PillKind.Err -> cs.errorContainer to cs.onErrorContainer
    }
    Row(
        Modifier.height(26.dp).background(container ?: bg, RoundedCornerShape(13.dp)).padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (icon != null) Icon(icon, null, Modifier.size(15.dp), tint = fg)
        Text(text, color = fg, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

/* ---------- avatar, seal, empty state ---------- */

@Composable
fun Avatar(size: Dp = 52.dp, ring: Dp = 3.dp, onClick: (() -> Unit)? = null) {
    Image(
        painterResource(R.drawable.jirka), "Profilová fotka",
        Modifier
            .size(size + ring * 2)
            .border(ring, MaterialTheme.colorScheme.primaryContainer, CircleShape)
            .padding(ring)
            .clip(CircleShape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentScale = ContentScale.Crop,
    )
}

/** Big spinning MaterialShapes badge used on success screens (.seal). */
@Composable
fun Seal(icon: ImageVector, shape: Shape, color: Color = MaterialTheme.colorScheme.primary, onColor: Color = MaterialTheme.colorScheme.onPrimary) {
    val pop = remember { Animatable(0f) }
    LaunchedEffect(Unit) { pop.animateTo(1f, spring(dampingRatio = .45f, stiffness = Spring.StiffnessLow)) }
    val rot = rememberSpin(14000)
    Box(
        Modifier.size(170.dp).graphicsLayer {
            val p = pop.value
            scaleX = .3f + .7f * p; scaleY = .3f + .7f * p
            rotationZ = -40f * (1 - p); alpha = p.coerceIn(0f, 1f)
        },
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.fillMaxSize().graphicsLayer { rotationZ = rot }.background(color, shape))
        Icon(icon, null, Modifier.size(84.dp), tint = onColor)
    }
}

@Composable
fun ShapeIcon(icon: ImageVector, shape: Shape, size: Dp = 120.dp, iconSize: Dp = 52.dp, bg: Color = MaterialTheme.colorScheme.surfaceContainerHigh, fg: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        Box(Modifier.fillMaxSize().background(bg, shape))
        Icon(icon, null, Modifier.size(iconSize), tint = fg)
    }
}

@Composable
fun ColumnScope.EmptyState(icon: ImageVector, shape: Shape, title: String, sub: String? = null, extra: @Composable () -> Unit = {}) {
    Column(
        Modifier.fillMaxWidth().weight(1f).padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
    ) {
        ShapeIcon(icon, shape)
        TitleText(title, size = 19.sp)
        if (sub != null) Body(sub, align = TextAlign.Center)
        extra()
    }
}

/* ---------- form bits ---------- */

@Composable
fun CheckRow(checked: Boolean, text: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .toggleable(checked, role = Role.Checkbox, onValueChange = { onClick() }),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = null, modifier = Modifier.padding(12.dp))
        Text(text, fontSize = 14.5.sp)
    }
}

/** Read-only text field used on the card-payment mock. */
@Composable
fun FakeField(label: String, text: String, modifier: Modifier = Modifier, icon: ImageVector? = null) {
    OutlinedTextField(
        value = text, onValueChange = {}, readOnly = true, modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = icon?.let { { Icon(it, null) } },
        shape = RoundedCornerShape(14.dp), singleLine = true,
    )
}

/** Three-step defect tracker (Přijato › Řeší se › Hotovo). M3 has no stepper, so this one stays custom. */
@Composable
fun Steps(st: Int) {
    val cs = MaterialTheme.colorScheme
    val names = listOf("Přijato", "Řeší se", "Hotovo")
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
        names.forEachIndexed { i, s ->
            if (i > 0) Box(
                Modifier.weight(1f).padding(top = 11.5.dp).height(3.dp)
                    .background(if (st >= i) cs.primary else cs.surfaceContainerHighest, RoundedCornerShape(2.dp))
            )
            val on = st >= i
            Column(Modifier.width(64.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    Modifier.size(26.dp).background(if (on) cs.primary else cs.surfaceContainerHighest, CircleShape),
                    contentAlignment = Alignment.Center,
                ) { if (on) Icon(Icons.Rounded.Check, null, Modifier.size(16.dp), tint = cs.onPrimary) }
                Text(
                    s, fontSize = 12.sp, textAlign = TextAlign.Center,
                    color = if (on) cs.onSurface else cs.onSurfaceVariant,
                    fontWeight = if (on) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
fun StatTile(value: String, label: String, modifier: Modifier = Modifier, extra: @Composable () -> Unit = {}) {
    Column(
        modifier.background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(20.dp)).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
        Row(verticalAlignment = Alignment.CenterVertically) { Label(label); extra() }
    }
}
