package ir.mostafa.launcher.ktx

import android.content.Context
import android.os.Process
import android.os.UserHandle
import android.os.UserManager

fun UserHandle.getSerialNumber(context: Context): Long {
    if (this == Process.myUserHandle()) return 0L
    val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager
    return userManager.getSerialNumberForUser(this)
}