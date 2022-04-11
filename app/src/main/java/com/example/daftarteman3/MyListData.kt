package com.example.daftarteman3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyListData : AppCompatActivity(), RecyclerViewAdapter.dataListener {

    private var recyclerView:RecyclerView? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    val database = FirebaseDatabase.getInstance()
    private var dataTeman3 = ArrayList<data_teman3>()
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_list_data)
        recyclerView = findViewById(R.id.datalist)
        supportActionBar!!.title = "DataTeman3"
        auth = FirebaseAuth.getInstance()
        MyRecyclearView()
        GetData()
    }

    private fun GetData() {
        Toast.makeText(applicationContext, "Mohon Tunggu Sebentar...",
            Toast.LENGTH_SHORT).show()
        val  getUserID: String = auth?.getCurrentUser()?.getUid().toString()
        val getReference = database.getReference()
        getReference.child("Admin").child(getUserID).child("DataTeman3")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(datasnapshot: DataSnapshot) {
                    if (datasnapshot.exists()) {
                        dataTeman3.clear()
                        for (snapshot in datasnapshot.children) {
                            val teman = snapshot.getValue(data_teman3::class.java)
                            teman?.key = snapshot.key
                            dataTeman3.add(teman!!)
                        }
                        adapter = RecyclerViewAdapter(dataTeman3, this@MyListData)
                        recyclerView?.adapter = adapter
                        (adapter as RecyclerViewAdapter).notifyDataSetChanged()
                        Toast.makeText(applicationContext, "data Berhasil Dimuat",
                        Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaserror: DatabaseError) {
                    Toast.makeText(applicationContext, "data gagal Dimuat",
                        Toast.LENGTH_SHORT).show()
                    Log.e("MyListActivity", databaserror.details + " " +
                            databaserror.message)

                }
            })
    }

    private fun MyRecyclearView() {
        layoutManager= LinearLayoutManager(this)
        recyclerView?.layoutManager = layoutManager
        recyclerView?.setHasFixedSize(true)

        val itemDecoration = DividerItemDecoration(applicationContext,
        DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(ContextCompat.getDrawable(applicationContext,
        R.drawable.line)!!)
        recyclerView?.addItemDecoration(itemDecoration)
    }

    override fun onDeleteData(data: data_teman3?, position: Int) {
        val getUserID: String = auth?.getCurrentUser()?.getUid().toString()
        val getReference = database.getReference()
        if (getReference != null) {
            getReference.child("Admin")
                .child(getUserID)
                .child("DataTeman3")
                .child(data?.key.toString())
                .removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this@MyListData, "Data berhasil dihapus",
                    Toast.LENGTH_SHORT).show()
                }
        }else {
            Toast.makeText(this@MyListData, "Reference Kosong",
                Toast.LENGTH_SHORT).show()
        }
    }
}