package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "mission_progress")
data class MissionProgressEntity(
    @PrimaryKey val missionId: Int,
    val completed: Boolean,
    val bestScore: Int,
    val stars: Int,
    val attempts: Int,
    val completedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_spreads")
data class SavedSpreadEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val softwareMode: String, // "InDesign" or "QuarkXPress"
    val columns: Int,
    val gutterPicas: Float,
    val marginPicas: Float,
    val bleedMm: Int,
    val headlineFont: String, // "Playfair", "SpaceGrotesk", "PlusJakarta", "JetBrainsMono"
    val headlineSizePt: Int,
    val bodySizePt: Int,
    val leadingPt: Int,
    val tracking: Int,
    val dropCapLines: Int,
    val textWrapEnabled: Boolean,
    val textWrapOffsetPicas: Float,
    val linkedFrames: Boolean,
    val masterPageApplied: Boolean,
    val baselineGridSnap: Boolean,
    val colorSpace: String, // "CMYK" or "RGB"
    val imagePpi: Int,      // 300 or 72
    val preflightScore: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val modeTitle: String,
    val score: Int,
    val totalQuestions: Int,
    val accuracyPercent: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "kerning_records")
data class KerningRecordEntity(
    @PrimaryKey val wordId: String,
    val wordText: String,
    val bestScore: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface PicaPressDao {
    @Query("SELECT * FROM mission_progress ORDER BY missionId ASC")
    fun observeMissionProgress(): Flow<List<MissionProgressEntity>>

    @Query("SELECT * FROM mission_progress WHERE missionId = :missionId LIMIT 1")
    suspend fun getMissionProgress(missionId: Int): MissionProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMissionProgress(entity: MissionProgressEntity)

    @Query("SELECT * FROM saved_spreads ORDER BY timestamp DESC")
    fun observeSavedSpreads(): Flow<List<SavedSpreadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedSpread(spread: SavedSpreadEntity)

    @Query("DELETE FROM saved_spreads WHERE id = :id")
    suspend fun deleteSavedSpread(id: Int)

    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC")
    fun observeQuizAttempts(): Flow<List<QuizAttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizAttempt(attempt: QuizAttemptEntity)

    @Query("SELECT * FROM kerning_records ORDER BY wordId ASC")
    fun observeKerningRecords(): Flow<List<KerningRecordEntity>>

    @Query("SELECT * FROM kerning_records WHERE wordId = :wordId LIMIT 1")
    suspend fun getKerningRecord(wordId: String): KerningRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertKerningRecord(record: KerningRecordEntity)
}

@Database(
    entities = [
        MissionProgressEntity::class,
        SavedSpreadEntity::class,
        QuizAttemptEntity::class,
        KerningRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PicaPressDatabase : RoomDatabase() {
    abstract fun picaPressDao(): PicaPressDao

    companion object {
        @Volatile
        private var INSTANCE: PicaPressDatabase? = null

        fun getInstance(context: Context): PicaPressDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PicaPressDatabase::class.java,
                    "picapress_studio.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class PicaPressRepository(private val dao: PicaPressDao) {
    val missionProgressFlow: Flow<List<MissionProgressEntity>> = dao.observeMissionProgress()
    val savedSpreadsFlow: Flow<List<SavedSpreadEntity>> = dao.observeSavedSpreads()
    val quizAttemptsFlow: Flow<List<QuizAttemptEntity>> = dao.observeQuizAttempts()
    val kerningRecordsFlow: Flow<List<KerningRecordEntity>> = dao.observeKerningRecords()

    suspend fun recordMissionCompletion(missionId: Int, score: Int, stars: Int) {
        val existing = dao.getMissionProgress(missionId)
        val bestScore = maxOf(existing?.bestScore ?: 0, score)
        val bestStars = maxOf(existing?.stars ?: 0, stars)
        val attempts = (existing?.attempts ?: 0) + 1
        dao.upsertMissionProgress(
            MissionProgressEntity(
                missionId = missionId,
                completed = stars >= 1 || (existing?.completed == true),
                bestScore = bestScore,
                stars = bestStars,
                attempts = attempts
            )
        )
    }

    suspend fun saveCustomSpread(spread: SavedSpreadEntity) {
        dao.insertSavedSpread(spread)
    }

    suspend fun deleteSpread(id: Int) {
        dao.deleteSavedSpread(id)
    }

    suspend fun recordQuizAttempt(modeTitle: String, score: Int, totalQuestions: Int) {
        val accuracy = if (totalQuestions > 0) (score * 100) / totalQuestions else 0
        dao.insertQuizAttempt(
            QuizAttemptEntity(
                modeTitle = modeTitle,
                score = score,
                totalQuestions = totalQuestions,
                accuracyPercent = accuracy
            )
        )
    }

    suspend fun recordKerningScore(wordId: String, wordText: String, score: Int) {
        val existing = dao.getKerningRecord(wordId)
        val best = maxOf(existing?.bestScore ?: 0, score)
        dao.upsertKerningRecord(
            KerningRecordEntity(
                wordId = wordId,
                wordText = wordText,
                bestScore = best
            )
        )
    }
}
