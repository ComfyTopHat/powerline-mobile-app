package com.comfy.powerline

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.comfy.powerline.data.Message
import com.comfy.powerline.ui.theme.BluePrimary
import com.comfy.powerline.utils.AppToolBox

enum class InputSelector {
    NONE,
    MAP,
    EMOJI,
    PHONE,
    PICTURE
}

class MessageThreadV2 : AppCompatActivity() {
    val api = ApiHandler()
    var jwt = ""
    var _messages = mutableStateListOf <Message>()
    var senderID:Int = 0
    var recipientID:Int = 0
    var recipientName:String = ""
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setValues()
        val appToolBox = AppToolBox(applicationContext)
        jwt = appToolBox.retrieveJWT()
        _messages = api.getThreadMessages(recipientID.toString(), jwt).toMutableStateList()
        setContent {
            Scaffold(topBar = {
                TopAppBar(colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = BluePrimary),
                    title = { Text(
                        text = "Messages",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White
                    ) },
                    navigationIcon = {
                        IconButton(onClick = {
                            // Toast.makeText(context, "Nav Button", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "Go back",
                            )
                        }
                    })
            },
                content = {
                        paddingValues ->
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues)) {
                        Thread(messages = _messages)

                    }
                },
                bottomBar = {UserInput()}

            )
        }
    }

    @Composable
    fun Thread(messages: List<Message>) {
        LazyColumn {
            items(messages) { message ->
                MessageCard(message)
            }
        }
    }


    @Composable
    fun MessageCard(msg: Message) {
        // Add padding around our message
        if (msg.selfAuthored) {
            Row(modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                ) {
                // Add a horizontal space between the image and the column
                Spacer(modifier = Modifier.width(8.dp))
                var isExpanded by remember { mutableStateOf(false) }
                // surfaceColor will be updated gradually from one color to the other
                val surfaceColor by animateColorAsState(
                    if (isExpanded) BluePrimary else MaterialTheme.colorScheme.surface,
                )
                Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
                    Text(
                        text = msg.senderName,
                        modifier = Modifier.padding(all = 4.dp),
                        // If the message is expanded, we display all its content
                        // otherwise we only display the first line
                        maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    // Add a vertical space between the author and message texts
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        shadowElevation = 1.dp,
                        // surfaceColor color will be changing gradually from primary to surface
                        color = surfaceColor,
                        // animateContentSize will change the Surface size gradually
                        modifier = Modifier
                            .animateContentSize()
                            .padding(1.dp)
                    ) {
                        Text(
                            text = msg.body,
                            modifier = Modifier.padding(all = 4.dp),
                            // If the message is expanded, we display all its content
                            // otherwise we only display the first line
                            maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Image(
                    painter = painterResource(R.mipmap.powerline),
                    contentDescription = "Contact profile picture",
                    modifier = Modifier
                        // Set image size to 40 dp
                        .size(40.dp)
                        // Clip image to be shaped as a circle
                        .clip(CircleShape)
                )
            }
        }
        else {
            Row(modifier = Modifier.padding(all = 8.dp)) {
                Image(
                    painter = painterResource(R.mipmap.powerline),
                    contentDescription = "Contact profile picture",
                    modifier = Modifier
                        // Set image size to 40 dp
                        .size(40.dp)
                        // Clip image to be shaped as a circle
                        .clip(CircleShape)
                )
                // Add a horizontal space between the image and the column
                Spacer(modifier = Modifier.width(8.dp))
                var isExpanded by remember { mutableStateOf(false) }
                // surfaceColor will be updated gradually from one color to the other
                val surfaceColor by animateColorAsState(
                    if (isExpanded) BluePrimary else MaterialTheme.colorScheme.surface,
                )
                Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
                    Text(
                        text = msg.senderName,
                        modifier = Modifier.padding(all = 4.dp),
                        // If the message is expanded, we display all its content
                        // otherwise we only display the first line
                        maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    // Add a vertical space between the author and message texts
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        shadowElevation = 1.dp,
                        // surfaceColor color will be changing gradually from primary to surface
                        color = surfaceColor,
                        // animateContentSize will change the Surface size gradually
                        modifier = Modifier
                            .animateContentSize()
                            .padding(1.dp)
                    ) {
                        Text(
                            text = msg.body,
                            modifier = Modifier.padding(all = 4.dp),
                            // If the message is expanded, we display all its content
                            // otherwise we only display the first line
                            maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }


    @Composable
    private fun UserInputSelector(
        onSelectorChange: (InputSelector) -> Unit,
        sendMessageEnabled: Boolean,
        onMessageSent: () -> Unit,
        currentInputSelector: InputSelector,
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier
                .height(72.dp)
                .wrapContentHeight()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InputSelectorButton(
                onClick = { onSelectorChange(InputSelector.EMOJI) },
                icon = Icons.Outlined.Call,
                selected = currentInputSelector == InputSelector.EMOJI,
                description = stringResource(id = R.string.app_name)
            )
            InputSelectorButton(
                onClick = { onSelectorChange(InputSelector.PICTURE) },
                icon = Icons.Outlined.Build,
                selected = currentInputSelector == InputSelector.PICTURE,
                description = stringResource(id = R.string.app_name)
            )
            val border = if (!sendMessageEnabled) {
                BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            } else {
                null
            }
            Spacer(modifier = Modifier.weight(1f))
            val disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            val buttonColors = ButtonDefaults.buttonColors(
                disabledContainerColor = Color.Transparent,
                disabledContentColor = disabledContentColor
            )

            // Send button
            Button(
                modifier = Modifier.height(36.dp),
                enabled = sendMessageEnabled,
                onClick = onMessageSent,
                colors = buttonColors,
                border = border,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    stringResource(id = R.string.send),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun UserInput(

    ) {
        var currentInputSelector by rememberSaveable { mutableStateOf(InputSelector.NONE) }
        val dismissKeyboard = { currentInputSelector = InputSelector.NONE }

        // Intercept back navigation if there's a InputSelector visible
        if (currentInputSelector != InputSelector.NONE) {
            BackHandler(onBack = dismissKeyboard)
        }

        var textState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue())
        }

        // Used to decide if the keyboard should be shown
        var textFieldFocusState by remember { mutableStateOf(false) }

        Surface(tonalElevation = 2.dp) {
            Column() {
                UserInputText(
                    textFieldValue = textState,
                    onTextChanged = { textState = it },
                    // Only show the keyboard if there's no input selector and text field has focus
                    keyboardShown = currentInputSelector == InputSelector.NONE && textFieldFocusState,
                    // Close extended selector if text field receives focus
                    onTextFieldFocused = { focused ->
                        if (focused) {
                            currentInputSelector = InputSelector.NONE
                        }
                        textFieldFocusState = focused
                    },
                    focusState = textFieldFocusState
                )
                UserInputSelector(
                    onSelectorChange = { currentInputSelector = it },
                    sendMessageEnabled = textState.text.isNotBlank(),
                    onMessageSent = {
                        api.sendMessage(jwt, senderID.toString(), textState.text)
                        val newMessage = Message(recipientName, recipientID, senderID, textState.text, "", null, true)
                        _messages.add(newMessage)
                        textState = TextFieldValue()
                        dismissKeyboard()
                    },
                    currentInputSelector = currentInputSelector
                )
                SelectorExpanded(
                    currentSelector = currentInputSelector
                )
            }
        }
    }

    private val KeyboardShownKey = SemanticsPropertyKey<Boolean>("KeyboardShownKey")
    var SemanticsPropertyReceiver.keyboardShownProperty by KeyboardShownKey

    @ExperimentalFoundationApi
    @Composable
    private fun UserInputText(
        keyboardType: KeyboardType = KeyboardType.Text,
        onTextChanged: (TextFieldValue) -> Unit,
        textFieldValue: TextFieldValue,
        keyboardShown: Boolean,
        onTextFieldFocused: (Boolean) -> Unit,
        focusState: Boolean
    ) {
        val a11ylabel = stringResource(id = R.string.app_name)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .semantics {
                    contentDescription = a11ylabel
                    keyboardShownProperty = keyboardShown
                },
            horizontalArrangement = Arrangement.End
        ) {
            Surface {
                Box(
                    modifier = Modifier
                        .height(64.dp)
                        .weight(1f)
                        .align(Alignment.Bottom)
                ) {
                    var lastFocusState by remember { mutableStateOf(false) }
                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = { onTextChanged(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 32.dp)
                            .align(Alignment.CenterStart)
                            .onFocusChanged { state ->
                                if (lastFocusState != state.isFocused) {
                                    onTextFieldFocused(state.isFocused)
                                }
                                lastFocusState = state.isFocused
                            },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = keyboardType,
                            imeAction = ImeAction.Send
                        ),
                        maxLines = 1,
                        cursorBrush = SolidColor(LocalContentColor.current),
                        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current)
                    )

                    val disableContentColor =
                        MaterialTheme.colorScheme.onSurfaceVariant
                    if (textFieldValue.text.isEmpty() && !focusState) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 32.dp),
                            text = stringResource(id = R.string.send_message),
                            style = MaterialTheme.typography.bodyLarge.copy(color = disableContentColor)
                        )
                    }
                }
            }
        }
    }


    @Composable
    private fun SelectorExpanded(
        currentSelector: InputSelector
    ) {
        if (currentSelector == InputSelector.NONE) return

        // Request focus to force the TextField to lose it
        val focusRequester = FocusRequester()
        // If the selector is shown, always request focus to trigger a TextField.onFocusChange.
        SideEffect {
            if (currentSelector == InputSelector.EMOJI) {
                focusRequester.requestFocus()
            }
        }

        Surface(tonalElevation = 8.dp) {
            when (currentSelector) {
                InputSelector.PICTURE -> FunctionalityNotAvailablePanel()
                InputSelector.MAP -> FunctionalityNotAvailablePanel()
                InputSelector.PHONE -> FunctionalityNotAvailablePanel()
                else -> { throw NotImplementedError() }
            }
        }
    }

    @Composable
    fun FunctionalityNotAvailablePanel() {
        AnimatedVisibility(
            visibleState = remember { MutableTransitionState(false).apply { targetState = true } },
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .height(320.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(id = R.string.app_name),
                    modifier = Modifier.paddingFrom(FirstBaseline, before = 32.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    @Composable
    private fun InputSelectorButton(
        onClick: () -> Unit,
        icon: ImageVector,
        description: String,
        selected: Boolean
    ) {
        val backgroundModifier = if (selected) {
            Modifier.background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(14.dp)
            )
        } else {
            Modifier
        }
        IconButton(
            onClick = onClick,
            modifier = Modifier.then(backgroundModifier)
        ) {
            val tint = if (selected) {
                MaterialTheme.colorScheme.onSecondary
            } else {
                MaterialTheme.colorScheme.secondary
            }
            Icon(
                icon,
                tint = tint,
                modifier = Modifier
                    .padding(8.dp)
                    .size(56.dp),
                contentDescription = description
            )
        }
    }

    fun setValues() {
        senderID = intent.getIntExtra("senderID", 0)
        recipientID = intent.getIntExtra("senderID", 0)
        recipientName = intent.getStringExtra("recipientName").toString()
    }
}