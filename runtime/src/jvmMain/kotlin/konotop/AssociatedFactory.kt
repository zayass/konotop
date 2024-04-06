package konotop

import kotlin.reflect.KClass

actual annotation class AssociatedFactory(actual val factory: KClass<out ApiFactory<*>>)