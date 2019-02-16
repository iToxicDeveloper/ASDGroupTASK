package com.tamimattafi.asdgroup.Classes

import android.content.Context
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object InternalStorage {

    //Write news list to internal storage
    @Throws(IOException::class)
    fun writeObject(context: Context, key: String, `object`: Any) {
        val mFileOutputStream = context.openFileOutput(key, Context.MODE_PRIVATE)
        val mObjectOutputStream = ObjectOutputStream(mFileOutputStream)
        mObjectOutputStream.writeObject(`object`)
        mObjectOutputStream.close()
        mFileOutputStream.close()
    }

    //Read news list from internal storage
    @Throws(IOException::class, ClassNotFoundException::class)
    fun readObject(context: Context, key: String): Any {
        val mFileInputStream = context.openFileInput(key)
        val mObjectInputStream = ObjectInputStream(mFileInputStream)
        return mObjectInputStream.readObject()
    }
}
