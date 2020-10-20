package com.example.dision.models

import com.google.firebase.firestore.DocumentReference

data class User(val id: String, val name: String, val email: String, val organizations: HashMap<String, DocumentReference>)