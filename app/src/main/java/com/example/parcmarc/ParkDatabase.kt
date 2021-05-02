package com.example.parcmarc

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Park::class], version = 1)
abstract class ParkDatabase: RoomDatabase() {
    abstract fun parkDao(): ParkDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ParkDatabase? = null

        fun getDatabase(context: Context): ParkDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ParkDatabase::class.java,
                    "park_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}