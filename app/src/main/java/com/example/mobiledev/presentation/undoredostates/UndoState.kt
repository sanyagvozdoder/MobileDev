package com.example.mobiledev.presentation.undoredostates

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class StateSaver(
    val initValue:Uri?
) {
    private val undoQueue = ArrayDeque<Uri?>()
    private val redoQueue = ArrayDeque<Uri?>()

    val currentValue = mutableStateOf<Uri?>(initValue)

    fun update(newValue:Uri?){
        currentValue.value = newValue
        undoQueue.addLast(newValue)
    }

    fun undo(){
        val last = undoQueue.last()
        undoQueue.removeLast()

        if(redoQueue.size == 0){
            redoQueue.addFirst(last)
        }

        redoQueue.addFirst(undoQueue.last())
        currentValue.value = undoQueue.last()
    }

    fun redo(){
        val first = redoQueue.first()
        redoQueue.removeFirst()

        if(undoQueue.size == 0){
            undoQueue.addLast(first)
        }

        undoQueue.addLast(redoQueue.first())
        currentValue.value = redoQueue.first()
    }

    fun undoSize() : Int{
        return undoQueue.size
    }
    fun redoSize() : Int{
        return redoQueue.size
    }
}