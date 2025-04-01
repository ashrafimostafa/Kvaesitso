package ir.mostafa.launcher.profiles

import android.content.Context
import android.os.Process
import android.os.UserHandle
import ir.mostafa.launcher.ktx.getSerialNumber

data class Profile(
    val type: Profile.Type,
    val userHandle: UserHandle,
    val serial: Long,
) {

    override fun equals(other: Any?): Boolean {
        if (other is Profile) {
            return userHandle == other.userHandle
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return userHandle.hashCode()
    }

    enum class Type {
        /**
         * The default profile.
         */
        Personal,

        /**
         * The work profile.
         */
        Work,

        /**
         * The private space profile (Android 15+)
         */
        Private,
    }

    data class State(
        val locked: Boolean = false,
    )

    companion object {
        fun fromContext(context: Context): Profile {
            val userHandle = Process.myUserHandle()
            val serial = userHandle.getSerialNumber(context)
            return Profile(Profile.Type.Personal, userHandle, serial)
        }
    }
}
