package konotop

import kotlin.reflect.KClass

internal expect fun <T : Any> KClass<T>.lookupFactory(): ApiFactory<T>?

