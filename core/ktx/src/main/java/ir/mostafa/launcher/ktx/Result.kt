package ir.mostafa.launcher.ktx

inline fun <T> Result<T>.orRunCatching(block: () -> T): Result<T> = this.fold(
    onSuccess = { this },
    onFailure = { runCatching { block() } }
)
