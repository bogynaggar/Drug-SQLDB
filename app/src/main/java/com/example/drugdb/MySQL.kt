package com.example.drugdb

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

val DATABASE_NAME ="MyDB"
val TABLE_NAME="Drugs"
val COL_ID = "id"
val COL_NAME = "name"
val COL_AI = "ai"
val COL_CAT = "category"
val COL_PRICE = "price"
val TABLE_DRUGCAT = "DrugCAT"
val COL_DRUGCAT = "dCat"


class MySQL(private var context: Context ) : SQLiteOpenHelper(context , DATABASE_NAME ,null , 1  ) {


    override fun onCreate(db: SQLiteDatabase?) {

        val createTable = "CREATE TABLE " + TABLE_NAME +" (" +
                COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NAME + " VARCHAR(256)," +
                COL_AI + " VARCHAR(256)," +
                COL_CAT + " VARCHAR(256)," +
                COL_PRICE +" REAL )"

        val createCATTable = "CREATE TABLE " + TABLE_DRUGCAT +" (" +
                COL_DRUGCAT +" VARCHAR(256) )"

        db?.execSQL(createTable)
        db?.execSQL(createCATTable)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun insertData(drug : Drug){
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_ID,drug.id)
        cv.put(COL_NAME,drug.name)
        cv.put(COL_AI,drug.activeI)
        cv.put(COL_CAT,drug.category)
        cv.put(COL_PRICE,drug.price)
        val result = db.insert(TABLE_NAME,null,cv)
        if(result.toInt() == -1)
            Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show()
    }
    fun insertCATData(cat : String){
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_DRUGCAT,cat)
        val result = db.insert(TABLE_DRUGCAT,null,cv)
      /*
        if(result.toInt() == -1)
           Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show()
        else
           Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show()
      */
    }


    @SuppressLint("Range")
    fun readData() : MutableList<Drug>{
        val list : MutableList<Drug> = ArrayList()

        val db = this.readableDatabase
        val query = "Select * from $TABLE_NAME"
        val result = db.rawQuery(query,null)
        if(result.moveToFirst()){
            do {
                val drug = Drug(
                    result.getString(result.getColumnIndex(COL_ID)).toInt() ,
                    result.getString(result.getColumnIndex(COL_NAME)) ,
                    result.getString(result.getColumnIndex(COL_AI)) ,
                    result.getString(result.getColumnIndex(COL_CAT)) ,
                    result.getString(result.getColumnIndex(COL_PRICE))
                )
                list.add(drug)
            }while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }

    @SuppressLint("Range")
    fun readCATData() : MutableList<String>{
        val list : MutableList<String> = ArrayList()

        val db = this.readableDatabase
        val query = "Select * from $TABLE_DRUGCAT"
        val result = db.rawQuery(query,null)
        if(result.moveToFirst()){
            do {
                val drugCat = result.getString(result.getColumnIndex(COL_DRUGCAT))
                list.add(drugCat)
            }while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }




    fun deleteALLData(){
        val db = this.writableDatabase
        db.delete(TABLE_NAME,null,null)
        db.close()
    }

    fun deleteSelected( id :Int ){
        val query = "delete from $TABLE_NAME Where $COL_ID = $id"
        try {
            writableDatabase?.execSQL(query)

        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }


    fun updateData(drug: Drug , id :String) :Boolean{
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_ID,drug.id)
        cv.put(COL_NAME,drug.name)
        cv.put(COL_AI,drug.activeI)
        cv.put(COL_CAT,drug.category)
        cv.put(COL_PRICE,drug.price)

        val selection = "id = ?"
        val selectionArgs = arrayOf<String>("" + id)
        val result = db.update(TABLE_NAME, cv, selection , selectionArgs)

        return result > 0

    /*    val query = "UPDATE $TABLE_NAME SET " +
                "$COL_ID = " + drug.id +
                ", $COL_NAME = " + drug.name +
                ", $COL_AI = " + drug.activeI +
                ", $COL_CAT = " + drug.category +
                ", $COL_PRICE = " + drug.price +
                " Where $COL_ID = $id "

        try {
            writableDatabase?.execSQL(query)

        } catch (exception: Exception) {
            exception.printStackTrace()
        }*/
    }

    }


