package iiitd.cognitrix.pages

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import iiitd.cognitrix.api.Api_data.Note
import iiitd.cognitrix.api.Dataload.CourseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val outputFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.US)
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}

@Composable
fun NotesScreen(
    videoId: String,
    viewModel: CourseViewModel,
    context: Context = LocalContext.current
) {
    val notes by viewModel.notes.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val noteError by viewModel.noteError.observeAsState()
    val noteAdded by viewModel.noteAddSuccess.observeAsState()

    var showAddNoteForm by rememberSaveable { mutableStateOf(false) }
    var showEditNoteForm by rememberSaveable { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<Note?>(null) }
    var title by rememberSaveable { mutableStateOf("") }
    var contents by rememberSaveable { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<Note?>(null) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var statusNote by remember { mutableStateOf<Note?>(null) }

    // Track previous notes count to detect successful operations
    var previousNotesCount by remember { mutableStateOf(notes.size) }
    var isEditOperation by remember { mutableStateOf(false) }
    var isDeleteOperation by remember { mutableStateOf(false) }

    LaunchedEffect(videoId) {
        viewModel.fetchNotes(context, videoId)
    }

    LaunchedEffect(noteAdded) {
        if (noteAdded == true) {
            viewModel.fetchNotes(context, videoId)
            showAddNoteForm = false
            showEditNoteForm = false
            editingNote = null
        }
    }

    // Monitor notes changes for edit/delete success detection
    LaunchedEffect(notes) {
        if (isEditOperation && !isLoading) {
            // Edit was successful - show toast and hide form
            Toast.makeText(context, "Note edited successfully", Toast.LENGTH_SHORT).show()
            showEditNoteForm = false
            editingNote = null
            title = ""
            contents = ""
            isEditOperation = false
        }

        if (isDeleteOperation && notes.size < previousNotesCount && !isLoading) {
            // Delete was successful - show toast
            Toast.makeText(context, "Note deleted successfully", Toast.LENGTH_SHORT).show()
            isDeleteOperation = false
        }

        previousNotesCount = notes.size
    }

    // Show error toast when noteError changes
    LaunchedEffect(noteError) {
        noteError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    // Status change confirmation dialog
    if (showStatusDialog && statusNote != null) {
        val note = statusNote!!
        val isPrivate = note.status.lowercase() == "private"

        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = {
                Text(
                    text = if (isPrivate) "Request to Make Note Public?" else "Withdraw Request?",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    text = if (isPrivate)
                        "This note will be sent for review to the professor and will be made public if approved."
                    else
                        "Withdraw request to professor to make this note public?",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        statusNote?.let { note ->
                            viewModel.requestNoteStatusChange(context, note._id, videoId)
                        }
                        showStatusDialog = false
                        statusNote = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        if (isPrivate) "Request" else "Withdraw",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showStatusDialog = false
                        statusNote = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                    )
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.secondary)
                }
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    }

    // Delete confirmation dialog
    if (showDeleteDialog && noteToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Delete Note",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete this note? This action cannot be undone.",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        noteToDelete?.let { note ->
                            isDeleteOperation = true
                            viewModel.deleteNote(context, note._id, videoId)
                        }
                        showDeleteDialog = false
                        noteToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                    )
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.secondary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                    )) {
                    Text("Cancel", color =MaterialTheme.colorScheme.secondary)
                }
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    }

    Scaffold(
        floatingActionButton = {
            if (!showAddNoteForm && !showEditNoteForm) {
                FloatingActionButton(
                    onClick = { showAddNoteForm = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Note",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading notes...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else if (notes.isEmpty() && !showAddNoteForm && !showEditNoteForm) {
                EmptyNotesPlaceholder(onClick = { showAddNoteForm = true })
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "${notes.size} note${if (notes.size != 1) "s" else ""}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    items(notes) { note ->
                        NoteCard(
                            note = note,
                            onEdit = { noteToEdit ->
                                editingNote = noteToEdit
                                title = noteToEdit.title
                                contents = noteToEdit.content
                                showEditNoteForm = true
                            },
                            onDelete = { note ->
                                noteToDelete = note
                                showDeleteDialog = true
                            },
                            onStatusClick = { note ->
                                statusNote = note
                                showStatusDialog = true
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            AnimatedVisibility(
                visible = showAddNoteForm,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                AddNoteForm(
                    title = title,
                    onTitleChange = { title = it },
                    content = contents,
                    onContentChange = { contents = it },
                    onAddNote = {
                        viewModel.addNote(context, videoId, title, contents)
                    },
                    onCancel = {
                        showAddNoteForm = false
                        title = ""
                        contents = ""
                    },
                    error = noteError,
                    isEditing = false
                )
            }

            AnimatedVisibility(
                visible = showEditNoteForm,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                AddNoteForm(
                    title = title,
                    onTitleChange = { title = it },
                    content = contents,
                    onContentChange = { contents = it },
                    onAddNote = {
                        editingNote?.let { note ->
                            isEditOperation = true
                            viewModel.editNote(context, note._id, title, contents, videoId)
                        }
                    },
                    onCancel = {
                        showEditNoteForm = false
                        editingNote = null
                        title = ""
                        contents = ""
                    },
                    error = noteError,
                    isEditing = true
                )
            }
        }
    }
}

@Composable
fun EmptyNotesPlaceholder(onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
//            Icon(
//                Icons.Default.AddCircle,
//                contentDescription = null,
//                modifier = Modifier.height(60.dp),
//                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "No notes Added yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Create your first note for this video by clicking the Add Button",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun NoteCard(
    note: Note,
    onEdit: (Note) -> Unit,
    onDelete: (Note) -> Unit,
    onStatusClick: (Note) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    note.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    TextButton(
                        onClick = { onStatusClick(note) },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            note.status,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    IconButton(onClick = { onEdit(note) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Note",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { onDelete(note) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Note",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .clip(RoundedCornerShape(1.dp)),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                note.content,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Status: ${note.status}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Created: ${formatDate(note.createdAt)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AddNoteForm(
    title: String,
    onTitleChange: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    onAddNote: () -> Unit,
    onCancel: () -> Unit,
    error: String?,
    isEditing: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        color = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                if (isEditing) "Edit Note" else "Add New Note",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            OutlinedTextField(
                value = title,
                onValueChange = { newValue ->
                    if (newValue.length <= 50) {
                        onTitleChange(newValue)
                    }
                },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor =  MaterialTheme.colorScheme.tertiary,
                    focusedLabelColor =  MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor =  MaterialTheme.colorScheme.primary,
                    cursorColor =  MaterialTheme.colorScheme.primary,
                    focusedTextColor =  MaterialTheme.colorScheme.primary,
                    unfocusedTextColor =  MaterialTheme.colorScheme.primary,
                ),
                placeholder = {
                    Text(
                        "Enter title here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
                    )
                },
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            "${title.length}/50",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { newValue ->
                    if (newValue.length <= 500) {
                        onContentChange(newValue)
                    }
                },
                label = { Text("Content") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    unfocusedTextColor = MaterialTheme.colorScheme.primary,
                ),
                placeholder = {
                    Text(
                        "Enter content here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
                    )
                },
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            "${content.length}/500",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )

            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text="Cancel",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Button(
                    onClick = onAddNote,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    ),
                    enabled = title.isNotBlank() && content.isNotBlank(),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = if (isEditing) "Update Note" else "Save Note",
                        color = if (title.isNotBlank() && content.isNotBlank())
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
