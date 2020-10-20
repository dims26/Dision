package com.example.dision

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.dision.helper.NetworkState
import com.example.dision.models.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class Repository(private val firestore: FirebaseFirestore) {
    fun createUserAndOrgRecord(orgName: String, name: String, email: String, uid: String, loadState: MutableLiveData<NetworkState>) {
        val memberData = HashMap<String, DocumentReference>()
        memberData[email] = firestore.document("people/$uid")

        val data = HashMap<String, Any>()
        data["name"] = orgName
        data["members"] = memberData

        firestore.collection("organizations")
                .add(data)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Create map containing ref to organization document
                        val orgData = HashMap<String, DocumentReference>()
                        orgData[orgName] = task.result!!
                        createAndLinkUserOrgRecord(name, email, uid, loadState, orgData)
                    }
                    else loadState.postValue(NetworkState.FAILURE)
                }
    }

    private fun createAndLinkUserOrgRecord(name: String, email: String, uid: String, loadState: MutableLiveData<NetworkState>, orgData: HashMap<String, DocumentReference>) {
        val data = HashMap<String, Any>()
        data["email"] = email
        data["id"] = uid
        data["name"] = name
        data["organizations"] = orgData
        firestore.document("people/$uid").set(data).addOnCompleteListener {task ->
            if (task.isSuccessful) loadState.postValue(NetworkState.SUCCESS)
            else loadState.postValue(NetworkState.FAILURE)
        }
    }

    fun createUserRecord(name: String, email: String, uid: String, loadState: MutableLiveData<NetworkState>) {
        val data = HashMap<String, Any>()
        data["email"] = email
        data["id"] = uid
        data["name"] = name
        firestore.document("people/$uid").set(data).addOnCompleteListener {task ->
            if (task.isSuccessful) loadState.postValue(NetworkState.SUCCESS)
            else loadState.postValue(NetworkState.FAILURE)
        }
    }

    fun loadOrgList(uid: String, loadState: MutableLiveData<NetworkState>, user: MutableLiveData<User>) {
        firestore.document("people/$uid").get()
                .addOnSuccessListener {userData ->
                    user.postValue(User(
                            userData.getString("id")!!,
                            userData.getString("name")!!,
                            userData.getString("email")!!,
                            userData.get("organizations") as HashMap<String, DocumentReference>
                    ))
                    loadState.postValue(NetworkState.SUCCESS)
                }
                .addOnFailureListener {
                    Log.e("REPOSITORY_LOADORGLIST", "Couldn't load user documents")
                    loadState.postValue(NetworkState.FAILURE)
                }
    }
}